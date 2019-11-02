package workshop.gateway;

import graphql.GraphQL;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;
import hu.akarnokd.rxjava2.interop.MaybeInterop;
import hu.akarnokd.rxjava2.interop.SingleInterop;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.htpasswd.HtpasswdAuthOptions;
import io.vertx.ext.web.RoutingContext;
import io.vertx.reactivex.ext.auth.htpasswd.HtpasswdAuth;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.*;
import io.vertx.reactivex.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.reactivex.ext.web.handler.graphql.GraphiQLHandler;
import io.vertx.reactivex.ext.web.sstore.LocalSessionStore;
import workshop.model.*;

import java.util.List;
import java.util.Map;

public class GatewayServer extends WorkshopVerticle {

  protected Router createRouter() {
    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());

    HtpasswdAuthOptions authOptions = new HtpasswdAuthOptions()
      .setHtpasswdFile("passwordfile")
      .setPlainTextEnabled(true);
    HtpasswdAuth authProvider = HtpasswdAuth.create(vertx, authOptions);

    router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)).setAuthProvider(authProvider));
    router.post("/login.html").handler(FormLoginHandler.create(authProvider).setDirectLoggedInOKURL("/"));
    router.get("/logout").handler(rc -> {
      rc.clearUser();
      rc.session().destroy();
      rc.response().setStatusCode(307).putHeader(HttpHeaders.LOCATION, "/").end();
    });

    GraphQL graphQL = setupGraphQLJava("musicstore.graphql");
    router.route("/graphql").handler(GraphQLHandler.create(graphQL));
    router.get("/graphiql/*").handler(GraphiQLHandler.create());

    router.get().handler(StaticHandler.create());

    router.route().failureHandler(ErrorHandler.create());

    return router;
  }

  protected RuntimeWiring runtimeWiring() {
    return RuntimeWiring.newRuntimeWiring()
      .type("Query", this::query)
      .type("Mutation", this::mutation)
      .type("Album", this::album)
      .type("CartItem", this::cartItem)
      .build();
  }

  private TypeRuntimeWiring.Builder query(TypeRuntimeWiring.Builder builder) {
    return builder
      .dataFetcher("genres", env -> genresRepository.findAll().to(SingleInterop.get()))
      .dataFetcher("albums", env -> {
        String genre = env.getArgument("genre");
        return albumsRepository.findAll(genre==null ? null:Integer.valueOf(genre)).to(SingleInterop.get());
      })
      .dataFetcher("album", env -> {
        Integer id = Integer.valueOf(env.getArgument("id"));
        Single<Album> inventoryData = albumsRepository.findById(id, true);
        Single<RatingInfo> ratingData = ratingRepository.findRatingAndReviewsByAlbum(id);
        Single<Album> album = inventoryData.zipWith(ratingData, (a, r) -> {
          a.setRating(r.getRating());
          a.setReviews(r.getReviews());
          return a;
        });
        return album.to(SingleInterop.get());
      })
      .dataFetcher("currentUser", env -> currentUser(env).to(MaybeInterop.get()))
      .dataFetcher("cart", env -> currentUser(env).flatMapSingleElement(cartRepository::findCart).to(MaybeInterop.get()));
  }

  private TypeRuntimeWiring.Builder mutation(TypeRuntimeWiring.Builder builder) {
    return builder
      .dataFetcher("addReview", env -> {
        Single<RatingInfo> reviewResult = loggedInUser(env).flatMap(currentUserName -> {
          Integer albumId = Integer.valueOf(env.getArgument("albumId"));
          ReviewInput reviewInput = new JsonObject((Map<String, Object>) env.getArgument("review"))
            .mapTo(ReviewInput.class);
          reviewInput.setName(currentUserName);
          return ratingRepository.addReview(albumId, reviewInput);
        });
        return reviewResult.to(SingleInterop.get());
      })
      .dataFetcher("addToCart", env -> {
        Single<Cart> cart = loggedInUser(env).flatMap(currentUserName -> {
          Integer albumId = Integer.valueOf(env.getArgument("albumId"));
          return cartRepository.addToCart(currentUserName, albumId)
            .andThen(cartRepository.findCart(currentUserName));
        });
        return cart.to(SingleInterop.get());
      })
      .dataFetcher("removeFromCart", env -> {
        Single<Cart> cart = loggedInUser(env).flatMap(currentUserName -> {
          Integer albumId = Integer.valueOf(env.getArgument("albumId"));
          return cartRepository.removeFromCart(currentUserName, albumId)
            .andThen(cartRepository.findCart(currentUserName));
        });
        return cart.to(SingleInterop.get());
      });
  }

  private Maybe<String> currentUser(DataFetchingEnvironment env) {
    RoutingContext routingContext = env.getContext();
    User user = routingContext.user();
    if (user!=null) {
      return Maybe.just(user.principal().getString("username"));
    }
    return Maybe.empty();
  }

  private Single<String> loggedInUser(DataFetchingEnvironment env) {
    return currentUser(env)
      .switchIfEmpty(Single.error(new NoStackTraceThrowable("Not logged in")));
  }

  private TypeRuntimeWiring.Builder album(TypeRuntimeWiring.Builder builder) {
    return builder
      .dataFetcher("tracks", env -> {
        Album album = env.getSource();
        Single<List<Track>> tracks;
        if (album.getTracks()!=null) {
          tracks = Single.just(album.getTracks());
        } else {
          tracks = tracksRepository.findByAlbum(album.getId());
        }
        return tracks.to(SingleInterop.get());
      })
      .dataFetcher("rating", env -> {
        Album album = env.getSource();
        Single<Integer> rating;
        if (album.getRating()!=null) {
          rating = Single.just(album.getRating());
        } else {
          rating = ratingRepository.findRatingByAlbum(album.getId());
        }
        return rating.to(SingleInterop.get());
      })
      .dataFetcher("reviews", env -> {
        Album album = env.getSource();
        Single<List<Review>> reviews;
        if (album.getReviews()!=null) {
          reviews = Single.just(album.getReviews());
        } else {
          reviews = ratingRepository.findReviewsByAlbum(album.getId());
        }
        return reviews.to(SingleInterop.get());
      });
  }

  private TypeRuntimeWiring.Builder cartItem(TypeRuntimeWiring.Builder builder) {
    return builder
      .dataFetcher("album", env -> {
        CartItem cartItem = env.getSource();
        return albumsRepository.findById(cartItem.getAlbumId(), false).to(SingleInterop.get());
      });
  }
}

package io.github.tsegismont.graphql.workshop.webapp;

import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import hu.akarnokd.rxjava2.interop.MaybeInterop;
import hu.akarnokd.rxjava2.interop.SingleInterop;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.htpasswd.HtpasswdAuthOptions;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.graphql.GraphQLHandlerOptions;
import io.vertx.ext.web.handler.graphql.GraphiQLHandlerOptions;
import io.vertx.ext.web.handler.graphql.VertxPropertyDataFetcher;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.auth.htpasswd.HtpasswdAuth;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.handler.*;
import io.vertx.reactivex.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.reactivex.ext.web.handler.graphql.GraphiQLHandler;
import io.vertx.reactivex.ext.web.sstore.LocalSessionStore;
import io.vertx.reactivex.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

import java.util.Map;

public class WebappServer extends AbstractVerticle {

  private static final String CREATE_TABLE =
    "create table if not exists cart ("
      + "username varchar not null"
      + ", "
      + "album_id int not null"
      + ", "
      + "quantity int not null"
      + ", "
      + "unique (username, album_id)"
      + ")";

  private GenresRepository genresRepository;
  private AlbumsRepository albumsRepository;
  private TracksRepository tracksRepository;
  private ReviewRepository reviewRepository;
  private CartRepository cartRepository;

  @Override
  public Completable rxStart() {
    PgPool pool = createPgPool("musicstore", "musicstore", "musicstore");
    cartRepository = new CartRepository(pool);

    Completable dbSetup = pool.rxQuery(CREATE_TABLE).ignoreElement();

    WebClient inventoryClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(8081));
    genresRepository = new GenresRepository(inventoryClient);
    albumsRepository = new AlbumsRepository(inventoryClient);
    tracksRepository = new TracksRepository(inventoryClient);

    WebClient reviewClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(8082));
    reviewRepository = new ReviewRepository(reviewClient);

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

    router.route("/graphql").handler(createGraphQLHandler());
    router.get("/graphiql/*").handler(createGraphiQLHandler());

    router.get().handler(StaticHandler.create());

    router.route().failureHandler(ErrorHandler.create());

    Completable httpSetup = vertx.createHttpServer()
      .requestHandler(router)
      .rxListen(8080)
      .ignoreElement();

    return dbSetup.andThen(httpSetup);
  }

  private PgPool createPgPool(String database, String user, String password) {
    PgConnectOptions pgConnectOptions = new PgConnectOptions()
      .setDatabase(database)
      .setUser(user)
      .setPassword(password);
    return PgPool.pool(vertx, pgConnectOptions, new PoolOptions());
  }

  private GraphiQLHandler createGraphiQLHandler() {
    GraphiQLHandlerOptions options = new GraphiQLHandlerOptions();
    return GraphiQLHandler.create(options);
  }

  private GraphQLHandler createGraphQLHandler() {
    GraphQLHandlerOptions options = new GraphQLHandlerOptions()
      .setRequestBatchingEnabled(true);
    return GraphQLHandler.create(setupGraphQLJava(), options);
  }

  private GraphQL setupGraphQLJava() {
    String schema = vertx.fileSystem().readFileBlocking("musicstore.graphql").toString();

    SchemaParser schemaParser = new SchemaParser();
    TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

    RuntimeWiring runtimeWiring = runtimeWiring();

    SchemaGenerator schemaGenerator = new SchemaGenerator();
    GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

    return GraphQL.newGraphQL(graphQLSchema)
      .build();
  }

  private RuntimeWiring runtimeWiring() {
    return RuntimeWiring.newRuntimeWiring()
      .type("Query", this::query)
      .type("Mutation", this::mutation)
      .type("Album", this::album)
      .type("CartItem", this::cartItem)
      .wiringFactory(new CustomWiringFactory())
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
        Single<JsonObject> inventoryData = albumsRepository.findById(id, true);
        Single<JsonObject> reviewData = reviewRepository.findRatingAndReviewsByAlbum(id);
        return inventoryData.zipWith(reviewData, (i, r) -> r.mergeIn(i)).to(SingleInterop.get());
      })
      .dataFetcher("currentUser", env -> getCurrentUserName(env).to(MaybeInterop.get()))
      .dataFetcher("cart", env -> {
        return this.getCurrentUserName(env)
          .flatMapSingleElement(cartRepository::findCart)
          .map(items -> new JsonObject().put("items", items))
          .to(MaybeInterop.get());
      });
  }

  private TypeRuntimeWiring.Builder mutation(TypeRuntimeWiring.Builder builder) {
    return builder
      .dataFetcher("addReview", env -> {
        return getCurrentUserName(env)
          .switchIfEmpty(Single.error(new NoStackTraceThrowable("Not logged in")))
          .flatMap(currentUserName -> {
            Integer albumId = Integer.valueOf(env.getArgument("albumId"));
            JsonObject input = new JsonObject((Map<String, Object>) env.getArgument("review"));
            input.put("name", currentUserName);
            return reviewRepository.addReview(albumId, input);
          }).to(SingleInterop.get());
      })
      .dataFetcher("addToCart", env -> {
        return getCurrentUserName(env)
          .switchIfEmpty(Single.error(new NoStackTraceThrowable("Not logged in")))
          .flatMap(currentUserName -> {
            Integer albumId = Integer.valueOf(env.getArgument("albumId"));
            return cartRepository.addToCart(currentUserName, albumId).andThen(cartRepository.findCart(currentUserName));
          })
          .map(items -> new JsonObject().put("items", items))
          .to(SingleInterop.get());
      })
      .dataFetcher("removeFromCart", env -> {
        return getCurrentUserName(env)
          .switchIfEmpty(Single.error(new NoStackTraceThrowable("Not logged in")))
          .flatMap(currentUserName -> {
            Integer albumId = Integer.valueOf(env.getArgument("albumId"));
            return cartRepository.removeFromCart(currentUserName, albumId).andThen(cartRepository.findCart(currentUserName));
          })
          .map(items -> new JsonObject().put("items", items))
          .to(SingleInterop.get());
      });
  }

  private Maybe<String> getCurrentUserName(DataFetchingEnvironment env) {
    RoutingContext routingContext = env.getContext();
    User user = routingContext.user();
    if (user!=null) {
      return Maybe.just(user.principal().getString("username"));
    }
    return Maybe.empty();
  }

  private TypeRuntimeWiring.Builder album(TypeRuntimeWiring.Builder builder) {
    return builder
      .dataFetcher("tracks", env -> {
        JsonObject album = env.getSource();
        Single<JsonArray> tracks;
        if (album.containsKey("tracks")) {
          tracks = Single.just(album.getJsonArray("tracks"));
        } else {
          tracks = tracksRepository.findByAlbum(album.getInteger("id"));
        }
        return tracks.to(SingleInterop.get());
      })
      .dataFetcher("rating", env -> {
        JsonObject album = env.getSource();
        Single<Integer> rating;
        if (album.containsKey("rating")) {
          rating = Single.just(album.getInteger("rating"));
        } else {
          rating = reviewRepository.findRatingByAlbum(album.getInteger("id"))
            .map(json -> json.getInteger("value"));
        }
        return rating.to(SingleInterop.get());
      })
      .dataFetcher("reviews", env -> {
        JsonObject album = env.getSource();
        Single<JsonArray> reviews;
        if (album.containsKey("reviews")) {
          reviews = Single.just(album.getJsonArray("reviews"));
        } else {
          reviews = reviewRepository.findReviewsByAlbum(album.getInteger("id"));
        }
        return reviews.to(SingleInterop.get());
      });
  }

  private TypeRuntimeWiring.Builder cartItem(TypeRuntimeWiring.Builder builder) {
    return builder
      .dataFetcher("album", env -> {
        JsonObject cartItem = env.getSource();
        return albumsRepository.findById(cartItem.getInteger("albumId"), false).to(SingleInterop.get());
      });
  }

  private static class CustomWiringFactory implements WiringFactory {
    @Override
    public DataFetcher getDefaultDataFetcher(FieldWiringEnvironment environment) {
      return new VertxPropertyDataFetcher(environment.getFieldDefinition().getName());
    }
  }
}

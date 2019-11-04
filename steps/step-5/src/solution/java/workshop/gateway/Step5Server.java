package workshop.gateway;

import graphql.GraphQL;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.auth.htpasswd.HtpasswdAuthOptions;
import io.vertx.reactivex.ext.auth.htpasswd.HtpasswdAuth;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.*;
import io.vertx.reactivex.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.reactivex.ext.web.handler.graphql.GraphiQLHandler;
import io.vertx.reactivex.ext.web.sstore.LocalSessionStore;

public class Step5Server extends WorkshopVerticle {

  protected Router createRouter() {
    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());

    HtpasswdAuthOptions authOptions = new HtpasswdAuthOptions()
      .setHtpasswdFile("passwordfile")
      .setPlainTextEnabled(true);
    HtpasswdAuth authProvider = HtpasswdAuth.create(vertx, authOptions);

    SessionHandler sessionHandler = SessionHandler.create(LocalSessionStore.create(vertx)).setAuthProvider(authProvider);
    router.route().handler(sessionHandler);
    FormLoginHandler formLoginHandler = FormLoginHandler.create(authProvider).setDirectLoggedInOKURL("/");
    router.post("/login.html").handler(formLoginHandler);
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
      .type("Album", this::album)
      .build();
  }

  private TypeRuntimeWiring.Builder query(TypeRuntimeWiring.Builder builder) {
    return builder
      .dataFetcher("genres", new GenresDataFetcher(genresRepository))
      .dataFetcher("albums", new AlbumsDataFetcher(albumsRepository))
      .dataFetcher("album", new AlbumDataFetcher(albumsRepository, ratingRepository))
      .dataFetcher("currentUser", new CurrentUserDataFetcher());
  }

  private TypeRuntimeWiring.Builder album(TypeRuntimeWiring.Builder builder) {
    return builder
      .dataFetcher("tracks", new AlbumTracksDataFetcher(tracksRepository))
      .dataFetcher("rating", new AlbumRatingDataFetcher(ratingRepository))
      .dataFetcher("reviews", new AlbumReviewsDataFetcher(ratingRepository));
  }
}

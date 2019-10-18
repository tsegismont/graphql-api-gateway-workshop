package io.github.tsegismont.graphql.workshop.webapp;

import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import hu.akarnokd.rxjava2.interop.SingleInterop;
import io.reactivex.Completable;
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
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.auth.htpasswd.HtpasswdAuth;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.handler.*;
import io.vertx.reactivex.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.reactivex.ext.web.handler.graphql.GraphiQLHandler;
import io.vertx.reactivex.ext.web.sstore.LocalSessionStore;

import java.util.Map;

public class WebappServer extends AbstractVerticle {

  private GenresRepository genresRepository;
  private AlbumsRepository albumsRepository;
  private TracksRepository tracksRepository;
  private ReviewRepository reviewRepository;

  @Override
  public Completable rxStart() {
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
      rc.response().setStatusCode(301).putHeader(HttpHeaders.LOCATION, "/").end();
    });

    router.route("/graphql").handler(createGraphQLHandler());
    router.get("/graphiql/*").handler(createGraphiQLHandler());

    router.get().handler(StaticHandler.create());

    router.route().failureHandler(ErrorHandler.create());

    return vertx.createHttpServer()
      .requestHandler(router)
      .rxListen(8080)
      .ignoreElement();
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
      .wiringFactory(new CustomWiringFactory())
      .build();
  }

  private TypeRuntimeWiring.Builder query(TypeRuntimeWiring.Builder builder) {
    return builder
      .dataFetcher("genres", env -> genresRepository.findAll().to(SingleInterop.get()))
      .dataFetcher("albums", env -> {
        String genre = env.getArgument("genre");
        return albumsRepository.findAll(genre == null ? null : Integer.valueOf(genre)).to(SingleInterop.get());
      })
      .dataFetcher("album", env -> {
        Integer id = Integer.valueOf(env.getArgument("id"));
        Single<JsonObject> inventoryData = albumsRepository.findById(id, true);
        Single<JsonObject> reviewData = reviewRepository.findRatingAndReviewsByAlbum(id);
        Single<JsonObject> result = inventoryData.zipWith(reviewData, (i, r) -> r.mergeIn(i));
        return result.to(SingleInterop.get());
      })
      .dataFetcher("currentUser", this::getCurrentUserName);
  }

  private TypeRuntimeWiring.Builder mutation(TypeRuntimeWiring.Builder builder) {
    return builder
      .dataFetcher("addReview", env -> {
        Integer albumId = Integer.valueOf(env.getArgument("albumId"));
        JsonObject input = new JsonObject((Map<String, Object>) env.getArgument("review"));
        String currentUserName = getCurrentUserName(env);
        Single<JsonObject> result;
        if (currentUserName==null) {
          result = Single.error(new NoStackTraceThrowable("Not logged in"));
        } else {
          input.put("name", currentUserName);
          result = reviewRepository.addReview(albumId, input);
        }
        return result.to(SingleInterop.get());
      });
  }

  private String getCurrentUserName(DataFetchingEnvironment env) {
    RoutingContext routingContext = env.getContext();
    User user = routingContext.user();
    return user==null ? null:user.principal().getString("username");
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

  private static class CustomWiringFactory implements WiringFactory {
    @Override
    public DataFetcher getDefaultDataFetcher(FieldWiringEnvironment environment) {
      return new VertxPropertyDataFetcher(environment.getFieldDefinition().getName());
    }
  }
}

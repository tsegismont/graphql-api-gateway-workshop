package io.github.tsegismont.graphql.workshop.webapp;

import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import hu.akarnokd.rxjava2.interop.SingleInterop;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.graphql.GraphQLHandlerOptions;
import io.vertx.ext.web.handler.graphql.GraphiQLHandlerOptions;
import io.vertx.ext.web.handler.graphql.VertxPropertyDataFetcher;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.ErrorHandler;
import io.vertx.reactivex.ext.web.handler.StaticHandler;
import io.vertx.reactivex.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.reactivex.ext.web.handler.graphql.GraphiQLHandler;

public class WebappServer extends AbstractVerticle {

  private GenresRepository genresRepository;
  private AlbumsRepository albumsRepository;
  private TracksRepository tracksRepository;

  @Override
  public Completable rxStart() {
    WebClient inventoryClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(8081));
    genresRepository = new GenresRepository(inventoryClient);
    albumsRepository = new AlbumsRepository(inventoryClient);
    tracksRepository = new TracksRepository(inventoryClient);

    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());

    router.route("/graphql").handler(createGraphQLHandler());
    router.get("/graphiql/*").handler(createGraphiQLHandler());

    router.get().handler(StaticHandler.create());

    router.route().failureHandler(ErrorHandler.create(true));

    return vertx.createHttpServer()
      .requestHandler(router)
      .rxListen(8080)
      .ignoreElement();
  }

  private GraphiQLHandler createGraphiQLHandler() {
    GraphiQLHandlerOptions options = new GraphiQLHandlerOptions()
      .setEnabled(true);
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
        String id = env.getArgument("id");
        return albumsRepository.findById(Integer.valueOf(id), true).to(SingleInterop.get());
      });
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
      });
  }

  private static class CustomWiringFactory implements WiringFactory {
    @Override
    public DataFetcher getDefaultDataFetcher(FieldWiringEnvironment environment) {
      return new VertxPropertyDataFetcher(environment.getFieldDefinition().getName());
    }
  }
}

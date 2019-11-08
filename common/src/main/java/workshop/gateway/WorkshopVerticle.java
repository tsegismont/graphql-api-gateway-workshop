package workshop.gateway;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.reactivex.Completable;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import workshop.repository.*;

public abstract class WorkshopVerticle extends AbstractVerticle {

  private static final Logger log = LoggerFactory.getLogger(WorkshopVerticle.class);

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

  protected GenresRepository genresRepository;
  protected AlbumsRepository albumsRepository;
  protected TracksRepository tracksRepository;
  protected RatingRepository ratingRepository;
  protected CartRepository cartRepository;

  @Override
  public Completable rxStart() {
    PgPool pool = createPgPool("musicstore", "musicstore", "musicstore");
    cartRepository = new CartRepository(pool);

    Completable dbSetup = pool.rxQuery(CREATE_TABLE)
      .ignoreElement()
      .doOnError(throwable -> log.warn("Failed to communicate with Postgres", throwable))
      .onErrorComplete();

    WebClient inventoryClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(8081));
    genresRepository = new GenresRepository(inventoryClient);
    albumsRepository = new AlbumsRepository(inventoryClient);
    tracksRepository = new TracksRepository(inventoryClient);

    WebClient ratingClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(8082));
    ratingRepository = new RatingRepository(ratingClient);

    Router router = createRouter();

    Completable httpSetup = vertx.createHttpServer()
      .requestHandler(router)
      .rxListen(8080)
      .ignoreElement();

    return dbSetup.andThen(httpSetup);
  }

  protected static void rerouteToVueIndex(RoutingContext rc) {
    if (!"/".equals(rc.normalisedPath())) {
      rc.reroute("/");
    } else {
      rc.next();
    }
  }

  protected PgPool createPgPool(String database, String user, String password) {
    PgConnectOptions pgConnectOptions = new PgConnectOptions()
      .setDatabase(database)
      .setUser(user)
      .setPassword(password);
    return PgPool.pool(vertx, pgConnectOptions, new PoolOptions());
  }

  protected Router createRouter() {
    return Router.router(vertx);
  }

  protected GraphQL setupGraphQLJava(String schemaFile) {
    String schema = vertx.fileSystem().readFileBlocking(schemaFile).toString();

    SchemaParser schemaParser = new SchemaParser();
    TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

    RuntimeWiring runtimeWiring = runtimeWiring();

    SchemaGenerator schemaGenerator = new SchemaGenerator();
    GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

    return GraphQL.newGraphQL(graphQLSchema)
      .build();
  }

  protected RuntimeWiring runtimeWiring() {
    return RuntimeWiring.newRuntimeWiring()
      .build();
  }
}

package io.github.tsegismont.graphql.workshop.webapp;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.ext.web.handler.graphql.GraphQLHandlerOptions;
import io.vertx.ext.web.handler.graphql.GraphiQLHandler;
import io.vertx.ext.web.handler.graphql.GraphiQLHandlerOptions;

public class WebappServer extends AbstractVerticle {

  @Override
  public void start() throws Exception {

    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());

    router.route("/graphql").handler(createGraphQLHandler());
    router.get("/graphiql/*").handler(createGraphiQLHandler());

    router.get().handler(StaticHandler.create());

    router.route().failureHandler(ErrorHandler.create(true));

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8080);

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

    RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
      .build();

    SchemaGenerator schemaGenerator = new SchemaGenerator();
    GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

    return GraphQL.newGraphQL(graphQLSchema)
      .build();
  }
}

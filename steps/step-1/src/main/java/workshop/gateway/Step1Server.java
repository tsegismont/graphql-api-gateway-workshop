package workshop.gateway;

import graphql.GraphQL;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.ErrorHandler;
import io.vertx.reactivex.ext.web.handler.StaticHandler;
import io.vertx.reactivex.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.reactivex.ext.web.handler.graphql.GraphiQLHandler;

public class Step1Server extends WorkshopVerticle {

  protected Router createRouter() {
    Router router = Router.router(vertx);

    // TODO: define generic route and set the BodyHandler

    // TODO: create GraphQL runtime with setupGraphQLJava method
    // TODO: define route for /graphql requests and set the GraphQLHandler
    // TODO: define route for /graphiql/* requests and set the GraphiQLHandler

    router.get().handler(StaticHandler.create());

    router.route().failureHandler(ErrorHandler.create());

    return router;
  }

  protected RuntimeWiring runtimeWiring() {
    return RuntimeWiring.newRuntimeWiring()
      .type("Query", this::query)
      .build();
  }

  private TypeRuntimeWiring.Builder query(TypeRuntimeWiring.Builder builder) {
    return builder
      // TODO: add data fetcher for the genres field of the root Query type
      ;
  }
}

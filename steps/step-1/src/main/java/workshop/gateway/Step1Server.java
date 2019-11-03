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

    // Define GraphQL and GraphiQL routes and set handlers

    router.get().handler(StaticHandler.create());

    router.route().failureHandler(ErrorHandler.create());

    return router;
  }

  protected RuntimeWiring runtimeWiring() {
    return RuntimeWiring.newRuntimeWiring()
      // Add type wiring for type Query
      .build();
  }

  private TypeRuntimeWiring.Builder query(TypeRuntimeWiring.Builder builder) {
    return builder
      // Add data fetcher for the genres field of the Query type
      ;
  }
}

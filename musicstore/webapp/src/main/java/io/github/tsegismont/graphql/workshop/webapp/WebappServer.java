package io.github.tsegismont.graphql.workshop.webapp;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class WebappServer extends AbstractVerticle {

  @Override
  public void start() throws Exception {

    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());

    router.get().handler(StaticHandler.create());

    router.route().failureHandler(ErrorHandler.create(true));

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8080);

  }
}

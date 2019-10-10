package io.github.tsegismont.graphql.workshop.inventory;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;

public class InventoryServer extends AbstractVerticle {

  @Override
  public void start() throws Exception {

    Router router = Router.router(vertx);

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8081);

  }
}

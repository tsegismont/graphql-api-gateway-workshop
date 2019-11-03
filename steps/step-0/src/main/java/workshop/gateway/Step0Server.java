package workshop.gateway;

import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.ErrorHandler;
import io.vertx.reactivex.ext.web.handler.StaticHandler;

public class Step0Server extends WorkshopVerticle {

  protected Router createRouter() {
    Router router = Router.router(vertx);

    // Define routes and set handlers

    return router;
  }
}

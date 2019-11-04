package workshop.gateway;

import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.ErrorHandler;
import io.vertx.reactivex.ext.web.handler.StaticHandler;

public class Step0Server extends WorkshopVerticle {

  protected Router createRouter() {
    Router router = Router.router(vertx);

    // TODO: create a route for GET requests and set the StaticHandler

    // TODO: create a generic route and set ErrorHandler as the failureHandler

    return router;
  }
}

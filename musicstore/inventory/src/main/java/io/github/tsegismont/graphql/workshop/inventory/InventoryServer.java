package io.github.tsegismont.graphql.workshop.inventory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.Arrays;

public class InventoryServer extends AbstractVerticle {

  private JsonArray genres;

  @Override
  public void start() throws Exception {

    loadData();

    Router router = Router.router(vertx);

    Router apiRouter = Router.router(vertx);
    apiRouter.route().handler(this::setResponseContentType);
    apiRouter.get("/genres").handler(this::allGenres);

    router.mountSubRouter("/api", apiRouter);

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8081);

  }

  private void loadData() {
    String content = vertx.fileSystem().readFileBlocking("data/genres.csv").toString();
    String[] lines = content.split("\n");
    genres = Arrays.stream(lines)
      .skip(1)
      .map(line -> line.split(","))
      .map(row -> new JsonObject()
        .put("id", Integer.parseInt(row[0].trim()))
        .put("name", row[1].trim())
        .put("image", row[2].trim())
      ).collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
  }

  private void setResponseContentType(RoutingContext rc) {
    rc.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    rc.next();
  }

  private void allGenres(RoutingContext rc) {
    rc.response().end(genres.toBuffer());
  }
}

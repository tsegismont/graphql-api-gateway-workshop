package io.github.tsegismont.graphql.workshop.inventory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class InventoryServer extends AbstractVerticle {

  private JsonArray genres;
  private JsonArray albums;

  @Override
  public void start() throws Exception {

    genres = loadGenreData();
    albums = loadAlbumData();

    Router router = Router.router(vertx);

    Router apiRouter = Router.router(vertx);
    apiRouter.route().handler(this::setResponseContentType);
    apiRouter.get("/genres").handler(this::allGenres);
    apiRouter.get("/albums").handler(this::allAlbums);

    router.mountSubRouter("/api", apiRouter);

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8081);

  }

  private JsonArray loadGenreData() {
    String content = vertx.fileSystem().readFileBlocking("data/genres.csv").toString();
    String[] lines = content.split("\n");
    return Arrays.stream(lines)
      .skip(1)
      .map(line -> line.split(","))
      .map(row -> new JsonObject()
        .put("id", Integer.parseInt(row[0].trim()))
        .put("name", row[1].trim())
        .put("image", row[2].trim())
      )
      .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
  }

  private JsonArray loadAlbumData() {
    String content = vertx.fileSystem().readFileBlocking("data/albums.csv").toString();
    String[] lines = content.split("\n");
    return Arrays.stream(lines)
      .skip(1)
      .map(line -> line.split(","))
      .map(row -> new JsonObject()
        .put("id", Integer.parseInt(row[0].trim()))
        .put("name", row[1].trim())
        .put("genre", genres.stream()
          .map(JsonObject.class::cast)
          .filter(genre -> genre.getInteger("id").equals(Integer.parseInt(row[2].trim())))
          .findFirst()
          .orElse(null)
        )
        .put("artist", row[3].trim())
        .put("image", row[4].trim())
      )
      .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
  }

  private void setResponseContentType(RoutingContext rc) {
    rc.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    rc.next();
  }

  private void allGenres(RoutingContext rc) {
    rc.response().end(genres.toBuffer());
  }

  private void allAlbums(RoutingContext rc) {
    List<Integer> filter = rc.queryParam("genre").stream().map(Integer::parseInt).collect(toList());
    if (filter.isEmpty()) {
      rc.response().end(albums.toBuffer());
    } else {
      JsonArray filtered = albums.stream()
        .map(JsonObject.class::cast)
        .filter(album -> filter.contains(album.getJsonObject("genre").getInteger("id")))
        .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
      rc.response().end(filtered.toBuffer());
    }
  }
}

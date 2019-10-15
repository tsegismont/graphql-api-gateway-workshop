package io.github.tsegismont.graphql.workshop.inventory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.ErrorHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class InventoryServer extends AbstractVerticle {

  private JsonArray genres;
  private JsonArray albums;
  private Map<Integer, JsonArray> tracksByAlbum;

  @Override
  public void start() throws Exception {

    genres = loadGenreData();
    albums = loadAlbumData();
    tracksByAlbum = loadTrackData();

    Router router = Router.router(vertx);

    router.route().handler(this::setResponseContentType);
    router.get("/genres").handler(this::allGenres);
    router.get("/albums").handler(this::allAlbums);
    router.get("/album/:id").handler(this::album);
    router.get("/album/:id/tracks").handler(this::tracks);
    router.route().failureHandler(ErrorHandler.create(true));

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

  private Map<Integer, JsonArray> loadTrackData() {
    String content = vertx.fileSystem().readFileBlocking("data/tracks.csv").toString();
    String[] lines = content.split("\n");
    return Arrays.stream(lines)
      .skip(1)
      .map(line -> line.split(","))
      .map(row -> new JsonObject()
        .put("album", Integer.parseInt(row[0].trim()))
        .put("number", Integer.parseInt(row[1].trim()))
        .put("name", row[2].trim())
      )
      .collect(
        groupingBy(
          json -> json.getInteger("album"),
          Collector.of(
            JsonArray::new,
            (array, value) -> array.add(new JsonObject().put("number", value.getInteger("number")).put("name", value.getString("name"))),
            JsonArray::addAll
          )
        )
      );
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

  private void album(RoutingContext rc) {
    boolean withTracks = rc.queryParams().contains("withTracks");
    Optional<JsonObject> album = albums.stream()
      .map(JsonObject.class::cast)
      .filter(json -> json.getInteger("id").equals(Integer.parseInt(rc.pathParam("id"))))
      .findFirst()
      .map(json -> withTracks ? json.copy().put("tracks", tracksByAlbum.get(json.getInteger("id"))) : json);
    if (album.isPresent()) {
      rc.response().end(album.get().toBuffer());
    } else {
      rc.fail(404);
    }
  }

  private void tracks(RoutingContext rc) {
    JsonArray tracks = tracksByAlbum.get(Integer.parseInt(rc.pathParam("id")));
    if (tracks != null) {
      rc.response().end(tracks.toBuffer());
    } else {
      rc.fail(404);
    }
  }
}

package io.github.tsegismont.graphql.workshop.inventory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class InventoryServer extends AbstractVerticle {

  private DataRepository dataRepository;

  @Override
  public void start() throws Exception {

    dataRepository = new DataRepository(vertx);

    Router router = Router.router(vertx);

    router.route().handler(LoggerHandler.create(LoggerFormat.TINY));

    router.route().handler(this::setResponseContentType);

    router.get("/genres").handler(this::allGenres);
    router.get("/albums").handler(this::allAlbums);
    router.get("/album/:id").handler(this::album);
    router.get("/album/:id/tracks").handler(this::tracks);
    router.route().failureHandler(ErrorHandler.create());

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8081);
  }

  private void setResponseContentType(RoutingContext rc) {
    rc.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    rc.next();
  }

  private void allGenres(RoutingContext rc) {
    rc.response().end(dataRepository.genres.toBuffer());
  }

  private void allAlbums(RoutingContext rc) {
    List<Integer> filter = rc.queryParam("genre").stream().map(Integer::parseInt).collect(toList());
    if (filter.isEmpty()) {
      rc.response().end(dataRepository.albums.toBuffer());
    } else {
      JsonArray filtered = dataRepository.albums.stream()
        .map(JsonObject.class::cast)
        .filter(album -> filter.contains(album.getJsonObject("genre").getInteger("id")))
        .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
      rc.response().end(filtered.toBuffer());
    }
  }

  private void album(RoutingContext rc) {
    boolean withTracks = rc.queryParams().contains("withTracks");
    Optional<JsonObject> album = dataRepository.albums.stream()
      .map(JsonObject.class::cast)
      .filter(json -> json.getInteger("id").equals(Integer.parseInt(rc.pathParam("id"))))
      .findFirst()
      .map(json -> withTracks ? json.copy().put("tracks", dataRepository.tracksByAlbum.get(json.getInteger("id"))) : json);
    if (album.isPresent()) {
      rc.response().end(album.get().toBuffer());
    } else {
      rc.fail(404);
    }
  }

  private void tracks(RoutingContext rc) {
    JsonArray tracks = dataRepository.tracksByAlbum.get(Integer.parseInt(rc.pathParam("id")));
    if (tracks != null) {
      rc.response().end(tracks.toBuffer());
    } else {
      rc.fail(404);
    }
  }
}

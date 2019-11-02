package workshop.inventory;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collector;

import static java.util.stream.Collectors.groupingBy;

public class DataRepository {

  public final JsonArray genres;
  public final JsonArray albums;
  public final Map<Integer, JsonArray> tracksByAlbum;

  public DataRepository(Vertx vertx) {
    genres = loadGenreData(vertx);
    albums = loadAlbumData(vertx);
    tracksByAlbum = loadTrackData(vertx);
  }

  public JsonArray loadGenreData(Vertx vertx) {
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

  public JsonArray loadAlbumData(Vertx vertx) {
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

  public Map<Integer, JsonArray> loadTrackData(Vertx vertx) {
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
}

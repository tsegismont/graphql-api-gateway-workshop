package workshop.repository;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.codec.BodyCodec;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ListOfCodec {

  public static <T> BodyCodec<List<T>> create(Class<T> listType) {
    return BodyCodec.create(buffer -> {
      return buffer.toJsonArray()
        .stream()
        .map(JsonObject.class::cast)
        .map(json -> json.mapTo(listType))
        .collect(toList());
    });
  }
}

package workshop.gateway;

import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.client.predicate.ResponsePredicate;
import io.vertx.reactivex.ext.web.codec.BodyCodec;

public class TracksRepository {

  private final WebClient inventoryClient;

  public TracksRepository(WebClient inventoryClient) {
    this.inventoryClient = inventoryClient;
  }

  public Single<JsonArray> findByAlbum(Integer id) {
    return inventoryClient.get("/album/" + id + "/tracks")
      .expect(ResponsePredicate.SC_OK)
      .expect(ResponsePredicate.JSON)
      .as(BodyCodec.jsonArray())
      .rxSend()
      .map(HttpResponse::body);
  }
}

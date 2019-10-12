package io.github.tsegismont.graphql.workshop.webapp;

import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.ext.web.client.HttpRequest;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.client.predicate.ResponsePredicate;
import io.vertx.reactivex.ext.web.codec.BodyCodec;

public class AlbumsRepository {

  private final WebClient inventoryClient;

  public AlbumsRepository(WebClient inventoryClient) {
    this.inventoryClient = inventoryClient;
  }

  public Single<JsonArray> findAll(Integer genre) {
    HttpRequest<JsonArray> request = inventoryClient.get("/api/albums")
      .expect(ResponsePredicate.SC_OK)
      .expect(ResponsePredicate.JSON)
      .as(BodyCodec.jsonArray());
    if (genre != null) {
      request.addQueryParam("genre", genre.toString());
    }
    return request
      .rxSend()
      .map(HttpResponse::body);
  }
}

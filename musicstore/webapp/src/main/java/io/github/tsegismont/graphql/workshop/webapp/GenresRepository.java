package io.github.tsegismont.graphql.workshop.webapp;

import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.client.predicate.ResponsePredicate;
import io.vertx.reactivex.ext.web.codec.BodyCodec;

public class GenresRepository {

  private final WebClient inventoryClient;

  public GenresRepository(WebClient inventoryClient) {
    this.inventoryClient = inventoryClient;
  }

  public Single<JsonArray> findAll() {
    return inventoryClient.get("/api/genres")
      .expect(ResponsePredicate.SC_OK)
      .expect(ResponsePredicate.JSON)
      .as(BodyCodec.jsonArray())
      .rxSend()
      .map(HttpResponse::body);
  }
}

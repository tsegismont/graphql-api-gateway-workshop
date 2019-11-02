package workshop.repository;

import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.client.predicate.ResponsePredicate;
import io.vertx.reactivex.ext.web.codec.BodyCodec;

public class RatingRepository {

  private final WebClient ratingClient;

  public RatingRepository(WebClient ratingClient) {
    this.ratingClient = ratingClient;
  }

  public Single<JsonObject> findRatingByAlbum(Integer albumId) {
    return ratingClient.get("/album/" + albumId + "/rating")
      .expect(ResponsePredicate.SC_OK)
      .expect(ResponsePredicate.JSON)
      .as(BodyCodec.jsonObject())
      .rxSend()
      .map(HttpResponse::body);
  }

  public Single<JsonArray> findReviewsByAlbum(Integer albumId) {
    return ratingClient.get("/album/" + albumId + "/reviews")
      .expect(ResponsePredicate.SC_OK)
      .expect(ResponsePredicate.JSON)
      .as(BodyCodec.jsonArray())
      .rxSend()
      .map(HttpResponse::body);
  }

  public Single<JsonObject> findRatingAndReviewsByAlbum(Integer albumId) {
    return ratingClient.get("/album/" + albumId)
      .expect(ResponsePredicate.SC_OK)
      .expect(ResponsePredicate.JSON)
      .as(BodyCodec.jsonObject())
      .rxSend()
      .map(HttpResponse::body);
  }

  public Single<JsonObject> addReview(Integer albumId, JsonObject review) {
    return ratingClient.post("/album/" + albumId + "/reviews")
      .expect(ResponsePredicate.SC_OK)
      .expect(ResponsePredicate.JSON)
      .as(BodyCodec.jsonObject())
      .rxSendJsonObject(review)
      .map(HttpResponse::body);
  }
}

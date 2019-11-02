package workshop.repository;

import io.reactivex.Single;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.client.predicate.ResponsePredicate;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import workshop.model.RatingInfo;
import workshop.model.Review;
import workshop.model.ReviewInput;

import java.util.List;

public class RatingRepository {

  private final WebClient ratingClient;

  public RatingRepository(WebClient ratingClient) {
    this.ratingClient = ratingClient;
  }

  public Single<Integer> findRatingByAlbum(Integer albumId) {
    return ratingClient.get("/album/" + albumId + "/rating")
      .expect(ResponsePredicate.SC_OK)
      .expect(ResponsePredicate.JSON)
      .as(BodyCodec.jsonObject())
      .rxSend()
      .map(HttpResponse::body)
      .map(json -> json.getInteger("value"));
  }

  public Single<List<Review>> findReviewsByAlbum(Integer albumId) {
    return ratingClient.get("/album/" + albumId + "/reviews")
      .expect(ResponsePredicate.SC_OK)
      .expect(ResponsePredicate.JSON)
      .as(ListOfCodec.create(Review.class))
      .rxSend()
      .map(HttpResponse::body);
  }

  public Single<RatingInfo> findRatingAndReviewsByAlbum(Integer albumId) {
    return ratingClient.get("/album/" + albumId)
      .expect(ResponsePredicate.SC_OK)
      .expect(ResponsePredicate.JSON)
      .as(BodyCodec.json(RatingInfo.class))
      .rxSend()
      .map(HttpResponse::body);
  }

  public Single<RatingInfo> addReview(Integer albumId, ReviewInput reviewInput) {
    return ratingClient.post("/album/" + albumId + "/reviews")
      .expect(ResponsePredicate.SC_OK)
      .expect(ResponsePredicate.JSON)
      .as(BodyCodec.json(RatingInfo.class))
      .rxSendJson(reviewInput)
      .map(HttpResponse::body);
  }
}

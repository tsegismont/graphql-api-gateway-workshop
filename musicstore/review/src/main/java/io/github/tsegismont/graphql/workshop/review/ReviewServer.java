package io.github.tsegismont.graphql.workshop.review;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ErrorHandler;

import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.averagingDouble;

public class ReviewServer extends AbstractVerticle {

  private final Map<Integer, JsonArray> reviewsByAlbum = new HashMap<>();

  @Override
  public void start() throws Exception {

    Router router = Router.router(vertx);

    router.route().handler(this::setResponseContentType);

    router.get("/album/:id").handler(this::dataToContext).handler(this::ratingAndReviews);
    router.get("/album/:id/rating").handler(this::dataToContext).handler(this::rating);
    router.get("/album/:id/reviews").handler(this::dataToContext).handler(this::reviews);
    router.post("/album/:id/reviews").handler(this::dataToContext).handler(BodyHandler.create()).handler(this::addReview);

    router.route().failureHandler(ErrorHandler.create());

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8082);
  }

  private void setResponseContentType(RoutingContext rc) {
    rc.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    rc.next();
  }

  private void dataToContext(RoutingContext rc) {
    Integer id = Integer.valueOf(rc.pathParam("id"));
    rc.put("albumId", id);
    rc.put("reviews", reviewsByAlbum.computeIfAbsent(rc.get("albumId"), k -> new JsonArray()));
    rc.next();
  }

  private void ratingAndReviews(RoutingContext rc) {
    JsonArray reviews = rc.get("reviews");
    rc.response().end(new JsonObject().put("rating", avgRating(reviews)).put("reviews", reviews).toBuffer());
  }

  private void rating(RoutingContext rc) {
    JsonArray reviews = rc.get("reviews");
    Double rating = avgRating(reviews);
    rc.response().end(new JsonObject().put("value", rating).toBuffer());
  }

  private void reviews(RoutingContext rc) {
    JsonArray reviews = rc.get("reviews");
    rc.response().end(reviews.toBuffer());
  }

  private void addReview(RoutingContext rc) {
    JsonObject submitted = rc.getBodyAsJson();
    JsonArray reviews = rc.get("reviews");
    reviews.add(submitted);
    ratingAndReviews(rc);
  }

  private Double avgRating(JsonArray reviews) {
    return reviews.stream()
      .map(JsonObject.class::cast)
      .collect(averagingDouble(review -> review.getDouble("rating")));
  }
}

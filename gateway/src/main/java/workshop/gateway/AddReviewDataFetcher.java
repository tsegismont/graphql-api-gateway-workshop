package workshop.gateway;

import graphql.schema.DataFetchingEnvironment;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import workshop.fetcher.RxDataFetcher;
import workshop.model.RatingInfo;
import workshop.model.ReviewInput;
import workshop.repository.RatingRepository;

import java.util.Map;

public class AddReviewDataFetcher implements RxDataFetcher<RatingInfo> {

  private final RatingRepository ratingRepository;

  public AddReviewDataFetcher(RatingRepository ratingRepository) {
    this.ratingRepository = ratingRepository;
  }

  @Override
  public Single<RatingInfo> rxGet(DataFetchingEnvironment env) throws Exception {
    String currentUser = UserUtil.currentUser(env);
    if (currentUser==null) {
      throw new NotLoggedInException();
    }
    Integer albumId = Integer.valueOf(env.getArgument("albumId"));
    ReviewInput reviewInput = new JsonObject((Map<String, Object>) env.getArgument("review"))
      .mapTo(ReviewInput.class);
    reviewInput.setName(currentUser);
    return ratingRepository.addReview(albumId, reviewInput);
  }
}

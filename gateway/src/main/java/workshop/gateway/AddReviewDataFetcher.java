package workshop.gateway;

import graphql.schema.DataFetchingEnvironment;
import io.reactivex.Single;
import workshop.model.RatingInfo;
import workshop.model.ReviewInput;
import workshop.repository.RatingRepository;

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
    Integer albumId = EnvironmentUtil.getIntegerArgument(env, "albumId");
    ReviewInput reviewInput = EnvironmentUtil.getInputArgument(env, "review", ReviewInput.class);
    reviewInput.setName(currentUser);
    return ratingRepository.addReview(albumId, reviewInput);
  }
}

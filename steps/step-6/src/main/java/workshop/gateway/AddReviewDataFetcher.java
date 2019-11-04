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
    // Get albumId argument from env
    // Get review input argument from env
    // Set currentUser in input
    // Add review and return rating info
    return null;
  }
}

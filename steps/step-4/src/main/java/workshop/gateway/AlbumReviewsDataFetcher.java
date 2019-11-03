package workshop.gateway;

import graphql.schema.DataFetchingEnvironment;
import io.reactivex.Single;
import workshop.model.Album;
import workshop.model.Review;
import workshop.repository.RatingRepository;

import java.util.List;

public class AlbumReviewsDataFetcher implements RxDataFetcher<List<Review>> {

  private final RatingRepository ratingRepository;

  public AlbumReviewsDataFetcher(RatingRepository ratingRepository) {
    this.ratingRepository = ratingRepository;
  }

  @Override
  public Single<List<Review>> rxGet(DataFetchingEnvironment env) throws Exception {
    // Retrieve source Album from env
    // If reviews are set, return right away with Single#just
    // Otherwise, find reviews by album id
    return null;
  }
}

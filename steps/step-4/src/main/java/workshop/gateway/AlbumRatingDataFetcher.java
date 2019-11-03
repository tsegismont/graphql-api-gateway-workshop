package workshop.gateway;

import graphql.schema.DataFetchingEnvironment;
import io.reactivex.Single;
import workshop.model.Album;
import workshop.repository.RatingRepository;

public class AlbumRatingDataFetcher implements RxDataFetcher<Integer> {

  private final RatingRepository ratingRepository;

  public AlbumRatingDataFetcher(RatingRepository ratingRepository) {
    this.ratingRepository = ratingRepository;
  }

  @Override
  public Single<Integer> rxGet(DataFetchingEnvironment env) throws Exception {
    // Retrieve source Album from env
    // If rating is set, return right away with Single#just
    // Otherwise, find rating by album id
    return null;
  }
}

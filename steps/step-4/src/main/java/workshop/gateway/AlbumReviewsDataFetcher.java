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
    Album album = env.getSource();
    Single<List<Review>> reviews;
    if (album.getReviews()!=null) {
      reviews = Single.just(album.getReviews());
    } else {
      reviews = ratingRepository.findReviewsByAlbum(album.getId());
    }
    return reviews;
  }
}

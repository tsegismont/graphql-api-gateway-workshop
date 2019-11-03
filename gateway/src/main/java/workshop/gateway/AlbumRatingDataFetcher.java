package workshop.gateway;

import graphql.schema.DataFetchingEnvironment;
import io.reactivex.Single;
import workshop.fetcher.RxDataFetcher;
import workshop.model.Album;
import workshop.repository.RatingRepository;

public class AlbumRatingDataFetcher implements RxDataFetcher<Integer> {

  private final RatingRepository ratingRepository;

  public AlbumRatingDataFetcher(RatingRepository ratingRepository) {
    this.ratingRepository = ratingRepository;
  }

  @Override
  public Single<Integer> rxGet(DataFetchingEnvironment env) throws Exception {
    Album album = env.getSource();
    Single<Integer> rating;
    if (album.getRating()!=null) {
      rating = Single.just(album.getRating());
    } else {
      rating = ratingRepository.findRatingByAlbum(album.getId());
    }
    return rating;
  }
}

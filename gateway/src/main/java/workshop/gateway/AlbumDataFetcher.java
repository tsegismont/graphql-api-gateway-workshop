package workshop.gateway;

import graphql.schema.DataFetchingEnvironment;
import io.reactivex.Single;
import workshop.model.Album;
import workshop.model.RatingInfo;
import workshop.repository.AlbumsRepository;
import workshop.repository.RatingRepository;

public class AlbumDataFetcher implements RxDataFetcher<Album> {

  private final AlbumsRepository albumsRepository;
  private final RatingRepository ratingRepository;

  public AlbumDataFetcher(AlbumsRepository albumsRepository, RatingRepository ratingRepository) {
    this.albumsRepository = albumsRepository;
    this.ratingRepository = ratingRepository;
  }

  @Override
  public Single<Album> rxGet(DataFetchingEnvironment env) {
    Integer id = Integer.valueOf(env.getArgument("id"));
    Single<Album> inventoryData = albumsRepository.findById(id, true);
    Single<RatingInfo> ratingData = ratingRepository.findRatingAndReviewsByAlbum(id);
    return inventoryData.zipWith(ratingData, (a, r) -> {
      a.setRating(r.getRating());
      a.setReviews(r.getReviews());
      return a;
    });
  }
}

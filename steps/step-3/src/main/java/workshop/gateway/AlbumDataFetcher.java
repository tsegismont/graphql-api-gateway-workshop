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
    Integer id = env.getArgument("id");
    // TODO: find album by id with tracks included
    // TODO: find rating info by id
    // TODO: use Single#zipWith operator for concurrent requests execution and merge results
    return null;
  }
}

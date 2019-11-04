package workshop.gateway;

import graphql.schema.DataFetchingEnvironment;
import io.reactivex.Single;
import workshop.model.Album;
import workshop.repository.AlbumsRepository;

import java.util.List;

public class AlbumsDataFetcher implements RxDataFetcher<List<Album>> {

  private final AlbumsRepository albumsRepository;

  public AlbumsDataFetcher(AlbumsRepository albumsRepository) {
    this.albumsRepository = albumsRepository;
  }

  @Override
  public Single<List<Album>> rxGet(DataFetchingEnvironment env) {
    Integer genre = env.getArgument("genre");
    return albumsRepository.findAll(genre);
  }
}

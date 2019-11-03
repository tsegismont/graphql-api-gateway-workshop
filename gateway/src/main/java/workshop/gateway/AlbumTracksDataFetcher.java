package workshop.gateway;

import graphql.schema.DataFetchingEnvironment;
import io.reactivex.Single;
import workshop.fetcher.RxDataFetcher;
import workshop.model.Album;
import workshop.model.Track;
import workshop.repository.TracksRepository;

import java.util.List;

public class AlbumTracksDataFetcher implements RxDataFetcher<List<Track>> {

  private final TracksRepository tracksRepository;

  public AlbumTracksDataFetcher(TracksRepository tracksRepository) {
    this.tracksRepository = tracksRepository;
  }

  @Override
  public Single<List<Track>> rxGet(DataFetchingEnvironment env) throws Exception {
    Album album = env.getSource();
    Single<List<Track>> tracks;
    if (album.getTracks()!=null) {
      tracks = Single.just(album.getTracks());
    } else {
      tracks = tracksRepository.findByAlbum(album.getId());
    }
    return tracks;
  }
}

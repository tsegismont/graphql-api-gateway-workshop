package workshop.gateway;

import graphql.schema.DataFetchingEnvironment;
import io.reactivex.Single;
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
    // TODO: retrieve source Album from env
    // TODO: if tracks are set, return right away with Single#just
    // TODO: otherwise, find tracks by album id
    return null;
  }
}

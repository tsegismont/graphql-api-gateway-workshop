package workshop.repository;

import io.reactivex.Single;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.client.predicate.ResponsePredicate;
import workshop.model.Track;

import java.util.List;

public class TracksRepository {

  private final WebClient inventoryClient;

  public TracksRepository(WebClient inventoryClient) {
    this.inventoryClient = inventoryClient;
  }

  public Single<List<Track>> findByAlbum(Integer id) {
    return inventoryClient.get("/album/" + id + "/tracks")
      .expect(ResponsePredicate.SC_OK)
      .expect(ResponsePredicate.JSON)
      .as(ListOfCodec.create(Track.class))
      .rxSend()
      .map(HttpResponse::body);
  }
}

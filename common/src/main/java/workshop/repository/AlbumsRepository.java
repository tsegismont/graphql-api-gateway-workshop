package workshop.repository;

import io.reactivex.Single;
import io.vertx.reactivex.ext.web.client.HttpRequest;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.client.predicate.ResponsePredicate;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import workshop.model.Album;

import java.util.List;

public class AlbumsRepository {

  private final WebClient inventoryClient;

  public AlbumsRepository(WebClient inventoryClient) {
    this.inventoryClient = inventoryClient;
  }

  public Single<List<Album>> findAll(Integer genre) {
    HttpRequest<List<Album>> request = inventoryClient.get("/albums")
      .expect(ResponsePredicate.SC_OK)
      .expect(ResponsePredicate.JSON)
      .as(ListOfCodec.create(Album.class));
    if (genre != null) {
      request.addQueryParam("genre", genre.toString());
    }
    return request
      .rxSend()
      .map(HttpResponse::body);
  }

  public Single<Album> findById(Integer id, boolean withTracks) {
    HttpRequest<Album> request = inventoryClient.get("/album/" + id)
      .expect(ResponsePredicate.SC_OK)
      .expect(ResponsePredicate.JSON)
      .as(BodyCodec.json(Album.class));
    if (withTracks) {
      request.addQueryParam("withTracks", "");
    }
    return request
      .rxSend()
      .map(HttpResponse::body);
  }
}

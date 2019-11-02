package workshop.repository;

import io.reactivex.Single;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.client.predicate.ResponsePredicate;
import workshop.model.Genre;

import java.util.List;

public class GenresRepository {

  private final WebClient inventoryClient;

  public GenresRepository(WebClient inventoryClient) {
    this.inventoryClient = inventoryClient;
  }

  public Single<List<Genre>> findAll() {
    return inventoryClient.get("/genres")
      .expect(ResponsePredicate.SC_OK)
      .expect(ResponsePredicate.JSON)
      .as(ListOfCodec.create(Genre.class))
      .rxSend()
      .map(HttpResponse::body);
  }
}

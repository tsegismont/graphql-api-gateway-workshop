package workshop.gateway;

import graphql.schema.DataFetchingEnvironment;
import io.reactivex.Single;
import workshop.model.Album;
import workshop.model.CartItem;
import workshop.repository.AlbumsRepository;

public class CartItemAlbumDataFetcher implements RxDataFetcher<Album> {

  private final AlbumsRepository albumsRepository;

  public CartItemAlbumDataFetcher(AlbumsRepository albumsRepository) {
    this.albumsRepository = albumsRepository;
  }

  @Override
  public Single<Album> rxGet(DataFetchingEnvironment env) throws Exception {
    // Retrieve source CartItem from env
    // Find corresponding *without loading tracks*
    return null;
  }
}

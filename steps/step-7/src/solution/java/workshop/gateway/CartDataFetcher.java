package workshop.gateway;

import graphql.schema.DataFetchingEnvironment;
import io.reactivex.Single;
import workshop.model.Cart;
import workshop.repository.CartRepository;

public class CartDataFetcher implements RxDataFetcher<Cart> {

  private final CartRepository cartRepository;

  public CartDataFetcher(CartRepository cartRepository) {
    this.cartRepository = cartRepository;
  }

  @Override
  public Single<Cart> rxGet(DataFetchingEnvironment env) {
    String curentUser = UserUtil.currentUser(env);
    return curentUser==null ? null:cartRepository.findCart(curentUser);
  }
}

package workshop.gateway;

import graphql.schema.DataFetchingEnvironment;
import io.reactivex.Single;
import workshop.model.Cart;
import workshop.repository.CartRepository;

public class RemoveFromCartDataFetcher implements RxDataFetcher<Cart> {

  private final CartRepository cartRepository;

  public RemoveFromCartDataFetcher(CartRepository cartRepository) {
    this.cartRepository = cartRepository;
  }

  @Override
  public Single<Cart> rxGet(DataFetchingEnvironment env) throws Exception {
    String currentUser = UserUtil.currentUser(env);
    if (currentUser==null) {
      throw new NotLoggedInException();
    }
    Integer albumId = Integer.valueOf(env.getArgument("albumId"));
    return cartRepository.removeFromCart(currentUser, albumId).andThen(cartRepository.findCart(currentUser));
  }
}

package workshop.gateway;

import graphql.schema.DataFetchingEnvironment;
import io.reactivex.Single;
import workshop.model.Cart;
import workshop.repository.CartRepository;

public class AddToCartDataFetcher implements RxDataFetcher<Cart> {

  private final CartRepository cartRepository;

  public AddToCartDataFetcher(CartRepository cartRepository) {
    this.cartRepository = cartRepository;
  }

  @Override
  public Single<Cart> rxGet(DataFetchingEnvironment env) throws Exception {
    String currentUser = UserUtil.currentUser(env);
    if (currentUser==null) {
      throw new NotLoggedInException();
    }
    Integer albumId = EnvironmentUtil.getIntegerArgument(env, "albumId");
    return cartRepository.addToCart(currentUser, albumId).andThen(cartRepository.findCart(currentUser));
  }
}

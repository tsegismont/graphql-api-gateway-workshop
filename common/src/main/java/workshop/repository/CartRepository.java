package workshop.repository;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.reactivex.pgclient.PgPool;
import io.vertx.reactivex.sqlclient.Row;
import io.vertx.reactivex.sqlclient.Tuple;
import workshop.model.Cart;
import workshop.model.CartItem;

public class CartRepository {

  private static final String FIND_CART = "select album_id, quantity from cart where username = $1 and quantity > 0";

  private static final String ADD_TO_CART = "insert into cart (username, album_id, quantity)"
    + " "
    + "values ($1, $2, 1)"
    + " "
    + "on conflict (username, album_id) do update set quantity = cart.quantity + 1";

  private static final String REMOVE_FROM_CART = "update cart set quantity = quantity - 1"
    + " "
    + "where username = $1 and album_id = $2 and quantity > 0";

  private final PgPool pool;

  public CartRepository(PgPool pool) {
    this.pool = pool;
  }

  public Single<Cart> findCart(String username) {
    return pool.rxPreparedQuery(FIND_CART, Tuple.of(username))
      .flatMapObservable(Observable::fromIterable)
      .map(CartRepository::rowToCartItem)
      .collectInto(new Cart(), Cart::add);
  }

  public Completable addToCart(String username, Integer albumId) {
    return pool.rxPreparedQuery(ADD_TO_CART, Tuple.of(username, albumId))
      .ignoreElement();
  }

  public Completable removeFromCart(String username, Integer albumId) {
    return pool.rxPreparedQuery(REMOVE_FROM_CART, Tuple.of(username, albumId))
      .ignoreElement();
  }

  private static CartItem rowToCartItem(Row row) {
    return new CartItem(row.getInteger("album_id"), row.getInteger("quantity"));
  }
}

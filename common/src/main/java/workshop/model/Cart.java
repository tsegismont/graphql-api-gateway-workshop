package workshop.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {

  private List<CartItem> items;

  public List<CartItem> getItems() {
    return items;
  }

  public void setItems(List<CartItem> items) {
    this.items = items;
  }

  public void add(CartItem cartItem) {
    if (items==null) {
      items = new ArrayList<>();
    }
    items.add(cartItem);
  }

  @Override
  public String toString() {
    return "Cart{" +
      "items=" + items +
      '}';
  }
}

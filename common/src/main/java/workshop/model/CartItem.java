package workshop.model;

public class CartItem {

  private Integer albumId;
  private Integer quantity;

  public CartItem() {
  }

  public CartItem(Integer albumId, Integer quantity) {
    this.albumId = albumId;
    this.quantity = quantity;
  }

  public Integer getAlbumId() {
    return albumId;
  }

  public void setAlbumId(Integer albumId) {
    this.albumId = albumId;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  @Override
  public String toString() {
    return "CartItem{" +
      "albumId=" + albumId +
      ", quantity=" + quantity +
      '}';
  }
}

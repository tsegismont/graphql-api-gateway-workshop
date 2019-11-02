package workshop.model;

public class Genre {

  private Integer id;
  private String name;
  private String image;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  @Override
  public String toString() {
    return "Genre{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", image='" + image + '\'' +
      '}';
  }
}

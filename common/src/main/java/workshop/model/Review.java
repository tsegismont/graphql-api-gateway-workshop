package workshop.model;

public class Review {

  private String name;
  private Integer rating;
  private String comment;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public String toString() {
    return "Review{" +
      "name='" + name + '\'' +
      ", rating=" + rating +
      ", comment='" + comment + '\'' +
      '}';
  }
}

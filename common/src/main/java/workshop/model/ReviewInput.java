package workshop.model;

public class ReviewInput {

  private Integer rating;
  private String comment;
  private String name;

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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "ReviewInput{" +
      "rating=" + rating +
      ", comment='" + comment + '\'' +
      ", name='" + name + '\'' +
      '}';
  }
}

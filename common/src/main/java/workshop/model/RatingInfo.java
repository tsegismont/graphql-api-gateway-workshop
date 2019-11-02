package workshop.model;

import java.util.List;

public class RatingInfo {

  private Integer rating;
  private List<Review> reviews;

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  public List<Review> getReviews() {
    return reviews;
  }

  public void setReviews(List<Review> reviews) {
    this.reviews = reviews;
  }

  @Override
  public String toString() {
    return "ReviewResult{" +
      "rating=" + rating +
      ", reviews=" + reviews +
      '}';
  }
}

package workshop.model;

import java.util.List;

public class Album {

  private Integer id;
  private String name;
  private Genre genre;
  private String artist;
  private String image;
  private List<Track> tracks;
  private Integer rating;
  private List<Review> reviews;

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

  public Genre getGenre() {
    return genre;
  }

  public void setGenre(Genre genre) {
    this.genre = genre;
  }

  public String getArtist() {
    return artist;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public List<Track> getTracks() {
    return tracks;
  }

  public void setTracks(List<Track> tracks) {
    this.tracks = tracks;
  }

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
    return "Album{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", genre=" + genre +
      ", artist='" + artist + '\'' +
      ", image='" + image + '\'' +
      ", tracks=" + tracks +
      ", rating=" + rating +
      ", reviews=" + reviews +
      '}';
  }
}

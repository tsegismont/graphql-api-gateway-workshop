package workshop.gateway;

public class NotLoggedInException extends Exception {

  public NotLoggedInException() {
    super("Not logged in", null, false, false);
  }
}

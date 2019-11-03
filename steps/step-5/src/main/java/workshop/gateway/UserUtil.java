package workshop.gateway;

import graphql.schema.DataFetchingEnvironment;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;

public class UserUtil {

  public static String currentUser(DataFetchingEnvironment env) {
    // Get RoutingContext from env#getContext
    // Retrieve user from RoutingContext
    // If user is not null, return username attribute from principal
    return null;
  }
}

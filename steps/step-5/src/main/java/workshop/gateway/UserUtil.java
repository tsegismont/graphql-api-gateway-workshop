package workshop.gateway;

import graphql.schema.DataFetchingEnvironment;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;

public class UserUtil {

  public static String currentUser(DataFetchingEnvironment env) {
    // TODO: get RoutingContext from env#getContext
    // TODO: get user from RoutingContext
    // TODO: if user is not null, get the principal and return username, otherwise return null
    return null;
  }
}

package workshop.gateway;

import graphql.schema.DataFetchingEnvironment;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;

public class UserUtil {

  public static String currentUser(DataFetchingEnvironment env) {
    RoutingContext routingContext = env.getContext();
    User user = routingContext.user();
    return user!=null ? user.principal().getString("username"):null;
  }
}

package workshop.gateway;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public class CurrentUserDataFetcher implements DataFetcher<String> {

  @Override
  public String get(DataFetchingEnvironment env) throws Exception {
    return UserUtil.currentUser(env);
  }
}

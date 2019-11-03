package workshop.gateway;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import hu.akarnokd.rxjava2.interop.SingleInterop;
import io.reactivex.Single;

import java.util.concurrent.CompletionStage;

public interface RxDataFetcher<T> extends DataFetcher<CompletionStage<T>> {

  @Override
  default CompletionStage<T> get(DataFetchingEnvironment environment) throws Exception {
    Single<T> single = rxGet(environment);
    return single==null ? null:single.to(SingleInterop.get());
  }

  Single<T> rxGet(DataFetchingEnvironment env) throws Exception;
}

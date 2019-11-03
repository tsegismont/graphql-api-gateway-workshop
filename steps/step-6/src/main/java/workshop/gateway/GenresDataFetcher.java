package workshop.gateway;

import graphql.schema.DataFetchingEnvironment;
import io.reactivex.Single;
import workshop.model.Genre;
import workshop.repository.GenresRepository;

import java.util.List;

public class GenresDataFetcher implements RxDataFetcher<List<Genre>> {

  private final GenresRepository genresRepository;

  public GenresDataFetcher(GenresRepository genresRepository) {
    this.genresRepository = genresRepository;
  }

  @Override
  public Single<List<Genre>> rxGet(DataFetchingEnvironment env) {
    return genresRepository.findAll();
  }
}

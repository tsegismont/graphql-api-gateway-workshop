import {ApolloClient} from 'apollo-client';
import {createHttpLink} from 'apollo-link-http';
import {InMemoryCache} from 'apollo-cache-inmemory';

const defaultOptions = {
  query: {
    fetchPolicy: 'no-cache',
    errorPolicy: 'all',
  },
};

const link = createHttpLink({
  uri: '/graphql',
  credentials: 'same-origin'
});

const apolloClient = new ApolloClient({
  cache: new InMemoryCache(),
  link,
  defaultOptions,
});

export default apolloClient;

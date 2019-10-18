import {ApolloClient} from 'apollo-client';
import {createHttpLink} from 'apollo-link-http';
import {InMemoryCache} from 'apollo-cache-inmemory';

const link = createHttpLink({
  uri: '/graphql',
  credentials: 'same-origin'
});

const apolloClient = new ApolloClient({
  cache: new InMemoryCache({
    resultCaching: false
  }),
  link,
});

export default apolloClient;

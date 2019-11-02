module.exports = {
  devServer: {
    port: 3000,
    proxy: {
      '^/login.html$': {
        target: 'http://localhost:8080',
      },
      '^/logout$': {
        target: 'http://localhost:8080',
      },
      '^/images/*': {
        target: 'http://localhost:8080',
      },
      '^/graphql$': {
        target: 'http://localhost:8080'

      }
    }
  }
};

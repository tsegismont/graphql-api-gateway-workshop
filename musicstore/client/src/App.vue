<template>
  <div id="app">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <div class="container">
        <div class="collapse navbar-collapse">
          <router-link class="navbar-brand mr-auto" to="/">Music Store</router-link>
          <ul class="navbar-nav">
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" id="navbarDropdown" role="button" data-toggle="dropdown"
                 aria-haspopup="true" aria-expanded="false">
                <template v-if="currentUser">
                  {{ currentUser }}
                </template>
                <font-awesome-icon :icon="['fas', 'user-circle']" size="lg"/>
              </a>
              <div class="dropdown-menu dropdown-menu-right" aria-labelledby="navbarDropdown">
                <a class="dropdown-item" href="/logout" v-if="currentUser">Logout</a>
                <a class="dropdown-item" href="/login.html" v-else>Login</a>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </nav>
    <router-view/>
  </div>
</template>

<script>
    import apolloClient from "./shared/apollo-client";
    import gql from 'graphql-tag';

    const getCurrentUser = gql`
      {
        currentUser
      }
    `;
    export default {
        name: 'App',
        data() {
            return {
                currentUser: null,
            }
        },
        created() {
            this.fetchData();
        },
        methods: {
            fetchData() {
                apolloClient.query({
                    query: getCurrentUser
                }).then(result => {
                    this.currentUser = result.data.currentUser;
                }).catch(reason => {
                    console.error(reason);
                });
            }
        }
    }
</script>

<style>
  nav {
    margin-bottom: 2em;
  }

  div.row {
    margin-top: 2em;
  }

  div.card {
    margin: 0 2em 2em 0;
  }
</style>

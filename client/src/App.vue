<template>
  <div id="app">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <div class="container">
        <div class="collapse navbar-collapse">
          <router-link class="navbar-brand mr-auto" to="/">Music Store</router-link>
          <ul class="navbar-nav">
            <cart-nav v-bind:cart-total="cartTotal"/>
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
    <router-view :currentUser="currentUser"/>
  </div>
</template>

<script>
    import CartNav from "./components/CartNav";
    import apolloClient from "./shared/apollo-client";
    import gql from 'graphql-tag';

    const getCartTotal = gql`
      {
        cart {
          items {
            quantity
          }
        }
      }
    `;

    const getCurrentUser = gql`
      {
        currentUser
      }
    `;

    export default {
        name: 'App',
        components: {
            CartNav
        },
        data() {
            return {
                currentUser: null,
                cartTotal: 0,
            }
        },
        created() {
            this.fetchCurrentUser();
            this.$on('update-cart', this.updateCart);
        },
        methods: {
            fetchCurrentUser() {
                apolloClient.query({
                    query: getCurrentUser
                }).then(result => {
                    this.currentUser = result.data.currentUser;
                    if (this.currentUser) this.fetchCartTotal();
                }).catch(reason => {
                    console.error(reason);
                });
            },
            fetchCartTotal() {
                apolloClient.query({
                    query: getCartTotal
                }).then(result => {
                    const items = result.data.cart.items;
                    this.cartTotal = items.reduce((total, item) => total + item.quantity, 0);
                }).catch(reason => {
                    console.error(reason);
                });
            },
            updateCart(total) {
                this.cartTotal = total;
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

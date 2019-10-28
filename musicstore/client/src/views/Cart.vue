<template>
  <div class="container">
    <div v-if="loading">
      Loading...
    </div>
    <div v-if="error" class="alert alert-danger">
      {{ error }}
    </div>
    <table class="table" v-if="items && items.length > 0">
      <thead>
      <tr>
        <th scope="col">Album</th>
        <th scope="col">Quantity</th>
        <th scope="col">Actions</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="item in items" v-bind:key="item.album.id">
        <td>
          <div class="media">
            <router-link :to="'/album/'+item.album.id">
              <img :src="'/images/albums/'+item.album.image" class="align-self-start mr-5 cart-album"
                   :alt="item.album.name">
            </router-link>
            <div class="media-body">
              <h5 class="mt-0">
                <router-link :to="'/album/'+item.album.id">{{ item.album.name }}</router-link>
              </h5>
              <p class="text-secondary">{{ item.album.artist }}, {{ item.album.genre.name }}</p>
            </div>
          </div>
        </td>
        <td>{{ item.quantity }}</td>
        <td>
          <button type="button" class="btn btn-primary mr-2" v-on:click="addOne(item.album.id)">Add one</button>
          <button type="button" class="btn btn-secondary" v-on:click="removeOne(item.album.id)">Remove one</button>
        </td>
      </tr>
      </tbody>
    </table>
    <template v-else-if="!currentUser">
      Please log in
    </template>
    <template v-else>
      Cart is empty
    </template>
  </div>
</template>

<script>
    import apolloClient from '../shared/apollo-client';
    import gql from 'graphql-tag';

    const getCart = gql`
      {
        cart {
          items {
            album {
              id
              name
              artist
              genre {
                name
              }
              image
            }
            quantity
          }
        }
      }
    `;

    const addToCart = gql`
      mutation ($albumId: ID!) {
        addToCart(albumId: $albumId) {
          items {
            album {
              id
              name
              artist
              genre {
                name
              }
              image
            }
            quantity
          }
        }
      }
    `;

    const removeFromCart = gql`
      mutation ($albumId: ID!) {
        removeFromCart(albumId: $albumId) {
          items {
            album {
              id
              name
              artist
              genre {
                name
              }
              image
            }
            quantity
          }
        }
      }
    `;

    export default {
        name: "cart",
        data() {
            return {
                loading: false,
                items: null,
                error: null
            }
        },
        props: {
            currentUser: String
        },
        created() {
            this.fetchData();
        },
        watch: {
            '$route': 'fetchData'
        },
        methods: {
            fetchData() {
                if (!this.currentUser) {
                    return;
                }
                this.beforeQuery();
                apolloClient.query({
                    query: getCart
                }).then(result => {
                    this.items = result.data.cart.items;
                }).catch(this.handleFailure).finally(this.afterQuery);
            },
            addOne(albumId) {
                this.beforeQuery();
                apolloClient.mutate({
                    mutation: addToCart,
                    variables: {albumId}
                }).then(result => {
                    this.items = result.data.addToCart.items;
                    this.updateCartTotal();
                }).catch(this.handleFailure).finally(this.afterQuery);
            },
            removeOne(albumId) {
                this.beforeQuery();
                apolloClient.mutate({
                    mutation: removeFromCart,
                    variables: {albumId}
                }).then(result => {
                    this.items = result.data.removeFromCart.items;
                    this.updateCartTotal();
                }).catch(this.handleFailure).finally(this.afterQuery);
            },
            updateCartTotal() {
                const cartTotal = this.items.reduce((total, item) => total + item.quantity, 0);
                this.$parent.$emit('update-cart', cartTotal);
            },
            beforeQuery() {
                this.error = this.items = null;
                this.loading = true;
            }, handleFailure(reason) {
                console.error(reason);
                this.error = 'Failed to load data...';
            }, afterQuery() {
                this.loading = false;
            }
        }
    }
</script>

<style scoped>
  img.cart-album {
    max-width: 100px;
    height: auto;
  }
</style>

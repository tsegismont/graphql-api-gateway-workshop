<template>
  <div class="container">
    <div v-if="loading">
      Loading...
    </div>
    <div v-if="error" class="alert alert-danger">
      {{ error }}
    </div>
    <template v-if="album">
      <div class="row">
        <div class="col-sm">
          <img :src="'/images/albums/'+album.image" :alt="album.name">
        </div>
        <div class="col-sm">
          <h2>{{ album.name }}</h2>
          <p class="text-secondary">{{ album.artist }}, {{ album.genre.name }}</p>
          <p>
            <Stars :rating="album.rating"/>
            <span v-if="reviews">
              -
              <a href="#reviews">Read reviews</a>
            </span>
          </p>
          <button type="button" class="btn btn-primary" v-on:click="addToCart()">Add to cart</button>
        </div>
      </div>
      <div class="row" v-if="album">
        <h3>Tracks</h3>
        <table class="table">
          <thead class="thead-light">
          <tr>
            <th scope="col">#</th>
            <th scope="col">Name</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="track in album.tracks" v-bind:key="track.number">
            <th scope="row">{{ track.number }}</th>
            <td>{{ track.name }}</td>
          </tr>
          </tbody>
        </table>
      </div>
    </template>
    <template v-if="reviews">
      <hr>
      <h3>Reviews</h3>
      <div id="reviews">
        <div class="row">
          <ReviewCard
            v-for="(review, index) in reviews"
            v-bind:review="review"
            v-bind:key="index"/>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
    import Stars from '../components/Stars';
    import ReviewCard from "../components/ReviewCard";
    import apolloClient from '../shared/apollo-client';
    import {gql} from "apollo-boost";

    const findAlbum = gql`
      query ($id: ID!) {
        album(id: $id) {
          id
          name
          genre {
            name
          }
          artist
          image
          rating
          tracks {
            number
            name
          }
          reviews {
            name
            rating
            comment
          }
        }
      }
    `;

    export default {
        name: "album",
        data() {
            return {
                loading: false,
                album: null,
                reviews: null,
                error: null
            }
        },
        created() {
            this.fetchData();
        },
        watch: {
            '$route': 'fetchData'
        },
        methods: {
            fetchData() {
                this.error = this.album = this.reviews = null;
                this.loading = true;
                apolloClient.query({
                    query: findAlbum,
                    variables: {id: this.$route.params.id}
                }).then(result => {
                    this.album = result.data.album;
                    this.reviews = (this.album.reviews && this.album.reviews.length > 0) ? this.album.reviews : null;
                    this.reviews = [
                        {
                            name: 'John',
                            rating: 3.7,
                            comment: 'Great content!'
                        },
                        {
                            name: 'Claire',
                            rating: 1,
                            comment: 'Very poor...'
                        },
                        {
                            name: 'Paul',
                            rating: 5,
                            comment: 'The album of my life. Period'
                        },
                        {
                            name: 'Claire',
                            rating: 1,
                            comment: 'Very poor...'
                        },
                        {
                            name: 'Paul',
                            rating: 5,
                            comment: 'The album of my life. Period'
                        },
                        {
                            name: 'John',
                            rating: 3.7,
                            comment: 'Great content!'
                        },
                        {
                            name: 'Claire',
                            rating: 1,
                            comment: 'Very poor...'
                        },
                        {
                            name: 'Paul',
                            rating: 5,
                            comment: 'The album of my life. Period'
                        },
                    ];
                }).catch(reason => {
                    console.error(reason);
                    this.error = 'Failed to load data...';
                }).finally(() => {
                    this.loading = false;
                });
            },
            addToCart() {
                window.alert('Done!')
            }
        },
        components: {
            Stars,
            ReviewCard
        }
    }
</script>

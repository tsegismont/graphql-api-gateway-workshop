<template>
  <div class="container">
    <div v-if="loading">
      Loading...
    </div>
    <div v-if="error" class="alert alert-danger">
      {{ error }}
    </div>
    <template v-if="album">
      <div class="media">
        <img class="align-self-start mr-5" :src="'/images/albums/'+album.image" :alt="album.name">
        <div class="media-body">
          <h2>{{ album.name }}</h2>
          <p class="text-secondary">{{ album.artist }}, {{ album.genre.name }}</p>
          <p>
            <Stars :rating="rating"/>
            -
            <a href="#reviews">Reviews</a>
          </p>
          <button type="button" class="btn btn-primary" v-on:click="addToCart()">Add to cart</button>
        </div>
      </div>
      <TrackList :album="album"/>
    </template>
    <hr>
    <h3>Reviews</h3>
    <div id="reviews">
      <template v-if="review.errors.length">
        <p>Please correct the following error(s):</p>
        <ul>
          <li v-for="(error,index) in review.errors" v-bind:key="index">{{ error }}</li>
        </ul>
      </template>
      <form id="reviewForm" class="form-inline" v-on:submit.prevent="checkForm">
        <label for="reviewComment" class="mb-2 mr-sm-2 sr-only">Comment</label>
        <input class="form-control mb-2 mr-sm-2" id="reviewComment" placeholder="Enter comment"
               v-model="review.comment">
        <label class="mb-2 mr-sm-2" for="reviewRating">Rating</label>
        <select class="custom-select mb-2 mr-sm-2" id="reviewRating" v-model="review.rating">
          <option value="1">1</option>
          <option value="2">2</option>
          <option value="3">3</option>
          <option value="4">4</option>
          <option value="5">5</option>
        </select>
        <button type="submit" class="btn btn-primary mb-2 mr-sm-2" form="reviewForm">Submit</button>
      </form>
      <div class="row" v-if="reviews">
        <ReviewCard
          v-for="(review, index) in reviews"
          v-bind:review="review"
          v-bind:key="index"/>
      </div>
    </div>
  </div>
</template>

<script>
    import TrackList from "../components/TrackList";
    import Stars from '../components/Stars';
    import ReviewCard from "../components/ReviewCard";
    import apolloClient from '../shared/apollo-client';
    import gql from 'graphql-tag';

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

    const addReview = gql`
      mutation ($albumId: ID!, $review: ReviewInput!) {
        addReview(albumId: $albumId, review: $review) {
          rating
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
                rating: null,
                reviews: null,
                review: {
                    errors: [],
                    comment: null,
                    rating: null
                },
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
                this.error = this.album = this.rating = this.reviews = null;
                this.loading = true;
                apolloClient.query({
                    query: findAlbum,
                    variables: {id: this.$route.params.id}
                }).then(result => {
                    this.album = result.data.album;
                    this.rating = this.album.rating;
                    this.reviews = (this.album.reviews && this.album.reviews.length > 0) ? this.album.reviews : null;
                }).catch(reason => {
                    console.error(reason);
                    this.error = 'Failed to load data...';
                }).finally(() => {
                    this.loading = false;
                });
            },
            addToCart() {
                window.alert('Done!')
            },
            checkForm() {
                this.review.errors = [];
                if (this.review.rating) {
                    this.sendForm();
                    return;
                }
                if (!this.review.rating) {
                    this.review.errors.push('Rating required.');
                }
            },
            sendForm() {
                apolloClient.mutate({
                    mutation: addReview,
                    variables: {
                        "albumId": this.$route.params.id,
                        "review": {"rating": this.review.rating, "comment": this.review.comment}
                    }
                }).then(result => {
                    const addResult = result.data.addReview;
                    this.rating = addResult.rating;
                    this.reviews = addResult.reviews;
                }).catch(reason => {
                    console.error(reason);
                });
                this.review.rating = this.review.comment = null;
            }
        },
        components: {
            TrackList,
            Stars,
            ReviewCard
        }
    }
</script>

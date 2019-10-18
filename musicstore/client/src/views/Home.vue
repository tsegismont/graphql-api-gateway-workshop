<template>
  <div class="container">
    <h2>All genres</h2>
    <div v-if="loading">
      Loading...
    </div>
    <div v-if="error" class="alert alert-danger">
      {{ error }}
    </div>
    <div class="row" v-if="genres">
      <GenreCard
        v-for="(genre,index) in genres"
        v-bind:genre="genre"
        v-bind:index="index"
        v-bind:key="genre.id"
      />
    </div>
  </div>
</template>

<script>
    import GenreCard from '../components/GenreCard'
    import apolloClient from '../shared/apollo-client';
    import {gql} from "apollo-boost";

    const allGenres = gql`
      {
        genres {
          id
          name
          image
        }
      }
    `;

    export default {
        name: 'home',
        data() {
            return {
                loading: false,
                genres: null,
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
                this.error = this.genres = null;
                this.loading = true;
                apolloClient.query({
                    query: allGenres
                }).then(result => {
                    this.genres = result.data.genres;
                }).catch(reason => {
                    console.error(reason);
                    this.error = 'Failed to load data...';
                }).finally(() => {
                    this.loading = false;
                });
            }
        },
        components: {
            GenreCard
        }
    }
</script>

<template>
  <div class="container">
    <h2>Albums</h2>
    <div v-if="loading">
      Loading...
    </div>
    <div v-if="error" class="alert alert-danger">
      {{ error }}
    </div>
    <div class="row" v-if="albums">
      <AlbumCard
        v-for="(album,index) in albums"
        v-bind:album="album"
        v-bind:index="index"
        v-bind:key="album.id"
      />
    </div>
  </div>
</template>

<script>
    import AlbumCard from '../components/AlbumCard'
    import apolloClient from '../shared/apollo-client';
    import {gql} from "apollo-boost";

    const allAlbums = gql`
      query ($genre: ID!) {
        albums(genre: $genre) {
          id
          name
          artist
          image
          rating
        }
      }
    `;

    export default {
        name: 'genre',
        data() {
            return {
                loading: false,
                albums: null,
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
                this.error = this.albums = null;
                this.loading = true;
                apolloClient.query({
                    query: allAlbums,
                    variables: {genre: this.$route.params.id}
                }).then(result => {
                    this.albums = result.data.albums;
                }).catch(reason => {
                    console.error(reason);
                    this.error = 'Failed to load data...';
                }).finally(() => {
                    this.loading = false;
                });
            }
        },
        components: {
            AlbumCard
        }
    }
</script>

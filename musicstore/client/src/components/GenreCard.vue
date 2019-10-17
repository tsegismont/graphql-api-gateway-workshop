<template>
  <div class="card" style="width: 18rem;">
    <img :src="'/images/genres/'+genre.image" class="card-img-top" :alt="genre.name">
    <div class="card-body bg-light">
      <h5 class="card-title">{{genre.name}}</h5>
      <router-link class="btn btn-primary" :to="'/genre/'+genre.id">Explore</router-link>
    </div>
  </div>
</template>

<script>
    export default {
        name: 'GenreCard',
        props: {
            genre: Object
        }
    }
</script>

<style scoped>
  div.card {
    margin: 1em;
  }
</style>

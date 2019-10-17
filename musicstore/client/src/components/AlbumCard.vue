<template>
  <div class="card" style="width: 18rem;">
    <img :src="'/images/albums/'+album.image" class="card-img-top" :alt="album.name">
    <div class="card-body bg-light">
      <h5 class="card-title">{{album.name}}</h5>
      <p class="card-text text-right">
        <Stars :rating="album.rating"/>
      </p>
      <router-link class="btn btn-primary" :to="'/album/'+album.id">Explore</router-link>
    </div>
  </div>
</template>

<script>
    import Stars from "./Stars";

    export default {
        name: 'AlbumCard',
        components: {Stars},
        props: {
            album: Object
        }
    }
</script>

<style scoped>
  div.card {
    margin: 1em;
  }
</style>

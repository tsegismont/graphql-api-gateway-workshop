<template>
  <div class="card" style="width: 18rem;">
    <div class="card-body bg-light">
      <p class="card-text">
        <Stars :rating="review.rating"/>
        &nbsp;
        <span class="text-secondary">{{ review.name }}</span>
      </p>
      <p class="card-text">{{ review.comment }}</p>
    </div>
  </div>
</template>

<script>
    import Stars from "./Stars";

    export default {
        name: 'ReviewCard',
        props: {
            review: Object
        },
        components: {
            Stars
        }
    }
</script>

<style scoped>
  div.card {
    margin: 1em;
  }
</style>

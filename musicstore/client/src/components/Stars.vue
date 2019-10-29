<template>
  <span>
    {{rating}} / 5
    <span v-if="rating < 1">
      <font-awesome-icon :icon="['far', 'star']"/>
    </span>
    <span v-else>
      <font-awesome-icon :icon="['fas', 'star']" class="text-warning" v-for="n in this.rating" :key="n"/>
    </span>
  </span>
</template>

<script>
    export default {
        name: "Stars",
        props: {
            rating: Number
        }
    }
</script>

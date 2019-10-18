<template>
  <span>
    {{roundedRating}} / {{maxRating}}
    <span v-if="roundedRating < 1">
      <font-awesome-icon :icon="['far', 'star']"/>
    </span>
    <span v-else>
      <font-awesome-icon :icon="['fas', 'star']" class="text-warning" v-for="n in this.count" :key="n"/>
    </span>
  </span>
</template>

<script>
    export default {
        name: "Stars",
        data() {
            const maxRating = 5;
            const roundedRating = Math.round(this.rating);
            const count = Math.min(maxRating, roundedRating);
            return {
                maxRating,
                roundedRating,
                count
            }
        },
        props: {
            rating: Number
        }
    }
</script>

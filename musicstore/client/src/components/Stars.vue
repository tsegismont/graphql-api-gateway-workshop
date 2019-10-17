<template>
  <span>
    {{rating}} / {{maxRating}}
    <span v-if="roundedRating < 1">
      <font-awesome-icon :icon="['far', 'star']"/>
    </span>
    <span v-if="roundedRating >= 1">
      <font-awesome-icon :icon="['fas', 'star']" class="text-warning" v-for="n in count" :key="n"/>
    </span>
  </span>
</template>

<script>
    export default {
        name: "Stars",
        data() {
            return {
                maxRating: 5,
                roundedRating: Math.round(this.rating),
                count: Math.min(this.maxRating, this.roundedRating)
            }
        },
        props: {
            rating: Number
        }
    }
</script>

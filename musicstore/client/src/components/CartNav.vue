<template>
  <li class="nav-item">
    <router-link class="nav-link" to="/cart">
      <font-awesome-icon :icon="['fas', 'shopping-cart']" size="lg"/>
      <span class="badge badge-dark" v-if="cartTotal > 0">{{ cartTotal }}</span>
    </router-link>
  </li>
</template>

<script>
    export default {
        name: "cartNav",
        props: {
            cartTotal: Number
        }
    }
</script>

<template>
  <div id="app">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <div class="container">
        <div class="collapse navbar-collapse">
          <router-link class="navbar-brand" to="/">Music Store</router-link>
        </div>
      </div>
    </nav>
    <router-view/>
  </div>
</template>

<style>
  nav {
    margin-bottom: 2em;
  }

  div.row {
    margin-top: 2em;
  }

  div.card {
    margin: 0 2em 2em 0;
  }
</style>

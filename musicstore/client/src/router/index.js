import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import Genre from '../views/Genre.vue'
import Album from '../views/Album.vue'

Vue.use(VueRouter);

const routes = [
  {
    path: '/',
    name: 'home',
    component: Home
  },
  {
    path: '/genre/:id',
    name: 'genre',
    component: Genre,
  },
  {
    path: '/album/:id',
    name: 'album',
    component: Album,
  }
];

const router = new VueRouter({
  mode: 'history',
  routes: routes
});

export default router;

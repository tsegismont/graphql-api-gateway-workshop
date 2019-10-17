import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import Genre from '../views/Genre.vue'

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
  }
];

const router = new VueRouter({
  routes
});

export default router;

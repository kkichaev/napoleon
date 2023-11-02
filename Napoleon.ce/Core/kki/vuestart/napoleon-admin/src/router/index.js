import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'users',
    component: () => import('../views/Users.vue')
  },
  {
    path: '/activity',
    name: 'activity',
    component: () => import('../views/Activity.vue')
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../components/Login.vue')
  },
  {
    path: '/about',
    name: 'about',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
  }
]

const router = new VueRouter({
  routes
})

router.beforeEach((to, from, next) => {
  // if (to.name != 'login' && localStorage.login != "true")
  // {
  //   console.log('goto to login page')
  //   next({ name: 'login' })

  // }else
    next()
})

export default router

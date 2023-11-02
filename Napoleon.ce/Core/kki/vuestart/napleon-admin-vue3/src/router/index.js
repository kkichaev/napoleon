// Composables
import { createRouter, createWebHistory } from 'vue-router'
import Users from '../views/Users.vue'
import Activity from '../views/Activity.vue'
import Updates from '../views/Updates.vue'
import Settings from '../views/Settings.vue'
import Log from '../views/Log.vue'

const routes = [
  {
    path: '/',
    name: 'users',
    component: Users
  },
  {
    path: '/updates',
    name: 'updtaes',
    component: Updates
  },
  {
    path: '/activity',
    name: 'activity',
    component: Activity
  },
  {
    path: '/settings',
    name: 'settings',
    component: Settings
  },
  {
    path: '/log',
    name: 'log',
    component: Log
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
})

export default router

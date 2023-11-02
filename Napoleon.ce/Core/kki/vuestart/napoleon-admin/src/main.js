import Vue from 'vue'
import App from './App.vue'
import router from './router'
import vuetify from './plugins/vuetify'
import store from './store'
import ViewResource from 'vue-resource'

Vue.config.productionTip = false

Vue.use(ViewResource)

Vue.http.options.root = 'http://localhost:3000/'

Vue.http.interceptors.push((request)=>{
  request.headers.set('Authorization', 'Bearer ' + localStorage.token)
})

new Vue({
  router,
  vuetify,
  store,
  render: h => h(App),
}).$mount('#app')

//if (localStorage.login != "true")
//  router.replace("/login").catch(()=>{});

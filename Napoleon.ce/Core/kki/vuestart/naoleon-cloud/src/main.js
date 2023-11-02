import Vue from 'vue'
import App from './App.vue'
import router from './router'
import vuetify from './plugins/vuetify'
import { createPinia, PiniaVuePlugin } from 'pinia'
import { initializeApp } from "firebase/app";
import i18n from './i18n'
import { getAuth, onAuthStateChanged } from "firebase/auth";
import { mainStore } from './stores/main'

Vue.config.productionTip = false

Vue.use(PiniaVuePlugin)

const pinia = createPinia()

new Vue({
  el: '#app',
  router,
  vuetify,
  pinia,
  render: h => h(App),
  i18n,

  created(){
    const firebaseConfig = {
      apiKey: "AIzaSyA_PrYcJNrkGsk3-QuzQ8p8XSO8uVa2unU",
      authDomain: "nadd-d1117.firebaseapp.com",
      projectId: "nadd-d1117",
      storageBucket: "nadd-d1117.appspot.com",
      messagingSenderId: "487250785974",
      appId: "1:487250785974:web:ca5950b43bbcd79e0e1817",
      measurementId: "G-75M6LKCXCC",
      databaseURL: "https://nadd-d1117-default-rtdb.firebaseio.com/",
    };

    initializeApp(firebaseConfig);

    onAuthStateChanged(getAuth(), (user) => {
      if (user) {
        mainStore().user = user
        
      } else {
        mainStore().user = null
        router.replace("/login").catch(()=>{});
      }
    });
  }
}).$mount('#app')




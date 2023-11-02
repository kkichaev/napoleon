import '@mdi/font/css/materialdesignicons.css'
import Vue from 'vue';
import Vuetify from 'vuetify/lib/framework';

var VueCookie = require('vue-cookie');

Vue.use(Vuetify);
Vue.use(VueCookie)

export default new Vuetify({
    theme: {
        themes: {
          light: {
            primary: '#1867C0',
            secondary: '#5CBBF6', 
          },
        },
      },
});

<template>
  <v-app>
      <Navbar v-if="!$route.path.includes('login')"/>
      <v-container fluid>
        <v-main>
          <router-view></router-view>
        </v-main>
      </v-container>

      <v-snackbar
        :value = "error"
        :timeout = -1
        :multi-line=true
        color="error">
          {{error}}

          <template v-slot:action="{ attrs }">
            <v-btn
              text
              v-bind="attrs"
              @click="clearError">
                Close
            </v-btn>
          </template>    
      </v-snackbar>
  </v-app>
</template>

<script>
import Navbar from './components/Navbar.vue'

export default {
  name: 'App',

  data: () => ({
    
  }),
  computed:{
    error(){
      return this.$store.state.error
    }
  },
  components:{
    Navbar,
  },
  methods:{
    clearError(){
      this.$store.commit('clearError');
    }
  }
};
</script>

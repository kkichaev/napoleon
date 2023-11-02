<template>
  <v-app class="app setting global">
    <nav>
        <v-app-bar 
            app
            clipped-left
            elevation="0"
            class="primary">
            <v-app-bar-nav-icon 
              class="white--text d-lg-none"  
              @click="showDrawer()">
            </v-app-bar-nav-icon>
            <v-toolbar-title class="white--text">
                {{ $t("apptitle") }}
            </v-toolbar-title>
            <v-spacer/>
            <LangSelector/>
            <v-btn 
              v-if="isLogin()"
              @click="logout">
              {{ $t('login.sign-out') }}
            </v-btn>
        </v-app-bar>

        <v-navigation-drawer
            ref="nav-driver"
            app
            :value="isDrawerVsisble()"
            class="white"
            height="100vh"
            clipped 
            hide-overlay
            :style="{ top: $vuetify.application.top + 'px', zIndex: 6 }">

            <v-list>
              <v-list-item link>
                <v-list-item-content>
                  <v-list-item-title class="text-h6">
                    Медведев Вячеслав
                  </v-list-item-title>
                  <v-list-item-subtitle>{{ userEmail }}</v-list-item-subtitle>
                </v-list-item-content>
              </v-list-item>
              <v-divider></v-divider>
              <v-list-item 
                  v-for="item in titles" 
                  :key="item.id"
                  :to="item.route">

                  <v-list-item-icon>
                      <v-icon>{{ item.icon }}</v-icon>
                  </v-list-item-icon>

                  <v-list-item-content>
                      <v-list-item-title>{{ $t(`${item.title}`) }}</v-list-item-title>
                  </v-list-item-content>
              </v-list-item>
            </v-list>
        </v-navigation-drawer>

    </nav>        
    <v-main>
      <router-view/>
    </v-main>
    <v-snackbar
      :value = "error"
      :timeout = -1
      color="error">
      {{ error }}

      <template v-slot:action="{ attrs }">
        <v-btn
          text
          v-bind="attrs"
          @click="clearError">
          Close
        </v-btn>
      </template>
    </v-snackbar>

    <v-footer app
      class="primary white--text">
    <span>+7(485)259-93-68</span>
    </v-footer>
  </v-app>
</template>

<script>
import LangSelector from '@/components/LangSelector.vue';
import {mainStore} from '@/stores/main'

export default {
    name: "App",
    
    data() {
        return {
          visibleDriver: true,
          titles :[
            {'id' : 1, 'icon' : 'mdi-account-multiple', 'title' : 'drawer.agents', 'route': '/'},
            {'id' : 2, 'icon' : 'mdi-account-star', 'title' : 'drawer.tasks', 'route': '/activity'},
          ]
        };
    },
    components: {
        LangSelector
    },
    computed:{
      error(){
        return mainStore().errorMessage
      },
      userEmail(){
        return this.isLogin() ? mainStore().user.email : ""
      }
    },
    methods:{
      clearError(){
        return mainStore().clearError()
      },
      logout(){
        mainStore().logout()
      },
      isLogin(){
        return mainStore().user != null
      },
      showDrawer(){
        this.visibleDriver = !this.visibleDriver
      },
      isDrawerVsisble(){
        return (this.isLogin() && this.visibleDriver)
      },
    },
    monuted(){
      this.visibleDriver = this.$vuetify.breakpoint.name != "xs"
    }

};
</script>

<style lang="scss">
@import './styles/app-styles.scss'
</style>
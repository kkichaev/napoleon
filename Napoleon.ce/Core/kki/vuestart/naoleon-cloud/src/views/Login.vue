<template>
    <v-app>
        <v-container fluid>
          <v-row justify="center">
            <v-col xs="12" sm="6" md="4" lg="4">
              <v-sheet  
                rounded="xl" 
                :outlined="border" 
                class="mt-xs-0 mt-md-16">
                <v-card 
                  rounded="xl"
                  elevation="0">
                  <v-toolbar elevation="0">
                      <v-spacer></v-spacer>
                      <v-toolbar-title 
                        class="title-card">
                        {{ $t("login.card-title") }}
                      </v-toolbar-title>
                      <v-spacer></v-spacer>
                  </v-toolbar>
                  
                  <v-card-text >
                    <v-form
                      class="mx-6"
                      ref="form"
                      validation>
                      <span class="ma-6 black--text">{{ $t("login.email") }}</span>
                      <v-text-field
                          filled
                          rounded
                          dense
                          :placeholder="$t('login.email-hint')"
                          v-model="email"/>

                      <span class="ma-6 black--text">{{ $t("login.pwd") }}</span>

                      <v-text-field
                          filled
                          dense
                          rounded
                          :placeholder="$t('login.pwd-hint')"
                          v-model="password"/>

                      <v-container>
                        <v-row >
                          <v-col >
                            <v-checkbox
                          color="black"
                          class="black--text"
                          >
                          <span slot="label" class="black--text">{{ $t("login.remember-me") }}</span>
                      </v-checkbox>
                          </v-col>
                          <v-col align-self="center">
                            <v-chip to="/register">{{$t('login.register')}}</v-chip>
                            </v-col>
                        </v-row>
                      </v-container>
                      
                      
                    </v-form>
                  </v-card-text>
                  <v-card-actions class="mx-6">
                    <v-spacer></v-spacer>
                    <v-btn 
                        class="primary mb-3"
                        min-width="100%"
                        height="64px"
                        rounded
                        @click="login">
                        {{$t('login.enter')}}
                    </v-btn>
                    <v-spacer></v-spacer>
                  </v-card-actions>
                </v-card>
              </v-sheet>
              <p class="text-center mt-5">
                  <router-link 
                    to="/posts" 
                    class="secondary--text 
                    footer-link">
                    {{$t('login.write-to-support')}}
                  </router-link>
              </p>
            </v-col>
          </v-row>
        </v-container>
    </v-app>
</template>

<script>
import {mainStore} from '../stores/main'

export default{
    data(){
      return {
        store: null,
        email: "",
        password: "",
      }
    },
    computed:{
        border(){
            return this.$vuetify.breakpoint.name == "lg" || this.$vuetify.breakpoint.name == "xl"
        },
        value(){
          return 'value: ' + this.store.count
        }
    },
    methods:{
      login(){
        this.store.login({email:this.email, password: this.password})
      }
    },
    created(){
      this.store = mainStore()
    }
}

</script>

<style scoped lang="scss">
.grid{
  height: 100px;
}
</style>


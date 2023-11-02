<template>
  <div>
    <v-container fluid>
      <v-row>
        <v-col lg="4">
          <v-card>
            <v-card-title>
              {{ $t('login.register') }}
            </v-card-title>    
            <v-card-text>
              <v-form>
                <span class="black--text">{{ $t("login.email") }}</span>

                <v-text-field
                    filled
                    dense
                    :placeholder="$t('login.email-hint')"
                    v-model="email"/>

                <span class="black--text">{{ $t("login.pwd") }}</span>

                <v-text-field
                    filled
                    dense
                    :placeholder="$t('login.pwd-hint')"
                    v-model="password"/>

                <vue-recaptcha sitekey="6Lc5GIkkAAAAAM_NOl-lNr88egwbFGsVoQCsC_sv"></vue-recaptcha>    
              </v-form>
            </v-card-text>

            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn 
                  class="primary"
                  @click="register">
                  {{$t('login.register-to')}}
              </v-btn>
            </v-card-actions>
            
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script>
  import {mainStore} from '../stores/main'
  import { VueRecaptcha } from 'vue-recaptcha';

  export default{
    data(){
      return{
        email:"",
        password:""
      }
    },
    methods:{
      register(){
        mainStore().register({email:this.email, password:this.password})
      }
    },
    components: { VueRecaptcha }
  }
</script>
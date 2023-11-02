<template>
    <v-app>
        <v-main>
            <v-container fluid fill-height>
                <v-row justify="center">
                    <v-col cols="8" sm="6" md="4" lg="3">
                        <v-card class="elevation-12">
                            <v-toolbar  dark class="primary white--text">
                                <v-toolbar-title>Наполелон Администратор</v-toolbar-title>
                            </v-toolbar>
                            
                            <v-card-text>
                                <v-form
                                    ref="form"
                                    v-model="valid"
                                    validation>

                                    <v-text-field
                                        prepend-icon="mdi-account"
                                        name="login"
                                        label="Login"
                                        :rules="loginRules"
                                        type="text"/>
                                    <v-text-field
                                        id="password"
                                        prepend-icon="mdi-lock"
                                        name="password"
                                        label="Password"
                                        type="password"
                                        :counter="6"
                                        :rules="passwordRules"/>
                                </v-form>
                            </v-card-text>
                            <v-card-actions>
                                <v-spacer></v-spacer>
                                <v-btn 
                                    class="primary" 
                                    @click="login"
                                    :disabled="!valid">
                                    Login
                                </v-btn>
                            </v-card-actions>
                        </v-card>
                    </v-col>
                </v-row>
            </v-container>
        </v-main>
    </v-app>
</template>

<script>

export default{
    name: 'Login',
    data(){
        return{
            valid:false,
            loginRules:[
                v => !!v || 'Поле не может быть пустым'
            ],
            passwordRules:[
                v => !!v || 'Поле не может быть пустым',
                v => (v && v.length >= 6 ) || 'Не меньше шести симовлов'
            ]
        }
    },
    computed:{
        error(){
            return true;
        }
    },
    methods:{
        login(){
            if (this.$refs.form.validate()){
                console.log('form validate')
                // this.$store.dispatch('loginUser')
                // .then(()=>{
                //     console.log('success')
                //     this.$router.push('/')
                // })
                // .catch(e=>console.log('error: ', e))
            }else
                console.log('form invalidate')
        }
    }  
}
</script>
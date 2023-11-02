<template>
    <v-dialog
        max-width="600px"
        v-model="dialog">
        <v-card>
        <v-card-title>Новый пользователь</v-card-title>
        
        <v-card-text>
            <v-container>
                <v-row>
                    <v-col
                        cols="12"
                        md="4">

                        <v-text-field label="ID" v-model="user.id"/>
                    </v-col>

                    <v-col
                        cols="12"
                        md="8">

                        <v-text-field label="Имя" v-model="user.name"/>
                    </v-col>
                </v-row>

            <v-row>
                <v-col
                    cols="6">

                    <v-text-field label="Логин" v-model="user.login"/>
                </v-col>

                <v-col
                    cols="6">
                    <v-text-field label="Пароль" v-model="user.pwd"/>
                </v-col>
            </v-row>

            <v-row>
                <v-col
                    cols="6">
                    
                    <v-select
                        label="Лицензия"
                        :items="licenseValues"
                        :value="user.license"
                        outlined/>
                </v-col>

                <v-col
                    cols="6">

                    <v-checkbox label="Слежение" v-model="user.gps"/>
                </v-col>
            </v-row>
            </v-container>
        </v-card-text>

        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn @click="dialog=false">Cancel</v-btn>
            <v-btn @click="okClick">OK</v-btn>
        </v-card-actions>
        </v-card>

    </v-dialog>
</template>
<script>
import {licenseValues} from '@/assets/data'

export default{
    data(){
        return{
            dialog: false,
            licenseValues,
            user: {} 
        }
    },
    methods:{
        show(user){
            console.log("EditAgentDlg show user: ", user)

            if (user){
                console.log('user defined')
                this.user = user
            }
            
            this.dialog = true;
        },

        okClick(){
            console.log("EditAgent okClick: ", this.user)
            this.$emit('okDlg', this.user)
            this.dialog = false
        }
    }

}
</script>
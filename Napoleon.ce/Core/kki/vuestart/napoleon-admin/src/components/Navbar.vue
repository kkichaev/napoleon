<template>
    <nav>
        <v-app-bar 
            app
            clipped-left>
            <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>
            <v-toolbar-title class="text-uppercase text-blue">
                {{text}}
            </v-toolbar-title>

            <v-spacer/>

           <v-btn @click="logout">
             <span>выход</span>
             <v-icon end>mdi-exit-to-app</v-icon>
           </v-btn>
        </v-app-bar>

        <v-navigation-drawer
            app
            v-model="drawer"
            class="primary lighten-4"
            height="100vh"
            clipped 
            hide-overlay
            :style="{ top: $vuetify.application.top + 'px', zIndex: 6 }">

            <v-list>
                <v-list-item 
                    v-for="item in titles" 
                    :key="item.title"
                    :to="item.route">

                    <v-list-item-icon>
                        <v-icon>{{ item.icon }}</v-icon>
                    </v-list-item-icon>

                    <v-list-item-content>
                        <v-list-item-title>{{ item.title }}</v-list-item-title>
                    </v-list-item-content>
                </v-list-item>
            </v-list>
        </v-navigation-drawer>
    </nav>
</template>

<script>
import router from '../router'
export default{
    data(){
        return{
            text: "наполеон",
            drawer: true,
            titles :[
            {'icon' : 'mdi-account-multiple', 'title' : 'Пользователи', 'route': '/'},
            {'icon' : 'mdi-account-star', 'title' : 'Активность менеджеров', 'route': '/activity'},
            {'icon' : 'mdi-update', 'title' : 'Обновления', 'route': '/updates'},
            {'icon' : 'mdi-cog', 'title' : 'Настройки', 'route': '/settings'},
            {'icon' : 'mdi-post-outline', 'title' : 'Лог синхронизации', 'route': '/log'},
        ]
        }
    },
    methods:{
        logout:()=>{
            console.log("login")
            router.replace('/login').catch(()=>{})
            localStorage.login = false
        }
    }
}
</script>

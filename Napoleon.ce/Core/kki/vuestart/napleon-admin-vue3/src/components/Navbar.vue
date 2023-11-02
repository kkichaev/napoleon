<template>
    <nav>
        <v-app-bar>
            <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>
            <v-toolbar-title class="text-uppercase text-blue">
                {{text}}
            </v-toolbar-title>

           <v-btn>
             <span>sign out</span>
             <v-icon end>mdi-exit-to-app</v-icon>
           </v-btn>
        </v-app-bar>

        <v-navigation-drawer
            v-model="drawer"
            class="bg-blue-lighten-4">

            <v-list>
                <v-list-item 
                    v-for="item in titles" 
                    :key="item.title"
                    :title="item.title"
                    :to="item.route"
                    :prependIcon="item.icon">
                    
                </v-list-item>
            </v-list>
        </v-navigation-drawer>
    </nav>
</template>

<script setup>
import { ref } from 'vue'

const text = "наполеон"    
const drawer = ref(true)
const titles = ref([
    {'icon' : 'mdi-account-multiple', 'title' : 'Пользователи', 'route': '/'},
    {'icon' : 'mdi-account-star', 'title' : 'Активность менеджеров', 'route': '/activity'},
    {'icon' : 'mdi-update', 'title' : 'Обновления', 'route': '/updates'},
    {'icon' : 'mdi-cog', 'title' : 'Настройки', 'route': '/settings'},
    {'icon' : 'mdi-post-outline', 'title' : 'Лог синхронизации', 'route': '/log'},
])

</script>

<template>
    <div>
        <v-container fluid>
            <v-card 
                v-for="item in activity"
                :class="`manager ${item.status}`">
                <v-row wrap>
                    <v-col md=12 lg=8 class="pa-5">
                        <div class="text-subtitle-2 text-grey">Менеджер</div>
                        <div>{{item.name}}</div>
                    </v-col>

                    <v-col md=3 lg=2 class="pa-5">
                        <div class="text-subtitle-2 text-grey">IP</div>
                        <div class="othercell">{{item.ip}}</div>
                    </v-col>

                    <v-col md=3 lg=2 class="pa-5">
                        <div class="text-subtitle-2 text-grey">Продолжительность</div>
                        <div class="othercell">{{item.duration}}</div>
                    </v-col>
                </v-row>
            </v-card>
        </v-container>

    </div>
</template>

<script setup>
import {activity} from '../assets/data.js'
</script>

<style>
.manager.exl{
    border-left: 4px solid rgb(73, 170, 17);
}

</style>
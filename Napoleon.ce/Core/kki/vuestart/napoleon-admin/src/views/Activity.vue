<template>
    <div>
        <v-container fluid>
            <v-row class="ma-5">
                <v-tooltip top>
                    <template v-slot:activator="{ on, attrs }">
                        <v-btn 
                            small 
                            plain 
                            v-bind="attrs"
                            v-on="on"
                            @click="sortBy('name')" >

                            <v-icon small class="text-grey mx-4">mdi-account-details</v-icon>
                            <span>по менеджеру</span>
                        </v-btn>

                    </template>
                    <span>сортировка по менеджеру</span>
                </v-tooltip>

                <v-tooltip top>
                    <template v-slot:activator="{on,attrs}">
                        <v-btn 
                            small 
                            plain 
                            v-bind="attrs"
                            v-on="on"
                            @click="sortBy('duration')">
                            <v-icon small class="text-grey mx-4">mdi-sort-clock-ascending</v-icon>
                            <span>по времени</span>
                        </v-btn>
                    </template>
                    <span>сортировка по времени</span>
                </v-tooltip>
            </v-row>
            <v-card 
                v-for="item in activityData"
                :class="`manager ${item.status} ma-5 pa-5`"
                :key="item.name">
                <v-row wrap>
                    <v-col md=12 lg=8>
                        <div class="text-subtitle-2 text-grey">Менеджер</div>
                        <div>{{item.name}}</div>
                    </v-col>

                    <v-col md=3 lg=2>
                        <div class="text-subtitle-2 text-grey">IP</div>
                        <div class="othercell">{{item.ip}}</div>
                    </v-col>

                    <v-col md=3 lg=2>
                        <div class="text-subtitle-2 text-grey">Продолжительность</div>
                        <div class="othercell">{{item.duration}}</div>
                    </v-col>
                </v-row>
            </v-card>
        </v-container>

    </div>
</template>

<script>
import {activity} from '../assets/data.js'
import {ref} from 'vue'

export default{
    data(){
        return {
            activityData: ref(activity)
        }
    },
    methods:{
        sortBy(prop){
            console.log('sortBy: ' + prop)
            this.activityData.sort((x,y)=> x[prop] < y[prop] ? -1 : 1)
            console.log(this.activityData[0].name, this.activityData[1].name)
        }
    }
}

</script>

<style>
.manager.exl{
    border-left: 4px solid rgb(73, 170, 17);
}

</style>
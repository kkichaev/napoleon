<template>
  <div 
    class="home">

    <v-data-table
      v-if="!agentMode"
      class="managerTable"
      fixed-header
      :height="tableHeight"
      :headers="columnsManager"
      :items="managersData"
      item-key="id">

      <template v-slot:top>
        
        <v-toolbar>
          <v-btn outlined color="secondary" class="ma-3" @click="agentMode = !agentMode">
            Агенты
          </v-btn>
          <v-btn :outlined="agentMode" color="secondary" class="ma-3" @click="agentMode = !agentMode">
            Менеджеры
          </v-btn>

        </v-toolbar>    
      </template>
      
    </v-data-table>
    <AgentsTable v-else :agentMode="agentMode" @agentMode="agentMode = $event"/>
  </div>
</template>

<script>
  import {managers} from '../assets/data.js'
  import { ref } from 'vue'
  import AgentsTable from '../components/AgentsTable.vue'

  export default{
    data(){
      return {
        tableHeight: "76vh",
        itemsPerPage: 0,
        agentMode: true,
        userDlg: false,
        isSetLicense: false,
        managersData: ref(managers),

        columnsManager: [
          {text: 'Подразделение', value: 'division', align: 'start'},
          {text: 'Логин', value: 'login'},
          {text: 'Пароль', value: 'pwd'},
          {text: 'Посл. достп', value: 'last'},
          {text: 'Версия', value: 'version'},
          {text: 'Запред ред. пароля', value: 'edit'},
          {text: 'Лицензия', value: 'license'},],
      }
    },
    components:{
      AgentsTable
    }
  }
</script>
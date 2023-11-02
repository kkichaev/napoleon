<template>
    <div class="agentTable">
        <v-data-table 
            class="agentTable"
            :headers="columnsAgent"
            :items="agentData" 
            item-key="id"
            fixed-header
            :height="tableHeight"
            sort-by = "name">
      
            <template v-slot:top>
                
                <v-toolbar>
                    <AgentManagerBtn @onChanged="setAgentMode($event)"></AgentManagerBtn>
                    <v-spacer></v-spacer>
                    <v-btn @click="showDialog">Dialog</v-btn>
                    <EditAgentDlg @okDlg="okDlg($event)" ref="editDlg"/>
                </v-toolbar>
            </template>

            <template v-slot:item.license="{ item }">
                <v-select
                v-model="item.license"
                :items="licenseValues"/>
            </template>

            <template v-slot:item.gps="{ item }">
                <v-checkbox
                v-model="item.gps"
                color="primary lighten-2"
                ></v-checkbox>
            </template>

        </v-data-table>
    </div>
</template>

<script>
import EditAgentDlg from './EditAgentDlg.vue'
import AgentManagerBtn from './AgentManagerBnt.vue'
import {licenseValues} from '@/assets/data'

const nameWidth = '100%'
const cellWidth = '140px'

const cmpdate = (a, b)=>{
  let d1 = Date.parse(a)
  let d2 = Date.parse(b)
  
  if (isNaN(d1)) d1 = 0
  if (isNaN(d2)) d2 = 0

  return d1 - d2
}

export default{
    props:{
        agentMode: Boolean
    },
    data(){
        return{
            resource: null,
            tableHeight: "76vh",
            itemsPerPage: 0,
            userDlg: false,
            editUser: {
                id:"",
                login:"",
                pwd:"",
                license: "нет"
            },

            license: ['нет'],
            licenseValues,

            emptyUser: {
                id:"",
                login:"",
                pwd:"",
                license: "нет"
            },

            columnsAgent: [
                {text: 'ID', value: 'id', align: 'start', width:cellWidth},
                {text: 'Пользователь', value: 'name',align: 'start', width: nameWidth},
                {text: 'Логин', value: 'login', width: cellWidth},
                {text: 'Пароль', value: 'pwd', width: cellWidth},
                {text: 'Посл. доступ', value: 'last', sort:cmpdate, width: cellWidth},
                {text: 'Версия', value: 'version', width: cellWidth},
                {text: 'Лицензия', value: 'license', width: cellWidth},
                {text: 'Слежение', value: 'gps', width: cellWidth},]  
        }
    },
    methods:{
      close() {
        this.userDlg = false; 
        this.editUser = Object.assign({}, this.emptyUser)
      },

      save(){
        this.agentData.push(this.editUser)
        this.userDlg = false;
        this.close()
      },

      setAgentMode(val){
        console.log('setAgentMode: ', val)
        //this.$emit('agentMode', !this.agentMode)
      },

      showDialog(){
        this.$refs.editDlg.show(this.editUser)
      },
    
      okDlg(user){
        console.log("AgentsTable okDlg: ", user)
        this.resource.save(user)
            .then(r=>{
                    alert("Записался: " + r.json())
                    this.agentData.push(user)
                  }, 
                  r=>alert("Ошибка: " + r.json()))
      }
    },
    components:{
        EditAgentDlg,
        AgentManagerBtn,
    },
    mounted(){
        this.$store.dispatch('loadAgent')
    },
    computed:{
        agentData(){
            return this.$store.state.agents
        }
    }
}
</script>
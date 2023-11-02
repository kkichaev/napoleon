<template>
  <div>
    <v-banner app sticky color="white">
    <v-container fluid>
      <v-row >
        <v-spacer></v-spacer>
        <v-btn class="btn" outlined @click.stop="addAgentDlg">
          +Сотрудник
        </v-btn>
        <v-btn class="btn" outlined @click.stop="addDivisionDlg">
          +Подразделение
        </v-btn>
        <v-btn class="btn" outlined @click.stop="deleteSelected">
          -Удалить
        </v-btn>
      </v-row>
      <v-row no-gutters class="mt-6">
        <v-col>
          <v-card class="py-4 d-flex justify-center" tile >
            Сотрудник/Отдел
          </v-card>
        </v-col>
        <v-col>
          <v-card class="py-4 d-flex justify-center" tile >
            Должность
          </v-card>
        </v-col>
        <v-col>
          <v-card class="py-4 d-flex justify-center" tile >
            Последний доступ
          </v-card>
        </v-col>
        <v-col>
          <v-card class="py-4 d-flex justify-center" tile >
            Статус
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </v-banner>

    <v-treeview
      ref="treeview"
      :open.sync="open"
      :items="items"
      activatable
      item-key="id"
      dense
      :active.sync="selected"
      return-object
      open-all>

      <template v-slot:label="{ item }">
        <v-container fluid>
          <v-row> 
            <v-col>
                {{ item.name }}
            </v-col>
          </v-row>
        </v-container>
        <v-divider></v-divider>
      </template>
    </v-treeview>

    <v-dialog 
      v-model="agentDlg"
      max-width="300">
      <v-card>
        <v-card-title>
          Агент
        </v-card-title>
        <v-card-text>
          <v-text-field
            hint="Введите наименование"
            label="Наименование"
            v-model="editAgent.name"
          ></v-text-field>
        </v-card-text>
      </v-card>
      <v-card-actions>
        <v-btn @click="agentDlg=false">Нет</v-btn>
        <v-btn @click="addAgent">Да</v-btn>
      </v-card-actions>
    </v-dialog>

    <v-dialog 
      v-model="divisionDlg"
      max-width="300">
      <v-card>
        <v-card-title>
          Подразделение
        </v-card-title>
        <v-card-text>
          <v-text-field
            hint="Введите наименование"
            label="Наименование"
            v-model="editDivision.name"
          ></v-text-field>
        </v-card-text>
      </v-card>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn @click="divisionDlg=false">Нет</v-btn>
        <v-btn @click="addDivision">Да</v-btn>
      </v-card-actions>
    </v-dialog>
  </div>
</template>

<script>

import {mainStore} from '../stores/main'

export default{
  data(){
    return{
      selected:[],
      editDivision:{ name: ""},
      editAgent:{name:""},
      divisionDlg: false,
      agentDlg:false,
      open: [],
      files: {
        html: 'mdi-language-html5',
        js: 'mdi-nodejs',
        json: 'mdi-code-json',
        md: 'mdi-language-markdown',
        pdf: 'mdi-file-pdf',
        png: 'mdi-file-image',
        txt: 'mdi-file-document-outline',
        xls: 'mdi-file-excel',
      },
      
    }
  },
  computed:{
    items(){
      const table = mainStore().agentsTable
      return table == null ? [] : table
    }
  },
  mounted(){
    mainStore().loadAgents()
  },  
  methods:{
    addDivision(){
      const obj = {id: crypto.randomUUID(), name:this.editDivision.name, children:[], type:'division'}

      if (this.selected.length == 0){
        mainStore().agentsTable.push(obj)
      }else{
        if (this.selected[0].children == null){
          this.selected[0].children = []
        }

        this.selected[0].children.push(obj)  
      }

      this.open.push(this.selected[0])
      this.divisionDlg=false
      this.editDivision.name = ''
      mainStore().writeDivision()
    },

    addAgent(){
      const obj = {id: crypto.randomUUID(), name:this.editAgent.name, children:[], type:'agent'}

      if (this.selected.length > 0 ){
        if (this.selected[0].children == null){
          this.selected[0].children = []
        }

        this.selected[0].children.push(obj)  
      }

      this.open.push(this.selected[0])
      this.agentDlg=false
      this.editAgent.name = ''

      mainStore().writeDivision()
    },

    addAgentDlg(){
      if (this.selected.length > 0 && this.selected[0].type == "division"){
        this.agentDlg = true
      }
    },
    addDivisionDlg(){
      if (this.selected.length == 0 || this.selected[0].type == "division"){
        this.divisionDlg = true
      }
    },
    deleteSelected(){
      if (this.selected.length > 0){
        var table = mainStore().agentsTable
        for(var i = 0 ; i < table.length; i++){
          if (this.selected[0].id == table[i].id){
            mainStore().agentsTable.splice(i,1)
            mainStore().writeDivision()
            break
          }
        }
      }
    }

  }

}
</script>  

<style scoped>
  .btn{
    margin: 10px;
  }
</style>
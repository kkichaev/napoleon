<template>
  <div class="home">
    <v-data-table 
      v-model:items-per-page="itemsPerPage"
      :headers="columnsAgents"
      :items="agentData" 
      :sort-by="[{ key: 'name', order: 'asc' }]"
      item-key="id"
      fixed-header
      height="400">

      
      <template v-slot:top>
        
        <v-toolbar>
          <v-btn :variant="!agentMode? 'outlined' : 'elevated'" color="secondary" class="ma-3" @click="agentMode = !agentMode">
            Агенты
          </v-btn>
          <v-btn :variant="agentMode? 'outlined' : 'elevated'" color="secondary" class="ma-3" @click="agentMode = !agentMode">
            Менеджеры
          </v-btn>

          <v-spacer></v-spacer>

          <v-btn>Добавить
            <v-dialog
              v-model="userDlg"
              max-width="600px"
              activator="parent">

              <v-card>
                <v-card-title>Новый пользователь</v-card-title>
                
                <v-card-text>
                  <v-container>
                    <v-row>
                      <v-col
                        cols="12"
                        md="4">
                        <v-text-field label="ID" v-model="editUser.id"/>
                      </v-col>
                      <v-col
                        cols="12"
                        md="8">
                        <v-text-field label="Имя" v-model="editUser.name"/>
                      </v-col>
                    </v-row>

                    <v-row>
                      <v-col
                        cols="6">
                        <v-text-field label="Логин" v-model="editUser.login"/>
                      </v-col>
                      <v-col
                        cols="6">
                        <v-text-field label="Пароль" v-model="editUser.pwd"/>
                      </v-col>
                    </v-row>

                    <v-row>
                      <v-col
                        cols="6">
                        
                        <v-select
                          v-model="editUser.license"
                          label="Лицензия"
                          :items="licenseValues"
                          outlined/>
                      </v-col>
                      <v-col
                        cols="6">
                        <v-checkbox-btn 
                          v-model="editUser.gps"
                          label="Слежение"
                        ></v-checkbox-btn>
                      </v-col>
                    </v-row>
                  </v-container>
                </v-card-text>

                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn @click="close">Cancel</v-btn>
                  <v-btn @click="save">OK</v-btn>
                </v-card-actions>
              </v-card>

            </v-dialog>
          </v-btn>

        </v-toolbar>
      </template>

      <template v-slot:item.license="{ item }">
        <v-select
          v-model="item.columns.license"
          :items="licenseValues"/>
      </template>

      <template v-slot:item.gps="{ item }">
        <v-checkbox-btn
          v-model="item.columns.gps"
        ></v-checkbox-btn>
      </template>

    </v-data-table>

  </div>
</template>

<script setup>
import { ref, watch, watchEffect, nextTick, computed} from 'vue'
import {managers, agents} from '../assets/data.js'

const itemsPerPage = ref(10)
const agentMode = ref(true)
const userDlg = ref(false)
const agentData = ref(agents)

const isSetLicense = computed((obj)=>{console.log('isSetLicense computed ' + obj)})

const editUser = ref({
  id:"",
  login:"",
  pwd:"",
  license: "нет"
})

const licenseValues = ['нет', 'Pre-Selling', 'Van-Selling']
const license = ['нет']
const emptyUser = ref({
  id:"",
  login:"",
  pwd:"",
  license: "нет"
})

const columnsManager = [
  {title: 'Подразделение', key: 'division', align: 'start'},
  {title: 'Логин', key: 'login'},
  {title: 'Пароль', key: 'pwd'},
  {title: 'Посл. достп', key: 'last'},
  {title: 'Версия', key: 'version'},
  {title: 'Запред ред. пароля', key: 'edit'},
  {title: 'Лицензия', key: 'license'},]

const cmpdate = (a, b)=>{
  let d1 = Date.parse(a)
  let d2 = Date.parse(b)
  
  if (isNaN(d1)) d1 = 0
  if (isNaN(d2)) d2 = 0

  return d1 - d2
}

const  columnsAgents =[
    {title: 'ID', key: 'id', align: 'start'},
    {title: 'Пользователь', key: 'name'},
    {title: 'Логин', key: 'login'},
    {title: 'Пароль', key: 'pwd'},
    {title: 'Посл. доступ', key: 'last', sort:cmpdate},
    {title: 'Версия', key: 'version'},
    {title: 'Лицензия', key: 'license'},
    {title: 'Слежение', key: 'gps'},]

watch(userDlg , (val)=>{
  if (!val){  
    close()
  }
})

const close = () =>{
  userDlg.value = false; 
  editUser.value = Object.assign({}, emptyUser.value)
}

const save = ()=>{
  agentData.value.push(editUser.value)
  userDlg.value = false;
  close()
}

</script>
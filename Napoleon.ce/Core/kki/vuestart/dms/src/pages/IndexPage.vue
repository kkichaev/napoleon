<template>
  <q-page padding>
    <q-page-sticky expand position="top">
      <q-toolbar class="bg-primary">
        <q-toolbar-title class="text-white">
          Папки
        </q-toolbar-title>
      </q-toolbar>
    </q-page-sticky>

    <div class="row q-mt-xl">
      <q-card class="card q-ml-xs q-mr-xs"
        v-for="(value, index) in folders"
        :key="index"
        @click="openFolder($event, index)">

        <q-card-section class="bg-indigo-1 full-height">
          <div class="text-h6">{{ value.title }}</div>
          <div class="text-caption">{{ value.descr }}</div>
          <div v-html="value.content"/>
        </q-card-section>

        <q-card-actions class="bg-indigo-2"
          align="right">
          <q-btn
            flat
            round
            icon="edit"
            dense
            color="green"/>

          <q-btn flat
            round
            dense
            icon="delete"
            color="red"
            @click.stop="deleteFolder(index)"/>

        </q-card-actions>

        <!-- <q-menu
          touch-position
          context-menu>

          <q-list
            dense
            min-width="100px">
            <q-item clickable v-close-popup @click="deleteFolder(`${index}`)">
              <q-item-section>Удалить</q-item-section>
            </q-item>
          </q-list>
        </q-menu> -->
      </q-card>
    </div>

  </q-page>
</template>

<script setup>
import { useMainStore } from 'src/stores/mainStore'
import { useFirebase } from './helpers'

const folders = useFirebase()

function newItem (data) {
  folders.value.push(data)
}

const newItemTitle = 'Добавить папку'

defineExpose({ newItem, folders, newItemTitle })

function openFolder (id) {
  console.log('openFolder: ' + id)
  // useMainStore().openFolder(id)
}

function deleteFolder (e, idx) {
  console.log('deleteFolder: delete!!!!!')
  folders.value.splice(idx, 1)
}

</script>

<style scoped>
.card{
  width: 100%;
  max-width: 400px;

}
</style>

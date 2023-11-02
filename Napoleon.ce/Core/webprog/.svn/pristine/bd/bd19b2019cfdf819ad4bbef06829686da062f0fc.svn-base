<template>
  <q-page class='page'>
    <q-card class="page-content">
      <div class="page-header">{{ $t('project.title') }}</div>

      <q-card
        flat bordered
        v-for="item in servers"
        :key="item.id">

        <div class="group-header">
          {{$t('project.name')}}{{ item.name }}
          <q-btn flat square size='xs' icon="img: /img/edit.svg" @click="rename(item)"/>
        </div>

        <div>
          {{$t('project.code')}} {{ item.code }} <q-btn flat square size='xs' @click="copy(item.code)" icon="img: /img/copy.svg"/>
        </div>
      </q-card>
    </q-card>
  </q-page>

</template>

<script setup>
import { ref, onMounted } from "vue";
import { getServers, renameServer } from "../backend/backend";
import { copyToClipboard, useQuasar  } from 'quasar'
import { useI18n } from "vue-i18n";

const servers = ref()
const $q = useQuasar()
const i18n = useI18n()

onMounted(()=>{
  getServers().then((responce)=>{servers.value = responce})
  .catch((error)=>{console.log("getServers ERROR! " + error)})
})

const copy = (text)=>{ copyToClipboard(text)}
const rename = (item)=>{
  const text = ref(item.name)
  $q.dialog({
    title: i18n.t('project.rename'),
    message: i18n.t('project.newNamePrompt'),
    prompt: {
      model: text,
      type: 'text',
    },
    cancel: true,
  }).onOk(() => {
    renameServer(item.code, text.value)
      .then((responce)=>{
        console.log("renameServer SUCCESS!")
        item.name = text.value
      })
      .catch((error)=>{
        console.log("renameServer ERROR!")})
  })
}
</script>

<style scoped>
.input-field{
  width: 297px
}
</style>

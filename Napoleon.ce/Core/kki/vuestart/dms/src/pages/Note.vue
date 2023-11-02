<template>
  <q-page>
    <q-page-sticky expand position="top">
      <q-toolbar class="bg-primary">
        <q-toolbar-title class="text-white">

          {{ name }}
        </q-toolbar-title>
      </q-toolbar>
    </q-page-sticky>

    <q-list style="margin-top: 52px;">
      <q-item
        v-for="note in folder.notes"
        :key="note.id">

        {{ note.text }}
      </q-item>
    </q-list>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useMainStore } from 'src/stores/mainStore'

const name = ref('')
const folder = ref({})

onMounted(() => {
  useMainStore().loadFolders().then((f) => {
    folder.value = f[useMainStore().getCurrentRouteParams('id')]
    name.value = f[useMainStore().getCurrentRouteParams('id')].name
  })
})

const newItemTitle = 'Добавить заметку'

function newItem (data) {
  const note = {
    id: crypto.randomUUID(),
    text: data,
    type: 'text',
    created: new Date()
  }

  console.log('newItem: ')
  useMainStore().addNote(folder.value, note)
  console.log(folder.value.notes)
}

defineExpose({ newItemTitle, newItem })
</script>

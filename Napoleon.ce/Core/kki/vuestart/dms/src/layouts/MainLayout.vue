<template>
  <q-layout view="hHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="toggleLeftDrawer"
        />

        <q-toolbar-title>
          DMS
        </q-toolbar-title>
        <q-space/>
        <q-btn @click="logout">{{ exit }}</q-btn>
      </q-toolbar>
    </q-header>

    <q-drawer
      v-model="leftDrawerOpen"
      show-if-above
      bordered>
      <q-list>
        <q-item clickable to="/">
          <q-item-section>
            <q-item-label>Заметки</q-item-label>
          </q-item-section>
        </q-item>
      </q-list>
    </q-drawer>

    <q-page-container>
      <!-- <router-view ref="currentView"/> -->
      <router-view v-slot="{ Component }">
        <component ref="view" :is="Component" />
      </router-view>
      <q-page-sticky position="bottom-right" :offset="[18, 18]">
        <q-btn fab icon="add" color="primary" @click="newItemDlg = true"/>
      </q-page-sticky>

      <q-dialog
        v-model="newItemDlg"
        persistent>

        <q-card class="dialog">
          <q-card-section>
            <div class="text-h6 text-center">{{ getDialogTitle()}}</div>
          </q-card-section>

          <q-card-section>
            <q-input
              label="Заголовок"
              outlined
              v-model="title"/>

            <q-input class="q-mt-sm"
              label="Описание"
              outlined
              v-model="descr"
              dense/>

            <div class="q-mt-sm q-ml-xs text-caption">Содержание</div>

            <q-editor class="q-mt-md"
              ref="editor"
              v-model="content"
              min-height="5rem"
              :definitions="{
                  fontColor: {
                      tip: 'Изменть цвет шрифта',
                      icon: 'color_lens',
                      handler: selectColor
                    },

                  backColor: {
                    tip: 'Изменть цвет фона',
                    icon: 'gradient',
                    handler: selectColor
                  },
                  fontSizes: {
                    label: $q.lang.editor.formatting,
                    icon: $q.iconSet.editor.formatting,
                    list: 'no-icons',
                    options: ['p', 'h3', 'h4', 'h5', 'h6', 'code']
                  }
                }"
              :toolbar="[['left','center','right','justify','bold','italic','underline','strike','undo','redo'],
                ['backColor', 'fontColor'], ['removeFormat']]"/>
          </q-card-section>

          <q-card-actions align="right">
            <q-btn flat label="Отменить" color="primary"  v-close-popup/>
            <q-btn flat label="OK" icon='track_changes' color="primary" @click="addItem"/>
          </q-card-actions>
        </q-card>

        <q-card v-if="editColor">
          <div class="text-center">Цвет шрифта</div>
          <q-color
            :value="textColor"
            @change="val=>applyColor(val)"
            no-header
            no-footer
            default-view="palette"/>
        </q-card>
      </q-dialog>

    </q-page-container>
  </q-layout>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useQuasar } from 'quasar'
import { useMainStore } from 'src/stores/mainStore'
import { text } from 'body-parser'
// import { useRouter } from 'vue-router'

const $q = useQuasar()
const exit = ref('Выход')
const leftDrawerOpen = ref(false)
const view = ref(null)
const newItemDlg = ref(false)
const title = ref('')
const descr = ref('')
const content = ref('')
const editColor = ref(false)
const textColor = ref('#000000')
const editor = ref(null)

function logout () {
  useMainStore().logout()
}

function toggleLeftDrawer () {
  leftDrawerOpen.value = !leftDrawerOpen.value
}

function getDialogTitle () {
  return view.value.newItemTitle
}

function addItem () {
  view.value.newItem({ title, descr, content })
  newItemDlg.value = false
}

function applyColor (color) {
  textColor.value = color
  console.log('applyColor: ' + color)
  editor.value.caret.restore()
  editor.value.runCmd('foreColor', textColor.value)
  editor.value.focus()
  editColor.value = false
}

function selectColor () {
  console.log('selectColor')
  editColor.value = !editColor.value
}

function closeColor () {
  editor.value.caret.restore()
  editor.value.focus()
  editColor.value = false
}

</script>

<style scoped>
.dialog{
  width: 100%;
  max-width: 600px;
}

</style>

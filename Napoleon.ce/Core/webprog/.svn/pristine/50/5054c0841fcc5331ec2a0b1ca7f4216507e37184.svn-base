<template>
  <q-page class='page'>
    <q-card class='page-content-full-hight'>
      <div class="page-header">{{ $t('script.title') }}</div>
      <div class="row">
        <div class="col-xs-12 col-sm-12 col-md col-lg list_with_caption_form">
          <div class="row header-form">
            <div class="column col justify-center">
              {{  $t('scripts') }}
            </div>
            <q-btn class="button" flat icon="img: /img/folder_open.svg" @click="expandAll()"/>
            <q-btn class="button" flat icon="img: /img/folder.svg" @click="collapseAll()"/>
            <q-btn class="button" flat icon="img: /img/plus_circle_outline.svg" @click="addScript"/>
          </div>

          <q-list>
            <q-expansion-item
              v-for="d in scriptDef"
              :key="d.id"
              hide-expand-icon
              @click.stop="scriptClick(d)"
              v-model="d.expanded"
              dense
              :style= "[isSelected(d) ? {'background-color': 'rgba(18, 110, 130, 0.50)'} : {'background-color': 'white'}]">

              <template v-slot:header="{ }">
                <q-item-section avatar>
                  <q-icon color="primary" :name="getScriptIcon(d)" />
                </q-item-section>

                <q-item-section>
                  {{ d.name }}
                </q-item-section>
                <q-btn class="button" flat icon="img: /img/edit.svg" @click="editScript(d)"/>
                <q-btn class="button" flat icon="img: /img/trash_full.svg" @click="deleteScript(d)" />
              </template>

              <div style="background-color: white">
                <q-list :style= "[isSelected(d) ? {'background-color': 'rgba(18, 110, 130, 0.10)'} : {'background-color': 'white'}]">
                  <q-item
                    v-for="i in d.items"
                    :key="i.id">

                    <q-item-section avatar class="col">
                      {{ i.name }}
                    </q-item-section>
                    <q-btn class="button" flat icon="img: /img/edit.svg" />
                    <q-btn class="button" flat icon="img: /img/short_down.svg" />
                    <q-btn class="button" flat icon="img: /img/short_up.svg" />
                    <q-btn class="button" flat :icon="getScriptItemStatusIcon(i)" />
                    <q-btn class="button" flat icon="img: /img/trash_full.svg" @click="deleteScriptItem(i)"/>
                  </q-item>
                </q-list>
              </div>

            </q-expansion-item>
          </q-list>
        </div>

        <div class="col-xs-12 col-sm-12 col-md col-lg list_with_caption_form">
          <div class="row header-form">
            <div class="column col justify-center">
              {{  $t('documents') }}
            </div>
          </div>

          <q-list>
            <q-item
              v-for="d in documents"
              :key="d.type"
              v-ripple
              clickable
              @click = "addDocument(d)">

              <q-item-section avatar>
                <q-btn class="button" flat icon="img: /img/circle_chevron_left.svg"/>
              </q-item-section>
              <q-item-section> {{ $t(d.label) }} </q-item-section>
            </q-item>
          </q-list>
        </div>
      </div>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useQuasar  } from 'quasar'
import { getServers, postObjects, deleteObjects, queryObjects} from "../backend/backend";
import { UuidTool} from 'uuid-tool'
import { Buffer } from 'buffer';
import { useI18n } from "vue-i18n"


const $q = useQuasar()
const i18n = useI18n()
const serverCode = ref()
const selScript = ref()
const scriptDef = ref([])

const addDocument = (d)=>{
  if (selScript.value){
    let item = {
      id: UuidTool.newUuid(),
      condition: true,
      curType: d.type,
      name: i18n.t(d.label),
      condParam: '',
      pos: selScript.value.items.length
    }

    let copy = {...selScript.value}
    copy.items.push(item)
    postObjects(serverCode.value, 'ScriptDef',[copy])
      .then(()=>scriptDef.value.items = copy.items)
  }
}

const removeScript = (s)=>{
  let idx = scriptDef.value.indexOf(s)

  if (idx != -1)
    scriptDef.value.splice(idx,1)
}

const deleteScript = (s)=>{
  deleteObjects(serverCode.value, "ScriptDef", `"id"=${s.id}`)
  .then(()=>removeScript(s))
  .catch((error)=> console.log(error))
}

const expandAll = ()=>{
  scriptDef.value.forEach(el=>el.expanded = true)
}

const collapseAll = ()=>{
  scriptDef.value.forEach(el=>el.expanded = false)
}

const isSelected = (sd)=>{
  return selScript.value && selScript.value.id == sd.id
}

const getScriptIcon = (sd)=>{
  var name = selScript.value && selScript.value.id == sd.id ? 'folder_open' : 'folder'
  return `img: /img/${name}.svg`
}

const scriptClick = (sd)=>{
  selScript.value = sd
  selScript.value.expanded = true
}

const getScriptItemStatusIcon = (si)=>{
  var name = true ? 'checkbox_checked' : 'checkbox'
  return `img: /img/${name}.svg`
}

onMounted(()=>{
  $q.loading.show()
  getServers()
    .then((response)=>{
      serverCode.value = response[0].code
      return queryObjects(serverCode.value, [{name: 'ScriptDef', filter: '"rem" != 1 or "rem" is null'}])
    })
    .then((response)=>rcvObjects(response))
    .catch((error)=>{console.log("get data ERROR! " + error)})
    .finally(()=>{ $q.loading.hide() })
})

const rcvObjects = (data)=>{
  let defs = undefined

  data.data.forEach((el=>{
    if (el.name == 'ScriptDef')
      defs = el.data
  }))

  defs.sort((a,b)=>a.name.localeCompare(b.name))
  if (defs){
    defs.forEach(el=>scriptDef.value.push({expanded: true, ...el}))
  }
}

const documents = [
  {
    type:"Question",
    label:"documents_label.question"
  },
  {
    type:"Incass",
    label:"documents_label.incass"
  },
  {
    type:"Remnant",
    label:"documents_label.remnant"
  },
  {
    type:"Visit",
    label:"documents_label.visit"
  },
  {
    type:"Order",
    label:"documents_label.order"
  },
]

const genID = ()=>{
  let id = UuidTool.newUuid();
  let bytes = UuidTool.toBytes(id);
  let buf = Buffer.from(bytes.slice(12))
  return buf.readInt32BE();
}

const addScript = ()=>{
  const text = ref('')
  console.log("addScript")
  $q.dialog({
    title: i18n.t('script.add_script'),
    message: i18n.t('script.add_script_prompt'),
    cancel: true,
    prompt: {
      model: text,
      type: 'text',
    },
  }).onOk(() => {
    if (text.value){
      const s = {id: genID(), name: text.value, items: []}
      postObjects(serverCode.value, 'ScriptDef',[s])
      .then(()=>scriptDef.value.push(s))
    }
  })
}

const editScript = (value)=>{
  const text = ref(value.name)
  console.log("addScript")
  $q.dialog({
    title: i18n.t('script.add_script'),
    message: i18n.t('script.add_script_prompt'),
    cancel: true,
    prompt: {
      model: text,
      type: 'text',
    },
  }).onOk(() => {
    if (text.value){
      let clone = {...value}
      clone.name = text.value

      postObjects(serverCode.value, 'ScriptDef',[clone])
        .then(()=>{
          value.name = text
        })
    }
  })
}

</script>

<style lang="scss">
.q-btn {
    padding: 4px;
  }
</style>

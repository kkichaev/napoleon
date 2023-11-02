<template>
  <q-page class='page' style="padding: 25px">
    <q-card class='page-content-full-hight'>
      <div class="page-header">{{ $t('division.title') }}</div>
      <q-tree
        ref="tree"
        :nodes="divisions"
        node-key="id"
        no-connectors
        dense
        style="margin-top: 16px;"
        no-transition>

        <template v-slot:header-main="prop">
          <div class="row items-center depart">
            <div class="col">
              <div class="vertical-middle">{{ prop.node.name }}</div>
            </div>
            <div class="col-auto self-end text-right"  >
              <q-btn class="button" flat icon="img: /img/plus_circle_outline.svg" @click.stop="addDepart(prop.node)">
                <q-tooltip>
                  {{ $t('division.add_department') }}
                </q-tooltip>
              </q-btn>
              <q-btn class="button" flat icon="img: /img/user_plus.svg" @click.stop="addManager(prop.node)">
                <q-tooltip>
                  {{ $t('division.add_manager') }}
                </q-tooltip>
              </q-btn>
              <q-btn class="button root-button" flat icon="img: /img/edit.svg" @click.stop="editDepart(prop.node)">
                <q-tooltip>
                  {{ $t('edit') }}
                </q-tooltip>
              </q-btn>
            </div>
          </div>
        </template>

        <template v-slot:header-manager="prop">
          <div class="row items-center user">
            <div class="col">
              <div class="vertical-middle">{{ prop.node.name }}</div>
            </div>
            <div class="col-auto self-end text-right"  >
              <q-btn class="button" flat icon="img: /img/edit.svg" @click.stop="editManager(prop.node)">
                <q-tooltip>
                  {{ $t('edit') }}
                </q-tooltip>
              </q-btn>
              <q-btn class="button" flat icon="img: /img/trash_full.svg" @click.stop="deleteItem(prop.node)">
                <q-tooltip>
                  {{ $t('delete') }}
                </q-tooltip>
              </q-btn>
            </div>
          </div>
        </template>

        <template v-slot:header-depart="prop">
          <div class="row items-center depart">
            <div class="col">
              <div class="vertical-middle">{{ prop.node.name }}</div>
            </div>
            <div class="col-auto self-end text-right"  >
              <q-btn class="button" flat icon="img: /img/plus_circle_outline.svg" @click.stop="addDepart(prop.node)">
                <q-tooltip>
                  {{ $t('division.add_department') }}
                </q-tooltip>
              </q-btn>
              <q-btn class="button" flat icon="img: /img/user_plus.svg" @click.stop="addManager(prop.node)">
                <q-tooltip>
                  {{ $t('division.add_manager') }}
                </q-tooltip>
              </q-btn>
              <q-btn class="button" flat icon="img: /img/edit.svg" @click.stop="editDepart(prop.node)">
                <q-tooltip>
                  {{ $t('edit') }}
                </q-tooltip>
              </q-btn>
              <q-btn class="button" flat icon="img: /img/trash_full.svg" @click.stop="deleteItem(prop.node)">
                <q-tooltip>
                  {{ $t('delete') }}
                </q-tooltip>
              </q-btn>
            </div>
          </div>
        </template>
      </q-tree>
    </q-card>

    <q-dialog v-model="addManagerDialog">
      <q-card class="q-dialog-plugin">
        <q-card-section class="q-dialog__title">
          {{ $t('division.add_manager') }}
        </q-card-section>

        <q-card-section class="q-dialog__message">
          {{ $t('division.add_manager_prompt') }}
        </q-card-section>

        <q-card-section class="q-dialog-plugin__form">
          <q-select
            v-model="selDivision"
            :options="divlist"
            option-value = "id"
            option-label = "name"/>
        </q-card-section>

        <q-card-section class="q-dialog__message">
          {{ $t('division.add_manager_prompt2') }}
        </q-card-section>

        <q-card-section class="q-dialog-plugin__form">
          <q-input v-model="input"/>
        </q-card-section>

        <q-card-actions align="right" class="text-primary">
          <q-btn flat :label="$t('dialog.cancel')" v-close-popup />
          <q-btn flat :label="$t('dialog.ok')" v-close-popup @click="okAddManager"/>
        </q-card-actions>
      </q-card>
    </q-dialog>

    <q-dialog v-model="editManagerDialog">
      <q-card class="q-dialog-plugin">
        <q-card-section class="q-dialog__title">
          {{ $t('division.edit_manager') }}
        </q-card-section>

        <q-card-section class="q-dialog__message">
          {{ $t('division.add_manager_prompt') }}
        </q-card-section>

        <q-card-section class="q-dialog-plugin__form">
          <q-select
            v-model="selDivision"
            :options="divlist"
            option-value = "id"
            option-label = "name"/>
        </q-card-section>

        <q-card-section class="q-dialog__message">
          {{ $t('division.add_manager_prompt2') }}
        </q-card-section>

        <q-card-section class="q-dialog-plugin__form">
          <q-input v-model="input"/>
        </q-card-section>

        <q-card-actions align="right" class="text-primary">
          <q-btn flat :label="$t('dialog.cancel')" v-close-popup />
          <q-btn flat :label="$t('dialog.ok')" v-close-popup @click="okEditManager"/>
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, reactive, onMounted  } from "vue"
import { useQuasar } from 'quasar'
import { useI18n } from "vue-i18n"
import { getServers, getObjects, postObjects, deleteObjects, queryObjects} from "../backend/backend";

const $q = useQuasar()
const i18n = useI18n()
const addManagerDialog = ref(false)
const editManagerDialog = ref(false)
const input = ref('')
const divlist = ref([])
const selDivision = ref()
const selManager = ref()
const divisions = ref([])
const severCode = ref('')
const maxid = ref(0)
const tree = ref()
const divisionTree = ref({})

onMounted(()=>{
  $q.loading.show()
  getServers()
    .then((response)=>{
      severCode.value = response[0].code
      return queryObjects(severCode.value, [{name: "Division"}, {name: "DivisionManager"}])
    })
    .then((response)=>{createDivision(response)})
    .then(()=>tree.value.expandAll())
    .catch((error)=>{console.log("get data ERROR! " + error)})
    .finally(()=>{ $q.loading.hide() })
})

const createLeaf = (m) => {
  return {id: m.id, name : m.name, header: 'manager', division: m.division}
}

const createNode = (d, root=false)=>{
  return  {id: d.id, name : d.name, parent: d.parent, header: root ? 'main' : 'depart', children: []}
}

/*
return:
  root - root of tree
  id - max of data id + 1
  map - map of nodes {id, division}
*/
const buildTree = (data, leaves)=>{
  var root = undefined
  var map = {}
  var id = -1

  for (let d of data){
    if (id <= d.id)
        id = d.id + 1

    let node = undefined

    if (d.parent == 0){
      node = createNode(d, true)
      root = node
    }else{
      node = createNode(d)

      if (d.parent in map){
        map[d.parent].children.push(node)
      }
    }

    map[d.id] = node
  }

  for (let m of leaves){
    if (m.division in map)
      map[m.division].children.push(createLeaf(m))
  }

  return { root, id, map }
}

const createDivision = (data)=>{
  var division
  var manager

  data.data.forEach((el=>{
    if (el.name == 'Division')
      division = el.data
    else if ( el.name == 'DivisionManager' )
      manager = el.data
  }))

  var res = buildTree(division, manager)
  maxid.value = res.id
  divisions.value = reactive([res.root])
  divlist.value = division
  divisionTree.value = res.map
}

const collectID = (item)=>{
  var res = {div: [], mgr: []}

  const travel = (item, ids)=>{
    if (item.children){
      item.children.forEach(el=>travel(el, ids))

      if (item.header == 'depart'){
        if (ids.div.length > 0) ids.div += ','
        ids.div += `"${item.id}"`
      }
    }

    if (item.header == 'manager'){
      if (ids.mgr.length > 0) ids.mgr += ','
      ids.mgr += `"${item.id}"`
    }
  }

  travel(item, res)

  return res
}

const deleteItem = (item)=>{
  console.log("deleteItem: ", item)
  $q.dialog({
    title: i18n.t('division.delete'),
    message: i18n.t('division.delete_prompt'),
    cancel: true,
  }).onOk(() => {
    if (item.header == "depart"){
      var ids = collectID(item)
      deleteObjects(severCode.value, 'DivisionManager', `"id" in (${ids.mgr})`).
        then(()=>deleteObjects(severCode.value, 'Division', `"id" in (${ids.div})`)
        .then(()=>{
          parent = divisionTree.value[item.parent]
          if (parent){
            var idx = parent.children.indexOf(item)

            if (idx >= 0)
              parent.children.splice(idx, 1)
          }}
        ))
    }if (item.header == "manager")
      deleteObjects(severCode.value, 'DivisionManager', `"id"="${item.id}"`)
        .then(()=>{
          parent = divisionTree.value[item.division]
          if (parent){
            var idx = parent.children.indexOf(item)

            if (idx >= 0)
              parent.children.splice(idx, 1)
          }}
        )
  })
}

const editDepart = (item)=>{
  console.log("editDepart: ", item)
  const text = ref(item.name)
  $q.dialog({
    title: i18n.t('division.add_department'),
    message: i18n.t('division.add_department_prompt'),
    prompt: {
      model: text,
      type: 'text',
    },
    cancel: true,
  }).onOk(() => {
    var d = {id: item.id, name: text.value, parent: item.parent}
    postObjects(severCode.value, 'Division',[d])
      .then(()=>{
        item.name = text.value
      })
  })
}

const addDepart = (item)=>{
  console.log("addDepart: ", item)
  const text = ref('')
  $q.dialog({
    title: i18n.t('division.add_department'),
    message: i18n.t('division.add_department_prompt'),
    prompt: {
      model: text,
      type: 'text',
    },
    cancel: true,
  }).onOk(() => {
    var d = {id: maxid.value, name: text.value, parent: item.id}
    postObjects(severCode.value, 'Division',[d])
      .then(()=>{
        maxid.value += 1
        item.children.push({id: d.id, name: d.name, header: 'depart', children: []})
        console.log(item.children)
      })
  })
}

const editManager = (item)=>{
  editManagerDialog.value = true
  selManager.value = item
  selDivision.value = divisionTree.value[item.division]
  input.value = item.name
}

const addManager = (item)=>{
  addManagerDialog.value = true;
  selDivision.value = item
  input.value = ''
}

const okAddManager = ()=>{
  var man = {id: crypto.randomUUID(), name: input.value, division: selDivision.value.id}

  postObjects(severCode.value, 'DivisionManager',[man])
      .then(()=>{
        selDivision.value.children.push({id: man.id, name: man.name, header: 'manager', division: selDivision.value.id})
      })
}

const okEditManager = ()=>{
  var man = {id: selManager.value.id, name: input.value, division: selDivision.value.id}

  postObjects(severCode.value, 'DivisionManager',[man])
      .then(()=>{
        if (selDivision.value.id != selManager.value.id){
          var idx = divisionTree.value[selManager.value.division].children.indexOf(selManager.value)

          if (idx > -1) {
            divisionTree.value[selManager.value.division].children.splice(idx,1)
            divisionTree.value[selDivision.value.id].children.push(
              {id: man.id, name: man.name, header: 'manager', division: selDivision.value.id})
          }
        }

        selManager.value.name = input.value
      })
}


</script>

<style scoped lang="scss">

.user{
  width: 100%;
}

.depart{
  @extend .user;
  background-color: $control-backgroud;
}

.button{
  padding:0px;
  width: 35px;
}

.root-button{
  margin-right: 35px;
}

</style>

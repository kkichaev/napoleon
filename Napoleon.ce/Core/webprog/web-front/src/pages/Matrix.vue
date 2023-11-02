<template>
  <q-page class='page'>
    <q-card class='page-content-full-hight'>
      <div class="page-header">{{ $t('matrix.title') }}</div>
      <div class="row">
        <div class="col-xs-12 col-sm-12 col-md col-lg list_with_caption_form">
          <div class="row header-form">
            <div class="column col justify-center">
              {{  $t('matrices') }}
            </div>
            <q-btn class="button" flat icon="img: /img/plus_circle_outline.svg" @click="addMatrix"/>
          </div>

          <q-list>
            <q-item
              clickable
              v-ripple
              v-for="m in matrix"
              :key="m.name"
              @click = "matrixClick(m)">
              <q-item-section avatar>
                <q-icon color="primary" :name="getMatrixIcon(m)" />
              </q-item-section>

              <q-item-section>{{m.name}}</q-item-section>
              <q-btn class="button" flat icon="img: /img/edit.svg" @click.stop="editMatrix(m)"/>
              <q-btn class="button" flat icon="img: /img/trash_full.svg" @click.stop="deleteMatrix(m)"/>
            </q-item>
          </q-list>
        </div>

        <div class="col-xs-12  col-sm-12 col-md col-lg list_with_caption_form">
          <div class="row header-form">
            <div class="column col justify-center">
              {{  $t('goods') }}
            </div>
            <q-btn class="button" flat icon="img: /img/plus_circle_outline.svg" @click.stop="priceShow"/>
          </div>

          <div
            ref="scrollMtxTgt"
            class="scrollarea">

            <q-infinite-scroll
              ref="scrollMatrix"
              @load="onLoad"
              :offset="250"
              :scroll-target="scrollMtxTgt">

              <div class="row"
                v-for="i, index in showItems"
                :key="i.id">

                <div class="col idx_col">
                  {{ i.order + 1 }}
                </div>

                <div class="col">
                   {{ getItemName(i) }}
                </div>

                <q-btn class="button" flat icon="img: /img/short_down.svg" @click="downItem(index)"/>
                <q-btn class="button" flat icon="img: /img/short_up.svg" @click="upItem(index)"/>
                <q-btn class="button" flat icon="img: /img/trash_full.svg" @click="deleteItem(index)"/>
              </div>

              <template v-slot:loading>
                <div class="row justify-center q-my-md">
                  <q-spinner-dots color="primary" size="40px"></q-spinner-dots>
                </div>
              </template>
            </q-infinite-scroll>
          </div>
        </div>
      </div>

      <q-dialog
        v-model="priceDlg"
        full-height
        full-width>

        <q-card
          class="q-dialog-plugin"
          style="padding: 10px">

          <div class="row">
            <div class="col-xs-12 col-sm-12 col-md col-lg dlg-form">
              <div class="row header-form">
                <div class="column col justify-center">
                  {{ $t('matrix.price') }}
                </div>
              </div>

              <q-scroll-area class="dlg-scroll">
                <q-tree
                  :nodes="priceTree"
                  node-key="id"
                  no-transition>

                  <template v-slot:header-folder="prop">
                    <div class="row no-wrap items-center folder-row" style="width: 100%; padding-right: 15px">
                      <div class="col">  {{ prop.node.name}} </div>
                      <q-btn class="button" flat icon="img: /img/plus_circle_outline.svg" @click.stop="addFolder(prop.node)"/>
                    </div>
                  </template>

                  <template v-slot:body-price="prop">
                    <div class="row no-wrap items-center" style="width: 100%; padding-right: 15px">
                      <span class='text-black col'>{{ prop.node.name }}</span>
                      <q-btn class="button" flat icon="img: /img/plus_circle_outline.svg" @click.stop="addItem(prop.node)"/>
                    </div>
                  </template>
                </q-tree>
              </q-scroll-area>
            </div>

            <div class="col-xs-12  col-sm-12 col-md col-lg dlg-form">
              <div class="row header-form">
                <div class="column col justify-center">
                  {{ $t('matrix.selected_goods') }}
                </div>
              </div>

              <div
                ref="scrollPriceTgt"
                class="scrollarea">

                <q-infinite-scroll
                  ref="scrollPrice"
                  :offset="250"
                  :scroll-target="scrollPriceTgt"
                  @load="onLoadPrice">

                  <div class="row"
                    v-for="i, index in showPrice"
                    :key="i.id">

                    <div class="col">
                      {{ i.name }}
                    </div>

                    <q-btn class="button" flat icon="img: /img/trash_full.svg" @click.stop="deletePrice(index)"/>
                  </div>
                  <template v-slot:loading>
                    <div class="row justify-center q-my-md">
                      <q-spinner-dots color="primary" size="40px"></q-spinner-dots>
                    </div>
                  </template>
                </q-infinite-scroll>
              </div>
            </div>
          </div>

          <q-card-actions align="right" class="row text-primary">
            <q-btn flat :label="$t('dialog.cancel')" v-close-popup />
            <q-btn flat :label="$t('dialog.ok')" v-close-popup @click="selectPriceOK"/>
          </q-card-actions>
        </q-card>
      </q-dialog>
    </q-card>
  </q-page>
</template>

<script setup>

import { ref, onMounted } from 'vue'
import { useQuasar  } from 'quasar'
import { useI18n } from "vue-i18n"
import { getServers, postObjects, deleteObjects, queryObjects} from "../backend/backend";

const $q = useQuasar()
const i18n = useI18n()
const priceDlg = ref(false)
const selMatrix = ref('')
const matrix = ref([])
const priceTree = ref()
const serverCode = ref('')
const priceMap = ref({})
const showItems = ref([])
const showPrice = ref([])
const priceItems = ref([])
const scrollMtxTgt = ref()
const scrollMatrix = ref()
const scrollPriceTgt = ref()
const scrollPrice = ref()
const SLICE_QTY = 50

const moveMatrixItem = (matrix, index, down)=>{
  const items = matrix.items
  const pos = down ? index+1 : index-1
  const item = items[index]
  const prev = items[pos]

  item.order = prev.order
  prev.order = index

  items.splice(index,1)
  items.splice(pos,0,item)
}
const moveItem = (index, down = false)=>{
  const clone = {...selMatrix.value}
  moveMatrixItem(clone, index, down)

  postObjects(serverCode.value, 'Matrix',[clone]).
    then(()=>{
      selMatrix.value.items = clone.items
    }).
    finally(()=>{
      scupdateScrollMatrix()
      $q.loading.hide()
    })
}

const upItem = (index)=>{
  console.log("upItem: ", index)

  if (index > 0 && selMatrix.value)
    moveItem(index)
}

const downItem = (index)=>{
  console.log("downItem: ", index)

  if (index < showItems.value.length - 1)
    moveItem(index, true)
}

const updateViewItems = (from, ref, index, done)=>{
  if (from && ref.value){
    console.log("onLoad....", index)

    if (index == 1)
      ref.value = []

    var pos = ref.value.length

    ref.value.push(...from.slice(pos, pos + SLICE_QTY))

    if ( ref.value.length >= from.length){
      done(true)
      return
    }
  }

  done()
}

const onLoad = (index, done)=>{
  updateViewItems(selMatrix.value.items, showItems, index, done)
}

const onLoadPrice = (index, done)=>{
  updateViewItems(priceItems.value, showPrice, index, done)
}

const getItemName = (item)=>{
  if ( item.id in priceMap.value )
    return priceMap.value[item.id].name
  else
    return item.id
}

const selectPriceOK = ()=>{
  $q.loading.show()

  if (selMatrix.value){
    let clone = { ...selMatrix.value}
    clone.items = []
    priceItems.value.forEach((el)=>clone.items.push({id: el.id, order: clone.items.length}))

    postObjects(serverCode.value, 'Matrix',[clone]).
      then(()=>selMatrix.value.items = clone.items).
      finally(()=>{
        scupdateScrollMatrix()
        $q.loading.hide()
      })
  }
}

const priceShow = ()=>{
  if (selMatrix.value){
    priceItems.value = []
    priceDlg.value=true
    showPrice.value=[]

    selMatrix.value.items.forEach(el=>{
      if (el.id in priceMap.value)
        priceItems.value.push(priceMap.value[el.id])
    })
  }
}

const createNode = (d)=>{
  return { id: d.id, name: d.name, header: 'folder', children: [] }
}

const createLeaf = (d)=>{
  return { id: d.id, name: d.name, body: 'price' }
}

const buildTree = (data, leaves)=>{
  var root = undefined
  var map = {}

  for (let d of data){
    let node = undefined

    if (d.parent == 0){
      node = createNode(d)
      root = node
    }else{
      node = createNode(d)

      if (d.parent in map){
        map[d.parent].children.push(node)
      }
    }

    map[d.fid] = node
  }

  for (let m of leaves){
    if (m.fid in map)
      map[m.fid].children.push(createLeaf(m))
  }

  return root
}

onMounted(()=>{
  $q.loading.show()
  getServers()
    .then((response)=>{
      serverCode.value = response[0].code
      return queryObjects(serverCode.value, [{name: 'Matrix'}, {name: 'Folder'}, {name: 'Price'}])
    })
    .then((response)=>rcvObjects(response))
    .catch((error)=>{console.log("get data ERROR! " + error)})
    .finally(()=>{ $q.loading.hide() })
})

const rcvObjects = (data)=>{
  var folder = []
  var price = []

  data.data.forEach((el=>{
    if (el.name == 'Folder')
      folder = el.data
    else if (el.name == 'Price')
      price = el.data
    else if (el.name == 'Matrix')
      matrix.value = el.data
  }))

  priceTree.value = [buildTree(folder, price)]
  price.forEach(el=>priceMap.value[el.id] = el)

  if (matrix.value && matrix.value.length > 0)
    selMatrix.value = matrix.value[0]
}

const addFolder = (folder)=>{
  var col = []
  extPrice(folder, col)

  var ids = []
  priceItems.value.forEach(e=>ids.push(e.id))
  var price = [...priceItems.value]
  col.forEach(e=>{if (!ids.includes(e.id)) price.push(e)})
  priceItems.value = price

  updateScrollPrice()
}

const updateScrollPrice = ()=>{
  scrollPrice.value.resume()
  scrollPrice.value.poll()
}

const extPrice = (folder, output)=>{
  for( var c of folder.children){
    if (c.body)
      output.push(c)
    else if (c.children)
      extPrice(c, output)
  }
}

const addItem = (item)=>{
  var ids = []
  priceItems.value.forEach(e=>ids.push(e.id))

  if (!ids.includes(item.id)){
    priceItems.value.push(item)
    updateScrollPrice()
  }
}

const editMatrix = (matrix)=>{
  const text = ref(matrix.name)
  $q.dialog({
    title: i18n.t('matrix.edit_matrix'),
    message: i18n.t('matrix.add_matrix_prompt'),
    cancel: true,
    prompt: {
      model: text,
      type: 'text',
    },
  }).onOk(() => {
    if (text.value){
      $q.loading.show()
      var m = {name: text.value, items: matrix.items}
      deleteObjects(serverCode.value, 'Matrix', `"name" = "${matrix.name}"`).
        then(()=>postObjects(serverCode.value, 'Matrix',[m])
        .then(()=>matrix.name=text.value))
        .finally(()=> $q.loading.hide())
    }
  })
}

const deleteMatrix = (item)=>{
  var idx = matrix.value.indexOf(item)
  if (idx > -1){
    deleteObjects(serverCode.value, 'Matrix', `"name" = "${item.name}"`).
      then(()=>{
        matrix.value.splice(idx, 1)
        showItems.value = []
      })
      .finally(()=> $q.loading.hide())
  }
}

const matrixClick = (mtx)=>{
  console.log("matrixClick")
  showItems.value = []
  selMatrix.value = mtx

  scupdateScrollMatrix()
}

const scupdateScrollMatrix = ()=>{
  scrollMatrix.value.reset()
  scrollMatrix.value.resume()
  scrollMatrix.value.poll()
}

const getMatrixIcon = (matrix)=>{
  return selMatrix.value.name == matrix.name ? 'img: /img/folder_open.svg' : 'img: /img/folder.svg'
}

const deleteItem = (idx)=>{
  if (selMatrix.value){
    var clone = {...selMatrix.value}
    if (idx > -1){
      $q.loading.show()
      clone.items.splice(idx, 1)
      clone.items.forEach((el,idx)=>el.order=idx)
      postObjects(serverCode.value, 'Matrix',[clone]).
        then(()=>{
          selMatrix.value.items = clone.items
          showItems.value.splice(idx, 1)
        }).
        finally(()=>{
          $q.loading.hide()
        })
    }
  }
}

const addMatrix = ()=>{
  const text = ref('')
  console.log("addMatrix: ")
  $q.dialog({
    title: i18n.t('matrix.add_matrix'),
    message: i18n.t('matrix.add_matrix_prompt'),
    cancel: true,
    prompt: {
      model: text,
      type: 'text',
    },
  }).onOk(() => {
    if (text.value){
      const m = {name: text.value, items: []}

      postObjects(serverCode.value, 'Matrix',[m])
        .then(()=>{
          matrix.value.push(m)
          selMatrix.value = m
          showItems.value = []
          scupdateScrollMatrix()
        })
    }
  })
}

const deletePrice = (idx)=>{
  if ( idx != -1){
    priceItems.value.splice(idx,1)
    showPrice.value.splice(idx,1)
  }
}

</script>

<style lang="scss">
  .scrollarea{
    max-height: calc((100vh - 100px) - 100px);
    overflow: auto;
  }

  .num_col{
    max-width: 15px;
  }

  .q-btn {
    padding: 4px;
  }

  .dlg-form{
    border: 2px solid $controls;
    min-height: calc((100vh - 120px));
    margin-right: 2px;

    @media (max-width: $breakpoint-sm-max){
      min-height: calc(100vh / 2 - 70px);
      margin-right: 0px;
    }

    .dlg-scroll {
      height: calc(100% - 65px)
    }
  }

  .dlg-form + .dlg-form{
    margin-left: 2px;
    margin-right: 0px;

    @media (max-width: $breakpoint-sm-max){
      margin-top: 10px;
      margin-left: 0px;
      margin-right: 0px;
    }
  }

  .folder-row{
    background-color: #D9D9D933
  }

  .idx_col{
    padding-left: 2px;
    max-width: 30px !important
  }

</style>

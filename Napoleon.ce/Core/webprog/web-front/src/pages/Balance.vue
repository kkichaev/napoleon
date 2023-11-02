<template>
  <q-page class='page'>
    <q-card class='page-content' >
      <div class="page-header">
        {{ $t('balance.title') }}
      </div>

      <div
        class="group-header">
        {{ $t('balance.details') }}
      </div>

      <div style="marginTop:8px">
        {{ $t('balance.remnants') }} {{ balance.sum }}
      </div>

      <div style="marginTop:8px">
        {{ dateRangeLabel }}
        <q-btn flat square size='xs' @click="showDateDialog" icon="img: /img/arrow.svg"/>
      </div>

      <div style="margin-top:19px">
        <q-table
          style="padding: 0px; width: 100%"
          :rows="rows"
          :columns="columns"
          row-key="id"
          bordered
          separator="cell">

          <template v-slot:bottom-row>
            <q-tr>
              <q-td colspan="100%">
                {{  $t('balance.expense_for_range') }} {{ expense }}
              </q-td>
            </q-tr>
            <q-tr>
              <q-td colspan="100%">
                {{  $t('balance.incoming_for_range') }} {{ incoming }}
              </q-td>
            </q-tr>
          </template>
        </q-table>
      </div>

      <q-dialog v-model="dialog">
        <q-card>
          <q-card-section>
            <div>{{ $t('balance.selectDate') }}</div>
          </q-card-section>

          <q-date
            v-model="dateModel"
            :options="getRestrictSelection"
            range
            />

          <q-card-actions align="right">
            <q-btn flat label="Отмена" color="primary" v-close-popup/>
            <q-btn flat label="OK" color="primary" v-close-popup @click="applayRange"/>
          </q-card-actions>
        </q-card>
      </q-dialog>
    </q-card>
  </q-page>
</template>

<script setup>

import { onMounted, ref, computed } from 'vue';
import { getBallance, getServers, zdig } from '../backend/backend'
import { useI18n } from "vue-i18n";
import { fmtDate } from '../backend/helper'

const dialog = ref(false)
const i18n = useI18n()
const rows = ref()
const balance = ref({sum: 0, payments: [], servers: []})
const server = ref({address: '', code : '', name : '', port: '', token: ''})
const dateRangeLabel = ref('')
const dateModel = ref({ from: '2000/01/01', to: '2000/01/01'})
const selectedRange = ref(dateModel.value)
const incoming = ref(0)
const expense = ref(0)

const columns = [
  { label: i18n.t('balance.table_headers.data'), align: 'left', field: 'date', headerStyle: 'width: 14%', format: (val)=>fmtDate(val)},
  { align: 'center', label: i18n.t('balance.table_headers.count_of_users'), align: 'left', field: 'agents', headerStyle: 'width: 14%',  },
  { label: i18n.t('balance.table_headers.project_name'), align: 'left', field: 'name', headerStyle: 'width: 14%',  },
  { label: i18n.t('balance.table_headers.tarif'), align: 'left', field: 'tarif', headerStyle: 'width: 14%',  },
  { label: i18n.t('balance.table_headers.outcoming'), align: 'left', field: 'expense',  headerStyle: 'width: 14%',  },
  { label: i18n.t('balance.table_headers.incoming'), align: 'left', field: 'sum',  headerStyle: 'width: 14%',  },
  { label: i18n.t('balance.table_headers.sum'), align: 'left', field: 'rest', headerStyle: 'width: 14%', },
]

const covertToServerDate = (val)=>{
  return val.replaceAll('/','')
}

const sync = ()=>{
  var from = ""
  var to = ""

  if (selectedRange.value.from == undefined){
    from = selectedRange.value;
    to = selectedRange.value;
  }else{
    from = selectedRange.value.from
    to = selectedRange.value.to
  }

  incoming.value = 0
  expense.value = 0

  getBallance({from: covertToServerDate(from), to: covertToServerDate(to)})
    .then((responce)=>{
      balance.value = responce;
    })
    .then(()=>getServers())
    .then((responce)=>{
      server.value = responce[0]
      var servers = new Map()

      for(s of responce)
        servers.set(s.code, s)

      var data = []
      var id = 0

      for (var s of balance.value.servers){
        s.type = 1
        var t = servers.get(s.serverid)
        s.name = t.name
        s.tarif = s.expense / s.agents
        s.id = id
        data.push(s)
        id++

        expense.value += s.expense
      }

      for (var s of balance.value.payments){
        s.type = 0
        s.id = id
        data.push(s)
        id++

        incoming.value += s.sum
      }

      data.sort((x,y)=>{
        var res = x.date - y.date

        if (res == 0)
          return x.type - y.type

        return res
      })

      var rest = balance.value.sum
      data.reverse().forEach((e)=>{
        e.rest = rest

        if ('expense' in e){
          rest += e.expense
        }else if ('sum' in e){
          rest -= e.sum
        }
      })


      rows.value = data
    })
    .catch((error)=>console.log("servers error: " + error))
}

const showDateDialog = ()=>{
  dateModel.value = selectedRange.value
  dialog.value = true
}

const applayRange = ()=>{
  selectedRange.value = dateModel.value
  dateRangeLabel.value = getRangeLabel()
  sync()
}

const getStartCurrentMont = ()=>{
  const now = new Date();
  return `${now.getFullYear()}/${zdig(now.getMonth()+1)}/01`
}

const getRestrictSelection = (date)=>{
  const now = new Date();
  return date <= getFmtNowaday()
}

const getFmtNowaday = ()=>{
  const now = new Date();
  return `${now.getFullYear()}/${zdig(now.getMonth()+1)}/${zdig(now.getDate())}`
}

const getRangeLabel = ()=>{
  if (dateModel.value.from == undefined)
    return dateModel.value
  return `${dateModel.value.from} - ${dateModel.value.to}`
}
onMounted(()=>{
  var from = getStartCurrentMont()
  var to = getFmtNowaday()
  dateModel.value.from = from
  dateModel.value.to = to
  dateRangeLabel.value = getRangeLabel()

  sync()
})
</script>

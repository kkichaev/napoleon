<template>
  <q-page class='page' >
    <q-card class="page-content">
      <div class="page-header">
        {{$t('tarif.current')}}
      </div>

      <div
        class="group-header">
        {{ $t('tarif.details') }}
      </div>

      <div>
        <div>
          {{$t('tarif.currentName')}} {{ name }}
        </div>

        <div style="margin-top: 8px;">
          {{$t('tarif.cost')}} {{ cost }} {{ currency }}
        </div>

        <div style="margin-top: 8px;">
          {{$t('tarif.offer')}} <a :href="link" style="margin-left: 5px" target="_blank">{{$t('tarif.link')}}</a>
        </div>
      </div>
    </q-card>
  </q-page>
</template>

<script setup>
import { onMounted, ref, computed} from 'vue';
import { getTarifs } from '../backend/backend';
import { useMainStore } from 'src/stores/main-store';
import { getOfferLink, getCurrency } from 'src/backend/helper'

const name = ref('')
const cost = ref('')
const mainStore = useMainStore()
const currency = computed(()=>getCurrency(mainStore.user.account.currency))
const link = computed(()=>getOfferLink(mainStore.user.account.country))

onMounted(()=>{
  getTarifs()
    .then((responce)=>{
      console.log("getTarifs SUCCESS: " + responce)

      if (responce.length > 0){
        const data = responce[0]
        name.value = data.name
        cost.value = data.detail.cost
      }
    })
    .catch((error)=>console.log("getTarifs ERROR: " + error.message))
})

</script>

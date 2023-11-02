<template>
  <q-layout view="hHh Lpr fFf"> <!-- Be sure to play with the Layout demo on docs -->

    <!-- (Optional) The Header -->
    <q-header elevated>
      <q-toolbar>
        <q-space/>
        <q-btn flat :to="{name:'Register'}" @click="testClick" >Регистрация</q-btn>
      </q-toolbar>
    </q-header>
    <!-- (Optional) The Footer -->
    <q-footer>
      <q-toolbar>
      </q-toolbar>
    </q-footer>
    <q-page-container>
      <!-- This is where pages get injected -->
      <router-view />
    </q-page-container>

  </q-layout>
</template>

<script>
import { ref, computed } from 'vue'
import { useMainStore } from 'src/stores/mainStore'
import { Notify } from 'quasar'

export default {
  // name: 'LayoutName',

  setup () {
    const errorMessage = computed(() => useMainStore().errorMessage)
    const leftDrawerOpen = ref(false)

    return {
      errorMessage,
      leftDrawerOpen,
      toggleLeftDrawer () {
        leftDrawerOpen.value = !leftDrawerOpen.value
      },
      testClick () {
        console.log('test click')
      }
    }
  },
  watch: {
    errorMessage (newval, oldval) {
      console.log('watch: ', newval, oldval)

      if (newval) {
        Notify.create({
          message: newval,
          color: 'negative'
        })
      }

      useMainStore().errorMessage = ''
    }
  }
}
</script>

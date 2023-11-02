<template>
  <v-app class="bg-grey-lighten-4">
    <Navbar/>
    <v-main class="mx-4 mb-4 mt-4">
      <router-view/>
    </v-main>
  </v-app>
</template>

<script setup>
  import Navbar from '@/components/Navbar.vue'
  //
  components:{Navbar}
</script>

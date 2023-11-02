<template>
  <q-layout view="hHh Lpr fFf">
    <q-header elevated>
      <q-toolbar class="toolbar">
        <div class="col-1" />
        <q-avatar class="cursor-pointer">
          <img src="/img/ic_launcher.png" @click="$router.push({ name: 'Login' })"/>
        </q-avatar>
        <q-toolbar-title
          class="app-title cursor-pointer"
          @click="$router.push({ name: 'Login' })">{{
          $t("appTitle")
        }}</q-toolbar-title>
        <q-space />
        <lng-switch/>
      </q-toolbar>
    </q-header>

    <q-page-container>
      <div class="row">
        <div class="q-ml-auto q-mr-auto">
          <router-view />
        </div>
      </div>
    </q-page-container>
  </q-layout>
</template>

<script setup>

</script>

<style scoped></style>

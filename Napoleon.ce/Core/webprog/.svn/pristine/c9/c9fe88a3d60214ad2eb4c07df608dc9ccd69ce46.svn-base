<template>
  <q-page class='page'>
    <q-card class="page-content">
      <div class="page-header">
        {{ $t('menuLabel.profil') }}
      </div>

      <div class="group-header">
        {{ $t('profile.title') }}
      </div>

      <div>
        <q-input
          class = "input-field"
          v-model="name"
          :label="$t('profile.name')"
          filled
          :rules="[val => !!val || $t('field_is_required')]"
          hide-bottom-space/>

        <q-input
          class = "input-field"
          v-model="surname"
          :label="$t('profile.surname')"
          filled
          :rules="[val => !!val || $t('field_is_required')]"
          hide-bottom-space
          style="marginTop: 16px"/>

        <q-input
          class = "input-field"
          v-model="email"
          :label="$t('profile.email')"
          filled
          :rules="[val => !!val || $t('field_is_required')]"
          hide-bottom-space
          style="marginTop: 16px"
          disable readonly/>
      </div>

      <div class="group-header">
        {{ $t('profile.security') }}
      </div>

      <div>
        <div style="font-size: 14px">
            {{ $t('profile.changePassword') }}
            <q-btn flat square size='xs' icon="img: /img/edit.svg" style="padding:8px"/>
        </div>

        <q-btn
          class="login-btn"
          no-caps
          style="margin-top: 16px; width: 176px; height: 24px;"
          :label="$t('profile.save')"
          @click="save"
        />
      </div>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useMainStore } from "../stores/main-store";
import { changeUser } from "../backend/backend"

const store = useMainStore()
const email = ref('')
const name = ref('')
const surname = ref('')

onMounted(()=>{
  email.value = store.user.email
  name.value = store.user.name
  surname.value = store.user.surname
})

const save = ()=>{
  changeUser({name: name.value, surname: surname.value})
  .then((responce)=>console.log("changeUser SUCCESS! " + responce ))
  .catch((error)=>console.log("changeUser ERROR! " + error ))
}

</script>

<style scoped>
.input-field{
  width: 297px
}
</style>

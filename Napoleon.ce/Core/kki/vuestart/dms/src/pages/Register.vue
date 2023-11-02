<template>
  <q-page padding>
    <q-card
      style="min-width: 300px"
      class="fixed-center">

      <q-card-section>
        <div class="text-h6">Register</div>
      </q-card-section>

      <q-card-section>
        <q-form>
          <q-input
            label="Email"
            label-color="black"
            hint="Input email"
            v-model="email"/>

          <q-input
            label="Password"
            label-color="black"
            v-model="email"
            hint="Input password"/>
        </q-form>
      </q-card-section>

      <q-card-actions
        align="right">
        <q-btn @click="login">Вход</q-btn>
      </q-card-actions>

    </q-card>
  </q-page>
</template>

<script>
import { ref } from 'vue'
import { useMainStore } from '../stores/mainStore'

export default {

  setup () {
    const email = ref('test@test.com')
    const password = ref('123456')

    return {
      email,
      password,
      login () {
        useMainStore().login({ login: email.value, pwd: password.value })
      }
    }
  }

}
</script>

<template>
  <q-page padding>
    <q-card
      style="min-width: 300px"
      class="fixed-center">

      <q-card-section>
        <div class="text-h6">Login </div>
      </q-card-section>

      <q-card-section>
        <q-form ref="inputForm">
          <q-input
            label="Email"
            label-color="black"
            hint="Input email"
            v-model="email"
            :rules="[value => testEmail(value) || 'Введите email']"/>

          <q-input
            label="Password"
            label-color="black"
            v-model="password"
            hint="Input password"
            :rules="[value => value.length >= 6 || 'Не меньше 6 символов']"/>
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
import { testPattern } from './patterns'

export default {

  setup () {
    const inputForm = ref(null)
    const email = ref('test@test.com')
    const password = ref('123456')

    return {
      inputForm,
      email,
      password,
      login () {
        inputForm.value.validate().then(succes => {
          if (succes) { useMainStore().login({ login: email.value, pwd: password.value }) }
        })
      },
      testEmail (email) {
        return testPattern.email(email)
      }
    }
  }

}
</script>

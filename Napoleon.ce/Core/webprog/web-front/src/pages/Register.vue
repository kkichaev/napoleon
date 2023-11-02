<template>
  <q-page padding>
    <q-card class="q-ml-auto q-mr-auto login-form" flat>
      <q-card-section class="form-title text-center login-card-section">
        {{ $t("register.title") }}
      </q-card-section>

      <q-card-section
        v-if="registerError"
        class="error-message text-center login-card-section"
        style="margin-top: 8px">
        {{ errorMessage }}
      </q-card-section>

      <q-card-section v-if="!regSuccess" class="login-card-section">
        <q-form ref="form">
          <q-input
            style="margin-top: 32px"
            v-model="email"
            :label="$t('login.emailLabel')"
            filled
            :rules="[val => !!val || $t('field_is_required')]"
            hide-bottom-space/>

          <q-input
            style="margin-top: 32px"
            v-model="name"
            :label="$t('register.nameLabel')"
            filled
            :rules="[val => !!val || $t('field_is_required')]"
            hide-bottom-space/>

          <q-input
            style="margin-top: 32px"
            v-model="servername"
            :label="$t('register.projectName')"
            filled
            :rules="[val => !!val || $t('field_is_required')]"
            hide-bottom-space/>

          <q-input
            style="margin-top: 32px"
            :label="$t('login.passwordLabel')"
            filled
            v-model="password"
            :rules="[val => !!val || $t('field_is_required')]"
            hide-bottom-space
          />

          <q-input
            style="margin-top: 32px"
            :label="$t('register.passwordConfirmLabel')"
            filled
            v-model="password2"
            :rules="[val => !!val || $t('field_is_required')]"
            hide-bottom-space
          />
        </q-form>

        <q-btn
          class="login-btn login-form-text"
          no-caps
          style="margin-top: 32px"
          padding="0px"
          @click="register"
          :label="$t('register.registerAction')"
        />
      </q-card-section>

      <q-card-section v-if="regSuccess" login-card-section>
        <div class="text-center" style="margin-top: 40px">Проект и ваши данные успешно зарегестрированы</div>
        <div class="text-center" style="margin-top: 36px">На указанный email было отправлено письмо. Вам необходимо подтвердить свой адрес</div>
        <div class="text-center">после этого можно будет авторизироваься</div>

      </q-card-section>

      <q-card-section v-if="regSuccess">

      </q-card-section>

      <q-card-section class="text-center login-card-section" style="margin-top: 32px">
        <q-btn
          class="login-form-text"
          flat
          :label="$t('register.autorization')"
          no-caps
          padding="0px"
          :to="{name: 'Login'}"
        />
      </q-card-section>

      <q-card-section class="text-center login-card-section" style="margin-top: 32px">
        <q-btn class="login-form-text"
          flat
          no-caps
          padding="0px"
          href="mailto:info@grsoft.app">
          <u>{{ $t("login.supportEmail") }}</u>
        </q-btn>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { registerUser } from "../backend/backend";
import { useQuasar } from "quasar";
import { useI18n } from "vue-i18n";

const name=ref('')
const servername=ref('')
const email = ref("");
const password = ref("");
const password2 = ref("");
const regSuccess = ref(false)
const $q = useQuasar()
const form = ref(null)
const registerError = ref(false)
const errorMessage = ref('')
const i18n = useI18n()

function register() {
  form.value.validate().then((success)=>{
    if (success){
      registerUser({
        email: email.value.toLocaleLowerCase(),
        name: name.value,
        password: password.value, surname: "", servername: servername.value, locale: i18n.locale.value})
        .then((responce)=>{
          console.log("register success: " + responce)
          regSuccess.value = true
        })
        .catch((error)=>{
          registerError.value = true
          errorMessage.value = error

          for (var e of error.response.data){
            if (e.name == 'ServerAnswer' && e.data[0].message == 'email_already_exists'){
              errorMessage.value = i18n.t('register.emailAlreadyExists')
            }
          }
        })
    }
  })
}

// watch([hasError, regSuccess], ()=>{
//   console.log("watched: ", regSuccess)
//   if (hasError.value){
//     $q.notify({
//       message: useBackendStore().errorMessage,
//       color: 'red'
//     })

//     useBackendStore().clearError()
//   }
// })
</script>

<style scoped></style>

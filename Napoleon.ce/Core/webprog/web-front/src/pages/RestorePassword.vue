<template>
  <q-page class="login-page">
    <q-card class="q-ml-auto q-mr-auto login-form" flat>
      <template v-if="emailSent">
        <q-card-section login-card-section>
          <div class="text-center" style="margin-top: 40px">
            {{ $t("restore.emailSent") }}
          </div>
        </q-card-section>
      </template>
      <template v-if="!emailSent">
        <q-card-section class="form-title text-center login-card-section">
          {{ $t("restore.title") }}
        </q-card-section>

        <q-card-section class="login-card-section" style="margin-top: 40px">
          <q-form ref="form">
            <q-input
              v-model="email"
              :label="$t('login.emailLabel')"
              filled
              :rules="[(val) => testEmail(val) || $t('enter_email')]"
              hide-bottom-space
            />
          </q-form>

          <div class="text-center" style="margin-top: 11px" v-if="false">
            {{ $t("restore.instruction") }}
          </div>

          <q-btn
            class="login-btn login-form-text"
            no-caps
            style="margin-top: 32px"
            @click="recovery"
          >
            {{ $t("restore.restore") }}
          </q-btn>
        </q-card-section>

        <q-card-section class="login-card-section text-center">
          <q-btn
            flat
            no-caps
            class="login-form-text"
            :to="{ name: 'Login' }"
            style="margin-top: 32px"
            padding="0px"
          >
            {{ $t("restore.autorization") }}
          </q-btn>
        </q-card-section>

        <q-card-section
          class="text-center login-card-section"
          style="margin-top: 32px"
        >
          <q-btn
            class="login-form-text"
            flat
            no-caps
            padding="0px"
            :href="mailto()"
          >
            <u>{{ $t("login.supportEmail") }}</u>
          </q-btn>
        </q-card-section>
      </template>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref } from "vue";
import { testEmail, mailto } from "../backend/helper";
import { recoveryPassword } from "src/backend/user";
import { useQuasar } from "quasar";
import { useI18n } from "vue-i18n";

const $q = useQuasar();
const email = ref("");
const form = ref("");
const emailSent = ref(false);
const i18n = useI18n();

function recovery() {
  form.value.validate().then((success) => {
    if (success) {
      $q.loading.show();

      recoveryPassword(email.value.toLocaleLowerCase(), i18n.locale.value)
        .then((r) => {
          console.log(r);
          $q.loading.hide();
          emailSent.value = true;
        })
        .catch((err) => {
          console.log(err);
          $q.loading.hide();
          emailSent.value = true;
        });
    }
  });
}
</script>

<style scoped></style>

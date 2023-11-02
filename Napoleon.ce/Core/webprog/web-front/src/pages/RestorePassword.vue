<template>
  <q-page class="login-page">
    <q-card class="q-ml-auto q-mr-auto login-form" flat>
      <q-card-section class="form-title text-center login-card-section">
        {{$t('restore.title')}}
      </q-card-section>

      <q-card-section class="login-card-section" style="margin-top: 40px">
          <q-input
            :label="$t('login.emailLabel')"
            filled
            hide-bottom-space
          />

        <div class="text-center"
          style="margin-top: 11px;"
          v-if="false">

          {{ $t('restore.instruction') }}
        </div>

        <q-btn class="login-btn login-form-text"
          no-caps
          style="margin-top: 32px;">
          {{ $t('restore.restore') }}
        </q-btn>
      </q-card-section>

      <q-card-section class="login-card-section text-center">
        <q-btn
          flat
          no-caps
          class="login-form-text"
          :to="{name: 'Login'}"
          style="margin-top: 32px;"
          padding="0px">
          {{ $t('restore.autorization') }}
        </q-btn>
      </q-card-section>

      <q-card-section class="text-center login-card-section" style="margin-top: 32px">
        <q-btn class="login-form-text" flat no-caps padding="0px">
          <u>{{ $t("login.supportEmail") }}</u>
        </q-btn>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup>
</script>

<style scoped>

</style>

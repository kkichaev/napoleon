<template>
  <q-page class="login-page">
    <q-card class="q-ml-auto q-mr-auto login-form" flat>
      <div class="form-title text-center">
        {{ $t("login.title") }}
      </div>

      <div class="form-content">
        <div
          v-if="loginError"
          class="error-message text-center login-card-section"
        >
          {{ $t("login.error") }}
        </div>

        <q-form ref="form" @submit.prevent="login">
          <q-input
            name="username"
            id="username"
            v-model="email"
            :label="$t('login.emailLabel')"
            filled
            :rules="[(val) => !!val || $t('field_is_required')]"
            hide-bottom-space
            autocomplete="username"
          />

          <q-input
            name="j_password"
            id="j_password"
            :label="$t('login.passwordLabel')"
            filled
            v-model="password"
            :rules="[(val) => !!val || $t('field_is_required')]"
            hide-bottom-space
            type="password"
            autocomplete="current-password"
          />

          <q-checkbox
            :label="$t('login.savePassword')"
            v-model="remember"
            class="login-form-text"
            hide-bottom-space
          />

          <q-btn
            v-if="loginError"
            flat
            :label="$t('login.forgotPassword')"
            no-caps
            class="login-form-text fit"
            :to="{ name: 'RestorePassword' }"
          />

          <q-btn
            class="login-btn login-form-text"
            no-caps
            :label="$t('login.enter')"
            type="submit"
          />
        </q-form>

        <div class="column">
          <q-btn
            class="login-form-text"
            flat
            :label="$t('login.reqistration')"
            no-caps
            :to="{ name: 'Register' }"
          />

          <q-btn class="login-form-text" flat no-caps :href="mailto()">
            <u>{{ $t("login.supportEmail") }}</u>
          </q-btn>
        </div>
      </div>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref } from "vue";
import { useBackendStore } from "../stores/backend";
import { loginUser } from "../backend/user";
import { mailto } from "../backend/helper";
import { useRouter } from "vue-router";

const remember = ref(true);
const email = ref("");
const password = ref("");
const form = ref(null);
const router = useRouter();
const loginError = ref(false);

const login = () => {
  loginError.value = false;

  form.value.validate().then((success) => {
    if (success)
      loginUser({
        email: email.value.toLocaleLowerCase(),
        password: password.value,
        remember: remember.value,
      })
        .then((responce) => {
          // useBackendStore().isAuth = true;
          router.push({ name: "Profile" });
        })
        .catch((error) => (loginError.value = true));
  });
};
</script>

<style scoped>
.q-btn {
  padding: 0px;
}

.form-content * + * {
  margin-top: 32px;
}

.form-title {
  margin-top: 32px;
}

.form-title + .form-content .error-message {
  margin-top: 8px;
}

.form-title + .form-content .q-form {
  margin-top: 40px;
}

.form-title + .form-content .error-message + * {
  margin-top: 8px;
}
</style>

<template>
  <q-btn-dropdown
    flat
    :label="getLocaleLabel($i18n.locale)"
    no-caps
    dropdown-icon="img: /img/arrow-white.png">
    <q-list>
      <q-item
        v-for="opt in locales"
        clickable
        v-close-popup
        @click="selectLocale(opt.value)"
        :key="opt.value"
      >
        <q-item-section>
          <q-item-label>{{ opt.label }}</q-item-label>
        </q-item-section>
      </q-item>
    </q-list>
  </q-btn-dropdown>
</template>

<script setup>
import { useQuasar, Quasar, LocalStorage } from "quasar";
import { useI18n } from "vue-i18n";

const $q = useQuasar()
const i18n = useI18n()

const locales = [
  { value: "en-US", label: "En" },
  { value: "ru", label: "Ru" },
];

function getLocaleLabel(key) {
  var res = "";
  if (key == "") {
    key = $q.lang.isoName;
  }
  locales.every((loc) => {
    if (loc.value === key) {
      res = loc.label;
      return false;
    }
    return true;
  });

  return res;
}

function selectLocale(key) {
  i18n.locale.value = key;

  const langList = import.meta.glob('../../node_modules/quasar/lang/*.mjs')

  try {
    langList[ `../../node_modules/quasar/lang/${ key }.mjs` ]().then(lang => {
      Quasar.lang.set(lang.default)
    })

    console.log("lang changed: " + key)
  }
  catch (err) {
    console.log("lang error: " + err)
  }

  LocalStorage.set('locale', key)
}
</script>

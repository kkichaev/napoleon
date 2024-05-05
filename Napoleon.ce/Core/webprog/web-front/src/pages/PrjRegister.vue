<template>
  <q-page padding>
    <q-card class="q-ml-auto q-mr-auto login-form" flat style="width: 396px">
      <q-card-section class="form-title text-center login-card-section">
        {{ $t("prjreg.title") }}
      </q-card-section>

      <q-card-section
        class="login-form-text text-center login-card-section"
        style="margin-top: 40px"
      >
        {{ $t("prjreg.selectCountry") }}
      </q-card-section>

      <q-card-section
        class="login-form-text text-center login-card-section"
        style="margin-top: 40px"
      >
        <q-btn-dropdown
          class="dropdown-btn"
          :label="getLabel"
          align="left"
          split
          no-caps
        >
          <q-list>
            <q-item
              v-for="item in data"
              :key="item.id"
              clickable
              v-close-popup
              @click="onItemClick(item)"
            >
              <q-item-section>
                <q-item-label>{{ $t(`countries.${item.id}`) }}</q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
        </q-btn-dropdown>
      </q-card-section>

      <q-card-section
        class="login-card-section text-center"
        style="margin-top: 16px"
      >
        <div class="link-text">
          {{ $t("prjreg.autoCurrencySelection") }}
        </div>
      </q-card-section>

      <div style="margin-top: 16px">
        <div class="row items-center">
          <q-checkbox v-model="personal" :label="$t('prjreg.personalAgree')" />
        </div>
        <div class="row items-center">
          <q-checkbox v-model="offer">
            {{ $t("prjreg.offerAgree") }}
          </q-checkbox>
          <a :href="getLink()" style="margin-left: 5px" target="_blank"
            >{{ $t("prjreg.offer") }}
          </a>
        </div>
      </div>

      <q-card-section class="login-card-section text-center">
        <q-btn
          class="login-btn login-form-text"
          no-caps
          style="margin-top: 32px; width: 280px"
          padding="0px"
          @click="register"
          :label="$t('prjreg.next')"
        />
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script>
import { ref } from "vue";
import { data } from "../assets/countries.js";
import { getCurrency } from "../backend/helper";
import { registerProject } from "../backend/backend";
import { useBackendStore } from "../stores/backend";
import { useRouter } from "vue-router";

export default {
  setup() {
    const sel = ref({ id: "", currency: "" });
    const store = useBackendStore();
    const router = useRouter();
    const personal = ref(false);
    const offer = ref(false);

    function onItemClick(item) {
      sel.value = item;
    }

    data.every((c) => {
      if (c.id == "RUS") {
        sel.value = c;
        return false;
      }

      return true;
    });

    function register() {
      if (sel.value && personal.value && offer.value) {
        const obj = {
          country: sel.value.id,
          currency: sel.value.currency,
          serverid: store.serverCode,
        };
        registerProject(obj)
          .then(() => router.push({ name: "Profile" }))
          .catch(() => router.push({ name: "Error" }));
      }
    }

    function personalClick(event) {
      router.push({ name: "Personal" });
    }

    function offerClick(event) {
      router.push({ name: "Offer", params: { country: sel.value.id } });
    }

    return {
      data,
      onItemClick,
      sel,
      register,
      personal,
      offer,
      personalClick,
      offerClick,
    };
  },
  computed: {
    getLabel: function () {
      if (this.sel)
        return (
          this.$t("countries." + this.sel.id) +
          " (" +
          this.$t("prjreg.currency") +
          " " +
          getCurrency(this.sel.currency) +
          ")"
        );
      return "";
    },
  },
  methods: {
    getOffer: (param) => {
      var file = "offerge.pdf";
      if (["RUS", "BLR"].includes(param)) file = "offerrus.pdf";

      return "/" + file;
    },
    getLink: function () {
      return this.getOffer(this.sel.id);
    },
  },
};
</script>

<style scoped>
.link-text {
  color: #008767;
  font-size: 14px;
}
</style>

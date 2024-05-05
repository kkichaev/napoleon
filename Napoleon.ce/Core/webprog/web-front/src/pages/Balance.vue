<style>
.modal-wait {
  display: none;
  position: fixed;
  z-index: 1000;
  top: 0;
  left: 0;
  height: 100%;
  width: 100%;
  background: rgba(100, 100, 100, 0.8);
}
body.loading .modal-wait {
  overflow: hidden;
  display: block;
}
</style>
<template>
  <q-page class="page">
    <q-card class="page-content">
      <div class="page-header">
        {{ $t("balance.title") }}
      </div>

      <div class="row items-start">
        <div class="col">
          {{ $t("balance.remnants") }}&nbsp;&nbsp;{{
            balance.sum + " " + getCurrency(mainStore.user.account.currency)
          }}
        </div>
      </div>
      <div class="row q-mt-sm">
        <div class="col-sm-2 col-xs-12">
          <q-input
            filled
            v-model="amount"
            dense
            :error="sumError"
            :error-message="sumErrorMsg"
            :label="$t('balance.sum')"
          />
        </div>
      </div>
      <div class="row q-mt-sm">
        <div v-if="rus_pay" class="col-sm-2 col-xs-12">
          <q-btn
            v-if="false"
            :style="{ width: 100 + '%' }"
            class="glossy"
            rounded
            color="deep-orange"
            @click="yookassaPayment(false)"
            label="Оплата через СБП"
          />
          <q-btn
            :style="{ width: 100 + '%' }"
            class="glossy q-mt-sm q-mb-sm"
            rounded
            color="primary"
            label="Оплата картой"
            @click="yookassaPayment(true)"
          />
        </div>
        <div v-else class="col-sm-2 col-xs-12">
          <div id="paypal-button-container"></div>
          <!-- <q-btn
            color="primary"
            :label="$t('balance.credit')"
            @click="creditAccount"
          /> -->
        </div>
      </div>

      <div class="row">
        <div class="col-sm-1 col-xs-12">
          {{ $t("balance.details") }}
        </div>
        <div class="col-sm-2 col-xs-12">
          {{ dateRangeLabel }}
          <q-btn
            flat
            square
            size="xs"
            @click="showDateDialog"
            icon="img: /img/arrow.svg"
          />
        </div>
      </div>

      <div style="margin-top: 19px">
        <q-table
          style="padding: 0px; width: 100%"
          :rows="rows"
          :columns="columns"
          row-key="id"
          bordered
          separator="cell"
        >
          <template v-slot:bottom-row>
            <q-tr>
              <q-td colspan="100%">
                {{ $t("balance.expense_for_range") }} {{ expense }}
              </q-td>
            </q-tr>
            <q-tr>
              <q-td colspan="100%">
                {{ $t("balance.incoming_for_range") }} {{ incoming }}
              </q-td>
            </q-tr>
          </template>
        </q-table>
      </div>

      <q-dialog v-model="dialog">
        <q-card>
          <q-card-section>
            <div>{{ $t("balance.selectDate") }}</div>
          </q-card-section>

          <q-date v-model="dateModel" :options="getRestrictSelection" range />

          <q-card-actions>
            <q-btn
              flat
              :label="$t('dialog.cancel')"
              color="primary"
              v-close-popup
            />
            <q-btn
              flat
              :label="$t('dialog.ok')"
              color="primary"
              v-close-popup
              @click="applayRange"
            />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </q-card>
  </q-page>
  <div class="modal-wait"></div>
</template>

<script setup>
import { onMounted, ref, computed } from "vue";
import {
  getBallance,
  getServers,
  zdig,
  getPayPalClientID,
  createPayPalOrder,
  createYookassaOrder,
  commitPayPalOrder,
} from "../backend/backend";
import { useI18n } from "vue-i18n";
import { fmtDate, getCurrency } from "../backend/helper";
import { useMainStore } from "src/stores/main-store";
import { useQuasar } from "quasar";
import { loadScript } from "@paypal/paypal-js";

const mainStore = useMainStore();
const dialog = ref(false);
const i18n = useI18n();
const rows = ref();
const balance = ref({ sum: 0, payments: [], servers: [] });
const server = ref({ address: "", code: "", name: "", port: "", token: "" });
const dateRangeLabel = ref("");
const dateModel = ref({ from: "2000/01/01", to: "2000/01/01" });
const selectedRange = ref(dateModel.value);
const incoming = ref(0);
const expense = ref(0);
const rus_pay = mainStore.user.account.currency == "RUB";
const amount = ref(rus_pay ? "5000.00" : "50.00");
const $q = useQuasar();
var sumError = ref();
var sumErrorMsg = ref();
var valAmount = 0;

const columns = [
  {
    label: i18n.t("balance.table_headers.data"),
    align: "left",
    field: "date",
    headerStyle: "width: 14%",
    format: (val) => fmtDate(val),
  },
  {
    align: "center",
    label: i18n.t("balance.table_headers.count_of_users"),
    align: "left",
    field: "agents",
    headerStyle: "width: 14%",
  },
  {
    label: i18n.t("balance.table_headers.project_name"),
    align: "left",
    field: "name",
    headerStyle: "width: 14%",
  },
  {
    label: i18n.t("balance.table_headers.tarif"),
    align: "left",
    field: "tarif",
    headerStyle: "width: 14%",
  },
  {
    label: i18n.t("balance.table_headers.outcoming"),
    align: "left",
    field: "expense",
    headerStyle: "width: 14%",
  },
  {
    label: i18n.t("balance.table_headers.incoming"),
    align: "left",
    field: "sum",
    headerStyle: "width: 14%",
  },
  {
    label: i18n.t("balance.table_headers.sum"),
    align: "left",
    field: "rest",
    headerStyle: "width: 14%",
  },
];

const covertToServerDate = (val) => {
  return val.replaceAll("/", "");
};

const sync = () => {
  var from = "";
  var to = "";

  if (selectedRange.value.from == undefined) {
    from = selectedRange.value;
    to = selectedRange.value;
  } else {
    from = selectedRange.value.from;
    to = selectedRange.value.to;
  }

  incoming.value = 0;
  expense.value = 0;

  getBallance({ from: covertToServerDate(from), to: covertToServerDate(to) })
    .then((responce) => {
      balance.value = responce;
    })
    .then(() => getServers())
    .then((responce) => {
      server.value = responce[0];
      var servers = new Map();

      for (s of responce) servers.set(s.code, s);

      var data = [];
      var id = 0;

      for (var s of balance.value.servers) {
        s.type = 1;
        var t = servers.get(s.serverid);
        s.name = t.name;
        s.tarif = s.expense / s.agents;
        s.id = id;
        data.push(s);
        id++;

        expense.value += s.expense;
      }

      for (var s of balance.value.payments) {
        s.type = 0;
        s.id = id;
        data.push(s);
        id++;

        incoming.value += s.sum;
      }

      data.sort((x, y) => {
        var res = x.date - y.date;

        if (res == 0) return x.type - y.type;

        return res;
      });

      var rest = balance.value.sum;
      data.reverse().forEach((e) => {
        e.rest = rest;

        if ("expense" in e) {
          rest += e.expense;
        } else if ("sum" in e) {
          rest -= e.sum;
        }
      });

      rows.value = data;
    })
    .catch((error) => console.log("servers error: " + error));
};

function numberError(msg) {
  if (msg) {
    sumError.value = true;
    sumErrorMsg.value = msg;
  } else {
    sumError.value = null;
    sumErrorMsg.value = null;
  }
}

const getAmount = (minValue) => {
  try {
    const val = parseFloat(amount.value);
    if (!val) {
      numberError(i18n.t("balance.invalid_number"));
    } else {
      if (val < minValue) {
        numberError(i18n.t("balance.input_greater") + " " + minValue);
        return 0;
      }
      numberError(null);
      return val;
    }
  } catch (error) {
    numberError(i18n.t("balance.invalid_number"));
  }
  return 0;
};

const showDateDialog = () => {
  dateModel.value = selectedRange.value;
  dialog.value = true;
};

const applayRange = () => {
  selectedRange.value = dateModel.value;
  dateRangeLabel.value = getRangeLabel();
  sync();
};

const getStartCurrentMont = () => {
  const now = new Date();
  return `${now.getFullYear()}/${zdig(now.getMonth() + 1)}/01`;
};

const getRestrictSelection = (date) => {
  const now = new Date();
  return date <= getFmtNowaday();
};

const getFmtNowaday = () => {
  const now = new Date();
  return `${now.getFullYear()}/${zdig(now.getMonth() + 1)}/${zdig(
    now.getDate()
  )}`;
};

const getRangeLabel = () => {
  if (dateModel.value.from == undefined) return dateModel.value;
  return `${dateModel.value.from} - ${dateModel.value.to}`;
};

function openPaymentWindow(url) {
  document.body.classList.add("loading");
  const w = 480;
  const h = 600;
  const left = (screen.width - w) / 2 + screen.availLeft;
  var top = (screen.height - h) / 4;
  const strWindowFeatures = `popup,height=${h},width=${w},top=${top},left=${left}`;

  const win = window.open(url, "Payment window", strWindowFeatures);
  const timer = setInterval(() => {
    if (win.closed) {
      clearInterval(timer);
      document.body.classList.remove("loading");
      sync();
    }
  }, 500);
}

const yookassaPayment = async (byCard) => {
  try {
    valAmount = getAmount(1000);
    if (valAmount) {
      const url = "/api/yookassa/payments";
      const method = byCard ? "bank_card" : "sbp";
      const itemid = 1;
      const data = { amount: valAmount, method: method, itemid: itemid };
      const retData = await createYookassaOrder(data);
      console.log("ret", retData);
      const payUrl = retData?.url;
      if (payUrl) {
        openPaymentWindow(payUrl);
      } else {
        const msg = retData?.message;
        alert(msg ? msg : "Метод оплаты не работает");
      }
    }
  } catch (error) {
    console.log("Error", error);
  }
};

const loadPayPal = (cliId) => {
  try {
    loadScript({
      "client-id": cliId,
    }).then(function (paypal) {
      paypal
        .Buttons({
          createOrder: async function (data, actions) {
            valAmount = getAmount(10);
            if (valAmount) {
              const orderData = await createPayPalOrder(valAmount);
              if (orderData.id) {
                return orderData.id;
              }
              const errorDetail = orderData?.details?.[0];
              const errorMessage = errorDetail
                ? `${errorDetail.issue} ${errorDetail.description} (${orderData.debug_id})`
                : JSON.stringify(orderData);
              $q.notify(errorMessage);
            }
          },
          onApprove: async function (data, actions) {
            try {
              const orderData = await commitPayPalOrder(
                valAmount,
                data.orderID
              );
              const errorDetail = orderData?.details?.[0];

              if (errorDetail?.issue === "INSTRUMENT_DECLINED") {
                // (1) Recoverable INSTRUMENT_DECLINED -> call actions.restart()
                // recoverable state, per https://developer.paypal.com/docs/checkout/standard/customize/handle-funding-failures/
                return actions.restart();
              } else if (errorDetail) {
                // (2) Other non-recoverable errors -> Show a failure message
                throw new Error(
                  `${errorDetail.description} (${orderData.debug_id})`
                );
              } else if (!orderData.purchase_units) {
                throw new Error(JSON.stringify(orderData));
              } else {
                // (3) Successful transaction -> Show confirmation or thank you message
                // Or go to another URL:  actions.redirect('thank_you.html');
                // const transaction =
                //   orderData?.purchase_units?.[0]?.payments?.captures?.[0] ||
                //   orderData?.purchase_units?.[0]?.payments?.authorizations?.[0];
                // resultMessage(
                //   `Transaction ${transaction.status}: ${transaction.id}<br><br>See console for all available details`
                // );
                sync();
              }
            } catch (error) {
              console.error(error);
              $q.notify(error);
              // resultMessage(
              //   `Sorry, your transaction could not be processed...<br><br>${error}`
              // );
            }
          },
          style: {
            // Adapt to your needs
            layout: "vertical",
            color: "gold",
            shape: "pill",
            label: "paypal",
          },
        })
        .render("#paypal-button-container");
    });
  } catch (error) {
    // Add proper error handling
    console.error(error);
  }
};

onMounted(() => {
  var from = getStartCurrentMont();
  var to = getFmtNowaday();
  dateModel.value.from = from;
  dateModel.value.to = to;
  dateRangeLabel.value = getRangeLabel();

  if (!rus_pay) {
    getPayPalClientID().then(function (data) {
      loadPayPal(data.data.clientid);
    });
  }

  // document.body.classList.add("loading");
  // document.body.classList.remove("loading");
  sync();
});
</script>

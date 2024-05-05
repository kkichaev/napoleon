<template>
  <q-page class="page">
    <q-card class="page-content-full-hight">
      <div class="page-header">{{ $t("users.title") }}</div>

      <div class="row">
        <div class="col-xs-9 col-sm-8 col-md-3 col-lg-2 justify-end column">
          <q-btn
            class="form_field"
            no-caps
            :disabled="editDisabled"
            :label="$t('users.select_division')"
            @click.stop="askToEditDivision"
          >
          </q-btn>
        </div>
        <div class="col-xs-3 col-sm-4 col-md-1 col-lg" />
        <div class="col-xs-9 col-sm-8 col-md-3 col-lg-2">
          <div>{{ $t("users.select_project") }}</div>
          <div>
            <q-select
              class="form_field"
              v-model="project"
              :options="projects"
              option-value="code"
              option-label="name"
              @update:model-value="(value) => onChangedProjetc(value)"
            />
          </div>
        </div>
        <div class="col-xs-3 col-sm-4 col-md-1 col-lg" />
        <div class="col-xs-9 col-sm-8 col-md-3 col-lg-2">
          <div>{{ $t("users.select_user_type") }}</div>
          <div>
            <q-select
              class="form_field"
              v-model="userType"
              :options="userTypes"
              @update:model-value="(value) => onChangedUserType(value)"
            />
          </div>
        </div>
        <div class="col-xs-3 col-sm-4 col-md col-lg" />
        <div class="col-xs-9 col-sm-8 col-md-3 col-lg-2 justify-end column">
          <div class="form_field">
            <q-input v-model="filter">
              <template v-slot:append>
                <q-icon name="search" />
              </template>
            </q-input>
          </div>
        </div>
      </div>

      <div class="row">
        <q-table
          class="q-table"
          :columns="columns"
          row-key="id"
          :rows="rows"
          bordered
          wrap-cells
          separator="cell"
          v-model:selected="selected"
          :table-header-class="{ 'table-header': true }"
          :filter="filter"
          :filter-method="filterMethod"
          :selection="selection"
        >
          <template v-slot:header-cell-division="prop">
            <q-th :props="prop">
              {{ prop.col.label }}
            </q-th>
          </template>

          <template v-slot:body-cell-division="prop">
            <q-td>
              <div class="row items-center">
                <div class="col">
                  {{ prop.row.division ? prop.row.division.name : "" }}
                </div>
              </div>
            </q-td>
          </template>

          <template v-slot:body-cell-user="prop">
            <q-td>
              <div>{{ prop.row.user.name }}</div>
              <div>{{ prop.row.link ? prop.row.link.uuid : "" }}</div>
            </q-td>
          </template>

          <template v-slot:body-cell-access="prop">
            <q-td>
              <div>{{ fmtDate(prop.row.access) }}</div>
              <div>{{ fmtTime(prop.row.access) }}</div>
            </q-td>
          </template>

          <template v-slot:body-cell-status="prop">
            <q-td>
              <div class="row items-center">
                <div class="col column">
                  <div class="row">{{ statusTextLine1(prop.row) }}</div>
                  <div class="row" v-if="statusTextLine2(prop.row).length > 0">
                    {{ statusTextLine2(prop.row) }}
                  </div>
                </div>

                <div class="col-auto column">
                  <q-toggle
                    v-model="prop.row.status"
                    @update:model-value="(value) => askToAllow(value, prop.row)"
                  >
                    <q-tooltip>
                      {{ $t("users.change_connect_status") }}
                    </q-tooltip>
                  </q-toggle>
                </div>
              </div>
            </q-td>
          </template>
        </q-table>
      </div>

      <q-dialog v-model="editDivisionDlg">
        <q-card class="q-dialog-plugin">
          <q-card-section class="q-dialog__title">
            {{ $t("users.edit_division_dlg.title") }}
          </q-card-section>

          <q-card-section class="q-dialog__message">
            {{ $t("users.edit_division_dlg.prompt") }}
          </q-card-section>

          <q-card-section class="q-dialog-plugin__form">
            <q-select
              v-model="seldiv"
              :options="divlist"
              option-value="id"
              option-label="name"
            />
          </q-card-section>

          <q-card-actions align="right" class="text-primary">
            <q-btn flat :label="$t('dialog.cancel')" v-close-popup />
            <q-btn
              flat
              :label="$t('dialog.ok')"
              v-close-popup
              @click="okEditDivision"
            />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </q-card>
  </q-page>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import { ref, reactive, onMounted, watch } from "vue";
import { useMainStore } from "../stores/main-store";
import {
  getServers,
  queryObjects,
  reqConnects,
  postObjects,
  setConnects,
  deleteObjects,
} from "../backend/backend";
import { useQuasar, date } from "quasar";
import { fmtDate, fmtTime } from "../backend/helper";

const store = useMainStore();
const i18n = useI18n();
const filter = ref("");
const $q = useQuasar();
const editDivisionDlg = ref(false);
const divlist = ref([]);
const seldiv = ref();
const selected = ref();
const divisions = ref();
const project = ref("");
const projects = ref();
const agentRows = ref([]);
const managerRows = ref([]);
const rows = reactive([]);
const userType = ref("");
const reqConn = ref();
const editDisabled = ref(true);
const selection = ref("multiple");

const userTypes = [
  i18n.t("users.user_types.agents"),
  i18n.t("users.user_types.managers"),
];

const columns = [
  {
    name: "division",
    label: i18n.t("users.table_headers.division"),
    align: "left",
    field: "division",
    sortable: true,
    headerStyle: "width: 20%",
    sort: (a, b) => a.name.localeCompare(b.name),
  },
  {
    name: "user",
    align: "center",
    label: i18n.t("users.table_headers.user"),
    align: "left",
    field: "user",
    sortable: true,
    headerStyle: "width: 20%",
    sort: (a, b) => a.name.localeCompare(b.name),
  },
  {
    name: "version",
    label: i18n.t("users.table_headers.version"),
    align: "left",
    field: "version",
    sortable: true,
    headerStyle: "width: 20%",
  },
  {
    name: "access",
    label: i18n.t("users.table_headers.access"),
    align: "left",
    field: "access",
    sortable: true,
    headerStyle: "width: 20%",
    format: (val) => fmtDate(val),
  },
  {
    name: "status",
    label: i18n.t("users.table_headers.status"),
    align: "left",
    field: "status",
    sortable: true,
    headerStyle: "width: 20%",
  },
];

watch(selected, (newVal, oldVal) => {
  editDisabled.value = newVal == undefined || newVal.length == 0;
});

const filterMethod = () => {
  var res = [];
  var text = filter.value.toUpperCase();

  rows.forEach((el) => {
    var f = false;

    if (!f && el.division) f = el.division.name.toUpperCase().includes(text);

    if (!f && el.user) f = el.user.name.toUpperCase().includes(text);

    if (!f && el.version) f = el.version.toUpperCase().includes(text);

    if (!f && el.access) f = el.access.toUpperCase().includes(text);

    if (f) res.push(el);
  });

  return res;
};

const statusTextLine1 = (val) => {
  return val.status
    ? val.link
      ? i18n.t("users.status_connected")
      : val.req
      ? `${i18n.t("users.status_code")} ${val.req.code} `
      : ""
    : "";
};

const statusTextLine2 = (val) => {
  return val.status ? (val.link ? "" : regStr(val.req)) : "";
};

const regStr = (req) => {
  if (req) {
    var d = new Date("1970-01-01 0:0:0");
    d.setSeconds(d.getSeconds() + req.till);

    return `до ${date.formatDate(d, "DD.MM.YYYY HH:MM:SS")}`;
  }

  return "";
};

const onChangedUserType = (value) => {
  selected.value = undefined;
  var idx = userTypes.indexOf(value);
  rows.splice(0, rows.length);
  if (idx == 0) {
    agentRows.value.forEach((el) => rows.push(el));
    selection.value = "multiple";
  } else if (idx > 0) {
    managerRows.value.forEach((el) => rows.push(el));
    selection.value = "none";
  }
};

const onChangedProjetc = (value) => {
  $q.loading.show();
  reqConnects(project.value.code)
    .then((data) => (reqConn.value = data))
    .then(() =>
      queryObjects(project.value.code, [
          { name: "Division" },
          { name: "DivisionManager" },
          { name: "Agents" },
          { name: "UserActivity" },
          { name: "LinkedUsers" }
        ]))
    .then((response) => setData(response))
    .catch((error) => {
      console.log("getServers ERROR! " + error);
    })
    .finally(() => {
      $q.loading.hide()
    })
}

const okEditDivision = () => {
  if (seldiv.value) {
    var ags = [];
    selected.value.forEach((el) => ags.push(el.user.id));
    var updateDivision = [];

    for (var d of divisions.value) {
      var agents = [];
      for (var i = 0; i < d.agents.length; i++) {
        if (!ags.includes(d.agents[i].id)) agents.push(d.agents[i]);
        else if (!updateDivision.includes(d)) updateDivision.push(d);
      }

      d.agents = agents;
    }

    updateDivision.push(seldiv.value);
    ags.forEach((id) => seldiv.value.agents.push({ id: id }));

    postObjects(project.value.code, "Division", updateDivision).then(() => {
      console.log("updated SUCCES");
      selected.value.forEach((el) => (el.division = seldiv.value));
      selected.value = undefined;
    });
  }
};

const askToEditDivision = () => {
  if (selected.value) {
    seldiv.value =
      selected.value.length > 0 ? selected.value[0].division : undefined;
    var type = selected.value.length > 0 ? selected.value[0].type : undefined;

    if (type == "Agents") editDivisionDlg.value = true;
  }
};

onMounted(() => {
  $q.loading.show();
  getServers()
    .then((response) => {
      projects.value = response;
      project.value = response[0];
      return reqConnects(project.value.code);
    })
    .then((data) => (reqConn.value = data))
    .then(() =>
      queryObjects(project.value.code, [
        { name: "Division" },
        { name: "DivisionManager" },
        { name: "Agents" },
        { name: "UserActivity" },
        { name: "LinkedUsers" },
      ])
    )
    .then((response) => setData(response))
    .catch((error) => {
      console.log("getServers ERROR! " + error);
    })
    .finally(() => {
      $q.loading.hide();
    });
});

const requestConnection = (value, row) => {
  if (!row.link)
    setConnects(project.value.code, value, { id: row.id, type: row.type })
      .then((response) => {
        if (value) {
          if (response.data && response.data) {
            for (var i of response.data) {
              if (i.name == "ReqConnect") {
                row.req = i.data[0];
                break;
              }
            }
          }
        } else row.req = "";
      })
      .catch((error) => console.log(error));
  else
    deleteObjects(
      project.value.code,
      "LinkedUsers",
      `"id"="${row.link.id}"`
    ).then(() => (row.link = undefined));
};

const askToAllow = (value, row) => {
  if (!value)
    $q.dialog({
      title: i18n.t("dialog.alert"),
      message: i18n.t("users.disconnect_uers_promt"),
      cancel: true,
    })
      .onOk(() => {
        requestConnection(value, row);
      })
      .onCancel(() => {
        row.status = !value;
      });
  else requestConnection(value, row);
};

var createRow = (val, division, type, linked, connects) => {
  return {
    id: val.id,
    user: val.user,
    version: val.version,
    access: val.access,
    status: false,
    type,
    division,
    link: val.id in linked ? linked[val.id] : undefined,
    status: val.id in linked || val.id in connects,
    req: val.id in connects ? connects[val.id] : undefined,
  };
};

const setData = (value) => {
  var activity = [];
  var linked = {};
  var agentData = {};
  var managerData = {};
  var agentDivisionMap = {};
  var divisionMap = {};
  var agents = [];
  var managers = [];
  var connects = {};

  reqConn.value.forEach((el) => {
    if (!(el.id in connects) || connects[el.id].till < el.til)
      connects[el.id] = el;
  });

  value.data.forEach((el) => {
    if (el.name == "Division") {
      divisions.value = el.data;
      el.data.forEach((el2) => {
        divisionMap[el2.id] = el2;
        el2.agents.forEach((el3) => (agentDivisionMap[el3.id] = el2));
      });
    } else if (el.name == "DivisionManager") managers = el.data;
    else if (el.name == "Agents") agents = el.data;
    else if (el.name == "UserActivity") activity = el.data;
    else if (el.name == "LinkedUsers") {
      for (var i of el.data) linked[i.id] = i;
    }
  });

  agents.forEach((el) => (agentData[el.id] = { id: el.id, user: el }));

  activity.forEach((el) => {
    if (el.id in agentData) {
      agentData[el.id].version = el.version;
      agentData[el.id].access = el.date;
    }
  });

  agentRows.value = []
  Object.entries(agentData).forEach(([key, val]) => {
    agentRows.value.push(
      createRow(
        val,
        val.id in agentDivisionMap ? agentDivisionMap[val.id] : undefined,
        "Agents",
        linked,
        connects
      )
    );
  });

  managers.forEach(
    (el) => (managerData[el.id] = { id: el.id, user: el, divid: el.division })
  );

  activity.forEach((el) => {
    if (el.id in managerData) {
      managerData[el.id].version = el.version;
      managerData[el.id].access = el.date;
    }
  });

  managerRows.value = []
  Object.entries(managerData).forEach(([key, val]) => {
    managerRows.value.push(
      createRow(
        val,
        val.divid in divisionMap ? divisionMap[val.divid] : undefined,
        "DivisionManager",
        linked,
        connects
      )
    );
  });

  userType.value = i18n.t("users.user_types.agents");

  var divisionTree = {};

  if (divisions.value) {
    for (var d of divisions.value)
      if (d.parent == 0) divisionTree[d.id] = { division: d, children: [] };

    for (var d of divisions.value) {
      if (d.parent == 0) continue;

      var div = { division: d, children: [] };

      if (d.parent in divisionTree) divisionTree[d.parent].children.push(div);

      divisionTree[d.id] = div;
    }
  }

  const travelTree = (node) => {
    divlist.value.push(node.division);
    node.children.forEach((el) => travelTree(el));
  };

  travelTree(divisionTree[1]);

  rows.splice(0, rows.length);
  agentRows.value.forEach((el) => rows.push(el));
};
</script>

<style scoped lang="scss">
.form_field {
  background-color: $control-backgroud;
  padding-left: 5px;
  padding-right: 5px;
  margin-top: 10px;
  min-width: 100%;
  min-height: 56px;
}

.page-header {
  margin-bottom: 21px;
}

.row + .row {
  margin-top: 16px;
}

.q-table {
  padding: 0px;
  width: 100%;
}

.header-button {
  margin-right: 10px;
}
</style>

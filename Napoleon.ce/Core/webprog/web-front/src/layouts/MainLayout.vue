<template>
  <q-layout view="hHh Lpr lff">
    <q-header elevated>
      <q-toolbar class="toolbar">
        <q-btn flat @click="drawerClick" round dense icon="menu" />

        <q-avatar>
          <img src="/img/ic_launcher.png" />
        </q-avatar>
        <q-toolbar-title class="app-title cursor-pointer">{{
          $t("appTitle")
        }}</q-toolbar-title>

        <q-space />
        <lng-switch />
        <q-btn flat icon="img: /img/exit.svg" @click="exit" />
        <div style="width: 15px" />
      </q-toolbar>
    </q-header>

    <q-drawer
      v-model="drawer"
      show-if-above
      :width="256"
      :breakpoint="500"
      bordered
      :mini="miniState"
    >
      <q-list class="full-height" @mouseover="closeMenu">
        <template v-for="(menuItem, index) in menuList" :key="index">
          <q-item
            clickable
            :active="isItemActive(menuItem)"
            v-ripple
            @click="itemClick(menuItem)"
          >
            <q-item-section avatar>
              <q-icon :name="getIconName(menuItem.icon)" />
            </q-item-section>
            <q-item-section>
              {{ $t(`menuLabel.${menuItem.label}`) }}
            </q-item-section>
          </q-item>
        </template>

        <q-separator />
        <template v-for="(menuItem, index) in expandedMenuList" :key="index">
          <div>
            <q-expansion-item
              hide-expand-icon
              :model-value="expanded[menuItem.label]"
              :active="isItemActive(menuItem)"
              :label="$t(`menuLabel.${menuItem.label}`)"
              :icon="getIconName(menuItem.icon)"
              @update:model-value="
                (value) => (expanded[menuItem.label] = value)
              "
              @click="itemClick(menuItem)"
              @mouseover="onMouseEnter($event, menuItem.label, true)"
              :header-class="expandClass(menuItem)"
            >
              <q-list style="min-width: 100px">
                <template
                  v-for="(subMenu, index) in menuItem.children"
                  :key="index"
                >
                  <q-item
                    clickable
                    :active="isItemActive(subMenu)"
                    @click="itemClick(subMenu, menuItem)"
                  >
                    {{ $t(`menuLabel.${subMenu.label}`) }}
                  </q-item>
                </template>
              </q-list>
            </q-expansion-item>

            <q-menu
              anchor="top right"
              no-parent-event
              :ref="
                (el) => {
                  menus[menuItem.label] = el;
                }
              "
              @mouseover="onMouseEnter($event, menuItem.label, true)"
            >
              <q-list style="min-width: 100px">
                <template
                  v-for="(subMenu, index) in menuItem.children"
                  :key="index"
                >
                  <q-item
                    clickable
                    v-close-popup
                    :active="isItemActive(subMenu)"
                    @click="itemClick(subMenu, menuItem)"
                  >
                    {{ $t(`menuLabel.${subMenu.label}`) }}
                  </q-item>
                </template>
              </q-list>
            </q-menu>
          </div>
        </template>

        <q-btn
          v-if="!miniState"
          class="fixed-bottom-left ellipsis"
          flat
          no-caps
          padding="0px"
          :href="mailto()"
          style="margin-bottom: 10px; margin-left: 16px"
        >
          <u>{{ $t("login.supportEmail") }}</u>
        </q-btn>
      </q-list>
    </q-drawer>

    <q-page-container @mouseover="closeMenu">
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useQuasar } from "quasar";
import { logout } from "../backend/user";
import { mailto } from "../backend/helper";
import { useRouter } from "vue-router";

const menuList = [
  {
    icon: "profile",
    label: "profil",
    route: "Profile",
  },
  {
    icon: "projects",
    label: "projects",
    route: "Project",
  },
  {
    icon: "balance",
    label: "balance",
    route: "Balance",
  },
  {
    icon: "tarif",
    label: "tarifs",
    route: "Tarif",
  },
];

const expandedMenuList = [
  {
    icon: "organization",
    label: "administration",
    route: "Users",
    children: [
      {
        label: "users",
        route: "Users",
      },
      {
        label: "structure",
        route: "Division",
      },
    ],
  },
  // {
  //   icon: "command",
  //   label: "command",
  //   route: "Matrix",
  //   children: [
  //     {
  //       label: "matrix",
  //       route: "Matrix",
  //     },
  //     {
  //       label: "script",
  //       route: "Script",
  //     },
  //     {
  //       label: "question",
  //       route: "Question",
  //     },
  //     {
  //       label: "usersmanagement",
  //       route: "Management",
  //     },
  //   ],
  // },

  // {
  //   icon: "command",
  //   label: "manager",
  //   route: "Manager",
  //   children: [
  //     {
  //       label: "manager_main",
  //       route: "Manager",
  //     },
  //   ],
  // },
];

const $q = useQuasar();
const router = useRouter();
const selected = ref("");
const miniState = ref(false);
const drawer = ref(false);
const menus = ref([]);
const expanded = ref({});

selected.value = router.currentRoute.value.name;

const isItemActive = (item) => {
  return item.route == selected.value;
};
onMounted(() => {
  expandedMenuList.forEach((el) => (expanded.value[el.label] = ref(false)));
});

const expandClass = (item) => {
  for (let i of item.children)
    if (i.route == selected.value) return "selected-item";

  return "regular-item";
};

const closeMenu = (label) => {
  for (var k in menus.value) {
    if (!(label && k == label)) menus.value[k].hide();
  }
};

const onMouseEnter = (event, label, vivisble) => {
  event.stopPropagation();
  closeMenu(label);

  if (label in expanded.value && !expanded.value[label] && vivisble) {
    menus.value[label].show();
  } else menus.value[label].hide();
};

const exit = () => {
  logout().finally(() => router.push({ name: "Login" }));
};

const getIconName = (icon) => {
  return `img: /img/${icon}.svg`;
};

const itemClick = (item, parent) => {
  closeMenu();

  if (item.label in expanded.value && !expanded.value[item.label])
    menus.value[item.label].show();

  selected.value = item.route;

  router.push({ name: item.route });
};

const drawerClick = () => {
  if ($q.screen.width <= 500) drawer.value = !drawer.value;
  else miniState.value = !miniState.value;
};
</script>

<style scoped></style>

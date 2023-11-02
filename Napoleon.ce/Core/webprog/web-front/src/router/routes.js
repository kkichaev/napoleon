const routes = [
  {
    path: "/",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      {
        path: "",
        name: "Profile",
        component: () => import("pages/Profile.vue"),
      },
      {
        path: "/project",
        name: "Project",
        component: () => import("pages/Project.vue"),
      },
      {
        path: "/balance",
        name: "Balance",
        component: () => import("pages/Balance.vue"),
      },
      {
        path: "/tarif",
        name: "Tarif",
        component: () => import("pages/Tarif.vue"),
      },
      {
        path: "/division",
        name: "Division",
        component: () => import("pages/Division.vue"),
      },
      {
        path: "/users",
        name: "Users",
        component: () => import("pages/Users.vue"),
      },
      {
        path: "/matrix",
        name: "Matrix",
        component: () => import("pages/Matrix.vue"),
      },
      {
        path: "/manager",
        name: "Manager",
        component: () => import("pages/ManagerMain.vue"),
      },
      {
        path: "/script",
        name: "Script",
        component: () => import("pages/Script.vue"),
      },
    ],
  },
  {
    path: "/",
    component: () => import("src/layouts/Welcome.vue"),
    children: [
      {
        path: "/login",
        name: "Login",
        component: () => import("pages/Login.vue"),
      },
      {
        path: "/register",
        name: "Register",
        component: () => import("pages/Register.vue"),
      },
      {
        path: "/restore",
        name: "RestorePassword",
        component: () => import("pages/RestorePassword.vue"),
      },
      {
        path: "/confirm",
        name: "Confirm",
        component: () => import("pages/Confirm.vue"),
      },
      {
        path: "/prjreg",
        name: "ProjectRegistration",
        component: () => import("pages/PrjRegister.vue"),
      },
      {
        path: "/error",
        name: "Error",
        component: () => import("pages/Error.vue"),
      },
    ],
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: "/:catchAll(.*)*",
    component: () => import("pages/ErrorNotFound.vue"),
  },
];

export default routes;

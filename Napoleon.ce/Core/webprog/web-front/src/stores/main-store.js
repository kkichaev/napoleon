import { defineStore } from 'pinia'
import useApi from 'boot/axios'


export const useMainStore = defineStore('mainStore', {
  state: () => ({
    forgotPassword: false,
    passwordRestore: false,
    user: undefined,
  }),

  // getters: {
  //   doubleCount (state) {
  //     return state.counter * 2
  //   }
  // },

  actions: {
    restorePassword () {
      console.log("restorePassword")
      this.router.push({name: 'RestorePassword'})
    },
  }
})

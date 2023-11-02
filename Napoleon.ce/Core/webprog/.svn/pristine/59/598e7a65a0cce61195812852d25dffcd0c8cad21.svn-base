import { defineStore } from 'pinia'
import { registerUser, loginUser, sendRegToken } from "src/backend/backend";

export const useBackendStore = defineStore('backend', {
  state: () => ({
    errorCode: 0,
    errorMessage: '',
    loading: false,
    registrationStatus : false,
    serverCode: ''
  }),

  actions: {
    registerUser(data){
      this.loading = true
      console.log('registerUser data: ', data)
      registerUser(data).then((response) => {
        console.log('registerUser responce: ', response)
        this.errorCode = 0
        this.errorMessage = ''
        this.registrationStatus = true
      })
      .catch((error) => {
        console.log('registerUser error: ', error)
        // if(error.response.data.data == undefined){
        //   this.errorMessage = error.response.statusText
        //   this.errorCode = error.response.status
        // }else{
        //   this.errorMessage = error.response.data.data.message
        //   this.errorCode = error.response.data.data.response
        // }
      }).finally(()=>this.loading = false);
    },
    clearError(){
      this.errorCode = 0
      this.errorMessage = ''
    },

    sendRegToken(data){
      sendRegToken(data).then((responce)=>{
        console.log("sendRegToken success: ", responce)

        if (responce.status != '200' || responce.data == undefined)
          this.router.push({name: 'Error'})

        responce.data.every(d => {
          if (d.name == 'ServerCode'){
            this.serverCode = d.data[0].code
            return false
          }
          return true
        });

        if (this.serverCode)
          this.router.push({name: 'ProjectRegistration'})
        else
          this.router.push({name: 'Error'})
      })
      .catch((error)=>{
        this.router.push({name: 'Error'})
      })
    }
  }
})

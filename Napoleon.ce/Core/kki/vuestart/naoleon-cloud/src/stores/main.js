import { defineStore } from 'pinia'
import { getAuth, createUserWithEmailAndPassword, signInWithEmailAndPassword, signOut } from "firebase/auth";
import { getDatabase, ref } from "firebase/database";
import { onValue } from "firebase/database";
import { set } from "firebase/database";
import router from '../router'

export const mainStore = defineStore('alerts', {
  id: 'mainStore',
  state: () => ({ 
    count: 2, 
    name: 'Eduardo',
    errorMessage: "",
    eerorCode: 0,
    user: null,
    agentsTable: []
  }),
  getters: {
    doubleCount: (state) => state.count * 2,
    agents(){
      const ar = ref(getDatabase(), 'agents/')
      onValue(ar, (s)=>{
         this.agentsTable = s.val()
      })
    }
  },
  actions: {
    increment() {
      this.count++
    },
    click(){
      console.log('into store click')
      this.count++
    },
    register(user){
      //"kkichaev@yandex.ru", "mysecretpassword!"
      console.log(user.email, user.password)
      createUserWithEmailAndPassword(getAuth(), user.email, user.password)
        .then(() => { 
          this.clearError()
          router.replace('/')
        })
        .catch((error)=>{
          console.log(error)
          this.errorCode = error.code;
          this.errorMessage = error.message;
        })
    },
    login(user){
      signInWithEmailAndPassword(getAuth(), user.email, user.password)
        .then((userCredential) => {
          this.clearError()
          this.user = userCredential.user;
          router.replace('/')
        })
        .catch((error) => {
          this.errorCode = error.code;
          this.errorMessage = error.message;
          console.log(this.errorMessage)
        });
    },
    clearError(){
      this.errorCode = 0;
      this.errorMessage = "";
    },
    logout(){
      console.log('Store logout')
      this.user=null
      signOut(getAuth())
        .then(()=>{})
        .catch(()=>{})
    },
    writeDivision(){
      const db = getDatabase();
      set(ref(db, 'napoleon/'), this.agentsTable);
    },
    loadAgents(){
      const ar = ref(getDatabase(), 'napoleon/')
      onValue(ar, (s)=>{
        this.agentsTable = s.val()
      })
    }

  },
})
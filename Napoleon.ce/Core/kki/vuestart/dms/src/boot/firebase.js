import { boot } from 'quasar/wrappers'
import { initializeApp } from 'firebase/app'

// "async" is optional;
// more info on params: https://v2.quasar.dev/quasar-cli/boot-files
export default boot(async (/* { app, router, ... } */) => {
  const firebaseConfig = {
    apiKey: 'AIzaSyA_PrYcJNrkGsk3-QuzQ8p8XSO8uVa2unU',
    authDomain: 'nadd-d1117.firebaseapp.com',
    projectId: 'nadd-d1117',
    storageBucket: 'nadd-d1117.appspot.com',
    messagingSenderId: '487250785974',
    appId: '1:487250785974:web:ca5950b43bbcd79e0e1817',
    measurementId: 'G-75M6LKCXCC',
    databaseURL: 'https://nadd-d1117-default-rtdb.firebaseio.com/'
  }

  initializeApp(firebaseConfig)
})

import { defineStore } from 'pinia'
import { getAuth, signInWithEmailAndPassword } from 'firebase/auth'

export const useMainStore = defineStore('main', {
  state: () => ({
    user: null,
    counter: 0
  }),
  actions: {
    login (user) {
      console.log(user.login, ' / ', user.pwd)
      this.counter++
      console.log('login(): ', this.counter)
      const auth = getAuth()
      signInWithEmailAndPassword(auth, user.login, user.pwd)
        .then((userCredential) => {
          this.user = userCredential.user
          this.router.push('/')
        })
        .catch((error) => {
          // const errorCode = error.code
          // const errorMessage = error.message
          console.log('error:', error)
        })
    },
    isLogin () {
      console.log('isLogin(): ', this.counter)
      this.counter++
      const res = this.user != null
      console.log('isLogin', res)

      return res
    }
  }
})

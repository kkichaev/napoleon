import { defineStore } from 'pinia'
import { getAuth, signInWithEmailAndPassword, signOut } from 'firebase/auth'
import { getDatabase, ref, set, onValue, update, remove } from 'firebase/database'

export const useMainStore = defineStore('mainStore', {

  state: () => ({
    suser: null,
    errorCode: null,
    errorMessage: null
  }),

  getters: {
    doubleCount (state) {
      return state.counter * 2
    }
  },

  actions: {
    increment () {
      this.counter++
    },
    login (user) {
      console.log('login', user)
      const auth = getAuth()
      signInWithEmailAndPassword(auth, user.login, user.pwd)
        .then((userCredential) => {
          this.user = userCredential.user
          this.router.push({ name: 'Index' })
        })
        .catch((error) => {
          this.errorCode = error.code
          this.errorMessage = error.message
        })
    },
    toLogin () {
      this.router.push({ name: 'Login' })
    },
    toIndex () {
      this.router.push({ name: 'Index' })
    },
    logout () {
      signOut(getAuth()).then(() => {
        this.toLogin()
      })
    },
    addFolder (folder) {
      if (this.user) {
        const db = getDatabase()

        const data = {
          id: crypto.randomUUID(),
          name: folder,
          created: new Date().toJSON()
        }

        set(ref(db, this.getFoldersPath(this.user.uid) + data.id), data)
          .then(() => {
          })
          .catch((error) => console.error('addFolder error: ', error))
      }
    },
    getFoldersPath (id) {
      return 'dms/' + id + '/folders/'
    },
    openFolder (id) {
      console.log('openFolder mainStore')
      this.router.push({ name: 'Folder', params: { id } })
    },
    getCurrentRouteParams (name) {
      return this.router.currentRoute.value.params[name]
    },
    addNote (folder, note) {
      if (this.user) {
        console.log('addNOte: ', note)
        const updates = {}
        const db = getDatabase()

        if (folder.notes === undefined) { folder.notes = [] }
        folder.notes.push(note)

        updates[this.getFoldersPath(this.user.uid) + folder.id] = folder
        update(ref(db), updates)
      }
    },
    deleteFolder (idx) {
      const db = getDatabase()
      const path = this.getFoldersPath(this.user.uid) + this.folders[idx].id
      console.log('delete folder: ', idx, ' : ', path)
      const refs = ref(db, path)
      remove(refs).then(() => console.log('removed')).catch((err) => console.log('remove error: ', err))
    }
  }
})

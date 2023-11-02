import { ref, onMounted, onUnmounted, watch, watchEffect } from 'vue'
import { getDatabase, ref as fref, set, onValue, update, remove } from 'firebase/database'
import { useMainStore } from 'src/stores/mainStore'

const useFirebaseStorage = (key, defaultValue) => {
  const value = ref(defaultValue)

  const db = getDatabase()
  const folders = fref(db, 'dms/' + useMainStore().user.uid + '/folders/')
  console.log('useFirebaseStorage', key, ' : ', defaultValue)
  const unwatch = watch([value], () => {}, { deep: true })

  const read = () => {
    console.log('  useFirebaseStorage.read')
    unwatch()

    onValue(folders, (snapshot) => {
      console.log(' onValue(): ', value.value)
      if (snapshot.val() != null) { value.value = snapshot.val() } else { value.value = [] }
      watch([value], write, { deep: true })
    })
  }

  const EVENT_LISTENER_KEY = 'useFirebaseStorageRead'

  onMounted(() => {
    window.addEventListener(EVENT_LISTENER_KEY, read)
  })

  onUnmounted(() => {
    window.removeEventListener(EVENT_LISTENER_KEY, read)
  })

  const write = (newValue, oldValue) => {
    console.log('  useFirebaseStorage.write()')

    if (useMainStore().user) {
      const db = getDatabase()

      set(folders, value.value)
        .catch((error) => console.error('addFolder error: ', error))
    }
  }

  read()

  return value
}

export const useFirebase = () => {
  return useFirebaseStorage('dms', [])
}

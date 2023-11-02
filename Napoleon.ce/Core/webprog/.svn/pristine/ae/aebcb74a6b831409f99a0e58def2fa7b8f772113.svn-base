import { onMounted } from 'vue'
import { useBackendStore } from 'src/stores/backend'
import { useRouter } from 'vue-router'
import { isAuth } from '../backend/backend'

export function useGuard (){
  const router = useRouter()
  const store = useBackendStore()

  onMounted( async function (){
    if (!store.isAuth)
      if ( store.isAuth === undefined){
        try{
        await isAuth()
          .then((responce)=>{
            store.isAuth = true
            router.push({ name: router.name })
          })
          .catch((error)=>{toLogin()})
        }catch{
          toLogin()
        }

      }else
        router.push(toLogin())
  })

  const toLogin = ()=>{
    const LOGIN = "Login"
    if (router.currentRoute.value.name != LOGIN)
      router.push({ name: 'Login' })
  }
}

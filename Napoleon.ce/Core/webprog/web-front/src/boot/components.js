import { boot } from 'quasar/wrappers'
import LanguageSwitcher from '../components/LanguageSwitcher.vue'

export default boot(async ({ app} ) => {
  app.component('lng-switch', LanguageSwitcher)
})

import { boot } from 'quasar/wrappers'
import { Quasar } from 'quasar'

const langList = import.meta.glob('../../node_modules/quasar/lang/*.mjs')

// "async" is optional;
// more info on params: https://v2.quasar.dev/quasar-cli/boot-files
export default boot(async (/* { app, router, ... } */) => {
  const langIso = fixLocale() // ... some logic to determine it (use Cookies Plugin?)

  try {
    langList[ `../../node_modules/quasar/lang/${ langIso }.mjs` ]().then(lang => {
      Quasar.lang.set(lang.default)
    })
  }
  catch (err) {
    // Requested Quasar Language Pack does not exist,
    // let's not break the app, so catching error
  }
})

function fixLocale(){
  var res = navigator.language;
  if (res == 'ru-RU')
    res = 'ru'

  return res
}


import { boot } from "quasar/wrappers";
import { createI18n } from "vue-i18n";
import messages from "src/i18n";
import { LocalStorage} from "quasar";

export default boot(({ app }) => {
  const i18n = createI18n({
    locale: fixLocale(),
    //    locale: "en-US",
    globalInjection: true,
    messages,
  });

  // Set i18n instance on app
  app.use(i18n);
});

function fixLocale(){
  var res = navigator.language;
  const LOCALE = 'locale'

  res = LocalStorage.getItem(LOCALE)

  if (res == 'ru-RU')
    res = 'ru'

  return res
}

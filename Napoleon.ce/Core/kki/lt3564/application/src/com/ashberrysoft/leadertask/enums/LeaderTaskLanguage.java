package com.ashberrysoft.leadertask.enums;

import java.util.Locale;

public enum LeaderTaskLanguage {

    // DEFAULT(""),
    RU("ru"), EN("en"), BE("be"), CS("cs"), DE("de"), EL("el"), ES("es"), FA("fa"), FR("fr"), HU("hu"), IT("it"), KK("kk"), NL(
            "nl"), PL("pl"), PT("pt"), RO("ro"), SK("sk"), TR("tr"), UK("uk");

    final Locale mLocale;

    LeaderTaskLanguage(String l) {
        mLocale = new Locale(l);
    }

    public Locale getLocale() {
        return mLocale;
    }
}
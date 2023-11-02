package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.VisitDocEx;

public class NapoleonAppEx extends NapoleonApp{
    @Override
    public void onCreate() {
        VisitDocEx.initialize();
        super.onCreate();
    }
}

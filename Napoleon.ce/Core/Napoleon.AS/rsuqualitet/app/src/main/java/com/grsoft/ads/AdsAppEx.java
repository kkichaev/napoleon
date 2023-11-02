package com.grsoft.ads;

public class AdsAppEx extends AdsApp {
    @Override
    public void onCreate() {
        AdsService.MAIN_SERVICE = AdsServiceEx.class;
        super.onCreate();
    }
}

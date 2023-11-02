package com.grsoft.napoleon;

import com.grsoft.util.RuntimeEnv;
import com.grsoft.util.SettingActivity;

public class SettingEx extends Setting {

    @Override
    protected void updatesTabs(boolean isAdmin) {
        tabsActivities.clear();

        if( isAdmin || canCreateForUser(NetworkSettingActivity) )
            createTabSpec(NetworkSettingActivity);

//        if (RuntimeEnv.isPhotoSupported() && (isAdmin || canCreateForUser(PhotoSettingActivity)))
//            createTabSpec(PhotoSettingActivity);

        if( isAdmin || canCreateForUser(BehaviorSettingActivity) )
            createTabSpec(BehaviorSettingActivity);

//        if( isAdmin || canCreateForUser(GPSSettingActivity) )
//            createTabSpec(GPSSettingActivity);
//
//        if( isAdmin || canCreateForUser(WarehouseSettingActivity) )
//            createTabSpec(WarehouseSettingActivity);

        for(Class<? extends SettingActivity> ca : addTabs)
            if( isAdmin || canCreateForUser(ca) )
                createTabSpec(ca);

        if( openTag != null )
            getTabHost().setCurrentTabByTag(openTag);
    }
}

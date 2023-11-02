package com.grsoft.napoleon;

import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadServiceBase;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.MenuActionHandler;
import com.grsoft.util.MenuHandler;

import java.util.ArrayList;
import java.util.Calendar;

public class MainEx extends Main {
    @Override
    protected ArrayList<MenuHandler> createMainMenuList() {
        ArrayList<MenuHandler> res = super.createMainMenuList();
        res.add(new MenuActionHandler(
                getString(R.string.plans)
//                ,() -> testConnection()
                ,() -> PlanView.open(this)
                ,R.drawable.ic_plans
        ));
        return res;
    }
}

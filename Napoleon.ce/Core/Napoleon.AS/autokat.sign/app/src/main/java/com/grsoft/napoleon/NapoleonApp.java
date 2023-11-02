/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;

public class NapoleonApp extends NapoleonAppBase {
    public boolean need_sync = true;

    @Override
    public void onCreate() {
        ConfigManager.initConfig(new CfgNplEx());

        super.onCreate();

        setProgrammVersion();

        //NapoleonChat.init(this);
    }

    @Override
    protected void defineNewType() {
        super.defineNewType();

        ServerCommand.Category = "vanpda";
    }

    @Override
    protected void initDocTypes() {
        initFeatures();
    }

    private void setProgrammVersion() {
        try {
            ServerCommand.ProgramVersion = getResources().getString(R.string.version);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

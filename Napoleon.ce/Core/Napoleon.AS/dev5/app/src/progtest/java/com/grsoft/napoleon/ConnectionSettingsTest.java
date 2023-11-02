package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.JSONAnswerParser;
import com.grsoft.dataobjects.LinkedUser;
import com.grsoft.dataobjects.ServerAnswer;
import com.grsoft.napmobile.R;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.SettingActivity;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ConnectionSettingsTest extends SettingActivity {
    protected int getLayoutID() { return  R.layout.conn_settings_test; }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        init();
    }

    protected void init() {
        CfgNpl c = (CfgNpl) ConfigManager.getConfig();

        ((EditText)findViewById(R.id.server_code)).setText(c.serverCode);
        ((EditText)findViewById(R.id.agent_code)).setText(c.uuid);

        findViewById(R.id.sync).setOnClickListener(v -> {
            c.uuid = ((EditText)findViewById(R.id.agent_code)).getText().toString();
            c.serverCode = ((EditText)findViewById(R.id.server_code)).getText().toString();

            ConfigManager.save();

            UpdateDB.openSync(ConnectionSettingsTest.this);
            finish();
        });
    }

    @Override
    public void save() {
    }

    @Override
    public void update() {
        init();
    }

    @Override
    public int getName() {
        return R.string.network;
    }

    @Override
    public int getIcon() {
        return R.drawable.setting_network;
    }

    @Override
    public boolean isAdminSettings() {
        return true;
    }
}

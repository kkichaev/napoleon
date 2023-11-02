package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.JSONAnswerParser;
import com.grsoft.dataobjects.LinkedUser;
import com.grsoft.dataobjects.ServerAnswer;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.SettingActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ConnectionSettings extends SettingActivity {
    protected int getLayoutID() { return  R.layout.conn_settings; }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        init();
    }

    protected void init()
    {
        EditText code = findViewById(R.id.code);
        code.setSelectAllOnFocus(true);

        findViewById(R.id.connect).setOnClickListener(v -> {
            doConnect();
        });

        findViewById(R.id.sync).setOnClickListener(v -> {
            UpdateDB.openSync(ConnectionSettings.this);
            finish();
        });
    }

    void requestLink(String code) {
        String url = String.format("%s/api/link_user?code=%s&type=Agents", Config.HOST_URL, code);
        try {
            URL addr = new URL(url.toString());
            HttpURLConnection conn = (HttpURLConnection) addr.openConnection();

            JSONAnswerParser jp = JSONAnswerParser.read(conn);
            List<ServerAnswer> l1 = jp.read("ServerAnswer", ServerAnswer.class);
            List<LinkedUser> l2 = jp.read("LinkedUsers", LinkedUser.class);

            String error = null;
            if(jp.haveError()) {
                error = jp.getError();
            } else {
                if(l2.size() > 0) {
                    LinkedUser lu = l2.get(0);
                    CfgNplW cfg = (CfgNplW) ConfigManager.getConfig();

                    cfg.serverCode = lu.server_code;
                    cfg.userid = lu.id;
                    cfg.uuid = lu.code;

                    ConfigManager.save();

                    runOnUiThread(() -> {
                        TextView tv = findViewById(R.id.connect_text);
                        tv.setText(R.string.connect_compleete);

                        findViewById(R.id.connect).setVisibility(View.GONE);
                        findViewById(R.id.sync).setVisibility(View.VISIBLE);
                    });
                } else {
                    if(l1.size() > 0) {
                        error = l1.get(l1.size()-1).message;
                    }
                    if(error == null || error.length() == 0) {
                        error = getString(R.string.connection_error);
                    }
                }

                if(error != null) {
                    final String err = error;
                    runOnUiThread(() -> {
                        Toast.makeText(ConnectionSettings.this, err, Toast.LENGTH_LONG).show();
                    });
                }
            }
        } catch(Exception e) {
            e.printStackTrace();

            runOnUiThread(() -> {
                String msg = e.getMessage();
                Toast.makeText(ConnectionSettings.this, msg, Toast.LENGTH_LONG).show();
            });
        }
    }

    void doConnect() {
        EditText edcode = findViewById(R.id.code);
        String code = edcode.getText().toString();

        if(code.length() == 0) {
            return;
        }

        Thread t = new Thread(() -> requestLink(code));
        t.start();
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

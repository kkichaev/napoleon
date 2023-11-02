package com.grsoft.napoleon;

import android.content.Context;
import android.util.Log;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.RouteDeviationSender;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.RouteDeviation;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.WriteServiceBase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlertSender {
    String lastId = null;
    Date lastTime = null;

    DbWriter w = new DbWriter();
    Object monitor = new Object();
    boolean canRun = true;
    Context context;

    Thread sender;
    public AlertSender(Context context) {
        this.context = context;

        sender = new Thread(this::sendProc);
        sender.start();
    }

    public void close() {
        w.close();
        canRun = false;

        synchronized (monitor) {
            monitor.notify();
        }
    }

    public void add(Org o, int type) {
        Date now = new Date();

//        if(lastId != null && lastId.equals(o.id)) {
//            CfgNpl c = (CfgNpl) ConfigManager.getConfig();
//            long nowV = now.getTime();
//            if(nowV - lastTime.getTime() < c.gps_valid_in_org) {
//                return;
//            }
//        }

        lastId = o.id;
        lastTime = now;

        RouteDeviation rd = new RouteDeviation();
        rd.id = o.id;
        rd.orgName = o.name;
        rd.date = now;
        rd.type = type;

        w.insertRecord(rd);

        Log.d("NapoleonApp", "Add alert " + Integer.toString(type) + " " + o.id + " " + now.toString());

        if(!canRun) {
            return;
        }
        synchronized (monitor) {
            monitor.notify();
        }
    }

    void sendProc() {
        synchronized (monitor) {
            List<RouteDeviation> data = DbReader.fetch(RouteDeviation.class, "exported=0");
            if(data.size() > 0) {
                sendData(data, 2);
            }

            while(canRun) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!canRun) {
                    return;
                }

                data = DbReader.fetch(RouteDeviation.class, "exported=0");
                if(data.size() > 0) {
                    sendData(data, 40);
                }
            }
        }
    }

    private void sendData(List<RouteDeviation> data, int tryCount) {
        Config config = ConfigManager.getConfig();
        if(config.login.length() > 0) {
            long sleepDelay = 10 * 1000;
            LoginData ld = new LoginData(config.login, config.passw, "", context, "", "");

            RouteDeviationSender rds = new RouteDeviationSender(data);
            List<ObjectExportListener> snd = new ArrayList<>();
            snd.add(rds);

            WriteServiceBase wr = RWServiceFactory.instance.createWriteService(snd);
            while(tryCount-- > 0) {
                try {
                    if(wr.write(context, ld)) {
                        break;
                    }
                    Thread.sleep(sleepDelay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(sleepDelay < 10 * 60 * 1000) {
                    sleepDelay *= 2;
                }
            }
        }
    }
}

package com.grsoft.network;

import android.content.Context;
import android.view.View;

import com.grsoft.database.Hitching;
import com.grsoft.database.ReportHitching;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.view.TimerMessageBox;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ExecServerModule extends NetworkAsyncTask {
    private WeakReference<View> wcontrol;
    protected WeakReference<Context> wcontext;
    ReportHitching report;
    boolean result = false;

    Events handler;

    public interface Events {
        void onComplete(ExecServerModule sender, boolean result);
    }


    public ExecServerModule(Context context, View control, ReportHitching report, Events handler) {
        super(new SendProgressManager(context, control));
        this.wcontrol = new WeakReference<>(control);
        this.wcontext = new WeakReference<>(context);
        this.report = report;
        this.handler = handler;
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        Context context = wcontext.get();

        onUpdate(UpdateProcessInfo.UpdateStatus.START_OF_PROCESS, 0);

        int traffic = 0;

        try	{
            View control = wcontrol.get();

            Config config = ConfigManager.getConfig();
            UserInfo userInfo = new LoginData(config.login, config.passw, config.impersonate, context);

            List<Hitching> rcvHitch = new ArrayList<Hitching>();
            rcvHitch.add(report);

            ReadServiceBase reader =  RWServiceFactory.instance.createReadService(rcvHitch);
            reader.setUpdateProcessListenet(this);
            if( !reader.update(context, userInfo, false) ){
                onUpdate(UpdateProcessInfo.UpdateStatus.END_OF_PROCESS, 0);
                showErrorMsg(reader.getMessage(), context);

                return false;
            }else{
                traffic += reader.getReceivedBytes();
            }

            result = true;
            onUpdate(UpdateProcessInfo.UpdateStatus.END_OF_PROCESS, 0);
            onUpdateMessage(new TimerMessageBox(
                    context.getString(R.string.result), context.getString(R.string.sync_end_traffic)
                    + Integer.toString((traffic + 512) / 1024) + " " + context.getString(R.string.kB)
                    , context));

        } catch(Exception exception){
            onUpdate(UpdateProcessInfo.UpdateStatus.END_OF_PROCESS, 0);
            showErrorMsg(exception.getMessage(), context);
            exception.printStackTrace();

            return false;
        }

        return true;
    }

    @Override
    protected void onPreExecute() {
        View control = wcontrol.get();
        if (control != null)
            control.setEnabled(false);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        View control = wcontrol.get();
        if (control != null)
            control.setEnabled(true);
        if(handler != null) {
            handler.onComplete(this, result);
        }
    }
}


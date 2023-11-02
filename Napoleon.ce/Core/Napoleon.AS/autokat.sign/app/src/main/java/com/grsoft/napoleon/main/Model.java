package com.grsoft.napoleon.main;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Pair;
import android.widget.Toast;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.grsoft.database.DocsToSignReceiver;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.SignDocResponse;
import com.grsoft.dataobjects.SyncInfo;
import com.grsoft.dataobjects.impl.SyncInfoImpl;
import com.grsoft.napoleon.BuildConfig;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.UpdateProcess;
import com.grsoft.network.UpdateProcessInfo;
import com.grsoft.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Model extends ViewModel {

    static public boolean TESTING = BuildConfig.DEBUG;

    public interface RefreshEvent {
        void refreshing(RefreshData data);
    }

    RefreshEvent refreshHandler;
    public void setRefreshHandler(RefreshEvent re) { this.refreshHandler = re; }

//    MutableLiveData<RefreshData> refreshing = new MutableLiveData<>(new RefreshData());
//    public LiveData<RefreshData> getRefreshing() { return refreshing; }
//    public void clearRefreshing() {
//        if(refreshHandler != null) {
//            refreshHandler.refreshing(new RefreshData());
//        }
////        refreshing.postValue(new RefreshData());
//    }

    public SignDocResponse signDocResponse = null;

    public Model(SavedStateHandle savedStateHandle){
    }

    public static class ExportAsync extends  AsyncTask<Void, Void, String>{
        public Context context;

        protected String doInBackground(Void... voids) {
            try {
                File src = new File(Path.getDataBasePath());
                File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), Path.SHARED_FOLDER);

                if (!folder.exists())
                    folder.mkdirs();

                File dist = new File(folder, Path.BASE_NAME);
                Util.copy(src,dist);

                return dist.getAbsolutePath();
            }catch (Exception e){
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String res) {
            if (res.length() == 0)
                res = context.getString(R.string.export_error);
            else
                res = context.getString(R.string.export_done) + " " + res;

            Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
        }
    }

    public void exportBase(Context context) {
       ExportAsync export = new ExportAsync();
       export.context = context;
       export.execute();
    }


    public void refresh(Context context, CfgNpl config) {
        UpdateProcess.Params params = new UpdateProcess.Params();
        params.setFrom(config);

        syncInfo();
        fillSyncParams(params, context);

        UpdateProcess updateProcess = new Updater(context, this);
        updateProcess.execute(params);
    }

    public void syncInfo(){
        SyncInfoImpl syncInfoImpl = new SyncInfoImpl();
        SyncInfo syncInfo = syncInfoImpl.getData();
        syncInfo.created = Util.getDateTime();

        Config cfg = ConfigManager.getConfig();
        syncInfo.login = cfg.login;
        syncInfo.password = cfg.passw;
        syncInfo.ip1 = cfg.address;
        syncInfo.ip2 = cfg.address2;
        syncInfo.port1 = cfg.port;
        syncInfo.port2 = cfg.port2;
        syncInfo.deviceID = ServerCommand.DeviceID;
        syncInfo.params = 0;

        syncInfoImpl.write();
        syncInfoImpl.close();
    }

    void fillSyncParams(UpdateProcess.Params params, Context context){
//        params.outdata.add(new ScheduleSender());
//        params.outdata.add(new GPSHitching());
//
        params.indata.addAll(requestHitchings(context));
    }


    static class Updater extends UpdateProcess {
        Model model;

        public Updater(Context context, Model model) {
            super(context, true);
            this.model = model;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(model.refreshHandler != null) {
                model.refreshHandler.refreshing(new RefreshData(true));
            }
//            model.refreshing.postValue(new RefreshData(true));
        }

//        @Override
//        public void onUpdate(UpdateProcessInfo.UpdateStatus status, int progress) {
//            Pair<String, Integer> val = new Pair<>(status.name(), progress);
//            model.syncProgress.postValue(val);
//        }

        @Override
        protected void broadcastError(String msg) {
            if(model.refreshHandler != null) {
                model.refreshHandler.refreshing(new RefreshData(msg));
            }
//            model.refreshing.postValue(new RefreshData(msg));
        }

        @Override
        protected void broadcastResult(int traffic) {
            if(model.refreshHandler != null) {
                model.refreshHandler.refreshing(new RefreshData(traffic));
            }
//            model.refreshing.postValue(new RefreshData(traffic));
        }
    }

    List<Hitching> requestHitchings(Context context) {
        List<Hitching> ret = new ArrayList<>();
        ret.add(new DocsToSignReceiver(context));
        ret.add(new Hitching(com.grsoft.dataobjects.Config.class, "ServerConfig"));

        return ret;
    }
}

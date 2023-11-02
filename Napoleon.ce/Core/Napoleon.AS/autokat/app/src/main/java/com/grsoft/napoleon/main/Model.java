package com.grsoft.napoleon.main;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.savedstate.SavedStateRegistry;

import com.grsoft.ScheduleSender;
import com.grsoft.database.DbWriter;
import com.grsoft.database.DocumentRestore;
import com.grsoft.database.GPSHitching;
import com.grsoft.database.Hitching;
import com.grsoft.database.MessageHitchingNew;
import com.grsoft.database.OrgHitching;
import com.grsoft.database.OrgSender;
import com.grsoft.database.PicStoreHitching;
import com.grsoft.database.PicStoreHitchingEx;
import com.grsoft.database.PriceHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.BNOper;
import com.grsoft.dataobjects.ClientType;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.FormatTT;
import com.grsoft.dataobjects.LogHitching;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PassportPhotos;
import com.grsoft.dataobjects.PayType;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PurchaseTemplate;
import com.grsoft.dataobjects.Question;
import com.grsoft.dataobjects.ScriptDefEx;
import com.grsoft.dataobjects.ScriptEx;
import com.grsoft.dataobjects.SyncInfo;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.dataobjects.impl.SyncInfoImpl;
import com.grsoft.napoleon.BuildConfig;
import com.grsoft.napoleon.PriceHolder;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.PurchaseDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.ScriptPropDoc;
import com.grsoft.napoleon.documents.SellingDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.DocExportListener;
import com.grsoft.network.RawObject;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.UpdateProcess;
import com.grsoft.network.UpdateProcessInfo;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class Model extends ViewModel {

    static public boolean TESTING = BuildConfig.DEBUG;
    private final static String CURRENT_ORG = "current_org";

    public String searchText = "";

    ScriptImplEx currentScript;
    CreatableDocument currentDoc;

    MutableLiveData<Date> workingDate = new MutableLiveData<>(new Date());
    MutableLiveData<Boolean> routeMode = new MutableLiveData<>(false);

    MutableLiveData<RefreshData> refreshing = new MutableLiveData<>(new RefreshData());
    MutableLiveData<Pair<String, Integer>> syncProgress = new MutableLiveData<>();
    MutableLiveData<OrgEx> currentOrg = new MutableLiveData<>();
    MutableLiveData<Boolean> checkGPS = new MutableLiveData<>(new Boolean(false));

    public LiveData<Date> getWorkingDate() { return workingDate; }
    public LiveData<Boolean> getRouteMode() { return routeMode; }
    public LiveData<RefreshData> getRefreshing() { return refreshing; }
    public LiveData<Pair<String, Integer>> getSyncProgress() { return syncProgress; }

    public void setWorkingDate(Date newDate) { workingDate.postValue(newDate);}
    public void setRouteMode(Boolean mode) { routeMode.postValue(mode);}

    public void clearRefrheshing() {
        refreshing.postValue(new RefreshData());
    }

    public void setCurrentOrg(OrgEx org){
        currentOrg.postValue(org);
    }

    public LiveData<OrgEx> getCurrentOrg() {return currentOrg;}

    public ScriptImplEx createScriptDoc(Context c, OrgEx o) {
        ScriptDefEx sd = ScriptDefEx.getActive();
        if(sd != null) {
            currentScript = new ScriptImplEx();
            currentScript.initData(c, o.id, GPSUtilNew.getLastKnownLocation(), sd);
            setCurrentOrg(o);
        } else {
            currentScript = null;
        }
        return currentScript;
    }

    public ScriptImplEx getCurrentScript() { return currentScript; }

    public Model(SavedStateHandle savedStateHandle){
        Bundle bundle = savedStateHandle.get(ScriptImplEx.class.toString());
        Log.d("MainModel", "model constructor");

        if (bundle != null) {
            currentScript = new ScriptImplEx();
            currentScript.read(bundle.getLong(ExtrasConst.DOC_ROW_ID_STR));
            currentScript.close();

            OrgImpl org = new OrgImpl();
            org.read("id", currentScript.getId());
            currentOrg.setValue((OrgEx) org.getData());

            Log.d("MainModel", "model init doc: " + this.currentScript + " this: " + this);
        }

        savedStateHandle.setSavedStateProvider(ScriptImplEx.class.toString(), new DocSavedStateProvider());
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

    private class DocSavedStateProvider implements SavedStateRegistry.SavedStateProvider {

        @Override
        public Bundle saveState() {
            Bundle bundle = new Bundle();
            if (currentScript != null) {
                Log.d("MainModel", "saveState");
                bundle.putLong(ExtrasConst.DOC_ROW_ID_STR, currentScript.getRowid());
            }
            return bundle;
        }
    }

    public void refresh(Context context, CfgNpl config) {
       refresh(context, config, false);
    }

    public void refresh(Context context, CfgNpl config, boolean acrh) {
        UpdateProcess.Params params = new UpdateProcess.Params();
        params.setFrom(config);

        syncInfo();
        fillSyncParams(params, acrh);

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

    void fillSyncParams(UpdateProcess.Params params, boolean arch) {
        params.outdata.add(new ScheduleSender());
        params.outdata.add(new GPSHitching());

        if (!arch) {
            params.outdata.addAll(DocType.getDocuments(true, false));
            params.outdata.add(new OrgSender());
        }else{
            List<DocExportListener> export = new ArrayList();
            Calendar c = Calendar.getInstance();
            c.set(2022, 10, 20);
            String where = String.format("created > %d", c.getTimeInMillis());

            for (DocTypeBase dt : DocType.docTypes) {
                if (dt == ScriptPropDoc.instance()) continue;
                if (CreatableDocument.class.isAssignableFrom(dt.getDocClass())) {
                    DocSendListner ds = new DocSendListner(dt.getObjectName(), (Class<? extends CreatableDocument<?>>) dt.getDocClass(), where);
                    export.add(ds);
                }
            }

            params.outdata.addAll(export);
            params.outdata.add(new OrgSender(true));
        }

        params.outdata.add(new LogHitching());
        params.outdata.add(new PicStoreHitchingEx());

        params.rcvdata.add(new MessageHitchingNew());
        params.indata.addAll(requestHitchings());

        params.slicedata.add(VisitDoc.instance().getDirtyDocuments());
    }

    public void restoreDB(Context context, CfgNpl config) {
        UpdateProcess.Params params = new UpdateProcess.Params();
        params.setFrom(config);

        fillSyncParams(params, false);

        params.indata.add(new DocumentRestore(ScriptDoc.instance()){
            @Override
            public void prepareReading() {
                DbWriter.dropTable(DataObjectInfo.getInstance().getTableName(dataObject));
            }

            @Override
            protected void beforeWrite(DataObject dobj) {
                super.beforeWrite(dobj);
                ScriptEx script = (ScriptEx)dobj;
                int offset = TimeZone.getDefault().getRawOffset();

                script.birthday = new Date(script.birthday.getTime() + offset);
                script.passportIssue = new Date(script.passportIssue.getTime() + offset);
            }
        });
        params.indata.add(new DocumentRestore(SellingDoc.instance()));
        params.indata.add(new DocumentRestore(PurchaseDoc.instance()));
        params.indata.add(new DocumentRestore(QuestionDoc.instance()));

        UpdateProcess updateProcess = new Updater(context, this);
        updateProcess.execute(params);
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
            model.refreshing.postValue(new RefreshData(true));
        }

        @Override
        public void onUpdate(UpdateProcessInfo.UpdateStatus status, int progress) {
            Pair<String, Integer> val = new Pair<>(status.name(), progress);
            model.syncProgress.postValue(val);
        }

        @Override
        protected void broadcastError(String msg) {
            model.refreshing.postValue(new RefreshData(msg));
        }

        @Override
        protected void broadcastResult(int traffic) {
            model.refreshing.postValue(new RefreshData(traffic));
            model.checkGPS.postValue(!model.checkGPS.getValue());
        }
    }

    List<Hitching> requestHitchings() {
        List<Hitching> ret = new ArrayList<>();

        ret.add(new OrgHitching());
//        ret.add(new PotenzialOrgRcv());

        PriceHitching ph = new PriceHitching("AgentPriceStores") {
            @Override public void setCondition(String condition) {}
        };
        ret.add(ph);

        ret.add(new Hitching(Folder.class, "Folder"));
        ret.add(new Hitching(com.grsoft.dataobjects.Config.class, "Config"));
        ret.add(new Hitching(com.grsoft.dataobjects.Config.class, "ServerConfig"));
        ret.add(new RcvNewHitching(DbObject.getDataType(ScriptDef.class), ScriptDefImpl.OBJECT_NAME));
        ret.add(new RcvNewHitching(PayType.class));
        ret.add(new RcvNewHitching(ClientType.class));
        ret.add(new RcvNewHitching(PurchaseTemplate.class));
        ret.add(new RcvNewHitching(Question.class, "Question"));
        ret.add(new RcvNewHitching(FormatTT.class));
        ret.add(new RcvNewHitching(AgentPrefix.class));
        ret.add(new RcvNewHitching(PassportPhotos.class));
        ret.add(new RcvNewHitching(BNOper.class));
        ret.add(new NeedRemoveHitching());

        PriceHolder.clear();
        return ret;
    }
}

package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.GPSHitching;
import com.grsoft.database.Hitching;
import com.grsoft.database.PostUpdateDB;
import com.grsoft.database.PriceCostHitching;
import com.grsoft.database.PriceTypeHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.ScheduleHitching;
import com.grsoft.database.StoreHitching;
import com.grsoft.database.StoreQtyHitching;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.LogHitching;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.LogImpl;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.PresentSdcard;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.DocExportListener;
import com.grsoft.network.Format;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadService;
import com.grsoft.network.SocketConnection;
import com.grsoft.network.UpdateProcessInfo;
import com.grsoft.network.UserInfo;
import com.grsoft.network.VisitSendHelper;
import com.grsoft.network.VisitSendHelperV5;
import com.grsoft.network.WriteService;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.view.SimpleMessageBox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class UpdateDBV5 extends UpdateDB {
    // порядок важен
    // StoreQtyHitching - после StoreHitching & PriceHitching

    @Override
    protected List<Hitching> getGenDataHitchings() throws RuntimeException {
        CostStrategyV5.clearCache();

        List<Hitching> res = super.getGenDataHitchings();

        // must be in this order
        res.add(new PriceTypeHitching());
        res.add(new PriceCostHitching());

        res.add(new ScheduleHitching());

        // must be in this order
        res.add(new StoreHitching());
        res.add(new StoreQtyHitching());

        res.add(new RcvNewHitching(Firm.class));

        return res;
    }

    @Override
    protected UpdateProcess getUpdateProcess() {
        return new UpdateProcessV5(this);
    }

    class UpdateProcessV5 extends UpdateProcess {
        private final static String TAG = "UpdateProcessV5";

        public UpdateProcessV5(Activity context) {
            super(context);
        }

        String exportDocs(UserInfo ui) {
            List<DocExportListener> exportedDocs;
            exportedDocs = getExportedDocs(true, false);
            if (exportedDocs.size() > 0) {
                Log.d(TAG, "Docs are exporting");

                WriteService writeService = (WriteService) RWServiceFactory.instance.createWriteService(exportedDocs);
                writeService.setUpdateProcessListenet(this);

                if (!writeService.write(activity, ui)) {
                    Log.d(TAG, "Doc are exported: FAILURE");
                    return writeService.getMessage();
                } else {
                    Log.d(TAG, "Doc are exported: SUCCESS");
                    traffic += writeService.getSendedBytes();
                }
            }
            return null;
        }

        String exportVisits(UserInfo ui) {
            List<CreateDocDataObject> docs = getPhotoDocs();
            if(docs.size() > 0) {
                VisitSendHelperV5 vsh = new VisitSendHelperV5();
                if( !vsh.send(UpdateDBV5.this, ui, docs, this) ) {
                    Log.d(TAG, "Visit are exported: FAILURE");
                    return vsh.getError();
                } else {
                    Log.d(TAG, "Visit are exported: SUCCESS");
                    traffic += vsh.getTraffic();
                }
            }
            return null;
        }

        String exportObjects(UserInfo ui) {
            List<ObjectListener> docs = new ArrayList<ObjectListener>();
            docs.addAll(getExported());

            if(docs.size() > 0){
                WriteService writeService = (WriteService) RWServiceFactory.instance.createWriteService(docs);
                writeService.setUpdateProcessListenet(this);

                if (!writeService.write(activity, ui)) {
                    Log.d(TAG, "Doc are exported: FAILURE");
                    return writeService.getMessage();
                } else {
                    Log.d(TAG, "Doc are exported: SUCCESS");
                    traffic += writeService.getSendedBytes();
                }
            }

            return null;
        }

        String exportGPS(UserInfo ui) {
            List<ObjectListener> docs = new ArrayList<ObjectListener>();
            GPSHitching gps = new GPSHitching();
            if (gps.size() > 0)
                docs.add(gps);

            LogHitching logHitching = new LogHitching();

            if (logHitching.needUpdate())
                docs.add(logHitching);
            if(docs.size() > 0){
                WriteService writeService = (WriteService) RWServiceFactory.instance.createWriteService(docs);
                writeService.setUpdateProcessListenet(this);

                if (!writeService.write(activity, ui)) {
                    Log.d(TAG, "Doc are exported: FAILURE");
                    return writeService.getMessage();
                } else {
                    Log.d(TAG, "Doc are exported: SUCCESS");
                    traffic += writeService.getSendedBytes();
                }
            }

            return null;
        }

        String receiveData() throws RuntimeException {
            Log.d(TAG, "Gen data are importing");

            CheckBox cbRemains = null;
            try{
                cbRemains = (CheckBox) findViewById(R.id.cbRemains);
            }catch(Exception e){}

            loadFullPrice = cbRemains != null && cbRemains.isChecked();
            List<Hitching> rcvHitch = getGenDataHitchings();

            if (rcvHitch.size() > 0) {
                UserInfo rcvUserInfo = getRcvUserInfo();
                if(rcvUserInfo.impersonate.trim().length() > 0)
                    for(Hitching hitch : rcvHitch)
                        hitch.impersonate(rcvUserInfo.impersonate);

                ReadService dataBaseUpdater = (ReadService) RWServiceFactory.instance.createReadService(rcvHitch);
                dataBaseUpdater.setUpdateProcessListenet(this);

                FoldersAdapter.resetCache();

                if (!dataBaseUpdater.update(activity, rcvUserInfo, false)) {
                    Log.d(TAG, "Gen data are imported: FAILURE");
                    return dataBaseUpdater.getMessage();
                } else {
                    Log.d(TAG, "Gen data are imported: SUCCESS");
                    traffic += dataBaseUpdater.getReceivedBytes();
                }
            }

            return null;
        }

        String receivePresentation() throws RuntimeException {
            List<Hitching> result = getPrezentHitching();

            ReadService dataBaseUpdater = (ReadService) RWServiceFactory.instance.createReadService(result);
            dataBaseUpdater.setUpdateProcessListenet(this);

            if (!dataBaseUpdater.update(activity, getRcvUserInfo(), false)) {
                Log.d(TAG, "Gen data are imported: FAILURE");
                return dataBaseUpdater.getMessage();
            } else {
                Log.d(TAG, "Gen data are imported: SUCCESS");
                traffic += dataBaseUpdater.getReceivedBytes();
            }

            return null;
        }

        String restoreHistory() throws RuntimeException {
            Log.d(TAG, "Order story is recreating");

            List<Hitching> recreateHitchings = getRestoreHitching();

            ReadService dataBaseUpdater = (ReadService) RWServiceFactory.instance.createReadService(recreateHitchings);
            dataBaseUpdater.setUpdateProcessListenet(this);

            if (!dataBaseUpdater.update(activity, getRcvUserInfo(),
                    false)) {
                Log.d(TAG, "Order story is recreated: FAULURE");
                return dataBaseUpdater.getMessage();
            } else {
                Log.d(TAG, "Order story is recreated: SUCCESS");
                traffic += dataBaseUpdater.getReceivedBytes();
            }
            return null;
        }

        String receiveDebet() throws RuntimeException {
            Log.d(TAG, "Debts are importing");

            List<Hitching> debtHitchings = getDebetHitching();

            ReadService dataBaseUpdater = (ReadService) RWServiceFactory.instance.createReadService(debtHitchings);
            dataBaseUpdater.setUpdateProcessListenet(this);

            if (!dataBaseUpdater.update(activity, getRcvUserInfo(),false)) {
                errMessage = dataBaseUpdater.getMessage();
                Log.d(TAG, "Debts are imported: FAILURE");
            } else {
                Log.d(TAG, "Debts are imported: SUCCESS");
                traffic += dataBaseUpdater.getReceivedBytes();
            }

            return null;
       }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean res = false;
            if(!lock.tryLock()) {
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(
                                activity,
                                Html.fromHtml(activity.getString(R.string.use_sync_later)),
                                Toast.LENGTH_LONG).show();

                    }
                });
                return false;
            }

//            SocketConnection activeCon = null;

            Log.d(TAG, "START Update");
            traffic = 0;
            errMessage = null;
            try {
                Format.clearFormats();

                enableControlButton(false);
                onUpdate(UpdateProcessInfo.UpdateStatus.BEGIN_UPDATE, 0);

                boolean exportDocs = ((CheckBox) findViewById(R.id.cbDocs)).isChecked();
                boolean exportVisit = ((CheckBox) findViewById(R.id.cbVisit)).isChecked();
                boolean clearDB = ((CheckBox) findViewById(R.id.cbClearDB)).isChecked();

                UserInfo sndUserInfo = getSndUserInfo();

                if (!isCancelled()
                        && (exportDocs || exportVisit)
                        && sndUserInfo.isValid()) {

                    errMessage = exportDocs(sndUserInfo);

                    if(errMessage == null && !isCancelled() && exportVisit) {
                        errMessage = exportVisits(sndUserInfo);
                    }
                }

                if (errMessage == null && !isCancelled()) {
                    UserInfo gpsUserInfo = getGpsUserInfo();
                    if(gpsUserInfo.impersonate.trim().length() > 0)
                        gpsUserInfo = null;

                    errMessage = exportObjects(sndUserInfo);
                    if(errMessage == null && gpsUserInfo != null)
                        errMessage = exportGPS(gpsUserInfo);
                }

                postExported(exportDocs && errMessage == null);

                if (errMessage == null && !isCancelled() && clearDB) {
                    Path.clearDataDir();
                    DataBaseManager.clearBase();
                    DbWriter.checkDBTable(OrgSum.class);
                    LogImpl.log(com.grsoft.dataobjects.Log.PDA_STATUS, com.grsoft.dataobjects.Log.MANAGER, "");
                    Log.d(TAG, "Tables are cleared");
                }

                if (errMessage == null && !isCancelled() && receiveGenData()) {
                    errMessage = receiveData();
                }

                if (errMessage == null && !isCancelled() && ((CheckBox) findViewById(R.id.cbPresent)).isChecked()) {
                    errMessage = receivePresentation();
                }

//                if (errMessage == null && !isCancelled()) {
//                    CheckBox rcvCost = (CheckBox) findViewById(R.id.cbCost);
//                    if (rcvCost != null && rcvCost.isChecked()) {
//
//                        List<Hitching> rcvHitch = getCostHitching();
//                        ReadService dataBaseUpdater = (ReadService) RWServiceFactory.instance
//                                .createReadService(rcvHitch);
//                        dataBaseUpdater.setUpdateProcessListenet(this);
//                        dataBaseUpdater.setActiveConnection(activeCon);
//
//                        if (!dataBaseUpdater.update(activity,
//                                getRcvUserInfo(), false)) {
//                            errMessage = dataBaseUpdater.getMessage();
//                            Log.d(TAG, "Gen data are imported: FAILURE");
//                        } else {
//                            dataBaseUpdater.setActiveConnection(activeCon);
//                            Log.d(TAG, "Gen data are imported: SUCCESS");
//                            traffic += dataBaseUpdater.getReceivedBytes();
//                        }
//                    }
//                }

                CheckBox cbRecreateStory = (CheckBox) findViewById(R.id.cbRecreateStory);
                if (errMessage == null && !isCancelled() && cbRecreateStory.isChecked()) {
                    errMessage = restoreHistory();
                }

                CheckBox cbDebt = (CheckBox) findViewById(R.id.cbDebt);
                if (errMessage == null && !isCancelled() && cbDebt.isChecked()) {
                    errMessage = receiveDebet();
                }

                if (errMessage == null && !isCancelled()) {
                    PostUpdateDB pdb = new PostUpdateDB();
                    pdb.run();
                    customSyncProcess();
                }

                if (!isCancelled())
                    onUpdate(UpdateProcessInfo.UpdateStatus.END_OF_PROCESS, 0);

                if (!isCancelled()) {
                    if (errMessage != null) {
                        showErrorMsg(errMessage, activity);
                        return res;
                    } else {
                        if (onFinishUpdate(this)) {
                            res = true;

                            SimpleMessageBox smb = new SimpleMessageBox(getString(R.string.result), getSyncFinishMessage(traffic), activity);
                            onUpdateMessage(smb);
                            Thread.sleep(3000);
                            smb.hide();
                        }
                    }
                }

                CfgNplW cfg = (CfgNplW) ConfigManager.getConfig();

                if (cfg.day_to_del_visit > 0) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.add(Calendar.DAY_OF_MONTH,
                            -cfg.day_to_del_visit);

                    String where = String.format(Locale.US, "created < %d",
                            calendar.getTime().getTime());

                    deleteVisits(where);
                    deletePics(where);
                }

                if (Features.PRESENTATION_ON_SDCARD && clearDB) {
                    SharedPreferences pref = getApplication()
                            .getSharedPreferences(
                                    PresentSdcard.PREF_NAME,
                                    Context.MODE_PRIVATE);

                    SharedPreferences.Editor ed = pref.edit();
                    ed.putLong(PresentSdcard.UPDTATE_PRESENT_TIME, -1);
                    ed.commit();
                }

                Log.d(TAG, "END UPDATE");
                return res;
            } catch (Exception exception) {
                SQLiteDatabase dataBase = DataBaseManager.getDataBase();

                if (dataBase.isDbLockedByCurrentThread()
                        || dataBase.isDbLockedByOtherThreads()) {
                    try {
                        dataBase.endTransaction();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                errMessage = exception.getMessage();
                if (errMessage == null)
                    errMessage = activity.getString(R.string.recieved_error);
                if (!isCancelled())
                    showErrorMsg(errMessage, activity);

                exception.printStackTrace();

                return false;
            } finally {
                enableControlButton(true);
                Log.d(TAG, "finally END finally");

                try{
                    lock.unlock();
                }catch(Exception e){}
            }
        }
    }
}

package com.grsoft.napoleon;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.Hitching;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.DocExportListener;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.ProgressHelper;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadService;
import com.grsoft.network.SocketConnection;
import com.grsoft.network.UpdateProcessInfo;
import com.grsoft.network.UserInfo;
import com.grsoft.network.WriteService;
import com.grsoft.network.util.ProgressManager;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.view.SimpleMessageBox;

import java.util.List;

public class SyncProcess extends NetworkAsyncTask {
    static String TAG = "SyncProcess";

    Activity context;

    List<Hitching> read;
    List<DocExportListener> send;

    int traffic = 0;
    String errMessage = null;

    public SyncProcess(Activity context, List<Hitching> read, List<DocExportListener> send) {
        super(new ProgressManager(context));
        ((ProgressManager) this.progressHelper).setUpdateProcess(this);

        this.context = context;
        this.read = read;
        this.send = send;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean res = false;

        try {
            onUpdate(UpdateProcessInfo.UpdateStatus.BEGIN_UPDATE, 0);

            Config config = ConfigManager.getConfig();
            UserInfo sndUserInfo = new LoginData(config.login, config.passw, config.impersonate, context);
            SocketConnection activeCon = null;

            if (!isCancelled() && send != null && send.size() > 0) {

                WriteService writeService = (WriteService) RWServiceFactory.instance.createWriteService(send);
                writeService.setUpdateProcessListenet(this);
                writeService.setActiveConnection(activeCon);

                if (!writeService.write(context, sndUserInfo)) {
                    errMessage = writeService.getMessage();
                    Log.d(TAG, "Doc are exported: FAILURE");
                } else {
                    activeCon = writeService.getActiveConnection();
                    Log.d(TAG, "Doc are exported: SUCCESS");
                    traffic += writeService.getSendedBytes();
                }
            }

            if (errMessage == null && !isCancelled() && read != null && read.size() > 0) {
                ReadService dataBaseUpdater = (ReadService) RWServiceFactory.instance.createReadService(read);
                dataBaseUpdater.setUpdateProcessListenet(this);
                dataBaseUpdater.setActiveConnection(activeCon);

                FoldersAdapter.resetCache();

                if (!dataBaseUpdater.update(context, sndUserInfo, false)) {
                    errMessage = dataBaseUpdater.getMessage();
                    Log.d(TAG, "Gen data are imported: FAILURE");
                } else {
                    activeCon = dataBaseUpdater.getActiveConnection();
                    Log.d(TAG, "Gen data are imported: SUCCESS");
                    traffic += dataBaseUpdater.getReceivedBytes();
                }
            }
            if (!isCancelled())
                onUpdate(UpdateProcessInfo.UpdateStatus.END_OF_PROCESS, 0);

            if (!isCancelled()) {
                if (errMessage != null) {
                    showErrorMsg(errMessage, context);
                    return res;
                } else {
                    res = true;

                    SimpleMessageBox smb = new SimpleMessageBox(
                            context.getString(R.string.result),
                            context.getString(R.string.sync_end_traffic) + Integer.toString((traffic + 512) / 1024) + " " + context.getString(R.string.kB),
                            context);
                    onUpdateMessage(smb);
                    Thread.sleep(3000);
                    smb.hide();
                }
            }
            Log.d(TAG, "END UPDATE");
            return res;
        } catch (Exception exception) {
            SQLiteDatabase dataBase = DataBaseManager.getDataBase();
            if (dataBase.isDbLockedByCurrentThread() || dataBase.isDbLockedByOtherThreads()) {
                try {
                    dataBase.endTransaction();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            errMessage = exception.getMessage();
            if (errMessage == null)
                errMessage = context.getString(R.string.recieved_error);
            if (!isCancelled())
                showErrorMsg(errMessage, context);

            exception.printStackTrace();

            return false;
        } finally {
            Log.d(TAG, "finally END finally");
        }
    }
}



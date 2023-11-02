package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.Decision;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.impl.DecisionImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadService;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.util.ProgressManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class DocListEx extends DocList {
	private static final String END_SYNC_ACTION = "end_sync_action";
	private static final String SYNC_RESULT = "sync_result";
	private static final String SYNC_MESSAGE = "sync_message";
	private View btnSync;

	@Override
	protected int getViewID() {
		return R.layout.doclistex;
	}

	@Override
	protected void initUI() {
		super.initUI();

		btnSync = findViewById(R.id.btnSync);

		btnSync.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Sync(v.getContext()).execute((Void[]) null);
			}
		});
	}

	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);

		btnSync.setVisibility(docType == ReturnDoc.instance() ? View.VISIBLE : View.GONE);
	}

	@Override
	protected void drawData(View view, Document<?> doc, int position) {
		super.drawData(view, doc, position);

		if (DocType.getCurDoc() == ReturnDoc.instance()) {
			DecisionImpl impl = new DecisionImpl();
			boolean res = impl.read(((Return) doc.getData()).created.getTime());
			impl.close();

			if (res && impl.getData().value != 0) {
				TextView tv = (TextView) view.findViewById(R.id.tvStatus);
				tv.setText(Html.fromHtml(doc.getDescription(this) + "&nbsp;"
						+ (impl.getData().value == Decision.APPROVED ? getString(R.string.approved)
								: getString(R.string.rejected))));
				tv.setVisibility(View.VISIBLE);
			}
		}
	}

	private BroadcastReceiver sync_result = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getBooleanExtra(SYNC_RESULT, false)) {
				Bundle args = new Bundle();
				args.putString(SYNC_MESSAGE, intent.getStringExtra(SYNC_MESSAGE));
				showDialog(R.id.sync_err_dlg, args);
			} else
				adapter.notifyDataSetChanged();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		if (id == R.id.sync_err_dlg)
			return createSyncErrDlg(args);
		else
			return super.onCreateDialog(id);
	}

	private Dialog createSyncErrDlg(Bundle args) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.error);
		builder.setMessage(args.getString(SYNC_MESSAGE));
		builder.setPositiveButton(R.string.ok, null);

		return builder.create();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		if (id == R.id.sync_err_dlg)
			prepareSyncErrDlg(dialog, args);
		else
			super.onPrepareDialog(id, dialog, args);
	}

	private void prepareSyncErrDlg(Dialog dialog, Bundle args) {
		((AlertDialog) dialog).setMessage(args.getString(SYNC_MESSAGE));
	}

	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver(sync_result, new IntentFilter(END_SYNC_ACTION));
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (isFinishing())
			unregisterReceiver(sync_result);
	}

	static class Sync extends NetworkAsyncTask {
		private Context context;

		public Sync(Context context) {
			super(new ProgressManager(context));
			((ProgressManager) this.progressHelper).setUpdateProcess(this);
			this.context = context;
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			onUpdate(UpdateStatus.BEGIN_UPDATE, 0);

			List<Hitching> rcv = new ArrayList<Hitching>();
			rcv.add(new DecisionHitching());

			String errMessage = "";

			ReadService dataBaseUpdater = (ReadService) RWServiceFactory.instance.createReadService(rcv);
			dataBaseUpdater.setUpdateProcessListenet(this);

			Config config = ConfigManager.getConfig();
			LoginData ld = new LoginData(config.login, config.passw, "", context);

			boolean res = false;

			try {
				res = dataBaseUpdater.update(context, ld, false);
				if (!res)
					errMessage = dataBaseUpdater.getMessage();
			} catch (com.grsoft.network.exception.RuntimeException e) {
				e.printStackTrace();

				errMessage = e.getMessage();
				if (errMessage == null)
					errMessage = context.getString(R.string.recieved_error);
			}

			onUpdate(UpdateStatus.END_OF_PROCESS, 0);

			Intent i = new Intent(END_SYNC_ACTION);
			i.putExtra(SYNC_RESULT, res);
			i.putExtra(SYNC_MESSAGE, errMessage);
			context.sendBroadcast(i);

			return res;
		}
	}

}

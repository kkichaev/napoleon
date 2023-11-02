package com.grsoft.napoleon.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.AgentInfo;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DivisionAgent;
import com.grsoft.dataobjects.ManagerMessage;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.ProgressHelper;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.RawObject;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.UserInfo;
import com.grsoft.network.WriteServiceBase;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.network.util.ProgressManager;
import com.grsoft.util.Util;

public class EditMessage extends DialogFragment implements UpdateProcessOwner {
	Button btnSend;
	EditText edMessage;

	public AgentInfo agentInfo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		View result = inflater.inflate(R.layout.editmessage, container);
		btnSend = (Button) result.findViewById(R.id.btnSend);
		edMessage = (EditText) result.findViewById(R.id.edMessage);

		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (agentInfo != null) {
					List<ObjectListener> snd = new ArrayList<ObjectListener>();
					snd.add(new SendMessage());
					new WriteMessageProcess(getActivity(), EditMessage.this, snd, agentInfo.getAgents())
							.execute((Void[]) null);
				}
			}
		});

		return result;
	}

	@Override
	public void enableControlButton(final boolean enabled) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				btnSend.setEnabled(enabled);
			}
		});

	}

	@Override
	public void onFinish() {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getActivity(), R.string.message_sended,
						Toast.LENGTH_SHORT).show();
				dismiss();
			}
		});

	}

	class SendMessage implements ObjectExportListener {
		@Override
		public void onStart() {
		}

		@Override
		public void onRead(RawObject rawObject) throws RuntimeException {
		}

		@Override
		public void onSave() {
		}

		@Override
		public void onEnd() {
		}

		@Override
		public String getObjectName() {
			return "Message";
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public DataObject get(int i) {
			ManagerMessage msg = new ManagerMessage();
			msg.date = Util.getDate();
			msg.message = edMessage.getText().toString().trim();

			return msg;
		}

	}
}

class WriteMessageProcess extends NetworkAsyncTask {
	protected int traffic = 0;
	protected Activity mActivity;
	protected UpdateProcessOwner mProcessOwner;
	protected static Lock lock = new ReentrantLock();
	List<ObjectListener> tosend;
	private List<DivisionAgent> userid;

	public WriteMessageProcess(Activity context, UpdateProcessOwner processOwner,
			List<ObjectListener> tosend, List<DivisionAgent> userid) {

		super(new ProgressManager(context));

		((ProgressManager) this.progressHelper).setUpdateProcess(this);

		this.tosend = tosend;
		this.userid = userid;
		mActivity = context;
		mProcessOwner = processOwner;
	}

	protected WriteMessageProcess(ProgressHelper progressHelper) {
		super(progressHelper);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		if (!lock.tryLock()) {
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(
							mActivity,
							Html.fromHtml(mActivity
									.getString(R.string.use_sync_later)),
							Toast.LENGTH_LONG).show();

				}
			});

			return false;
		}

		boolean ret = true;
		try {

			mProcessOwner.enableControlButton(false);

			String errMessage = null;

			if (tosend.size() > 0) {
				WriteServiceBase reader = RWServiceFactory.instance
						.createWriteService(tosend);
				reader.setUpdateProcessListenet(this);

				for (DivisionAgent da : userid) {
					if (!reader.write(mActivity, getUserInfo(da.id))) {
						errMessage = reader.getMessage();
					} else {
						traffic += reader.getSendedBytes();
					}
				}
			}

			if (!isCancelled())
				onUpdate(UpdateStatus.END_OF_PROCESS, 0);

			if (!isCancelled()) {
				if (errMessage != null) {
					showErrorMsg(errMessage, mActivity);
					return false;
				} else
					mProcessOwner.onFinish();
			}

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

			String message = exception.getMessage();
			if (message == null)
				message = mActivity.getString(R.string.recieved_error);
			if (!isCancelled())
				showErrorMsg(message, mActivity);

			exception.printStackTrace();

			ret = false;
		} finally {
			mProcessOwner.enableControlButton(true);
			lock.unlock();
		}
		return ret;
	}

	protected UserInfo getUserInfo(String id) {
		Config config = ConfigManager.getConfig();
		return new LoginData(config.login, config.passw, id,
				mActivity);
	}
}

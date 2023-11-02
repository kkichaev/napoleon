package com.grsoft.manager;

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
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.database.ArchiveMessageHitching;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DivisionAgent;
import com.grsoft.dataobjects.Message;
import com.grsoft.manager.view.RowItem;
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

public class EditMessage extends DialogFragment implements UpdateCtrl {
	Button btnSend;
	Button btnHistory;
	EditText edMessage;

	public RowItem agentInfo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		View result = inflater.inflate(R.layout.editmessage, container);

		TextView tv = (TextView) result.findViewById(R.id.tvTitle);
		tv.setText(getActivity().getResources().getString(R.string.write_message_to, agentInfo.getTitle()));
		btnSend = (Button) result.findViewById(R.id.btnSend);
		edMessage = (EditText) result.findViewById(R.id.edMessage);
		btnHistory = (Button) result.findViewById(R.id.btnHistory);

		if (agentInfo != null) {
			final List<DivisionAgent> agentList = agentInfo.getAgents();

			btnSend.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (agentInfo != null && edMessage.getText().toString().trim().length() > 0) {
						List<ObjectListener> snd = new ArrayList<ObjectListener>();
						snd.add(new SendMessage());
						new WriteMessageProcess(getActivity(),
								EditMessage.this, snd, agentList)
								.execute((Void[]) null);
					}
				}
			});

			if (agentList.size() == 1)
				btnHistory.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						List<Hitching> ret = new ArrayList<Hitching>();
						ret.add(new ArchiveMessageHitching(agentList.get(0).id));
						UpdateProcess upp = new UpdateProcess( getActivity(), new UpdateCtrl() {

							@Override
							public void onFinish(boolean result) {
								if( result )
									getActivity().runOnUiThread(new Runnable() {
										@Override public void run() { HistoryMessage.open(getActivity(), agentList.get(0).id); }
									});
							}

							@Override
							public void updateCtrl(boolean enabled) {
							}
						}, ret);
						upp.execute((Void[]) null);
					}
				});
			else
				btnHistory.setEnabled(false);
		}

		return result;
	}

	@Override
	public void updateCtrl(final boolean enabled) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				btnSend.setEnabled(enabled);
			}
		});

	}

	@Override
	public void onFinish(final boolean result) {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if( result )
					Toast.makeText(getActivity(), R.string.message_sended, Toast.LENGTH_SHORT).show();
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
			Message msg = new Message();
			msg.date = Util.getDate();
			msg.message = edMessage.getText().toString().trim();

			return msg;
		}

	}
}

class WriteMessageProcess extends NetworkAsyncTask {
	protected int traffic = 0;
	protected Activity mActivity;
	protected UpdateCtrl mProcessOwner;
	protected static Lock lock = new ReentrantLock();
	List<ObjectListener> tosend;
	private List<DivisionAgent> userid;

	public WriteMessageProcess(Activity context,
			UpdateCtrl processOwner, List<ObjectListener> tosend,
			List<DivisionAgent> userid) {

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

			mProcessOwner.updateCtrl(false);

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
					mProcessOwner.onFinish(false);
					return false;
				} else
					mProcessOwner.onFinish(true);
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
			mProcessOwner.updateCtrl(true);
			lock.unlock();
		}
		return ret;
	}

	protected UserInfo getUserInfo(String id) {
		Config config = ConfigManager.getConfig();
		return new LoginData(config.login, config.passw, id, mActivity);
	}
}

package com.grsoft.ads;

import com.grsoft.ads.dataobjects.impl.TaskResponceImpl;
import com.grsoft.napoleon.dataobjects.TaskQuery;
import com.grsoft.network.BaseFragmentActivity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;


public class TaskReadyToStart extends TaskPreview implements OnClickListener {
	private ImageButton ivAttachment;

	public static final Class<? extends BaseFragmentActivity> activity = TaskReadyToStart.class;

	public static void open(Context context, String taskid) {
		Intent intent = new Intent(context, activity);
		intent.putExtra(AdsConsts.TASKID, taskid);
		context.startActivity(intent);
	}
	
	@Override
	protected void inflateView() {
		View v = getLayoutInflater().inflate(R.layout.task_ready_action_bar, null);
		v.findViewById(R.id.btnOrder).setOnClickListener(noteClick());
		ivAttachment = v.findViewById(R.id.ivAttachment);
		ivAttachment.setOnClickListener(this);

		ActionBar a = getActionBar();
        a.setCustomView(v);
        a.setDisplayShowCustomEnabled(true);
        
		super.inflateView();
	}
	
	@Override
	protected void initView() {
		super.initView();
		btnCancel.setVisibility(View.GONE);
		((TextView)btnOK).setText(R.string.Start);
		btnOK.setOnClickListener(startClick());
		btnNote.setVisibility(View.VISIBLE);


		findViewById(R.id.topPanel).setVisibility(View.GONE);
		ivAttachment.setVisibility(taskHasAttachments() ? View.VISIBLE : View.GONE);
	}

	private OnClickListener startClick() {
		return new OnClickListener() {
			@Override public void onClick(View v) {
				TaskResponceImpl impl = new TaskResponceImpl();
				impl.init(getContext(), task.getData());
				impl.getData().solution = TaskQuery.INWORK;
				impl.write();
				
				task.getData().solution = TaskQuery.INWORK;
				task.write();
				task.close();
				
				sendBroadcast(new Intent(AdsService.SYNC_ACTION));
				finish();
				TaskEdit.open(getContext(), task.getData().taskid);
			}
		};
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.ivAttachment)
			TaskAttachmentActivity.open(this, task.getData().taskid);
	}
}

package com.grsoft.ads;

import com.grsoft.network.BaseFragmentActivity;
import android.content.Context;
import android.content.Intent;
import android.view.View;


public class TaskReview extends TaskEdit {
	public static final Class<? extends BaseFragmentActivity> activity = TaskReview.class;
	
	public static void open(Context context, String taskid) {
		Intent intent = new Intent(context, activity);
		intent.putExtra(AdsConsts.TASKID, taskid);
		context.startActivity(intent);
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		btnOK.setVisibility(View.GONE);
		tvRemark.setEnabled(false);
		btnPhoto.setVisibility(View.GONE);
	}
}

package com.keeper.db;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.keeper.R;
import com.keeper.views.DataSetContext;

public abstract class GroupAsyncOper extends AsyncTask<Object, Void, Boolean>{
	protected Context context;
	
	public GroupAsyncOper(Context context){
		this.context = context;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if(result == true){
			((DataSetContext)context).notifyAdapterDataChanged();
		}else
			Toast.makeText(context, 
					context.getResources().getString(R.string.error), 
					Toast.LENGTH_SHORT).show();
	}
}

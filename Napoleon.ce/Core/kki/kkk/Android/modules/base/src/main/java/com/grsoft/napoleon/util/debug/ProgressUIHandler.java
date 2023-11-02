package com.grsoft.napoleon.util.debug;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Message;
import com.grsoft.napoleon.R;

public class ProgressUIHandler extends TestUIHandler
{
	protected Context context;
	private ProgressDialog progressDialog = null;
	
	final int WAIT_MSG = R.string.wait;
	final int MSG_MSG  = R.string.table_is_creating;
	
	public ProgressUIHandler(Context context)
	{
		this.context = context;
	}
	
	@Override
	public void handleMessage(Message msg)
	{
		switch (msg.arg1)
		{
			case TestUIHandler.SHOW_PROGRESS_DIALOG:
				progressDialog = ProgressDialog.show(context, context.getString(WAIT_MSG), 
						context.getString(MSG_MSG));
				break;
			case TestUIHandler.HIDE_PROGRESS_DIALOG:
				if (progressDialog != null)
					progressDialog.hide();
		}
		
		if (msg.obj != null)
		{
			synchronized (msg.obj) 
			{
				msg.obj.notify();
			}
		}
	}
}

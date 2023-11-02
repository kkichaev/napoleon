package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import com.grsoft.dataobjects.impl.VisitImplEx;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.util.ReceiveVisit;

public class VisitEdit2Ex extends VisitEditEx {
	ImageButton btnRcv;
	ReceiveVisit receiveVisit = null;
	
	{
		visit = new VisitImplEx();
	}
	
	protected int getContentView() {
		return R.layout.visiteditex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnRcv = (ImageButton) findViewById(R.id.btnRcv);
		
		btnRcv.setOnClickListener(new  OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateVisit();
			}
		});
		
		btnRcv.setVisibility(visit.isExported() ? View.VISIBLE : View.GONE);
	}
	
	void updateVisit() {
		if( receiveVisit == null ) {
			receiveVisit = new ReceiveVisit(this, new ReceiveVisit.TaskDoneHandler() {
				@Override public void taskDone(NetworkAsyncTask task) {
					if( receiveVisit == task )
						receiveVisit = null;
					((BaseAdapter)adapter).notifyDataSetChanged();
				}
			}, visit);
			
			receiveVisit.execute((Void[])null);
		}
	} 
	
}

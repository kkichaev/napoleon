package com.grsoft.manager;

import com.grsoft.manager.spk.R;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.SPKTaskRcv;
import com.grsoft.dataobjects.ManagerAgent;
import com.grsoft.manager.documents.SPKTaskDoc;
import com.grsoft.network.ObjectListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SPKTaskActivity extends DrawerActivity implements OnItemClickListener {
	private ListView list;
	private SPKTaskAdapter adapter;

	public static void open(Context context) {
		Intent i = new Intent(context, SPKTaskActivity.class);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		list = (ListView) findViewById(R.id.list);
		
		adapter = new SPKTaskAdapter(this);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		adapter.reload();
		adapter.notifyDataSetChanged();
	}
	
	@Override
	protected int getLayoutID() {
		return R.layout.task_view;
	}

	@Override
	protected void postSyncUpdate() {
	}

	@Override
	protected String getActionBarTitle() {
		return getString(R.string.tasks);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ManagerAgent a = (ManagerAgent) parent.getItemAtPosition(position);
		
		SPKTaskAgentActivity.open(this, a.id);
	}
	
	protected void doSync() {
		List<Hitching> ret = new ArrayList<Hitching>();
		ret.add(new SPKTaskRcv());
		
		UpdateProcess upp = new UpdateProcess(this, new UpdateCtrl() {
			@Override public void updateCtrl(boolean enabled) {}
			
			@Override
			public void onFinish(boolean success) {
				if( success )
					postSyncUpdate();
			}
		}, ret);
		
		List<ObjectListener> toSend = new ArrayList<ObjectListener>();
		toSend.add(SPKTaskDoc.instance().getDirtyDocuments());
		upp.setSending(toSend);
		
		upp.execute((Void[]) null);
	}
}

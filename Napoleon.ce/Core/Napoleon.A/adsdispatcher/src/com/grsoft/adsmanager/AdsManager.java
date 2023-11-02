package com.grsoft.adsmanager;

import java.util.List;

import com.grsoft.adsmanager.dataobjects.MAgent;
import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Agent;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AdsManager  extends DrawerActivity implements OnItemClickListener{
	private ListView list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		list = (ListView) findViewById(R.id.list);
		list.setAdapter(new AdsManagerListAdapter(this));
		list.setOnItemClickListener(this);
	}
	
	@Override
	protected int getLayoutID() {
		return R.layout.adsmanager;
	}

	@Override
	protected void postSyncUpdate() {
		AdsManagerListAdapter a = (AdsManagerListAdapter) list.getAdapter();
		
		if(a != null) {
			a.refresh();
			a.notifyDataSetChanged();
		}
	}

	@Override
	protected String getActionBarTitle() {
		return null;
	}
	
	@Override
	protected void initHitchings(List<Hitching> list) {
		super.initHitchings(list);
		
		list.add(new RcvNewHitching(Agent.class));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		MAgent a = (MAgent) parent.getItemAtPosition(position);
		
		if(a != null)
			TaskActivity.open(this, a.id);
	}
}

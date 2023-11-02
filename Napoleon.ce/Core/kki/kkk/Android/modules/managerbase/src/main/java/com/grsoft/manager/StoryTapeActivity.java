package com.grsoft.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.ReportHitching;
import com.grsoft.database.StoryTape;
import com.grsoft.dataobjects.StoryTapeParam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class StoryTapeActivity extends DrawerActivity implements OnItemClickListener {
	private ListView list;
	private SwipeRefreshLayout swipeRefresh;

	public static void open(Context context){
		Intent i = new Intent(context, StoryTapeActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
		context.startActivity(i);
	}
	
	@Override
	protected int getLayoutID() { return R.layout.story; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		list = (ListView) findViewById(R.id.list);
		swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.refresh);
		
		list.setAdapter(new StoryTapeAdapter(this));
		list.setOnItemClickListener(this);
		swipeRefresh.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				doSync();
			}
		});
	}

	@Override
	protected void postSyncUpdate() {
		StoryTapeAdapter a = (StoryTapeAdapter) list.getAdapter();
		a.reload();
		a.notifyDataSetChanged();
		swipeRefresh.setRefreshing(false);
	}

	@Override
	protected String getActionBarTitle() {
		return getString(R.string.story_activity);
	}
	
	@Override
	protected void initHitchings(List<Hitching> list) {
		super.initHitchings(list);
		List<Hitching> repResult = new ArrayList<Hitching>();
		repResult.add(new RcvNewHitching(StoryTape.class));
		list.add(new ReportHitching("storytape", new StoryTapeParam(), repResult));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		StoryTape t = (StoryTape) parent.getItemAtPosition(position);
		SyncDetail.sync(this, createUpdateCtrl(this, t.created, t.userid), 
				t.userid, t.created, true);
	}
	
	private UpdateCtrl createUpdateCtrl(final Activity activity, final Date date, final String userid) {
		return new UpdateCtrl() {
			@Override public void onFinish(boolean result) {
				if( result )
					AgentRouteNew.open(activity, userid, date, AgentRouteNew.DOC_VIEW_TYPE);
			}
			
			@Override public void updateCtrl(boolean enabled) {} };
	}
	
	@Override
	protected int getActionBarLayoutID() {
		return R.layout.story_tape_action_bar;
	}
	
	@Override
	protected int getOptionsMenuID() {
		return R.menu.story_tape_menu;
	}
}

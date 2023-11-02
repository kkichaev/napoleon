package com.grsoft.adsmanager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.grsoft.adsmanager.dataobjects.impl.MAgentImpl;
import com.grsoft.database.HitchOnSelect;
import com.grsoft.database.Hitching;
import com.grsoft.napoleon.dataobjects.TaskQuery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

public class TaskActivity extends DrawerActivity {
	private static final String USERID = "userid";
	private String userid = "";
	private ListView list;
	
	public static void open(Context context, String id) {
		Intent i = new Intent(context, TaskActivity.class);
		i.putExtra(USERID, id);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		list = (ListView) findViewById(R.id.list);
		
		userid = getIntent().getStringExtra(USERID);
	}
	
	@Override
	protected int getLayoutID() {
		return R.layout.tasks;
	}

	@Override
	protected void postSyncUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getActionBarTitle() {
		MAgentImpl a = new MAgentImpl();
		a.read("id", getIntent().getStringExtra(USERID));

		return a.getData().name;
	}
	
	@Override
	protected void initHitchings(List<Hitching> list) {
		super.initHitchings(list);
		
		SimpleDateFormat sdf =  new SimpleDateFormat("dd.MM.yyyy");
		Calendar c = Calendar.getInstance();
		Date start = c.getTime();
		c.add(Calendar.DATE, 1);
		Date finish = c.getTime();
		HitchOnSelect h = new HitchOnSelect(TaskQuery.class, "TaskQueryManager");
		h.setCondition(String.format("%s;%s;%s", sdf.format(start), sdf.format(finish), userid));
		list.add(h);
	}
}

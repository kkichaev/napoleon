package com.grsoft.manager;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class DrawerHelper implements OnItemClickListener  {
	public static Class<? extends DrawerHelper> instanceType = DrawerHelper.class;
	protected ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	
	public static DrawerHelper getInstance() {
		DrawerHelper res = new DrawerHelper();
		
		try {
			res = instanceType.newInstance();
		}catch(Exception e) {
			e.printStackTrace(); 
		}
			
		return res;
	}
	
	protected DrawerHelper() {
	}
	
	public void onCreate(Activity activity){
		drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
		ListView leftMenu = (ListView) activity.findViewById(R.id.left_drawer);
		
		if(leftMenu != null){
			leftMenu.setAdapter(new LeftMenuAdapter(activity, getLeftMenuID()));
			drawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, R.drawable.action_bar_nav_ico, R.string.drawer_open, R.string.drawer_close );
	        drawerLayout.setDrawerListener(drawerToggle);
	        leftMenu.setOnItemClickListener(this);
		}
	}
	
	protected int getLeftMenuID() {
		return R.menu.main_navigate;
	}


	public void syncState(){
		if(drawerToggle != null)
			drawerToggle.syncState(); 
	}
	
	public void onConfigurationChanged(Configuration newConfig){
		if(drawerToggle != null)
			drawerToggle.onConfigurationChanged(newConfig); 
	}
	
	public boolean onOptionsItemSelected(MenuItem item) { return drawerToggle != null ? drawerToggle.onOptionsItemSelected(item) : false; }
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
		MenuItem item = (MenuItem) parent.getItemAtPosition(position);
		int id = item.getItemId();
		Context context = view.getContext();
		
		if(id == R.id.works)
			ManagerNew.open(context);
		else if (id == R.id.setting)
			ManagerConfigurationNew.open(context);
		else if (id == R.id.reports)
			ReportListActivity.open(context);
		else if (id == R.id.agents_in_fields)
			AgentsInFields.open(context);
		else if (id == R.id.about)
			AboutNew.open(context);
		else if (id == R.id.story)
			StoryTapeActivity.open(context);
		
		childItemClick(context, id);
	}
	
	protected void childItemClick(Context context, int id) {
	}

	public void closeDrawer() {
		if(drawerLayout != null)
			drawerLayout.closeDrawer(Gravity.LEFT);
	}
}

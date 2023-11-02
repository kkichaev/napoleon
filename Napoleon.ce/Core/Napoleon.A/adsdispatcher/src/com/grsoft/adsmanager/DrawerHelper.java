package com.grsoft.adsmanager;

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
	protected ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	
	public void onCreate(Activity activity){
		drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
		ListView leftMenu = (ListView) activity.findViewById(R.id.left_drawer);
		
		if(leftMenu != null){
			leftMenu.setAdapter(new LeftMenuAdapter(activity));
			drawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, R.drawable.action_bar_nav_ico, R.string.drawer_open, R.string.drawer_close );
	        drawerLayout.setDrawerListener(drawerToggle);
	        leftMenu.setOnItemClickListener(this);
		}
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
		
		if(id == R.id.setting) {
			Setting.open(context);
		}
		
	}
	
	public void closeDrawer() {
		drawerLayout.closeDrawer(Gravity.LEFT);
	}
}

package com.grsoft.manager;

import java.util.Date;

import com.grsoft.dataobjects.ManagerAgent;
import com.grsoft.dataobjects.impl.ManagerAgentImpl;
import com.grsoft.view.Refreshable;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

public class AgentRouteNew extends AgentRoute implements OnClickListener{
	private TextView tvTitle;
	private Fragment mapFragment;
	private Fragment orderFragment;
	private SelectAgentHelper slAgentHelper;
	private MapTypeSelectorHelper mtSelectHelper = new MapTypeSelectorHelper();
	private static final String VIEW_TYPE = "view_type"; 
	public static final String MAP_VIEW_TYPE = "map_view_type";
	public static final String DOC_VIEW_TYPE = "doc_view_type";
	
	public static int routePoints = 0;
	
	public static void open(Context context, String userid, Date date, String vt){
		Intent i = new Intent(context, AgentRouteNew.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
		i.putExtra(USERID, userid);
		i.putExtra(DATE, date.getTime());
		i.putExtra(VIEW_TYPE, vt);
		context.startActivity(i);
	}
	
	protected int getActionBarLayoutID(){ return R.layout.agent_route_action_bar; }
	
	@Override
	protected void onCreate(Bundle arg0) {
		slAgentHelper = new SelectAgentHelper();
		slAgentHelper.init();
		
		super.onCreate(arg0);
		
		ActionBar a = getActionBar();
		View v = a.getCustomView();
		tvTitle = (TextView) v.findViewById(R.id.tvTitle);
		tvTitle.setOnClickListener(this);
		
		mapFragment = new MapFragmentNew();
		orderFragment = new DocFragmentNew();
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		Fragment f = getIntent().getStringExtra(VIEW_TYPE).equals(DOC_VIEW_TYPE) ? orderFragment : mapFragment;
		ft.add(R.id.container, f);
		ft.commit();
	
		ManagerAgentImpl m = new ManagerAgentImpl();
		m.read("id", userid);
		
		slAgentHelper.setControl(tvTitle);
		slAgentHelper.setSelection(userid);
		
		final CheckBox cb = (CheckBox)v.findViewById(R.id.cbRoutePoints);
		cb.setChecked(routePoints != 0);
		
		cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				routePoints = arg1 ? 15 : 0;
				cb.setCompoundDrawablesWithIntrinsicBounds(0, 0, arg1 ? R.drawable.check_box_on : R.drawable.check_box_off, 0);
				updateChildFragment();						
			}
		});
	}
	
	@Override
	public String getUserid() {
		String result = userid;
		ManagerAgent s = slAgentHelper.getSelected();
		
		if(s != null)
			result = s.id;
		
		return result;
	}
	
	@Override public String getActionBarTitle() { return ""; }
	
	
	@Override protected int getLayoutID() { return R.layout.route_new; }

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.tvTitle)
			showDialog(R.id.agent_dlg);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.agent_dlg)
			return slAgentHelper.createDialog(this);
		else if (id == R.id.select_map_type_dlg)
			return mtSelectHelper.createSelectMapTypeDlg(this);
		else
			return super.onCreateDialog(id);
	}

	@Override protected int getOptionsMenuID() { return R.menu.agent_route_menu; }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == R.id.itDoc){
			setupMode(orderFragment);
			return true;
		} else if(id == R.id.itMap){
			setupMode(mapFragment);
			return true;
		} else if(id == R.id.itMapType){
			showDialog(R.id.select_map_type_dlg);
			return true;
		} else
			return super.onOptionsItemSelected(item);
	}
	
	private void setupMode(Fragment fragment){
		replaceFragment(fragment);
		invalidateOptionsMenu();
	}
	
	private void replaceFragment(Fragment f){
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.container, f);
		ft.commit();
		fm.executePendingTransactions();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		FragmentManager fm = getSupportFragmentManager();
		Fragment f = fm.findFragmentById(R.id.container);
		
		if(f instanceof MapFragmentNew){
			menu.findItem(R.id.itMap).setVisible(false);
			menu.findItem(R.id.itDoc).setVisible(true);
		}else{
			menu.findItem(R.id.itMap).setVisible(true);
			menu.findItem(R.id.itDoc).setVisible(false);
		}
		
		mtSelectHelper.addMenuItem(menu);
		
		return result;
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == R.id.agent_dlg)
			slAgentHelper.prepareDialog(dialog);
		if(id == R.id.select_map_type_dlg)
			mtSelectHelper.prepareSelectMapTypeDlg(dialog);
		else
			super.onPrepareDialog(id, dialog);
	}

	@Override
	protected void updateChildFragment() {
		FragmentManager fm = getSupportFragmentManager();
		Fragment f = fm.findFragmentById(R.id.container);
		if(f instanceof Refreshable)
			((Refreshable)f).refreshContent();
	}
}

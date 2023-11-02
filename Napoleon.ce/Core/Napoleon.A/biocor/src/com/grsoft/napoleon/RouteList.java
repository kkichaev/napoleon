package com.grsoft.napoleon;

import java.util.Date;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.grsoft.dataobjects.OrgFoldersEx;
import com.grsoft.network.BaseFragmentActivity;
import com.grsoft.util.ExtrasConst;


public class RouteList extends BaseFragmentActivity{
	private ListView list;
	private View btnAdd;
	
	public static void open(Context ctx) {
		Intent intent = new Intent(ctx, RouteList.class);
		ctx.startActivity(intent);
	}
	
	@Override protected int getLayoutID() { return R.layout.routelist; }
	@Override
	protected void inflateView() {
		list = (ListView) findViewById(R.id.list);
		btnAdd = findViewById(R.id.btnAdd);
	}
	
	@Override
	protected void initView() {
		list.setDividerHeight(0);
		list.setOnItemClickListener(onItemClick());
		btnAdd.setOnClickListener(addClick());
		registerForContextMenu(list);
	}
	
	private OnClickListener addClick() { return new OnClickListener() { @Override public void onClick(View v) { openCalendar(); } }; }

	private OnItemClickListener onItemClick() {
		return new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				OrgFoldersEx of =  (OrgFoldersEx) parent.getItemAtPosition(position);
				EdRoute.open(getContext(), of.date);
			}};
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver(dateChangeRcv, new IntentFilter(CalendarFragment.DATE_CHANGE_ACTION));
	};
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(isFinishing())
			try{
				unregisterReceiver(dateChangeRcv);
			}catch(Exception e){
				e.printStackTrace();
			}
	};
	
	BroadcastReceiver dateChangeRcv = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			long date = intent.getLongExtra(ExtrasConst.DATE_TAG, new Date().getTime());
			EdRoute.open(getContext(), new Date(date));
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		list.setAdapter(new RouteListAdapter(getContext()));
	};
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) { getMenuInflater().inflate(R.menu.routelist_ctx_menu, menu); };
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int pos = ((AdapterContextMenuInfo)item.getMenuInfo()).position;
		switch(item.getItemId()){
		case R.id.itDelete:
			deleteItem(pos);
			return true;
		case R.id.itAdd:
			addItem();
			return true;
		case R.id.itEdit:
			editItem(pos);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void editItem(int pos) {
		OrgFoldersEx o = (OrgFoldersEx) list.getAdapter().getItem(pos);
		EdRoute.open(getContext(), o.date);
	}

	private void addItem() { openCalendar(); }

	private void deleteItem(int pos) {
		RouteListAdapter a = (RouteListAdapter) list.getAdapter();
		a.removeItem(pos);
		a.notifyDataSetChanged();
	}

	protected void openCalendar() {
		DialogFragment dlg = new CalendarFragment();
		Bundle args = new Bundle();
		args.putLong(ExtrasConst.DATE_TAG, new Date().getTime());
		dlg.setArguments(args);
		dlg.show(getSupportFragmentManager(), dlg.getClass().getCanonicalName());
	}
}

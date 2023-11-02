package com.grsoft.ads;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ListView;

import com.grsoft.ads.dataobjects.impl.UserOrderImpl;
import com.grsoft.ads.documents.UserOrderDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.gps.GPSUtilNew;

public class UserOrders extends UpdateActivity {
	private UpdateProcess updateProcess;
	private ListView lvUserOrderItems;
	protected LinesCountController linesController;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());
		lvUserOrderItems = (ListView) findViewById(R.id.lvUserOrderItems);
		registerForContextMenu(lvUserOrderItems);
		
		findViewById(R.id.btnNewDoc).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createNewOrder();
			}
		});
		
		findViewById(R.id.btnSent).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sync();
			}
		});
		
		ImageButton btnLines = (ImageButton) findViewById(R.id.btnLines);
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(
				lvUserOrderItems, btnLines, this);
		linesController = linesOnClickListener.getController();
	}

	public int getLayoutId() {
		return R.layout.user_orders;
	}

	public static void open(Context context){
		DocType.setCurDoc(UserOrderDoc.instance());
		Intent intent = new Intent(context, UserOrders.class);
		context.startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.user_order_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		
		if (itemId == R.id.itCreate)
			createNewOrder();
		else if (itemId == R.id.itUpdate)
    		sync();

		return true;
	}

	protected void sync() {
		updateProcess = UpdateProcess.createProcess(this, this);
		updateProcess.setPostWorker(new Runnable() {
			
			@Override
			public void run() {
				((UserOrderDoc)UserOrderDoc.instance()).setListControls(
						UserOrders.this, lvUserOrderItems, linesController);
				getSharedPreferences(
						Setting.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).
							edit().putBoolean(Setting.CLEAR, false).commit();
			}
		});
		updateProcess.execute((Void[])null);
	}

	private void createNewOrder() {
		UserOrderImpl userOrderImpl = new UserOrderImpl();
		userOrderImpl.init(this, "", GPSUtilNew.getLastKnownLocation());
		OrderTabActivity.open(this, userOrderImpl.write());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		((UserOrderDoc)UserOrderDoc.instance()).setListControls(
				this, lvUserOrderItems, linesController);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.order_detail_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int position = ((AdapterContextMenuInfo)
				item.getMenuInfo()).position;
		int itemId = item.getItemId();
		
		if (itemId == R.id.itCreate)
			createNewOrder();
		else if (itemId == R.id.itEdit)
			lvUserOrderItems.performItemClick(null, position, 0);
		else if (itemId == R.id.itDel)
			((UserOrderDoc)UserOrderDoc.instance()).deleteItem(position);
			
		return true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (updateProcess != null)
    		updateProcess.cancel(false);
	}
}

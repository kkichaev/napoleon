package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.util.MenuHandler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;


public class NapoleonEx extends Napoleon {
	private ImageButton btnAct;
	private ImageButton btnNew;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.ivMenu).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showDialog(DLG_MAIN_MENU);
			}
		});
		
		btnAct = (ImageButton) findViewById(R.id.btnAct);
		btnNew = (ImageButton) findViewById(R.id.btnNew);
		
		btnAct.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) { openWarehouse(R.id.tvAct); } });
		
		btnNew.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) { openWarehouse(R.id.tvNew); } });
	}
	
	@Override
	protected int getResourceID() { return R.layout.mainex; }
	
	private void openWarehouse(int id){
		Intent intent = new Intent(this, Warehouse.activity);
		intent.putExtra(WarehouseEx.CTRL_ID, id);
		startActivity(intent);
	}
	
	@Override
	protected Dialog createMenuDlg(String title, final ArrayList<MenuHandler> items) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
		builder.setTitle(title);
						
		int idx = 0;
		CharSequence[] titles = new CharSequence[items.size()];
		
		for(MenuHandler mh : items)
			titles[idx++] = mh.name;
		
		builder.setItems(titles, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which){
					items.get(which).handler.run();
			}
		});
		
		return builder.create();
	}
}
	

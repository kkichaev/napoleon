package com.grsoft.manager;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.OrdDlv;
import com.grsoft.dataobjects.OrderRequest;
import com.grsoft.util.Util;


public class ManagerEx extends Manager {
	Button btnOrder;
	@Override
	protected int getContentViewID() {	return R.layout.managerex;	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inflateView();
		initView();
	}

	private void initView() {
		btnOrder.setOnClickListener(orderClick());
	}
	
	@Override
	public void updateCtrl(boolean enabled) {
		super.updateCtrl(enabled);
		btnOrder.setEnabled(enabled);
	}
	
	private OnClickListener orderClick() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				SyncOrdDlv sync = new SyncOrdDlv(v.getContext(), new UpdateCtrl() {
					@Override public void updateCtrl(boolean enabled) { ManagerEx.this.updateCtrl(enabled);}
					@Override public void onFinish(boolean success) { OrderReviewEdit.open(ManagerEx.this); }					
				});
				
				sync.start();
			}
		};
	}

	private void inflateView() {
		btnOrder = (Button)findViewById(R.id.btnOrder);
	}
	
	private int getWaitedOrderCount(){
		int result = 0;
		Cursor c = null;
		
		try{
			DbWriter.checkDBTable(OrdDlv.class);
			DbWriter.checkDBTable(OrderRequest.class);
			SQLiteDatabase db = DataBaseManager.getDataBase();
			final String query = "select count(*) from orddlv where created >= ? and not created in (select [order] from orderrequest)";
			
			c = db.rawQuery(query, new String[] { Long.toString(Util.getDate().getTime())});
			
			if (c != null && c.moveToFirst())
				result = c.getInt(0);
					
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (c != null)
				c.close();
		}
		
		return result;
	}
	
	private void updateOrderButton(){
		int cnt = getWaitedOrderCount();
		String text = "";
		
		if (cnt == 0)
			text = getString(R.string.orderwaitzero);
		else
			text = getString(R.string.orderwaitbold, cnt);
		
		btnOrder.setText(Html.fromHtml(text));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateOrderButton();
	}
	
	@Override
	public void onFinish(boolean success) {
		super.onFinish(success);
		runOnUiThread(new Runnable() { @Override public void run() { updateOrderButton(); }});
	}
	
	@Override
	protected void addResultHitching(List<Hitching> repResult) {
		repResult.add(new Hitching(OrdDlv.class));
	}
}

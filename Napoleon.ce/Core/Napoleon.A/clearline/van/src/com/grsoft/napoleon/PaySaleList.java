package com.grsoft.napoleon;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.PaySale;
import com.grsoft.dataobjects.impl.PaySaleImpl;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.gps.GPSUtilNew;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;


public class PaySaleList extends FragmentActivity 
	implements OnClickListener, DataSetNotify, OnItemClickListener{
	
	private ListView list;
	private View btnNewDoc;
	
	public static void open(Context context){
		Intent i = new Intent(context, PaySaleList.class);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.paysale);
		list = (ListView) findViewById(R.id.list);
		btnNewDoc = findViewById(R.id.btnNewDoc);
		
		btnNewDoc.setOnClickListener(this);
		list.setAdapter(new PaySaleListAdapter(this));
		list.setDividerHeight(0);
		list.setOnItemClickListener(this);
		
		registerForContextMenu(list);
	}

	@Override
	public void onClick(View v) {
		PaySaleImpl doc = new PaySaleImpl();
		doc.init(this, "", GPSUtilNew.getLastKnownLocation());
		long rowid = doc.getRowid();
		openEditDlg(rowid);
	}

	protected void openEditDlg(long rowid) {
		DialogFragment f = new PaySaleEdit();
		Bundle args = new Bundle();
		args.putLong(ExtrasConst.DOC_ROW_ID_STR, rowid);
		f.setArguments(args);
		f.show(getSupportFragmentManager(), f.getClass().getCanonicalName());
	}
	
	@Override
	public void notifyDataSetChanged() {
		PaySaleListAdapter a = (PaySaleListAdapter) list.getAdapter();
		
		if(a != null){
			a.load();
			a.notifyDataSetChanged();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View arg1, int pos, long arg3) {
		PaySale i = (PaySale) adapter.getItemAtPosition(pos);
		openEditDlg(i.created.getTime());
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.paysale_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		boolean result = false;
		
		AdapterContextMenuInfo i = (AdapterContextMenuInfo) item.getMenuInfo();
		PaySaleListAdapter a = (PaySaleListAdapter) list.getAdapter();
		PaySale s = (PaySale) a.getItem(i.position);
		
		switch(item.getItemId()){
		case R.id.itAdd:
			btnNewDoc.performClick();
			result = true;
			break;
		case R.id.itEdit:
			openEditDlg(s.created.getTime());
			result = true;
			break;
		case R.id.itDelete:
			delete(s);
			result = true;
			break;
		default:
			result = false;
		}
		
		return result;
	}

	private boolean delete(PaySale ps) {
		boolean result = false;
		DbWriter wr = new DbWriter();
		result = wr.deleteRecord(ps, ps.created.getTime());
		
		if(result){
			PaySaleListAdapter a = (PaySaleListAdapter) list.getAdapter();
			
			if(a != null){
				a.load();
				a.notifyDataSetChanged();
			}
		}
		
		return result;
	}
}


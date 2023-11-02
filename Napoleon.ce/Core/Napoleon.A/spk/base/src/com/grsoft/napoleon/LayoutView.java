package com.grsoft.napoleon;

import com.grsoft.database.LayoutItem;
import com.grsoft.dataobjects.impl.LayoutImpl;
import com.grsoft.util.ExtrasConst;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;


public class LayoutView extends Activity {
	private LayoutImpl doc = new LayoutImpl();
	private ListView list;
	private TextView tvTitle;
	private LayoutAdapter adapter; 
	private View btnNext;
	private View btnPrev;
	
	public static void open(Context context, long rowid){
		Intent i = new Intent(context, LayoutView.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layoutview);
		
		list = (ListView) findViewById(R.id.list);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		btnPrev = findViewById(R.id.btnPrev);
		btnNext = findViewById(R.id.btnNext);
		
		btnPrev.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { adapter.prev();} });
		btnNext.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { adapter.next();} });
		
		list.setDividerHeight(0);
		
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		doc.close();
		
		adapter = new LayoutAdapter(this, doc);
		adapter.registerDataSetObserver(new DataSetObserver() {
			@Override public void onChanged() { tvTitle.setText(adapter.getGroupText()); }
		});
		
		list.setAdapter(adapter);
		list.setOnItemClickListener(itemClick);
		tvTitle.setText(adapter.getGroupText());
	}
	
	private OnItemClickListener itemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			LayoutItem i = (LayoutItem) parent.getItemAtPosition(position);
			
			if( i != null)
				doc.editItem(i.itid, view.getContext());
		}};
		
	private BroadcastReceiver dataChanged = new BroadcastReceiver(){
		@Override public void onReceive(Context context, Intent intent) {	adapter.notifyDataSetChanged();}};
		
	@Override
	protected void onStart() {
		super.onStart();
		
		registerReceiver(dataChanged, new IntentFilter(LayoutImpl.DATA_CHANGED_ACTION));
	}	
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (isFinishing())
			unregisterReceiver(dataChanged);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		doc.read(doc.getRowid(), false);
		doc.close();
		adapter.loadData(doc);
		adapter.notifyDataSetChanged();
	}
}

package com.grsoft.napoleon;

import com.grsoft.dataobjects.LayoutItem;
import com.grsoft.dataobjects.VisitEx;
import com.grsoft.dataobjects.impl.LayoutImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.LayoutDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.ExtrasConst;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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

import java.util.ArrayList;
import java.util.List;


public class LayoutView extends Activity implements SendResultListener {
	private LayoutImpl doc = new LayoutImpl();
	private ListView list;
	private TextView tvTitle;
	private LayoutAdapter adapter; 
	private View btnNext;
	private View btnPrev;
	private View btnSend;
	private View btnFinish;

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
		btnFinish = findViewById(R.id.btnFinish);
		btnSend = findViewById(R.id.btnSend);
		
		btnPrev.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { adapter.prev();} });
		btnNext.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { adapter.next();} });
		btnFinish.setOnClickListener((x)->showDialog(R.id.finish_work_dlg));
		btnSend.setOnClickListener(this::send);
		
		list.setDividerHeight(0);
		
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		doc.close();
		
		adapter = new LayoutAdapter(this, doc,(x)->updateView(x));
		adapter.registerDataSetObserver(new DataSetObserver() {
			@Override public void onChanged() { tvTitle.setText(adapter.getGroupText()); }
		});
		
		list.setAdapter(adapter);
		list.setOnItemClickListener(itemClick);
		tvTitle.setText(adapter.getGroupText());
	}

	private void send(View view) {
		List<DocExportListener> sends = DocType.getDocuments(true, true);
		new DocumentSender(this, findViewById(R.id.btnSend), sends, this).execute((Void[])null);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.finish_work_dlg)
			return  createFinishWorkDlg();
		return super.onCreateDialog(id);
	}

	private Dialog createFinishWorkDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.question);
		builder.setMessage(R.string.ask_to_finish_work);
		builder.setPositiveButton(R.string.ok, (w,e)->finishWork());
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}

	private void finishWork() {
		doc.getData().inwork = 0;
		doc.write();
		doc.close();
		finish();
	}

	private void updateView(boolean last) {
		btnSend.setVisibility(last? View.VISIBLE : View.INVISIBLE);
		btnFinish.setVisibility(last? View.VISIBLE : View.INVISIBLE);
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

	@Override
	public void postSendExecute(boolean result) {

	}
}

package com.grsoft.napoleon;

import com.grsoft.dataobjects.InvFrg;
import com.grsoft.dataobjects.InvFrgItem;
import com.grsoft.dataobjects.impl.InvFrgImpl;
import com.grsoft.dataobjects.impl.InvFrgSt1Impl;
import com.grsoft.dataobjects.impl.InvFrgSt2Impl;
import com.grsoft.dataobjects.impl.InvFrgSt3Impl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.InvFrgDoc;
import com.grsoft.napoleon.documents.InvFrgSt1Doc;
import com.grsoft.napoleon.documents.InvFrgSt2Doc;
import com.grsoft.napoleon.documents.InvFrgSt3Doc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.gps.GPSUtilNew;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class InvFrgEdit extends Activity{
	private ListView list;
	private InvFrgImpl doc = new InvFrgImpl();
	private View btnSt1;
	private View btnSt2;
	private View btnSt3;
	boolean creating = true;
	
	public static void open(Context context, long rowid){
		Intent i = new Intent(context, InvFrgEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invfrgedit);
		
		list = (ListView) findViewById(R.id.list);
		btnSt1 = findViewById(R.id.btnSt1);
		btnSt2 = findViewById(R.id.btnSt2);
		btnSt3 = findViewById(R.id.btnSt3);
		
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		doc.close();
		
		list.setAdapter(new Adapter());

		if(doc.isEditable()){
			btnSt1.setOnClickListener(docSt1Click);
			btnSt2.setOnClickListener(docSt2Click);
			btnSt3.setOnClickListener(docSt3Click);
			list.setOnItemClickListener(itemClick);
		}
	}
	
	OnClickListener docSt1Click = new OnClickListener() {
		@Override
		public void onClick(View v) {
			DocType.setCurDoc(InvFrgSt1Doc.theInstance());
			InvFrgSt1Impl i = new InvFrgSt1Impl();
			boolean status = false;
			
			if (doc.getData().st1_state == InvFrg.INITED){
				status = i.read(doc.getData().st1.getTime());
				i.close();
			}else{
				status = i.init(v.getContext(), doc.getId(), GPSUtilNew.getLastKnownLocation(), doc.getData().created);
				
				if(status){
					doc.getData().st1_state = InvFrg.INITED;
					doc.getData().st1 = i.getData().created;
					doc.write();
					doc.close();
				}
			}
			
			i.open(v.getContext());
		}
	}; 
	
	OnClickListener docSt2Click = new OnClickListener() {
		@Override
		public void onClick(View v) {
			DocType.setCurDoc(InvFrgSt2Doc.theInstance());
			InvFrgSt2Impl i = new InvFrgSt2Impl();
			boolean status = false;
			
			if (doc.getData().st2_state == InvFrg.INITED){
				status = i.read(doc.getData().st2.getTime());
				i.close();
			}else{
				status = i.init(v.getContext(), doc.getId(), GPSUtilNew.getLastKnownLocation(), doc.getData().created);
				
				if(status){
					doc.getData().st2_state = InvFrg.INITED;
					doc.getData().st2 = i.getData().created;
					doc.write();
					doc.close();
				}
			}
			
			i.open(v.getContext());
		}
	}; 
	
	OnClickListener docSt3Click = new OnClickListener() {
		@Override
		public void onClick(View v) {
			DocType.setCurDoc(InvFrgSt3Doc.theInstance());
			InvFrgSt3Impl i = new InvFrgSt3Impl();
			boolean status = false;
			
			if (doc.getData().st3_state == InvFrg.INITED){
				status = i.read(doc.getData().st3.getTime());
				i.close();
			}else{
				status = i.init(v.getContext(), doc.getId(), GPSUtilNew.getLastKnownLocation(), doc.getData().created);
				
				if(status){
					doc.getData().st3_state = InvFrg.INITED;
					doc.getData().st3 = i.getData().created;
					doc.write();
					doc.close();
				}
			}
			
			i.open(v.getContext());
		}
	}; 
	
	OnItemClickListener itemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			InvFrgItem i = (InvFrgItem) parent.getItemAtPosition(position);
			i.prez ^= 1;
			doc.write();
			doc.close();
			
			((BaseAdapter)parent.getAdapter()).notifyDataSetChanged();
		}};
	
	private class Adapter extends BaseAdapter{
		@Override public int getCount() { return doc.getData().items.size();}
		@Override public Object getItem(int position) {	return doc.getData().items.get(position); }
		@Override public long getItemId(int position) {	return 0; }

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if(view == null)
				view = View.inflate(InvFrgEdit.this, R.layout.invfrgitem, null);
			
			InvFrgItem i = (InvFrgItem) getItem(position);
			
			TextView tv = (TextView) view.findViewById(R.id.tvNumber);
			tv.setText(i.number);
			
			ImageView iv = (ImageView) view.findViewById(R.id.ivCheck);
			iv.setImageResource(i.prez == 0 ? R.drawable.btn_check_off : R.drawable.btn_check_on);
			
			return view;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		DocType.setCurDoc(InvFrgDoc.instance());
		
		if(!creating)
			doc.read(doc.getRowid(), false);
		
		creating = false;
	}
}

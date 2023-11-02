package com.grsoft.napoleon;

import com.grsoft.dataobjects.ConcurentItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgUnitable;
import com.grsoft.dataobjects.RemnantsEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.network.BaseSimpleActivity;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;


public class CreateRemnants extends BaseSimpleActivity{
	public static Class<? extends Activity> activity = CreateRemnants.class;
	public Spinner spUnits;
	public ListView list;
	public View btnOk;
	public View btnCancel;
	public boolean editMode = false;
	protected RemnantsImpl document;
	private OrgImpl org;
	private Adapter adapter;
	
	public static void open(Context ctx, Document<?> doc) {	openW(ctx, doc, false);	}
	
	public static void openEdit(Context ctx, Document<?> doc) {	openW(ctx, doc, true);}
	
	public static void openW(Context ctx, Document<?> doc, boolean edit) {
		Intent i = new Intent(ctx, activity);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(ExtrasConst.EDIT_MODE_STR, edit);
		ctx.startActivity(i);
	}

	@Override
	protected int getLayoutID() { return R.layout.createremnants; }
	
	@Override
	protected void inflateView() {
		list = (ListView) findViewById(R.id.list);
		spUnits = (Spinner) findViewById(R.id.spUnits);
		
		btnOk = findViewById(R.id.btnOK);
		btnCancel = findViewById(R.id.btnCancel);
	}
	
	@Override
	protected void init() {
		Bundle b = getIntent().getExtras();
		editMode = b.getBoolean(ExtrasConst.EDIT_MODE_STR);
		long id = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		document = new RemnantsImpl();
		document.read(id);
		
		org = new OrgImpl();
		org.read("id", document.getId());
		OrgEx oe = (OrgEx)org.getData();
		
		if( !editMode )
			OrgUnitHelper.initDoc((OrgUnitable) document.getData(), oe);
		
		adapter = new Adapter();
	}
	
	@Override
	protected void initView() {
		list.setAdapter(adapter);
		btnOk.setOnClickListener(okClick());
		btnOk.setEnabled(document.isEditable());
		btnCancel.setOnClickListener(cancelClick());
		
		OrgUnitHelper.initUnits(spUnits, (OrgUnitable) document.getData(), (OrgEx)org.getData());
	}
	
	private OnClickListener cancelClick() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!editMode) {
					if( document.getData().items == null || document.getData().items.size() == 0 )
						document.delete();
				}
				
				finish();
			}
		};
	}

	private OnClickListener okClick() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				save();
				finish();
				
				if(!editMode)
					Warehouse.open(v.getContext(), document, false);
			}
		};
	}

	private void save() {
		ConcurentItem my = (ConcurentItem) adapter.getItem(0);
		RemnantsEx r = (RemnantsEx) document.getData();
		r.ourgrkqty = my.grk;
		r.ourvtrqty = my.vtr;
		r.ourcmnqty = my.cmn;
		
		ConcurentItem cn = (ConcurentItem) adapter.getItem(1);
		r.cncgrkqty = cn.grk;
		r.cncvtrqty = cn.vtr;
		r.cnccmnqty = cn.cmn;
		
		document.write();
	}

	private OnClickListener inputClick() {
		return new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				InputNumberDlg.open(v.getContext(), new InputNumber() {
					
					@Override public boolean useComma() { return !Features.INTEGER_INPUTS_QTY; }
					@Override public boolean replaceCommaToPlus() { return Features.REPLACE_COMMA_TO_PLUS; }
					
					@Override
					public void applayInput(int value, Object... params) {
						
						if (!document.isEditable())
							return;
						
						ConcurentItem i =  (ConcurentItem) v.getTag();
						
						int c =  (Integer) v.getTag(R.id.clmn);
						
						if(c == 0)
							i.grk = value;
						else
							i.vtr = value;
						
						i.cmn = i.grk + i.vtr;
						
						adapter.notifyDataSetChanged();
					}

					@Override
					public int getValue() {
						int c =  (Integer) v.getTag(R.id.clmn);
						ConcurentItem i =  (ConcurentItem) v.getTag();
						
						if(c == 0)
							return i.grk;
						else 
							return i.vtr;
					}
				});
				
			}
		};
	}

	@Override
	protected void onPause() {
		super.onPause();
		document.close();
	}
	
	class Adapter extends BaseAdapter{
		private final int MYROWSZ = 2; 
		private ConcurentItem myitem = new ConcurentItem();
		private ConcurentItem cncitem = new ConcurentItem();
		
		
		public Adapter() {
			RemnantsEx r = (RemnantsEx) document.getData();
			myitem.name = getString(R.string.our);
			myitem.cmn = r.ourcmnqty;
			myitem.grk = r.ourgrkqty;
			myitem.vtr = r.ourvtrqty;
			
			cncitem.name = getString(R.string.concurent);
			cncitem.cmn = r.cnccmnqty;
			cncitem.grk = r.cncgrkqty;
			cncitem.vtr = r.cncvtrqty;
					
		}
		@Override
		public int getCount() { return ((RemnantsEx)document.getData()).cncs.size() + MYROWSZ; }

		@Override
		public Object getItem(int position) {
			if(position == 0)
				return myitem;
			else if (position == 1)
				return cncitem;
			else
				return ((RemnantsEx)document.getData()).cncs.get(position - MYROWSZ);	
		}

		@Override
		public long getItemId(int position) { return 0;	}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if(view == null)
				view = View.inflate(CreateRemnants.this, R.layout.createremnrow, null);
			
			ConcurentItem c = (ConcurentItem) getItem(position);
			TextView tv = (TextView) view.findViewById(R.id.tvName);
			tv.setText(c.name);
			
			tv = (TextView) view.findViewById(R.id.tvGrg);
			tv.setText(Util.IntToScaleStr(c.grk, Consts.QTY_SCALE));
			setViewInfo(tv, c, 0);
			
			tv = (TextView) view.findViewById(R.id.tvVtr);
			tv.setText(Util.IntToScaleStr(c.vtr, Consts.QTY_SCALE));
			setViewInfo(tv, c, 1);
			
			tv = (TextView) view.findViewById(R.id.tvCmn);
			tv.setText(Util.IntToScaleStr(c.cmn, Consts.QTY_SCALE));
			
			return view;
		}
		
		private void setViewInfo(View v, ConcurentItem i, int cid){
			v.setTag(i);
			v.setTag(R.id.clmn,cid);
			v.setOnClickListener(inputClick());
		}
	}
}

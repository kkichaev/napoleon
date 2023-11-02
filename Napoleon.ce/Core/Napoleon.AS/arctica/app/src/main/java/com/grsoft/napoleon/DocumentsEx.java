package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgParam;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.OrgInfoClickListener;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

public class DocumentsEx extends Documents {
	
	@Override
	protected String orgInfo(Org o) {
		String txt = o.name;
		
		OrgEx oe = (OrgEx)o;
		txt += "\nОтсрочка: " + Integer.toString(oe.delay);
		if( oe.refregerators > 0 )
			txt += ". Имеется холодильное оборудование";
		return txt;
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( docType == DebtDoc.instance() ) {
			DocType.setCurDoc(docType);
			DebetView.open(this, org.getData().id);
			finish();
		} else
			super.adjustViewForDocType(docType);
	}

	@Override
	protected OnClickListener createInfoClickListener() {
		return new OrgInfoEx(org.getData(), getContactViewid(), this);
	}
	
	class OrgInfoEx extends OrgInfoClickListener {
		public OrgInfoEx(Org o, int contactViewId, ContactViewChanger changer) {
			super(o, contactViewId, changer);
		}

		@Override
		protected void adjustDialogView(View view) {
			super.adjustDialogView(view);
			ListView lvp = (ListView) view.findViewById(R.id.lvParams);
			lvp.setAdapter(new ParamsAdapter());
			
			TabHost tb = (TabHost)view.findViewById(android.R.id.tabhost);
			tb.setup();
			TabHost.TabSpec ts;
			ts = tb.newTabSpec("Телефон").setIndicator("Телефон").setContent(R.id.ll1);
			tb.addTab(ts);
			ts = tb.newTabSpec("Парам.").setIndicator("Парам.").setContent(R.id.ll2);
			tb.addTab(ts);
		}
		
		@Override
		protected int getContentView() {
			return R.layout.org_detail_info_ex;
		}
	}
	
	class ParamsAdapter extends BaseAdapter {
		OrgEx oe = (OrgEx) org.getData();
		
		@Override
		public int getCount() { return oe.params.size(); }

		@Override
		public Object getItem(int index) {
			return (index < getCount()) ? oe.params.get(index) : null;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int index, View view, ViewGroup arg2) {
			OrgParam param = (OrgParam) getItem(index);
			if( view == null )
				view = View.inflate(DocumentsEx.this, R.layout.param_row, null);
			
			if( param != null ) {
				TextView tv;
				tv = (TextView)view.findViewById(R.id.tvName);
				tv.setText(param.key);

				tv = (TextView)view.findViewById(R.id.tvValue);
				tv.setText(param.value);
			}
			return view;
		}
		
	}
}

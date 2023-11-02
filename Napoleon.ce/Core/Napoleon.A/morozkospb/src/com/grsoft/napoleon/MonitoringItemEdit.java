package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.dataobjects.Catalog;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Monitoring;
import com.grsoft.dataobjects.MonitoringItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.MonitoringImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.MerchDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.KeypadHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MonitoringItemEdit extends Activity {
	EditText edCost;
	EditText edCost1;
	EditText edCost2;
	Spinner spCatalog;
	TextView tvDate1;
	TextView tvDate2;
	MonitoringItem item = new MonitoringItem();
	MonitoringImplBase<?> doc;
	
	public static void open(Context context, long rowid, String itemid) {
		Intent i = new Intent(context, MonitoringItemEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		i.putExtra(ExtrasConst.ORG_ID_STR, itemid);
		
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.inputnumbermonitoring);
		
		edCost = (EditText) findViewById(R.id.edCount);
		edCost1 = (EditText) findViewById(R.id.edCost1);
		edCost2 = (EditText) findViewById(R.id.edCost2);
		spCatalog = (Spinner) findViewById(R.id.spCatalog);
		tvDate1 = (TextView) findViewById(R.id.tvDate1);
		tvDate2 = (TextView) findViewById(R.id.tvDate2);
		
		KeypadHelper kh = new KeypadHelper(this, R.id.edCount);
		for(EditText tv : new EditText[] {edCost, edCost1, edCost2})
			edInit(tv, kh);
		
		doc = (MonitoringImplBase<?>) DocType.getCurDoc().create();
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		item = (MonitoringItem) doc.findItem(getIntent().getStringExtra(ExtrasConst.ORG_ID_STR));
		
		spCatalog.setAdapter(new CatalogAdapter());
		
		if (item == null) {
			item = new MonitoringItem();
			item.id = getIntent().getStringExtra(ExtrasConst.ORG_ID_STR);
			item.date1 = Util.getDate();
			item.date2 = Util.getDate();
			
			((Monitoring)doc.getData()).items.add(item);
		}
		
		tvDate1.setOnClickListener(dateClick);
		tvDate2.setOnClickListener(dateClick);
		
		refreshDate();
		
		findViewById(R.id.btnOK).setOnClickListener(okclick);
		
		edCost.setText(Util.IntToScaleStr(item.cost, Consts.SUM_SCALE));
		edCost1.setText(Util.IntToScaleStr(item.cost1, Consts.SUM_SCALE));
		edCost2.setText(Util.IntToScaleStr(item.cost2, Consts.SUM_SCALE));
		
		for (int i = 0; i < spCatalog.getCount(); i++) {
			Catalog c = (Catalog) spCatalog.getItemAtPosition(i);
			
			if (c.id.equals(item.catID)) {
				spCatalog.setSelection(i, true);
				break;
			}
		}
			
	}
	
	OnClickListener dateClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			Intent i = new Intent(MonitoringItemEdit.this, CalendarActivity.class);
			i.putExtra(ExtrasConst.DATE_TAG, getItemDate(id));
			startActivityForResult(i, id);
		}
	};
	
	OnClickListener okclick  = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int cost = Util.StrToScale(edCost.getText().toString().trim(), Consts.SUM_SCALE);
			int cost1 = Util.StrToScale(edCost1.getText().toString().trim(), Consts.SUM_SCALE);
			int cost2 = Util.StrToScale(edCost2.getText().toString().trim(), Consts.SUM_SCALE);
			Catalog c = (Catalog) spCatalog.getSelectedItem();
			
			if (cost2 != item.cost2 && c.id.length() == 0)
				Toast.makeText(v.getContext(), R.string.select_catalog, Toast.LENGTH_SHORT).show();
			else {
				item.cost = cost;
				item.cost1 = cost1;
				item.cost2 = cost2;
				item.catID = c.id;
				
				doc.write();
				doc.close();
				
				MerchDoc.instance().refreshDocSum(doc.getId());
				finish();
			}
			
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Date newDate = new Date(ct);
			
			if (requestCode == R.id.tvDate1)
				item.date1 = newDate;
			else if (requestCode == R.id.tvDate2)
				item.date2 = newDate;
			
			refreshDate();
		}
	}

	
	private void refreshDate() {
		tvDate1.setText(Util.simpleDateFormat.format(item.date1));
		tvDate2.setText(Util.simpleDateFormat.format(item.date2));
	}

	private void edInit(EditText ed, final KeypadHelper nh) {
		ed.setInputType(InputType.TYPE_NULL);
		ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {				
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if( arg1 ) {
					nh.setTargetID(arg0.getId());
					((EditText)arg0).selectAll();
				}
			}
		});
	}
	
	protected long getItemDate(int id) {
		long res = 0;
		
		if (id == R.id.tvDate1)
			res = item.date1.getTime();
		
		if (id == R.id.tvDate2)
			res = item.date2.getTime();
		
		return res;
	}

	class CatalogAdapter extends BaseAdapter{
		private List<Catalog> data = new ArrayList<Catalog>();
		
		public CatalogAdapter() {
			
			OrgImpl org = new OrgImpl();
			org.read("id", doc.getId());
			String where = String.format("idCompany='%s'", ((OrgEx)org.getData()).idCompany);
			
			DataTraveler.travel(Catalog.class, new DataTraveler.Travel<Catalog>(true) {

				@Override
				public boolean travel(DataTraveler<Catalog> item) {
					data.add(item.data);
					return true;
				}}, where);
			
			data.add(0, new Catalog());
		}
		
		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(MonitoringItemEdit.this, R.layout.catalogrow, null);
			
			Catalog c = (Catalog) getItem(position);
			
			if (c.id.length() == 0)
				for(int i = 0; i < ((LinearLayout)view).getChildCount(); i++) {
					View v = ((LinearLayout)view).getChildAt(i);
					v.setVisibility(View.INVISIBLE);
				}
			else {
				TextView tv = (TextView) view.findViewById(R.id.tvName);
				tv.setText(c.name);
				
				tv = (TextView) view.findViewById(R.id.tvDate1);
				tv.setText(Util.simpleDateFormat.format(c.start));
				
				tv = (TextView) view.findViewById(R.id.tvDate2);
				tv.setText(Util.simpleDateFormat.format(c.finish));
			}
			return view;
		}
		
		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			return getView(position, convertView, parent);
		}
	}
}

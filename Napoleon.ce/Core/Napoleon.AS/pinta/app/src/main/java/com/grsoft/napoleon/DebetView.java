package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class DebetView extends DocumentsBase {
	
	public static void open(Context ctx, String orgId) {
		Intent i = new Intent(ctx, DebetView.class);
		i.putExtra(ExtrasConst.ORG_ID_STR, orgId);
		ctx.startActivity(i);
	}
	
	@Override protected int getContentViewID() { return R.layout.debet_docs; }
	
	DebtAdapter da = new DebtAdapter();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ExpandableListView lv = (ExpandableListView)findViewById(R.id.lvDocs);
		lv.setAdapter(da);
		
		lv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				DocData dd = (DocData) da.getChild(groupPosition, childPosition);
				if( dd != null && dd.isDelivery ) {
					DeliveryImpl di = new DeliveryImpl();
					di.read(dd.rowid);
					di.close();
					di.open(DebetView.this);
				}
				return false;
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		da.load(org.getData().id);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(ExtrasConst.ORG_ID_STR, org.getData().id);
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( docType != DebtDoc.instance() ) {
			DocType.setCurDoc(docType);
			Documents.open(this, org.getData());
			finish();
		} else
			super.adjustViewForDocType(docType);
	}
	
	class DebtAdapter extends BaseExpandableListAdapter {
		
		PayList data = new PayList();
		
		DebtAdapter() {
		}
		
		public void load(String orgId) {
			StringBuilder sb = new StringBuilder();

			Map<String, String> firms = new HashMap<>();
			List<KeyValue> vf = new ArrayList<>();

			ConfigImpl ci = new ConfigImpl();
			ci.getValue(sb, "Организация");

			DialogHelper.makeListWithKey(sb.toString(), vf, "");
			for(KeyValue kv : vf) {
				firms.put(kv.key.toString(), kv.value.toString());
			}

			data.clear();
			
			DocList dl = DebtDoc.instance().docList(orgId);
			for(int i=0; i<dl.getCount(); i++ ) {
				DocData dd;
				String payType;
				
				Document<?> doc = dl.get(i);
				if( doc instanceof DeliveryImpl ) {
					DeliveryEx d = (DeliveryEx) doc.getData();
					payType = d.firma;
					dd = new DocData(d);
				} else {
					PaymentEx p = (PaymentEx) doc.getData();
					payType = p.firma;
					dd = new DocData(p);
				}
				dd.rowid = doc.getRowid();
				String pt = firms.get(payType);
				if( pt == null) pt = "Организация с кодом '" + payType + "'";
				data.add(dd, pt);
			}
			dl.close();
			data.sort();
			
			notifyDataSetChanged();
		}

		@Override public int getGroupCount() { return data.size(); }

		@Override
		public int getChildrenCount(int groupPosition) {
			PayData pd = (PayData) getGroup(groupPosition);
			return (pd == null) ? 0 : pd.documents.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return (groupPosition < data.size()) ? data.get(groupPosition) : null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			PayData pd = (PayData) getGroup(groupPosition);
			if( pd == null )
				return null;
			
			return (childPosition < pd.documents.size()) ? pd.documents.get(childPosition) : null; 
		}

		@Override public long getGroupId(int groupPosition) { return groupPosition; }

		@Override
		public long getChildId(int groupPosition, int childPosition) { return groupPosition * 10000 + childPosition; }

		@Override public boolean hasStableIds() { return true; }

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			if( convertView == null )
				convertView = View.inflate(DebetView.this, R.layout.debet_group_row, null);
			
			PayData pd = (PayData) getGroup(groupPosition);
			if( pd != null ) {
				TextView tv;
				tv = (TextView)convertView.findViewById(R.id.tvText);
				tv.setText(pd.name);
			
				tv = (TextView)convertView.findViewById(R.id.tvSum);
				tv.setText(Util.IntToScaleStr(pd.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			}
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			if( convertView == null )
				convertView = View.inflate(DebetView.this, R.layout.debet_item_row, null);
			
			DocData dd = (DocData) getChild(groupPosition, childPosition);
			if( dd != null ) {
				int color = (dd.delayedPay) ? Color.RED : Color.BLACK;
				TextView tv;
				tv = (TextView)convertView.findViewById(R.id.tvText);
				tv.setText(dd.number);
				tv.setTextColor(color);

				tv = (TextView)convertView.findViewById(R.id.tvDate);
				String text = Util.simpleDateFormat.format(dd.date);
				if( dd.payDate != null )
					text += "\n" + Util.simpleDateFormat.format(dd.payDate);
				tv.setText(text);
				tv.setTextColor(color);
			
				tv = (TextView)convertView.findViewById(R.id.tvSum);
				tv.setText(Util.IntToScaleStr(dd.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
				tv.setTextColor(color);
			}
			return convertView;
		}

		@Override public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
	}
}

class PayData implements Comparable<PayData> {
	public String name;
	public int sum;
	
	ArrayList<DocData> documents = new ArrayList<DocData>();
	
	void sort() { Collections.sort(documents); }

	@Override
	public int compareTo(PayData another) {
		return name.compareTo(another.name);
	}
}

class PayList extends ArrayList<PayData> {

	private static final long serialVersionUID = 7746543028606665599L;

	void add(DocData dd, String payType) {
		PayData pd = null;
		for(PayData pi : this) {
			if( pi.name.equals(payType) ) {
				pd = pi;
				break;
			}
		}
		if( pd == null ) {
			pd = new PayData();
			add(pd);
		}
		
		pd.documents.add(dd);
		pd.sum += dd.sum;
		pd.name = payType;
	}
	
	void sort() {
		Collections.sort(this);
		for(PayData pd : this) {
			pd.sort();
		}
	}
}

class DocData implements Comparable<DocData> {
	public long rowid;
	public String number;
	public long sum;
	public boolean delayedPay = false; 
	public Date date;
	public Date payDate;
	
	boolean isDelivery;

	public DocData(DeliveryEx d) {
		isDelivery = true;
		sum = d.sumD;
		date = d.date;
		number = d.number;
		
		payDate = d.payDate;
		if( sum > 0 && payDate.compareTo(new Date()) < 0 )
			delayedPay = true;
	}

	public DocData(PaymentEx p) {
		isDelivery = false;
		sum = p.sum;
		date = p.date;
		number = p.number;
	}

	@Override
	public int compareTo(DocData another) {
		int cmp = date.compareTo(another.date);
		if( cmp != 0 )
			return cmp;
		
		if( isDelivery != another.isDelivery )
			return (isDelivery) ? - 1 : 1;
		
		return number.compareTo(another.number);
	}
}
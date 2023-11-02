package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class DebetView extends DocumentsBase {
	
	public static void open(Context ctx, String orgId) {
		Intent i = new Intent(ctx, DebetView.class);
		i.putExtra(ExtrasConst.ORG_ID_STR, orgId);
		ctx.startActivity(i);
	}
	
	@Override protected int getContentViewID() { return R.layout.debet_docs; }
	
	DebtAdapter da = new DebtAdapter();
	Spinner spFilter;

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
		
		spFilter = (Spinner)findViewById(R.id.spFilter);
		spFilter.setOnItemSelectedListener(new OnItemSelectedListener() {
			int lastPos = -1;
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(lastPos == -1)
					lastPos = position;
				else
					da.load(org.getData().id);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
	}
	
	@Override
	protected String orgInfo(Org o) {
		OrgEx oe = (OrgEx)o;
		String info = oe.name;
		info += "\nВремя работы: " + oe.wrkTime;
		info += "\nДолг: " + Util.IntToScaleStr(oe.balance, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		return info;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		da.load(org.getData().id);
		
		ArrayList<String> data = new ArrayList<String>();
		
		for(PayData pd : da.data)
			for(DocData dd : pd.documents)
				if(!data.contains(dd.agent))
					data.add(dd.agent);
		
		Collections.sort(data, new Comparator<String>() {

			@Override
			public int compare(String lhs, String rhs) {
				return lhs.compareToIgnoreCase(rhs);
			}
		});
		
		data.add(0, "<Все>");
		ArrayAdapter<String> filter = new ArrayAdapter<String>
			(this, R.layout.simple_spinner_layout, data);
		
		spFilter.setAdapter(filter);
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
			data.clear();
			
			DocList dl = DebtDoc.instance().docList(orgId);
			for(int i=0; i<dl.getCount(); i++ ) {
				DocData dd;
				String payType;
				
				Document<?> doc = dl.get(i);
				if( doc instanceof DeliveryImpl ) {
					DeliveryEx d = (DeliveryEx) doc.getData();
					payType = d.payType;
					dd = new DocData(d);
				} else {
					PaymentEx p = (PaymentEx) doc.getData();
					payType = p.payType;
					dd = new DocData(p);
				}
				dd.rowid = doc.getRowid();
				
				if(spFilter.getSelectedItemPosition() > 0){
					String flt = (String)spFilter.getSelectedItem();
					
					if(dd.agent.equals(flt))
						data.add(dd, payType);
				}else
					data.add(dd, payType);
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
				tv.setText(Html.fromHtml(dd.number + "<br><i>" + dd.agent + "</i>"));
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
	public int sum;
	public boolean delayedPay = false; 
	public Date date;
	public Date payDate;
	public String agent;
	
	boolean isDelivery;

	public DocData(DeliveryEx d) {
		isDelivery = true;
		sum = (int)d.sumD;
		date = d.date;
		number = d.number;
		agent = d.agent;
		
		payDate = d.payDate;
		if( sum > 0 && payDate.compareTo(new Date()) < 0 )
			delayedPay = true;
	}

	public DocData(PaymentEx p) {
		isDelivery = false;
		sum = (int)p.sum;
		date = p.date;
		number = p.number;
		agent = p.agent;
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
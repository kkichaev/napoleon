package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DeliveryDebt;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.OrgDog;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PaymentDebt;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class DebetView extends DocumentsBase {
	
	String openFirm = null;
	boolean inited = false;
	
	DocTypeBase curDocType;
	
	static final String FIRM_ID = "FirmID";
	
	public static void open(Context ctx, String orgId, String firmId) {
		Intent i = new Intent(ctx, DebetView.class);
		i.putExtra(ExtrasConst.ORG_ID_STR, orgId);
		if(firmId != null && firmId != "")
			i.putExtra(FIRM_ID, firmId);
		ctx.startActivity(i);
	}
	
	@Override protected int getContentViewID() { return R.layout.debet_docs; }
	
	DebtAdapter da = new DebtAdapter();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent i = getIntent();
		openFirm = i.getStringExtra(FIRM_ID);
		
		ExpandableListView lv = (ExpandableListView)findViewById(R.id.lvDocs);
		lv.setAdapter(da);
		
		lv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				DeliveryDebt dd = (DeliveryDebt) da.getChild(groupPosition, childPosition);
				if( dd != null && dd.rowid != ExtrasConst.INVALID_ROWID ) {
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
	public void onBackPressed() {
		super.onBackPressed();
		if(curDocType != null)
			DocType.setCurDoc(curDocType);
	}
	
	@Override
	protected void onResume() {
		if(openFirm != null && openFirm != "") {
			if(curDocType == null)
				curDocType = DocType.getCurDoc();
			if(curDocType != DebtDoc.instance())
				DocType.setCurDoc(DebtDoc.instance());			
		}
		
		super.onResume();
		da.load((OrgEx)org.getData());
		
		if( !inited ) {
			inited = true;
			if(openFirm != null && openFirm != "") {
				int pos = da.findGroup(openFirm);
				if( pos >= 0 ) {
					((ExpandableListView)findViewById(R.id.lvDocs)).expandGroup(pos);
				}
			}
		}
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
		
//		PayList data = new PayList();
		
		List<PaymentDebt> debts = new ArrayList<PaymentDebt>();
		Map<String, List<DeliveryDebt>> docs = new HashMap<String, List<DeliveryDebt>>();
		
		DebtAdapter() {
			
		}
		
		public void load(OrgEx org) {
			debts.clear();
			docs.clear();
			
			final HashMap<String, String> firms = new HashMap<String, String>();
			DataTraveler.travel(Firm.class, new DataTraveler.Travel<Firm>() {

				@Override
				public boolean travel(DataTraveler<Firm> item) {
					firms.put(item.data.id, item.data.name);
					return true;
				}
			}, "");

			String where = "ido='" + org.ido + "'";
			
			final HashMap<String, String> dgv = new HashMap<String, String>();
			DataTraveler.travel(OrgDog.class, new DataTraveler.Travel<OrgDog>() {

				@Override
				public boolean travel(DataTraveler<OrgDog> item) {
					dgv.put(item.data.firm, item.data.name);
					return true;
				}
			}, where);

			DataTraveler.travel(PaymentDebt.class, new DataTraveler.Travel<PaymentDebt>(true) {

				@Override
				public boolean travel(DataTraveler<PaymentDebt> item) {
					String firmName = "<b>" + firms.get(item.data.firm) + "</b>";
					String dg = dgv.get(item.data.firm);
					if(dg != null)
						firmName += " " + dg;

					item.data.name = firmName;
					debts.add(item.data);
					return true;
				}
				
			}, where);
			
			Collections.sort(debts);
			
			where = "id='" + org.id + "'";
			DataTraveler.travel(DeliveryDebt.class, new DataTraveler.Travel<DeliveryDebt>(true) {

				@Override
				public boolean travel(DataTraveler<DeliveryDebt> item) {
					List<DeliveryDebt> fdocs = docs.get(item.data.firm);
					if(fdocs == null) {
						fdocs = new ArrayList<DeliveryDebt>();
						docs.put(item.data.firm, fdocs);
					}
					fdocs.add(item.data);
					return true;
				}
			}, where, "date desc");
			
//			if(org.calcDebet != 0) {
//				distributeSums();
//			}
			
			notifyDataSetChanged();
		}
		
//		private void distributeSums() {
//			Map<String, Long> firmSums = new HashMap<String, Long>();
//			for(PaymentDebt pd : debts)
//				firmSums.put(pd.firm, pd.sum);
//			
//			for(Entry<String, List<DeliveryDebt>> kv : docs.entrySet()) {
//				Long sum = firmSums.get(kv.getKey());
//				Date docDate = null;
//				if(sum == null)
//					sum = (long) 0;
//				
//				for(DeliveryDebt dd : kv.getValue()) {
//					docDate = dd.date;
//						
//					long debtDocSum = dd.sum();
//					if(debtDocSum > sum)
//						debtDocSum = sum;
//					dd.sumD = debtDocSum;
//					sum -= debtDocSum;
//					
//					if(sum <= 0)
//						break;
//				}
//				
//				if( sum > 0) {
//					// make fake deliveery
//					DeliveryDebt dd = new DeliveryDebt();
//					dd.date = docDate == null ? new Date() : new Date(docDate.getTime() - 24 * 3600 * 1000);
//					dd.number = "Нач. сальдо";
//					dd.sumD = sum;
//					dd.rowid = ExtrasConst.INVALID_ROWID;
//					kv.getValue().add(dd);
//				}
//			}
//		}

		public int findGroup(String firmId) {
			for(int i=0; i<debts.size(); i++) {
				if(debts.get(i).firm.equals(firmId))
					return i;
			}
			
			return -1;
		}

		@Override public int getGroupCount() { return debts.size(); }

		@Override
		public int getChildrenCount(int groupPosition) {
			List<DeliveryDebt> fdocs = null;
			PaymentDebt pd = (PaymentDebt) getGroup(groupPosition);
			if(pd != null) {
				fdocs = docs.get(pd.firm); 
			}
			return (fdocs == null) ? 0 : fdocs.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return (groupPosition < debts.size()) ? debts.get(groupPosition) : null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			List<DeliveryDebt> fdocs = null;
			PaymentDebt pd = (PaymentDebt) getGroup(groupPosition);
			if(pd != null) {
				fdocs = docs.get(pd.firm); 
			}
			return (fdocs == null) ? null : fdocs.get(childPosition); 
		}

		@Override public long getGroupId(int groupPosition) { return groupPosition; }

		@Override
		public long getChildId(int groupPosition, int childPosition) { return groupPosition * 10000 + childPosition; }

		@Override public boolean hasStableIds() { return true; }

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			if( convertView == null )
				convertView = View.inflate(DebetView.this, R.layout.debet_group_row, null);
			
			PaymentDebt pd = (PaymentDebt) getGroup(groupPosition);
			if( pd != null ) {
				TextView tv;
				tv = (TextView)convertView.findViewById(R.id.tvText);
				tv.setText(Html.fromHtml(pd.name));
			
				tv = (TextView)convertView.findViewById(R.id.tvSum);
				tv.setText(Util.IntToScaleStr(pd.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			}
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			if( convertView == null )
				convertView = View.inflate(DebetView.this, R.layout.debet_item_row, null);
			
			DeliveryDebt dd = (DeliveryDebt) getChild(groupPosition, childPosition);
			if( dd != null ) {
				String text = "";
				TextView tv;
				
				Date curDate = Util.getDayStart(new Date());				
				boolean overdue = dd.payDate.compareTo(curDate) < 0;
				long daysDiff = (curDate.getTime() - dd.date.getTime()) / (1000l * 24 * 3600);
				
				text = dd.number + "\n" + Util.simpleDateFormat.format(dd.date);
				tv = (TextView)convertView.findViewById(R.id.tvText);
				tv.setText(text);

				tv = (TextView)convertView.findViewById(R.id.tvDate);
				text = Util.simpleDateFormat.format(dd.date);
				tv.setText(text);
				
			
				String sumText = Util.IntToScaleStr(dd.sumD, Consts.SUM_SCALE, Util.DEC_DELIM, false);
				text = sumText;
				if(overdue && dd.sumD > 0)
					text += "\n" + sumText;
				tv = (TextView)convertView.findViewById(R.id.tvSum);
				tv.setText(text);
				
				if(daysDiff < 5)
					tv = (TextView)convertView.findViewById(R.id.tvSum1);
				else if(daysDiff < 16)
					tv = (TextView)convertView.findViewById(R.id.tvSum2);
				else if(daysDiff < 30)
					tv = (TextView)convertView.findViewById(R.id.tvSum3);
				else
					tv = (TextView)convertView.findViewById(R.id.tvSum4);
				
				tv.setText(sumText);					
			}
			return convertView;
		}

		@Override public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
	}
}

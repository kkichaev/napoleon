package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.OrgBalance;
import com.grsoft.dataobjects.OrgBalanceData;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.napoleon.documents.AgentMemoDoc;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

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
				if( dd != null ) {
					DeliveryImpl di = new DeliveryImpl();
					String where = "ido='" + ((OrgEx)org.getData()).ido + "' and number='" + dd.number + "'";
					List<Long> ids = DbReader.readIds(di.getTableName(), where, null);
					if(ids != null && ids.size() > 0) {
						di.read(ids.get(0));
						di.close();
						di.open(DebetView.this);
					}else {
						Toast.makeText(DebetView.this, "Накладной нет на планшете", Toast.LENGTH_LONG).show();
					}
				}
				return false;
			}
		});
		
	}
	
	@Override
	protected DocFilterOnClickListener createDocFilter() {
		List<DocTypeBase> filter = new ArrayList<DocTypeBase>();
		filter.add(DebtDoc.instance());
		filter.add(AgentMemoDoc.instance());

		return new DocFilterOnClickListener(this, false, ScriptDefImpl.canScripting(), filter);
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
	protected void createNewDoc() {
		CreatableDocument<?> doc = (CreatableDocument<?>)AgentMemoDoc.instance().create();
		if( doc.init(this, org.getData().id, GPSUtilNew.getLastKnownLocation(this)))
			doc.open(this);
		doc.close();
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
			
			final Date dueDate = Util.getDate();
			final Map<String, Integer> docDebet = new HashMap<String, Integer>();
			
			final String baseID = orgId.split("\t")[0];
			String where = "id='" + baseID + "'";
			DataTraveler.travel(OrgBalance.class, new DataTraveler.Travel<OrgBalance>() {
				@Override
				public boolean travel(DataTraveler<OrgBalance> item) {
					data.add(item.data);
					return true;
				}
			}, where);

			where = "ido='" + baseID + "' or id in (select id from Org where ido='" + baseID + "')";
			DataTraveler.travel(OrgBalanceData.class, new DataTraveler.Travel<OrgBalanceData>() {
				@Override
				public boolean travel(DataTraveler<OrgBalanceData> item) {
					docDebet.put(item.data.number, item.data.sumD);
//					if(item.data.number.equals(BalanceHelper.START_BALANCE)) {
						data.add(item.data, dueDate);
//					}
					return true;
				}
			}, where);
			
//			DocList dl = DeliveryDoc.instance().docList(null, null, "ido='" + baseID + "'");
//			for(int i=0; i<dl.getCount(); i++ ) {
//				DeliveryEx doc = (DeliveryEx) dl.get(i).getData();
//				data.add(doc, docDebet, dueDate);
//			}
//			dl.close();
			data.sort();
			
			notifyDataSetChanged();
		}

		@Override public int getGroupCount() { return data.size(); }

		@Override
		public int getChildrenCount(int groupPosition) {
			DogovorData pd = (DogovorData) getGroup(groupPosition);
			return (pd == null) ? 0 : pd.documents.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return (groupPosition < data.size()) ? data.get(groupPosition) : null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			DogovorData pd = (DogovorData) getGroup(groupPosition);
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
			
			DogovorData pd = (DogovorData) getGroup(groupPosition);
			if( pd != null ) {
				// special case no docs
				if(pd.endSum < 0 && pd.sum == 0) {
					pd.sum = pd.endSum;
				}
				int color = pd.sum <= 0 ? Color.BLACK : BalanceHelper.getColorFromDueDays(pd.overdueDays);
				TextView tv;
				String text = "";
				tv = (TextView)convertView.findViewById(R.id.tvText);
				tv.setText(pd.name);
				tv.setTextColor(color);
				
				text = Integer.toString(pd.dueDays) + "<br/><b>" + Integer.toString(pd.overdueDays) + "</b>";
				if(pd.unlockDate != null) {
					text = "<b>" + Util.simpleDateFormat.format(pd.unlockDate) + "</b> " + text;
				}
				tv = (TextView)convertView.findViewById(R.id.tvDue);
				tv.setText(Html.fromHtml(text));
				tv.setTextColor(color);


				text = Util.IntToScaleStr(pd.sum - pd.overdueSum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "<br/><b>" + 
						Util.IntToScaleStr(pd.overdueSum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</b>";
				tv = (TextView)convertView.findViewById(R.id.tvSumDetail);
				tv.setText(Html.fromHtml(text));
				tv.setTextColor(color);

				tv = (TextView)convertView.findViewById(R.id.tvSum);
				tv.setText(Util.IntToScaleStr(pd.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
				tv.setTextColor(color);
			}
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			if( convertView == null )
				convertView = View.inflate(DebetView.this, R.layout.debet_item_row, null);
			
			DocData dd = (DocData) getChild(groupPosition, childPosition);
			if( dd != null ) {
				int color = dd.sum <= 0 ? Color.BLACK : BalanceHelper.getColorFromDueDays(dd.overdueDays);
				TextView tv;
				String text = "";

				tv = (TextView)convertView.findViewById(R.id.tvText);
				tv.setText(Html.fromHtml(dd.number));
				tv.setTextColor(color);

				text = dd.overdueDays > 0 ? Integer.toString(dd.overdueDays) : "";
				tv = (TextView)convertView.findViewById(R.id.tvDue);
				tv.setText(Html.fromHtml(text));
				tv.setTextColor(color);

				tv = (TextView)convertView.findViewById(R.id.tvDate);
				text = Util.simpleDateFormat.format(dd.date);
				if( dd.payDate != null )
					text += "<br/>" + Util.simpleDateFormat.format(dd.payDate);
				tv.setText(Html.fromHtml(text));
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

class DogovorData implements Comparable<DogovorData> {
	public String id;
	public String name;
	public int dueDays;
	public int overdueDays;
	public long sum;
	public long overdueSum;
	public long endSum;
	public Date unlockDate;
		
	ArrayList<DocData> documents = new ArrayList<DocData>();
	
	void sort() { Collections.sort(documents); }
	
	public DogovorData(OrgBalance data) {
		id = data.idDog;
		name = data.name;
		sum = 0;
		overdueDays = 0;
		overdueSum = 0;
		dueDays = data.dueDays;
		endSum = data.balance;
		if(data.unlockDate.getTime() > OrgBalance.CHECK_DATE)
			unlockDate = data.unlockDate;
	}
	
	public void add(DocData doc) {
		sum += doc.sum;
		if(doc.overdueDays > 0) {
			overdueSum += doc.sum;
			if(overdueDays < doc.overdueDays)
				overdueDays = doc.overdueDays;
		}
			
		documents.add(doc);
	}

	@Override
	public int compareTo(DogovorData another) {
		return name.compareTo(another.name);
	}
}

class PayList extends ArrayList<DogovorData> {

	private static final long serialVersionUID = 7746543028606665599L;

	void add(OrgBalance blncData) {
		DogovorData pd = new DogovorData(blncData);
		add(pd);
	}
	
	public void add(OrgBalanceData data, Date dueDate) {
		DogovorData pd = null;
		for(DogovorData pi : this) {
			if( pi.id.equals(data.idDog) ) {
				pd = pi;
				DocData dd = new DocData(data, dueDate);
				pd.add(dd);
				break;
			}
		}
	}

	void add(DeliveryEx doc, Map<String, Integer> debetSums, Date dueDate) {
		DogovorData pd = null;
		for(DogovorData pi : this) {
			if( pi.id.equals(doc.dogovor) ) {
				pd = pi;
				break;
			}
		}
		if( pd == null ) {
			OrgBalance ob = new OrgBalance();
			ob.idDog = doc.dogovor;
			ob.name = doc.dogovor;
			pd = new DogovorData(ob);
			add(pd);
		}
		
		DocData dd = new DocData(doc, debetSums, dueDate);
		pd.add(dd);
	}
	
	void sort() {
		Collections.sort(this);
		for(DogovorData pd : this) {
			pd.sort();
		}
	}
}

class DocData implements Comparable<DocData> {
	public String number;
	public long sum;
	public Date date;
	public Date payDate;
	public int overdueDays;
	
	public DocData(DeliveryEx d, Map<String, Integer> debetSums, Date dueDate) {
		Integer val = debetSums.get(d.number);
		sum = (val == null) ? 0 : val;

		date = d.date;
		number = d.number;
		payDate = d.payDate;
		overdueDays = 0;
		
		if( sum > 0 ) {
			long pd = payDate.getTime();
			long cd = dueDate.getTime();
			if(pd < cd) {
				overdueDays = (int)((cd - pd) / (24 * 3600 * 1000));
			}
		}
	}
	
	public DocData(OrgBalanceData data, Date dueDate) {
		sum = data.sumD;
		number = data.number;
		payDate = data.payDate;
		date = data.date;
		overdueDays = 0;
		
		long pd = payDate.getTime();
		long cd = dueDate.getTime();
		if(pd < cd) {
			overdueDays = (int)((cd - pd) / (24 * 3600 * 1000));
		}
	}


	@Override
	public int compareTo(DocData another) {
		int cmp = another.date.compareTo(date);
		if( cmp != 0 )
			return cmp;
		
		return another.number.compareTo(number);
	}
}
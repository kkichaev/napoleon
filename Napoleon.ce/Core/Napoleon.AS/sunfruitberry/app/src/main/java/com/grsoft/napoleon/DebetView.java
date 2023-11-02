package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.napoleon.documents.AgentMemoDoc;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.debet_data.DocData;
import com.grsoft.napoleon.debet_data.DogovorData;
import com.grsoft.napoleon.debet_data.DebetList;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

import android.content.Context;
import android.content.Intent;
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
		
		DebetList data = new DebetList();
		
		DebtAdapter() {
		}
		
		public void load(String orgId) {
			data.load(orgId);
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
				int color = pd.getColor();
				TextView tv;
				String text = "";
				tv = (TextView)convertView.findViewById(R.id.tvText);
				tv.setText(pd.name);
				tv.setTextColor(color);
				
				text = Integer.toString(pd.dueDays) + "<br/><b>" + Integer.toString(pd.overdueDays) + "</b>";
				if(pd.unlockDate != null) {
					tv = ((TextView)convertView.findViewById(R.id.unlock));
					tv.setText(Util.simpleDateFormat.format(pd.unlockDate));
					tv.setTextColor(color);
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
				dd.update(convertView);
			}
			return convertView;
		}

		@Override public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
	}
}

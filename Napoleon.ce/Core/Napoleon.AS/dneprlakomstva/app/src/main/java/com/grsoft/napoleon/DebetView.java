package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DlvMove;
import com.grsoft.dataobjects.DlvMoveItem;
import com.grsoft.dataobjects.DocDataObject;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class DebetView extends DocumentsBase {
	
	DebtAdapter da;
	Date startDate;
	
	public static void open(Context ctx, String orgId) {
		Intent i = new Intent(ctx, DebetView.class);
		i.putExtra(ExtrasConst.ORG_ID_STR, orgId);
		ctx.startActivity(i);
	}

	@Override protected int getContentViewID() { return R.layout.debetview; }
	@Override protected String orgInfo(Org o) { return OrgUtils.makeOrgInfo((OrgEx) o, null); }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		da = new DebtAdapter();
		
		ExpandableListView lv = (ExpandableListView)findViewById(R.id.lvDocs);
		lv.setAdapter(da);
		
		lv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				DlvMoveItem dd = (DlvMoveItem)da.getChild(groupPosition, childPosition);
				if( dd != null ) {
					DocType dtype = (DocType) DocType.getDocType(dd.type);
					if( dtype != null ) {
						CreatableDocument<?> doc = (CreatableDocument<?>) dtype.create();
						doc.getData().created = dd.created;
						if( doc.read() )
							doc.open(DebetView.this);
					}
				}
				return false;
			}
		});
		
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long id) {
				int itemType = ExpandableListView.getPackedPositionType(id);
				if( itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
					int pos =  ExpandableListView.getPackedPositionGroup(id);
					
					DocDataObject pd = (DocDataObject) da.getGroup(pos);
					if( pd instanceof Delivery ) {
						DeliveryImpl di = new DeliveryImpl();
						Delivery d = di.getData();
						d.number = ((Delivery)pd).number;
						d.id = org.getData().id;
						di.read();
						di.close();
						
						di.open(DebetView.this);
					}
				}
				return false;
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		da.load(org.getData().id);
		tvOrgInfo.setText(Html.fromHtml(orgInfo(org.getData())));
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
		
		List<DocDataObject> data = new ArrayList<DocDataObject>();
		HashMap<String, List<DlvMoveItem>> items = new HashMap<String, List<DlvMoveItem>>();
		
		DebtAdapter() {}
				
		public void load(String orgId) {
			OrgSumImpl osi = new OrgSumImpl();
			final OrgSum os = osi.getData();
			os.id = orgId;
			os.type = DebtDoc.instance().getName();
			osi.read();
			osi.close();
			
			data.clear();
			items.clear();
			
			startDate = new Date();
			DataTraveler.travel(Delivery.class, new DataTraveler.Travel<Delivery>() {

				@Override
				public boolean travel(DataTraveler<Delivery> item) {
					os.sum -= item.data.sumD;
					if( startDate.compareTo(item.data.date) > 0)
						startDate = item.data.date;
					
					data.add(item.data);
					item.data = new Delivery();
					return true;
				}
			}, "id='" + orgId + "'", "date");
			
			DataTraveler.travel(DlvMove.class, new DataTraveler.Travel<DlvMove>(){
				@Override
				public boolean travel(DataTraveler<DlvMove> item) {
					Collections.sort(item.data.items);
					items.put(item.data.num, item.data.items);
					item.data = new DlvMove();
					return true;
				}
			}, "id='" + orgId + "'");
			
			if( os.sum > 0 ) {
				Payment p = new Payment();
				p.number = "Нач.остаток";
				p.date = startDate;
				p.sum = os.sum;
				p.id = orgId;
				
				data.add(0, p);
			} else if( os.sum < 0 ) {
				Payment p = new Payment();
				p.number = "Неразнеcено";
				p.date = null;
				p.sum = os.sum;
				p.id = orgId;
				
				data.add(p);
			}
		}
		
		List<DlvMoveItem> getChilds(int groupPosition) {
			DocDataObject pd = (DocDataObject) getGroup(groupPosition);			
			List<DlvMoveItem> childs = null;
			if( pd instanceof Delivery )
				childs = items.get(((Delivery)pd).number);
			
			return childs;
		}
		
		@Override
		public int getChildrenCount(int groupPosition) {
			List<DlvMoveItem> childs = getChilds(groupPosition);
			return (childs == null) ? 0 : childs.size();
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			List<DlvMoveItem> childs = getChilds(groupPosition);
			if( childs == null )
				return null;
			
			return (childPosition < childs.size()) ? childs.get(childPosition) : null; 
		}

		@Override public int getGroupCount() { return data.size(); }
		@Override public Object getGroup(int groupPosition) { return (groupPosition < data.size()) ? data.get(groupPosition) : null; }
		@Override public long getGroupId(int groupPosition) { return groupPosition; }
		@Override public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }		
		@Override public long getChildId(int groupPosition, int childPosition) { return groupPosition * 10000 + childPosition; }
		@Override public boolean hasStableIds() { return true; }

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			if( convertView == null )
				convertView = View.inflate(DebetView.this, R.layout.debet_group_row, null);
			
			DocDataObject pd = (DocDataObject) getGroup(groupPosition);
			if( pd != null ) {
				
				String text = "";
				String sum = "";
				int color = Color.BLACK;
				String date;
				
				if( pd instanceof Delivery ) {
					Delivery d = (Delivery)pd;
					text = d.number;
					sum = "<small>" + Util.IntToScaleStr(d.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false)+ "</small><br/>";
					if( d.sumD != 0 ) {
						sum += "<b>" + Util.IntToScaleStr(d.sumD, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</b>";
						if(d.sumD > 0 && d.payDate.compareTo(new Date()) < 0)
							color = Color.RED;						
					}
					
					date = Util.simpleDateFormat.format(d.date) + "\n" + Util.simpleDateFormat.format(d.payDate);					
				} else {
					Payment p = (Payment)pd;
					text = p.number;
					sum = Util.IntToScaleStr(p.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
					if( p.date == null )
						date = "";
					else
						date = Util.simpleDateFormat.format(p.date);
				}
				
				TextView tv;
				tv = (TextView)convertView.findViewById(R.id.tvText);
				tv.setText(text);
				tv.setTextColor(color);

				tv = (TextView)convertView.findViewById(R.id.tvDate);
				tv.setText(date);
				tv.setTextColor(color);
				
				tv = (TextView)convertView.findViewById(R.id.tvSum);
				tv.setText(Html.fromHtml(sum));
				tv.setTextColor(color);
			}
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			if( convertView == null )
				convertView = View.inflate(DebetView.this, R.layout.debet_item_row, null);
			
			DlvMoveItem dd = (DlvMoveItem) getChild(groupPosition, childPosition);
			if( dd != null ) {
				TextView tv;
				String docText = dd.type.equals(OrderDoc.instance().getObjectName()) ? "Заказ" :
					dd.type.equals(IncassDoc.instance().getObjectName()) ? "Инкассация" :
					dd.type.equals(ReturnDoc.instance().getObjectName()) ? "Возврат" :
					"";
				tv = (TextView)convertView.findViewById(R.id.tvText);
				tv.setText(Html.fromHtml(docText + " <b>" + dd.num + "</b>"));

				tv = (TextView)convertView.findViewById(R.id.tvDate);
				String text = Util.simpleDateFormat.format(dd.date);
				tv.setText(text);
			
				tv = (TextView)convertView.findViewById(R.id.tvSum);
				tv.setText(Util.IntToScaleStr(dd.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			}
			return convertView;
		}
	}
}

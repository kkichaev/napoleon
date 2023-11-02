package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.CellsAuditItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.VandSellItem;
import com.grsoft.dataobjects.impl.CellsAuditImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.VandSellImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.documents.VandSellDoc;
import com.grsoft.napoleon.util.AskForSend;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class VandSellDetail extends BaseActivity implements SendResultListener {
	VandSellImpl doc;
	List<CellData> cellDataList;
	long rid;
	
	Adapter adapter;
	
	public static void open(Context context, VandSellImpl doc) {
		Intent i = new Intent(context, VandSellDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.vand_sell_detail);
		
		Bundle b = (savedInstanceState != null) ? savedInstanceState : getIntent().getExtras();
		doc = new VandSellImpl();
		rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		
		adapter = new Adapter();
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setDividerHeight(0);
		lv.setAdapter(adapter);

		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { send(); }
		});
	}
	
	protected void send() {
		AskForSend.askSend(this, new DocumentSender(this, findViewById(R.id.btnSend), VandSellDoc.instance().getObjectName(),
				doc, doc.getRowid(), this));
//		new DocumentSender(this, findViewById(R.id.btnSend), VandSellDoc.instance().getObjectName(),
//				doc, doc.getRowid(), this).execute((Void[])null);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		doc.read(rid, false);
		/*if( audit == null )
			audit = CellsAuditImpl.getLastDoc(doc.getId());*/
		if( cellDataList == null )
			cellDataList = CellData.getVandData(doc.getId(), doc.getData().created);

		OrgImpl oi = new OrgImpl();
		Org org = oi.getData();
		org.id = doc.getId();
		oi.read();
		oi.close();

		TextView tv = (TextView)findViewById(R.id.tvOrg);
		tv.setText(org.name + "\nПродажа");

		adapter.refresh();
		refreshDocSum();
	}
	
	void refreshDocSum() {
		TextView tv = (TextView)findViewById(R.id.tvDocSum);
		String text = Util.IntToScaleStr(doc.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false);
		tv.setText(text);
	}
	
	enum SetQtyType { stChek, stLoad, stUnload }
	
	SetQty setChek = new SetQty(SetQtyType.stChek);
	SetQty setLoad = new SetQty(SetQtyType.stLoad);
	SetQty setUnload = new SetQty(SetQtyType.stUnload);
	
	class SetQty extends InputNumber implements OnClickListener {
		
		RowData changedItem;
		SetQtyType type;

		public SetQty(SetQtyType type) { this.type = type; }
		
		@Override
		public void onClick(View view) {
			if( doc.isExported() )
				return;
			
			changedItem = (RowData)((View)view.getParent()).getTag();
			
			String title = (type == SetQtyType.stChek) ? "Чек" :
				(type == SetQtyType.stLoad) ? "Загрузка" :
				"Выгрузка";
			InputNumberDlg.open(view.getContext(), this, Consts.QTY_SCALE, true, title);
		}

		@Override
		public void applayInput(int value, Object... params) {
			if( changedItem.item == null )
				return;
			
			switch(type) {
			case stChek:
				changedItem.item.chek = value;
				break;
			case stLoad:
				changedItem.item.load = value;
				break;
			case stUnload:
				changedItem.item.unload = value;
				break;
			}
			
			doc.write();
			adapter.notifyDataSetChanged();
			if(type == SetQtyType.stChek)
				refreshDocSum();
		}

		@Override
		public int getValue() {
			if(changedItem.item == null)
				return 0;
			
			return type == SetQtyType.stChek ? changedItem.item.chek :
				type == SetQtyType.stLoad ? changedItem.item.load :
				changedItem.item.unload;
		}
		
	}
	
	class Adapter extends BaseAdapter {
		
		List<RowData> items = new ArrayList<RowData>();
		
		public void refresh() {
			items.clear();
			
			PriceImpl pi = new PriceImpl();
			Price prc = pi.getData();
			
			for(VandSellItem vsi : doc.getData().items) {
				prc.id = vsi.id;
				pi.read();
				
				items.add(new RowData(vsi, prc));
			}			
			pi.close();
			
			for(CellData cai : cellDataList) {
				for(RowData ri : items) {
					if( ri.cell == cai.cell ) {
						ri.set(cai);
						break;
					}
				}
			}
			
			notifyDataSetChanged();
		}

		@Override public int getCount() { return items.size(); }
		@Override public Object getItem(int arg0) { return arg0 < items.size() ? items.get(arg0) : null; }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if( view == null )
				view = View.inflate(VandSellDetail.this, R.layout.vand_sell_row, null);
			
			view.setBackgroundResource(pos % 2 != 0 ? R.drawable.even_row_selector : R.drawable.list_selector);
			RowData item = (RowData) getItem(pos);
			if( item != null ) {
				view.setTag(item);
				
				TextView tv;
				String text;
				tv = (TextView)view.findViewById(R.id.tvOrder);
				text = Integer.toString(item.cell);
				tv.setText(text);
				
				tv = (TextView)view.findViewById(R.id.tvName);
				tv.setText(item.name);
				
				tv = (TextView)view.findViewById(R.id.tvLimit);
				text = Util.IntToScaleStr(item.limit, Consts.QTY_SCALE) + "<br><b><font color='red'>" + 
						Util.IntToScaleStr(item.qty, Consts.QTY_SCALE) + "</font></b>";
				tv.setText(Html.fromHtml(text));
								
				tv = (TextView)view.findViewById(R.id.tvChek);
				tv.setOnClickListener(setChek);
				text = item.item != null ? Util.IntToScaleStr(item.item.chek, Consts.QTY_SCALE) : "";
				tv.setText(text);

				tv = (TextView)view.findViewById(R.id.tvCost);
				text = Util.IntToScaleStr(item.item.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false);
				tv.setText(text);

				tv = (TextView)view.findViewById(R.id.tvLoad);
				tv.setOnClickListener(setLoad);
				int fullLoad = item.limit - (item.qty - item.item.chek + item.item.load);
				if(fullLoad > item.limit)
					fullLoad = item.limit;
				text = Util.IntToScaleStr(item.item.load, Consts.QTY_SCALE) + 
						"<br><b><font color='red'>" + Util.IntToScaleStr(fullLoad, Consts.QTY_SCALE) + "</font></b>";
				tv.setText(Html.fromHtml(text));
			
				tv = (TextView)view.findViewById(R.id.tvUnload);
				tv.setOnClickListener(setUnload);
				text = item.item != null ? Util.IntToScaleStr(item.item.unload, Consts.QTY_SCALE) : "";
				tv.setText(text);
			}
			return view;
		}
		
	}

	@Override
	public void postSendExecute(boolean result) {
		if( result )
			doc.read(doc.getRowid(), false);
	}
}

class RowData {
	public String name;
	public String id;
	public int cell;
	public int limit;
	public int qty;
	
	public VandSellItem item;
	
	public RowData(VandSellItem item, Price prc) {
		name = prc.name;
		id = item.id;
		
		cell = item.cell;
		this.item = item;
	}
	
	public void set(CellData item) {
		limit = item.limit;
		qty = item.rest;		
	}
}

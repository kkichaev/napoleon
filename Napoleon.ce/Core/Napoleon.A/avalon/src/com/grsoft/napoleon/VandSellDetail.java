package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.VandSellItem;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.VandSellImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.documents.VandSellDoc;
import com.grsoft.napoleon.util.AskForSend;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class VandSellDetail extends BaseActivity implements SendResultListener {
	VandSellImpl doc;
	List<CellData> cellDataList;
	long rid;
	
	boolean haveCommonCheck = false;
	
	String moneyID = null;
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

		btnSend = (ImageButton) findViewById(R.id.btnSend);
		
		btnSend.setOnClickListener(new View.OnClickListener() {
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
		
		if( Features.CANT_SEND_SCRIPT_PART ) {
			if(ScriptImpl.containsDocument(DocType.getCurDoc().getObjectName(), doc.getData().created, doc.getId()))
				btnSend.setVisibility(View.GONE);
		}
		
		if( cellDataList == null )
			cellDataList = CellData.getVandData(doc.getId(), doc.getData().created);

		OrgImpl oi = new OrgImpl();
		OrgEx org = (OrgEx) oi.getData();
		org.id = doc.getId();
		oi.read();
		oi.close();
		haveCommonCheck = org.commonChek != 0;

		TextView tv = (TextView)findViewById(R.id.tvOrg);
		tv.setText(org.name + "\nПродажа");

		adapter.refresh();
		refreshDocSum();
	}
	
	void refreshDocSum() {
		TextView tv = (TextView)findViewById(R.id.tvDocSum);
		
		if( moneyID == null ) {
			moneyID = "";
			ConfigImpl ci = new ConfigImpl();
			StringBuilder sb = new StringBuilder();
			if( ci.getValue(sb, "РазменнаяМонета") ) {
				String[] vals = sb.toString().split("\t");
				moneyID = vals[1];
			}
		}
		
		int load = 0, unload = 0;
		for(VandSellItem i : doc.getData().items) {
			if( i.id.equals(moneyID) )
				continue;
			load += i.load;
			unload += i.unload;
		}
		
		String text = "загр." + Util.IntToScaleStr(load, Consts.QTY_SCALE) + 
				" выгр." + Util.IntToScaleStr(unload, Consts.QTY_SCALE) + "<br/>";
		text += "<b>" + Util.IntToScaleStr(doc.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</b>";
		tv.setText(Html.fromHtml(text));
	}
	
	enum SetQtyType { stChek, stLoad, stUnload }
	
	SetQty setChek = new SetQty(SetQtyType.stChek);
	SetQty setLoad = new SetQty(SetQtyType.stLoad);
	SetQty setUnload = new SetQty(SetQtyType.stUnload);
	private ImageButton btnSend;
	
	class SetQty extends InputNumber implements OnClickListener {
		
		RowData changedItem;
		SetQtyType type;

		public SetQty(SetQtyType type) { this.type = type; }
		
		@Override
		public void onClick(View view) {
			if( doc.isExported() )
				return;
			
			Object tag = ((View)view.getParent()).getTag();
			if( tag == null )
				tag = ((View)view.getParent().getParent()).getTag();
			open((RowData)tag, view.getContext());
		}
		
		public void open(RowData item, Context context) {
			changedItem = item;
			
			String title = (type == SetQtyType.stChek) ? "Чек" :
				(type == SetQtyType.stLoad) ? "Загрузка" :
				"Выгрузка";
			title += " (" + Integer.toString(changedItem.cell) + ")";
			InputNumberDlg.open(context, this, Consts.QTY_SCALE, true, title);
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
				
				if(value > changedItem.limit)
					Toast.makeText(VandSellDetail.this,R.string.cell_overload_warng, Toast.LENGTH_SHORT).show();
				
				break;
			case stUnload:
				changedItem.item.unload = value;
				break;
			}
			
			doc.write();
			adapter.notifyDataSetChanged();
			if(type == SetQtyType.stChek)
				refreshDocSum();
			
			sumUpdated(changedItem, type);
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
	
	public void sumUpdated(RowData changedItem, SetQtyType type) {
		if( adapter.getCount() == 0 )
			return;
		
		int pos = adapter.getItemPos(changedItem);
		boolean selectNext = (pos >= 0 && pos < adapter.getCount() - 1);
		RowData newRow = (selectNext) ? (RowData)adapter.getItem(++pos) : (RowData)adapter.getItem(0);
		
		// для денег пропустим ввод чека 
		if( selectNext && type == SetQtyType.stChek && newRow.isMoney ) {
			pos++;
			selectNext = (pos >= 0 && pos < adapter.getCount() - 1);
			newRow = (selectNext) ? (RowData)adapter.getItem(++pos) : (RowData)adapter.getItem(0);
		}
		
		switch(type) {
		case stChek:
			if( selectNext )
				setChek.open(newRow, this);
			else
				setLoad.open(newRow, this);
			break;
		case stLoad:
			if( selectNext )
				setLoad.open(newRow, this);
			else
				setUnload.open(newRow, this);
			break;
		case stUnload:
			if( selectNext )
				setUnload.open(newRow, this);
			break;
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
				if( !pi.read() )
					prc.name = vsi.id.length() == 0 ? "<нет>" : "<Код товара'"+ vsi.id + "'>";
				
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
			Collections.sort(items);
			
			notifyDataSetChanged();
		}
		
		public int getItemPos(RowData item) { return items.indexOf(item); }

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
				if( item.isMoney)
					view.setBackgroundResource(R.drawable.money_color);
				
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
				if( !item.isMoney )
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
				
				text = Util.IntToScaleStr(item.item.load, Consts.QTY_SCALE);
				String p2 = "<b><font color='red'>" + Util.IntToScaleStr(fullLoad, Consts.QTY_SCALE) + "</font></b>";

				tv.setText(text);
				tv = (TextView)view.findViewById(R.id.tvRecLoad);
				tv.setText(Html.fromHtml(p2));
				tv.setOnClickListener(setLoad);
			
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

class RowData implements Comparable<RowData> {
	public String name;
	public String id;
	public int cell;
	public int limit;
	public int qty;
	boolean isMoney;
	
	public VandSellItem item;
	
	public RowData(VandSellItem item, Price prc) {
		name = prc.name;
		id = item.id;
		
		cell = item.cell;
		this.item = item;
		isMoney = prc.color == NapoleonApp.MONEY_COLOR;
	}
	
	public void set(CellData item) {
		limit = item.limit;
		qty = item.rest;		
	}

	@Override
	public int compareTo(RowData arg0) {
		return cell - arg0.cell;
	}
}

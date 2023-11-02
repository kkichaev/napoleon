package com.grsoft.napoleon;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.CellsAuditItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.CellsAuditImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.CellsAuditDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.util.AskForSend;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class CellsAuditDetail extends BaseActivity implements SendResultListener {
	
	protected static final int CHANGE_ITEM = 0;
	
	CellsAuditImpl doc;
	PriceImpl price = new PriceImpl();
	Adapter adapter;
	CellsAuditItem changedItem;
	
	public static void open(Context context, CellsAuditImpl doc) {
		Intent i = new Intent(context, CellsAuditDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		DocType.setCurDoc(CellsAuditDoc.instance());
		doc = new CellsAuditImpl();
		
		setContentView(R.layout.cells_audit_detail);
		Bundle b = (savedInstanceState != null) ? savedInstanceState : getIntent().getExtras();
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		doc.read(rid);
		
		OrgImpl oi = new OrgImpl();
		Org org = oi.getData();
		org.id = doc.getId();
		oi.read();
		oi.close();

		TextView tv = (TextView)findViewById(R.id.tvOrg);
		tv.setText(org.name + "\nАудит");

		
		adapter = new Adapter(); 
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setDividerHeight(0);
		lv.setAdapter(adapter);
		
		View v;
		v = findViewById(R.id.btnAdd);
		v.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { addItem(); }
		});
		
		v = findViewById(R.id.btnSend);
		v.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { send(); }
		});
	}
	
	OnClickListener changeItem = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			if(doc.isExported())
				return;

			changedItem = (CellsAuditItem) ((View)view.getParent()).getTag();
			showDialog(CHANGE_ITEM);
		}
	};
	
	ChangeQty changeLimit = new ChangeQty(ChangeType.ctLimit);
	ChangeQty changeQty = new ChangeQty(ChangeType.ctQty);
	ChangeQty changeCost = new ChangeQty(ChangeType.ctCost);
	
	class ChangeQty extends InputNumber implements OnClickListener {
		
		ChangeType changeLimit;
		
		public ChangeQty(ChangeType changelimit) {
			this.changeLimit = changelimit;
		}

		@Override
		public void onClick(View view) {
			if(doc.isExported())
				return;
			
			changedItem = (CellsAuditItem) ((View)view.getParent()).getTag();
			
			int scale = (changeLimit == ChangeType.ctCost) ? Consts.SUM_SCALE : Consts.QTY_SCALE;
			boolean hideRest = (changeLimit == ChangeType.ctCost) ? false : true;
			String title = (changeLimit == ChangeType.ctCost) ? "Введите цену" :
				(changeLimit == ChangeType.ctLimit) ? "Введите лимит" :
				"Введите количество";
			InputNumberDlg.open(view.getContext(), this, scale, hideRest, title);
		}

		@Override
		public void applayInput(int value, Object... params) {
			switch (changeLimit) {
			case ctLimit:
				changedItem.limit = value;
				break;
			case ctCost:
				changedItem.cost = value;
				break;
			case ctQty:
				changedItem.qty = value;
				break;
			default:
				return;
			}
			doc.write();
			adapter.notifyDataSetChanged();
		}

		@Override
		public int getValue() {
			return changeLimit == ChangeType.ctLimit ? changedItem.limit :
				changeLimit == ChangeType.ctQty ? changedItem.qty :
				changedItem.cost;
		}
		
	}
	
	protected android.app.Dialog onCreateDialog(int id) {
		switch(id) {
		case CHANGE_ITEM:
			return PriceDialog.create(this, new PriceDialog.ItemClicked() {
				
				@Override
				public void clicked(Price item) {
					if( changedItem != null ) {
						changedItem.id = item.id;
						doc.write();
						adapter.notifyDataSetChanged();
						changedItem = null;
					}
				}
			});
		}
		return super.onCreateDialog(id);
	}
	
	protected void send() {
		AskForSend.askSend(this, new DocumentSender(this, findViewById(R.id.btnSend), CellsAuditDoc.instance().getObjectName(),
				doc, doc.getRowid(), this));
//		new DocumentSender(this, findViewById(R.id.btnSend), CellsAuditDoc.instance().getObjectName(),
//				doc, doc.getRowid(), this).execute((Void[])null);
	}

	protected void addItem() {
		if(doc.isExported())
			return;

		List<CellsAuditItem> items = doc.getData().items; 
		CellsAuditItem item = new CellsAuditItem();
		item.id = "";
		item.cell = items.size() + 1;
		items.add(item);
		doc.write();
		
		adapter.notifyDataSetChanged();
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
		price.close();
	}
	
	class Adapter extends BaseAdapter {

		@Override public int getCount() { return doc.getData().items.size(); }

		@Override
		public Object getItem(int arg0) {
			return arg0 < getCount() ? doc.getData().items.get(arg0) : null;
		}

		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if( view == null )
				view = View.inflate(CellsAuditDetail.this, R.layout.cells_audit_row, null);
			
			view.setBackgroundResource(pos % 2 != 0 ? R.drawable.even_row_selector : R.drawable.list_selector);
			
			CellsAuditItem item = (CellsAuditItem)getItem(pos);
			if( item != null ) {
				view.setTag(item);
				Price prc = price.getData();
				
				TextView tv;
				String text;
				tv = (TextView)view.findViewById(R.id.tvOrder);
				tv.setText(Integer.toString(item.cell));
				
				text = "нет";
				if(item.id.length() != 0) {
					prc.id = item.id;
					if(price.read())
						text = prc.name;
					else
						text = "<Код товара'"+ item.id + "'>";
				}
				Button b = (Button)view.findViewById(R.id.btnName);
				b.setText(text);
				b.setOnClickListener(changeItem);
				
				text = Util.IntToScaleStr(item.limit, Consts.QTY_SCALE);
				tv = (TextView)view.findViewById(R.id.tvLimit);
				tv.setOnClickListener(changeLimit);
				tv.setText(text);
				
				text = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE);
				tv = (TextView)view.findViewById(R.id.tvQty);
				tv.setOnClickListener(changeQty);
				tv.setText(text);
			
				text = Util.IntToScaleStr(item.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false);
				tv = (TextView)view.findViewById(R.id.tvCost);
				tv.setOnClickListener(changeCost);
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

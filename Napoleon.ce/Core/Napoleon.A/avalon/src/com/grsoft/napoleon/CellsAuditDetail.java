package com.grsoft.napoleon;

import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class CellsAuditDetail extends BaseActivity implements SendResultListener {
	
	protected static final int CHANGE_ITEM = 0;
	protected static final int DELETE_ITEM = 1;
	
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
		Collections.sort(doc.getData().items);
		
		OrgImpl oi = new OrgImpl();
		Org org = oi.getData();
		org.id = doc.getId();
		oi.read();
		oi.close();

		TextView tv = (TextView)findViewById(R.id.tvOrg);
		tv.setText(org.name + "\nјудит");

		
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
		
		if( Features.CANT_SEND_SCRIPT_PART ) {
			if(ScriptImpl.containsDocument(DocType.getCurDoc().getObjectName(), doc.getData().created, doc.getId()))
				v.setVisibility(View.GONE);
		}
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
	
	View.OnLongClickListener changeCell = new OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			final CellsAuditItem item = (CellsAuditItem) ((View)v.getParent()).getTag();
			
			InputNumberDlg.open(CellsAuditDetail.this, new InputNumber() {
				@Override public int getValue() { return item.cell; }
				
				@Override
				public void applayInput(int value, Object... params) {
					item.cell = value;
					doc.write();
					adapter.notifyDataSetChanged();
				}
				
				public boolean isValid(int value, Object... params) {
					boolean canDo = true;
					for(CellsAuditItem ci : doc.getData().items) {
						if( ci.cell == value && ci != item) {
							canDo = false;
							Toast.makeText(CellsAuditDetail.this, "Ќомер €чейки должен быть уникальным!", Toast.LENGTH_SHORT).show();
							break;
						}
					}
					
					return canDo;
				}
			}, 1, true, "¬ведите €чейку");

			return true;
		}
	};
	
	OnLongClickListener deleteItem = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			changedItem = (CellsAuditItem) ((View)v.getParent()).getTag();
			showDialog(DELETE_ITEM);
			return true;
		}
	};
	
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
			String title = (changeLimit == ChangeType.ctCost) ? "¬ведите цену" :
				(changeLimit == ChangeType.ctLimit) ? "¬ведите лимит" :
				"¬ведите количество";
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
						int cost = item != null && item.cost != null && item.cost.size() > 0 ? item.cost.get(0).cost : 0;
						changedItem.cost = cost;
						doc.write();
						adapter.notifyDataSetChanged();
						changedItem = null;
					}
				}
			});
		case DELETE_ITEM:
			return createDeleteItemDlg();
		}
		return super.onCreateDialog(id);
	}
	
	private Dialog createDeleteItemDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.warning);
		builder.setMessage(R.string.ask_to_delete);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(changedItem != null){
					doc.getData().items.remove(changedItem);
					int cell = 1;
					for(CellsAuditItem ci : doc.getData().items) {
						ci.cell = cell++;
					}
					doc.write();
					adapter.notifyDataSetChanged();
				}
			}
		});
		
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
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
		
		((ListView)findViewById(R.id.lvItems)).setSelection(adapter.getCount() - 1);
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
				tv.setOnLongClickListener(changeCell);
				
				text = "нет";
				if(item.id.length() != 0) {
					prc.id = item.id;
					if(price.read())
						text = prc.name;
					else
						text = "< од товара'"+ item.id + "'>";
					if( prc.color == NapoleonApp.MONEY_COLOR )
						view.setBackgroundResource(R.drawable.money_color);
				}
				Button b = (Button)view.findViewById(R.id.btnName);
				b.setText(text);
				b.setOnClickListener(changeItem);
				
				text = Util.IntToScaleStr(item.limit, Consts.QTY_SCALE);
				tv = (TextView)view.findViewById(R.id.tvLimit);
				tv.setOnClickListener(changeLimit);
				tv.setText(text);
				tv.setOnLongClickListener(deleteItem);
				
				text = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE);
				tv = (TextView)view.findViewById(R.id.tvQty);
				tv.setOnClickListener(changeQty);
				tv.setText(text);
				tv.setOnLongClickListener(deleteItem);
			
				text = Util.IntToScaleStr(item.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false);
				tv = (TextView)view.findViewById(R.id.tvCost);
				tv.setOnClickListener(changeCost);
				tv.setText(text);
				tv.setOnLongClickListener(deleteItem);
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

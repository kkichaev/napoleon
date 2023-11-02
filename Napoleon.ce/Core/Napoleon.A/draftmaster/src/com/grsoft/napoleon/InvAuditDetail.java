package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.grsoft.dataobjects.InvAuditItem;
import com.grsoft.dataobjects.impl.InvAuditImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.InvAuditDoc;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;


public class InvAuditDetail extends BaseActivity {
	private ListView list;
	private TextView tvOrg;
	private View btnEdit;
	private View btnLines;
	private View btnSend;
	private InvAuditImpl doc = new InvAuditImpl();
	private LinesCountController linesController;
	
	public static void open(Context ctx, long rowid){
		Intent i = new Intent(ctx, InvAuditDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		ctx.startActivity(i);
	}
	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invauditdetail);
		
		list = (ListView) findViewById(R.id.list);
		tvOrg = (TextView) findViewById(R.id.tvOrg);
		btnEdit = findViewById(R.id.btnEditOrder);
		btnLines = findViewById(R.id.btnLines);
		btnSend = findViewById(R.id.btnSend);
		
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		doc.close();
		
		OrgImpl org = new OrgImpl();
		org.read("id", doc.getId());
		org.close();
		
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(list, (ImageView)btnLines, this, true);
		linesController = linesOnClickListener.getController();
		
		tvOrg.setText(org.getData().name);
		list.setAdapter(createAdapter());
		btnEdit.setOnClickListener(editDocClick());
		btnSend.setOnClickListener(sendClick());
		
	}

	private OnClickListener sendClick() { return new OnClickListener() { @Override	public void onClick(View v) { send(); } }; }

	protected void send() {
		new DocumentSender(this, btnSend, InvAuditDoc.instance().getObjectName(), doc, doc.getRowid()).execute((Void[])null);
	}

	private OnClickListener editDocClick() { return new OnClickListener() { @Override public void onClick(View v) { editDoc(); } }; }

	protected void editDoc() { InvAuditEdit.open(this, doc.getRowid(), true); }

	private ListAdapter createAdapter() {
		return new BaseAdapter() {
			@Override public View getView(int position, View convertView, ViewGroup parent) { return drawView(position, convertView, getItem(position)); }
			@Override public long getItemId(int position) { return 0; }
			@Override public Object getItem(int position) { return doc.getData().items.get(position);	}
			@Override public int getCount() {	return doc.getData().items.size();	}
		};
	}

	protected View drawView(int position, View view, Object item) {
		if(view == null)
			view = View.inflate(this, R.layout.invauditrow, null);
		
		InvAuditItem i = (InvAuditItem)item;
		
		TextView tv = (TextView) view.findViewById(R.id.tvName);
		tv.setText(i.name);
		linesController.prepareTextView(tv);
		
		tv = (TextView) view.findViewById(R.id.tvQty);
		tv.setText(Util.IntToScaleStr(i.qty, Consts.QTY_SCALE));
		
		tv = (TextView) view.findViewById(R.id.tvFact);
		tv.setText(Util.IntToScaleStr(i.fact, Consts.QTY_SCALE));
		tv.setTag(i);
		
		if (doc.isEditable())
			tv.setOnClickListener(factClick);
		
		CheckBox cb = (CheckBox) view.findViewById(R.id.cbClear);
		cb.setOnCheckedChangeListener(null);
		cb.setChecked(i.clear == 1);
		cb.setTag(i);
		
		if(doc.isEditable())
			cb.setOnCheckedChangeListener(clearClick);
		
		tv = (TextView) view.findViewById(R.id.tvGood);
		tv.setText(Util.IntToScaleStr(i.good, Consts.QTY_SCALE));
		tv.setTag(i);
		
		if(doc.isEditable())
			tv.setOnClickListener(goodClick);
		
		return view;
	};
	
	OnClickListener factClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final InvAuditItem item = (InvAuditItem) v.getTag();
			
			InputNumberDlg.open(v.getContext(), new InputNumber() {
				
				@Override public boolean useComma() { return false; }
				@Override public boolean replaceCommaToPlus() { return false; }
				
				@Override
				public void applayInput(int value, Object... params) {
					item.fact = value;
					doc.write();
					doc.close();
					((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
				}

				@Override
				public int getValue() {	return item.fact;}
			});
		}
	};
	
	OnCheckedChangeListener clearClick = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			final InvAuditItem item = (InvAuditItem) buttonView.getTag();
			item.clear = isChecked ? 1 : 0;
			doc.write();
			doc.close();
			((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
		}
	};
	
	OnClickListener goodClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final InvAuditItem item = (InvAuditItem) v.getTag();
			
			InputNumberDlg.open(v.getContext(), new InputNumber() {
				
				@Override public boolean useComma() { return false; }
				@Override public boolean replaceCommaToPlus() { return false; }
				
				@Override
				public void applayInput(int value, Object... params) {
					item.good = value;
					doc.write();
					doc.close();
					((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
				}

				@Override
				public int getValue() {	return item.good;}
			});
		}
	};
}

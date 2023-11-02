package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.dataobjects.ExistItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.ExistOutImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DiscountMonitoringDoc;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.ExistDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class ExistOutEdit extends BaseActivity implements SendResultListener {
	ExistOutImpl doc;
	PriceImpl pi = new PriceImpl();
	LinesCountController linesController;
	
	Adapter adapter;
	
	public static void open(Context context, ExistOutImpl doc) {
		Intent i = new Intent(context, ExistOutEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.exist_edit);
		doc = new ExistOutImpl();
		
		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		
		
		OrgImpl org = new OrgImpl();
		org.getData().id = doc.getId();
		org.read();
		org.close();
				
		TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
		tvOrg.setText(Html.fromHtml(org.getData().name));
		
		adapter = new Adapter(doc.getData().items);
		ListView lv = (ListView) findViewById(R.id.lvItems);
		lv.setAdapter(adapter);
	
		ImageButton btnLines = (ImageButton) findViewById(R.id.btnLines);
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(lv, btnLines, this, true);
		linesController = linesOnClickListener.getController();
	
		final View btnSend = findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new DocumentSender(ExistOutEdit.this, btnSend, ExistDoc.instance().getObjectName(), doc, 
						doc.getRowid(), ExistOutEdit.this).execute((Void[])null);
			}
		});
		if( Features.CANT_SEND_SCRIPT_PART ) {
			if(ScriptImpl.containsDocument(DiscountMonitoringDoc.instance().getObjectName(), doc.getData().created, doc.getId()))
				btnSend.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
		pi.close();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}
	
	View.OnClickListener setCost = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			if(doc.isEditable() == false)
				return;
			
			final ExistItem item = (ExistItem) ((View)arg0.getParent()).getTag(); 
			InputNumberDlg.open(ExistOutEdit.this, new InputNumber() {
				@Override public int getValue() { return item.cost; }
				
				@Override
				public void applayInput(int value, Object... params) {
					item.cost = value;
					doc.write();
					ExistDoc.instance().refreshDocSum(doc.getId());
					adapter.notifyDataSetChanged();
				}
			}, Consts.SUM_SCALE, false, "¬ведите цену");
		}
	};
	
	CompoundButton.OnCheckedChangeListener setPrezent = new CompoundButton.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			if(doc.isEditable() == false)
				return;
			
			ExistItem item = (ExistItem) ((View)arg0.getParent()).getTag();
			item.present = arg1 ? 1 : 0;
			doc.write();
			ExistDoc.instance().refreshDocSum(doc.getId());
			adapter.notifyDataSetChanged();
		}
	};
	
	class Adapter extends BaseAdapter {
		List<ExistItem> items;
		
		public Adapter(List<ExistItem> items) { this.items = items; }
		
		@Override public int getCount() { return items.size(); }
		@Override public Object getItem(int arg0) { return items.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if(view == null)
				view = View.inflate(ExistOutEdit.this, R.layout.exist_row, null);
			
			ExistItem item = (ExistItem) getItem(arg0);
			Price p = pi.getData();
			p.id = item.id;
			pi.read();
			
			view.setTag(item);

			TextView tv = (TextView)view.findViewById(R.id.tvName);
			
			linesController.prepareTextView(tv);
			tv.setText(p.name);
			
			String text;
			tv = (TextView)view.findViewById(R.id.tvAvgLoad);
			text = Integer.toString(item.sred);
			tv.setText(text);

			tv = (TextView)view.findViewById(R.id.tvCost);
			text = Util.IntToScaleStr(item.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			tv.setText(text);
			tv.setOnClickListener(setCost);
			tv.setBackgroundResource(R.drawable.list_selector);
			
			CheckBox cb = (CheckBox)view.findViewById(R.id.cbIsPresent);
			cb.setChecked(item.present > 0);
			cb.setOnCheckedChangeListener(setPrezent);
			
			view.setBackgroundResource(item.priz > 0 ? R.drawable.out_row_selector : R.drawable.list_selector);
			return view;
		}
		
	}

	@Override
	public void postSendExecute(boolean result) {
		if(result)
			doc.read(doc.getRowid(), false);
	}
}

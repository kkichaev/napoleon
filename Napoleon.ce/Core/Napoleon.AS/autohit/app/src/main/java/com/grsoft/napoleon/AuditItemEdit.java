package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.grsoft.dataobjects.Goods;
import com.grsoft.dataobjects.GoodsAuditItem;
import com.grsoft.dataobjects.impl.GoodsAuditImpl;
import com.grsoft.dataobjects.impl.GoodsImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;
import com.grsoft.view.KeypadHelper;

public class AuditItemEdit extends BaseActivity {
	GoodsAuditImpl doc = new GoodsAuditImpl();
	GoodsImpl goods = new GoodsImpl();
	KeypadHelper keypadHelper;
	
	View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
		@Override
		public void onFocusChange(View arg0, boolean arg1) {
			if( arg1 ) {
				keypadHelper.setTargetID(arg0.getId());
				((EditText)arg0).selectAll();
			}
		}
	};
	
	static public void open(Context context, GoodsAuditImpl doc, long priceRowId) {
		Intent i = new Intent(context, AuditItemEdit.class);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRowId);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		context.startActivity(i);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.audit_item_edit);
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		goods.read(b.getLong(ExtrasConst.PRICE_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		
		keypadHelper = new KeypadHelper(this, R.id.shelfAll);
		
		Goods g = goods.getData();
		
		TextView tv;
		tv = (TextView)findViewById(R.id.tvGood);
		tv.setText(g.name);
		
		GoodsAuditItem item = (GoodsAuditItem) doc.findItem(g.id);
		if( item == null )
			item = new GoodsAuditItem();
		
		EditText ed;
		ed = (EditText)findViewById(R.id.shelfAll);
		ed.setText(Util.IntToScaleStr(item.shelfAll, Consts.SUM_SCALE));
		ed.setInputType(InputType.TYPE_NULL);
		ed.selectAll();
		ed.setOnFocusChangeListener(focusListener);

		ed = (EditText)findViewById(R.id.shelfOur);
		ed.setInputType(InputType.TYPE_NULL);
		ed.setText(Util.IntToScaleStr(item.shelfOur, Consts.SUM_SCALE));
		ed.setOnFocusChangeListener(focusListener);

		ed = (EditText)findViewById(R.id.scuAll);
		ed.setInputType(InputType.TYPE_NULL);
		ed.setText(Util.IntToScaleStr(item.scuAll, Consts.SUM_SCALE));
		ed.setOnFocusChangeListener(focusListener);
	
		ed = (EditText)findViewById(R.id.scuOur);
		ed.setInputType(InputType.TYPE_NULL);
		ed.setText(Util.IntToScaleStr(item.scuOur, Consts.SUM_SCALE));
		ed.setOnFocusChangeListener(focusListener);
		
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) { 
				save();
				finish();
			}
		});
	}
	
	protected void save() {
		String id = goods.getData().id;
		GoodsAuditItem item = (GoodsAuditItem) doc.findItem(id);
		if( item == null ) {
			item = new GoodsAuditItem();
			item.id = id;
			doc.getData().items.add(item);
		}
		
		EditText ed;
		ed = (EditText)findViewById(R.id.shelfAll);
		item.shelfAll = Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);

		ed = (EditText)findViewById(R.id.shelfOur);
		item.shelfOur = Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);

		ed = (EditText)findViewById(R.id.scuAll);
		item.scuAll = Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);
	
		ed = (EditText)findViewById(R.id.scuOur);
		item.scuOur = Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);
		
		doc.write();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		outState.putLong(ExtrasConst.PRICE_ROW_ID_STR, goods.getRowid());
		
		super.onSaveInstanceState(outState);
	}
}

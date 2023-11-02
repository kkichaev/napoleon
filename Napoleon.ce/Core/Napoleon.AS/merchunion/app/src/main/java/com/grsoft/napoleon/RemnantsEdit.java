package com.grsoft.napoleon;

import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class RemnantsEdit extends Activity implements OnClickListener {
	RemnantsImplEx doc = new RemnantsImplEx();
	EditText edFormat;
	EditText edQty;
	EditText edFace;
	EditText edCost;
	CheckBox cbPromo;
	EditText edOOS;
	EditText edRemark;
	
	private RemnantItemEx item;
	private PriceImpl price = new PriceImpl();
	
	public static void open(Context context, long rowid, long itemRowid) {
		Intent intent = new Intent(context, RemnantsEdit.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		intent.putExtra(ExtrasConst.PRICE_ROW_ID_STR, itemRowid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.remnantsedit);
		
		edFormat = (EditText)findViewById(R.id.edFormat);
		edQty = (EditText)findViewById(R.id.edQty);
		edFace = (EditText)findViewById(R.id.edFace);
		edCost = (EditText)findViewById(R.id.edCost);
		edOOS = (EditText)findViewById(R.id.edOOS);
		cbPromo = (CheckBox)findViewById(R.id.cbPromo);
		edRemark = (EditText) findViewById(R.id.edRemark);
		
		price.read(getIntent().getLongExtra(ExtrasConst.PRICE_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		
		((TextView)findViewById(R.id.tvPrice)).setText(price.getData().name);
		
		findViewById(R.id.btnOK).setOnClickListener(this);
		findViewById(R.id.btnCancel).setOnClickListener(this);

		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		
		item = (RemnantItemEx) doc.findItem(price.getData().id);
		
		if (item != null) {
			edFormat.setText(Integer.toString(item.format));
			edQty.setText(Util.IntToScaleStr(item.qty, Consts.QTY_SCALE));
			edFace.setText(Util.IntToScaleStr(item.face, Consts.QTY_SCALE));
			edCost.setText(Util.IntToScaleStr(item.cost, Consts.SUM_SCALE));
			edOOS.setText(item.oos);
			cbPromo.setChecked(item.promo != 0);
			edRemark.setText(item.remark);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnOK) 
			okClick();
		
	}

	private void okClick() {
		if (item == null) {
			item = new RemnantItemEx();
			item.id = price.getData().id;
			doc.getData().items.add(item);
		}
		
		item.format = 0;
		
		try {
			item.format = Integer.parseInt(edFormat.getText().toString().trim());
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		item.qty = (int)Util.StrToScale(edQty.getText().toString(), Consts.QTY_SCALE);
		item.face = (int)Util.StrToScale(edFace.getText().toString(), Consts.QTY_SCALE);
		item.cost = (int)Util.StrToScale(edCost.getText().toString(), Consts.SUM_SCALE);
		item.promo = cbPromo.isChecked() ? 1 : 0;
	    item.oos = edOOS.getText().toString().trim();
	    item.remark = edRemark.getText().toString().trim();
			
		doc.write();
		doc.close();
		
		try {
			RemnantsDoc.instance().refreshDocSum();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
		finish();
	}
}

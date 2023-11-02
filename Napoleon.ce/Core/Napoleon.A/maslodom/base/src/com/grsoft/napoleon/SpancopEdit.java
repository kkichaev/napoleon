package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.CategoryProduct;
import com.grsoft.dataobjects.Chance;
import com.grsoft.dataobjects.ClientLevel;
import com.grsoft.dataobjects.Competitor;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Segment;
import com.grsoft.dataobjects.Spancop;
import com.grsoft.dataobjects.TableValue;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.SpancopImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class SpancopEdit extends Activity {
	private TextView tvName;
	private CheckBox cbYes;
	private Spinner spCategoryProduct;
	private Spinner spSegment;
	private DatePicker dpFirst;
	private EditText edSuccesReason;
	private Spinner spChance;
	private EditText edCub;
	private DatePicker dpS;
	private DatePicker dpP1;
	private DatePicker dpA;
	private DatePicker dpN;
	private DatePicker dpC;
	private DatePicker dpO;
	private DatePicker dpP2;
	private EditText edNotice;
	private EditText edHolding;
	private Spinner spCompetitor;
	private Spinner spClientLevel;
	
	public SpancopImpl doc = new SpancopImpl();

	public static void open(Context context, String org) {
		Intent intent = new Intent(context, SpancopEdit.class);
		intent.putExtra(ExtrasConst.ORG_ID_STR, org);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spancopedit);

		tvName = (TextView) findViewById(R.id.tvName);
		cbYes = (CheckBox) findViewById(R.id.cbYes);
		spCategoryProduct = (Spinner) findViewById(R.id.spCategoryProduct);
		spSegment = (Spinner) findViewById(R.id.spSegment);
		dpFirst = (DatePicker) findViewById(R.id.dpFirst);
		edSuccesReason = (EditText) findViewById(R.id.edSuccesReason);
		spChance = (Spinner) findViewById(R.id.spChance);
		edCub = (EditText) findViewById(R.id.edCub);
		dpS = (DatePicker) findViewById(R.id.dpS);
		dpP1 = (DatePicker) findViewById(R.id.dpP1);
		dpA = (DatePicker) findViewById(R.id.dpA);
		dpN = (DatePicker) findViewById(R.id.dpN);
		dpC = (DatePicker) findViewById(R.id.dpC);
		dpO = (DatePicker) findViewById(R.id.dpO);
		dpP2 = (DatePicker) findViewById(R.id.dpP2);
		edNotice = (EditText) findViewById(R.id.edNotice);
		edHolding = (EditText) findViewById(R.id.edHolding);
		spClientLevel = (Spinner) findViewById(R.id.spClientLevel);
		spCompetitor = (Spinner) findViewById(R.id.spCompetitor);

		String id = getIntent().getStringExtra(ExtrasConst.ORG_ID_STR);
		
		OrgImpl orgImpl = new OrgImpl();
		orgImpl.getData().id = id;
		orgImpl.read();
		orgImpl.close();

		tvName.setText(orgImpl.getData().name);
		fillSpinner(CategoryProduct.class, spCategoryProduct);
		fillSpinner(Segment.class, spSegment);
		fillSpinner(Chance.class, spChance);
		fillSpinner(ClientLevel.class, spClientLevel);
		fillSpinner(Competitor.class, spCompetitor);
		
		if (doc.read(id)) {
			Spancop sp = doc.getData();
			cbYes.setChecked(sp.realclient == 1);
			setSpinnerPos(sp.category, spCategoryProduct);
			setSpinnerPos(sp.segment, spSegment);
			setDatePicker(dpFirst, sp.first);
			edSuccesReason.setText(sp.success);
			setSpinnerPos(sp.chance, spChance);
			edCub.setText(Util.IntToScaleStr(sp.cub, Consts.WEIGHT_SCALE));
			setDatePicker(dpS, sp.s);
			setDatePicker(dpP1, sp.p1);
			setDatePicker(dpA, sp.a);
			setDatePicker(dpN, sp.n);
			setDatePicker(dpC, sp.c);
			setDatePicker(dpO, sp.o);
			setDatePicker(dpP2, sp.p2);
			edNotice.setText(sp.remark);
			edHolding.setText(sp.holding);
			setSpinnerPos(sp.clientLevel, spClientLevel);
			setSpinnerPos(sp.competitor, spCompetitor);
		}else
			doc.init(this, id, GPSUtilNew.getLastKnownLocation());
	}
	
	private void setDatePicker(DatePicker dp, Date val){
		dp.updateDate(val.getYear() + 1900, val.getMonth(), val.getDate());
	}

	private void fillSpinner(Class<? extends TableValue> type, Spinner spinner) {
		try {
			TableValue data = type.newInstance();
			DbReader reader = new DbReader();
			boolean bdo = reader.select(data, DataObjectInfo.getInstance()
					.getTableName(data.getClass()), null);
			ArrayList<KeyValue> list = new ArrayList<KeyValue>();
			
			while (bdo) {
				KeyValue kv = new KeyValue(data.key, data.value);
				list.add(kv);
				bdo = reader.selectNext(data);
			}
			
			ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, list);
			aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
			spinner.setAdapter(aa);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setSpinnerPos(String key, Spinner sp){
		
		for(int i = 0; i < sp.getCount(); i++){
			KeyValue kv = (KeyValue)sp.getItemAtPosition(i);
			
			if(kv.key.equals(key)){
				sp.setSelection(i, true);
				break;
			}
		}
	}
	
	public Date getDatePicker(DatePicker dp){
		return new Date(dp.getYear() - 1900, dp.getMonth(), dp.getDayOfMonth());
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		Spancop sp = doc.getData();
		sp.realclient = cbYes.isChecked() ? 1 : 0;
		sp.category = ((KeyValue)spCategoryProduct.getSelectedItem()).key.toString();
		sp.segment = ((KeyValue)spSegment.getSelectedItem()).key.toString();
		sp.first = getDatePicker(dpFirst);
		sp.success = edSuccesReason.getText().toString();
		sp.chance = ((KeyValue)spChance.getSelectedItem()).key.toString();
		sp.cub = Util.StrToScale(edCub.getText().toString(), Consts.WEIGHT_SCALE);
		sp.s = getDatePicker(dpS);
		sp.p1 = getDatePicker(dpP1);
		sp.a = getDatePicker(dpA);
		sp.n = getDatePicker(dpN);
		sp.c = getDatePicker(dpC);
		sp.o = getDatePicker(dpO);
		sp.p2 = getDatePicker(dpP2);
		sp.remark = edNotice.getText().toString();
		sp.holding = edHolding.getText().toString();
		sp.clientLevel =  ((KeyValue)spClientLevel.getSelectedItem()).key.toString();
		sp.competitor =  ((KeyValue)spCompetitor.getSelectedItem()).key.toString();
		
		doc.setExported(false);
		doc.write();
		doc.close();
	}
}

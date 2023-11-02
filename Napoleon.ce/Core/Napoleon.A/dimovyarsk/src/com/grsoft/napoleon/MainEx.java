package com.grsoft.napoleon;

import java.util.HashMap;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainEx extends Main {
	
	private static final String WEIGHT_SHOW = "WeighShow";

	RadioButton rb1;
	RadioButton rb2;
	RadioButton rb3;
	CheckBox cb;
	
	boolean updatingDialog = false;
	
	
	@Override
	protected Dialog createDocSumDlg() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		View v = View.inflate(this, R.layout.select_doc_period, null);
		adb.setView(v);
		
		AlertDialog weightDialog = adb.create();
		rb1 = (RadioButton)v.findViewById(R.id.rbAll);
		rb1.setOnClickListener(new SetPeriodType(weightDialog, 0));
		rb2 = (RadioButton)v.findViewById(R.id.rbMonth);
		rb2.setOnClickListener(new SetPeriodType(weightDialog, 1));
		rb3 = (RadioButton)v.findViewById(R.id.rbDay);
		rb3.setOnClickListener(new SetPeriodType(weightDialog, 2));
	
		cb = (CheckBox)v.findViewById(R.id.cbWeight);
		cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
				pref.edit().putBoolean(WEIGHT_SHOW, isChecked).commit();
			}
		});
		
		return weightDialog;
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);

		if( docType == OrderDoc.instance() )
			updateColumnText();
	}

	private void updateColumnText() {
		SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
		boolean isWeightShow = pref.getBoolean(WEIGHT_SHOW, false);
		
		TextView tv = (TextView) findViewById(R.id.tvMainDocValColTitle);
		if (tv != null)
			tv.setText(isWeightShow ? "Вес" : "Сумма" );
	}
	
	HashMap<String, Integer> orgsWeight = new HashMap<String, Integer>();
	
	long countDocsWeight(int period) {
		String where = makePeriodWhere(OrderDoc.instance(), period);
		DocList dl = OrderDoc.instance().docList(null, null, where);
		
		long weight = 0;
		
		OrgSumImpl.periodSum = new HashMap<String, Long>();
		for(Document<?> d : dl) {
			long docWeight = (long)((OrderImpl)d).weight() * Consts.SUM_SCALE / Consts.WEIGHT_SCALE;
			weight += docWeight;			
			
			long si = 0;
			if(OrgSumImpl.periodSum.containsKey(d.getId()))
				si = OrgSumImpl.periodSum.get(d.getId());
			
			OrgSumImpl.periodSum.put(d.getId(), docWeight + si);
		}
		
		dl.close();
		return weight;
	}
	
	@Override
	protected void refreshDocSum(DocType docType) {
		if( docType == OrderDoc.instance()) {		
			SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
			int cur_type = pref.getInt(PERIOD_TYPE, 0);
			boolean isWeightShow = pref.getBoolean(WEIGHT_SHOW, false);
			if( isWeightShow ) {
				updateTotalSum(countDocsWeight(cur_type), 0);
				return;
			}
		}		
		super.refreshDocSum(docType);
	}
	
	class SetPeriodType implements View.OnClickListener {
		Dialog d;
		int which;
		
		public SetPeriodType(Dialog d, int which) {
			this.d = d;
			this.which = which;
		}
		
		@Override
		public void onClick(View v) {
			if( !updatingDialog ) {
				d.dismiss();
				// делаем так, чтобы отрисовка была всегда
				getPreferences(Context.MODE_PRIVATE).edit().putInt(PERIOD_TYPE, which-1).commit();
				updateDocView(which);
				updateColumnText();
			}
		}		
	}
	
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if( id == DOC_SUM_DLG ) {
			updatingDialog = true;
			SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
			int ct = pref.getInt(PERIOD_TYPE, 0);
			boolean isWeightShow = pref.getBoolean(WEIGHT_SHOW, false);
			
			cb.setChecked(isWeightShow);
			if( ct == 0 )
				rb1.setChecked(true);
			else if( ct == 1)
				rb2.setChecked(true);
			else if(ct == 2)
				rb3.setChecked(true);
			updatingDialog = false;
		}
		super.onPrepareDialog(id, dialog);
	}
}

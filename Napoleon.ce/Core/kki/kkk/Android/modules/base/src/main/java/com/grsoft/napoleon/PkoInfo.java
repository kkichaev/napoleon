package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.Pko;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PaImpl;
import com.grsoft.dataobjects.impl.PkoImpl;
import com.grsoft.dataobjects.impl.PkoImplBase;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.PkoDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.napoleon.printsources.PkoSource;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class PkoInfo extends BaseActivity implements SendResultListener {
	public static Class<? extends PkoInfo> activity = PkoInfo.class;
	protected static final int SELECT_PRINT_FORM_DLG = 0;
	private static final int WAIT_FOR_PRINT_DLG = 1;
	
	protected EditText edNumber;
	protected PkoImplBase<? extends Pko> pkoImpl = createDocument();

	protected PkoImplBase<? extends Pko> createDocument() { return new PkoImpl(); }
	
	public static Class<? extends PkoSource> PkoSourceType = PkoSource.class;
	private TextView tvSum;
	int selectedFirm;
	
	@Override
	protected void onStop() {
		super.onStop();
		pkoImpl.close();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());
		
		edNumber = (EditText) findViewById(R.id.edNumber);
		
		if (pkoImpl.read(getIntent()
				.getLongExtra(ExtrasConst.DOC_ROW_ID_STR, 
						ExtrasConst.INVALID_ID))){
			edNumber.setText(pkoImpl.getData().number);
			
			final Pko pko = pkoImpl.getData();
			OrgImpl orgImpl = new OrgImpl();
			orgImpl.getData().id = pko.id;
			
			if (orgImpl.read())
				((TextView)findViewById(R.id.tvOrg)).setText(orgImpl.getData().name);
			
			orgImpl.close();
			
			final List<Firm> fv = new ArrayList<Firm>();
			selectedFirm = 0;
			DataTraveler.travel(Firm.class, new DataTraveler.Travel<Firm>(){

				@Override
				public boolean travel(DataTraveler<Firm> item) {
					if( item.data.id.equals(pko.supplyercode) )
						selectedFirm = fv.size();
					fv.add(item.data);
					item.data = new Firm();
					return true;
				}
				
			}, null);
			Spinner spFirm = (Spinner) findViewById(R.id.spFirm);
			ArrayAdapter<Firm> aa = new ArrayAdapter<Firm>(this, R.layout.simple_spinner_layout, fv);
			spFirm.setAdapter(aa);
			if( selectedFirm < aa.getCount())
				spFirm.setSelection(selectedFirm);
			spFirm.setEnabled(pko.salesnumber.length() == 0);

			tvSum = ((TextView)findViewById(R.id.tvSum));
			
			if(tvSum != null){
				updateSumTextView(tvSum);
				tvSum.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if( pkoImpl.isEditable() ) { 
						InputNumberDlg.open(v.getContext(), new InputNumber() {
								
								@Override
								public void applayInput(int value, Object... params) {
									Pko pko = pkoImpl.getData();
									pko.sum = value;
									updateSumTextView(tvSum);
								}
								@Override public int getValue() { return (int)pkoImpl.getData().sum; }
							}, Consts.SUM_SCALE, true,"¬ведите значение");
						}
					}
				});
			}
			
			ImageButton btnPrint = (ImageButton) findViewById(R.id.btnPrint);
			btnPrint.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(final View v) {
					try{
						save();
						PkoSource pko = PkoSourceType.getConstructor(PkoImpl.class).newInstance(pkoImpl);
						SelectPrinFormDlg.createPrintForm((Activity)v.getContext(), 
								pko, WAIT_FOR_PRINT_DLG, "pko",
								new Runnable() {
									@Override
									public void run() {
										pkoImpl.markPrinted();
										pkoImpl.write();
										saveDoc();
									}
								});
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			findViewById(R.id.btnPa).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					save();
					PaImpl paImpl = PaImpl.initFromPay(pkoImpl, 
							new GpsCoord(pkoImpl.getData().latitude, pkoImpl.getData().longitude, pkoImpl.getData().stltime));
					paImpl.open(v.getContext());
					finish();
				}
			});
		}
		
		View v = findViewById(R.id.btnSend);
		if( v != null )
			v.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View arg0) { 
					save();
					send(); 
				}
			});
	}
	
	protected void send() {
		new DocumentSender(this, findViewById(R.id.btnSend), PkoDoc.instance().getObjectName(), 
				pkoImpl, pkoImpl.getRowid(), this).execute((Void[])null);
	}

	protected void updateSumTextView(TextView tvSum) {
		SpannableString ss = new SpannableString(Util.IntToScaleStr(pkoImpl.getData().sum, 
				Consts.SUM_SCALE, Util.DEC_DELIM, false));
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tvSum.setTextColor(Color.BLUE);
		tvSum.setText(ss);
	}

	protected int getLayoutId() {
		return R.layout.pkoinfo;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			if(pkoImpl.getData().sum == 0)
				pkoImpl.delete();
			else
				saveDoc();
		}
		
		return super.onKeyDown(keyCode, event);
	}

	protected void saveDoc() {
		save();
		DocHelper.saveDocNumber(pkoImpl.getTableName(), pkoImpl.getData().number);
		finish();
	}
	
	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, activity);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case WAIT_FOR_PRINT_DLG:
			return SelectPrinFormDlg.createWaitDlg(this);
		default:
			return super.onCreateDialog(id);
		}
	}
	
	protected void adjustPko(){
		Pko pko = pkoImpl.getData();
		pko.number = edNumber.getText().toString();
		
		Spinner spFirm = (Spinner)findViewById(R.id.spFirm);
		Firm sel = (Firm)spFirm.getSelectedItem();
		if( sel != null ) {
			pko.supplyercode = sel.id;
		}
	}
	
	protected void save(){
		if(!pkoImpl.isEditable())
			return;
		
		adjustPko();
		pkoImpl.write();
	
		String id = pkoImpl.getId();
		PkoDoc.instance().refreshDocSum(id);
	}

	@Override
	public void postSendExecute(boolean result) {
		pkoImpl.read(pkoImpl.getRowid(), false);
	}
}

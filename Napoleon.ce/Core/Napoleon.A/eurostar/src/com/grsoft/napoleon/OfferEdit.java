package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import com.grsoft.dataobjects.Agreements;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Offer;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.PayTime;
import com.grsoft.dataobjects.impl.OfferImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;


public class OfferEdit extends Activity {
	private Button btnStart;
	private Button btnFinish;
	private EditText edRemark;
	private Button btnOK;
	private Button btnCancel;
	private boolean editmode;
	private Spinner spFirma;
	private Spinner spPayTime;
	private EditText edNumber;
	private CheckBox cbCash;
	
	private OfferImpl doc = new OfferImpl();
	private Spinner spAgr;
	private int selected;
	
	
	public static void open (Context context, long rowd, boolean editmode){
		Intent intent = new Intent(context, OfferEdit.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowd);
		intent.putExtra(ExtrasConst.EDIT_MODE_STR, editmode);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offeredit);
		inflateView();
		initData();
		initView();
	}

	private void initView() {
		final Offer offer = doc.getData();
		btnStart.setText(Util.simpleDateFormat.format(offer.start));
		btnStart.setTag(offer.start);
		btnStart.setOnClickListener(new OnClickListener() {	@Override public void onClick(View v) { startCalendar(v); } });
		
		btnFinish.setText(Util.simpleDateFormat.format(offer.finish));
		btnFinish.setTag(offer.finish);
		btnFinish.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { startCalendar(v); } });
		
		edRemark.setText(offer.remark);
		
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				save();
				if(!editmode)
					Warehouse.open(v.getContext(), doc, false);
				finish();
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				deleteEmptyDoc();
				finish();
			}
		});
		
		edNumber.setText(offer.number);
		
		OrgImpl org = new OrgImpl();
		org.read("id", doc.getId());
		
		selected = 0;
		final List<Agreements> va = new ArrayList<Agreements>();
		DataTraveler.travel(Agreements.class, new DataTraveler.Travel<Agreements>() {

			@Override
			public boolean travel(DataTraveler<Agreements> item) {
				if(item.data.id.equals(offer.agreement) && selected == 0)
					selected = va.size();
				
				va.add(item.data);
				item.data = new Agreements();
				return true;
			}
		}, "common=1 or idOrg = '" + ((OrgEx)org.getData()).ido + "'", "common,name");
		
		BaseAdapter adapter = new ArrayAdapter<Agreements>(this,  R.layout.simple_spinner_layout, va);
		spAgr.setAdapter(adapter);
		if( selected < va.size() )
			spAgr.setSelection(selected);
	}
	
	
	
	@Override
	public void onBackPressed() {
		deleteEmptyDoc();
		super.onBackPressed();
	}

	private void deleteEmptyDoc() {
		if(!editmode && doc.getData().items.size() == 0){
			doc.delete();
			doc.close();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data != null){
			Button btn = (Button) findViewById(requestCode);
			
			if(btn != null){
				long ct = data.getLongExtra(ExtrasConst.DATE_TAG, ((Date)btn.getTag()).getTime());
				Date dt = new Date(ct);
				btn.setText(Util.simpleDateFormat.format(dt));
				btn.setTag(dt);
			}
		}
	}

	private void save() {
		Offer offer = doc.getData();
		offer.start = (Date) btnStart.getTag();
		offer.finish = (Date) btnFinish.getTag();
		offer.remark = edRemark.getText().toString().trim();
		offer.firmCode = ((Firm)spFirma.getSelectedItem()).id;
		offer.number = edNumber.getText().toString().trim();
		if( cbCash.isChecked() ) offer.params |= ParamState.ofCash; else offer.params &= (~ParamState.ofCash);
		offer.paytime = ((PayTime)spPayTime.getSelectedItem()).id;
		
		Agreements agr = (Agreements) spAgr.getSelectedItem();
		if(agr != null)
			offer.agreement = agr.id;
		
		doc.write();
		doc.close();
	}
	
	private void initData() {
		Intent intent = getIntent();
		editmode = intent.getBooleanExtra(ExtrasConst.EDIT_MODE_STR, false);
		doc.read(intent.getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		doc.close();
		
		final Offer offer = doc.getData();
		
		initSpinner(FirmEx.class, spFirma, new SelectItem() {
			@Override
			public boolean select(Object object) {
				Firm f = (Firm)object;
				return f.id.equals(offer.firmCode);
			}
		});
		
		spFirma.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				doc.getData().firmCode = ((FirmEx)parent.getItemAtPosition(position)).id;
				doc.getData().number = DocHelper.makeDocNumber(doc);
				edNumber.setText(doc.getData().number);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}});
		
		initSpinner(PayTime.class, spPayTime, new SelectItem() {
			@Override
			public boolean select(Object object) {
				PayTime p = (PayTime) object;
				return p.id.equals(offer.paytime);
			}
		});
		
		cbCash.setChecked((offer.params & ParamState.ofCash) == ParamState.ofCash);
	}
	
	interface SelectItem {
		boolean select(Object object);
	}
	
	private <T extends DataObject> void initSpinner(final Class<T> dataType, Spinner spinner, SelectItem select){
		final List<T> firms = new ArrayList<T>();
		
		DataTraveler.travel(dataType, new DataTraveler.Travel<T>() {
			@Override
			public boolean travel(DataTraveler<T> item) {
				try{
					firms.add(item.data);
					item.data = dataType.newInstance();
					return true;
				}catch(Exception e){
					e.printStackTrace();
					return false;
				}
			}}, null);
		
		BaseAdapter adapter = new ArrayAdapter<T>(this,  R.layout.simple_spinner_layout, firms);
		spinner.setAdapter(adapter);
		
		for(int i=0; i < adapter.getCount(); i++)
			if (select.select(adapter.getItem(i))){
				spinner.setSelection(i, true);
				break;
			}
	}

	private void inflateView() {
		btnStart = (Button) findViewById(R.id.btnStart);
		btnFinish = (Button) findViewById(R.id.btnFinish);
		edRemark = (EditText) findViewById(R.id.edRemark);
		btnOK = (Button) findViewById(R.id.btnOK);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		spFirma = (Spinner) findViewById(R.id.spFirms);
		spPayTime = (Spinner) findViewById(R.id.spPayTime);
		edNumber = (EditText) findViewById(R.id.edNumber);
		cbCash = (CheckBox) findViewById(R.id.cbCash);
		spAgr = (Spinner)findViewById(R.id.spAgr);
	}

	protected void startCalendar(View v) {
		Intent i = new Intent(v.getContext(), CalendarActivity.class);
		i.putExtra(ExtrasConst.DATE_TAG, ((Date)v.getTag()).getTime());
		startActivityForResult(i, v.getId());
	}
}

package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Dogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PriceType;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.Sklads;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.view.BaseActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class CreateSalesEx extends BaseActivity {
	private boolean editMode = false;
	String clientid;
	boolean canSaveWithoutDogovor = false;

	private ArrayList<Sklads> sklads = new ArrayList<Sklads>();
	List<PriceType> prcItems = null;

	SalesImplEx salesImpl = new SalesImplEx(); 
	
	int selItem = -1;
	int inited = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createsalesex);
		init();
	}

	private void init() {
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
				
		salesImpl.read(orderRowId);
		final SalesEx o = (SalesEx) salesImpl.getData();
		
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(!salesImpl.isEditable());
		btnOK.setOnClickListener(new OKClickListener());

		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx)oi.getData();
		oe.id = o.id;
		oi.read();
		oi.close();
		((TextView) findViewById(R.id.tvOrgName)).setText(oe.name);
		clientid = oe.clientid;

		loadSklads(o);
				
		if( !editMode ) 
			initOrder(o, oe);

		ConfigImpl config = new ConfigImpl();
		
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		com.grsoft.dataobjects.Config cfg = config.getData(); 
		cfg.key = "ћожно»змен€ть÷ену";
		try {
			if (config.read() && Integer.parseInt(cfg.value) == 0)
				spPrices.setEnabled(false);
			cfg.key = "ћожно—оздаватьЅезƒоговора";
			if( config.read() && Integer.parseInt(cfg.value) != 0 )
				canSaveWithoutDogovor = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		config.close();
		
		((EditText) findViewById(R.id.edNumber)).setText(o.number);
		
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);
		
        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
	}

	private void loadSklads(final SalesEx o) {
		selItem = -1;

		DataTraveler.travel(Sklads.class, new DataTraveler.Travel<Sklads>(){
			@Override
			public boolean travel(DataTraveler<Sklads> item) {
				if( !editMode ) {
					if( sklads.size() == 0 ) {
						o.storeid = item.data.id;
						selItem = sklads.size();
					}
				} else {
					if( o.storeid.equals(item.data.id))
						selItem = sklads.size();
				}
				sklads.add(item.data);
				item.data = new Sklads();
				return true;
			}
			
		}, "van=1");
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		ArrayAdapter<Sklads> aa = new ArrayAdapter<Sklads>(this, R.layout.simple_spinner_layout, sklads);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		spFirma.setAdapter(aa);
		if( selItem >= 0 )
			spFirma.setSelection(selItem);
		
		spFirma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if( (inited & 1)  == 0)
					inited |= 1;
				else
					refreshDogovors(true); 
			}
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
	
		refreshDogovors(!editMode);
	}


	protected void refreshDogovors(final boolean updateOrder) {
		selItem = -1;
		final SalesEx o = (SalesEx)salesImpl.getData();
		
		Sklads sel = (Sklads)((Spinner) findViewById(R.id.spFirma)).getSelectedItem();
		if( sel == null )
			return;
		
		final List<Dogovor> dogovors = new ArrayList<Dogovor>();
		DataTraveler.travel(Dogovor.class, new DataTraveler.Travel<Dogovor>(){
			@Override
			public boolean travel(DataTraveler<Dogovor> item) {
				if( updateOrder ) {
					if( item.data.def != 0 ) {
						o.contractid = item.data.id;
						selItem = dogovors.size();
					}
				} else {
					if( o.contractid.equals(item.data.id))
						selItem = dogovors.size();
				}
				dogovors.add(item.data);
				item.data = new Dogovor();
				return true;
			}
			
		}, "clientid='" + clientid + "' and companyid='" + sel.idOrg + "'");
		
		Spinner spDog = (Spinner) findViewById(R.id.spDogovor);
		ArrayAdapter<Dogovor> aa = new ArrayAdapter<Dogovor>(this, R.layout.simple_spinner_layout, dogovors);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		spDog.setAdapter(aa);
		
		if( selItem >= 0 && selItem < dogovors.size())
			spDog.setSelection(selItem);
		
		((TextView)findViewById(R.id.tvDogInfo)).setText(Html.fromHtml(""));
		
		if( dogovors.size() == 0 && updateOrder )
			o.contractid = "";

		findViewById(R.id.btnOK).setEnabled(salesImpl.isEditable() && (dogovors.size() > 0 || canSaveWithoutDogovor));

		spDog.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) { 
				Dogovor selDog =(Dogovor)(arg0.getAdapter().getItem(arg2)); 
				String di = selDog == null ? "" : selDog.info;
				((TextView)findViewById(R.id.tvDogInfo)).setText(Html.fromHtml(di));

				if( (inited & 2)  == 0)
					inited |= 2;
				else {
					refreshCost(true);
				}
			}
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
	
		refreshCost(!editMode);
	}

	protected void refreshCost(boolean updateOrder) {

		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		final SalesEx o = (SalesEx)salesImpl.getData();

		String selid = "";
		if( updateOrder ) {
			final Dogovor sel = (Dogovor)((Spinner) findViewById(R.id.spDogovor)).getSelectedItem();
			if( sel != null ) 
				selid = sel.priceid;

			if( selid.length() == 0 ) {
				Sklads sels = (Sklads)((Spinner) findViewById(R.id.spFirma)).getSelectedItem();
				if( sels != null )
					selid = sels.priceid;
			}
		} else
			selid = o.priceid;

		if( prcItems == null ) {
			prcItems = new ArrayList<PriceType>();
			DataTraveler.travel(PriceType.class, new DataTraveler.Travel<PriceType>() {

				@Override
				public boolean travel(DataTraveler<PriceType> item) {
					prcItems.add(item.data);
					item.data = new PriceType();
					return true;
				}
			}, "");

			ArrayAdapter<PriceType> aprc = new ArrayAdapter<PriceType>(this, R.layout.simple_spinner_layout, prcItems);
			aprc.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
			spPrices.setAdapter(aprc);
		}

		Adapter a = spPrices.getAdapter();
		for( int i=0; i<a.getCount(); i++) {
			PriceType pt = (PriceType)a.getItem(i);
			if( pt != null ) {
				if( pt.id.equals(selid) )  {
					spPrices.setSelection(i);
					break;
				}
			}
		}
	}

	private void initOrder(SalesEx o, OrgEx org) {
		o.sumType = org.costype;
	}

	@Override
	protected void onStop() {
		salesImpl.close();
		super.onStop();
	}

	class CancelClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			deleteEmptyDoc();			
			finish();
		}
	}
	
	private void deleteEmptyDoc() {
		if(!editMode) {
			if( salesImpl.getData().items == null || salesImpl.getData().items.size() == 0 )
				salesImpl.delete();
		}
	}

	protected void okDone(Context context, boolean updateSumType) {
		SalesEx sales = (SalesEx) salesImpl.getData();
		
		if (sales.created == null)
			sales.created = new Date();
		
		sales.number = ((EditText) findViewById(R.id.edNumber)).getText().toString();
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		int suppl = spFirma.getSelectedItemPosition();
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		PriceType pt = (PriceType) spPrices.getSelectedItem();

		if( suppl >= 0 ) {
			sales.supplyer = suppl;
			Sklads skl = (Sklads)spFirma.getSelectedItem();
			sales.supplyercode = skl.idOrg;
			sales.storeid = skl.id;
		}
		
		if( pt != null )
			sales.priceid = pt.id;
		else
			sales.priceid = "";
		
		Spinner spDog = (Spinner)findViewById(R.id.spDogovor);
		Dogovor selDg = (Dogovor) spDog.getSelectedItem();
		if(selDg != null)
			sales.contractid = selDg.id;
		else
			sales.contractid = "";

		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		sales.remark = remark.getText().toString();
		
		salesImpl.write();
		
		DocHelper.saveDocNumber(salesImpl.getTableName(), salesImpl.getData().number);
		if(!editMode)
			Warehouse.open(context, salesImpl, false);
		
		finish();
	}

	class OKClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			okDone(v.getContext(), false);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK){
				deleteEmptyDoc();
				finish();
				return true;
			}else
				return super.onKeyDown(keyCode, event);
	}
}

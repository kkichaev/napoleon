package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDog;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ReturnRequestImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.view.BaseActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class CreateReturnRequest extends BaseActivity {

	private static final int ASK_CLEAR_ITEMS = 10;
	protected static final int DIALOG_DATE_PICKER_ID = 0;
	boolean editMode;
	OrderImplBase<? extends Order> doc;
	List<OrgDog> dogs = new ArrayList<OrgDog>();
	int selected = -1;
	
	public static void open(Context context, OrderImplBase<? extends Order> doc, boolean editOldOrder) {
		Intent i = new Intent(context, CreateReturnRequest.class);
		
		i.putExtra(ExtrasConst.EDIT_MODE_STR, editOldOrder);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		context.startActivity(i);		
	}
	
	protected OrderImplBase<? extends Order> createDoc() { return new ReturnRequestImpl(); }
	protected int getLayoutID() { return R.layout.createreturn; }
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(getLayoutID());

		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long rid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		
		doc = createDoc();
		doc.read(rid);
		
		final Order rr = doc.getData();
		OrgImpl oi = new OrgImpl();
		oi.getData().id = rr.id;
		oi.read();
		oi.close();

		Org org = (Org) oi.getData();		
		String ret = org.name;
		if(Features.SHOW_ORG_ADDRESS && org.address.length() > 0 ) {
			ret += "<br><i>" + org.address + "</i>";
		}		
		((TextView) findViewById(R.id.tvOrgName)).setText(Html.fromHtml(ret));
		
		boolean haveFirms = loadFirms(rr, org);		

		refreshDate();
		initDateView();
		

//		ConfigImpl config = new ConfigImpl();		
//		CreateOrder.loadFirms(config, spFirma, rr.firmCode, rr.id, dogs);

		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(doc.isEditable());
		btnOK.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { 
				if(!saveChanges())
					return;
				if(!editMode)
					openPrice();
				finish();
			}
		});
		btnOK.setEnabled(haveFirms);

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				deleteEmptyDoc();
				finish();
			}
		});
	}

	protected void openPrice() {
		Warehouse.open(this, doc, false);
	}
	
	protected void initDateView() {
		if(doc.getData().items.size() != 0) {
			findViewById(R.id.tvDate).setEnabled(false);
			return;
		}
		
		findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, doc.getDate().getTime());
				startActivityForResult(i, DIALOG_DATE_PICKER_ID);
			}
		});
	}

	protected void refreshDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());		
		((TextView)findViewById(R.id.tvDate)).setText(sd.format(doc.getDate()));		
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null ) {
			if( requestCode == DIALOG_DATE_PICKER_ID ) {
				Date curDate = new Date();
				long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
				doc.getData().date = new Date(ct);
				refreshDate();
			}
		}
	}
	
	private boolean loadFirms(final Order rr, Org org) {
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		final Map<String, FirmEx> allFirms = FirmEx.get();
		final List<KeyValue> firms = new ArrayList<KeyValue>();
		final Set<String> used = new HashSet<String>();
		selected = -1;
		DataTraveler.travel(DeliveryEx.class, new DataTraveler.Travel<DeliveryEx>() {

			@Override
			public boolean travel(DataTraveler<DeliveryEx> item) {
				String firm = item.data.firm;
				if(used.contains(firm) == false) {
					used.add(firm);
					FirmEx f = allFirms.get(firm);
					if( f != null ) {
						KeyValue kv = new KeyValue(f.id, f.name);
						if(f.id.equals(rr.firmCode))
							selected = firms.size();
						firms.add(kv);
					}
				}
				return true;
			}
		}, "id='" + org.id + "'");
		ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(spFirma.getContext(), R.layout.simple_spinner_layout, firms);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		spFirma.setAdapter(aa);
		if( selected >= 0 )
			spFirma.setSelection(selected);
	
		if( !editMode && spFirma.getCount() > 0) {
			spFirma.setSelection(0);
			rr.supplyer = 0;
			rr.firmCode = ((KeyValue)spFirma.getSelectedItem()).key.toString();
		}
		
		return (firms.size() > 0);
	}
	
	@Override
	public void onBackPressed() {
		deleteEmptyDoc();
		super.onBackPressed();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == ASK_CLEAR_ITEMS) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Предупреждение");
			b.setMessage("При изменении фабрики документ очиститься. Удалить все товары?");
			b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Order rr = doc.getData();
					Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
					int suppl = spFirma.getSelectedItemPosition();
					String newFirm = ((KeyValue)spFirma.getSelectedItem()).key.toString();
					
					rr.supplyer = suppl;
					rr.firmCode = newFirm;
					rr.items.clear();
					doc.write();
					
					arg0.dismiss();
				}
			});
			
			b.setNegativeButton(android.R.string.no, null);
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	protected boolean saveChanges() {
		Order rr = doc.getData();
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		int suppl = spFirma.getSelectedItemPosition();
		if( suppl >= 0 ) {
			String newFirm = ((KeyValue)spFirma.getSelectedItem()).key.toString();
			if(rr.firmCode.equals(newFirm) == false && rr.items.size() > 0) {
				showDialog(ASK_CLEAR_ITEMS);
				return false;
			}
			rr.supplyer = suppl;
			rr.firmCode = newFirm;
		}
		doc.write();
		return true;
	}

	void deleteEmptyDoc() {
		if(!editMode && doc.getData().items == null || doc.getData().items.size() == 0)
			doc.delete();
	}
}

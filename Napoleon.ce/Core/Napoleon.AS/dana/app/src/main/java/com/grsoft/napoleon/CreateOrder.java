/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Создать накладную
 *
 * kki   24/11/2010   creating
 */
package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.ConfigHelper;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DisableOrg;
import com.grsoft.dataobjects.GoodProject;
import com.grsoft.dataobjects.GoodProjectEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgAddress;
import com.grsoft.dataobjects.OrgCost;
import com.grsoft.dataobjects.OrgCostItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Sklad;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.util.view.dialog_helper.TimeHandler;
import com.grsoft.view.BaseActivity;

@SuppressWarnings("deprecation")
public class CreateOrder extends BaseActivity
{
	private OrderImpl order = (OrderImpl)OrderDoc.instance().create();
	
	private static final int DIALOG_DATE_PICKER_ID = 0;
	private static final int DIALOG_TIME_PICKER_ID = 1;
	
	private boolean editMode = false;
	
//	DateHandler dateHandler;
	TimeHandler timeHandler;
	List<Sklad> stores = new ArrayList<>();
	Map<Object, DisableOrg> dsbl = new HashMap<>();

	Map<String, String> firmCost = new HashMap<>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createorder);
		init();
	}

	public static void open(Context context, OrderImpl order) {
		open(context, order, true); 
	}
	
	public static void open(Context context, OrderImpl order, boolean editOldOrder) {
		Intent i = new Intent(context, CreateOrder.class);
		
		i.putExtra(ExtrasConst.EDIT_MODE_STR, editOldOrder);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());

		context.startActivity(i);		
	}

	void refreshSklads(String base, OrderEx o) {
		if(stores.size() == 0) {
			DataTraveler.travel(Sklad.class, new DataTraveler.Travel<Sklad>(true) {
				@Override
				public boolean travel(DataTraveler<Sklad> item) {
					stores.add(item.data);
					return true;
				}
			}, "");
		}

		int selected = 0;
		List<Sklad> values = new ArrayList<>();
		for(Sklad s : stores) {
			if(s.base.equals(base)) {
				if(s.id.equals(o.whCode)) {
					selected = values.size();
				}
				values.add(s);
			}
		}
		Collections.sort(values);
		Spinner spSklad = (Spinner) findViewById(R.id.spSklad);
		ArrayAdapter<Sklad> aa = new ArrayAdapter<Sklad>(this, R.layout.simple_spinner_layout, values);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		spSklad.setAdapter(aa);
		if(selected < values.size())
			spSklad.setSelection(selected);

	}
	
	private void init() {
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
				
		order.read(orderRowId);
		final OrderEx o = (OrderEx) order.getData();
		
		OrgImpl oi = new OrgImpl();
		oi.getData().id = o.id;
		oi.read();
		oi.close();

		OrgEx org = (OrgEx) oi.getData();
		String ret = org.name;
		if(Features.SHOW_ORG_ADDRESS && org.address.length() > 0 ) {
			ret += "<br><i>" + org.address + "</i>";
		}		
		((TextView) findViewById(R.id.tvOrgName)).setText(Html.fromHtml(ret));

		if( !editMode ) 
			initOrder(o, org);

		List<OrgCost> ocst = DbReader.fetch(OrgCost.class, "id='" + org.id + "'");
		for(OrgCost oc : ocst) {
			for(OrgCostItem oci : oc.items) {
				firmCost.put(oci.firm, oci.cost);
			}
		}

		dsbl = DbReader.fetchDic(DisableOrg.class, "idOrg", "id='" + org.id + "'");
		boolean isEmpty = o.items.size() == 0;
		String where = "";
		if(!isEmpty) {
			where = "base = '" + o.base + "'";
		}
		GoodProjectEx.dsbl = dsbl;
		Spinner spProject = (Spinner) findViewById(R.id.spProject);
		DialogHelper.loadSpinnerFromDataObject(spProject, GoodProjectEx.class, new DialogHelper.Selected<GoodProjectEx>() {
			@Override public boolean isSelected(GoodProjectEx object) { return object.id.equals(o.project); }
		}, false, "name", where);

		if(isEmpty) {
			spProject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
					GoodProject prj = (GoodProject) adapterView.getAdapter().getItem(i);
					refreshSklads(prj.base, o);
					String cid = firmCost.get(prj.idOrg);
					if(cid != null) {
						o.prcType = cid;
					}
				}
				@Override public void onNothingSelected(AdapterView<?> adapterView) {}
			});
		} else {
			refreshSklads(o.base, o);
			findViewById(R.id.spSklad).setEnabled(false);
		}

		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);

		CheckBox cb = (CheckBox)findViewById(R.id.cbCreateOrderCash);
		if( (o.params & ParamState.ofCash) != 0 )
			cb.setChecked(true);
		cb.setEnabled(org.onluCash == 0);

		((CheckBox)findViewById(R.id.cbSF)).setChecked(o.sf != 0);
		
		findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(CreateOrder.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, order.getDate().getTime());
				startActivityForResult(i, DIALOG_DATE_PICKER_ID);
			}
		});
		
//		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), o.date, DIALOG_DATE_PICKER_ID);
		timeHandler = new TimeHandler((TextView)findViewById(R.id.tvTime), o.date, DIALOG_TIME_PICKER_ID);
		
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(order.isEditable());
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
		refreshDate();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null && requestCode == DIALOG_DATE_PICKER_ID ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Date newDate = new Date(ct);
			order.getData().date = newDate;
			refreshDate();
		}
	}
	
	private void refreshDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());		
		((TextView)findViewById(R.id.tvDate)).setText(sd.format(order.getDate()));		
	}

	/**
	 * инициализация дополнительных полей заявки (индивидуально для проекта)
	 * @param o
	 */
	private void initOrder(OrderEx o, OrgEx org) {
		o.sumType = org.costype;
		if(org.onluCash > 0)
			o.params |= ParamState.ofCash;

		switch(ConfigHelper.getDateType()){
		case workday:
			dateworkday(o);
			break;
		case nextday:
			datenextday(o);
			break;
		default:
			break;
		}
	}
	
	private void datenextday(Order o) {
		Calendar c = Calendar.getInstance();
		c.setTime(o.date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		o.date = c.getTime();	
	}

	private void dateworkday(Order o) {
		Calendar c = Calendar.getInstance();
		c.setTime(o.date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		
		if( c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY )
			c.add(Calendar.DAY_OF_MONTH, 1);
		
		o.date = c.getTime();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
//			case DIALOG_DATE_PICKER_ID:
//				return dateHandler.createDialog();
			case DIALOG_TIME_PICKER_ID:
				return timeHandler.createDialog();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onStop() {
		order.close();
		super.onStop();
	}
	

	class CancelClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			deleteEmptyOrder();			
			finish();
		}
	}
	
	private void deleteEmptyOrder() {
		if(!editMode) {
			if( order.getData().items == null || order.getData().items.size() == 0 )
				order.delete();
		}
	}
	
	class OKClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			okDone(false);
		}
		
		private void okDone(boolean updateSumType) {
			OrderEx o = (OrderEx) order.getData();
			o.date = timeHandler.adjustTime(o.date);
//			o.date = timeHandler.adjustTime(dateHandler.getDate());
			
			if (o.created == null)
				o.created = new Date();
			
			Spinner spProject = (Spinner) findViewById(R.id.spProject);
			GoodProject gp = (GoodProject) spProject.getSelectedItem();
			if(gp != null) {
				o.project = gp.id;
				o.base = gp.base;
			}

			Sklad s = (Sklad) ((Spinner)findViewById(R.id.spSklad)).getSelectedItem();
			if(s != null) {
				o.whCode = s.id;
				o.whIndex = s.index;
			}

			CheckBox cash = (CheckBox)findViewById(R.id.cbCreateOrderCash);
			if( cash.isChecked() ) o.params |= ParamState.ofCash;
			else o.params &= (~ParamState.ofCash);

			o.sf = ((CheckBox)findViewById(R.id.cbSF)).isChecked() ? 1 : 0;

			if(gp != null) {
				DisableOrg dso = dsbl.get(gp.idOrg);
				if (dso != null) {
					if (dso.block > 0) {
						Toast.makeText(CreateOrder.this, R.string.sell_disabled, Toast.LENGTH_LONG).show();
						return;
					}
					if (dso.creditDisable > 0 && !cash.isChecked()) {
						Toast.makeText(CreateOrder.this, R.string.credit_disabled, Toast.LENGTH_LONG).show();
						return;
					}
				}
			}

			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			o.remark = remark.getText().toString();
			
			order.write();
			
			if(!editMode)
				Warehouse.open(CreateOrder.this, order, false);
			
			finish();
		}
		
		private void askToApplyNewSumType(Context context, final int newSumType){
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Внимание");
			builder.setMessage("Тип цены был изменен, пересчитать заказ?");

			builder.setPositiveButton("Пересчитать", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					okDone(true);
				}
			});
			
			builder.setNegativeButton("Оставить", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					okDone(false);
				}
			});
			
			builder.create().show();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK){
				deleteEmptyOrder();
				finish();
				return true;
			}else
				return super.onKeyDown(keyCode, event);
	}
}

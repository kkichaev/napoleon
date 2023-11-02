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
import java.util.Date;
import java.util.List;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgTask;
import com.grsoft.dataobjects.VisitEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgTaskImpl;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.ConfigAgama;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.view.BaseActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class CreateOrder extends BaseActivity
{
	private OrderImpl order = (OrderImpl)OrderDoc.instance().create();
	OrgImpl oi = new OrgImpl();
	
	private static final int DIALOG_DATE_PICKER_ID = R.id.dialog_date_picker_id;
	private static final int DIALOG_DLV_DATE_PICKER_ID = R.id.dialog_dlv_date_picker_id;
	private static final int NOTIFY_TASK_DLG = R.id.notify_task_dlg;
	
	private boolean editMode = false;
	private List<Long> curTaskList;
		
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
	
	private void init() {
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
				
		order.read(orderRowId);
		OrderEx o = (OrderEx) order.getData();
		
		OrgEx oe = (OrgEx)oi.getData();
		oe.id = o.id;
		oi.read();
		oi.close();
		((TextView) findViewById(R.id.tvOrgName)).setText(oe.name);

		if( !editMode ) 
			initOrder(o, oe);

		ConfigImpl config = new ConfigImpl();
		Config c = config.getData();
		
		ArrayList<CharSequence> priceType = new ArrayList<CharSequence>();
		c.key = "ВидЦены";
		config.read();
		DialogHelper.makeList(c.value, priceType);
		if( oe.costype < priceType.size() && oe.costype >= 0 ) {
			TextView tv = (TextView)findViewById(R.id.tvCostType);
			String text = "Тип цены: " + priceType.get(oe.costype).toString();
			tv.setText(text);
		}

		config.close();
		
		ConfigAgama cfg = (ConfigAgama)ConfigManager.getConfig();
		if( cfg.autoVisit )
			((CheckBox)findViewById(R.id.cbCreateVisit)).setChecked(true);
		
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);

		findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(CreateOrder.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, order.getDate().getTime());
				startActivityForResult(i, DIALOG_DATE_PICKER_ID);
			}
		});
		
		findViewById(R.id.tvDlvDate).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(CreateOrder.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, ((OrderEx)order.getData()).supplDate.getTime());
				startActivityForResult(i, DIALOG_DLV_DATE_PICKER_ID);
			}
		});
		
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(!order.isExported());
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
		refreshDate();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id){
		case NOTIFY_TASK_DLG: return createNotifyTaskDlg();
		default: return super.onCreateDialog(id);
		}
	}
	
	private Dialog createNotifyTaskDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.current_tasks);
		builder.setMessage("");
		return builder.create();
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id){
		case NOTIFY_TASK_DLG: prepareNotifyTaskDlg(dialog);
		default: super.onPrepareDialog(id, dialog);
		}
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("dd:MM");
	
	private void prepareNotifyTaskDlg(Dialog dialog) {
		StringBuilder sb = new StringBuilder();
		OrgTaskImpl taskImpl = new OrgTaskImpl();
		
		int cnt = 1;
		for(Long rowid: curTaskList){
			if(taskImpl.read(rowid)){
				OrgTask task = taskImpl.getData();
				StringBuilder range = new StringBuilder();
				range.append(sdf.format(task.start)).append(" - ")
						.append(sdf.format(task.finish));
				sb.append(cnt++).append(") ").append(range.toString()).append("<br>");
				sb.append(task.text);
			}
		}
		
		taskImpl.close();
		
		((AlertDialog)dialog).setMessage(Html.fromHtml(sb.toString()));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( order.isExported() )
			return;
		
		if( data != null && requestCode == DIALOG_DATE_PICKER_ID ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Date newDate = new Date(ct);
			order.getData().date = newDate;
			refreshDate();
		}
		if( data != null && requestCode == DIALOG_DLV_DATE_PICKER_ID ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Date newDate = new Date(ct);
			((OrderEx)order.getData()).supplDate = newDate;
			refreshDate();
		}
	}
	
	private void refreshDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");		
		((TextView)findViewById(R.id.tvDate)).setText(sd.format(order.getDate()));		
		((TextView)findViewById(R.id.tvDlvDate)).setText(sd.format(((OrderEx)order.getData()).supplDate));		
	}

	/**
	 * инициализация дополнительных полей заявки (индивидуально для проекта)
	 * @param o
	 */
	private void initOrder(OrderEx o, OrgEx org) {
		o.sumType = org.costype;
		o.supplDate = o.date;
		o.whIndex = ((ConfigAgama)ConfigManager.getConfig()).whDefault;
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
			
			OrderEx o = (OrderEx) order.getData();
			
			if (o.created == null)
				o.created = new Date();
			
			EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
			o.remark = remark.getText().toString();			
			order.write();
			
			if(!editMode && ((CheckBox)findViewById(R.id.cbCreateVisit)).isChecked()) {
				VisitImpl vi = new VisitImpl();
				GpsCoord coord = new GpsCoord(o.latitude, o.longitude);
				vi.init(null, o.id, coord);
				VisitEx vis = (VisitEx)vi.getData();
				vis.remark = "Автовизит";
				vi.write();
				vi.close();
			}
			
			if(!editMode)
				Warehouse.open(CreateOrder.this, order, false);
			
			finish();
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

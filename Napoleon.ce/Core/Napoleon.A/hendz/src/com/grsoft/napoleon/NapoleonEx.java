package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Odometr;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.dataobjects.impl.VandSellImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.VandSellDoc;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

public class NapoleonEx extends Napoleon {
	private ImageButton btnStart;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * ѕровер€ет включен ли рабочий день
	 * @return если началс€ новый день, а старый не закончен вернет false
	 */
	boolean checkIsStarted() {
		boolean ret = false;
		
		Odometr odmr = getOpenedOdometr();
		if( odmr != null ) {
			ret = true;
			
			long startDay = odmr.start.getTime() / (24 * 3600000);
			long curDay = Calendar.getInstance().getTime().getTime() / (24 * 3600000);
			if( curDay > startDay ) {
				ret = false;
				showDialog(R.id.odometr_dlg);
			}
		}
		
		return ret;
	}
	
	@Override
	protected void refreshDocSum(DocType docType) {
		if( docType != VandSellDoc.instance()) {
			super.refreshDocSum(docType);
			return;
		}
			
		SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
		int cur_type = pref.getInt(PERIOD_TYPE, 0);
		if (docType == DebtDoc.instance()) cur_type=0;		
		if(cur_type > 0){
			long sum = 0;
			int qty = 0;
			
			String where = makePeriodWhere(docType, cur_type);
			com.grsoft.napoleon.documents.DocList list = docType.docList(null, null, where);
			
			for( int i=0; i<list.getCount(); i++ ) {
				VandSellImpl d = (VandSellImpl) list.get(i);
				sum += d.sum();
				qty += d.qty();
			}
			
			list.close();
			
			
			updateTotalSum(sum, 0, qty);
		}else
			updateTotalSum(OrgSumImpl.docSum(docType.getName()), 0);
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		boolean started = checkIsStarted();
		
		btnStart = (ImageButton) findViewById(R.id.btnStart);
		btnStart.setImageResource(started ? R.drawable.stop : R.drawable.start);
		btnStart.setOnClickListener(new OnClickListener() { @Override
			public void onClick(View v) {
//				DbReader reader = new DbReader();
//				Odometr data = new Odometr();
//				Calendar cal = Calendar.getInstance();
//				long st = Util.getDate().getTime();
//				cal.setTimeInMillis(st);
//				cal.add(Calendar.DATE, 1);
//				long ft = cal.getTimeInMillis();
//				StringBuilder where = new StringBuilder();
//				where.append("start >=").append(st).append(" and start <=").append(ft).append(" and end is not null or end = 0");
//				
//				boolean bdo = reader.select(data, DataObjectInfo.getInstance().getTableName(data.getClass()), where.toString());
//				reader.close();
//				
//				if(bdo){
//					SQLiteDatabase db = DataBaseManager.getDataBase();
//					where.setLength(0);
//					where.append("update ").append(DataObjectInfo.getInstance().getTableName(Odometr.class)).append(" SET end = null, end_lat=0, end_long=0, end_odo=0, end_rest = 0 WHERE start = ?");
//					db.execSQL(where.toString(), new Object[]{data.start.getTime()});
//					
//					btnStart.setImageResource(R.drawable.stop);
//				}else
					showDialog(R.id.odometr_dlg);	
			}
		});
	}
	
	@Override
	protected int getResourceID() { return R.layout.mainex; }
	
	public static Odometr getOpenedOdometr(){
		DbReader reader = new DbReader();
		Odometr result = new Odometr();
		boolean bdo = reader.select(result, DataObjectInfo.getInstance().getTableName(result.getClass()), "end is null or end = 0");
		reader.close();
		
		return bdo ? result : null;
	}
	public static boolean isStarted(){
		return getOpenedOdometr() != null;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id){
		case R.id.odometr_dlg:
			return createOdometrDlg();
		default:
			return super.onCreateDialog(id);
		}
	}

	private Dialog createOdometrDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.odometr_title);
		builder.setView(View.inflate(this, R.layout.odometredit, null));
		builder.setPositiveButton(R.string.ok, null);
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id){
		case R.id.odometr_dlg:
			prepareOdometrDlg(dialog);
		default:
			super.onPrepareDialog(id, dialog);
		}
	}

	private void prepareOdometrDlg(Dialog dialog) {
		OdometrDlgHelper helper = getDialogHelper((AlertDialog)dialog);
		helper.init();
	}
	
	private OdometrDlgHelper getDialogHelper(AlertDialog dialog){ return isStarted() ? new FinishOdometrDlgHelper(dialog) : new StartOdometrDlgHelper(dialog); }
	
	abstract class OdometrDlgHelper{
		private AlertDialog dialog;
		private EditText edOdometr;
		private EditText edRest;
		protected EditText edRefuel;
		
		class Data{
			int odo = 0;
			int rest = 0;
			int refuel = 0;
		}
		
		public OdometrDlgHelper(AlertDialog dialog){
			this.dialog = dialog;
			edRest = (EditText) dialog.findViewById(R.id.edFuelRest);
			edOdometr = (EditText) dialog.findViewById(R.id.edOdometr);
			edRefuel = (EditText) dialog.findViewById(R.id.edRefuel);
			
			edRest.setText("");
			edOdometr.setText("");
			edRefuel.setText("");
		}
		
		protected View.OnClickListener okClick = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String rest = edRest.getText().toString().trim();
				String odo = edOdometr.getText().toString().trim();
				String refuel = edRefuel.getText().toString().trim();
				
				if(rest.length() > 0 && odo.length() > 0){
					try{
						Data data = new Data();
						data.odo = Integer.parseInt(odo);
						data.rest = Integer.parseInt(rest);
						
						if(refuel.length() > 0)
							data.refuel = Integer.parseInt(refuel);
						else
							data.refuel = 0;
						
						apply(data);
						dialog.dismiss();
					}catch(Exception e){
						Toast.makeText(v.getContext(), R.string.need_integer_value, Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
					
				}else
					Toast.makeText(v.getContext(), R.string.odometr_empty_value_not_allowed, Toast.LENGTH_SHORT).show();
					
			}
		};
		
		void init(){
			((TextView)dialog.findViewById(R.id.tvTitle)).setText(getTitle());
			dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(okClick);
			dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (DialogInterface.OnClickListener) null);
		}
		
		protected abstract int getTitle();
		protected abstract void apply(Data data);
	}
	
	class StartOdometrDlgHelper extends OdometrDlgHelper{
		
		public StartOdometrDlgHelper(AlertDialog dialog) {
			super(dialog);
		}

		@Override
		protected int getTitle() { return R.string.startday; }

		@Override
		protected void apply(Data data) { 
			Odometr odo = new Odometr();
			odo.start = Util.getDateTime();
			
			GpsCoord coord =  GPSUtilNew.getLastKnownLocation();
			
			if(coord != null){
				odo.start_lat = coord.latitude;
				odo.start_long = coord.longitude;
			}
			
			odo.start_odo = data.odo;
			odo.start_rest = data.rest;
			
			DbWriter writer = new DbWriter();
			writer.insertRecord(odo);
			writer.close();
			
			btnStart.setImageResource(R.drawable.stop);
		}
		
		@Override
		void init() {
			super.init();
		}
	}
	
	class FinishOdometrDlgHelper extends OdometrDlgHelper{
		public FinishOdometrDlgHelper(AlertDialog dialog) {
			super(dialog);
			edRefuel.setVisibility(View.VISIBLE);
			Odometr o = getOpenedOdometr();
			
			if(o.refuel > 0)
				edRefuel.setText(Integer.toString(o.refuel));
		}

		@Override
		protected int getTitle() { return R.string.finishday; }

		@Override
		protected void apply(Data data) { 
			Odometr odo = getOpenedOdometr();
			
			if(odo != null){
				// ≈сли нова€ дата - то завершение работы 23:59:59
				Date curDate = Util.getDateTime();
				if( (curDate.getTime() / (24 * 3600000)) != (odo.start.getTime() / (24*3600000)) ) {
					Calendar c = Calendar.getInstance();
					c.setTime(odo.start);
					c.set(Calendar.HOUR, 23);
					c.set(Calendar.MINUTE, 59);
					c.set(Calendar.SECOND, 59);
					c.set(Calendar.MILLISECOND, 0);
					curDate = c.getTime();
				}
				odo.end = curDate;
				GpsCoord coord =  GPSUtilNew.getLastKnownLocation();

				if(coord != null){
					odo.end_lat = coord.latitude;
					odo.end_long = coord.longitude;
				}
				
				odo.end_odo = data.odo;
				odo.end_rest = data.rest;
				odo.refuel = data.refuel;
				
				odo.params = 0;
				
				DbWriter wr = new DbWriter();
				wr.insertRecord(odo);
				wr.close();
				
				btnStart.setImageResource(R.drawable.start);
				
				UpdateDB.open(NapoleonEx.this);
			}
		}
	}
	
	@Override
	protected OnItemClickListener getItemOnClickListner() {
		return new OrglListOnClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(checkIsStarted())
					super.onItemClick(arg0, arg1, arg2, arg3);
				else
					Toast.makeText(arg1.getContext(), R.string.must_start_work_day, Toast.LENGTH_SHORT).show();
			}
		};
	}
}

package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Odometr;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderReserv;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

public class NapoleonEx extends Napoleon {
	private ImageButton btnStart;
	Set<String> changedOrders = new HashSet<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		btnStart = (ImageButton) findViewById(R.id.btnStart);
		btnStart.setOnClickListener(new OnClickListener() { 
			@Override public void onClick(View v) { showDialog(R.id.odometr_dlg); }
		});
	}
	
	protected void onResume() {
		super.onResume();
		btnStart.setImageResource(isStarted() ? R.drawable.stop_day : R.drawable.start_day);
		
		changedOrders.clear();
		final Map<String, OrderEx> orders = new HashMap<String, OrderEx>();
		
		Date sdate = Util.getDayStart(new Date());
		String where = "created > " + Long.toString(sdate.getTime() - 3 * 24 * 3600 * 1000) + " and created < " + 
				Long.toString(sdate.getTime() + 24 * 3600 * 1000) + " and orderNumber <> ''";
		DataTraveler.travel(OrderEx.class, new DataTraveler.Travel<OrderEx>(true) {

			@Override
			public boolean travel(DataTraveler<OrderEx> item) {
				orders.put(item.data.orderNumber, item.data);
				return true;
			}
		}, where);
		
		where = "number in(";
		for(String num : orders.keySet())
			where += "'" + num + "',";
		where = where.substring(0, where.length()-1) + ")";
		
		DataTraveler.travel(OrderReserv.class, new DataTraveler.Travel<OrderReserv>() {

			@Override
			public boolean travel(DataTraveler<OrderReserv> item) {
				OrderEx doc = orders.get(item.data.number);
				if(doc != null) {
					if(haveMissing(doc, item.data))
						changedOrders.add(doc.id);
				}
				return true;
			}
		}, where);
		
	}
	
	protected boolean haveMissing(OrderEx doc, OrderReserv data) {
		boolean res = false;
		Map<String, Integer> qty = new HashMap<String, Integer>();
		for(OrderItem oi : doc.items) {
			Integer val = qty.get(oi.id);
			if(val == null)
				val = 0;
			qty.put(oi.id, val + oi.qty);
		}
		
		for(DeliveryItem di : data.items) {
			Integer val = qty.get(di.id);
			if(val == null || val != di.qty) {
				res = true;
				break;
			}
		}
		return res;
	}

	@Override
	protected void drawOrg(OrgImpl oi, View view) {
		super.drawOrg(oi, view);
		if(changedOrders.contains(oi.getData().id)) {
			TextView tv = (TextView)view.findViewById(R.id.tvOrgName);
			tv.setTextColor(Color.RED);
		}
	}
	
	@Override
	protected int getResourceID() { return R.layout.mainex; }
	
	public static boolean isStarted(){
		return getOdometr() != null;
	}
	
	@Override
	protected void exit() {
		if(isStarted()){
			Toast.makeText(this, R.string.WorkDayShouldClosed, Toast.LENGTH_SHORT).show();
			showDialog(R.id.odometr_dlg);
		}else
			super.exit();
	}
	
	public static Odometr getOdometr(){
		DbReader reader = new DbReader();
		Odometr result = new Odometr();
		boolean bdo = reader.select(result, DataObjectInfo.getInstance().getTableName(result.getClass()), "end is null or end = 0");
		reader.close();
		
		return bdo ? result : null;
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
				String rest = edRest.getText().toString().trim().replace(',', '.');
				String odo = edOdometr.getText().toString().trim().replace(',', '.');
				String refuel = edRefuel.getText().toString().trim().replace(',', '.');
				
				if(odo.length() > 0){
					try{
						Data data = new Data();
						data.odo = ( odo.length() > 0 ) ? Integer.parseInt(odo) : 0;
						data.rest = (rest.length() > 0 ) ? Integer.parseInt(rest) : 0;
						data.refuel = (refuel.length() > 0) ? Integer.parseInt(refuel) : 0;
						
						if( apply(v.getContext(), data) )
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
		protected abstract boolean apply(Context context, Data data);
	}
	
	class StartOdometrDlgHelper extends OdometrDlgHelper{
		
		public StartOdometrDlgHelper(AlertDialog dialog) {
			super(dialog);
		}

		@Override
		protected int getTitle() { return R.string.startday; }

		@Override
		protected boolean apply(Context context, Data data) { 
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
			
			btnStart.setImageResource(R.drawable.stop_day);
			return true;
		}
		
		@Override
		void init() {
			super.init();
		}
	}
	
	class FinishOdometrDlgHelper extends OdometrDlgHelper{
		public FinishOdometrDlgHelper(AlertDialog dialog) {
			super(dialog);
			//edRefuel.setVisibility(View.VISIBLE);
			Odometr o = getOdometr();
			
			if(o.refuel > 0)
				edRefuel.setText(Integer.toString(o.refuel));
		}

		@Override
		protected int getTitle() { return R.string.finishday; }

		@Override
		protected boolean apply(Context context, Data data) { 
			Odometr odo = getOdometr();
			
			if(odo != null){
				if( odo.start_odo > data.odo ) {
					Toast.makeText(context, R.string.end_below_start, Toast.LENGTH_SHORT).show();
					return false;
				}
				odo.end = Util.getDateTime();
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
				wr.updateRecord(odo, odo.start.getTime());
				wr.close();
				
				btnStart.setImageResource(R.drawable.start_day);
			}
			return true;
		}
	}
	
	@Override
	protected OnItemClickListener getItemOnClickListner() {
		return new OrglListOnClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(isStarted())
					super.onItemClick(arg0, arg1, arg2, arg3);
				else
					Toast.makeText(arg1.getContext(), R.string.must_start_work_day, Toast.LENGTH_SHORT).show();
			}
		};
	}
}

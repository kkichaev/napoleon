package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.IOrder;
import com.grsoft.dataobjects.Zone;

public class ClientDelivery extends SelfDelivery {
	private static final String TIME_DELIMITER = ":";
	private Spinner spZone;
	private List<Zone> zone = new ArrayList<Zone>(); 
	private static final String ORG_ZONE = "org_zone";
	private static final String ORG_ID = "org_id"; 
	private static final String SHARED_PREF_NAME = "com.grsoft.napoleon.ClientDelivery.SharedPreferences";
	private static final String INVALID_ID = "invalid_id";
	private TextView tvTimeStart;
	private TextView tvTimeFinish;
	private EditText edDlvInfo;
	
	@Override protected int getLayoutID() { return R.layout.client_delivery; }
	
	
	@Override
	protected void inflateView(View view) {
		super.inflateView(view);
		spZone = (Spinner) view.findViewById(R.id.spZone);
		tvTimeStart = (TextView) view.findViewById(R.id.tvTimeStart);
		tvTimeFinish = (TextView) view.findViewById(R.id.tvTimeFinish);
		edDlvInfo = (EditText) view.findViewById(R.id.edDlvInfo);
	}
	
	@Override
	protected void initView() {
		super.initView();
		spZone.setAdapter(new ArrayAdapter<Zone>((Context)getActivity(),  R.layout.simple_spinner_layout, zone));
		
		IOrder o = (IOrder) order.getData();
		String zone = o.getZone().trim();
		
		if(zone.length() == 0){
			SharedPreferences pref = getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
			String id = pref.getString(ORG_ID, INVALID_ID);
			
			if(id.equals(order.getId()))
				zone = pref.getString(ORG_ZONE, INVALID_ID);
		}
		
		if(zone.length() > 0 && !zone.equals(INVALID_ID)){
			Adapter a = spZone.getAdapter();
			
			for(int i = 0; i < a.getCount(); i++){
				Zone z = (Zone) a.getItem(i);
				
				if(z.id.equals(zone)){
					spZone.setSelection(i, true);
					break;
				}
			}
		}
		
		tvTimeStart.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { showTimeDlg(new TimeFragmentStart()); } });
		tvTimeStart.setText(o.getTimeStart());
		tvTimeFinish.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) { showTimeDlg(new TimeFragmentFinish());} });
		tvTimeFinish.setText(o.getTimeFinish());
		
		edDlvInfo.setText(o.getDlvInfo());
	}
	
	private void showTimeDlg(TimeFragmentStart dialog){
		try{
			dialog.init();
			dialog.show(getChildFragmentManager(), dialog.getClass().getName());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void initData() {
		super.initData();
		zone.add(new Zone());
		DataTraveler.travel(Zone.class, new DataTraveler.Travel<Zone>() {
			@Override
			public boolean travel(DataTraveler<Zone> item) {
				zone.add(item.data);
				item.data = new Zone();
				return true;
			}
		}, null);
	}
	
	@Override
	public boolean checkAndUpdate(IOrder ord) {
		boolean result = super.checkAndUpdate(ord);
		
		if(result){
			String z = ((Zone)spZone.getSelectedItem()).id;
			SharedPreferences pref = getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
			Editor ed = pref.edit();
			ed.putString(ORG_ID, order.getId());
			ed.putString(ORG_ZONE, z);
			ed.commit();
			
			ord.setZone(z);
			ord.setTimeStart(tvTimeStart.getText().toString());
			ord.setTimeFinish(tvTimeFinish.getText().toString());
			ord.setDlvInfo(edDlvInfo.getText().toString());
		}
		
		return result;
	}
	
	private String creatTimeStr(int hour, int min){
		StringBuilder sb = new StringBuilder();
		
		if(hour < 10) sb.append(0);
		sb.append(hour);
		sb.append(TIME_DELIMITER);
		if(min < 10) sb.append(0);
		sb.append(min);
		
		return sb.toString();
	}
	
	private OnTimeSetListener startTimeSetListener = new OnTimeSetListener() { @Override public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		tvTimeStart.setText(creatTimeStr(hourOfDay, minute));}};
	
	private OnTimeSetListener finishTimeSetListener = new OnTimeSetListener() { @Override public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		tvTimeFinish.setText(creatTimeStr(hourOfDay, minute));}};
	
	class TimeFragmentStart extends DialogFragment{
		int hour = 0;
		int min = 0;
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return new TimePickerDialog(getActivity(), getTimeSetListener(), hour, min, true);
		}
		
		public void init() {
			String[] arr = getTimeStr().split(TIME_DELIMITER);
			
			if(arr.length == 2){
				hour = Integer.parseInt(arr[0]);
				min = Integer.parseInt(arr[1]);
			}
		}

		protected OnTimeSetListener getTimeSetListener(){ return startTimeSetListener; }
		protected String getTimeStr(){ return tvTimeStart.getText().toString(); } 
	}
	
	class TimeFragmentFinish extends TimeFragmentStart{
		protected OnTimeSetListener getTimeSetListener(){ return finishTimeSetListener; }
		protected String getTimeStr(){ return tvTimeFinish.getText().toString(); } 
	}
}

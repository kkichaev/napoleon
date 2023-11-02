package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.grsoft.database.Hitching;
import com.grsoft.database.OrderImportedHitching;
import com.grsoft.database.OrderQueryHitching;
import com.grsoft.database.OrderToDelHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.RetQueryHitching;
import com.grsoft.database.ReturnImportHitching;
import com.grsoft.dataobjects.AgentCfgHitching;
import com.grsoft.dataobjects.PlanApprove;
import com.grsoft.dataobjects.Quality;
import com.grsoft.dataobjects.Question;
import com.grsoft.dataobjects.ReportAnswerSPK;
import com.grsoft.dataobjects.Reports;
import com.grsoft.dataobjects.ReportsRequestHitching;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Util;

public class UpdateDBEx extends UpdateDB {
	private EditText edDate;
	private CheckBox cbOrdQuery;
	private EditText edRetDate;
	private CheckBox cbRetQuery;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		edDate = (EditText)findViewById(R.id.edDate);
		cbOrdQuery = (CheckBox)findViewById(R.id.cbOrdQuery);
		edDate.setEnabled(false);
		edDate.setInputType(InputType.TYPE_NULL);
		
		edRetDate = (EditText)findViewById(R.id.edRetDate);
		cbRetQuery = (CheckBox)findViewById(R.id.cbRetQuery);
		edRetDate.setEnabled(false);
		edRetDate.setInputType(InputType.TYPE_NULL);
		
		cbOrdQuery.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				edDate.setEnabled(isChecked);
			}
		});
		
		cbRetQuery.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				edRetDate.setEnabled(isChecked);
			}
		});
		
		String s = Util.simpleDateFormat.format(Calendar.getInstance().getTime());
		edDate.setText(s);
		edRetDate.setText(s);
		
		edDate.setOnClickListener(new DateSelectListener(edDate));
		edRetDate.setOnClickListener(new DateSelectListener(edRetDate));
	}
	
	class DateSelectListener implements OnClickListener{
		private final EditText editText;
		
		public DateSelectListener(EditText editText){
			this.editText = editText;
		}
		
		@Override
		public void onClick(View v) {
			Calendar calendar = Calendar.getInstance();
			DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
					new OnDateSetListener() {
						
						@Override
						public void onDateSet(DatePicker view, int year, int monthOfYear,
								int dayOfMonth) {
							StringBuilder sb = new StringBuilder();
							sb.append(String.format("%02d", dayOfMonth)).append(".")
								.append(String.format("%02d", monthOfYear+1)).append(".").append(year);
							editText.setText(sb.toString());
						}
					}, 
					calendar.get(Calendar.YEAR), 
					calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
			datePickerDialog.show();
		}
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> result = new ArrayList<Hitching>();
		
		result.addAll(super.getGenDataHitchings());
		result.add(new RcvNewHitching(Quality.class, "Quality"));
		result.add(new OrderImportedHitching());
		result.add(new ReturnImportHitching());
		result.add(new RcvNewHitching(Question.class, "Question"));
		result.add(new RcvNewHitching(Reports.class, "Reports"));
		result.add(new Hitching(ReportAnswerSPK.class, "ReportAnswer"));
		result.add(new Hitching(PlanApprove.class, "PlanApprove"));
		return result;
	}
	
	@Override
	protected int getContentView() { return  R.layout.updatedbex; }
	
	@Override
	public List<ObjectListener> getExported() {
		 List<ObjectListener> result = super.getExported();
		 
		 if(result == null)
			 result = new ArrayList<ObjectListener>();
			 
		 if (cbOrdQuery.isChecked()){
			 try{
			 	Date date = Util.simpleDateFormat.parse(edDate.getText().toString());
			 	result.add(new OrderQueryHitching(date));
			 }catch(Exception e){
				 e.printStackTrace();
			 }
		 }
			 
		 if (cbRetQuery.isChecked()){
			 try{
			 	Date date = Util.simpleDateFormat.parse(edRetDate.getText().toString());
			 	result.add(new RetQueryHitching(date));
			 }catch(Exception e){
				 e.printStackTrace();
			 }
		 }
		 
//		 result.add(new OrderToDelHitching());
		 result.add(new ReportsRequestHitching());
		 result.add(new AgentCfgHitching());
		 return result;
	}
}

package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.Reports;
import com.grsoft.dataobjects.impl.ReportsRequestImpl;
import com.grsoft.dataobjects.impl.ReportsImpl;
import com.grsoft.view.BaseActivity;

public class ReportParamEdit extends BaseActivity {
	private static final String NAME = "name";
	
	private TextView tvName;
	private TextView tvType;
	private EditText edP1;
	private EditText edP2;
	private EditText edP3;
	private EditText edP4;
	private EditText edP5;
	private EditText edP6;
	private EditText edP7;
	private EditText edP8;
	private EditText edP9;
	private EditText edP10;
	
	public static void open(Context context, String name){
		Intent intent = new Intent(context, ReportParamEdit.class);
		intent.putExtra(NAME, name);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_param_edit);
		
		String reportName = getIntent().getStringExtra(NAME);
		
		final ReportsRequestImpl rri = new ReportsRequestImpl();
		rri.getData().name = reportName;
				
		ReportsImpl ri = new ReportsImpl();
		ri.getData().name = reportName;
		
		Button btnSave = (Button)findViewById(R.id.btnSave);
		
		tvName = (TextView) findViewById(R.id.tvName);
		tvType = (TextView) findViewById(R.id.tvType);
		edP1 = (EditText) findViewById(R.id.edP1);
		edP2 = (EditText) findViewById(R.id.edP2);
		edP3 = (EditText) findViewById(R.id.edP3);
		edP4 = (EditText) findViewById(R.id.edP4);
		edP5 = (EditText) findViewById(R.id.edP5);
		edP6 = (EditText) findViewById(R.id.edP6);
		edP7 = (EditText) findViewById(R.id.edP7);
		edP8 = (EditText) findViewById(R.id.edP8);
		edP9 = (EditText) findViewById(R.id.edP9);
		edP10 = (EditText) findViewById(R.id.edP10);
		
		if (rri.read())
			setInputParam(rri.getData());
		else if (ri.read()){
			setInputParam(ri.getData());
			rri.getData().type = ri.getData().type;
		} else {
			btnSave.setEnabled(false);
			Toast.makeText(this, "Ошибка при чтении шаблона очета", Toast.LENGTH_LONG).show();
		}
		
		rri.close();
		ri.close();
		
		btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Reports report = rri.getData();
				
				report.p0 = edP1.getText().toString();
				report.p1 = edP2.getText().toString();
				report.p2 = edP3.getText().toString();
				report.p3 = edP4.getText().toString();
				report.p4 = edP5.getText().toString();
				report.p5 = edP6.getText().toString();
				report.p6 = edP7.getText().toString();
				report.p7 = edP8.getText().toString();
				report.p8 = edP9.getText().toString();
				report.p9 = edP10.getText().toString();
				
				rri.write();
				rri.close();
				
				finish();
			}
		});
	}

	private void setInputParam(Reports data) {
		tvName.setText(data.name);
		tvType.setText(data.type);
		edP1.setText(data.p0);
		edP2.setText(data.p1);
		edP3.setText(data.p2);
		edP4.setText(data.p3);
		edP5.setText(data.p4);
		edP6.setText(data.p5);
		edP7.setText(data.p6);
		edP8.setText(data.p7);
		edP9.setText(data.p8);
		edP10.setText(data.p9);
	}
}

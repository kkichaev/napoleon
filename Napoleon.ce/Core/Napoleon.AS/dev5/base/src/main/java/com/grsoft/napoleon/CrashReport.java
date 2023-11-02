package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import com.grsoft.util.ReportService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class CrashReport extends Activity{
	private static String FOLDER = "folder";
	private static String STACK = "stack";
	private TextView tvStack;
	private EditText edComment;
	
	public static void open(Context context, String folder, String stack){
		Intent intent=new Intent(context,CrashReport.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(FOLDER, folder);
		intent.putExtra(STACK, stack);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crash_report);
		
		tvStack = (TextView) findViewById(R.id.tvStack);
		edComment = (EditText)findViewById(R.id.edComment);
		
		findViewById(R.id.btnClose).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		findViewById(R.id.btnSendReport).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ReportService.open(v.getContext(), false,
						((EditText)findViewById(R.id.edComment)).getText().toString().trim(),
						getIntent().getStringExtra(FOLDER));
				finish();
			}
		});
		
		findViewById(R.id.btnSendBase).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ReportService.open(v.getContext(), true,
						edComment.getText().toString().trim(), 
						getIntent().getStringExtra(FOLDER)); 
				finish();
			}
		});
		
		String stack = getIntent().getStringExtra(STACK);
		
		if (stack != null)
			tvStack.setText(stack);
	}
}



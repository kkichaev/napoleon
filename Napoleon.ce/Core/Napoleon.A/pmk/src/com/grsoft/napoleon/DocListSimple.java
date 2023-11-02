package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;

import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class DocListSimple extends DocList implements OnClickListener {
	private View btnPrevDate;
	private View btnNewxtDate;
	private TextView tvDate;
	private Date date;
	private DocListHelper dlh = new DocListHelper();
	
	static void openDocList(Context context) {
		Intent i = new Intent(context, DocListSimple.class);
		context.startActivity(i);		
	}
	
	protected int getViewID() {
		return R.layout.doclistex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tvDate = (TextView) findViewById(R.id.tvDate);
		btnPrevDate = findViewById(R.id.btnPrevDate);
		btnNewxtDate = findViewById(R.id.btnNextDate);
		
		date = Util.resetTime(new Date());
		tvDate.setText(Util.simpleDateFormat.format(new Date()));
		btnPrevDate.setOnClickListener(this);
		btnNewxtDate.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		if (id == R.id.btnPrevDate)
			cal.add(Calendar.DATE, -1);
		else if (id == R.id.btnNextDate)
			cal.add(Calendar.DATE, 1);
		
		date = cal.getTime();
		cal.add(Calendar.DATE, 1);
		Date end = cal.getTime();
		
		tvDate.setText(Util.simpleDateFormat.format(date));
		
		DatePeriod dp = new DatePeriod(date, end);
		adapterFilter(dp, null);
		refreshTotalSum(false);
	}
	
	@Override
	protected boolean countSumFromDocuments(boolean useFilter) {
		return true;
	}
	
	protected int getDocStatusResource(CreatableDocument<?> doc) {
		return dlh.getDocStatusResource(doc);
	}
}

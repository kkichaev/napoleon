package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DataTraveler.Travel;
import com.grsoft.dataobjects.Org;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;


public class OrgPlanView extends Activity {
	private Date start;
	private Date finish;
	private Button btnStart;
	private Button btnFinish;
	private int resPlan;
	private int resFact;
	
	List<AdapterData> data = new ArrayList<AdapterData>();
	
	public static void open(Context context){
		Intent intent = new Intent(context, OrgPlanView.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orgplanview);
		inflateView();
		init();
	}

	private void inflateView() {
		btnStart = (Button) findViewById(R.id.btnStart);
		btnFinish = (Button) findViewById(R.id.btnFinish);
	}

	private void init() {
		DatePeriod dp = DatePeriod.createRange(Util.getDate(), 24*60);
		start = dp.begin;
		finish = dp.end;
		
		btnFinish.setOnClickListener(calendarClick);
		btnStart.setOnClickListener(calendarClick);
		
		adapter = new Adapter();
		list = ((ListView) findViewById(R.id.list));
		list.setDividerHeight(0);
		refreshDate();
	}

	private void startAdapterTask() {
		new AsyncTask<Void , Void, Void>(){

			protected void onPreExecute() {
				showDialog(R.id.waitdlg);
				list.setAdapter(null);
			};
			
			protected void onPostExecute(Void result) {
				dismissDialog(R.id.waitdlg);
				list.setAdapter(adapter);
				
				resPlan = resPlan / Consts.WEIGHT_SCALE * Consts.WEIGHT_SCALE;
				resFact = resFact / Consts.WEIGHT_SCALE * Consts.WEIGHT_SCALE;
				
				TextView tv = (TextView) findViewById(R.id.tvPlan);
				tv.setText(Util.IntToScaleStr(resPlan, Consts.WEIGHT_SCALE));
				
				tv = (TextView) findViewById(R.id.tvFact);
				tv.setText(Util.IntToScaleStr(resFact, Consts.WEIGHT_SCALE));
				
				

				int percent = 0;
				
				if (resPlan > 0)
					percent = (int) Math.round(((double) resFact / resPlan * 100));
				
				tv = (TextView) findViewById(R.id.tvProgress);
				tv.setText(Integer.toString(percent));
				
			};
			
			@Override
			protected Void doInBackground(Void... params) {
				fillAdapterData();
				return null;
			}
			
		}.execute((Void[])null);
	}
	
	private View.OnClickListener calendarClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent i = new Intent(v.getContext(), CalendarActivity.class);
			i.putExtra(ExtrasConst.DATE_TAG, v.getId() == R.id.btnStart ? start.getTime() : finish.getTime());
			startActivityForResult(i, v.getId());
		}
	};
	private Adapter adapter;
	private ListView list;
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG,
					curDate.getTime());
			Date newDate = new Date(ct);
			
			if(requestCode == R.id.btnStart)
				start = newDate;
			else
				finish = newDate;
			
			refreshDate();
		}
	};
	
	private void refreshDate() {
		startAdapterTask();
		btnStart.setText(Util.simpleDateFormat.format(start));
		btnFinish.setText(Util.simpleDateFormat.format(finish));
	}
	
	

	protected void fillAdapterData() {
		data.clear();
		DataTraveler.travel(Org.class, new Travel<Org>() {

			@Override
			public boolean travel(DataTraveler<Org> item) {
				final AdapterData a = new AdapterData();
				a.name = item.data.name;
				DocumentsEx.readPlan(item.data.id, new DocumentsEx.PlanHandler() {
					
					@Override
					public void inflate(int plan, int weight) {
						a.plan = plan;
						a.fact = weight;
						
						resPlan += plan;
						resFact += weight;
					}
				}, start, finish);
				
				data.add(a);
				
				item.data = new Org();
				return true;
			}}, null);
		
		Collections.sort(data, new Comparator<AdapterData>() {
			@Override public int compare(AdapterData lhs, AdapterData rhs) { return lhs.name.compareTo(rhs.name);	}});
	}

	class Adapter extends BaseAdapter{

		@Override
		public int getCount() {	return data.size();	}

		@Override
		public Object getItem(int position) { return data.get(position); }

		@Override
		public long getItemId(int position) { return 0;	}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if(view == null)
				view = View.inflate(OrgPlanView.this, R.layout.orgplanviewrow, null);
			
			AdapterData item = (AdapterData) getItem(position);
			
			if(item != null){
				((TextView)view.findViewById(R.id.tvName)).setText(item.name);
				((TextView)view.findViewById(R.id.tvPlan)).setText(Util.IntToScaleStr(item.plan / Consts.WEIGHT_SCALE * Consts.WEIGHT_SCALE, Consts.WEIGHT_SCALE) );
				((TextView)view.findViewById(R.id.tvFact)).setText(Util.IntToScaleStr(item.fact, Consts.WEIGHT_SCALE) );
				
				int percent = 0;
				
				if (item.plan > 0)
					percent = (int) Math.round(((double) item.fact	/ item.plan * 100));
				
				((TextView)view.findViewById(R.id.tvPercent)).setText(Integer.toString(percent));
			}
			
			view.setBackgroundResource(position % 2 != 0 ? 
					R.drawable.even_row_selector :
					R.drawable.list_selector);		
			
			return view;
		}
	}
	
	class AdapterData 
	{
		public String name;
		public int plan;
		public int fact;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.waitdlg)
			return createWaitDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog createWaitDlg() {
		ProgressDialog result = new ProgressDialog(this);
		result.setMessage(getString(R.string.please_wait));
		return result;

	}
}

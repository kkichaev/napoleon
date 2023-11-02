package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SalesReport extends Activity implements OnClickListener {
	public static final String PARAMS = "params";
	ListView list;
	int scale;
	TextView tvSum1;
	TextView tvSum2;
	TextView tvSum3;
	TextView tvSumRes;
	private SalesParams.Params args;
	
	public static void open(Context context, SalesParams.Params args) {
		Intent intent = new Intent(context, SalesReport.class);
		intent.putExtra(PARAMS, args);
		context.startActivity(intent);
	}
	
	private static class Data{
		public String name = "";
		public String id = "";
		
		public int t1 = 0;
		public int t2 = 0;
		public int t3 = 0;	
		public int t4 = 0;
		
		public Data(String id, String name) {
			this.name = name;
			this.id = id;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.salesreport);
		
		list = (ListView)findViewById(R.id.list);
		tvSum1 = (TextView) findViewById(R.id.tvSum1);
		tvSum2 = (TextView) findViewById(R.id.tvSum2);
		tvSum3 = (TextView) findViewById(R.id.tvSum3);
		tvSumRes = (TextView) findViewById(R.id.tvSumRes);
		findViewById(R.id.btnFilter).setOnClickListener(this);
		
		args = getIntent().getParcelableExtra(PARAMS);
		scale = args.unit == 0 ? Consts.WEIGHT_SCALE : Consts.SUM_SCALE;
		
		doReport(args);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.wait_dlg)
			return createWaitDlg();
		return super.onCreateDialog(id);
	}

	private Dialog createWaitDlg() {
		ProgressDialog dlg = new ProgressDialog(this);
		dlg.setMessage(getString(R.string.please_wait));
		return dlg;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void doReport(SalesParams.Params arg) {
		new AsyncTask<SalesParams.Params, Void, List<Data>>(){

			@Override
			protected List<Data> doInBackground(SalesParams.Params... args) {
				List<Data> result = new ArrayList<SalesReport.Data>();
				OrgImpl org = new OrgImpl();
				PriceImpl price = new PriceImpl();
				
				SalesParams.Params params = args[0];
				DatePeriod dp = new DatePeriod(params.start, params.finish);
				com.grsoft.napoleon.documents.DocList docList =  DebtDoc.instance().docList(null, null, dp);
				
				Map<String, Data> map = new HashMap<String, Data>();
				Map<String, Set<Date>> dlvs = new HashMap<String, Set<Date>>(); 
				
				for(Document<?> d : docList) {
					if (params.orgs.contains(d.getId()) && d instanceof DeliveryImpl) {
						DeliveryImpl dlv = (DeliveryImpl) d;
						
						if (dlv != null && (
								params.status == 0 ||
								params.status == 1 && ((DeliveryEx)dlv.getData()).pending == 0 ||
								params.status == 2 && ((DeliveryEx)dlv.getData()).pending == 1)) {
							
							if (org.read("id", dlv.getId())) {
								Date dt = Util.resetTime(dlv.getDate());
								
								if (!dlvs.containsKey(dlv.getId()))
									dlvs.put(dlv.getId(), new HashSet<Date>());
								
								dlvs.get(dlv.getId()).add(dt);
								
								if (params.slsch.contains(((OrgEx)org.getData()).salesChannel)){
									if(!map.containsKey(d.getId()))
										map.put(d.getId(), new Data(org.getData().id, org.getData().name));

									for(DeliveryItem di : dlv.getData().items) {
										if (price.read("id", di.id)) {
											Data item = map.get(d.getId());
											
											int val = 0;
											if (params.unit == 0) {
													val = price.getData().weight; 
													if( val != 0 ) {
														val = (int)(((long)di.qty * val + Consts.WEIGHT_SCALE/2) / Consts.WEIGHT_SCALE);
														//val -= (val % Consts.WEIGHT_SCALE); // округлим 
													}
												
											}else {
												val = di.sum;
											}
											
											PriceEx pe = (PriceEx) price.getData();
											switch(pe.type) {
											case 0:
												item.t1 += val;
												break;
											case 1:
												item.t2 += val;
												break;
											case 2:
												item.t3 += val;
												break;
											default:
											}
										}
									}
								}
							}
						}
					}
				}
				
				
				for (Data d : map.values())
					if(dlvs.containsKey(d.id))
						d.t4 = dlvs.get(d.id).size() * scale;
				
				result.addAll(map.values());
				return result;
			}
			
			protected void onPreExecute() {
				showDialog(R.id.wait_dlg);
			}; 
			
			protected void onPostExecute(List<Data> result) {
				dismissDialog(R.id.wait_dlg);
				showData(result);
			}; 
			
		}.execute(arg);
		

	}

	protected void showData(final List<Data> data) {
		list.setAdapter(new BaseAdapter() {
			
			@Override
			public View getView(int position, View view, ViewGroup parent) {
				if (view == null)
					view = View.inflate(SalesReport.this, R.layout.salesreport_row, null);
				
				TextView tv = (TextView) view.findViewById(R.id.tvPos);
				tv.setText(Integer.toString(position + 1));
				
				Data item = (Data) getItem(position);
				
				tv = (TextView) view.findViewById(R.id.tvName);
				tv.setText(item.name);
				
				tv = (TextView) view.findViewById(R.id.tvCol1);
				tv.setText(Util.IntToScaleStr(item.t1, scale));
				
				tv = (TextView) view.findViewById(R.id.tvCol2);
				tv.setText(Util.IntToScaleStr(item.t2, scale));
				
				tv = (TextView) view.findViewById(R.id.tvCol3);
				tv.setText(Util.IntToScaleStr(item.t3, scale));
				
				tv = (TextView) view.findViewById(R.id.tvCol4);
				tv.setText(Util.IntToScaleStr(item.t4, scale));
				
				tv = (TextView) view.findViewById(R.id.tvRes);
				tv.setText(Util.IntToScaleStr(item.t1 + item.t2 + item.t3, scale));
				
				return view;
			}
			
			@Override
			public long getItemId(int position) {
				return 0;
			}
			
			@Override
			public Object getItem(int position) {
				return data.get(position);
			}
			
			@Override
			public int getCount() {
				return data.size();
			}
		});
		
		int s1 = 0;
		int s2 = 0;
		int s3 = 0;
		int s4 = 0;
		
		for(Data d : data) {
			s1 += d.t1;
			s2 += d.t2;
			s3 += d.t3;
		}
		
		tvSum1.setText(Util.IntToScaleStr(s1, scale));
		tvSum2.setText(Util.IntToScaleStr(s2, scale));
		tvSum3.setText(Util.IntToScaleStr(s3, scale));
		tvSumRes.setText(Util.IntToScaleStr(s1 + s2 + s3 + s4, scale));
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnFilter)
			doFilter();
	}

	private void doFilter() {
		finish();
		SalesParams.open(this, args);
	}
}

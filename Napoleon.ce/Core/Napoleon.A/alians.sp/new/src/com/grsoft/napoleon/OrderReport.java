package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;
import com.grsoft.util.FolderTree;
import com.grsoft.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

public class OrderReport extends Activity {
	private OrderReportAdapter adapter;
	private ListView list;
	private TextView tvSum;
	private TextView tvWeight;
	private TextView tvFilter;
	
	
	public static void open(Context context) {
		Intent i = new Intent(context, OrderReport.class);
		context.startActivity(i);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.orderreport);
		
		list = (ListView) findViewById(R.id.list);
		tvSum = (TextView) findViewById(R.id.tvSum);
		tvWeight = (TextView) findViewById(R.id.tvWeight);
		tvFilter = (TextView) findViewById(R.id.tvFilter);
		
		findViewById(R.id.llFilterPanel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(R.id.select_period_dlg);
			}
		});;
		
		adapter = new OrderReportAdapter(this);
		list.setAdapter(adapter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		showDialog(R.id.select_period_dlg);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.select_period_dlg)
			return createSelectPeriodDlg();
		else if (id == R.id.wait_dlg)
			return createWaitDlg();
		else
			return super.onCreateDialog(id);
	}
	
	private Dialog createWaitDlg() {
		ProgressDialog dlg = new ProgressDialog(this);
		dlg.setMessage(getString(R.string.please_wait));
		return dlg;
	}


	private Dialog createSelectPeriodDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(View.inflate(this, R.layout.selectperiod, null));
		builder.setTitle(R.string.select_period_title);
		builder.setPositiveButton(R.string.ok, okclick);
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}

	DialogInterface.OnClickListener okclick = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Dialog dlg = (Dialog)dialog; 
			DatePicker p1 = (DatePicker) dlg.findViewById(R.id.datePicker1);
			DatePicker p2 = (DatePicker) dlg.findViewById(R.id.datePicker2);
			
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			
			c.set(p1.getYear(), p1.getMonth(), p1.getDayOfMonth());
			Date d1 = c.getTime();
			c.set(p2.getYear(), p2.getMonth(), p2.getDayOfMonth());
			Date d2 = c.getTime();
			rebuild(d1, d2);
		}
	};
	
	private static class TaskParam{
		public Date start;
		public Date finish;
	}
	
	static class DataItem
	{
		public long weight = 0;
		public long sum = 0;
		public Folder folder;
		
		public DataItem(Folder folder) {
			this.folder = folder;
		}
	}
	
	static class AdapterData
	{
		public long sum = 0;
		public long weight = 0;
		
		PriceImpl price = new PriceImpl();
		FolderTree ftree = new FolderTree();
		Map<Integer, DataItem> map = new HashMap<Integer, DataItem>();

		public AdapterData() {
			ftree.load();
		}
		
		public void add(OrderImpl doc) {
			for(OrderItem i : doc.getData().items) {
				price.read("id", i.id);
				
				int fid = price.getData().folderID;

				Folder f = ftree.getFolder(fid);
				
				if (f != null) {
					if (!map.containsKey(fid))
						map.put(fid, new DataItem(f));

					DataItem c = new DataItem(f);
					c.weight =  FPOperation.itemMul(i.qty, price.getData().weight, Consts.WEIGHT_SCALE);
					c.sum = (long)i.cost * i.qty / Consts.QTY_SCALE;
							
					DataItem di = map.get(fid);
					
					di.weight += c.weight;
					di.sum += c.sum;
					
					Folder parent = ftree.getParent(ftree.getFolder(fid));
					
					if(parent != null)
						addToParent(parent, c);
				}
			}
		}

		private void addToParent(Folder folder, DataItem item) {
			if (folder != null) {
				addToParent(ftree.getParent(folder), item);
				
				if (!map.containsKey(folder.id))
					map.put(folder.id, new DataItem(folder));
				
				DataItem di = map.get(folder.id);
				di.weight += item.weight;
				di.sum += item.sum;
			}
		}
		
		public List<DataItem> getData(){
			List<DataItem> res = new ArrayList<DataItem>();
			res.addAll(map.values());
			Collections.sort(res, new Comparator<DataItem>() {

				@Override
				public int compare(DataItem lhs, DataItem rhs) {
					return lhs.folder.id - rhs.folder.id;
				}
			});
			
			sum = 0;
			weight = 0;
			
			for(DataItem i : res)
				if (i.folder.level == 0) {
					sum += i.sum;
					weight += i.weight;
				}
			
			return res;
		}
	} 

	protected void rebuild(final Date d1, final Date d2) {
		TaskParam p  = new TaskParam();
		p.start = d1;
		p.finish = d2;
		
		new AsyncTask<TaskParam, Void, AdapterData>(){
			protected void onPreExecute() {
				showDialog(R.id.wait_dlg);
			}; 
			
			protected void onPostExecute(AdapterData result) {
				dismissDialog(R.id.wait_dlg);
				adapter.setData(result);
				adapter.notifyDataSetChanged();
				tvSum.setText(Util.IntToScaleStr(result.sum, Consts.SUM_SCALE));
				tvWeight.setText(Util.IntToScaleStr(result.weight, Consts.WEIGHT_SCALE));
				
				tvFilter.setText(getString(R.string.date_filter, 
						d1.getDate(), d1.getMonth() + 1, d1.getYear() + 1900,
						d2.getDate(), d2.getMonth() + 1, d2.getYear() + 1900));
			}; 
			
			@Override
			protected AdapterData doInBackground(TaskParam... params) {
				return collectAdapterData(params[0]);
			}}.execute(p);
	}


	protected AdapterData collectAdapterData(TaskParam arg) {
		final AdapterData res = new AdapterData();
		
		Calendar c = Calendar.getInstance();
		c.setTime(arg.finish);
		c.add(Calendar.DAY_OF_MONTH, 1);
		Date f = c.getTime();
		String where = String.format("\"created\" >= %d and \"created\" < %d", arg.start.getTime(), f.getTime());
		DocList dl = new DocList(OrderImpl.class, where, null);
		
		for(Document<?> d : dl) {
			if (d instanceof OrderImpl) {
				res.add((OrderImpl)d);
			}
		}
		
		return res;
	}
}

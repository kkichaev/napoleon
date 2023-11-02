package com.grsoft.napoleon.chart;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.grsoft.dataobjects.AgentAKBData;
import com.grsoft.dataobjects.AgentOrderSum;
import com.grsoft.dataobjects.AgentTopSale;
import com.grsoft.dataobjects.AgentVisit;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

public class ChartActivity extends Activity {
	public static Class<?> activity = ChartActivity.class;
	//TopSalesChartView topSalesChartView;
	protected  VisitChartView visitChartView;
	protected OrdersChartView ordersChartView;
	protected AKBChartView akbChartView;
	
	TextView tvLastSync;
	
	public static void open(Context context) {
		Intent intent = new Intent(context, activity);
		context.startActivity(intent);
	}
	
	private ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chart);

		list = (ListView) findViewById(R.id.list);
		
		tvLastSync = (TextView) findViewById(R.id.tvLastSync);
		
		adapter = new ChartAdapter();
		visitChartView = new VisitChartView(this, buildVisitData());
		ordersChartView = new OrdersChartView(this, buildSalesData());
		akbChartView = new AKBChartView(this, buildAKBData());

		addAdapterView();

		list.setAdapter(adapter);
		
		updateLastSyncText();
	}

	protected void addAdapterView() {
		adapter.addView(visitChartView);
		adapter.addView(ordersChartView);
		adapter.addView(akbChartView);
	}

	private ChartView.ChartViewData buildVisitData() {
		final VisitChartView.Data result = new VisitChartView.Data();

		DataTraveler.travel(AgentVisit.class, new DataTraveler.Travel<AgentVisit>() {

			@Override
			public boolean travel(DataTraveler<AgentVisit> item) {
				VisitChartView.DataItem i = new VisitChartView.DataItem();
				i.date = item.data.date;
				i.count = item.data.count;
				result.values.add(i);
				return true;
			}}, null);

		return result;
	}

	private void updateLastSyncText() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		long time = pref.getLong(ChartSync.LAST_SYNC_TIME, -1);
		String text = "";
		
		if (time != -1) {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(time);
			
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
			text = getString(R.string.last_sync_time,sdf.format(c.getTime()));
		}
			
		tvLastSync.setText(text);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(syncFinished, new IntentFilter(ChartSync.FINISH_SYNC));
	}

	BroadcastReceiver syncFinished = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			visitChartView.setData(buildVisitData());
			//topSalesChartView.setData(buildTopSalesData());
			ordersChartView.setData(buildSalesData());
			akbChartView.setData(buildAKBData());
			
			adapter.notifyDataSetChanged();
			updateLastSyncText();
		}
	};

	protected ChartAdapter adapter;
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (isFinishing()) {
			unregisterReceiver(syncFinished);
		}
	}
	
	private OrdersChartView.Data buildSalesData() {
		final OrdersChartView.Data result = new OrdersChartView.Data();
		
		DataTraveler.travel(AgentOrderSum.class, new DataTraveler.Travel<AgentOrderSum>() {

			@Override
			public boolean travel(DataTraveler<AgentOrderSum> item) {
				OrdersChartView.DataItem i = new OrdersChartView.DataItem();
				i.date = item.data.date;
				i.sum = item.data.sum / Consts.SUM_SCALE;
				result.values.add(i);
				return true;
			}}, null);

		return result;
	}

	private TopSalesChartView.Data buildTopSalesData() {
		final TopSalesChartView.Data result = new TopSalesChartView.Data();

		DataTraveler.travel(AgentTopSale.class, new DataTraveler.Travel<AgentTopSale>() {

			@Override
			public boolean travel(DataTraveler<AgentTopSale> item) {
				result.values.put(item.data.name, item.data.qty);
				return true;
			}}, null);

		return result;
	}

	private AKBChartView.Data buildAKBData() {
		final AKBChartView.Data result = new AKBChartView.Data();
		
		result.alldoc = 0 ;
		result.inroute = 0 ;
		
		DataTraveler.travel(AgentAKBData.class, new DataTraveler.Travel<AgentAKBData>() {

			@Override
			public boolean travel(DataTraveler<AgentAKBData> item) {
				result.alldoc = item.data.alldoc;
				result.inroute = item.data.inroute;
				return false;
			}}, null);

		return result;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.chart_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = false;
		
		int id = item.getItemId();
		
		if (id == R.id.itSetting) {
			openChartSettins();
			result = true;
		}else if (id == R.id.itSync) {
			sync();
			result = true;
		}else
			result = super.onOptionsItemSelected(item);
		
		return result;
	}

	private void sync() {
		new ChartSync(this).execute((Void[])null);
		
	}

	private void openChartSettins() {
		DialogFragment dlg = new ChartSettings();
		dlg.show(getFragmentManager(), dlg.getClass().toString());
	}
}

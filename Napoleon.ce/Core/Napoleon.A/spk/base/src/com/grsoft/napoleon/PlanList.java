package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.PlanRoute;
import com.grsoft.dataobjects.impl.PlanRouteImpl;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class PlanList extends BaseActivity {
	public static Class<? extends Activity> activity = PlanList.class;
	ImageButton btnNewDoc;
	private Adapter adapter;

	public static void open(Context context) {
		Intent intent = new Intent(context, activity);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.planlist);
		btnNewDoc = (ImageButton) findViewById(R.id.btnNewDoc);
		ListView list = (ListView) findViewById(R.id.list);

		btnNewDoc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				planCreate();
			}
		});

		adapter = new Adapter();
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				long key = (Long) parent.getItemAtPosition(position);
				PlanEdit.open(PlanList.this, key);
			}
		});

		registerForContextMenu(list);
	}

	protected void planCreate() {
		PlanEdit.open(this, findNextFreeWeek());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		PlanRouteImpl plan = new PlanRouteImpl();
		plan.getData().created = new Date((Long) adapter.getItem(
				((AdapterView.AdapterContextMenuInfo)menuInfo).position));
		plan.read();
		plan.close();
		
		if(!plan.isExported())
			getMenuInflater().inflate(R.menu.planlist_contextmenu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuinfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		if(item.getItemId() == R.id.itDelete){
			Date begin = new Date((Long) adapter.getItem(menuinfo.position));
			Calendar cal = Calendar.getInstance();
			cal.setTime(begin);
			cal.add(Calendar.DATE, 6);
			Date end = cal.getTime();
			DataBaseManager.getDataBase().delete(
					DataObjectInfo.getInstance().getTableName(PlanRoute.class),
					"date >= ? and date <= ?",
					new String[] { Long.toString(begin.getTime()),
							Long.toString(end.getTime()) });
			adapter.notifyDataSetChanged();
			return true;
		}else
			return false;
	}

	private long findNextFreeWeek() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(Util.getDate());
		cal.add(Calendar.DATE, 6);
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		Date begin = cal.getTime();
		cal.add(Calendar.DATE, 6);
		Date end = cal.getTime();

		DbReader r = new DbReader();
		PlanRoute pr = new PlanRoute();
		r.setReadingFields("date");
		boolean bdo = false;

		do {
			bdo = r.select(
					pr,
					DataObjectInfo.getInstance().getTableName(PlanRoute.class),
					"date >= " + begin.getTime() + " and date <= "
							+ end.getTime());

			if (bdo) {
				cal.add(Calendar.DAY_OF_MONTH, 1);
				begin = cal.getTime();
				cal.add(Calendar.DAY_OF_MONTH, 6);
				end = cal.getTime();
			}

		} while (bdo);

		r.close();

		return begin.getTime();
	}

	@Override
	protected void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();
	}

	class Adapter extends BaseAdapter {
		ArrayList<Long> data = new ArrayList<Long>();
		private SimpleDateFormat captionFmt = new SimpleDateFormat("dd.MM.yyyy");

		private void refresh() {
			data.clear();
			DbReader r = new DbReader();
			PlanRoute planRoute = new PlanRoute();
			r.setReadingFields("date");

			boolean bdo = r.select(planRoute, DataObjectInfo.getInstance()
					.getTableName(PlanRoute.class), null);
			Calendar cal = Calendar.getInstance();

			while (bdo) {
				cal.setTime(planRoute.created);
				cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
				long key = cal.getTime().getTime();
				if (!data.contains(key))
					data.add(key);
				bdo = r.selectNext(planRoute);
			}
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null)
				view = View.inflate(PlanList.this, R.layout.planlist_row, null);

			TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
			Date date = new Date((Long) getItem(position));
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_WEEK, 6);
			tvTitle.setText(String.format("%s - %s", captionFmt.format(date),
					captionFmt.format(cal.getTime())));

			return view;
		}

		@Override
		public void notifyDataSetChanged() {
			refresh();
			super.notifyDataSetChanged();
		}
	}
}

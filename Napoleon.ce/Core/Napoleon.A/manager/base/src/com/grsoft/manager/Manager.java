package com.grsoft.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.Hitching;
import com.grsoft.database.ManagerAgentHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.ReportHitching;
import com.grsoft.dataobjects.AgentReportData;
import com.grsoft.dataobjects.Division;
import com.grsoft.dataobjects.ReportOnAgentForDatesParams;
import com.grsoft.manager.view.GroupRowItem;
import com.grsoft.manager.view.RowItem;
import com.grsoft.napoleon.adapters.AgentsAdapter;
import com.grsoft.napoleon.adapters.DailyReportAdapter;
import com.grsoft.napoleon.adapters.ReportAdapter;
import com.grsoft.napoleon.util.CalendarDlg;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.MainExceptionHandler;

public class Manager extends FragmentActivity implements UpdateCtrl {
	static final int CALENFAR_DLG = R.id.calendar_dlg;

	static final int DAILY_REPORT = 0;
	static final int WEEKLY_REPORT = 1;
	static final int MONTHLY_REPORT = 2;
	static final int PERIOD_REPORT = 3;

	ReportAdapter adapter;
	ViewPager pager;
	int selectedPage;

	private TextView tvDate;

//	ItemClickHandler itemClickHandler;

	HashMap<String, ExpandDivisionData> expandDivisionMap = new HashMap<String, ExpandDivisionData>();
	List<ExpandDivisionData> expandDivisionList = new ArrayList<ExpandDivisionData>();
//	private IShared shared;
//	private static final String LOGIN_PREF = "LoginData";
//	private static final String DURATION_KEY = "Duration";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(getContentViewID());
		
		Thread.setDefaultUncaughtExceptionHandler(new MainExceptionHandler(this, Path.SHARED_FOLDER));

		tvDate = (TextView) findViewById(R.id.tvDate);
		tvDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(CALENFAR_DLG);
			}
		});

//		itemClickHandler = new ItemClickHandler();

		// ((Spinner)findViewById(R.id.selectPeriod)).setOnItemClickListener(new
		// AdapterView.OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		// long arg3) {
		// ReportAdapter rep = null;
		// switch(arg2) {
		// case DAILY_REPORT:
		// break;
		// case WEEKLY_REPORT:
		// break;
		// case MONTHLY_REPORT:
		// break;
		// case PERIOD_REPORT:
		// break;
		// }
		//
		// if(rep != null && rep.getClass() != adapter.getClass())
		// assignNewAdapter(rep);
		// }
		// });

		Intent shared = new Intent("com.grsoft.napoleon.ExchangeService");
		bindService(shared, conn, Context.BIND_AUTO_CREATE);
	}

	protected int getContentViewID() {
		return R.layout.manager;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == CALENFAR_DLG)
			return createCalendarDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog createCalendarDlg() {
		return CalendarDlg.create(this, new CalendarDlg.Handler() {			
			@Override
			public void selectedDate(Date d) {
				adapter.setDate(d);
				tvDate.setText(adapter.getPageTitle(1));
				pager.setCurrentItem(1, true);
			}
		});
	}

	protected void assignNewAdapter(ReportAdapter rep) {
		adapter = rep;
//		adapter.setOnItemClickHandler(itemClickHandler);

		pager = (ViewPager) findViewById(R.id.viewPager);
		pager.setAdapter(adapter);

		selectedPage = adapter.getCount() - 1;
		pager.setCurrentItem(selectedPage, false);
	}

//	class ItemClickHandler implements ReportAdapter.OnItemClickHandler {
//		@Override
//		public void ItemClicked(final ReportAdapter owner, final RowItem rowitem) {
//			Date date = adapter.getItemData(selectedPage).date;
//			rowitem.open(Manager.this, date);
//		}
//	}

	@Override
	protected void onResume() {
		super.onResume();

		if (adapter == null) {
			createExpandInfo();
			assignNewAdapter(new DailyReportAdapter(this, getAgentsForReport(),
					getLayoutInflater()));

			pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
				@Override
				public void onPageSelected(int pos) {
					selectedPage = pos;
					tvDate.setText(adapter.getPageTitle(pos));
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
				}

				@Override
				public void onPageScrollStateChanged(int state) {
					if (state == ViewPager.SCROLL_STATE_IDLE) {
						if (selectedPage != 1) {
							if (adapter.shift((selectedPage == 2))){}
								//pager.setCurrentItem(1, false);
							pager.setCurrentItem(1, false);
						}
					}
				}
			});

			if (adapter.getCount() > 0)
				tvDate.setText(adapter.getPageTitle(adapter.getCount() - 1));
		} else {
			adapter.refresh(getAgentsForReport());
		}
	}

	List<RowItem> getAgentsForReport() {

		List<RowItem> ret = new ArrayList<RowItem>();

		List<GroupRowItem> divs = GroupRowItem.getHierarchicalDivisionList();
		HashMap<String, RowItem> agents = RowItem.loadAgentsMap();

		for (int i = 0; i < divs.size(); i++) {
			GroupRowItem div = divs.get(i);
			ExpandDivisionData edd = expandDivisionMap.get(Integer
					.toString(div.division.id));

			if (edd == null || !edd.visible)
				continue;

			ret.add(div);

			if (!edd.collapsed) {
				for (int j = 0; j < div.agents.size(); j++) {
					String agent_id = div.agents.get(j).id;
					if (agents.containsKey(agent_id)) {
						RowItem ai = agents.get(agent_id);
						ai.setLevel(div.getLevel() + 1);
						ret.add(ai);
					}
				}
			}
		}

		return ret;
	}

	private void createExpandInfo() {
		expandDivisionMap.clear();
		expandDivisionList.clear();

		List<GroupRowItem> divs = GroupRowItem.getHierarchicalDivisionList();

		for (int i = 0; i < divs.size(); i++) {
			ExpandDivisionData edd = new ExpandDivisionData();
			GroupRowItem di = divs.get(i);
			edd.id = Integer.toString(di.division.id);
			edd.parent = Integer.toString(di.division.parent);

			expandDivisionList.add(edd);
			expandDivisionMap.put(edd.id, edd);
		}
	}

	private void refreshExpandInfo() {
		for (int i = 0; i < expandDivisionList.size(); i++) {
			ExpandDivisionData edd = expandDivisionList.get(i);

			if (edd != null) {
				ExpandDivisionData parent_edd = expandDivisionMap
						.get(edd.parent);
				if (parent_edd == null)
					edd.visible = true;
				else
					edd.visible = parent_edd.visible && !parent_edd.collapsed;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.mainoptionsmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId(); 
		
		if (id == R.id.itSettings){
			ManagerConfiguration.open(this);
			return true;
		} else if (id == R.id.itShowabout){
			new About().show(getSupportFragmentManager(), About.class.toString());
			return true;
		} else if (id == R.id.itExit){
			finish();
			return true;
		} else if (id == R.id.itEdit){
			Editors.open(this);
			return true;
		} else if (id == R.id.itUpdate){
			refreshData();
			return true;
		} else if (id == R.id.itTask){
			ManagerTaskList.open(this);
			return true;
		} else
			return super.onOptionsItemSelected(item);
	}

	@Override
	public void updateCtrl(final boolean enabled) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// findViewById(R.id.refreshButton).setVisibility(
				// (!enabled) ? View.INVISIBLE : View.VISIBLE);
				// findViewById(R.id.refreshProgressBar).setVisibility(
				// (enabled) ? View.INVISIBLE : View.VISIBLE);
			}
		});
	}

	protected void refreshData() {
		int pos = pager.getCurrentItem();
		ReportData data = adapter.getItemData(pos);

		if (data != null) {

			List<Hitching> ret = new ArrayList<Hitching>();
			List<Hitching> repResult = new ArrayList<Hitching>();

			repResult.add(new ManagerAgentHitching());
			repResult.add(new RcvNewHitching(Division.class, "Division"));

			repResult.add(new Hitching(AgentReportData.class, "TypeName"));

			addResultHitching(repResult);
			ret.add(new ReportHitching("summary",
					new ReportOnAgentForDatesParams(data.getDate(), data
							.getEndDate()), repResult));
			
			
			UpdateProcess upp = new UpdateProcess(this, this, ret);
			upp.execute((Void[]) null);
		}
	}

	protected void addResultHitching(List<Hitching> repResult) {}

	@Override
	public void onFinish(boolean result) {
		if( result )
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					createExpandInfo();
					adapter.refresh(getAgentsForReport());
				}
			});
	}

	class ExpandDivisionData {
		String id;
		String parent;
		boolean visible = true;
		boolean collapsed = false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.maincontextmenu, menu);
		AdapterContextMenuInfo ac = (AdapterContextMenuInfo) menuInfo;
		Adapter a = ((ListView) v).getAdapter();
		RowItem ai = (RowItem) a.getItem(ac.position);
		ai.adjustMenu(menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		ListView list = (ListView) findViewById(R.id.lvItems);
		Adapter a = list.getAdapter();
		RowItem ai = (RowItem) a.getItem(info.position);

		int id = item.getItemId(); 

		if (id == R.id.itNapoleon){
			ai.runNapoleon(this);
			return true;
		} else if (id == R.id.itMessage){
			writeMessage(ai);
			return true;
		}else
			return false;
	}

	private void writeMessage(RowItem ai) {
		EditMessage dlg = new EditMessage();
		dlg.agentInfo = ai;
		dlg.show(getSupportFragmentManager(), dlg.getClass().toString());
	}

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
//			shared = IShared.Stub.asInterface(service);

//			try {
//				SharedPreferences prf = getSharedPreferences(LOGIN_PREF,
//						Context.MODE_PRIVATE);
//				SharedPreferences.Editor e = prf.edit();
//				int duration = shared.getDuration();
//				e.putInt(DURATION_KEY, duration);
//				e.commit();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
		}
	};

//	protected void onPause() {
//		super.onPause();
//
//		try {
//			SharedPreferences prf = getSharedPreferences(LOGIN_PREF,
//					Context.MODE_PRIVATE);
//			shared.putDuration(prf.getInt(DURATION_KEY, 0));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public void expandGroup(String id) {
		ExpandDivisionData edd = expandDivisionMap.get(id);
		if (edd != null) {
			edd.collapsed = !edd.collapsed;
			refreshExpandInfo();
			adapter.refresh(getAgentsForReport());
		}
	}

	private OnItemClickListener rowItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			RowItem rowitem = (RowItem) parent.getAdapter().getItem(position);
			Date date = adapter.getItemData(selectedPage).date;
			rowitem.open(Manager.this, date);
		}
	};
	
	public void adjustListView(View v, int position) {
		ListView lv = (ListView)v.findViewById(R.id.lvItems);
		registerForContextMenu(lv);
		ReportData data = adapter.getItemData(position);
		
		lv.setAdapter(new AgentsAdapter(getAgentsForReport(), data, this));
		lv.setOnItemClickListener(rowItemClickListener);
		lv.setDividerHeight(0);
	};
}

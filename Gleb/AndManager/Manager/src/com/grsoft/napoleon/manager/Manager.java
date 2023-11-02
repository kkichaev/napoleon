package com.grsoft.napoleon.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.AgentInfoHitching;
import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.ReportHitching;
import com.grsoft.dataobjects.AgentInfo;
import com.grsoft.dataobjects.AgentReportData;
import com.grsoft.dataobjects.Division;
import com.grsoft.dataobjects.DivisionInfo;
import com.grsoft.dataobjects.ReportOnAgentForDatesParams;
import com.grsoft.dataobjects.RouteParam;
import com.grsoft.dataobjects.RouteResult;
import com.grsoft.napoleon.IShared;
import com.grsoft.napoleon.UpdateProcess;
import com.grsoft.napoleon.adapters.DailyReportAdapter;
import com.grsoft.napoleon.adapters.ReportAdapter;
import com.grsoft.util.view.CalendarView;

public class Manager extends FragmentActivity implements UpdateProcessOwner {
	static final int CALENFAR_DLG = R.id.calendar_dlg;

	static final int DAILY_REPORT = 0;
	static final int WEEKLY_REPORT = 1;
	static final int MONTHLY_REPORT = 2;
	static final int PERIOD_REPORT = 3;

	ReportAdapter adapter;
	ViewPager pager;
	int selectedPage;

	private TextView tvDate;

	ItemClickHandler itemClickHandler;

	HashMap<String, ExpandDivisionData> expandDivisionMap = new HashMap<String, ExpandDivisionData>();
	List<ExpandDivisionData> expandDivisionList = new ArrayList<ExpandDivisionData>();
	private IShared shared;
	private static final String LOGIN_PREF = "LoginData";
	private static final String DURATION_KEY = "Duration";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.manager);

		tvDate = (TextView) findViewById(R.id.tvDate);
		tvDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(CALENFAR_DLG);
			}
		});

		itemClickHandler = new ItemClickHandler();

		findViewById(R.id.refreshButton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						refreshData();
					}
				});

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

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case CALENFAR_DLG:
			return createCalendarDlg();
		default:
			return super.onCreateDialog(id);
		}
	}

	private Dialog createCalendarDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_date);
		final CalendarView calendar = new CalendarView(this);
		builder.setView(calendar);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Date d = calendar.getCurrentDate();
						adapter.setDate(d);
						tvDate.setText(adapter.getPageTitle(1));
						pager.setCurrentItem(1, true);
					}
				});
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}

	protected void assignNewAdapter(ReportAdapter rep) {
		adapter = rep;
		adapter.setOnItemClickHandler(itemClickHandler);

		pager = (ViewPager) findViewById(R.id.viewPager);
		pager.setAdapter(adapter);

		selectedPage = adapter.getCount() - 1;
		pager.setCurrentItem(selectedPage, false);
	}

	class ItemClickHandler implements ReportAdapter.OnItemClickHandler {
		@Override
		public void ItemClicked(final ReportAdapter owner, final AgentInfo agent) {
			if (agent != null) {
				if (agent instanceof DivisionInfo) {
					ExpandDivisionData edd = expandDivisionMap.get(agent.id);
					if (edd != null) {
						edd.collapsed = !edd.collapsed;

						if (((DivisionInfo) (agent)).allDivisionAgents.size() > 0) {
							refreshExpandInfo();
							owner.refresh(getAgentsForReport());
						}
					}
				} else {
					List<Hitching> ret = new ArrayList<Hitching>();
					List<Hitching> repResult = new ArrayList<Hitching>();
					repResult.add(new Hitching(RouteResult.class,
							"AgentRouteResult"));
					RouteParam param = new RouteParam();
					param.id = agent.id;
					param.date = adapter.getItemData(selectedPage).date;
					ret.add(new ReportHitching("route", param, repResult));

					UpdateProcess upp = new UpdateProcess(
							(Activity) owner.getContext(),
							new UpdateProcessOwner() {

								@Override
								public void onFinish() {
									AgentRoute.open(owner.getContext(),
											agent.id);
								}

								@Override
								public void enableControlButton(boolean enabled) {
								}
							}, ret);
					upp.execute((Void[]) null);
				}
			}
		}
	}

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
							if (adapter.shift((selectedPage == 2)))
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

	List<AgentInfo> getAgentsForReport() {

		List<AgentInfo> ret = new ArrayList<AgentInfo>();

		List<DivisionInfo> divs = DivisionInfo.getHierarchicalDivisionList();
		HashMap<String, AgentInfo> agents = AgentInfo.loadAgentsMap();

		for (int i = 0; i < divs.size(); i++) {
			DivisionInfo div = divs.get(i);
			ExpandDivisionData edd = expandDivisionMap.get(div.id);

			if (edd == null || !edd.visible)
				continue;

			ret.add(div);

			if (!edd.collapsed) {
				for (int j = 0; j < div.agents.size(); j++) {
					String agent_id = div.agents.get(j).id;
					if (agents.containsKey(agent_id)) {
						AgentInfo ai = agents.get(agent_id);
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

		List<DivisionInfo> divs = DivisionInfo.getHierarchicalDivisionList();

		for (int i = 0; i < divs.size(); i++) {
			ExpandDivisionData edd = new ExpandDivisionData();
			DivisionInfo di = divs.get(i);
			edd.id = di.id;
			edd.parent = di.parent;

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
		switch (item.getItemId()) {
		case R.id.itSettings:
			ManagerConfiguration.open(this);
			return true;
		case R.id.itShowabout:
			new About().show(getSupportFragmentManager(),
					About.class.toString());
			return true;
		case R.id.itExit:
			finish();
			return true;
		case R.id.itEdit:
			Editors.open(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void enableControlButton(final boolean enabled) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				findViewById(R.id.refreshButton).setVisibility(
						(!enabled) ? View.INVISIBLE : View.VISIBLE);
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

			repResult.add(new AgentInfoHitching());
			repResult.add(new RcvNewHitching(Division.class, "Division"));

			repResult.add(new Hitching(AgentReportData.class, "TypeName"));

			ret.add(new ReportHitching("summary",
					new ReportOnAgentForDatesParams(data.getDate(), data
							.getEndDate()), repResult));

			UpdateProcess upp = new UpdateProcess(this, this, ret);
			upp.execute((Void[]) null);
		}
	}

	@Override
	public void onFinish() {
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
		AgentInfo ai = (AgentInfo) a.getItem(ac.position);
		ai.adjustMenu(menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		
		ListView list = (ListView) findViewById(R.id.lvItems);
		Adapter a = list.getAdapter();
		AgentInfo ai = (AgentInfo) a.getItem(info.position);

		switch (item.getItemId()) {
		case R.id.itNapoleon:
			ai.runNapoleon(this);
			return true;
		case R.id.itMessage:
			writeMessage(ai);
			return true;
		default:
			return false;
		}
	}
	
	private void writeMessage(AgentInfo ai) {
		EditMessage dlg = new EditMessage();
		dlg.agentInfo = ai;
		dlg.show(getSupportFragmentManager(), dlg.getClass().toString());
	}

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			shared = IShared.Stub.asInterface(service);

			try {
				SharedPreferences prf = getSharedPreferences(LOGIN_PREF,
						Context.MODE_PRIVATE);
				SharedPreferences.Editor e = prf.edit();
				int duration = shared.getDuration();
				e.putInt(DURATION_KEY, duration);
				e.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
		}
	};
	
	protected void onPause() {
		super.onPause();
		
		try {
			SharedPreferences prf = getSharedPreferences(LOGIN_PREF,
					Context.MODE_PRIVATE);
			shared.putDuration(prf.getInt(DURATION_KEY, 0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	};
}

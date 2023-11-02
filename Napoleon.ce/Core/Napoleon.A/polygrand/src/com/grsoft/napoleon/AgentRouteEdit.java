package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentRoute;
import com.grsoft.dataobjects.AgentRouteItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.impl.AgentRouteImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.dialogs.SelectDialog;
import com.grsoft.napoleon.util.OrgFoldersTree;
import com.grsoft.napoleon.util.WeekDay;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class AgentRouteEdit extends FragmentActivity {
	private Button btnDate;
	private Adapter adapter;
	private ListView list;
	private Button btnAdd;
	private List<Org> agentOrgList = new ArrayList<Org>();
	private List<Org> filterOrgList = new ArrayList<Org>();
	private AgentRouteImpl agentRouteImpl = new AgentRouteImpl();
	private final Date tomorrow = getTomorrow();
	private static final int DIALOG_DATE_PICKER_ID = 0;

	public static void open(Context context) {
		Intent intent = new Intent(context, AgentRouteEdit.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.agentrouteedit);

		btnDate = (Button) findViewById(R.id.btnDate);
		list = (ListView) findViewById(R.id.list);
		btnAdd = (Button) findViewById(R.id.btnAdd);

		btnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment df = new SelectDialog() {
					final boolean selitems[] = new boolean[filterOrgList.size()];
					private ListView listView;

					@Override
					public void onOKButtonPressed(View result) {
						List<Org> lst = new ArrayList<Org>();
						for (int i = 0; i < selitems.length; i++)
							if (selitems[i])
								lst.add((Org) listView.getItemAtPosition(i));

						adapter.updateUserRoute(lst);
						dismiss();
					}

					@Override
					public int getViewId() {
						return R.layout.select_org;
					}

					@Override
					public void prepareView(View view) {
						listView = (ListView) view.findViewById(R.id.list);
						List<String> userRouteIds = adapter.getUserRouteIds();

						for (int i = 0; i < filterOrgList.size(); i++)
							selitems[i] = userRouteIds.contains(((Org) filterOrgList.get(i)).id);

						listView.setAdapter(new BaseAdapter() {

							@Override
							public int getCount() {
								return filterOrgList.size();
							}

							@Override
							public Object getItem(int position) {
								return filterOrgList.get(position);
							}

							@Override
							public long getItemId(int position) {
								return 0;
							}

							@Override
							public View getView(int position, View convertView, ViewGroup parent) {
								if (convertView == null)
									convertView = View.inflate(AgentRouteEdit.this, R.layout.select_org_row, null);

								Org org = (Org) getItem(position);
								TextView tv = (TextView) convertView.findViewById(R.id.tvName);
								tv.setText(org.name);

								tv = (TextView) convertView.findViewById(R.id.tvAddress);
								tv.setText(org.address);

								CheckBox cb = (CheckBox) convertView.findViewById(R.id.cbSelected);
								cb.setTag(position);
								cb.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

									@Override
									public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
										selitems[(Integer) buttonView.getTag()] = isChecked;
									}
								});
								cb.setChecked(selitems[position]);

								return convertView;
							}
						});

					}

					@Override
					public int getTitle() {
						return R.string.select_orgs;
					}
				};

				df.show(getSupportFragmentManager(), df.getClass().toString());
			}
		});

		btnDate.setText(Util.simpleDateFormat.format(tomorrow));
		btnDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				save();
				Intent i = new Intent(AgentRouteEdit.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, agentRouteImpl.getData().date.getTime());
				startActivityForResult(i, DIALOG_DATE_PICKER_ID);
			}
		});

		makeAgentOrgList();

		adapter = new Adapter(this);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final String TASK = "task";
				final String ID = "id";
				DialogFragment df = new SelectDialog() {

					private EditText edTask;

					@Override
					public void prepareView(View view) {
						edTask = (EditText) view.findViewById(R.id.edTask);
						edTask.setText(getArguments().getString(TASK));

						view.findViewById(R.id.ivClear).setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								edTask.setText("");
							}
						});
					}

					@Override
					public void onOKButtonPressed(View result) {
						String task = edTask.getText().toString();
						adapter.setTask(getArguments().getString(ID), task);
						dismiss();
					}

					@Override
					public int getViewId() {
						return R.layout.taskedit;
					}

					@Override
					public int getTitle() {
						return R.string.input_task;
					}
				};

				OrgFolderItem ofi = (OrgFolderItem) parent.getItemAtPosition(position);
				String task = adapter.getTask(ofi.name);
				Bundle args = new Bundle();
				args.putString(TASK, task);
				args.putString(ID, ofi.name);

				df.setArguments(args);
				df.show(getSupportFragmentManager(), df.getClass().toString());
			}
		});

		reload(tomorrow);
	}

	protected Date getTomorrow() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	protected void reload(Date date) {
		agentRouteImpl.read("date", date);
		adapter.reload(agentRouteImpl);

		HashSet<String> ids = new HashSet<String>();
		for (int i = 0; i < adapter.getCount(); i++) {
			if (adapter.getItemId(i) == Adapter.BASE_ROUTE)
				ids.add(((OrgFolderItem) adapter.getItem(i)).name);
		}

		filterOrgList.clear();
		for (Org o : agentOrgList)
			if (!ids.contains(o.id))
				filterOrgList.add(o);

		btnDate.setText(Util.simpleDateFormat.format(date));
	}

	protected void makeAgentOrgList() {
		DbReader reader = new DbReader();
		Org org = new Org();
		boolean bdo = reader.select(org, DataObjectInfo.getInstance().getTableName(org.getClass()), null);

		while (bdo) {
			agentOrgList.add((Org) org.clone());
			bdo = reader.selectNext(org);
		}

		reader.close();

		Collections.sort(agentOrgList, new Comparator<Org>() {
			@Override
			public int compare(Org lhs, Org rhs) {
				return lhs.name.compareTo(rhs.name);
			}
		});
	}

	@Override
	public void onBackPressed() {
		if (adapter.hasChanged())
			save();
		else
			agentRouteImpl.delete();

		agentRouteImpl.close();

		super.onBackPressed();
	}

	protected void save() {
		AgentRoute route = agentRouteImpl.getData();
		route.items.clear();

		for (int i = 0; i < adapter.getCount(); i++) {
			AgentRouteItem ari = new AgentRouteItem();
			ari.id = ((OrgFolderItem) adapter.getItem(i)).name;
			ari.isNew = adapter.getItemId(i) == Adapter.USER_ROUTE ? 1 : 0;
			ari.task = adapter.getTask(ari.id);

			route.items.add(ari);
		}
		
		agentRouteImpl.getData().changed = new Date();
		agentRouteImpl.getData().params = 0;
		
		agentRouteImpl.write();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null && requestCode == DIALOG_DATE_PICKER_ID) {
			Date selDate = new Date(data.getExtras().getLong(ExtrasConst.DATE_TAG, tomorrow.getTime()));
			if (!agentRouteImpl.read("date", selDate)) {
				agentRouteImpl = new AgentRouteImpl();
				agentRouteImpl.getData().date = selDate;
			}

			reload(selDate);
		}
	}
}

class Adapter extends BaseAdapter {
	public final static int BASE_ROUTE = 0;
	public final static int USER_ROUTE = 1;

	private List<OrgFolderItem> baseRoute = new ArrayList<OrgFolderItem>();
	private List<OrgFolderItem> userRoute = new ArrayList<OrgFolderItem>();

	private OrgFoldersTree orgFoldersTree = new OrgFoldersTree();
	private Context context;
	private OrgImpl org = new OrgImpl();
	private Map<String, String> tasks = new HashMap<String, String>();

	public Adapter(Context context) {
		this.context = context;
	}

	public void setTask(String id, String task) {
		tasks.put(id, task);
		notifyDataSetChanged();
	}

	public String getTask(String id) {
		String result = "";

		if (tasks.containsKey(id))
			result = tasks.get(id);

		return result;
	}

	public List<String> getUserRouteIds() {
		List<String> result = new ArrayList<String>();

		for (OrgFolderItem i : userRoute)
			result.add(i.name);

		return result;
	}

	public void reload(AgentRouteImpl impl) {
		AgentRoute ar = impl.getData();
		tasks.clear();
		baseRoute.clear();
		userRoute.clear();

		if (ar.items.size() == 0) {
			Date date = impl.getData().date;
			orgFoldersTree.makeTree(date);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			WeekDay dayOfWeek = WeekDay.getDayBySystemId(calendar.get(Calendar.DAY_OF_WEEK));

			for (int i = 0; i < orgFoldersTree.getCount(); i++)
				if (WeekDay.getWeekDay(((OrgFolders) orgFoldersTree.getItem(i)).name).equals(dayOfWeek)) {
					baseRoute = ((OrgFolders) orgFoldersTree.getItem(i)).items;
					break;
				}
		} else {
			for (int i = 0; i < ar.items.size(); i++) {
				AgentRouteItem arr = ar.items.get(i);
				OrgFolderItem item = new OrgFolderItem();
				item.name = arr.id;
				if (arr.isNew == 1) {
					userRoute.add(item);
				} else {
					baseRoute.add(item);
				}

				setTask(arr.id, arr.task);
			}
		}

		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return baseRoute.size() + userRoute.size();
	}

	@Override
	public Object getItem(int position) {
		return position < baseRoute.size() ? baseRoute.get(position) : userRoute.get(position - baseRoute.size());
	}

	@Override
	public long getItemId(int position) {
		return position < baseRoute.size() ? BASE_ROUTE : USER_ROUTE;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(context, R.layout.agentrouteedut_row, null);

		OrgFolderItem of = (OrgFolderItem) getItem(position);
		org.getData().id = of.name;
		org.read();
		org.close();

		TextView tv = (TextView) convertView.findViewById(R.id.tvName);
		tv.setText(org.getData().name);

		tv = (TextView) convertView.findViewById(R.id.tvAddress);
		tv.setText(org.getData().address);

		if (getItemId(position) == BASE_ROUTE)
			convertView.setBackgroundResource(R.drawable.base_org_selector);
		else
			convertView.setBackgroundResource(R.drawable.user_org_selector);

		String task = getTask(of.name);
		convertView.findViewById(R.id.ivTask).setVisibility(task.length() > 0 ? View.VISIBLE : View.INVISIBLE);
		return convertView;
	}

	public void updateUserRoute(List<Org> orgs) {
		userRoute.clear();

		for (Org o : orgs) {
			OrgFolderItem i = new OrgFolderItem();
			i.name = o.id;
			userRoute.add(i);
		}

		notifyDataSetChanged();
	}

	public boolean hasChanged() {
		return tasks.size() > 0 || userRoute.size() > 0;
	}
}

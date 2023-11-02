package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.AgentRcv;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.FilterCmp;
import com.grsoft.dataobjects.impl.ReportsRequestImpl;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.FindOnClickListener;
import com.grsoft.napoleon.util.OrgFoldersTree;
import com.grsoft.network.Format;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class MainEx extends Main {
	Spinner spFilter;
	BroadcastReceiver screenOn;
	int count = 0;

	static boolean DEBUG = false;

	@Override
	protected int getResourceID() {
		return R.layout.mainex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initCount();
		super.onCreate(savedInstanceState);

		spFilter = (Spinner) findViewById(R.id.spFilter);
		spFilter.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				FilterAdapter adapter = (FilterAdapter) list.getAdapter();
				if(position == 0)
					adapter.resetFilter();
				else
					adapter.applyFilter(edFind.getText().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});

		findViewById(R.id.btnRepReq).setOnClickListener(repReqClick());

		if(!DEBUG) {
		screenOn = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(Consts.D_TAG, "MyReceiver");

				if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
					Log.d(Consts.D_TAG, "Screen ON");
				} else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
					Log.d(Consts.D_TAG, "Screen OFF");
					initCount();
					if(count > 0)
						LoginActivity.open(context);
				}
			}
		};

		registerReceiver(screenOn, new IntentFilter(Intent.ACTION_SCREEN_ON));
		registerReceiver(screenOn, new IntentFilter(Intent.ACTION_SCREEN_OFF));

		if (count == 0) {
			UpdateDBEx.openBlocked(this);
		} else {
			LoginActivity.open(this);
		}

		if( ServerCommand.DeviceID == null )
			ServerCommand.DeviceID = "123456";
		if(PinChecker.getIsRegistred(this) == false) {
			Registration.open(this);
		}
		}
	}

	private void initCount() {
		String tableName = DataObjectInfo.getInstance().getTableName(AgentRcv.class);
		Cursor c = null;
		try {
			c = DataBaseManager.getDataBase().rawQuery("select count(*) from " + tableName + " where id=userid", null);
			if (c.moveToNext())
				count = c.getInt(0);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null)
				c.close();
		}
	}

	private OnClickListener repReqClick() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ReportsRequestImpl impl = new ReportsRequestImpl();
				impl.getData().date = Util.getDateTime();
				impl.write();
				impl.close();
				
				new RepReqSync(v.getContext(), v){
					protected void onPostExecute(Boolean result) {
						super.onPostExecute(result);
						
						if(result)
							Toast.makeText(MainEx.this, R.string.request_report_success, Toast.LENGTH_SHORT).show();
					};
				}.execute((Void[])null);
			}
		};
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(!DEBUG) {
			LoginActivity.open(this);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (screenOn != null) {
			unregisterReceiver(screenOn);
			screenOn = null;
		}
	}

	@Override
	protected FindOnClickListener createFindOnClickListener() {
		return new FindOnClickListener(edFind, list, llFind) {
			@Override
			public void beginFiltering() {
				spFilter.setAdapter(new SpinnerAdapter() {
					List<KeyValue> data = new ArrayList<KeyValue>();
					{
						ConfigImpl cfg = new ConfigImpl();
						StringBuilder sb = new StringBuilder();

						if (cfg.getValue(sb, "Организация"))
							DialogHelper.makeListWithKey(sb.toString(), data, null);

						data.add(0, new KeyValue("-1", "Все"));
					}

					@Override
					public void unregisterDataSetObserver(DataSetObserver observer) {}

					@Override
					public void registerDataSetObserver(DataSetObserver observer) {}

					@Override
					public boolean isEmpty() {
						return false;
					}

					@Override
					public boolean hasStableIds() {
						return false;
					}

					@Override
					public int getViewTypeCount() {
						return 1;
					}

					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						if (convertView == null)
							convertView = View.inflate(MainEx.this, R.layout.simple_spinner_layout, null);

						TextView tv = (TextView) convertView.findViewById(R.id.tvFirmaName);
						tv.setText(((KeyValue) getItem(position)).value.toString());
						return tv;
					}

					@Override
					public int getItemViewType(int position) {
						return 0;
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

					@Override
					public View getDropDownView(int position, View convertView, ViewGroup parent) {
						return getView(position, convertView, parent);
					}
				});

				super.beginFiltering();
			}
		};
	}

	protected BaseAdapter createSolidMainAdapter() {
		return new SolidMainAdapter(this) {
			String sql = "";

			@Override protected String getWhereStr() { return sql; }

			@SuppressLint("DefaultLocale")
			@Override
			public void applyFilter(String value) {
				if (spFilter.getSelectedItemPosition() > 0) {
					sql = "orgid='" + (((KeyValue) spFilter.getSelectedItem()).key) + "'";
				} else {
					sql = "";
				}

				super.applyFilter(value);
			}
		};
	}

	@Override
	protected BaseAdapter createFoldersMainAdapter() {
		return new FoldersMainAdapter(this) {
			@Override
			protected FilterComparer createRouteFilter() {
				return new FilterComparer() {
					@SuppressLint("DefaultLocale")
					@Override
					public boolean compareTo(DataObject dataObject, String filter) {
						OrgFolderItem ofi = (OrgFolderItem) dataObject;
						orgImpl.getData().id = ofi.name;
						if (!orgImpl.read())
							return false;

						boolean result = true;
						
						if(filter.length() > 0)
							result = orgImpl.getData().srchName.contains(filter.toUpperCase());

						if((result || filter.length() == 0) && spFilter.getSelectedItemPosition() > 0) {
							String orgid = ((KeyValue) spFilter.getSelectedItem()).key.toString();
							result = ((OrgEx) orgImpl.getData()).orgid.equals(orgid);
						}

						return result;
					}
				};
			}

			protected OrgFoldersTree createOrgFoldersTree() {
				return new OrgFoldersTree() {
					public void applyFilter(FilterCmp filter, String value) {
						if (value.length() == 0 && spFilter.getSelectedItemPosition() == 0) {
							if (filteredArray != null)
								filteredArray = null;
							return;
						}

						if (filteredArray == null)
							filteredArray = new ArrayList<OrgFolderItem>();
						else
							filteredArray.clear();

						if (currentOrgFolder == null)
							for (int i = 0; i < orgFolders.size(); i++)
								processItems(filter, orgFolders.get(i).items, value);
						else
							processItems(filter, currentOrgFolder.items, value);
					}

				};
			}
		};
	}
}

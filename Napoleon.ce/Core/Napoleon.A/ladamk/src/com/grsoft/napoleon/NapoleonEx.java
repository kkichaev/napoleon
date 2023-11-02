package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
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
import com.grsoft.napoleon.documents.DailyPlanDoc;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.FindOnClickListener;
import com.grsoft.napoleon.util.OrgFoldersTree;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class NapoleonEx extends Napoleon {
	Spinner spFilter;
	
	protected String getOrgReadingFields() {
		return "name,id,address,color,flags,debt";
	}

	@Override
	protected int getResourceID() {
		return R.layout.mainex;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		int count = 0;
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

		super.onCreate(savedInstanceState);

		spFilter = (Spinner) findViewById(R.id.spFilter);

		if (count == 0) {
			UpdateDBEx.openBlocked(this);
		} else {
//			screenOn = new BroadcastReceiver() {
//
//				@Override
//				public void onReceive(Context context, Intent intent) {
//					Log.d(Consts.D_TAG, "MyReceiver");
//
//					if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
//						Log.d(Consts.D_TAG, "Screen ON");
//					} else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
//						Log.d(Consts.D_TAG, "Screen OFF");
//						LoginActivity.open(context);
//					}
//				}
//			};
//
//			registerReceiver(screenOn, new IntentFilter(Intent.ACTION_SCREEN_ON));
//			registerReceiver(screenOn, new IntentFilter(Intent.ACTION_SCREEN_OFF));
//			LoginActivity.open(this);
		}

		spFilter.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				FilterAdapter adapter = (FilterAdapter) lvMainOrgs.getAdapter(); 
				
				if(position == 0)
					adapter.resetFilter();
				else
					adapter.applyFilter(edFind.getText().toString());

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		
		findViewById(R.id.btnRepReq).setOnClickListener(repReqClick());
	}
	
	@Override
	protected void onResume() {
		try {
			DailyPlanDoc.instance().refreshDocSum();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		super.onResume();
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
							Toast.makeText(NapoleonEx.this, R.string.request_report_success, Toast.LENGTH_SHORT).show();
					};
				}.execute((Void[])null);
			}
		};
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		//LoginActivity.open(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (screenOn != null) {
			unregisterReceiver(screenOn);
			screenOn = null;
		}
	}

	BroadcastReceiver screenOn;

	@Override
	protected FindOnClickListener createFindOnClickListener() {
		return new FindOnClickListener(edFind, lvMainOrgs, llFind) {
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
						return 0;
					}

					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						if (convertView == null)
							convertView = View.inflate(NapoleonEx.this, R.layout.simple_spinner_layout, null);

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

	protected BaseAdapter getMainOrgAdapter() throws IllegalAccessException, InstantiationException {
		return new MainOrgsAdapter(this) {
			
			@Override
			public void resetFilter() {
				cursor.setCondition("");
				super.resetFilter();
			}
			
			@SuppressLint("DefaultLocale")
			@Override
			public void applyFilter(String value) {
				if (value.length() == 0 && spFilter.getSelectedItemPosition() == 0) {
					resetFilter();
					return;
				}

				StringBuilder sql = new StringBuilder();
				if (value.length() > 0)
					sql.append("srchName LIKE '%").append(value.toUpperCase()).append("%'");

				if (spFilter.getSelectedItemPosition() > 0) {
					if (sql.length() > 0)
						sql.append(" and ");

					sql.append("orgid='").append(((KeyValue) spFilter.getSelectedItem()).key).append("'");
				}

				cursor.setCondition(sql.toString());
				super.notifyDataSetChanged();
			}
		};
	}

	@Override
	protected OrgFoldersAdapter getOrgFoldersAdapter() {
		return new OrgFoldersAdapter() {
			@Override
			protected RouteFilter createRouteFilter() {
				return new RouteFilter() {
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
						
						String orgid = "";
						
						if (spFilter.getSelectedItemPosition() > 0)
							orgid = ((KeyValue) spFilter.getSelectedItem()).key.toString();
						
						int flen = filter.length();
						if(flen == 0 || (flen > 0 && result))
							result = ((OrgEx) orgImpl.getData()).orgid.equals(orgid);
						
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

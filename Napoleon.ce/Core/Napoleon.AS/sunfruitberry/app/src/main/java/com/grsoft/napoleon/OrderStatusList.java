package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrderDriverRouteInfo;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderRouteInfo;
import com.grsoft.dataobjects.OrderStatusData;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.RoutePhotos;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadServiceBase;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.util.ProgressManager;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class OrderStatusList extends BaseActivity implements OrgSelectDialog.OrgSelect {
	
	protected static final int SET_START_DATE = 1;
	protected static final int SET_END_DATE = 2;
	protected static final int SELECT_ORG = 10;
	
	List<OrderStatusData> status = new ArrayList<OrderStatusData>();
	
	Adapter adapter;
	
	Date start, end;
	
	public static void open(Context context) {
		Intent i = new Intent(context, OrderStatusList.class);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.order_status);
		init();		
	}
	
	void init() {
		start = new Date(Util.getDate().getTime() - 24 * 3600 * 1000);
		end = Util.getDayEnd(Util.getDate());
				
		for(int i : new int[] { OrderStatusData.STATUS_ALL, OrderDriverRouteInfo.STATUS_ACTIVE, OrderDriverRouteInfo.STATUS_REJECT,
				OrderDriverRouteInfo.STATUS_FINISHED, OrderDriverRouteInfo.STATUS_IN_ROUTE, 
				OrderDriverRouteInfo.STATUS_DONE_WIITH_RETURNS, OrderStatusData.STATUS_NOT_IN_ROUTE}) {
			status.add(OrderStatusData.create(i));
		}
		
		Spinner sp = (Spinner) findViewById(R.id.spStatus);
		ArrayAdapter<OrderStatusData> aa = new ArrayAdapter<OrderStatusData>(this, R.layout.filter_spinner, status);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		sp.setAdapter(aa);
		sp.setSelection(0);
		
		sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				OrderStatusData sd = status.get(arg2);
				adapter.filter(sd);
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});

		findViewById(R.id.tvFilterFrom).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(OrderStatusList.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, start.getTime());
				startActivityForResult(i, SET_START_DATE);
			}
		});

		findViewById(R.id.tvFilterTo).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(OrderStatusList.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, end.getTime());
				startActivityForResult(i, SET_END_DATE);
			}
		});
		
		adapter = new Adapter();
		adapter.refresh(start, end, !((CheckBox)findViewById(R.id.cbSort)).isChecked());
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(adapter);
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				RowData rd = (RowData) adapter.getItem(arg2);
				OrderImpl oi = new OrderImpl();
				oi.getData().created = rd.order.created;
				oi.read();
				oi.close();
				oi.open(OrderStatusList.this);
				return true;
			}
		});
		
		((CheckBox)findViewById(R.id.cbSort)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				adapter.sort(!arg1);
			}
		});
		
		findViewById(R.id.btnRefresh).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { sync(); }
		});
		
		findViewById(R.id.btnDel).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) {
				adapter.setFiltredOrg(null); 
				updateOrgFilter();
			}
		});
		
		findViewById(R.id.tvOrg).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(SELECT_ORG); }
		});

		updateDate();
		updateOrgFilter();
	}
	
	void updateOrgFilter() {
		String text;
		Org fo = adapter.getFiltredOrg();
		if(fo == null)
			text = "<u>" + getString(R.string.select_org) + "</u>";
		else 
			text = "<u>" + fo.name + " <small><i>" + fo.address + "</i></small>" + "</u>";
		((TextView)findViewById(R.id.tvOrg)).setText(Html.fromHtml(text));		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == SELECT_ORG)
			return OrgSelectDialog.create(this, this);
		return super.onCreateDialog(id);
	}
	
	void sync() {
		new Sync().execute((Void[])null);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data != null && (requestCode == SET_START_DATE || requestCode == SET_END_DATE)) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			if(requestCode == SET_START_DATE)
				start = new Date(ct);
			else
				end = Util.getDayEnd(new Date(ct));
			updateDate();
			adapter.refresh(start, end, !((CheckBox)findViewById(R.id.cbSort)).isChecked());
		}
	}
	
	void updateDate() {
		TextView tv;
		String text;
		text = "<font color='white'><u>C " + Util.simpleDateFormat.format(start) + "</u></font>";
		tv = (TextView)findViewById(R.id.tvFilterFrom);
		tv.setText(Html.fromHtml(text));

		text = "<font color='white'><u>по " + Util.simpleDateFormat.format(end) + "</u></font>";
		tv = (TextView)findViewById(R.id.tvFilterTo);
		tv.setText(Html.fromHtml(text));
	}
	
	View.OnClickListener callPhone = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			String phone = (String) arg0.getTag();
			if(phone != null && phone.length() > 0) {
				Intent intent = new Intent(Intent.ACTION_CALL,  Uri.parse(String.format("tel: %s", phone)));
				OrderStatusList.this.startActivity(intent);
			}
		}
	};
	
	class Sync extends NetworkAsyncTask {
		public Sync() {
			super(new ProgressManager(OrderStatusList.this));
			((ProgressManager) this.progressHelper).setUpdateProcess(this);
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			onUpdate(UpdateStatus.BEGIN_UPDATE, 0);

			Config config = ConfigManager.getConfig();
			LoginData ld = new LoginData(config.login, config.passw, config.impersonate, OrderStatusList.this);
			List<Hitching> h = new ArrayList<Hitching>();
			h.add(new RcvNewHitching(RoutePhotos.class));
			ReadServiceBase read = RWServiceFactory.instance.createReadService(h);
			try {
				if(read.update(OrderStatusList.this, ld, false)) {
					runOnUiThread(new Runnable() {
						@Override public void run() { adapter.refresh(start, end, !((CheckBox)findViewById(R.id.cbSort)).isChecked()); }
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			onUpdate(UpdateStatus.END_OF_PROCESS, 0);
			return true;
		}
	}
	
	public static class RowData {
		public String name = "";
		public String address = "";
		
		public OrderEx order = null;		
		public OrderStatusData status = null;
		
		public OrderRouteInfo ri = null;
		
		public RoutePhotos photo = null;
		
		public String docStatus() {
			if(status != null)
				return status.text;
			if(ri == null)
				return "Маршрут не назначен";
			return ri.toText();
		}
	}
	
	class SortName implements Comparator<RowData> {

		@Override
		public int compare(RowData arg0, RowData arg1) {
			int cmp = arg0.name.compareTo(arg1.name);
			if( cmp != 0)
				return cmp;
			cmp = arg0.address.compareTo(arg1.address);
			if(cmp != 0)
				return cmp;
			return arg0.docStatus().compareTo(arg1.docStatus());
		}		
	}
	
	class SortStatus implements Comparator<RowData> {

		@Override
		public int compare(RowData arg0, RowData arg1) {
			int cmp = arg0.docStatus().compareTo(arg1.docStatus());
			if( cmp != 0)
				return cmp;
			cmp = arg0.name.compareTo(arg1.name);
			if(cmp != 0)
				return cmp;
			return arg0.address.compareTo(arg1.address);
		}		
	}
	
	class Adapter extends BaseAdapter {
		
		List<RowData> data = new ArrayList<OrderStatusList.RowData>();
		List<RowData> allData = new ArrayList<OrderStatusList.RowData>();
		Org filtredOrg = null;
		
		boolean curSort;

		public void refresh(Date start, Date end, boolean sortByName) {
			
			allData.clear();
			
			// docNumber - >RowData
			final Map<String, RowData> orders = new HashMap<String, RowData>();
			
			// docNumber - >RoutePhoto
			final Map<String, RoutePhotos> photos = new HashMap<String, RoutePhotos>();

			final OrgImpl oi = new OrgImpl();
			final OrgEx oe = (OrgEx) oi.getData();
			
			// docNumber -> ORI
			final Map<String, OrderRouteInfo> routes = new HashMap<String, OrderRouteInfo>();
			//RouteItem -> Route
			final Map<String, String> routeItems = new HashMap<String, String>();
			final List<String> drivers = new ArrayList<String>();
			
			String where = "finish >= " + Long.toString(start.getTime());
			DataTraveler.travel(OrderRouteInfo.class, new DataTraveler.Travel<OrderRouteInfo>(true) {

				@Override
				public boolean travel(DataTraveler<OrderRouteInfo> item) {
					routes.put(item.data.number, item.data);
					routeItems.put(item.data.itemid, item.data.route);
					return true;
				}
			}, where);
			
			where = "created >= " + Long.toString(start.getTime());
			DataTraveler.travel(RoutePhotos.class, new DataTraveler.Travel<RoutePhotos>(true) {

				@Override
				public boolean travel(DataTraveler<RoutePhotos> item) {
					photos.put(item.data.routeItemId, item.data);
					return true;
				}
			}, where);
			
			DataTraveler.travel(OrderEx.class, new DataTraveler.Travel<OrderEx>(true) {

				@Override
				public boolean travel(DataTraveler<OrderEx> item) {
					RowData rd = new RowData();
					rd.order = item.data;
					rd.ri = routes.get(item.data.docNumber);
					if(rd.ri == null)
						rd.status = OrderStatusData.create(OrderStatusData.STATUS_NOT_IN_ROUTE);
					else {
						rd.photo = photos.get(rd.ri.itemid);						
					}
					
					oe.id = item.data.id;
					if(oi.read()) {
						rd.address = oe.address;
						rd.name = oe.name;
					} else {
						rd.name = "Код контрагента [" + oe.id + "]";
					}
					
					orders.put(item.data.docNumber, rd);
					return true;
				}
			}, where + " and created <= " + Long.toString(end.getTime()));
			
			DataTraveler.travel(OrderDriverRouteInfo.class, new DataTraveler.Travel<OrderDriverRouteInfo>(){

				@Override
				public boolean travel(DataTraveler<OrderDriverRouteInfo> item) {
					if(item.data.status == OrderDriverRouteInfo.STATUS_ACTIVE) {
						if(drivers.contains(item.data.userid) == false) {
							drivers.add(item.data.userid);
							markActiveRoute(item.data.routeItemId, routeItems.get(item.data.routeItemId), orders.values());
						}
					} else if(item.data.docNumber.length() > 0){
						OrderStatusData osd = OrderStatusData.create(item.data.status);
						if(osd != null) {
							RowData rd = orders.get(item.data.docNumber);
							if(rd != null && (rd.status == null || rd.status.status == OrderDriverRouteInfo.STATUS_IN_ROUTE))
								rd.status = osd;
						}
					}
					return true;
				}
				
			}, where, "created desc");
			
			oi.close();
			
			allData.addAll(orders.values());
			
			curSort = sortByName;
			OrderStatusData osd = (OrderStatusData) ((Spinner) findViewById(R.id.spStatus)).getSelectedItem();
			filter(osd);
		}
		
		public Org getFiltredOrg() { return filtredOrg; }
		
		public void sort(boolean sortByName) {
			curSort = sortByName;
			Collections.sort(data, sortByName ? new SortName() : new SortStatus());
			notifyDataSetChanged();
		}

		protected void markActiveRoute(String routeItemId, String route, Collection<RowData> values) {
			for(RowData rd : values) {
				if(rd.ri != null && rd.status == null && rd.ri.route.equals(route)) {
					rd.status = OrderStatusData.create(rd.ri.itemid.equals(routeItemId) ? OrderDriverRouteInfo.STATUS_ACTIVE : 
						OrderDriverRouteInfo.STATUS_IN_ROUTE);
				}
			}			
		}
		
		boolean sameStatus(RowData rd, OrderStatusData status) {
			return (rd.status != null && rd.status.status == status.status) || 
					(status.status == OrderDriverRouteInfo.STATUS_IN_ROUTE && rd.status == null && rd.ri != null);
		}
		
		public void filter(OrderStatusData status) {
			data.clear();
			if(status.status == OrderStatusData.STATUS_ALL && filtredOrg == null)
				data.addAll(allData);
			else {
				for(RowData rd : allData)
					if((status.status == OrderStatusData.STATUS_ALL || sameStatus(rd, status)) && 
							(filtredOrg == null || rd.order.id.equals(filtredOrg.id)) ) {
						data.add(rd);
					}
			}
			sort(curSort);
		}
		
		@Override public int getCount() { return data.size(); }
		@Override public Object getItem(int arg0) { return data.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if(view == null) {
				view = View.inflate(OrderStatusList.this, R.layout.order_status_row, null);
			}
			
			final RowData rd = (RowData) getItem(arg0);
			
			String text;
			TextView tv;
			
			text = "<b>" + rd.name + "</b><br/><small>" + rd.address + "</small>";
			tv = (TextView) view.findViewById(R.id.tvName);
			tv.setText(Html.fromHtml(text));
			
			tv = (TextView) view.findViewById(R.id.tvDriver);
			if(rd.ri != null) {
				text = Util.simpleDateFormat.format(rd.ri.finish) + "&nbsp;" + rd.ri.name + "&nbsp;&nbsp;<u>" + rd.ri.phone + "</u>";
				tv.setTag(rd.ri.phone);
				tv.setOnClickListener(callPhone);
			} else {
				text = "";
				tv.setOnClickListener(null);
				
			}
			tv.setText(Html.fromHtml(text));
			
			text = rd.order.docNumber + " " + Util.simpleDateFormat.format(rd.order.date) + "<br/>" + Util.IntToScaleStr(rd.order.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false);
			tv = (TextView) view.findViewById(R.id.tvDoc);
			tv.setText(Html.fromHtml(text));

			text = rd.docStatus();
			tv = (TextView) view.findViewById(R.id.tvStatus);
			tv.setText(Html.fromHtml(text));
			
			ImageView iv = (ImageView) view.findViewById(R.id.ivPhoto);
			if(rd.photo != null) {
				iv.setVisibility(View.VISIBLE);
				iv.setOnClickListener(new View.OnClickListener() {
					@Override public void onClick(View arg0) {
						RoutePhotoList.open(OrderStatusList.this, rd.photo);
					}
				});
			} else {
				iv.setVisibility(View.INVISIBLE);
				iv.setOnClickListener(null);
			}
			
			return view;
		}

		public void setFiltredOrg(Org o) {
			filtredOrg = o;
			OrderStatusData osd = (OrderStatusData)((Spinner) findViewById(R.id.spStatus)).getSelectedItem();
			filter(osd);
		}
		
	}

	@Override
	public void selected(Org o) {
		adapter.setFiltredOrg(o);
		updateOrgFilter();
	}
}

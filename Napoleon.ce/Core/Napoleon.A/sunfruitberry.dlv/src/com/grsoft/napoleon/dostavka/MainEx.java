package com.grsoft.napoleon.dostavka;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.DocumentRestore;
import com.grsoft.database.HitchOnSelect;
import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DShipment;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Dispatch;
import com.grsoft.dataobjects.DispatchDocDataObject;
import com.grsoft.dataobjects.DispatchItem;
import com.grsoft.dataobjects.DispatchReturnsInfo;
import com.grsoft.dataobjects.DriverRouteActions;
import com.grsoft.dataobjects.ItemDef;
import com.grsoft.dataobjects.OrderProceededEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Route;
import com.grsoft.dataobjects.RouteDocEx;
import com.grsoft.dataobjects.RouteItem;
import com.grsoft.dataobjects.RouteItemRow;
import com.grsoft.dataobjects.RoutePoint;
import com.grsoft.dataobjects.Waybill;
import com.grsoft.dataobjects.impl.DispatchImpl;
import com.grsoft.napoleon.ReportList;
import com.grsoft.napoleon.documents.DShipmentDoc;
import com.grsoft.napoleon.documents.DispatchDoc;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.DriverRouteActionsExport;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.OrderProceededExport;
import com.grsoft.util.Util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class MainEx extends Main {
	
	Menu mainMenu;
	RouteItem newActivePoint;
	DriverRouteActions prevActionPoint;
	
	public static String RESTORE_BASE = "com.grsoft.napoleon.dostavka.RestoreBase";
	public static String RESTORE_MONTHS = "RestoreMonths";
	
	public static MainService MAIN_SERVICE = null;
	
	int restoreMonths = -1;
	boolean restoreDispatch = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		registerForContextMenu(list);
		
		registerReceiver(rcvRestore, new IntentFilter(RESTORE_BASE));
	}
	
	BroadcastReceiver rcvRestore = new BroadcastReceiver() {
		@Override public void onReceive(Context arg0, Intent arg1) { 
			restoreMonths = arg1.getIntExtra(RESTORE_MONTHS, 1); 
			Intent i = new Intent(arg0, MainEx.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			arg0.startActivity(i);
		}
	};
	
	protected void onResume() {
		super.onResume();
		
		if(restoreMonths > 0) {
			doRestore();
		}
	}
	
	void doRestore() {
		int months = restoreMonths;
		restoreMonths = -1;
		restoreDispatch = true;
		
		List<Hitching> h = new ArrayList<Hitching>();

		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -months);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		String where = "[finish] >= ToDate('" + sdf.format(c.getTime()) + "') and [userid] = '$CURRENT_USERID'";
		
		h.add(new HitchOnSelect(Route.class, "Route", where, true));
		String where1 = "route in (select id from route where " + where + ")";

		h.add(new HitchOnSelect(RouteItem.class, "RouteItem", where1, true));
		
		where1 = "number in (select d.number from Route as r left join RouteItem as ri on r.id = ri.route "+
				"left join RouteItem$docs as d on ri.itemid = d.RouteItem$itemid where r.[finish] >= ToDate('" + 
				sdf.format(c.getTime()) + "') and r.[userid] = '$CURRENT_USERID')"; 
		h.add(new HitchOnSelect(Waybill.class, "Waybill", where1, true));
		
		h.add(new HitchOnSelect(RoutePoint.class, "Org", "id in (select id from RouteItem where route in (select id from route where " + where + "))", true));
		h.add(new RcvNewHitching(Price.class, "Price"));
		
//		h.add(new DocumentRestore(DispatchDoc.instance(), "Dispatch", months) {
//			@Override
//			protected void beforeWrite(DataObject dobj) {
//				super.beforeWrite(dobj);
//				((Dispatch)dobj).params &= (~Dispatch.NOT_READY_TO_SEND);
//			}
//		});
		h.add(new DocumentRestore(DShipmentDoc.instance(), "DShipment", months){
			@Override
			protected void beforeWrite(DataObject dobj) {
				super.beforeWrite(dobj);
				((DispatchDocDataObject)dobj).params &= (~Dispatch.NOT_READY_TO_SEND);
			}
		});

		progress.show(getSupportFragmentManager(), progress.getClass().toString());
		
		mainsrv.recieve(h);	
	}
	
	static class RouteData {
		public RouteItem item;
		public ItemDef doc;
	}
	
	Map<String, RouteData> loadRoutes() {
		final Map<String, RouteData> routes = new HashMap<String, MainEx.RouteData>();
		
		DataTraveler.travel(RouteItem.class, new DataTraveler.Travel<RouteItem>(true) {

			@Override
			public boolean travel(DataTraveler<RouteItem> item) {
				for(ItemDef id : item.data.docs) {
					RouteData rd = new RouteData();
					rd.doc = id;
					rd.item = item.data;
					routes.put(id.number, rd);
				}
				return true;
			}
		}, "");
		
		return routes;
	}
	
	Dispatch createDispatch(RouteItem ri) {
		Dispatch d = new Dispatch();
		d.created = Util.getDateTime();
		
		d.id = ri.id;
		d.latitude = 0;
		d.longitude = 0;
		d.stltime = 0;
		d.params = ParamState.ofExported;
		
		TimeZone tz = TimeZone.getDefault();
		Date now = new Date();
		d.timeZone = -tz.getOffset(now.getTime()) / (60*1000);
		
		for (ItemDef id : ri.docs) {
			DispatchItem i = new DispatchItem();
			i.number = id.number;
			i.type = id.type;
			i.remark = id.remark;
			i.itemid = id.id;
			d.items.add(i);
		}

		d.itemid = ri.itemid;
		
		return d;
	}
	
	void bindDocs(Dispatch d, DShipment item) {
		d.created = item.dispatch;
		for(DispatchItem di : d.items) {
			if(di.number.equals(item.number)) {
				di.date = item.created;
				di.state = DispatchItem.DOC_INITED;
				break;
			}
		}
	}
	
	@Override
	protected void reload() {
		super.reload();
		
		if(restoreDispatch) {
			restoreDispatch = false;

			// doc.num => RouteData
			final Map<String, RouteData> routes = loadRoutes();
			final DbWriter wr = new DbWriter();
			
			// itemid => Dispatch
			final Map<String, Dispatch> dsp = new HashMap<String, Dispatch>();
			
			DataTraveler.travel(DShipment.class, new DataTraveler.Travel<DShipment>(true) {

				@Override
				public boolean travel(DataTraveler<DShipment> item) {
					RouteData rd = routes.get(item.data.number);
					if(rd != null) {
						Dispatch d = dsp.get(rd.item.itemid);
						if(d == null) {
							d = createDispatch(rd.item);
							dsp.put(rd.item.itemid, d);
						}
						bindDocs(d, item.data);
						item.data.routeItemId = rd.item.itemid;
						wr.insertRecord(item.data);
					}
					return true;
				}
			}, "");
			
			for(Dispatch d : dsp.values()) {
				wr.insertRecord(d);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(rcvRestore);
	}	
	
	@Override protected int getOptionsMenuID() { return R.menu.main_options_menu_ex; }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId(); 
		if(id == R.id.itDriverStatus) {
			(new SetDriverStatus()).show(getFragmentManager(), "");
			return true;
		}
		if(id == R.id.itReports) {
			syncConfig();
			ReportList.open(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// for reportlist sync
	private void syncConfig() {
		Config c = ConfigManager.getConfig();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		
		c.address = pref.getString(getString(R.string.ip1_pref), "");
		c.address2 = pref.getString(getString(R.string.ip2_pref), "");
		c.port =  Integer.parseInt(pref.getString(getString(R.string.port_pref),getString(R.string.def_port_val)));
		c.port2 = c.port;
		c.login = pref.getString(getString(R.string.login_pref), "");
		c.passw = pref.getString(getString(R.string.pass_pref), "");
		
		try {
			ConfigManager.save(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean ret = super.onCreateOptionsMenu(menu);
		mainMenu = menu;
		return ret;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.route_item_options, menu);
	}
	
	void askAboutNewPoint(RouteItem newItem) {
		AskAbandRoute dlg = new AskAbandRoute();
		dlg.show(getFragmentManager(), "");
	}

	void markNewPoint() {
		if(newActivePoint == null)
			return;
		
		DriverRouteActions.setStatus(newActivePoint.itemid, "", DriverRouteActions.STAUS_ACTIVE, "");
		adapter.reload(workDate, false);
		sendStatus();
	}
	
	@Override
	public void doSync(boolean clear) {
		if(mainsrv.isLoginChanged()) {
			SQLiteDatabase db = DataBaseManager.getDataBase();
			String [] tables = new String[] {
					(new OrderProceededEx()).getTableName(),
					(new DriverRouteActions()).getTableName(),
					(new DShipment()).getTableName(),
					(new Dispatch()).getTableName(),
					(new DispatchReturnsInfo()).getTableName(),
			};
			for(String tn : tables) {
				String sql = "DELETE FROM " + tn;
				try {
					db.execSQL(sql);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		DbWriter.checkDBTable(OrderProceededEx.class);
		super.doSync(clear);
	}
	
	void sendStatus() {
		List<ObjectListener> toSend = new ArrayList<ObjectListener>();
		toSend.add(new DriverRouteActionsExport());
		toSend.add(new OrderProceededExport());
		mainsrv.send(toSend, true);
	}
	
	void updateStatusText(String newStatus) {
		MenuItem mi = mainMenu.findItem(R.id.itDriverStatus);
		if(mi != null)
			mi.setTitle(newStatus);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo ami = (AdapterContextMenuInfo) item.getMenuInfo();
		if(item.getItemId() == R.id.itStartDoing) {
			RouteItemRow ritem = (RouteItemRow) adapter.getItem(ami.position);
			boolean canStart = true;
			DispatchImpl dimpl = DispatchImpl.create();
			if(dimpl.readFromId(ritem.item.itemid)) {
				if(dimpl.isEditable() == false || dimpl.isDocFinished(this))
					canStart = false;
			}
			
			if(canStart) {
				boolean isDocsCompleete = true;
				for(ItemDef id :ritem.item.docs) {
					if(((RouteDocEx)id).dlvNumber.length() == 0) {
						isDocsCompleete = false;
						break;
					}
				}
				if(isDocsCompleete) {
					prevActionPoint = DriverRouteActions.getActiveItem();
					if(!prevActionPoint.routeItemId.equals(ritem.item.itemid)) {
						newActivePoint = ritem.item;
						if( !prevActionPoint.isEmpty() && dimpl.readFromId(prevActionPoint.routeItemId) && !dimpl.isDocFinished(this) ) {
							askAboutNewPoint(ritem.item);
						} else {
							markNewPoint();
						}
					}
				} else {
					Toast.makeText(this, "Нет реализаций по документам", Toast.LENGTH_LONG).show();					
				}
			} else {
				Toast.makeText(this, "Работа в точке уже выполнены", Toast.LENGTH_LONG).show();
			}
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	protected void onServiceConnected() {
		MAIN_SERVICE = mainsrv;
		List<ObjectListener> toSend = new ArrayList<ObjectListener>();
		toSend.add(new DriverRouteActionsExport());
		toSend.add(new OrderProceededExport());
		mainsrv.addSendedData(toSend);
	}
	
	class AskAbandRoute extends DialogFragment implements DialogInterface.OnClickListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
			b.setTitle("Предупреждение");
			b.setMessage("Есть активная не завершенная точка маршрута. Назначить другую точку активной?");
			b.setPositiveButton(android.R.string.yes, this);
			b.setNegativeButton(android.R.string.no, this);
			return b.create();
		}
		
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			if(arg1 == Dialog.BUTTON_POSITIVE) {
				if(prevActionPoint != null && !prevActionPoint.isEmpty()) {
					DriverRouteActions.setStatus(prevActionPoint.routeItemId, "", DriverRouteActions.STAUS_CANCEL, "");
				}
				markNewPoint();
			} else if(arg1 == Dialog.BUTTON_NEGATIVE) {
				newActivePoint = null;
			}
		}
		
	}
	
	class SetDriverStatus extends DialogFragment implements DialogInterface.OnClickListener {
		String[] items = new String[] {
				"Свободен",
				"На работе",
				"В рейсе",
				"Сломался",
		};

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
			b.setTitle("Статус");
			b.setSingleChoiceItems(items, -1, this);
			return b.create();
		}

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			DriverRouteActions.setStatus("", "", arg1 + DriverRouteActions.STAUS_DRIVER_FREE, "");
			updateStatusText(items[arg1]);
			sendStatus();
			dismiss();
		}		
	}
}

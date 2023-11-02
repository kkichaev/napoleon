package com.grsoft.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.HitchOnSelect;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ManagerOrgTask;
import com.grsoft.dataobjects.OrgTask;
import com.grsoft.dataobjects.impl.MOrgImpl;
import com.grsoft.napoleon.util.CalendarDlg;
import com.grsoft.network.ManagerOrgTaskExport;
import com.grsoft.network.ManagerOrgTaskRemove;
import com.grsoft.network.ObjectListener;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

@SuppressLint("SimpleDateFormat")
public class OrgTaskList extends Activity {
	
	protected static final int SELECT_FROM_DATE = 0;
	protected static final int SELECT_TILL_DATE = 1;
	static Date dateFrom;
	static Date dateTill;
	
	String orgId, userId;
	
	public static void open(Context context, String id, String userId) {
		Intent i = new Intent(context, OrgTaskList.class);
		i.putExtra(ExtrasConst.ORG_ID_STR, id);
		i.putExtra(ExtrasConst.USER_ID_STR, userId);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(getLayoutID());
		
		Bundle b = getIntent().getExtras();
		orgId = b.getString(ExtrasConst.ORG_ID_STR);
		userId = b.getString(ExtrasConst.USER_ID_STR);
		
		MOrgImpl org = new MOrgImpl();
		if(org.read("id", orgId))
				((TextView)findViewById(R.id.tvOrgName)).setText(org.getData().name);
		org.close();
		
		if( dateFrom == null) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MONTH, -1);
			dateFrom = c.getTime();
		}
		
		if( dateTill == null ) {
			dateTill = new Date();
		}
		
		refreshDate();
		
		findViewById(R.id.tvFrom).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(SELECT_FROM_DATE); }
		});
		findViewById(R.id.tvTill).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(SELECT_TILL_DATE); }
		});
		
		findViewById(R.id.btnRefresh).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { refreshTask(); }
		});
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				editItem((ManagerOrgTask) arg0.getItemAtPosition(arg2)); 
		}});
		lv.setDividerHeight(0);
		
		registerForContextMenu(lv);
		
		findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { editItem(null); }
		});
	}

	protected int getLayoutID() {
		return R.layout.org_task_list;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		loadData();
	}
	
	@SuppressLint("DefaultLocale")
	protected void refreshTask() {
		Calendar cfrom = Calendar.getInstance();
		cfrom.setTime(dateFrom);
		
		Calendar ctill = Calendar.getInstance();
		ctill.setTime(dateTill);
		ctill.add(Calendar.DAY_OF_MONTH, 1);

		
		String where = String.format("\"userid\" = '%s' and \"start\" > ToDate('%02d.%02d.%d') and \"start\" < ToDate('%02d.%02d.%d')", userId, 
				cfrom.get(Calendar.DAY_OF_MONTH), cfrom.get(Calendar.MONTH), cfrom.get(Calendar.YEAR),
				ctill.get(Calendar.DAY_OF_MONTH), ctill.get(Calendar.MONTH), ctill.get(Calendar.YEAR));
		
		
		ArrayList<Hitching> upd = new ArrayList<Hitching>();
		HitchOnSelect hs = new HitchOnSelect(OrgTask.class, "OrgTask");
		hs.setCondition(where);
		upd.add(hs);
		
		UpdateProcess up = new UpdateProcess(this, new UpdateCtrl() {
			@Override public void updateCtrl(boolean enabled) { 
				findViewById(R.id.btnRefresh).setEnabled(enabled);
			}
			
			@Override
			public void onFinish(boolean success) {
				if( success )
					loadData();
			}
		}, upd);
		
		List<ObjectListener> toSend = new ArrayList<ObjectListener>();
		toSend.add(new ManagerOrgTaskExport());
		up.setSending(toSend);
		
		up.execute((Void[])null);
	}
	
	void editItem(ManagerOrgTask item) {
		EditOrgTask.open(this, item, userId, orgId);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		menu.add(getString(R.string.edit));
		menu.add(getString(R.string.delete));
	} 
	
	ManagerOrgTask getItem(AdapterContextMenuInfo mi) {
		return (ManagerOrgTask) ((ListView)findViewById(R.id.lvItems)).getItemAtPosition(mi.position);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		String text = item.getTitle().toString();
		if( text == getString(R.string.edit)) {
			editItem(getItem((AdapterContextMenuInfo) item.getMenuInfo()));
		} else if( text == getString(R.string.delete)) {
			
			final ManagerOrgTask mi = getItem((AdapterContextMenuInfo) item.getMenuInfo());
			List<Hitching> upd = new ArrayList<Hitching>();
			upd.add(new ManagerOrgTaskRemove(mi));
			UpdateProcess up = new UpdateProcess(this, new UpdateCtrl() {
				
				@Override public void updateCtrl(boolean enabled) { }
				
				@Override
				public void onFinish(boolean success) {
					if( success ) {
						try {
							String sql = "delete from \"" + mi.getTableName() + "\" where id='" + mi.id +"'";
							DataBaseManager.getDataBase().execSQL(sql);
							loadData();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}					
				}
			}, upd);
			up.execute((Void[])null);
			
		}
		return super.onContextItemSelected(item);
	}
	
	protected void loadData() {
		
		final List<ManagerOrgTask> data = new ArrayList<ManagerOrgTask>();
		
		String where = String.format("\"userid\" = '%s' and \"start\" > %s and \"start\" < %s", userId, 
				Long.toString(Util.resetTime(dateFrom).getTime()),
				Long.toString(Util.resetTime(dateTill).getTime() + 3600 * 1000 * 24));
		
		DataTraveler.travel(ManagerOrgTask.class, new DataTraveler.Travel<ManagerOrgTask>() {

			@Override
			public boolean travel(DataTraveler<ManagerOrgTask> item) {
				data.add(item.data);
				item.data = new ManagerOrgTask();
				return true;
			}
		}, where);
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(new Adapter(data));
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		
		if( id == SELECT_FROM_DATE || id == SELECT_TILL_DATE ) {
			CalendarDlg.setCurrentDate(dialog, (id == SELECT_FROM_DATE) ? dateFrom : dateTill);
		}
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		if( id == SELECT_FROM_DATE || id == SELECT_TILL_DATE ) {
			return CalendarDlg.create(this, new CalendarDlg.Handler() {
				
				@Override
				public void selectedDate(Date d) {
					if(id == SELECT_FROM_DATE)
						dateFrom = d;
					else
						dateTill = d;
					
					refreshDate();
				}
			});
		}
		return super.onCreateDialog(id);
	}

	protected void refreshDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		
		String str = sdf.format(dateFrom);
		SpannableString content = new SpannableString(str);
		content.setSpan(new UnderlineSpan(), 0, str.length(), 0);

		((TextView)findViewById(R.id.tvFrom)).setText(content);
		
		str = sdf.format(dateTill);
		content = new SpannableString(str);
		content.setSpan(new UnderlineSpan(), 0, str.length(), 0);
		((TextView)findViewById(R.id.tvTill)).setText(content);
	}

	class Adapter extends BaseAdapter {
		
		List<ManagerOrgTask> items;
		
		public Adapter(List<ManagerOrgTask> items) { this.items = items; }

		@Override public int getCount() { return items.size(); }
		@Override public Object getItem(int arg0) { return arg0 < items.size() ? items.get(arg0) : null; }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if( view == null )
				view = View.inflate(OrgTaskList.this, R.layout.org_task_row, null);
			
			ManagerOrgTask data = (ManagerOrgTask) getItem(arg0);
			if( data != null ) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");
				TextView tv;
				tv = (TextView)view.findViewById(R.id.tvFrom);
				tv.setText(sdf.format(data.start));
				
				tv = (TextView)view.findViewById(R.id.tvTill);
				tv.setText(sdf.format(data.finish));
				
				tv = (TextView)view.findViewById(R.id.tvText);
				tv.setText(data.text);
			}
			
			view.setBackgroundResource(arg0 % 2 == 0 ? R.drawable.list_selector : R.drawable.even_row_selector);
			return view;
		}
		
	}
}

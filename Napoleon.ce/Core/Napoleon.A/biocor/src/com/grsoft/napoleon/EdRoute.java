package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.OrgFolderItemEx;
import com.grsoft.dataobjects.OrgFoldersEx;
import com.grsoft.dataobjects.impl.OrgFoldersImpl;
import com.grsoft.dataobjects.impl.OrgFoldersImplEx;
import com.grsoft.network.BaseFragmentActivity;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;


public class EdRoute extends BaseFragmentActivity {
	private TextView tvDate; 
	private View btnSend;
	private View btnAdd;
	private EdRouteAdapter adapter;
	private ListView list;
	private List<Org> orgs = new ArrayList<Org>();
	private OrgFoldersImpl orgFolders = new OrgFoldersImplEx();
	private final static String DATE = "date";
	
	public static void open(Context ctx, Date date) {
		Intent intent = new Intent(ctx, EdRoute.class);
		intent.putExtra(DATE, date.getTime());
		ctx.startActivity(intent);
	}
	
	@Override
	protected int getLayoutID() { return R.layout.edroute; }

	@Override
	protected void inflateView() {
		tvDate = (TextView) findViewById(R.id.tvDate);
		btnSend = findViewById(R.id.btnSend);
		btnAdd = findViewById(R.id.btnAdd);
		list = (ListView) findViewById(R.id.list);
	}

	@Override
	protected void init() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);
		Date date = c.getTime();
		
		Intent i = getIntent();
		if(i != null)
			date = new Date(i.getLongExtra(DATE, date.getTime()));
		
		date = Util.resetTime(date);
		readData(date);
		adapter = new EdRouteAdapter(this, orgFolders, editComment);
		
		DataTraveler.travel(Org.class, new DataTraveler.Travel<Org>() {
			@Override
			public boolean isDataNewInstance() { return true; }
			
			@Override
			public boolean travel(DataTraveler<Org> item) {
				orgs.add(item.data);
				return true;
			}}, null);
		
		registerForContextMenu(list);
	}
	
	View.OnClickListener editComment = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			EditComment dlg = new EditComment();
			dlg.setData(orgFolders, (OrgFolderItemEx)v.getTag());
			dlg.show(getSupportFragmentManager(), dlg.getClass().getCanonicalName());
		}
	};
	
	@Override
	protected void initView() {
		tvDate.setOnClickListener(dateClick());
		btnSend.setOnClickListener(sendClick());
		btnAdd.setOnClickListener(addClick());
		list.setDividerHeight(0);
		list.setAdapter(adapter);
		list.setOnItemClickListener(listItemClick());
		updateDateView();
	}

	private OnItemClickListener listItemClick() {
		return new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				DialogFragment dlg = new ReorderDlg();
				Bundle args = new Bundle();
				args.putInt(ReorderDlg.SIZE, parent.getCount());
				args.putInt(ReorderDlg.CURRENT, position);
				dlg.setArguments(args);
				dlg.show(getSupportFragmentManager(), dlg.getClass().getCanonicalName());
			}};
	}

	private OnClickListener addClick() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				SelectOrg dlg = new SelectOrg();
				dlg.setOrgList(orgs, collectIds());
				dlg.show(getSupportFragmentManager(), dlg.getClass().getCanonicalName());
			}
		};
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.edroute_ctx_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.itDelete:
			deleteItem(((AdapterContextMenuInfo)item.getMenuInfo()).position);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	private void deleteItem(int position) {
		orgFolders.getData().items.remove(position);
		orgFolders.write();
		adapter.notifyDataSetChanged();
	}

	private OnClickListener sendClick() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				new RouteSync(getContext(), btnSend).execute((Void[])null);
			}
		};
	}

	private OnClickListener dateClick() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment dlg = new CalendarFragment();
				Bundle args = new Bundle();
				args.putLong(ExtrasConst.DATE_TAG, ((OrgFoldersEx)orgFolders.getData()).date.getTime());
				dlg.setArguments(args);
				dlg.show(getSupportFragmentManager(), dlg.getClass().getCanonicalName());
			}
		};
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		registerReceiver(dateChangeRcv, new IntentFilter(CalendarFragment.DATE_CHANGE_ACTION));
		registerReceiver(addItemRcv, new IntentFilter(SelectOrg.SELECT_ORG_ACTION));
		registerReceiver(reorderRcv, new IntentFilter(ReorderDlg.REORDER_ACTION));
		registerReceiver(refreshRcv, new IntentFilter(EditComment.REFRESH_ACTION));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(isFinishing()){
			try{
				unregisterReceiver(dateChangeRcv);
				unregisterReceiver(addItemRcv);
				unregisterReceiver(reorderRcv);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	};
	
	BroadcastReceiver dateChangeRcv = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			long date = intent.getLongExtra(ExtrasConst.DATE_TAG, new Date().getTime());
			onDataChange(new Date(date));
		}
	};

	protected void onDataChange(Date data) {
		readData(data);
		updateDateView();
		adapter.notifyDataSetChanged();
	}

	protected void readData(Date data) {
		((OrgFoldersEx)orgFolders.getData()).date = data;
		orgFolders.getData().name = Util.simpleDateFormat.format(data);
		orgFolders.read();
		Collections.sort(orgFolders.getData().items, orgFolderCmp);
	}

	protected List<String> collectIds() {
		List<String> ids = new ArrayList<String>();

		for(OrgFolderItem i : orgFolders.getData().items)
			ids.add(i.name);

		return ids;
	}

	protected void updateDateView() {
		SpannableString ss = new SpannableString(Util.simpleDateFormat.format(((OrgFoldersEx)orgFolders.getData()).date));
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tvDate.setTextColor(Color.BLUE);
		tvDate.setText(ss);
	}

	private Comparator<OrgFolderItem> orgFolderCmp = new Comparator<OrgFolderItem>() {
		@Override public int compare(OrgFolderItem lhs, OrgFolderItem rhs) { return lhs.pos - rhs.pos; }};

	BroadcastReceiver refreshRcv = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {
			adapter.notifyDataSetChanged();
		}
	};
	
	BroadcastReceiver addItemRcv = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			List<OrgFolderItem> items = orgFolders.getData().items;
			ArrayList<String> ids = new ArrayList<String>();
			ids.addAll(Arrays.asList(intent.getStringArrayExtra(SelectOrg.SELECTED)));
			List<OrgFolderItem> lived = new ArrayList<OrgFolderItem>();
			
			int maxpos = 0;
			
			//Сохранить текущие items и запомним макс номер
			for(OrgFolderItem i : items)
				if(ids.contains(i.name)){
					lived.add(i);
					ids.remove(i.name);
					
					if(maxpos < i.pos)
						maxpos = i.pos;
				}
			
			items.clear();
			items.addAll(lived);
			
			//добавляем новые в конец с номером maxpos++
			for(String id : ids){
				OrgFolderItemEx i = new OrgFolderItemEx();
				i.name = id;
				i.pos = maxpos++;
				i.comment = "";
				items.add(i);
			}
			
			//исправляем порядковые номера, что бы шли от 0 до size()
			for(int i = 0; i < orgFolders.getData().items.size(); i++){
				OrgFolderItem item = orgFolders.getData().items.get(i);
				item.pos = i;
			}
			
			orgFolders.write();
			orgFolders.close();
			
			adapter.notifyDataSetChanged();
		}
	};
	
	BroadcastReceiver reorderRcv = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int cur = intent.getIntExtra(ReorderDlg.CURRENT, 0);
			int old = intent.getIntExtra(ReorderDlg.OLD, 0);
			
			int sz = adapter.getCount();
			if(cur != old && cur < sz && old < sz){
				((OrgFolderItem)adapter.getItem(cur)).pos = old;
				((OrgFolderItem)adapter.getItem(old)).pos = cur;
				Collections.sort(orgFolders.getData().items, orgFolderCmp);
				orgFolders.write();
				adapter.notifyDataSetChanged();
			}
		}
	};
}

package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.database.PlanRouteSndHitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.PlanRoute;
import com.grsoft.dataobjects.PlanRouteItem;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PlanRouteImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.network.DocExportListener;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.view.CalendarView;
import com.grsoft.util.view.CalendarView.OnCalendarActionListener;
import com.grsoft.view.BaseActivity;

public class PlanEdit extends BaseActivity implements OnCalendarActionListener {
	public static Class<? extends Activity> activity = PlanEdit.class;
	private ExpandableListView exList;
	private Date weekBegin;
	private Button btnCalendar;
	private static final int WEEK_SELECT_DLG = 1;
	private static final int ADD_ORG_DLG = 2;
	private static final int PLAN_ITEM_EDIT_DLG = 3;
	private static final String START_WEEK_DATE = "start_week_date";
	
	private Map<Long, PlanRouteImpl> plan = new HashMap<Long, PlanRouteImpl>();
	private long selectedDate;
	private PlanRouteImpl selectedPlanRouteImpl;
	private PlanRouteItem selectedPlanRouteItem;
	
	private SimpleDateFormat captionFmt = new SimpleDateFormat("dd.MM");
	private SimpleDateFormat shortName = new SimpleDateFormat("EE");
	private static final long GROUPID = 1;
	private static final long CHILDID = 2;
	private ImageButton btnSend;
	
	private OnClickListener addOrgClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			selectedDate = ((Date) v.getTag()).getTime();
			
			if(plan.containsKey(selectedDate)){
				PlanRouteImpl planRouteImpl = plan.get(selectedDate);
				
				if(!planRouteImpl.isExported()){
					orgListAdapter.notifyDataSetChanged();
					showDialog(ADD_ORG_DLG);
				}
			}
		}
	};;
	
	private BaseAdapter orgListAdapter = new BaseAdapter() {
		ArrayList<Long> data = new ArrayList<Long>();
		OrgImpl orgImpl = new OrgImpl();
		
		{
			data.addAll(DbReader
					.readIds(DataObjectInfo.getInstance()
							.getTableName(Org.class), null, "name"));
		}
		
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null)
				view = View.inflate(PlanEdit.this, R.layout.addorgplan_row, null);
			
			long rowid = (Long) getItem(position);
			
			if(orgImpl.read(rowid)){
				TextView tvOrgName = (TextView) view.findViewById(R.id.tvOrgName);
				Org org = orgImpl.getData();
				tvOrgName.setText(Html.fromHtml(org.name + "<br><i>" + org.address + "</i>"));
				
				TextView tvWeekDay = (TextView) view.findViewById(R.id.tvWeekDay);
				final int WEEK_CLNM_LEN = 60;
				tvWeekDay.setMinWidth(WEEK_CLNM_LEN);
				tvWeekDay.setMaxWidth(WEEK_CLNM_LEN);
				String planDay = getDaysPlan(orgImpl.getData().id);
				tvWeekDay.setText(planDay);
				
				if(planDay.length() > 0)
					view.setBackgroundColor(getResources().getColor(R.color.grey));
				else
					view.setBackgroundColor(getResources().getColor(R.color.white));
			}
			
			orgImpl.close();
			return view;
		}
		
		@Override
		public long getItemId(int position) { return 0; }
		
		@Override
		public Object getItem(int position) {
			return data.get(position);
		}
		
		@Override
		public int getCount() {
			return data.size();
		}
	};
	
	private OnItemClickListener orgListClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Long rowid = (Long) parent.getItemAtPosition(position);
			
			OrgImpl orgImpl = new OrgImpl();
			
			if(orgImpl.read(rowid)){
				if(plan.containsKey(selectedDate)){
					PlanRouteImpl planRouteImpl = plan.get(selectedDate);
					PlanRoute planRoute = planRouteImpl.getData();
					
					boolean contains = false;
					String orgid = orgImpl.getData().id;
					
					for(PlanRouteItem item : planRoute.items){
						if(item.id.equals(orgid)){
							contains = true;
							planRoute.items.remove(item);
							break;
						}
					}
					
					if(!contains){
						PlanRouteItem item = new PlanRouteItem();
						item.id = orgid;
						planRouteImpl.getData().items.add(item);
					}

					final OrgImpl orgLeft = new OrgImpl();
					final OrgImpl orgRight = new OrgImpl();
					
					if(planRoute.items.size() > 0)
						Collections.sort(planRoute.items, 
								new Comparator<PlanRouteItem>() {

									@Override
									public int compare(PlanRouteItem lhs,
											PlanRouteItem rhs) {
										orgLeft.getData().id = lhs.id;
										orgLeft.read();
										orgRight.getData().id = rhs.id;
										orgRight.read();
										return orgLeft.getData().name.compareTo(orgRight.getData().name);
									}
								});
					
					orgLeft.close();
					orgRight.close();
					
					if(planRoute.items.size() > 0){
						planRouteImpl.getData().plan = weekBegin;
						planRouteImpl.write();
					}else
						planRouteImpl.delete();
					
					planRouteImpl.close();
					((BaseAdapter)parent.getAdapter()).notifyDataSetChanged();
				}
			}
			
			orgImpl.close();
		}
	};
	
	private Adapter adapter; 
	
	public static void open(Context context, long date){
		Intent intent = new Intent(context, activity);
		intent.putExtra(START_WEEK_DATE, date);
		context.startActivity(intent);
	}
	
	protected String getDaysPlan(String id) {
		StringBuilder result = new StringBuilder();
		Calendar cal = Calendar.getInstance();
		cal.setTime(weekBegin);
		cal.add(Calendar.DAY_OF_MONTH, 6);
		Date d = new Date(cal.getTime().getTime());
		cal.setTime(weekBegin);
		
		
		while(cal.getTime().getTime() <= d.getTime()){
			long key = cal.getTime().getTime();
			if(plan.containsKey(key)){
				PlanRouteImpl planRouteImpl = plan.get(key);
				
				for(PlanRouteItem item : planRouteImpl.getData().items){
					if(item.id.equals(id)){
						if(result.length() > 0)
							result.append(", ");
						
						result.append(shortName.format(cal.getTime()));
					}
				}
			}
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		return result.toString();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.planedit);
		exList = (ExpandableListView) findViewById(R.id.list);
		btnCalendar = (Button) findViewById(R.id.btnCalendar);
		btnSend = (ImageButton) findViewById(R.id.btnSend);
		
		btnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new DocumentSender(v.getContext(), btnSend, new ArrayList<DocExportListener>()){
					protected boolean isDocListEmpty() {return false;}
					@Override
					protected Collection<ObjectListener> getObjectsToSend() {
						ArrayList<ObjectListener> result = new ArrayList<ObjectListener>();
						result.add(new ObjectExportListener(){
							ArrayList<Long> ids = new ArrayList<Long>();
							{
								PlanRouteImpl impl = new PlanRouteImpl();
								for(int i = 0; i < adapter.getGroupCount(); i++)
									if(adapter.getChildrenCount(i) > 0){
										impl.getData().created = adapter.getGroupDate(i);
										
										if(impl.read())
											ids.add(impl.getRowid());
									}
								
								impl.close();
							}
							
							@Override
							public void onStart() {}
							@Override
							public void onRead(RawObject rawObject)	throws RuntimeException {}
							@Override
							public void onSave() {}
							@Override
							public void onEnd() {
								PlanRouteImpl impl = new PlanRouteImpl();
								for (int i = 0; i < ids.size(); i++) {
									impl.read(ids.get(i));
									impl.getData().params |= ParamState.ofExported;
									impl.write();
								}
								impl.close();
							}
							@Override
							public String getObjectName() {	return PlanRouteSndHitching.OBJECT_NAME; }
	
							@Override
							public int size() {
								return ids.size();
							}
	
							@Override
							public DataObject get(int i) {
								PlanRouteImpl impl = new PlanRouteImpl();
								impl.read(ids.get(i));
								impl.close();
								return impl.getData();
							}});
							
						return result;
						
					}
				}.execute((Void[])null);
			}
		});
		
		adapter = new Adapter(); 
		exList.setAdapter(adapter);
		
		long date = getIntent().getLongExtra(START_WEEK_DATE, ExtrasConst.INVALID_ID);
		
		if(date != ExtrasConst.INVALID_ID){
			weekBegin = new Date(date);
			applyForNewWeek(weekBegin);
		}
		
		btnCalendar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(WEEK_SELECT_DLG);
			}
		});
		
		exList.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Date date  = (Date) adapter.getGroup(groupPosition);
				long key = date.getTime();
				
				if(plan.containsKey(key)){
					selectedPlanRouteImpl = plan.get(key);
					selectedPlanRouteItem = (PlanRouteItem) adapter.getChild(groupPosition, childPosition);
					showDialog(PLAN_ITEM_EDIT_DLG);
				}
				return true;
			}
		});

		registerForContextMenu(exList);
		
		for(int i = 0; i < adapter.getGroupCount(); i++){
			if (adapter.getChildrenCount(i) > 0)
				exList.expandGroup(i);
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		ExpandableListView.ExpandableListContextMenuInfo inf = 
				(ExpandableListView.ExpandableListContextMenuInfo)menuInfo;
		Date date  = (Date) adapter.getGroup(
				ExpandableListView.getPackedPositionGroup(inf.packedPosition));
		long key = date.getTime();
		boolean isExported = true;
		
		if(plan.containsKey(key))
			isExported = plan.get(key).isExported();
		
		if(inf.id == CHILDID && !isExported)
			getMenuInflater().inflate(R.menu.planedit_contextmenu, menu);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id){
		case PLAN_ITEM_EDIT_DLG: preparePlanItemEditDlg(dialog);
		default:super.onPrepareDialog(id, dialog);
		}
	}
	
	private void preparePlanItemEditDlg(Dialog dialog) {
		if (selectedPlanRouteItem != null){
			OrgImpl org = new OrgImpl();
			org.getData().id = selectedPlanRouteItem.id;
			
			if(org.read()){
				TextView tvOrgName = (TextView) dialog.findViewById(R.id.tvOrgName);
				tvOrgName.setText(org.getData().name);
				EditText edSpecTask = (EditText) dialog.findViewById(R.id.edSpecTask);
				edSpecTask.setText(selectedPlanRouteItem.spectask);
				CheckBox cbOrder = (CheckBox) dialog.findViewById(R.id.cbOrder);
				cbOrder.setChecked(selectedPlanRouteItem.order != 0);
				CheckBox cbIncass = (CheckBox) dialog.findViewById(R.id.cbIncass);
				cbIncass.setChecked(selectedPlanRouteItem.incass != 0);
				CheckBox cbReturn = (CheckBox) dialog.findViewById(R.id.cbReturn);
				cbReturn.setChecked(selectedPlanRouteItem.returns != 0);
				CheckBox cbVisit = (CheckBox) dialog.findViewById(R.id.cbVisit);
				cbVisit.setChecked(selectedPlanRouteItem.visit != 0);
			}
			
			org.close();
		}
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		adapter.close();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case WEEK_SELECT_DLG: return createWeekSelectDlg();
		case ADD_ORG_DLG: return createAddOrgDlg();
		case PLAN_ITEM_EDIT_DLG: return createPlanItemEditDlg();
		default: return super.onCreateDialog(id);
		}
	}
	
	private Dialog createPlanItemEditDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.planitem_edit, null);
		builder.setView(view);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(!selectedPlanRouteImpl.isExported()){
					AlertDialog dlg = ((AlertDialog)dialog);
					CheckBox cbOrder = (CheckBox) dlg.findViewById(R.id.cbOrder);
					selectedPlanRouteItem.order = cbOrder.isChecked() ? 1 : 0;
					CheckBox cbIncass = (CheckBox) dlg.findViewById(R.id.cbIncass);
					selectedPlanRouteItem.incass = cbIncass.isChecked() ? 1 : 0;
					CheckBox cbReturn = (CheckBox) dlg.findViewById(R.id.cbReturn);
					selectedPlanRouteItem.returns = cbReturn.isChecked() ? 1 : 0;
					CheckBox cbVisit = (CheckBox) dlg.findViewById(R.id.cbVisit);
					selectedPlanRouteItem.visit = cbVisit.isChecked() ? 1 : 0;
					EditText edSpecTask = (EditText) dlg.findViewById(R.id.edSpecTask);
					selectedPlanRouteItem.spectask = edSpecTask.getText().toString();
					selectedPlanRouteImpl.write();
					selectedPlanRouteImpl.close();
					adapter.notifyDataSetChanged();
				}
			}
		});
		
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}

	private Dialog createAddOrgDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.addorgplan, null);
		builder.setView(view);
		ListView list = (ListView) view.findViewById(R.id.list);
		list.setAdapter(orgListAdapter);
		list.setOnItemClickListener(orgListClick);
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				adapter.notifyDataSetChanged();
				final int NOT_FOUND = -1;
				int index = NOT_FOUND;
				
				for(int i = 0; i < adapter.getGroupCount(); i ++)
					if(((Date)adapter.getGroup(i)).getTime() == selectedDate){
						index = i;
						break;
					}
				
				if(index != NOT_FOUND){
					exList.setSelectedGroup(index);
					exList.expandGroup(index);
				}
			}
		});
		return builder.create();
	}

	private Dialog createWeekSelectDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		CalendarView view = new CalendarView(this);
		view.setCalendarActionListener(this);
		builder.setView(view);
		return builder.create();
	}
	
	class Adapter extends BaseExpandableListAdapter{
		final int GROUP_CNT = 7;
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
		OrgImpl org = new OrgImpl();
		
		@Override
		public int getGroupCount() {
			return GROUP_CNT;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			PlanRoute planRoute = getChildObject(groupPosition);
			return planRoute == null ? 0 : planRoute.items.size();
		}
		
		private PlanRoute getChildObject(int groupPosition){
			Date date = getGroupDate(groupPosition);
			long key = date.getTime();
			
			return plan.containsKey(key) ? plan.get(key).getData() : null;
		}

		@Override
		public Object getGroup(int groupPosition) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(weekBegin);
			cal.add(Calendar.DAY_OF_MONTH, groupPosition);
			return cal.getTime();
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			PlanRouteItem result = null;
			PlanRoute planRoute = getChildObject(groupPosition);
			
			if(planRoute != null)
				result = planRoute.items.get(childPosition);
			
			return result;
		}

		@Override
		public long getGroupId(int groupPosition) {	return GROUPID; }

		@Override
		public long getChildId(int groupPosition, int childPosition) { return CHILDID; }

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View view, ViewGroup parent) {
			view = View.inflate(PlanEdit.this, R.layout.planedit_group_row, null);
			TextView text = (TextView) view.findViewById(R.id.text);
			Date date = getGroupDate(groupPosition);
			text.setText(sdf.format(date));
			
			TextView tvAddOrg = (TextView) view.findViewById(R.id.tvAddOrg);
			tvAddOrg.setTag(date);
			tvAddOrg.setOnClickListener(addOrgClick);
			return view;
		}

		public Date getGroupDate(int groupPosition) {
			Calendar cl = Calendar.getInstance();
			cl.setTime(weekBegin);
			cl.add(Calendar.DAY_OF_MONTH, groupPosition);
			Date date = cl.getTime();
			return date;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View view, ViewGroup parent) {
			
			if(view == null)
				view = View.inflate(PlanEdit.this, R.layout.planedit_child_row, null);
			
			PlanRouteItem item = (PlanRouteItem) getChild(groupPosition, childPosition);
			
			if(item != null){
				org.getData().id = item.id;
				
				if(org.read()){
					Org o = org.getData();
					TextView tvOrgName = (TextView) view.findViewById(R.id.tvOrgName);
					tvOrgName.setText(o.name);
					TextView tvAddress = (TextView) view.findViewById(R.id.tvAddress);
					tvAddress.setText(o.address);
					
					view.findViewById(R.id.ivSpecTask).setVisibility(item.spectask.length() > 0 ? View.VISIBLE : View.INVISIBLE);
					view.findViewById(R.id.ivOrder).setVisibility(item.order != 0 ? View.VISIBLE : View.INVISIBLE );
					view.findViewById(R.id.ivIncass).setVisibility(item.incass != 0 ? View.VISIBLE : View.INVISIBLE );
					view.findViewById(R.id.ivReturn).setVisibility(item.returns != 0 ? View.VISIBLE : View.INVISIBLE );
					view.findViewById(R.id.ivVisit).setVisibility(item.visit != 0 ? View.VISIBLE : View.INVISIBLE);
				}
			}
			
			return view;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
		
		public void close(){ 
			org.close();
		}
	}

	@Override
	public void onDateChanged(Date oldDate, Date newDate) {
		dismissDialog(WEEK_SELECT_DLG);
		applyForNewWeek(newDate);
	}

	protected void applyForNewWeek(Date newDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(newDate);
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		weekBegin = cal.getTime();

		cal.add(Calendar.DATE, 6);
		Date d = new Date(cal.getTime().getTime());
		cal.setTime(weekBegin);
		
		plan.clear();
		
		while(cal.getTime().getTime() <= d.getTime()){
			PlanRouteImpl planRouteImpl = new PlanRouteImpl();
			planRouteImpl.getData().created = cal.getTime();
			planRouteImpl.read();
			planRouteImpl.close();
			plan.put(cal.getTime().getTime(), planRouteImpl);
			cal.add(Calendar.DATE, 1);
		}
		
		adapter.notifyDataSetChanged();
		updateCalendarCaption();
	}

	private void updateCalendarCaption() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(weekBegin);
		cal.add(Calendar.DAY_OF_WEEK, 6);
		btnCalendar.setText(
				String.format("%s - %s", 
				captionFmt.format(weekBegin), captionFmt.format(cal.getTime())));
	}

	@Override
	public void onOtherDateChanged(Date currentDate, Date otherDate) {}

	@Override
	public void onCalendarCancelled() {}

	
}


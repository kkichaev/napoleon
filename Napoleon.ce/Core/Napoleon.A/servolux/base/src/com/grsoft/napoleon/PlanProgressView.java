package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.grsoft.dataobjects.AgentPlanItem;
import com.grsoft.dataobjects.AgentPlanNew;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PlanGroups;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceTypes;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.view.BaseActivity;

public class PlanProgressView extends BaseActivity {
	protected static final int DIALOG_DATE_PICKER_ID = 0;
	protected static final int CHOOSE_ITEM_TYPE = 1;
	Adapter adapter;
	Date planDate = null;
	List<PriceTypes> priceTypes = new ArrayList<PriceTypes>();
	int selectedItem = -1;
	
	public static void open(Context context) {
		Intent i = new Intent(context, PlanProgressView.class);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		planDate = Util.getDate();
		
		setContentView(R.layout.plan_progress);
		ExpandableListView lv = (ExpandableListView)findViewById(R.id.lvItems);
		adapter = new Adapter();
		lv.setAdapter(adapter);
		lv.setDividerHeight(0);
		
		findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(PlanProgressView.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, planDate.getTime());
				startActivityForResult(i, DIALOG_DATE_PICKER_ID);
			}
		});
		
		findViewById(R.id.tvItemType).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(CHOOSE_ITEM_TYPE); }
		});
		
		refreshDate();
		refreshPriceType();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == CHOOSE_ITEM_TYPE ) {
			final List<String> types = new ArrayList<String>();
			priceTypes = new ArrayList<PriceTypes>();
			
			DataTraveler.travel(PriceTypes.class, new DataTraveler.Travel<PriceTypes>() {
				@Override
				public boolean travel(DataTraveler<PriceTypes> item) {
					priceTypes.add(item.data);
					types.add(item.data.name);
					item.data = new PriceTypes();
					return true;
				}
			}, null, "name");
			
			CharSequence[] items = new CharSequence[types.size()]; 
			items = types.toArray(items);
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setTitle("Выберите вид груза");
			ab.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					selectPriceType(which);
				}
			});
			
			return ab.create();
		}
		return super.onCreateDialog(id);
	}
	
	protected void selectPriceType(int which) {
		selectedItem = which;
		if( selectedItem >= 0) {
			refreshPriceType();
			refreshPlan();
		}
	}

	private void refreshPlan() {
		if(planDate != null && selectedItem >= 0 && selectedItem < priceTypes.size())
			adapter.refresh(planDate, priceTypes.get(selectedItem));
	}

	private void refreshPriceType() {
		String text = "Вид груза ";
		
		String itemText = "";
		if( selectedItem >= 0 && selectedItem < priceTypes.size())
			itemText = priceTypes.get(selectedItem).name;
		else
			itemText = "Выберите вид";
		text += "<font color='#ff'><u>" + itemText + "</u></font>";
		TextView tv = (TextView)findViewById(R.id.tvItemType);
		tv.setText(Html.fromHtml(text));
	}

	private void refreshDate() {
		TextView tv = (TextView)findViewById(R.id.tvDate);
		String text = "План на <font color='#ff'><u>" + Util.simpleDateFormat.format(planDate) + "</u></font>";
		tv.setText(Html.fromHtml(text));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null && requestCode == DIALOG_DATE_PICKER_ID ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			planDate = Util.getDayStart(new Date(ct));
			refreshDate();
			refreshPlan();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	class Adapter extends BaseExpandableListAdapter {
		
		
		List<FirmData> data = new ArrayList<FirmData>();
		
		HashMap<String, ProgressData> loadGroups(PriceTypes priceType) {
			final HashMap<String, PriceEx> prc = new HashMap<String, PriceEx>();
			
			DataTraveler.travel(PriceEx.class, new DataTraveler.Travel<PriceEx>() {

				@Override
				public boolean travel(DataTraveler<PriceEx> item) {
					prc.put(item.data.id, item.data);
					item.data = new PriceEx();
					return true;
				}
			}, "idType='" + priceType.id + "'");
			
			// priceId => group
			final HashMap<String, ProgressData> ret = new HashMap<String, ProgressData>();

			// groupId => GroupData
			final HashMap<String, ProgressData> groups = new HashMap<String, ProgressData>();
			
			DataTraveler.travel(PlanGroups.class, new DataTraveler.Travel<PlanGroups>() {

				@Override
				public boolean travel(DataTraveler<PlanGroups> item) {
					if(prc.containsKey(item.data.id)) { 
						ProgressData gd = groups.get(item.data.group);
						if(gd == null) {
							gd = new ProgressData();
							groups.put(item.data.group, gd);
						}
						gd.addPrice(prc.get(item.data.id), gd.inPack);
						ret.put(item.data.id, gd);
					}
					return true;
				}
			}, null);
			
			for(Entry<String, PriceEx> kv : prc.entrySet()) {
				if(!ret.containsKey(kv.getKey())) {
					ProgressData gd = new ProgressData();
					gd.addPrice(kv.getValue(), kv.getValue().qtyInPack);
					ret.put(kv.getKey(), gd);
				}
			}
			return ret;
		}
		
		HashMap<String, HashMap<String, ProgressData>> loadPlans(final Date date, PriceTypes priceType) {
			final HashMap<String, HashMap<String, ProgressData>> ret = new HashMap<String, HashMap<String,ProgressData>>();
			
			final HashMap<String, ProgressData> prcGroup = loadGroups(priceType);
			
			String where = "date >= " + Long.toString(Util.getMonthStart(date).getTime()) + 
					" and date <= " + Long.toString(Util.getMonthEnd(date).getTime());
			
			final HashSet<String> totalLoad = new HashSet<String>();
			
			DataTraveler.travel(AgentPlanNew.class, new DataTraveler.Travel<AgentPlanNew>() {

				@Override
				public boolean travel(DataTraveler<AgentPlanNew> item) {
					Date cd = Util.getDayStart(item.data.date);
					if(item.data.isMonthly != 0) {
						if( totalLoad.contains(item.data.firm) == false) {
							HashMap<String, ProgressData> ftd = ret.get(item.data.firm);
							if(ftd == null) {
								ftd = new HashMap<String, ProgressData>();
								ret.put(item.data.firm, ftd);
							}
							for(AgentPlanItem i : item.data.items) {
								ProgressData pd = prcGroup.get(i.id);
								if( pd != null ) {
									pd.monthPlan = (int)((long)i.qty * pd.inPack / Consts.QTY_SCALE);
									ftd.put(i.id, pd);
								}
							}
							totalLoad.add(item.data.firm);	
						}
					} else if(date.compareTo(cd) >= 0) {
						boolean curDate = date.compareTo(cd) == 0;
						HashMap<String, ProgressData> ftd = ret.get(item.data.firm);
						if(ftd == null) {
							ftd = new HashMap<String, ProgressData>();
							ret.put(item.data.firm, ftd);
						}
						for(AgentPlanItem i : item.data.items) {
							ProgressData pd = prcGroup.get(i.id);
							if(pd == null)
								continue;
							if(curDate) {
								pd.curDayPlan = (int)((long)i.qty * pd.inPack / Consts.QTY_SCALE);
							}
							pd.addDayPlan(cd, i.qty);
							ftd.put(i.id, pd);
						}
					}
					return true;
				}
			}, where, "date desc");
			
			return ret;
		}
		
		void loadDeliveries(Date date, HashMap<String, HashMap<String, ProgressData>> pdata) {
			String where = "date >= " + Long.toString(Util.getMonthStart(date).getTime()) + 
					" and date < " + Long.toString(Util.getDayStart(date).getTime());
			
			DocList dl = DeliveryDoc.instance().docList(null, null, where);
			for(Document<?> doc : dl) {
				DeliveryEx de = (DeliveryEx)doc.getData();
				HashMap<String, ProgressData> priceData = pdata.get(de.firm);
				if( priceData == null)
					continue;
				
				for(DeliveryItem di : de.items) {
					ProgressData progressData = priceData.get(di.id);
					if(progressData == null)
						continue;
					
					int qty = di.qty; //(int)((long)di.qty * Consts.QTY_SCALE / progressData.inPack);
					progressData.toDateFact += qty;
				}
			}
			dl.close();
		}
		void loadOrders(Date date, HashMap<String, HashMap<String, ProgressData>> pdata) {
			String where = "created >= " + Long.toString(Util.getDayStart(date).getTime()) + 
					" and created <= " + Long.toString(Util.getDayEnd(date).getTime());
			
			DocList dl = OrderDoc.instance().docList(null, null, where);
			for(Document<?> doc : dl) {
				OrderEx oe = (OrderEx)doc.getData();
				HashMap<String, ProgressData> priceData = pdata.get(oe.firmCode);
				if( priceData == null)
					continue;
				
				for(OrderItem di : oe.items) {
					ProgressData progressData = priceData.get(di.id);
					if(progressData == null)
						continue;
					
					int qty = di.qty; //(int)((long)di.qty * Consts.QTY_SCALE / progressData.inPack);
					progressData.toDateFact += qty;
					progressData.curDayFact += qty;
				}
			}
			dl.close();
		}
		
		public void refresh(Date date, PriceTypes priceType) {
			
			data.clear();
			date = Util.getDayStart(date);
			
			HashMap<String, HashMap<String, ProgressData>> pdata = loadPlans(date, priceType);
			
			loadDeliveries(date, pdata);
			loadOrders(date, pdata);
			
			ConfigImpl cfg = new ConfigImpl();
			ArrayList<KeyValue> firms = new ArrayList<KeyValue>();
			CreateOrder.loadConfigFirms(cfg, firms, null, null);
			
			for( KeyValue kv : firms ){	
				HashMap<String, ProgressData> prcData = pdata.get(kv.key.toString());
				if(prcData != null) {
					HashSet<String> loadedItems = new HashSet<String>();
					
					FirmData fd = new FirmData();
					fd.name = kv.value.toString();
					fd.id = kv.key.toString();
					
					for(Entry<String, ProgressData> pd : prcData.entrySet()) {
						boolean loaded = false;  
						ProgressData pdv = pd.getValue();
						for(PriceEx p : pdv.price) {
							if(loadedItems.contains(p.id))
								loaded = true;
							loadedItems.add(p.id);
						}
						if( !loaded )
							fd.data.add(pd.getValue());
					}
					Collections.sort(fd.data);
					data.add(fd);
				}
			}
			
			Collections.sort(data);
			notifyDataSetChanged();
		}

		@Override
		public Object getChild(int arg0, int arg1) {
			FirmData fd = (FirmData)getGroup(arg0);
			return fd.data.get(arg1);
		}

		@Override
		public int getChildrenCount(int arg0) {
			FirmData fd = (FirmData)getGroup(arg0);
			return fd.data.size();
		}

		@Override public long getChildId(int arg0, int arg1) { return arg0 * 10000 + arg1; }
		@Override public boolean isChildSelectable(int arg0, int arg1) { return false; }
		@Override public Object getGroup(int arg0) { return data.get(arg0); }
		@Override public int getGroupCount() { return data.size(); }
		@Override public long getGroupId(int arg0) { return arg0; }
		@Override public boolean hasStableIds() { return true; }

		@Override
		public View getChildView(int arg0, int arg1, boolean arg2, View view, ViewGroup arg4) {
			if( view == null )
				view = View.inflate(PlanProgressView.this, R.layout.plan_progress_row, null);
			
			ProgressData pd = (ProgressData) getChild(arg0, arg1);
			
			setItems(pd, view, pd.getName());			

			view.setBackgroundResource(arg1 % 2 == 0 ? R.drawable.even_row_selector : R.drawable.list_selector);
			return view;
		}

		@Override
		public View getGroupView(int arg0, boolean arg1, View view, ViewGroup arg3) {
			if( view == null )
				view = View.inflate(PlanProgressView.this, R.layout.plan_progress_firm_row, null);
			
			FirmData fd = (FirmData)getGroup(arg0);
			ProgressData pd = fd.countTotal();

			setItems(pd, view, fd.name);			
			
			return view;
		}
		
		void setItems(ProgressData pd, View view, String name) {
			String text;
			TextView tv;
			
			tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(Html.fromHtml(name));
			
			tv = (TextView)view.findViewById(R.id.tvPlan);
			text = Util.IntToScaleStr((pd.curDayPlan + Consts.QTY_SCALE / 2) / Consts.QTY_SCALE, 0);
			tv.setText(Html.fromHtml(text));
			
			tv = (TextView)view.findViewById(R.id.tvFact);
			tv.setText(Util.IntToScaleStr((pd.curDayFact + Consts.QTY_SCALE / 2) / Consts.QTY_SCALE, 0));

			tv = (TextView)view.findViewById(R.id.tvFactToDate);
			tv.setText(Util.IntToScaleStr((pd.toDateFact + Consts.QTY_SCALE / 2) / Consts.QTY_SCALE, 0));

			tv = (TextView)view.findViewById(R.id.tvPlanToDate);
			tv.setText(Util.IntToScaleStr((pd.toDatePlan + Consts.QTY_SCALE / 2) / Consts.QTY_SCALE, 0));

			tv = (TextView)view.findViewById(R.id.tvPlanMonth);
			tv.setText(Util.IntToScaleStr((pd.monthPlan + Consts.QTY_SCALE / 2) / Consts.QTY_SCALE, 0));
			
			tv = (TextView)view.findViewById(R.id.tvDiff);
			tv.setText(Util.IntToScaleStr(pd.dailyProgress(), 10, Util.DEC_DELIM, false));			

			tv = (TextView)view.findViewById(R.id.tvDiffMonth);
			tv.setText(Util.IntToScaleStr(pd.monthProgress(), 10, Util.DEC_DELIM, false));			

			tv = (TextView)view.findViewById(R.id.tvDiffToDate);
			tv.setText(Util.IntToScaleStr(pd.currentProgress(), 10, Util.DEC_DELIM, false));			
		}
	}
}

class FirmData implements Comparable<FirmData> {
	public String id = "";
	public String name = "";
	public List<ProgressData> data = new ArrayList<ProgressData>();
	
	@Override public int compareTo(FirmData another) { return name.compareTo(another.name); }

	public ProgressData countTotal() {
		ProgressData ret = new ProgressData();
		for(ProgressData pd : data)
			ret.add(pd);
		return ret;
	}	
}

class ProgressData implements Comparable<ProgressData> {
	String name = null;
	public long curDayPlan;
	public long monthPlan;
	public long toDatePlan;

	/***
	 * идут уже в упаковках
	 */
	public long curDayFact;

	/***
	 * идут уже в упаковках
	 */
	public long toDateFact;
	
//	public int diffOnDay;
//	public int diffMonth;
//	public int diffToDate;
	
	public List<PriceEx> price = new ArrayList<PriceEx>();
	public int inPack;
	
	public boolean blueBack = false;
	
	HashSet<Date> dayPlanAdded = new HashSet<Date>();
	
	ProgressData() {}
	
	public int dailyProgress() {
		int prc = 0;
		if(curDayPlan != 0)
			prc = (int)((curDayFact * 1000)/ curDayPlan);
		
		return prc;
	}

	public int monthProgress() {
		int prc = 0;
		if(monthPlan != 0)
			prc = (int)((toDateFact * 1000)/ monthPlan);
		
		return prc;
	}

	public int currentProgress() {
		int prc = 0;
		if(toDatePlan != 0)
			prc = (int)((toDateFact * 1000)/ toDatePlan);
		
		return prc;
	}

	public void add(ProgressData pd) {
		curDayPlan += pd.curDayPlan;
		curDayFact += pd.curDayFact;

		toDateFact += pd.toDateFact;
		toDatePlan += pd.toDatePlan;

		monthPlan += pd.monthPlan;
	}
	
	public String getName() {
		if(name == null) {
			for(PriceEx p : price) {
				if( name == null )
					name = p.name + "<br>"  + p.thermalState + "/" + p.packName;
				else
					name += "<br>" + p.thermalState + "/" + p.packName;
			}
		}
		
		if(name == null)
			name = "";
		
		return name;
	}
	
	public void addPrice(PriceEx priceEx, int priceInPack) {
		if(priceInPack != 0)
			this.inPack = priceInPack;
		else
			this.inPack = priceEx.qtyInPack;
		price.add(priceEx);
		if( priceEx.thermalState.contains("Охл"))
			blueBack = true;
	}
	
	public void addDayPlan(Date d, int qty) {
		if((dayPlanAdded.contains(d)) == false) {
			toDatePlan += (int)((long)qty * inPack / Consts.QTY_SCALE);
			dayPlanAdded.add(d);
		}
	}
	
	@Override
	public int compareTo(ProgressData another) {
		return getName().compareTo(another.getName());
	}
}
package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.OrgFolderItemEx;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.OrgFoldersCmp;
import com.grsoft.napoleon.util.WeekDay;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class NapoleonEx extends Napoleon {
	private Button btnRoute;
	private Button btnOrg;
	private final static int SELECTDAYDLG = R.id.selectdaydlg;
	private static List<OrgFolders> route = new ArrayList<OrgFolders>();
	private static final int UNSELDAY = -1;
	private static int selday = UNSELDAY;
	public static boolean notifyDataSet = false;
	private Adapter adapter;
	public Map<String, Integer> debtMap = new HashMap<String, Integer>();
	public TextView tvTotalSum;
	
	@Override
	protected int getResourceID() {
		return R.layout.mainex;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tvTotalSum = (TextView) findViewById(R.id.tvTotalSum);
		tvTotalSum.setText("");
		
		btnRoute = (Button) findViewById(R.id.btnRoute);
		btnOrg = (Button) findViewById(R.id.btnOrg);

		btnRoute.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(SELECTDAYDLG);
			}
		});

		btnOrg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectDay(UNSELDAY);
			}
		});

		loadRoute();
		adapter = new Adapter(this);
		lvMainOrgs.setAdapter(adapter);
		selectToday();
		
		lvMainOrgs.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Org org = (Org) parent.getItemAtPosition(position);
				VisitInfo vi = null;
				if(selday != UNSELDAY){
					OrgFolders of  = route.get(selday);
					OrgFolderItemEx i = (OrgFolderItemEx) of.items.get(position);
					vi = new VisitInfo(of.name, i.time, i.everyday == 1);
				}
					
				DocumentsEx.open(view.getContext(), org.id, vi);
			}
		});
		
		ConfigImpl cfg = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		if(cfg.getValue(sb, "јктивныйјссортимент")){
			try{
				AssortmentMatrixAdapter.PERIOD_IN_DAY = Integer.parseInt(sb.toString());
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (notifyDataSet) {
			notifyDataSet = false;
			NapoleonEx.loadRoute();
			adapter.init();
		} else
			adapter.refrehs();
		
//		adapter.notifyDataSetChanged();
		DocTypeBase.setCurDoc(OrderDoc.instance(), true);
	}

	private void selectToday() {
		WeekDay today = WeekDay.today();
		for (int i = 0; i < route.size(); i++){
			WeekDay wd = WeekDay.getWeekDay(route.get(i).name);
			if (wd != null && wd.equals(today))
				selectDay(i);
		}
	}

	public static void loadRoute() {
		route.clear();
		List<OrgFolders> everyday = new ArrayList<OrgFolders>();
		
		DbReader reader = new DbReader();
		OrgFolders data = new OrgFolders();

		boolean bdo = reader.select(data, DataObjectInfo.getInstance()
				.getTableName(data.getClass()), null);

		while (bdo) {
			OrgFolders of = (OrgFolders) data.clone();
			
			if(of.name.trim().length() == 0)
				everyday.add(of);
			else	
				route.add(of);
			
			bdo = reader.selectNext(data);
		}
		
		reader.close();
		
		for(OrgFolders of : route){
			for(OrgFolders ev : everyday)
				for(OrgFolderItem i : ev.items){
					OrgFolderItemEx item = (OrgFolderItemEx) i.clone();
					((OrgFolderItemEx)item).everyday = 1;
					of.items.add(item);
				}
			
			Collections.sort(of.items, new OrgFolderItemCmp());
		}
		
		reader.close();

		Collections.sort(route, new OrgFoldersCmp());
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {

	}

	@Override
	protected void setListMode(ListViewMode mode) {

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == SELECTDAYDLG)
			return createSelectDayDlg();
		else
			return super.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == SELECTDAYDLG)
			prepareDayDlg(dialog);
		else
			super.onPrepareDialog(id, dialog);
	}

	private void prepareDayDlg(Dialog dialog) {
		((BaseAdapter) ((AlertDialog) dialog).getListView().getAdapter())
				.notifyDataSetChanged();
	}

	private View inflateView(int position, View view) {
		return View.inflate(this, R.layout.orgfolderrow, null);
	}

	private Dialog createSelectDayDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.id.selectdaydlg);
		builder.setAdapter(new BaseAdapter() {
			@Override
			public View getView(int position, View view, ViewGroup parent) {
				if (view == null)
					view = inflateView(position, view);

				OrgFolders of = (OrgFolders) getItem(position);
				TextView tv = (TextView) view.findViewById(R.id.textView);
				tv.setText(of.name);

				return view;
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public Object getItem(int position) {
				return route.get(position);
			}

			@Override
			public int getCount() {
				return route.size();
			}
		}, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				selectDay(which);
			}
		});

		return builder.create();
	}

	private void selectDay(int which) {
		setFirstColumnCaption(which == UNSELDAY ? getString(R.string.orglbl)
						: getString(R.string.dayofweek, route.get(which).name));

		selday = which;
		adapter.changeRoute(which);
		adapter.notifyDataSetChanged();
		
		int sum = adapter.getTotalSum();
		String text = sum > 0 ? Util.IntToScaleStr(sum, Consts.SUM_SCALE) : "";  
		tvTotalSum.setText(text);
		
		llFind.setVisibility(View.GONE);
	}

	class AdapterData {
		public Map<String, OrgEx> orgs = new HashMap<String, OrgEx>();
		public List<OrgEx> list = new ArrayList<OrgEx>();
		public List<OrgEx> curroute = new ArrayList<OrgEx>();

		public AdapterData() {
			DbWriter.checkDBTable(Org.class);
			refresh();
		}

		public void refresh() {
			list.clear();
			orgs.clear();
			
			DbReader reader = new DbReader();
			OrgEx data = new OrgEx();
			boolean bdo = reader.select(data, DataObjectInfo.getInstance().getTableName(data.getClass()), "hidden = 0");
			while (bdo) {
				if (!orgs.containsKey(data.id))
					orgs.put(data.id, (OrgEx) data.clone());
				bdo = reader.selectNext(data);
			}

			reader.close();
			list.addAll(orgs.values());
			Collections.sort(list, new Comparator<OrgEx>() {

				@Override
				public int compare(OrgEx lhs, OrgEx rhs) {
					return lhs.name.compareTo(rhs.name);
				}
			});
		}

		public int getCount() {
			int result = 0;
			
			if (selday == UNSELDAY)
				result = list.size(); 
			else if (selday >= 0 && selday < route.size() && route.get(selday).items != null)
				result = curroute.size();
			
			return  result;
		}
		
		public Object getItem(int position) {
			return selday == UNSELDAY ? list.get(position) : curroute.get(position);
		}

		public CharSequence getTime(int position) {
			String result = "";
			if (selday != UNSELDAY)
				result = ((OrgFolderItemEx) route.get(selday).items
						.get(position)).time;
			return result;
		}

		public void changeRoute(int which) {
			curroute.clear();
			
			if(which != UNSELDAY)
				for(OrgFolderItem i : route.get(selday).items)
					if(orgs.containsKey(i.name))
						curroute.add(orgs.get(i.name));
		}
	}

	class Adapter extends BaseAdapter implements FilterAdapter {
		private Context context;
		
		private AdapterData data;
		private AdapterData filter;
		
		String curFilter = "";

		public Adapter(Context context) {
			this.context = context;
			this.data = new AdapterData();
			this.filter = new AdapterData();
		}

		public void changeRoute(int which) {
			resetFilter();
			data.changeRoute(which);
			filter.changeRoute(which);
		}

		public void refrehs() {
			data.refresh();
			filter.refresh();
			applyFilter(curFilter);
		}
		
		public void init() {
			data.refresh();
			filter.refresh();
			resetFilter();
		}

		public int getTotalSum() {
			int result = 0;
			for(int i = 0; i < getCount(); i++){
				Org o = (Org) getItem(i);
				result += getOrgDebt(o.id);
			}
			
			return result;
		}

		@Override
		public int getCount() {
			return filter.getCount();
		}

		@Override
		public Object getItem(int position) {
			return filter.getItem(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		private void updateTextColor(boolean applay, TextView tv){
			if(applay)
				tv.setTextColor(Color.GREEN);
			else
				tv.setTextColor(Color.BLACK);
		}
		
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null)
				view = View.inflate(context, R.layout.orgrow, null);

			OrgEx org = (OrgEx) getItem(position);

			if (org == null)
				org = new OrgEx();

			TextView tv = (TextView) view.findViewById(R.id.tvName);
			tv.setText(org.name);
			
			boolean hdt = DocType.getCurDoc().isHasCreatedToday(org.id);
			updateTextColor(hdt, tv);
			
			
			tv = (TextView) view.findViewById(R.id.tvAddress);
			tv.setText(org.address);
			updateTextColor(hdt, tv);
			
			tv = (TextView) view.findViewById(R.id.tvTime);
			tv.setText(getTime(position));
			tv = (TextView) view.findViewById(R.id.tvSum);
			tv.setText(getSumText(org.id));
				
			view.setBackgroundResource(position % 2 != 0 ? R.drawable.even_row_selector
					: R.drawable.list_selector);
			
			
				
			return view;
		}

		private CharSequence getTime(int position) {
			return data.getTime(position);
		}

		@Override
		public void applyFilter(String value) {
			curFilter = value;
			filter.list.clear();
			filter.curroute.clear();
			
			for(OrgEx o : data.list)
				if(value.trim().length() == 0 ||  o.name.toUpperCase().contains(value.toUpperCase()))
					filter.list.add(o);
			
			for(OrgEx o : data.curroute)
				if(value.trim().length() == 0 || o.name.toUpperCase().contains(value.toUpperCase()))
					filter.curroute.add(o);
			
			notifyDataSetChanged();
		}

		@Override
		public void resetFilter() {
			applyFilter("");
		}
	}

	public CharSequence getSumText(String id) {
		int sum = getOrgDebt(id);
		
		String result = "";

		if(sum > 0)
			result = getString(R.string.debt_sum_fmt, Util.IntToScaleStr(sum, Consts.SUM_SCALE));
		
		return result;
	}

	private int getOrgDebt(String id) {
		int sum = 0;
		
		if(debtMap.containsKey(id))
			sum = debtMap.get(id);
		else{
			DbReader reader = new DbReader();
			DeliveryEx data = new DeliveryEx();
			StringBuilder where = new StringBuilder();
			where.append("id='").append(id).append("'");
			boolean bdo = reader.select(data, "delivery", where.toString());
			Date now = Util.getDate();
			
			while(bdo){
				if(data.sumD > 0 && data.payDate != null && data.payDate.compareTo(now) < 0)
					sum += data.sumD;
					
				bdo = reader.selectNext(data);
			}
			
			debtMap.put(id, sum);

		}
		
		return sum;
	}
}

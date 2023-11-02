package com.grsoft.napoleon;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.AgentPrefixEx;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ItemsAudit;
import com.grsoft.dataobjects.ItemsAuditItem;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Rfrg;
import com.grsoft.dataobjects.RfrgAudit;
import com.grsoft.dataobjects.RfrgAuditItem;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.ItemsAuditDoc;
import com.grsoft.napoleon.documents.PkoDoc;
import com.grsoft.napoleon.documents.RfrgAuditDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.napoleon.modules.print.BaseDataSource;
import com.grsoft.napoleon.modules.print.DataSource;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.util.FindOnClickListener;
import com.grsoft.napoleon.utl.WiFiPrint;
import com.grsoft.napoleon.utl.WiFiPrinterConfig;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class NapoleonEx extends Napoleon {

	protected static final int WAIT_PRINT_DIALOG = 0x44;
	
	@Override protected int getResourceID() { return R.layout.main_ex; }

	public static void moveTo(Context context) {
		Intent i = new Intent(context, NapoleonEx.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(i);
	}
	
	@Override
	protected ArrayList<MenuHandler> createDocMenuList() {
		ArrayList<MenuHandler> ret = super.createDocMenuList(); 
		ret.add(new MenuHandler(getString(R.string.wsorder_title), new Runnable() {
			@Override public void run() { WSOrderList.open(NapoleonEx.this); }
		}));
		return ret;
	}
	
	@Override
	protected void setFirstColumnCaption(String caption) {
		super.setFirstColumnCaption(caption);
		refreshTitle();
	}
	
	void refreshTitle() {
		DocType dt = DocType.getCurDoc();
		String docTitle = dt.getName();
		int res = dt.getDocTitle();
		if( res != -1 )
			docTitle = getString(res);
		((TextView)findViewById(R.id.tvDocCaption)).setText(docTitle);
		
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);
		refreshTitle();
	}
	
	@Override
	protected void onResume() {
		if(DocType.getCurDoc() == WSOrderDoc.instance())
			DocType.setCurDoc(SalesDoc.instance());
		super.onResume();
	}
	
	@Override
	protected FindOnClickListener createFindOnClickListener() {
		return new FindClickEx(edFind, lvMainOrgs, llFind);
	}
	
	@Override
	protected DocFilterOnClickListener createDocFilter() {
		return new DocFilterOnClickListener(this){
			@Override
			protected void initData(boolean creatableFilter) {
				super.initData(creatableFilter);
				data.remove(WSOrderDoc.instance());
			}
		};
	}
	
	@Override
	protected ArrayList<MenuHandler> createMainMenuList() {
		ArrayList<MenuHandler> menu = super.createMainMenuList();
		menu.add(0, new MenuHandler(getString(R.string.daily_report), new Runnable() {
			@Override public void run() { printReport(); }
		}));
		
		return menu;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == WAIT_PRINT_DIALOG ) {
			return SelectPrinFormDlg.createWaitDlg(this);
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if( id == WAIT_PRINT_DIALOG ) {
			ProgressBar p = (ProgressBar) dialog.findViewById(android.R.id.progress);
			if( p != null ) {
		        p.setVisibility(View.GONE);
		        p.setVisibility(View.VISIBLE);
			}
	        return;
		}
		super.onPrepareDialog(id, dialog);
	}
	
	protected void printReport() {
		new AsyncTask<Void, Void, File>(){
			protected void onPreExecute() { showDialog(WAIT_PRINT_DIALOG); };
			
			@Override
			protected File doInBackground(Void... params) {
				File result = null;
				DataSource dataSource = makeDataSource();
				result = NPrinter.print(NapoleonEx.this, "daily_report", dataSource);

				if( result != null ) {
					NapoleonEx.this.runOnUiThread(new Runnable() {
						@Override public void run() { Toast.makeText(NapoleonEx.this, "Документ отправлен на печать", Toast.LENGTH_SHORT).show(); }
					});
					WiFiPrinterConfig cfg = WiFiPrinterConfig.get(NapoleonEx.this);
					WiFiPrint.print(cfg, NapoleonEx.this, result.getAbsolutePath());
				}
				
				return result;
			}
			
			protected void onPostExecute(File output) {
				try {
					dismissDialog(WAIT_PRINT_DIALOG);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.execute();
	}

	protected DataSource makeDataSource() {
		return new BaseDataSource(new DailyReportData(((OrgFoldersAdapter)orgFoldersAdapter).getTodayItems()));
	}

	class FindClickEx extends FindOnClickListener {

		public FindClickEx(EditText findField, ListView listView, View groupView) {
			super(findField, listView, groupView);
		}
		
		@Override
		public void beginFiltering() {
			super.beginFiltering();
			
			findField.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(findField, InputMethodManager.SHOW_IMPLICIT);
		}
	}
}

class RepGroup {
	HashMap<String, Integer> items = new HashMap<String, Integer>();
	public String name;
	
	public RepGroup(String name) { this.name = name; }
	
	public Integer getPackQty(OrderItem item) { return items.get(item.id); }
	public boolean contains(OrderItem item) { return items.containsKey(item.id); }
	
	public static RepGroup[] load() {
		final RepGroup[] ret = new RepGroup[4];
		
		ArrayList<CharSequence> groups = new ArrayList<CharSequence>();
		ConfigImpl ci = new ConfigImpl();
		Config cfg = ci.getData();
		cfg.key = "ТоварныеГруппы";
		ci.read();
		ci.close();
		
		DialogHelper.makeList(cfg.value, groups);
		
		for(int i=0; i<4; i++ )
			ret[i] = new RepGroup(i < groups.size() ? groups.get(i).toString() : String.format("Группа %d", i+1) );

		DataTraveler.travel(PriceEx.class, new DataTraveler.Travel<PriceEx>() {
			@Override
			public boolean travel(DataTraveler<PriceEx> item) {
				PriceEx data = item.data;
				if( data.reportGroup > 0 && data.reportGroup <= ret.length )
					ret[data.reportGroup-1].items.put(data.id, data.qtyInPack == 0 ? Consts.QTY_SCALE : data.qtyInPack);
				return true;
			}
		}, null);
		
		return ret;
	}
}

class GroupData {
	/**
	 *  QTY_SCALE - расчет в упаковках, но масштабирование есть
	 */
	int qty;
	// SUM_SCALE
	int sum;
}

class OrgDailyData {
	int exclusive;
	HashSet<String> lines = new HashSet<String>();
	int sum; // сумма заявок
	int cash;

	GroupData[] groups;
	
	public OrgDailyData(RepGroup[] repGroups) {
		groups = new GroupData[repGroups.length];
		for( int i=0; i<groups.length ; i++ )
			groups[i] = new GroupData();
	}
}

class DailyReportData {
	public Date date;
	public String route;
	public String agent;
	
	public String cold = "-";
	public int exclusive;
	public String lines;
	
	@Scale(value=Consts.SUM_SCALE, hideRest=false)
	public int group1Sum;
	@Scale(value=Consts.SUM_SCALE, hideRest=false)
	public int group2Sum;
	@Scale(value=Consts.SUM_SCALE, hideRest=false)
	public int group3Sum;
	@Scale(value=Consts.SUM_SCALE, hideRest=false)
	public int group4Sum;
	
	public int group1Qty;
	public int group2Qty;
	public int group3Qty;
	public int group4Qty;
	
	public String group1 = "";
	public String group2 = "";
	public String group3 = "";
	public String group4 = "";
	
	@Scale(value=Consts.SUM_SCALE, hideRest=false)
	public int credit;
	@Scale(value=Consts.SUM_SCALE, hideRest=false)
	public int cash;
	@Scale(value=Consts.SUM_SCALE, hideRest=false)
	public int sum;

	public List<Item> items = new ArrayList<Item>();
	
	public DailyReportData(List<OrgFolderItem> orgs) {
		date = Util.getDate();
		AgentPrefixEx ap = (AgentPrefixEx) AgentPrefix.get();
		route = ap.route;
		agent = ap.name;
		
		Date now = Util.getDate();
		Date end = new Date(now.getTime() + 24l * 3600000 - 1);
		DatePeriod dp = new DatePeriod(now, end);
		dp.periodType = DatePeriod.CREATED;

		RepGroup[] repGroups = RepGroup.load();		
		HashMap<String, OrgDailyData> orgData = loadOrders(dp, repGroups);
		loadIncass(dp, orgData, repGroups);
		loadVisits(dp, orgData, repGroups);
		
		if( repGroups.length > 0 )
			group1 = repGroups[0].name;
		if( repGroups.length > 1 )
			group2 = repGroups[1].name;
		if( repGroups.length > 2 )
			group3 = repGroups[2].name;
		if( repGroups.length > 3 )
			group4 = repGroups[3].name;
	
		OrgImpl oi = new OrgImpl(); 
		OrgEx org = (OrgEx)oi.getData();
		
		HashSet<String> used = new HashSet<String>();
		int order = 1;
		for(OrgFolderItem item : orgs) {
			org.id = item.name;
			oi.read();
			
			used.add(org.id);
			addItem(repGroups, orgData.get(org.id), org, order++);
		}
		
		HashSet<String> allLines = new HashSet<String>();
		for(Entry<String, OrgDailyData> kv : orgData.entrySet()) {
			OrgDailyData odd = kv.getValue();
			allLines.addAll(odd.lines);
			if( used.contains(kv.getKey()) )
				continue;
			
			org.id = kv.getKey();
			oi.read();
			addItem(repGroups, odd, org, order++);
		}
		oi.close();

		loadItemsAudits(dp, allLines.size(), repGroups, order);
		
		loadRfrgAudits(dp, repGroups, order);
	}

	private void loadItemsAudits(DatePeriod dp, int docItems, RepGroup[] repGroups, int order) {
		HashMap<String, Integer> auditData = new HashMap<String, Integer>();
		HashSet<String> items = new HashSet<String>();
		HashSet<String> allAudits = new HashSet<String>();
		
		String curId = "";
		DocList dl = ItemsAuditDoc.instance().docList(null, "id", dp);
		for(Document<?> doc : dl) {
			String id = doc.getId();
			if( curId.equals(id) == false ) {
				items.clear();
				curId = id;
			}
			
			ItemsAudit ia = (ItemsAudit) doc.getData();
			for(ItemsAuditItem ii : ia.items) {
				items.add(ii.id);
				allAudits.add(ii.id);
			}
			auditData.put(curId, items.size());
		}
		
		for(Entry<String, Integer> kv : auditData.entrySet()) {
			Item curItem = findOrCreateItem(kv.getKey(), repGroups, order);
			if( curItem.order >= order )
				order = curItem.order + 1;
			curItem.lines.replace("/-", String.format("/%d", (int)kv.getValue()));
		}
		
		dl.close();
		
		this.lines = String.format("%d/%d", docItems, allAudits.size()); 
	}

	private void loadRfrgAudits(DatePeriod dp, RepGroup[] repGroups, int order) {
		HashMap<String, Integer> docRfrg = new HashMap<String, Integer>();
		
		try {
			String stmt = "select count(id), ido as id from " + DataObjectInfo.getInstance().getTableName(Rfrg.class) + " group by ido"; 
			Cursor c = DataBaseManager.getDataBase().rawQuery(stmt, null);
			while(c.moveToNext()) {
				docRfrg.put(c.getString(1), c.getInt(0));
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int kisRfrg = 0, factRfrg = 0;

		int exclusiveTotal = 0;
		int orgCount = 0;
		Item curItem = null;
				
		DocList dl = RfrgAuditDoc.instance().docList(null, "id, created desc", dp);
		for(Document<?> doc : dl) {
			String id = doc.getId();
			RfrgAudit ra =(RfrgAudit)doc.getData();

			if( curItem == null || curItem.id.equals(id) == false ) {				
				if( curItem != null )
					factRfrg += curItem.factRfrg.size();
				
				curItem = findOrCreateItem(id, repGroups, order);
				if( curItem.order >= order )
					order = curItem.order + 1;
				
				Integer docRfrgCount = docRfrg.get(id);
				if(docRfrgCount != null) {
					curItem.docRfrg = docRfrgCount;
					kisRfrg += docRfrgCount;
					
					exclusiveTotal += ra.exclusive;
					orgCount++;
				}				
				curItem.exclusive = ra.exclusive;
			}
			curItem.updateRfrg(ra);
		}
		dl.close();
		
		if( curItem != null )
			factRfrg += curItem.factRfrg.size();
		
		cold = String.format("%d/%d", factRfrg, kisRfrg);
		if( orgCount > 0 )
			exclusive = (exclusiveTotal  * 10 / orgCount + 5) / 10;
	}

	private Item findOrCreateItem(String id, RepGroup[] repGroups, int order) {
		for(Item i : items) {
			if(i.id.equals(id))
				return i;
		}
		
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx)oi.getData();
		oe.id = id;
		oi.read();
		oi.close();
		
		Item newItem = new Item(oe, null, repGroups, order);
		items.add(newItem);
		return newItem;
	}

	private void addItem(RepGroup[] repGroups, OrgDailyData odd, OrgEx org, int order) {
		Item newItem = new Item(org, odd, repGroups, order);
		items.add(newItem);
//		lines += newItem.lines;
		group1Sum += newItem.group1Sum;
		group1Qty += newItem.group1Qty;
		group2Sum += newItem.group2Sum;
		group2Qty += newItem.group2Qty;
		group3Sum += newItem.group3Sum;
		group3Qty += newItem.group3Qty;
		group4Sum += newItem.group4Sum;
		group4Qty += newItem.group4Qty;
		
		credit += newItem.credit;
		cash += newItem.cash;
		sum += (newItem.credit + newItem.cash);
	}
	
	private void loadIncass(DatePeriod dp, HashMap<String, OrgDailyData> orgData, RepGroup[] repGroups) {
		DocList dl = PkoDoc.instance().docList(null, null, dp);
		for(Document<?> doc : dl) {
			OrgDailyData odd = orgData.get(doc.getId());
			if( odd == null ) {
				odd = new OrgDailyData(repGroups);
				orgData.put(doc.getId(), odd);
			}
			odd.cash += doc.sum();
		}
		dl.close();
	}

	private void loadVisits(DatePeriod dp, HashMap<String, OrgDailyData> orgData, RepGroup[] repGroups) {
		DocList dl = VisitDoc.instance().docList(null, null, dp);
		for(Document<?> doc : dl) {
			OrgDailyData odd = orgData.get(doc.getId());
			if( odd == null ) {
				odd = new OrgDailyData(repGroups);
				orgData.put(doc.getId(), odd);
			}
		}
		dl.close();
	}
	
	private HashMap<String, OrgDailyData> loadOrders(DatePeriod dp, RepGroup[] repGroups) {
		HashMap<String, OrgDailyData> ret = new HashMap<String, OrgDailyData>();
		
		DocList dl = SalesDoc.instance().docList(null, null, dp);
		for(Document<?> doc : dl) {
			SalesImpl oi = (SalesImpl) doc;
			OrgDailyData odd = ret.get(oi.getId());
			if( odd == null ) {
				odd = new OrgDailyData(repGroups);
				ret.put(oi.getId(), odd);
			}
			odd.sum += oi.sum();
			
			for(OrderItem item : oi.getData().items) {
				odd.lines.add(item.id);
				for(int i=0; i<repGroups.length; i++ ) {
					Integer packQty = repGroups[i].getPackQty(item);
					if( packQty != null ) {
						GroupData gd = odd.groups[i];
						gd.qty += (int)((long)item.qty * Consts.QTY_SCALE / packQty);
						gd.sum += (int)((long)item.cost * item.qty / Consts.QTY_SCALE);
						break;
					}
				}
			}
		}
		dl.close();
		
		return ret;
	}

	class Item {
		public Item(OrgEx org, OrgDailyData odd, RepGroup[] repGroups, int order) {
			this.order = order;
			
			name = org.name;
			id = org.id;
			if( org.address.length() > 0 )
				name += " " + org.address;
			
			group1 = repGroups[0].name;
			group2 = repGroups[1].name;
			group3 = repGroups[2].name;
			group4 = repGroups[3].name;
			balance = org.balance;
			
			if( odd != null ) {
				credit = odd.sum - odd.cash;
				cash = odd.cash;
				lines = String.format("%d/-", odd.lines.size());
				balance += credit;
				
				group1Sum = odd.groups[0].sum;
				group1Qty = odd.groups[0].qty / Consts.QTY_SCALE;
				group2Sum = odd.groups[1].sum;
				group2Qty = odd.groups[1].qty / Consts.QTY_SCALE;
				group3Sum = odd.groups[2].sum;
				group3Qty = odd.groups[2].qty / Consts.QTY_SCALE;
				group4Sum = odd.groups[3].sum;
				group4Qty = odd.groups[3].qty / Consts.QTY_SCALE;
			}
		}
		
		public void updateRfrg(RfrgAudit ra) {
			for(RfrgAuditItem i : ra.items) {
				if( i.fact_id.length() > 0 )
					factRfrg.add(i.fact_id);
			}
			
			cold = String.format("%d/%d", factRfrg.size(), docRfrg);
		}
		
		public String group1;
		public String group2;
		public String group3;
		public String group4;
		
		@Scale(value=Consts.SUM_SCALE, hideRest=false)
		public int balance;
		@Scale(value=Consts.SUM_SCALE, hideRest=false)
		public int credit;
		@Scale(value=Consts.SUM_SCALE, hideRest=false)
		public int cash;
		
		@Scale(value=Consts.SUM_SCALE, hideRest=false)
		public int group1Sum;
		@Scale(value=Consts.SUM_SCALE, hideRest=false)
		public int group2Sum;
		@Scale(value=Consts.SUM_SCALE, hideRest=false)
		public int group3Sum;
		@Scale(value=Consts.SUM_SCALE, hideRest=false)
		public int group4Sum;
		
		public int group1Qty;
		public int group2Qty;
		public int group3Qty;
		public int group4Qty;
		
		public String lines = "0/-";
		
		/**
		 *  кол-во холодильников
		 */
		public String cold = "-";

		HashSet<String> factRfrg = new HashSet<String>();
		int docRfrg;
		public int exclusive; 
		
		public String name;
		public int order;
		
		public String id;
	}
}
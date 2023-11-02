package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.AgentMemo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgBalance;
import com.grsoft.dataobjects.OrgBalanceData;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgTaskExecImpl;
import com.grsoft.napoleon.documents.AgentMemoDoc;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DispatchReturnsDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.Util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MainEx extends Main {
	
	Map<String, Integer> debetColorOrgs = new HashMap<String, Integer>();
	Map<String, DebetOrgData> debetData = new HashMap<String, DebetOrgData>();
	
	Set<String> todayMemos = new HashSet<String>();
	
	
	public interface OrgFilterColor {
		void filterByColor(int color);
	}
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		findViewById(R.id.btnFilter).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(R.id.debt_filter_dialog);}
		});
	}
	
	@Override
	protected ArrayList<MenuHandler> createDocMenuList() {
		ArrayList<MenuHandler> ret = super.createDocMenuList();
		ret.add(0, new MenuHandler(getString(R.string.order_status), new Runnable() {			
			@Override public void run() { OrderStatusList.open(MainEx.this); }
		}));
		return ret;
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void drawOrg(Org org, View view) {
		super.drawOrg(org, view);
		
		if(DocType.getCurDoc() == DebtDoc.instance()) {
			OrgEx oe = (OrgEx)org;
			Integer clr = debetColorOrgs.get(oe.ido);
			if(clr != null) {
				((TextView)view.findViewById(R.id.tvOrgName)).setTextColor(BalanceHelper.getColor(clr));
			}
			
			long ct = (new Date()).getTime();
			DebetOrgData dd = debetData.get(oe.ido);
			String text = "0.00";
			if(dd != null) {
				long diff = ct - dd.payDate.getTime(); 
				text = String.format("<i>%d</i> %s<br/><b>%s</b>",
						diff < 0 ? 0 : diff / (24 * 3600 * 1000),
						Util.IntToScaleStr(dd.sum, Consts.SUM_SCALE),
						Util.IntToScaleStr(dd.overdueSum, Consts.SUM_SCALE)
						);
				if(dd.unlockDateValid()) {
					text = "<b>" + Util.simpleDateFormat.format(dd.unlockDate) + "</b>&nbsp;&nbsp;&nbsp;" + text;
				}
			}
			TextView tvOrgSum = (TextView)view.findViewById(R.id.tvOrgSum);
			tvOrgSum.setText(Html.fromHtml(text));
//			int color = (todayMemos.contains(oe.ido)) ? -16751616 : Color.BLACK;
			int color = (todayMemos.contains(oe.ido)) ? -16733696 : Color.BLACK;
			tvOrgSum.setTextColor(color);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.debt_filter_dialog) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Фильтр долгов");
			
			CharSequence[] items = new CharSequence[] {
				"все",
				"просрочено > 25 дней",
				"просрочено 11 - 25 дней",
				"просрочено 1 - 10 дней",
			};
			
			b.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					doFilterOrg(arg1); 
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	protected void doFilterOrg(int arg1) {
		Adapter a = list.getAdapter();
		if(a instanceof OrgFilterColor) {
			((OrgFilterColor)a).filterByColor(arg1);
			refreshDocSum(DebtDoc.instance());
		}
	}
	
	String getOrgByColors(int color) {
		String ret = "";
		for(Entry<String, Integer> kv : debetColorOrgs.entrySet()) {
			if(kv.getValue() == color) {
				if(ret.length() > 0)
					ret += ",";
				ret += "'" + kv.getKey() + "'";
			}
		}
		return ret;
	}

	@Override
	protected void refreshDocSum(DocType docType) {
		if(docType == DebtDoc.instance()) {
			long sum = 0;
			BaseMainAdapter adapter = (BaseMainAdapter) list.getAdapter();
			int count = adapter.getCount() - 1;
			while(count>0) {
				OrgEx o = (OrgEx) adapter.getOrg(count--);
				if(o != null) {
					DebetOrgData dd = debetData.get(o.ido);
					if(dd != null)
						sum += dd.sum;
//					DebetOrgData baseDD = debetData.get(o.ido);
//					if(baseDD != null) {
//						if(dd != null) {
//							dd.sum += baseDD.sum;
//							dd.payDate = baseDD.payDate;
//						} else {
//							debetData.put(o.id, baseDD);
//						}
//						debetData.remove(o.ido);
//						sum += baseDD.sum;
//						int colorIndex = BalanceHelper.colorIndex(Util.getDate(), baseDD.payDate);
//						if(colorIndex >= 0) {
//							debetColorOrgs.put(o.id, colorIndex);
//						}
//					}
				}
			}
			updateTotalSum(sum, 0);
		} else
			super.refreshDocSum(docType);
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {		
		int vis = View.GONE;
		if(docType == DebtDoc.instance()) {
			vis = View.VISIBLE;
			
			todayMemos.clear();
			
			try {
				long cur = Util.getDate().getTime();
				String stmt = "select distinct o.ido from Org o, AgentMemo am where o.id = am.id and am.created > " + Long.toString(cur) + " and am.topic='" + AgentMemo.UNLOCK_TOPIC_ID + "'";
				Cursor c = DataBaseManager.getDataBase().rawQuery(stmt, null);
				while(c.moveToNext()) {
					todayMemos.add(c.getString(0));
				}
			} catch(Exception e){
				e.printStackTrace();
			}
			
			debetColorOrgs.clear();
			debetData.clear();
			final OrgImpl oi = new OrgImpl();
			final OrgEx oe = (OrgEx) oi.getData();

			final Date dueDate = Util.getDate();
			DataTraveler.travel(OrgBalanceData.class, new DataTraveler.Travel<OrgBalanceData>() {

				@Override
				public boolean travel(DataTraveler<OrgBalanceData> item) {
					String id = item.data.ido; 
					if(id.isEmpty()) {
						oe.id = item.data.id;
						oi.read();
						id = oe.ido;
					}
					DebetOrgData d = debetData.get(id);
					if(d == null) {
						debetData.put(id, new DebetOrgData(item.data, dueDate));
					} else {
						d.add(item.data, dueDate);
					}
					return true;
				}
			}, "");
			oi.close();
			
			
			for(Entry<String, DebetOrgData> kv : debetData.entrySet()) {
				String id = kv.getKey();
				int colorIndex = BalanceHelper.colorIndex(dueDate, kv.getValue().payDate);
				if(colorIndex >= 0)
					debetColorOrgs.put(id, colorIndex);
			}
			
			DataTraveler.travel(OrgBalance.class, new DataTraveler.Travel<OrgBalance>() {

				@Override
				public boolean travel(DataTraveler<OrgBalance> item) {
					DebetOrgData d = debetData.get(item.data.id);
					if(d != null )
						d.upateUnlockDate(item.data);
					return true;
				}
			}, "");
		}
		
		findViewById(R.id.btnFilter).setVisibility(vis);
		super.adjustViewForDocType(docType);
	}
	
	@Override protected int getResourceID() { return R.layout.mainex; }
	
	@Override protected BaseAdapter createSolidMainAdapter() { return new SolidAdapterEx(this); }
	@Override protected BaseAdapter createFoldersMainAdapter() { return new DebetOrgAdapter(this); }
	
	
	class SolidAdapterEx extends SolidMainAdapter implements OrgFilterColor {

		String colorFilter = null;
		
		public SolidAdapterEx(Main main) {
			super(main);
		}

		@Override
		public void filterByColor(int color) {
			if(color == 0) {
				colorFilter = null;
			} else {
				colorFilter = " ido in (" + getOrgByColors(color - 1) + ")";
			}
			
			load(null);
			notifyDataSetChanged();
		}
		
		@Override
		protected String getWhereStr() {
			String ret = super.getWhereStr();
			if(colorFilter != null && colorFilter.length() > 0) {
				if(ret == null)
					ret = "";
				if(ret.length() > 0)
					ret += " and ";
				ret += colorFilter; 
			}
			return ret;
		}
		
	}
	
	@Override
	protected DocFilterOnClickListener createDocFilter() {
		List<DocTypeBase> filter = new ArrayList<DocTypeBase>();
		if(mode == FOLDER_VIEW) {
			filter.add(DebtDoc.instance());
			filter.add(AgentMemoDoc.instance());
		} else {
			filter.add(OrderDoc.instance());
			filter.add(VisitDoc.instance());
			filter.add(RemnantsDoc.instance());
			filter.add(QuestionDoc.instance());
			filter.add(IncassDoc.instance());
			filter.add(TaskDoneDoc.instance(OrgTaskExecImpl.class));
			filter.add(DispatchReturnsDoc.instance()); //ReturnDoc.instance());
		}
		return new DocFilterOnClickListener(this, false, ScriptDefImpl.canScripting(), filter);
	}
	
	@Override
	protected void setAdapterMode() {
		super.setAdapterMode();
		btnDocFilter.setOnClickListener(createDocFilter());
		DocType ct = (mode == FOLDER_VIEW) ? DebtDoc.instance() : OrderDoc.instance();
		adjustViewForDocType(ct);
	}
	
	
	class DebetOrgAdapter extends SolidMainAdapter implements OrgFilterColor {

		String colorFilter = null;
		Set<String> loaded;

		public DebetOrgAdapter(Main main) {
			super(main);
		}
		
		@Override
		protected void load(String filter) {
			if(loaded == null)
				loaded = new HashSet<String>();
			else
				loaded.clear();
			
			super.load(filter);
		}

		@Override
		protected boolean skipItem(Org o) {
			if(loaded.contains(((OrgEx)o).ido))
				return true;
			
			loaded.add(((OrgEx)o).ido);
			return false;
		}
		
		@Override
		public void filterByColor(int color) {
			if(color == 0) {
				colorFilter = null;
			} else {
				colorFilter = " ido in (" + getOrgByColors(color - 1) + ")";
			}
			
			load(null);
			notifyDataSetChanged();
		}
		
		@Override
		protected String getWhereStr() {
			String ret = super.getWhereStr();
			if(colorFilter != null && colorFilter.length() > 0) {
				if(ret == null)
					ret = "";
				if(ret.length() > 0)
					ret += " and ";
				ret += colorFilter; 
			}
			return ret;
		}
	}
}

class DebetOrgData {
		
	public int sum = 0;
	public int overdueSum = 0;
	public Date payDate;
	public Date unlockDate;
	
	public DebetOrgData(OrgBalanceData src, Date dueDate) {
		sum = src.sumD;
		if(sum > 0 && Util.getDayStart(src.payDate).compareTo(dueDate) < 0) {
			overdueSum  = src.sumD;
		}
		payDate = src.payDate;
	}
	
	public void add(OrgBalanceData src, Date dueDate) {
		sum += src.sumD;
		if(src.sumD > 0 && src.payDate.compareTo(dueDate) < 0) {
			overdueSum  += src.sumD;
		}
		if(src.payDate.compareTo(payDate) < 0)
			payDate = src.payDate;
	}
	
	public void upateUnlockDate(OrgBalance src) {
		if(src.unlockDate.getTime() > OrgBalance.CHECK_DATE) {
			if(unlockDate == null)
				unlockDate = src.unlockDate;
			else {
				unlockDate = new Date(OrgBalance.CHECK_DATE);
			}
		}
	}
	
	public boolean unlockDateValid() { return unlockDate != null && unlockDate.getTime() > OrgBalance.CHECK_DATE; }
}

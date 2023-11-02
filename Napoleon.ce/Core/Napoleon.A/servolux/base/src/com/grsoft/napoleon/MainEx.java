package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.MerchRouteForAgent;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.OrgFolderItemEx;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.ScriptEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.OrgFoldersTree;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;

import android.graphics.Color;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MainEx extends Main {
	
//	Map<String, List<FirmEx>> debts = new HashMap<String, List<FirmEx>>();
	List<OrgFolderItem> todayItems = null;
	
	Set<String> compleeteMainScripts = new HashSet<String>();
	Set<String> notComplScripts = new HashSet<String>();
	Set<String> compleeteOtherScripts = new HashSet<String>();
	
	@Override protected int getSolidRowID() { return R.layout.main_list_row_ex; }
	
	
//	@Override
//	protected void onResume() {
//		super.onResume();
//
//		// start_stop фича не включена
//		String orgId = WorkTimeListener.getWorkOrg(this);
//		if(orgId.length() > 0) {
//			OrgImpl oi = new OrgImpl();
//			oi.getData().id = orgId;
//			oi.read();
//			oi.close();
//			Documents.open(this, oi.getData());
//		}
		
//		
//		debts.clear();
//		todayItems = null;
//		
//		Cursor c = null;
//		String stmt = "select f.id, f.shortName, p.ido from Firm f join (select ido, firm from Payment group by ido, firm) as p on f.id = p.firm order by p.ido, f.shortName";
//		try {
//			c = DataBaseManager.getDataBase().rawQuery(stmt, null);
//			while(c.moveToNext()) {
//				String ido = c.getString(2);
//				
//				List<FirmEx> fe = debts.get(ido);
//				if(fe == null) {
//					fe = new ArrayList<FirmEx>();
//					debts.put(ido, fe);
//				}
//				FirmEx f = new FirmEx();
//				f.id = c.getString(0);
//				f.shortName = c.getString(1);
//				fe.add(f);
//				
//			}
//		} catch(Exception e) {
//			e.printStackTrace();
//		} finally {
//			if( c != null )
//				c.close();
//		}
//	}
	
	@Override
	public View getSolidMainView(Org org, int pos, View view) {
		view = super.getSolidMainView(org, pos, view);
		String text = "";
		int color = Color.BLACK;
		if(mode == FOLDER_VIEW) {
			Object item = foldersMainAdapter.getItem(pos);
			if(item instanceof OrgFolderItemEx ) {
				text = ((OrgFolderItemEx)item).kind;
				if(text.equals("М"))
					color = Color.MAGENTA;
			}
		} else {
			if(todayItems == null)
				todayItems = ((FoldersMainAdapter)foldersMainAdapter).getTodayItems();
			if(todayItems != null) {
				for(OrgFolderItem ofi : todayItems) {
					if(ofi.name.equals(org.id)) {
						text = ((OrgFolderItemEx)ofi).kind;
						break;
					}
				}
			}
		}
		
		TextView tv = (TextView)view.findViewById(R.id.tvRouteKind);
		tv.setText(text);
		if(color != Color.BLACK)
			tv.setTextColor(color);
		
		tv = (TextView)view.findViewById(R.id.tvOrgName);
		if(color != Color.BLACK)
			tv.setTextColor(color);
		return view;
	}
		
	@Override
	public void openOrg(Org org, int pos) {
		String scriptKind = "";
		Object item = list.getItemAtPosition(pos);
		if(item instanceof OrgFolderItemEx)
			scriptKind = ((OrgFolderItemEx)item).kind;
		else {
			if(todayItems != null) {
				for(OrgFolderItem ofi : todayItems) {
					if(ofi.name.equals(org.id)) {
						scriptKind = ((OrgFolderItemEx)ofi).kind;
						break;
					}
				}
			}
		}
		
		DocumentsEx.openForScript(this, org, scriptKind);
	}
	
	@Override
	protected void openDocumentsFormStartStop(String orgId) {
		String scriptKind = "";
		if(todayItems == null) {
			todayItems = ((FoldersMainAdapter)foldersMainAdapter).getTodayItems();
		}
		if(todayItems != null) {
			for(OrgFolderItem ofi : todayItems) {
				if(ofi.name.equals(orgId)) {
					scriptKind = ((OrgFolderItemEx)ofi).kind;
					break;
				}
			}
		}
		
		OrgImpl oi = new OrgImpl();
		oi.getData().id = orgId;
		oi.read();
		oi.close();
		DocumentsEx.openForScript(this, oi.getData(), scriptKind);
	}
	
	@Override
		protected void drawOrg(Org org, View view) {
			super.drawOrg(org, view);
			
			if(ScriptDefImpl.canScripting()) {
				int color = Color.BLACK;
				if(compleeteMainScripts.contains(org.id)) color = getResources().getColor(R.color.item_highlight);
				else if(notComplScripts.contains(org.id)) color = Color.RED;
				else if(compleeteOtherScripts.contains(org.id)) color = Color.BLUE;
				
				((TextView)view.findViewById(R.id.tvOrgName)).setTextColor(color);
			}
		}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		compleeteMainScripts.clear();
		compleeteOtherScripts.clear();
		notComplScripts.clear();
		if(ScriptDefImpl.canScripting()) {
			DatePeriod dp = new DatePeriod(Util.getDate(), Util.getDayEnd(new Date()));
			DocList dl = ScriptDoc.instance().docList(null, "", dp);
			for(Document<?> d : dl) {
				ScriptImplEx se = (ScriptImplEx)d;
				if(se.isComplete()) {
					if(((ScriptEx)se.getData()).isMain > 0) {
						compleeteMainScripts.add(se.getId());
					} else {
						compleeteOtherScripts.add(se.getId());
					}
				} else {
					notComplScripts.add(se.getId());
				}
					
			}
			dl.close();
		}
	}
	
//	class OpenDebt implements View.OnClickListener {
//
//		String firmId, orgId;
//		
//		public OpenDebt(String orgId, String firmId) {
//			this.orgId = orgId;
//			this.firmId = firmId;
//		}
//		
//		@Override
//		public void onClick(View v) {
//			DebetView.open(MainEx.this, orgId, firmId);
//		}
//	}
	
	@Override
		protected BaseAdapter createFoldersMainAdapter() {
			return new FoldersMainAdapterEx(this);
		}
	
	class FoldersMainAdapterEx extends FoldersMainAdapter {
		public FoldersMainAdapterEx(Main main) {
			super(main);
		}
		
		@Override
		protected OrgFoldersTree createOrgFoldersTree() {
			return new OrgFoldersTreeEx();
		}
		
//		@Override
//		public void itemsMode(OrgFolders currentOrgFolders) {
//			Collections.sort(currentOrgFolders.items, new Comparator<OrgFolderItem>() {
//
//				@Override
//				public int compare(OrgFolderItem arg0, OrgFolderItem arg1) {
//					if(((OrgFolderItemEx)arg0).kind.equals(((OrgFolderItemEx)arg1).kind))
//						return arg0.pos - arg1.pos;
//					return ((OrgFolderItemEx)arg0).kind.equals("М") ? 1 : -1;
//				}
//			});
//			super.itemsMode(currentOrgFolders);
//		}
	}
}

class OrgFoldersTreeEx extends OrgFoldersTree {
	@Override 
	protected String getValidWhere() {
		CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
		return cfg.onlyNewstItems == 1 ? "(hidden = 0 or hidden is null)" : null; 
	}
	
	@Override 
	protected void prepareFolder(final OrgFolders of) {
		
		String where = "day='" + of.name + "'"; 
		
		DataTraveler.travel(MerchRouteForAgent.class, new DataTraveler.Travel<MerchRouteForAgent>() {

			@Override
			public boolean travel(DataTraveler<MerchRouteForAgent> item) {
				OrgFolderItemEx ofi = new OrgFolderItemEx();
				ofi.name = item.data.id;
				ofi.pos = 1000;
				ofi.kind = "М";
				of.items.add(ofi);
				return true;
			}
		}, where);
		super.prepareFolder(of);
	}
	
}

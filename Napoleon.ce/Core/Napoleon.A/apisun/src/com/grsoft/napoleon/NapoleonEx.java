package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map.Entry;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.OrgFoldersEx;
import com.grsoft.dataobjects.OrgRegion;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.OrgFoldersTree;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.MenuHandler;

public class NapoleonEx extends Napoleon {
	public static boolean loaded = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		loaded = false;
		super.onCreate(savedInstanceState);
		findViewById(R.id.btnMode).setVisibility(View.GONE);
		setListMode(ListViewMode.ROUTE_LIST);
	}

	@Override
	protected OrgFoldersAdapter getOrgFoldersAdapter() {
		return new OrgFoldersAdapterEx() {
			@Override
			public void refreshCurrentFolder() {
				super.refreshCurrentFolder();
				((OrgFoldersTreeEx) tree).reloadCurrentFolder();
			}
		};
	}

	class OrgFoldersAdapterEx extends OrgFoldersAdapter {
		@Override
		protected OrgFoldersTree createOrgFoldersTree() {
			return new OrgFoldersTreeEx();
		}
		
		@Override
		protected void resetFilterProcess() {}
	}

	@Override
	protected void drawOrg(OrgImpl oi, View view) {
		DocType.getCurDoc().setMainView(view, linesController, oi, os);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		View view = ((AdapterContextMenuInfo) menuInfo).targetView;
		Object tag = view.getTag();
		if (tag instanceof OrgFoldersEx)
			return;
		
//		boolean alreadyAdded = false;
//		for(int i = 0; i < menu.size(); i++){
//			alreadyAdded = menu.getItem(i).getTitle().equals(getString(R.string.visit));
//			if(alreadyAdded)	break;
//		}
//		
//		if(!alreadyAdded)
//			menu.add(R.string.visit);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CreateOrg.CREATEORGID) {
//			OrgFoldersAdapter adapter = (OrgFoldersAdapter) lvMainOrgs
//					.getAdapter();
			NapoleonEx.loaded = false;
			// adapter.refresh();
//			adapter.refreshCurrentFolder();
//			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	protected ArrayList<MenuHandler> createMainMenuList() {
		ArrayList<MenuHandler> ret = super.createMainMenuList();
		if (lvMainOrgs.getAdapter() instanceof OrgFoldersAdapter) {
			String checkStr = getString(R.string.add_org);
			for (MenuHandler mh : ret) {
				if (mh.name.equals(checkStr)) {
					mh.handler = new Runnable() {
						@Override
						public void run() {
							createNewOrg(ExtrasConst.INVALID_ID);
						}
					};
					break;
				}
			}
		}
		return ret;
	}

	protected void createNewOrg(long rowid) {
		OrgFoldersAdapter adapter = (OrgFoldersAdapter) lvMainOrgs.getAdapter();
		OrgFoldersEx ofe = (OrgFoldersEx) adapter.currentFolder();

		if (ofe != null)
			CreateOrg.open(NapoleonEx.this, ofe.parent, rowid);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle().equals(getString(R.string.edit)))
			createNewOrg((Long) ((AdapterContextMenuInfo) item.getMenuInfo()).targetView
					.getTag());
		else
			return super.onContextItemSelected(item);

		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		OrgFoldersAdapter adapter = (OrgFoldersAdapter) lvMainOrgs
				.getAdapter();
		
		if(adapter != null){
			adapter.refreshCurrentFolder();
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	protected boolean isPotencialOrg(long rowid) {
		return true;
	}
}

class OrgFoldersTreeEx extends OrgFoldersTree {

	@Override
	public boolean isToday(int pos) {
		return false;
	}

	public void reloadCurrentFolder() {
		if (currentOrgFolder != null) {
			// load orgs & add to folders
			currentOrgFolder.items.clear();
			OrgEx o = new OrgEx();
			DbWriter.checkDBTable(OrgEx.class);
			DbReader r = new DbReader();
			String table = DataObjectInfo.getInstance().getTableName(Org.class);
			boolean bdo = r.select(o, table, "parent='" + ((OrgFoldersEx)currentOrgFolder).parent + "'");
			while (bdo) {
				OrgFolderItem oi = new OrgFolderItem();
				oi.name = o.id;
				currentOrgFolder.items.add(oi);
				bdo = r.selectNext(o);
			}
			
			Collections.sort(currentOrgFolder.items, new OrgFolderItemCmp());
		}
	}

	@Override
	protected void loadData(final Date onDate) {

		if (NapoleonEx.loaded)
			return;

		NapoleonEx.loaded = true;

		Log.d(getClass().getName(), "loadData");
		orgFolders.clear();
		DbWriter.checkDBTable(OrgRegion.class);
		// load folders
		Hashtable<String, OrgFolders> folders = new Hashtable<String, OrgFolders>();
		OrgRegion or = new OrgRegion();
		String table = DataObjectInfo.getInstance().getTableName(
				OrgRegion.class);
		DbReader r = new DbReader();
		boolean bdo = r.select(or, table, null);
		while (bdo) {
			OrgFoldersEx of = new OrgFoldersEx();
			of.name = or.name;
			of.parent = or.id;
			
			folders.put(or.id, of);

			bdo = r.selectNext(or);
		}
		r.close();

		// load orgs & add to folders
		OrgEx o = new OrgEx();
		DbWriter.checkDBTable(OrgEx.class);
		table = DataObjectInfo.getInstance().getTableName(Org.class);
		bdo = r.select(o, table, null);
		while (bdo) {
			OrgFoldersEx of = (OrgFoldersEx) folders.get(o.parent);
			if (of != null) {
				OrgFolderItem oi = new OrgFolderItem();
				oi.name = o.id;
				of.items.add(oi);
			}
			bdo = r.selectNext(o);
		}

		// write folders
		for (Entry<String, OrgFolders> e : folders.entrySet()) {
			if (e.getValue().items.size() > 0) {
				orgFolders.add(e.getValue());
				Collections.sort(e.getValue().items, new OrgFolderItemCmp());
			}
		}

		Collections.sort(orgFolders, new OrgFoldersNameCmp());
	}
}

class OrgFolderItemCmp implements Comparator<OrgFolderItem> {

	@Override
	public int compare(OrgFolderItem lhs,
			OrgFolderItem rhs) {
		int result = -1;

		OrgImpl lhsOrg = new OrgImpl();
		OrgImpl rhsOrg = new OrgImpl();

		lhsOrg.getData().id = lhs.name;
		lhsOrg.read();
		rhsOrg.getData().id = rhs.name;
		rhsOrg.read();

		result = lhsOrg.getData().name.compareTo(rhsOrg
				.getData().name);

		lhsOrg.close();
		rhsOrg.close();

		return result;
	}
}

class OrgFoldersNameCmp implements Comparator<OrgFolders> {
	@Override
	public int compare(OrgFolders object1, OrgFolders object2) {
		return object1.name.compareTo(object2.name);
	}
}

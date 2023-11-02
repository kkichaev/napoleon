package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.impl.FilterCmp;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.Main.MainAdapter;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.OrgFoldersTree;


public class FoldersMainAdapter extends BaseMainAdapter implements FilterAdapter, MainAdapter {
	private Main main;
	protected OrgFoldersTree tree;
	protected FilterComparer routeFilter = createRouteFilter();
	
	static class ViewData{
		String name;
		List<String> ids = new ArrayList<String>();
	}
	
	public FoldersMainAdapter(Main main){
		this.main = main;
		tree = createOrgFoldersTree();
	}
	
	protected FilterComparer createRouteFilter() {	return new FilterComparer(); }
	protected OrgFoldersTree createOrgFoldersTree() { 
		return new OrgFoldersTree(){
		@Override protected String getValidWhere() {
			CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
			return cfg.onlyNewstItems == 1 ? "(hidden = 0 or hidden is null)" : null; 
		}
	}; }
	
	public void close() {
		routeFilter.close();
		tree.close();
	}
	
	public List<OrgFolderItem> getTodayItems() { return tree.getTodayItems(); }
	
	@Override public int getCount() { return tree.getCount();	}
	@Override public Object getItem(int position) { return tree.getItem(position); }
	@Override public long getItemId(int position) { return 0; }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		OrgImpl org = (OrgImpl) tree.getOrg(position);

		if (org == null){
			Object item = getItem(position);
			if(item != null && item instanceof OrgFolders){
				OrgFolders of = (OrgFolders)item;
				ViewData data = new ViewData();
				data.name = of.name;
				for(OrgFolderItem ofi : of.items)
					data.ids.add(ofi.name);
				convertView = main.getFolderMainView(convertView, position, data);
			}
		}else
			convertView =  main.getSolidMainView((Org) org.getData(), position, convertView);

		if (convertView != null) {
			ImageView ivFolder = (ImageView) convertView.findViewById(R.id.ivFolder);

			if (isTopLevel() && !tree.isFiltered()) {
				ivFolder.setImageResource(tree.isToday(position) ? R.drawable.folder_open : R.drawable.folder);
				ivFolder.setVisibility(View.VISIBLE);
			} else
				ivFolder.setVisibility(View.GONE);
		}

		return convertView;
	}

	public void itemsMode(OrgFolders currentOrgFolders) { tree.currentOrgFolder = currentOrgFolders; }
	
	public void routeMode() { tree.currentOrgFolder = null; }
	
	public OrgFolders currentFolder() { return tree.currentOrgFolder; }
	
	public void refreshCurrentFolder() {
		if(tree.currentOrgFolder != null)
			for (OrgFolders io : tree.orgFolders) {
				if(io.name.equals(tree.currentOrgFolder.name)){
					tree.currentOrgFolder = io;
					break;
				}
			}
	}
	
	public boolean isTopLevel()
	{
		return tree.currentOrgFolder == null;
	}

	@Override
	public void applyFilter(String value)
	{
		tree.applyFilter(routeFilter, value);	
		super.notifyDataSetChanged();
	}
	
	public void refresh() {
		tree.resetFilter();
		super.notifyDataSetChanged();
	}

	@Override
	public void resetFilter()
	{
		notifyDataSetChanged();
	}
	
	@Override
	public void notifyDataSetChanged() {
		resetFilterProcess();
		super.notifyDataSetChanged();
	}
	
	protected void resetFilterProcess(){
		tree.resetFilter();
	}

	@Override
	public void adjustView() {
		View v = main.findViewById(R.id.ivGoUp);
		if(v != null) {
			v.setVisibility(isTopLevel() ? View.GONE : View.VISIBLE);
			v.setOnClickListener(topLevelClick);
		}
		
		v = main.findViewById(R.id.btnMode);
		
		if(v != null) 
			((ImageView)v).setImageResource(R.drawable.route);
		
		if(isTopLevel()){
			v = main.findViewById(R.id.tvFirstColumnCaption);
			
			if(v != null){
				v.setOnClickListener(topLevelClick);
				((TextView)v).setText(main.getString(R.string.Day_of_week));
			}
			
			v = main.findViewById(R.id.tvMainDocValColTitle);
			
			if(v != null)
				((TextView)v).setText(main.getString(R.string.Clients_of));
		}else {
			DocType.getCurDoc().viewOpened(main);
			TextView tv = (TextView)main.findViewById(R.id.tvFirstColumnCaption); 
			if( tv != null )
				tv.setText(tree.currentOrgFolder.name);
		}
		
		main.onAdapterViewAdjusted();
	}
	
	protected OnClickListener topLevelClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (!isTopLevel())
			{
				routeMode();
				notifyDataSetChanged();
				main.resetFind();
				adjustView();
			}
		}
	};

	@Override
	public void click(int position) {
		Object item = getItem(position);
		
		if(item instanceof OrgFolders){
			OrgFolders of = (OrgFolders) item;
			itemsMode(of);
			notifyDataSetChanged();
			adjustView();
		}else{
			OrgImpl org = tree.getOrg(position);
			if(org != null)
				main.openOrg((Org) org.getData(), position);
		}
			
	}

	@Override
	void reload() {
		OrgFoldersTree curTree = tree;
		tree = createOrgFoldersTree();
		tree.setFrom(curTree);
	}

	@Override
	public Org getOrg(int pos) {
		OrgImpl org = tree.getOrg(pos);
		return (Org) (org != null ? org.getData() : null);
	}

	public String getWeekIndexTrace() {
		return tree == null ? "No tree" : tree.getWeekIndexTrace();
	}
}

class FilterComparer implements FilterCmp
{
	OrgImpl orgImpl = new OrgImpl();
	
	public void close() {
		orgImpl.close();
	}

	@Override
	public boolean compareTo(DataObject dataObject, String filter)
	{
		OrgFolderItem ofi = (OrgFolderItem) dataObject;
		orgImpl.getData().id = ofi.name;
		if (!orgImpl.read())
			return false;
		
		return orgImpl.getData().srchName.contains(filter.toUpperCase());
	}
	
}

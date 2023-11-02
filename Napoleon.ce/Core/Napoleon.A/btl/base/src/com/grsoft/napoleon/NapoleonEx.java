package com.grsoft.napoleon;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.impl.Cursor;
import com.grsoft.dataobjects.impl.OrgFoldersImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.dataobjects.impl.RegionImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.FindTextWatcher;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.MenuHandler;

public class NapoleonEx extends Napoleon {
	private RouteAdapter routeAdapter;
	private ImageButton btnAdd;  
	private TextView tvTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		routeAdapter = new RouteAdapter();
		lvMainOrgs.setAdapter(routeAdapter);
		lvMainOrgs.setOnItemClickListener(routeAdapter);
		findViewById(R.id.btnMode).setVisibility(View.GONE);
		
		btnAdd = (ImageButton) findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PotenzialOrgEx.open(NapoleonEx.this, routeAdapter.getRegionid());
			}
		});
		
		btnAdd.setVisibility(View.GONE);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		//findViewById(R.id.btnFind).setVisibility(View.GONE);
		
		OnClickListener onclick = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setTopLevelForTableHeader();
			}
		};
		
		tvTitle.setOnClickListener(onclick);
		findViewById(R.id.llTop).setOnClickListener(onclick);
		edFind.addTextChangedListener(new FindTextWatcher(edFind, lvMainOrgs));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		routeAdapter.requery();
		updateTitle();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		routeAdapter.close();
	}
	
	@Override
	protected int getResourceID() { return R.layout.mainex; }
	
	@Override
	public void setTopLevelForTableHeader() {
		routeAdapter.goUp();
		updateTitle();
		btnAdd.setVisibility(routeAdapter.isAllowCreateOrg() ? View.VISIBLE : View.GONE);
	}
	
	@Override
	protected ArrayList<MenuHandler> createMainMenuList() {
		ArrayList<MenuHandler> result = super.createMainMenuList();
		
		if(result != null)
			for(MenuHandler handler : result){
				if(handler.name.equals("Добавить организацию")){
					result.remove(handler);
					break;
				}
			}
			
		return result;
	}
	
	protected void updateTitle() {
		String title = routeAdapter.getTitle();
		
		if (title.length() > 0){
			tvTitle.setVisibility(View.VISIBLE);
			tvTitle.setText(Html.fromHtml(title));
		} else
			tvTitle.setVisibility(View.GONE);
	}

	@Override
	public void setGoUpVisibility(boolean visible) {
		super.setGoUpVisibility(visible);
		
		btnAdd.setVisibility(routeAdapter.isAllowCreateOrg() 
				? View.VISIBLE : View.GONE);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
		if (routeAdapter.isAllowCreateOrg())
			menu.add(getString(R.string.edit));
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		if(item.getTitle().equals(getString(R.string.edit))){
			Object object =  routeAdapter.getItem(
					((AdapterContextMenuInfo)item.getMenuInfo()).position);
			
			if (object instanceof OrgImpl)
				PotenzialOrgEx.open(this,((OrgImpl)object).getRowid(), 
						true, routeAdapter.getRegionid());
		}
		
		return true;
	}
	@Override
	protected ArrayList<MenuHandler> createDocMenuList() {
		ArrayList<MenuHandler> result = super.createDocMenuList();
		
		if(result != null)
			for(MenuHandler handler : result){
				if(handler.name.equals("Список документов")){
					result.remove(handler);
					break;
				}
			}
			
		return result;
	}
	
	class RouteAdapter extends BaseAdapter 
		implements OnItemClickListener, FilterAdapter{
		Cursor<OrgFolders> folders;
		Cursor<Org> orgs;
		private OrgFoldersImpl rootFolder;
		private RegionImpl region = new RegionImpl();
		private int level = 0;
		private OrgSumImpl dummy = new OrgSumImpl();
		
		public void requery() {
			if (folders == null)
				folders = new Cursor<OrgFolders>(
						new OrgFoldersImpl(), "", "id asc");
			else
				folders.updateIds();
			
			region.read();
			
			if (orgs == null)
				orgs = new Cursor<Org>(new OrgImpl(), 
					String.format("region = '%s'", region.getData().id));
			else
				orgs.updateIds();
			
			if (folders.getCount() == 0)
				level = 0;
			
			if( rootFolder != null && rootFolder.getRowid() != ExtrasConst.INVALID_ID )
				rootFolder.read(rootFolder.getRowid(), false);
			
			notifyDataSetChanged();
		}

		public boolean isAllowCreateOrg() {
			return level == 2;
		}

		public String getRegionid() {
			return region.getData().id;
		}

		public void goUp() {
			if (level > 0){
				level--;
				
				if(level == 0)
					setGoUpVisibility(false);
				
				notifyDataSetChanged();
			} 
		}

		@Override
		public int getCount() {
			int result = 0;
			
			switch(level){
				case 0: result = folders != null ? folders.getCount() : 0; break;
				case 1: result = rootFolder.getData().items.size(); break;
				default: result = orgs != null ? orgs.getCount() : 0;
			}

			return result;
		}

		@Override
		public Object getItem(int pos) {
			Object result = null;
			
			switch(level){
				case 0: result = folders.get(pos); break;
				case 1: OrgFolderItem item = rootFolder.getData().items.get(pos);
						region.getData().id = item.name;
						region.read();
						
						result = region;
						break;
				default: result = orgs.get(pos, false);
			}
				
			return result;
		}

		@Override
		public long getItemId(int position) { return 0;	}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if (convertView == null)
				convertView = View.inflate(NapoleonEx.this, getRowResourceID(), null);
			
			Object object = getItem(position);
			
			ImageView ivFolder = (ImageView) convertView.findViewById(R.id.ivFolder);
			TextView tvOrgSum = (TextView)convertView.findViewById(R.id.tvOrgSum);
			TextView tvOrgName = (TextView)convertView.findViewById(R.id.tvOrgName);
			
			if (!isAllowCreateOrg()){
				if (tvOrgName != null){
					linesController.prepareTextView(tvOrgName);
					tvOrgName.setText(getCaption(object));
					tvOrgName.setTextColor(getResources().getColor(R.color.black));
				}
				
				tvOrgSum.setVisibility(View.GONE);
				ivFolder.setVisibility(View.VISIBLE);
				ivFolder.setImageResource(R.drawable.folder);
			}else{
				ivFolder.setVisibility(View.GONE);
				tvOrgSum.setVisibility(View.VISIBLE);
				
				if( object != null && 
						object instanceof OrgImpl && 
						!((OrgImpl)object).getData().id.contains("\t"))
					((OrgImpl)object).getData().color = Color.rgb(255, 0, 0);
					
				DocType.getCurDoc().setMainView(convertView, linesController, (OrgImpl)object, dummy);
			}
			
			convertView.setBackgroundResource(position % 2 != 0 ? 
					R.drawable.even_row_selector :
					R.drawable.list_selector);		
			
			return convertView;
		}

		private CharSequence getCaption(Object object) {
			String result = "объект не найден";
			
			if (object instanceof OrgFoldersImpl)
				result = ((OrgFoldersImpl)object).getData().name;
			if (object instanceof RegionImpl)
				result = ((RegionImpl)object).getData().name;
			if (object instanceof OrgImpl)
				result = ((OrgImpl)object).getData().name;
			
			return result;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
			Object object = getItem(pos);
			
			if (level < 2)
				level++;
			
			setGoUpVisibility(true);
			
			if (object instanceof OrgFoldersImpl)
				rootFolder = (OrgFoldersImpl)object;
			else if (object instanceof RegionImpl){
				region = (RegionImpl)object;
				orgs = new Cursor<Org>(new OrgImpl(), 
						String.format("region = '%s'", region.getData().id));
			}else {
				Documents.open(NapoleonEx.this, orgs.getItemId(pos), true);
			}
			
			updateTitle();
			notifyDataSetChanged();
		}
		
		public void close(){
			if (rootFolder != null)
				rootFolder.close();
			
			region.close();
		}
		
		public String getTitle(){
			StringBuilder result = new StringBuilder();
			
			if (level > 0)
				result.append(rootFolder.getData().name);
			
			if (level > 1){
				result.append("<br><i>");
				result.append(region.getData().name);
				result.append("</i>");
			}
			
			return result.toString();
		}

		@Override
		public void applyFilter(String value) {
			if(value.trim().length() > 0){
				level = 2;
				orgs.applyFilter("srchName LIKE '%" + value.toUpperCase() + "%'");
				notifyDataSetChanged();
			}
		}

		@Override
		public void resetFilter() {
			level = 0;
			requery();
		}
	}
}

package com.grsoft.napoleon;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import com.grsoft.dataobjects.Contact;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.OrgFolderItemEx;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.OrgFoldersEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrgFoldersImpl;
import com.grsoft.dataobjects.impl.OrgFoldersImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.util.FindOnClickListener;
import com.grsoft.napoleon.util.OrgFoldersTree;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import android.content.Intent;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;


public class NapoleonEx extends Napoleon {
	private long orgSelRowID = ExtrasConst.INVALID_ROWID;

	@Override
	protected void refreshDocSum(DocType docType) {
		tvTotalSum.setVisibility(View.GONE);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		View view = ((AdapterContextMenuInfo)menuInfo).targetView;
		Object tag =  view.getTag();
		if( tag instanceof OrgFolders )
			return;
		
		menu.add(R.string.add_to_route);
	}
	
	@Override
	protected int getResourceID() {	return R.layout.mainex; }
	
	@Override
	protected int getRowResourceID() { return R.layout.main_list_rowex; }
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(item.getTitle().equals(getString(R.string.add_to_route))){
			orgSelRowID = (Long)((AdapterContextMenuInfo)item.getMenuInfo()).targetView.getTag();
			addToRoute();
			return true;
		} else
			return super.onContextItemSelected(item);
	}

	private void addToRoute() {
		Intent i = new Intent(this, CalendarActivity.class);
		i.putExtra(ExtrasConst.DATE_TAG, new Date());
		startActivityForResult(i, R.id.sel_date);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if( data != null && requestCode == R.id.sel_date ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Date newDate = Util.resetTime(new Date(ct));
			OrgFoldersImpl of = new OrgFoldersImplEx();
			if(!of.read("name", Util.simpleDateFormat.format(newDate)))
				((OrgFoldersEx)of.getData()).date = newDate;
			
			OrgImpl org = new OrgImpl();
			if(org.read(orgSelRowID)){
				boolean f = false;
				for(OrgFolderItem item : of.getData().items)
					if(item.name.equals(org.getData().id)){
						f = true;
						break;
					}
				
				if (!f){
					try{
						Class<? extends DataObject> type = DataObjectInfo.getInstance().getListType(of.getData().getClass(), "items");
						OrgFolderItem i = (OrgFolderItem) type.newInstance();
						i.name = org.getData().id;
						i.pos = of.getData().items.size();
						of.getData().items.add(i);
						of.write();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private Comparator<OrgFolderItem> itemsCmp = new Comparator<OrgFolderItem>() {
	
		@Override
		public int compare(OrgFolderItem lhs, OrgFolderItem rhs) {
			return lhs.pos - rhs.pos;
		}
	};
	
	protected void setDefaultDocType() { DocType.setCurDoc(RemnantsDoc.instance());	}
	
	@Override
	protected OrgFoldersAdapter getOrgFoldersAdapter() {
		
		return new OrgFoldersAdapter(){
			@Override
			protected OrgFoldersTree createOrgFoldersTree() {
				return new OrgFoldersTree(){
					@Override
					protected void loadData(final Date onDate) {
						orgFolders.clear();
						Date date = Util.resetTime(new Date());
						String where = String.format("date >= %d", date.getTime());
						DataTraveler.travel(DbObject.getDataType(OrgFolders.class), new DataTraveler.Travel<OrgFolders>(){
							@Override public boolean isDataNewInstance() { return true; }
							
							@Override
							public boolean travel(DataTraveler<OrgFolders> item) {
								orgFolders.add(item.data);
								return true;
							}} , where);
						
						Collections.sort(orgFolders, new Comparator<OrgFolders>() {
							@Override public int compare(OrgFolders lhs, OrgFolders rhs) { return ((OrgFoldersEx)lhs).date.compareTo(((OrgFoldersEx)rhs).date); }});
						
						for(OrgFolders o : orgFolders)
							Collections.sort(o.items, itemsCmp);
					};
					
					@Override
					public boolean isToday(int pos) {
						Date today = Util.resetTime(new Date());
						OrgFoldersEx o = (OrgFoldersEx) orgFolders.get(pos);
						
						return today.getTime() == o.date.getTime();
					}
				};
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				view.findViewById(R.id.tvOrgSum).setVisibility(View.GONE);
				
				Object pos = tree.getItem(position);
				if( pos instanceof OrgFolderItemEx) {
					OrgFolderItemEx ofi = (OrgFolderItemEx)pos;
					if(ofi.comment.length() > 0) {
						TextView tvOrgName = (TextView)view.findViewById(R.id.tvOrgName);
						OrgImpl orgImpl = tree.getOrg(position);
						
						if(orgImpl != null){
							Org org = orgImpl.getData();
							String str = "<b>" + org.name + "</b><br>" + org.address + "<br/><i>" + ofi.comment + "</i>";
							tvOrgName.setText(Html.fromHtml(str));
						}
					}
				}
				return view;
			}
		};
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(llFind != null)
			llFind.setVisibility(View.VISIBLE);
		
		tvTotalSum.setVisibility(View.GONE);
	}
	
	@Override
	protected FindOnClickListener createFindOnClickListener() {
		return new FindOnClickListener(edFind, lvMainOrgs, llFind){	@Override protected void setInputVisible(boolean show) { }	};
	}
	
	protected void drawOrg(OrgImpl oi, View view) {
		super.drawOrg(oi, view);
		
		Org o = oi.getData();
		
		StringBuilder sb = new StringBuilder();
		sb.append("<b>").append(o.name).append("</b>");
		
		Contact c = null;
		if(o.contacts.size() > 0){
			c = o.contacts.get(0);
			
			if(c.name.trim().length() > 0)
				sb.append("<br>").append(c.name);
		}
		
		if(o.address.trim().length() > 0)
			sb.append("<br><i>").append(o.address).append("</i>");
		
		TextView tv = (TextView) view.findViewById(R.id.tvOrgName);
		tv.setText(Html.fromHtml(sb.toString()));
		
		tv = (TextView) view.findViewById(R.id.tvPhone);
		tv.setVisibility(View.VISIBLE);
		tv.setVisibility(linesController.isVariable() ? View.VISIBLE : View.GONE);
		
		if(c != null && c.phone.trim().length() > 0)
			tv.setText(Html.fromHtml(String.format("<a href=\"tel:%s\">Телефон: %s</a>", c.phone, c.phone)));
		else
			tv.setText("");
		
		tv = (TextView) findViewById(R.id.tvOrgSum);
	}
	
	@Override protected String getOrgReadingFields() { return super.getOrgReadingFields() + ",contacts" ;	}
}

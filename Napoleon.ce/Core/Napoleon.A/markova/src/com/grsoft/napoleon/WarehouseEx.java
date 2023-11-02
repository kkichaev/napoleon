package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseAdapter.PriceInfo;
import android.view.View;
import android.widget.TextView;

public class WarehouseEx extends WarehouseNew {
	private Set<String> saled = new HashSet<String>();
	private Map<Integer, Integer> fldcolor = new HashMap<Integer, Integer>();
	boolean needupdate = false;
	
	@Override
	protected void onResume() {
		super.onResume();
		
		saled.clear();
		
		Date f = Util.resetTime(new Date());
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);
		f = c.getTime();
		
		CfgNplEx n = (CfgNplEx) ConfigManager.getConfig();
		c.add(Calendar.MONTH, -n.selsail);
		Date s = c.getTime();
		
		com.grsoft.napoleon.documents.DocList docs = OrderDoc.instance().docList(null, "created", new DatePeriod(s, f));
		
		for(Document<?> d : docs){
			OrderImpl o = (OrderImpl)d;
			
			for(OrderItem i : o.getData().items)
				saled.add(i.id);
		}
		
		if(needupdate){
			fillColorCache();
			needupdate = false;
		}
	}
	
	@Override
	public void editItem(long rowid) {
		super.editItem(rowid);
		
		needupdate = true;
	}
	
	@Override
	public void afterBuildSet() {
		super.afterBuildSet();
		fillColorCache();
	}

	protected void fillColorCache() {
		if(folderTree.size() == 0)
			folderTree.load();
		
		int color = getResources().getColor(R.color.notsaled);
		
		for(Folder r : folderTree){
			List<PriceInfo> list =  adapter.getPriceInfo(r.id);
			
			if (list != null)
				for(PriceInfo p : list){
					if(!saled.contains(p.id)){
						fldcolor.put(r.id, color);
						
						Folder cur = folderTree.getParent(r);
						while(cur != null){
							fldcolor.put(cur.id, color);
							cur = folderTree.getParent(cur);
						}
						
						break;
					}
				}
		}
	}

	@Override
	public View getFolderView(FolderTreeNode node, View convertView) {
		View view = super.getFolderView(node, convertView);
		
		int color = getResources().getColor(R.color.black);
		
		if(fldcolor.containsKey(node.id))
			color = fldcolor.get(node.id);
		
		((TextView)view.findViewById(R.id.tvItemSelectRowName)).setTextColor(color);
		
		return view;
	}
	
	@Override
	public void setColor(TextView textView, Price price) {
		if(!saled.contains(price.id))
			textView.setTextColor(getResources().getColor(R.color.notsaled));
		else
			super.setColor(textView, price);
	}
}

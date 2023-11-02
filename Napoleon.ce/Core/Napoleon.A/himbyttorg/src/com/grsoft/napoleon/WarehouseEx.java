package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.FolderTree;
import com.grsoft.util.Util;

import android.view.View;
import android.widget.TextView;

public class WarehouseEx extends WarehouseNew {

	List<String> curMonthPrice = new ArrayList<String>();
	List<String> prevMonthPrice = new ArrayList<String>();
	List<String> prevPrevMonthPrice = new ArrayList<String>();
	
	List<Integer> curMonthFolders = new ArrayList<Integer>();
	List<Integer> prevMonthFolders = new ArrayList<Integer>();
	List<Integer> prevPrevMonthFolders = new ArrayList<Integer>();
	
	@Override
	protected void loadLastBuyingItems(String orgId) {
		
		lastBuyingItems.clear();
		
		curMonthPrice = new ArrayList<String>();
		prevMonthPrice = new ArrayList<String>();
		prevPrevMonthPrice = new ArrayList<String>();
		
		Calendar c = Calendar.getInstance();
		Date now = Util.getDayEnd(new Date());
		c.setTime(now);
		c.add(Calendar.DAY_OF_MONTH, -30);
		Date m1 = c.getTime();
		c.add(Calendar.DAY_OF_MONTH, -30);
		Date m2 = c.getTime();
		c.add(Calendar.DAY_OF_MONTH, -30);
		Date start = c.getTime();
		
		FolderTree ft = new FolderTree();
		ft.load();
		
		PriceImpl pi = new PriceImpl();
		
		DatePeriod dp = new DatePeriod(start, now);
		com.grsoft.napoleon.documents.DocList dl = OrderDoc.instance().docList(orgId, "", dp);
		for(Document<?> d : dl) {
			Order od = (Order)d.getData();
			Date cr =od.created;
			if(m1.compareTo(cr) <= 0) 
				putItems(curMonthPrice, curMonthFolders, od.items, ft, pi);
			else if(m2.compareTo(cr) <= 0)
				putItems(prevMonthPrice, prevMonthFolders, od.items, ft, pi);
			else
				putItems(prevPrevMonthPrice, prevPrevMonthFolders, od.items, ft, pi);
		}
		dl.close();
		pi.close();
	}
	
	@Override
	protected int getDefaultColor(Price p) {
		int color = -1;
		if(curMonthPrice.contains(p.id)) {
			color = (int)(0xFFFF6A00);
		} else if(prevMonthPrice.contains(p.id)) {
			color = (int)(0xFFC65D00);
		} else if(prevPrevMonthPrice.contains(p.id)) {
			color = (int)(0xFF0026FF);
		}
		if(color != -1)
			return color;
		return super.getDefaultColor(p);
	}
	
	@Override
	public View getFolderView(FolderTreeNode node, View convertView) {
		View ret = super.getFolderView(node, convertView);
		int color = -1;
		
		if(curMonthFolders.contains(node.id)) {
			color = (int)(0xFFFF6A00);
		} else if(prevMonthFolders.contains(node.id)) {
			color = (int)(0xFFC65D00);
		} else if(prevPrevMonthFolders.contains(node.id)) {
			color = (int)(0xFF0026FF);
		}
		
		if(color != -1) {
			TextView tv = (TextView) ret.findViewById(R.id.tvItemSelectRowName);
			tv.setTextColor(color);
		}
		
		return ret;
	}
	

	private void putItems(List<String> list, List<Integer> folders, List<OrderItem> items, FolderTree ft, PriceImpl pi) {
		Price p = pi.getData();
		for(OrderItem oi : items)
			if(!list.contains(oi.id)) {
				list.add(oi.id);
				p.id = oi.id;
				pi.read();
				
				if(!folders.contains(p.folderID)) {
					Folder folder = ft.getFolder(p.folderID);
					while(folder != null) {
						folders.add(folder.id);
						folder = ft.getParent(folder);
					}
				}
			}
	}
}

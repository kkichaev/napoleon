package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.FolderTree;
import com.grsoft.util.Util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class OrderInfoAdapter extends BaseAdapter {
	static View dlgView;
	
	Context context;

	List<OrderInfoData> data = new ArrayList<OrderInfoData>();
	
	FolderTree ft = new FolderTree();
	SparseArray<OrderInfoData> topFolders = new SparseArray<OrderInfoData>();
	
	OrderInfoAdapter(Context context) {
		ft.load();
		this.context = context;
		
		int level = ft.get(0).level;
		for(Folder f : ft) {
			if(f.level == level) {
				OrderInfoData ofd = new OrderInfoData();
				ofd.folder = f;
				data.add(ofd);					
			}
		}
	}
	
	public void refresh(List<Order> docs) {
		for(OrderInfoData od : data)
			od.clear();
		
		if(ft.size() == 0)
			return;
		
		PriceImpl pi = new PriceImpl();
		PriceEx pe = (PriceEx)pi.getData();
		
		for(Order doc : docs) {
			for(OrderItem oi : doc.items) {
				pe.id = oi.id;
				pi.read();
				
				OrderInfoData odata = findInfoData(pe.folderID);
				if(odata != null)
					odata.add(oi, pe);
			}
		}
		
		pi.close();
		
		Collections.sort(data);
		
		notifyDataSetChanged();
	}
	
	static public Dialog createInfoDialog(Context context) {
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setTitle("Информация по заявке");
		dlgView = View.inflate(context, R.layout.order_info, null);
		b.setView(dlgView);
		b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface arg0, int arg1) { arg0.dismiss(); }
		});
		return b.create();
	}
	
	static public void prepareDialog(Context context, List<Order> docs) {
		ListView lv = (ListView)dlgView.findViewById(R.id.lvItems);
		OrderInfoAdapter adapter = new OrderInfoAdapter(context);
		lv.setAdapter(adapter);
		adapter.refresh(docs);
	}
	
	private OrderInfoData findInfoData(int folderID) {
		OrderInfoData fdata = topFolders.get(folderID);
		if( fdata == null) {
			Folder topf = null;
			int level = ft.get(0).level;
			for(Folder f : ft) {
				if(f.level == level)
					topf = f;
				if(f.id == folderID)
					break;
			}
				
			for(OrderInfoData ofd : data)
				if(ofd.folder == topf) {
					topFolders.put(folderID, ofd);
					return ofd;
				}
		}
		return fdata;
	}

	@Override public int getCount() { return data.size(); }
	@Override public Object getItem(int arg0) { return data.get(arg0); }
	@Override public long getItemId(int arg0) { return arg0; }

	@Override
	public View getView(int arg0, View view, ViewGroup arg2) {
		if(view == null)
			view = View.inflate(context, R.layout.order_info_row, null);
		
		OrderInfoData od = (OrderInfoData)getItem(arg0);
		
		TextView tv = (TextView)view.findViewById(R.id.tvName);
		
		tv.setText(od.folder.name);
		
		String text = Util.IntToScaleStr(od.dal, 10, Util.DEC_DELIM, false) + " дал / ";
		text += Util.IntToScaleStr(od.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " руб.";
		tv = (TextView)view.findViewById(R.id.tvInfo);
		tv.setText(text);
		return view;
	}
}

class OrderInfoData implements Comparable<OrderInfoData> {
	public Folder folder;
	public long dal;
	public long sum;
	
	public void add(OrderItem oi, PriceEx price) {
		dal += ((long)price.volume * oi.qty / Consts.QTY_SCALE) / 100;
		sum +=  ((long)oi.cost * oi.qty / Consts.QTY_SCALE);
	}

	@Override
	public int compareTo(OrderInfoData arg0) {
		return folder.id = arg0.folder.id;
	}
	
	public void clear() {
		dal = 0;
		sum = 0;
	}
}


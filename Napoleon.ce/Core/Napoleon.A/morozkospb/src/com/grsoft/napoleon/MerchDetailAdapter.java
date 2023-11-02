package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.MerchFolder;
import com.grsoft.dataobjects.MerchItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.MerchImpl;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class MerchDetailAdapter extends BaseExpandableListAdapter {
	private final static int FOLDER_VIEW_TYPE = 0;
	private final static int PRICE_VIEW_TYPE = 1;
	private final static int TYPE_COUNT = 2;
	
	private Context context;
	
	public List<DataGroup> data = new ArrayList<DataGroup>();
	private LinesCountController linesController;
	
	static class DataGroup{
		public String title = "";
		public List<Item> items = new ArrayList<Item>();
	}
	
	abstract static class Item{
		public String id = "";
		public String title = "";
		abstract public int getViewType();
	}
	
	static class FolderItem extends Item{
		public int mine = 0;
		public int their = 0;
		
		@Override
		public int getViewType() {
			return FOLDER_VIEW_TYPE;
		}
	}
	
	static class PriceItem extends Item{
		public int qty = 0;
		public int system = 0;

		@Override
		public int getViewType() {
			return PRICE_VIEW_TYPE;
		}
	}
	
	public MerchDetailAdapter(Context context) {
		this.context = context;
	}
	
	public void refresh(Context context, MerchImpl doc) {
		data.clear();
		
		SQLiteDatabase db = DataBaseManager.getDataBase();
		
		if (doc.getData().folders.size() > 0) {
			DataGroup dg = new DataGroup();
			dg.title = context.getString(R.string.folders);
		
			SQLiteStatement stm = db.compileStatement(
					String.format("SELECT name FROM %s WHERE fid = ?", DataObjectInfo.getInstance().getTableName(Folder.class)));
			
			for(MerchFolder f : doc.getData().folders) {
				stm.bindString(1, f.id);
				
				FolderItem i = new FolderItem();
				i.id = f.id;
				i.title = stm.simpleQueryForString();
				i.mine = f.mine;
				i.their = f.their;
				
				dg.items.add(i);
			}
			
			stm.close();
			data.add(dg);
		}
		
		if (doc.getData().items.size() > 0) {
			DataGroup dg = new DataGroup();
			dg.title = context.getString(R.string.items);
			
			SQLiteStatement stm = db.compileStatement(
					String.format("SELECT name FROM %s WHERE id = ?", DataObjectInfo.getInstance().getTableName(Price.class)));
			
			for(MerchItem id : doc.getData().items) {
				stm.bindString(1, id.id);
				
				PriceItem i = new PriceItem();
				i.id = id.id;
				i.title = stm.simpleQueryForString();
				i.qty = id.qty;
				i.system = id.system;
				
				dg.items.add(i);
			}
			
			stm.close();
			data.add(dg);
		}
		
	}
	
	@Override
	public int getGroupCount() {
		return data.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return data.get(groupPosition).items.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return data.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return data.get(groupPosition).items.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}
	
	@Override
	public int getChildType(int groupPosition, int childPosition) {
		return ((Item)getChild(groupPosition, childPosition)).getViewType();
	}
	
	@Override
	public int getChildTypeCount() {
		return TYPE_COUNT;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {
		if (view == null)
			view = View.inflate(context, R.layout.merchgrouprow, null);
		
		DataGroup dg = (DataGroup) getGroup(groupPosition);
		TextView tv = (TextView) view.findViewById(R.id.tvName);
		tv.setText(dg.title);
		((ExpandableListView)parent).expandGroup(groupPosition);
		
		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view,
			ViewGroup parent) {
		int tp = getChildType(groupPosition, childPosition);
		
		switch (tp) {
		case PRICE_VIEW_TYPE:
			view = getPriceView(groupPosition, childPosition, view);
			break;
		case FOLDER_VIEW_TYPE:
			view = getFolderView(groupPosition, childPosition, view);
			break;
		}
		
		return view;
	}

	private View getFolderView(int groupPosition, int childPosition, View view) {
		if (view == null)
			view = View.inflate(context, R.layout.merchfolderrow, null);
		
		FolderItem f = (FolderItem) getChild(groupPosition, childPosition);
		TextView tv = (TextView) view.findViewById(R.id.tvName);
		tv.setText(f.title);
		
		if (linesController != null)
			linesController.prepareTextView(tv);
		
		tv = (TextView) view.findViewById(R.id.tvMine);
		tv.setText(Util.IntToScaleStr(f.mine, Consts.QTY_SCALE));
		
		tv = (TextView) view.findViewById(R.id.tvTheir);
		tv.setText(Util.IntToScaleStr(f.their, Consts.QTY_SCALE));
		
		return view;
	}

	private View getPriceView(int groupPosition, int childPosition, View view) {
		if (view == null)
			view = View.inflate(context, R.layout.merchpricerow, null);
		
		PriceItem f = (PriceItem) getChild(groupPosition, childPosition);
		TextView tv = (TextView) view.findViewById(R.id.tvName);
		tv.setText(f.title);
		
		if (linesController != null)
			linesController.prepareTextView(tv);
		
		tv = (TextView) view.findViewById(R.id.tvQty);
		tv.setText(Util.IntToScaleStr(f.qty, Consts.QTY_SCALE));
		
		tv = (TextView) view.findViewById(R.id.tvSystem);
		tv.setText(Util.IntToScaleStr(f.system, Consts.QTY_SCALE));
		
		return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void setLinesController(LinesCountController linesController) {
		this.linesController = linesController;
	}

}

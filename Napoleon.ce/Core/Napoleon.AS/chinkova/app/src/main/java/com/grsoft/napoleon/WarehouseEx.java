package com.grsoft.napoleon;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.FolderColor;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.FolderColorImpl;
import com.grsoft.dataobjects.impl.FolderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.TreeNodeCmp;
import com.grsoft.util.Util;
import com.grsoft.util.ZeroPositionFilter;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class WarehouseEx extends Warehouse{
	private List<String> redids = new ArrayList<String>();
	private List<String> blueids = new ArrayList<String>();
	
	FolderImpl folder = new FolderImpl();
	FolderColorImpl fcolor = new FolderColorImpl();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		lvItemSelect.setDividerHeight(1);
		FoldersAdapter.TreeNodeComparator = new GroupComparator(false);
	}
	
	@Override
	protected void onDestroy() {
		folder.close();
		fcolor.close();
		super.onDestroy();
	}
	
	@Override
	public boolean useInterlaceBackground() { return false; }
	
	@Override
	public View getFolderView(FolderTreeNode node, View convertView) {
		Folder fe = folder.getData();
		fe.id = node.id;
		folder.read();
		
		int color = Color.LTGRAY;
		FolderColor fc = fcolor.getData();
		fc.id = fe.fid;
		if(fcolor.read()) {
			if(fc.color != 0)
				color = fc.color;
		}
		
		View v = super.getFolderView(node, convertView);
		TextView tv;
		tv = (TextView)v.findViewById(R.id.tvItemSelectRowName);
		tv.setTextColor(Color.BLACK);
		v.setBackgroundColor(Util.GrServerColorToSystem(color));
		return v;
	}
	
	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		super.setTextColumnValue(textView, type, price);
		if(type == COLUMN_COST) {
			PriceEx pe = (PriceEx) price;
			if(pe.inCateg()) {
				textView.setBackgroundResource(R.drawable.category_one_row);
			} else {
				textView.setBackgroundColor(Color.WHITE);
			}
		}
	}
	
	@Override
	public void sortingPriceList(ArrayList<TreeNode> price) {
		GroupComparator gc = new GroupComparator(true);

		Collections.sort(price, gc);
		gc.close();
	}

	class GroupComparator extends TreeNodeCmp {
		boolean upReds = false;

		Map<Long, String> names = new HashMap<>();
		SQLiteStatement stmt;

		public GroupComparator(boolean upReds) {
			try {
				SQLiteDatabase db = DataBaseManager.getDataBase();
				stmt = db.compileStatement("select name from folder where id = (select folderid from price where rowid = ?)");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			this.upReds = upReds;
		}

		public void close() {
			if(stmt != null)
				stmt.close();
		}

		String getFolder(PriceTreeNode p) {
			if(stmt == null) return "";
			String f = names.get(p.getRowid());
			if(f == null) {
				stmt.clearBindings();
				stmt.bindLong(1, p.getRowid());
				try {
					f = stmt.simpleQueryForString();
				} catch (Exception e) {
//					e.printStackTrace();
				}
				if(f == null) f = "";
				names.put(p.getRowid(), f);
			}
			return f;
		}

		int compareFolderAndName (PriceTreeNode l, PriceTreeNode r) {
			String f1 = getFolder(l);
			String f2 = getFolder(r);
			int result = f1.compareTo(f2);
			if(result == 0)
				result = l.compareTo(r);

			return result;
		}

		@Override
		public int compare(TreeNode object1, TreeNode object2) {
			if (object1 instanceof PriceTreeNode && object2 instanceof PriceTreeNode) {
				PriceTreeNode l = (PriceTreeNode)object1;
				PriceTreeNode r = (PriceTreeNode)object2;
				if(upReds) {
					boolean lc = redids.contains(l.getId());
					boolean rc = redids.contains(r.getId());
					if(lc && !rc) return -1;
					if(rc && !lc) return 1;
					if(lc && rc) return compareFolderAndName(l, r);

					lc = blueids.contains(l.getId());
					rc = blueids.contains(r.getId());
					if(lc && !rc) return -1;
					if(rc && !lc) return 1;
				}

				return compareFolderAndName(l, r);
			} else
				return super.compare(object1, object2);
		}
	}

	@Override
	protected void adapterInit() {
		blueids.clear();
		redids.clear();
//		FoldersAdapter.resetCache();

		if (DocType.getCurDoc() == OrderDoc.instance()) {
			OrderEx data = new OrderEx();
			DbReader reader = new DbReader();
			Calendar cal =  Calendar.getInstance();
			cal.add(Calendar.MONTH, -1);
			long redTime = cal.getTime().getTime();
			StringBuilder where = new StringBuilder();
			where.append("created >= ").append(redTime)
				.append(" and id='").append(document.getId()).append("'"); 
			boolean bdo = reader.select(data, DataObjectInfo.getInstance()
					.getTableName(data.getClass()), where.toString());
			
			while(bdo){
				if(data.items != null && data.items.size() > 0)
					for(OrderItem i : data.items)
						if(!redids.contains(i.id))
							redids.add(i.id);
				bdo = reader.selectNext(data);
			}
			
			cal.add(Calendar.MONTH, -2);
			long blueTime = cal.getTime().getTime();
			where.setLength(0);
			where.append("created >= ").append(blueTime).append(" and created <= ").append(redTime)
				.append(" and id='").append(document.getId()).append("'");;
			bdo = reader.select(data, DataObjectInfo.getInstance()
					.getTableName(data.getClass()), where.toString());
			
			while(bdo){
				if(data.items != null && data.items.size() >0)
					for(OrderItem i : data.items)
						if(!redids.contains(i.id) && !blueids.contains(i.id))
							blueids.add(i.id);
				
				bdo = reader.selectNext(data);
			}
			reader.close();
		}

		if(document.getRowid() != ExtrasConst.INVALID_ROWID) {
			OrgImpl org = new OrgImpl();
			org.getData().id = document.getId();
			org.read();
			org.close();
			String firmid = ((OrgEx)org.getData()).orgid;

			adapter.putFilter(new OrgPriceFilter(firmid));
		}
		
		super.adapterInit();
	}

	@Override
	protected int getItemLayoutId() { return R.layout.priceitemrowex; }
	
	@Override
	protected void updateChildPriceView(View view, Price p) {
		super.updateChildPriceView(view, p);
		
		((TextView)view.findViewById(R.id.tvPackName)).setText(((PriceEx)p).packName);
		((TextView)view.findViewById(R.id.tvArticle)).setText(((PriceEx)p).article);
	}
	
	@Override
	public void setColor(TextView textView, Price price) {
		super.setColor(textView, price);
		
		if (document != null && ((Itemsable) document).findItem(price.id) != null)
			textView.setTextColor(getResources().getColor(((Itemsable) document).getItemColor()));
		else if(redids.contains(price.id))
			textView.setTextColor(getResources().getColor(R.color.red));
		else if (blueids.contains(price.id))
			textView.setTextColor(getResources().getColor(R.color.blue));
		
		if( price.color != 0 )
			textView.setTextColor(Util.GrServerColorToSystem(price.color));
	}
	
	class OrgPriceFilter extends Filter {
		String orgid;
		
		public OrgPriceFilter(String orgid) {
			super("orgid" + orgid);
			this.orgid = ";" + orgid + ";";
		}
		
		@Override
		public boolean inset(long priceRowID, String id) {
			price.read(priceRowID);
			PriceEx pe = (PriceEx) price.getData();
			String prOrg = ';' + pe.orgid + ';'; 
			return prOrg.contains(orgid);
		}
	}
	
	@Override
	protected Filter createZeroPositionFilter() {
		return new ZeroFilter();
	}
	
	class ZeroFilter extends ZeroPositionFilter {
		
		@Override public String getWhereStr() { return ""; }
		
		@Override
		public boolean inset(long priceRowID, String id) {
			if( !(document instanceof Itemsable) )
				return super.inset(priceRowID, id);
			
			boolean result = false; 			
			if(price.read(priceRowID))
				result = (((Itemsable)document).getItemValue(price.getData()) > 0);			
			return result;
		}
	}
	
}

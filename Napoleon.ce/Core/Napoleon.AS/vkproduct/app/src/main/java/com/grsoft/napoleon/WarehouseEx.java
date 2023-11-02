package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
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

public class WarehouseEx extends Warehouse {
	private List<String> redids = new ArrayList<String>();
	private List<String> blueids = new ArrayList<String>();
	private String orgid = "";
	
	FolderImpl folder = new FolderImpl();
	FolderColorImpl fcolor = new FolderColorImpl();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		OrgImpl org = new OrgImpl();
		org.getData().id = document.getId();
		org.read();
		org.close();
		orgid = ((OrgEx)org.getData()).orgid;
		
		FoldersAdapter.TreeNodeComparator = new GroupComparator();
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
	
	
//	@Override
//	public View getFolderView(FolderTreeNode node, View convertView) {
//		Folder fe = folder.getData();
//		fe.id = node.id;
//		folder.read();
//		
//		int color = Color.BLACK;
//		FolderColor fc = fcolor.getData();
//		fc.id = fe.fid;
//		if(fcolor.read())
//			color = fc.color;
//		
//		View v = super.getFolderView(node, convertView);
//		TextView tv;
//		tv = (TextView)v.findViewById(R.id.tvItemSelectRowName);
//		tv.setTextColor(Util.GrServerColorToSystem(color));
//		return v;
//	}

	class GroupComparator extends TreeNodeCmp {
		@Override
		public int compare(TreeNode object1, TreeNode object2) {
			if (object1 instanceof PriceTreeNode
					&& object2 instanceof PriceTreeNode) {
				int result = 0;

				Cursor lhs = null;
				Cursor rhs = null;

				try {
					SQLiteDatabase db = DataBaseManager.getDataBase();
					lhs = db.rawQuery(
							"select name from folder where id = (select folderid from price where rowid = ?)",
							new String[] { Long
									.toString(((PriceTreeNode) object1)
											.getRowid()) });

					rhs = db.rawQuery(
							"select name from folder where id = (select folderid from price where rowid = ?)",
							new String[] { Long
									.toString(((PriceTreeNode) object2)
											.getRowid()) });

					if (lhs.moveToFirst() && rhs.moveToFirst()) {
						result = lhs.getString(lhs.getColumnIndex("name"))
								.compareTo(
										rhs.getString(rhs
												.getColumnIndex("name")));

						if (result == 0)
							result = super.compare(object1, object2);

					}
					return result;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (lhs != null)
						lhs.close();
					if (rhs != null)
						rhs.close();
				}

				return result;
			} else
				return super.compare(object1, object2);
		}
	}

	@Override
	protected void adapterInit() {
		blueids.clear();
		redids.clear();
		FoldersAdapter.resetCache();

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

		if(document.getRowid() != ExtrasConst.INVALID_ROWID)
			adapter.putFilter(createOrgidFilter());
		
		super.adapterInit();
	}

	public Filter createOrgidFilter() {
		return new Filter("orgid") {
			@Override
			public boolean inset(long priceRowID, String id) {
				price.read(priceRowID);
				PriceEx pe = (PriceEx) price.getData();
				final String DELIM = ";";
				String[] par = pe.orgid.trim().split(DELIM);
				
				return pe.orgid.length() == 0 || contains(par, orgid);
			}
		};
	}
	
	private boolean contains(String[] arr, String id){
		boolean result = false;
		
		for(int i = 0; i < arr.length; i ++)
			if(arr[i].equals(id)){
				result = true;
				break;
			}
		
		return result;
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
			textView.setTextColor(((Itemsable) document).getItemColor());
		else if(redids.contains(price.id))
			textView.setTextColor(getResources().getColor(R.color.red));
		else if (blueids.contains(price.id))
			textView.setTextColor(getResources().getColor(R.color.blue));
		
		if( price.color != 0 )
			textView.setTextColor(Util.GrServerColorToSystem(price.color));
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

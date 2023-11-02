package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.Accounts;
import com.grsoft.dataobjects.CostTypes;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.OrgMatrixItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.WHCost;
import com.grsoft.dataobjects.impl.AccountsImpl;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.FocusedItemsTCImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgMatrixImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixItemsAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends WarehouseNew {
	protected static final int SELECT_TAX_TYPE = 0x1000;
	private static final String ORG_MATRIX = "Ассортимент точки";
	PriceImpl filterData = new PriceImpl();
	HashSet<String> notRecommend = new HashSet<String>();
	
	MatrixItemsAdapter orgMatrix = null;
	
	TypeAdapter typeAdapter;
	
	public String taxType() { return (document instanceof OrderImpl) ? ((OrderEx)document.getData()).taxType : ""; }
	
	@Override protected int getLayoutId() { return R.layout.warehouseex; }
	
	@Override
	protected void loadLastBuyingItems(String orgId) {
		if(DocType.getCurDoc() == OrderDoc.instance()){
			HashSet<String> ids = new HashSet<String>();

			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, -30);
			Date end = c.getTime();
			c.add(Calendar.DATE, -30);
			Date begin = c.getTime();
			
			DatePeriod dp = new DatePeriod(begin, end);
			dp.periodType = DatePeriod.DATE;			
			DocList dl = DeliveryDoc.instance().docList(orgId, "", dp);
			for(Document<?> doc : dl) {
				for(DeliveryItem ditem : ((DeliveryImpl)doc).getData().items) {
					ids.add(ditem.id);
				}
			}
			dl.close();
			
			begin = end;
			end = new Date();
			dp = new DatePeriod(begin, end);
			dp.periodType = DatePeriod.DATE;
			
			dl = DeliveryDoc.instance().docList(orgId, "", dp);
			for(Document<?> doc : dl) {
				for(DeliveryItem ditem : ((DeliveryImpl)doc).getData().items) {
					ids.remove(ditem.id);
				}
			}
			dl.close();
			
			if(document instanceof OrderImplEx) {
				for(OrderItem oi : ((OrderImplEx)document).getData().items)
					ids.remove(oi.id);
			}

			lastBuyingItems.clear();
			lastBuyingItems.addAll(ids);
		} else
			super.loadLastBuyingItems(orgId);
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		if( starting ) {
			OrgImpl oi = new OrgImpl();
			OrgEx o = (OrgEx)oi.getData();
			o.id = orgid;
			oi.read();
			oi.close();
			
			FocusedItemsTCImpl.loadItems(notRecommend, o.orgType, false);

			if( document instanceof OrderImplEx && document.getRowid() != ExtrasConst.INVALID_ID ) {
				OrgMatrixImpl om = new OrgMatrixImpl();
				OrgMatrix mtx = om.getData();
				mtx.id = ((OrderEx)document.getData()).account;
				mtx.ida = document.getId(); 
				if( om.read() && mtx.items.size() > 0 )
					orgMatrix = new OrgMatrixAdapter(this, mtx.items, orgid);
				om.close();
	
				if( orgMatrix != null ) {
					matrixName = ORG_MATRIX;
					return orgMatrix;
				}
			}
		}
		
		Adapter a = new Adapter(this);
		a.clearCache();
		return a;
	}
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		if( orgMatrix != null )
			items.add(1, ORG_MATRIX);
		return items;
	}
	
	@Override
	protected void applayMatrix(String matrixName) {
		if( matrixName.equals(ORG_MATRIX)) {
			this.matrixName = matrixName;
			applayAdapter(orgMatrix);
			return;
		}
		super.applayMatrix(matrixName);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if( document instanceof OrderImpl ) {
			((OrderImpl)document).setUpdateQtyHandler(new OrderImplBase.UpdateQtyHandler() {
				@Override
				public void itemUpdated(OrderItem item, Order order, boolean isNew) {
					((OrderItemEx)item).taxType = ((OrderEx)order).taxType;
				}
			});
			
			findViewById(R.id.btnSelectTaxType).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) { showDialog(SELECT_TAX_TYPE); }
			});
		}
		
		findViewById(R.id.btnPresentation).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Presentation.open(WarehouseEx.this, document.getRowid(), adapter.getFoldersIds(), adapter.getFolderTop().id);
				finish();
			}
		});
	}
	
	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		Itemsable id = (Itemsable)document;
		int value = 0;
		int scale = Consts.QTY_SCALE;
		boolean showInPack = false;

		switch(type){
		case COLUMN_QTY_WH:
			value = id.getItemValue(price);
			break;
		case COLUMN_QTY_ORD:
			showInPack = ((CfgNpl)ConfigManager.getConfig()).isPackView;
			value = id.getItemQty(price);
			break;
		case COLUMN_QTY_WH_ORD:
			value = id.getItemQty(price);
			if( value == 0 ) {
				value = id.getItemValue(price);
			} else
				showInPack = ((CfgNpl)ConfigManager.getConfig()).isPackView;
			break;
			
		default:
			super.setTextColumnValue(textView, type, price);
			return;
		}
		
		if( showInPack ) {
			int inPack = price.qtyInPack;
			if( inPack == 0 )
				inPack = Consts.QTY_SCALE;
			int qty = (int)((long)value * Consts.QTY_SCALE / inPack);
			textView.setText(Util.IntToScaleStr(qty, Consts.QTY_SCALE) + " у.");
		} else
			textView.setText(Util.IntToScaleStr(value, scale, Util.DEC_DELIM, (scale == Consts.QTY_SCALE)));
	}
	
	@Override
	public void setColor(TextView textView, Price price) {
		if( Features.PACK_INPUT && packQty!= null && packQty.contains(price.id))
			textView.setTextColor(Color.BLUE);
		else 
			super.setColor(textView, price);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == SELECT_TAX_TYPE ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle(R.string.select_tax);
			if( typeAdapter == null )
				typeAdapter = new TypeAdapter();

			b.setSingleChoiceItems(typeAdapter, -1, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int index) {
					CostTypes ct = (CostTypes)typeAdapter.getItem(index);
					OrderEx order = ((OrderEx)document.getData());
					order.taxType = ct.id;
					order.sumType = index;
					if( document.getRowid() != ExtrasConst.INVALID_ID )
						document.write();
					
					if( adapter instanceof Adapter ) {
						((Adapter)adapter).clearCache();
						adapter.setFolder(-1);
						adapter.buildSet();
					} else if ( adapter instanceof OrgMatrixAdapter ) {
						((OrgMatrixAdapter)adapter).clearCache();
						adapter.buildSet();
					}
					arg0.dismiss();
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected int getItemLayoutId() { return  R.layout.priceitemrowex; }
	
	@Override
	protected void onStop() {
		filterData.close();
		super.onStop();
	}
	
//	@Override
//	protected BaseAdapter createListAdapter() {
//		WarehouseAdapter ret = (WarehouseAdapter)new Adapter(this); 
//		if(document instanceof OrderImpl)
//			ret.putFilter(new ZeroCostFilter());
//		return ret;
//	}
	
	protected void setItemBackColor(int arg0, TreeNode node, View view) {
		int rowback = arg0 % 2 != 0 ? 
				R.drawable.even_row_selector :
				R.drawable.list_selector;
		
		if( !node.isFolderNode() ) {
			if( notRecommend.contains(price.getData().id) )
				rowback = R.drawable.bad_row_selector;
			else if( price.getData().qty <= 0 )
				rowback = R.drawable.below_zero_row_selector;
		}
		view.setBackgroundResource(rowback);
	}

	class ZeroCostFilter extends Filter {
		
		CostStrategy cs;
		
		@Override
		public String getName() { return ZeroPositionFilter.NAME; }
		
		@SuppressWarnings("unchecked")
		public ZeroCostFilter() {
			super("");
			cs = CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass());
		}
		
		@Override
		public boolean inset(long priceRowID, String id) {
			boolean ret = false;
			if( filterData.read(priceRowID) )
				ret = (cs.getItemCost(filterData.getData(), document) != 0);
			return ret;
		}
		
	}
	
	class Adapter extends FoldersAdapter {

		public Adapter(WarehouseNew warehouse) {
			super(warehouse);
		}
		
		void clearCache() {
			globalPrice = null;
			globalRoot = null;
		}
		
		@Override
		protected void fillPriceIds(SQLiteDatabase database) {
			if(!(document instanceof OrderImpl))
				super.fillPriceIds(database);
			else
			{
				fprice.clear();

				String priceTable = DataObjectInfo.getInstance().getTableName(Price.class);
				String costTable = DataObjectInfo.getInstance().getTableName(WHCost.class);

				if( !DbWriter.isTableExists(priceTable) || !DbWriter.isTableExists(costTable) )
					return;
				
				String tax = ((OrderEx)document.getData()).taxType;
				String str = getWhereStr();
				if( str.length() > 0 )
					str += " AND ";
				str += "idc='" + tax + "' and whcost.cost > 0";
				String stmt = "select folderID, price.rowid, name, price.id from " + priceTable + " inner join " + 
						costTable + " on price.id = whcost.id where " + str;
				Cursor prcCursor = null;
				try{
					prcCursor = database.rawQuery(stmt, null);
					while( prcCursor.moveToNext() ) {
						long rowid = prcCursor.getLong(1);
						String id = prcCursor.getString(3);
						
						if( !inset( rowid, id ) )
							continue;
						
						int folderid = prcCursor.getInt(0);
						
						if(!fprice.containsKey(folderid))
							fprice.put(folderid, new ArrayList<PriceInfo>());
						
						PriceInfo pi = new PriceInfo(rowid, prcCursor.getString(2), id);
						fprice.get(folderid).add(pi);
					}
				} catch(Exception e) { 
					e.printStackTrace();
				} finally {
					if( prcCursor != null )
						prcCursor.close();
				}
			}
		}
		
		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			TreeNode node = (TreeNode) getItem(arg0);
			View view = node.isFolderNode()
					? warehouse.getFolderView((FolderTreeNode)node, convertView)
					: warehouse.getPriceView((PriceTreeNode)node, convertView);
			
			if (view != null)
				setItemBackColor(arg0, node, view);
			
			return view;
		}
	}
	
	class TypeAdapter extends BaseAdapter {

		ArrayList<CostTypes> values = new ArrayList<CostTypes>();
		
		public TypeAdapter() {
			CostTypes ct = new CostTypes();
			DbReader r = new DbReader();
			String table = DataObjectInfo.getInstance().getTableName(ct.getClass());
			
			OrderEx oe = (OrderEx)document.getData();
//			String taxType = (document instanceof OrderImpl) ? oe.taxType : "";
			
			AccountsImpl ai = new AccountsImpl();
			Accounts a = ai.getData();
			a.type = oe.account;
			a.ido = oe.ido;
			
			String accType = "";
			if( ai.read() )
				accType = a.taxType;
			ai.close();
			
			boolean bdo = r.select(ct, table, "", "name");
			while( bdo ) {
				if( ct.id.equals(accType) || ct.userid.length() == 0 ) {
					values.add(ct);
					ct = new CostTypes();
				}
				bdo = r.selectNext(ct);
			}
			r.close();
		}
		
		@Override
		public int getCount() {
			return values.size();
		}

		@Override
		public Object getItem(int arg0) {
			return (arg0 < values.size()) ? values.get(arg0) : null;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if( view == null )
				view = View.inflate(WarehouseEx.this, R.layout.org_type_item, null);
			CostTypes ct = (CostTypes)getItem(arg0);
			if( ct != null ) {
				TextView tv;
				
				tv = (TextView)view.findViewById(R.id.tvName);
				tv.setText(ct.name);
			}
			return view;
		}
		
	}
}

class OrgMatrixAdapter extends MatrixItemsAdapter {

	Hashtable<Long, Integer> itemData = new Hashtable<Long, Integer>(); 
	
	public OrgMatrixAdapter(WarehouseNew warehouse, List<? extends MatrixItem> items, String name) {
		super(warehouse, items, name);
	}
	
	public void clearCache() {
		globalPrice = null;
		globalRoot = null;
	}
	
	@Override
	protected void makeStmt(StringBuilder sql, String priceTable) {
		String costTable = DataObjectInfo.getInstance().getTableName(WHCost.class);
		super.makeStmt(sql, priceTable + " inner join " + costTable + " whcost on price.id = whcost.id");
	}
	
	@Override
	public String getWhereStr() {
		String str = super.getWhereStr(); 
		String tax = ((WarehouseEx)warehouse).taxType();
		if( str.length() > 0 )
			str += " AND";
		str += " whcost.idc='" + tax + "' and whcost.cost > 0";
		return str;
	}
	
	@Override
	protected void addPriceInfo(long rowid, int folderid, String name, String id) {
		super.addPriceInfo(rowid, folderid, name, id);
		for(MatrixItem mi : getMatrixItems()) {
			if( mi.id.equals(id)) {
				itemData.put(rowid, ((OrgMatrixItem)mi).qty);
			}
		}
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		TreeNode node = (TreeNode) getItem(arg0);
		View view = node.isFolderNode()
				? warehouse.getFolderView((FolderTreeNode)node, convertView)
				: warehouse.getPriceView((PriceTreeNode)node, convertView);
		
		if (view != null) {
			((WarehouseEx)warehouse).setItemBackColor(arg0, node, view);
			if(!node.isFolderNode()) {
				TextView tv = (TextView) view.findViewById(R.id.tvMatrixQty);
				Integer q = itemData.get(node.getRowid());
				tv.setText((q != null) ? Util.IntToScaleStr(q, Consts.QTY_SCALE) : "");
				tv.setVisibility(View.VISIBLE);
			}
		}
		
		return view;
	}
}


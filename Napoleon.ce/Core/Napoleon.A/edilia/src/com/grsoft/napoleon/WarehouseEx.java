package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnFolders;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixBaseAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseAdapter;
import com.grsoft.util.WarehouseManager;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WarehouseEx extends WarehouseNew {
	
	static boolean isRetPrice = false;
	
//	static final String TOP_PRICE_MATRIX = "<ТОП Ассортимент>"; 
	
	HashMap<String, Integer> lastPrice = new HashMap<String, Integer>();
//	List<MatrixItem> topPrice = new ArrayList<MatrixItem>();
//	Boolean showTop;
	
//	static public void openTopMatrix(Context context,  Document<?> doc) {
//		Intent i = new Intent(context, WarehouseEx.class);
//		
//		if( doc != null ) {
//			i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
//			i.putExtra(ExtrasConst.ORG_ID_STR, doc.getId());
//			i.putExtra(ExtrasConst.EDIT_MODE_STR, true);
//			i.putExtra(TOP_PRICE_MATRIX, TOP_PRICE_MATRIX);
//		}
//		context.startActivity(i);		
//	}
	
	
	@Override
	protected void loadLastBuyingItems(String orgId) {
		super.loadLastBuyingItems(orgId);
		
		Date end = Util.getDayEnd(Util.getDate());
		Calendar c = Calendar.getInstance();
		c.setTime(end);
		c.add(Calendar.MONTH, -1);
		
		DatePeriod dp = new DatePeriod(Util.getDayStart(c.getTime()), end);
		DocList dl = OrderDoc.instance().docList(orgId, "created desc", dp);
		for(Document<?> d : dl) {
			if(d.getRowid() == docRowId)
				continue;
			
			for(OrderItem oi : ((OrderImpl)d).getData().items)
				if(lastPrice.containsKey(oi.id) == false) {
					lastPrice.put(oi.id, oi.cost);
				}
		}
		
		dl.close();
	}
	
//	@Override
//	public void sortingPriceList(ArrayList<TreeNode> price) {
//		if(adapter instanceof TopListMatrix) {
//			Comparator<? super TreeNode> cmp = new MatrixOrderComparer(((TopListMatrix)adapter).getMatrix());
//			Collections.sort(price, cmp);
//		} else
//			super.sortingPriceList(price);
//	}
	
//	@Override
//	public void setColor(TextView textView, Price price) {
//		if(document != null && ((Itemsable)document).findItem(price.id) == null) {
//			for(MatrixItem mi : topPrice) 
//				if(mi.id.equals(price.id)){
//					textView.setTextColor(0xFF21007F);
//					return;
//				}
//		}
//		super.setColor(textView, price);
//	}
	
//	@Override
//	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
//		ArrayList<String> ret = super.prepareMatrixList(items);
//		ret.add(TOP_PRICE_MATRIX);
//		return ret;
//	}
//	
//	@Override
//	protected boolean inheritedApplayMatrix(String matrixName) {
//		if(matrixName.equals(TOP_PRICE_MATRIX)) {
//			FoldersAdapter.resetCache();
//			applayAdapter(new TopListMatrix(topPrice, this));
//			return true;
//		}
//		return false;
//	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		Bundle b = (savedInstanceState == null) ? getIntent().getExtras(): savedInstanceState;

//		showTop = (b != null && b.getString(TOP_PRICE_MATRIX) != null);
		super.onCreate(savedInstanceState);
//		if(!showTop && document instanceof OrderImplEx)
		if(!editMode && document instanceof OrderImplEx)
			showDialog(DLG_MATRIX);
//		showTop = false;
	}
	
	@Override
	protected void applyAdapter(WarehouseAdapter newadapter, boolean expanded) {
		super.applyAdapter(newadapter, (newadapter instanceof MatrixBaseAdapter));
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		if( document instanceof ReturnImplEx) {
			isRetPrice = true;
			return new ReturnAdapter(this, (Return)document.getData());
		}
		if(isRetPrice) {
			isRetPrice = false;
			FoldersAdapter.resetCache();
		}
//		if(document != null)
//			topPrice = PriceTop.get(document.getId());
//		if(showTop) {
//			FoldersAdapter.resetCache();
//			FoldersAdapter ret =  new TopListMatrix(topPrice, this);
//			ret.setExpanded(true);
//			return ret;
//		}
		return super.createListAdapter();
	}
	
	@Override
	int getWhQty(Itemsable id, Price p) {
		int qty = id.getItemValue(p);
		int qip = p.qtyInPack;
		if(qip == 0 )
			qip = Consts.QTY_SCALE;
		if(((PriceEx)p).boxed == 0)
			qty = (int)((long)qty * Consts.QTY_SCALE / qip);
			
		return qty;
	}
	
	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		super.setTextColumnValue(textView, type, price);
		if( type == COLUMN_COST ) {
			int changes = 0;//((PriceEx)price).changes;
			if( lastPrice.containsKey(price.id) )
				changes = getCost(price) - lastPrice.get(price.id);

//			textView.setBackgroundColor(changes == 0 ? Color.TRANSPARENT : changes > 0 ? Color.GREEN : Color.RED );
			textView.setTextColor(changes == 0 ? Color.BLACK : changes > 0 ? 0xFF006400 : Color.RED );
			Drawable d = changes == 0 ? null : getResources().getDrawable(changes > 0 ? R.drawable.prc_up : R.drawable.prc_down );
			textView.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
		}
	}
	
	@Override protected boolean useInpackInPacking() { return false; }
	
	@Override
	protected void changeQty(int value, boolean inPack) {
		PriceImpl p = new PriceImpl();
		PriceEx prc = (PriceEx)p.getData();
		OrderImplBase<?> o = (OrderImplBase<?>) document;
		
		@SuppressWarnings("unchecked")
		CostStrategy cs = CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass());
		for (String e : packQty) {
			prc.id = e;
			if (p.read()) {
				int cv = value;
				inPack = prc.boxed == 0;
				if (inPack)
					cv = (int) ((long) value * p.getData().qtyInPack) / Consts.QTY_SCALE;
				o.updateQty(p, cv, cs.getItemCost(prc, o), inPack);
			}
		}
		p.close();
		packQty.clear();
		updateTotalSum();
		notifyDataSetChanged();
	}
	
	@Override
	public void onBackPressed() {
		if( !editMode && document instanceof OrderImplEx ) {
			OrderImpl.OrderEditor.edit(this, (OrderImpl) document, editMode);
			finish();
			return;
		}
		super.onBackPressed();
	}
}

class ReturnAdapter extends FoldersAdapter
{
	HashSet<Integer> allowed = new HashSet<Integer>();  
	HashSet<String> ids = new HashSet<String>();
	
	public ReturnAdapter(WarehouseManager warehouse, Return document) {
		super(warehouse);
		
		
		FoldersAdapter.resetCache();
		
		String orgId = document.id;
		List<Return> rdocs = OrderHelper.loadRetDocs(orgId, (Return)document);
				
		try {
			String ft = (new Folder()).getTableName();
			String rf = (new ReturnFolders()).getTableName();

			String sql = "select id from " + ft + ", " + rf + " where " + ft + ".fid = " + rf + ".fid";
			Cursor c = DataBaseManager.getDataBase().rawQuery(sql, null);
			while(c.moveToNext()) {
				allowed.add(c.getInt(0));
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		DocList dl = DeliveryDoc.instance().docList(orgId);
		for(Document<?> d : dl) {
			Delivery ddoc = (Delivery)d.getData(); 
			for(DeliveryItem item : ddoc.items) {
				int rq = OrderHelper.countRetQty(ddoc, (DeliveryItemEx)item, rdocs); 
				if(rq > 0)
					ids.add(item.id);
			}
		}
		dl.close();
	}
	
	@Override
	public boolean inset(long rowid, String id, int folder) {
		return ids.contains(id) && (allowed.contains(folder) || allowed.size() == 0);
	}
	
}

//class TopListMatrix extends MatrixBaseAdapter {
//	List<MatrixItem> tops;
//	Matrix m = new Matrix();
//	
//	public TopListMatrix(List<MatrixItem> tops, WarehouseNewW warehouse) {
//		super(warehouse);
//		this.tops = tops;
//		m.name = WarehouseEx.TOP_PRICE_MATRIX;
//		m.items = tops;
//	}
//	
//	public Matrix getMatrix() { return m; }
//
//	@Override protected List<? extends MatrixItem> getMatrixItems() { return tops; }
//	
//	@Override
//	protected void sortFullTree(TreeNode node) {
//		Comparator<TreeNode> sv = TreeNodeComparator;
//		TreeNodeComparator = new MatrixOrderComparer(m);
//		
//		super.sortFullTree(node);
//		
//		TreeNodeComparator = sv;
//	}
//	
//}

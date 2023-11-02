package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.MatrixItemEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixBaseAdapter;
import com.grsoft.util.TreeNodeCmp;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseAdapter;

public class WarehouseEx extends WarehouseNew {
	HashSet<String> missPrice = new HashSet<String>();
	PriceImpl pi = new PriceImpl();
	List<MatrixItemEx> spectask = new ArrayList<MatrixItemEx>();
	List<MatrixItemEx> orgmtx = new ArrayList<MatrixItemEx>();
	HashMap<String, Integer> orgpos = new HashMap<String, Integer>();  
	
	private int curMatrixIdx = 0;
	private List<String> MATRIX_ARR = new ArrayList<String>();
	private boolean showPrice = false;
	private boolean showAssortMtx = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		FoldersAdapter.TreeNodeComparator = new NodeComparer();
		super.onCreate(savedInstanceState);
		
		((TextView) findViewById(R.id.tvMatrixName)).setText(matrixName);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.warehouseex;
	}
	
	protected int getOptionsMenuId() {
		return R.menu.warehouse_opt_menuex;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		pi.close();
	}

	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		if (spectask != null && spectask.size() > 0)
			items.add(getString(R.string.spectaskclient));

		if (orgmtx != null && orgmtx.size() > 0)
			items.add(getString(R.string.clientprice));

		if (!showPrice)
			items.remove(PRICE_WITHOUT_MATRIX);
		
		if(!showAssortMtx)
			items.remove(AssortmentMatrixAdapter.TITLE);
		return items;
	}

	@Override public boolean isPriceExpand() { return false; }
	
	@Override
	protected BaseAdapter createListAdapter() {
		OrgImpl oi = new OrgImpl();
		Org org = oi.getData();
		org.id = document.getId();
		oi.read();
		oi.close();

		OrgEx oe = (OrgEx) org;

		MATRIX_ARR.clear();
		
		OrgMatrixAdapter ret = null;
		if (oe.matrix.size() > 0) {
			for (MatrixItemEx mie : oe.matrix)
				if (mie.spectask == 0){
					orgmtx.add(mie);					
					if(!orgpos.containsKey(mie.id))
						orgpos.put(mie.id, mie.pos);
				}else
					spectask.add(mie);

			if( orgmtx.size() > 0 ) {
				matrixName = getString(R.string.clientprice);
				MATRIX_ARR.add(0, matrixName);
				ret = new OrgMatrixAdapter(this, orgmtx, matrixName);
			}
			if( spectask.size() > 0 ) {
				matrixName = getString(R.string.spectaskclient);
				MATRIX_ARR.add(0, matrixName);
				ret = new OrgMatrixAdapter(this, spectask, matrixName);
			}
		}
		
		if(docRowId != ExtrasConst.INVALID_ROWID){
			ConfigImpl cfg = new ConfigImpl();
			StringBuilder value = new StringBuilder();
			
			if (cfg.getValue(value, "ShowPrice"))
				showPrice = Boolean.parseBoolean(value.toString());
			
			value.setLength(0);
			if (cfg.getValue(value, "ShowAssortMtx"))
				showAssortMtx = Boolean.parseBoolean(value.toString());
		}else{
			showPrice = true;
			showAssortMtx = true;
		}
		
		if( orgmtx.size() == 0 && spectask.size() == 0 )
			showPrice = true;

		if( showPrice )
			MATRIX_ARR.add(PRICE_WITHOUT_MATRIX);
		return (ret == null) ? super.createListAdapter() : ret;
	}
	
	@Override
	protected void applayMatrix(String matrixName) {
		if (matrixName.equals(getString(R.string.clientprice))){
			this.matrixName = matrixName; 
			applyAdapter(new OrgMatrixAdapter(this, orgmtx, matrixName), adapter.isExpanded());
		}else if (matrixName.equals(getString(R.string.spectaskclient))){
			this.matrixName = matrixName;
			applyAdapter(new OrgMatrixAdapter(this, spectask, matrixName), adapter.isExpanded());
		}else
			super.applayMatrix(matrixName);
		
		((TextView) findViewById(R.id.tvMatrixName)).setText(matrixName);
	}

	@Override
	protected void resetMatrix() {
		WarehouseAdapter newAdapter = null;
		if( showPrice ) {
			matrixName = PRICE_WITHOUT_MATRIX;
			newAdapter = (WarehouseAdapter) super.createListAdapter();			
		} else {
			matrixName = getString(R.string.clientprice);
			newAdapter = new OrgMatrixAdapter(this, orgmtx, matrixName);
		}
		applayAdapter(newAdapter);
		((TextView) findViewById(R.id.tvMatrixName)).setText(matrixName);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if( !super.onPrepareOptionsMenu(menu) )
			return false;

		MenuItem nextPrice = menu.findItem(R.id.itNextPrice); 
		if(MATRIX_ARR.size() > 1 )
			nextPrice.setTitle((curMatrixIdx >= MATRIX_ARR.size() - 1) ? "Назад" : "Вперед");
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);

		for( int id : new int[] { R.id.itMatrix, R.id.itColumns, R.id.itExpand } ) {
			MenuItem mi = menu.findItem(id);
			if( mi != null )
				mi.setVisible(false);
		}

		MenuItem nextPrice = menu.findItem(R.id.itNextPrice); 
		if(docRowId == ExtrasConst.INVALID_ROWID || MATRIX_ARR.size() == 1 ){
			nextPrice.setVisible(false);
		} 
			
		return result;
	}
	
	class NodeComparer extends TreeNodeCmp {
		@Override
		public int compare(TreeNode object1, TreeNode object2) {
			if (object1 instanceof PriceTreeNode && object2 instanceof PriceTreeNode) {
				
				if(matrixName.equals(getString(R.string.spectaskclient))){
					PriceEx pe = (PriceEx) price.getData();
					price.read(((PriceTreeNode) object1).getRowid());
					String leftName = pe.name;
					price.read(((PriceTreeNode) object2).getRowid());
					String rightName = pe.name;
					return leftName.compareTo(rightName);
				}else if(matrixName.equals(getString(R.string.clientprice))){
					PriceEx pe = (PriceEx) price.getData();
					price.read(((PriceTreeNode) object1).getRowid());
					
					int leftPos = 0;
					
					if(orgpos.containsKey(pe.id))
						leftPos = orgpos.get(pe.id);
					
					price.read(((PriceTreeNode) object2).getRowid());
					int rightPos = 0;
					
					if(orgpos.containsKey(pe.id))
						rightPos = orgpos.get(pe.id);
					return leftPos - rightPos;
				}
					
				PriceEx pe = (PriceEx) price.getData();
				price.read(((PriceTreeNode) object1).getRowid());
				int id = pe.sortOrder;
				price.read(((PriceTreeNode) object2).getRowid());
				return id - pe.sortOrder;
			}
			return super.compare(object1, object2);
		}
	}

	public void overview() {
		missPrice.clear();
		OrderDoc orderType = (OrderDoc) OrderDoc.instance();

		if (document != null && DocType.getCurDoc() == orderType) {
			final int ALL_RANGE_MONTH = 2;
			final int ORDER_RANGE_DAYS = 14;
			final String RANGE = "Range";
			final String ORDRNG = "OrdRng";
			int allRangeMonth = ALL_RANGE_MONTH;
			int orderRangeDays = ORDER_RANGE_DAYS;

			try {
				StringBuilder value = new StringBuilder();
				ConfigImpl cfg = new ConfigImpl();

				if (cfg.getValue(value, RANGE))
					allRangeMonth = Integer.parseInt(value.toString());

				value.setLength(0);
				if (cfg.getValue(value, ORDRNG))
					orderRangeDays = Integer.parseInt(value.toString()) * 7;

			} catch (Exception e) {
				e.printStackTrace();
			}

			Calendar cal = Calendar.getInstance();
			cal.setTime(Util.getDate());
			cal.add(Calendar.DAY_OF_MONTH, 1);
			Date finish = cal.getTime();
			cal.add(Calendar.MONTH, -allRangeMonth);
			Date start = cal.getTime();
			DatePeriod dp = new DatePeriod(start, finish);

			com.grsoft.napoleon.documents.DocList list = orderType.docList(
					document.getId(), "created", dp);

			for (int i = 0; i < list.getCount(); i++) {
				Order ord = (Order) list.get(i).getData();

				if (ord.items != null)
					if (DatePeriod.daysDiff(ord.created, finish) > orderRangeDays) {
						for (OrderItem item : ord.items)
							missPrice.add(item.id);
					} else
						for (OrderItem item : ord.items)
							missPrice.remove(item.id);
			}
		}
	}

	@Override
	public void setColor(TextView textView, Price price) {
		textView.setTypeface(null, Typeface.NORMAL);

		
		// Письмо 11 апр 2014 
		// не нужно никаких выделений цветом кроме стандартных выделений цветом:
		// 1) выделять цветом (серым) выбранные позиции во множественном выборе
		// 2) позиции уже присутствующие в заказе
		// 3) позиции прошлого заказа (красный)
		//всё это конечно же распространяется на все прайс листы

		if (Features.PACK_INPUT && packQty != null && packQty.contains(price.id))
			textView.setTextColor(getResources().getColor(R.color.grey));
		else if (((Itemsable) document).findItem(price.id) != null)
			textView.setTextColor(((Itemsable) document).getItemColor());
		else if (lastBuyingItems.contains(price.id))
			textView.setTextColor(getResources().getColor(R.color.red));
		else
			textView.setTextColor(getDefaultColor(price));
		
//		if (focusedItems.contains(price.id)
//				|| focusedGroups.contains(price.folderID))
//			textView.setBackgroundResource(R.drawable.focused_item_back);
//		else
//			textView.setBackgroundColor(Color.TRANSPARENT);
//		if (matrixName.equals(getString(R.string.spectaskclient))
//				|| matrixName.equals(getString(R.string.clientprice)))
//			textView.setTextColor(getResources().getColor(R.color.black));
//		else {
//			if (Features.PACK_INPUT && packQty != null
//					&& packQty.contains(price.id))
//				textView.setTextColor(getResources().getColor(R.color.grey));
//			else if (((Itemsable) document).findItem(price.id) != null)
//				textView.setTextColor(((Itemsable) document).getItemColor());
//			else if (((PriceEx) price).leader == 1
//					&& missPrice.contains(price.id)) {
//				textView.setTextColor(getResources().getColor(R.color.blue));
//				textView.setTypeface(null, Typeface.BOLD);
//			} else if (remnantsDoc != null
//					&& remnantsDoc.findItem(price.id) != null)
//				textView.setTextColor(remnantsDoc.getItemColor());
//			else if (lastBuyingItems.contains(price.id))
//				textView.setTextColor(getResources().getColor(R.color.red));
//			else
//				textView.setTextColor(getDefaultColor(price));
//			
//			if (focusedItems.contains(price.id)
//					|| focusedGroups.contains(price.folderID))
//				textView.setBackgroundResource(R.drawable.focused_item_back);
//			else
//				textView.setBackgroundColor(Color.TRANSPARENT);
//		}
	}

	class OrgMatrixAdapter extends MatrixBaseAdapter {
		List<MatrixItem> matrix;
		String name = "";

		public OrgMatrixAdapter(WarehouseNew warehouse, List<MatrixItemEx> matrix, String name) {
			super(warehouse);
			this.matrix = new ArrayList<MatrixItem>();
			this.name = name;

			for (MatrixItem mi : matrix)
				this.matrix.add(mi);
		}

		public String getName() {
			return name;
		}

		@Override
		protected List<MatrixItem> getMatrixItems() {
			return matrix;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.itNextPrice){
			nextPrice();
			return true;
		}else
			return super.onOptionsItemSelected(item);
	}

	private void nextPrice() {
		if( MATRIX_ARR.size() == 1 )
			return;
		
		if(curMatrixIdx >= MATRIX_ARR.size()-1) {
			curMatrixIdx = MATRIX_ARR.size()-2;
		} else
			curMatrixIdx++;
		applayMatrix(MATRIX_ARR.get(curMatrixIdx));
	}
}

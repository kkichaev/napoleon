package com.grsoft.napoleon;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.MatrixOrder;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixAdapter;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends Warehouse {
	static long lastOrder = ExtrasConst.INVALID_ROWID;
	static int curMatrix = 0;
	MatrixOrder matrixOrder = null;
	boolean hideMatrix = false;
	private String LAST_DOC_TYPE = "last_doc_type";
	static int whIndex = 0;

	@Override protected int getLayoutId() { return R.layout.warehouse_ex; }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		findViewById(R.id.ibNextPrice).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				curMatrix++;
				resetMatrix();
			}
		});
		if( DocType.getCurDoc() == ReturnDoc.instance())
			findViewById(R.id.btnPack).setVisibility(View.GONE);
	}
	
	final int PERIOD_FOR_DELIVERY = 25;
	final int PERIOD_FOR_ORDER = 1;
	
	
	@Override
	protected AssortmentMatrixAdapter createAssortementMatrixAdapter() {
		if (DocType.getCurDoc() == ReturnDoc.instance()) {
			AssortmentMatrixAdapter.MATRIX_DOC = DeliveryDoc.instance();
			AssortmentMatrixAdapter.PERIOD_IN_MONTH = PERIOD_FOR_DELIVERY;
		}else {
			AssortmentMatrixAdapter.MATRIX_DOC = OrderDoc.instance();
			AssortmentMatrixAdapter.PERIOD_IN_MONTH = PERIOD_FOR_ORDER;
		}
		
		return super.createAssortementMatrixAdapter();
	}

	@Override
	protected BaseAdapter createListAdapter() {
		String lastDocType = getPreferences(Context.MODE_PRIVATE).getString(LAST_DOC_TYPE, "");
		String curDocName = DocType.getCurDoc().getName();

		if(!curDocName.equals(lastDocType)){
			SharedPreferences.Editor ed = getPreferences(Context.MODE_PRIVATE).edit();
			ed.putString(LAST_DOC_TYPE, curDocName);
			ed.commit();

			FoldersAdapter.resetCache();
		}
		
		if( DocType.getCurDoc() == ReturnDoc.instance())
			return createAssortementMatrixAdapter();


		hideMatrix = false;

		if( DocType.getCurDoc() == OrderDoc.instance() && document.getRowid() != ExtrasConst.INVALID_ROWID ) {
			if( lastOrder != document.getRowid() ) {
				lastOrder = document.getRowid();
				curMatrix = 0;
			}

			if( matrixOrder == null ) {
				matrixOrder = new MatrixOrder();
				DbReader r = new DbReader();
				r.select(matrixOrder, matrixOrder.getTableName(), null);
				r.close();
			}

			if( curMatrix < matrixOrder.items.size() ) {
				hideMatrix = true;
				findViewById(R.id.llMatrixOrder).setVisibility(View.VISIBLE);
				String name = matrixOrder.items.get(curMatrix).name;
				TextView tv = (TextView)findViewById(R.id.tvMatrixName);
				tv.setText(name);
				if(name.equals("<Активный ассортимент>")) {
					return createAssortementMatrixAdapter();
				}
				return new MatrixAdapter(this, name);
			}
		}
		findViewById(R.id.llMatrixOrder).setVisibility(View.GONE);
		return super.createListAdapter();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean ret = super.onPrepareOptionsMenu(menu);
		MenuItem mi = menu.findItem(R.id.itMatrix);
		if( mi != null)
			mi.setVisible(!hideMatrix);
		return ret;
	}

	@Override
	protected Filter createZeroPositionFilter() {
		if( document instanceof OrderImplEx ) {
			if( whIndex != ((OrderEx)document.getData()).whIndex ) {
				whIndex = ((OrderEx)document.getData()).whIndex;
				FoldersAdapter.resetCache();
			}
		} else if( whIndex != 0 ) {
			whIndex = 0;
			FoldersAdapter.resetCache();			
		}
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

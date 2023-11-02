package com.grsoft.napoleon;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.MatrixOrder;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixAdapter;
import com.grsoft.util.ZeroPositionFilter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WarehouseEx extends Warehouse {
	static int whIndex = 0;
	static long lastOrder = ExtrasConst.INVALID_ROWID;
	static int curMatrix = 0;
	MatrixOrder matrixOrder = null;
	boolean hideMatrix = false;
	
	private String LAST_DOC_TYPE = "last_doc_type"; 
	
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
	}

	@Override
	protected Filter createZeroPositionFilter() {
		if(document instanceof OrderImplEx)
			return new ZeroFilter();
		return super.createZeroPositionFilter();
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

		int newIndex = 0;
		if( document instanceof OrderImplEx) {
			newIndex = ((OrderEx)document.getData()).whIndex;
		}
		if(whIndex != newIndex) {
			whIndex = newIndex;
			FoldersAdapter.resetCache();
		}
		
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

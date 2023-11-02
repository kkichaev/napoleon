package com.grsoft.napoleon;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.MatrixOrder;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixAdapter;

public class WarehouseEx extends WarehouseNew {
	static long lastOrder = ExtrasConst.INVALID_ROWID;
	static int curMatrix = 0;
	MatrixOrder matrixOrder = null;
	boolean hideMatrix = false;
	
	private String LAST_DOC_TYPE = "last_doc_type"; 
	
	@Override
	protected int getLayoutId() {
		return R.layout.warehouse_ex;
	}
	
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
	protected BaseAdapter createListAdapter() {
		String lastDocType = getPreferences(Context.MODE_PRIVATE).getString(LAST_DOC_TYPE, "");
		String curDocName = DocType.getCurDoc().getName();
		
		if(!curDocName.equals(lastDocType)){
			Editor ed = getPreferences(Context.MODE_PRIVATE).edit();
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
				String table = DataObjectInfo.getInstance().getTableName(matrixOrder.getClass());
				DbReader r = new DbReader();
				r.select(matrixOrder, table, null);
				r.close();
			}
			
			if( curMatrix < matrixOrder.items.size() ) {
				hideMatrix = true;
				findViewById(R.id.llMatrixOrder).setVisibility(View.VISIBLE);
				String name = matrixOrder.items.get(curMatrix).name;
				TextView tv = (TextView)findViewById(R.id.tvMatrixName);
				tv.setText(name);
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
}





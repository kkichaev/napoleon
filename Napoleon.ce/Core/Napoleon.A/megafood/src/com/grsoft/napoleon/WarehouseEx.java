package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashSet;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.FocusMatrix;
import com.grsoft.dataobjects.MatrixOrder;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixAdapter;
import com.grsoft.util.WarehouseManager;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class WarehouseEx extends WarehouseNew {
	static String orgId = "";
	static HashSet<String> rmntFilter = new HashSet<String>();

	private String LAST_DOC_TYPE = "last_doc_type";
	
	MatrixOrder matrixOrder = null;
	boolean hideMatrix = false;

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
				if(document instanceof OrderImplEx) {
					OrderImplEx od = (OrderImplEx)document;
					if(od.isValid() == false) {
						Toast.makeText(WarehouseEx.this, "Ќе весь фокусный ассортимент заполнен", Toast.LENGTH_SHORT).show();
						return;
					}
					((OrderEx)document.getData()).curMatrix++;
					document.write();
					resetMatrix();
				}
			}
		});
	}
	
	@Override
	public void setColor(TextView textView, Price price) {
		if(document instanceof OrderImplEx) {
			OrderImplEx oe = (OrderImplEx)document;
			if(oe.haveItem(price.id)) {
				textView.setTextColor(oe.getItemColor());
				return;
			}
		}
		super.setColor(textView, price);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean ret = super.onPrepareOptionsMenu(menu);
		MenuItem mi = menu.findItem(R.id.itMatrix);
		if( mi != null)
			mi.setVisible(!hideMatrix);
		return ret;
	}
	
	FoldersAdapter tryCreateAdapter(int curMatrix) {
		FoldersAdapter ret = null;
		String name = matrixOrder.items.get(curMatrix).name;
		if(name.equals(FocusMatrix.MATRIX_NAME)) {
			HashSet<String> items = FocusMatrix.get(document);
			if(items.size() == 0)
				return null;
			
			((OrderEx)document.getData()).needCheckFocusItems = 1;
			document.write();
			ret = new FocusAdapter(this, items);
		}
		
		hideMatrix = true;
		findViewById(R.id.llMatrixOrder).setVisibility(View.VISIBLE);
		TextView tv = (TextView)findViewById(R.id.tvMatrixName);
		tv.setText(name);
		
		if(ret == null)
			ret = new MatrixAdapter(this, name);
		return ret;
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
			OrderEx o = (OrderEx)document.getData();
			if(o.curMatrix < 0) {
				o.curMatrix = 0;
				document.write();
			}
			
			if( matrixOrder == null ) {
				matrixOrder = new MatrixOrder();
				String table = DataObjectInfo.getInstance().getTableName(matrixOrder.getClass());
				DbReader r = new DbReader();
				r.select(matrixOrder, table, null);
				r.close();
			}
			
			while( o.curMatrix < matrixOrder.items.size() ) {
//				hideMatrix = true;
//				findViewById(R.id.llMatrixOrder).setVisibility(View.VISIBLE);
//				String name = matrixOrder.items.get(o.curMatrix).name;
//				TextView tv = (TextView)findViewById(R.id.tvMatrixName);
//				tv.setText(name);
//				return new MatrixAdapter(this, name);

				FoldersAdapter a = tryCreateAdapter(o.curMatrix);
				if(a != null)
					return a;
				o.curMatrix++;
				document.write();
			}
		}
		findViewById(R.id.llMatrixOrder).setVisibility(View.GONE);

		if(document instanceof RemnantsImpl)
			return new RemnantsAdapter(this, document.getId());
		
		return super.createListAdapter();
	}
	
	class FocusAdapter extends FoldersAdapter {
		HashSet<String> items;
		
		public FocusAdapter(WarehouseManager warehouse, HashSet<String> items) {
			super(warehouse);
			this.items = items;
		}
		
		@Override
		public boolean inset(long rowid, String id, int folder) {
			return items.contains(id);
		}
		
	}
	
	class RemnantsAdapter extends FoldersAdapter {
				
		public RemnantsAdapter(WarehouseManager warehouse, String id) {
			super(warehouse);
			if(orgId.equals(id) == false) {
				resetCache();
				orgId = id;
			}
			
		}
		
		@Override
		public boolean inset(long rowid, String id, int folder) {
			if( rmntFilter.size() > 0 )
				return rmntFilter.contains(id);

			return super.inset(rowid, id, folder);
		}
		
		@Override
		protected void fillPriceIds(SQLiteDatabase database) {
			rmntFilter.clear();
			
			Date end = new Date();
			Date begin = new Date(end.getTime() - 31 * 24 * 3600 * 1000l);
			DatePeriod dp = new DatePeriod(begin, end);
			DocList dl = DeliveryDoc.instance().docList(orgId, "", dp);
			for(Document<?> d : dl) {
				for(OrderItem ri : ((OrderImplEx)d).getData().items)
					rmntFilter.add(ri.id);
			}
			dl.close();
			
			super.fillPriceIds(database);
		}
	}
}

package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.BaseAdapter;
import com.grsoft.dataobjects.FocusedItemsItem;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.CommonMatrixImpl;
import com.grsoft.dataobjects.impl.FocusedItemsImpl;
import com.grsoft.dataobjects.impl.OrgMtxImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.DeliveryList;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixAdapter;
import com.grsoft.util.MatrixBaseAdapter;

public class WarehouseEx extends WarehouseNew {
	final static String MATRIX_NAME = "<Матрица контрагента>";
	final static String F_MATRIX_NAME = "<Фокусный ассортимент>";
	
	
	BaseAdapter listAdapter;
	FocusedMatrix fmatrix;
	View llMatrixOrder;
	
	private String orgMatrix = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.ibNextPrice).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openNextMatrix();
			}
		});
	}
	
	protected void postDocInited() {
		llMatrixOrder = findViewById(R.id.llMatrixOrder);
	}
	
	protected void openNextMatrix() {
		llMatrixOrder.setVisibility(View.GONE);
		if(orgMatrix.length() > 0)
			applayMatrix(orgMatrix);
		else
			resetMatrix();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if( !super.onCreateOptionsMenu(menu) )
			return false;
		
		menu.removeItem(R.id.itZeroFilter);
		return true;
	}
	
	@Override
	protected int getLayoutId() { return R.layout.warehouse_ex; }
		
	@Override
	protected BaseAdapter createListAdapter() {
		if( listAdapter == null ) {
			if (docRowId != ExtrasConst.INVALID_ID){
				if(document.getRowid() == ExtrasConst.INVALID_ID)
					document.read(docRowId);
				
				if( document instanceof ReturnImplEx )
					return new ReturnAdapter(this, document.getId());
			}
			if( listAdapter == null )
				listAdapter = super.createListAdapter();
		}
		
		return listAdapter;
	}
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		if(useMatrixOrder())
			items.add(0, F_MATRIX_NAME);
		
		return items;
	}

	
	@Override
	protected void postAdapterInit() {
		
		if (useMatrixOrder()){
			fmatrix = new FocusedMatrix(this);
			
			OrgMtxImpl orgMtx = new OrgMtxImpl();
			orgMtx.getData().id = document.getId();
			orgMtx.read();
			orgMtx.close();
			orgMatrix = orgMtx.getData().matrix;
			
			CommonMatrixImpl matrix = new CommonMatrixImpl();
			matrix.getData().name = orgMatrix;
			matrix.read();
			matrix.close();

			if(matrix.getData().items.size() == 0)
				orgMatrix = "";
			
			if(fmatrix.getMatrixItems().size() > 0){
				matrixName = F_MATRIX_NAME;
				llMatrixOrder.setVisibility(View.VISIBLE);
				applayAdapter(fmatrix);
			}else
				applayMatrix(MATRIX_NAME);
		}else
			super.postAdapterInit();
	}

	protected boolean useMatrixOrder() {
		return DocType.getCurDoc() == OrderDoc.instance() && document.getRowid() != ExtrasConst.INVALID_ROWID;
	}
	
	class FocusedMatrix extends MatrixBaseAdapter{
		List<MatrixItem> list = new ArrayList<MatrixItem>();
		
		public FocusedMatrix(WarehouseNewW warehouse) {
			super(warehouse);
			
			FocusedItemsImpl focusedItems = new  FocusedItemsImpl();
			focusedItems.getData().id = document.getId();
			if( !focusedItems.read() ) {
				focusedItems.getData().id = "";
				focusedItems.read();
			}
			
			
			for(FocusedItemsItem i : focusedItems.getData().items){
				MatrixItem m = new MatrixItem();
				m.id = i.id;
				list.add(m);
			}
		}

		@Override
		protected List<? extends MatrixItem> getMatrixItems() { return list; }
	}
	
	protected boolean inheritedApplayMatrix(String matrixName){
		boolean result = true;
		
		if(matrixName.equals(F_MATRIX_NAME))
			applayAdapter(fmatrix);
		else if(matrixName.equals(MATRIX_NAME)){
			if (orgMatrix.length() > 0)
				applayAdapter(new MatrixAdapter(this, orgMatrix));
			else
				resetMatrix();
		}
		else
			result = false;
		
		return result; 
	}
}

class ReturnAdapter extends FoldersAdapter {
	
	DeliveryList list;
	
	public ReturnAdapter(WarehouseEx owner, String orgId) {
		super(owner);
		list = DeliveryList.open(orgId);
	}
	
	@Override
	protected void fillPriceIds(SQLiteDatabase database) {
		PriceImpl p = new PriceImpl();
		Price price = p.getData();
		
		fprice.clear();
		for( String id : list.getSaledItems() ) {
			price.id = id;
			if( p.read() ) {
				if( !fprice.containsKey(price.folderID) )
					fprice.put(price.folderID, new ArrayList<PriceInfo>());
				
				PriceInfo pi = new PriceInfo(p.getRowid(), price.name, price.id);
				fprice.get(price.folderID).add(pi);				
			}
		}
		
		p.close();
	}
	
	@Override
	public String getName() {
		return super.getName() + list.getId();
	}
}

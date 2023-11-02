package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import android.widget.TextView;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.CommonMatrixImpl;
import com.grsoft.dataobjects.impl.OrgMtxImpl;
import com.grsoft.dataobjects.impl.RetMtxImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.Filter;
import com.grsoft.util.MatrixBaseAdapter;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends WarehouseNew {
	final static String MATRIX_NAME = "<Матрица контрагента>";
	
	List<MatrixItem> orgMatrix = null;
	private boolean matrixInited = false;
	
	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		if( type == COLUMN_QTY_WH || type == COLUMN_QTY_WH_ORD )
			type = COLUMN_QTY_ORD;
		super.setTextColumnValue(textView, type, price);
	}
	@Override
	protected void adapterInit(){}

	@Override
	protected void onResume() {
		super.onResume();
		
		if( document != null && !matrixInited) {
			String name = "";
			
			if(DocType.getCurDoc() == ReturnDoc.instance()){
				RetMtxImpl retMtx = new RetMtxImpl();
				retMtx.getData().id = document.getId();
				retMtx.read();
				retMtx.close();
				name = retMtx.getData().matrix;
			}else{
				OrgMtxImpl orgMtx = new OrgMtxImpl();
				orgMtx.getData().id = document.getId();
				orgMtx.read();
				orgMtx.close();
				name = orgMtx.getData().matrix;
			}
			
			CommonMatrixImpl matrix = new CommonMatrixImpl();
			matrix.getData().name = name;
			matrix.read();
			matrix.close();
			matrixInited = true;
			orgMatrix = matrix.getData().items;
			
			if(DocType.getCurDoc() != ReturnDoc.instance())
				adapter.putFilter(createZeroPositionFilter());
			
			if(orgMatrix != null && orgMatrix.size() > 0)
				applayAdapter(new OrgMatrixAdapter(this, matrix.getData().items));
			else
				adapter.buildSet();
		}
	}
	
	@Override
	protected void resetMatrix() {
		matrixName = MATRIX_NAME;
		applayAdapter(new OrgMatrixAdapter(this, orgMatrix));
	}
	

	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		int pos = 1;
		if( orgMatrix != null && orgMatrix.size() > 0) {
			items.add(pos++, MATRIX_NAME);
		}
		
		items.remove(PRICE_WITHOUT_MATRIX);
		
		return items;
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

class OrgMatrixAdapter extends MatrixBaseAdapter {
	List<MatrixItem> matrix;
	public OrgMatrixAdapter(WarehouseNew warehouse, List<MatrixItem> matrix) {
		super(warehouse);
		this.matrix = matrix;
	}

	public String getName() { return WarehouseEx.MATRIX_NAME; }

	@Override
	protected List<MatrixItem> getMatrixItems() {
		return matrix;
	}
}

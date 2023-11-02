package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixItemsAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.ZeroPositionFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class WarehouseEx extends WarehouseNew {
	static int whIndex = 0;
	final String MATRIX_NAME = "<Матрица контрагента>";
	public static final String CURRENT_MATRIX = "current_matrix";
	public static final String PREF_NAME = "warehouse_pref";
		
	List<MatrixItem> orgMatrix = null;
	List<MatrixItem> focusMatrix = null;
//	private boolean matrixInited = false;
	private ToggleButton tbColorFilter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tbColorFilter = (ToggleButton) findViewById(R.id.tbColorFilter);
		
		lvItemSelect.setDividerHeight(1);
		tbColorFilter.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
					adapter.putFilter(new ColorFilter());
				else 
					adapter.deleteFilter(ColorFilter.NAME);
				
				adapter.buildSet();
			}
		});
		
	}
	
	@Override protected int getLayoutId() { return R.layout.warehouseex;}
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		int pos = 1;
		if( orgMatrix != null ) 
			items.add(pos++, MATRIX_NAME);
		
		return items;
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		if( document != null ) {
			if( document.getRowid() == ExtrasConst.INVALID_ID )
				document.read(docRowId);
			
			OrgImpl oi = new OrgImpl();
			Org org = oi.getData();
			org.id = document.getId();
			oi.read();
			oi.close();
			
			OrgEx oe = (OrgEx)org;
			if( oe.matrix.size() > 0 )
				orgMatrix = oe.matrix;
			
			SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
			String matrixName = sp.getString(CURRENT_MATRIX, "");

			if ( !matrixName.equals(PRICE_WITHOUT_MATRIX) && orgMatrix != null )
				return new OrgMatrix(this, orgMatrix, org.id);
		}
		return super.createListAdapter();
	}

	@Override
	protected void applayMatrix(String matrixName) {
		if( matrixName.equals(MATRIX_NAME)) {
			applayAdapter(new MatrixItemsAdapter(this, orgMatrix));
		} else
			super.applayMatrix(matrixName);
		
		this.matrixName = matrixName;
		SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		sp.edit().putString(CURRENT_MATRIX, matrixName).commit();
	}
	
//	@Override
//	protected void onResume() {
//		super.onResume();		
//	}
	
	@Override
	protected void updateTotalSum() {
		if (document instanceof OrderImplBase<?>)
			updateTotalSum(document.sum(), ((OrderImplBase<?>)document).weight(), ((OrderImplBase<?>)document).count());
		else
			super.updateTotalSum();
	}
	
	@Override
	protected void resetMatrix() {
		SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		sp.edit().putString(CURRENT_MATRIX, matrixName).commit();
		super.resetMatrix();
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
	
	class ColorFilter extends Filter
	{
//		private PriceImpl price = new PriceImpl();
		public static final String NAME = "ColorFilter";
		
		public ColorFilter() {
			super(NAME);
		}
		
//		@Override
//		public boolean inset(long priceRowID, String id) {
//			price.read(priceRowID, false);
//			price.close();
//			
//			PriceEx pe = (PriceEx) price.getData();
//			
//			return pe.color != 0 || pe.bkgcolor != 0 && Util.GrServerColorToSystem(pe.bkgcolor) != Color.WHITE;
//		}
		
		@Override
		public String getWhereStr() {
			return "color != 0 or bkgcolor != 0 and bkgcolor != 16777215";
		}
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
	
	@Override
	public void setColor(TextView textView, Price price) {
		super.setColor(textView, price);
		int c = Color.WHITE;
		if(((PriceEx)price).bkgcolor != 0)
			c = ((PriceEx)price).bkgcolor;
		textView.setBackgroundColor(Util.GrServerColorToSystem(c));
		
		if (price.color != 0)
			textView.setTypeface(null, Typeface.BOLD);
		else 
			textView.setTypeface(null, Typeface.NORMAL);
	}
}

class OrgMatrix extends MatrixItemsAdapter {
	String id;
	public OrgMatrix(WarehouseNew warehouse, List<MatrixItem> items, String id) {
		super(warehouse, items);
		this.id = id;
	}
	
	@Override
	public String getName() {
		return super.getName() + id;
	}
}

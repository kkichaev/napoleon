package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.MatrixItemsAdapter;

public class WarehouseEx extends WarehouseNew {
	final String MATRIX_NAME = "<Матрица контрагента>";
	public static final String CURRENT_MATRIX = "current_matrix";
	public static final String PREF_NAME = "warehouse_pref";
		
	List<MatrixItem> orgMatrix = null;
//	private boolean matrixInited = false;
	
	HashSet<String> focusedItems = new HashSet<String>();
	private ImageButton btnExpandPrice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnLevelUp).setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				adapter.upLevel();
			}
		});
		
		btnExpandPrice = (ImageButton)findViewById(R.id.btnExpandPrice);
		btnExpandPrice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				expandingPrice();
			}
		});
	}
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		int pos = 1;
		if( orgMatrix != null ) {
			items.add(pos++, MATRIX_NAME);
		}
		
		return items;
	}
	
	@Override
	protected void expandingPrice() {
		super.expandingPrice();
		btnExpandPrice.setImageResource(adapter.isExpanded() ? 
				R.drawable.price_as_folder : R.drawable.price_as_list);
	}
	
	@Override
	protected int getDefaultColor(Price p) {
		if( focusedItems.contains(p.id) )
			return Color.BLUE;
		return super.getDefaultColor(p);
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		if( document != null ) {
			if( document.getRowid() == ExtrasConst.INVALID_ID )
				document.read(docRowId);
			
			orgMatrix = null;
			if( document instanceof OrderImpl ) {
				OrderEx oe = (OrderEx)document.getData();
				OrgImpl oi = new OrgImpl();
				Org o = oi.getData();
				OrgEx ob = (OrgEx)o;
				o.id = document.getId();
				
				if( oi.read() ) {
					for(OrgDogovor od : ob.dogovors )
						if( oe.dogId.equals(od.id) ) {
							if( od.matrix.size() > 0 ) {
								orgMatrix = new ArrayList<MatrixItem>();
								orgMatrix.addAll(od.matrix);
							} else
								orgMatrix = null;
						}
				}
				oi.close();

			}			
			
			SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
			String matrixName = sp.getString(CURRENT_MATRIX, "");

			if ( !matrixName.equals(PRICE_WITHOUT_MATRIX) && orgMatrix != null )
				return new OrgMatrix(this, orgMatrix, document.getId());
		}
		return super.createListAdapter();
	}

	@Override
	protected void applayMatrix(String matrixName) {
		if( matrixName.equals(MATRIX_NAME) && orgMatrix != null ) {
			applayAdapter(new OrgMatrix(this, orgMatrix, document.getId()));
		} else
			super.applayMatrix(matrixName);
		
		this.matrixName = matrixName;
		SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		sp.edit().putString(CURRENT_MATRIX, matrixName).commit();
	}

	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if( orgMatrix != null )
			menu.removeItem(R.id.itMatrix);
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if( orgMatrix != null )
			menu.removeItem(R.id.itMoveToFolder);
	}
	
	@Override
	protected void updateTotalSum() {
		if (document instanceof OrderImplBase<?>)
			updateTotalSum(document.sum(), ((OrderImplBase<?>)document).weight(),
					((OrderImplBase<?>)document).count());
		else
			super.updateTotalSum();
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.warehouseex;
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
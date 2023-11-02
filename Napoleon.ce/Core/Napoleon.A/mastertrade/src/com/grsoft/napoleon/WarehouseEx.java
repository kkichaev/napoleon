package com.grsoft.napoleon;

import java.util.List;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FoldersAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class WarehouseEx extends WarehouseNew {
	/***
	 * 28.03.2016 Кабанов просил для Меню - Документы - Прайст лист,
	 * брать цену из COST2
	 */
	public static int GetSumTypeForNonDoc() {
		return 1;
	}
	
	private static final int SELECT_COST = 0x241;
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if( !super.onCreateOptionsMenu(menu) )
			return false;
		
		if( document instanceof OrderImpl && document.getRowid() == ExtrasConst.INVALID_ROWID ){
			menu.add(Menu.NONE, SELECT_COST, Menu.NONE, "Тип цен");
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == SELECT_COST){
			showDialog(SELECT_COST);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == SELECT_COST)
			return createCostDialog();
		
		return super.onCreateDialog(id);
	}
	

	private Dialog createCostDialog() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Цены");
		
		final List<CostItem> items = CostItem.getItems(false);
		if( items.size() > 0 ) {
			CharSequence[] csa = new CharSequence[items.size()];
			for(int i=0; i<items.size(); i++)
				csa[i] = items.get(i).name;
			
			b.setItems(csa, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) { selectCost(items.get(which).index); }
			});
		}

		return b.create();
	}

	protected void selectCost(int which) {
		((OrderImpl)document).getData().sumType = which;
		FoldersAdapter.resetCache();
		adapter.buildSet();
	}
	
	/* бага 1782 сделать при создании заказа, чтобы фильтр нулевых позиций сразу по умолчанию был включен */
	protected void initZeroFilter() {
		if(DocType.getCurDoc() == OrderDoc.instance() && docRowId != ExtrasConst.INVALID_ROWID)
			adapter.putFilter(createZeroPositionFilter());
	}
}

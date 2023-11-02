package com.grsoft.napoleon;

import java.util.HashSet;
import java.util.Set;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ItemGroups;
import com.grsoft.dataobjects.ItemGroupsItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.AssortmentMatrixAdapterEx;
import com.grsoft.util.Consts;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseManager;


public class WarehouseEx extends WarehouseNew {
	public AssortmentMatrixAdapterEx assortmentMatrixAdapter;
	public Set<String> itemGR = new HashSet<String>(); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DbWriter.checkDBTable(ItemGroups.class);
//		String userid = "";
//		SQLiteStatement stm = null;
//		
//		try{
//			stm = DataBaseManager.getDataBase().compileStatement(
//					String.format("SELECT userid FROM %s WHERE userid IS NOT NULL AND userid != ''", 
//							DataObjectInfo.getInstance().getTableName(ItemGroups.class)));
//			userid = stm.simpleQueryForString();
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			if(stm != null)
//				stm.close();
//		}
		
		StringBuilder where = new StringBuilder();
//		where.append("userid='").append(userid).append("'");
		DataTraveler.travel(ItemGroups.class, new DataTraveler.Travel<ItemGroups>() {
			@Override public boolean travel(DataTraveler<ItemGroups> item) {
				for(ItemGroupsItem i : item.data.items)
					if (!itemGR.contains(i.id))
						itemGR.add(i.id);
				return true;
			}}, where.toString());
		
		super.onCreate(savedInstanceState);
		createAssortementMatrixAdapter();
	}
	
	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		if( type == COLUMN_QTY_WH && remnantsDoc != null ) {
			RemnantItem ri = (RemnantItem)remnantsDoc.findItem(price.id);
			if( ri != null ) {
				textView.setText(Util.IntToScaleStr(ri.qty, Consts.QTY_SCALE, Util.DEC_DELIM, true));
				return;
			}
		}
		super.setTextColumnValue(textView, type, price);
	}
	
	@Override
	public void setColor(TextView textView, Price price) {
		if(assortmentMatrixAdapter != null && assortmentMatrixAdapter.isIdInMatrix(price.id) &&
			((Itemsable)document).findItem(price.id) == null && !lastBuyingItems.contains(price.id)){
			textView.setTextColor(getResources().getColor(R.color.blue));
		} else
			super.setColor(textView, price);
		
		if(itemGR.contains(price.id))
			textView.setTypeface(Typeface.DEFAULT_BOLD);
	};
	
	@Override
	protected AssortmentMatrixAdapter createAssortementMatrixAdapter() {
		if (assortmentMatrixAdapter == null)
			assortmentMatrixAdapter =  new AssortmentMatrixAdapterEx(this, document.getId());
		
		return assortmentMatrixAdapter;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.findItem(R.id.itExpand).setVisible(false);
		return true;
	}
	
	
	@Override
	protected BaseAdapter createListAdapter() {
		if(DocType.getCurDoc() == RemnantsDoc.instance())
			return new RemnantsAdapter(this);
		return super.createListAdapter();
	}
	
	class RemnantsAdapter extends FoldersAdapter{

		public RemnantsAdapter(WarehouseManager warehouse) {
			super(warehouse);
		}
		
		@Override
		public boolean inset(long rowid, String id) {
			if(itemGR.size() > 0)
				return itemGR.contains(id);
			else			
				return super.inset(rowid, id);
		}
	}
}



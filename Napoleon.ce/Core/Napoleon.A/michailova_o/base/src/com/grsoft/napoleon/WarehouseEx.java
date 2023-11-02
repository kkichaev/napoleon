package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.MatrixEx;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.impl.MatrixImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgTypeMatrixImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.DeliveryList;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixAdapter;
import com.grsoft.util.WarehouseAdapter;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WarehouseEx extends WarehouseNew {
	private BaseAdapter listAdapter;
	private View llMatrixOrder;
	private TextView tvMatrixName;
	private View ibNextPrice;
	private boolean useCore = false;
	
	@Override protected int getLayoutId() { return R.layout.warehouse_ex;	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if( !super.onCreateOptionsMenu(menu) )
			return false;
		
		menu.removeItem(R.id.itZeroFilter);
		return true;
	}

	@Override
	protected void postInitUI() {
		super.postInitUI();
		llMatrixOrder = findViewById(R.id.llMatrixOrder);
		tvMatrixName = (TextView)findViewById(R.id.tvMatrixName);
		ibNextPrice = findViewById(R.id.ibNextPrice);
		ibNextPrice.setEnabled(false);
		ibNextPrice.setOnClickListener(ibNextPriceClick);
	}
	
	private OnClickListener ibNextPriceClick = new OnClickListener() {
		@Override public void onClick(View v) {
			listAdapter = null;
			applayAdapter((WarehouseAdapter) createListAdapter());
		}
	}; 
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		items.clear();
		items.add(PRICE_WITHOUT_MATRIX);

		MatrixEx me = new MatrixEx();
		String table = DataObjectInfo.getInstance().getTableName(me.getClass());
		DbReader r = new DbReader();
		if( r.select(me, table, "common <> 0") ) {
			items.add(me.name);
		}
		r.close();
		return items;
	}
	
	@Override
	public void notifyDataSetChanged() {
		if(useCore)
			ibNextPrice.setEnabled(isRemnantsComplete(((OrgTypeMatrix)adapter).getMatrixItems()));
		
		super.notifyDataSetChanged();
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		if( listAdapter == null ) {
			if (docRowId != ExtrasConst.INVALID_ID){
				if(document.getRowid() == ExtrasConst.INVALID_ID)
					document.read(docRowId);
				
				if( document instanceof ReturnImplEx )
					return new ReturnAdapter(this, document.getId());
				
				OrgImpl orgImpl = new OrgImpl();
				OrgEx oe = (OrgEx) orgImpl.getData(); 
				oe.id = document.getId();
				orgImpl.read();
				orgImpl.close();
				
				OrgTypeMatrixImpl otm = new OrgTypeMatrixImpl();
				if(otm.read("id", oe.id) && useTypeOrgMatrix(otm.getData().matrix)){
					String name = otm.getData().matrix;
					llMatrixOrder.setVisibility(View.VISIBLE);
					useCore = true;
					this.matrixName = name;
					tvMatrixName.setText(name);
					String oem = oe.matrix != null && oe.matrix.size() > 0 ? oe.matrix.get(0).name : ""; 
					listAdapter = new OrgTypeMatrix(this, name, oem); 
					
					if (isRemnantsComplete(((OrgTypeMatrix)listAdapter).getMatrixItems())){
						listAdapter = null;
						llMatrixOrder.setVisibility(View.GONE);
						useCore = false;
					}
				}
				
				if(listAdapter == null && oe.matrix != null && oe.matrix.size() > 0 ) {
					String name = oe.matrix.get(0).name;
					if( name.length() > 0 ) {
						this.matrixName = name;
						listAdapter = new MatrixAdapter(this, name);
					}
				}
				
			}
			
			if( listAdapter == null )
				listAdapter = super.createListAdapter();
		}
		
		return listAdapter;
	}

	private boolean useTypeOrgMatrix(String mtx) {
		boolean result = false;
		
		CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
		result = (cfg.isComplexSalesHistory && document instanceof OrderImpl) || document instanceof RemnantsImpl;
		
		return result;
	}

	protected boolean isRemnantsComplete(List<MatrixItem> items)  {
		boolean result = false;
		
		RemnantsImpl curremnants = null;
		
		if(document instanceof OrderImpl){
			long rowid = RemnantsImpl.find(document.getId(), new Date());
			
			if(rowid != ExtrasConst.INVALID_ID){
				curremnants = (RemnantsImpl) RemnantsDoc.instance().create();
				curremnants.read(rowid);
				curremnants.close();
			}
		}else if(document instanceof RemnantsImpl){
			curremnants = (RemnantsImpl) document;
		}
		
		
		Set<String> mid = new HashSet<String>();
		for(MatrixItem i : items)
			mid.add(i.id);
		
		if (curremnants != null)
			for(RemnantItem i : curremnants.getData().items)
				mid.remove(i.id);
		
		result = mid.size() == 0;
		
		return result;
	}
}

class OrgTypeMatrix extends MatrixAdapter{
	private String orgmtxname = "";
	
	public OrgTypeMatrix(WarehouseNewW warehouse, String matrix, String orgmtxname) {
		super(warehouse, matrix);
		this.orgmtxname = orgmtxname;
	}
	
	@Override
	protected List<MatrixItem> getMatrixItems() {
		List<MatrixItem> result = new ArrayList<MatrixItem>();
		List<MatrixItem> baseMatrix = super.getMatrixItems();
		Set<String> ids = new HashSet<String>();
				
		MatrixImpl orgmtx = new MatrixImpl();
				
		if(orgmtxname.trim().length() > 0 && orgmtx.read("name", orgmtxname))
			for(MatrixItem i : orgmtx.getData().items)
				ids.add(i.id);
		
		for(MatrixItem i : baseMatrix)
			if(ids.contains(i.id) || orgmtx.getData().items.size() == 0)
				result.add(i);
		
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


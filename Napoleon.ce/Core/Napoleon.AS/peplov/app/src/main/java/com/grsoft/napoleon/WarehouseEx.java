package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.MatrixOrder;
import com.grsoft.dataobjects.NETMtx;
import com.grsoft.dataobjects.NetMtxItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixAdapter;
import com.grsoft.util.MatrixBaseAdapter;
import com.grsoft.util.WarehouseAdapter;
import com.grsoft.util.WarehouseManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WarehouseEx extends Warehouse {
	private static final String ACTION_MTX = "<Акция>";
	private final String LAST_DOC_TYPE = "last_doc_type";
	public static String netid = "";
	public static Map<String, Integer> netItems = new HashMap<String, Integer>();
	static int curMatrix = 0;
	boolean hideMatrix = false;
	static long lastOrder = ExtrasConst.INVALID_ROWID;
	MatrixOrder matrixOrder = null;

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
				TextView tv = findViewById(R.id.tvMatrixName);
				tv.setText(name);

				if(name.equals("<Активный ассортимент>")) {
					return createAssortementMatrixAdapter();
				}

				if (name.equals("<Акция>")) {
					return createActionMatrixAdapter();
				}

				return new MatrixAdapter(this, name);
			}
		}

		findViewById(R.id.llMatrixOrder).setVisibility(View.GONE);

		FoldersAdapter a = (FoldersAdapter) super.createListAdapter();
		
		DocType cd = DocType.getCurDoc(); 
		if(cd == OrderDoc.instance() || cd == RemnantsDoc.instance() ) {
			a.putFilter(new NetItemsFilter());
		}
		return a;
	}

	private BaseAdapter createActionMatrixAdapter() {
		FoldersAdapter.resetCache();
		FoldersAdapter f = new MatrixBaseAdapter(this){
			@Override
			protected List<? extends MatrixItem> getMatrixItems() {
				final List<MatrixItem> result = new ArrayList<MatrixItem>();

				DataTraveler.travel(PriceEx.class, new DataTraveler.Travel<PriceEx>() {
					@Override
					public boolean travel(DataTraveler<PriceEx> item) {
						MatrixItem i = new MatrixItem();
						i.id = item.data.id;
						result.add(i);
						return true;
					}
				}, "action = 1");

				return result;
			}
		};

		f.putFilter(new NetItemsFilter());
		if (Features.SHOW_ZERO_FILTER)
			f.putFilter(createZeroPositionFilter());

		f.putFilter(new NetItemsFilter());

		return f;
	}

	public static class NetItemsFilter extends  Filter {

		public NetItemsFilter() {

			super("NETITEMS");
		}

		@Override
		public boolean inset(long priceRowID, String id) {
			boolean res =  super.inset(priceRowID, id);

			if (res && netid.length() > 0)
				res = netItems.containsKey(id);

			return res;
		}
	}

	@Override
	protected boolean inheritedApplayMatrix(String matrixName) {
		if (matrixName.equals(ACTION_MTX)){
			applayAdapter((WarehouseAdapter) createActionMatrixAdapter());
			return true;
		}else
			return super.inheritedApplayMatrix(matrixName);
	}

	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		ArrayList<String> result = super.prepareMatrixList(items);
		result.add(ACTION_MTX);
		return result;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean ret = super.onPrepareOptionsMenu(menu);
		MenuItem mi = menu.findItem(R.id.itMatrix);
		if( mi != null)
			mi.setVisible(!hideMatrix);
		return ret;
	}

	@Override
	protected void postDocInited() {
		super.postDocInited();
		
		OrgImpl org = new OrgImpl();
		netid = "";
		org.read("id", document.getId());
		
		if (((OrgEx)org.getData()).netid.length() == 0 || !((OrgEx)org.getData()).netid.equals(netid)) {
			netid = ((OrgEx)org.getData()).netid;
			
			DataTraveler.travel(NETMtx.class, new DataTraveler.Travel<NETMtx>() {
				@Override
				public boolean travel(DataTraveler<NETMtx> item) {
					for(NetMtxItem i : item.data.items)
						if(!netItems.containsKey(i.id))
							netItems.put(i.id, i.cost);
					
					return false;
				}
			}, "id = '" + netid + "'");
			
		}
	}
	
	@Override
	protected FoldersAdapter createAdapterInstance() {
		FoldersAdapter.resetCache();
		
		if( document instanceof ReturnImplEx)
			return new ReturnAdapter(this, document.getId());
		else
			return super.createAdapterInstance();
	}
	
	class ReturnAdapter extends FoldersAdapter {

		HashSet<String> ids = new HashSet<String>();
		
		public ReturnAdapter(WarehouseManager warehouse, String orgId) {
			super(warehouse);
			
			com.grsoft.napoleon.documents.DocList dl = DeliveryDoc.instance().docList(orgId);
			for(Document<?> d : dl) {
				for(DeliveryItem di : ((DeliveryImpl)d).getData().items)
					ids.add(di.id);
			}
			dl.close();
		}
		
		@Override public boolean inset(long rowid, String id) { return ids.contains(id); }
	}

}

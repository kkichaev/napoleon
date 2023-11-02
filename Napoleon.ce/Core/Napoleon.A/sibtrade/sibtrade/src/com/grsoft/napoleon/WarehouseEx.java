package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixBaseAdapter;
import com.grsoft.util.PriceTextFilter;
import com.grsoft.util.WarehouseManager;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import android.os.Bundle;


public class WarehouseEx extends WarehouseNew {
	static String ORG_MATRIX = "<Матрица точки>";
	boolean showMatrix = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		showMatrix = (DocType.getCurDoc() == RemnantsDoc.instance());
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected FoldersAdapter createAdapterInstance() {
		FoldersAdapter.resetCache();
		if(showMatrix && document != null) {
			OrgImpl oi = new OrgImpl();
			oi.read("id", document.getId());
			OrgEx oe = (OrgEx) oi.getData();
			return new OrgMatrixAdapter(oe, this);
		}
		if (DocType.getCurDoc() == ReturnDoc.instance())
			return new ReturnAdapter(this);
		else
			return new FoldersAdapter(this);
	}
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		ArrayList<String> ret = super.prepareMatrixList(items); 
		if(DocType.getCurDoc() == RemnantsDoc.instance()) {
			ret.add(ORG_MATRIX);
			if(showMatrix)
				matrixName = ORG_MATRIX;
		}
		return ret;
	}
	
	@Override
	protected boolean inheritedApplayMatrix(String matrixName) {
		if(matrixName.equals(ORG_MATRIX)) {
			showMatrix = true;
			OrgImpl oi = new OrgImpl();
			oi.read("id", document.getId());
			OrgEx oe = (OrgEx) oi.getData();
			applayAdapter(new OrgMatrixAdapter(oe, this));
			return true;
		}
		return super.inheritedApplayMatrix(matrixName);
	}
	
	@Override
	protected void resetMatrix() {
		showMatrix = false;
		super.resetMatrix();
	}
	
	protected PriceTextFilter createPriceTextFilter() {
		return new PriceTextFilter() {
			protected void collectFolderID(TreeNode node, List<Integer> fids) {}
		};
	}
	
	class ReturnAdapter extends FoldersAdapter {

		HashSet<String> ids = new HashSet<String>();
		
		public ReturnAdapter(WarehouseManager warehouse) {
			super(warehouse);
			
			ConfigImpl config = new ConfigImpl();
			Config c = config.getData();
			c.key = "Организация";
			
			if( config.read()) {
				List<CharSequence> firms = new ArrayList<CharSequence>();
				DialogHelper.makeList(c.value, firms);
				String orgId = document.getId();
				Return r = (Return) document.getData();
				
				if(r.supplyer >= 0 && r.supplyer < firms.size()) {
					String firma = (String) firms.get(r.supplyer);
					
					com.grsoft.napoleon.documents.DocList dl = DeliveryDoc.instance().docList(orgId);
					
					for(Document<?> d : dl) {
						DeliveryEx dlv = (DeliveryEx) d.getData();
						
						if(dlv.firma.equals(firma))
							for(DeliveryItem di : ((DeliveryImpl)d).getData().items)
								ids.add(di.id);
					}
					
					dl.close();
				}
			}	
		}
		
		@Override public boolean inset(long rowid, String id) { return ids.contains(id); }
	}

	class OrgMatrixAdapter extends MatrixBaseAdapter {
		List<MatrixItem> items;
		
		public OrgMatrixAdapter(OrgEx org, WarehouseNewW warehouse) {
			super(warehouse);
			items = org.matrix;
			
		}
		
		@Override
		protected List<? extends MatrixItem> getMatrixItems() {
			return items;
		}
		
	}
}

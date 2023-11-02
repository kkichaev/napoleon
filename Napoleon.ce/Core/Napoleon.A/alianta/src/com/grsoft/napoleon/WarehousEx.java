package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.Matrix;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.UserAssortMtx;
import com.grsoft.dataobjects.impl.AliantaOfferImpl;
import com.grsoft.dataobjects.impl.MatrixImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixAdapter;
import com.grsoft.util.TreeNodeCmp;
import com.grsoft.util.WarehouseManager;
import com.grsoft.util.ZeroPositionFilter;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class WarehousEx extends WarehouseNew {
	static boolean useMatrix = false;
	static long lastOrder = ExtrasConst.INVALID_ROWID;
	static final String ACTION_MATRIX = "<Акции>"; 
	private String LAST_DOC_TYPE = "last_doc_type"; 
	
//	HashSet<String> actionItems = new HashSet<String>();

	@Override protected int getItemLayoutId() { return R.layout.priceitemrowex; }
	@Override protected int getLayoutId() { return R.layout.warehouseex; }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		findViewById(R.id.ibNextPrice).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				useMatrix = false;
				resetMatrix();
			}
		});
		
		FoldersAdapter.TreeNodeComparator = new ActionComparer();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if(document instanceof AliantaOfferImpl && ((AliantaOfferImpl) document).isEditable()) {
			AdapterView.AdapterContextMenuInfo mi = (AdapterContextMenuInfo) menuInfo;		
			TreeNode tn = (TreeNode) adapter.getItem(mi.position);
			if(tn instanceof FolderTreeNode) {
				getMenuInflater().inflate(R.menu.folder_select, menu);
				return;
			}
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	protected Filter createZeroPositionFilter() {
		return new ZeroQtyFilter();
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.itSelect) {
			AdapterView.AdapterContextMenuInfo mi = (AdapterContextMenuInfo)item.getMenuInfo();		
			TreeNode tn = (TreeNode) adapter.getItem(mi.position);
			if(tn instanceof FolderTreeNode) {
				FolderTreeNode fn = (FolderTreeNode)tn;
				fn.loadNodes();
				PriceImpl pi = new PriceImpl();
				Price p = pi.getData();
				
				for(TreeNode chn : fn.getChilds()) {
					if(chn instanceof PriceTreeNode) {
						p.id = ((PriceTreeNode) chn).getId();
						pi.read();
						((AliantaOfferImpl)document).addItem(p);
					}
				}
				pi.close();
				document.write();
				adapter.notifyDataSetChanged();
			}			
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		items.add(ACTION_MATRIX);
		return items;
	}
	
	@Override
	protected void applayMatrix(String matrixName) {
		if(matrixName.equals(ACTION_MATRIX)) {
			applayAdapter(new ActionAdapter(this, 
					(document == null? "" : document.getId()), 
					(document == null ? null : document.getDate())));
			return;
		}
		super.applayMatrix(matrixName);
	}
	
	public class ActionComparer extends TreeNodeCmp {
		@Override
		public int compare(TreeNode object1, TreeNode object2) {
			
			if(document != null && object1 instanceof PriceTreeNode && object2 instanceof PriceTreeNode) {
				List<ActionHelper.ActionData> actions = ActionHelper.getActions(document.getId(), document.getDate(), ((PriceTreeNode)object1).getId());
				boolean haveActions1 = (actions != null && actions.size() > 0);
				actions = ActionHelper.getActions(document.getId(), document.getDate(), ((PriceTreeNode)object2).getId());
				boolean haveActions2 = (actions != null && actions.size() > 0);
				if(haveActions1 != haveActions2)
					return haveActions1 ? -1 : 1;
			}
			return super.compare(object1, object2);
		}
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
		if( DocType.getCurDoc() == OrderDoc.instance() && document.getRowid() != ExtrasConst.INVALID_ROWID ) {
			if( lastOrder != document.getRowid() ) {
				lastOrder = document.getRowid();
				useMatrix = true;
			}
		}
		
		if(useMatrix) {
			findViewById(R.id.ibNextPrice).setVisibility(View.VISIBLE);
			DbWriter.checkDBTable(UserAssortMtx.class);
			UserAssortMtx data  = new UserAssortMtx();
			DbReader reader = new DbReader();
			if( reader.select(data, data.getTableName(), null, null) ) {
				MatrixImpl mi = new MatrixImpl();
				Matrix m = mi.getData();
				m.name = data.matrix;
				boolean readed = mi.read();
				mi.close();
				if(readed && m.items.size() > 0)
					return new MatrixAdapter(this, data.matrix);
			}
		}
		useMatrix = false;
		findViewById(R.id.ibNextPrice).setVisibility(View.GONE);
		return super.createListAdapter();		
	}
	
	
	@Override
	protected void setName(View view, Price p, int linesCount, PriceTreeNode node) {
		super.setName(view, p, linesCount, node);

		ImageView iv = (ImageView)view.findViewById(R.id.iAction);
		if( iv != null ) {
			List<ActionHelper.ActionData> actions = document == null ? null : ActionHelper.getActions(document.getId(), document.getDate(), p.id);
			iv.setImageResource( (actions != null && actions.size() > 0) ? R.drawable.action : R.drawable.empty );
		}
	}
}
class ZeroQtyFilter extends Filter {
	public ZeroQtyFilter() {
		super(ZeroPositionFilter.NAME);
		where = "id in (select distinct id from whqty where qty > 0)"; 
	}
}

class ActionAdapter extends FoldersAdapter {

	List<String> items;
	
	public ActionAdapter(WarehouseManager warehouse, String orgId, Date date) {
		super(warehouse);
		
		FoldersAdapter.resetCache();
		items = ActionHelper.getActionItems(orgId, date);
	}
	
	@Override
	public boolean inset(long rowid, String id, int folder) {
		return items.contains(id);
	}
}

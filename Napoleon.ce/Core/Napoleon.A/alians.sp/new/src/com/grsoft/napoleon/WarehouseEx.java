package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.FolderEx;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.MatrixItemEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Price2Ex;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.SelectMatrix;
import com.grsoft.dataobjects.SelectMatrixImpl;
import com.grsoft.dataobjects.impl.CategoriesImpl;
import com.grsoft.dataobjects.impl.FolderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.CategAdapter;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.FoldersAdapterEx;
import com.grsoft.util.TreeNodeCmp;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseAdapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class WarehouseEx extends WarehouseNew {
	private static final String ADAPTER_NAME = "com.grsoft.napoleon.WarehouseEx.ADAPTER_NAME";
	List<MatrixItemEx> orgmtx = new ArrayList<MatrixItemEx>();
	private Set<String> active = new HashSet<String>();
	private static final String DOC_ID = "doc_id";
	TextView tvMatrixName;
	ImageButton btnMatrix;
	private ImageButton btnAdapter;
 	
	public static void open(Context context, String id){
		Intent intent = new Intent(context,  Warehouse.activity);
		intent.putExtra(DOC_ID, id);
		context.startActivity(intent);
	}
	
	static public void open(Context context, Document<?> doc, int folderID, boolean editMode) {
		Intent i = new Intent(context, Warehouse.activity);
		
		if( doc != null ) {
			i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
			i.putExtra(ExtrasConst.ORG_ID_STR, doc.getId());
			i.putExtra(ExtrasConst.FOLDER_ID, folderID);
			i.putExtra(ExtrasConst.EDIT_MODE_STR, editMode);
		}
		
		context.startActivity(i);		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		OrgImpl oi = new OrgImpl();
		Org org = oi.getData();
		org.id = document.getId();
		oi.read();
		oi.close();

		OrgEx oe = (OrgEx) org;
		for (MatrixItemEx mie : oe.matrix)
			orgmtx.add(mie);

		btnMatrix.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DLG_MATRIX);
			}
		});
		
		Intent intent = getIntent();
		
		if(intent != null){
			String docID = intent.getStringExtra(DOC_ID);
			if(docRowId == ExtrasConst.INVALID_ROWID && docID != null && docID.length() > 0)
				((Order)document.getData()).id = docID;
		}
		
		btnAdapter.setOnClickListener(btnAdapterClick);
		updateAdapterNameButton();
	}

	@Override
	protected int calcCellWidth(DisplayMetrics metrics) {
		return (int)getResources().getDimension(R.dimen.cost_cell_width);
	}
	
	private OnClickListener btnAdapterClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			FoldersAdapter.resetCache();
			
			String adapterName = CategAdapter.CATEG_ADAPTER;
			
			if (adapter instanceof CategAdapter)
				adapterName = FoldersAdapter.FOLDERS_ADAPTER;
			
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(v.getContext());
			Editor ed = sp.edit();
			ed.putString(ADAPTER_NAME, adapterName);
			ed.commit();
			
			updateAdapterNameButton();
			applayAdapter((WarehouseAdapter) createListAdapter());
		}
	};
	
	@Override
	protected void postInitUI() {
		btnMatrix = (ImageButton) findViewById(R.id.btnMatrix);
		tvMatrixName = (TextView) findViewById(R.id.tvMatrixName);
		btnAdapter = (ImageButton) findViewById(R.id.btnAdapter);
	}

	protected void updateAdapterNameButton() {
		Drawable caption = getResources().getDrawable(R.drawable.price1);
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		
		if (sp.getString(ADAPTER_NAME, FoldersAdapter.FOLDERS_ADAPTER).equals(FoldersAdapter.FOLDERS_ADAPTER))
			caption = getResources().getDrawable(R.drawable.price2);
		
		btnAdapter.setImageDrawable(caption);
	}

	@Override
	protected void postAdapterInit() {
		DbWriter.checkDBTable(SelectMatrix.class);
		SelectMatrixImpl selectMtx = new SelectMatrixImpl();
		selectMtx.read("id", document.getId());
		
		if(selectMtx.getData().name.trim().length() > 0){
			String name = selectMtx.getData().name;
			
			if(name.equals(PRICE_WITHOUT_MATRIX))
				startBuildSet();
			else
				applayMatrix(name);
		}else if(AssortmentMatrixAdapter.hasAssortiment(document.getId()))
			applayMatrix(AssortmentMatrixAdapter.TITLE);
		else
			startBuildSet();
	}

	private void startBuildSet(){
		preResetMatrix();
		adapter.buildSet(folderID);
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.warehouseex;
	}

	@Override
	protected int getItemLayoutId() {
		return R.layout.priceitemrowex;
	}

	@Override
	protected void updateChildPriceView(View view, Price p) {
		TextView tv = (TextView) view.findViewById(R.id.tvType);
		Price2Ex pe = (Price2Ex) p;
		tv.setText(pe.type);
		
		ImageView iv = (ImageView) view.findViewById(R.id.imageView);
		
		if(iv != null)
			iv.setImageResource(getNdsResource(((PriceEx)p).nds));
		
		
		int disc = ((CostStrategy2Ex)CostStrategy.defaultInstance).getDiscount(pe, document);
		
		tv = (TextView) view.findViewById(R.id.tvDisc);
		tv.setText(Util.IntToScaleStr(disc, Consts.SUM_SCALE) + " %");
	}

	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		ArrayList<String> result = super.prepareMatrixList(items);
		result.add(AssortmentMatrixAdapterA.TITLE);
		result.add(AssortmentMatrixAdapterAB.TITLE);
		result.add(AssortmentMatrixAdapterABC.TITLE);

		return result;
	}
	
	protected BaseAdapter createListAdapter() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String adapterName = pref.getString(ADAPTER_NAME, FoldersAdapter.FOLDERS_ADAPTER);
		
		FoldersAdapter ret = null;
		
		if(adapterName.equals(FoldersAdapter.FOLDERS_ADAPTER))
			ret = new FoldersAdapterEx(this);
		else
			ret = new CategAdapter(this);
		ret.putFilter(new HiddenFilter());
		
		if (Features.SHOW_ZERO_FILTER)
			ret.putFilter(createZeroPositionFilter());

		tvMatrixName.setText(matrixName);

		return ret;
	}

	@Override
	protected void resetMatrix() {
		btnAdapter.setEnabled(true);
		preResetMatrix();
		super.resetMatrix();
	}

	protected void preResetMatrix() {
		FoldersAdapter.TreeNodeComparator = new TreeNodeCmp();
		
		SelectMatrixImpl selectMtx = new SelectMatrixImpl();
		selectMtx.getData().id = document.getId();
		selectMtx.getData().name = PRICE_WITHOUT_MATRIX;
		selectMtx.write();
		selectMtx.close();
	}
	
	
	@Override
	protected void applayMatrix(String matrixName) {
		btnAdapter.setEnabled(false);
		
		active.clear();
		FoldersAdapter.TreeNodeComparator = new NodeComparer();
		this.matrixName = matrixName;
		
		SelectMatrixImpl selectMtx = new SelectMatrixImpl();
		selectMtx.getData().id = document.getId();
		selectMtx.getData().name = this.matrixName;
		selectMtx.write();
		selectMtx.close();

		if (matrixName.equals(AssortmentMatrixAdapter.TITLE))
			applayAdapter(new AssortmentMatrixAdapterEx(this, document.getId()));
		else if (matrixName.equals(AssortmentMatrixAdapterA.TITLE))
			applayAdapter(new AssortmentMatrixAdapterA(this, document.getId()));
		else if (matrixName.equals(AssortmentMatrixAdapterAB.TITLE))
			applayAdapter(new AssortmentMatrixAdapterAB(this, document.getId()));
		else if (matrixName.equals(AssortmentMatrixAdapterABC.TITLE))
			applayAdapter(new AssortmentMatrixAdapterABC(this, document.getId()));

		tvMatrixName.setText(matrixName);
	}

	public void background(View result, TreeNode node) {
		if (matrixName != AssortmentMatrixAdapter.TITLE
				&& node instanceof PriceTreeNode) {
			PriceTreeNode pnt = (PriceTreeNode) node;
			if (active.contains(pnt.getId()))
				result.setBackgroundResource(R.drawable.active_row_selector);
		}
	}
	
	

	class AssortmentMatrixAdapterType extends AssortmentMatrixAdapter {
		public AssortmentMatrixAdapterType(WarehouseNew warehouse, String id) {
			super(warehouse, id);
		}

		@Override
		protected List<MatrixItem> getMatrixItems() {
			List<MatrixItem> result = super.getMatrixItems();
			Set<Integer> activeFolder = new HashSet<Integer>();
			
			for (MatrixItem mi : result){
				if (!active.contains(mi.id))
					active.add(mi.id);
				
				price.getData().id = mi.id;
				price.read();
				
				if(!activeFolder.contains(price.getData().folderID))
					activeFolder.add(price.getData().folderID);
			}

			if(orgmtx.size() > 0)
				for (MatrixItem mi : orgmtx) {
					price.getData().id = mi.id;
					price.read();
	
					if (!active.contains(mi.id) && activeFolder.contains(price.getData().folderID) && cond((Price2Ex) price.getData()))
						result.add(mi);
				}
			else{
				DbReader reader = new DbReader();
				Price2Ex data = new Price2Ex();
				boolean bdo = reader.select(data, DataObjectInfo.getInstance().getTableName(data.getClass()), null);
				
				while(bdo){
					if (!active.contains(data.id) && activeFolder.contains(data.folderID) && cond((Price2Ex) data)){
						MatrixItem mi = new MatrixItem();
						mi.id = data.id;
						result.add(mi);
					}
					
					bdo = reader.selectNext(data);
				}
				
				reader.close();
			}
				
			price.close();
			
			return result;
		}

		protected boolean cond(Price2Ex p) {
			return false;
		}

		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			View result = super.getView(arg0, convertView, arg2);

			TreeNode node = (TreeNode) getItem(arg0);
			background(result, node);
			return result;
		}
	}

	class AssortmentMatrixAdapterEx extends AssortmentMatrixAdapterType {
		public AssortmentMatrixAdapterEx(WarehouseNew warehouse, String id) {
			super(warehouse, id);
		}
	}

	class AssortmentMatrixAdapterA extends AssortmentMatrixAdapterType {
		public static final String TITLE = "<Активный ассортимент> + группа A";

		public AssortmentMatrixAdapterA(WarehouseNew warehouse, String id) {
			super(warehouse, id);
		}

		protected boolean cond(Price2Ex p) {
			return p.type.equals("A");
		}

	}

	class AssortmentMatrixAdapterAB extends AssortmentMatrixAdapterType {
		public static final String TITLE = "<Активный ассортимент> + группа AB";

		public AssortmentMatrixAdapterAB(WarehouseNew warehouse, String id) {
			super(warehouse, id);
		}

		protected boolean cond(Price2Ex p) {
			return p.type.equals("A") || p.type.equals("B");
		}

	}

	class AssortmentMatrixAdapterABC extends AssortmentMatrixAdapterType {
		public static final String TITLE = "<Активный ассортимент> + группа ABC";

		public AssortmentMatrixAdapterABC(WarehouseNew warehouse, String id) {
			super(warehouse, id);
		}

		protected boolean cond(Price2Ex p) {
			return p.type.equals("A") || p.type.equals("B")
					|| p.type.equals("C");
		}
	}

	class NodeComparer extends TreeNodeCmp {
		@Override
		public int compare(TreeNode object1, TreeNode object2) {
			if (object1 instanceof PriceTreeNode
					&& object2 instanceof PriceTreeNode) {
				PriceTreeNode lhs = (PriceTreeNode) object1;
				PriceTreeNode rhs = (PriceTreeNode) object2;

				if (active.contains(lhs.getId())
						&& !active.contains(rhs.getId()))
					return -1;
				else if (!active.contains(lhs.getId())
						&& active.contains(rhs.getId()))
					return 1;
				else
					super.compare(object1, object2);
			}
			return super.compare(object1, object2);
		}
	}
	
	private FolderImpl folder = new FolderImpl();
	private CategoriesImpl categories = new CategoriesImpl();
	
	@Override
	public View getFolderView(FolderTreeNode node, View convertView) {
		View result = super.getFolderView(node, convertView);
		
		FolderEx fex = (FolderEx) folder.getData();
		
		if(adapter instanceof CategAdapter)
			fex = categories.getData();
		
		DbReader reader = new DbReader();
		fex.id = node.id;
		
		if (reader.read(fex, DataObjectInfo.getInstance().getTableName(fex.getClass())) != -1) {
			TextView tv = (TextView) result.findViewById(R.id.tvItemSelectRowName);
			tv.setTextColor(getResources().getColor(R.color.black));
			
			String clr = fex.color.trim();
			
			if(clr.length() > 0){
				final char SHARP = '#';
				
				if(clr.charAt(0) != SHARP)
					clr= SHARP + clr;
				
				try{
					tv.setTextColor(Color.parseColor(clr));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			tv = (TextView) result.findViewById(R.id.tvItemSelectRowName);
			int r = getNdsResource(fex.nds);  
			tv.setCompoundDrawablesWithIntrinsicBounds(r, 0, 0, 0);
		}
		reader.close();
		return result;
	}
	
	private int getNdsResource(int val){
		return val > 0 ?  R.drawable.btn_check_on : R.drawable.btn_check_off; 
	}
	
}

class HiddenFilter extends Filter {
	public HiddenFilter() {
		super("Hidden");
		where = "hidden = 0";
	}
}
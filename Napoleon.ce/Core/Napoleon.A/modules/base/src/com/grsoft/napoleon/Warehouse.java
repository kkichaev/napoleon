/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма для выбора Price
 *
 * kki   07/10/2010   creating
 */
package com.grsoft.napoleon;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.FoldersTree;
import com.grsoft.database.PricePhotoHitching;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.MatrixImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PresentImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.documents.DocItemsStock;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.FindOnClickListener;
import com.grsoft.napoleon.util.FindTextWatcher;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.view.RegDurationActivity;

public class Warehouse extends RegDurationActivity
	implements DataSetNotify
{
	private static final int DLG_MATRIX = 0;
	protected static final int VISIBLE_COLUMNS = 1;

	public static Class<? extends Activity> activity = Warehouse.class;
	
	public static final int COLUMN_NONE = 0;
	public static final int COLUMN_QTY_WH = 1;
	public static final int COLUMN_QTY_ORD = 2;
	public static final int COLUMN_COST = 3;
	public static final int COLUMN_SUM = 4;
	public static final int COLUMN_QTY_WH_ORD = 5;
	public static final int COLUMN_COST_SUM = 6;
	
	public FoldersTree foldersTree;
	protected ListView lvItemSelect;
	protected TextView tvItemSelectUpLevel;
	protected ImageButton btnUp;
	protected ImageButton btnDown;

	long docRowId = ExtrasConst.INVALID_ID;
	protected Document<?> document;

	protected RemnantsImpl remnantsDoc;
	protected PriceImpl price = new PriceImpl();
	protected boolean editMode;
	protected ImageView ivGoUp;
	protected ArrayList<String> lastBuyingItems = new ArrayList<String>();
	protected EditText edFind;
	protected FindOnClickListener findOnClickListener;
	protected boolean zeroPozitionFiltered = false;
	protected String PRICE_WITHOUT_MATRIX;
	protected String matrixName;
	protected int folderID = -1;
	protected FindTextWatcher textWatcher;
	
	LinesCountController linesController;
	
//	boolean cached = true;

	boolean expanded = false;
	WarehouseMover priceMover;
	
	boolean inPackMode = false;	
	HashSet<String> packQty = null;
	protected GoUpLevelClickListener goUpLevelClickListener;

	static public void open(Context context) { 
		open(context, null, false); 
	}
	
	static public void open(Context context,  Document<?> doc, boolean editMode) {
		Intent i = new Intent(context, activity);
		
		if( doc != null ) {
			i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
			i.putExtra(ExtrasConst.ORG_ID_STR, doc.getId());
			i.putExtra(ExtrasConst.EDIT_MODE_STR, editMode);
		}
		context.startActivity(i);		
	}
	
	static public void open(Context context, Document<?> doc, int folderID) {
		Intent i = new Intent(context, activity);
		
		if( doc != null ) {
			i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
			i.putExtra(ExtrasConst.ORG_ID_STR, doc.getId());
			i.putExtra(ExtrasConst.FOLDER_ID, folderID);
		}
		context.startActivity(i);		
	}
	
	protected ItemSelectAdapter createItemAdapter() { return new ItemSelectAdapter(); }

	protected int getItemLayoutId() { return  R.layout.priceitemrow; }
	
	protected int getLayoutId() { return R.layout.warehouse; }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, document.getRowid());
		outState.putString(ExtrasConst.ORG_ID_STR, document.getId());
		outState.putBoolean(ExtrasConst.EDIT_MODE_STR, editMode);
		if( folderID != -1 )
			outState.putInt(ExtrasConst.FOLDER_ID, folderID);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());
		
		PRICE_WITHOUT_MATRIX = getString(R.string.all_price);
		matrixName = PRICE_WITHOUT_MATRIX;
		
//		Debug.startMethodTracing("wh2");
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		
		try{
			editMode = true;
			if( b != null ) {
				docRowId = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
				editMode = b.getBoolean(ExtrasConst.EDIT_MODE_STR, true);
				folderID = b.getInt(ExtrasConst.FOLDER_ID, -1);
			}
	
			document = DocType.getCurDoc().create();
			if (!(document instanceof Itemsable)) {
				document = OrderDoc.instance().create();
			}
			
//			if (docRowId != ExtrasConst.INVALID_ID)
//				document.read(docRowId);
//						
			tvItemSelectUpLevel = (TextView) findViewById(R.id.tvItemSelectUpLevel);
			lvItemSelect = (ListView) findViewById(R.id.lvOrderItemSelect);
			lvItemSelect.setDividerHeight(0);
			
			registerForContextMenu(lvItemSelect);
						
			updateTotalSum();
			
			foldersTree = (FoldersTree) getLastNonConfigurationInstance();
			if( foldersTree == null )
				foldersTree = createFoldersTree();
			
			btnUp = (ImageButton) findViewById(R.id.btnUp);
			btnDown = (ImageButton) findViewById(R.id.btnDown);
			
			btnDown.setOnClickListener(new DownClickListener());
			btnUp.setOnClickListener(new UpClickListrner());
			tvItemSelectUpLevel.setTag(foldersTree.top);
			goUpLevelClickListener = new GoUpLevelClickListener();
			tvItemSelectUpLevel.setOnClickListener(goUpLevelClickListener);
			ivGoUp = (ImageView) findViewById(R.id.ivGoUp);
			ivGoUp.setOnClickListener(goUpLevelClickListener);
			ivGoUp.setVisibility(View.INVISIBLE);
			
			lvItemSelect.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
					if (matrixName.equals(PRICE_WITHOUT_MATRIX))
						ivGoUp.setVisibility(View.VISIBLE);
					
					TextView tvItemSelectRowName = (TextView) arg1.findViewById(R.id.tvItemSelectRowName);
					if (tvItemSelectRowName != null) {
						FolderTreeNode node = (FolderTreeNode) tvItemSelectRowName.getTag();
						setTitleText(node);
						foldersTree.setTop(node);
						arg1.setTag(zeroPozitionFiltered);
						node.onClick(arg1);
						((ItemSelectAdapter)lvItemSelect.getAdapter()).notifyDataSetChanged();
						
						if (node.hasChilds())
							lvItemSelect.setSelection(0);
					}
					else {
						TextView tvPriceItemName = (TextView) arg1.findViewById(R.id.tvPriceItemName);
						if (tvPriceItemName != null){
							PriceTreeNode node = (PriceTreeNode) tvPriceItemName.getTag();
//							cached = false;
							nodeClicked(node, arg1, pos);
						}
					}
				}
			});
	
			lvItemSelect.setAdapter(createItemAdapter());
			ImageButton btnLines = (ImageButton) findViewById(R.id.btnLines);
			LinesOnClickListener linesOnClickListener = new LinesOnClickListener(
					lvItemSelect, btnLines, this);
			linesController = linesOnClickListener.getController();
			
			edFind = (EditText) findViewById(R.id.edFind);
			textWatcher = new FindTextWatcher(edFind, lvItemSelect);
			edFind.addTextChangedListener(textWatcher);
			
			View v = findViewById(R.id.llFind);
			if( v != null ) {
				v.setVisibility(View.GONE);
				if( Features.ID_COLUMN_IN_PRICE_LIST ) {
					View v1 = v.findViewById(R.id.spFind);
					if( v1 != null )
						v1.setVisibility(View.VISIBLE);
				}
			} else
				edFind.setVisibility(View.GONE);

			findOnClickListener = new FindOnClickListener(edFind, lvItemSelect, v);
			ImageButton btnFind = (ImageButton) findViewById(R.id.btnFind);
			btnFind.setOnClickListener(findOnClickListener);
			v = findViewById(R.id.btnDelFind);
			if( v != null ) {
				v.setOnClickListener(new OnClickListener() {
					@Override public void onClick(View v) { edFind.setText(""); }
				});
			}
			
			OrgImpl org = new OrgImpl();			
			String orgId = getIntent().getStringExtra(ExtrasConst.ORG_ID_STR);		
			if (orgId != null)
			{
				org.getData().id = orgId;
				org.read();
				org.close();
				DocType dt = DocType.getCurDoc();
				if( dt instanceof DocItemsStock)
					((DocItemsStock)dt).getItemsFromLastDoc(orgId, lastBuyingItems, Features.LAST_SALED_ITEMS_PERIOD);
			}
			
			if( Features.HAVE_PRICE_MOVER ) {
				priceMover = new WarehouseMover();
				PriceCountW.PriceMover = priceMover;
			}
			
			if( Features.PACK_INPUT ) {
				packQty = new HashSet<String>();
				ImageView iv = (ImageView)findViewById(R.id.btnPack);
				if( iv != null && document instanceof OrderImplBase<?> && docRowId != ExtrasConst.INVALID_ID ) {
					iv.setVisibility(View.VISIBLE);
					iv.setOnClickListener(new View.OnClickListener() {
						@Override public void onClick(View v) { packing(); }
					});
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected void onResume() {
		if( Features.HAVE_PRICE_MOVER && priceMover != null ) {
			int fid = priceMover.getFolderID();
			if( fid >= 0 )
				folderID = fid;
			priceMover.init();
		}

		if(folderID != -1)
			setFolder(folderID);

		document.read(docRowId, false);
		
		if( OrderImpl.class.isAssignableFrom(document.getClass()) && document.getRowid() != ExtrasConst.INVALID_ID) {
			long rid = RemnantsImpl.find(document.getId(), ((OrderImpl)document).getData().created);
			if( rid != ExtrasConst.INVALID_ID ) {
				if( remnantsDoc == null )
					remnantsDoc = new RemnantsImpl();
				remnantsDoc.read(rid, false);
			}
		}
		
		notifyDataSetChanged();
		updateTotalSum();

//		Debug.stopMethodTracing();
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		if( Features.HAVE_PRICE_MOVER )
			PriceCountW.PriceMover = null;

		document.close();
		if( remnantsDoc != null )
			remnantsDoc.close();
				
		super.onDestroy();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		TreeNode tn = (TreeNode)(lvItemSelect.getItemAtPosition(((AdapterContextMenuInfo)menuInfo).position));
		if( tn != null && (tn instanceof PriceTreeNode)) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.wh_price_menu, menu);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itMoveToFolder){
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
			PriceTreeNode tn = (PriceTreeNode)(lvItemSelect.getItemAtPosition(menuInfo.position));
			PriceImpl pi = new PriceImpl();
			pi.read(tn.getPriceId());
			pi.close();
			
			findOnClickListener.resetFilter();
			matrixName = PRICE_WITHOUT_MATRIX;
			foldersTree = createFoldersTree(zeroPozitionFiltered);
			setFolder(pi.getData().folderID);
			return true;
		} else
			return super.onContextItemSelected(item);
	}
	
	protected void packing() {
		int resId = R.drawable.pack_off;
		
		inPackMode = !inPackMode;
		if(inPackMode) {
			resId = R.drawable.pack_on;
		} else {
			if(packQty.size() > 0) {
				InputNumberDlg.open(this, new InputNumber() {
					@Override public int getValue() { return Consts.QTY_SCALE; }					
					@Override public void applayInput(int value, Object... params) { 
						changeQty(value, (params==null) ? false : (Boolean)params[0]); }
				}, Consts.QTY_SCALE, true, getString(R.string.input_qty), true);
			}
		}
		
		ImageView iv = (ImageView)findViewById(R.id.btnPack);
		iv.setImageResource(resId);
	}
	
	@SuppressWarnings("unchecked")
	protected void changeQty(int value, boolean inPack) {
		PriceImpl p = new PriceImpl();
		Price prc = p.getData();
		OrderImplBase<?> o = (OrderImplBase<?>)document;
		CostStrategy cs = CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass());
		for(String e : packQty) {
			prc.id = e;
			if( p.read() ) {
				int cv = value;
				if( inPack )
					cv = (int)((long)value * p.getData().qtyInPack) / Consts.QTY_SCALE;
				o.updateQty(p, cv, cs.getItemCost(prc, o), inPack);
			}
		}
		p.close();
		packQty.clear();
		updateTotalSum();
		notifyDataSetChanged();
	}
	
	protected void nodeClicked(PriceTreeNode node, View view, int pos) {
		if( Features.PACK_INPUT && inPackMode ) {
			price.read(node.getPriceId());
			String id = price.getData().id;
			if( packQty.contains(id) )
				packQty.remove(id);
			else
				packQty.add(id);
			((BaseAdapter)lvItemSelect.getAdapter()).notifyDataSetChanged();
		} else
			node.onClick(view);
	}

	protected FoldersTree createFoldersTree() {
		return new FoldersTree((Itemsable) document);
	}
	
	protected FoldersTree createFoldersTree(String matrixName, String filter, boolean zeroFilter) {
		return new FoldersTree(matrixName, filter, zeroFilter, (Itemsable) document);
	}
	
	protected FoldersTree createFoldersTree(String matrixName, boolean zeroFilter){
		return new FoldersTree(matrixName, zeroFilter, (Itemsable) document);
	}
	
	protected FoldersTree createFoldersTree(boolean zeroFilter){
		return new FoldersTree(zeroFilter, (Itemsable) document);
	}

	protected void updateTotalSum() {
//		int weight = 0;
//		if( document instanceof OrderImplBase<?> )
//			weight = ((OrderImplBase<?>)document).weight();

		updateTotalSum(document.sum(), 0);
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() { return foldersTree;	}

	@Override
	protected void onStop() {
		super.onStop();
		document.close();
		price.close();
	}
	
	protected void setTitleText(FolderTreeNode folder) {
		tvItemSelectUpLevel.setTag(folder);
		tvItemSelectUpLevel.setText(((folder != null) ? folder.name : getString(R.string.price)));			
	}
	
	protected void setAsTopLevelGoUp() {
		setTitleText(null);
		ivGoUp.setVisibility(View.INVISIBLE);
	}
	
	protected void setFolder(int folderId) {
		if( foldersTree.findFolder(folderId) ) {
			
			if (foldersTree.isTop())
				setAsTopLevelGoUp();
			else{
				setTitleText((FolderTreeNode)foldersTree.top);
				View v = new View(this);
				v.setTag(zeroPozitionFiltered);
				foldersTree.top.onClick(v);
				ivGoUp.setVisibility(View.VISIBLE);
			}
			((ItemSelectAdapter)lvItemSelect.getAdapter()).notifyDataSetChanged();
		}
	}
	
	class GoUpLevelClickListener implements OnClickListener {

		@Override
		public void onClick(View v)
		{
			TreeNode node = (TreeNode) tvItemSelectUpLevel.getTag();
			
			if(node == null)
				return;
			
			int pos = 0;
			TreeNode parent = node.getParent();
			if(parent != null)
			{
				pos = parent.indexOf(node);
				setTitleText((FolderTreeNode)(parent));
				
				ItemSelectAdapter adapter = (ItemSelectAdapter)lvItemSelect.getAdapter(); 
				foldersTree.setTop((TreeNode) parent);
				adapter.notifyDataSetChanged();
				ivGoUp.setVisibility(View.VISIBLE);
			}
			
			if (foldersTree.isTop())
				setAsTopLevelGoUp();
			
			findOnClickListener.resetFilter();
			lvItemSelect.setSelection(pos);
			
			expanded = false;
			onMoveUp();
		}
	}
	
	protected void onMoveUp() {}
	
	private void setNextFolder(String sql){
		Cursor c = null;
		try{
			c = DataBaseManager.getDataBase().rawQuery(sql, null);
			if(c.moveToFirst())
				setFolder(c.getInt(0));
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(c != null)
				c.close();
		}
	}
	
	protected boolean moveNext() {
		StringBuilder sql = new StringBuilder("select min(folderID) from price");
		FolderTreeNode topNode = (FolderTreeNode) foldersTree.getTop();
		
		if(topNode.id > 0)
			sql.append(" where folderID >").append(topNode.id);
		
		setNextFolder(sql.toString());
		return true;
		
//		if(foldersTree.isTop())
//			return false;
//		expanded = false;
//		return foldersTree.nextLeaf();
//		return true;
	}
	
	protected boolean movePrev() {
		StringBuilder sql = new StringBuilder("select max(folderID) from price");
		FolderTreeNode topNode = (FolderTreeNode) foldersTree.getTop();
		
		if(topNode.id > 0)
			sql.append(" where folderID <").append(topNode.id);
		
		setNextFolder(sql.toString());		
		
		return true;
//		if(foldersTree.isTop())
//			return false;
//		expanded = false;
//		return foldersTree.prevLeaf();
//		return true;
	}
	
	class DownClickListener extends OnClickListenerToNotify {

		@Override
		public void onClick(View v)
		{
			super.onClick(v);
			
			if (matrixName.equals(PRICE_WITHOUT_MATRIX) && moveNext() ) 
				((ItemSelectAdapter)lvItemSelect.getAdapter()).notifyDataSetChanged();
		}
		
	}
	
	class UpClickListrner extends OnClickListenerToNotify
	{

		@Override
		public void onClick(View v)
		{
			super.onClick(v);
			
			if(matrixName.equals(PRICE_WITHOUT_MATRIX) && movePrev() ) 
				((ItemSelectAdapter)lvItemSelect.getAdapter()).notifyDataSetChanged();
		}
		
	}

	class ItemSelectAdapter extends BaseAdapter implements FilterAdapter {
		private ArrayList <PriceTreeNode> filterNodes;
		
		@Override
		public int getCount() {
			if (filterNodes != null)
				return filterNodes.size();
			else if (foldersTree.top instanceof FolderTreeNode) {
				FolderTreeNode ftn = (FolderTreeNode) foldersTree.top;
				
				if (ftn.getChilds() == null)
					return 0;
				else
					return ftn.getChilds().size();
			}
			else
				return 0;
		}

		@Override
		public Object getItem(int arg0) {
			if (filterNodes != null)
				return filterNodes.get(arg0);
			else
				return (TreeNode) foldersTree.top.getChilds().get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}
		
		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			TreeNode node = (TreeNode) getItem(arg0);
			View view = node.isFolderNode()
					? getFolderView((FolderTreeNode) node, convertView)
					: getPriceView((PriceTreeNode) node, convertView);
			
			if (view != null)
				view.setBackgroundResource(arg0 % 2 != 0 ? 
					R.drawable.even_row_selector :
					R.drawable.list_selector);
			
			return view;
		}
		
		public int getPos(TreeNode node) {
			return foldersTree.top.getChilds().indexOf(node);
		}
		
		private void collectFolderID(TreeNode node, List<Integer> fids) {
			addFolderId(node, fids);			
			
			for (TreeNode child: node.getChilds()) {
				if (child.hasChilds())
					collectFolderID(child, fids);
				else
					addFolderId(child, fids);
			}
		}
		
		private void addFolderId(TreeNode node, List<Integer> fids) {
			if (node instanceof FolderTreeNode) {
				FolderTreeNode ftn = (FolderTreeNode)node;
				
				if (!fids.contains(ftn.id))
					fids.add(ftn.id);
			}	
		}
	
		@Override
		public void applyFilter(String value) {
			if (value.length() == 0) {
				//if (filterNodes != null)
				resetFilter();
				return;
			}
			
			boolean searchingByID = false;
			View v = findViewById(R.id.llFind);
			if( Features.ID_COLUMN_IN_PRICE_LIST && v != null ) {
				Spinner sp = (Spinner) v.findViewById(R.id.spFind);
				if( v != null )
					searchingByID = (sp.getSelectedItemPosition() == 1);
			}

			if (matrixName.equals(PRICE_WITHOUT_MATRIX)) {
				if (filterNodes == null)
					filterNodes = new ArrayList<PriceTreeNode>();
			
				filterNodes.clear();
				ArrayList<Integer> fids = new ArrayList<Integer>();
				collectFolderID(foldersTree.top, fids);
				readFilteredNodes(value, fids, null, searchingByID);
			} else {
				if( !searchingByID )
					foldersTree = createFoldersTree(matrixName, value, zeroPozitionFiltered);
			}
			
			notifyDataSetChanged();
		}
		
		public void expandPrice(boolean expand) {
			if (matrixName.equals(PRICE_WITHOUT_MATRIX)){
				if( !expand )
					resetFilter();
				else {
					if (filterNodes == null)
						filterNodes = new ArrayList<PriceTreeNode>();
				
					filterNodes.clear();
					ArrayList<Integer> fids = new ArrayList<Integer>();
					collectFolderID(foldersTree.top, fids);
					readFilteredNodes("", fids, "name", false);					
					notifyDataSetChanged();
				}
			}
		}
		
		class PriceSearch extends DataObject {
			public long rowid;
			public String name;
			public String id;
		}
		
		protected void makeSearchStr(StringBuilder where, String value, boolean srchById) {
			value = value.replace ("|", "||").replace("_", "|_");
			if( srchById )
				where.append("(").append("id LIKE '").append(value).append("%' ESCAPE '|' )");
			else
				where.append("(").append("srchName LIKE '%").append(value.toUpperCase()).append("%' ESCAPE '|' )");
		}
		
		private void readFilteredNodes(String value, ArrayList<Integer> fids, String orderStr, boolean srchById) {
			StringBuilder where = new StringBuilder();
			if( value.length() > 0 )
				makeSearchStr(where, value, srchById);

			if (fids.size() > 0){
				String fidsBeforeStr = fids.toString();
				String fidsStr =  fidsBeforeStr.substring(1, fidsBeforeStr.length()-1);
				if( where.length() > 0 )
					where.append(" AND ");
				where.append("folderid IN (").append(fidsStr).append(")");
			}
			
			PriceSearch ps = new PriceSearch();
			DbReader dbr = new DbReader();
			boolean bdo = dbr.select(ps, DataObjectInfo.getInstance().getTableName(price.getData().getClass()), where.toString(), orderStr);
			while( bdo ) {
				PriceTreeNode node = foldersTree.createPriceTreeNode(null, ps.rowid, ps.name, ps.id);
				filterNodes.add(node);
				bdo = dbr.selectNext(ps);
			}
			dbr.close();
		}

		@Override
		public void resetFilter() {
			try{
				filterNodes = null;
				
				if (!matrixName.equals(PRICE_WITHOUT_MATRIX))
					foldersTree = createFoldersTree(matrixName, zeroPozitionFiltered);
				
				notifyDataSetChanged();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		/**
		 * Только освобождает фильтр - ничего больше не делает
		 */
		public void freeFilterNodes() {
			filterNodes = null;
		}
	}
	
	@Override
	public void onBackPressed() {
		if( !editMode && document.getRowid() != ExtrasConst.INVALID_ID )
			document.open(this);

		super.onBackPressed();
	}
	
	@Override
	public void notifyDataSetChanged() {
		BaseAdapter adapter = (BaseAdapter)lvItemSelect.getAdapter();
		
		if (adapter != null)
			adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.warehouse_opt_menu, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem itZeroFilter = menu.findItem(R.id.itZeroFilter);
		if( itZeroFilter != null ) {
			if (zeroPozitionFiltered)
				itZeroFilter.setTitle(R.string.disable_zero_filter);
			else
				itZeroFilter.setTitle(R.string.enable_zero_filter);
		}
		
		MenuItem itPresentation = menu.findItem(R.id.itPresentation);
		File f = new File(PricePhotoHitching.PHOTO_DIRECTORY); 
		
		itPresentation.setVisible(f.exists() &&
				f.isDirectory() && f.list().length > 0 && 
				PresentImpl.count() > 0);
		
		MenuItem it = menu.findItem(R.id.itExpand);
		if( it != null ) {
			if( Features.CAN_EXPAND_PRICE )
				it.setTitle((expanded) ? R.string.show_folder : R.string.show_price);
			else
				it.setVisible(false);
		}

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itZeroFilter) {
			updateForZeroFilter();
			return true;
		} else if (item.getItemId() == R.id.itMatrix) {
			showDialog(DLG_MATRIX);
			return true;
		} else if (item.getItemId() == R.id.itPresentation) {
			Presentation.open(this, document.getRowid(), foldersTree.getFoldersIds(), -1);
			return true;
		} else if (item.getItemId() == R.id.itExpand) {
			expandingPrice();
			return true;
		} else if( item.getItemId() == R.id.itColumns) {
			showDialog(VISIBLE_COLUMNS);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	protected void expandingPrice() {
		expanded = !expanded;
		((ItemSelectAdapter)lvItemSelect.getAdapter()).expandPrice(expanded);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case DLG_MATRIX:
			return createMatrixSelectDlg();
		case VISIBLE_COLUMNS:
			return createColumnsDialog();
		default:
			return super.onCreateDialog(id);
		}
	}
	
	protected Dialog createColumnsDialog() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(R.string.setting_clmns);
		b.setView(View.inflate(this, R.layout.wh_colums_dialog, null));
		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) { refreshColums(dialog); }
		});
		b.setNegativeButton(R.string.cancel, null);
		return b.create();
	}

	protected void refreshColums(DialogInterface d) {
		CfgNplW config = (CfgNplW) ConfigManager.getConfig();
		Dialog dialog = (Dialog)d;
		Spinner sp;

		sp = (Spinner) dialog.findViewById(R.id.spColumn2);
		config.priceClmn2Type = sp.getSelectedItemPosition();
		
		sp = (Spinner) dialog.findViewById(R.id.spColumn3);
		config.priceClmn3Type = sp.getSelectedItemPosition();

		if( Features.ID_COLUMN_IN_PRICE_LIST) {
			CheckBox cb = (CheckBox)dialog.findViewById(R.id.cbItemID);
			config.idInPriceList = cb.isChecked();
		}

		ConfigManager.save();
		
		notifyDataSetChanged();
	}

	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		return items;
	}
	
	private Dialog createMatrixSelectDlg() {
		ArrayList<String> items = new ArrayList<String>();
		items.add(PRICE_WITHOUT_MATRIX);
		List<String> matrixes = MatrixImpl.getNames();
		if( matrixes != null )
			items.addAll(matrixes);
		items = prepareMatrixList(items);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_matrix);
		
		final String[] items_array = new String[items.size()];
		int sel_item = 0;
		
		for(int i = 0; i < items_array.length; i++){
			String item = items.get(i);
			items_array[i] = item;
			
			if(item.equals(matrixName))
				sel_item = i;
		}
		
		builder.setSingleChoiceItems(items_array, sel_item, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		        Toast.makeText(getApplicationContext(), items_array[item], Toast.LENGTH_SHORT).show();
		        matrixName = items_array[item];
		        dialog.dismiss();
		        
		        //Самый первый элемент - без матрицы
		        if (item > 0)
		        	applayMatrix(matrixName);
		        else
		        	resetMatrix();
		    }
		});
		
		return builder.create();
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id){
		case DLG_MATRIX:
			setSelectedCurrMatrix(dialog);
			break;
		case VISIBLE_COLUMNS:
			CfgNplW config = (CfgNplW) ConfigManager.getConfig();
			Spinner sp;

			sp = (Spinner) dialog.findViewById(R.id.spColumn2);
			sp.setSelection(config.priceClmn2Type);
			
			sp = (Spinner) dialog.findViewById(R.id.spColumn3);
			sp.setSelection(config.priceClmn3Type);
			
			if( Features.ID_COLUMN_IN_PRICE_LIST) {
				View v = dialog.findViewById(R.id.trItemID);
				if( v != null ) {
					v.setVisibility(View.VISIBLE);
					CheckBox cb = (CheckBox)dialog.findViewById(R.id.cbItemID);
					cb.setChecked(config.idInPriceList);
				}
			}
			break;
		}
	}

	private void setSelectedCurrMatrix(Dialog dialog) {
		ListView lv = ((AlertDialog)dialog).getListView();
		
		for(int i = 0; i < lv.getCount(); i ++){
			if (lv.getItemAtPosition(i).equals(matrixName)){
				lv.setItemChecked(i, true);
				return;
			}
		}
	}
	
	protected void resetMatrix() {
		applayZeroFilter(true);
	}

	protected void applayMatrix(String matrixName) {
		try{
			foldersTree = createFoldersTree(matrixName, zeroPozitionFiltered);
			ImageView ivFilter = (ImageView) findViewById(R.id.ivFilter);
			ivFilter.setVisibility(zeroPozitionFiltered ? View.VISIBLE : View.GONE); 
			setPriceForTopLevel();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	protected void setPriceForTopLevel() {
		setAsTopLevelGoUp();
		findOnClickListener.resetFilter();
		notifyDataSetChanged();
	}
	
	private void updateForZeroFilter() {
		zeroPozitionFiltered = !zeroPozitionFiltered;
		ImageView ivFilter = (ImageView) findViewById(R.id.ivFilter);
		ivFilter.setVisibility((zeroPozitionFiltered) ? View.VISIBLE : View.GONE);
		applayZeroFilter(false);
	}

	protected void applayZeroFilter(boolean goTop) {
		try{
			FolderTreeNode top = (FolderTreeNode) foldersTree.getTop();
			foldersTree = matrixName.equals(PRICE_WITHOUT_MATRIX) ?
					createFoldersTree(zeroPozitionFiltered) :
					createFoldersTree(matrixName, zeroPozitionFiltered);
			
			if (top != null && !goTop)
				setFolder(top.id);
			else
				setPriceForTopLevel();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void setCurrMatrixName(String value){
		matrixName = value;
	}
	
	protected int getDefaultColor(Price p) {
		return Util.GrServerColorToSystem(p.color);
	}

	public void setColor(TextView textView, Price price) {
		if( Features.PACK_INPUT && packQty!= null && packQty.contains(price.id))
			textView.setTextColor(textView.getResources().getColor(R.color.grey));
		else if( ((Itemsable)document).findItem(price.id) != null )
			textView.setTextColor(((Itemsable)document).getItemColor());
		else if( remnantsDoc != null && remnantsDoc.findItem(price.id) != null )
			textView.setTextColor(remnantsDoc.getItemColor());
		else if(lastBuyingItems.contains(price.id))
			textView.setTextColor(textView.getResources().getColor(R.color.red));
		else 
			textView.setTextColor(getDefaultColor(price));
	}
	
	protected int getFolderLayoutId(){
		return R.layout.itemselectrow;
	}
	
	protected int getPriceLayoutId(){
		return  R.layout.priceitemrow;
	}
	
	private View getFolderView(FolderTreeNode node, View convertView){
		
		int id = getFolderLayoutId();
		View result;
		if( convertView != null && convertView.getTag(id) != null)
			result = convertView;
		else {
			result = View.inflate(this, id, null);
			result.setTag(id, true);
		}
		TextView tvOrgName = (TextView)result.findViewById(R.id.tvItemSelectRowName);
		
		linesController.prepareTextView(tvOrgName);
		
		tvOrgName.setText(node.name);
		tvOrgName.setTag(node);
		
		return result;
	}
	
	
//	HashMap<Integer, View> views = new HashMap<Integer, View>();
	protected View getPriceView(PriceTreeNode node, View convertView){
		price.read(node.getPriceId());
		Price p = price.getData();
		
		View view;
		int id = getItemLayoutId();
		if( convertView != null && convertView.getTag(id) != null)
			view = convertView;
		else {
			view = View.inflate(this, id, null);
			view.setTag(id, true);
		}

		setName(view, p, 1, node);
		LinearLayout llQuant = (LinearLayout) view.findViewById(R.id.llQuant);
		llQuant.setOrientation(linesController.isMinLines() 
				?  LinearLayout.HORIZONTAL 
				: LinearLayout.VERTICAL);
		llQuant.setMinimumWidth(linesController.isMinLines() ? 110 : 80);
		TextView tvClmn1 = (TextView) view.findViewById(R.id.tvClmn1);
		tvClmn1.setGravity(linesController.isMinLines() ? Gravity.LEFT : Gravity.RIGHT);
		TextView tvClmn2 =(TextView) view.findViewById(R.id.tvClmn2);
		CfgNplW config = (CfgNplW) ConfigManager.getConfig();
		
		setTextColumnValue(tvClmn1, config.priceClmn2Type, p);
		setTextColumnValue(tvClmn2, config.priceClmn3Type, p);
		
		if( Features.ID_COLUMN_IN_PRICE_LIST) {
			TextView tv = (TextView)view.findViewById(R.id.tvItemID);
			if( tv != null ) {
				if(config.idInPriceList) {
					tv.setVisibility(View.VISIBLE);
					tv.setText(p.id);
				} else
					tv.setVisibility(View.GONE);
			}
			
		}
		return view;
	}
	
	int getWhQty(Itemsable id, Price p) {
		int qty = id.getItemValue(p);
		if( Features.QTY_IN_PACK_IN_DOCS &&((CfgNplW)ConfigManager.getConfig()).isPackView )
			qty = (int)((long)qty * Consts.QTY_SCALE / p.qtyInPack);
		
		return qty;
	}
	
	@SuppressWarnings("unchecked")
	private void setTextColumnValue(TextView textView, int type, Price price){
		Itemsable id = (Itemsable)document;
		long value = 0;
		int scale = Consts.QTY_SCALE;
		
		switch(type){
			case 0: 
				textView.setVisibility(View.GONE);
				return;
			case 1:
				value = getWhQty(id, price);
				break;
			case 2:
				value = id.getItemQty(price);
				break;
			case 3: 
				scale = Consts.SUM_SCALE;
				value =  CostStrategy.getInstance(
						(Class<? extends Document<?>>) document.getClass())
							.getItemCost(price, (Document<?>) document); 
				break;
			case 4:
				scale = Consts.SUM_SCALE;
				value = id.getItemSum(price);
				break;
			case 5:
				value = id.getItemQty(price);
				if( value == 0 )
					value = getWhQty(id, price);
				break;
			case 6:
				scale = Consts.SUM_SCALE;
				value = id.getItemSum(price);
				if( value == 0 )
					value = CostStrategy.getInstance(
							(Class<? extends Document<?>>) document.getClass())
								.getItemCost(price, (Document<?>) document); 
				break;
		}
		
//		boolean showPack = ConfigManager.getConfig().isPackView();
//		if( showPack && scale == Consts.QTY_SCALE) {
//			int inPack = price.qtyInPack;
//			if( inPack == 0 )
//				inPack = Consts.QTY_SCALE;
//			int qty = (int)((long)value * Consts.QTY_SCALE / inPack);
//			String qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE) + " у.";
//			textView.setText(qtyText);
//		} else
		textView.setText(Util.IntToScaleStr(value, scale, Util.DEC_DELIM, (scale == Consts.QTY_SCALE)));
	}
	
	protected String getItemName(Price p) { return p.name; }
	
	protected void setName(View view, Price p, int linesCount, PriceTreeNode node) {
		TextView tvPriceItemName = (TextView) view.findViewById(R.id.tvPriceItemName);
		
		setColor(tvPriceItemName, p);
		tvPriceItemName.setText(getItemName(p));
		tvPriceItemName.setTag(node);
		
		linesController.prepareTextView(tvPriceItemName);
	}
}



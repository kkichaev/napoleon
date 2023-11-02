package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.CategoryMatrix;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.MatrixOrder;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.SCGoodItem;
import com.grsoft.dataobjects.Storcheck;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.StorcheckDoc;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.AssortmentMatrixAdapterEx;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixAdapter;
import com.grsoft.util.WarehouseManager;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WarehouseEx extends WarehouseNew {
	static long lastOrder = ExtrasConst.INVALID_ROWID;
	static int curMatrix = 0;
	MatrixOrder matrixOrder = null;
	boolean hideMatrix = false;
	private String LAST_DOC_TYPE = "last_doc_type"; 

	public AssortmentMatrixAdapterEx assortmentMatrixAdapter;
	List<MatrixItem> ami = null;
	CategoryMatrix categoryMatrix;
	
	@Override
	protected int getLayoutId() {
		return R.layout.warehouse_ex;
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createAssortementMatrixAdapter();
		findViewById(R.id.ibNextPrice).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				curMatrix++;
				resetMatrix();
			}
		});
		lvItemSelect.setDividerHeight(1);
		categoryMatrix = CategoryMatrix.get(document);
	}
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View v = super.getPriceView(node, convertView);
		if(categoryMatrix.inSet(node.getId()))
			v.setBackgroundResource(R.drawable.lt_yellow);
		return v;
	}
	
	@Override public boolean useInterlaceBackground() { return false; }
	
	@Override
	protected boolean hasPresentation() {
		if(DocType.getCurDoc() == StorcheckDoc.instance())
			return false;
		return super.hasPresentation();
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		if(DocType.getCurDoc() == StorcheckDoc.instance())
			return new StorcheckAdapteer(this, (Storcheck)document.getData());
		
		String lastDocType = getPreferences(Context.MODE_PRIVATE).getString(LAST_DOC_TYPE, "");
		String curDocName = DocType.getCurDoc().getName();
		
		if(!curDocName.equals(lastDocType)){
			Editor ed = getPreferences(Context.MODE_PRIVATE).edit();
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
				String table = DataObjectInfo.getInstance().getTableName(matrixOrder.getClass());
				DbReader r = new DbReader();
				r.select(matrixOrder, table, null);
				r.close();
			}
			
			if( curMatrix < matrixOrder.items.size() ) {
				hideMatrix = true;
				findViewById(R.id.llMatrixOrder).setVisibility(View.VISIBLE);
				String name = matrixOrder.items.get(curMatrix).name;
				TextView tv = (TextView)findViewById(R.id.tvMatrixName);
				tv.setText(name);
				MatrixAdapter ret =  new MatrixAdapter(this, name);
//				ret.putFilter(new CategoryFilter(document.getId()));
				return ret;
			}
		}
		findViewById(R.id.llMatrixOrder).setVisibility(View.GONE);
		FoldersAdapter fs = (FoldersAdapter) super.createListAdapter();
//		if(document != null)
//			fs.putFilter(new CategoryFilter(document.getId()));		
		return fs;
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
	public void setColor(TextView textView, Price price) {
		if(assortmentMatrixAdapter != null && assortmentMatrixAdapter.isIdInMatrix(price.id) &&
			((Itemsable)document).findItem(price.id) == null)
		{
			textView.setTextColor(getResources().getColor(R.color.red));
		} else
			super.setColor(textView, price);
	};
	
	@Override
	protected AssortmentMatrixAdapter createAssortementMatrixAdapter() {
		if (assortmentMatrixAdapter == null)
			assortmentMatrixAdapter =  new AssortmentMatrixAdapterEx(this, document.getId());
		
		return assortmentMatrixAdapter;
	}
	
	class StorcheckAdapteer extends FoldersAdapter {

		com.grsoft.dataobjects.StorcheckGoods goods;
		
		public StorcheckAdapteer(WarehouseManager warehouse, Storcheck doc) {
			super(warehouse);
			goods = com.grsoft.dataobjects.StorcheckGoods.get(doc.created);
		}
		
		@Override
		protected void fillPriceIds(SQLiteDatabase database) {
			fprice.clear();
			
			if(goods == null)
				return;
			
			PriceImpl pi = new PriceImpl();
			Price p = pi.getData();
			
			for(SCGoodItem item : goods.items) {
				p.id = item.id;
				if( pi.read() ) {
					if(!fprice.containsKey(item.folder))
						fprice.put(item.folder, new ArrayList<PriceInfo>());
						
					PriceInfo pinfo = new PriceInfo(pi.getRowid(), p.name, p.id);
					fprice.get(item.folder).add(pinfo);
				}
			}
			pi.close();
		}
		
		@Override
		protected void fillTree(SQLiteDatabase database) {
			ArrayList<TreeNode> list = root.getChilds();
			list.clear();
			FolderTreeNode f = createFoldersTreeNode(root);
			f.id = com.grsoft.dataobjects.StorcheckGoods.NEW_GOODS_FOLDER;
			f.level = 0;
			f.setLeaf(true);
			f.name = "Новинки";
			list.add(f);
			
			f = createFoldersTreeNode(root);
			f.id = com.grsoft.dataobjects.StorcheckGoods.TOP_30_FOLDER;
			f.setLeaf(true);
			f.level = 0;
			f.name = "TOP 30";
			list.add(f);
		}
	}
}

//class CategoryFilter extends Filter {
//	HashSet<String> items = new HashSet<String>();
//	public CategoryFilter(String id) {
//		super(id + "categFiltere");
//		
//		OrgImpl oi = new OrgImpl();
//		OrgEx oe = (OrgEx) oi.getData();
//		oe.id = id;
//		if(oi.read()) {
//			CategoryMatrix cm = new CategoryMatrix(); 
//			DbReader r = new DbReader();
//			r.select(cm, cm.getTableName(), "name = '" + oe.category + "'");
//			r.close();
//			for(MatrixItem mi : cm.items)
//				items.add(mi.id);
//		}
//		oi.close();
//	}
//	
//	@Override
//	public boolean inset(long priceRowID, String id) {
//		return (items.size() == 0 || items.contains(id));
//	}
//}

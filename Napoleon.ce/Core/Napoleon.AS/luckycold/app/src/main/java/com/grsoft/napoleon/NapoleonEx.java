package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgFS;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.PriceTextFilter;
import com.grsoft.util.WarehouseAdapter;
import com.grsoft.util.WarehouseAdapter.OnChangeListener;
import com.grsoft.util.WarehouseManager;
import com.grsoft.view.FolderPath;

public class NapoleonEx extends Napoleon implements WarehouseManager {

	private FolderPath folderPath;
	private HorizontalScrollView scrollView;
	private ImageButton btnSolidList;
	
	public static final String SHARED_PREF_NAME = "com.grsoft.napoleon.NapoleonEx";
	public static final String EXPAND_PRICE_PREF = "expand_price";
	public boolean buildingProcess = false;
	public static String topFolder = "";
	
	HashMap<String, Integer> dlvDays = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		folderPath = new FolderPath(scrollView, R.id.tvHome, R.id.llPath, this, (WarehouseAdapter) mainOrgsAdapter);
		
		btnSolidList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((Adapter)mainOrgsAdapter).expandSwitch();
				
				if (mainOrgsAdapter != null) {
					Editor editor = getSharedPreferences(SHARED_PREF_NAME,
							Context.MODE_PRIVATE).edit();
					editor.putBoolean(EXPAND_PRICE_PREF, ((Adapter)mainOrgsAdapter).isExpanded());
					editor.commit();
				}
			}
		});
	}

	@Override
	protected void initView() {
		scrollView = ((HorizontalScrollView) findViewById(R.id.hswPricePage));
		btnSolidList = (ImageButton) findViewById(R.id.btnSolidList);
	}
	
	protected int getFolderLayoutId() {
		return R.layout.itemselectrow;
	}

	@Override
	public View getFolderView(FolderTreeNode node, View convertView) {
		int id = getFolderLayoutId();
		View result;
		if (convertView != null && convertView.getTag(id) != null)
			result = convertView;
		else {
			result = View.inflate(this, id, null);
			result.setTag(id, true);
		}

		TextView tvOrgName = (TextView) result.findViewById(R.id.tvItemSelectRowName);
		tvOrgName.setText(node.name);
		linesController.prepareTextView(tvOrgName);
		tvOrgName.setTag(node);

		return result;
	}
	
	void loadDlvDays() {
		if( dlvDays == null ) {
			dlvDays = new HashMap<String, Integer>();
			try {
				Cursor c = DataBaseManager.getDataBase().rawQuery("select min(date), id from Delivery where sumD > 0 group by id", null);
				while(c.moveToNext()) {
					Date d = new Date(); 
					int days = (int)((d.getTime() - c.getLong(0))/ (1000 * 3600 * 24));
					dlvDays.put(c.getString(1), days);
				}
				c.close();
			} catch(Exception e) {
				
			}
		}
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);
		int visible = ( docType == DebtDoc.instance() ) ? View.VISIBLE : View.GONE;
		findViewById(R.id.tvDays).setVisibility(visible);
	}
	
	@Override protected int getRowResourceID() { return R.layout.main_list_row_ex; }

	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		int id = getRowResourceID();
		View result;
		if (convertView != null && convertView.getTag(id) != null)
			result = convertView;
		else {
			result = View.inflate(this, id, null);
			result.setTag(id, true);
		}

		OrgImpl orgImpl = new OrgImpl();
		orgImpl.read("id", node.getId());
		result.setTag(orgImpl.getRowid());
		drawOrg(orgImpl, result);
		ImageView ivFolder = (ImageView) result.findViewById(R.id.ivFolder);
		ivFolder.setVisibility(View.GONE);
		
		int visible = View.GONE;
		TextView tv = (TextView)result.findViewById(R.id.tvDays);
		if( DocType.getCurDoc() == DebtDoc.instance() ) {
			visible = View.VISIBLE;
			loadDlvDays();
			String text = "";
			Integer i = dlvDays.get(node.getId());
			if( i != null )
				text = Integer.toString(i);
			tv.setText(text);
			
		}
		
		tv.setVisibility(visible);

		return result;
	}

	protected OnItemClickListener getItemOnClickListner() {
		return new OrglListOnClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				Object tag = arg1.getTag();

				if (tag == null) {
					((Adapter) mainOrgsAdapter).onClick(pos);
				} else
					super.onItemClick(arg0, arg1, pos, arg3);
			}
		};
	}

	@Override
	public void editItem(long rowid) {}

	@Override
	public void applySearchFilter(String value) {
		if( buildingProcess )
			return;
		
		FoldersAdapter adapter = (FoldersAdapter)mainOrgsAdapter;
		if (mainOrgsAdapter != null && value.trim().length() > 0) {
			PriceTextFilter filter = (PriceTextFilter) adapter
					.getFilter(PriceTextFilter.NAME);

			if (filter == null) {
				filter = new PriceTextFilter(){ 
					@Override public void build(WarehouseAdapter adapter, String cond, boolean searchExcact){
						StringBuilder sbWhere = new StringBuilder();
						
						if (cond.trim().length() > 0){
							makeSearchStr(cond, sbWhere, searchExcact);
						}	
						
						where = sbWhere.toString();
					}	
				};
				adapter.putFilter(filter);
			}

			adapter.setExpanded(isPriceExpand());
			filter.build(adapter, value, false);
			adapter.buildSet(true);
		} else
			((FilterAdapter) adapter).resetFilter();
	}

	@Override
	public boolean isPriceExpand() {
		return getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
			.getBoolean(EXPAND_PRICE_PREF, false); 
	}

	@Override
	public void sortingPriceList(ArrayList<TreeNode> childs) {
		
		Collections.sort(childs, new Comparator<TreeNode>(){

			@Override
			public int compare(TreeNode lhs, TreeNode rhs) {
				PriceTreeNode pleft = (PriceTreeNode)lhs;
				PriceTreeNode pright = (PriceTreeNode) rhs;
				
				int result = new Boolean(pleft.getId().contains("\t")).compareTo(pright.getId().contains("\t"));

				if (result == 0)
					result = pleft.toString().compareTo(pright.toString());
				
				return result;
			}});
	}

	@Override
	public boolean useInterlaceBackground() { return false;	}

	@Override
	public void afterBuildSet() {}

	@Override
	protected BaseAdapter getMainOrgAdapter() throws IllegalAccessException, InstantiationException {
		Adapter result = new Adapter(this);
		result.setOnChangeListener(adapterOnChangeListener);
		result.setExpanded(isPriceExpand());

		return result;
	}


	@Override
	protected void onResume() {
		dlvDays = null;
		
		super.onResume();
		
		if(listViewMode == ListViewMode.ORG_LIST){
			Adapter adapter = (Adapter)mainOrgsAdapter;
			//btnSolidList.setImageResource(adapter.isExpanded() ? R.drawable.orglist : R.drawable.orglisttree );
			
			if(adapter != null)
				((Adapter)mainOrgsAdapter).buildSet(adapter.getFolderTop().id);
		}
	}
	
	@Override
	protected int getResourceID() {
		return R.layout.mainex;
	}

	protected WarehouseAdapter.OnChangeListener adapterOnChangeListener = new OnChangeListener() {
		int top = -1;
		@Override
		public void startBuildSet(WarehouseAdapter adapter) {
			top = lvMainOrgs.getFirstVisiblePosition();
			buildingProcess = true;
			btnFind.setEnabled(false);
			textWatcher.blockListner(true);
			edFind.setEnabled(false);

			if (folderPath != null)
				folderPath.setEnabled(false);
			
			btnSolidList.setEnabled(false);
			showDialog(R.id.dlg_wait);
		}

		@Override
		public void endBuildSet(WarehouseAdapter adapter) {
			try {
				setSelection(top);
				btnFind.setEnabled(true);
				textWatcher.blockListner(false);
				edFind.setEnabled(true);

				if (folderPath != null)
					folderPath.setEnabled(true);

				buildingProcess = false;
				btnSolidList.setEnabled(true);
				//btnSolidList.setImageResource(((Adapter)mainOrgsAdapter).isExpanded() ? R.drawable.orglist : R.drawable.orglisttree);
				
				dismissDialog(R.id.dlg_wait);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onAdapterChange(final WarehouseAdapter adapter) {
			if (folderPath != null){
				folderPath.refreshPath(adapter);
			}
		}

		@Override
		public void setSelection(int position) { lvMainOrgs.setSelection(position);	}
	};
	
	protected android.app.Dialog onCreateDialog(int id) {
		switch(id){
		case R.id.dlg_wait:
			return createWaitDlgDialog();
		default:
			return super.onCreateDialog(id);
		}
	};
	
	private Dialog createWaitDlgDialog() {
		ProgressDialog result = new ProgressDialog(this);
		result.setMessage(getString(R.string.price_loading));

		result.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if (mainOrgsAdapter != null)
					((Adapter)mainOrgsAdapter).close();
			}
		});

		return result;
	}
	
	@Override
	protected void setListMode(ListViewMode mode) {
		super.setListMode(mode);
		
		
		int status = mode == ListViewMode.ORG_LIST ? View.VISIBLE : View.GONE;
		scrollView.setVisibility(status);
		btnSolidList.setVisibility(status);
	}
	
	@Override
	public void switchListMode() {
		super.switchListMode();
		
		if(listViewMode == ListViewMode.ORG_LIST){
			Adapter adapter = (Adapter)mainOrgsAdapter;
			
			if(adapter != null)
				(adapter).buildSet(adapter.getFolderTop().id);
		}
	}
}

class Adapter extends FoldersAdapter {

	public Adapter(WarehouseManager warehouse) {
		super(warehouse);
	}

	@Override
	protected void fillPriceIds(SQLiteDatabase database) {
		try {
			fprice.clear();
			String priceTable = DataObjectInfo.getInstance().getTableName(OrgEx.class);
			String folderTable = getFolderTableName();

			if (DbWriter.isTableExists(priceTable)) {
				String sql = "SELECT f.id, p.rowid, p.name, p.id FROM " + priceTable + " p LEFT JOIN " + folderTable + " f ON p.folder = f.fid ";
				String where = getWhereStr();
				if (where.length() > 0) {
					sql += " WHERE " + where;
				}
				Cursor cursor = database.rawQuery(sql, null);
				if (cursor.moveToFirst()) {
					try {
						do {
							long rowid = cursor.getLong(1);
							String id = cursor.getString(3);

							if (!inset(rowid, id))
								continue;

							int folderid = cursor.getInt(0);

							// потенциальные организации складываем в первую папку
							if(folderid == 0)
								folderid = 1;
							
							if (!fprice.containsKey(folderid))
								fprice.put(folderid, new ArrayList<PriceInfo>());

							PriceInfo pi = new PriceInfo(rowid, cursor.getString(2), id);
							fprice.get(folderid).add(pi);
						} while (cursor.moveToNext());
					} finally {
						cursor.close();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String getFolderTableName() {
		return DataObjectInfo.getInstance().getTableName(OrgFS.class);
	}

	@Override
	public void buldProcess(AsyncTask<?, ?, ?> task) {
		Map<Integer, ArrayList<PriceInfo>> svPrice = globalPrice;
		FolderTreeNode svRoot = globalRoot;

		globalPrice = null;
		globalRoot = null;

		super.buldProcess(task);

		globalPrice = svPrice;
		globalRoot = svRoot;
	}
	
	public PriceTreeNode createPriceTreeNode(TreeNode parent, long priceRowId, String name, String id) {
		
		return new PriceTreeNode(root, priceRowId, name, id){
			@Override
			public int compareTo(TreeNode treeNode) {
				if (this.getClass() != treeNode.getClass())
					return 1;
				else {
					int result = new Boolean(getId().contains("\t")).compareTo(((PriceTreeNode)treeNode).getId().contains("\t"));
					
					if (result == 0)
						result = toString().compareTo(treeNode.toString());
					
					return result; 
				}
			}
		};
	}
}

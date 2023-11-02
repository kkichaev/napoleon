package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.grsoft.database.DbWriter;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.Category;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Goods;
import com.grsoft.dataobjects.impl.GoodsAuditImpl;
import com.grsoft.dataobjects.impl.GoodsImpl;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.FindOnClickListener;
import com.grsoft.napoleon.util.FindTextWatcher;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.PriceTextFilter;
import com.grsoft.util.WarehouseAdapter;
import com.grsoft.util.WarehouseManager;
import com.grsoft.util.ZeroPositionFilter;
import com.grsoft.util.WarehouseAdapter.OnChangeListener;
import com.grsoft.view.FolderPath;
import com.grsoft.view.RegDurationActivity;

public class AuditGoods extends RegDurationActivity implements DataSetNotify, WarehouseManager  {
	private static final int DLG_WAIT = 0;
	
	GoodsAuditImpl doc = new GoodsAuditImpl();
	GoodsImpl goods = new GoodsImpl();
	int folderID = -1;
	Adapter adapter;
	boolean editMode, buildingProcess, starting = true;
	View btnFind;
	EditText edFind;
	FolderPath folderPath;
	LinesCountController linesController;
	
	public static void open(Context ctx, GoodsAuditImpl doc, boolean editMode) {
		Intent i = new Intent(ctx, AuditGoods.class);
		
		if( doc != null ) {
			i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
			i.putExtra(ExtrasConst.ORG_ID_STR, doc.getId());
			i.putExtra(ExtrasConst.EDIT_MODE_STR, editMode);
		}
		ctx.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.audit_goods);
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		editMode = true;
		long docRowId = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		editMode = b.getBoolean(ExtrasConst.EDIT_MODE_STR, true);
		folderID = b.getInt(ExtrasConst.FOLDER_ID, -1);

		doc.read(docRowId, false);

		
		adapter = new Adapter(this);
		ListView items = (ListView)findViewById(R.id.lvItems);
		items.setAdapter(adapter);
		items.setDividerHeight(0);
		items.setOnItemClickListener(new OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) { adapter.onClick(position); }
		});
		
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener( items, (ImageView) findViewById(R.id.btnLines), this);
		linesController = linesOnClickListener.getController();

		edFind = (EditText)findViewById(R.id.edFind);
		edFind.addTextChangedListener(new FindTextWatcher(edFind, items));
		btnFind = findViewById(R.id.btnFind);
		btnFind.setOnClickListener(new FindOnClickListener(edFind, items, findViewById(R.id.llFind)));
		
		findViewById(R.id.btnUp).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (adapter.isExpanded() == false)
					adapter.prevFolder();
			}
		});
		
		findViewById(R.id.btnDown).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (adapter.isExpanded() == false)
					adapter.nextFolder();
			}
		});
	
		HorizontalScrollView scrollView = ((HorizontalScrollView) findViewById(R.id.hswPricePage));
		folderPath = new FolderPath(scrollView, R.id.tvHome, R.id.llPath, this, adapter);

		adapter.setOnChangeListener(new OnChangeListener() {

			@Override
			public void startBuildSet(WarehouseAdapter adapter) {
				buildingProcess = true;
				btnFind.setEnabled(false);
				edFind.setEnabled(false);
				showDialog(DLG_WAIT);
			}

			@Override
			public void endBuildSet(WarehouseAdapter adapter) {
				try {
					btnFind.setEnabled(true);
					edFind.setEnabled(true);
					dismissDialog(DLG_WAIT);
					buildingProcess = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onAdapterChange(final WarehouseAdapter adapter) {
				findViewById(R.id.ivFilterLabel).setVisibility(adapter.getFilter(ZeroPositionFilter.NAME) != null ? View.VISIBLE : View.GONE);
				folderPath.refreshPath(adapter);
			}

			@Override public void setSelection(int position) { ((ListView)findViewById(R.id.lvItems)).setSelection(position); }
		});
		adapter.buildSet(folderID);
	}
	
	@Override
	public void onBackPressed() {
		if( !editMode && doc.getRowid() != ExtrasConst.INVALID_ROWID )
			doc.open(this);
		super.onBackPressed();
	}
	
	@Override
	protected void onDestroy() {
		doc.close();
		goods.close();
		
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		if (starting)
			starting = false;
		else
			doc.read(doc.getRowid(), false);
		
		notifyDataSetChanged();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		outState.putBoolean(ExtrasConst.EDIT_MODE_STR, editMode);

		if (folderID != -1)
			outState.putInt(ExtrasConst.FOLDER_ID, folderID);

		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == DLG_WAIT ) {
			ProgressDialog result = new ProgressDialog(this);
			result.setMessage(getString(R.string.price_loading));

			result.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					if (adapter != null)
						adapter.close();
				}
			});

			return result;
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	public View getFolderView(FolderTreeNode node, View convertView) {
		int id = R.layout.audit_folderrow;
		View result;
		if (convertView != null && convertView.getTag(id) != null)
			result = convertView;
		else {
			result = View.inflate(this, id, null);
			result.setTag(id, true);
		}

		TextView item = (TextView) result.findViewById(R.id.tvItemSelectRowName);
		item.setText(node.name);
		linesController.prepareTextView(item);
		item.setTag(node);

		return result;
	}

	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		goods.read(node.getRowid());
		Goods p = goods.getData();

		View view;
		int id = R.layout.audit_itemrow;
		if (convertView != null && convertView.getTag(id) != null)
			view = convertView;
		else {
			view = View.inflate(this, id, null);
			view.setTag(id, true);
		}

		TextView tvName = (TextView) view.findViewById(R.id.tvPriceItemName);

		if (doc.findItem(p.id) != null)
			tvName.setTextColor(doc.getItemColor());
		else
			tvName.setTextColor(Color.BLACK);
		
		tvName.setText(p.name);

		linesController.prepareTextView(tvName);
		tvName.setTag(node);
		return view;
	}

	@Override public void editItem(long rowid) { doc.editItem(rowid, this); }

	@Override
	public void applySearchFilter(String value) {
		if( buildingProcess )
			return;
		if (value.trim().length() > 0) {
			PriceTextFilter filter = (PriceTextFilter) adapter.getFilter(PriceTextFilter.NAME);

			if (filter == null) {
				filter = new PriceTextFilter();
				adapter.putFilter(filter);
			}

			filter.srchFieldName = PriceTextFilter.SRCH_NAME_FLD;
			adapter.setExpanded(isPriceExpand());
			filter.build(adapter, value, false);
			adapter.buildSet(true);
		} else
			((FilterAdapter) adapter).resetFilter();
	}

	@Override public boolean isPriceExpand() { return false; }
	@Override public void sortingPriceList(ArrayList<TreeNode> price) { Collections.sort(price, FoldersAdapter.TreeNodeComparator); }
	@Override public boolean useInterlaceBackground() { return true; }
	@Override public void afterBuildSet() { }
	@Override public void notifyDataSetChanged() { adapter.notifyDataSetChanged(); }
	
	class Adapter extends FoldersAdapter {

		public Adapter(WarehouseManager warehouse) {
			super(warehouse);
		}
		
		@Override
		protected void fillPriceIds(SQLiteDatabase database) {
			try{
				fprice.clear();
				String priceTable = DataObjectInfo.getInstance().getTableName(Goods.class);
				String folderTable = getFolderTableName();
				
				if(DbWriter.isTableExists(priceTable)){
					String sql = "SELECT f.id, p.rowid, p.name, p.id FROM " + priceTable + " p INNER JOIN " + folderTable + " f ON p.fid = f.id ";
					String where = getWhereStr();
					if( where.length() > 0 ) {
						sql += " WHERE " + where;
					}
					Cursor cursor = database.rawQuery(sql, null);
					if (cursor.moveToFirst()) {
						try{
							do{
								long rowid = cursor.getLong(1);
								String id = cursor.getString(3);
								
								if( !inset( rowid, id ) )
									continue;
								
								int folderid = cursor.getInt(0);
								
								if(!fprice.containsKey(folderid))
									fprice.put(folderid, new ArrayList<PriceInfo>());
								
								PriceInfo pi = new PriceInfo(rowid, cursor.getString(2), id);
								fprice.get(folderid).add(pi);
							} while(cursor.moveToNext());
						} finally { 
							cursor.close();
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		@Override
		protected String getFolderTableName() {
			return DataObjectInfo.getInstance().getTableName(Category.class);
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
	}
}

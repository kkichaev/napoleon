package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.WarehouseAdapter;
import com.grsoft.util.WarehouseManager;
import com.grsoft.view.BaseActivity;
import com.grsoft.view.FolderPath;

public class PresentationEx extends BaseActivity implements WarehouseManager {
	
	private static final String SETTING = "presentation_setting";
	private static final String NUM_COL_STR = "num_col";
	private static final int SETTING_DLG = 1;
	private static final int DLG_WAIT = 2;

	long docRowId;
	String folderList;
	String selection;
	boolean buildingProcess = false;
	
	private GridView gvPresentation;
	FoldersAdapter adapter;
	FolderPath folderPath;
	AdapterChangeListener changeListener;
	PriceImpl price = new PriceImpl();
	
	int topFolder;
	
	Drawable folder;
	int picSize;
	
	public static PresentationList items = new PresentationList();
	HashMap<String, BitmapDrawable> images = new HashMap<String, BitmapDrawable>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.presentationex);
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		
		docRowId = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		folderList = b.getString(ExtrasConst.FOLDERS_LIST_STR);
		topFolder = b.getInt(ExtrasConst.FOLDER_ID, -1);
		
		 findViewById(R.id.btnPrice).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Document<?> document = DocType.getCurDoc().create();
				if (!(document instanceof Itemsable)) {
					document = OrderDoc.instance().create();
				}
				if (docRowId != ExtrasConst.INVALID_ID)
					document.read(docRowId);
				document.close();

				Warehouse.open(PresentationEx.this, document, adapter.getFolderTop().id);
				finish();
			}
		});
		
		try{
			changeListener = new AdapterChangeListener();
			adapter = new PhotoFolder(this);
			adapter.setOnChangeListener(changeListener);
			
			calcPicSize();			
			
			final String PHOTO_PATH_NOT_NULL = "photoPath NOT NULL";
			selection = folderList.length() > 0 ? 
					String.format("folderid in (%s) and %s", folderList, PHOTO_PATH_NOT_NULL) 
					: PHOTO_PATH_NOT_NULL;
					
			gvPresentation = (GridView) findViewById(R.id.gvPresentation);
			gvPresentation.setAdapter(adapter);
			gvPresentation.setOnItemClickListener(new AdapterView.OnItemClickListener() {	
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					adapter.onClick(position);					
				}				
			});
			gvPresentation.setNumColumns(getNumColFromPref());
			
			adapter.buildSet();
			
			HorizontalScrollView scrollView = ((HorizontalScrollView)findViewById(R.id.hswPricePage));
			if( scrollView != null ) {
				folderPath = new FolderPath(scrollView, R.id.tvHome, R.id.llPath, this, adapter);			
				scrollView.setVisibility(View.VISIBLE);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	void calcPicSize() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        int screenWidth = getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ?
			displaymetrics.heightPixels : 
			displaymetrics.widthPixels;
        
        final int space = 20;
        int colCount = getNumColFromPref();
        colCount = colCount == -1 ? 3 : colCount;
        picSize = (screenWidth - space) / colCount; 

		Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.folder_pic);
		folder = new BitmapDrawable(Bitmap.createScaledBitmap(b, picSize, picSize, true));
		folder.setBounds(0, 0, picSize, picSize);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		price.close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	class AdapterChangeListener implements WarehouseAdapter.OnChangeListener {
		
		@Override
		public void startBuildSet(WarehouseAdapter adapter) {
			buildingProcess = true;
			showDialog(DLG_WAIT);
		}
		
		@Override
		public void endBuildSet(WarehouseAdapter adapter) {
			try {
				if( topFolder != -1)
					adapter.setFolder(topFolder);
				
				dismissDialog(DLG_WAIT);
				buildingProcess = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onAdapterChange(final WarehouseAdapter adapter) {
			if( folderPath != null )
				folderPath.refreshPath(adapter);			
		}

		@Override
		public void setSelection(int position) {
			gvPresentation.setSelection(position);
		}
	};
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, docRowId);
		outState.putString(ExtrasConst.FOLDERS_LIST_STR, folderList);
		outState.putInt(ExtrasConst.FOLDER_ID, topFolder);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.presentation_opt_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		
		if (itemId == R.id.itSetting)
			showDialog(SETTING_DLG);
		return true;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case DLG_WAIT:
			return createWaitDlgDialog();
		case SETTING_DLG:
			return createSettingDlg();
		default:
			return super.onCreateDialog(id);
		}
	}

	private Dialog createWaitDlgDialog() {
		ProgressDialog result = new ProgressDialog(this);
		result.setMessage(getString(R.string.price_loading));
		
		result.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				if(adapter != null)
					adapter.close();
			}
		});
		
		return result;
	}

	private Dialog createSettingDlg() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(R.string.setting);
		b.setView(View.inflate(this, R.layout.pres_setting_dialog, null));
		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) { refreshSetting(dialog); }
		});
		b.setNegativeButton(R.string.cancel, null);
		return b.create();
	}

	protected void refreshSetting(DialogInterface dialog) {
		int pos = ((Spinner)
				((AlertDialog)dialog).findViewById
					(R.id.spNumCol)).getSelectedItemPosition();
		pos = pos == 0 ? -1 : pos;
		SharedPreferences pref = getSharedPreferences(SETTING, MODE_PRIVATE);
		Editor edit = pref.edit();
		edit.putInt(NUM_COL_STR, pos);
		edit.commit();

		calcPicSize();
		gvPresentation.setNumColumns(pos);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id){
		case SETTING_DLG: prepareSettingDlg(dialog); break;
		}
	}

	int getNumColFromPref(){
		SharedPreferences pref = getSharedPreferences(SETTING, MODE_PRIVATE);
		return pref.getInt(NUM_COL_STR, 3);
	}
	
	private void prepareSettingDlg(Dialog dialog) {
		int numCol = getNumColFromPref();
		((Spinner)dialog.findViewById(R.id.spNumCol))
			.setSelection(numCol == -1 ? 0 : numCol);
	}

	@Override
	public View getFolderView(FolderTreeNode node, View convertView) {
		if( convertView == null )
			convertView = View.inflate(this, R.layout.presentation_item, null);

		TextView tv;
		tv = (TextView)convertView.findViewById(R.id.tvItem);
		tv.setCompoundDrawables(null, null, null, folder);
		tv.setText(node.name);
		
		return convertView;
	}

	BitmapDrawable createBitmap(String path, int size) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
    	opt.inSampleSize = 3;
    	
    	Bitmap src = BitmapFactory.decodeFile(path, opt);
    	double coef = (double)size / Math.max(src.getWidth(), src.getHeight());
    	
    	BitmapDrawable b;
    	if( coef == 1.0 )
    		b = new BitmapDrawable(src);
    	else
	    	b = new BitmapDrawable(Bitmap.createScaledBitmap(src, 
    			(int)(src.getWidth() * coef + 0.5), 
    			(int)(src.getHeight() * coef + 0.5), true));
    	
    	b.setBounds(0, 0, size, size);
    	
    	return b;
	}
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		long rid = node.getRowid();
		price.read(rid);
		Price p = price.getData();

		if( convertView == null )
			convertView = View.inflate(this, R.layout.presentation_item, null);

		String path = items.getImage(rid);
		TextView tv;
		tv = (TextView)convertView.findViewById(R.id.tvItem);
		if( path != null ) {
			BitmapDrawable b = images.get(path);
			
	        try {
				if( b == null || b.getBounds().bottom != picSize ) {
					b = createBitmap(path, picSize);
		        	images.put(path, b);
				}
	        } catch(Exception e) {
	        	e.printStackTrace();
	        }
			
			tv.setCompoundDrawables(null, null, null, b);
		}
		tv.setText(p.name);
		if( p.qty <= 0 )
			tv.setBackgroundColor(Color.LTGRAY);
		else
			tv.setBackgroundColor(Color.WHITE);
		
		return convertView;
	}

	@Override
	public void editItem(long rowid) {
		String path = items.getImage(rowid);
		if( path != null ) {
//			PricePresentation.open(this, path, docRowId, selection);
			PricePresentationEx.open(this, rowid, docRowId, selection);
		}
	}

	@Override public void applySearchFilter(String value) { }
	@Override public boolean isPriceExpand() { return false; }
	@Override public void sortingPriceList(ArrayList<TreeNode> childs) { }

	@Override public boolean useInterlaceBackground() { return false; }
	
	class PhotoFolder extends FoldersAdapter {
	
		public PhotoFolder(WarehouseManager warehouse) {
			super(warehouse);
		}
		
		@Override
		public void buldProcess(AsyncTask<?, ?, ?> task) {
			resetCache();
			super.buldProcess(task);
		}
	
		@Override
		protected void fillPriceIds(SQLiteDatabase database) {
			try{
				fprice.clear();
				items.clear();
				
				String table = DataObjectInfo.getInstance().getTableName(Present.class);
				String ptable = DataObjectInfo.getInstance().getTableName(Price.class); 
				
				if(DbWriter.isTableExists(table)){
					String sql="select ph.folderid, price.rowid, price.name, ph.photopath, price.id from \"" + ptable +
							"\" inner join \"" + table + "\" ph on price.id = ph.id";
	
					Cursor cursor = null;
					try{
						cursor = DataBaseManager.getDataBase().rawQuery(sql, null);					
						while (cursor.moveToNext()) {
							long rowid = cursor.getLong(1);
							String id = cursor.getString(4);
							if( !inset( rowid, id ) )
								continue;
							
							int folderid = cursor.getInt(0);
							if(!fprice.containsKey(folderid))
								fprice.put(folderid, new ArrayList<PriceInfo>());
							
							String name = cursor.getString(2);
							PriceInfo pi = new PriceInfo(rowid, name, id);
							fprice.get(folderid).add(pi);
							
							items.add(new PresentationData(rowid, folderid, name, cursor.getString(3), id));
						}
					} finally {
						if( cursor != null )
							cursor.close();
					}
					
					items.sort();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	@Override
	public void afterBuildSet() {
		
	}
}
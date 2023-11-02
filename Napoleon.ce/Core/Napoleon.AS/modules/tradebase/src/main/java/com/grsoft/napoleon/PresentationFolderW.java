package com.grsoft.napoleon;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocItemsStock;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.BitmapUtils;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseAdapter;
import com.grsoft.util.WarehouseManager;
import com.grsoft.util.ZeroPositionFilter;
import com.grsoft.util.view.ViewUtil;
import com.grsoft.view.BaseActivity;
import com.grsoft.view.FolderPath;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class PresentationFolderW extends BaseActivity implements WarehouseManager {
	public static Class<? extends Activity> activity = PresentationFolderW.class; 
	
	protected static final String SETTING = "presentation_setting";
	private static final String NUM_COL_STR = "num_col";
	public static final String ZERO_FILTER = "zero_filter";
	private static final int SETTING_DLG = 1;
	private static final int DLG_WAIT = 2;

	long docRowId;
	String folderList;
	String selection;
	boolean buildingProcess = false;
	
	Document<?> doc = null;
	
	protected GridView gvPresentation;
	protected FoldersAdapter adapter;
	FolderPath folderPath;
	AdapterChangeListener changeListener;
	PriceImpl price = new PriceImpl();
	
	int topFolder;
	
	Drawable folder;
	int picSize = 0;
	
	public static PresentationList items = new PresentationList();
	//HashMap<String, BitmapDrawable> images = new HashMap<String, BitmapDrawable>();
	Map<String, WeakReference<BitmapDrawable>> images = new WeakHashMap<String, WeakReference<BitmapDrawable>>();
	protected ArrayList<String> lastBuyingItems = new ArrayList<String>();
	private HashSet<Long> priceSaledIDs = new HashSet<Long>();

	private ImageView ivFilter;
	
	public static void open(Context context, long orderId, String folders, int topFolder, Class<? extends Activity> window){
		Intent intent = new Intent(context, window);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, orderId);
		intent.putExtra(ExtrasConst.FOLDERS_LIST_STR, folders);
		intent.putExtra(ExtrasConst.FOLDER_ID, topFolder);
		context.startActivity(intent);
	}
	
	public static void open(Context context, long orderId, String folders, int topFolder){
		open(context, orderId, folders, topFolder, activity);
	}
	
	private void inflateView(){
		ivFilter = (ImageView) findViewById(R.id.ivFilterLabel);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());
		
		CfgNplW cfg = (CfgNplW) ConfigManager.getConfig();
		
		if(cfg.keepAwayInOrder)
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		inflateView();
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		
		docRowId = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		folderList = b.getString(ExtrasConst.FOLDERS_LIST_STR);
		topFolder = b.getInt(ExtrasConst.FOLDER_ID, -1);
		
		if( Features.COST_IN_PRESENTATION ) {
			doc = DocType.getCurDoc().create();
			if (!(doc instanceof Itemsable))
				doc = OrderDoc.instance().create();
			doc.read(docRowId);
			loadLastBuyingItems(doc.getId());
		}
		
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

				Warehouse.open(PresentationFolderW.this, document, adapter.getFolderTop().id);
				finish();
			}
		});
		
		try{
			changeListener = new AdapterChangeListener();
			adapter = createAdapter();
			initAdapter(adapter);
			adapter.setOnChangeListener(changeListener);
			
			calcPicSize();			
			
			final String PHOTO_PATH_NOT_NULL = "photoPath NOT NULL";
			selection = folderList.length() > 0 ? 
					String.format("folderid in (%s) and %s", folderList, PHOTO_PATH_NOT_NULL) 
					: PHOTO_PATH_NOT_NULL;
					
			gvPresentation = (GridView) findViewById(R.id.gvPresentation);
			gvPresentation.setAdapter(adapter);
			adapterInit();
			gvPresentation.setOnItemClickListener(new AdapterView.OnItemClickListener() {	
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					adapter.onClick(position);					
				}				
			});
			gvPresentation.setNumColumns(getNumColFromPref());

			rebuildAdapter();

			HorizontalScrollView scrollView = ((HorizontalScrollView)findViewById(R.id.hswPricePage));
			if( scrollView != null ) {
				folderPath = new FolderPath(scrollView, R.id.tvHome, R.id.llPath, this, adapter);			
				scrollView.setVisibility(View.VISIBLE);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void initAdapter(FoldersAdapter adapter) {}

	protected void adapterInit() {
		SharedPreferences pref = getSharedPreferences(Warehouse.SHARED_PREF_NAME, Context.MODE_PRIVATE);
		if (pref.getBoolean(ZERO_FILTER, false))
			adapter.putFilter(createZeroPositionFilter());
	}
	
	protected FoldersAdapter createAdapter() { return new PhotoFolder(this); }

	protected int getLayoutId() {
		return R.layout.presentationfolder;
	}
	
	void calcPicSize() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        int screenWidth = displaymetrics.widthPixels;
        
        final int space = (int)getResources().getDimension(R.dimen.prezent_grid_space);
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
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		calcPicSize();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateDocument();
	}

	public void updateDocument() {
		if (doc != null) {
			doc.read(docRowId, false);
			updateTotalSum();
			reloadAdapter();
		}
	}

	public void reloadAdapter() {
		((BaseAdapter)gvPresentation.getAdapter()).notifyDataSetChanged();
	}

	@Override
	protected void onStop() {
		super.onStop();
		FoldersAdapter.resetCache();
		if(doc != null)
			doc.close();
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
					setAdapterFolder(adapter);
				dismissDialog(DLG_WAIT);
				buildingProcess = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onAdapterChange(final WarehouseAdapter adapter) {
			ivFilter.setVisibility(adapter.getFilter(ZeroPositionFilter.NAME) != null ? View.VISIBLE
					: View.GONE);
			
			if( folderPath != null )
				folderPath.refreshPath(adapter);
		}

		@Override
		public void setSelection(int position) {
			PresentationFolderW.this.setSelection(position);
		}
	}

	public void setSelection(int position) {
		gvPresentation.setSelection(position);
	}

	public void setAdapterFolder(WarehouseAdapter adapter) {
		adapter.setFolder(topFolder);
	}

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
		
		if (itemId == R.id.itSetting){
			showDialog(SETTING_DLG);
			return true;
		}
		if (item.getItemId() == R.id.itZeroFilter) {
			updateForZeroFilter();
			return true;
		}
		
		return false;
	}
	
	protected void updateForZeroFilter() {
		boolean zeroFilter = false;

		if (adapter.getFilter(ZeroPositionFilter.NAME) == null) {
			adapter.putFilter(createZeroPositionFilter());
			zeroFilter = true;
		} else
			adapter.deleteFilter(ZeroPositionFilter.NAME);

		SharedPreferences pref = getSharedPreferences(Warehouse.SHARED_PREF_NAME, Context.MODE_PRIVATE);
		Editor ed = pref.edit();
		ed.putBoolean(ZERO_FILTER, zeroFilter);
		ed.commit();

		rebuildAdapter();
	}

	public void rebuildAdapter() {
		adapter.buildSet();
	}

	protected Filter createZeroPositionFilter() {
		return new ZeroPositionFilter(doc, price);
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
		View view = View.inflate(this, getSettingLayoutId(), null);
		b.setView(view);
		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) { refreshSetting(dialog); } });
		b.setNegativeButton(R.string.cancel, null);
		return b.create();
	}

	protected int getSettingLayoutId() { return R.layout.pres_setting_dialog; }

	protected void refreshSetting(DialogInterface dialog) {
		int pos = ((Spinner)
				((AlertDialog)dialog).findViewById
					(R.id.spNumCol)).getSelectedItemPosition();
		pos = pos == 0 ? -1 : pos;
		SharedPreferences pref = getSharedPreferences(SETTING, MODE_PRIVATE);
		Editor edit = pref.edit();
		edit.putInt(NUM_COL_STR, pos);
		childEditSetting(edit, (Dialog)dialog);
		edit.commit();
		calcPicSize();
		gvPresentation.setNumColumns(pos);
		adapter.notifyDataSetChanged();
	}
	
	protected void childEditSetting(Editor edit, Dialog dialog) {}

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
		
		postPrepareSettingDlg(dialog);
	}

	protected void postPrepareSettingDlg(Dialog dialog) {}

	protected int getItemLayoutId() {
		return R.layout.presentation_item;
	}

	protected int getFolderItemLayoutId() { return getItemLayoutId(); }

	@Override
	public View getFolderView(FolderTreeNode node, View convertView) {
		if( convertView == null )
			convertView = View.inflate(this, getFolderItemLayoutId(), null);

		TextView tv;
		tv = (TextView)convertView.findViewById(R.id.tvItem);
		//tv.setCompoundDrawables(null, null, null, folder);
		tv.setText(node.name);
		tv.setBackgroundColor(Color.WHITE);
		tv.setTextColor(getResources().getColor(R.color.black));
		
		View v = convertView.findViewById(R.id.ivImage);
		
		if (v != null) 
			((ImageView)v).setImageResource(R.drawable.folder_pic);
		
		return convertView;
	}

	@SuppressWarnings("unchecked")
	protected void setPriceText(TextView textView, Price price){
		String text = price.name;
		if( Features.COST_IN_PRESENTATION ) {
			CostStrategy cs = CostStrategy.getInstance((Class<? extends Document<?>>) doc.getClass());
			text += " <i>" + Util.IntToScaleStr(cs.getItemCost(price, doc), Consts.SUM_SCALE) + "</i>";
		}
		textView.setText(Html.fromHtml(text));
	}
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		long rid = node.getRowid();
		price.read(rid);
		Price p = price.getData();

		if( convertView == null )
			convertView = View.inflate(this, getItemLayoutId(), null);

		String path = items.getImage(rid);
		TextView tv;

		tv = (TextView)convertView.findViewById(R.id.tvItem);
		
		ImageView iv = (ImageView) convertView.findViewById(R.id.ivImage);
		if(iv != null)
			iv.setVisibility(View.GONE);
		
		if( path != null ) {
			WeakReference<BitmapDrawable> b = images.get(path);
			BitmapDrawable bd = null;
			
	        try {
				if( b == null  || b.get() == null || b.get().getBounds().bottom != picSize ) {
					bd = BitmapUtils.createBitmap(path, (int) ViewUtil.dipToPixels(this, picSize));
		        	images.put(path, new WeakReference<BitmapDrawable>(bd));
				}
				else 
					bd = b.get();
	        } catch(Exception e) {
	        	e.printStackTrace();
	        }
			
			//tv.setCompoundDrawables(null, null, null, bd);
	        
	        tv.setCompoundDrawables(null, null, null, null);
	        if(iv != null){
	        	iv.setVisibility(View.VISIBLE);
	        	iv.setImageDrawable(bd);
	        }
		}
		//tv.setText(p.name);
		setPriceText(tv, p);
		if( p.qty <= 0 )
			tv.setBackgroundColor(Color.LTGRAY);
		else
			tv.setBackgroundColor(Color.WHITE);
		
		setColor(tv, p);
		
		return convertView;
	}
	
	@Override
	public void editItem(long rowid) {
		String path = items.getImage(rowid);
		if( path != null ) {
//			PricePresentation.open(this, path, docRowId, selection);
			PricePresentationFolder.open(this, rowid, docRowId, selection);
		}
	}
	
	@Override public void applySearchFilter(String value) { }
	@Override public boolean isPriceExpand() { return false; }
	@Override public void sortingPriceList(ArrayList<TreeNode> childs) { 
		Collections.sort(childs);
	}

	@Override public boolean useInterlaceBackground() { return false; }
	
	protected class PhotoFolder extends FoldersAdapter {
	
		public PhotoFolder(WarehouseManager warehouse) {
			super(warehouse);
		}
		
		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			View view = super.getView(arg0, convertView, arg2);
			view.setLayoutParams(new AbsListView.LayoutParams(picSize, (int)(picSize + getResources().getDimension(R.dimen.prezent_item_text_height))));
			return view;
		}
		
		@Override
		public void buldProcess(AsyncTask<?, ?, ?> task) {
			resetCache();
			super.buldProcess(task);
		}
	
		@Override
		protected void fillPriceIds(SQLiteDatabase database) {
			try{
				items.setWhereStr(getWhereStr());
				items.fill(solidPrice);
				fprice.clear();

				ArrayList<PresentationData> toRem = new ArrayList<PresentationData>();
				
				for(PresentationData pd : items){
					if( !inset( pd.rowid, pd.id ) )
						toRem.add(pd);
					else{
						if(!fprice.containsKey(pd.folder))
							fprice.put(pd.folder, new ArrayList<PriceInfo>());
						
						PriceInfo pi = new PriceInfo(pd.rowid, pd.name, pd.id);
						fprice.get(pd.folder).add(pi);
					}
				}
				
				for(PresentationData pd : toRem)
					items.remove(pd);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	@Override
	public void afterBuildSet() {
	}
	
	protected void updateTotalSum() {
		if (doc != null)
			updateTotalSum(doc.sum(), 0);
	}
	
	public void setColor(TextView textView, Price price) {
		if (doc != null && ((Itemsable) doc).findItem(price.id) != null)
			textView.setTextColor(getResources().getColor(((Itemsable) doc).getItemColor()));
		else if (lastBuyingItems.contains(price.id))
			textView.setTextColor(getResources().getColor(R.color.red));
		else
			textView.setTextColor(getDefaultColor(price));
	}

	private int getDefaultColor(Price p) {
		return Util.GrServerColorToSystem(p.color);
	}
	
	protected void loadLastBuyingItems(String orgId) {
		DocType dt = DocType.getCurDoc();
		if (dt instanceof DocItemsStock) {
			if (Features.PUT_SALED_ITEMS_BEFORE && dt == OrderDoc.instance()) {
				HashSet<String> idPrice = new HashSet<String>();
				priceSaledIDs.clear();
				PriceImpl pi = new PriceImpl();
				pi.setReadingFields("id,name");
				Price p = pi.getData();

				Date end = new Date();
				Calendar c = Calendar.getInstance();
				c.add(Calendar.MONTH, -1);
				Date begin = c.getTime();
				DatePeriod dp = new DatePeriod(begin, end);
				dp.periodType = DatePeriod.CREATED;
				DocList dl = dt.docList(orgId, null, dp);
				for (Document<?> d : dl) {
					OrderImpl oi = (OrderImpl) d;
					for (OrderItem item : oi.getData().items) {
						if (idPrice.contains(item.id) == false) {
							idPrice.add(item.id);
							p.id = item.id;
							if (pi.read())
								priceSaledIDs.add(pi.getRowid());
						}
					}
				}
				dl.close();
				pi.close();

				lastBuyingItems.clear();
				lastBuyingItems.addAll(idPrice);
			} else {
				if( !Features.SALES_FROM_ORDERS  && dt == OrderDoc.instance() )
					((DeliveryDoc)DeliveryDoc.instance()).getItemsFromLastDoc(orgId,lastBuyingItems, Features.LAST_SALED_ITEMS_PERIOD);
				else
					((DocItemsStock) dt).getItemsFromLastDoc(orgId,lastBuyingItems, Features.LAST_SALED_ITEMS_PERIOD);
			}
		}
	}
}
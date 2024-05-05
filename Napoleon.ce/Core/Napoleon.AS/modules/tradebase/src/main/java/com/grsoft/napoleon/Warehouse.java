package com.grsoft.napoleon;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PricePhotoHitching;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.FocusedGroup;
import com.grsoft.dataobjects.FocusedGroupItem;
import com.grsoft.dataobjects.FocusedItems;
import com.grsoft.dataobjects.FocusedItemsItem;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.MatrixOrder;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.FocusedGroupImpl;
import com.grsoft.dataobjects.impl.FocusedItemsImpl;
import com.grsoft.dataobjects.impl.MatrixImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PresentImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocItemsStock;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.FindOnClickListener;
import com.grsoft.napoleon.util.FindTextWatcher;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.napoleon.util.PresentSdcard;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FPOperation;
import com.grsoft.util.Filter;
import com.grsoft.util.FolderTree;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.InputNumber;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.MatrixAdapter;
import com.grsoft.util.MatrixOrderComparer;
import com.grsoft.util.PriceTextFilter;
import com.grsoft.util.TreeNodeCmp;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseAdapter;
import com.grsoft.util.WarehouseAdapter.OnChangeListener;
import com.grsoft.util.WarehouseManager;
import com.grsoft.util.ZeroPositionFilter;
import com.grsoft.view.BaseActivity;
import com.grsoft.view.FolderPath;

import android.annotation.SuppressLint;
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
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Warehouse extends BaseActivity implements DataSetNotify,
		WarehouseManager {
	public static Class<? extends Activity> activity = Warehouse.class;

	public static final int COLUMN_NONE = 0;
	public static final int COLUMN_QTY_WH = 1;
	public static final int COLUMN_QTY_ORD = 2;
	public static final int COLUMN_COST = 3;
	public static final int COLUMN_SUM = 4;
	public static final int COLUMN_QTY_WH_ORD = 5;
	public static final int COLUMN_COST_SUM = 6;
	public static final int COLUMN_QTY_WH_PACK = 7;

	protected int getLayoutId() {
		return R.layout.warehouse;
	}

	public Document<?> document;
	protected int folderID = -1;
	protected boolean editMode;
	private boolean inPackMode = false;
	protected boolean inItemSelectMode = false;
	protected boolean canSelectFolder = false;
	
	protected long docRowId = ExtrasConst.INVALID_ID;
	protected ListView lvItemSelect;
	protected WarehouseAdapter adapter;
	protected LinesCountController linesController;
	protected PriceImpl price = new PriceImpl();
	protected HashSet<String> packQty = null;
	protected RemnantsImpl remnantsDoc;
	protected ArrayList<String> lastBuyingItems = new ArrayList<String>();
	protected static final int DLG_MATRIX = 0;
	protected static final int DLG_VISIBLE_COLUMNS = 1;
//	protected static final int DLG_WAIT = 2;
	protected WarehouseMover priceMover;
	protected ImageView ivGoUp;
	protected TextView tvItemSelectUpLevel;
	protected ImageButton btnUp;
	protected ImageButton btnDown;
	protected ImageButton btnLines;
	protected EditText edFind;
	protected String PRICE_WITHOUT_MATRIX;
	protected String matrixName;
	protected ImageView ivFilter;
	private HashSet<Long> priceSaledIDs = new HashSet<Long>();
	public static final String SHARED_PREF_NAME = "com.grsoft.napoleon.WarehouseNew";
	public static final String EXPAND_PRICE_PREF = "expand_price";
	public static final String ZERO_FILTER = "zero_filter";
	public static final String SEL_MATRIX = "sel_matrix";
	public boolean buildingProcess = false;
	protected ImageButton btnFind;
	protected String orgid;
	protected Spinner spFind;
	FindTextWatcher textWatcher;

	HashSet<String> focusedItems = new HashSet<String>();
	HashSet<Integer> focusedGroups = new HashSet<Integer>();
	
	@SuppressLint("UseSparseArrays")
	HashMap<Integer, WarehouseCurrentOrderData> currentOrders = new HashMap<Integer, WarehouseCurrentOrderData>();
	FolderTree folderTree = new FolderTree();
	HashMap<String, Integer> foldersCache = new HashMap<String, Integer>();

	FolderPath folderPath;

	Dialog waitDialog = null;
	/**
	 * флаг определяет что форма стартует, сбрасывается в первом onResume
	 */
	protected boolean starting = true;
	static int whIndex = 0;
	static int curMatrix = 0;
	MatrixOrder matrixOrder = null;
	static long lastOrder = ExtrasConst.INVALID_ROWID;
	boolean hideMatrix = false;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PRICE_WITHOUT_MATRIX = getString(R.string.all_price);
		AssortmentMatrixAdapter.TITLE = getString(R.string.assortiment_matrix_title);
		
		matrixName = PRICE_WITHOUT_MATRIX;

		setContentView(getLayoutId());
		
		CfgNplW cfg = (CfgNplW) ConfigManager.getConfig();
		
		if(cfg.keepAwayInOrder)
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		spFind = (Spinner) findViewById(R.id.spFind);
		btnFind = (ImageButton) findViewById(R.id.btnFind);
		edFind = (EditText) findViewById(R.id.edFind);

		lvItemSelect = (ListView) findViewById(R.id.lvOrderItemSelect);
		
		textWatcher = new FindTextWatcher(edFind, lvItemSelect);
		edFind.addTextChangedListener(textWatcher);

		postInitUI();
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras()
				: savedInstanceState;
		
		// canChangeOrientation = false;
		try {
			editMode = true;
			if (b != null) {
				if (b.getString(ExtrasConst.WAREHOUSE_ID_TAG) != null)
				{
					editMode = false;
					TextView tvSum = findViewById(R.id.tvTotalSum);
					tvSum.setVisibility(View.GONE);
					inItemSelectMode = true;
					canSelectFolder = b.getInt(ExtrasConst.FOLDER_SELECT_ID_TAG) != 0;
				} else {
					docRowId = b.getLong(ExtrasConst.DOC_ROW_ID_STR,
							ExtrasConst.INVALID_ID);
					editMode = b.getBoolean(ExtrasConst.EDIT_MODE_STR, true);
					folderID = b.getInt(ExtrasConst.FOLDER_ID, -1);
				}
			}
			// документ должен быть создан и прочитан до вызова функции
			// createListAdapter()
			createDocument();
			document.read(docRowId, false);
			postDocInited();

			lvItemSelect.setDividerHeight(0);
			registerForContextMenu(lvItemSelect);
			updateTotalSum();

			lvItemSelect.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					adapter.onClick(position);
				}
			});

			OrgImpl org = new OrgImpl();
			orgid = getIntent().getStringExtra(ExtrasConst.ORG_ID_STR);
			if(orgid == null)
				orgid = document.getId();
			if (orgid != null) {
				org.getData().id = orgid;
				org.read();
				org.close();
				loadLastBuyingItems(orgid);
			}

			if (Features.FOCUSED_ITEMS) {
				FocusedItemsImpl fi = new FocusedItemsImpl();
				FocusedItems fc = fi.getData();
				fc.id = document.getId();
				if (!fi.read()) {
					fc.id = "";
					fi.read();
				}
				fi.close();
				if (fc.items != null) {
					for (FocusedItemsItem fii : fc.items)
						focusedItems.add(fii.id);
				}
			}

			if (Features.FOCUSED_GROUP) {
				FocusedGroupImpl fgi = new FocusedGroupImpl();
				FocusedGroup fg = fgi.getData();
				fg.id = document.getId();
				if (!fgi.read()) {
					fg.id = "";
					fgi.read();
				}
				fgi.close();

				if (fg.items != null) {
					for (FocusedGroupItem fgii : fg.items)
						focusedGroups.add(fgii.folderID);
				}
			}

			adapter = (WarehouseAdapter) createListAdapter();
			adapter.setOnChangeListener(adapterOnChangeListener);
			adapter.setExpanded(isPriceExpand());

			lvItemSelect.setAdapter(adapter);

			adapterInit();

			if (enablePackInput()) {
				packQty = new HashSet<String>();
				ImageView iv = (ImageView) findViewById(R.id.btnPack);
				if (iv != null && document instanceof OrderImplBase<?>
						&& docRowId != ExtrasConst.INVALID_ID) {
					iv.setVisibility(View.VISIBLE);
					iv.setOnClickListener(new View.OnClickListener() {
						@Override public void onClick(View v) { packing(); }
					});
				}
			}

			btnLines = (ImageButton) findViewById(R.id.btnLines);

			LinesOnClickListener linesOnClickListener = createLinesOnClickListener();

			linesController = linesOnClickListener.getController();

			ivGoUp = (ImageView) findViewById(R.id.ivGoUp);
			ivGoUp.setVisibility(View.INVISIBLE);
			OnClickListener goUpListenr = new OnClickListener() {
				@Override public void onClick(View v) { adapter.upLevel(); }
			};

			ivGoUp.setOnClickListener(goUpListenr);

			tvItemSelectUpLevel = (TextView) findViewById(R.id.tvItemSelectUpLevel);
			tvItemSelectUpLevel.setOnClickListener(goUpListenr);

			btnUp = (ImageButton) findViewById(R.id.btnUp);
			btnUp.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (adapter.isExpanded() == false)
						adapter.prevFolder();
				}
			});

			btnDown = (ImageButton) findViewById(R.id.btnDown);
			btnDown.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (adapter.isExpanded() == false)
						adapter.nextFolder();
				}
			});

			View v = findViewById(R.id.llFind);
			if (v != null) {
				v.setVisibility(View.GONE);
				if (Features.ID_COLUMN_IN_PRICE_LIST) {
					View v1 = v.findViewById(R.id.spFind);
					if (v1 != null)
						v1.setVisibility(View.VISIBLE);
				}
			} else
				edFind.setVisibility(View.GONE);

			FindOnClickListener findOnClickListener = new FindOnClickListener(edFind, lvItemSelect, v);
			btnFind.setOnClickListener(findOnClickListener);

			v = findViewById(R.id.btnDelFind);
			if (v != null) {
				v.setOnClickListener(new OnClickListener() {
					@Override public void onClick(View v) {
						textWatcher.setNoDelay();
						edFind.setText("");
					}
				});
			}

			if (enablePackInput()) {
				packQty = new HashSet<String>();
				ImageView iv = (ImageView) findViewById(R.id.btnPack);
				if (iv != null && document instanceof OrderImplBase<?>
						&& docRowId != ExtrasConst.INVALID_ID) {
					iv.setVisibility(View.VISIBLE);
					iv.setOnClickListener(new View.OnClickListener() {
						@Override public void onClick(View v) { packing(); }
					});
				}
			}

			ivFilter = (ImageView) findViewById(R.id.ivFilterLabel);

			HorizontalScrollView scrollView = ((HorizontalScrollView) findViewById(R.id.hswPricePage));
			LinearLayout llPriceControl = (LinearLayout) findViewById(R.id.llPriceControl);

			if (scrollView != null && llPriceControl != null) {
				CfgNplW config = (CfgNplW) ConfigManager.getConfig();

				if (config.isNewPriceNavType) {
					folderPath = new FolderPath(scrollView, R.id.tvHome, R.id.llPath, this, adapter);

					scrollView.setVisibility(View.VISIBLE);
					llPriceControl.setVisibility(View.GONE);
				} else {
					scrollView.setVisibility(View.GONE);
					llPriceControl.setVisibility(View.VISIBLE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		View nextPrice = findViewById(R.id.ibNextPrice);

		if (nextPrice != null)
			nextPrice.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					curMatrix++;
					resetMatrix();
				}
			});
	}

	protected boolean enablePackInput() {
		return Features.PACK_INPUT;
	}

	protected LinesOnClickListener createLinesOnClickListener() {
		return new LinesOnClickListener( lvItemSelect, btnLines, this, true){
			@Override
			public void onLinesCountChanged() {
				super.onLinesCountChanged();

				CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
				cfg.linesCount = getController().getLinesInRow();
				ConfigManager.save();
			}
		};
	}

	protected void postDocInited() {}

	protected void loadDailySales() {
		currentOrders.clear();
		
		Date beg = Util.getDate();
		Date end = new Date(beg.getTime() + 1000l * 3600 * 24);
		
		PriceImpl pi = new PriceImpl();
		Price p = pi.getData();
		
		HashMap<String, Integer> weightCach = new HashMap<String, Integer>();
		DatePeriod dp = new DatePeriod(beg, end);
		DocList dl = OrderDoc.instance().docList(null, null, dp);
		for(Document<?> d : dl) {
			OrderImpl oi = (OrderImpl)d;
			for(OrderItem item : oi.getData().items) {
				long sum = (int)((long)item.cost * item.qty / Consts.QTY_SCALE);
				Integer baseWeight = weightCach.get(item.id);
				if( baseWeight == null ) {
					p.id = item.id;
					pi.read();
					baseWeight = p.weight;
					weightCach.put(p.id, baseWeight);
				}
				//long weight = item.qty * baseWeight; /// (Consts.QTY_SCALE * Consts.WEIGHT_SCALE);
				long weight = FPOperation.itemMul(item.qty, baseWeight, Consts.QTY_SCALE);
				Integer folder = foldersCache.get(item.id);
				if( folder == null ) {
					if( p.id.equals(item.id) == false ){
						p.id = item.id;
						pi.read();
					}
					folder = p.folderID;
					foldersCache.put(p.id, folder);
				}
				
				WarehouseCurrentOrderData val = currentOrders.get(folder);
				if( val == null ) {
					val = new WarehouseCurrentOrderData();
					currentOrders.put(folder, val);
				}
				val.sum += sum;
				val.weight += weight;
			}
		}
		dl.close();
		pi.close();
		
		if( folderTree.size() == 0 )
			folderTree.load();
	}

	protected void postInitUI() {}

	@Override
	public void afterBuildSet() {
		if (Features.PRESENTATION_ON_SDCARD)
			PresentSdcard.init(this);
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

	protected void adapterInit() {
		adapter.putFilter(new LivePriceFilter());

		initZeroFilter();

		if (Features.HAVE_PRICE_MOVER && priceMover != null) {
			int fid = priceMover.getFolderID();
			if (fid >= 0)
				folderID = fid;
		}

		postAdapterInit();
	}

	protected void initZeroFilter() {
		SharedPreferences pref = getSharedPreferences(SHARED_PREF_NAME,
				Context.MODE_PRIVATE);
		if (pref.getBoolean(ZERO_FILTER, false)
				&& DocType.getCurDoc() != ReturnDoc.instance())
			adapter.putFilter(createZeroPositionFilter());
	}

	protected void postAdapterInit() {
		if (Features.OPEN_LAST_MATRIX)
			openLastMatrix();
		else
			adapter.buildSet(folderID);
	}

	protected void openLastMatrix() {
		String matrix = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).getString(SEL_MATRIX, "");
		
		if(matrix.length() > 0){
			Toast.makeText(getApplicationContext(), matrix, Toast.LENGTH_SHORT).show();
			applayMatrix(matrix);
		}else
			adapter.buildSet(folderID);
	}

	@Override
	protected void onDestroy() {
		if (document != null) {
			document.close();
		}
		PriceCount.PriceMover = null;
		super.onDestroy();
	}

	public void applySearchFilter(String value) {
		if( buildingProcess )
			return;
		if (value.trim().length() > 0) {
			PriceTextFilter filter = (PriceTextFilter) adapter
					.getFilter(PriceTextFilter.NAME);

			if (filter == null) {
				filter = createPriceTextFilter();
				adapter.putFilter(filter);
			}

			boolean searchExact = false;
			if (Features.ID_COLUMN_IN_PRICE_LIST && spFind.getSelectedItemPosition() == 1) {
				filter.srchFieldName = PriceTextFilter.SRCH_ID_FLD;
				searchExact = Features.SEARCh_PRICE_ID_EXACT;
			} else
				filter.srchFieldName = PriceTextFilter.SRCH_NAME_FLD;

			adapter.setExpanded(isPriceExpand());
			filter.build(adapter, value, searchExact);
			adapter.buildSet(true);
		} else
			((FilterAdapter) adapter).resetFilter();
	}

	protected PriceTextFilter createPriceTextFilter() {
		return new PriceTextFilter();
	}

	protected void fireBuildSet() {
	}

	protected WarehouseAdapter.OnChangeListener adapterOnChangeListener = new OnChangeListener() {
		
		@Override
		public void startBuildSet(WarehouseAdapter adapter) {
			buildingProcess = true;
			btnFind.setEnabled(false);
			textWatcher.blockListner(true);
//			edFind.setEnabled(false);
			
			if(folderPath != null)
				folderPath.setEnabled(false);

			if(waitDialog == null) {
				waitDialog = createWaitDlgDialog();
				waitDialog.show();
			}
//			showDialog(DLG_WAIT);
		}

		@Override
		public void endBuildSet(WarehouseAdapter adapter) {
			try {
				btnFind.setEnabled(true);
				textWatcher.blockListner(false);
//				edFind.setEnabled(true);
				
				if(folderPath != null)
					folderPath.setEnabled(true);
				
				buildingProcess = false;
				//adapter.setFolder(adapter.getPrevTopFolder());
				fireBuildSet();

				if(waitDialog != null) {
					waitDialog.dismiss();
					waitDialog = null;
				}
//				dismissDialog(DLG_WAIT);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onAdapterChange(final WarehouseAdapter adapter) {
			ivGoUp.setVisibility(adapter.isTop() ? View.INVISIBLE
					: View.VISIBLE);
			tvItemSelectUpLevel.setText(adapter.getTitle());
			ivFilter.setVisibility(adapter.getFilter(ZeroPositionFilter.NAME) != null ? View.VISIBLE
					: View.GONE);

			if (folderPath != null)
				folderPath.refreshPath(adapter);
			
			postAdapterChange();
		}

		@Override
		public void setSelection(int position) {
			if(lvItemSelect.getAdapter().getCount() > 0 && position == -1)
				position = 0;
			lvItemSelect.setSelection(position);
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
//		case DLG_WAIT:
//			return createWaitDlgDialog();
		case DLG_MATRIX:
			return createMatrixSelectDlg();
		case DLG_VISIBLE_COLUMNS:
			return createColumnsDialog();
		default:
			return super.onCreateDialog(id);
		}
	}

	protected void postAdapterChange() { }

	protected Dialog createMatrixSelectDlg() {
		ArrayList<String> items = new ArrayList<String>();
		
		if (isAllowAllPrice())
			items.add(PRICE_WITHOUT_MATRIX);

		if (Features.ASSORTMENT_MATRIX && isValidDoc())
			items.add(AssortmentMatrixAdapter.TITLE);
		
		List<String> matrixes = MatrixImpl.getNames();

		if (matrixes != null)
			items.addAll(matrixes);

		items = prepareMatrixList(items);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_matrix);

		final String[] items_array = new String[items.size()];
		int sel_item = 0;

		for (int i = 0; i < items_array.length; i++) {
			String item = items.get(i);
			items_array[i] = item;

			if (item.equals(matrixName))
				sel_item = i;
		}
		builder.setSingleChoiceItems(items_array, sel_item,
				(dialog, item) -> {
					dialog.dismiss();
					onMatrixSelected(items_array[item]);
				});

		return builder.create();
	}

	protected void onMatrixSelected(String name) {
		Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
		if (!name.equals(PRICE_WITHOUT_MATRIX))
			applayMatrix(name);
		else
			resetMatrix();
	}

	protected boolean isAllowAllPrice() {
		return true;
	}

	public boolean isValidDoc() {
		return document.getRowid() != ExtrasConst.INVALID_ROWID;
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
			openPresentation();
			return true;
		} else if (item.getItemId() == R.id.itExpand) {
			expandingPrice();
			return true;
		} else if (item.getItemId() == R.id.itColumns) {
			Setting.open(this, Setting.WarehouseSettingActivity);
			// showDialog(DLG_VISIBLE_COLUMNS);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	protected void openPresentation() {
		if (Features.FOLDER_PRESENTATION)
			PresentationFolderW.open(this, document.getRowid(),
					adapter.getFoldersIds(), adapter.getFolderTop().id);
		else
			Presentation.open(this, document.getRowid(),
					adapter.getFoldersIds(), adapter.getFolderTop().id);
	}

	protected void expandingPrice() {
		adapter.expandSwitch();

		if (adapter != null) {
			Editor editor = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
			editor.putBoolean(EXPAND_PRICE_PREF, adapter.isExpanded());
			editor.commit();
		}
	}

	/**
	 * Сортировка прайса в развернутом варианте
	 * 
	 * @param price
	 */
	public void sortingPriceList(ArrayList<TreeNode> price) {
		if (priceSaledIDs.size() > 0
				&& adapter.getName().equals(FoldersAdapter.FOLDERS_ADAPTER))
			Collections.sort(price, new ExpandPriceCmp());
		else {
			Comparator<? super TreeNode> cmp = FoldersAdapter.TreeNodeComparator;
			if(Features.USE_MATRIX_ORDER && adapter instanceof MatrixAdapter)
				cmp = new MatrixOrderComparer(((MatrixAdapter)adapter).getMatrix());

			Collections.sort(price, cmp);
		}
	}

	class ExpandPriceCmp extends TreeNodeCmp {

		@Override
		public int compare(TreeNode lhs, TreeNode rhs) {
			if (!(lhs instanceof PriceTreeNode)
					|| !(rhs instanceof PriceTreeNode))
				return super.compare(lhs, rhs);

			PriceTreeNode l = (PriceTreeNode) lhs;
			PriceTreeNode r = (PriceTreeNode) rhs;
			boolean lc = priceSaledIDs.contains(l.getRowid());
			boolean rc = priceSaledIDs.contains(r.getRowid());
			return (lc && !rc) ? -1 : (rc && !lc) ? 1 : FoldersAdapter.TreeNodeComparator.compare(lhs, rhs);
		}

	}

	protected Filter createZeroPositionFilter() {
		return	new ZeroPositionFilter(document, price);
	}

	protected void updateForZeroFilter() {
		boolean zeroFilter = false;

		if (adapter.getFilter(ZeroPositionFilter.NAME) == null) {
			adapter.putFilter(createZeroPositionFilter());
			zeroFilter = true;
		} else
			adapter.deleteFilter(ZeroPositionFilter.NAME);

		SharedPreferences pref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
		Editor ed = pref.edit();
		ed.putBoolean(ZERO_FILTER, zeroFilter);
		ed.commit();

		adapter.buildSet();
	}

	protected boolean inheritedApplayMatrix(String matrixName){ return false; }
	
	protected void applayMatrix(String matrixName) {
		FoldersAdapter.resetCache();

		this.matrixName = matrixName;
		
		Editor ed = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit(); 
		ed.putString(SEL_MATRIX, matrixName);
		ed.commit();
		
		if (!inheritedApplayMatrix(matrixName)){
			if (matrixName.equals(AssortmentMatrixAdapter.TITLE))
				applayAdapter(createAssortementMatrixAdapter());
			else
				applayAdapter(new MatrixAdapter(this, matrixName));
		}
	}

	protected AssortmentMatrixAdapter createAssortementMatrixAdapter() {
		return new AssortmentMatrixAdapter(this, document.getId());
	}

	protected void resetMatrix() {
		FoldersAdapter.resetCache();

		Editor ed = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
		ed.putString(SEL_MATRIX, "");
		ed.commit();
		
		matrixName = PRICE_WITHOUT_MATRIX;
		applayAdapter((WarehouseAdapter) createListAdapter());
	}

	protected void applyAdapter(WarehouseAdapter newadapter, boolean expanded) {
		applyAdapter(newadapter, expanded, true);
	}

	protected void applyAdapter(WarehouseAdapter newadapter, boolean expanded, boolean setFolder) {
		adapter.close();
		newadapter.setExpanded(expanded);
		newadapter.copyFilters(adapter);
		
	    int top = setFolder ? adapter.getFolderTop().id : -1;
	
		adapter = newadapter;
		adapter.setOnChangeListener(adapterOnChangeListener);
		
		adapter.buildSet(top);
		
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				lvItemSelect.setAdapter(adapter);
			}
		});
		if(Features.HAVE_PRICE_MOVER && priceMover != null)
			priceMover.setAdapter(adapter);
	}

	protected void applayAdapter(WarehouseAdapter newadapter) {
		applyAdapter(newadapter, adapter.isExpanded());
	}

	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		return items;
	}

	private Dialog createWaitDlgDialog() {
		ProgressDialog result = new ProgressDialog(this);
		result.setMessage(getString(R.string.price_loading));
		result.setCancelable(false);

		result.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if (adapter != null)
					adapter.close();
			}
		});

		return result;
	}

	protected Dialog createColumnsDialog() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(R.string.setting_clmns);
		b.setView(View.inflate(this, R.layout.wh_colums_dialog, null));
		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				refreshColums(dialog);
			}
		});
		b.setNegativeButton(R.string.cancel, null);
		return b.create();
	}

	protected void refreshColums(DialogInterface d) {
		CfgNplW config = (CfgNplW) ConfigManager.getConfig();
		Dialog dialog = (Dialog) d;
		Spinner sp;

		sp = (Spinner) dialog.findViewById(R.id.spColumn2);
		config.priceClmn2Type = sp.getSelectedItemPosition();

		sp = (Spinner) dialog.findViewById(R.id.spColumn3);
		config.priceClmn3Type = sp.getSelectedItemPosition();

		if (Features.ID_COLUMN_IN_PRICE_LIST) {
			CheckBox cb = (CheckBox) dialog.findViewById(R.id.cbItemID);
			config.idInPriceList = cb.isChecked();
		}

		ConfigManager.save();

		notifyDataSetChanged();
	}

	void initMatrixLayout(){

	}

	FoldersAdapter createMatrixOrderAdapter(){
		hideMatrix = false;

		View matrixOrderLayout = findViewById(R.id.llMatrixOrder);
		if (matrixOrderLayout != null) {
			matrixOrderLayout.setVisibility(View.GONE);

			if( DocType.getCurDoc() == OrderDoc.instance() && document.getRowid() != ExtrasConst.INVALID_ROWID ) {
				if( lastOrder != document.getRowid() ) {
					lastOrder = document.getRowid();
					curMatrix = 0;
				}

				if( matrixOrder == null ) {
					matrixOrder = new MatrixOrder();
					DbWriter.checkDBTable(matrixOrder.getClass());
					DbReader r = new DbReader();
					r.select(matrixOrder, matrixOrder.getTableName(), null);
					r.close();
				}

				if( curMatrix < matrixOrder.items.size() ) {
					hideMatrix = true;
					matrixOrderLayout.setVisibility(View.VISIBLE);
					String name = matrixOrder.items.get(curMatrix).name;
					TextView tv = findViewById(R.id.tvMatrixName);
					tv.setText(name);

					if(name.equals("<Активный ассортимент>")) {
						return createAssortementMatrixAdapter();
					}

					return new MatrixAdapter(this, name);
				}
			}
		}

		return null;
	}
	/**
	 * Инициализация адаптера document должен быть создан и прочитан до вызова
	 * этой функции
	 * 
	 * @return
	 */
	protected BaseAdapter createListAdapter() {
		int newIndex = 0;

		if( document instanceof OrderImpl) {
			newIndex = ((Order)document.getData()).whIndex;
		}

		if(whIndex != newIndex) {
			whIndex = newIndex;
			FoldersAdapter.resetCache();
		}

		FoldersAdapter ret = createAdapterInstance();

		if (Features.SHOW_ZERO_FILTER)
			ret.putFilter(createZeroPositionFilter());

		return ret;
	}

	protected FoldersAdapter createAdapterInstance() {
		FoldersAdapter moa = createMatrixOrderAdapter();

		if (moa != null)
			return moa;

		if(adapter == null && Features.OPEN_ASSORTIMENT_IN_REMNANTS) {
			if (document instanceof RemnantsImpl) {
				List<MatrixItem> mitems = new ArrayList<>();
				AssortmentMatrixAdapter.collectItems(document.getId(), mitems);
				if(mitems.size() > 0) {
					return new AssortmentMatrixAdapter(this, document.getId(), mitems);
				}
			}
		}
		return new FoldersAdapter(this);
	}

	protected void packing() {
		int resId = R.drawable.pack_off;

		inPackMode = !inPackMode;
		if (inPackMode) {
			resId = R.drawable.pack_on;
		} else {
			if (packQty.size() > 0) {
				InputNumberDlg.open(this, new InputNumber() {
					@Override
					public long getValue() {
						return Consts.QTY_SCALE;
					}

					@Override
					public boolean isInpack() {
						return Features.INPUT_QTY_IN_PACK;
					}

					@Override
					public boolean useComma() {
						return Features.INTEGER_INPUTS_QTY;
					}

					@Override
					public void applayInput(int value, Object... params) {
						changeQty(value, (params == null) ? false
								: (Boolean) params[0]);
					}
				}, Consts.QTY_SCALE, true, getString(R.string.input_qty), useInpackInPacking());
			}
		}

		ImageView iv = (ImageView) findViewById(R.id.btnPack);
		iv.setImageResource(resId);
	}
	
	protected boolean useInpackInPacking() { return true; }

	@SuppressWarnings("unchecked")
	protected void changeQty(int value, boolean inPack) {
		PriceImpl p = new PriceImpl();
		Price prc = p.getData();
		OrderImplBase<?> o = (OrderImplBase<?>) document;
		CostStrategy cs = CostStrategy
				.getInstance((Class<? extends Document<?>>) document.getClass());
		for (String e : packQty) {
			prc.id = e;
			if (p.read()) {
				packetInsert(o, p, value, inPack, cs);
//				int cv = value;
//
//				if (inPack) {
//					int pack = p.getData().qtyInPack;
//
//					if (pack == 0)
//						pack = Consts.QTY_SCALE;
//
//
//					cv = (int) ((long) value * pack)/ Consts.QTY_SCALE;
//				}
//
//				o.updateQty(p, cv, cs.getItemCost(prc, o), inPack);
			}
		}
		p.close();
		packQty.clear();
		updateTotalSum();
		notifyDataSetChanged();
	}

	void packetInsert(OrderImplBase<?> o, PriceImpl p, int qty, boolean inPack, CostStrategy cs) {
		int cv = qty;

		if (inPack) {
			int pack = p.getData().qtyInPack;
			if (pack == 0)
				pack = Consts.QTY_SCALE;

			cv = (int) ((long) qty * pack)/ Consts.QTY_SCALE;
		}

		o.updateQty(p, cv, cs.getItemCost(p.getData(), o), inPack);
	}

	@Override
	public void notifyDataSetChanged() {
		if (document != null && document.getRowid() != ExtrasConst.INVALID_ID) {
			readDocument();
			updateTotalSum();
		}

		BaseAdapter adapter = (BaseAdapter) lvItemSelect.getAdapter();

		if (adapter != null)
			adapter.notifyDataSetChanged();
	}

	protected void readDocument() {
		document.read(document.getRowid(), false);
	}

	protected void createDocument() {
		document = DocType.getCurDoc().create();
		if (!(document instanceof Itemsable))
			document = OrderDoc.instance().create();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (outState != null) {
			if (document != null) {
				outState.putLong(ExtrasConst.DOC_ROW_ID_STR,
						document.getRowid());
				outState.putString(ExtrasConst.ORG_ID_STR, document.getId());
			}

			outState.putBoolean(ExtrasConst.EDIT_MODE_STR, editMode);

			if (folderID != -1)
				outState.putInt(ExtrasConst.FOLDER_ID, folderID);
		}
	}

	protected void updateTotalSum() {
		if (document != null)
			updateTotalSum(document.sum(), 0);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return adapter;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(getOptionsMenuId(), menu);
		return true;
	}

	protected int getOptionsMenuId() {
		return R.menu.warehouse_opt_menu;
	}

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

		TextView tvSales = (TextView)result.findViewById(R.id.tvSales);
		if(tvSales != null ) {
			int visibility = View.GONE;
			if(isShowDailySales() && currentOrders.size() > 0) {
				visibility = View.VISIBLE;
				WarehouseCurrentOrderData sales = getSales(node.id);
				String text = "";
				
				if( sales.sum > 0) {
					text = Util.IntToScaleStr(sales.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
					if(Features.SHOW_DAILY_WEIGHT_IN_WAREHOUSE) {
						text += "<br/>";
						text += DocType.getCurDoc().weightToString(sales.weight, getString(R.string.kg));
						//text += Util.IntToScaleStr(sales.weight, 1, Util.DEC_DELIM, true) + " " + getString(R.string.kg);
					}
				}
					
				tvSales.setText(Html.fromHtml(text));
			}
			tvSales.setVisibility(visibility);
		}
		
		return result;
	}

	protected boolean isShowDailySales() {
		CfgNpl cfg =  (CfgNpl) ConfigManager.getConfig();
		return cfg.showDailySales;
	}

	protected WarehouseCurrentOrderData getSales(int folderId) {
		WarehouseCurrentOrderData ret = new WarehouseCurrentOrderData();//currentOrders.get(folderId);
//		if( ret == null )
//			ret = new WarehouseCurrentOrderData();
		
		int index = folderTree.findFolder(folderId);
		if( index >= 0 ) {
			// добавим все подчиненные папки
			Folder f = folderTree.get(index);
			for(  ; index < folderTree.size(); index++) {
				Folder check = folderTree.get(index);
				if( check.level <= f.level && f != check )
					break;
				
				WarehouseCurrentOrderData csum = currentOrders.get(check.id); 
				if( csum != null ) {
					ret.sum += csum.sum;
					ret.weight += csum.weight;
				}
			}
		}
		return ret;
	}

	protected int getFolderLayoutId() {
		return R.layout.itemselectrow;
	}

	void readPriceNode(long rowid) {
		price.read(rowid, false);		
	}
	
	public View getPriceView(PriceTreeNode node, View convertView) {
		readPriceNode(node.getRowid());
		Price p = price.getData();

		View view;
		int id = getItemLayoutId();
		if (convertView != null && convertView.getTag(id) != null)
			view = convertView;
		else {
			view = View.inflate(this, id, null);
			view.setTag(id, true);
		}

		setName(view, p, 1, node);

		TextView tvClmn1 = (TextView) view.findViewById(R.id.tvClmn1);
		TextView tvClmn2 = (TextView) view.findViewById(R.id.tvClmn2);

		WindowManager wm = (WindowManager) view.getContext().getSystemService(
				Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);

		int cellWidth = calcCellWidth(metrics);

		tvClmn1.setVisibility(inItemSelectMode ? View.GONE : View.VISIBLE);
		tvClmn2.setVisibility(inItemSelectMode ? View.GONE : View.VISIBLE);

		LinearLayout llQuant = (LinearLayout) view.findViewById(R.id.llQuant);
		llQuant.setVisibility(inItemSelectMode ? View.GONE : View.VISIBLE);
		layoutColumns(tvClmn1, tvClmn2, cellWidth, llQuant);

		CfgNplW config = (CfgNplW) ConfigManager.getConfig();

		setTextColumnValue(tvClmn1, config.priceClmn2Type, p);
		setTextColumnValue(tvClmn2, config.priceClmn3Type, p);

		if (Features.ID_COLUMN_IN_PRICE_LIST) {
			TextView tv = (TextView) view.findViewById(R.id.tvItemID);
			if (tv != null) {
				if (config.idInPriceList) {
					tv.setVisibility(View.VISIBLE);
					tv.setText(getItemId(p));
				} else
					tv.setVisibility(View.GONE);
			}

		}

		TextView tv = view.findViewById(R.id.tvDate);

		if(tv != null) {
			tv.setVisibility(View.GONE);

			if (adapter instanceof AssortmentMatrixAdapter) {
				AssortmentMatrixAdapter.AssortmentMatrixItem mi = ((AssortmentMatrixAdapter) adapter).getMatrixItem(p.id);

				if (mi != null && mi.created != null) {
					tv.setVisibility(View.VISIBLE);
					tv.setText(Util.simpleDateFormat.format(mi.created));
					setColor(tv, p);
				}
			}
		}

		updateChildPriceView(view, p);
		
		return view;
	}

	protected void layoutColumns(TextView tvClmn1, TextView tvClmn2, int cellWidth, LinearLayout llQuant) {
		if (linesController.isMinLines()) {
			tvClmn1.setGravity(Gravity.RIGHT);
			tvClmn2.setWidth(cellWidth / 2);

			llQuant.setOrientation(LinearLayout.HORIZONTAL);
		} else {
			tvClmn1.setGravity(Gravity.RIGHT);
			tvClmn2.setWidth(cellWidth / 2);
			llQuant.setOrientation(LinearLayout.VERTICAL);
		}
	}

	protected String getItemId(Price p) { return p.id; }

	protected void updateChildPriceView(View view, Price p) {}
	
	protected int calcCellWidth(DisplayMetrics metrics) {
		return (int)(metrics.widthPixels / 2.1);
	}

	protected int getItemLayoutId() {
		return R.layout.priceitemrow;
	}

	protected void setName(View view, Price p, int linesCount,
			PriceTreeNode node) {
		TextView tvPriceItemName = (TextView) view
				.findViewById(R.id.tvPriceItemName);

		setColor(tvPriceItemName, p);
		tvPriceItemName.setText(getItemName(p));

		linesController.prepareTextView(tvPriceItemName);
		tvPriceItemName.setTag(node);
	}

	int getWhQty(Itemsable id, Price p) {
		int qty = id.getItemValue(p);
		int qip = p.qtyInPack;
		if(qip == 0) {
			qip = Consts.QTY_SCALE;
		}
		if( Features.QTY_IN_PACK_IN_DOCS &&((CfgNplW)ConfigManager.getConfig()).isPackView )
			qty = (int)((long)qty * Consts.QTY_SCALE / qip);
		
		return qty;
	}

	protected void setTextColumnValue(TextView textView, int type, Price price) {
		Itemsable id = (Itemsable) document;
		long value = 0;
		int scale = Consts.QTY_SCALE;
		
		if (id == null)
			type = COLUMN_NONE;
		
		switch (type) {
		case COLUMN_NONE:
			textView.setVisibility(View.GONE);
			return;
		case COLUMN_QTY_WH:
			value = getWhQty(id, price);
			break;
		case COLUMN_QTY_ORD:
			value = id.getItemQty(price);
			break;
		case COLUMN_COST:
			scale = Consts.SUM_SCALE;
			value = getCost(price);
			break;
		case COLUMN_SUM:
			scale = Consts.SUM_SCALE;
			value = id.getItemSum(price);
			break;
		case COLUMN_QTY_WH_ORD:
			value = id.getItemQty(price);
			if (value == 0)
				value = getWhQty(id, price);
			break;
		case COLUMN_QTY_WH_PACK: {
			value = getWhQty(id, price);
			int inPack = price.qtyInPack;
			if(inPack == 0)
				inPack = Consts.QTY_SCALE;
			String txt = Util.IntToScaleStr(value, Consts.QTY_SCALE, Util.DEC_DELIM, true);
			txt += " (" + Util.IntToScaleStr(inPack, Consts.QTY_SCALE, Util.DEC_DELIM, true) + ")";
			textView.setText(txt);
			return;
		}
		case COLUMN_COST_SUM:
			scale = Consts.SUM_SCALE;
			value = id.getItemSum(price);
			if (value == 0)
				value = getCost(price);
			break;
		}

		String text = formatCellValue(type, price, Util.IntToScaleStr(value, scale, Util.DEC_DELIM,
				(scale == Consts.QTY_SCALE)));

		textView.setText(text);
	}

	public String formatCellValue(int clmnID, Price price, String value) {
		return value;
	}

	@SuppressWarnings("unchecked")
	protected long getCost(Price price) {
		return CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass()).getItemCost(price, (Document<?>) document);
	}

	protected String getItemName(Price p) {
		return p.name;
	}

	public void setColor(TextView textView, Price price) {
		if (enablePackInput() && packQty != null
				&& packQty.contains(price.id))
			textView.setTextColor(getResources().getColor(R.color.grey));
		else if (document != null && ((Itemsable) document).findItem(price.id) != null)
			textView.setTextColor(getResources().getColor(((Itemsable) document).getItemColor()));
		else if (remnantsDoc != null && remnantsDoc.findItem(price.id) != null)
			textView.setTextColor(remnantsDoc.getItemColor());
		else if (lastBuyingItems.contains(price.id))
			textView.setTextColor(getResources().getColor(R.color.red));
		else
			textView.setTextColor(getDefaultColor(price));

		if (focusedItems.contains(price.id)
				|| focusedGroups.contains(price.folderID))
			textView.setBackgroundResource(R.drawable.focused_item_back);
		else
			setDefaultBackground(textView);
	}

	protected void setDefaultBackground(TextView textView) {
		textView.setBackgroundColor(Color.TRANSPARENT);
	}

	protected int getDefaultColor(Price p) {
		return Util.GrServerColorToSystem(p.color);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if(inItemSelectMode && canSelectFolder) {
			getMenuInflater().inflate(R.menu.price_select_folder, menu);
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.idSelect) {
			Intent i = new Intent();

			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
			TreeNode tn = (TreeNode)adapter.getItem(menuInfo.position);
			if(tn instanceof FolderTreeNode) {
				FolderTreeNode ftn = (FolderTreeNode)tn;
				i.putExtra(ExtrasConst.FOLDER_SELECT_ID_TAG, ftn.id);
			} else {
				PriceTreeNode ptn = (PriceTreeNode)tn;
				i.putExtra(ExtrasConst.WAREHOUSE_ID_TAG, ptn.getId());
			}
			setResult(RESULT_OK, i);
			finish();
		}
		return super.onContextItemSelected(item);
	}
	
	public void editItem(long rowid) {
		if (inItemSelectMode)
		{
			Intent i = new Intent();
			price.read(rowid);
			price.close();
			i.putExtra(ExtrasConst.WAREHOUSE_ID_TAG, price.getData().id);
			i.putExtra(ExtrasConst.WAREHOUSE_NAME_TAG, getItemName(price.getData()));
			setResult(RESULT_OK, i);
			finish();
		} else if (enablePackInput() && inPackMode) {
			price.read(rowid);
			price.close();
			String id = price.getData().id;
			if (packQty.contains(id))
				packQty.remove(id);
			else
				packQty.add(id);
			((BaseAdapter) lvItemSelect.getAdapter()).notifyDataSetChanged();
		} else {
			((Itemsable) document).editItem(rowid, this);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (Features.HAVE_PRICE_MOVER && priceMover != null) {
			priceMover.init();
			folderID = adapter.getFolderTop().id;
		}
		
		if (price != null)
			price.close();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (starting)
			starting = false;
		else if (document != null)
			document.read(docRowId, false);

		if (OrderImpl.class.isAssignableFrom(document.getClass()) ) {
			if( document.getRowid() != ExtrasConst.INVALID_ID) {
				long rid = RemnantsImpl.find(document.getId(), ((OrderImpl) document).getData().created);
				if (rid != ExtrasConst.INVALID_ID) {
					if (remnantsDoc == null)
						remnantsDoc = new RemnantsImpl();
					remnantsDoc.read(rid, false);
				}
			}
		}
		
		if( OrderImplBase.class.isAssignableFrom(document.getClass()) && isShowDailySales() )
			loadDailySales();

		notifyDataSetChanged();
		updateTotalSum();

		CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
		if(linesController != null)
			linesController.setLinesCount(cfg.linesCount == 0 ? LinesOnClickListener.VARIABLE_LINE_HEIGHT : cfg.linesCount);

		if( cfg.usePriceMover ) {
			priceMover = new WarehouseMover();
			PriceCount.PriceMover = priceMover;
			priceMover.setAdapter(adapter);
		} else {
			PriceCount.PriceMover = null;
		}
		// Debug.stopMethodTracing();
	}

	protected void setFolder(int folderId) {
		adapter.setFolder(folderId);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (buildingProcess || adapter == null)
			return true;

		MenuItem itZeroFilter = menu.findItem(R.id.itZeroFilter);
		if (itZeroFilter != null) {
			if (adapter.getFilter(ZeroPositionFilter.NAME) != null)
				itZeroFilter.setTitle(R.string.disable_zero_filter);
			else
				itZeroFilter.setTitle(R.string.enable_zero_filter);
		}

		MenuItem itPresentation = menu.findItem(R.id.itPresentation);
		if (itPresentation != null) {
			itPresentation.setVisible(hasPresentation());
		}

		MenuItem it = menu.findItem(R.id.itExpand);
		if (it != null) {
			if (Features.CAN_EXPAND_PRICE)
				it.setTitle((adapter.isExpanded()) ? R.string.show_folder
						: R.string.show_price);
			else
				it.setVisible(false);
		}

		MenuItem mi = menu.findItem(R.id.itMatrix);
		if( mi != null)
			mi.setVisible(!hideMatrix);

		return true;
	}

	protected boolean hasPresentation() {
		if (Features.PRESENTATION_ON_SDCARD || Features.PRESENTATION_IN_DB)
			return PresentImpl.count() > 0;
		else {
			File f = PricePhotoHitching.getPhotoDir(this);
			return (f.exists() && f.isDirectory() && f.list().length > 0 && PresentImpl
					.count() > 0);
		}
	}

	@Override
	public void onBackPressed() {
		if (!editMode && document != null && document.getRowid() != ExtrasConst.INVALID_ID)
			document.open(this);
		super.onBackPressed();
	}

	public boolean isPriceExpand() {
		return getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
				.getBoolean(EXPAND_PRICE_PREF, false);
	}

	@Override
	public boolean useInterlaceBackground() {
		return true;
	}

//
//	добавил функционал в  ZeroPositionFilter(Document<?> document, PriceImpl price) 
//
//	public class ZeroCostFilter extends Filter {
//		public static final String NAME = "ZeroCostFilter";
//		Document<?> document;
//		CostStrategy costStrategy;
//
//		@SuppressWarnings("unchecked")
//		public ZeroCostFilter(Document<?> document) {
//			super(NAME);
//			
//			this.document = document;
//			this.costStrategy = CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass());
//		}
//		
//		@Override
//		public boolean inset(long priceRowID, String id) {
//			if(document != null && price.read(priceRowID)) {
//				return (costStrategy.getItemCost(price.getData(), document) > 0);
//			}
//			return super.inset(priceRowID, id);
//		}
//	}

	class LivePriceFilter extends Filter{
		private static final String NAME = "LivePriceFilter";
		public LivePriceFilter() {
			super(NAME);
			DbWriter.checkDBTable(DbObject.getDataType(Price.class));
		}

		@Override
		public String getWhereStr() {
			CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
			return cfg.onlyNewstItems == 1 ? "hidden = 0 or hidden is null" : "";
		}

	}
}
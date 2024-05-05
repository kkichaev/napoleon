package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.ActionResult;
import com.grsoft.dataobjects.ActionResultItem;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.ItemActionData;
import com.grsoft.dataobjects.KupecAction;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.WhData;
import com.grsoft.dataobjects.impl.FirmImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.PriceComparer;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseManager;
import com.grsoft.util.ZeroPositionFilter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class WarehouseEx extends Warehouse {
	static final String ACTION_MATRIX = "<Акции>";

	static int actionIco[] = new int[] {
			R.drawable.ic_action_0,
			R.drawable.ic_action_1,
			R.drawable.ic_action_2,
			R.drawable.ic_action_3,
			R.drawable.ic_action_4,
			R.drawable.ic_action_5,
			R.drawable.ic_action_6,
	};

	String discountText = "";
//	private List<String> userMtx = new ArrayList<String>();

	protected int getOptionsMenuId() {
		return R.menu.warehouse_opt_menuex;
	}

	@Override
	protected int getLayoutId() { return R.layout.warehouseex;}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itColorFilter) {
			doColorFilter();
			return true;
		} else if (item.getItemId() == R.id.itSort) {
			sortDialog();
			return true;
		} else
			return super.onOptionsItemSelected(item);
	}

	void sortDialog() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Варианты сортировки");
		String[] vals = new String[] {
			"По продажам", "По алфавиту"
		};
		int sel = ((PriceComparer)FoldersAdapter.TreeNodeComparator).getSortByName() ? 1 : 0;
		b.setSingleChoiceItems(vals, sel, (dialog, which) -> {
			((PriceComparer)FoldersAdapter.TreeNodeComparator).setSortByName(which == 1, WarehouseEx.this);
			sortingPriceList(adapter.getRootNode().getChilds());
			adapter.notifyDataSetChanged();
			dialog.dismiss();
		});
		b.create().show();
	}

	private void doColorFilter() {
		if (adapter.getFilter(ColorFilterName) == null) {
			showDialog(R.id.color_filter_dlg);
		} else {
			adapter.deleteFilter(ColorFilterName);
			selectedColors.clear();
			adapter.buildSet();
		}
	}

	@Nullable
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		if (id == R.id.color_filter_dlg)
			return colorFilterDlg();
		else if (id == R.id.action_filter_dlg)
			return actionFilterDlg();
		else
			return super.onCreateDialog(id, args);
	}

	private Dialog actionFilterDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.actions_filter);
		View view  = View.inflate(this, R.layout.action_filter_dlg, null);
		builder.setView(view);
		GridView list = view.findViewById(R.id.list);
		list.setAdapter(new ActionListAdapter());
		list.setOnItemClickListener((p,v,pos,id)->{
			if (!selectedActions.contains(pos))
				selectedActions.add(pos);
			else
				selectedActions.remove(pos);

			((CheckBox)v.findViewById(R.id.cbSelColor)).setChecked(selectedActions.contains(pos));
		});

		builder.setPositiveButton(R.string.ok, (d, w)->{
			applyActionFilter();});
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}

	private Dialog colorFilterDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Фильтр по цветам");
		View view  = View.inflate(this, R.layout.color_filter_dlg, null);
		builder.setView(view);
		ListView list = view.findViewById(R.id.list);
		list.setAdapter(new ColorAdapter());
		list.setOnItemClickListener((p,v,pos,id)->{
			Integer c = (Integer) p.getItemAtPosition(pos);

			if (!selectedColors.contains(c))
				selectedColors.add(c);
			else
				selectedColors.remove((Integer)c);

			((CheckBox)v.findViewById(R.id.cbSelColor)).setChecked(selectedColors.contains(c));
		});

		builder.setPositiveButton(R.string.ok, (d, w)->{
			applyActionFilter();});
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}

	private void applyColorFilter() {
		if(selectedColors.size() > 0) {
			List<Integer> list = new ArrayList<>();

			for (int c : selectedColors)
				list.add(c);

			adapter.putFilter(new ColorFilter(list));
			adapter.buildSet();
		}
	}

	private void applyActionFilter() {
		if(selectedActions.size() > 0) {
			List<Integer> list = new ArrayList<>();

			for (int c : selectedActions)
				list.add(c);

			adapter.putFilter(new ActionFilter(list));
			adapter.buildSet();
		}
	}

	List<Integer> selectedColors = new ArrayList<>();
	List<Integer> selectedActions = new ArrayList<>();

	class  ActionListAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return actionIco.length;
		}

		@Override
		public Object getItem(int position) {
			return actionIco[position];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				view = View.inflate(WarehouseEx.this, R.layout.action_filter_row, null);
			}

			int id = (int) getItem(position);

			ImageView iv = view.findViewById(R.id.ivActionIcon);
			iv.setImageResource(id);

			CheckBox cb = view.findViewById(R.id.cbSelColor);
			cb.setChecked(selectedActions.contains(position));

			return view;
		}
	}

	class  ColorAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return colors.size();
		}

		@Override
		public Object getItem(int position) {
			return colors.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				view = View.inflate(WarehouseEx.this, R.layout.color_row, null);
			}

			int color = (int) getItem(position);

			View viewColor = view.findViewById(R.id.viewColor);
			viewColor.setBackgroundColor(Util.GrServerColorToSystem(color));

			CheckBox cb = view.findViewById(R.id.cbSelColor);
			cb.setTag(color);
			cb.setChecked(selectedColors.contains(color));

			return view;
		}
	}

	static List<Integer> colors = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		findViewById(R.id.btnFilter).setOnClickListener(this::filterAction);
		if (colors.size() == 0) {
			DbReader reader = new DbReader();
			for (DataObject p : reader.fetch(Price.class)) {
				int c = ((Price)p).color;
				if (c != 0 && !colors.contains(c))
					colors.add(c);
			}
		}
//		FoldersAdapter.TreeNodeComparator = new NodeComparer();
//		DbWriter.checkDBTable(UserAssortMtx.class);
//		UserAssortMtx data  = new UserAssortMtx();
//		DbReader reader = new DbReader();
//		boolean bdo = reader.select(data, DataObjectInfo.getInstance().getTableName(data.getClass()), null, null);
//		
//		if(bdo){
//			MatrixImpl mtx = new MatrixImpl();
//			mtx.read("name", data.matrix);
//			
//			for(MatrixItem i : mtx.getData().items)
//				userMtx.add(i.id);
//		}
	}

	@Override
	protected void readDocument() {
		super.readDocument();
		((PriceComparer)FoldersAdapter.TreeNodeComparator).setDocument(document instanceof OrderImplEx ? (OrderImplEx) document : null);
	}

	private void filterAction(View view) {
		if (adapter.getFilter(ActionFilterName) == null) {
			showDialog(R.id.action_filter_dlg);
		} else {
			adapter.deleteFilter(ActionFilterName);
			selectedActions.clear();
			adapter.buildSet();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		((PriceComparer)FoldersAdapter.TreeNodeComparator).setDocument(null);
	}

	@Override protected int getItemLayoutId() { return R.layout.priceitemrowex; }
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		items.add(ACTION_MATRIX);
		return items;
	}
	
	@Override
	protected void applayMatrix(String matrixName) {
		if(matrixName.equals(ACTION_MATRIX)) {
			applayAdapter(new ActionAdapter(this, document));
			return;
		}
		super.applayMatrix(matrixName);
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter ret = (FoldersAdapter)super.createListAdapter();
		if(document instanceof OrderImplEx) {
			OrgImpl oi = new OrgImpl();
			OrgEx oe = (OrgEx)oi.getData();
			oe.id = document.getId();
			oi.read();
			oi.close();
			if(oe.mark == 1) {
				FirmImpl fi = new FirmImpl();
				FirmEx f = (FirmEx)fi.getData();
				f.id = ((OrderEx)document.getData()).firmCode;
				fi.read();
				fi.close();
				if(f.mark > 0)
					ret.putFilter(new MarkFilter());
			}
		}
		return ret;
	}
	
	protected android.app.Dialog onCreateDialog(int id) {
		if(id == R.id.discount_desc_dlg) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Акции");
			b.setMessage(Html.fromHtml(discountText));
			b.setNeutralButton(android.R.string.ok, null);
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	protected void onPrepareDialog(int id, android.app.Dialog dialog) {
		if(id == R.id.discount_desc_dlg) {
			((AlertDialog)dialog).setMessage(Html.fromHtml(discountText));
		} else {	
			super.onPrepareDialog(id, dialog);
		}
	}
	
	View.OnClickListener discountShow = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			ActionResult res = (ActionResult) arg0.getTag();
			discountText = "";
			for(ActionResultItem ari : res.conditions) {
				discountText += Util.IntToScaleStr(ari.value, Consts.SUM_SCALE, Util.DEC_DELIM, false) + 
						" р. " + ari.action.id + " " + ari.action.condition + "<br/>";
			}
			showDialog(R.id.discount_desc_dlg);
		}
	};
	
	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		boolean done = false;
		if(document instanceof OrderImpl && type == COLUMN_COST) {
			ActionResult res = ((CostStrategyEx)CostStrategy.defaultInstance).getOrderItemCost(price, (OrderImpl)document);
			if(res != null) {
				done = true;
				String text = Util.IntToScaleStr(res.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false);
				if(res.conditions.size() > 0) {
					text = "<font color='red'><b>!</b></font> " + text;
					textView.setTag(res);
					textView.setOnClickListener(discountShow);
				}

				textView.setText(Html.fromHtml(text));
			}
		} 
		if(!done)
			super.setTextColumnValue(textView, type, price);
	}
	
	@Override
	public void afterBuildSet() {
		super.afterBuildSet();
		if(document instanceof OrderImpl) {
			OrderEx order = (OrderEx) document.getData();
			CostStrategyEx.loadActions(order.id, order.whCode);
		}
	}
	
	@Override
	protected Filter createZeroPositionFilter() {
		String whCode = "";
		int whIndex = 0;
		if( document instanceof OrderImplEx ) {
			OrderImplEx oie = (OrderImplEx)document;
			whCode = oie.getWhId();
			whIndex = oie.getWhIndex();
		}
		return new ZeroFilter(whIndex, whCode);
	}

	class MarkFilter extends Filter {

		public MarkFilter() {
			super("MarkFilter");
			where = "mark=1";
		}
		
	}

	final String ColorFilterName = "ColorFilter";

	class ColorFilter extends Filter {
		List<Integer> fc = new ArrayList<>();

		public ColorFilter(List<Integer> list) {
			super(ColorFilterName);
			fc.addAll(list);
		}

		@Override
		public String getWhereStr() {
			StringBuilder sb  = new StringBuilder();


			for (int c : fc) {
				if (sb.length() > 0)
					sb.append(",");
				sb.append(c);
			}

			return String.format("color in (%s)", sb.toString());
		}
	}

	final String ActionFilterName = "ColorFilter";

	class ActionFilter extends Filter {
		List<Integer> fc = new ArrayList<>();
		PriceImpl price = new PriceImpl();

		public ActionFilter(List<Integer> list) {
			super(ActionFilterName);
			fc.addAll(list);
		}

		@Override
		public boolean inset(long priceRowID, String id) {
			price.read("id", id);
			ItemActionData iad = ((CostStrategyEx)CostStrategy.defaultInstance).getActionData(price.getData(), document);

			if (iad == null)
				return false;

			return fc.contains(iad.getIcon());
		}
	}
	
	class ZeroFilter extends ZeroPositionFilter {
		
		public ZeroFilter(int whIndex, String whCode) {
			super();
			
			if( whIndex != 0) {
				WhData wh = new WhData();
				where = "id in (select id from [" + wh.getTableName() + "] where whCode='" + whCode + "' )";
				
				if(prevWhere != where) {
					FoldersAdapter.resetCache();
					prevWhere = where;
				}
			}
		}
	}

	@Override
	protected void setName(View view, Price p, int linesCount, PriceTreeNode node) {
		super.setName(view, p, linesCount, node);
		
		ImageView iv = (ImageView)view.findViewById(R.id.iAction);
		if( iv != null ) {
			ItemActionData iad = ((CostStrategyEx)CostStrategy.defaultInstance).getActionData(p, document);
			int icon = R.drawable.empty;

			if(iad != null && iad.actions.size() > 0) {
				int iconIndex = iad.getIcon();
				if(iconIndex >= actionIco.length)
					iconIndex = 0;
				icon = actionIco[iconIndex];
			}
			iv.setImageResource(icon);
		}
	}
}

class ActionAdapter extends FoldersAdapter {

	List<String> items;
	
	public ActionAdapter(WarehouseManager warehouse, Document<?> doc) {
		super(warehouse);
		
		FoldersAdapter.resetCache();
		items = ((CostStrategyEx)CostStrategy.defaultInstance).getActionItems(doc);
	}
	
	@Override
	public boolean inset(long rowid, String id, int folder) {
		return items.contains(id);
	}
}

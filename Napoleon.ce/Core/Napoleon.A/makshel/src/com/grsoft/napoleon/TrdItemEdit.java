package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.ActionDataItem;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.TrdPromoItem;
import com.grsoft.dataobjects.TrdPromoItemRng;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.Consts;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TrdItemEdit extends BaseActivity {
	public static final int TRD_ITEM_EDIT_CODE = 321;
	public static final String NAME_TAG = "name";
	public static final String SUM_TAG = "sum";
	public static final String ACTION_TAG = "action";
	protected static final int FIND_ITEM_DLG = 345;
	protected static final int EDIT_ITEM_DISCOUNT = 4560;

	String name;
	long baseSum;
	TrdPromoItem action;
	ArrayList<ActionDataItem> items;
	List<PriceEx> allItems = new ArrayList<PriceEx>();
	Adapter adapter;
	LinesCountController linesController;
		
	ActionDataItem curItem;
	View itemView;
	PriceEx priceItem;
	
	CostStrategyEx cs;
	
	public static void open(Activity context, long baseSum, String name, TrdPromoItem action, ArrayList<ActionDataItem> items) {
		Intent i = new Intent(context, TrdItemEdit.class);
		i.putExtra(NAME_TAG, name);
		i.putExtra(SUM_TAG, baseSum);
		i.putExtra(ACTION_TAG, action);
		i.putParcelableArrayListExtra(TrdActionEdit.ITEMS_TAG, items);
		
		context.startActivityForResult(i, TRD_ITEM_EDIT_CODE);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.trd_item_edit);
		Bundle b = (savedInstanceState != null) ? savedInstanceState : getIntent().getExtras();
		name = b.getString(NAME_TAG);
		baseSum = b.getLong(SUM_TAG);
		action = b.getParcelable(ACTION_TAG);
		items = b.getParcelableArrayList(TrdActionEdit.ITEMS_TAG);
		
		((TextView)findViewById(R.id.tvOrg)).setText(name + "/" + action.name);
		
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(checkAndSave()) {
					Intent i = new Intent();
					i.putParcelableArrayListExtra(TrdActionEdit.ITEMS_TAG, items);
					setResult(RESULT_OK, i);
					finish();
				}
			}
		});
		
		findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { finish(); }
		});
		
		findViewById(R.id.btnFind).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(FIND_ITEM_DLG); }
		});
		
		cs = (CostStrategyEx) CostStrategy.getInstance(TrdActionList.doc.getClass());
	
		String where = "";
		for(TrdPromoItemRng pi : action.items) {
			if(where.length() != 0)
				where += " or ";
			where += "(" + pi.getWhere() + ")";
		}
		
		DataTraveler.travel(PriceEx.class, new DataTraveler.Travel<PriceEx>(true) {
			@Override public boolean travel(DataTraveler<PriceEx> item) {
				allItems.add(item.data);
				return true;
			}
		}, where);
		
		adapter = new Adapter(allItems);
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				priceItem = (PriceEx) arg0.getItemAtPosition(arg2);
				curItem = findItem(priceItem.id);
				if(curItem == null) {
					curItem = new ActionDataItem();
					curItem.priceCost = cs.getBaseItemCost(priceItem, TrdActionList.doc);
					curItem.promoId = action.id;
					curItem.priceId = priceItem.id;
					if(action.valSbj == TrdPromoItem.SBJ_QTY)
						curItem.qty = action.start * 10;
					else {
						curItem.qty = action.start / curItem.priceCost * Consts.QTY_SCALE; 
					}
					
					if(action.valType == TrdPromoItem.TYPE_DSC) {
						curItem.dsc = action.val;
						curItem.cost = CostStrategy.costWithDiscount(curItem.priceCost, curItem.dsc, Consts.SUM_SCALE);
					} else {
						TrdPromoItemRng ci = findPromoItem(priceItem);
						if(ci != null)
							curItem.cost = ci.cost;
						else
							curItem.cost = action.val;
					}
					items.add(curItem);
				}
				showDialog(EDIT_ITEM_DISCOUNT);
			}
		});
		linesController = (new LinesOnClickListener(lv, (ImageView) findViewById(R.id.btnLines), this, true)).getController();
	}
	
	@Override
	public void onBackPressed() {
		if(checkAndSave()) {
			Intent i = new Intent();
			i.putParcelableArrayListExtra(TrdActionEdit.ITEMS_TAG, items);
			setResult(RESULT_OK, i);
		}
		super.onBackPressed();
	}
	
	protected TrdPromoItemRng findPromoItem(PriceEx prc) {
		for(TrdPromoItemRng ti : action.items) {
			if(ti.contains(prc))
				return ti;
		}
		return null;
	}

	long getInputQty() {
		return Util.StrToScale(((EditText)itemView.findViewById(R.id.edQty)).getText().toString(), Consts.QTY_SCALE);
	}

	int getDiacount() {
		return Util.StrToScale(((EditText)itemView.findViewById(R.id.edDsc)).getText().toString(), Consts.SUM_SCALE);
	}
	
	protected boolean checkDialog() {
		long qty = getInputQty();
		if(qty == 0) {
			removeItem(curItem.priceId);
			return true;
		}
		
		if(action.valType == TrdPromoItem.TYPE_DSC) {
			int dsc = getDiacount();
			if(dsc > action.val) {
				Toast.makeText(this, R.string.discount_above_max, Toast.LENGTH_SHORT).show();
				return false;
			}
			curItem.dsc = dsc;
			curItem.cost = CostStrategy.costWithDiscount(curItem.priceCost, curItem.dsc, Consts.SUM_SCALE);
		}
//
//		if(action.valSbj == TrdPromoItem.SBJ_QTY) {
//			if(qty < (action.start * 10) || (action.end != 0 && qty > (action.end * 10))) {
//				Toast.makeText(this, String.format("Товар должен быть заказан в количестве от %s до %s", 
//						Util.IntToScaleStr(action.start, Consts.SUM_SCALE), 
//						Util.IntToScaleStr(action.end, Consts.SUM_SCALE)), Toast.LENGTH_SHORT).show();
//				return false;
//			}
//		} else {
//			long sum = (long)qty * curItem.cost / Consts.QTY_SCALE;
//			if(sum < action.start || (action.end != 0 && sum > action.end)) {
//				Toast.makeText(this, String.format("Товар должен быть заказан на сумму от %s до %s", 
//						Util.IntToScaleStr(action.start, Consts.SUM_SCALE, Util.DEC_DELIM, false), 
//						Util.IntToScaleStr(action.end, Consts.SUM_SCALE, Util.DEC_DELIM, false)), Toast.LENGTH_SHORT).show();
//				return false;
//			}
//		}
//		
		
		curItem.qty = (int)qty;
		return true;
	}
	
	protected boolean checkAndSave() {
		TrdSumItemData sum = sumItems(action.id);
		if(action.valSbj == TrdPromoItem.SBJ_QTY) {
			if(sum.qty < (action.start * 10) || (action.end != 0 && sum.qty > (action.end * 10))) {
				Toast.makeText(this, String.format("Товар должен быть заказан в количестве от %s до %s", 
						Util.IntToScaleStr(action.start, Consts.SUM_SCALE), 
						Util.IntToScaleStr(action.end, Consts.SUM_SCALE)), Toast.LENGTH_SHORT).show();
				return false;
			}
		} else {
			if(sum.sum < action.start || (action.end != 0 && sum.sum > action.end)) {
				Toast.makeText(this, String.format("Товар должен быть заказан на сумму от %s до %s", 
						Util.IntToScaleStr(action.start, Consts.SUM_SCALE, Util.DEC_DELIM, false), 
						Util.IntToScaleStr(action.end, Consts.SUM_SCALE, Util.DEC_DELIM, false)), Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return true;
	}

	private void removeItem(String priceId) {
		for(ActionDataItem i : items) {
			if(i.priceId.equals(priceId)) {
				items.remove(i);
				break;
			}
		}
	}

	protected void countTotal() {
		long total = 0;
		long qty = getInputQty();
		if(action.valType == TrdPromoItem.TYPE_DSC) {
			int dsc = Util.StrToScale(((EditText)itemView.findViewById(R.id.edDsc)).getText().toString(), Consts.SUM_SCALE);
			if(dsc > action.val) {
				Toast.makeText(this, R.string.discount_above_max, Toast.LENGTH_SHORT).show();
				dsc = action.val;
			}
			total = qty * CostStrategy.costWithDiscount(curItem.priceCost, dsc, Consts.SUM_SCALE)/ Consts.QTY_SCALE; 
		} else {
			total = qty * curItem.cost / Consts.QTY_SCALE;
		}
		TextView tv = (TextView) itemView.findViewById(R.id.tvTotal);
		tv.setText(Util.IntToScaleStr(total,  Consts.SUM_SCALE, Util.DEC_DELIM, false));
	}

	
	@Override
	protected void onPrepareDialog(int id, final Dialog dialog) {
		if(id == EDIT_ITEM_DISCOUNT) {
			TextView tv;
			tv = (TextView)itemView.findViewById(R.id.tvFreeQty);
			tv.setText(Util.IntToScaleStr(priceItem.qty, Consts.QTY_SCALE));
			
			EditText ed;
			ed = (EditText)itemView.findViewById(R.id.edDsc);
			if(action.valType == TrdPromoItem.TYPE_COST)
				ed.setText(Util.IntToScaleStr(curItem.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			else
				ed.setText(Util.IntToScaleStr(curItem.dsc, Consts.SUM_SCALE));
			ed.setEnabled(action.valType == TrdPromoItem.TYPE_DSC && action.valCnd == TrdPromoItem.CND_NO_CHECK);
			
			ed = (EditText)itemView.findViewById(R.id.edQty);
			ed.setText(Util.IntToScaleStr(curItem.qty, Consts.QTY_SCALE));
			ed.selectAll();
			
			((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View arg0) {
					if( checkDialog() ) {
						dialog.dismiss();
						adapter.notifyDataSetChanged();
						refreshTotalSum();
					}
				}
			});;
			return;
		}
		super.onPrepareDialog(id, dialog);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == FIND_ITEM_DLG) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Введите название");

			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_TEXT);
			b.setView(input);
			
			b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					doSearch(input.getText().toString());
				}
			});
			
			b.setNegativeButton("Очистить", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					doSearch("");
				}
			});
			return b.create();
		} else if(id == EDIT_ITEM_DISCOUNT) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			itemView = View.inflate(this, R.layout.edt_item_discount, null);
			EditText ed = (EditText) itemView.findViewById(R.id.edDsc);
			ed.addTextChangedListener(totalWatcher);
			ed = (EditText) itemView.findViewById(R.id.edQty);
			ed.addTextChangedListener(totalWatcher);
			ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override public void onFocusChange(View arg0, boolean arg1) {
					if(arg1)
						((EditText)arg0).selectAll();
				}
			});
			b.setView(itemView);
			b.setPositiveButton(android.R.string.ok, null);
			
			b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface arg0, int arg1) { arg0.dismiss(); }
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	TextWatcher totalWatcher = new TextWatcher() {
		@Override public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { countTotal(); }
		@Override public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
		@Override public void afterTextChanged(Editable arg0) { }
	};
	
	protected void doSearch(String string) {
	
		if(string.length() == 0)
			adapter.refresh(allItems);
		else {
			List<PriceEx> items = new ArrayList<PriceEx>();
			for(PriceEx i : allItems)
				if(i.name.contains(string))
					items.add(i);
			
			adapter.refresh(items);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	void refreshTotalSum() {
		updateTotalSum(baseSum + sumItems(-1).sum, 0);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refreshTotalSum();
	}
	
	private TrdSumItemData sumItems(int i) {
		TrdSumItemData sum = new TrdSumItemData();
		for(ActionDataItem adi : items) {
			if(i == -1 || adi.promoId == i) 
				sum.add(adi);
		}
		return sum;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(NAME_TAG, name);
		outState.putLong(SUM_TAG, baseSum);
		outState.putParcelable(ACTION_TAG, action);
		outState.putParcelableArrayList(TrdActionEdit.ITEMS_TAG, items);
	}
	
	ActionDataItem findItem(String priceId) {
		for(ActionDataItem adi : items) {
			if(adi.promoId == action.id && adi.priceId.equals(priceId))
				return adi;
		}
		return null;
	}
	
	class Adapter extends BaseAdapter {
		List<PriceEx> items = new ArrayList<PriceEx>();
		
		public Adapter(List<PriceEx> items) {
			this.items = items;
		}
		
		public void refresh(List<PriceEx> items) {
			this.items = items;
			notifyDataSetChanged();
		}

		@Override public int getCount() { return items.size(); }
		@Override public Object getItem(int arg0) { return items.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if(view == null) {
				view = View.inflate(TrdItemEdit.this, R.layout.trd_item_edit_row, null);
			}
			
			TextView tv;
			PriceEx i = (PriceEx) getItem(pos);
			ActionDataItem adi = findItem(i.id);
			
			tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(i.name);
			linesController.prepareTextView(tv);
			
			tv = (TextView)view.findViewById(R.id.tvQty);
			tv.setText(adi == null || adi.qty == 0 ? "" : Util.IntToScaleStr(adi.qty, Consts.QTY_SCALE));

			tv = (TextView)view.findViewById(R.id.tvFreeQty);
			tv.setText(Util.IntToScaleStr(i.qty, Consts.QTY_SCALE));
			
			tv = (TextView)view.findViewById(R.id.tvCost);
			if(adi == null) {
				int val = action.val;
				if(action.valType == TrdPromoItem.TYPE_COST) {
					TrdPromoItemRng irng = findPromoItem(i);
					if(irng != null)
						val = irng.cost;
				}
				tv.setText(Util.IntToScaleStr(val, Consts.SUM_SCALE));
			} else
				tv.setText(action.valType == TrdPromoItem.TYPE_COST ?  
						Util.IntToScaleStr(adi.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false) : 
						Util.IntToScaleStr(adi.dsc, Consts.SUM_SCALE));

			tv = (TextView)view.findViewById(R.id.tvPrice);
			tv.setText(Util.IntToScaleStr(cs.getBaseItemCost(i, TrdActionList.doc), Consts.SUM_SCALE));

			tv = (TextView)view.findViewById(R.id.tvTotal);
			tv.setText(adi == null || adi.qty == 0 ? "" : Util.IntToScaleStr(adi.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false));
			return view;
		}
	}
}

class TrdSumItemData {
	public long sum = 0;
	public int qty = 0;
	
	public void add(ActionDataItem adi) {
		sum += adi.sum();
		qty += adi.qty;
	}
}

package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Merch;
import com.grsoft.dataobjects.MerchFolder;
import com.grsoft.dataobjects.MerchItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.ISuppl;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.InputNumberDlg.Decorator;
import com.grsoft.napoleon.MerchDetail;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.MerchDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.view.KeypadHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

public class MerchImpl extends CreatableDocument<Merch> implements Itemsable, ISuppl {

	@Override
	public void open(Context context) {
		MerchDetail.open(context, getRowid());
	}
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		super.init(context, orgId, gpsCoord);
		Warehouse.open(context, this, false);
		return false;
	}
	
	protected String getItemID(long itemRowid) {
		PriceImpl price = new PriceImpl();
		price.read(itemRowid);
		price.close();
		
		return price.getData().id;
	}

	protected boolean updateItem(String id, int system, int qty) {
		setItemVal(id, system, qty);
		return wc(); 
	}

	private void setItemVal(String id, int system, int qty) {
		MerchItem mf = (MerchItem) findItem(id);
		
		if (mf == null) 
			mf = initItem(id);
		
		mf.system = system;
		mf.qty = qty;
	}
	
	private MerchItem initItem(String id) {
		MerchItem res = new MerchItem();
		res.id = id;
		data.items.add(res);
		return res;
	}
	
	protected int getDialogLayoutID() { return R.layout.inputnumbermerch; }
	
	@Override
	public void editItem(long itemRowid, final Context context) {
		if( !isEditable() )
			return;
		
		final String id = getItemID(itemRowid);

		InputNumberDlg.open(context, new InputNumber() {
			
			@Override
			public void applayInput(int value, Object... params) {
				if (isEditable() && params.length > 1){
					applayDocInput(context, id, params[1]);
				}
			}

			protected void applayDocInput(final Context context, final String id, Object param) {
				Dialog view = (Dialog) param; 
				
				if (view != null) {
					EditText edCount = (EditText) view.findViewById(R.id.edCount);
					EditText eCount1 = (EditText) view.findViewById(R.id.edCount1);
					
					int system = Util.StrToScale(edCount.getText().toString().trim(), Consts.QTY_SCALE);
					int qty = Util.StrToScale(eCount1.getText().toString().trim(), Consts.QTY_SCALE);
					
					if (updateItem(id, system, qty)) {
						((DataSetNotify)context).notifyDataSetChanged();
						MerchDoc.instance().refreshDocSum(data.id);
					}
				}
			}
	
			@Override
			public int getValue() {				
				return 0;
			}
				
			}, Consts.SUM_SCALE, true, context.getString(R.string.input_value), false, 
			new Decorator() {
				
				@Override public int getContentView() { return getDialogLayoutID(); }
					
				private void edInit(EditText ed, final KeypadHelper nh) {
					ed.setInputType(InputType.TYPE_NULL);
					ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {				
						@Override
						public void onFocusChange(View arg0, boolean arg1) {
							if( arg1 ) {
								nh.setTargetID(arg0.getId());
								((EditText)arg0).selectAll();
							}
						}
					});
				}
				
//				private void edFocus(EditText ed) {
//					ed.requestFocus();
//					ed.selectAll();
//				}
				
				@Override
				public void adjustView(AlertDialog dialog, View view, final KeypadHelper nh) {
					EditText edCount = (EditText) view.findViewById(R.id.edCount);
					EditText edCount1 = (EditText) view.findViewById(R.id.edCount1);
					
					for(EditText tv : new EditText[] {edCount, edCount1})
						edInit(tv, nh);
					
					MerchItem mf = (MerchItem) findItem(id);
					
					if (mf != null) {
						edCount.setText(Util.IntToScaleStr(mf.system, Consts.QTY_SCALE));
						edCount1.setText(Util.IntToScaleStr(mf.qty, Consts.QTY_SCALE));
					}
					
					edCount.selectAll();
					edCount1.selectAll();
				}
			}
		);
	}

	@Override
	public DataObject findItem(String id) {
		DataObject res = null;
		
		for(MerchItem i : data.items)
			if(i.id.equals(id)) {
				res = i;
				break;
				
			}
		
		return res;
	}

	@Override
	public int getItemColor() {
		return R.color.item_highlight;
	}

	@Override
	public int getItemValue(Price item) {
		return 0;
	}

	@Override
	public int getItemQty(Price item) {
		return 0;
	}

	@Override
	public long getItemSum(Price item) {
		return 0;
	}

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		String id = priceImpl.getData().id;
		MerchItem i = (MerchItem) findItem(id);
		
		if (i == null)
			i = initMerchItem(id);
		
		i.qty = qty;
		
		return wc();
	}
	
	private MerchItem initMerchItem(String id) {
		MerchItem res = new MerchItem();
		res.id = id;
		
		data.items.add(res);
		
		return res;
		
	}

	public MerchFolder findFolder(String id) {
		MerchFolder res = null;
		
		for(MerchFolder f : data.folders)
			if(f.id.equals(id)) {
				res = f;
				break;
			}
		
		return res;
	}
	
	public boolean updateFolder(String id, int their, int mine) {
		setFolderVal(id, their, mine);
		return wc(); 
	}

	private boolean wc() {
		boolean res = write() != ExtrasConst.INVALID_ROWID;
		close();
		return res;
	}

	protected void setFolderVal(String id, int mine, int their) {
		MerchFolder mf = findFolder(id);
		
		if (mf == null) 
			mf = initFolder(id);
		
		mf.mine = mine;
		mf.their = their;
	}

	private MerchFolder initFolder(String id) {
		MerchFolder res = new MerchFolder();
		res.id = id;
		
		data.folders.add(res);
		return res;
	}
	
	public void editFolder(final Context context, final String id, final int edID) {
		if( !isEditable() )
			return;

		InputNumberDlg.open(context, new InputNumber() {
			
			@Override
			public void applayInput(int value, Object... params) {
				if (isEditable() && params.length > 1){
					applayDocInput(context, id, params[1]);
				}
			}

			protected void applayDocInput(final Context context, final String id, Object param) {
				Dialog view = (Dialog) param; 
				
				if (view != null) {
					EditText edMine = (EditText) view.findViewById(R.id.edCount);
					EditText edTheir = (EditText) view.findViewById(R.id.edTheir);
					
					int mine = Util.StrToScale(edMine.getText().toString().trim(), Consts.QTY_SCALE);
					int their = Util.StrToScale(edTheir.getText().toString().trim(), Consts.QTY_SCALE);
					
					if (updateFolder(id, mine, their)) {
						((DataSetNotify)context).notifyDataSetChanged();
						MerchDoc.instance().refreshDocSum(data.id);
					}
				}
			}
	
			@Override
			public int getValue() {				
				return 0;
			}
				
			}, Consts.QTY_SCALE, true, context.getString(R.string.input_value), false, 
			new Decorator() {
				
				@Override
				public int getContentView() {
					return R.layout.inputnumbeoosrdlg;
				}
					
				private void edInit(EditText ed, final KeypadHelper nh) {
					ed.setInputType(InputType.TYPE_NULL);
					ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {				
						@Override
						public void onFocusChange(View arg0, boolean arg1) {
							if( arg1 ) {
								nh.setTargetID(arg0.getId());
								((EditText)arg0).selectAll();
							}
						}
					});
				}
				
				private void edFocus(EditText ed) {
					ed.requestFocus();
					ed.selectAll();
				}
				
				@Override
				public void adjustView(AlertDialog dialog, View view, final KeypadHelper nh) {
					EditText edMine = (EditText) view.findViewById(R.id.edCount);
					EditText edTheir = (EditText) view.findViewById(R.id.edTheir);
					
					for(EditText tv : new EditText[] {edMine, edTheir})
						edInit(tv, nh);
					
					if (edID == R.id.tvMine) 
						edMine.requestFocus();
					if (edID == R.id.tvTheir) 
						edTheir.requestFocus();
				
					MerchFolder mf = findFolder(id);
					
					if (mf != null) {
						edMine.setText(Util.IntToScaleStr(mf.mine, Consts.QTY_SCALE));
						edTheir.setText(Util.IntToScaleStr(mf.their, Consts.QTY_SCALE));
					}
					
					edMine.selectAll();
					edTheir.selectAll();
				}
			}
		);
	}

	@Override
	public boolean isEmpty() {
		return data.folders.size() == 0 && data.items.size() == 0;
	}

	@Override
	public String getSuppl() {
		return data.suppl;
	}
}

package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.MonitoringItemMSPB;
import com.grsoft.dataobjects.MonitoringMSPB;
import com.grsoft.dataobjects.MonitoringItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.ISuppl;
import com.grsoft.napoleon.MonitoringDetail;
import com.grsoft.napoleon.MonitoringItemEdit;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;

import android.content.Context;

public abstract class MonitoringImplBase<T extends MonitoringMSPB> extends CreatableDocument<MonitoringMSPB>
implements Itemsable, ISuppl {
	@Override
	public void open(Context context) {
		MonitoringDetail.open(context, getRowid());
	}

	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		super.init(context, orgId, gpsCoord);
		Warehouse.open(context, this, false);
		return false;
	}
	
	protected int getDialogLayoutID() { return R.layout.inputnumbermonitoring; }

	protected String getItemID(long itemRowid) {
		PriceImpl price = new PriceImpl();
		price.read(itemRowid);
		price.close();
		
		return price.getData().id;
	}
	
	@Override
	public void editItem(long itemRowid, final Context context) {
		if( !isEditable() )
			return;
		
		final String id = getItemID(itemRowid);
		
		MonitoringItemEdit.open(context, rowid, id);
//		InputNumberDlg.open(context, new InputNumber() {
//			
//			@Override
//			public void applayInput(int value, Object... params) {
//				if (isEditable() && params.length > 1){
//					applayDocInput(context, id, params[1]);
//				}
//			}
//
//			protected void applayDocInput(final Context context, final String id, Object param) {
//				Dialog view = (Dialog) param; 
//				
//				if (view != null) {
//					EditText edCost = (EditText) view.findViewById(R.id.edCount);
//					EditText edCost1 = (EditText) view.findViewById(R.id.edCost1);
//					EditText edCost2 = (EditText) view.findViewById(R.id.edCost2);
//					
//					int cost = Util.StrToScale(edCost.getText().toString().trim(), Consts.SUM_SCALE);
//					int cost1 = Util.StrToScale(edCost1.getText().toString().trim(), Consts.SUM_SCALE);
//					int cost2 = Util.StrToScale(edCost2.getText().toString().trim(), Consts.SUM_SCALE);
//					
//					if (updateItem(id, cost, cost1, cost2)) {
//						((DataSetNotify)context).notifyDataSetChanged();
//						MerchDoc.instance().refreshDocSum(data.id);
//					}
//				}
//			}
//	
//			@Override
//			public int getValue() {				
//				return 0;
//			}
//				
//			}, Consts.SUM_SCALE, true, context.getString(R.string.input_value), false, 
//			new Decorator() {
//				
//				@Override public int getContentView() { return getDialogLayoutID(); }
//					
//				private void edInit(EditText ed, final KeypadHelper nh) {
//					ed.setInputType(InputType.TYPE_NULL);
//					ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {				
//						@Override
//						public void onFocusChange(View arg0, boolean arg1) {
//							if( arg1 ) {
//								nh.setTargetID(arg0.getId());
//								((EditText)arg0).selectAll();
//							}
//						}
//					});
//				}
//				
////				private void edFocus(EditText ed) {
////					ed.requestFocus();
////					ed.selectAll();
////				}
//				
//				@Override
//				public void adjustView(AlertDialog dialog, View view, final KeypadHelper nh) {
//					EditText edCost = (EditText) view.findViewById(R.id.edCount);
//					EditText edCost1 = (EditText) view.findViewById(R.id.edCost1);
//					EditText edCost2 = (EditText) view.findViewById(R.id.edCost2);
//					
//					for(EditText tv : new EditText[] {edCost, edCost1, edCost2})
//						edInit(tv, nh);
//					
//					MonitoringItem mf = (MonitoringItem) findItem(id);
//					
//					if (mf != null) {
//						edCost.setText(Util.IntToScaleStr(mf.cost, Consts.SUM_SCALE));
//						edCost1.setText(Util.IntToScaleStr(mf.cost1, Consts.SUM_SCALE));
//						edCost2.setText(Util.IntToScaleStr(mf.cost2, Consts.SUM_SCALE));
//					}
//					
//					edCost.selectAll();
//					edCost1.selectAll();
//					edCost2.selectAll();
//				}
//			}
//		);
	}

	public boolean updateItem(String id, int cost, int cost1, int cost2) {
		setItemVal(id, cost, cost1, cost2);
		return wc(); 
	}

	private void setItemVal(String id, int cost, int cost1, int cost2) {
		MonitoringItemMSPB mf = (MonitoringItemMSPB) findItem(id);
		
		if (mf == null) 
			mf = initItem(id);
		
		mf.cost = cost;
		mf.cost1 = cost1;
		mf.cost2 = cost2;
		
	}

	private MonitoringItemMSPB initItem(String id) {
		MonitoringItemMSPB res = new MonitoringItemMSPB();
		res.id = id;
		data.items.add(res);
		return res;
	}

	private boolean wc() {
		boolean res = write() != ExtrasConst.INVALID_ROWID;
		close();
		return res;
	}

	
	@Override
	public DataObject findItem(String itemId) {
		DataObject res = null;
		
		for(MonitoringItemMSPB i : data.items)
			if (i.id.equals(itemId)) {
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
		return false;
	}

	public void deleteItem(String id) {
		MonitoringItem i = (MonitoringItem) findItem(id);
		
		if (i != null) {
			data.items.remove(i);
			wc();
		}
	}
	
	@Override
	public boolean isEmpty() {
		return data.items.size() == 0;
	}
	
	@Override
	public String getSuppl() {
		return data.suppl;
	}
}

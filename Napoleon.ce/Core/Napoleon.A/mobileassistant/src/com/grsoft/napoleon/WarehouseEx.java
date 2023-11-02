package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import com.grsoft.dataobjects.DogovorItem;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.DogovorImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixBaseAdapter;


public class WarehouseEx extends WarehouseNew implements ScannerHelper.DocUpdated {
	protected static final String TAG = "WHEx";
	ScannerHelper helper;
	EditText tv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		FoldersAdapter.resetCache();
		super.onCreate(savedInstanceState);
	
		if( document instanceof OrderImpl )
			helper = new ScannerHelper((OrderImpl)document, this);
		
		helper.registerReciver(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(isFinishing())
			helper.unregisterReciver(this);
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		Log.d(TAG, "Activity key down: " + event.getNumber());
		
		if( event.getKeyCode() != KeyEvent.KEYCODE_BACK && helper != null)
			return helper.onKeyDown(event);
		
		return super.dispatchKeyEvent(event);
	}
	
	@Override protected FoldersAdapter createAdapterInstance() { return new MatrixBaseAdapter(this) {
			
			@Override
			protected List<? extends MatrixItem> getMatrixItems() {
				List<MatrixItem> result = new ArrayList<MatrixItem>();
				
				if (document instanceof OrderImpl){
					OrderEx o = (OrderEx) document.getData();
					DogovorImpl dgv = new DogovorImpl();
					dgv.read("id", o.dgv);
					
					for(int i = 0; i < dgv.getData().items.size(); i++ ){
						MatrixItem mi = new MatrixItem();
						DogovorItem di = dgv.getData().items.get(i);
						
						mi.id = di.id;
						mi.order = i;
						result.add(mi);
					}
				}
				return result;
			}
		};
	}
	
	@Override protected void initZeroFilter() { }
	
	@Override
	protected int getOptionsMenuId() { return R.menu.warehouse_opt_menuex;	}
	
	@Override public boolean isPriceExpand() { return true; }

	@Override public void updated(OrderImpl doc, PriceImpl p) { PriceCount.open(this, p.getRowid(), doc); }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if( helper != null )
			helper.close();
	}	
}

//class EditTextEx extends EditText {
//
//	public EditTextEx(Context context) {
//		super(context);
//	}
//	
//	@Override
//	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
//		Log.d("EditTextEx", text.toString()); 
//		super.onTextChanged(text, start, lengthBefore, lengthAfter);
//	}
//}

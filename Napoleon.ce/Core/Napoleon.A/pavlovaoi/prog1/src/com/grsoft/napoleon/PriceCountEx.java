package com.grsoft.napoleon;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.Consts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.TextView;

public class PriceCountEx extends PriceCount {
	
	Runnable runAfterAlert;
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected boolean isInputValid(Runnable r) {
		if(!cbPackets.isChecked()) {
			PriceEx pe = (PriceEx)price.getData();
			int quant = pe.quant == 0 ? 1 : pe.quant;
			quant *= Consts.QTY_SCALE;
			
			int qty = getCountValue();
			int rest = qty % quant; 
			if(rest != 0) {
				boolean showAlert = false;
				ConfigImpl ci = new ConfigImpl();
				StringBuilder sb = new StringBuilder();
				if( ci.getValue(sb, "ПредупреждатьОбОкруглении")){
					try {
						showAlert = Integer.parseInt(sb.toString()) == 1;
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				
				qty += (quant - rest);
				edCount.setText(Integer.toString(qty/ Consts.QTY_SCALE));
				if( showAlert ) {
					runAfterAlert = r;
					showDialog(R.id.quantAlert);
					return false;
				}
			}
		}
		return super.isInputValid(r);
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		TextView tv = (TextView)findViewById(R.id.tvQuant);
		PriceEx pe = (PriceEx)price.getData();
		
		int quant = pe.quant;
		if(quant == 0)
			quant = 1;
		tv.setText(Integer.toString(quant));
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == R.id.quantAlert) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Ошибка");
			b.setMessage("Количество будет округлено в большую сторону");
			b.setOnCancelListener(new DialogInterface.OnCancelListener(){
				@Override
				public void onCancel(DialogInterface arg0) {
					if(runAfterAlert!= null) {
						Runnable r = runAfterAlert;
						runAfterAlert = null;
						r.run();
					}
				}
			});
			b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					if(runAfterAlert!= null) {
						Runnable r = runAfterAlert;
						runAfterAlert = null;
						r.run();
					}
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
}

package com.grsoft.napoleon;

import com.grsoft.dataobjects.SalesEx;
import com.grsoft.napoleon.modules.print.NPrinter;

import android.os.Bundle;
import android.view.View;

public class SalesDetailEx extends SalesDetail {
	@Override
	protected String[] createPrintCaption() {
		boolean isBlack = ((SalesEx)doc.getData()).isBlack != 0;
		return isBlack ? new String[] { "Накладная", "Доверенность" } : 
				//new String[] {NPrinter.TORG_12_CAPTION, NPrinter.SCHET_FACT_CAPTION, "Счет", "Доверенность", "Удостоверение качества"};
				new String[] {NPrinter.TORG_12_CAPTION, NPrinter.SCHET_FACT_CAPTION, "Счет", "Доверенность"};
	}
	
	protected SelectPrinFormDlg createPrintDlg() {
		return new SelectPrintFormDlgNew(this, WAIT_FOR_PRINT_DLG);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		View v = findViewById(R.id.btnSend);
		if(v != null)
			v.setVisibility(View.VISIBLE);
	}
	
//	@Override
//	protected void drawItemQty(int color, OrderItem item, TextView tvQty) {
//		boolean showPack = (item.inPack() && ((CfgNplW)ConfigManager.getConfig()).isPackView);
//		String qtyText;
//		if( !showPack )
//			qtyText = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE) + " уп.";
//		else {
//			Price p = price.getData();
//			int inPack = p.qtyInPack;
//			if( inPack == 0 )
//				inPack = Consts.QTY_SCALE;
//			int qty = (int)((long)item.qty * Consts.QTY_SCALE / inPack);
//			qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE) + " шт.";
//		}
//		tvQty.setText(qtyText);
//		tvQty.setGravity(Gravity.RIGHT);
//		tvQty.setTextColor(color);
//	}
//	
//	@Override
//	protected void updateTotalSum() {
//		updateTotalSum(doc.sum(), doc.weight(), doc.count());
//	}
}

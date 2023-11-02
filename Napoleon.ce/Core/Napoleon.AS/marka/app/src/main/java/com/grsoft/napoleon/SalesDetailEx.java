package com.grsoft.napoleon;

import com.grsoft.dataobjects.SalesEx;
import com.grsoft.napoleon.modules.print.NPrinter;

public class SalesDetailEx extends SalesDetail {
	@Override
	protected String[] createPrintCaption() {
		boolean isBlack = ((SalesEx)doc.getData()).isBlack != 0;
		return isBlack ? new String[] { "Накладная" } :
				new String[] {NPrinter.TORG_12_CAPTION, NPrinter.SCHET_FACT_CAPTION, NPrinter.UPD_CAPTION };
	}
//
//	protected SelectPrinFormDlg createPrintDlg() {
//		return new SelectPrintFormDlgNew(this, WAIT_FOR_PRINT_DLG);
//	}
	
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

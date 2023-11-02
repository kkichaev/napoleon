package com.grsoft.napoleon;

import android.text.Html;
import android.view.View;
import android.widget.TextView;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;


public class PricePresentationFolderEx extends PricePresentationFolder {
	@SuppressWarnings("unchecked")
	protected void setText(View view, PresentationData pd) {
		TextView tv = (TextView) view.findViewById(R.id.tvPriceItems);
		StringBuilder sb = new StringBuilder();
		pi.read("id", pd.id);
		Price p = pi.getData();
		CostStrategy cs = CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass());
		
		sb.append("***** ")
			.append(pd.name)
			.append("<br><i>")
			.append(Util.IntToScaleStr(p.qtyInPack, Consts.QTY_SCALE))
			.append("/")
			.append(Util.IntToScaleStr(cs.getItemCost(p, document), Consts.SUM_SCALE))
			.append("/")
			.append(Util.IntToScaleStr(p.qty, Consts.QTY_SCALE))
			.append("</i>");
		
		tv.setText(Html.fromHtml(sb.toString()));
	}
}

package com.grsoft.napoleon;

import android.text.Html;
import android.view.View;
import android.widget.TextView;
import com.grsoft.dataobjects.impl.PrezentDataImp;

public class PricePresentationFolderEx extends PricePresentationFolder {
	PrezentDataImp prezendData = new PrezentDataImp();
	
	@Override
	protected void setText(View view, PresentationData pd) {
		prezendData.read(pd.rowid);
		prezendData.close();

		TextView tv = (TextView) view.findViewById(R.id.tvPriceItems);
		tv.setText(Html.fromHtml(pd.name + "<br><i>" + prezendData.getData().remark + "</i>"));
		tv.setTextColor(prezendData.getData().color);
	}
}

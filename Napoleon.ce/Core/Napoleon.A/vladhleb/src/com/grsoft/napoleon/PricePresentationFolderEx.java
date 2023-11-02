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
		
		((TextView)view).setText(Html.fromHtml(pd.name + 
				"<br><i>" + prezendData.getData().remark + "</i>"));
		((TextView)view).setTextColor(prezendData.getData().color);
	}
}

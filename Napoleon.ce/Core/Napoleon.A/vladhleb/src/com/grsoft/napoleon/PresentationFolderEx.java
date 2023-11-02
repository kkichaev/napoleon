package com.grsoft.napoleon;

import android.text.Html;
import android.widget.TextView;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PrezentDataImp;

public class PresentationFolderEx extends PresentationFolder {
	PrezentDataImp prezendData = new PrezentDataImp();
	
	@Override
	protected void setPriceText(TextView textView, Price price) {
		prezendData.getData().id = price.id;
		prezendData.read();
		prezendData.close();
		
		textView.setText(Html.fromHtml(price.name + 
				"<br><i>" + prezendData.getData().remark + "</i>"));
		textView.setTextColor(prezendData.getData().color);
	}
}

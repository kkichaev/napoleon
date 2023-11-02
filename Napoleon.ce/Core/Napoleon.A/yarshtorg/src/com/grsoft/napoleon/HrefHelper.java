package com.grsoft.napoleon;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

public class HrefHelper implements View.OnClickListener{

	String href;
	
	public HrefHelper(String href) {
		this.href = href;
	}
	
	@Override
	public void onClick(View v) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(href));
		v.getContext().startActivity(i);
	}
	
	public static void setImageView(ImageView iv, String href, boolean gone) {
		if(href.length() > 0) {
			iv.setOnClickListener(new HrefHelper(href));
			iv.setVisibility(View.VISIBLE);
		} else {
			iv.setVisibility(gone ? View.GONE : View.INVISIBLE);
		}
	}
}

package com.grsoft.napoleon;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.grsoft.dataobjects.Contact;
import com.grsoft.dataobjects.ContactEx;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents 
implements OnClickListener{
	
	@Override
	public void setContactView(Contact contact, View view){
		if (contact != null && view != null){
			ContactEx cex = (ContactEx)contact;
			
			TextView tvBday = (TextView)view.findViewById(R.id.tvBday);
			
			if(tvBday != null && cex.bday != null)
				tvBday.setText(String.format("Дата рождения: %s", Util.simpleDateFormat.format(cex.bday)));
			
			TextView tvMail = (TextView)view.findViewById(R.id.tvMail);
			
			if(tvMail != null){
				tvMail.setText(cex.email);
				tvMail.setOnClickListener(this);
			}
		}
	}
	
	@Override
	protected int getContactViewid() {
		return R.layout.org_detail_info_rowex;
	}

	@Override
	public void onClick(View v) {
		if(v != null && v instanceof TextView){
			TextView tvMail = (TextView)v;
			String mail = tvMail.getText().toString();
			
			if(mail != null && mail.length() > 0){
				Intent intent = new Intent(android.content.Intent.ACTION_SEND);
				intent.setType("plain/text");
				intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"to@email.com"});
				v.getContext().startActivity(Intent.createChooser(intent, "Отправить e-mail..."));
			}
			
		}
		
	}
}

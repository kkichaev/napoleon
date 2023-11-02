package com.grsoft.napoleon.util;
import com.grsoft.aceteam.R;

import com.grsoft.dataobjects.Contact;
import com.grsoft.dataobjects.Org;
import com.grsoft.aceteam.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class OrgInfoClickListener implements OnClickListener {
	
	protected Org o;
	Context context;
	int contactViewId;
	ContactViewChanger changer;
	
	public interface ContactViewChanger {
		public void setContactView(Contact contact, View view);
	}
	
	public OrgInfoClickListener(Org o, int contactViewId, ContactViewChanger changer) {
		this.o = o;
		this.contactViewId = contactViewId;
		this.changer = changer;
	}

	public void onClick(View v) {
		context = v.getContext();
		AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
		builder.setTitle(R.string.inform);
		View dialogView = View.inflate(context, getContentView(), null);
		builder.setView(dialogView);
		final AlertDialog dialog = builder.create();
		TextView tvDetailName = (TextView) dialogView.findViewById(R.id.tvName);
		tvDetailName.setText(o.name);
		TextView tvAddress = (TextView) dialogView.findViewById(R.id.tvAddress);
		tvAddress.setText(o.address);
		
		if (o.contacts.size() > 0){
			ListView lvContacts = (ListView) dialogView.findViewById(R.id.lvContacts);
			lvContacts.setAdapter(new ContactsListAdapter());
		}
		
		adjustDialogView(dialogView);
		dialog.show();
	}

	protected int getContentView() {
		return R.layout.org_detail_info;
	}

	protected void adjustDialogView(View view) {
		ListView lvContacts = (ListView) view.findViewById(R.id.lvContacts);
		lvContacts.setOnItemClickListener(new MakePhoneCall());
	}
	
	class MakePhoneCall implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
			TextView tvPhone = (TextView) view.findViewById(R.id.tvPhone);
			Intent intent = new Intent(Intent.ACTION_CALL, 
					Uri.parse(String.format("tel: %s", 
							tvPhone.getText().toString())));
			context.startActivity(intent);
		}
		
	}
	
	class ContactsListAdapter extends BaseAdapter {

		@Override
		public int getCount() { return o.contacts.size(); }

		@Override
		public Object getItem(int arg0) { return o.contacts.get(arg0); }

		@Override
		public long getItemId(int arg0) { return 0; }

		@Override
		public View getView(int position, View view, ViewGroup arg2)
		{
			Contact contact = (Contact) getItem(position);
			
			if(view == null)
				view = View.inflate(context, contactViewId, null);
			
			view.setTag(contact);
			TextView tvFio = (TextView) view.findViewById(R.id.tvFio);
			TextView tvPhone = (TextView) view.findViewById(R.id.tvPhone);
			tvFio.setText(Html.fromHtml(contact.name.trim() + "<br>" + contact.phone.trim()));
			tvPhone.setText(contact.phone.trim());
			if( changer != null )
				changer.setContactView(contact, view);
			return view;
		}
	}
}

package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.Contact;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;

public class DocListEx extends DocList {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getDocStatusResource(CreatableDocument<?> doc) {
		int result = DocStatus.getImage(doc); 
		
		if(result == Consts.INVALID_ID)
			result = R.drawable.notsend;
		
		return result;
	}
	
	@Override
	protected DocListAdapter createListAdapter(DocType docType) { 
		return new DocListAdapterEx(this, docType, saveDatePeriod, R.layout.docs_list_row2ex);
	}

	@Override
	protected String getDocText(Org o, Document<?> doc) {
		return "<b>" + o.name + "</b>";
	}
	
	protected String docSumText(Document<?> doc) {
		return Util.IntToScaleWStr(getDocSum(doc), Consts.SUM_SCALE, 2, true);
	}
	
	@Override
	protected void drawData(View view, Document<?> doc, int position) {
		super.drawData(view, doc, position);
		
		Org o = org.getData();
		o.id = doc.getId();
		org.read();
		org.close();
		
		TextView tv = (TextView) view.findViewById(R.id.tvAddress);
		if(tv != null){
			tv.setText(Html.fromHtml("<i>" + org.getData().address + "</>"));
			tv.setVisibility(View.GONE);
		}
		
		tv = (TextView) view.findViewById(R.id.tvName);
		
		if(tv != null)
			tv.setText(Html.fromHtml(org.getData().name));
	}
	
	private String curorgid = ""; 
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		boolean result = false;
		
		if(item.getItemId() == R.id.iInfo){
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
			CreatableDocument<?> doc = (CreatableDocument<?>) adapter.getItem(menuInfo.position);
			curorgid = doc.getId();
			showDialog(R.id.org_info_dlg);
			result = true;
		}else
			result = super.onContextItemSelected(item);
		return result;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case R.id.org_info_dlg:
			return createOrgInfoDlg();
		default:
			return super.onCreateDialog(id);
		}
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id){
		case R.id.org_info_dlg:
			prepareOrgInfoDlg(dialog);
			break;
		default:
			super.onPrepareDialog(id, dialog);
		}
		
	}
	
	private void prepareOrgInfoDlg(Dialog dialog) {
		OrgImpl org = new OrgImpl();
		org.read("id", curorgid);
		Org o = org.getData();
		
		TextView tvDetailName = (TextView) dialog.findViewById(R.id.tvName);
		tvDetailName.setText(o.name);
		TextView tvAddress = (TextView) dialog.findViewById(R.id.tvAddress);
		tvAddress.setText(o.address);
		
		if (o.contacts.size() > 0){
			ListView lvContacts = (ListView) dialog.findViewById(R.id.lvContacts);
			lvContacts.setAdapter(new ContactsListAdapter(o));
			lvContacts.setOnItemClickListener(new MakePhoneCall());
		}
	}
	
	class MakePhoneCall implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
			TextView tvPhone = (TextView) view.findViewById(R.id.tvPhone);
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(String.format("tel: %s", tvPhone.getText().toString())));
			startActivity(intent);
		}
	}
	
	class ContactsListAdapter extends BaseAdapter {
		Org org;
		public ContactsListAdapter(Org org) {
			this.org = org;
		}

		@Override
		public int getCount() { return org.contacts.size(); }

		@Override
		public Object getItem(int arg0) { return org.contacts.get(arg0); }

		@Override
		public long getItemId(int arg0) { return 0; }

		@Override
		public View getView(int position, View view, ViewGroup arg2)
		{
			Contact contact = (Contact) getItem(position);
			
			if(view == null)
				view = View.inflate(DocListEx.this, R.layout.org_detail_info_row, null);
			
			view.setTag(contact);
			TextView tvFio = (TextView) view.findViewById(R.id.tvFio);
			TextView tvPhone = (TextView) view.findViewById(R.id.tvPhone);
			tvFio.setText(Html.fromHtml(contact.name.trim() + "<br>" + contact.phone.trim()));
			tvPhone.setText(contact.phone.trim());
			return view;
		}
	}

	private Dialog createOrgInfoDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.inform);
		View dialogView = View.inflate(this, R.layout.org_detail_info, null);
		builder.setView(dialogView);
		return builder.create();
		
	}

	@Override
	protected int getContextMenu() { return R.menu.doc_context_menuex ; }
	
	protected int getFilterLayout() { return R.layout.date_selectionex;	}
	
	@Override
	protected void postUpdateFilterView(View view) {
		Spinner sp = (Spinner)view.findViewById(R.id.spStatus);
		ArrayList<String> values = new ArrayList<String>();
		values.add(getResources().getString(R.string.all));
		DocStatus.collect(values);
		ArrayAdapter<String> a = new ArrayAdapter<String>(this, R.layout.simple_spinner_layout, values);
		sp.setAdapter(a);
	}
	
	protected void setFilterText(int d1,int m1, int y1,int d2, int m2, int y2, String org){
		TextView tvFilter = (TextView) llFilterPanel.findViewById(R.id.tvFilter);
		String data = getString(R.string.date_filter, d1,m1+1,y1,d2,m2+1,y2);
		if( org != null )  
			data += "<br>по " + org;
		
		if(dialogView != null){
			Spinner sp = (Spinner) dialogView.findViewById(R.id.spStatus);
			if(sp != null && sp.getSelectedItemPosition() > 0)
				data += "<br>статус: " + sp.getSelectedItem().toString();	
		}
		
		tvFilter.setText(Html.fromHtml(data));
	}
	
	class DocListAdapterEx extends DocListAdapter{
		
		protected DocListAdapterEx(Context context, DocType docType, DatePeriod filter, int layoutid) {
			super(context, docType, filter, layoutid);
		}

		@Override
		public void fetchByPeriod(DocType docType, DatePeriod dp, String orgId,
				Price item, HashMap<Long, Integer> values) {
			super.fetchByPeriod(docType, dp, orgId, item, values);
			
			if(dialogView != null){
				Spinner sp = (Spinner)dialogView.findViewById(R.id.spStatus);
				
				if(sp != null)
					filterByStatus(sp.getSelectedItem().toString());
			}
		}
		
		public void filterByStatus(String status){
			if(!status.equals(getResources().getString(R.string.all))){
				List<Long> rmid = new ArrayList<Long>();
				for (Document<?> d : documents) {
					if(!DocStatus.getStatusStr(d).equals(status))
						rmid.add(d.getRowid());
				}
				
				if(rmid.size() > 0)
					documents.removeDocuments(rmid);
			}
		}
	}
}



package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.InvAuditItem;
import com.grsoft.dataobjects.Inventory;
import com.grsoft.dataobjects.impl.InvAuditImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.InvAuditDoc;
import com.grsoft.network.BaseFragmentActivity;
import com.grsoft.util.ExtrasConst;

public class InventoryEdit extends BaseFragmentActivity {
	public static Class<? extends FragmentActivity> activity = InventoryEdit.class;
	private ListView list;
	private Map<String, Inventory> inventory = new HashMap<String, Inventory>();
	private InvAuditImpl doc = new InvAuditImpl();
	private View btnAdd;
	private TextView tvOrg;
	private View btnSend;
	
	public static void open(Context context, long rowid){
		Intent i = new Intent(context, activity);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(i);
	}
	
	@Override protected int getLayoutID() { return R.layout.invedit; }
	
	@Override
	protected void inflateView() {
		super.inflateView();
		list = (ListView) findViewById(R.id.list);
		btnAdd = findViewById(R.id.btnAdd);
		tvOrg = (TextView) findViewById(R.id.tvOrg);
		btnSend = findViewById(R.id.btnSend);
	}

	@Override
	protected void init() {
		super.init();
		
		Intent i = getIntent();
		
		if(i != null){
			doc.read(i.getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
			
			String id = doc.getId();
			
			if(id != null && id.length() > 0){
				DataTraveler.travel(Inventory.class, new DataTraveler.Travel<Inventory>(){
					@Override public boolean isDataNewInstance() { return true; }
					
					@Override
					public boolean travel(DataTraveler<Inventory> item) {
						inventory.put(item.data.id, item.data);
						return true;
					}}, "ido='"+id+"'");
			}
		}
	}
	
	@Override
	protected void initView() {
		super.initView();
		list.setAdapter(createAdapter());
		btnAdd.setOnClickListener(addItemClick());
		registerForContextMenu(list);
		initTvOrg();
		btnSend.setOnClickListener(sendClick());
		btnAdd.setEnabled(doc.isEditable());
	}

	private OnClickListener sendClick() {
		return new OnClickListener() {
			@Override public void onClick(View v) { 
				new DocumentSender(v.getContext(), btnSend, InvAuditDoc.instance().getObjectName(), doc, doc.getRowid()){
					protected void onPostExecute(Boolean result) {
						super.onPostExecute(result);
						finish();
					};
				}.execute((Void[])null); }
		};
	}

	protected void initTvOrg() {
		OrgImpl org = new OrgImpl();
		org.read("id", doc.getId());
		tvOrg.setText(org.getData().name);
	}

	private OnClickListener addItemClick() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				InvDialog dlg = new InvDialog();
				dlg.inventoryEdit = InventoryEdit.this;
				dlg.show(getSupportFragmentManager(), dlg.getClass().getCanonicalName());
			}
		};
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (scanResult != null) {
		     String id = scanResult.getContents();
		     addNewItem(id);
		}
	}

	private void addNewItem(String id) {
		if (id != null && id.length() > 0 && doc.addItem(id, !inventory.containsKey(id))){
			InvItemsAdapter a = (InvItemsAdapter) list.getAdapter();
			a.addItem(id);
			doc.write();
			doc.close();
		}
	}

	static class InvData{
		public String id;
		public boolean isnew;
		public boolean doc;
		
		public InvData(String id, boolean isnew){
			this.id = id;
			this.isnew = isnew;
		}
	}
	
	private ListAdapter createAdapter() {
		return new InvItemsAdapter(); 
	}
	
	class InvItemsAdapter extends BaseAdapter {
		Map<String, InvData> map = new HashMap<String, InvData>();
		List<InvData> data = new ArrayList<InvData>();
		
		public InvItemsAdapter(){
			for(Inventory i : inventory.values())
				map.put(i.id, new InvData(i.id, false));
			
			for(InvAuditItem i : doc.getData().items){
				if(!map.containsKey(i.id))
					map.put(i.id, new InvData(i.id, true));
				
				map.get(i.id).doc = true;
			}
			
			data.addAll(map.values());
			Collections.sort(data, new Comparator<InvData>() {

				@Override
				public int compare(InvData lhs, InvData rhs) {
					int x = lhs.isnew ? 1 : 0;
					int y = rhs.isnew ? 1 : 0;
					return x - y;
				}});
		}
		
		public void addItem(String id) {
			addItemData(id);
			notifyDataSetChanged();
		}

		protected void addItemData(String id) {
			if(!map.containsKey(id)){
				InvData i = new InvData(id, true);
				map.put(id, i);
				data.add(i);
			}
			
			InvData i = map.get(id);
			i.doc = true;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = View.inflate(getContext(), R.layout.inveditrow, null);
			
			convertView.setBackgroundResource(R.drawable.freeitem);
			InvData id = (InvData) getItem(position);
			
			if(id != null){
				TextView tv = (TextView) convertView.findViewById(R.id.tvDocId);
				tv.setText(id.isnew ? "" : id.id);
				
				tv = (TextView) convertView.findViewById(R.id.tvFactId);
				tv.setText(id.isnew ? id.id : "");
				
				if(id.doc)
					convertView.setBackgroundResource(R.drawable.docitem);
			}
			
			return convertView;
		}
		
		@Override public long getItemId(int position) { return 0;	}
		@Override public Object getItem(int position) { return data.get(position); }
		@Override public int getCount() {	return data.size();	}

		public void remDoc(String id) {
			remItemDoc(id);
			notifyDataSetChanged();
		}

		protected void remItemDoc(String id) {
			if(map.containsKey(id)){
				InvData i = map.get(id);
				i.doc = false;
				
				if(i.isnew){
					data.remove(i);
					map.remove(id);
				}
			}
		}
	};
	
	
	public static class InvDialog extends DialogFragment{
		private EditText edNumber;
		public InventoryEdit inventoryEdit;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.invdialog, null, false);
			getDialog().setTitle(R.string.inv_doc);
			View v = view.findViewById(R.id.btnScanner);
			v.setOnClickListener(scanClick());
			v = view.findViewById(R.id.btnOK);
			v.setOnClickListener(inputClick());
			edNumber = (EditText)view.findViewById(R.id.edNumber);
			
			return view;
		}

		private OnClickListener inputClick() {
			return new OnClickListener() {
				@Override
				public void onClick(View v) {
					inventoryEdit.addNewItem(edNumber.getText().toString().trim());
					dismiss();
				}
			};
		}
		
		public OnClickListener scanClick() {
			return new OnClickListener() {
				@Override
				public void onClick(View v) {
					IntentIntegrator ii = new IntentIntegrator((Activity) getContext());
					ii.initiateScan();
					dismiss();
				}
			};
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.invedit_context, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		boolean result = false;
		if(item.getItemId() == R.id.itDelete){
			AdapterView.AdapterContextMenuInfo m = (AdapterContextMenuInfo) item.getMenuInfo();
			InvItemsAdapter a = (InvItemsAdapter) list.getAdapter();
			InvData d = (InvData) a.getItem(m.position);
			a.remDoc(d.id);
			
			doc.remItem(d.id);
			doc.write();
			doc.close();
		}
		
		return result;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(doc.isEmpty()){
			doc.delete();
			doc.close();
		}
	}
}
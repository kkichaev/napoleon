package com.grsoft.napoleon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.grsoft.dataobjects.InvEquItem;
import com.grsoft.dataobjects.impl.InvEquImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.VisitImplEx;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.util.ExtrasConst;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InvEquEdit extends Activity {
	private ListView list;
	private InvEquImpl doc = new InvEquImpl();
	private final static String FRGID = "idfrg";
	private View btnScanItem;

	public static void open(Context context, long rowid) {
		Intent i = new Intent(context, InvEquEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invequedit);

		list = (ListView) findViewById(R.id.list);
		btnScanItem = findViewById(R.id.btnScanItem);
		btnScanItem.setOnClickListener((x)->{addItem();});

		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		doc.close();

		list.setAdapter(new Adapter());

		OrgImpl org = new OrgImpl();
		org.read("id", doc.getId());
		
		TextView tv = (TextView) findViewById(R.id.tvOrg);
		tv.setText(org.getData().name);

		registerForContextMenu(list);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		getMenuInflater().inflate(R.menu.inv_equ_edit_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.itClear){
			int pos = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
			InvEquItem bi = (InvEquItem)(list.getAdapter()).getItem(pos);

			VisitImplEx.removePhoto(doc.getData().visitDoc.getTime(), bi.id);

			if(bi.newItem == 1)
				doc.getData().items.remove(bi);
			else
				bi.check = 0;

			doc.write();
			doc.close();

			((Adapter)list.getAdapter()).buildData();
			((Adapter)list.getAdapter()).notifyDataSetChanged();

			return true;
		}

		return false;
	}

	protected void addItem() {
		IntentIntegrator ii = new IntentIntegrator(InvEquEdit.this);
		ii.initiateScan();
	}

	private InvEquItem createNewItem(String name, String number, String barcode) {
		InvEquItem item = new InvEquItem();
		item.id = UUID.randomUUID().toString().replace("-", "");
		item.number = number;
		item.name = name;
		item.barcode = barcode;
		item.newItem = 1;
		item.check = 0;
		
		return item;
	}

	protected void barcodeCheck(String bc) {
		InvEquItem item = doc.getItemByBarcode(bc);
		
		if (item == null){
			item = createNewItem(getString(R.string.new_equip), "", bc);
			doc.getData().items.add(item);

			doc.write();
			doc.close();
		}

		PhotoBarcode.open(this, doc.getRowid(), item.id, bc);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		doc.read(doc.getRowid(), false);
		doc.close();

		((Adapter)list.getAdapter()).buildData();
		((Adapter)list.getAdapter()).notifyDataSetChanged();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (isFinishing() && doc.isEmpty()){
			doc.delete();
			doc.close();
		}
	}

	private class Adapter extends BaseAdapter implements FilterAdapter {
		List<InvEquItem> data = new ArrayList<InvEquItem>();
		
		public Adapter() {
			buildData();
		}
		
		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null)
				view = View.inflate(InvEquEdit.this, R.layout.invfrgeditrow, null);

			InvEquItem i = (InvEquItem) getItem(position);

			TextView tv = view.findViewById(R.id.tvName);
			tv.setText(i.name);

			tv = view.findViewById(R.id.tvBarcode);
			tv.setText(getString(R.string.barcode_title,i.barcode));
			
			int bd = R.drawable.list_selector;
			
			if (i.newItem == 1)
				bd = R.drawable.red_row;
			else if (i.check == 1)
				bd = R.drawable.gray_row;
			
			view.setBackgroundDrawable(getResources().getDrawable(bd));
			
			return view;
		}

		@Override
		public void applyFilter(String value) {
			data.clear();
			value = value.toUpperCase();
			for (InvEquItem i : doc.getData().items) {
				if (i.name.toUpperCase().contains(value) || i.number.toUpperCase().contains(value))
					data.add(i);
			}
			
			((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
		}

		@Override
		public void resetFilter() {
			buildData();
			((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
		}
		
		public void buildData() {
			data.clear();
			data.addAll(doc.getData().items);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (scanResult != null) {
		     String bc = scanResult.getContents();
		     
		     if (bc != null)
			     barcodeCheck(bc);
		}
	}
}

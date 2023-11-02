package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DMPType;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.impl.DMPImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.ExtrasConst;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DMPItemsList extends Activity implements OnItemClickListener, OnClickListener, android.content.DialogInterface.OnClickListener{
	private DMPImpl doc = new DMPImpl();
	private TextView tvInfo;
	private ListView list;
	private String priceID = "";
	private final static int PHOTO_REQUEST = 0;
	public final static String PRICE_ID = "price_id";
	private View btnNew;
	private List<DMPType> dtl = new ArrayList<DMPType>();
	private DMPItemsListAdapter adapter;
	
	
	public static void open(Context context, long rowid, String itemid) {
		Intent i = new Intent(context, DMPItemsList.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		i.putExtra(PRICE_ID, itemid);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dmpitemslist);
		tvInfo = (TextView) findViewById(R.id.tvOrgInfo);
		list = (ListView) findViewById(R.id.list);
		btnNew = findViewById(R.id.btnNew);
		
		priceID = getIntent().getExtras().getString(PRICE_ID);
		
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		doc.close();
		
		PriceImpl p = new PriceImpl();
		p.read("id", priceID);
		tvInfo.setText(p.getData().name);
		
		DataTraveler.travel(DMPType.class, new DataTraveler.Travel<DMPType>(true) {

			@Override
			public boolean travel(DataTraveler<DMPType> item) {
				dtl.add(item.data);
				return true;
			}
		}, null);
		
		if (doc.isEditable()) {
			btnNew.setOnClickListener(this);
			registerForContextMenu(list);
		}
		
		adapter = new DMPItemsListAdapter(this);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		getMenuInflater().inflate(R.menu.dmp_conext_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itDel) {
			DMPType d = (DMPType) adapter.getItem(((AdapterContextMenuInfo)item.getMenuInfo()).position);
			doc.removeDMP(priceID, d.id);
			adapter.refresh();
			((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
			return true;
		}else
			return super.onContextItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		DMPType t = (DMPType) parent.getItemAtPosition(position);
		DMPItemEdit.open(this, doc.getRowid(), priceID, t.id);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		adapter.refresh();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		showDialog(R.id.dmp_type_dlg);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.dmp_type_dlg)
			return createDmpTypeDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog createDmpTypeDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		CharSequence[] arr = new CharSequence[dtl.size()];
		
		for(int i = 0; i < dtl.size(); i++)
			arr[i] = dtl.get(i).text;
			
		builder.setItems(arr, this);
		builder.setTitle(R.string.select_dmp);
		
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		DMPType d = dtl.get(which);
		DMPItemEdit.open(this, doc.getRowid(), priceID, d.id);
	}
	
	public DMPImpl getDocument() {
		return doc;
	}
	
	public String getPriceID() {
		return priceID;
	}
	
	public List<DMPType> getDMPTypes(){
		return dtl;
	}

	public View getRowView(int position, View view, DMPType item) {
		if(view == null)
			view = View.inflate(this, R.layout.dmitemslistrow, null);
		
		TextView tv = (TextView) view.findViewById(R.id.tvItem);
		tv.setText(item.text);
		
		return view;
	}
}

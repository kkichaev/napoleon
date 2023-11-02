package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.grsoft.dataobjects.Fridge;
import com.grsoft.dataobjects.InvFrgItem;
import com.grsoft.dataobjects.impl.InvFrgImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.FindOnClickListener;
import com.grsoft.napoleon.util.FindTextWatcher;
import com.grsoft.util.ExtrasConst;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class InvFrgEdit extends Activity {
	private ListView list;
	private InvFrgImpl doc = new InvFrgImpl();
	boolean creating = true;
	private List<Fridge> data = new ArrayList<Fridge>();
	private final static String FRGID = "idfrg";
	private View btnAddItem;
	private String selId = "";
	private View findView;
	private View btnFind;
	private EditText edFind;
	private FindTextWatcher textWatcher;
	
	public static void open(Context context, long rowid) {
		Intent i = new Intent(context, InvFrgEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invfrgedit);

		list = (ListView) findViewById(R.id.list);
		btnAddItem = findViewById(R.id.btnAddItem);
		findView = findViewById(R.id.llFind);
		btnFind = findViewById(R.id.btnFind);
		edFind = (EditText) findViewById(R.id.edFind);
		
		btnAddItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addItem();
			}
		});

		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		doc.close();

		list.setAdapter(new Adapter());
		list.setOnItemClickListener(itemClick);
		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle args = new Bundle();
				InvFrgItem f = (InvFrgItem) parent.getItemAtPosition(position);
				args.putString(FRGID, f.id);
				showDialog(R.id.input_barcode_dlg, args);
				return true;
			}
		});
		
		OrgImpl org = new OrgImpl();
		org.read("id", doc.getId());
		
		TextView tv = (TextView) findViewById(R.id.tvOrg);
		tv.setText(org.getData().name);
		
		btnFind.setOnClickListener(new FindOnClickListener(edFind, list, findView));
		
		View v = findViewById(R.id.btnDelFind);
		if (v != null) {
			v.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) { edFind.setText(""); }
			});
		}
		
		textWatcher = new FindTextWatcher(edFind, list);
		edFind.addTextChangedListener(textWatcher);
		findView.setVisibility(View.GONE);
	}

	protected void addItem() {
		if(doc.isEditable())
			showDialog(R.id.new_item_dlg);
	}

	OnItemClickListener itemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			InvFrgItem f = (InvFrgItem) parent.getItemAtPosition(position);
			selId = f.id;
			
			IntentIntegrator ii = new IntentIntegrator(InvFrgEdit.this);
			ii.initiateScan();
		}
	};
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		if (id == R.id.input_barcode_dlg)
			return createBarcodeDlg();
		if(id == R.id.new_item_dlg)
			return createNewItemDlg();
		else
			return super.onCreateDialog(id);
	}
	
	private Dialog createNewItemDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.new_frg_item);
		builder.setView(View.inflate(this, R.layout.frgitemedit, null));
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				appendNewItem((Dialog) dialog);
				((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
			}
		});
		
		builder.setNegativeButton(R.string.cancel, null);
		
		return builder.create();
	}

	protected void appendNewItem(Dialog dialog) {
		InvFrgItem item = new InvFrgItem();
		item.id = UUID.randomUUID().toString().replace("-", "");
		EditText ed = (EditText) dialog.findViewById(R.id.edInvNum);
		item.number = ed.getText().toString().trim();
		
		ed = (EditText) dialog.findViewById(R.id.edName);
		item.name = ed.getText().toString().trim();
		
		doc.getData().items.add(item);
		doc.write();
	}

	private Dialog createBarcodeDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = new EditText(this);
		view.setId(R.id.edBarcode);
		builder.setView(view);
		builder.setTitle(R.string.input_barcode);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText ed = (EditText) ((Dialog)dialog).findViewById(R.id.edBarcode);
				String id = ed.getTag().toString();
				
				InvFrgItem item = doc.getItem(id);
				if (item != null) 
					item.barcode = ed.getText().toString().trim();
				
				doc.write();
				doc.close();
				
				((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
			}
		});
		
		builder.setNegativeButton(R.string.cancel, null);
		
		return builder.create();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		if (id == R.id.input_barcode_dlg)
			prepareBarcodeDlg(dialog, args);
		super.onPrepareDialog(id, dialog);
	}
	
	private void prepareBarcodeDlg(Dialog dialog, Bundle args) {
		EditText ed = (EditText) ((Dialog)dialog).findViewById(R.id.edBarcode);
		String id = args.getString(FRGID);
		ed.setTag(id);
		ed.setText(getDocBarcode((id)));
	}

	private class Adapter extends BaseAdapter implements FilterAdapter {
		List<InvFrgItem> data = new ArrayList<InvFrgItem>();
		
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
				view = View.inflate(InvFrgEdit.this, R.layout.invfrgeditrow, null);

			InvFrgItem i = (InvFrgItem) getItem(position);

			TextView tv = (TextView) view.findViewById(R.id.tvNumber);
			tv.setText(i.number);

			tv = (TextView) view.findViewById(R.id.tvName);
			tv.setText(i.name);

			String bc = getDocBarcode(i.id); 
			tv = (TextView) view.findViewById(R.id.tvBarcode);
			
			String bc_val = bc.length() > 0 ? getString(R.string.barcode_val, bc) : "";
			tv.setText(bc_val);
			
			view.setBackgroundDrawable(getResources().getDrawable(
					bc.length() > 0 ? R.drawable.gray_row : R.drawable.list_selector));
			
			return view;
		}

		@Override
		public void applyFilter(String value) {
			data.clear();
			value = value.toUpperCase();
			for (InvFrgItem i : doc.getData().items) {
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
		
		private void buildData() {
			data.clear();
			data.addAll(doc.getData().items);
		}
	}

	public String getDocBarcode(String id) {
		String res = "";
		InvFrgItem i = doc.getItem(id);
		
		if (i != null)
			res = i.barcode;
		
		return res;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (scanResult != null) {
		     String bc = scanResult.getContents();
		     
		     if (bc != null) {
		    	 //http://212.232.41.126/bugzilla/show_bug.cgi?id=2880
		    	 while (bc.trim().length() < 13) 
		    		 bc = "0" + bc;
		    	 
			     String id = selId;
			     selId = "";
			     
			     InvFrgItem item = doc.getItem(id);
			     
			     if (item != null)
			    	 item.barcode = bc;
			     
			     doc.write();
			     doc.close();
			     
			     ((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
		     }
		}
	}

	
}

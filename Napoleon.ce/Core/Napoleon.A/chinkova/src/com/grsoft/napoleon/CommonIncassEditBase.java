package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.Banks;
import com.grsoft.dataobjects.CommonIncass;
import com.grsoft.dataobjects.CommonIncassItem;
import com.grsoft.dataobjects.Dover;
import com.grsoft.dataobjects.IncassRights;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.BanksImpl;
import com.grsoft.dataobjects.impl.CommonIncassImpl;
import com.grsoft.dataobjects.impl.CommonIncassImplBase;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DoverImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.network.CommonIncassExportHitching;
import com.grsoft.network.DataObjectSender;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

@SuppressLint("NewApi")
public class CommonIncassEditBase extends FragmentActivity {

	protected ImageButton btnAdd;
	protected ListView list;
	protected EditText edComment;
	private TextView tvSum;
	private ImageButton btnSend;
	
	DoverImpl dover = new DoverImpl();
	IncassRights rights;

//	private List<OrgEx> orgList = new ArrayList<OrgEx>();
	protected CommonIncassImplBase<? extends CommonIncass> doc;
	
	protected static final int DATA_ROW = 0;
	protected static final int DEL_ROW = 1;
	
	protected static final int SELECT_DOVER = 0;
	protected static final int SELECT_BANK = 1;
	CommonIncassItem selectedItem = null;

	protected CommonIncassImplBase<? extends CommonIncass> createDocument() { return new CommonIncassImpl(); }
	protected String sendObjectName() { return "CommonIncass"; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_incass_edit);
		
		doc = createDocument();
		
		inflateViews();
		initData();
		initUI();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		dover.close();
	}

	protected void initUI() {
		CommonIncass ci = doc.getData();
		edComment.setText(ci.remark);
		initList();
		tvSum.setText(Util.IntToScaleStr(doc.sum(), Consts.SUM_SCALE));
		btnSend.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) { send(); }
		});

		ConfigImpl cfg = new ConfigImpl();
		if(doc.isEditable() && ci.org.length() == 0) {
			StringBuilder sb = new StringBuilder();
			if(cfg.getValue(sb, "DefaultOrg")) {
				ci.org = sb.toString();
			}
		}
		
		Spinner spOrg = (Spinner)findViewById(R.id.spFirm);
		DialogHelper.loadSpinnerWithKeyW(cfg, "Организация", new ArrayList<KeyValue>(), spOrg, ci.org, true);
		cfg.close();
		
		spOrg.setEnabled((doc.isEditable() && ci.items.size() == 0));
		spOrg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				KeyValue sel = (KeyValue) arg0.getSelectedItem();
				CommonIncass cdoc = doc.getData();
				if(!cdoc.org.equals(sel.key.toString())) {
					cdoc.bank = "";
					cdoc.items.clear();
					updateBank();
					((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
				}				
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		updateBank();
		findViewById(R.id.tvBank).setOnClickListener(setBank);
		
		if(doc.isExported()){
			edComment.setEnabled(false);
			btnAdd.setEnabled(false);
			btnSend.setEnabled(false);
			list.setOnItemClickListener(null);
		}
	}
	
	boolean isValid() {
		CommonIncass ci = doc.getData();
		if(ci.org.length() == 0)
			return false;
		
		if( rights.bank == 0 && ci.bank.length() == 0 )
			return false;
		
		if( rights.dvr == 0 ) {
			for( CommonIncassItem cii : ci.items)
				if( cii.dvr.length() == 0)
					return false;
		}
		
		return true;
	}
	
	protected void send() {
		if( !isValid() ) {
			Toast.makeText(this, R.string.fill_bank_dover, Toast.LENGTH_SHORT).show();
			return;			
		}
		if(doc.getData().items.size() == 0){
			Toast.makeText(this, R.string.cant_send_empty_doc_str, Toast.LENGTH_SHORT).show();
			return;
		}
		
		boolean hasEmpty = doc.hasEmptyItems();
		
		if(hasEmpty){
			AskToDelEmptyItems dlg = new AskToDelEmptyItems();
			dlg.show(getSupportFragmentManager(), dlg.getClass().toString());
		}else
			doSend();
	}

	protected void doSend() {
		writeDoc(doc.getData());
		doc.close();
		
		new DataObjectSender(CommonIncassEditBase.this, R.id.btnSend, new CommonIncassExportHitching(doc, sendObjectName())){
			protected void onPostExecute(Boolean result) { finish(); };
		}.execute((Void)null);
	}

	protected void initList() {
		list.setAdapter(new CIEAdapter());
		list.setOnItemClickListener(new ListItemSelector());
		list.setDividerHeight(0);
		((BaseAdapter) list.getAdapter()).registerDataSetObserver(new ListDataObserver());
		registerForContextMenu(list);
	}

	protected void initData() {
		rights = IncassRights.get();
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
//		updateOrgList();
	}

	protected void inflateViews() {
		btnAdd = (ImageButton) findViewById(R.id.btnAdd);
		list = (ListView) findViewById(R.id.list);
		edComment = (EditText) findViewById(R.id.edComment);
		tvSum = (TextView) findViewById(R.id.tvSum);
		btnSend = (ImageButton) findViewById(R.id.btnSend);
	}

	class ListDataObserver extends DataSetObserver {
		@Override public void onChanged() { tvSum.setText(Util.IntToScaleStr(doc.sum(), Consts.SUM_SCALE)); }
	}

	class ListItemSelector implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			final CommonIncassItem item = (CommonIncassItem) parent.getItemAtPosition(position);
			editItem(item);
		}
	}

	class OrgDelimiter extends OrgEx{};
	
//	protected void updateOrgList() {
//		DbReader reader = new DbReader();
//		OrgEx data = new OrgEx();
//		boolean bdo = reader.select(data, DataObjectInfo.getInstance().getTableName(data.getClass()), null);
//		
//		while (bdo) {
//			orgList.add(data);
//			data = new OrgEx();
//			bdo = reader.selectNext(data);
//		}
//		
//		Collections.sort(orgList, new Comparator<OrgEx>() {
//			@Override
//			public int compare(OrgEx lhs, OrgEx rhs) {
//				int result = lhs.orgid.compareTo(rhs.orgid);
//				
//				if(result == 0)
//					result = lhs.name.compareTo(rhs.name);
//				
//				return result; 
//			}
//		});
//
//		List<OrgEx> dl = new ArrayList<OrgEx>();
//		String orgid = null;
//		
//		for(OrgEx o :orgList){
//			if(orgid != null && !orgid.equals(o.orgid))
//				dl.add(new OrgDelimiter());
//			
//			orgid = o.orgid;
//			dl.add(o);	
//		}
//		
//		orgList = dl;
//	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if(!doc.isExported())
			getMenuInflater().inflate(R.menu.common_incass_edit_context_menu, menu);
	}

	protected void deleteItem(CommonIncassItem cii) {
		doc.getData().items.remove(cii);
		((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int itemid = item.getItemId();
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		CommonIncassItem cii = (CommonIncassItem) list.getItemAtPosition(menuInfo.position);
		switch (itemid) {
		case R.id.itDelete:
			deleteItem(cii);
			return true;
		case R.id.itEdit:
			editItem(cii);
			return true;
		case R.id.itCopy:
			CommonIncassItem ciicopy = new CommonIncassItem();
			ciicopy.id = cii.id;
			doc.getData().items.add(ciicopy);
			((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
			return true;
		default:
			return false;
		}
	}
	
	void writeDoc(CommonIncass ci) {
		ci.remark = edComment.getText().toString();

		Spinner sp = (Spinner) findViewById(R.id.spFirm);
		KeyValue kv = (KeyValue) sp.getSelectedItem();
		if(kv != null)
			ci.org = kv.key.toString();
		
		doc.write();
	}

	@Override
	public void onBackPressed() {
		CommonIncass ci = doc.getData();
		if (ci.items.size() > 0)
			removeZeroSumItems(ci);
		if (ci.items.size() == 0)
			doc.delete();
		else {
			writeDoc(ci);
		}
		doc.close();
		super.onBackPressed();
	}

	protected void removeZeroSumItems(CommonIncass ci) {
		Iterator<CommonIncassItem> iter = ci.items.iterator();
		while (iter.hasNext()) {
			CommonIncassItem item = iter.next();
			if (item.sum == 0)
				iter.remove();
		}
	}

	protected void editItem(final CommonIncassItem item) {
		InputNumberDlg.open(CommonIncassEditBase.this, new InputSum(item), Consts.SUM_SCALE, true, getString(R.string.value), false);
	}

	class InputSum extends InputNumber {

		private CommonIncassItem item;

		public InputSum(CommonIncassItem item) {
			this.item = item;
		}

		@Override
		public boolean useComma() {
			return !Features.INTEGER_INPUTS_QTY;
		}

		@Override
		public boolean replaceCommaToPlus() {
			return Features.REPLACE_COMMA_TO_PLUS;
		}

		@Override
		public void applayInput(int value, Object... params) {
			if (doc.isExported())
				return;
			item.sum = value;
			doc.write();
			doc.close();
			((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
		}

		@Override
		public int getValue() {
			return item.sum;
		}
	}

//	@SuppressLint("ValidFragment")
//	class SelectOrgDlg extends SelectDialog {
//
//		final boolean selitems[] = new boolean[orgList.size()];
//		private ListView listView;
//
//		@Override
//		public void onOKButtonPressed(View result) {
//			for (int i = 0; i < selitems.length; i++) {
//				Org org = ((Org) listView.getItemAtPosition(i));
//				CommonIncassItem item = commonIncassImpl.findItem(org.id);
//				if (selitems[i]) {
//					if (item == null) {
//						CommonIncassItem cii = new CommonIncassItem();
//						cii.id = org.id;
//						commonIncassImpl.getData().items.add(cii);
//					}
//				} else
//					commonIncassImpl.getData().items.remove(item);
//			}
//			((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
//		}
//
//		@Override
//		public int getViewId() {
//			return R.layout.select_org;
//		}
//
//		@Override
//		public void prepareView(View view) {
//			for (int i = 0; i < orgList.size(); i++) {
//				Org org = orgList.get(i);
//				selitems[i] = commonIncassImpl.findItem(org.id) != null;
//			}
//
//			listView = (ListView) view.findViewById(R.id.list);
//			listView.setDividerHeight(0);
//			listView.setAdapter(new OrgListAdapter());
//		}
//
//		@Override
//		public int getTitle() {
//			return R.string.select_orgs;
//		}
//
//		class OrgListAdapter extends BaseAdapter {
//			@Override
//			public int getCount() {
//				return orgList.size();
//			}
//
//			@Override
//			public Object getItem(int position) {
//				return orgList.get(position);
//			}
//
//			@Override
//			public long getItemId(int position) {
//				return getItem(position) instanceof OrgDelimiter ? DEL_ROW : DATA_ROW;
//			}
//
//			@Override
//			public View getView(int position, View convertView, ViewGroup parent) {
//				long itemId = getItemId(position);
//				int id = (itemId == DEL_ROW) ? R.layout.row_delimiter : R.layout.select_org_row;
//				if( convertView == null || ((Integer)convertView.getTag()) != id ) {
//					convertView = View.inflate(CommonIncassEditBase.this, id, null);
//					convertView.setTag(id);
//				}
//				
//				if(getItemId(position) != DEL_ROW){
//					Org org = (Org) getItem(position);
//					TextView tv = (TextView) convertView.findViewById(R.id.tvName);
//					tv.setText(org.name);
//					tv = (TextView) convertView.findViewById(R.id.tvAddress);
//					tv.setText(org.address);
//					CheckBox cb = (CheckBox) convertView.findViewById(R.id.cbSelected);
//					cb.setTag(position);
//					
//					cb.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
//	
//						@Override
//						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//							selitems[(Integer) buttonView.getTag()] = isChecked;
//						}
//					});
//					
//					cb.setChecked(selitems[position]);
//					convertView.setBackgroundResource(position % 2 != 0 ? R.drawable.even_row_selector : R.drawable.list_selector);
//				}
//				
//				return convertView;
//			}
//		}
//	}
//	
	View.OnClickListener setDover = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if( doc.isExported() )
				return;
			selectedItem = (CommonIncassItem)v.getTag();
			Intent i = SelectKeyValue.makeIntent(CommonIncassEditBase.this, SelectKeyValue.DocTypes.Dover);
			startActivityForResult(i, SELECT_DOVER);
		}
	};
	
	View.OnClickListener setBank = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if( doc.isExported() )
				return;
			Spinner sp = (Spinner) findViewById(R.id.spFirm);
			KeyValue kv = (KeyValue) sp.getSelectedItem(); 
			Intent i = SelectKeyValue.makeBanksIntent(CommonIncassEditBase.this, kv == null ? "" : kv.key.toString());
			startActivityForResult(i, SELECT_BANK);
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent i) {
		if( resultCode != RESULT_OK  || i == null)
			return;
		
		String id = i.getStringExtra(ExtrasConst.ORG_ID_STR);
		if( id == null )
			return;
		
		if( requestCode == SELECT_BANK ) {
			doc.getData().bank = id;
			doc.write();
			updateBank();
		} else {
			if( selectedItem != null ) {
				selectedItem.dvr = id;
				doc.write();
				((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
			}
		}
	}

	private void updateBank() {
		TextView tv;
		
		String text = doc.getData().bank;
		tv = (TextView)findViewById(R.id.tvBank);
		if( text.length() == 0){
			text = getString(R.string.input_bank);
		} else {
			BanksImpl bi = new BanksImpl();
			Banks b = bi.getData();
					
			b.id = text;
			bi.read();
			bi.close();
					
			text = b.name;
		}
		
		SpannableString content = new SpannableString(text);
		content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
		tv.setText(content);
		
	}

	class CIIDel extends CommonIncassItem{}
	
	@SuppressLint("ValidFragment")
	class CIEAdapter extends BaseAdapter {
		OrgImpl org = new OrgImpl();
		List<CommonIncassItem> data = new ArrayList<CommonIncassItem>();
		
		public CIEAdapter() {
			load();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(getItemId(position) == DEL_ROW)
				convertView = View.inflate(CommonIncassEditBase.this, R.layout.row_delimiter, null);
			else{
				convertView = View.inflate(CommonIncassEditBase.this, R.layout.common_incass_edit_row, null);
				CommonIncassItem item = (CommonIncassItem) getItem(position);
				org.read("id", item.id);
				TextView tv = (TextView) convertView.findViewById(R.id.tvName);
				tv.setText(org.getData().name);
				tv = (TextView) convertView.findViewById(R.id.tvAddress);
				tv.setText(org.getData().address);
				tv = (TextView) convertView.findViewById(R.id.tvSum);
				tv.setText(Util.IntToScaleStr(item.sum, Consts.SUM_SCALE));
				
				String text;
				tv = (TextView) convertView.findViewById(R.id.tvDvr);
				if( item.dvr.length() == 0 ) {
					if(rights.dvr == 0)
						convertView.setBackgroundResource(R.drawable.no_dvr_row);
					text = getString(R.string.no_dvr);
				} else {
					Dover d = dover.getData();
					d.id = item.dvr;
					dover.read();
					text = d.name;
					convertView.setBackgroundResource(position % 2 != 0 ? R.drawable.even_row_selector : R.drawable.list_selector);
				}
				
				if (text == null)
					text = "";
				
				SpannableString content = new SpannableString(text);
				content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
				tv.setText(content);
				tv.setTag(item);
				tv.setOnClickListener(setDover);
			}
			
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return getItem(position) instanceof CIIDel ? DEL_ROW : DATA_ROW;
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public int getCount() {
			return data.size();
		}
		
		@Override
		public void notifyDataSetChanged() {
			load();
			super.notifyDataSetChanged();
		}

		protected void load() {
			data.clear();
			data.addAll(doc.getData().items);
			Collections.sort(data, new Comparator<CommonIncassItem>() {

				@Override
				public int compare(CommonIncassItem lhs, CommonIncassItem rhs) {
					OrgEx o = (OrgEx) org.getData();
					
					org.read("id", lhs.id);
					String lhsorgid = o.orgid;
					String lhsname = o.name;;
					
					org.read("id", rhs.id);
					String rhsorgid = o.orgid;
					String rhsname = o.name;

					int result = lhsorgid.compareTo(rhsorgid);
					
					if(result == 0)
						result = lhsname.compareTo(rhsname);
					
					return result;
				}});
			
			List<CommonIncassItem> dex = new ArrayList<CommonIncassItem>();
			String orgid = null; 
			
			for(CommonIncassItem i : data){
				org.read("id", i.id);
				OrgEx o = (OrgEx) org.getData(); 
				
				if(orgid != null && !orgid.equals(o.orgid))
					dex.add(new CIIDel());
				
				orgid = o.orgid;
				dex.add(i);	
			}
			
			data = dex;
		}
	}
	
	@SuppressLint("ValidFragment")
	class AskToDelEmptyItems extends DialogFragment{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(CommonIncassEditBase.this);
			builder.setTitle(R.string.question);
			builder.setMessage(R.string.ask_to_del_empty_items);
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) {
					removeZeroSumItems(doc.getData());
					
					if(doc.getData().items.size() > 0)
						doSend();
					else
						Toast.makeText(getActivity(), R.string.cant_send_empty_doc_str, Toast.LENGTH_SHORT).show();
				}
			});
			builder.setNegativeButton(R.string.cancel, null);
			return builder.create();
		}
	}
}

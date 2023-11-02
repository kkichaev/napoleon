package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Dealer;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDealerItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgType;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DealerImpl;
import com.grsoft.napoleon.CreateOrg.DealerAdapter.Data;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class CreateOrg extends PotenzialOrg {
	public static int CREATEORGID = 1;
	private static final int ADD_DEALER_DLG = R.id.add_dealer_dlg;
	private static final String FOLDER = "FOLDER";

	Spinner spOrgType;
	EditText edCheif;
	EditText edCheifPhone;
	EditText edContact;
	EditText edContactPhone;
	CheckedTextView ctvLicense;
	ImageButton btnDealer;
	TextView tvDealers;
	EditText edAvgTraffic;
	String folder;
	EditText edEmail;

	public static void open(Activity context, String folder, long rowid) {
		boolean am = rowid == ExtrasConst.INVALID_ID;

		Intent intent = new Intent(context, CreateOrg.class);
		intent.putExtra(ExtrasConst.ORG_ID_STR, rowid);
		intent.putExtra(EDIATBLE_STR, true);
		intent.putExtra(APPEND_STR, am);
		intent.putExtra(FOLDER, folder);
		context.startActivityForResult(intent, CREATEORGID);
	}

	@Override
	protected void cancelEdit() {
		if (appendMode)
			orgImpl.delete();
		
		super.cancelEdit();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (orgImpl.getData().name.length() == 0)
				cancelEdit();
		}

		return super.onKeyDown(keyCode, event);
	}

	private void updateDealers() {
		StringBuilder sb = new StringBuilder();
		DealerImpl dealerImpl = new DealerImpl();

		OrgEx oe = (OrgEx) orgImpl.getData();

		for (OrgDealerItem i : oe.dealers) {
			dealerImpl.getData().id = i.id;
			dealerImpl.read();
			sb.append(dealerImpl.getData().name).append("<br>");
		}

		dealerImpl.close();
		tvDealers.setText(Html.fromHtml(sb.toString()));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		spOrgType = (Spinner) findViewById(R.id.spOrgType);
		edCheif = (EditText) findViewById(R.id.edCheif);
		edCheifPhone = (EditText) findViewById(R.id.edCheifPhone);
		edContact = (EditText) findViewById(R.id.edContact);
		edContactPhone = (EditText) findViewById(R.id.edContactPhone);
		ctvLicense = (CheckedTextView) findViewById(R.id.ctvLicense);
		btnDealer = (ImageButton) findViewById(R.id.btnDealer);
		tvDealers = (TextView) findViewById(R.id.tvDealers);
		edAvgTraffic = (EditText) findViewById(R.id.edAvgTraffic);
		edEmail = (EditText) findViewById(R.id.edEmail);
		
		folder = getIntent().getExtras().getString(FOLDER);
		
		StringBuilder sb = new StringBuilder();
		ConfigImpl cfg = new ConfigImpl();
		enableExtraViews(appendMode || (cfg.getValue(sb, "TOTAL_ORG_EDIT") && sb.toString().equals("1")) || Util.isToday(orgImpl.getData().created)); 
		fillSpinner(new OrgType(), spOrgType);

		OrgEx oe = (OrgEx) orgImpl.getData();
		edCheif.setText(oe.cheif);
		edCheifPhone.setText(oe.cheifPhone);
		edContact.setText(oe.contact);
		edContactPhone.setText(oe.contactPhone);
		edAvgTraffic.setText(Integer.toString(oe.avgTraff));
		edEmail.setText(oe.email);

		setSpinnerSelect(spOrgType, oe.orgType);

		ctvLicense.setChecked(oe.license > 0);
		ctvLicense.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((CheckedTextView) v).setChecked(!((CheckedTextView) v)
						.isChecked());
			}
		});

		btnDealer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(ADD_DEALER_DLG);
			}
		});

		updateDealers();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case ADD_DEALER_DLG:
			return createAddDealerDlg();
		default:
			return super.onCreateDialog(id);
		}
	}

	private Dialog createAddDealerDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.add_delear_title);
		final DealerAdapter adapter = new DealerAdapter();
		builder.setAdapter(adapter, null);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						List<DealerAdapter.Data> data = adapter.getSelected();
						OrgEx oe = (OrgEx) orgImpl.getData();
						oe.dealers.clear();

						for (Data d : data)
							if (d.selected) {
								OrgDealerItem item = new OrgDealerItem();
								item.id = d.kv.key.toString();
								oe.dealers.add(item);
							}

						updateDealers();
					}
				});
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}

	class DealerAdapter extends BaseAdapter {
		class Data {
			KeyValue kv;
			boolean selected;

			public Data(KeyValue kv) {
				this.kv = kv;
			}
		}

		private List<KeyValue> dealers;
		private List<Data> data;

		public DealerAdapter() {
			dealers = getDataList(new Dealer());
			data = new ArrayList<Data>();
			OrgEx oe = (OrgEx) orgImpl.getData();

			for (KeyValue kv : dealers) {
				Data item = new Data(kv);
				for (OrgDealerItem d : oe.dealers)
					if (d.id.equals(kv.key.toString())) {
						item.selected = true;
						break;
					}

				data.add(item);
			}
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
				view = View.inflate(CreateOrg.this, R.layout.select_dealer_row,
						null);

			Data d = (Data) getItem(position);
			CheckedTextView ctvItem = ((CheckedTextView) view
					.findViewById(R.id.ctvItem));
			ctvItem.setText(d.kv.value);
			ctvItem.setChecked(d.selected);
			ctvItem.setTag(d);
			ctvItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CheckedTextView ctv = (CheckedTextView) v;
					ctv.setChecked(!ctv.isChecked());
					((Data) ctv.getTag()).selected = ctv.isChecked();
				}
			});

			return view;
		}

		List<Data> getSelected() {
			ArrayList<Data> result = new ArrayList<CreateOrg.DealerAdapter.Data>();

			for (Data d : data)
				if (d.selected)
					result.add(d);

			return result;
		}

	}

	private void setSpinnerSelect(Spinner spinner, String id) {
		if (id != null && id.length() > 0) {
			Adapter adapter = spinner.getAdapter();

			for (int i = 0; i < adapter.getCount(); i++) {
				KeyValue kv = (KeyValue) adapter.getItem(i);
				if (kv != null && kv.key != null && kv.key.equals(id)) {
					spinner.setSelection(i, true);
					break;
				}
			}
		}
	}

	@Override
	protected int getContentViewId() {
		return R.layout.potenzial_orgex;
	}

	private void fillSpinner(DataObject data, Spinner spinner) {
		ArrayList<KeyValue> valueList = getDataList(data);

		ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(this,
				R.layout.simple_spinner_layout, valueList);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		spinner.setAdapter(aa);
	}

	protected ArrayList<KeyValue> getDataList(DataObject data) {
		ArrayList<KeyValue> valueList = new ArrayList<KeyValue>();
		DbReader read = new DbReader();
		boolean bdo = read.select(data, DataObjectInfo.getInstance()
				.getTableName(data.getClass()), null);

		while (bdo) {
			try {
				Class<?> type = data.getClass();

				final String ID = "id";
				final String NAME = "name";

				KeyValue kv = new KeyValue(
						(String) type.getField(ID).get(data), (String) type
								.getField(NAME).get(data));

				valueList.add(kv);
				bdo = read.selectNext(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		read.close();

		Collections.sort(valueList, new Comparator<KeyValue>() {
			@SuppressLint("DefaultLocale")
			@Override
			public int compare(KeyValue lhs, KeyValue rhs) {
				return lhs.value.toString().toLowerCase()
						.compareTo(rhs.value.toString().toLowerCase());
			}
		});
		return valueList;
	}

	@Override
	protected OKListener createOKListener() {
		return new OKListener() {
			
			@Override
			protected String genOrgId() {
				return  UUID.randomUUID().toString().replace("-", "");
			}
			
			@Override
			protected void postOnClick(Org org) {
				OrgEx oe = (OrgEx) org;
				oe.cheif = edCheif.getText().toString().trim();
				oe.cheifPhone = edCheifPhone.getText().toString().trim();
				oe.contact = edContact.getText().toString().trim();
				oe.contactPhone = edContactPhone.getText().toString().trim();
				KeyValue kv = (KeyValue) spOrgType.getSelectedItem();				
				oe.orgType = kv == null || kv.key == null ? "" : kv.key.toString().trim();
				oe.license = ctvLicense.isChecked() ? 1 : 0;
				oe.parent = folder;
				try {
					oe.avgTraff = Integer.parseInt(edAvgTraffic.getText()
							.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				oe.email = edEmail.getText().toString().trim();
			}
		};
	}
	
	private void enableExtraViews(boolean val){
		for(View view : new View[]{
				edName, edAddress,
				spOrgType, edCheif, edCheifPhone, edContact, edContactPhone, ctvLicense, btnDealer, 
				tvDealers, edAvgTraffic, edEmail, btnGetLocation, btnOk})
			view.setEnabled(val);
	}
}

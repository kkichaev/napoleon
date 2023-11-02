package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.Action;
import com.grsoft.dataobjects.CommonAudit;
import com.grsoft.dataobjects.CommonAuditItem;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.CommonAuditImpl;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.CommonAuditDoc;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class CommonAuditEdit extends AuditActivityBase {
	private Adapter adapter;

	public static void open(Context context, CommonAuditImpl doc) {
		AuditActivityBase.open(context, doc, CommonAuditEdit.class);
	}

	@Override
	protected CreatableDocument<? extends CreateDocDataObject> createDocument() {
		return (CommonAuditImpl) CommonAuditDoc.instance().create();
	}

	@Override
	protected int getLayoutID() {
		return R.layout.common_audit;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CommonAuditItem item = (CommonAuditItem) parent
						.getItemAtPosition(position);
				Adapter adapter = (Adapter) parent.getAdapter();
				CommonAuditActionList.open(view.getContext(),
						document.getRowid(), item.id,
						adapter.getActions(position));
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (adapter != null)
			adapter.close();
		
		if(isFinishing()){
			CommonAudit ca = (CommonAudit) document.getData();
			if(ca.answer.size() == 0 && allItemsClear(ca))
				document.delete();
		}
	}

	private boolean allItemsClear(CommonAudit ca) {
		for(CommonAuditItem i : ca.items){
			if(i.presents == 1 || i.stock.length() > 0 ||
					i.merch.length() > 0 || i.price.length() > 0)
				return false;
		}
		
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		document.read();
		adapter = new Adapter(this, (CommonAuditImpl) document);
		list.setAdapter(adapter);
	}
}

class Adapter extends BaseAdapter {
	public List<CommonAuditItem> data;
	public Context context;
	private PriceImpl price = new PriceImpl();
	private OnClickListener selectPrice;
	private OnCheckedChangeListener setPresents;
	private OnClickListener selectInventory;

	private static final String POSITION = "position";
	private List<KeyValue> priceVal = new ArrayList<KeyValue>();
	private Map<String, String> priceMap = new HashMap<String, String>();
	private List<KeyValue> stockVal = new ArrayList<KeyValue>();
	private Map<String, String> invMap = new HashMap<String, String>();
	private CommonAuditImpl document;
	private Map<String, List<String>> questions = new HashMap<String, List<String>>();
	private Map<String, List<String>> actions = new HashMap<String, List<String>>();

	public Adapter(Context ctx, CommonAuditImpl document) {
		this.document = document;
		this.data = document.getData().items;
		this.context = ctx;

		selectPrice = new SelectDlg((FragmentActivity) ctx, SelectPrice.class);
		selectInventory = new SelectDlg((FragmentActivity) ctx, SelectQty.class);
		setPresents = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton view, boolean isChecked) {
				int pos = (Integer) view.getTag();
				CommonAuditItem item = (CommonAuditItem) getItem(pos);
				item.presents = isChecked ? 1 : 0;
				Adapter.this.document.write();
			}
		};

		ConfigImpl cfg = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		cfg.getValue(sb, "“ип÷енника");
		priceVal.add(new KeyValue("", ""));
		DialogHelper.makeListWithKey(sb.toString(), priceVal, null);
		priceMap.put("", "");
		for (KeyValue kv : priceVal)
			if (!priceMap.containsKey(kv.key))
				priceMap.put(kv.key.toString(), kv.value.toString());
		sb.setLength(0);
		cfg.getValue(sb, "“ип“оварного«апаса");
		stockVal.add(new KeyValue("", ""));
		DialogHelper.makeListWithKey(sb.toString(), stockVal, null);
		for (KeyValue kv : stockVal)
			if (!invMap.containsKey(kv.key))
				invMap.put(kv.key.toString(), kv.value.toString());

		Cursor c = null;
		long now = new Date().getTime();
		try {
			c = DataBaseManager.getDataBase().query(
					DataObjectInfo.getInstance().getTableName(Action.class),
					null,
					"org=? and begin<=? and end>=?",
					new String[] { document.getId(), Long.toString(now),
							Long.toString(now) }, null, null, null);

			while (c.moveToNext()) {
				String item = c.getString(c.getColumnIndex("item"));
				String id = c.getString(c.getColumnIndex("id"));
				String action = c.getString(c.getColumnIndex("action"));

				List<String> list = null;
				if (questions.containsKey(item))
					list = questions.get(item);
				else {
					list = new ArrayList<String>();
					questions.put(item, list);
				}

				if (!list.contains(action))
					list.add(action);

				if (actions.containsKey(item))
					list = actions.get(item);
				else {
					list = new ArrayList<String>();
					actions.put(item, list);
				}

				if (!list.contains(id))
					list.add(id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null)
				c.close();
		}
	}

	public void close() {
		price.close();
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
			view = View.inflate(context, R.layout.common_audit_row, null);

		boolean editable = document.isEditable();

		CommonAuditItem item = (CommonAuditItem) getItem(position);
		price.getData().id = item.id;
		price.read();

		Price p = price.getData();
		TextView tv = (TextView) view.findViewById(R.id.tvName);
		tv.setText(p.name);
		tv.setTextColor(Util.GrServerColorToSystem(p.color));

		CheckBox cb = (CheckBox) view.findViewById(R.id.cbPresent);
		cb.setOnCheckedChangeListener(null);
		cb.setChecked(item.presents != 0);
		cb.setTag(position);
		cb.setOnCheckedChangeListener(setPresents);
		cb.setEnabled(editable);

		Button btn = (Button) view.findViewById(R.id.btnCost);
		btn.setText(priceMap.get(item.price));
		btn.setTag(position);
		btn.setOnClickListener(selectPrice);
		btn.setEnabled(editable);

		btn = (Button) view.findViewById(R.id.btnQty);
		btn.setText(invMap.get(item.stock));
		btn.setTag(position);
		btn.setOnClickListener(selectInventory);
		btn.setEnabled(editable);

		View v = view.findViewById(R.id.ivAction);
		v.setVisibility(actions.containsKey(item.id) ? View.VISIBLE
				: View.INVISIBLE);

		if (questions.containsKey(item.id)) {
			List<String> q = questions.get(item.id);

			List<String> qid = new ArrayList<String>(q);

//			for (Answer a : document.getData().answer)
//				qid.remove(a.question);

			if (qid.size() == 0)
				view.setBackgroundResource(R.drawable.list_green_selector);
			else
				setBackground(view, position);

		} else
			setBackground(view, position);

		return view;
	}

	private void setBackground(View v, int pos) {
		v.setBackgroundResource(pos % 2 != 0 ? R.drawable.even_row_selector
				: R.drawable.list_selector);
	}

	private class SelectDlg implements OnClickListener {
		FragmentActivity activity;
		Class<? extends DialogFragment> dialog;

		public SelectDlg(FragmentActivity activity,
				Class<? extends DialogFragment> dialog) {
			this.activity = activity;
			this.dialog = dialog;
		}

		@Override
		public void onClick(View v) {
			int pos = (Integer) v.getTag();
			try {
				DialogFragment dlg = createDialog(dialog);
				Bundle bundle = new Bundle();
				bundle.putInt(POSITION, pos);
				dlg.setArguments(bundle);
				dlg.show(activity.getSupportFragmentManager(),
						SelectPrice.class.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private DialogFragment createDialog(Class<? extends DialogFragment> type) {
		if (type == SelectPrice.class)
			return new SelectPrice();
		else if (type == SelectQty.class)
			return new SelectQty();
		else
			return null;
	}

	private class SelectPrice extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			String[] items = new String[priceVal.size()];

			for (int i = 0; i < items.length; i++)
				items[i] = (String) priceVal.get(i).value;

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.select_price).setItems(items,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							int pos = getArguments().getInt(POSITION);
							CommonAuditItem item = (CommonAuditItem) getItem(pos);
							item.price = priceVal.get(which).key.toString();
							document.write();
							notifyDataSetChanged();
						}
					});
			return builder.create();
		}
	}

	private class SelectQty extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			String[] items = new String[stockVal.size()];

			for (int i = 0; i < items.length; i++)
				items[i] = (String) stockVal.get(i).value;

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.select_inv).setItems(items,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							int pos = getArguments().getInt(POSITION);
							CommonAuditItem item = (CommonAuditItem) getItem(pos);
							item.stock = stockVal.get(which).key.toString();
							document.write();
							notifyDataSetChanged();
						}
					});
			return builder.create();
		}
	}

	public List<String> getActions(int pos) {
		CommonAuditItem item = (CommonAuditItem) getItem(pos);
		if (actions.containsKey(item.id))
			return actions.get(item.id);

		return null;
	}
}

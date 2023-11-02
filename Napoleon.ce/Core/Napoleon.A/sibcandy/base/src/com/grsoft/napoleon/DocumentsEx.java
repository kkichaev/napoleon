package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.FindTextWatcher;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;
import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.grsoft.napoleon.R;

public class DocumentsEx extends Documents {
	ImageView btnFind;
	HashMap<String, List<Delivery>> dlvItems = new HashMap<String, List<Delivery>>();
	private PriceImpl price = new PriceImpl();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnFind = (ImageButton) findViewById(R.id.btnFind);
		btnFind.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(R.id.find_item_dlg);
			}
		});
	}

	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);

		if (docType == ReturnDoc.instance()) {
			btnFind.setVisibility(View.VISIBLE);
			dlvItems.clear();

			DocumentsAdapter dlvAdapter = new DocumentsAdapter(this,
					DebtDoc.instance(), org.getData().id, null);
			for (int i = 0; i < dlvAdapter.getCount(); i++) {
				Document<?> d = (Document<?>) dlvAdapter.getItem(i);

				if (d instanceof DeliveryImpl) {
					Delivery dlv = (Delivery) (((DeliveryImpl) d).getData())
							.clone();
					for (DeliveryItem item : dlv.items) {
						List<Delivery> list = null;
						if (dlvItems.containsKey(item.id))
							list = (List<Delivery>) dlvItems.get(item.id);
						else {
							list = new ArrayList<Delivery>();
							dlvItems.put(item.id, list);
						}
						list.add(dlv);
					}
				}
			}

		} else
			btnFind.setVisibility(View.GONE);

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.find_item_dlg)
			return createFindItemDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog createFindItemDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.find_item, null);
		final EditText edFind = (EditText) view.findViewById(R.id.edFind);
		final ListView list = (ListView) view.findViewById(R.id.list);
		list.setAdapter(new SearchAdapter());
		edFind.addTextChangedListener(new FindTextWatcher(edFind, list));
		builder.setView(view);
		builder.setTitle(R.string.search);
		ImageButton btnDel = (ImageButton) view.findViewById(R.id.btnDel);
		btnDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				edFind.setText("");
				((FilterAdapter) list.getAdapter()).resetFilter();
			}
		});
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ReturnImpl ret = new ReturnImplEx();
				DbWriter.checkDBTable(Return.class);
				SearchInfo info = (SearchInfo) parent
						.getItemAtPosition(position);

				List<Long> rowids = DbReader
						.readIds(
								DataObjectInfo.getInstance().getTableName(
										Return.class),
								new StringBuilder("id = '").append(org.getData().id)
										.append("' and dlvNum='")
										.append(info.number).append("' and ")
										.append("(([params] & " + ParamState.ofExported + " ) == 0)")
										.toString(), null);

				boolean bdo = false;
				
				if(rowids.size() > 0)
					bdo = ret.read(rowids.get(0));
				else
					bdo = ret.initSilent(org.getData().id,
							GPSUtilNew.getLastKnownLocation());
				
				if(bdo){
					price.getData().id = info.id;
					price.read();

					ReturnEx retEx = (ReturnEx) ret.getData();
					retEx.dlvDate = info.data;
					retEx.dlvNum = info.number;
					ret.write();
					ret.close();

					ret.editItem(price.getRowid(), view.getContext());
					dismissDialog(R.id.find_item_dlg);
				}
			}
		});

		return builder.create();
	}

	@Override
	protected int getContentViewID() {
		return R.layout.documentsex;
	}

	@Override
	protected void onPause() {
		super.onPause();
		price.close();
	}

	class SearchInfo {
		String id = "";
		String number = "";
		Date data;

		public SearchInfo(String id, String number, Date data) {
			this.id = id;
			this.number = number;
			this.data = data;
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == R.id.find_item_dlg)
			prepareFindItemDlg(dialog);
		else
			super.onPrepareDialog(id, dialog);
	}

	private void prepareFindItemDlg(Dialog dialog) {
		EditText ed = (EditText) dialog.findViewById(R.id.edFind);
		ed.setText("");
		ListView list = (ListView) dialog.findViewById(R.id.list);
		((FilterAdapter) list.getAdapter()).resetFilter();
	}

	class SearchAdapter extends BaseAdapter implements FilterAdapter {
		ArrayList<SearchInfo> data = new ArrayList<SearchInfo>();

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
				view = View.inflate(DocumentsEx.this, R.layout.find_item_row,
						null);
			SearchInfo si = (SearchInfo) getItem(position);

			price.getData().id = si.id;
			price.read();

			TextView tv = (TextView) view.findViewById(R.id.tvItem);
			tv.setText(price.getData().name);
			tv = (TextView) view.findViewById(R.id.tvDlv);
			tv.setText(si.number);
			tv = (TextView) view.findViewById(R.id.tvDate);
			tv.setText(Util.simpleDateFormat.format(si.data));
			return view;
		}

		@Override
		public void applyFilter(String value) {
			if (value.trim().length() == 0)
				resetFilter();
			else {
				refresh(value);
				notifyDataSetChanged();
			}
		}

		@Override
		public void resetFilter() {
			data.clear();
			notifyDataSetChanged();
		}

		private void refresh(String filter) {
			SQLiteDatabase db = DataBaseManager.getDataBase();
			Cursor c = null;
			final String ID_CLMN = "id";
			data.clear();

			try {
				c = db.query(
						DataObjectInfo.getInstance().getTableName(Price.class),
						new String[] { ID_CLMN }, "srchName LIKE ?",
						new String[] { "%" + filter.toUpperCase() + "%" },
						null, null, null);

				if (c.moveToFirst())
					do {
						String id = c.getString(c.getColumnIndex(ID_CLMN));

						if (dlvItems.containsKey(id)) {
							for (Delivery dlv : dlvItems.get(id))
								data.add(new SearchInfo(id, dlv.number,
										dlv.date));
						}
					} while (c.moveToNext());

				Collections.sort(data, new Comparator<SearchInfo>() {
					PriceImpl price = new PriceImpl();

					@Override
					public int compare(SearchInfo lhs, SearchInfo rhs) {
						price.getData().id = lhs.id;
						price.read();
						price.close();
						String leftName = price.getData().name;

						price.getData().id = rhs.id;
						price.read();
						price.close();
						String rightName = price.getData().name;

						int result = leftName.compareTo(rightName);

						if (result == 0)
							result = lhs.number.compareTo(rhs.number);

						return result;
					}
				});

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (c != null)
					c.close();
			}
		}
	}
}

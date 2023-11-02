package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.AnswerImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.LayoutDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.Util;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DocumentsEx extends Documents {
	private static final int FIND_ITEM_DLG = R.id.find_item_dlg;

	ImageView btnFind;
	HashMap<String, List<Delivery>> dlvItems = new HashMap<String, List<Delivery>>();
	private PriceImpl price = new PriceImpl();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnFind = (ImageButton) findViewById(R.id.btnFind);
		btnFind.setVisibility(View.GONE);
	}

	@Override
	protected void doCreate() {
		if (neddSync()){
			UpdateDB.open(this);
			return;
		}

		String where = "inwork = 1 and params = 0";

		for (com.grsoft.napoleon.documents.DocList docs : new com.grsoft.napoleon.documents.DocList[]{
				VisitDoc.instance().docList(null, null, where),
				LayoutDoc.instance().docList(null, null, where)})
			if (docs.getCount() > 0) {
				docs.get(0).open(this);
				return;
			}

		super.doCreate();

		View v = findViewById(R.id.btnSendDocList);
		if( v != null ) {
			v.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					List<DocExportListener> sends = DocType.getDocuments(true, true);
					new DocumentSender(DocumentsEx.this, findViewById(R.id.btnSendDocList), sends, null).execute((Void[]) null);
				}
			});
		}
	}

	private boolean neddSync() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		Long lastSync = sp.getLong(UpdateDBEx.SYNCDATE, -1);
		Date now = Util.resetTime(new Date());

		return lastSync != now.getTime();
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

	@Override protected void onlyVisitInit() {}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case FIND_ITEM_DLG:
			prepareFindItemDlg(dialog);
		default:
			super.onPrepareDialog(id, dialog);
		}
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

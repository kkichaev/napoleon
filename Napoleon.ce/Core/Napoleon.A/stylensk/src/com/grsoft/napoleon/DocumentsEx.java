package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.Util;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
	private List<DocTypeBase> filter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnFind = (ImageButton) findViewById(R.id.btnFind);
		btnFind.setVisibility(View.GONE);
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

	@Override 	protected void onlyVisitInit() {
/*
		DocType cd = DocType.getCurDoc(); 
		if( cd != VisitDoc.instance() && cd != QuestionDoc.instance() ) {
			Napoleon.prevDocType = (DocType) DocType.getCurDoc();
			DocType.setCurDoc(VisitDoc.instance());
		}
		btnDocFilter.setOnClickListener(new DocFilterOnClickListener(this){{
			filter = new ArrayList<DocTypeBase>();
			filter.add(VisitDoc.instance());
			filter.add(QuestionDoc.instance());
		}});
//		if (!ScriptDefImpl.canScripting()) {
//			filter = ((NapoleonApp) getApplication()).potenzialOrgDocFilter;
//
//			if (filter != null && filter.size() > 0
//					&& !filter.contains(DocType.getCurDoc()))
//				DocType.setCurDoc(filter.get(0));
//
//			btnDocFilter.setOnClickListener(new DocFilterOnClickListener(this, false, false, filter));
//		}
*/
	}

	
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

		@SuppressLint("DefaultLocale")
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

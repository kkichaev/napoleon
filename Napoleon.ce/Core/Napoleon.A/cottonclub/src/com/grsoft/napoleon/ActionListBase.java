package com.grsoft.napoleon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.Action;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.impl.Answerable;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.ExtrasConst;

public abstract class ActionListBase extends Activity {
	public static final String ITEM_ID = "id";
	protected CreatableDocument<? extends CreateDocDataObject> document;
	protected String itemid = "";
	protected ListView list;
	private TextView tvPrice;
	protected List<String> actions;
	private static final String ACTIONS = "actions";

	public static void open(Context context,
			Class<? extends Activity> activity, long rowid, String itemid,
			List<String> actions) {
		Intent intent = new Intent(context, activity);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		intent.putExtra(ITEM_ID, itemid);
		intent.putExtra(ACTIONS, (Serializable) actions);

		context.startActivity(intent);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());

		list = (ListView) findViewById(R.id.list);
		tvPrice = (TextView) findViewById(R.id.tvPrice);

		Intent intent = getIntent();
		actions = (List<String>) intent.getSerializableExtra(ACTIONS);
		document = createDocument();
		document.read(intent.getLongExtra(ExtrasConst.DOC_ROW_ID_STR,
				ExtrasConst.INVALID_ID));
		itemid = intent.getStringExtra(ITEM_ID);

		list.setAdapter(createAdapter());
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Action action = (Action) parent.getItemAtPosition(position);
				ActionAnswerEdit.open(view.getContext(), document.getRowid(),
						itemid, action.id);
			}
		});

		PriceImpl price = new PriceImpl();
		price.getData().id = itemid;
		price.read();
		price.close();

		tvPrice.setText(price.getData().name);
	}

	public abstract int getLayoutId();

	public BaseAdapter createAdapter() {
		return new ActionAdapter(this, actions);
	};

	protected abstract CreatableDocument<? extends CreateDocDataObject> createDocument();

	@Override
	protected void onResume() {
		super.onResume();

		BaseAdapter adapter = (BaseAdapter) list.getAdapter();

		if (adapter != null)
			adapter.notifyDataSetChanged();
	}

	class ActionAdapter extends BaseAdapter {
		List<Action> data = new ArrayList<Action>();
		Context context;

		public ActionAdapter(Context context, List<String> actions) {
			this.context = context;

			if (actions != null) {
				StringBuilder ids = new StringBuilder();
				for (String id : actions) {
					if (ids.length() > 0)
						ids.append(",");
					ids.append(id);
				}

				Cursor c = null;

				try {
					c = DataBaseManager.getDataBase().query(true,
							DataObjectInfo.getInstance().getTableName(
									Action.class), new String[]{"id","name","action"}, "id in (?)",
							new String[] { ids.toString() }, null, null, null, null);

					while (c.moveToNext()) {
						Action a = new Action();
						a.id = c.getString(c.getColumnIndex("id"));
						a.name = c.getString(c.getColumnIndex("name"));
						a.action = c.getString(c.getColumnIndex("action"));
						data.add(a);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (c != null)
						c.close();
				}
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
				view = View.inflate(context, R.layout.actionlistrow, null);

			Action action = (Action) getItem(position);
			TextView tv = (TextView) view.findViewById(R.id.tvName);
			tv.setText(action.name);

			if (((Answerable<?>) document).findAnswer(itemid, action.action) != null)
				view.setBackgroundResource(R.drawable.list_green_selector);
			else
				setBackgroud(position, view);

			return view;
		}

		public void setBackgroud(int position, View view) {
			view.setBackgroundResource(position % 2 != 0 ? R.drawable.even_row_selector
					: R.drawable.list_selector);
		}
	}
}

package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.BitmapUtils;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	protected static final int SELECT_RING_SIZE_DLG = R.id.select_ring_size_dlg;
	protected static final String PRICE_ROWID = "price_rowid";
	private TextView tvAvgw;
	private TextView tvCostgr;
	private LinearLayout llPic;
	private CheckBox cbComplete;

	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tvAvgw = (TextView) findViewById(R.id.tvAvgw);
		tvCostgr = (TextView) findViewById(R.id.tvCostGr);
		llPic = (LinearLayout) findViewById(R.id.llPic);
		cbComplete = (CheckBox) findViewById(R.id.cbComplete);

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

//		int screenWidth = getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ? displaymetrics.heightPixels
//				: displaymetrics.widthPixels;
		int screenWidth = displaymetrics.widthPixels;
		final int SPACE = 20;
		final int COL_COUNT = getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ? 4 : 8 ;
		int picSize = (screenWidth - SPACE) / COL_COUNT;

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				picSize, picSize);
		layoutParams.setMargins(0, 0, 0, 2);
		ivPresent.setLayoutParams(layoutParams);
		ivPresent.setPadding(0, 0, 20, 0);

		final PriceEx p = (PriceEx) price.getData();
		tvAvgw.setText(Util.IntToScaleStr(p.avgw, Consts.WEIGHT_SCALE));
		tvCostgr.setText(Util.IntToScaleStr(p.costgr, Consts.SUM_SCALE));

		DbWriter.checkDBTable(Present.class);

		if (p.art.length() >= 4) {
			try {
				SQLiteDatabase db = DataBaseManager.getDataBase();
				Cursor c = db
						.rawQuery(
								"select id, photoPath from presentation where "
										+ "id in (select id from price where art like ?) "
										+ "and id <> ?",
								new String[] { p.art.substring(0, 4) + "%",
										p.id });

				if (c.moveToFirst()) {
					boolean ringPresent = ((PriceEx) price.getData()).isRing();
					PriceImpl pi = new PriceImpl();

					do {
						try {
							String id = c.getString(c.getColumnIndex("id"));
							pi.getData().id = id;
							pi.read();

							if (((PriceEx) pi.getData()).isRing())
								if (ringPresent)
									continue;
								else
									ringPresent = true;

							TextView tv = new TextView(PriceCountEx.this);
							LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
									LinearLayout.LayoutParams.WRAP_CONTENT,
									LinearLayout.LayoutParams.WRAP_CONTENT);
							lp.setMargins(SPACE / 2, 0, SPACE / 2, 0);
							lp.gravity = Gravity.CENTER_VERTICAL;
							tv.setLayoutParams(lp);
							tv.setTag(id);
							tv.setText(((PriceEx) pi.getData()).art);
							tv.setCompoundDrawablesWithIntrinsicBounds(null,
									BitmapUtils.createBitmap(c.getString(c
											.getColumnIndex("photoPath")),
											picSize), null, null);
							tv.setGravity(Gravity.CENTER_HORIZONTAL);
							tv.setTextColor(getResources().getColor(
									R.color.black));
							tv.setOnClickListener(itemClick);
							llPic.addView(tv);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} while (c.moveToNext());

					pi.close();
				}

				c.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		cbComplete.setVisibility(p.isRing() ? View.VISIBLE : View.GONE);
	}

	protected void postOKProcess() {
		Toast.makeText(this, getString(R.string.item_has_been_buied, price.getData().name), 
				Toast.LENGTH_SHORT).show();
	};
	
	OnClickListener itemClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			PriceImpl p = new PriceImpl();
			p.getData().id = (String) v.getTag();
			p.read();
			p.close();

			if (p.getRowid() != ExtrasConst.INVALID_ID) {
				if (((PriceEx) p.getData()).isRing()) {
					Bundle bundle = new Bundle();
					bundle.putLong(PRICE_ROWID, p.getRowid());
					showDialog(SELECT_RING_SIZE_DLG, bundle);
				} else {
					PriceCount.open(v.getContext(), p.getRowid(), document);
					finish();
				}
			}
		}
	};

	protected boolean updateQty(boolean inPack, int qty) {
		boolean result = super.updateQty(inPack, qty);

		if (cbComplete.isChecked()) {
			final PriceEx p = (PriceEx) price.getData();

			SQLiteDatabase db = DataBaseManager.getDataBase();
			String mask = p.art.substring(0, 4) + "_"
					+ p.art.substring(5, p.art.length());
			Cursor c = db.rawQuery("select id from price where art like ?"
					+ "and id <> ?", new String[] { mask, p.id });
			if (c.moveToFirst()) {
				PriceImpl pi = new PriceImpl();
				do {
					pi.getData().id = c.getString(c.getColumnIndex("id"));
					pi.read();
					pi.close();
					
					if(!((PriceEx)pi.getData()).isRing())
						((Itemsable) document).updateQty(pi, qty,
								getInputCost(pi.getData()), inPack);
				} while (c.moveToNext());
			}
			c.close();
		}

		return result;
	};

	@Override
	protected Dialog onCreateDialog(int id, Bundle bundle) {
		switch (id) {
		case SELECT_RING_SIZE_DLG:
			return createSelectRingDlg();
		default:
			return super.onCreateDialog(id, bundle);
		}
	}

	class RingSelectAdapter extends BaseAdapter {
		List<Price> data = new ArrayList<Price>();

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int pos) {
			return data.get(pos);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if (view == null)
				view = View.inflate(PriceCountEx.this,
						R.layout.simple_spinner_layout, null);

			PriceEx p = (PriceEx) getItem(pos);
			((TextView) view.findViewById(R.id.tvFirmaName)).setText(p.name);
			return view;
		}

		public void update(long rowid) {
			data.clear();
			PriceImpl price = new PriceImpl();

			if (price.read(rowid)
					&& ((PriceEx) price.getData()).art.length() > 5) {
				DbReader reader = new DbReader();
				PriceEx obj = new PriceEx();
				String where = "art LIKE '"
						+ ((PriceEx) price.getData()).art.substring(0,
								PriceEx.RING_FLAG_POS) + "%'";
				boolean bdo = reader.select(obj, price.getTableName(), where);
				if (bdo) {
					do {
						if (((PriceEx) obj).isRing())
							data.add((Price) obj.clone());
						bdo = reader.selectNext(obj);
					} while (bdo);
				}
			}

			price.close();
			notifyDataSetChanged();
		}
	}

	private Dialog createSelectRingDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_ring_size);
		ListView list = new ListView(this);
		list.setId(R.id.list);
		list.setAdapter(new RingSelectAdapter());
		list.setBackgroundColor(getResources().getColor(R.color.white));
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View arg1,
					int pos, long arg3) {
				PriceEx p = (PriceEx) adapterView.getAdapter().getItem(pos);
				PriceImpl impl = new PriceImpl();
				impl.getData().id = p.id;
				boolean res = impl.read();
				impl.close();

				if (res) {
					PriceCount.open(adapterView.getContext(), impl.getRowid(), document);
					finish();
				}
			}
		});
		builder.setView(list);

		return builder.create();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
		switch (id) {
		case SELECT_RING_SIZE_DLG:
			prepareSelectRingDlg(dialog, bundle);
			break;
		default:
			super.onPrepareDialog(id, dialog, bundle);
		}
	}

	private void prepareSelectRingDlg(Dialog dialog, Bundle bundle) {
		ListView list = (ListView) ((AlertDialog) dialog)
				.findViewById(R.id.list);
		RingSelectAdapter adapter = (RingSelectAdapter) list.getAdapter();
		long rowid = bundle.getLong(PRICE_ROWID);
		adapter.update(rowid);
	}
}

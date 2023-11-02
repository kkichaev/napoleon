package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class RemnantsDetailEx extends RemnantsDetail {
	private static final String SHOW_TOTAL = "show_total";
	private ToggleButton btnShowTotal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		unregisterForContextMenu(lvRemnantItems);
		btnShowTotal = (ToggleButton) findViewById(R.id.btnShowTotal);
		btnShowTotal.setChecked(getPreferences(Context.MODE_PRIVATE).getBoolean(SHOW_TOTAL,
					true));
		btnShowTotal.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences pref =  getPreferences(Context.MODE_PRIVATE);
				Editor ed = pref.edit();
				ed.putBoolean(SHOW_TOTAL, isChecked);
				ed.commit();
				notifyDataSetChanged();
			}
		});
	}

	@Override
	protected int getLayoutId() {
		return R.layout.remnantsdetailex;
	}

	protected RemnantItemsAdapter createAdapter() {
		return new RemnantItemsAdapterEx();
	};

	@Override
	protected ItemsOnClickListener createItemsOnClickHandler() {
		return new ItemsOnClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				RemnantItem item = (RemnantItem) arg1.getTag();
				((RemnantsImplEx) remnantsImpl).editItem((RemnantItemEx) item,
						RemnantsDetailEx.this);
			}
		};
	}

	class RemnantItemsAdapterEx extends RemnantItemsAdapter {
		class RemnantItemResult extends RemnantItemEx {
			public RemnantItemResult(int qty, int weight) {
				this.qty = qty;
				this.weight = weight;
			}
		}

		private ArrayList<RemnantItem> data = new ArrayList<RemnantItem>();

		public RemnantItemsAdapterEx() {
			fillData();
		}

		@Override
		public void notifyDataSetChanged() {
			fillData();
			super.notifyDataSetChanged();
		}

		protected void fillData() {
			data.clear();
			final PriceImpl p1 = new PriceImpl();
			final PriceImpl p2 = new PriceImpl();
			ArrayList<RemnantItem> items = new ArrayList<RemnantItem>();
			items.addAll(remnantsImpl.getData().items);
			Collections.sort(items, new Comparator<RemnantItem>() {

				@Override
				public int compare(RemnantItem lhs, RemnantItem rhs) {
					if (lhs.id.compareTo(rhs.id) == 0)
						return 0;
					else {
						p1.getData().id = lhs.id;
						p1.read();
						p1.close();
						p2.getData().id = rhs.id;
						p2.read();
						p2.close();

						return (p1.getData().name.compareTo(p2.getData().name));
					}
				}
			});

			boolean showTotal = getPreferences(Context.MODE_PRIVATE).getBoolean(SHOW_TOTAL,
					true);

			int qty = 0;
			int weight = 0;
			String id = "";
			for (RemnantItem i : items) {
				RemnantItemEx iex = (RemnantItemEx) i;

				if (!id.equals(i.id) && id != "" && showTotal) {
					data.add(new RemnantItemResult(qty, weight));
					id = iex.id;
					qty = 0;
					weight = 0;
				}

				if (id == "")
					id = iex.id;

				data.add(i);
				qty += iex.qty;
				weight += iex.weight;
			}

			if (items.size() > 0 && showTotal)
				data.add(new RemnantItemResult(qty, weight));
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int arg0) {
			return data.get(arg0);
		}

		@Override
		protected View setView(View view, PriceImpl priceImpl, int qty,
				Object tag) {
			View result = super.setView(view, priceImpl, qty, tag);
			RemnantItemEx item = (RemnantItemEx) tag;
			TextView tvFace = (TextView) result.findViewById(R.id.tvFace);
			tvFace.setText(Util.IntToScaleStr(item.weight, Consts.QTY_SCALE));

			for (int i = 0; i < ((LinearLayout) result).getChildCount(); i++) {
				View v = ((LinearLayout) result).getChildAt(i);

				if (v instanceof TextView) {
					TextView tv = (TextView) v;

					if (item instanceof RemnantItemResult) {
						tv.setTextColor(getResources().getColor(R.color.red));
						if (tv.getId() == R.id.tvName)
							tv.setText(getString(R.string.total));
					} else
						tv.setTextColor(getResources().getColor(R.color.black));
				}
			}

			return result;
		}

		@Override
		protected int getViewId() {
			return R.layout.remnantsdetail_list_rowex;
		}
	}
}

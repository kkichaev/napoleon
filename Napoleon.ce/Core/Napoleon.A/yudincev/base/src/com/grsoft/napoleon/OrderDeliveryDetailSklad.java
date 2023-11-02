package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.DeliverySklad;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.SkladsImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class OrderDeliveryDetailSklad extends BaseActivity {
	public static Class<? extends Activity> activity = OrderDeliveryDetailSklad.class;
	public DeliveryImpl delivery = new DeliveryImpl();
	private TextView tvOrg;
	private ExpandableListView list;
	private OrgImpl org;
	private ImageButton btnLines;
	private LinesCountController linesController;

	public static void open(Context context, long rowid) {
		Intent intent = new Intent(context, activity);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orderdeliverydetailsklad);

		list = (ExpandableListView) findViewById(R.id.list);
		tvOrg = (TextView) findViewById(R.id.tvOrg);
		btnLines = (ImageButton) findViewById(R.id.btnLines);

		delivery.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR,
				ExtrasConst.INVALID_ID));
		delivery.close();

		list.setAdapter(new Adapter(this, delivery));

		org = new OrgImpl();
		org.getData().id = delivery.getId();
		org.read();
		org.close();

		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(
				list, btnLines, this, true);
		linesController = linesOnClickListener.getController();

		tvOrg.setText(Html.fromHtml(getOrgText(org.getData())));
		refreshTotalSum();
	}

	private String getOrgText(Org o) {
		return o.name;
	}

	protected void refreshTotalSum() {
		OrgSumImpl oi = new OrgSumImpl();
		OrgSum os = oi.getData();
		os.id = org.getData().id;
		os.type = DocType.getCurDoc().getName();
		oi.read();
		oi.close();
		updateTotalSum(os.sum, 0);
	}

	@SuppressLint("UseSparseArrays")
	class Adapter extends BaseExpandableListAdapter {
		class Data {
			public String name = "";
			public String num = "";
			public int sum;
			public int flags;
			public String remark = "";
			public ArrayList<DeliveryItemEx> items = new ArrayList<DeliveryItemEx>();

			public void add(DeliveryItemEx item) {
				items.add(item);
				sum += item.sum;
			}
		}

		private ArrayList<Data> data = new ArrayList<OrderDeliveryDetailSklad.Adapter.Data>();
		private Context context;
		private PriceImpl price = new PriceImpl();

		public Adapter(Context context, DeliveryImpl delivery) {
			this.context = context;
			HashMap<Integer, Data> map = new HashMap<Integer, Data>();
			SkladsImpl sklad = new SkladsImpl();

			if (delivery != null && delivery.getData() != null
					&& delivery.getData().items != null)
				for (DeliveryItem i : delivery.getData().items) {
					DeliveryItemEx item = (DeliveryItemEx) i;
					Data d = map.get(item.skladid);

					if (d == null) {
						d = new Data();
						sklad.getData().id = item.skladid;
						sklad.read();
						sklad.close();

						d.name = sklad.getData().name;

						for (DeliverySklad ds : ((DeliveryEx) delivery
								.getData()).sklads)
							if (ds.skladid == item.skladid) {
								d.remark = ds.remark;
								d.flags = ds.flags;
								d.num = ds.numdlvsk;
								d.remark = ds.remark;
							}

						map.put(item.skladid, d);
					}

					d.add(item);
				}

			data.addAll(map.values());
		}

		@Override
		public int getGroupCount() {
			return data.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return data.get(groupPosition).items.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return data.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return data.get(groupPosition).items.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View view, ViewGroup parent) {

			if (view == null)
				view = View.inflate(context, R.layout.skladrow, null);

			Data d = (Data) getGroup(groupPosition);

			TextView tv = (TextView) view.findViewById(R.id.tvName);
			tv.setText(d.name);

			tv = (TextView) view.findViewById(R.id.tvNum);
			tv.setText(d.num);

			tv = (TextView) view.findViewById(R.id.tvSum);
			tv.setText(Util.IntToScaleStr(d.sum, Consts.SUM_SCALE));

			tv = (TextView) view.findViewById(R.id.tvRemark);
			tv.setText(d.remark);

			CheckBox cb = (CheckBox) view.findViewById(R.id.cbPayoff);
			cb.setChecked((d.flags & 1) == 1);
			cb.setClickable(false);

			cb = (CheckBox) view.findViewById(R.id.cbSellout);
			cb.setChecked((d.flags & 2) == 2);
			cb.setClickable(false);

			cb = (CheckBox) view.findViewById(R.id.cbPending);
			cb.setChecked((d.flags & 4) == 4);
			cb.setClickable(false);

			cb = (CheckBox) view.findViewById(R.id.cbShipped);
			cb.setChecked((d.flags & 8) == 8);
			cb.setClickable(false);

			return view;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View view, ViewGroup parent) {

			if (view == null)
				view = View.inflate(context, R.layout.orderdetail_list_row,
						null);

			DeliveryItemEx d = (DeliveryItemEx) getChild(groupPosition,
					childPosition);

			price.getData().id = d.id;
			price.read();
			price.close();

			TextView tv = (TextView) view.findViewById(R.id.tvName);
			tv.setText(price.getData().name);
			linesController.prepareTextView(tv);

			tv = (TextView) view.findViewById(R.id.tvQty);
			tv.setText(Util.IntToScaleStr(d.qty, Consts.QTY_SCALE));

			tv = (TextView) view.findViewById(R.id.tvSum);
			tv.setText(Util.IntToScaleStr(d.sum, Consts.SUM_SCALE));

			return view;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}
}

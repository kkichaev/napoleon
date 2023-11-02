package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PricePhotoServ;
import com.grsoft.dataobjects.PriceQty;
import com.grsoft.dataobjects.PriceQtyItem;
import com.grsoft.dataobjects.impl.ColorsImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.PriceQtyImpl;
import com.grsoft.dataobjects.impl.SizesImpl;
import com.grsoft.napoleon.ColorAdapter.ColorAdapterItem;
import com.grsoft.napoleon.ColorAdapter.ItemVal;
import com.grsoft.napoleon.QtyController.OnChangeQty;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.BitmapUtils;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FPOperation;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class OrderItemEdit extends Activity {
	private ListView list;
	private QtyController controller;
	private TextView tvName;
	private TextView tvOrdCost;
	private ImageView ivPresent;
	private TextView tvDescr;
	private Button btnOK;
	private Button btnMinus;
	private Button btnPlus;
	protected PriceImpl price = new PriceImpl();
//	protected CreatableDocument<?> document = null;
	private CostStrategy costStrategy;
	private TextView tvCost;
	private OrderImplEx order = new OrderImplEx();
	private ColorAdapter adapter;
	private int cost;
	private ListView servers;
	
	public static void open(Context context, long itemRowid,
			DbObject<Order> dbObject) {
		Intent i = new Intent(context, OrderItemEdit.class);
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, itemRowid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, dbObject.getRowid());
		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orderitemedit);
		
		list = (ListView) findViewById(R.id.list);
		tvName = (TextView) findViewById(R.id.tvName);
		tvOrdCost = (TextView) findViewById(R.id.tvOrdCost);
		ivPresent = (ImageView) findViewById(R.id.ivPresent);
		btnOK = (Button) findViewById(R.id.btnOK);
		tvCost = (TextView) findViewById(R.id.tvCost);
		btnMinus = (Button) findViewById(R.id.btnMinus);
		btnPlus = (Button) findViewById(R.id.btnPlus);
		tvDescr = (TextView) findViewById(R.id.tvDescr);
		servers = (ListView) findViewById(R.id.servers);
		
		price.read(getIntent().getLongExtra(ExtrasConst.PRICE_ROW_ID_STR,
				ExtrasConst.INVALID_ID));
		price.close();

		order.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR,
				ExtrasConst.INVALID_ID));
		order.close();

		boolean exported = order.isExported();
		btnOK.setEnabled(!exported);
		btnMinus.setEnabled(!exported);
		btnPlus.setEnabled(!exported);

		adapter = new ColorAdapter(this, price.getData().id, order);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos,
					long arg3) {
				findViewById(R.id.tvHint).setVisibility(View.GONE);
				findViewById(R.id.tlQty).setVisibility(View.VISIBLE);

				ColorAdapterItem item = (ColorAdapterItem) adapter
						.getItemAtPosition(pos);
				controller.update(pos, item);
				((ColorAdapter) adapter.getAdapter()).setSelection(pos);
			}
		});

		controller = new QtyController(this, R.id.btnPlus, R.id.btnMinus,
				adapter, !exported);

		controller.setChangeQtyListener(new OnChangeQty() {

			@Override
			public void onChangeQty(ColorAdapterItem item) {
				int cnt = 0;
				for (ItemVal val : item.items)
					cnt += val.val;

				long val = FPOperation.itemMul(costStrategy.getItemCost(price.getData(), order), cnt, Consts.QTY_SCALE);

				updateOrderView(val);
			}
		});

		tvName.setText(price.getData().name);

		costStrategy = CostStrategy.getInstance((Class<? extends Document<?>>) (order.getClass()));

		cost = costStrategy.getItemCost(price.getData(), order);
		String value = Util.IntToScaleStr(cost, Consts.SUM_SCALE,
				Util.DEC_DELIM, false);

		updateOrderCostText(value, tvCost);

		updateOrderView(0);

		ivPresent.setVisibility(View.INVISIBLE);
		Cursor cursor = null;
		try {
			final String CLMN_NAME = "photoPath";
			DbWriter.checkDBTable(DbObject.getDataType(Present.class));
			cursor = DataBaseManager.getDataBase().query(
					DataObjectInfo.getInstance().getTableName(Present.class),
					new String[] { CLMN_NAME }, "id=?",
					new String[] { price.getData().id }, null, null, null);

			if (cursor.moveToFirst()) {
				ivPresent.setVisibility(View.VISIBLE);
				String path = cursor.getString(cursor
						.getColumnIndex(CLMN_NAME));
				ivPresent.setTag(path);
				ivPresent.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_VIEW);
						intent.setDataAndType(Uri.parse("file://" + v.getTag().toString()), "image/*");
						startActivity(intent);
						
//						PricePresentation.open(v.getContext(), path,
//								price.getRowid());
					}
				});

				final int PICSZ = 120;
				Resources r = getResources();
				float px = TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, PICSZ,
						r.getDisplayMetrics());
				ivPresent.setImageDrawable(BitmapUtils.createBitmap(path,
						(int) px));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}

		btnOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PriceQtyImpl pqiImpl = new PriceQtyImpl();
				pqiImpl.getData().id = price.getData().id;
				pqiImpl.read();
				pqiImpl.close();

				for (int i = 0; i < adapter.getCount(); i++) {
					ColorAdapterItem item = (ColorAdapterItem) adapter
							.getItem(i);
					for (ItemVal val : item.items) {
						order.updateQty(price, val.val, cost, item.colorid,
								val.sizeid);
					}
				}

				finish();
			}
		});
		
		String descr = ((PriceEx)price.getData()).descr;
		
		if(descr.length() == 0)
			descr = getString(R.string.item_descr);
		
		tvDescr.setText(descr);
		servers.setAdapter(new ServersAdapter(this, price.getData().id));
		servers.setDividerHeight(0);
		servers.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
				PricePhotoServ i = (PricePhotoServ) adapter.getItemAtPosition(position);
				String url = i.url;
				Intent in = new Intent(Intent.ACTION_VIEW);
				in.setData(Uri.parse(url));
				startActivity(in);
				
			}});
	}

	protected void updateOrderCostText(String value, TextView tvCost) {
		StringBuilder sb = new StringBuilder();
		sb.append(getString(R.string.cost1)).append(" ").append(value);
		tvCost.setText(sb.toString());
	}

	private void updateOrderView(long val) {
		String value = Util.IntToScaleStr(val, Consts.SUM_SCALE,
				Util.DEC_DELIM, false);
		StringBuilder sb = new StringBuilder();
		sb.append(getString(R.string.ordercost)).append(" ").append(value);
		tvOrdCost.setText(sb.toString());
		adapter.notifyDataSetChanged();
	}
}

class ServersAdapter extends BaseAdapter{
	private Context context;
	private List<PricePhotoServ> data = new ArrayList<PricePhotoServ>();
	
	public ServersAdapter(Context context, String id) {
		this.context = context;
		
		DataTraveler.travel(PricePhotoServ.class, new DataTraveler.Travel<PricePhotoServ>(true) {

			@Override
			public boolean travel(DataTraveler<PricePhotoServ> item) {
				data.add(item.data);
				return true;
			}}, "id='"+id+"'");
	}
	
	@Override
	public int getCount() { return data.size();	}

	@Override public Object getItem(int position) { return data.get(position); }

	@Override public long getItemId(int position) { return 0; }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null)
			convertView = new TextView(context);
		
		TextView tv = ((TextView)convertView); 
		PricePhotoServ i = (PricePhotoServ) getItem(position);
		tv.setText(i.name);
		tv.setTextColor(context.getResources().getColor(R.color.black));
		float p = context.getResources().getDimension(R.dimen.pt);
		tv.setPadding(0, (int)p, 0, (int)p);
		
		tv.setBackgroundResource(position % 2 != 0 ? 
				R.drawable.even_row_selector :
				R.drawable.list_selector);		
		
		return convertView;
	}
}

class ColorAdapter extends android.widget.BaseAdapter {
	private final int NOT_USED = -1;
	private int selIdx = NOT_USED;

	public static class ColorAdapterItem {
		public String caption;
		public int colorid;
		public ArrayList<ItemVal> items = new ArrayList<ColorAdapter.ItemVal>();
	}

	public static class ItemVal {
		public int sizeid;
		public int qty;
		public int val;
		public String title;
	}

	private ArrayList<ColorAdapterItem> data = new ArrayList<ColorAdapterItem>();
	private Context context;

	public ColorAdapter(Context context, String id, OrderImplEx order) {
		this.context = context;

		PriceQtyImpl pqiImpl = new PriceQtyImpl();
		pqiImpl.getData().id = id;
		pqiImpl.read();
		pqiImpl.close();

		ColorsImpl colors = new ColorsImpl();
		SizesImpl sizes = new SizesImpl();

		PriceQty pq = pqiImpl.getData();

		SparseArray<ColorAdapterItem> hash = new SparseArray<ColorAdapter.ColorAdapterItem>();

		for (int i = 0; i < pq.items.size(); i++) {
			PriceQtyItem pqi = pq.items.get(i);

			ColorAdapterItem item = hash.get(pqi.colorid);

			if (item == null) {
				item = new ColorAdapterItem();
				hash.put(pqi.colorid, item);

				colors.getData().id = pqi.colorid;
				colors.read();

				item.caption = colors.getData().name;
				item.colorid = pqi.colorid;
			}

			sizes.getData().id = pqi.sizeid;
			sizes.read();

			ItemVal val = new ItemVal();
			val.sizeid = pqi.sizeid;
			val.title = sizes.getData().name;
			val.qty = pqi.qty;

			OrderItemEx oie = order.findUpdateItem(id, pqi.colorid, pqi.sizeid);

			if (oie != null)
				val.val = oie.qty;

			item.items.add(val);
		}

		for (int i = 0; i < hash.size(); i++) {
			ColorAdapterItem value = hash.valueAt(i);
			Collections.sort(value.items, new Comparator<ItemVal>() {
				@Override
				public int compare(ItemVal lhs, ItemVal rhs) {
					return lhs.title.compareTo(rhs.title);
				}
			});
			data.add(value);
		}
		
		colors.close();
		sizes.close();

		Collections.sort(data, new Comparator<ColorAdapterItem>() {
			@Override
			public int compare(ColorAdapterItem lhs, ColorAdapterItem rhs) {
				return lhs.caption.compareTo(rhs.caption);
			}
		});
	}


	public void setSelection(int pos) {
		selIdx = pos;
		notifyDataSetChanged();
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
			view = View.inflate(context, R.layout.simple_spinner_layout, null);

		ColorAdapterItem item = (ColorAdapterItem) getItem(position);
		
		int count = 0;
		
		for (ItemVal val : item.items) 
				count += val.val;
				
		TextView tv = (TextView) view.findViewById(R.id.tvFirmaName);
		tv.setText(String.format("%s (%s)", item.caption, Util.IntToScaleStr(count, Consts.QTY_SCALE)));
		
		tv.setTypeface(null, selIdx == position ? Typeface.BOLD_ITALIC
				: Typeface.NORMAL);

		view.setBackgroundResource(position % 2 != 0 ? R.drawable.even_row_selector
				: R.drawable.list_selector);

		return view;
	}
}

class QtyController implements android.view.View.OnClickListener {
	public interface OnChangeQty {
		void onChangeQty(ColorAdapterItem item);
	}

	static final int SIZE = 10;
	NumberBox[] qty = new NumberBox[SIZE];
	TextBox[] title = new TextBox[SIZE];
	NumberBox[] input = new NumberBox[SIZE];
	final static int NOT_USED = -1;
	int curSelected = NOT_USED;
	ColorAdapter adapter;
	int adapterPos = NOT_USED;
	Context context;
	OnChangeQty changeQty;

	public QtyController(Activity layout, int idIncVal, int idDecVal,
			ColorAdapter data, boolean editable) {
		this.adapter = data;
		this.context = layout;
		Button btnPlus = (Button) layout.findViewById(idIncVal);
		Button btnMinus = (Button) layout.findViewById(R.id.btnMinus);
		TableRow trQty = (TableRow) layout.findViewById(R.id.trQty);
		TableRow trTitle = (TableRow) layout.findViewById(R.id.trTitle);
		TableRow trInput = (TableRow) layout.findViewById(R.id.trInput);

		if (trQty.getChildCount() < SIZE && trTitle.getChildCount() < SIZE
				&& trInput.getChildCount() < SIZE)
			throw new RuntimeException("Invalid child count in controller");

		for (int i = 0; i < SIZE; i++) {
			qty[i] = (NumberBox) trQty.getChildAt(i);
			qty[i].setTag(i);
			title[i] = (TextBox) trTitle.getChildAt(i);
			title[i].setTag(i);
			input[i] = (NumberBox) trInput.getChildAt(i);
			input[i].setTag(i);

			if (editable) {
				qty[i].setOnClickListener(this);
				title[i].setOnClickListener(this);
				input[i].setOnClickListener(this);
			}

			qty[i].setNotUsed(true);
			title[i].resetText();
			input[i].setNotUsed(true);
		}

		btnPlus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateVal(false);
			}
		});

		btnMinus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateVal(true);
			}
		});
	}

	public void setChangeQtyListener(OnChangeQty changeQty) {
		this.changeQty = changeQty;
	}

	private void fireChangeQty(ColorAdapterItem item) {
		if (changeQty != null)
			changeQty.onChangeQty(item);
	}

	private void updateVal(boolean dec) {
		if (adapterPos == NOT_USED)
			return;

		ColorAdapterItem item = (ColorAdapterItem) adapter.getItem(adapterPos);

		for (int i = 0; i < item.items.size(); i++) {
			if (dec) {
				if (item.items.get(i).val > 0) {
					item.items.get(i).val -= Consts.QTY_SCALE;
					item.items.get(i).qty += Consts.QTY_SCALE;
				}
			} else {
				if (item.items.get(i).qty > 0) {
					item.items.get(i).val += Consts.QTY_SCALE;
					item.items.get(i).qty -= Consts.QTY_SCALE;
				}
			}
		}

		update(adapterPos, item);
		fireChangeQty(item);
	}

	public void update(int pos, ColorAdapterItem item) {
		adapterPos = pos;
		curSelected = NOT_USED;

		for (int i = 0; i < SIZE; i++) {
			this.qty[i].setSelected(false);
			this.input[i].setSelected(false);

			if (i >= item.items.size()) {
				this.qty[i].setNotUsed(true);
				this.qty[i].setVal(0);
				this.input[i].setNotUsed(true);
				this.input[i].setVal(0);
				this.title[i].resetText();
				continue;
			}

			ItemVal val = item.items.get(i);
			this.qty[i].setNotUsed(false);
			this.input[i].setNotUsed(false);
			this.qty[i].setVal(val.qty);
			this.title[i].setText(val.title);
			this.input[i].setVal(val.val);
			this.qty[i].setLimit(val.qty == 0);
			this.input[i].setLimit(val.qty == 0);
		}
	}

	@Override
	public void onClick(View view) {
		int idx = (Integer) view.getTag();

		for (int i = 0; i < SIZE; i++) {
			if (i != idx) {
				qty[i].setSelected(false);
				input[i].setSelected(false);
			}
		}

		qty[idx].setSelected(true);
		input[idx].setSelected(true);
		curSelected = idx;

		InputNumberDlg.open(context, new InputNumber() {

			@Override
			public void applayInput(int value, Object... params) {
				ColorAdapterItem item = (ColorAdapterItem) adapter
						.getItem(adapterPos);

				item.items.get(curSelected).qty += item.items.get(curSelected).val;

				if (value > item.items.get(curSelected).qty) {
					item.items.get(curSelected).val = item.items
							.get(curSelected).qty;
					item.items.get(curSelected).qty = 0;

					Toast.makeText(context,
							context.getString(R.string.qty_has_been_reduced),
							Toast.LENGTH_SHORT).show();
				} else {
					item.items.get(curSelected).val = value;
					item.items.get(curSelected).qty -= value;
				}

				update(adapterPos, item);
				fireChangeQty(item);
			}

			@Override
			public int getValue() {
				ColorAdapterItem item = (ColorAdapterItem) adapter
						.getItem(adapterPos);
				return item.items.get(curSelected).val;
			}

			@Override
			public boolean useComma() {
				return false;
			}
		});
	}
}

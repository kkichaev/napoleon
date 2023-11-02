package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.grsoft.dataobjects.MoveItem;
import com.grsoft.dataobjects.impl.MoveImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.MoveDoc;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.view.RegDurationActivity;

public class MoveDetail extends RegDurationActivity implements DataSetNotify {
	protected MoveImpl moveImpl;
	private ListView lvMoveItems;
	private OptionsMenuHelper optionsMenuHelper = new OptionsMenuHelper();
	private ImageButton btnAdd;
	protected ImageButton btnSend;
	protected LinesCountController linesController;
	protected ImageButton btnLines;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		moveImpl = (MoveImpl) MoveDoc.instance().create();
		setContentView(R.layout.movedetail);

		long rowid;
		if (savedInstanceState == null)
			rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR,
					ExtrasConst.INVALID_ID);
		else
			rowid = savedInstanceState.getLong(ExtrasConst.DOC_ROW_ID_STR);

		if (rowid == ExtrasConst.INVALID_ID)
			return;

		moveImpl.read(rowid);
		lvMoveItems = (ListView) findViewById(R.id.lvRemnantItems);
		lvMoveItems.setAdapter(createAdapter());
		lvMoveItems.setOnItemClickListener(createItemsOnClickHandler());
		registerForContextMenu(lvMoveItems);

		OrgImpl orgIml = new OrgImpl();
		orgIml.getData().id = moveImpl.getData().id;

		if (orgIml.read()) {
			TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
			tvOrg.setText(orgIml.getData().name);
			orgIml.close();
		}

		btnAdd = (ImageButton) findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addItem();
			}
		});

		btnSend = (ImageButton) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				send();
			}
		});
		btnLines = (ImageButton) findViewById(R.id.btnLines);
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(
				lvMoveItems, btnLines, this);
		linesController = linesOnClickListener.getController();
		
		findViewById(R.id.btnEditOrder).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MoveProperties.open(v.getContext(), moveImpl.getRowid(), true);
			}
		});
	}

	protected ItemsOnClickListener createItemsOnClickHandler() {
		return new ItemsOnClickListener();
	}

	protected MoveItemsAdapter createAdapter() {
		return new MoveItemsAdapter();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (!moveImpl.isExported())
			getMenuInflater().inflate(R.menu.order_detail_context_menu, menu);
	}

	@Override
	protected void onResume() {
		super.onResume();
		moveImpl.read(moveImpl.getRowid(), false);
		notifyDataSetChanged();
		updateTotalSum();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		MoveItem moveItem = (MoveItem) ((AdapterContextMenuInfo) item
				.getMenuInfo()).targetView.getTag();
		PriceImpl pi = new PriceImpl();
		pi.getData().id = moveItem.id;
		pi.read();
		pi.close();

		if (item.getItemId() == R.id.itDelete) {
			moveImpl.updateQty(pi, 0, 0, false);
		} else if (item.getItemId() == R.id.itEdit) {
			moveImpl.editItem(pi.getRowid(), this);
		}

		notifyDataSetChanged();
		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		optionsMenuHelper.onCreateOptionsMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		optionsMenuHelper.onOptionsItemSelect(item);
		return super.onOptionsItemSelected(item);
	}

	public static void open(Context context, MoveImpl moveImpl) {
		Intent i = new Intent(context, MoveDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, moveImpl.getRowid());
		context.startActivity(i);
	}

	@Override
	protected void onStop() {
		moveImpl.close();
		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, moveImpl.getRowid());
		super.onSaveInstanceState(outState);
	}

	class ItemsOnClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if (!moveImpl.isExported()) {
				MoveItem item = (MoveItem) arg1.getTag();
				PriceImpl priceImpl = new PriceImpl();
				priceImpl.getData().id = item.id;

				if (priceImpl.read())
					moveImpl.editItem(priceImpl.getRowid(),
							MoveDetail.this);
				priceImpl.close();
			}
		}

	}

	class MoveItemsAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			return moveImpl.getData().items.size();
		}

		@Override
		public Object getItem(int arg0) {
			return moveImpl.getData().items.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			MoveItem moveItem = (MoveItem) getItem(arg0);
			PriceImpl priceImpl = new PriceImpl();
			priceImpl.getData().id = moveItem.id;
			priceImpl.read();
			priceImpl.close();
			
			int cost = (int) CostStrategy.getInstance(
					(Class<? extends Document<?>>) moveImpl.getClass())
					.getItemCost(priceImpl.getData(), (Document<?>) moveImpl);
			
			int sum = cost * moveItem.qty / Consts.QTY_SCALE;;
			
			return setView(arg1, priceImpl, moveItem.qty, moveItem, sum);
		}

		protected View setView(View view, PriceImpl priceImpl, int qty,
				Object tag, int sum) {
			if (view == null)
				view = View.inflate(MoveDetail.this,
						R.layout.orderdetail_list_row, null);

			TextView tvName = (TextView) view.findViewById(R.id.tvName);
			linesController.prepareTextView(tvName);
			tvName.setText(priceImpl.getData().name);
			TextView tvQty = (TextView) view.findViewById(R.id.tvQty);
			tvQty.setText(Util.IntToScaleStr(qty, Consts.QTY_SCALE));

			TextView tvSum = (TextView)view.findViewById(R.id.tvSum);	
			tvSum.setText(Util.IntToScaleWStr(sum, Consts.SUM_SCALE, Consts.PRICE_DEC_WIDTH, false));
			tvSum.setGravity(Gravity.RIGHT);
			
			
			view.setTag(tag);
			return view;
		}
	}

	@Override
	public void notifyDataSetChanged() {
		BaseAdapter adapter = (BaseAdapter) lvMoveItems.getAdapter();

		if (adapter != null)
			adapter.notifyDataSetChanged();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// remove empty remnants
			if (moveImpl.getData().items.size() == 0)
				moveImpl.delete();
		}

		return super.onKeyDown(keyCode, event);
	}

	class OptionsMenuHelper {
		public static final int MNU_ADD_ITEM_ID = 0;
		public static final int MNU_SEND_ID = 1;

		public void onCreateOptionsMenu(Menu menu) {
			menu.add(Menu.NONE, MNU_ADD_ITEM_ID, Menu.NONE, R.string.add);
			menu.add(Menu.NONE, MNU_SEND_ID, Menu.NONE, R.string.send);
		}

		public void onOptionsItemSelect(MenuItem item) {
			switch (item.getItemId()) {
			case MNU_ADD_ITEM_ID:
				selectForAddItem();
				break;
			case MNU_SEND_ID:
				selectFormSend();
				break;
			}
		}

		private void selectFormSend() {
			send();
		}

		private void selectForAddItem() {
			addItem();
		}
	}

	protected void send() {
		new DocumentSender(MoveDetail.this, null, MoveDoc.OBJ_NAME,
				moveImpl, moveImpl.getRowid()).execute((Void[]) null);
	}

	protected void addItem() {
		DocType.setCurDoc(MoveDoc.instance());
		Warehouse.open(MoveDetail.this, moveImpl, true);
	}

	protected void updateTotalSum(){
		updateTotalSum(moveImpl.sum(), moveImpl.weight(), moveImpl.count());
	}
	
	
}

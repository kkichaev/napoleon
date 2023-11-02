package com.grsoft.napoleon;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.OrderProceededEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PresentImpl;
import com.grsoft.napoleon.util.QtyEditor;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.util.ReceiveRemnants;
import com.grsoft.util.Consts;

public class WarehouseEx extends WarehouseNew {
	private static final int WAIT_DLG = R.id.wait_dlg;

	// boolean longClick = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		findViewById(R.id.btnGoUp).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				adapter.upLevel();
			}
		});

		// lvItemSelect.setOnItemLongClickListener(new OnItemLongClickListener()
		// {
		//
		// @Override
		// public boolean onItemLongClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// longClick = true;
		// adapter.onClick(position);
		// return true;
		// }
		// });

		btnSync = (ImageButton) findViewById(R.id.btnSync);
		btnSync.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateRemnants();
			}
		});

		findViewById(R.id.btnFilter).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateForZeroFilter();
			}
		});

		((NapoleonApp) getApplication()).initPresentation();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.warehouseex;
	}

	@Override
	protected int getOptionsMenuId() {
		return R.menu.warehouse_opt_menuex;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itRecieveRemains) {
			updateRemnants();
			return true;
		} else
			return super.onOptionsItemSelected(item);
	}

	ReceiveRemnants remnantsReceiver = null;

	void updateRemnants() {
		if (remnantsReceiver == null) {
			remnantsReceiver = new ReceiveRemnants(this,
					new ReceiveRemnants.TaskDoneHandler() {
						@Override
						public void finish(NetworkAsyncTask task) {
							if (remnantsReceiver == task)
								remnantsReceiver = null;
							btnSync.setEnabled(true);
							adapter.notifyDataSetChanged();
							dismissDialog(WAIT_DLG);
						}

						@Override
						public void start() {
							btnSync.setEnabled(false);
							showDialog(WAIT_DLG);
						}
					});
			remnantsReceiver.execute((Void[]) null);
		}
	}

	protected android.app.Dialog onCreateDialog(int id) {
		switch (id) {
		case WAIT_DLG:
			return new ProgressDialog(this);
		default:
			return super.onCreateDialog(id);
		}
	};

	// @Override
	// public void editItem(long rowid) {
	// if (document instanceof OrderImpl && !longClick) {
	// price.read(rowid);
	// price.close();
	// final OrderImpl cdoc = (OrderImpl)document;
	//
	// if (!cdoc.isExported()
	// || cdoc.isProceeded()
	// && !((cdoc.getData().params & OrderProceededEx.APPROVED) ==
	// OrderProceededEx.APPROVED)) {
	// final String priceid = price.getData().id;
	// final OrderItem orderItem = (OrderItem) cdoc.findItem(priceid);
	//
	// InputNumberDlg.open(WarehouseEx.this, new InputNumber() {
	//
	// @Override
	// public int getValue() {
	// return orderItem == null ? 1000 : orderItem.qty;
	// }
	//
	// @Override
	// public boolean isInpack() {
	// return orderItem == null ?
	// ((CfgNpl)ConfigManager.getConfig()).isPackView :
	// orderItem.inPack();
	// }
	//
	// @Override
	// public boolean isPackCanChange() {
	// boolean result = true;
	// PriceImpl priceImpl = new PriceImpl();
	// priceImpl.getData().id = priceid;
	//
	// if (priceImpl.read()) {
	// result = ((PriceEx) price.getData()).pack == 0;
	// }
	//
	// priceImpl.close();
	//
	// return result;
	// }
	//
	// @Override
	// public void applayInput(final int value,
	// Object... params) {
	// PriceImpl priceImpl = new PriceImpl();
	// priceImpl.getData().id = priceid;
	//
	// if (priceImpl.read()) {
	// @SuppressWarnings("unchecked")
	// CostStrategy cs = CostStrategy.getInstance((Class<? extends Document<?>>)
	// document.getClass());
	// int cost = cs.getItemCost(priceImpl.getData(), document);
	// if (cdoc.updateQty(priceImpl, value,
	// cost, (Boolean) params[0]))
	// adapter.notifyDataSetChanged();
	// }
	//
	// priceImpl.close();
	//
	// cdoc.setExported(false);
	// cdoc.unsetProceeded();
	// cdoc.write();
	// cdoc.close();
	// }
	// }, Consts.QTY_SCALE, true,
	// getString(R.string.input_new_qty), true);
	// }
	// }else{
	// longClick = false;
	// super.editItem(rowid);
	// }
	// }

	OnClickListener opencard = new OnClickListener() {
		@Override
		public void onClick(View v) {
			editItem((Long) v.getTag());
		}
	};

	OnClickListener openfolder = new OnClickListener() {
		@Override
		public void onClick(View v) {
			adapter.setFolder((Integer) v.getTag());
		}
	};

	OnClickListener openqty = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (document instanceof OrderImpl) {
				final OrderImpl cdoc = (OrderImpl) document;
				if (!cdoc.isExported()
						|| cdoc.isProceeded()
						&& !((cdoc.getData().params & OrderProceededEx.APPROVED) == OrderProceededEx.APPROVED)) {
					final String priceid = (String) v.getTag();
					InputNumberDlg.open(WarehouseEx.this, new QtyEditor(cdoc,
							priceid, adapter), Consts.QTY_SCALE, true,
							getString(R.string.input_new_qty), true);
				}
			}
		}
	};

	private ImageButton btnSync;

	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View result = super.getPriceView(node, convertView);

		TextView tv = (TextView) result.findViewById(R.id.tvPriceItemName);
		tv.setTag(node.getRowid());
		tv.setOnClickListener(opencard);

		price.read(node.getRowid());
		Price p = price.getData();

		tv = (TextView) result.findViewById(R.id.tvClmn1);
		if (tv != null) {
			tv.setTag(p.id);
			tv.setOnClickListener(openqty);
		}

		tv = (TextView) result.findViewById(R.id.tvClmn2);
		if (tv != null) {
			tv.setTag(p.id);
			tv.setOnClickListener(openqty);
		}

		return result;
	}

	@Override
	public View getFolderView(FolderTreeNode node, View convertView) {
		View result = super.getFolderView(node, convertView);
		result.setOnClickListener(openfolder);
		result.setTag(node.id);
		return result;
	}

	@Override
	protected boolean hasPresentation() {
		return PresentImpl.count() > 0;
	}
}

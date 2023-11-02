package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import com.grsoft.dataobjects.Bonus;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.ZeroPositionFilter;

import java.util.ArrayList;
import java.util.List;

public class WarehouseEx extends Warehouse {
	static boolean MakeBonus = false;
    boolean updateBackground = false;
	static public String BONUS_FILTER_NAME = "Bonus";

    boolean initing = true;

	@Override protected int getLayoutId() { return R.layout.warehouseex; }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MakeBonus = false;

		super.onCreate(savedInstanceState);

		View v = findViewById(R.id.btnBonus);
		v.setVisibility((document instanceof OrderImplEx) ? View.VISIBLE : View.GONE);

		v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MakeBonus = !MakeBonus;

				FoldersAdapter.resetCache();
				((ImageButton)v).setImageResource((MakeBonus) ? R.drawable.bonus_on : R.drawable.bonus_off);
				FoldersAdapter ret = (FoldersAdapter) WarehouseEx.super.createListAdapter();
				if(MakeBonus) {
					// добавляем фильтр в текущий адаптер, т.к. в applyAdapter есть copyfilter
					adapter.putFilter(new BonusFilter());
				} else {
					adapter.deleteFilter(BONUS_FILTER_NAME);
				}
				((OrderImplEx)document).setBonusMode(MakeBonus);

				applyAdapter(ret, adapter.isExpanded(), false);
			}
		});
	}

	@Override
	public boolean useInterlaceBackground() {
		if(updateBackground) {
			updateBackground = false;
			return false;
		}
		return super.useInterlaceBackground();
	}

	@Override
    protected void updateChildPriceView(View view, Price p) {
        super.updateChildPriceView(view, p);
        if(((PriceEx)p).mml > 0) {
			updateBackground = true;
            view.setBackgroundResource(R.drawable.list_ltred_selector);
        }
    }

	@Override
	protected BaseAdapter createListAdapter() {
		boolean prevmb = MakeBonus;
		FoldersAdapter ret = (FoldersAdapter) super.createListAdapter();
		if(initing) {
			initing = false;

			if(document instanceof OrderImplEx && ((OrderImplEx)document).isBonusDoc()) {
				MakeBonus = true;
				ImageButton v = findViewById(R.id.btnBonus);
				v.setImageResource(R.drawable.bonus_on);
				v.setEnabled(false);
			}
		}
		if(MakeBonus) {
			ret.putFilter(new BonusFilter());
		}

		if(prevmb != MakeBonus) {
			FoldersAdapter.resetCache();
		}
		return ret;
	}

	@Override
	protected Filter createZeroPositionFilter() {
    	if(document instanceof OrderImplEx) {
			FoldersAdapter.resetCache();
			return new ZeroFilter();
		}

		return super.createZeroPositionFilter();
	}

	class ZeroFilter extends ZeroPositionFilter {
		@Override public String getWhereStr() { return ""; }

		@Override
		public boolean inset(long priceRowID, String id) {
			if( !(document instanceof Itemsable) )
				return super.inset(priceRowID, id);

			boolean result = false;

			if(price.read(priceRowID))
				result = (((Itemsable)document).getItemValue(price.getData()) > 0);
			return result;
		}
	}

//	class BonusMatrix extends MatrixBaseAdapter {
//		List<MatrixItem> items = new ArrayList<>();
//
//		public BonusMatrix(WarehouseNewW warehouse, String whid) {
//			super(warehouse);
//
//			for(Bonus b : ((OrderImplEx)document).getBonus().values()) {
//				MatrixItem mi = new MatrixItem();
//				mi.id = b.id;
//				mi.order = items.size();
//				items.add(mi);
//			}
//		}
//
//		@Override protected List<? extends MatrixItem> getMatrixItems() { return items; }
//	}
//
	class BonusFilter extends Filter {
		List<String> ids = new ArrayList<>();

		public BonusFilter() {
			super(BONUS_FILTER_NAME);

			for(Bonus b : ((OrderImplEx)document).getBonus().values())
				ids.add(b.id);
		}

		@Override
		public boolean inset(long priceRowID, String id) {
			return ids.contains(id);
		}
	}
}

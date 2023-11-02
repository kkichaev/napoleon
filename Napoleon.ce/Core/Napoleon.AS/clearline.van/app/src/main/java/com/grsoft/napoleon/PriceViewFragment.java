package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.QtyItem;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


public class PriceViewFragment extends Fragment {
	private PriceView parent;
	private Price price;
	private PriceImpl priceImpl = new PriceImpl();
	private int cost;
	private TextView tvCount;
	private TextView tvSum;
	private TextView tvQty;
	private TextView tvCost;
	private TextView tvQtyInPack;
	private TextView tvName;
	private String priceId;
	private View btnInc;
	private View btnDec;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.priceviewfragment, null, false); 
		tvCount = (TextView) view.findViewById(R.id.tvCount);
		tvSum = (TextView) view.findViewById(R.id.tvSum);
		tvQty = (TextView) view.findViewById(R.id.tvQty);
		tvCost = (TextView) view.findViewById(R.id.tvCost);
		tvQtyInPack = (TextView) view.findViewById(R.id.tvQtyInPack);
		tvName = (TextView) view.findViewById(R.id.tvName);
		
		btnInc = view.findViewById(R.id.btnInc);
		btnInc.setOnClickListener(incClick);
		btnDec = view.findViewById(R.id.btnDec);
		btnDec.setOnClickListener(decClick);
		
		Bundle b = getArguments();
		int pos = b.getInt(ExtrasConst.PRICE_ROW_ID_STR);
		PresentationData pd = PricePresentationFolder.list.get(pos);
		priceId = pd.id;
		
		try {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			Bitmap bitmap = BitmapFactory.decodeFile(pd.image, opt);

			ImageView iv = ((ImageView) view.findViewById(R.id.ivPresent));
			iv.setImageBitmap(bitmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return view;
	}

	private long calcPackCount(long qty, int inPack){
		return inPack == 0 ? 0 : qty * Consts.QTY_SCALE / inPack;
	}
	
	protected void updateWhQty() {
		long whQty = ((long)PriceView.document.getItemValue(price));
		long qty = calcPackCount(whQty, price.qtyInPack) / Consts.QTY_SCALE * Consts.QTY_SCALE;
		tvQty.setText(getResources().getString(R.string.rest_qty, Util.IntToScaleStr(qty, Consts.QTY_SCALE)));
	}

	protected void updateQty() {
		QtyItem i = (QtyItem) PriceView.document.findItem(price.id);
		long qty = i == null  ? 0 : calcPackCount(i.getQty(),  price.qtyInPack);
		tvCount.setText(Util.IntToScaleStr(qty, Consts.QTY_SCALE));
	}
	
	@Override
	public void onAttach(Context activity) {
		super.onAttach(activity);
		
		parent = (PriceView) activity;
	}
	
	private OnClickListener incClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			QtyItem i = (QtyItem) PriceView.document.findItem(price.id);
			int qty = i == null ? 0 : i.getQty();
			qty += price.qtyInPack;
			PriceView.document.updateQty(priceImpl, qty, cost, true);
			updateViews();
		}
	};
	
	private OnClickListener decClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			QtyItem i = (QtyItem) PriceView.document.findItem(price.id);
			int qty = i == null ? 0 : i.getQty();
			qty -= price.qtyInPack;
			PriceView.document.updateQty(priceImpl, qty, cost, true);
			updateViews();
		}
	};
	
	protected void updateViews() {
		priceImpl.read("id", priceId);
		price = priceImpl.getData();
		
		int qty = PriceView.document.getItemQty(price);
		
		btnDec.setEnabled(qty - price.qtyInPack >= 0);
		
		int rest = PriceView.document.getItemValue(price);
		
		btnInc.setEnabled(rest - price.qtyInPack >= 0);
		
		updateQty();
		updateSum();
		updateWhQty();
		parent.updateDocument();
	}
	
	private void updateSum(){
		QtyItem i = (QtyItem) PriceView.document.findItem(price.id);
        int count = i == null ? 0 : i.getQty();
		long val = (long)cost * count / Consts.QTY_SCALE;
		
		tvSum.setText(getResources().getString(R.string.sum_str, Util.IntToScaleStr(val, Consts.SUM_SCALE)));
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		refresh();
	}

	public void refresh() {
		if(parent != null){
			priceImpl.read("id", priceId);
			price = priceImpl.getData();
			
			CostStrategy costStrategy = CostStrategy.getInstance(PriceView.document.getClass());
			cost = (int)costStrategy.getItemCost(price, PriceView.document);
			tvCost.setText(getResources().getString(R.string.item_cost, Util.IntToScaleStr(cost, Consts.SUM_SCALE)));
			
			tvName.setText(price.name);
			tvQtyInPack.setText(getResources().getString(R.string.qty_str, Util.IntToScaleStr(price.qtyInPack, Consts.QTY_SCALE)));
			
			updateQty();
			updateWhQty();
			updateSum();
		}
	}
}

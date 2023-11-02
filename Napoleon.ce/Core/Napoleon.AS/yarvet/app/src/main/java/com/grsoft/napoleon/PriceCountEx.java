package com.grsoft.napoleon;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.FileProvider;

import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.IOrder;
import com.grsoft.dataobjects.IOrderItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class PriceCountEx extends PriceCount implements OnClickListener {
	TextView tvRen;
	OrgImpl org = new OrgImpl();

	Integer customCost = null;
	
	int minCost;
	int discount;
	int priceCost;
	private ArrayList<CharSequence> priceType = new ArrayList<CharSequence>();
	int sumType;
	private InfoReader info = new InfoReader();
	ImageView ivInfo;
	
	@Override
	protected int getContentViewId() { return R.layout.pricecount2ex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ivInfo = (ImageView) findViewById(R.id.ivInfo);
		
		updateCustomCost();
		if(customCost != 0) {
			findViewById(R.id.trDiscount).setVisibility(View.GONE);
		} else {
			TextView tv;
			tv = (TextView)findViewById(R.id.tvDiscount);
			tv.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) { 
					DiscountInputDlg.open(PriceCountEx.this, new InputNumber() {
						@Override public int getValue() { return -discount; }
						@Override
						public void applayInput(int value, Object... params) {
							int oldDiscount = discount;
							int oldPriceVal = priceVal;
							
							discount = -value;
							priceVal = priceCost - (int)(((long)priceCost * discount + Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
							
							if( checkCost() && (priceVal < minCost)) {
								Toast.makeText(PriceCountEx.this, 
										R.string.cost_below_min, Toast.LENGTH_SHORT).show();
								discount = oldDiscount;
								priceVal = oldPriceVal;
								return;
							}
							
							updateCost();
							updateDicsount();
							updateSumTextView();
						}});
				}
			});		}

		Spinner spPrices = findViewById(R.id.spPrices);
		spPrices.setVisibility(View.GONE);

		if(document != null && document instanceof OrderImplBase<?> && document.getRowid() != ExtrasConst.INVALID_ID ) {
			spPrices.setVisibility(View.VISIBLE);
			if(customCost == 0) {
				OrderImplBase<?> o = (OrderImplBase<?>)document;
				IOrderItem oe = (IOrderItem) o.findItem(price.getData().id);
				
				if( oe != null ) {
					discount = oe.getDisc();
					sumType = oe.getSumType();
					
					if( priceVal != oe.getCost() ) {
						priceVal = oe.getCost();
						updateCost();
						updateSumTextView();
					}
				}else{
					discount = ((IOrder)o.getData()).getDisc();
					sumType = ((Order)o.getData()).sumType;
				}
				
				ConfigImpl config = new ConfigImpl();
				DialogHelper.loadSpinnerFromConfig(config, "¬ид÷ены", priceType, spPrices, sumType);
				spPrices.setOnItemSelectedListener(new OnItemSelectedListener() {
	
					@Override
					public void onItemSelected(AdapterView<?> parent, View view,
							int position, long id) {
						sumType = position;
						Price p = price.getData();
						priceCost = (p.cost != null && p.cost.size() > sumType && sumType >= 0) ? 
								p.cost.get(sumType).cost : 0;
						priceVal = priceCost - (int)(((long)priceCost * discount + Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
						updateCost();
						updateSumTextView();
					}
	
					@Override
					public void onNothingSelected(AdapterView<?> parent) {			
					}
				});
				
				Price p = price.getData();
				priceCost = (p.cost != null && p.cost.size() > sumType && sumType >= 0) ? 
						p.cost.get(sumType).cost : 0;
						
				((OrderImplBase<?> )document).setUpdateQtyHandler(new OrderImplBase.UpdateQtyHandler() {
					@Override
					public void itemUpdated(OrderItem item, Order order, boolean isNew) {
						IOrderItem ie = (IOrderItem) item;
						ie.setDisc(discount);
						ie.setSumType(sumType);
					}
				});
			} else {
				onChangeCost(customCost);
				spPrices.setVisibility(View.GONE);
			}
		}
		
		updateDicsount();
		
		String file = info.getInfo(price.getData().id);
		
		if (file != null && file.length() > 0) {
			ivInfo.setVisibility(View.VISIBLE);
			ivInfo.setTag(file);
			ivInfo.setOnClickListener(this);
		}else
			ivInfo.setVisibility(View.GONE);

		tvRen = (TextView) findViewById(R.id.tvRen);

		if (document == null)
			document = new OrderImplEx();

		org.read("id", document.getId());

		if(document instanceof OrderImpl) {
			String selected = "";
			OrderImpl orderImpl = (OrderImpl)document;

			if (document.getRowid() != ExtrasConst.INVALID_ID )
				selected = ((OrderEx)document.getData()).whCode;

			ConfigImpl config = new ConfigImpl();

			ArrayList<KeyValue> values = new ArrayList<KeyValue>();
			Config c = config.getData();
			c.key = "—клады";
			config.read();

			int sel = DialogHelper.makeListWithKey(c.value, values, selected);

			ArrayList<KeyValueIndex> indexs = new ArrayList<KeyValueIndex>();
			int index  = 0;
			for(KeyValue kv:values) {
				if (sel != index)
					indexs.add(new KeyValueIndex(kv, index));

				index++;
			}

			StringBuilder sb = new StringBuilder();
			String fmtstr = "%s&nbsp;&nbsp;%s";
			String whCode = ((OrderEx)orderImpl.getData()).whCode;
			int whIndex = ((OrderEx)orderImpl.getData()).whIndex;

			for(KeyValueIndex v : indexs) {
				((OrderEx)orderImpl.getData()).whCode = v.key;
				((OrderEx)orderImpl.getData()).whIndex = v.index;

				int qty = orderImpl.getItemValue(price.getData());

				sb.append(String.format(fmtstr, v.value, Util.IntToScaleStr(qty, Consts.QTY_SCALE)));
				sb.append("<br>");
			}

			((OrderEx)orderImpl.getData()).whCode = whCode;
			((OrderEx)orderImpl.getData()).whIndex = whIndex;

			TextView tv = (TextView) findViewById(R.id.tvSklads);
			tv.setText(Html.fromHtml(sb.toString()));
		}
	}
	
	void updateCustomCost() {
		if( customCost == null ) {
			customCost = 0;
			if(Features.COST_MANAGER != null && document != null && document instanceof OrderImplBase<?> && document.getRowid() != ExtrasConst.INVALID_ID ) {
				int ci = Features.COST_MANAGER.getCostIndex(document.getId());
				if( ci >= 0) {
					customCost = Features.COST_MANAGER.getCost(price.getData().id, ci);
				}
			}
		}
	}
	
	private void updateDicsount() {
		int val = discount;
		String label = "скидка,%";
		if( val < 0 ) {
			label = "наценка,%";
			val = -val;
		}
		((TextView)findViewById(R.id.tvDiscountLabel)).setText(label);
		
		String value = Util.IntToScaleStr(val, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		TextView tv = (TextView)findViewById(R.id.tvDiscount);
		SpannableString ss = new SpannableString(value);
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setTextColor(Color.BLUE);
		tv.setText(ss);
	}
	
	@Override
	protected boolean canChangeCost() {
		updateCustomCost();
		return customCost == 0;
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		minCost = ((PriceEx)price.getData()).minCost;
	}
	
	@Override
	protected void onChangeCost(int newCost) {
		if( checkCost() && (newCost < minCost)) {
			Toast.makeText(this, R.string.cost_below_min, Toast.LENGTH_SHORT).show();
			return;
		}
		
		discount = 100 * Consts.SUM_SCALE - (int)(((float)newCost/(float)priceCost) * Consts.SUM_SCALE * 100 );
		updateDicsount();
		super.onChangeCost(newCost);
	}
	
	@Override
	protected boolean isInputValid(Runnable r) {
		if(checkCost() && (priceVal < minCost)){
			Toast.makeText(this, R.string.cost_below_min, Toast.LENGTH_SHORT).show();
			return false;
		}else
			return true;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.ivInfo) {
			Object tag = v.getTag();
			
			if (tag != null)
				showInfo(tag.toString());
		}
		
	}

	private void showInfo(String file) {
		try {
			Intent i = new Intent();
			i.setAction(Intent.ACTION_VIEW);
			String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file));
			
			Uri uri = null;
			
			if (Build.VERSION.SDK_INT >= 24) {
				uri = FileProvider.getUriForFile(this, "com.grsoft.napoleon.fileprovider", new File(file)); 
			}else
				uri = Uri.fromFile(new File(file));
			
			i.setDataAndType(uri, mime);
			i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			startActivity(i);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
	}
	
	protected boolean checkCost() {
		int check = 0;

		ConfigImpl cfg = new ConfigImpl();
		StringBuilder sb = new StringBuilder();

		cfg.getValue(sb, "–азрешитьѕродаватьЌижећинимальной÷ены");

		try {
			check = Integer.parseInt(sb.toString());
		}catch(Exception e) {
			e.printStackTrace();
		}

		return check == 0;
	}

	@Override
	protected void updateSumTextView() {
		super.updateSumTextView();

		//–ентабельность
		long ren = 0;

		if (priceVal != 0) {
			ren = (long)(((double)(priceVal - minCost)) / priceVal * 100 * Consts.SUM_SCALE);
		}

		if (tvRen == null)
			tvRen = (TextView) findViewById(R.id.tvRen);

		tvRen.setText(Util.IntToScaleStr(ren, Consts.SUM_SCALE));
	}
}

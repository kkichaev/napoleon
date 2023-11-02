package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class PricePresentationFolder extends FragmentActivity {
	public static Class<? extends Activity> activity = PricePresentationFolder.class;

	long priceId;
	static Document<?> document;

	// String selection;

	ViewPager pager;
	Adapter adapter;

	PriceImpl pi = new PriceImpl();
	
	static PresentationList list;

	public static void open(Context context, long priceId, long orderId, String condition) {
		Intent intent = new Intent(context, activity);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, orderId);
		intent.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceId);
		// intent.putExtra(ExtrasConst.FOLDERS_LIST_STR, condition);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutID());
		
		CfgNplW cfg = (CfgNplW) ConfigManager.getConfig();
		
		if(cfg.keepAwayInOrder)
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		Bundle b = (savedInstanceState != null) ? savedInstanceState : getIntent().getExtras();

		long orderRowId = ExtrasConst.INVALID_ROWID;
		
		if(b != null)
			orderRowId = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID);
		// selection = b.getString(ExtrasConst.FOLDERS_LIST_STR);

		document = DocType.getCurDoc().create();
		if (!(document instanceof Itemsable)) {
			document = OrderDoc.instance().create();
		}
		
		if(b != null)
			priceId = b.getLong(ExtrasConst.PRICE_ROW_ID_STR);

		if (orderRowId != ExtrasConst.INVALID_ID)
			document.read(orderRowId);

		initPresentList();
		
		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new Adapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		
		int idx = list.indexOf(priceId);
		
		if (idx >= 0)
			pager.setCurrentItem(idx, true);
	}

	protected void initPresentList() {
		list = PresentationFolderW.items;

		if (list.size() == 0)
			PresentationFolderW.items.fill(false);
	}

	protected int getLayoutID() { return R.layout.price_present_folder; }

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (document != null)
			document.close();

		document = null;
		pi.close();
	}

	class Adapter extends FragmentStatePagerAdapter {

		public Adapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Fragment getItem(int arg0) {
			if (arg0 < getCount()) {
				Fragment f = createFragment();
				Bundle b = new Bundle();
				b.putInt(ExtrasConst.PRICE_ROW_ID_STR, arg0);
				f.setArguments(b);
				return f;
			}
			return null;
		}
	}

	protected Fragment createFragment() {
		return new PriceFrag(this);
	}

	@SuppressWarnings("unchecked")
	protected void setText(View view, PresentationData pd) {
		TextView tv = (TextView) view.findViewById(R.id.tvPriceItems);
		String text = "***** " + pd.name;
		if( Features.COST_IN_PRESENTATION ) {
			Price p = pi.getData();
			p.id = pd.id;
			pi.read();			
			CostStrategy cs = CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass());
			text += "<br/>";
			if(document instanceof Itemsable) {
				int qty = ((Itemsable)document).getItemValue(p);
				text += Util.IntToScaleStr(qty, Consts.QTY_SCALE) + "&nbsp;&nbsp;&nbsp;";
			}
			text += "<i>" + Util.IntToScaleStr(cs.getItemCost(p, document), Consts.SUM_SCALE) + "</i>";
		}
		tv.setText(Html.fromHtml(text));
	}

	protected int getFragmentLayoutId() {
		return R.layout.price_present_fragment;
	}

	@SuppressLint({ "ValidFragment" })
	public static class PriceFrag extends Fragment {
		PresentationData pd;
		ViewGroup v1 = null, v2 = null, current = null;
		PricePresentationFolder owner;

		public PriceFrag(PricePresentationFolder owner) {
			this.owner = owner;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			Bundle b = getArguments();
			int pos = b.getInt(ExtrasConst.PRICE_ROW_ID_STR);
			pd = PricePresentationFolder.list.get(pos);
		}

		@Override
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);
			outState.putInt(ExtrasConst.PRICE_ROW_ID_STR, PricePresentationFolder.list.indexOf(pd));
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			if (v1 == null) {
				v1 = (ViewGroup) inflater.inflate(owner.getFragmentLayoutId(), container, false);
				v2 = (ViewGroup) inflater.inflate(owner.getFragmentLayoutId(), container, false);
				current = v1;
			} else {
				current = (current == v1) ? v2 : v1;
			}
			// ViewGroup rootView = (ViewGroup)
			// inflater.inflate(R.layout.price_present_fragment, container,
			// false);
			ViewGroup rootView = current;

			owner.setText(rootView, pd);
			// tv.setText(pd.name);

			try {
				BitmapFactory.Options opt = new BitmapFactory.Options();
				Bitmap bitmap = BitmapFactory.decodeFile(pd.image, opt);

				ImageView iv = ((ImageView) rootView.findViewById(R.id.ivPresent));
				iv.setImageBitmap(bitmap);
				if (PricePresentationFolder.document.getRowid() != ExtrasConst.INVALID_ID) {
					if( pd != null ) {
						iv.setTag(pd.rowid);
						iv.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								Long rid = (Long) v.getTag();
								((Itemsable) PricePresentationFolder.document).editItem(rid, v.getContext());
							}
						});
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return rootView;
		}
	}
}

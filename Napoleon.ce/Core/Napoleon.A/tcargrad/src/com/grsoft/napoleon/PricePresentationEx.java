package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;

public class PricePresentationEx extends FragmentActivity  {
	public static Class<? extends Activity> activity = PricePresentationEx.class;

	long priceId;
	static Document<?> document;
	
//	String selection;
	
	ViewPager pager;
	Adapter adapter;
	
	static PresentationList list;
	
    public static void open(Context context, long priceId, long orderId, String condition){
		Intent intent = new Intent(context, activity);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, orderId);
		intent.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceId);
//		intent.putExtra(ExtrasConst.FOLDERS_LIST_STR, condition);
		context.startActivity(intent);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.price_present_ex);

		Bundle b = (savedInstanceState != null) ? savedInstanceState : getIntent().getExtras();
		
		long orderRowId = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
//		selection = b.getString(ExtrasConst.FOLDERS_LIST_STR);
		
		document = DocType.getCurDoc().create();
		if (!(document instanceof Itemsable)) {
			document = OrderDoc.instance().create();
		}
		priceId = b.getLong(ExtrasConst.PRICE_ROW_ID_STR);
		
		if (orderRowId != ExtrasConst.INVALID_ID)
			document.read(orderRowId);

    	list = PresentationEx.items;
    	if( list == null || document.getRowid() == ExtrasConst.INVALID_ID ) {
    		list = new PresentationList();
    		String image =  b.getString(ExtrasConst.PRICE_PHOTO_PATH);
    		if( image != null ) {
    			PriceImpl pi = new PriceImpl();
    			Price p = pi.getData();
    			pi.read(priceId);
    			PresentationData pd = new PresentationData(priceId, p.folderID, p.name, image, p.id);
    			list.add(pd);
    		}
    	}

		pager = (ViewPager)findViewById(R.id.pager);
    	adapter = new Adapter(getSupportFragmentManager());
    	pager.setAdapter(adapter);
    	
    	int idx = list.indexOf(priceId);
    	if( idx >= 0 )
    		pager.setCurrentItem(idx);
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	Document<?> d = document;
    	document = null;
    	d.close();
    }
        
    class Adapter extends FragmentStatePagerAdapter  {

		public Adapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() { return list.size(); }

		@Override
		public Fragment getItem(int arg0) {
			if( arg0 < getCount() ) {
				Fragment f =  new PriceFrag();
				Bundle b = new Bundle();
				b.putInt(ExtrasConst.PRICE_ROW_ID_STR, arg0);
				f.setArguments(b);
				return f;
			}
			return null;
		}
    }
}

class PriceFrag extends Fragment {
	PresentationData pd;
	
	ViewGroup v1 = null, v2 = null, current = null;
	
	public PriceFrag() {
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle b = getArguments();
		int pos = b.getInt(ExtrasConst.PRICE_ROW_ID_STR);
		pd = PricePresentationEx.list.get(pos);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(ExtrasConst.PRICE_ROW_ID_STR, PricePresentationEx.list.indexOf(pd));
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if( v1 == null ) {
			v1 = (ViewGroup) inflater.inflate(R.layout.price_present_fragment, container, false);
			v2 = (ViewGroup) inflater.inflate(R.layout.price_present_fragment, container, false);
			current = v1;
		} else {
			current = (current == v1) ? v2 : v1; 
		}
        //ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.price_present_fragment, container, false);
		ViewGroup rootView = current;

        TextView tv;
        tv = (TextView)rootView.findViewById(R.id.tvPriceItems);
        tv.setText(pd.name);

        BitmapFactory.Options opt = new BitmapFactory.Options();
    	Bitmap bitmap = BitmapFactory.decodeFile(pd.image, opt);

        ImageView iv = ((ImageView)rootView.findViewById(R.id.ivPresent));
    	iv.setImageBitmap(bitmap);
		if( PricePresentationEx.document.getRowid() != ExtrasConst.INVALID_ID )
	        iv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((Itemsable)PricePresentationEx.document).editItem(pd.rowid, v.getContext());
				}
			});
        
        return rootView;
	}
}

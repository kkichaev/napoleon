package com.grsoft.napoleon;

import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;
import com.grsoft.dataobjects.IOrder;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.ExtrasConst;


public class DeliveryType extends FragmentActivity{
	private Spinner spDlvType;
	private FrameLayout holder;
	private CreatableDocument<?> order = null;
	
	public interface UpdateOrder{ boolean checkAndUpdate(IOrder order); }
	
	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, DeliveryType.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.dlvtype);
		
		order = (CreatableDocument<?>) DocType.getCurDoc().create();
		
		inflateView();
		initData();
		initView();
	}

	private void initView() {
		spDlvType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				holder.removeAllViews();
				
				if(position > 0){
					FragmentManager fm = getSupportFragmentManager();
					FragmentTransaction transact = fm.beginTransaction();
					
					List<Fragment> list = fm.getFragments();
					
					if(list != null)
						for(Fragment f : list)
							transact.remove(f);
					
					Fragment f = getFragment(position);
					Bundle bundle = new Bundle();
					bundle.putLong(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
					f.setArguments(bundle);
					
					transact.add(holder.getId(), f);
					transact.commit();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}});
		
		spDlvType.setSelection(((IOrder)order.getData()).getDlvType(), true);
	}
	
	private Fragment getFragment(final int idx){
		switch(idx){
		case 1:
			return new SelfDelivery();
		case 2:
			return new ClientDelivery();
		case 3:
			return new CarrierDelivery();
		default: return null;
		}
	}

	private void initData() {
		order.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		order.close();
	}

	private void inflateView() {
		spDlvType = (Spinner) findViewById(R.id.spDlvType);
		holder = (FrameLayout) findViewById(R.id.holder);
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onBackPressed() {
		FragmentManager fm = getSupportFragmentManager();
		boolean allowBack = true;
		IOrder ord = (IOrder) order.getData();
		
		
		int dt = spDlvType.getSelectedItemPosition();
		
		if(dt > 0){
			List<Fragment> list = fm.getFragments();
			
			if(list != null)
				for(Fragment f: list){
					if(f instanceof UpdateOrder)
						allowBack =((UpdateOrder)f).checkAndUpdate(ord); 
					
					if(!allowBack)
						break;
				}
		}
		
		if(allowBack){
			ord.setDlvType(dt);
			order.write();
			super.onBackPressed();
		}else
			Toast.makeText(this, R.string.should_post_neccessary_fields, Toast.LENGTH_SHORT).show();
	}
}

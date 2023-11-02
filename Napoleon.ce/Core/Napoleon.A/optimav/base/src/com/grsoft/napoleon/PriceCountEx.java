package com.grsoft.napoleon;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import com.grsoft.dataobjects.IOrg;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Itemsable;

public class PriceCountEx extends PriceCount {
	int ostatok = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences pref = getSharedPreferences(DocumentsEx.CREDIT_PREF_NAME, 
				Context.MODE_PRIVATE);
		ostatok = pref.getInt(DocumentsEx.CREDIT_OST, 0);
	}
	@Override
	protected boolean updateOrder() {
		OrgImpl orgImpl = new OrgImpl();
	    orgImpl.getData().id = document.getData().id;
	    boolean acl = false;
	    
	    if(orgImpl.read())
	    	acl = ((IOrg)orgImpl.getData()).isApplyCreditLimit();
	    	
	    orgImpl.close();	
		
	    long docSum = document.sum();
	    if( document instanceof Itemsable )
	    	docSum -= ((Itemsable)document).getItemSum(price.getData());
	    
		if(acl && ostatok - docSum - getSumValue() < 0){
			Toast.makeText(this, R.string.creatid_exceed, Toast.LENGTH_SHORT).show();
			return false;
		}else
			return super.updateOrder();
	}
}

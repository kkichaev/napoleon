package com.grsoft.napoleon;

import com.grsoft.dataobjects.PaySale;
import com.grsoft.dataobjects.impl.PaySaleImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;


public class PaySaleEdit extends DialogFragment implements OnClickListener{
	private EditText edName;
	private EditText edSum;
	private View btnOK;
	private View btnCancel;
	private PaySaleImpl document = new PaySaleImpl();
	private DataSetNotify parent;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.paysaleedit, null, false);
	
		edName = (EditText) view.findViewById(R.id.edName);
		edSum = (EditText) view.findViewById(R.id.edSum);
		btnOK = view.findViewById(R.id.btnOK);
		btnCancel = view.findViewById(R.id.btnCancel);
		
		long r = getArguments().getLong(ExtrasConst.DOC_ROW_ID_STR);
		document.read(r);
		
		edName.setText(document.getData().name);
		
		if(document.getData().sum > 0)
			edSum.setText(Util.IntToScaleStr(document.getData().sum, Consts.SUM_SCALE));
		
		btnOK.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if(id == R.id.btnOK)
			okClick();
		
		dismiss();
	}

	protected void okClick() {
		String name = edName.getText().toString().trim();
		
		if(name.length() > 0){
			int sum = Util.StrToScale(edSum.getText().toString().trim(), Consts.SUM_SCALE);
			
			PaySale ps = document.getData();
			ps.name = name;
			ps.sum = sum;
			
			document.write();
		}else{
			document.delete();
		}
		
		if(parent != null)
			parent.notifyDataSetChanged();
		
		document.close();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		if(activity instanceof DataSetNotify)
			parent = (DataSetNotify) activity;
	}
}

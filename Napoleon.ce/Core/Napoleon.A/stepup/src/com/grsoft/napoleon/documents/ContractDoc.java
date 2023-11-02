package com.grsoft.napoleon.documents;

import android.app.Activity;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;
import com.grsoft.dataobjects.impl.ContractImpl;
import com.grsoft.napoleon.R;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.documents.CreateByScriptDef;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;


public class ContractDoc extends DateDocType implements CreateByScriptDef{
	static private ContractDoc instance = null;
	
	private static final String DOC_NAME = "Контракт";
	private static final String OBJ_NAME = "Contract";
	
	public static DocType instance() {
		if( instance == null )
			instance = new ContractDoc();
		return instance;
	}
	
	protected ContractDoc() {
		super(DOC_NAME, OBJ_NAME, ContractImpl.class);
	}
	
	@Override
	public int getDocTitle() {return R.string.contract_doc_title; }
	
	@Override
	public int getResurceId() {	return R.drawable.contract_doc; }

	@Override
	public Document<?> create(ScriptDefItem item) {
		ContractImpl result = (ContractImpl)create();
		result.getData().def = item.condParam;
		return result;
	}
	
	public void updateTotalSum(Activity activity, long qty, int face, int count, int textViewId){
		TextView tvTotalSum = (TextView) activity.findViewById(textViewId);		
		if (tvTotalSum != null)
		{
			tvTotalSum.setVisibility(View.VISIBLE);
			String sumStr = Util.IntToScaleStr(qty, Consts.QTY_SCALE, Util.DEC_DELIM, true);
			String str;
			int si = 0, ei = sumStr.length(), ii = -1, iie = 0;
			if( face != 0 || count != 0 ) {
				str = "";
				if( count != 0 )
					str += Integer.toString(count) + " " + activity.getString(R.string.sht);
				if( face != 0 ) {
					if( str.length() > 0 ) str += ", ";
					str += Util.IntToScaleStr(face, Consts.QTY_SCALE, Util.DEC_DELIM, true);
				}
				
				ii = 0;
				iie = str.length();
				ei += iie + 1;
				si += iie + 1;
				str += "\n" + sumStr;				
			} else
				str = sumStr;
			
			SpannableString ss = new SpannableString(str);
			if( ii >= 0 ) 
				ss.setSpan( new StyleSpan(android.graphics.Typeface.ITALIC), ii, iie, 0);			
			ss.setSpan( new StyleSpan(android.graphics.Typeface.BOLD), si, ei, 0);			
			tvTotalSum.setText(ss);//
		}
	}

}

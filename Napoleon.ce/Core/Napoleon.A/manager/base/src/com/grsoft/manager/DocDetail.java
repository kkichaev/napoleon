package com.grsoft.manager;

import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.impl.MPriceImpl;
import com.grsoft.manager.documents.MDocType;
import com.grsoft.napoleon.documents.Document;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class DocDetail extends FragmentActivity {
	public static DocDetailDecorator decorator = new DocDetailDecorator();
	protected MPriceImpl price = new MPriceImpl();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(decorator.getLayoutID());
		decorator.initView(this);

		if(decorator.initDoc(getIntent()))
			decorator.init(this);
	}

	public String getTitle(CreateDocDataObject exdata) {	return MDocType.getTitle(this, exdata.getClass()); }
	
	public ListAdapter createAdapter(){ return null; };

	public void showRemark(View v) {
		RemarkFragment rf = new RemarkFragment();
		Bundle args = new Bundle();
		args.putString(RemarkFragment.REMARK, ((TextView)v).getText().toString());
		rf.setArguments(args);
		rf.show(getSupportFragmentManager(), rf.getClass().toString());
	}
	
	protected String priceName(String id){
		String result = String.format("товар с кодом<%s>", id);
		final String ID_STR = "id";
		if(price.read(ID_STR, id ))
			result = price.getData().name;
		price.close();
		
		return result;
	}
	
	protected Document<?> getDocument(){ return decorator.getDocument(); }
}

class RemarkFragment extends DialogFragment {

	protected static final String REMARK = "remark";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		TextView result = new TextView(inflater.getContext());
		result.setTextColor(getResources().getColor(R.color.black));
		result.setText(getArguments().getString(REMARK).trim());
		result.setGravity(Gravity.CENTER);
		result.setTextSize(22);
		
		return result;
	}
}

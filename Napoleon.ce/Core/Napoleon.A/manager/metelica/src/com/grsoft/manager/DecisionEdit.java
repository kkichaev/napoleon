package com.grsoft.manager;

import java.util.Date;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderRequest;
import com.grsoft.dataobjects.impl.OrderRequestImpl;
import com.grsoft.napoleon.documents.DocumentUtils;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;


public class DecisionEdit extends DialogFragment {
	private final static String ORDER = "order";
	private final static String USERID = "userid";
	private final static String ID = "id";
	private long order;
	private String userid;
	private String id;
	private EditText edDecision;
	private Spinner spDecision;
	private OrderRequest decision;
	private View btnOK;
	
	public static class Params{
		public long order = ExtrasConst.INVALID_ROWID;
		public long decision = ExtrasConst.INVALID_ROWID;
		public String userid = "";
		protected String id = "";
	}
	
	public static void showDialog(FragmentActivity parent, Params arg){
		Bundle args = new Bundle();
		args.putLong(ORDER, arg.order);
		args.putString(USERID, arg.userid);
		args.putString(ID, arg.id);
		
		DialogFragment dialog = new DecisionEdit();
		dialog.setArguments(args);
		dialog.show(parent.getSupportFragmentManager(), dialog.getClass().toString());
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.FragmentDialog);
		
		Bundle args = getArguments();
		order = args.getLong(ORDER);
		userid = args.getString(USERID);
		id = args.getString(ID);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View result = inflater.inflate(R.layout.decisionedit, container);
		inflateView(result);
		
		getDialog().setTitle(R.string.decision);
		
		result.findViewById(R.id.btnCancel).setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) { dismiss(); }
		});
		
		btnOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (spDecision.getSelectedItemPosition() > 0 && edDecision.getText().toString().trim().length() == 0){
					Toast.makeText(v.getContext(), R.string.needinputcomment, Toast.LENGTH_SHORT).show();
				}else{
					if(decision == null)
						newDecision();
					else
						applayDecision();
					
					getActivity().sendBroadcast(new Intent(OrderReviewEdit.REFRESH_ACTION));
					dismiss();
				}
			}
		});
		
		result.findViewById(R.id.btnDel).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SQLiteDatabase db = DataBaseManager.getDataBase();
				db.delete(DataObjectInfo.getInstance().getTableName(OrderRequest.class), "[order]=?", new String[]{Long.toString(order)});
				getActivity().sendBroadcast(new Intent(OrderReviewEdit.REFRESH_ACTION));
				dismiss();
			}
		});
		
		edDecision = (EditText) result.findViewById(R.id.edDecision);
		spDecision = (Spinner) result.findViewById(R.id.spDecision);
		spDecision.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) { edDecision.setEnabled(arg2 > 0); }
			@Override public void onNothingSelected(AdapterView<?> arg0) {}});
		
		decision = OrderRequestImpl.inflateDecision(order);
		
		if(decision != null){
			spDecision.setSelection(decision.decision, true);
			edDecision.setText(decision.remark);
			
			if(DocumentUtils.isExported(decision.params)){
				btnOK.setEnabled(false);
			}
		}
			
		return result;
	}
	
	protected void applayDecision() {
		decision.decision = spDecision.getSelectedItemPosition();
		decision.remark = edDecision.getText().toString().trim();
		
		DbWriter writer = new DbWriter();
		writer.insertRecord(decision);
		writer.close();
	}

	private void inflateView(View result) {
		btnOK = result.findViewById(R.id.btnOK);
	}

	private void newDecision() {
		OrderRequestImpl decImpl = new OrderRequestImpl();
		OrderRequest dec = decImpl.getData();
		dec.created = Util.getDateTime();
		dec.order = new Date(order);
		dec.userid = userid;
		dec.id = id;
		dec.remark = edDecision.getText().toString().trim();
		dec.decision = spDecision.getSelectedItemPosition();
		decImpl.write();
		decImpl.close();
	}
}

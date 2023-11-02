package com.grsoft.napoleon;

import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.IncassDebDistrEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.IncassDoc;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class IncassDebDistrEditEx extends IncassDebDistrEdit {
	OrgImpl org = new OrgImpl();
	RadioGroup rgBrakAnswer;
	
	protected int getRowLayoutID() {
		return R.layout.incass_deb_distr_row_ex;
	}

	protected ItemsAdapter createAdapter() {
		return new ItemsAdapter() {
			private DeliveryImpl delivery = new DeliveryImpl();

			@Override
			public View getView(int position, View view, ViewGroup parent) {
				view = super.getView(position, view, parent);

				Item i = (Item) getItem(position);
				delivery.getData().number = i.dlv.number;
				delivery.getData().id = doc.getId();
				delivery.read();
				delivery.close();

				TextView tv = (TextView) view.findViewById(R.id.tvAgent);
				tv.setText(((DeliveryEx) delivery.getData()).agent);
				tv.setTextColor(i.dlv.color);

				return view;
			}

		};
	}
	
	@Override protected int getContentViewID() { return R.layout.incass_deb_distr_ex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		View view = findViewById(R.id.brakAnswerLayout);
		RadioButton rbYes = (RadioButton) findViewById(R.id.rbYes);
		RadioButton rbNo = (RadioButton) findViewById(R.id.rbNo);
		rgBrakAnswer = (RadioGroup) findViewById(R.id.rgBrakAnswer);
		
		org.getData().id = doc.getId();
		org.read();
		org.close();
		
		if (((OrgEx)org.getData()).brak == 1) {
			IncassDebDistrEx inc = (IncassDebDistrEx) doc.getData();
			
			if (inc.brak.equals("1"))
				rbYes.setChecked(true);
			else if(inc.brak.equals("0"))
				rbNo.setChecked(true);
		}else
			view.setVisibility(View.GONE);
	}
	
	@Override
	public void onBackPressed() {
		if (((OrgEx)org.getData()).brak == 1 && doc.isEditable())
		{
			if (rgBrakAnswer.getCheckedRadioButtonId() == -1)
				Toast.makeText(this, "Необходимо выбрать забрать брак да или нет", Toast.LENGTH_SHORT).show();
			else
				super.onBackPressed();
		}else
			super.onBackPressed();
	}
	
	@Override
	protected void setDocument() {
		
		int id = rgBrakAnswer.getCheckedRadioButtonId();
		
		if (id == R.id.rbYes)
			((IncassDebDistrEx)doc.getData()).brak = "1";
		else if (id == R.id.rbNo)
			((IncassDebDistrEx)doc.getData()).brak = "0";
		
		super.setDocument();
	}
	
	@Override
	protected void send() {
		if (((OrgEx)org.getData()).brak == 1 && doc.isEditable())
		{
			if (rgBrakAnswer.getCheckedRadioButtonId() == -1)
				Toast.makeText(this, "Необходимо выбрать забрать брак да или нет", Toast.LENGTH_SHORT).show();
			else
				super.send();
		}else
			super.send();
	}
	
	protected void save() {
		if( !doc.isEditable() )
			return;
		
		setDocument();
		
		doc.write();
		IncassDoc.instance().refreshDocSum(doc.getId());
	}
	
	@Override
	protected boolean isEmptyDocHaveToRemove() {
		return false;
	}
	
	protected void btnOkPressed() {
		if (((OrgEx)org.getData()).brak == 1 && doc.isEditable())
		{
			if (rgBrakAnswer.getCheckedRadioButtonId() == -1)
				Toast.makeText(this, "Необходимо выбрать забрать брак да или нет", Toast.LENGTH_SHORT).show();
			else
				super.btnOkPressed();
		}else
			super.btnOkPressed();
	}
}

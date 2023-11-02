package com.grsoft.view;

import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.grsoft.napoleon.R;
import com.grsoft.util.OnClickListenerToNotify;

/**
 * Класс для работы ввода цифр 
 * @author 1111
 *
 */
public class InputNumberHelper {

	public static int keys[] = { R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
		R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDel, R.id.btnComma };
	
	private EditText edCount;
	
	public InputNumberHelper(EditText ed) { edCount = ed; }
	
	public void makeNumericKeypad(View view) { makeNumericKeypad(keys, view); }
	
	public void makeNumericKeypad(int[] idRes, View view)
	{
		edCount.selectAll();
		
		OnClickListener numKeyPress = new OnClickListenerToNotify()
		{
			@Override
			public void onClick(View v)
			{
				super.onClick(v);
				int s = edCount.getSelectionStart();
				int e = edCount.getSelectionEnd();
				Editable editable = edCount.getText();

				if(v.getId() == R.id.btnComma && editable.toString().indexOf((String)v.getTag()) != -1)
					return;
				if (v.getId() == R.id.btnDel) {
					if( e < 0 ) e = editable.length();
					if( s < 0 || s == e ) s = e - 1;
					if (e > 0)
						editable.delete(s, e);
				} else {
					if( e < 0 ) {
						e = editable.length();
						s = e;
					}
					editable.replace(s, e, (String)v.getTag());
					edCount.setSelection(editable.length());
				}
				
			}
		};
		
		for (int resourceId: keys)
			view.findViewById(resourceId).setOnClickListener(numKeyPress);
	}
}

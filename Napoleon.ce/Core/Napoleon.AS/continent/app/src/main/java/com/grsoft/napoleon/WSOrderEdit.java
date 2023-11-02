package com.grsoft.napoleon;

import com.grsoft.dataobjects.WSOrder;
import com.grsoft.dataobjects.impl.WSOrderImpl;
import com.grsoft.util.ExtrasConst;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WSOrderEdit extends Activity implements OnClickListener {
	private static final int DIALOG_DATE_PICKER_ID = 0;

	WSOrderImpl doc = new WSOrderImpl();
	EditText edRemark;
	private boolean editMode = false;
	
	public static void open(Context context, long rowid, boolean edit) {
		Intent i = new Intent(context, WSOrderEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		i.putExtra(ExtrasConst.EDIT_MODE_STR, edit);
		
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.wsorderedit);
		
		edRemark = (EditText)findViewById(R.id.edRemark);
		findViewById(R.id.btnOK).setOnClickListener(this);
		findViewById(R.id.btnCancel).setOnClickListener(this);
		
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		doc.close();


		WSOrder src = doc.getData();
		edRemark.setText(src.remark);
		
		((CheckBox)findViewById(R.id.cbDelibery)).setChecked(src.delivery > 0);

		findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(WSOrderEdit.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, doc.getDate().getTime());
				startActivityForResult(i, DIALOG_DATE_PICKER_ID);
			}
		});

		refreshDate();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null && requestCode == DIALOG_DATE_PICKER_ID ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Date newDate = new Date(ct);
			doc.getData().date = newDate;
			refreshDate();
		}
	}

	private void refreshDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
		((TextView)findViewById(R.id.tvDate)).setText(sd.format(doc.getDate()));
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnCancel) {
			if (!editMode && doc.isEmpty()) {
				doc.delete();
			}
		}else if (v.getId() == R.id.btnOK) {
			if(doc.isEditable()) {
				WSOrder src = doc.getData();

				src.remark = edRemark.getText().toString().trim();
				src.delivery = ((CheckBox)findViewById(R.id.cbDelibery)).isChecked() ? 1 : 0;
				doc.write();

				if(!editMode)
					Warehouse.open(WSOrderEdit.this, doc, false);
			}
		}
			
		doc.close();
		finish();
	}

}

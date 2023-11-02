package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.TextView;
import com.grsoft.dataobjects.impl.InvAuditImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.BaseActivity;


public class InvAuditEdit extends BaseActivity {
	private InvAuditImpl doc = new InvAuditImpl();
	private View btnOK;
	private View btnCancel;
	private boolean editMode = false;
	private DatePicker dpPenult;
	private DatePicker dpLast;
	
	public static void open(Context ctx, long rowid, boolean edit){
		Intent i = new Intent(ctx, InvAuditEdit.class);	
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		i.putExtra(ExtrasConst.EDIT_MODE_STR, edit);
		ctx.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invaudit);
		
		TextView tvOrgName = (TextView) findViewById(R.id.tvOrgName);
		btnOK = findViewById(R.id.btnOK);
		btnCancel = findViewById(R.id.btnCancel);
		dpPenult = (DatePicker) findViewById(R.id.dpPenult);
		dpLast = (DatePicker) findViewById(R.id.dpLast);
		
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, false);
		
		OrgImpl org = new OrgImpl();
		org.read("id", doc.getId());
		
		tvOrgName.setText(org.getData().name);
		btnOK.setOnClickListener(okClick());
		btnCancel.setOnClickListener(cancelClick());
		
		Calendar c = Calendar.getInstance();
		c.setTime(doc.getData().penult);
		dpPenult.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
		c.setTime(doc.getData().last);
		dpLast.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
	}

	private OnClickListener cancelClick() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!editMode)
					doc.delete();
				finish();
			}
		};
	}

	private Date getDate(DatePicker picker){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, picker.getYear());
		c.set(Calendar.MONTH, picker.getMonth());
		c.set(Calendar.DAY_OF_MONTH, picker.getDayOfMonth());
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c.getTime();
	}
	
	private OnClickListener okClick() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doc.getData().penult = getDate(dpPenult);
				doc.getData().last = getDate(dpLast);
				doc.write();
				doc.close();
				doc.open(v.getContext());
				finish();
			}
		};
	}
}

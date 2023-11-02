package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import java.util.Date;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.ReportList;
import com.grsoft.dataobjects.ReportRequest;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ReportListImpl;
import com.grsoft.dataobjects.impl.ReportRequestImpl;
import com.grsoft.network.ReportSync;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class ReportParams extends BaseActivity implements OrgSelectDialog.OrgSelect {
	protected static final int DATE_START_ID = 0;
	protected static final int DATE_END_ID = 1;
	
	public static Class<? extends Activity> activity = ReportParams.class;

	public interface Init {
		void init(ReportRequest request, ReportList repDef, ReportParams owner);
	}
	public static Init init = null;

	public static void open(Context context, String id) {
		Intent i = new Intent(context, activity);
		i.putExtra(ExtrasConst.ORG_ID_STR, id);
		context.startActivity(i);
	}
	
	ReportRequestImpl request = new ReportRequestImpl();
	OrgImpl org = new OrgImpl();
	boolean noUpdate = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(getContentViewID());
		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		String id = b.getString(ExtrasConst.ORG_ID_STR);
		
		ReportListImpl rlist = new ReportListImpl();
		ReportList rl = rlist.getData();
		
		ReportRequest rr = request.getData();
		rr.id = id;
		request.read();
		
		rl.id = id;
		rlist.read();
		rlist.close();
		
		TextView tv;
		tv = (TextView)findViewById(R.id.tvName);
		
		tv.setText(rl.name);
		
		tv = (TextView)findViewById(R.id.tvOrg);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(R.id.select_org_dlg); }
		});

		if(init != null) {
			init.init(rr, rl, this);
		}

		refreshDate(true);
		refreshDate(false);
		refreshOrg();
		
		findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				request.getData().idOrg = "";
				writeRequest();
				refreshOrg();
			}
		});
		
		findViewById(R.id.tvDateStart).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ReportParams.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, request.getData().start.getTime());
				startActivityForResult(i, DATE_START_ID);
			}
		});

		findViewById(R.id.tvDateEnd).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ReportParams.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, request.getData().end.getTime());
				startActivityForResult(i, DATE_END_ID);
			}
		});
		
		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			
			@Override public void onClick(View arg0) { sendRequest(); }
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == R.id.select_org_dlg)
			return OrgSelectDialog.create(this, this);
		return super.onCreateDialog(id);
	}
	
	protected void sendRequest() {
		if(noUpdate)
			writeRequest();
		
		ReportSync rs = new ReportSync(this, findViewById(R.id.btnSend));
		rs.execute((Void[])null);
	}

	void writeRequest() {
		noUpdate = false;
		ReportRequest rr = request.getData(); 
		rr.date = new Date();
		rr.sent = null;
		request.write();
	}
	
	private void refreshOrg() {
		String name = "";
		Org o = org.getData();
		o.id = request.getData().idOrg;
		if(org.read())
			name = "<u><b><font color='blue'>" + o.name + "</font></b></u><br/>" + o.address;
	
		TextView tv = (TextView)findViewById(R.id.tvOrg);
		tv.setText(Html.fromHtml(name));
	}

	private void refreshDate(boolean start) {
		ReportRequest rr = request.getData();
		Date d = (start) ? rr.start : rr.end;
		TextView tv = (TextView)findViewById(start ? R.id.tvDateStart : R.id.tvDateEnd);
		String text = "";
		
		if( d != null)
			text = "<u><b><font color='blue'>" + Util.simpleDateFormat.format(d) + "</font></b></u>";
		tv.setText(Html.fromHtml(text));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Date newDate = new Date(ct);
			ReportRequest rr = request.getData();
			if( requestCode == DATE_START_ID ) {
				rr.start = newDate;
				refreshDate(true);
			} else if( requestCode == DATE_END_ID) {
				rr.end = newDate;
				refreshDate(false);
			}
			writeRequest();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		request.close();
		org.close();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(ExtrasConst.ORG_ID_STR, request.getData().id);
	}
	
	protected int getContentViewID() { return R.layout.report_params; }

	@Override
	public void selected(Org o) {
		request.getData().idOrg = o.id;
		writeRequest();
		refreshOrg();
	}
}

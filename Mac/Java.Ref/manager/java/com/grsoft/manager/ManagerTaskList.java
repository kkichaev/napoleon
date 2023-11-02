package com.grsoft.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.ReportHitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ManagerAgent;
import com.grsoft.dataobjects.TaskReportParam;
import com.grsoft.dataobjects.TaskReportResult;
import com.grsoft.manager.R;
import com.grsoft.napoleon.util.CalendarDlg;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ManagerTaskList extends Activity {
	protected static final int SELECT_FROM_DATE = 0;
	protected static final int SELECT_TILL_DATE = 1;
	static Date dateFrom;
	static Date dateTill;

	public static void open(Context context) {
		Intent i = new Intent(context, ManagerTaskList.class);
		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.manager_task_report);
		
		final List<ManagerAgent> agents = new ArrayList<ManagerAgent>();
		DataTraveler.travel(ManagerAgent.class, new DataTraveler.Travel<ManagerAgent>() {

			@Override
			public boolean travel(DataTraveler<ManagerAgent> item) {
				agents.add(item.data);
				item.data = new ManagerAgent();
				return true;
			}
		}, "");
		
		Collections.sort(agents);
		
		ArrayAdapter<ManagerAgent> aa = new ArrayAdapter<ManagerAgent>(this, R.layout.simple_spinner_layout, agents);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		Spinner s = (Spinner)findViewById(R.id.spAgents);
		s.setAdapter(aa);
		if( aa.getCount() > 0 )
			s.setSelection(0);
		
		
		if( dateFrom == null) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MONTH, -1);
			dateFrom = c.getTime();
		}
		
		if( dateTill == null ) {
			dateTill = new Date();
		}
		
		refreshDate();
		
		findViewById(R.id.tvFrom).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(SELECT_FROM_DATE); }
		});
		findViewById(R.id.tvTill).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(SELECT_TILL_DATE); }
		});
		
		findViewById(R.id.btnRefresh).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { refresh(); }
		});
	}

	@SuppressLint("SetJavaScriptEnabled")
	protected void refresh() {
		ManagerAgent agent = (ManagerAgent)((Spinner)findViewById(R.id.spAgents)).getSelectedItem();
		if( agent == null ) {
			Toast.makeText(this, R.string.no_agent_selected, Toast.LENGTH_SHORT).show();
			return;
		}
		Calendar ctill = Calendar.getInstance();
		ctill.setTime(dateTill);
		ctill.add(Calendar.DAY_OF_MONTH, 1);

		TaskReportParam param = new TaskReportParam();
		param.start = dateFrom;
		param.finish = ctill.getTime();
		
		param.agentID = agent.id;
		param.agentName = agent.name;
		
		param.mode = "report";

		final ResultHitching rh = new ResultHitching();
		ReportHitching rp = new ReportHitching("orgtask", param, rh);
		
		List<Hitching> upd = new ArrayList<Hitching>();
		upd.add(rp);
		UpdateProcess up = new UpdateProcess(this, new UpdateCtrl() {
			
			@Override public void updateCtrl(boolean enabled) { findViewById(R.id.btnRefresh).setEnabled(enabled); }
			
			@Override
			public void onFinish(boolean success) {
				if( success ) {
					WebView webView = (WebView) findViewById(R.id.wbReport);
					webView.getSettings().setJavaScriptEnabled(true); 
			        webView.getSettings().setSupportZoom(true);
			        webView.loadDataWithBaseURL(null, rh.html(), "text/html", null, null);
			        webView.getSettings().setBuiltInZoomControls(true);
				}
			}
		}, upd);
		up.execute((Void[])null);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		
		if( id == SELECT_FROM_DATE || id == SELECT_TILL_DATE ) {
			CalendarDlg.setCurrentDate(dialog, (id == SELECT_FROM_DATE) ? dateFrom : dateTill);
		}
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		if( id == SELECT_FROM_DATE || id == SELECT_TILL_DATE ) {
			return CalendarDlg.create(this, new CalendarDlg.Handler() {
				
				@Override
				public void selectedDate(Date d) {
					if(id == SELECT_FROM_DATE)
						dateFrom = d;
					else
						dateTill = d;
					
					refreshDate();
				}
			});
		}
		return super.onCreateDialog(id);
	}

	@SuppressLint("SimpleDateFormat")
	private void refreshDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		
		String str = sdf.format(dateFrom);
		SpannableString content = new SpannableString(str);
		content.setSpan(new UnderlineSpan(), 0, str.length(), 0);

		((TextView)findViewById(R.id.tvFrom)).setText(content);
		
		str = sdf.format(dateTill);
		content = new SpannableString(str);
		content.setSpan(new UnderlineSpan(), 0, str.length(), 0);
		((TextView)findViewById(R.id.tvTill)).setText(content);
	}
}


class ResultHitching extends Hitching {
	
	TaskReportResult data;
	
	public ResultHitching() {
		super(TaskReportResult.class, "Result");
		data = new TaskReportResult();
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		data = (TaskReportResult)rawObject.createDataObject(dataObject);
	}

	public String html() { return data.html; }
}

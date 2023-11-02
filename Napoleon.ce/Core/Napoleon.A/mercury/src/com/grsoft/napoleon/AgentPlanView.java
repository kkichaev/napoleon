package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Price;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

@SuppressLint("UseSparseArrays")
public class AgentPlanView extends Activity {
	private WebView webView;

	public static void open(Context context) {
		Intent intent = new Intent(context, AgentPlanView.class);
		context.startActivity(intent);
	}

	public String fmtHeaderCell(String width, String val) {
		StringBuilder result = new StringBuilder();

		result.append("<td width='").append(width).append("'>")
				.append("<font size='4'><b>").append(val).append("<b></font>")
				.append("</td>");

		return result.toString();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agentplanview);

		webView = (WebView) findViewById(R.id.web);

		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);

		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				PlanHelper.init();
				DbWriter.checkDBTable(Folder.class);
				
				StringBuilder html = new StringBuilder();
				html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
				html.append("<table>");
				html.append("<tr>");

				html.append(fmtHeaderCell("55%", "Наименование"));
				html.append(fmtHeaderCell("5%", "&nbsp"));
				html.append(fmtHeaderCell("5%", "План руб."));
				html.append(fmtHeaderCell("5%", "План бут."));
				html.append(fmtHeaderCell("5%", "Факт руб.."));
				html.append(fmtHeaderCell("5%", "Факт бут."));
				html.append(fmtHeaderCell("5%", "% руб."));
				html.append(fmtHeaderCell("5%", "% бут."));
				html.append(fmtHeaderCell("5%", "Аппр. руб."));
				html.append(fmtHeaderCell("5%", "Аппр. бут."));

				html.append("</tr>");

				final List<Price> list = new ArrayList<Price>();
				DataTraveler.travel(Price.class, new DataTraveler.Travel<Price>() {
					@Override public boolean isDataNewInstance() { return true; }
					@Override
					public boolean travel(DataTraveler<Price> item) {
						list.add(item.data);
						return true;
					}}, null);
				
				Collections.sort(list, new Comparator<Price>(){@Override
				public int compare(Price lhs, Price rhs) { return lhs.name.compareTo(rhs.name);	}});

				for (Price p : list) 
					collectFromNode(p, html);

				html.append("</table>");

				return html.toString();
			}

			protected void onPreExecute() {
				showDialog(R.id.waitdlg);
			};

			protected void onPostExecute(String result) {
				webView.loadDataWithBaseURL(null, result, "text/html", null,
						null);
				dismissDialog(R.id.waitdlg);
			};

		}.execute((Void[]) null);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case R.id.waitdlg:
			return createWaitDlg();
		default:
			return super.onCreateDialog(id);
		}
	}

	private Dialog createWaitDlg() {
		return ProgressDialog.show(this, getString(R.string.wait),
				getString(R.string.waiting));
	}

	private void collectFromNode(Price node, StringBuilder sb) {
		sb.append("<tr>");
		sb.append("<td>");
		sb.append("<font color=\"blue\">");
		insertIntent(0, sb);
		sb.append(node.name);
		sb.append("</font>");

		sb.append("</td>");
		sb.append("<td width='50'>");
		sb.append("&nbsp");
		sb.append("</td>");

		sb.append("<td width='70'>");
		sb.append("<font color=\"blue\">");
		int planSum = getGroupPlanSum(node.id);
		sb.append(Util.IntToScaleStr(planSum, Consts.SUM_SCALE));
		sb.append("</font>");
		sb.append("</td>");

		sb.append("<td width='70'>");
		sb.append("<font color=\"blue\">");
		int planWeight = getGroupPlanWeight(node.id);
		sb.append(Util.IntToScaleStr(planWeight, Consts.QTY_SCALE));
		sb.append("</font>");
		sb.append("</td>");

		sb.append("<td>");
		sb.append("<font color=\"blue\">");
		int factSum = getGroupFactSum(node.id);
		sb.append(Util.IntToScaleStr(factSum, Consts.SUM_SCALE));
		sb.append("</font>");
		sb.append("</td>");

		sb.append("<td>");
		sb.append("<font color=\"blue\">");
		int factWeight = getGroupFactWeight(node.id);
		sb.append(Util.IntToScaleStr(factWeight, Consts.QTY_SCALE));
		sb.append("</font>");
		sb.append("</td>");

		sb.append("<td>");
		sb.append("<font color=\"blue\">");
		int percent = planSum == 0 ? 0 : (int) Math.round(((double) factSum
				/ planSum * 100));
		sb.append(percent);
		sb.append("</font>");
		sb.append("</td>");

		sb.append("<td>");
		sb.append("<font color=\"blue\">");
		percent = planWeight == 0 ? 0 : (int) Math.round(((double) factWeight
				/ planWeight * 100));
		sb.append(percent);
		sb.append("</font>");
		sb.append("</td>");

		Calendar cal = Calendar.getInstance();
		long now = cal.getTime().getTime();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		long tomorrow = cal.getTimeInMillis();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		long begin = cal.getTimeInMillis();
		cal.set(Calendar.DAY_OF_MONTH,
				cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		long end = cal.getTimeInMillis();
		int wd = workingDay(cal, begin, now);
		int avgSum = wd != 0 ? factSum / wd : 0;
		int avgWeight = wd != 0 ? factWeight / wd : 0;
		wd = workingDay(cal, tomorrow, end);
		int endSum = avgSum * wd;
		int endWeight = avgWeight * wd;
		int apprSum = factSum + endSum;
		int apprWeight = factWeight + endWeight;
		percent = planSum == 0 ? 0 : (int) Math.round(((double) apprSum
				/ planSum * 100));

		sb.append("<td>");
		sb.append("<font color=\"blue\">");
		sb.append(percent);
		sb.append("</font>");
		sb.append("</td>");

		percent = planWeight == 0 ? 0 : (int) Math.round(((double) apprWeight
				/ planWeight * 100));
		sb.append("<td>");
		sb.append("<font color=\"blue\">");
		sb.append(percent);
		sb.append("</font>");
		sb.append("</td>");

		sb.append("</tr>");
	}

	public int workingDay(Calendar cal, long start, long end) {
		int result = 0;

		while (end >= start) {
			cal.setTime(new Date(end));
			int day = cal.get(Calendar.DAY_OF_WEEK);

			if (!(day == Calendar.SATURDAY || day == Calendar.SUNDAY))
				result++;

			end -= 1000 * 60 * 60 * 24;
		}

		return result;
	}

	private int getGroupPlanWeight(String id) { return PlanHelper.getPlanQty(id); }

	private int getGroupFactWeight(String id) {	return PlanHelper.getOrdQty(id); }

	private int getGroupFactSum(String id) { return PlanHelper.getOrdSum(id); }

	private int getGroupPlanSum(String id) { return PlanHelper.getPlanSum(id); }

	protected void insertIntent(int level, StringBuilder sb) {
		final int INTENT = 8;
		for (int a = 1; a < level; a++)
			for (int aa = 0; aa < INTENT; aa++)
				sb.append("&nbsp;");
	}

}

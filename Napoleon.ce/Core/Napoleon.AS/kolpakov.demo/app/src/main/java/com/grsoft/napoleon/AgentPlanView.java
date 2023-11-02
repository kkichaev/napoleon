package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.AgentPlan;
import com.grsoft.dataobjects.AgentPlanItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;
import com.grsoft.util.Pair;
import com.grsoft.util.Util;

@SuppressLint("UseSparseArrays")
public class AgentPlanView extends Activity {
	private WebView webView;
	private Map<String, Pair<Integer, Integer>> groupsPlan = new HashMap<String, Pair<Integer, Integer>>();
	private Map<Integer, String> idfid = new HashMap<Integer, String>();
	private Map<Integer, Pair<Integer, Integer>> groups = new HashMap<Integer, Pair<Integer, Integer>>();

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
				String result = null;

				AgentPlan data = new AgentPlan();
				DbReader reader = new DbReader();

				long now = new Date().getTime();
				StringBuilder where = new StringBuilder();
				where.append("begin <= ").append(now).append(" and end >= ")
						.append(now);

				boolean bdo = reader.select(data, DataObjectInfo.getInstance()
						.getTableName(data.getClass()), where.toString());

				if (bdo) {
					collectOrders(data.begin, data.end);
					DbWriter.checkDBTable(Folder.class);
					Cursor c = null;

					try {
						c = DataBaseManager.getDataBase().query(
								DataObjectInfo.getInstance().getTableName(
										Folder.class),
								new String[] { "id", "fid" }, null, null, null,
								null, null);
						idfid.clear();
						while (c.moveToNext())
							idfid.put(c.getInt(c.getColumnIndex("id")),
									c.getString(c.getColumnIndex("fid")));

					} finally {
						if (c != null)
							c.close();
					}

					groupsPlan.clear();

					for (AgentPlanItem item : data.groups)
						groupsPlan.put(item.id, new Pair<Integer, Integer>(
								item.valueSum, item.valueWeight));

					StringBuilder html = new StringBuilder();
					html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
					html.append("<table>");
					html.append("<tr>");

					html.append(fmtHeaderCell("55%", "Группа"));
					html.append(fmtHeaderCell("5%", "&nbsp"));
					html.append(fmtHeaderCell("5%", "План руб."));
					html.append(fmtHeaderCell("5%", "План кг."));
					html.append(fmtHeaderCell("5%", "Факт руб.."));
					html.append(fmtHeaderCell("5%", "Факт кг."));
					html.append(fmtHeaderCell("5%", "% руб."));
					html.append(fmtHeaderCell("5%", "% кг."));
					html.append(fmtHeaderCell("5%", "Аппр. руб."));
					html.append(fmtHeaderCell("5%", "Аппр. кг."));

					html.append("</tr>");

					Folder folder = new Folder();
					bdo = reader.select(folder, DataObjectInfo.getInstance()
							.getTableName(folder.getClass()), null);

					while (bdo) {
						collectFromNode(folder, html);
						bdo = reader.selectNext(folder);
					}

					html.append("</table>");
					result = html.toString();
				}

				reader.close();

				return result;
			}

			private void collectOrders(Date begin, Date end) {
				Order data = new Order();
				DbReader reader = new DbReader();
				StringBuilder where = new StringBuilder();
				where.append("created >= ").append(begin.getTime())
						.append(" and created <= ").append(end.getTime());
				PriceImpl priceImpl = new PriceImpl();
				boolean bdo = reader.select(data, DataObjectInfo.getInstance()
						.getTableName(data.getClass()), where.toString());

				while (bdo) {
					for (OrderItem item : data.items) {
						priceImpl.getData().id = item.id;
						priceImpl.read();

						int w = (int) FPOperation.itemMul(priceImpl.getData().weight,
								item.qty, Consts.QTY_SCALE);
						int s = (int) FPOperation.itemMul(item.cost, item.qty,
								Consts.QTY_SCALE);

						int fid = priceImpl.getData().folderID;
						Pair<Integer, Integer> pair = null;

						if (groups.containsKey(fid))
							pair = groups.get(fid);
						else
							pair = new Pair<Integer, Integer>(0, 0);

						s += pair.first;
						w += pair.second;

						groups.put(fid, new Pair<Integer, Integer>(s, w));
					}

					bdo = reader.selectNext(data);
				}

				priceImpl.close();
				reader.close();

				FolderTree folderTree = new FolderTree();
				folderTree.load();
				
				ArrayList<Item> tree = makeTree(folderTree);
				proccessTree(tree, new Pair<Integer, Integer>(0,0));
			}

			
			private void proccessTree(ArrayList<Item> tree, Pair<Integer, Integer> val) {
				for(Item i: tree){
					Pair<Integer, Integer> x = new Pair<Integer, Integer>(0, 0);
					
					if(i.childs.size() > 0 )
						proccessTree(i.childs, x);
					
					Pair<Integer, Integer> p = new Pair<Integer, Integer>(0, 0);
					
					if(groups.containsKey(i.folder.id))
						p = groups.get(i.folder.id);
					
					x.first += p.first;
					x.second += p.second;
					
					groups.put(i.folder.id, x);
					
					val.first += x.first;
					val.second += x.second;
				}
			}


			class Item {
				public ArrayList<Item> childs = new ArrayList<Item>();
				public Item parent;
				public Folder folder;
			}

			private ArrayList<Item> makeTree(ArrayList<Folder> folders) {
				ArrayList<Item> ret = new ArrayList<Item>();
				
				@SuppressWarnings("unused")
				int level = 0;
				Item curParent = null;
				for(Folder f : folders) {
					Item ci = new Item();
					ci.folder = f;
					
					if( curParent != null ) {
						if( f.level > curParent.folder.level ) {
							level++;
						} else {
							while(curParent != null && f.level <= curParent.folder.level ) {
								if( f.level != curParent.folder.level )
									level--;
								curParent = curParent.parent;
							}
							if( curParent == null )
								level = 0;
						}
					}

					ci.parent = curParent;
					if( curParent != null )
						curParent.childs.add(ci);
					curParent = ci;
					ret.add(ci);
				}
				
				return ret;
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

	private void collectFromNode(Folder node, StringBuilder sb) {
		sb.append("<tr>");
		sb.append("<td>");
		sb.append("<font color=\"blue\">");
		insertIntent(node.level, sb);
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
		int avgSum = factSum / wd;
		int avgWeight = factWeight / wd;
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

	private int getGroupPlanWeight(int id) {
		int result = 0;

		if (idfid.containsKey(id)) {
			String fid = idfid.get(id);

			if (groupsPlan.containsKey(fid))
				result = groupsPlan.get(fid).second;
		}

		return result;
	}

	private int getGroupFactWeight(int id) {
		int result = 0;

		if (groups.containsKey(id))
			result = groups.get(id).second;

		return result;
	}

	private int getGroupFactSum(int id) {
		int result = 0;

		if (groups.containsKey(id))
			result = groups.get(id).first;

		return result;
	}

	private int getGroupPlanSum(int id) {
		int result = 0;

		if (idfid.containsKey(id)) {
			String fid = idfid.get(id);

			if (groupsPlan.containsKey(fid))
				result = groupsPlan.get(fid).first;
		}

		return result;
	}

	protected void insertIntent(int level, StringBuilder sb) {
		final int INTENT = 8;
		for (int a = 1; a < level; a++)
			for (int aa = 0; aa < INTENT; aa++)
				sb.append("&nbsp;");
	}

}

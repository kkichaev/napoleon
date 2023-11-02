package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentPlan;
import com.grsoft.dataobjects.AgentPlanItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.FolderTree;
import com.grsoft.util.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;

@SuppressLint("UseSparseArrays")
public class AgentPlanView extends Activity {
	
	class Item implements Comparable<Item> {
		public List<Item> childs = new ArrayList<Item>();
		
		public Item parent;
		public String id;
		public String name;
		public int type;
		public int planSum;
//		public int planQty;
		public int factSum;
//		public int factQty;
		
		public void addChild(Item i) {
			childs.add(i);
			i.parent = this;
		}

		@Override
		public int compareTo(Item arg0) {
			return name.compareTo(arg0.name);
		}
		
		public void sort() {
			Collections.sort(childs);
			for(Item i : childs)
				i.sort();
		}
	}
	
	private WebView webView;
	private Map<String, Item> addedFolders = new HashMap<String, Item>();
	Map<String, Item> planItems = new HashMap<String, AgentPlanView.Item>();
	
	private List<Item> itemsTree = new ArrayList<Item>();
	private FolderTree foldersTree = new FolderTree();

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
				
				if (fillAgentPlan(data)) {
					fillFactValues(data.begin, data.end);
					
					Collections.sort(itemsTree);
					for(Item i : itemsTree)
						i.sort();
					
					StringBuilder html = new StringBuilder();
					makeHtmlHeader(html);
					
					for (Item root : itemsTree)
						printNode(root, html, 1);
					
					html.append("</table>");
					result = html.toString();
				}

				return result;
			}
			
			private void addItemToPlanTree(Map<String, Item> planFolders, Folder parent, Item i) {
				if(i.type == AgentPlanItem.FOLDER_TYPE)
					planFolders.put(i.id, i);
				do {
					Item pi = planFolders.get(parent.fid);
					if(pi == null) {
						pi = new Item();
						pi.type = AgentPlanItem.FOLDER_TYPE;
						pi.id = parent.fid;
						pi.name = parent.name;
						pi.addChild(i);
					} else {
						pi.addChild(i);
						break;
					}
					i = pi;
					planFolders.put(i.id, i);
					
					parent = foldersTree.getParent(parent);
					if(parent == null) {
						itemsTree.add(i);
						break;
					}						
				} while(true);
			}


			@SuppressLint("DefaultLocale")
			private boolean fillAgentPlan(AgentPlan plan) {
				boolean bdo = false;
				
				DbReader reader = new DbReader();
				long now = new Date().getTime();
				String planTableName = DataObjectInfo.getInstance().getTableName(plan.getClass());
				String planWhere = String.format("begin <= %d and end >= %d", now, now);
				
				foldersTree.clear();
				itemsTree.clear();
				addedFolders.clear();
				planItems.clear();
				
				if ( (bdo = reader.select(plan, planTableName, planWhere))) {
					foldersTree.load();
					
					PriceImpl pi = new PriceImpl();
					for (AgentPlanItem planItem : plan.groups) {
						if(planItem.valueSum == 0)
							continue;
						
						Folder parent= null;
						Item i = new Item();
						i.id = planItem.id;
						i.planSum = planItem.valueSum;
						i.type = planItem.type;
						if (AgentPlanItem.PRICE_TYPE == planItem.type) {
							Price p = pi.getData();
							p.id = planItem.id;
							if( pi.read() ) {
								i.name = p.name;
								parent = foldersTree.getFolder(p.folderID);
							}
							planItems.put(i.id, i);
						} else {
							Folder f = foldersTree.getFolder(planItem.id);
							if( f != null ) {
								if(addedFolders.containsKey(f.fid) != false) {
									addedFolders.get(f.fid).planSum = planItem.valueSum;
								} else {
									i.name = f.name;
									parent = foldersTree.getParent(f);
								}
							}
						}
						if(i.name != null) {
							if(parent == null) {
								itemsTree.add(i);
								addedFolders.put(i.id, i);
							} else
								addItemToPlanTree(addedFolders, parent, i);
						}
					}
					pi.close();
				}
				reader.close();
				
				return bdo;
			}
			
			
			
			private void makeHtmlHeader(StringBuilder html) {
				html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
				html.append("<table>");
				html.append("<tr>");

				html.append(fmtHeaderCell("55%", "Группа"));
				html.append(fmtHeaderCell("5%", "&nbsp"));
				html.append(fmtHeaderCell("5%", "План руб."));
//				html.append(fmtHeaderCell("5%", "План бут."));
				html.append(fmtHeaderCell("5%", "Факт руб.."));
//				html.append(fmtHeaderCell("5%", "Факт бут."));
				html.append(fmtHeaderCell("5%", "% руб."));
//				html.append(fmtHeaderCell("5%", "% бут."));
				html.append(fmtHeaderCell("5%", "Аппр. руб."));
//				html.append(fmtHeaderCell("5%", "Аппр. бут."));

				html.append("</tr>");
			}
			
			@SuppressLint("DefaultLocale")
			private void fillFactValues(Date begin, Date end) {
				Delivery data = new Delivery();
				PriceImpl priceImpl = new PriceImpl();
				Price p = priceImpl.getData();
				
				DbReader reader = new DbReader();
				String where = String.format("date >= %d and date <= %d", begin.getTime(), end.getTime());
//				String orderTable = DataObjectInfo.getInstance().getTableName(data.getClass());
				if (reader.select(data, data.getTableName(), where)) {
					do {
						for (DeliveryItem item : data.items) {
							Item planItem = planItems.get(item.id);
							if(planItem != null) {
								planItem.factSum += item.sum;
							}
							
							p.id = item.id;
							if(priceImpl.read()) {
								Folder f = foldersTree.getFolder(p.folderID);
								while(f != null) {
									Item folderItem = addedFolders.get(f.fid);
									if(folderItem != null) {
										while(folderItem != null) {
											folderItem.factSum += item.sum;
											folderItem = folderItem.parent;
										}
										break;
									}
									f = foldersTree.getParent(f);
								}
							}
						}
					}
					while (reader.selectNext(data));
				}

				priceImpl.close();
				reader.close();
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
		if(id == R.id.waitdlg )
			return createWaitDlg();
		return super.onCreateDialog(id);
	}

	private Dialog createWaitDlg() {
		return ProgressDialog.show(this, getString(R.string.wait),
				getString(R.string.waiting));
	}

	private void printNode(Item node, StringBuilder sb, int level) {
		sb.append("<tr>");
		sb.append("<td>");
		sb.append("<font color=\"blue\">");
		insertIntent(level, sb);
		sb.append(node.name);
		sb.append("</font>");

		sb.append("</td>");
		sb.append("<td width='50'>");
		sb.append("&nbsp");
		sb.append("</td>");

		sb.append("<td width='70'>");
		sb.append("<font color=\"blue\">");
		int planSum = node.planSum;
		sb.append(Util.IntToScaleStr(planSum, Consts.SUM_SCALE));
		sb.append("</font>");
		sb.append("</td>");

//		sb.append("<td width='70'>");
//		sb.append("<font color=\"blue\">");
//		int planQty = node.planQty;
//		sb.append(Util.IntToScaleStr(planQty, Consts.QTY_SCALE));
//		sb.append("</font>");
//		sb.append("</td>");

		sb.append("<td>");
		sb.append("<font color=\"blue\">");
		int factSum = node.factSum; 
		sb.append(Util.IntToScaleStr(factSum, Consts.SUM_SCALE));
		sb.append("</font>");
		sb.append("</td>");

//		sb.append("<td>");
//		sb.append("<font color=\"blue\">");
//		int factQty = node.factQty; 
//		sb.append(Util.IntToScaleStr(factQty, Consts.QTY_SCALE));
//		sb.append("</font>");
//		sb.append("</td>");

		sb.append("<td>");
		sb.append("<font color=\"blue\">");
		int percent = planSum == 0 ? 0 : (int) Math.round(((double) factSum
				/ planSum * 100));
		sb.append(percent);
		sb.append("</font>");
		sb.append("</td>");

//		sb.append("<td>");
//		sb.append("<font color=\"blue\">");
//		percent = planQty == 0 ? 0 : (int) Math.round(((double) factQty
//				/ planQty * 100));
//		sb.append(percent);
//		sb.append("</font>");
//		sb.append("</td>");

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
//		int avgQty = factQty / wd;
		wd = workingDay(cal, tomorrow, end);
		int endSum = avgSum * wd;
//		int endQty = avgQty * wd;
		int apprSum = factSum + endSum;
//		int apprQty = factQty + endQty;
		percent = planSum == 0 ? 0 : (int) Math.round(((double) apprSum
				/ planSum * 100));

		sb.append("<td>");
		sb.append("<font color=\"blue\">");
		sb.append(percent);
		sb.append("</font>");
		sb.append("</td>");

//		percent = planQty == 0 ? 0 : (int) Math.round(((double) apprQty
//				/ planQty * 100));
//		sb.append("<td>");
//		sb.append("<font color=\"blue\">");
//		sb.append(percent);
//		sb.append("</font>");
//		sb.append("</td>");

		sb.append("</tr>");
		
		for (Item child : node.childs)
			printNode(child, sb, level + 1);
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

	protected void insertIntent(int level, StringBuilder sb) {
		final int INTENT = 8;
		for (int a = 1; a < level; a++)
			for (int aa = 0; aa < INTENT; aa++)
				sb.append("&nbsp;");
	}

}

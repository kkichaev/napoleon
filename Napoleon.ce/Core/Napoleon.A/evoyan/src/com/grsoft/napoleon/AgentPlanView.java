package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentPlan;
import com.grsoft.dataobjects.AgentPlanItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.impl.FolderImpl;
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
	
	class Item {
		public Map<String, Item> childs = new HashMap<String, Item>();
		public Item parent;
		public String id;
		public String name;
		public int type;
		public int level;
		public int planSum;
		public int planQty;
		public int factSum;
		public int factQty;
	}
	
	final private int PRICE_TYPE = 0;
	final private int FOLDER_TYPE = 1;
	
	private WebView webView;
	private Map<String, Item> addedItems = new HashMap<String, Item>();
	private List<Item> itemsTree = new ArrayList<Item>();
	private FolderTree foldersTree = null;

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
					
					StringBuilder html = new StringBuilder();
					makeHtmlHeader(html);
					
					for (Item root : itemsTree)
						collectFromNode(root, html);
					
					html.append("</table>");
					result = html.toString();
				}

				return result;
			}
			
			@SuppressLint("DefaultLocale")
			private boolean fillAgentPlan(AgentPlan plan) {
				boolean bdo = false;
				
				DbReader reader = new DbReader();
				long now = new Date().getTime();
				String planTableName = DataObjectInfo.getInstance().getTableName(plan.getClass());
				String planWhere = String.format("begin <= %d and end >= %d", now, now);
				if (reader.select(plan, planTableName, planWhere)) {
					foldersTree = new FolderTree();
					foldersTree.load();
					
					String pricesTable = DataObjectInfo.getInstance().getTableName(Price.class);
					
					List<Item> defferedItems = new ArrayList<Item>();
					for (AgentPlanItem planItem : plan.groups) {
						Item newItem = null;
						String priceWhere = String.format("id='%s'", planItem.id);
						
						String folderId = null;
						if (PRICE_TYPE == planItem.type) {
							Price price = new Price();
							reader.select(price, pricesTable, priceWhere);
							newItem = new Item();
							newItem.id = price.id;
							newItem.name = price.name;
							
							FolderImpl folderReader = new FolderImpl();
							folderReader.getData().id = price.folderID;
							folderReader.read();
							folderId = folderReader.getData().getValue("fid");
							folderReader.close();
						} else {
							folderId = planItem.id;
						}
	
						Item folderItem = buildFolderBranch(folderId);
						if (null == newItem) {
							newItem = folderItem;
						} else if (null != folderItem) {
							folderItem.childs.put(newItem.id, newItem);
							newItem.parent = folderItem;
							newItem.level = folderItem.level + 1;
						} else {
							//#NOTE: this is needed for items not in folders placed at the end of the list
							defferedItems.add(newItem);
						}
						
						newItem.planSum = planItem.valueSum;
						newItem.planQty = planItem.valueQty;
						newItem.type = planItem.type;
						
						addedItems.put(newItem.id, newItem);
					}
					
					//after adding all folders placing items that are not in folders to the end of the list
					for (Item defItem : defferedItems) {
						itemsTree.add(defItem);
					}
					bdo = true;
				}
				reader.close();
				return bdo;
			}
			
			private Item buildFolderBranch(String folderId)
			{
				if (addedItems.containsKey(folderId))
					return addedItems.get(folderId);
					
				if (null == foldersTree)
					return null;
				
				Item result = null;
				Folder folder = foldersTree.getFolder(folderId);
				if (null != folder) {
					result = new Item();
					result.id = folder.fid;
					result.name = folder.name;
					result.level = folder.level;
					
					Item currentChild = result;
					Item rootItem = result;
					while (null != (folder = foldersTree.getParent(folder))) {
						Item parentFolder = null;
						if (addedItems.containsKey(folder.fid)) {
							parentFolder = addedItems.get(folder.fid);
							currentChild.parent = parentFolder;
							parentFolder.childs.put(currentChild.id, currentChild);
							break;
						} else {
							parentFolder = new Item();
							parentFolder.id = folder.fid;
							parentFolder.name = folder.name;
							parentFolder.level = folder.level;
							parentFolder.type = FOLDER_TYPE;
							addedItems.put(parentFolder.id, parentFolder);
						}
						currentChild.parent = parentFolder;
						parentFolder.childs.put(currentChild.id, currentChild);
						currentChild = parentFolder;
						rootItem = parentFolder;
					}

					if (null != rootItem)
						itemsTree.add(rootItem);
				}

				return result;
			}
			
			private void makeHtmlHeader(StringBuilder html) {
				html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
				html.append("<table>");
				html.append("<tr>");

				html.append(fmtHeaderCell("55%", "Группа"));
				html.append(fmtHeaderCell("5%", "&nbsp"));
				html.append(fmtHeaderCell("5%", "План руб."));
				html.append(fmtHeaderCell("5%", "План уп"));
				html.append(fmtHeaderCell("5%", "Факт руб.."));
				html.append(fmtHeaderCell("5%", "Факт уп"));
				html.append(fmtHeaderCell("5%", "% руб."));
				html.append(fmtHeaderCell("5%", "% шт"));
				html.append(fmtHeaderCell("5%", "Аппр. руб."));
				html.append(fmtHeaderCell("5%", "Аппр. уп"));

				html.append("</tr>");
			}
			
			@SuppressLint("DefaultLocale")
			private void fillFactValues(Date begin, Date end) {
				Sales data = new Sales();
				PriceImpl priceImpl = new PriceImpl();
				Price p = priceImpl.getData();
				
				DbReader reader = new DbReader();
				String where = String.format("created >= %d and created <= %d", begin.getTime(), end.getTime());
				String orderTable = DataObjectInfo.getInstance().getTableName(data.getClass());
				if (reader.select(data, orderTable, where)) {
					do {
						for (OrderItem item : data.items) {
							p.id = item.id;
							priceImpl.read();
							
							String itemId = item.id;
							if (!addedItems.containsKey(itemId)) {
								FolderImpl folderReader = new FolderImpl();
								folderReader.getData().id = priceImpl.getData().folderID;
								folderReader.read();
								itemId = folderReader.getData().getValue("fid");
								folderReader.close();
							}
							Item foundItem = addedItems.get(itemId);
							int qty = (int)((long)item.qty * Consts.QTY_SCALE/ p.qtyInPack);
							int sum = ((SalesItem)item).sum;
							bubbleUpValues(foundItem, qty, sum);
						}
					}
					while (reader.selectNext(data));
				}

				priceImpl.close();
				reader.close();
			}
			
			private void bubbleUpValues(Item item, int qty, int sum)
			{
				if (null == item)
					return;
				
				item.factQty += qty;
				item.factSum += sum;
				
				bubbleUpValues(item.parent, qty, sum);
			}

			protected void onPreExecute() { showDialog(R.id.waitdlg); };

			protected void onPostExecute(String result) {
				webView.loadDataWithBaseURL(null, result, "text/html", null, null);
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

	private void collectFromNode(Item node, StringBuilder sb) {
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
		int planSum = node.planSum;
		sb.append(Util.IntToScaleStr(planSum, Consts.SUM_SCALE));
		sb.append("</font>");
		sb.append("</td>");

		sb.append("<td width='70'>");
		sb.append("<font color=\"blue\">");
		int planQty = node.planQty;
		sb.append(Util.IntToScaleStr(planQty, Consts.QTY_SCALE));
		sb.append("</font>");
		sb.append("</td>");

		sb.append("<td>");
		sb.append("<font color=\"blue\">");
		int factSum = node.factSum; 
		sb.append(Util.IntToScaleStr(factSum, Consts.SUM_SCALE));
		sb.append("</font>");
		sb.append("</td>");

		sb.append("<td>");
		sb.append("<font color=\"blue\">");
		int factQty = node.factQty; 
		sb.append(Util.IntToScaleStr(factQty, Consts.QTY_SCALE));
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
		percent = planQty == 0 ? 0 : (int) Math.round(((double) factQty
				/ planQty * 100));
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
		int avgQty = factQty / wd;
		wd = workingDay(cal, tomorrow, end);
		int endSum = avgSum * wd;
		int endQty = avgQty * wd;
		int apprSum = factSum + endSum;
		int apprQty = factQty + endQty;
		percent = planSum == 0 ? 0 : (int) Math.round(((double) apprSum
				/ planSum * 100));

		sb.append("<td>");
		sb.append("<font color=\"blue\">");
		sb.append(percent);
		sb.append("</font>");
		sb.append("</td>");

		percent = planQty == 0 ? 0 : (int) Math.round(((double) apprQty
				/ planQty * 100));
		sb.append("<td>");
		sb.append("<font color=\"blue\">");
		sb.append(percent);
		sb.append("</font>");
		sb.append("</td>");

		sb.append("</tr>");
		
		for (Item child : node.childs.values())
			collectFromNode(child, sb);
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

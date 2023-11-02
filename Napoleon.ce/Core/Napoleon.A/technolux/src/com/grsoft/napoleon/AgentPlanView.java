package com.grsoft.napoleon;

import java.util.ArrayList;
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
import android.view.View;
import android.webkit.WebView;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.AgentPlan;
import com.grsoft.dataobjects.AgentPlanItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseManager;

@SuppressLint("UseSparseArrays")
public class AgentPlanView extends Activity{
	private WebView webView;
	private FoldersAdapter adapter;
	private Map<String, Integer> itemsPlan = new HashMap<String, Integer>();
	private Map<String, Integer> groupsPlan = new HashMap<String, Integer>();
	private Map<Integer, String> idfid = new HashMap<Integer, String>(); 
	private Map<String, Integer> itemsQty = new HashMap<String, Integer>(); 
	private Map<Integer, Integer> groupsQty = new HashMap<Integer, Integer>();
	
	public static void open(Context context) {
		Intent intent = new Intent(context, AgentPlanView.class);
		context.startActivity(intent);
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agentplanview);
		
		webView = (WebView) findViewById(R.id.web); 
		
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);
		
		adapter = new FoldersAdapter(new WarehouseManager() {
			
			@Override
			public boolean useInterlaceBackground() { return false;	}
			
			@Override
			public void sortingPriceList(ArrayList<TreeNode> childs) {}
			
			@Override
			public boolean isPriceExpand() { return false; }
			
			@Override
			public String getString(int price) { return null; }
			
			@Override
			public View getPriceView(PriceTreeNode node, View convertView) { return null; }
			
			@Override
			public View getFolderView(FolderTreeNode node, View convertView) { return null;	}
			
			@Override
			public void editItem(long rowid) { }
			
			@Override
			public void applySearchFilter(String value) { }
			
			@Override
			public void afterBuildSet() {}
		});
		
		
		new AsyncTask<Void, Void, String>(){

			@Override
			protected String doInBackground(Void... params) {
				String result = null;
				
				AgentPlan data = new AgentPlan();
				DbReader reader = new DbReader();
				
				long now = new Date().getTime();
				StringBuilder where = new StringBuilder();
				where.append("begin <= ").append(now).append(" and end >= ").append(now);
				
				boolean bdo = reader.select(data, DataObjectInfo.getInstance().getTableName(data.getClass()), where.toString());
				
				if(bdo){
					collectOrdersWeight(data.begin, data.end);
					DbWriter.checkDBTable(Folder.class);
					Cursor c = null;
					
					try{
						c = DataBaseManager.getDataBase().query(DataObjectInfo.getInstance().getTableName(Folder.class), 
								new String[]{"id", "fid"}, null, null, null, null, null);
						idfid.clear();
						while(c.moveToNext())
							idfid.put(c.getInt(c.getColumnIndex("id")), c.getString(c.getColumnIndex("fid")));
						
					}finally{
						if(c != null)
							c.close();
					}
					
					itemsPlan.clear();
					for(AgentPlanItem item : data.items)
						itemsPlan.put(item.id, item.value);
					
					groupsPlan.clear();
					for(AgentPlanItem item : data.groups)
						groupsPlan.put(item.id, item.value);
					
					FoldersAdapter.resetCache();
					adapter.buldProcess(this);
					
					StringBuilder html = new StringBuilder();
					html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
					html.append("<table>");
					html.append("<tr>");
					html.append("<td>");
					html.append("Группа/Товар");
					html.append("</td>");
					html.append("<td width='50'>");
					html.append("&nbsp");
					html.append("</td>");
					html.append("<td width='50'>");
					html.append("План");
					html.append("</td>");
					html.append("<td width='70'>");
					html.append("Факт");
					html.append("</td>");
					html.append("<td>");
					html.append("%");
					html.append("</td>");
					html.append("</tr>");
					
					for(int i = 0; i < adapter.getCount(); i++){
						TreeNode node = (TreeNode) adapter.getItem(i);
						
						if(node instanceof FolderTreeNode){
							collectFromNode(((FolderTreeNode)node), 0, html);
						}
					}
					html.append("</table>");
					result = html.toString();
				}
				
				reader.close();
				
				return result;
			}
			
			private void collectOrdersWeight(Date begin, Date end) {
				Order data = new Order();
				DbReader reader = new DbReader();
				StringBuilder where = new StringBuilder();
				where.append("created >= ").append(begin.getTime()).append(" and created <= " ).append(end.getTime());
				PriceImpl priceImpl = new PriceImpl();
				boolean bdo = reader.select(data, DataObjectInfo.getInstance().getTableName(data.getClass()), where.toString());
				
				while(bdo){
					for(OrderItem item : data.items){
						priceImpl.getData().id = item.id;
						priceImpl.read();
						
						int w = 0;
						
						if(itemsQty.containsKey(item.id))
							w = itemsQty.get(item.id);
						
						w += item.qty;
						
						itemsQty.put(item.id, w);
						
						int gw = 0;
						int fid = priceImpl.getData().folderID;
						
						if(groupsQty.containsKey(fid))
							gw = groupsQty.get(fid);
						
						gw += w;
						
						groupsQty.put(fid, gw);
					}
					
					bdo = reader.selectNext(data);
				}
				
				priceImpl.close();
				reader.close();
			}

			protected void onPreExecute() {
				showDialog(R.id.waitdlg);
			};
			
			protected void onPostExecute(String result) {
				webView.loadDataWithBaseURL(null, result, "text/html", null, null);
				dismissDialog(R.id.waitdlg);
			};
			
		}.execute((Void[])null);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		FoldersAdapter.resetCache();
		
		if(adapter != null)
			adapter.close();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case R.id.waitdlg:
			return createWaitDlg();
		default: 
			return super.onCreateDialog(id);
		}
	}


	private Dialog createWaitDlg() {
		return ProgressDialog.show(this, getString(R.string.wait), getString(R.string.waiting));
	}
	
	
	private void collectFromNode(FolderTreeNode node, int level, StringBuilder sb){
		node.open();
		
		insertIntent(level, sb);
		
		sb.append("<tr>");
		
		sb.append("<td>");
		sb.append("<font color=\"blue\">");
		sb.append(node.name);
		sb.append("</font>");
		sb.append("</td>");
		sb.append("<td width='50'>");
		sb.append("&nbsp");
		sb.append("</td>");
		sb.append("<td width='70'>");
		sb.append("<font color=\"blue\">");
		int plan = getGroupPlan(node.id);
		sb.append(Util.IntToScaleStr(plan, Consts.QTY_SCALE));
		sb.append("</font>");
		sb.append("</td>");
		sb.append("<td>");
		sb.append("<font color=\"blue\">");
		int fact = getGroupFact(node.id);
		sb.append(Util.IntToScaleStr(fact, Consts.QTY_SCALE));
		sb.append("</font>");
		sb.append("</td>");
		sb.append("<td>");
		sb.append("<font color=\"blue\">");
		int percent = plan == 0 ? 0 : (int)((double)fact / plan * 100);
		sb.append(percent);
		sb.append("</font>");
		sb.append("</td>");
		
		sb.append("</tr>");
		
		for(int i = 0; i < node.getChildsCount(); i++){
			TreeNode c = node.getChild(i);
			
			if(c instanceof FolderTreeNode)
				collectFromNode((FolderTreeNode) c, level + 1, sb);
			else if(c instanceof PriceTreeNode){
				sb.append("<tr>");
				sb.append("<td>");
				PriceTreeNode ptn = (PriceTreeNode)c;
				insertIntent(level + 1, sb);
				sb.append(ptn.toString()).append("<br>");
				sb.append("</td>");
				sb.append("<td width='50'>");
				sb.append("&nbsp");
				sb.append("</td>");
				sb.append("<td>");
				plan = getItemPlan(ptn.getId());
				sb.append(Util.IntToScaleStr(plan, Consts.QTY_SCALE));
				sb.append("</td>");
				sb.append("<td>");
				fact = getItemFact(ptn.getId());
				sb.append(Util.IntToScaleStr(fact, Consts.QTY_SCALE));
				sb.append("</td>");
				percent = plan == 0 ? 0 : (int)((double)fact / plan * 100);
				sb.append("<td>");
				sb.append(percent);
				sb.append("</td>");
				
				sb.append("</tr>");
			}
		}
	}


	private int getItemFact(String id) {
		int result = 0;
		
		if(itemsQty.containsKey(id))
			result = itemsQty.get(id);;
		
		return result;
	}


	public int getItemPlan(String id) {
		int result = 0;
		
		if(itemsPlan.containsKey(id))
			result = itemsPlan.get(id);
		
		return result;
	}
	
	private int getGroupFact(int id) {
		int result = 0;
		
		if(groupsQty.containsKey(id))
			result = groupsQty.get(id); 
		
		return result;
	}


	private int getGroupPlan(int id) {
		int result = 0;
		
		if(idfid.containsKey(id)){
			String fid = idfid.get(id);
			
			if(groupsPlan.containsKey(fid))
				result = groupsPlan.get(fid); 
		}
		
		return result;
	}


	protected void insertIntent(int level, StringBuilder sb) {
		final int INTENT = 4;
		for(int a = 0; a < level; a++)
			for(int aa = 0; aa < INTENT; aa++)
				sb.append("&nbsp;");
	}

}

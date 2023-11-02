package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.webkit.WebView;
import android.widget.TextView;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.PurchaseItem;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PurchaseImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixBaseAdapter;

public class PurchaseList extends WarehouseNew {
	public static Class<? extends Activity> activity = PurchaseList.class;
	private OrgImpl orgImpl = new OrgImpl();
	private PurchaseImpl purchaseIml = new PurchaseImpl();
	private ArrayList<MatrixItem> items;
	private WebView webView;
	
	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, activity);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.purchase);
		webView = (WebView) findViewById(R.id.wv);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);
		
		orgImpl.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR,
				ExtrasConst.INVALID_ID));
		orgImpl.close();

		TextView tv = (TextView) findViewById(R.id.tvOrgInfo);
		tv.setText(Html.fromHtml(orgInfo(orgImpl.getData())));

		purchaseIml.getData().id = orgImpl.getData().id;
		purchaseIml.read();
		purchaseIml.close();

		items = new ArrayList<MatrixItem>();
		if (purchaseIml.getData().items != null)
			for (PurchaseItem i : purchaseIml.getData().items) {
				MatrixItem item = new MatrixItem();
				item.id = i.id;
				items.add(item);
			}
		
		FoldersAdapter.resetCache();
		adapter.setOnChangeListener(null);
		adapter = new PurchaseMatrix(this, items);
		adapter.setOnChangeListener(adapterOnChangeListener);
		adapter.buildSet();
	};
	
	private String orgInfo(Org o) {
		String ret = o.name;
		if(Features.SHOW_ORG_ADDRESS && o.address.length() > 0 ) {
			ret += "<br><i>" + o.address + "</i>";
		}
		return ret; 
	}

	@Override
	protected void onStop() {
		super.onStop();
		FoldersAdapter.resetCache();
		
		if(adapter != null)
			adapter.close();
	}
	
	@Override
	protected void adapterInit() {
		 
	}
	
	@Override
	protected void fireBuildSet() {
		StringBuilder html = new StringBuilder();
		html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
		for(int i = 0; i < adapter.getCount(); i++){
			TreeNode node = (TreeNode) adapter.getItem(i);
			
			if(node instanceof FolderTreeNode){
				collectFromNode(((FolderTreeNode)node), 0, html);
			}
		}
		
		webView.loadDataWithBaseURL(null, html.toString(), "text/html", null, null);
	}
	
	private void collectFromNode(FolderTreeNode node, int level, StringBuilder sb){
		node.open();
		
		insertIntent(level, sb);
		
		sb.append("<font color=\"blue\">");
		sb.append(node.name).append("<br>");
		sb.append("</font>");
		
		for(int i = 0; i < node.getChildsCount(); i++){
			TreeNode c = node.getChild(i);
			
			if(c instanceof FolderTreeNode)
				collectFromNode((FolderTreeNode) c, level + 1, sb);
			else if(c instanceof PriceTreeNode){
				PriceTreeNode ptn = (PriceTreeNode)c;
				insertIntent(level + 1, sb);
				sb.append(ptn.toString()).append("<br>");
			}
		}
	}

	protected void insertIntent(int level, StringBuilder sb) {
		final int INTENT = 4;
		for(int a = 0; a < level; a++)
			for(int aa = 0; aa < INTENT; aa++)
				sb.append("&nbsp;");
	}
	
	class PurchaseMatrix extends MatrixBaseAdapter{
		private List<MatrixItem> items;
		public PurchaseMatrix(WarehouseNew warehouse, List<MatrixItem> items) {
			super(warehouse);
			this.items = items;
		}

		@Override
		protected List<? extends MatrixItem> getMatrixItems() {
			return items;
		}
	}
}


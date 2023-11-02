package com.grsoft.view;
import com.grsoft.aceteam.R;

import java.util.List;

import android.app.Activity;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.aceteam.R;
import com.grsoft.util.WarehouseAdapter;

public class FolderPath {

	Activity parent;
	HorizontalScrollView scrollView;
	LinearLayout llPath;
	TextView tvHome;
	WarehouseAdapter adapter;
	boolean enabled = true;

	public FolderPath(HorizontalScrollView scrollView, int homeId, int layoutId, Activity parent, WarehouseAdapter adapter) {		
		this.scrollView = scrollView;
		this.parent = parent;
		this.adapter = adapter;
	
		llPath = (LinearLayout)parent.findViewById(layoutId);		
		tvHome = (TextView)parent.findViewById(homeId);
		if(tvHome != null)
			tvHome.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					if(enabled)
						FolderPath.this.adapter.setFolder(-1); 
				}
			});		
	}
	
	public void refreshPath(WarehouseAdapter adapter) {
		if(tvHome == null || scrollView == null || llPath == null)
			return;
		
		this.adapter = adapter;
		llPath.removeAllViews();
		
		List<TreeNode> path = adapter.path();
		int sw = parent.getWindowManager().getDefaultDisplay().getWidth();
		int ps =  path.size();
		int totalWidth = tvHome.getWidth();
		tvHome.setBackgroundResource(R.drawable.price_path);
		scrollView.setBackgroundResource(R.drawable.price_path);
		
		boolean s = false;
		for(int i = 0; i < ps; i++){
			TreeNode node = path.get(i);
			if(node.getParent() != null){
				if(!s){
					scrollView.setBackgroundResource(R.drawable.price_path_top);
					s = true;
				}
				
				TextView tvEx = new TextView(parent);
				tvEx.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
				tvEx.setBackgroundResource(i < ps - 1 ? R.drawable.price_path_ex : R.drawable.price_path_ex_top);
				llPath.addView(tvEx);
				
				TextView tvNode = new TextView(parent);
				
				String text = node.toString();
				int textSize = (int) tvNode.getPaint().measureText(text);
				int width = sw / 3;
				
				if(i == ps - 1)
					width = sw - totalWidth;
					
				if(i < ps - 1  || totalWidth + width >= sw)
					tvNode.setMaxWidth(width);
				
				totalWidth += 20 + textSize;
				
				tvNode.setText(text);
				tvNode.setLines(1);
				tvNode.setEllipsize(TruncateAt.END);
				tvNode.setHorizontallyScrolling(true);
				tvNode.setGravity(Gravity.CENTER);
				tvNode.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
				tvNode.setPadding(10, 0, 10, 0);
				tvNode.setClickable(true);
				tvNode.setTag(((FolderTreeNode)node).id);
				//tvNode.setTextColor(parent.getResources().getColorStateList(R.drawable.price_path_selector));
				tvNode.setTextSize(16);
				
				tvNode.setBackgroundResource(i < ps - 1 ? R.drawable.price_path : R.drawable.price_path_top);
				
				tvNode.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(enabled)
							FolderPath.this.adapter.setFolder((Integer) v.getTag());
					}
				});
				
				llPath.addView(tvNode);
			}
			
			scrollView.post(new Runnable() {
				public void run() {
					scrollView.scrollTo(scrollView.getWidth(), 0);
				}
			});
		}
	}
	
	public void setEnabled(boolean val){
		enabled = val;
	}
}

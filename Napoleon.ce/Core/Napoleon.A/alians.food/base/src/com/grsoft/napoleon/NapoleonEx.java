package com.grsoft.napoleon;

import java.util.ArrayList;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrgHelper;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.WSAddOrderDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.MenuHandler;

public class NapoleonEx extends Napoleon {
	private static final int BLOCK_DAY_CNT = 1;
	TaskInfoHelper taskInfo = new TaskInfoHelper();
	
	@Override
	protected void drawOrg(OrgImpl oi, View view) {
		super.drawOrg(oi, view);
		
		if (DocType.getCurDoc() == TaskDoneDoc.instance()) {
			int color = getResources().getColor(R.color.black);
			String orgid = oi.getData().id;
			switch(taskInfo.getStatus(orgid)) {
			case EXPIRED:
				color = getResources().getColor(R.color.red);
				break;
			case PENDING:
				color = getResources().getColor(R.color.blue);
				break;
			case DONE:
				color = getResources().getColor(R.color.green);
				break;
			case MULTI_DONE:
				color = getResources().getColor(R.color.orange);
				break;
			default:
				color = getResources().getColor(R.color.black);	
			}
			
			((TextView)view.findViewById(R.id.tvOrgName)).setTextColor(color);
		}else if(OrgHelper.getUnpayDays(oi.getData().id) >= BLOCK_DAY_CNT )
			((TextView)view.findViewById(R.id.tvOrgName)).setTextColor(Color.RED);
	}
	
	@Override
	protected ArrayList<MenuHandler> createDocMenuList() {
		ArrayList<MenuHandler> ret = super.createDocMenuList(); 
		ret.add(new MenuHandler(getString(R.string.wsorder_title), new Runnable() {
			@Override public void run() {
				DocType.setCurDoc(WSOrderDoc.instance());
				WSOrderList.open(NapoleonEx.this);
			}
		}));
		
		ret.add(new MenuHandler(getString(R.string.add_ws_order_title), new Runnable() {
			@Override public void run() {
				DocType.setCurDoc(WSAddOrderDoc.instance());
				WSOrderList.open(NapoleonEx.this);
			}
		}));
		
		return ret;
	}
	
	@Override
	protected void onResume() {
		if(DocType.getCurDoc() == WSOrderDoc.instance() ||
				DocType.getCurDoc() == WSAddOrderDoc.instance())
			DocType.setCurDoc(SalesDoc.instance());
		
		taskInfo.refresh();
		super.onResume();
	}
	
	@Override
	protected DocFilterOnClickListener createDocFilter() {
		return new DocFilterOnClickListener(this){
			@Override
			protected void initData(boolean creatableFilter) {
				super.initData(creatableFilter);
				data.remove(WSOrderDoc.instance());
			}
		};
	}
}

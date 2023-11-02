package com.grsoft.napoleon;

import java.util.Date;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnCommit;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.ReturnImplBase;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ReturnDetailEx extends ReturnDetail {
	@Override protected void setContentView() { setContentView(R.layout.return_detail); }
	
	public static void open(Context ctx, ReturnImplBase<? extends Return> doc) {
		ReturnCommit rc = ReturnCommit.get(doc.getData().created);
		
		Class<? extends Activity> ac = activity;
		if(rc != null)
			ac = ReturnCommitDetail.class;
		
		Intent i = new Intent(ctx, ac);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		ctx.startActivity(i);
	}

	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new ReturnAdapter());
	}
	
	@Override
	protected void editItem(OrderItem orderItem) {
		ReturnPriceCount.open(this, orderItem, (ReturnImplEx)doc);
	}
	
	@Override
	protected void deleteItem(OrderItem orderItem) {
		doc.getData().items.remove(orderItem);
		doc.write();
		((BaseAdapter)lvItems.getAdapter()).notifyDataSetChanged();
		updateTotalSum();
	}
	
	class ReturnAdapter extends OrderItemsAdapter {
		@Override int getResourceID() { return R.layout.returndetail_list_row; }
		
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item, int pos) {
			super.drawInternal(view, name, color, item, pos);
			
			Date bbf = ((ReturnItemEx)item).bestBefore;
			TextView tv = (TextView)view.findViewById(R.id.tvBestBefore);
			tv.setText(bbf == null ? "" : Util.simpleDateFormat.format(bbf));
		}
	}
}

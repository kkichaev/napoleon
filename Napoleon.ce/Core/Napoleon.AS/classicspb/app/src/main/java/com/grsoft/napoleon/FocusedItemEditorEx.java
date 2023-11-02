package com.grsoft.napoleon;

import java.util.HashSet;

import com.grsoft.dataobjects.OrderFocusedFolder;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;

public class FocusedItemEditorEx extends FocusItemEditor {

	@Override protected ItemsAdapter createItemsAdapter() { return new ItemsAdapterEx(); }
	
	class ItemsAdapterEx extends ItemsAdapter {
		@Override
		public void refresh() {
			items.clear();
			
			PriceImpl pi = new PriceImpl();
			Price p = pi.getData();
			HashSet<String> flist = ((OrderImplEx)order).getFocusedItems();
			
			for(String pid : flist) {
				p.id = pid;
				if(pi.read() && p.qty > 0)
					items.add(new FocusItem(p.name, pid, pi.getRowid()));
			}
			
			for(OrderItem oi : order.getData().items) {
				p.id = oi.id;
				pi.read();
				items.markSold(p);
			}
			pi.close();
			
			for(OrderFocusedFolder ff : order.getData().focusedFolders) {
				items.assignRemark(ff);
			}

			notifyDataSetChanged();
		}
		
//		@Override
//		public View getView(int pos, View view, ViewGroup arg2) {
//			View v = super.getView(pos, view, arg2);
//			v.findViewById(R.id.ivFolder).setVisibility(View.GONE);
//
//			FocusGroupItem item = (FocusGroupItem)getItem(pos);
//			if( item == null )
//				return v;
//			TextView tv = (TextView) v.findViewById(R.id.tvName);
//			tv.setTextColor((item.getStateImage() == R.drawable.focus_error) ? Color.MAGENTA : Color.BLACK);
//
//			return v;
//		}
	}
}

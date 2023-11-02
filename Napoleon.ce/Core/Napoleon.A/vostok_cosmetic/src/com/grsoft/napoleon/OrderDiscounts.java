package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.FolderDiscount;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.MessageBox;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class OrderDiscounts extends BaseActivity {
	
	int maxDscDelta = 10000;

	OrderImplBase<? extends Order> doc = null;
	OrgEx org;
	Adapter adapter;
	
	public static void open(Context ctx, OrderImplBase<? extends Order> doc) {
		Intent i = new Intent(ctx, OrderDiscounts.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		ctx.startActivity(i);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orderdiscounts);
		
		doc = (OrderImplBase<? extends Order>) OrderDoc.instance().create();
		Bundle b = (savedInstanceState != null) ? savedInstanceState : getIntent().getExtras();
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID));
		
		OrgImpl oi = new OrgImpl();
		org = (OrgEx) oi.getData();
		org.id = doc.getId();
		oi.read();
		oi.close();
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				adapter.onItemSelected(position);
			}
		});
		
		ConfigImpl ci = new ConfigImpl();
		Config c = ci.getData();
		c.key = "МаксимальнаяСкидка";
		if( ci.read() )
			maxDscDelta = Integer.parseInt(c.value) * Consts.SUM_SCALE;
		ci.close();

		adapter = new Adapter();
		lv.setAdapter(adapter);
		
		updateTotalSum(doc.sum(), 0);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}
	
	@Override
	protected void onStop() {
		super.onStop();		
		doc.close();
	}

	class Adapter extends BaseAdapter {
		
		ArrayList<Item> items;
		
		public Adapter() {
			loadItems();
		}
		
		void loadItems() {
			FolderTree folders = new FolderTree();

			folders.load();
			if( folders.size() == 0 )
				return;

			HashMap<String, Integer> discounts = loadDiscounts();			
			
			items = makeTree(folders, discounts);
			
			loadOrderItems();			
			removeEmpty();
		}

		private void removeEmpty() {
			do {
				ArrayList<Item> remove = new ArrayList<Item>(); 
				for(Item i: items) {
					if(i.items.size() > 0 || i.childs.size() > 0)
						continue;

					remove.add(i);
					Item parent = i.parent;
					if( parent != null )
						parent.childs.remove(i);
				}
				
				if( remove.size() == 0 )
					break;
				items.removeAll(remove);
			} while(true);
		}

		private void loadOrderItems() {
			int sumType = doc.getSumType();
			PriceImpl pi = new PriceImpl();
			Price p = pi.getData();
			for(OrderItem oi : doc.getData().items) {
				p.id = oi.id;
				if( pi.read() ) {
					for(Item i : items) {
						if( i.folder.id == p.folderID ) {
							OrderItemEx2 oi2 = new OrderItemEx2();
							oi2.item = (OrderItemEx) oi;
							oi2.priceCost = ((p.cost.size() > sumType && sumType >= 0) ? p.cost.get(sumType).cost : 0);
							
							i.discount = oi2.item.discount;
							Item parent = i.parent;
							while( parent != null ) {
								if(parent.discount == 0)
									parent.discount = i.discount;
								parent = parent.parent;
							}
							i.items.add(oi2);
							break;
						}
					}
				}
			}
			pi.close();
		}

		private ArrayList<Item> makeTree(ArrayList<Folder> folders, HashMap<String, Integer> discounts) {
			ArrayList<Item> ret = new ArrayList<Item>();
			
			int level = 0;
			Item curParent = null;
			for(Folder f : folders) {
				Item ci = new Item();
				ci.folder = f;
				
				Integer dsc = discounts.get(f.fid);
				ci.maxDiscount = (dsc == null) ? 0 : dsc;
				
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

				String name = "";
				for( int li=0; li<level; li++ ) {
					name += "   ";
				}
				ci.level = name;
								
				ci.parent = curParent;
				if( curParent != null )
					curParent.childs.add(ci);
				curParent = ci;
				ret.add(ci);
			}
			
			return ret;
		}
		
		HashMap<String, Integer> loadDiscounts() {
			HashMap<String, Integer> d = new HashMap<String, Integer>();

			DbReader r = new DbReader();
			String table = DataObjectInfo.getInstance().getTableName(FolderDiscount.class);
			FolderDiscount data = new FolderDiscount();
			boolean bdo = r.select(data, table, null);
			while (bdo) {
				d.put(data.fid, data.discount);
				bdo = r.selectNext(data);
			}
			r.close();
			
			return d;
		}
		
		public void onItemSelected(int pos) {
			final Item i = (Item) getItem(pos);
			if( i != null ) {
				InputNumberDlg.open(OrderDiscounts.this, new InputNumber() {
					
					@Override public int getValue() { return -i.discount; }
					
					@Override
					public void applayInput(int value, Object... params) {
						if( value <= i.maxDiscount && i.belowDiscountLimit(value) ) {
							i.setDiscount(-value);
							
							doc.write();
							updateTotalSum(doc.sum(), 0);
							notifyDataSetChanged();
						} else
							MessageBox.show(OrderDiscounts.this, "Ошибка", "Скидка выше максимальной");
						
					}
				}, Consts.SUM_SCALE, false, "Введите скидку");
			}
		}

		@Override public int getCount() { return items.size(); }

		@Override public Object getItem(int position) { return (position < items.size()) ? items.get(position) : null; }

		@Override public long getItemId(int position) { return position; }

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if( view == null )
				view = View.inflate(OrderDiscounts.this, R.layout.orderdiscount_row, null);
			
			Item item = (Item) getItem(position);
			if( item == null )
				return view;
			
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvOrder);
			tv.setText(item.level);

			tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(item.folder.name);
			
			tv = (TextView)view.findViewById(R.id.tvDiscount);
			tv.setText(Util.IntToScaleStr(-item.discount, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " %");
			return view;
		}
		
	}

	class Item {
		public ArrayList<OrderItemEx2> items = new ArrayList<OrderItemEx2>();
		
		public ArrayList<Item> childs = new ArrayList<Item>();
		public Item parent;
		
		public int discount;
		public int maxDiscount;
		public Folder folder;
		public String level;
		
		public void setDiscount(int newDiscount) {
			if( -newDiscount <= maxDiscount ) {
				discount = newDiscount;
				for(OrderItemEx2 oi : items)
					oi.updateCost(newDiscount);
			}
			
			for(Item ci : childs)
				ci.setDiscount(newDiscount);
		}

		public boolean belowDiscountLimit(int value) {
			if( maxDscDelta == 0 )
				return false;
			
			Integer dsc = CostStrategyEx.findDiscount(org, folder.fid);
			if( dsc == null )
				dsc = org.discount;
			
			return  ((value - dsc) <= maxDscDelta);
		}
	}
}

class OrderItemEx2 {
	OrderItemEx item;
	public int priceCost;
	
	public void updateCost(int newDiscount) {
		item.cost = priceCost + (int)(((long)priceCost * newDiscount - Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
		item.discount = newDiscount;
	}
}

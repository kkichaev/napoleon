package com.grsoft.napoleon;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.FocusedGroupItem;
import com.grsoft.dataobjects.FocusedItemsItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderFocusedFolder;
import com.grsoft.dataobjects.OrderFocusedItem;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.FocusedGroupImpl;
import com.grsoft.dataobjects.impl.FocusedItemsImpl;
import com.grsoft.dataobjects.impl.FolderImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.view.RegDurationActivity;

public class FocusItemEditor extends RegDurationActivity {
	
	protected static final int OPEN_ITEM = 0;
	private static final int COMMENT = 1;
	
	public static Class<? extends FocusItemEditor> activity = FocusItemEditor.class;

	long rid = ExtrasConst.INVALID_ID;
	
	protected OrderImplBase<?> order = null;
	FocusedGroupImpl focusedFolders = new FocusedGroupImpl();
	FocusedItemsImpl focusedItems = new FocusedItemsImpl();
	
	ItemsAdapter itemsAdapter;
	
	LinesOnClickListener loc;
	EditText commentEditor;
	private int itemPos;
	
	protected ItemsAdapter createItemsAdapter() { return new ItemsAdapter(); }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(getLayout());
		
		order = (OrderImplBase<?>)DocType.getCurDoc().create();
		
		itemsAdapter = createItemsAdapter();
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR);

		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(itemsAdapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				FocusGroupItem fi = (FocusGroupItem)itemsAdapter.getItem(position);
				fi.open(FocusItemEditor.this, order);
			}			
		});
		registerForContextMenu(lv);
		
		loc = new LinesOnClickListener(lv, (ImageView)findViewById(R.id.btnLines), this);
	}

	protected int getLayout() {
		return R.layout.focus_editor;
	}
		
	void setItemText(String text, int pos) {		
		FocusGroupItem item = (FocusGroupItem)itemsAdapter.getItem(itemPos);
		
		item.setComment(order.getData(), text);

		order.write();
		itemsAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if( id == COMMENT ) {
			AlertDialog ad = (AlertDialog)dialog;
			FocusGroupItem item = (FocusGroupItem)itemsAdapter.getItem(itemPos);
			
			if(item != null){
				ad.setMessage(item.name);
				commentEditor.setText(item.getComment());
			}
		} else
			super.onPrepareDialog(id, dialog);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == COMMENT ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle(R.string.note);
			b.setMessage("");
			
			commentEditor = new EditText(this);
			b.setView(commentEditor);
			if( !order.isEditable() ) {
				commentEditor.setEnabled(false);
				b.setNegativeButton(R.string.close, null);
			} else {
				b.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						setItemText(commentEditor.getText().toString(), itemPos);
					}
				});
				
				b.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						setItemText("", itemPos);
					}
				});
			}
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		order.close();
		focusedFolders.close();
		focusedItems.close();
	}

	@Override
	protected void onResume() {
		super.onResume();

		order.read(rid, false);
		
		focusedFolders.getData().id = order.getId();
		if ( !focusedFolders.read() ) {
			focusedFolders.getData().id = "";
			focusedFolders.read();
		}
		
		focusedItems.getData().id = order.getId();
		if( !focusedItems.read() ) {
			focusedItems.getData().id = "";
			focusedItems.read();
		}
		
		itemsAdapter.refresh();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		if( v.getId() == R.id.lvItems ) {
			menu.setHeaderTitle(R.string.action);
			menu.add(Menu.NONE, OPEN_ITEM, 0, R.string.open);
			menu.add(Menu.NONE, COMMENT, 1, R.string.comment);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo i = (AdapterContextMenuInfo) item.getMenuInfo();
		FocusGroupItem fi = (FocusGroupItem)itemsAdapter.getItem(i.position);
		if( fi != null ) {
			switch(item.getItemId()) {
			case OPEN_ITEM:
				fi.open(this, order);
				break;
			case COMMENT:
				itemPos = i.position;
				showDialog(COMMENT);
				break;
			}
		}
		return super.onContextItemSelected(item);
	}
	
	public static void open(Context context, OrderImplBase<?> order) {
		Intent i = new Intent(context, activity);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
		context.startActivity(i);
	}
	
	class ItemsAdapter extends BaseAdapter {
		
		ItemList items = new ItemList();
		
		public ItemsAdapter() {
		}
		
		public void refresh() {
			FolderImpl folder = new FolderImpl();
			PriceImpl p = new PriceImpl();
			Price prc = p.getData();
			
			items.clear();
			for( FocusedGroupItem fgi : focusedFolders.getItems() )
				items.add(new FocusGroupItem(folder, fgi));			
			
			if( focusedItems.getData().items != null ) {
				for(FocusedItemsItem fii : focusedItems.getData().items) {
					prc.id = fii.id;
					if( p.read() && order.getItemValue(prc) > 0 ) 
						items.add(new FocusItem(prc.name, prc.id, p.getRowid()));
				}
			}
			
			// отметим все проданные товары
			for(OrderItem oi : order.getData().items) {
				prc.id = oi.id;
				p.read();

				items.markSold(p.getData().folderID);
				items.markSold(prc);
			}
			p.close();
			folder.close();
			
			for(OrderFocusedFolder ff : order.getData().focusedFolders)
				items.assignRemark(ff);
			
			for(OrderFocusedItem fi : order.getData().focusedItems)
				items.assignRemark(fi);
			

			notifyDataSetChanged();
		}

		@Override public int getCount() { return items.size(); }

		@Override public Object getItem(int arg0) { return (arg0 < items.size()) ? items.get(arg0) : null; }

		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			FocusGroupItem item = (FocusGroupItem)getItem(pos);
			if( item == null )
				return null;
			
			if( view == null )
				view = View.inflate(FocusItemEditor.this, getItemLayout(), null);
			
			TextView tv = (TextView) view.findViewById(R.id.tvName);
			tv.setText(item.name);
			loc.getController().prepareTextView(tv);
			
			ImageView iv  = (ImageView)view.findViewById(R.id.ivImage);
			iv.setImageResource(item.getStateImage());
			
			view.findViewById(R.id.ivFolder).setVisibility(item.isFolder() ? View.VISIBLE : View.INVISIBLE);
			
			return view;
		}

		protected int getItemLayout() { return R.layout.focus_editor_row; }
	}
}

class FocusGroupItem {
	public String name;
	public long id;
	public String fid;
	public boolean sold;
	
	private OrderFocusedFolder comment;
	
	public FocusGroupItem(FolderImpl f, FocusedGroupItem fgi) {
		f.getData().id = fgi.folderID;
		f.read();
		
		name = f.getData().name;
		id = fgi.folderID;
		fid = fgi.fid;
	}
	
	public void open(Context context, OrderImplBase<?> order) {
		Warehouse.open(context, order, (int)id);
	}
	
	boolean isSame(int folderid) { return id == folderid; }
	boolean isSame(OrderFocusedFolder off) { return fid.equals(off.fid); }
	boolean isSame(Price p) { return false; }
	boolean isSame(OrderFocusedItem ofi) { return false; }

	public FocusGroupItem(String name, String fid, long id) {
		this.name = name;
		this.fid = fid;
		this.id = id;
	}
	
	public boolean isFolder() { return true; }
	
	public int getStateImage() {
		return (sold) ? R.drawable.focus_goods : (comment != null) ? R.drawable.focus_remark : R.drawable.focus_error;
	}
	
	public void setComment(Order order, String text) {
		if( text.length() > 0 ) {
			if( comment == null ) {
				OrderFocusedFolder off = new OrderFocusedFolder();
				off.fid = fid;
				comment = off;
				order.focusedFolders.add(off);
			}
			comment.remark = text; 
		} else {
			if( comment != null ) {
				order.focusedFolders.remove(comment);
				comment = null;
			}
		}
	}
	
	public String getComment() { return comment == null ? "" : comment.remark; }
	
	public void assignComment(OrderFocusedFolder off) { comment = off; }
	public void assignComment(OrderFocusedItem ofi) {}
}

class FocusItem extends FocusGroupItem {
	
	private OrderFocusedItem remark;
	
	public FocusItem(String name, String id, long rid) {
		super(name, id, rid);
	}

	boolean isSame(int folderid) { return false; }
	boolean isSame(OrderFocusedFolder off) { return false; }
	boolean isSame(Price p) { return fid.equals(p.id); }
	boolean isSame(OrderFocusedItem ofi) { return fid.equals(ofi.id); }

	@Override
	public boolean isFolder() { return false; }

	@Override
	public int getStateImage() {
		return (sold) ? R.drawable.focus_goods : (remark != null) ? R.drawable.focus_remark : R.drawable.focus_error;
	}
	
	@Override
	public void setComment(Order order, String text) {
		if( text.length() > 0 ) {
			if( remark == null ) {
				OrderFocusedItem ofi = new OrderFocusedItem();
				ofi.id = fid;
				remark = ofi;
				order.focusedItems.add(ofi);
			}
			remark.remark = text; 
		} else {
			if( remark != null ) {
				order.focusedItems.remove(remark);
				remark = null;
			}
		}
	}
	
	@Override
	public void open(Context context, OrderImplBase<?> order) {
		order.editItem(id, context);
	}

	@Override
	public String getComment() { return remark == null ? "" : remark.remark; }

	@Override
	public void assignComment(OrderFocusedFolder off) {}

	@Override
	public void assignComment(OrderFocusedItem ofi) { remark = ofi; }
}

@SuppressWarnings("serial")
class ItemList extends ArrayList<FocusGroupItem> {
	
	public void markSold(int folderId) {
		for(FocusGroupItem i : this) {
			if( i.isSame(folderId) ) {
				i.sold = true;
				break;
			}
		}
	}
	
	public void markSold(Price prc) {
		for(FocusGroupItem i : this) {
			if( i.isSame(prc) ) {
				i.sold = true;
				break;
			}
		}
	}
	
	public void assignRemark(OrderFocusedFolder comment) {
		for(FocusGroupItem i : this) {
			if( i.isSame(comment) ) {
				i.assignComment(comment);
				break;
			}
		}
	}

	public void assignRemark(OrderFocusedItem comment) {
		for(FocusGroupItem i : this) {
			if( i.isSame(comment)) {
				i.assignComment(comment);
				break;
			}
		}
	}
}

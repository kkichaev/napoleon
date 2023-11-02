package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Income;
import com.grsoft.dataobjects.IncomeItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.FolderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.view.RegDurationActivity;

public class IncomeForm extends RegDurationActivity {
	
	IncomeList data = new IncomeList();
	TreeAdapter tree;
	LinesOnClickListener lines;
	
	public static void open(Context ctx) {
		Intent i = new Intent(ctx, IncomeForm.class);
		ctx.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.income_form);
		
		data.load();
		tree = new TreeAdapter(data, findViewById(R.id.llHead));
		tree.setMoveButtons(findViewById(R.id.btnDown), findViewById(R.id.btnUp));
		tree.setExpandButton((ImageButton)findViewById(R.id.btnViewMode));
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(tree);
		lv.setOnItemClickListener(tree);
		
		ImageButton ib;
		ib = (ImageButton)findViewById(R.id.btnLines);
		lines = new LinesOnClickListener(lv, ib, this);
	}
	
	interface Drawable {
		void draw(View v);
	}
	
	interface TreeObserver extends Drawable {
		TreeObserver move(boolean next, boolean moveToLeaf);
		
		TreeObserver up();
		TreeObserver dn(int index);
		
		int size();
		Drawable getItem(int index);
		
		void drawHead(View v);
		void drawItem(View view, int index);
	}
	
	TreeObserver getLeafs(TreeObserver cur, boolean next) {
		if( cur.size() == 0 )
			return null;

		while( true ) {
			TreeObserver f = ((next) ? cur.dn(0) : cur.dn(cur.size()-1));
			if( f == null || f.size() == 0 )
				return cur;
			cur = f;
		}
	}

	class PriceData implements Drawable {
		String name;
		String id;
		int qty;
		String remark;
		
		public PriceData(IncomeItem ii, Price p) {
			name = p.name;
			id = p.id;
			qty = ii.qty;
			remark = ii.remark;
		}
		
		public void draw(View view) {
			TextView tv = (TextView)view.findViewById(R.id.tvName);
			lines.getController().prepareTextView(tv);
			tv.setText(name);

			tv = (TextView)view.findViewById(R.id.tvQty);
			tv.setText(Util.IntToScaleStr(qty, Consts.QTY_SCALE));
			tv.setVisibility(View.VISIBLE);
			
			tv = (TextView)view.findViewById(R.id.tvRemark);
			tv.setText(remark);
			tv.setVisibility(View.VISIBLE);

			ImageView iv = (ImageView)view.findViewById(R.id.ivImage);
			iv.setVisibility(View.GONE);
		}
	}
	
	class FolderData implements TreeObserver {
		IncomeData parent;
		String name;
		int folderID;
		ArrayList<PriceData> data = new ArrayList<PriceData>();
		
		public PriceData get(String id) {
			for( PriceData pd : data ) {
				if( pd.id.compareTo(id) == 0 )
					return pd;
			}
			
			return null;
		}
		
		public FolderData(IncomeData parent, IncomeItem ii, Folder f, Price p) {
			folderID = ii.folderID;
			name = f.name;
			this.parent = parent;
			
			data.add(new PriceData(ii, p));
		}

		public void add(IncomeItem ii, Price p) {
			PriceData pd = get(p.id);
			if( pd == null ) {
				data.add(new PriceData(ii, p));
			} else {
				pd.qty += ii.qty;
			}
		}

		@Override public TreeObserver dn(int index) { return null; }

		@Override
		public void drawHead(View v) {
			TextView tv = (TextView)v.findViewById(R.id.tvGoUp);
			tv.setText(name);
		}

		@Override
		public void drawItem(View view, int index) {
			if( index < data.size()) {
				PriceData pd = data.get(index);
				pd.draw(view);
			}			
		}

		@Override public Drawable getItem(int index) { return (index < data.size()) ? data.get(index) : null; }

		@Override
		public TreeObserver move(boolean next, boolean moveToLeaf) { return parent.moveToNext(this, next, moveToLeaf); }

		@Override public int size() { return data.size(); }

		@Override public TreeObserver up() { return parent; }

		public void sortItems() {
			Collections.sort(data, new Comparator<PriceData>() {
				@Override 
				public int compare(PriceData object1, PriceData object2) {
					return object1.name.compareTo(object2.name);
				}
			});
		}

		@Override
		public void draw(View view) {
			TextView tv = (TextView)view.findViewById(R.id.tvName);
			lines.getController().prepareTextView(tv);
			tv.setText(name);
			
			View v;
			v = view.findViewById(R.id.tvQty);
			v.setVisibility(View.GONE);
			
			v = view.findViewById(R.id.tvRemark);
			v.setVisibility(View.GONE);
			
			ImageView iv = (ImageView)view.findViewById(R.id.ivImage);
			iv.setVisibility(View.VISIBLE);
			iv.setImageResource(R.drawable.folder);
		}
	}
	
	class IncomeData implements TreeObserver {
		IncomeList parent;
		Date date;
		ArrayList<FolderData> data = new ArrayList<FolderData>();
		
		public FolderData get(int folderID) {
			for(FolderData fd : data) {
				if( fd.folderID == folderID )
					return fd;
			}
			
			return null;
		}
		
		public TreeObserver moveToNext(FolderData folderData, boolean next, boolean moveToLeaf) {
			int index = data.indexOf(folderData);
			if( next ) {
				if(index < data.size())
					return data.get(index+1);
			} else {
				if(index > 0)
					return data.get(index-1);
			}
			return parent.moveToNext(this, next, moveToLeaf);
		}

		public IncomeData(IncomeList parent, Income i, FolderImpl f, PriceImpl p) {
			date = i.date;
			this.parent = parent;
			
			addItems(i, f, p);
		}

		private void addItems(Income i, FolderImpl f, PriceImpl p) {
			for( IncomeItem ii : i.items ) {
				p.getData().id = ii.id;
				p.read();
				
				FolderData fd = get(ii.folderID);
				if( fd == null ) {
					f.getData().id = ii.folderID;
					f.read();
					fd = new FolderData(this, ii, f.getData(), p.getData());
					data.add(fd);
				} else {
					fd.add(ii, p.getData());
				}
			}
		}

		@Override public TreeObserver dn(int index) { return (index < data.size()) ? data.get(index) : null; }

		@Override
		public void drawHead(View v) {
			SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy");
			TextView tv = (TextView)v.findViewById(R.id.tvGoUp);
			tv.setText(sf.format(date));
		}

		@Override
		public void drawItem(View view, int index) {
			if( index < data.size()) {
				FolderData fd = data.get(index);
				fd.draw(view);
			}
		}

		@Override
		public TreeObserver move(boolean next, boolean moveToLeaf) {
			if( size() != 0 && next && moveToLeaf )
				return getLeafs(this, next);

			return parent.moveToNext(this, next, moveToLeaf);
		}

		@Override public int size() { return data.size(); }

		@Override public TreeObserver up() { return parent; }

		@Override public Drawable getItem(int index) { return (index < data.size()) ? data.get(index): null;  }

		public void sortItems() {
			Collections.sort(data, new Comparator<FolderData>() {
				@Override public int compare(FolderData object1, FolderData object2) { return object1.folderID - object2.folderID; }
			});
			
			for(FolderData fd : data)
				fd.sortItems();
		}

		@Override
		public void draw(View view) {
			SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy");
			TextView tv = (TextView)view.findViewById(R.id.tvName);
			lines.getController().prepareTextView(tv);
			tv.setText(sf.format(date));
			
			View v;
			v = view.findViewById(R.id.tvQty);
			v.setVisibility(View.GONE);
			
			v = view.findViewById(R.id.tvRemark);
			v.setVisibility(View.GONE);
			
			ImageView iv = (ImageView)view.findViewById(R.id.ivImage);
			iv.setVisibility(View.VISIBLE);
			iv.setImageResource(R.drawable.folder);
		}
	}
	
	@SuppressWarnings("serial")
	class IncomeList extends ArrayList<IncomeData> implements TreeObserver {
		
		public IncomeData get(Date date) {
			for(IncomeData d : this) {
				if( d.date.compareTo(date) == 0 )
					return d;
			}
			
			return null;
		}
		
		public TreeObserver moveToNext(IncomeData incomeData, boolean next, boolean moveToLeaf) {
			int index = indexOf(incomeData);
			if( next ) {
				if(index < size() - 1) index++;
				else index = 0;
			} else {
				if(index > 0) index--;
				else index = size() - 1;
			}
			TreeObserver t = get(index);
			return (moveToLeaf) ? getLeafs(t, next) : t;
		}

		public void load() {
			PriceImpl p = new PriceImpl();
			FolderImpl f = new FolderImpl();
			DbReader r = new DbReader();
			Income i = new Income();
			String table = DataObjectInfo.getInstance().getTableName(Income.class);
			boolean bdo = r.select(i, table, "", "date");
			while( bdo ) {
				IncomeData id = get(i.date);
				if( id == null ) {
					add(new IncomeData(this, i, f, p));
				} else {
					id.addItems(i, f, p);
				}
				bdo = r.selectNext(i);
			}
			r.close();
			p.close();
			f.close();
			
			for(IncomeData id : this)
				id.sortItems();
		}

		@Override public TreeObserver dn(int index) { return ( index >= size() ) ? null :  get(index); }

		@Override
		public void drawHead(View v) {
			TextView tv = (TextView)v.findViewById(R.id.tvGoUp);
			tv.setText("Приходы");
		}

		@Override
		public void drawItem(View view, int index) {
			if( index < size()) {
				IncomeData id = get(index);
				id.draw(view);
			}
		}

		@Override public TreeObserver move(boolean next, boolean moveToLeaf) { return (moveToLeaf) ? getLeafs(this, next) : null; }

		@Override public TreeObserver up() { return null; }

		@Override public Drawable getItem(int index) { return (index < size()) ? get(index) : null;  }

		@Override public void draw(View v) { }
	}
	
	class ExpandedTreeObserver implements TreeObserver {
		TreeObserver src;
		ArrayList<Drawable> data = new ArrayList<Drawable>();
		
		public ExpandedTreeObserver(TreeObserver s) {
			src = s;
			loadData(s);
		}

		private void loadData(TreeObserver s) {
			for(int i=0; i<s.size(); i++) {
				Drawable d = s.getItem(i);
				if( d instanceof TreeObserver )
					loadData((TreeObserver) d);
				else
					data.add(d);
			}
		}

		@Override public TreeObserver dn(int index) { return null; }

		@Override
		public void drawHead(View v) { src.drawHead(v); }

		@Override
		public void drawItem(View view, int index) {
			if( index < data.size() ) {
				data.get(index).draw(view);
			}
		}

		@Override public Drawable getItem(int index) { return (index < data.size()) ? data.get(index) : null; }

		@Override
		public TreeObserver move(boolean next, boolean moveToLeaf) {
			TreeObserver t = src.move(next, false);
			if( t != null )
				return new ExpandedTreeObserver(t);
			return null;
		}

		@Override public int size() { return data.size(); }

		@Override
		public TreeObserver up() {
			TreeObserver t = src.up();
			if( t != null )
				return new ExpandedTreeObserver(t);
			return null;
		}

		@Override
		public void draw(View v) { src.draw(v); }
	}
	
	class TreeAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
		
		TreeObserver tree;
		View headView;
		
		UpController upController;
		
		class UpController implements View.OnClickListener {
			@Override public void onClick(View v) { setNewTree(tree.up()); }
		}
		
		public TreeAdapter(TreeObserver t, View headView) {
			tree = t;
			this.headView = headView;
			
			upController = new UpController();
			
			tree.drawHead(headView);
			
			if( headView instanceof ViewGroup ) {
				ViewGroup vg = (ViewGroup)headView;
				for( int i = vg.getChildCount() - 1; i >= 0; i-- ) {
					vg.getChildAt(i).setOnClickListener(upController);
				}
			}
			headView.setOnClickListener(upController);
		}
		
		boolean expanded() { return (tree instanceof ExpandedTreeObserver); }
		
		public void setMoveButtons(View nextBtn, View prevBtn) {
			nextBtn.setOnClickListener(new View.OnClickListener() {				
				@Override public void onClick(View v) { setNewTree(tree.move(true, !expanded())); }
			});
			
			prevBtn.setOnClickListener(new View.OnClickListener() {				
				@Override public void onClick(View v) { setNewTree(tree.move(false, !expanded())); }
			});
		}
		
		public void setExpandButton(final ImageButton btn) {
			btn.setOnClickListener(new View.OnClickListener() {				
				@Override public void onClick(View v) {
					boolean expand = !expanded();
					btn.setImageResource((expand) ? R.drawable.view_list : R.drawable.view_tree);
					if( expand )
						setNewTree(new ExpandedTreeObserver(tree));
					else
						setNewTree(((ExpandedTreeObserver)tree).src);
				}
			});
		}

		@Override public int getCount() { return tree.size(); }

		@Override public Object getItem(int arg0) { return tree.getItem(arg0); }

		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if( convertView == null )
				convertView = View.inflate(IncomeForm.this, R.layout.income_row, null);
			
			tree.drawItem(convertView, position);
			return convertView;
		}
		
		TreeObserver current() { return tree; }

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			setNewTree(tree.dn(position));
		}

		private void setNewTree(TreeObserver newTree) {
			if( newTree != null ) {
				tree = newTree;
				tree.drawHead(headView);
				notifyDataSetChanged();
			}
		}
	}
}

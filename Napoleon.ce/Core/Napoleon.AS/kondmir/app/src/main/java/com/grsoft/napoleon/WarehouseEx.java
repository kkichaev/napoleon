package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Category;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.FindTextWatcher;
import com.grsoft.util.Filter;
import com.grsoft.util.ZeroPositionFilter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class WarehouseEx extends Warehouse implements OnItemClickListener, OnClickListener  {
	CategoryAdapter catAd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		catAd = new CategoryAdapter();
		ivFilter.setOnClickListener(this);
	}
	
	@Override
	protected int getOptionsMenuId() {
		return R.menu.warehouse_opt_menuex;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itCategory) {
			showDialog(R.id.category_dlg);
			return true;
		}else
			return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.category_dlg)
			return createCategoryDlg();
		return super.onCreateDialog(id);
	}

	private Dialog createCategoryDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_category);
		View view = View.inflate(this, R.layout.category, null);
		builder.setView(view);
		
		ListView list = (ListView) view.findViewById(R.id.list);
		list.setAdapter(catAd);
		list.setOnItemClickListener(this);
		
		final EditText ed = (EditText) view.findViewById(R.id.edSearch); 
		
		FindTextWatcher fw = new FindTextWatcher(ed, list);
		ed.addTextChangedListener(fw);
		
		view.findViewById(R.id.btnClearFind).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ed.setText("");				
			}
		});

		return builder.create();
	}
	
	private class CategoryAdapter extends BaseAdapter implements FilterAdapter{
		private List<Category> data = new ArrayList<Category>();
		private List<Category> filter = new ArrayList<Category>();
		
		public CategoryAdapter() {
			DataTraveler.travel(Category.class, new DataTraveler.Travel<Category>(true) {

				@Override
				public boolean travel(DataTraveler<Category> item) {
					data.add(item.data);
					return true;
				}}, null);
			
			Collections.sort(data, new Comparator<Category>() {

				@Override
				public int compare(Category lhs, Category rhs) {
					return lhs.name.compareTo(rhs.name);
				}
			});
			
			filter.addAll(data);
		}
		
		@Override
		public int getCount() {
			return filter.size();
		}

		@Override
		public Object getItem(int position) {
			return filter.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null)
				view = View.inflate(WarehouseEx.this, R.layout.category_row, null);
			
			Category item = (Category) getItem(position);
			((TextView)view).setText(item.name);
			
			return view;
		}
		
		public void search(String val) {
			filter.clear();
			
			if (val.trim().length() == 0) 
				filter.addAll(data);
			else 
				for(Category c : data)
					if (c.name.toUpperCase().contains(val.toUpperCase()))
						filter.add(c);
					
			notifyDataSetChanged();
		}

		@Override
		public void applyFilter(String value) {
			search(value);
			
		}

		@Override
		public void resetFilter() {
			search("");
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		try {
			dismissDialog(R.id.category_dlg);
			
			Category c = (Category) parent.getItemAtPosition(position);
			adapter.deleteFilter(CategoryFilter.NAME);
			adapter.putFilter(new CategoryFilter(c.id));
			adapter.buildSet();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static class CategoryFilter extends Filter{
		public static final String NAME = "CategoryFilter"; 
		private String id = "";
		
		public CategoryFilter(String id) {
			super(NAME);
			DbWriter.checkDBTable(DbObject.getDataType(Price.class));
			this.id = id;
		}
		
		@Override
		public String getWhereStr() {
			return "category = \"" + id + "\"";
		}
		
	}
	
	@Override
	protected void postAdapterChange() {
		
		super.postAdapterChange();
		
		ivFilter.setVisibility(
				adapter.getFilter(ZeroPositionFilter.NAME) != null ||
				adapter.getFilter(CategoryFilter.NAME) != null
				? View.VISIBLE
				: View.GONE);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ivFilterLabel) {
			adapter.deleteFilter(ZeroPositionFilter.NAME);
			adapter.deleteFilter(CategoryFilter.NAME);
			adapter.buildSet();
		}
	}
}

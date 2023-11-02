package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.grsoft.dataobjects.City;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.impl.Citylmpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.FoldersMainAdapter.ViewData;
import com.grsoft.napoleon.Main.MainAdapter;
import com.grsoft.napoleon.documents.DocType;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class OrgAdapter extends BaseMainAdapter implements MainAdapter{
	private List<City> cities = new ArrayList<City>();
	private City curCity = null;
	private Main main;
	
	public OrgAdapter(Main main) {
		this.main = main;
	}
	
	@Override
	public int getCount() {
		return curCity == null ? cities.size() : getOrgCount();
	}

	private int getOrgCount() {
		int result = 0;
		
		if (curCity != null)
			result = curCity.items.size();
		
		return result;
	}

	@Override
	public Object getItem(int position) {
		return curCity == null ? cities.get(position) : getOrgItem(position);
	}

	private Org getOrgItem(int position) {
		Org result = null;
		
		if(curCity != null && curCity.items.size() > position) {
			OrgImpl impl = new OrgImpl();
			impl.read("id", curCity.items.get(position).name);
			result = impl.getData();
		}

		return result;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view =  curCity == null ? getCityView(position, convertView) : getOrgView(position, convertView);
		view.findViewById(R.id.tvOrgSum).setVisibility(View.GONE);
		return view;
	}

	private View getCityView(int position, View convertView) {
		City c = (City) getItem(position);
		
		if(c != null) {
			ViewData v = new ViewData();
			v.name = c.name;
			convertView = main.getFolderMainView(convertView, position, v);
			ImageView iv = (ImageView) convertView.findViewById(R.id.ivFolder);
			iv.setImageResource(R.drawable.folder);
			iv.setVisibility(View.VISIBLE);
		}
			
		return convertView;
	}

	private View getOrgView(int position, View convertView) {
		return main.getSolidMainView(getOrgItem(position), position, convertView);
	}

	@Override
	void reload() {
		cities.clear();
		
		DataTraveler.travel(City.class, new DataTraveler.Travel<City>(true) {
			@Override
			public boolean travel(DataTraveler<City> item) {
				cities.add(item.data);
				return true;
			}
		}, null);
		
		Collections.sort(cities, new Comparator<City>() {

			@Override
			public int compare(City lhs, City rhs) {
				return lhs.name.compareTo(rhs.name);
			}
		});
		
		if(curCity != null) {
			boolean v = false;
			
			for(City c : cities) {
				if (c.id.equals(curCity.id)) {
					v = true;
					break;
				}
			}
			
			if (!v)
				curCity = null;
		}
		
		Collections.sort(cities, new Comparator<City>() {

			@Override
			public int compare(City lhs, City rhs) {
				return lhs.name.compareTo(rhs.name);
			}
		});
	}

	@Override
	public Org getOrg(int pos) {
		return getOrgItem(pos);
	}

	public boolean isTop() {
		return curCity == null;
	}

	public void putCityItem(Citylmpl c) {
		cities.add(c.getData());
		notifyDataSetChanged();
	}
	
	protected OnClickListener topLevelClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (!isTop())
			{
				curCity = null;
				adjustView();
				notifyDataSetChanged();
			}
		}
	};

	@Override
	public void adjustView() {
		View v = main.findViewById(R.id.ivGoUp);
		if(v != null)
			v.setVisibility(isTop() ? View.GONE : View.VISIBLE);
		
		v = main.findViewById(R.id.btnMode);
		
		if(v != null)
			((ImageView)v).setImageResource(R.drawable.route);
		
		if(isTop()){
			v = main.findViewById(R.id.tvFirstColumnCaption);
			
			if(v != null){
				v.setOnClickListener(topLevelClick);
				((TextView)v).setText(main.getString(R.string.city));
			}
			
			v = main.findViewById(R.id.tvMainDocValColTitle);
			
			if(v != null)
				((TextView)v).setText(main.getString(R.string.Clients_of));
		}else {
			DocType.getCurDoc().viewOpened(main);
			TextView tv = (TextView)main.findViewById(R.id.tvFirstColumnCaption); 
			if( tv != null )
				tv.setText(curCity.name);
		}
		
		main.onAdapterViewAdjusted();
	}

	@Override
	public void click(int position) {
		if (isTop()) 
			cityClick(position);
		else
			orgClick(position);
	}

	private void orgClick(int position) {
		Org o = getOrgItem(position);
		main.openOrg(o, position);
	}

	protected void cityClick(int position) {
		curCity = cities.get(position);
		adjustView();
		notifyDataSetChanged();
	}

	public void putOrgItem(String id) {
		if (curCity != null) {
			Citylmpl impl = new Citylmpl();
			impl.read("id", curCity.id);
			
			if(!curCityContains(id)) {
				OrgFolderItem i = new OrgFolderItem();
				i.name = id;
				curCity.items.add(i);
				impl.getData().items.add(i);
			}
			
			impl.getData().flags = Org.FL_USER_CREATED;
			impl.write();
			impl.close();
			
			notifyDataSetChanged();
		}
	}
	
	private boolean curCityContains(String orgid) {
		for(OrgFolderItem i : curCity.items)
			if (i.name.equals(orgid))
				return true;
		
		return false;
	}
	
	public String getCityName() {
		String result = "";
			
		if (curCity != null)
			result = curCity.name;
		
		return result;	
	}

}

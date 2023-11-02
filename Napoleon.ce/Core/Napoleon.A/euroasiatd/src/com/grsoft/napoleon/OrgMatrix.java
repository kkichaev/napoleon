package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.MatrixBaseAdapter;

public class OrgMatrix extends MatrixBaseAdapter {
	public static final String NAME = "Матрица ТТ ";
	List<MatrixItem> items = new ArrayList<MatrixItem>();
	Set<String> ids = new HashSet<String>();
	
	public OrgMatrix(WarehouseNewW warehouse, String id) {
		super(warehouse);
		collectItems(id);
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected List<? extends MatrixItem> getMatrixItems() {
		return items;
	}

	protected List<? extends MatrixItem> collectItems(String id) {
		items.addAll(readItems(id));
		
		Collections.sort(items, new Comparator<MatrixItem>() {
			@Override
			public int compare(MatrixItem lhs, MatrixItem rhs) {
				return lhs.order - rhs.order;
			}});
		
		for(MatrixItem i : items)
			ids.add(i.id);
		
		return items;
	}
	
	private List<MatrixItem> readItems(String id) {
		OrgImpl org = new OrgImpl();
		org.read("id", id);
		
		return ((OrgEx)org.getData()).matrix;
	}
	

	public boolean hasItems() {
		return items.size() > 0;
	}
	
	public boolean contains(String id) {
		return ids.contains(id);
	}
}

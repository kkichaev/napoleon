package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RivalFolder;
import com.grsoft.dataobjects.RivalPrice;
import com.grsoft.util.FolderTree;
import com.grsoft.util.MatrixBaseAdapter;
import android.annotation.SuppressLint;
import android.util.SparseArray;

public class OrgMatrixAdapter extends MatrixBaseAdapter{
	String orgID = "";
	String suppl = "";
	
	@SuppressLint("UseSparseArrays")
	SparseArray<Integer> topFolders = new SparseArray<Integer>();
	boolean ourMatrix;
	
	public OrgMatrixAdapter(Warehouse warehouse, String id, boolean ourMatrix, String suppl) {
		super(warehouse);
		
		this.orgID = id;
		this.ourMatrix = ourMatrix;
		this.suppl = suppl;
		
		FolderTree ft = new FolderTree();
		ft.load((ourMatrix ? (new Folder()).getTableName() : (new RivalFolder()).getTableName()));
		int curTop = -1, curLevel = -1;
		for(Folder f : ft) {
			if(curTop == -1 || f.level <= curLevel) {
				curTop = f.id;
				if(curLevel == -1)
					curLevel = f.level;
				topFolders.put(curTop, curTop);
				continue;
			}
			topFolders.put(f.id, curTop);
		}
	}

	public static String NAME = "Матрица ТТ";
	@Override
	protected String getFolderTableName() {
		return ourMatrix ? super.getFolderTableName() : DataObjectInfo.getInstance().getTableName(RivalFolder.class);
	}
	
	@Override
	protected String getPriceTableName() {
		return ourMatrix ? super.getPriceTableName() : DataObjectInfo.getInstance().getTableName(RivalPrice.class);
	}

	@Override
	protected List<? extends MatrixItem> getMatrixItems() {
		final List<MatrixItem> res = new ArrayList<MatrixItem>();
		
		String where = String.format("id='%s' and isRival=%d" , orgID, (int)(ourMatrix ? 0 : 1));
		
		if (suppl.length() > 0)
			where = String.format("%s and suppl='%s'", where, suppl);
			
		DataTraveler.travel(OrgMatrix.class, new DataTraveler.Travel<OrgMatrix>() {

			@Override
			public boolean travel(DataTraveler<OrgMatrix> item) {
				MatrixItem i = new MatrixItem();
				i.id = item.data.id_i;
				i.order = item.data.pos;
				res.add(i);
				return true;
			}}, where);
		return res;
	}
	
	@Override
	protected void addPriceInfo(long rowid, int folderid, String name, String id) {
		Integer fid = topFolders.get(folderid);
		if( fid != null)
			super.addPriceInfo(rowid, fid, name, id);
	}
}

package com.grsoft.napoleon.documents;


import com.grsoft.dataobjects.impl.OrgCoordImpl;
import com.grsoft.napoleon.R;
import com.grsoft.util.gps.GPSUtilNew;

public class OrgCoordDoc extends DateDocType {
	static public final String DOC_NAME = "OrgCoord";
	static public final String OBJ_NAME = "OrgCoord";
	static private OrgCoordDoc instance = null;
	
	protected OrgCoordDoc() {
		super(DOC_NAME, OBJ_NAME, OrgCoordImpl.class);
	}

	public static DocTypeBase instance() {
		if( instance == null )
			instance = new OrgCoordDoc();
		return instance;
	}

	public static void setGPS(String id) {
		OrgCoordImpl impl = new OrgCoordImpl();
		impl.init(null, id, GPSUtilNew.getLastKnownLocation());
		impl.close();
	}

	@Override
	public int getDocTitle() {
		return R.string.org_doc_coord;
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.org_coord_doc;
	}
	
	@Override
	public boolean outOfScript() {
		return true;
	}
}

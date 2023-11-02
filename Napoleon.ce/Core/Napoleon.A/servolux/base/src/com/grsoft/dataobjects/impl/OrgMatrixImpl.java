package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.IdoMtx;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.MatrixItemEx;
import com.grsoft.dataobjects.OrgDog;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgMatrix;


public class OrgMatrixImpl extends DbObject<OrgMatrix> {
	public static Map<OrgDog, List<MatrixItemEx>> getItems(final OrgEx org) {
		final Map<OrgDog, List<MatrixItemEx>> ret = new HashMap<OrgDog, List<MatrixItemEx>>();
		final OrgMatrixImpl omi = new OrgMatrixImpl();
		
		
		DataTraveler.travel(OrgDog.class, new DataTraveler.Travel<OrgDog>(true) {

			@Override
			public boolean travel(DataTraveler<OrgDog> item) {
				String mtx = getMatrixName(org, item.data.firm);
				if(mtx.length() > 0) {
					HashSet<String> used = new HashSet<String>();
					List<MatrixItemEx> matrix = new ArrayList<MatrixItemEx>();
					OrgMatrix om = omi.getData();
					om.name = mtx;
					if(omi.read()) {
						for(MatrixItem mi : om.items) {
							if(used.contains(mi.id))
								continue;
							used.add(mi.id);
							MatrixItemEx mie = new MatrixItemEx();
							mie.id = mi.id;
							mie.mustBe = ((MatrixItemEx)mi).mustBe;
							matrix.add(mie);
						}
					}
					if(matrix.size() > 0)
						ret.put(item.data, matrix);
				}
				return true;
			}
		},  "ido='" + ((OrgEx)org).ido + "'");
		
		omi.close();
		return ret;
	}
	
	static String getMatrixName(OrgEx org, String firm) {
		String matrix = ""; 
		
		if(org != null && firm != null && firm.trim().length() > 0){
			IdMtxImpl am = new IdMtxImpl();
			am.getData().id = org.id;
			am.getData().firm = firm;
			boolean readed = am.read();
			am.close();
			matrix = am.getData().mtx;
			
			if(!readed) {
				String objects[] = new String[] {
						"", IdoMtx.ORG_OBJ, IdoMtx.ORG_TYPE_OBJ, IdoMtx.RETAIL_OBJ, IdoMtx.CHANNEL_OBJ,	
				};
				String ids[] = new String[] {
						org.ido, org.ido, org.formatTT, org.idRetailer, org.idChannel,	
				};
				
				IdoMtxImpl om = new IdoMtxImpl();
				IdoMtx omd = om.getData();
				omd.firm = firm;
				int idx = 0;
				for(String obj : objects) {
					omd.id = ids[idx];
					omd.objectType = obj;
					if( om.read() ) {
						matrix = om.getData().mtx;
						break;
					}
					idx++;
				}
				om.close();
			}
		}
		
		return matrix;
	}
	
	public static HashSet<String> getItems(OrgEx org, String firm) {
		HashSet<String> result = null;
		String matrix = getMatrixName(org, firm); 
		
		if(matrix.trim().length() > 0){
			OrgMatrixImpl omi = new OrgMatrixImpl();
			if(omi.read("name", matrix)){
				result = new HashSet<String>();
				
				for(MatrixItem mi : omi.getData().items)
					result.add(mi.id);
			}
		}
		
		return result;
	}
}

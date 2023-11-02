package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="distrib", keyFields="created")
public class Distrib extends CreateDocDataObject {
//	public List<DistribPhoto> photo = new ArrayList<DistribPhoto>();
	public List<DistribRemark> items = new ArrayList<DistribRemark>();
}

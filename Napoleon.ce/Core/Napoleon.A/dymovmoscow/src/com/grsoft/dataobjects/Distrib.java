package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.TableInfo;

@TableInfo(name="distrib", keyFields="created")
public class Distrib extends CreateDocDataObject {
	public List<MatrixItem> matrix = new ArrayList<MatrixItem>();
	public List<DistribDef> defs = new ArrayList<DistribDef>();
	public List<DistribItem> items = new ArrayList<DistribItem>();
}

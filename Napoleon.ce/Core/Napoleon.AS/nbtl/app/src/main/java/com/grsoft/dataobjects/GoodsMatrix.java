package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="goodsmatrix", keyFields="name")
@ServerInfo(name="GoodsMatrix")
public class GoodsMatrix extends DataObject {
	public String name = "";
	public List<MatrixItem> items = new ArrayList<MatrixItem>();
}

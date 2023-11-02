package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrgEx extends Org {
	
	public String costType = "";
	
	public List<UnitItem> units;
	
	/***
	 * Экспедитор
	 */
	public String forvarder = "";
	
	/***
	 * Матрица контрагента
	 */
	public List<MatrixItem> matrix = new ArrayList<MatrixItem>();
}

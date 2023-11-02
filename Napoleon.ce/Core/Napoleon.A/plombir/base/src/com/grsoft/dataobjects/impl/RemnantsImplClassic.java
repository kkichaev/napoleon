package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrgMatrix;

public class RemnantsImplClassic extends RemnantsImpl implements IMatrix{

	public OrgMatrixImpl matrix = null;
	
	public OrgMatrix getMatrix() {
		return MatrixInflator.inflate(this);
	}

}

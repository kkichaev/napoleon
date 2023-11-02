package com.grsoft.dataobjects.impl;

import java.lang.reflect.Field;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgBase;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.ExtrasConst;

public class MatrixInflator {
	public static OrgMatrix inflate(Document<?> o) {
		try {
			Field f = o.getClass().getField("matrix");
			OrgMatrixImpl matrix = (OrgMatrixImpl) f.get(o);

			if (matrix == null) {
				matrix = new OrgMatrixImpl();
				OrgMatrix m = matrix.getData();

				OrgImpl oi = new OrgImpl();
				Org org = oi.getData();
				OrgBase ob = (OrgBase) oi.getData();
				org.id = o.getId();
				oi.read();
				oi.close();

				String matrixName = ob.getMatrix();
				if (matrixName != null && matrixName.length() > 0) {
					m.name = matrixName;
					matrix.read();
					matrix.close();
				}
			}

			return (matrix.getRowid() == ExtrasConst.INVALID_ID) ? null
					: matrix.getData();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

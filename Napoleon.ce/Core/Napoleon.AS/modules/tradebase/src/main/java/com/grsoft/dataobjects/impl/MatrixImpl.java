package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Matrix;

public class MatrixImpl extends DbObject<Matrix> {

	public static List<String> getNames(){
		final String NAME = "name";
		Cursor cursor = null;
		try{
			DbWriter.checkDBTable(getDataType(Matrix.class));
			cursor = DataBaseManager.getDataBase().query(
					"Matrix", new String[] {NAME}, null, null, null, null, null);
			List<String> result = new ArrayList<String>();
			
			while(cursor.moveToNext())
				result.add(cursor.getString(cursor.getColumnIndex(NAME)));
			
			return result;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			if( cursor != null )
				cursor.close();
		}
	}
}

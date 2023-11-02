package com.grsoft.napoleon;

import java.util.List;

import android.widget.CheckBox;

import com.grsoft.database.GeoRcvHitching;
import com.grsoft.database.GeoSndHitching;
import com.grsoft.database.Hitching;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> result = super.getGenDataHitchings();
		
		if (result != null && 
				((CheckBox)findViewById(R.id.cbGenData)).isChecked())
			result.add(new GeoRcvHitching());
		
		return result;
	}
	
	@Override
	public List<ObjectListener> getExported() {
		List<ObjectListener> result = super.getExported();
		
		if (result != null)
			result.add(new GeoSndHitching());
		
		return result; 
	}
}

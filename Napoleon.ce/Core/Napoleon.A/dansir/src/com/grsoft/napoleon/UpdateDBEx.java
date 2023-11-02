package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.ServerInfoHitching;
import com.grsoft.network.exception.RuntimeException;

import android.content.Intent;
import android.widget.Toast;

public class UpdateDBEx extends UpdateDB {
	
	ServerInfoHitching sih = null;
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings(); 
		sih = new ServerInfoHitching();
		ret.add(sih);
		return ret;
	}
	
	protected void postSync(Boolean result) {
		if(result != null && sih != null) {			
			Calendar c = Calendar.getInstance();
			long diff = sih.serverDate.getTime() - c.getTime().getTime();
			if(Math.abs(diff) > 24 * 3600 * 1000l ) {
				Toast.makeText(this, "Время системы отличается от серверного", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
			}
		}
	}

}

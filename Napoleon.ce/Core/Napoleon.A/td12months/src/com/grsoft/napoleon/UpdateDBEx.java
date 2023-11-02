package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;

import com.grsoft.napoleon.R.string;
import com.grsoft.util.Util;

import android.content.SharedPreferences;
import android.text.format.DateUtils;
import android.widget.CheckBox;
import android.widget.Toast;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected UpdateProcess getUpdateProcess() {
		if (((CheckBox) findViewById(R.id.cbGenData)).isChecked()) {
			SharedPreferences sp = getSharedPreferences(DocumentsEx.LAST_SYNC,
					MODE_PRIVATE);
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			
			SharedPreferences.Editor e = sp.edit();
			e.putLong(DocumentsEx.LAST_SYNC, c.getTime().getTime());
			e.commit();
		}
		return super.getUpdateProcess();
	}

	@Override
	protected void postSync(Boolean result) {

		if (!result) {
			SharedPreferences sp = getSharedPreferences(DocumentsEx.LAST_SYNC,
					MODE_PRIVATE);
			long value = sp.getLong(DocumentsEx.LAST_SYNC, 0);

			if (value != 0) {
				Toast.makeText(
						this,
						getString(R.string.last_sync_date,
								Util.simpleDateFormat.format(new Date(value))),
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}

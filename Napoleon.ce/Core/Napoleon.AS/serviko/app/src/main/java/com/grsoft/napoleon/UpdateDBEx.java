package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.PrezentHitching;
import com.grsoft.database.SendPhotoCountHitching;
import com.grsoft.database.UpdatePrezentHitching;
import com.grsoft.napoleon.documents.BankDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.network.NetworkAsyncTask;

public class UpdateDBEx extends UpdateDB {

	SyncProgress progressWin = null;
	int picCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((CheckBox) findViewById(R.id.cbDebt)).setChecked(true);
	}

	@Override
	protected List<Hitching> getPrezentHitching() {
		List<Hitching> ret = new ArrayList<Hitching>();
		ret.add(new PrezentHitching(this));


		SendPhotoCountHitching.handler = count -> {
			progressWin.setPicCount(count);
		};

		UpdatePrezentHitching.handler = () -> {
			picCount++;
			progressWin.updatePicProgress(picCount);
		};

		return ret;
	}

	@Override
	protected UpdateProcess getUpdateProcess() {
		if(((CheckBox)findViewById(R.id.cbPresent)).isChecked()) {
			progressWin = new SyncProgress(this);
			return new UpdateProcess(progressWin, this);
		}
		return super.getUpdateProcess();
	}

	@Override
	protected void closeActivity() {
		UpdatePrezentHitching.handler = null;
		super.closeActivity();
	}

	@Override
	protected void postSync(Boolean result) {
		if (result){
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.add(Calendar.DAY_OF_MONTH, -30);

			BankDoc.instance().removeTill(calendar.getTime());
		}
	}
}

package com.grsoft.ads.database;

import java.text.SimpleDateFormat;

import com.grsoft.ads.dataobjects.Certificate;
import com.grsoft.ads.util.SyncInfoUtil;
import com.grsoft.database.HitchOnSelect;

public class CertificateHitching extends HitchOnSelect {
	public CertificateHitching() {
		super(Certificate.class, "Certificate");
		SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM.yyyy");
		setCondition(String.format(" userid = '$CURRENT_USERID' " +
				"and assigned >= ToDate('%s 00:00:00')",
				simpleDateFormat.format(SyncInfoUtil.getLastSync())));
	}

}

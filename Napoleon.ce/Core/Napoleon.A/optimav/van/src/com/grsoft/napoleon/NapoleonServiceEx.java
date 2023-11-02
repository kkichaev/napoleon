package com.grsoft.napoleon;

import com.grsoft.database.HandledDocumentsHitching;
import com.grsoft.network.WriteServiceBase;
import com.grsoft.util.NapoleonService;

public class NapoleonServiceEx extends NapoleonService {
	@Override
	public void configWriteSetvice(WriteServiceBase writeService) {
		writeService.addRecieveHitch(new HandledDocumentsHitching());
	}
}

package com.grsoft.napoleon;

import android.app.Dialog;

public class CreateReturnEx extends CreateReturn {
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == DATE_DIALOG )
			return null;
		return super.onCreateDialog(id);
	}
}

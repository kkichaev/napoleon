package com.grsoft.napoleon.dostavka;

import com.grsoft.napoleon.Features;

public class NapoleonApp extends NapoleonAppBase {
	@Override
	protected void initChildFeatures() {
		super.initChildFeatures();
		Features.ORG_DISPOSITION = true;
	}
}

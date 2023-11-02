package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.IncassEx;

public class IncassSource extends PkoSource {
	public IncassSource(IncassEx incass) {
		setMainData(incass.number, incass.date, incass.sum);
		setFirm(incass.supplyercode);
		setOrg(incass.id);
	}
}

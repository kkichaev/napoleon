package com.grsoft.dataobjects;

import java.util.List;

public class PriceEx extends Price {

	/**
	 * кол-во дней реализации
	 */
	public int realiz;
	
	/**
	 * кол-во дней выгрузки после реализации
	 */
	public int unload;
	
	public List<PriceParty> party;
}

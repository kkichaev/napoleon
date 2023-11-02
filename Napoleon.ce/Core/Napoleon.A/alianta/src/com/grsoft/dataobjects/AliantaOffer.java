package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="AliantaOffer", keyFields="created")
@ServerInfo(name="AliantaOffer")
public class AliantaOffer extends CreateDocDataObject {
	@Scale(value=Consts.SUM_SCALE)
	public int discount = 0;
	
	public String offerDoc = "";
	public int emailSended = 0;
	public int costType = 0;
	public List<OfferItem> items = new ArrayList<OfferItem>();
}

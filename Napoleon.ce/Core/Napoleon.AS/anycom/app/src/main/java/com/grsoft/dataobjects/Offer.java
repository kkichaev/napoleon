package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name = "Offer", keyFields = "created")
@ServerInfo(name="Offer")
public class Offer extends CreateDocDataObject {
    public String email = "";
    public List<OfferItem> items = new ArrayList<>();
}

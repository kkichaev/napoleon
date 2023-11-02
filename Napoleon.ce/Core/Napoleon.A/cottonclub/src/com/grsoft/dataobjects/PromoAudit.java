package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="promoAudit", keyFields="created")
public class PromoAudit extends CreateDocDataObject {
	public List<AnswerId> answer = new ArrayList<AnswerId>();
}

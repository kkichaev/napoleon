package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="auditequip", keyFields="created")
public class AuditEquip extends CreateDocDataObject {
	public List<AuditEquipItem> items = new ArrayList<AuditEquipItem>();
}

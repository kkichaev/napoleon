package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name="income", keyFields = "created")
@ServerInfo(name="Income")
public class Income extends CreateDocDataObject {
    public String number = "";
    public List<IncomeItem> items = new ArrayList<>();
}

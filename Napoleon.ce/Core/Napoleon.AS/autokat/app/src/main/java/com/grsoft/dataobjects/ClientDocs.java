package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name="clientdocs", keyFields = "created")
public class ClientDocs extends CreateDocDataObject{
    public List<ClientDocsItem> items = new ArrayList<>();
}

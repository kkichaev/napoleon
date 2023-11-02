package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@TableInfo(name="DocsToSign", keyFields = "created")
public class DocsToSign extends DataObject {
    public Date created = new Date();
    public String number = "";
    public Date date = new Date();

    public List<DocsToSignItem> documents = new ArrayList<>();
}

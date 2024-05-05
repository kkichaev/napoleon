package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name = "newclient", keyFields = "id")
public class NewClient extends CreateDocDataObject{

    public String inn = "";
    public String name = "";
    public String address = "";
    public String phone = "+7";
    public String salesChannel = "";
    public String profile = "";
    public String typeTT = "";
    public String time1 = "";
    public String time2 = "";
    public String fio = "";
}

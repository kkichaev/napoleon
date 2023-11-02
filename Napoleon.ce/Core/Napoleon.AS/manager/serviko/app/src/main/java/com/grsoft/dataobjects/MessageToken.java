package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.dataobjects.DataObject;

@ServerInfo(name="MessageTokens")
public class MessageToken extends DataObject {
    public String token;
}

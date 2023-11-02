package com.grsoft.com.grsoft.database;

import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.MessageToken;
import com.grsoft.network.ObjectExportListener;

public class MessageTokenSender extends Hitching implements ObjectExportListener {

    MessageToken token;

    public MessageTokenSender(String token) {
        super(MessageToken.class);
        this.token = new MessageToken();
        this.token.token = token;
    }

    @Override public int size() { return 1; }
    @Override public DataObject get(int i) { return i == 0 ? token : null; }
}

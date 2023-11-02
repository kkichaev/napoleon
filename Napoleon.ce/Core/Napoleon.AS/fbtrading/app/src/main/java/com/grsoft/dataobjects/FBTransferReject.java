package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

import java.util.Date;

@TableInfo(name="trrejects", keyFields = "created")
@ServerInfo(name="TransferReject")
public class FBTransferReject extends CreateDocDataObject {
    public String agent = "";

    public static FBTransferReject createFrom(FBTransfer ref) {
        FBTransferReject ret = new FBTransferReject();

        ret.created = ref.created;
        ret.agent = ref.userid;

        return ret;
    }
}

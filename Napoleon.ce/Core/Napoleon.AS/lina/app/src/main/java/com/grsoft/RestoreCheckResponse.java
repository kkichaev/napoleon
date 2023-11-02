package com.grsoft;

import com.grsoft.database.DataObjectRestore;
import com.grsoft.dataobjects.CheckResponse;

public class RestoreCheckResponse extends DataObjectRestore {
    public RestoreCheckResponse() {
        super(CheckResponse.class, "ArchiveChekResponse", "created");
    }
}

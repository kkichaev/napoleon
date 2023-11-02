package com.grsoft.network;

import com.grsoft.database.DbWriter;

public class UploadContext {
    public DbWriter writer;

    public UploadContext() {
        writer = new DbWriter();
    }

    public void close() {
        writer.close();
    }
}

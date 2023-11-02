package com.grsoft.network;

import com.grsoft.napoleon.util.Config;
import com.grsoft.network.exception.UploadException;

public interface DataUploader {
    void upload(UploadContext context) throws UploadException;
}

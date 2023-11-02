package com.ashberrysoft.leadertask.interfaces;

import org.apache.http.HttpResponse;

public interface EntityRequestInterface extends Runnable {

    boolean startRequest();

    HttpResponse sendRequest() throws Exception;

    void parseResponse(HttpResponse httpResponse) throws Exception;

    String getMethodName();
}
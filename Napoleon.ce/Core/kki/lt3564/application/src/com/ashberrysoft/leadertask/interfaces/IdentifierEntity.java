package com.ashberrysoft.leadertask.interfaces;

import java.util.UUID;

public interface IdentifierEntity {


    UUID getId();

    int getIdTask();

    void setId(int id);

    String getUid();

    void setUid(String uid);
}
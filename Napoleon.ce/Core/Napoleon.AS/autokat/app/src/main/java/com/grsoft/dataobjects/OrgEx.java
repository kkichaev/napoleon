package com.grsoft.dataobjects;

public class OrgEx extends Org {
    public String orgType = "";
    public String remark = "";
    public String orgFormat = "";

    public void markDirty() {
        flags |= FL_USER_CREATED;
        flags &= (~FL_EXPORTED);
    }

    /**
     * Частник или нет
     *
     * @return
     */
    public boolean isPerson() {
        return id.equals("0000000000");
    }

    public boolean locationValid() {
        return latitude != 0 && longitude != 0;
    }
}

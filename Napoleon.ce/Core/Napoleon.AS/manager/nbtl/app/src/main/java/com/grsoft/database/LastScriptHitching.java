package com.grsoft.database;

import com.grsoft.dataobjects.LastScript;

public class LastScriptHitching extends HitchOnSelect {
    public LastScriptHitching(String userid) {
        super(LastScript.class, "LastScript");
        setCondition(String.format(" \"userid\" = '%s'", userid));
    }
}

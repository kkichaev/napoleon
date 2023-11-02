package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.script.dataobjects.ScriptDef;

import java.util.List;

public class ScriptDefEx extends ScriptDef {
    public int active = 0;

    public static ScriptDefEx getActive() {
        List<ScriptDefEx> res = DbReader.fetch(ScriptDefEx.class, "active <> 0");
        return res.size() == 0 ? null : res.get(0);
    }
}

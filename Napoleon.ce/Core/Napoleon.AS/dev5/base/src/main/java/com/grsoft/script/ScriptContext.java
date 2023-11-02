package com.grsoft.script;
import com.grsoft.aceteam.R;

import android.content.Context;

import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.dataobjects.impl.ScriptImpl;

import java.util.List;

public class ScriptContext {
    private ScriptImpl document;
    private int pos;

    /**
     *
     * @param script Текущий документ сценарий
     * @param index Индекс выбранного документа внутри сценария
     */
    public ScriptContext(ScriptImpl script, int index){
        this.document = script;
        this.pos = index;
    }

    public ScriptImpl getScript(){
        return  document;
    }

    public  int getIndex(){
        return pos;
    }

    public boolean canOpenNext() {
        if(document.isExported()) return true;

        boolean ret = false;
        ScriptDefImpl def = new ScriptDefImpl();

        if (def.read(document.getData().scriptId)){
            List<ScriptDefItem> item = def.getData().items;

            ret = item.size() > pos && item.get(pos).canSkip();
        }

        def.close();
        return ret;
    }

    public void openNext(Context context) {
        ScriptDefImpl def = new ScriptDefImpl();

        if (def.read(document.getData().scriptId)){
            document.openDoc(context, pos + 1, def.getData());
        }

        def.close();
    }
}

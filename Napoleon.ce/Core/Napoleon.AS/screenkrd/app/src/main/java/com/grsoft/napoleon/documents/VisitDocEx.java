package com.grsoft.napoleon.documents;

public class VisitDocEx extends VisitDoc {
    public static void initialize() {
        if( instance != null )
            throw new RuntimeException("VisitDocEx уже создан!");
        instance = new VisitDocEx();
    }

    @Override
    public boolean outOfScript() {
        return true;
    }
}

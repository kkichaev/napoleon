package com.grsoft.database;

public class PODHitchingEx extends PODHitching {
    @Override
    protected ProceededDocHandler createHandler() {
        return new ProceededDocHandlerEx();
    }
}

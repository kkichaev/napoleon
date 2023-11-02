package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.Date;

@TableInfo(name="autowaybill", keyFields = "created")
public class AutoWaybill extends CreateDocDataObject{
    public Date visit;
    public int closed = 0;

    @Scale(value= Consts.DISTANCE_SCALE)
    public int startKM;
    @Scale(value= Consts.DISTANCE_SCALE)
    public int finishKM;

    @Scale(value= Consts.QTY_SCALE)
    public int fuel1Start;

    @Scale(value= Consts.QTY_SCALE)
    public int fuel2Start;

    @Scale(value= Consts.QTY_SCALE)
    public int fuel1Finish;

    @Scale(value= Consts.QTY_SCALE)
    public int fuel2Finish;

    @Scale(value= Consts.QTY_SCALE)
    public int fuel1Input;

    @Scale(value= Consts.QTY_SCALE)
    public int fuel2Input;
}

package com.grsoft.dataobjects;

import com.grsoft.script.dataobjects.Script;

import java.util.Date;

public class ScriptEx extends Script {
    public enum PSTATYS { NOT_CHECK, SUCCESS, OPERATOR}
    public static final int PASSPORT_RF = 0;
    // foreign doc
    public static final int PASSPORT_FGN = 1;

    public static Date NULL_DATE = new Date(Date.parse("01 Jan 1900 00:00:00 GMT"));

    public String fio = "";
    public String phone = "";
    public String passportSeria = "";
    public String passportNumber = "";
    public String issueOrg = "";
    public String issueCode = "";
    public String clientType = "";
    public String payType = "";
    public String address = "";
    public int passportType = 0;
    public Date finish;
    public Date birthday = NULL_DATE;
    public Date passportIssue = NULL_DATE;
    public Date visitDoc;
    public Date clientDoc;
    public int pstatus = PSTATYS.NOT_CHECK.ordinal();

    public boolean issueValid() { return passportIssue.compareTo(NULL_DATE) > 0; }

    public boolean birthdayValid() {return birthday.compareTo(NULL_DATE) > 0; }
}

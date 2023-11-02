package com.ashberrysoft.leadertask.modern.generate;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;

public class LionEntityGenerator implements Runnable {

    // BASE
    private final String mLionName;
    private final String mString;

    public LionEntityGenerator(Context context) {
        mLionName = "LionTask";
        mString = "<Uid xmlns=\"\">string</Uid>            <UIDParent xmlns=\"\">string</UIDParent>            <Order xmlns=\"\">string</Order>            <Collapsed xmlns=\"\">string</Collapsed>            <Name xmlns=\"\">string</Name>            <Comment xmlns=\"\">string</Comment>            <Status xmlns=\"\">string</Status>            <TermBegin xmlns=\"\">string</TermBegin>            <TermEnd xmlns=\"\">string</TermEnd>            <EmailPerformer xmlns=\"\">string</EmailPerformer>            <UidProject xmlns=\"\">string</UidProject>            <UidMarker xmlns=\"\">string</UidMarker>            <Readed xmlns=\"\">string</Readed>            <OrderCustomer xmlns=\"\">string</OrderCustomer>            <TermBeginCustomer xmlns=\"\">string</TermBeginCustomer>            <TermEndCustomer xmlns=\"\">string</TermEndCustomer>            <EmailCustomer xmlns=\"\">string</EmailCustomer>            <Categories xmlns=\"\">string</Categories>            <Contacts xmlns=\"\">string</Contacts>            <CreateTime xmlns=\"\">string</CreateTime>            <PerformTime xmlns=\"\">string</PerformTime>            <CompleteTime xmlns=\"\">string</CompleteTime>            <SeriesType xmlns=\"\">string</SeriesType>            <SeriesAfterType xmlns=\"\">string</SeriesAfterType>            <SeriesAfterCount xmlns=\"\">string</SeriesAfterCount>            <SeriesWeekCount xmlns=\"\">string</SeriesWeekCount>            <SeriesWeekMon xmlns=\"\">string</SeriesWeekMon>            <SeriesWeekTue xmlns=\"\">string</SeriesWeekTue>            <SeriesWeekWed xmlns=\"\">string</SeriesWeekWed>            <SeriesWeekThu xmlns=\"\">string</SeriesWeekThu>            <SeriesWeekFri xmlns=\"\">string</SeriesWeekFri>            <SeriesWeekSat xmlns=\"\">string</SeriesWeekSat>            <SeriesWeekSun xmlns=\"\">string</SeriesWeekSun>            <SeriesMonthType xmlns=\"\">string</SeriesMonthType>            <SeriesMonthCount xmlns=\"\">string</SeriesMonthCount>            <SeriesMonthDay xmlns=\"\">string</SeriesMonthDay>            <SeriesMonthWeekType xmlns=\"\">string</SeriesMonthWeekType>            <SeriesMonthDayOfWeek xmlns=\"\">string</SeriesMonthDayOfWeek>            <SeriesYearType xmlns=\"\">string</SeriesYearType>            <SeriesYearMonth xmlns=\"\">string</SeriesYearMonth>            <SeriesYearMonthDay xmlns=\"\">string</SeriesYearMonthDay>            <SeriesYearWeekType xmlns=\"\">string</SeriesYearWeekType>            <SeriesYearDayOfWeek xmlns=\"\">string</SeriesYearDayOfWeek>            <SeriesEnd xmlns=\"\">string</SeriesEnd>            <__usn_entity xmlns=\"\">string</__usn_entity>            <__usn_field_uid_parent xmlns=\"\">string</__usn_field_uid_parent>            <__usn_field_email_performer xmlns=\"\">string</__usn_field_email_performer>            <__usn_field_name xmlns=\"\">string</__usn_field_name>            <__usn_field_comment xmlns=\"\">string</__usn_field_comment>            <__usn_field_status xmlns=\"\">string</__usn_field_status>            <__usn_field_order xmlns=\"\">string</__usn_field_order>            <__usn_field_uid_project xmlns=\"\">string</__usn_field_uid_project>            <__usn_field_uid_marker xmlns=\"\">string</__usn_field_uid_marker>            <__usn_field_term xmlns=\"\">string</__usn_field_term>            <__usn_field_readed xmlns=\"\">string</__usn_field_readed>            <__usn_field_collapsed xmlns=\"\">string</__usn_field_collapsed>            <__usn_field_customer_order xmlns=\"\">string</__usn_field_customer_order>            <__usn_field_customer_term xmlns=\"\">string</__usn_field_customer_term>            <__usn_field_categories xmlns=\"\">string</__usn_field_categories>            <__usn_field_contacts xmlns=\"\">string</__usn_field_contacts>            <__usn_field_createtime xmlns=\"\">string</__usn_field_createtime>            <__usn_field_performtime xmlns=\"\">string</__usn_field_performtime>            <__usn_field_completetime xmlns=\"\">string</__usn_field_completetime>            <__usn_field_series xmlns=\"\">string</__usn_field_series>";
    }

    @Override
    public void run() {
        try {

            final String lionTableName;
            {
                final String rightSide = mLionName.replace("Lion", "");
                lionTableName = "TABLE_" + mLionName.replace(rightSide, "_").toUpperCase() + rightSide.toUpperCase();
            }
            final String lionContractName = mLionName + "Contract";
            final List<KeyValue> list = parseString();

            writeToFile(lionContractName, createContract(lionTableName, lionContractName, list));
            writeToFile(mLionName, createEntity(lionTableName, lionContractName, list));

        } catch (Exception e) {
            Utils.toLog(e);
        }
    }

    private void writeToFile(String fileName, String value) {
        writeToFile(new File("sdcard/" + fileName + ".java"), value);
    }

    public static void writeToFile(File file, String value) {
        PrintWriter printWriter = null;
        try {
            file.mkdirs();
            file.delete();
            file.createNewFile();

            printWriter = new PrintWriter(file);
            printWriter.print(value);

        } catch (Exception e) {
            Utils.toLog(e);

        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    public List<KeyValue> parseString() {
        final List<KeyValue> list = new ArrayList<>();

        final String[] byLines = filterString().split("!");
        for (String s : byLines) {
            final String[] byKeyValue = s.split("-");
            list.add(new KeyValue(byKeyValue[0], byKeyValue[1]));
        }

        return list;
    }

    private String filterString() {
        return mString.replace(" ", "").replace("<", "").replace("xmlns=", "-").replace("\"", "").replace("/", "")
                .replace(">string", "").replace(">", "!").replace("\n", "");
    }

    public String createContract(String lionTableName, String lionContractName, List<KeyValue> list) {
        final StringBuilder sb = new StringBuilder();

        sb.append("public static final class ");
        sb.append(lionContractName);
        sb.append(" extends BaseLionColumns {\n");

        sb.append("public static final String ");
        sb.append(lionTableName);
        sb.append(" = \"");
        sb.append(mLionName);
        sb.append("\";\n");

        sb.append("public static final String TABLE_NAME");
        sb.append(" = ");
        sb.append(lionTableName);
        sb.append(";\n");

        sb.append("public static final Uri CONTENT_URI = getContentUri(TABLE_NAME);\n\n");

        for (KeyValue keyValue : list) {
            sb.append("public static final String ");
            sb.append(keyValue.key);
            sb.append(" = \"");
            sb.append(keyValue.value);
            sb.append("\";\n");
        }

        sb.append("}");

        return sb.toString();
    }

    public String createEntity(String lionTableName, String lionContractName, List<KeyValue> list) {
        final StringBuilder sb = new StringBuilder();

        sb.append("public class ");
        sb.append(mLionName);
        sb.append(" extends BaseLion<LTask> {\nprivate static final int serialVersionUID = ");
        sb.append(mLionName.hashCode());
        sb.append("L;\n\n");

        sb.append(createVariables(lionTableName, lionContractName, list));

        sb.append("private static int[] sColumns;\n\n");

        sb.append("public ");
        sb.append(mLionName);
        sb.append("(){}\n\n");

        sb.append("@Override public int getIdTask() { return mId; }\n\n");
        sb.append("@Override public String getUid() { return mUid; }\n\n");
        sb.append("@Override public int getUsnEntity() { return mUsnEntity; }\n\n");

        sb.append("@Override\npublic String getLionName() {\nreturn ");
        sb.append(lionContractName);
        sb.append(".TABLE_NAME;\n}\n\n");

        sb.append("@Override\npublic Uri getContentUri() {\nreturn ");
        sb.append(lionContractName);
        sb.append(".CONTENT_URI;\n}\n\n");

        sb.append(createCursorFiller(lionTableName, lionContractName, list));
        sb.append(createEntityFiller(lionTableName, lionContractName, list));
        sb.append(createEntityDifference(lionTableName, lionContractName, list));
        sb.append(createGettersAndSetters(lionTableName, lionContractName, list));
        sb.append("}");

        return sb.toString();
    }

    private String createVariables(String lionTableName, String lionContractName, List<KeyValue> list) {
        final StringBuilder sb = new StringBuilder();

        sb.append("@DatabaseField(columnName =");
        sb.append(lionContractName);
        sb.append("._ID");
        sb.append(", dataType = DataType.INTEGER, generatedId = true)\nprivate int mId;\n\n");

        sb.append("@DatabaseField(columnName =");
        sb.append(lionContractName);
        sb.append(".Orders");
        sb.append(", dataType = DataType.INTEGER)\nprivate int mOrder;\n\n");

        for (KeyValue keyValue : list) {
            sb.append("@DatabaseField(columnName =");
            sb.append(lionContractName);
            sb.append(".");
            sb.append(keyValue.key);
            sb.append(", dataType = DataType.");
            sb.append(keyValue.numeric ? "INTEGER" : "STRING");
            sb.append(", index = true)\n");

            sb.append("private ");
            sb.append(keyValue.numeric ? "int" : "String");
            sb.append(" m");
            sb.append(keyValue.key);
            sb.append(";\n\n");
        }

        return sb.toString();
    }

    private String createCursorFiller(String lionTableName, String lionContractName, List<KeyValue> list) {
        final StringBuilder sb = new StringBuilder();

        sb.append("@Override\npublic ContentValues getContentValues(ContentValues cv) {\n");
        sb.append("if (cv==null){\ncv=new ContentValues(");
        sb.append(list.size() + 1);
        sb.append(");\n} else{\ncv.clear();\n}\n");

        sb.append("cv.put(");
        sb.append(lionContractName);
        sb.append(".Orders");
        sb.append(", getOrder());\n");

        for (KeyValue keyValue : list) {
            if (keyValue.value.equalsIgnoreCase("order")) {
                continue;
            }

            sb.append("cv.put(");
            sb.append(lionContractName);
            sb.append(".");
            sb.append(keyValue.key);
            sb.append(", get");
            sb.append(keyValue.key);
            sb.append("());\n");
        }
        sb.append(" return cv;\n}\n");

        sb.append("@Override\npublic void fillFromCursor(Cursor cursor) {\n");
        sb.append("int count = 0;\nif (sColumns==null){\nsynchronized (");
        sb.append(mLionName);
        sb.append(".class){\nif (sColumns==null){\nsColumns = new int[cursor.getColumnCount()];\n");
        sb.append("for (String s : cursor.getColumnNames()) { sColumns[count++] = cursor.getColumnIndex(s); } }\n}\ncount = 0; for (String key : cursor.getColumnNames()) { fillKeyValue(key, cursor.getString(sColumns[count++]));}\n}");

        sb.append("}\n\n");

        return sb.toString();
    }

    private String createEntityFiller(String lionTableName, String lionContractName, List<KeyValue> list) {
        final StringBuilder sb = new StringBuilder();

        sb.append("@Override\npublic StringBuilder getLionEntity(StringBuilder sb) {\nfinal SoapBuilder soap = new SoapBuilder(sb);\nsoap.addStart(getLionName());\n");
        for (KeyValue keyValue : list) {
            sb.append("soap.");
            sb.append(keyValue.numeric ? "addNumeric" : "addString");
            sb.append("(");
            sb.append(lionContractName);
            sb.append(".");
            sb.append(keyValue.key);
            sb.append(", get");
            sb.append(keyValue.key);
            sb.append("());\n");
        }
        sb.append("soap.addEnd(getLionName());\nreturn soap.build();\n}\n\n");

        sb.append("@Override\npublic boolean fillKeyValue(String key, String value) {\nswitch (key) {");

        sb.append("case ");
        sb.append(lionContractName);
        sb.append("._ID");
        sb.append(":\nsetId(Integer.parseInt(value));\nreturn true;\n\n");

        sb.append("case ");
        sb.append(lionContractName);
        sb.append(".Orders");
        sb.append(":\nsetOrder(Integer.parseInt(value));\nreturn true;\n\n");

        for (KeyValue keyValue : list) {
            sb.append("case ");
            sb.append(lionContractName);
            sb.append(".");
            sb.append(keyValue.key);
            sb.append(":\nset");
            sb.append(keyValue.key);
            sb.append(keyValue.numeric ? "(Integer.parseInt(value)" : "(value");
            sb.append(");\nreturn true;\n\n");
        }
        sb.append("default:\nreturn false;\n}\n}\n\n");

        return sb.toString();
    }

    private String createGettersAndSetters(String lionTableName, String lionContractName, List<KeyValue> list) {
        final StringBuilder sb = new StringBuilder();

        sb.append("public void setId(int value){\nmId = value;\n}\n\n");
        sb.append("public int getIdTask(){\nreturn mId;\n}\n\n");

        sb.append("public void setOrder(int value){\nmOrder = value;\n}\n\n");
        sb.append("public int getOrder(){\nreturn mOrder;\n}\n\n");

        for (KeyValue keyValue : list) {
            sb.append("public void set");
            sb.append(keyValue.key);
            sb.append("(");
            sb.append(keyValue.numeric ? "int" : "String");
            sb.append(" value){\nm");
            sb.append(keyValue.key);
            sb.append(" = value;\n}\n\n");

            sb.append("public ");
            sb.append(keyValue.numeric ? "int" : "String");
            sb.append(" get");
            sb.append(keyValue.key);
            sb.append("(){\nreturn m");
            sb.append(keyValue.key);
            sb.append(";\n}\n\n");
        }

        return sb.toString();
    }

    private String createEntityDifference(String lionTableName, String lionContractName, List<KeyValue> list) {
        final StringBuilder sb = new StringBuilder();

        sb.append("@Override\npublic ContentValues getDifference(");
        sb.append(mLionName);
        sb.append(" entity) {\nfinal ContentValues cv = new ContentValues();\n\n");

        final List<UsnKeys> listUsnKeys = getKeys(list);
        appendUsnKeys(sb, listUsnKeys.get(0), lionContractName);
        listUsnKeys.remove(0);

        for (UsnKeys usnKeys : listUsnKeys) {
            sb.append("if (get");
            sb.append(usnKeys.usn);
            sb.append("() < entity.get");
            sb.append(usnKeys.usn);
            sb.append("()){\n");

            appendUsnKeys(sb, usnKeys, lionContractName);

            sb.append("} else if (get");
            sb.append(usnKeys.usn);
            sb.append("() > entity.get");
            sb.append(usnKeys.usn);
            sb.append("()){\n");

            sb.append("cv.put(");
            sb.append(lionContractName);
            sb.append(".UsnEntity, 0);}\n\n");
        }

        sb.append("return cv;\n}\n\n");

        return sb.toString();
    }

    private void appendUsnKeys(StringBuilder sb, UsnKeys usnKeys, String lionContractName) {
        appendContentValues(sb, lionContractName, usnKeys.usn);

        if (usnKeys.keys.isEmpty()) {
            sb.append("//TODO no found difference comparison\n");

        } else {
            for (String s : usnKeys.keys) {
                appendContentValues(sb, lionContractName, s);
            }
        }

        sb.append(SharedStrings.NEW_LINE_C);
    }

    /** @Warning may contains inconsistency */
    private List<UsnKeys> getKeys(List<KeyValue> list) {
        final List<UsnKeys> listUsnKeys = new ArrayList<>();

        final List<KeyValue> listCopy = new ArrayList<>(list);
        Iterator<KeyValue> iterator = listCopy.iterator();
        boolean hasUsnField = false;

        while (true) {
            if (iterator.hasNext()) {
                final KeyValue keyValue = iterator.next();
                if (keyValue.key.startsWith("UsnField")) {
                    hasUsnField = true;
                    iterator.remove();

                    final UsnKeys usnKeys = new UsnKeys();
                    usnKeys.setUsn(keyValue.key);
                    usnKeys.setKeys(getKeysForUsn(usnKeys.usn, listCopy.iterator()));

                    listUsnKeys.add(usnKeys);

                    iterator = listCopy.iterator();
                    hasUsnField = false;
                }

            } else {
                if (!hasUsnField) {
                    break;
                }

                iterator = listCopy.iterator();
                hasUsnField = false;
            }
        }

        iterator = listCopy.iterator();
        while (iterator.hasNext()) {
            final KeyValue keyValue = iterator.next();
            if ("UsnEntity".equals(keyValue.key)) {
                iterator.remove();

                final UsnKeys usnKeys = new UsnKeys();
                usnKeys.setUsn(keyValue.key);

                final List<String> keys = new ArrayList<>(listCopy.size());
                for (KeyValue kv : listCopy) {
                    keys.add(kv.key);
                }
                usnKeys.setKeys(keys);

                listUsnKeys.add(0, usnKeys);
            }
        }

        return listUsnKeys;
    }

    private List<String> getKeysForUsn(String usn, Iterator<KeyValue> iterator) {
        final String part = usn.replace("UsnField", "").toLowerCase();
        final List<String> keys = new ArrayList<>();

        while (iterator.hasNext()) {
            final KeyValue keyValue = iterator.next();
            if (!keyValue.key.startsWith("Usn") && keyValue.key.toLowerCase().contains(part)) {
                iterator.remove();
                keys.add(keyValue.key);
            }
        }

        return keys;
    }

    private void appendContentValues(StringBuilder sb, String lionContractName, String key) {
        sb.append("cv.put(");
        sb.append(lionContractName);
        sb.append(".");
        sb.append(key);
        sb.append(", entity.get");
        sb.append(key);
        sb.append("());\n");
    }

    private static final class KeyValue {

        private String key;
        private String value;
        private boolean numeric;

        public KeyValue(String key, String value) {
            setKey(key);
            setValue(value);
        }

        public void setKey(String key) {
            final StringBuilder sb = new StringBuilder(key.replace("__", ""));
            this.numeric = key.length() != sb.length();

            toUpperCase(sb, 0);
            for (int i = 0; i < sb.length(); i++) {
                if (sb.charAt(i) == '_') {
                    toUpperCase(sb, i + 1);
                }
            }

            this.key = sb.toString().replace("_", "");
        }

        public void setValue(String value) {
            this.value = value.toLowerCase();
        }

        private void toUpperCase(StringBuilder sb, int position) {
            final String firstChar = String.valueOf(sb.charAt(position)).toUpperCase();
            sb.setCharAt(position, firstChar.charAt(0));
        }
    }

    private static final class UsnKeys {

        private String usn;
        private List<String> keys;

        public void setUsn(String usnKey) {
            this.usn = usnKey;
        }

        public void setKeys(List<String> keys) {
            this.keys = keys;
        }

        @Override
        public String toString() {
            return "usn = " + usn + "\t keys" + Arrays.toString(keys.toArray());
        }
    }
}
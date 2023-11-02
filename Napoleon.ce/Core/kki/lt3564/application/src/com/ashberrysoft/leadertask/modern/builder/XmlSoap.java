package com.ashberrysoft.leadertask.modern.builder;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringEscapeUtils;

import android.text.TextUtils;

import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;

public final class XmlSoap {

    private static final String FORMAT_OPEN = "<";
    private static final String FORMAT_CLOSE = ">";
    private static final String FORMAT_OPEN_CLOSE = "</";
    private static final String FORMAT_CLOSE_XMLNS = " xmlns=\"\">";
    private static final String FORMAT_CLOSE_OPEN_XMLNS = " xmlns=\"\"/>";
    private static final String BEGIN_TERM_DATE = "01.01.1900 00:00:00";
    private static final String END_TERM_DATE = "01.01.9000 23:59:59";

    public static String getOpen(String value) {
        return getOpen(null, value);
    }

    public static String getOpen(StringBuilder sb, String value) {
        if (sb == null) {
            sb = new StringBuilder();
        }

        sb.append(FORMAT_OPEN);
        sb.append(value);
        sb.append(FORMAT_CLOSE);

        return sb.toString();
    }

    public static String getClose(String value) {
        return getClose(null, value);
    }

    public static String getClose(StringBuilder sb, String value) {
        if (sb == null) {
            sb = new StringBuilder();
        }

        sb.append(FORMAT_OPEN_CLOSE);
        sb.append(value);
        sb.append(FORMAT_CLOSE);

        return sb.toString();
    }

    private static String getOpenXmlns(StringBuilder sb, String value) {
        if (sb == null) {
            sb = new StringBuilder();
        }

        sb.append(FORMAT_OPEN);
        sb.append(value);
        sb.append(FORMAT_CLOSE_XMLNS);

        return sb.toString();
    }

    private static String getOpenCloseXmlns(StringBuilder sb, String value) {
        if (sb == null) {
            sb = new StringBuilder();
        }

        sb.append(FORMAT_OPEN);
        sb.append(value);
        sb.append(FORMAT_CLOSE_OPEN_XMLNS);

        return sb.toString();
    }

    private static String createValueLine(String tag, String value) {
        final StringBuilder sb = new StringBuilder();

        getOpen(sb, tag);
        sb.append(value);
        getClose(sb, tag);

        return sb.toString();
    }

    public static String getValueLine(String tag, String value) {
        return createValueLine(tag, value);
    }

    private static String createXmlnsValueLine(String tag, String value) {
        final StringBuilder sb = new StringBuilder();

        getOpenXmlns(sb, tag);
        sb.append(value);
        getClose(sb, tag);

        return sb.toString();
    }

    public static String createXmlnsNoValueLine(String tag) {
        return getOpenCloseXmlns(null, tag);
    }

    public static String getXmlnsValueLine(String tag, String value) {
        if (TextUtils.isEmpty(value)) {
            return createXmlnsNoValueLine(tag);
        }
        return createXmlnsValueLine(tag, StringEscapeUtils.escapeXml(value));
    }

    public static String getXmlnsValueLine(String tag, int value) {
        return createXmlnsValueLine(tag, String.valueOf(value));
    }

    public static String getXmlnsValueLine(String tag, boolean value) {
        return createXmlnsValueLine(tag, value ? SharedStrings.ONE : SharedStrings.ZERO);
    }

    public static String getXmlnsValueLine(String tag, long value) {
        return createXmlnsValueLine(tag, String.valueOf(value));
    }

    public static String getXmlnsValueLine(String tag, double value) {
        return createXmlnsValueLine(tag, String.valueOf(value));
    }

    public static String getXmlnsValueLine(String tag, UUID value) {
        if (value == null) {
            return createXmlnsNoValueLine(tag);
        }
        return createXmlnsValueLine(tag, value.toString());
    }

    public static String getXmlnsValueLine(String tag, Date value, boolean beginTerm) {
        return createXmlnsValueLine(tag, value != null ? TaskHelper.getTaskFormat().format(value) : //
                beginTerm ? BEGIN_TERM_DATE : END_TERM_DATE);
    }

    public static String getXmlnsValueLine(String tag, Date value) {
        return createXmlnsValueLine(tag, value == null ? END_TERM_DATE : TaskHelper.getTaskFormat().format(value));
    }

    public static boolean equalsOne(String value) {
        return SharedStrings.ONE.equals(value);
    }

    public static boolean equalsOne(int value) {
        return value == 1;
    }

    public static Date parseDate(String value) {
        switch (value) {
        case BEGIN_TERM_DATE:
        case END_TERM_DATE:
            return null;

        default:
            try {
                final Date date = TaskHelper.getTaskFormat().parse(value);
                return date;

            } catch (ParseException e) {
                Utils.toLog(e);
                return null;
            }
        }
    }

    private static long parseDateLong(String value) {
        try {
            return Long.parseLong(value);

        } catch (Exception e) {
            return 0;
        }
    }

    public static long parseDateMillis(String value) {
        long answer = 0;
        if (value != null) {
            if (TextUtils.isDigitsOnly(value)) {
                answer = parseDateLong(value);

            } else {
                switch (value) {
                case BEGIN_TERM_DATE:
                case END_TERM_DATE:
                    break;

                default:
                    try {
                        answer = TaskHelper.getTaskFormat().parse(value).getTime();

                    } catch (Exception e) {
                        Utils.toLog(e);
                    }
                }
            }
        }
        return answer;
    }

    public static final class Builder {
        private final StringBuilder mSb;
        private final Date mDate;

        public Builder(StringBuilder sb) {
            mSb = sb;
            mDate = new Date();
        }

        public Builder() {
            this(new StringBuilder());
        }

        public Builder addStart(String lionName) {
            mSb.append(XmlSoap.getOpen(lionName));

            return this;
        }

        public Builder addString(String key, String value) {
            mSb.append(XmlSoap.getXmlnsValueLine(key, value));

            return this;
        }

        public Builder addNumeric(String key, long value) {
            mSb.append(XmlSoap.getXmlnsValueLine(key, value));

            return this;
        }

        public Builder addNumeric(String key, double value) {
            mSb.append(XmlSoap.getXmlnsValueLine(key, value));

            return this;
        }

        public Builder addDate(String key, long value) {
            if (value == 0) {
                mSb.append(XmlSoap.getXmlnsValueLine(key, (Date) null));

            } else {
                mDate.setTime(value);
                mSb.append(XmlSoap.getXmlnsValueLine(key, mDate));
            }

            return this;
        }

        public Builder addDate(String key, long value, boolean beginTerm) {
            if (value == 0) {
                mSb.append(XmlSoap.getXmlnsValueLine(key, null, beginTerm));

            } else {
                mDate.setTime(value);
                mSb.append(XmlSoap.getXmlnsValueLine(key, mDate, beginTerm));
            }

            return this;
        }

        public Builder addBoolean(String key, boolean value) {
            mSb.append(XmlSoap.getXmlnsValueLine(key, value));

            return this;
        }

        public Builder addEnd(String lionName) {
            mSb.append(XmlSoap.getClose(lionName));

            return this;
        }

        public StringBuilder build() {
            return mSb;
        }
    }
}
package com.ashberrysoft.leadertask.xml_handlers.unique;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.interfaces.ProcessSOAPResponseConstants;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.xml_handlers.ErrorEntity;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.list.ListEmployeeHandler;
import com.ashberrysoft.leadertask.xml_handlers.unique.VerifyUserHandler.VerifyUserEntity;

public class VerifyUserHandler extends BaseXmlSaxHandlerProcessAll<VerifyUserEntity>//
        implements ProcessSOAPResponseConstants {

    private StringBuilder mStringBuilder;
    private ListEmployeeHandler mListEmployeeHandler;
    private boolean afterInfo = false;

    public VerifyUserHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
        mData = new VerifyUserEntity();

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        super.startElement(uri, localName, qName, atts);

        if (ERROR_CODE.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (ERROR_STRING.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.COUNT_BYTES.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.END_DATE.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.EMPLOYEES.equalsIgnoreCase(localName)) {
            mListEmployeeHandler = new ListEmployeeHandler(mReader, this);
            mReader.setContentHandler(mListEmployeeHandler);
            mListEmployeeHandler.startElement(uri, localName, qName, atts);
        }

        else if (VerifyUserEntity.USERID.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.ADDINS.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.AVAILABLE_BYTES.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.NAME_ORG.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.EMAIL_DIRECTOR.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.NAME_DIRECTOR.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.KEY.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.COUNT.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.INVITE_UID.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.SETTINGS.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.add_task_to_begin.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.cal_number_of_first_week.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.cal_show_week_number.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.nav_show_tags.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.nav_show_overdue.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.nav_show_summary.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.nav_show_emps.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.nav_show_markers.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity._language.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.stopwatch.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.cal_work_time.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.reminders_in_n_minutes.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.__usn_field_add_task_to_begin.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.__usn_field_cal_number_of_first_week.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.__usn_field_cal_show_week_number.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.__usn_field_nav_show_tags.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.__usn_field_nav_show_overdue.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.__usn_field_nav_show_summary.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.__usn_field_nav_show_emps.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.__usn_field_nav_show_markers.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.__usn_field_language.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.__usn_field_stopwatch.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }


        else if (VerifyUserEntity.__usn_field_cal_work_time.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (VerifyUserEntity.__usn_field_reminders_in_n_minutes.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }


    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        if (ERROR_CODE.equalsIgnoreCase(localName)) {
            mData.setErrorCode(Integer.parseInt(mStringBuilder.toString()));
            mStringBuilder = null;
        }

        else if (ERROR_STRING.equalsIgnoreCase(localName)) {
            mData.setMessage(mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.COUNT_BYTES.equalsIgnoreCase(localName)) {
            mData.setCountBytes(mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.END_DATE.equalsIgnoreCase(localName)) {
            mData.setEndDate(mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.EMPLOYEES.equalsIgnoreCase(localName)) {
            if (mListEmployeeHandler != null) {
                mData.setEmployees(mListEmployeeHandler.getData());
                mListEmployeeHandler = null;
            }
        }

        else if (VerifyUserEntity.USERID.equalsIgnoreCase(localName)) {
            mData.setUserId(mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.ADDINS.equalsIgnoreCase(localName)) {
            mData.setAddins(mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.AVAILABLE_BYTES.equalsIgnoreCase(localName)) {
            mData.setAvailableBytes(mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.NAME_ORG.equalsIgnoreCase(localName)) {
            if (!afterInfo) {
                mData.setNameOrg(mStringBuilder.toString());
                mStringBuilder = null;
            } else {
                mData.setInviteOrg(mStringBuilder.toString());
            }
        }

        else if (VerifyUserEntity.EMAIL_DIRECTOR.equalsIgnoreCase(localName)) {
            if (!afterInfo) {
                mData.setEmailDirector(mStringBuilder.toString());
            } else {
                mData.setInviteEmail(mStringBuilder.toString());
            }
            mStringBuilder = null;

        }

        else if (VerifyUserEntity.NAME_DIRECTOR.equalsIgnoreCase(localName)) {
            if (!afterInfo) {
                mData.setNameDirector(mStringBuilder.toString());
                afterInfo = true;
            } else {
                mData.setInviteName(mStringBuilder.toString());
            }
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.KEY.equalsIgnoreCase(localName)) {
            mData.setKey(mStringBuilder.toString());
            mStringBuilder = null;

        }

        else if (VerifyUserEntity.COUNT.equalsIgnoreCase(localName)) {
            mData.setCount(mStringBuilder.toString());
            mStringBuilder = null;

        }

        else if (VerifyUserEntity.INVITE_UID.equalsIgnoreCase(localName)) {
            mData.setInviteUUID(mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.SETTINGS.equalsIgnoreCase(localName)) {
            mData.setSettings();
            mStringBuilder = null;
        }

        //////////////////////////////////////////////////////////////////////////

        else if (VerifyUserEntity.add_task_to_begin.equalsIgnoreCase(localName)) {
            mData.setadd_task_to_begin (mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.cal_number_of_first_week.equalsIgnoreCase(localName)) {
            mData.setcal_number_of_first_week (mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.cal_show_week_number.equalsIgnoreCase(localName)) {
            mData.setcal_show_week_number (mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.nav_show_tags.equalsIgnoreCase(localName)) {
            mData.setnav_show_tags (mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.nav_show_overdue.equalsIgnoreCase(localName)) {
            mData.setnav_show_overdue (mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.nav_show_summary.equalsIgnoreCase(localName)) {
            mData.setnav_show_summary (mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.nav_show_emps.equalsIgnoreCase(localName)) {
            mData.setnav_show_emps (mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.nav_show_markers.equalsIgnoreCase(localName)) {
            mData.setnav_show_markers (mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity._language.equalsIgnoreCase(localName)) {
            mData.setlanguage (mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.stopwatch.equalsIgnoreCase(localName)) {
            mData.setstopwatch(mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.cal_work_time.equalsIgnoreCase(localName)) {
            mData.setCalendarTime(mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.reminders_in_n_minutes.equalsIgnoreCase(localName)) {
            mData.setreminders_in_n_minutes(mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.__usn_field_add_task_to_begin.equalsIgnoreCase(localName)) {
            mData.set__usn_field_add_task_to_begin (mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.__usn_field_cal_number_of_first_week.equalsIgnoreCase(localName)) {
            mData.set__usn_field_cal_number_of_first_week (mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.__usn_field_cal_show_week_number.equalsIgnoreCase(localName)) {
            mData.set__usn_field_cal_show_week_number (mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.__usn_field_nav_show_tags.equalsIgnoreCase(localName)) {
            mData.set__usn_field_nav_show_tags (mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.__usn_field_nav_show_overdue.equalsIgnoreCase(localName)) {
            mData.set__usn_field_nav_show_overdue (mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.__usn_field_nav_show_summary.equalsIgnoreCase(localName)) {
            mData.set__usn_field_nav_show_summary (mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.__usn_field_nav_show_emps.equalsIgnoreCase(localName)) {
            mData.set__usn_field_nav_show_emps (mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.__usn_field_nav_show_markers.equalsIgnoreCase(localName)) {
            mData.set__usn_field_nav_show_markers (mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.__usn_field_language.equalsIgnoreCase(localName)) {
            mData.set__usn_field_language (mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.__usn_field_stopwatch.equalsIgnoreCase(localName)) {
            mData.set__usn_field_stopwatch (mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.__usn_field_cal_work_time.equalsIgnoreCase(localName)) {
            mData.set__usn_field_cal_work_time(mStringBuilder.toString());
            mStringBuilder = null;
        }

        else if (VerifyUserEntity.__usn_field_reminders_in_n_minutes.equalsIgnoreCase(localName)) {
            mData.set__usn_field_reminders_in_n_minutes(mStringBuilder.toString());
            mStringBuilder = null;
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);

        if (mStringBuilder != null) {
            for (int i = start; i < length; i++) {
                mStringBuilder.append(ch[i]);
            }
        }
    }

    public static class VerifyUserEntity extends ErrorEntity {
        private static final long serialVersionUID = 1L;

        public static final String COUNT_BYTES = "count_bytes";
        public static final String AVAILABLE_BYTES = "available_bytes";
        public static final String NAME_ORG = "name_org";
        public static final String EMAIL_DIRECTOR = "email_director";
        public static final String NAME_DIRECTOR = "name_director";
        public static final String INVITE_UID = "uid_invite";
        public static final String END_DATE = "end_date";
        public static final String EMPLOYEES = "employees";
        public static final String USERID = "userid";
        public static final String ADDINS = "addins";
        public static final String KEY = "key";
        public static final String COUNT = "count";
        public static final String SETTINGS = "settings";


        //settings
        public static final String add_task_to_begin = "add_task_to_begin";
        public static final String cal_number_of_first_week = "cal_number_of_first_week";
        public static final String cal_show_week_number = "cal_show_week_number";
        public static final String nav_show_tags = "nav_show_tags";
        public static final String nav_show_overdue = "nav_show_overdue";
        public static final String nav_show_summary = "nav_show_summary";
        public static final String nav_show_emps = "nav_show_emps";
        public static final String nav_show_markers = "nav_show_markers";
        public static final String _language = "language";
        public static final String stopwatch = "stopwatch";
        public static final String cal_work_time = "cal_work_time";
        public static final String reminders_in_n_minutes = "reminders_in_n_minutes";

        public static final String __usn_field_add_task_to_begin = "__usn_field_add_task_to_begin";
        public static final String __usn_field_cal_number_of_first_week = "__usn_field_cal_number_of_first_week";
        public static final String __usn_field_cal_show_week_number = "__usn_field_cal_show_week_number";
        public static final String __usn_field_nav_show_tags = "__usn_field_nav_show_tags";
        public static final String __usn_field_nav_show_overdue = "__usn_field_nav_show_overdue";
        public static final String __usn_field_nav_show_summary = "__usn_field_nav_show_summary";
        public static final String __usn_field_nav_show_emps = "__usn_field_nav_show_emps";
        public static final String __usn_field_nav_show_markers = "__usn_field_nav_show_markers";
        public static final String __usn_field_language = "__usn_field_language";
        public static final String __usn_field_stopwatch = "__usn_field_stopwatch";
        public static final String __usn_field_cal_work_time = "__usn_field_cal_work_time";
        public static final String __usn_field_reminders_in_n_minutes = "__usn_field_reminders_in_n_minutes";

        private String mCountBytes;
        private String mEndDate;
        private List<Employee> mEmployees;
        private String mUserId;
        private String mAddins;
        private String mEmailDirector;
        private String mNameDirector;
        private String mCont;
        private String mKey;
        private String mAvailableBytes;
        private String mNameOrg;

        private String mInviteNameDir;
        private String mInviteEmailDir;
        private String mInviteOrg;
        private String mInviteUUID;
        private String mSettings;


        private String madd_task_to_begin;
        private String mcal_number_of_first_week;
        private String mcal_show_week_number;
        private String mnav_show_tags;
        private String mnav_show_overdue;
        private String mnav_show_summary;
        private String mnav_show_emps;
        private String mnav_show_markers;
        private String mlanguage;
        private String mstopwatch;
        private String mcal_work_time;
        private String mreminders_in_n_minutes;

        private String m__usn_field_add_task_to_begin;
        private String m__usn_field_cal_number_of_first_week;
        private String m__usn_field_cal_show_week_number;
        private String m__usn_field_nav_show_tags;
        private String m__usn_field_nav_show_overdue;
        private String m__usn_field_nav_show_summary;
        private String m__usn_field_nav_show_emps;
        private String m__usn_field_nav_show_markers;
        private String m__usn_field_language;
        private String m__usn_field_stopwatch;
        private String m__usn_field_cal_work_time;
        private String m__usn_field_reminders_in_n_minutes;



        public VerifyUserEntity() {
            mEmployees = new ArrayList<Employee>();
        }

        public String getCountBytes() {
            return mCountBytes;
        }

        public void setCountBytes(String countBytes) {
            mCountBytes = countBytes;

            LTSettings.getInstance().setVerifyBytes(mCountBytes);
        }

        public String getEndDate() {
            return mEndDate;
        }

        public void setEndDate(String endDate) {
            mEndDate = endDate;
            //дата так и записываем как получили (стрингом)
            LTSettings.getInstance().setVerifyEndDate(mEndDate);
        }

        public String getUserId() {
            return mUserId;
        }

        public void setUserId(String userId) {
            mUserId = userId;
            //так и записываем
            LTSettings.getInstance().setVerifyUserId(mUserId);
        }

        public void setAddins(String addins) {
            mAddins = addins;
            mAddins = mAddins.toLowerCase();
            //так и записываем
            LTSettings.getInstance().setAddins(mAddins);

            // ПЛАГИНЫ. настройки устанавливаем
            if (mAddins.indexOf("b32674b-c991-42bd-b8e6-a0e7a4650c2c") != -1) {
                // хронометраж
                //LTSettings.getInstance().setShowChrono(true);

            } else {
                LTSettings.getInstance().setShowChrono(false);
            }

            if (mAddins.indexOf("2dd459c-9d6e-4d0b-8543-9c92df480855") != -1) {
                // контакты
                LTSettings.getInstance().setContactsEnable(true);
            } else {
                LTSettings.getInstance().setContactsEnable(false);
            }
            //
        }

        public void setEmailDirector(String EmailDirector) {
            mEmailDirector = EmailDirector;
            //так и записываем
            LTSettings.getInstance().setVerifyEmailDirector(mEmailDirector);
        }

        public void setNameDirector(String nameDirector) {
            mNameDirector = nameDirector;
            //так и записываем
            LTSettings.getInstance().setVerifyNameDirector(mNameDirector);
        }

        public void setKey (String key) {
            mKey = key;
            //так и записываем
            LTSettings.getInstance().setVerifyKey(mKey);
        }

        public void setCount (String count) {
            mCont = count;
            //так и записываем
            LTSettings.getInstance().setVerifyCount(mCont);
        }

        public void setSettings () {
            JSONObject manJson = new JSONObject();
            try {
                manJson.put(add_task_to_begin, madd_task_to_begin);
                manJson.put(cal_number_of_first_week, mcal_number_of_first_week);
                manJson.put(cal_show_week_number, mcal_show_week_number);
                manJson.put(nav_show_tags, mnav_show_tags);
                manJson.put(nav_show_overdue, mnav_show_overdue);
                manJson.put(nav_show_summary, mnav_show_summary);
                manJson.put(nav_show_emps, mnav_show_emps);
                manJson.put(nav_show_markers, mnav_show_markers);
                manJson.put(_language, mlanguage);
                manJson.put(stopwatch, mstopwatch);
                manJson.put(reminders_in_n_minutes, mreminders_in_n_minutes);
                manJson.put(cal_work_time, mcal_work_time);

                manJson.put(__usn_field_add_task_to_begin, m__usn_field_add_task_to_begin);
                manJson.put(__usn_field_cal_number_of_first_week, m__usn_field_cal_number_of_first_week);
                manJson.put(__usn_field_cal_show_week_number, m__usn_field_cal_show_week_number);
                manJson.put(__usn_field_nav_show_tags, m__usn_field_nav_show_tags);
                manJson.put(__usn_field_nav_show_overdue, m__usn_field_nav_show_overdue);
                manJson.put(__usn_field_nav_show_summary, m__usn_field_nav_show_summary);
                manJson.put(__usn_field_nav_show_emps, m__usn_field_nav_show_emps);
                manJson.put(__usn_field_nav_show_markers, m__usn_field_nav_show_markers);
                manJson.put(__usn_field_language, m__usn_field_language);
                manJson.put(__usn_field_stopwatch, m__usn_field_stopwatch);
                manJson.put(__usn_field_reminders_in_n_minutes, m__usn_field_reminders_in_n_minutes);
                manJson.put(__usn_field_cal_work_time, m__usn_field_cal_work_time);
            } catch (Exception e) {

            }

            mSettings = manJson.toString();
            //так и записываем
            //android.util.Log.v("Tedorius","настройки получили "+mSettings);
            if (!LTSettings.getInstance().isNeedToPutSettings()) {
                if (LTSettings.getInstance().isNeedToShowLoadingScreen() && LTSettings.needToShowToastAfterAddTask) {
                    Utils.setDefaultSetting();
                } else {
                    Utils.setSettingsIfNeed(mSettings);
                }
            } else {
                if (LTSettings.getInstance().isNeedToShowLoadingScreen()) {
                    Utils.setDefaultSetting();
                }
            }
        }

        private void setadd_task_to_begin (String value) {
            madd_task_to_begin = value;
        }

        private void setcal_number_of_first_week (String value) {
            mcal_number_of_first_week = value;
        }

        private void setcal_show_week_number (String value) {
            mcal_show_week_number = value;
        }

        private void setnav_show_tags (String value) {
            mnav_show_tags = value;
        }

        private void setnav_show_overdue (String value) {
            mnav_show_overdue = value;
        }

        private void setnav_show_summary (String value) {
            mnav_show_summary = value;
        }

        private void setnav_show_emps (String value) {
            mnav_show_emps = value;
        }

        private void setnav_show_markers (String value) {
            mnav_show_markers = value;
        }

        private void setlanguage (String value) {
            mlanguage = value;
        }

        private void setstopwatch (String value) {
            mstopwatch = value;
        }

        private void setCalendarTime (String value) {
            mcal_work_time = value;
        }

        private void setreminders_in_n_minutes (String value) {
            mreminders_in_n_minutes = value;
        }

        private void set__usn_field_add_task_to_begin (String value) {
            m__usn_field_add_task_to_begin = value;
        }

        private void set__usn_field_cal_number_of_first_week (String value) {
            m__usn_field_cal_number_of_first_week = value;
        }

        private void set__usn_field_cal_show_week_number (String value) {
            m__usn_field_cal_show_week_number = value;
        }

        private void set__usn_field_nav_show_tags (String value) {
            m__usn_field_nav_show_tags = value;
        }

        private void set__usn_field_nav_show_overdue (String value) {
            m__usn_field_nav_show_overdue = value;
        }

        private void set__usn_field_nav_show_summary (String value) {
            m__usn_field_nav_show_summary = value;
        }

        private void set__usn_field_nav_show_emps (String value) {
            m__usn_field_nav_show_emps = value;
        }

        private void set__usn_field_nav_show_markers (String value) {
            m__usn_field_nav_show_markers = value;
        }

        private void set__usn_field_reminders_in_n_minutes (String value) {
            m__usn_field_reminders_in_n_minutes = value;
        }

        private void set__usn_field_cal_work_time (String value) {
            m__usn_field_cal_work_time = value;
        }

        private void set__usn_field_language (String value) {
            m__usn_field_language = value;
        }

        private void set__usn_field_stopwatch (String value) {
            m__usn_field_stopwatch = value;
        }

        public void setAvailableBytes(String AvailableBytes) {
            mAvailableBytes = AvailableBytes;
            //так и записываем
            LTSettings.getInstance().setVerifyAvailableBytes(mAvailableBytes);
        }
        public void setNameOrg(String NameOrg) {
            mNameOrg = NameOrg;
            //так и записываем
            LTSettings.getInstance().setVerifyOrgName(mNameOrg);
        }

        public void setInviteName(String name) {
            mInviteNameDir = name;
        }

        public void setInviteOrg(String name) {
            mInviteOrg = name;
        }

        public void setInviteEmail(String name) {
            mInviteEmailDir = name;
        }

        public void setInviteUUID(String name) {
            mInviteUUID = name;
        }

        public String getInviteName() {
            return mInviteNameDir;
        }

        public String getInviteOrg() {
            return mInviteOrg;
        }

        public String getInviteEmail() {
            return mInviteEmailDir;
        }

        public String getInviteUUID() {
            return mInviteUUID;
        }

        public List<Employee> getEmployees() {
            return mEmployees;
        }

        public void setEmployees(List<Employee> employees) {
            mEmployees = employees;
            ////////////////////////////////////////////////////////////////////////////////////////
            for (Employee employee : employees) {
                if (employee.getInvite().equals("1")) {
                    // записать тех кого пригласили
                    LTSettings.allInvitedUsers.add(employee);
                } else {
                    // проверить был ли юзер в приглашенных
                    for (Employee invitedUser : LTSettings.allInvitedUsersWas) {
                        if (invitedUser.getEmail().equals(employee.getEmail())) {
                            // был в приглашенных и перестал быть
                            LTSettings.allInvitedAcceptedUsers.add(employee);
                        }
                    }
                }
                // TODO ТУТ СДЕЛАТЬ КАК ДИМОН СКАЗАЛ - ПОЛЕ ТЕЛЕФОНА ЗАПИСЫВАЕТСЯ В EMP КОНКРЕТНОМУ ЮЗЕРУ ЕСЛИ USN В EMP = 0;
            }

            LTSettings.allInvitedUsersWas.clear();
            LTSettings.allInvitedUsersWas.addAll(LTSettings.allInvitedUsers);
            LTSettings.allInvitedUsers.clear();

            ////////////////////////////////////////////////////////////////////////////////////////
            LTSettings.getInstance().setVerifyEmployeesCount(mEmployees.size());
        }
    }
}
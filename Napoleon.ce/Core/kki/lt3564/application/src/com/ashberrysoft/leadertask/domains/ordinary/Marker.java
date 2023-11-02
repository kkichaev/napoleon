package com.ashberrysoft.leadertask.domains.ordinary;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.data_providers.network.BaseSOAP;
import com.ashberrysoft.leadertask.modern.cache.MarkerCache;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.xml_handlers.BaseLionEntityInterface;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Класс, который представляет собой описание маркера для задачи.
 * 
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * 
 */
@DatabaseTable(tableName = Marker.TABLE_NAME)
public class Marker implements IEntity, BaseLionEntityInterface, Comparable<Marker> {

    private static final long serialVersionUID = 1L;

    public static final String SERVER_CLASS = "LionMarker";
    public static final String TABLE_NAME = "markers";

    public static final String FIELD_UID = "UID";
    public static final String FIELD_ORDER = "Order";
    public static final String FIELD_NAME = "Name";
    public static final String FIELD_IS_UPPERCASE = "Uppercase";
    public static final String FIELD_TEXT_COLOR = "Forecolor";
    public static final String FIELD_BACK_COLOR = "Backcolor";
    public static final String FIELD_USN = "__usn_entity";
    public static final String FIELD_USN_ORDER = "__usn_field_order";
    public static final String FIELD_USN_NAME = "__usn_field_name";
    public static final String FIELD_USN_IS_UPPERCASE = "__usn_field_uppercase";
    public static final String FIELD_USN_TEXT_COLOR = "__usn_field_forecolor";
    public static final String FIELD_USN_BACK_COLOR = "__usn_field_backcolor";
    public static final String FIELD_EMAIL_CREATOR = "EmailCreator";

    public static final String ORDERS = "Orders";

    public static final String DEFAULT_MARKER_UUID_STRING = "0F9F5BEE-ED84-4E73-A7F3-3136EE5AF672";
    public static final String DEFAULT_MARKER_UUID_STRING_LOWER = DEFAULT_MARKER_UUID_STRING.toLowerCase();
    public static final UUID DEFAULT_MARKER_UUID = UUID.fromString(DEFAULT_MARKER_UUID_STRING);
    public static final String NO_COLOR = "-1";
    public static final String DEFAULT_MARKER_STRING = "default";

    /**
     * UID - уникальный идентификатор элемента (текст)
     */
    @DatabaseField(columnName = FIELD_UID, id = true)
    private UUID mId;

    /**
     * USN – номер изменения элемента (число, начиная с 0)
     */
    @DatabaseField(columnName = FIELD_USN)
    private long mUsn;

    /**
     * Order – порядок элемента в дереве/списке (число, начиная с 1)
     */
    @DatabaseField(columnName = ORDERS)
    private int mOrder;

    /**
     * USN_Order – номер изменения порядка (число, начиная с 0)
     */
    @DatabaseField(columnName = FIELD_USN_ORDER)
    private int mUsnOrder;

    /**
     * Name – заголовок элемента (текст)
     */
    @DatabaseField(columnName = FIELD_NAME)
    private String mName;

    /**
     * USN_Name – номер изменения заголовка (число, начиная с 0)
     */
    @DatabaseField(columnName = FIELD_USN_NAME)
    private int mUsnName;

    /**
     * IsUppercase – все заглавные (0 или 1)
     */
    @DatabaseField(columnName = FIELD_IS_UPPERCASE)
    private boolean mIsUppercase;

    /**
     * USN_IsUppercase – номер изменения поля все заглавные (число, начиная с 0)
     */
    @DatabaseField(columnName = FIELD_USN_IS_UPPERCASE)
    private int mUsnIsUppercase;

    /**
     * TextColor – цвет текста (RGB, по умолчанию -1, т.е. не менять)
     */
    @DatabaseField(columnName = FIELD_TEXT_COLOR)
    private String mTextColor;

    /**
     * USN_TextColor – номер изменения поля цвета текста (число, начиная с 0)
     */
    @DatabaseField(columnName = FIELD_USN_TEXT_COLOR)
    private int mUsnTextColor;

    /**
     * BackColor – цвет фона (RGB, по умолчанию -1, т.е. не менять)
     */
    @DatabaseField(columnName = FIELD_BACK_COLOR)
    private String mBackColor;

    /**
     * USN_BackColor – номер изменения поля цвета фона (число, начиная с 0)
     */
    @DatabaseField(columnName = FIELD_USN_BACK_COLOR)
    private int mUsnBackColor;

    @DatabaseField(columnName = FIELD_EMAIL_CREATOR)
    private String mCreator;

    // default constructor
    public Marker() {}

    // parameterized constructor
    public Marker(Map<String, String> map) {
        setUsn(Integer.parseInt(map.get(FIELD_USN)));
        setId(checkSoapUUID(map.get(FIELD_UID)));
        setOrder(Integer.parseInt(map.get(FIELD_ORDER)));
        setUsnOrder(Integer.parseInt(map.get(FIELD_USN_ORDER)));
        setCreator(map.get(FIELD_EMAIL_CREATOR));

        if (map.containsKey(FIELD_NAME) && map.get(FIELD_NAME) != null) {
            setName(checkSoap(map.get(FIELD_NAME)));
        }

        setUsnName(Integer.parseInt(map.get(FIELD_USN_NAME)));
        setIsUppercase((map.get(FIELD_IS_UPPERCASE)).equals("1"));
        setUsnIsUppercase(Integer.parseInt(map.get(FIELD_USN_IS_UPPERCASE)));

        if (map.containsKey(FIELD_TEXT_COLOR)) {
            setTextColor(checkSoap(map.get(FIELD_TEXT_COLOR)));
        }

        setUsnTextColor(Integer.parseInt(map.get(FIELD_USN_TEXT_COLOR)));

        if (map.containsKey(FIELD_BACK_COLOR)) {
            setBackColor(checkSoap(map.get(FIELD_BACK_COLOR)));
        }

        setUsnBackColor(Integer.parseInt(map.get(FIELD_USN_BACK_COLOR)));
    }

    /*
     * setterts for class fields
     */
    public void setUsn(long mUsn) {
        this.mUsn = mUsn;
    }

    public void setId(UUID mId) {
        this.mId = mId;
    }

    public void setOrder(int mOrder) {
        this.mOrder = mOrder;
    }

    public void setUsnOrder(int mUsnOrder) {
        this.mUsnOrder = mUsnOrder;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public void setUsnName(int mUsnName) {
        this.mUsnName = mUsnName;
    }

    public void setIsUppercase(boolean mIsUppercase) {
        this.mIsUppercase = mIsUppercase;
    }

    public void setUsnIsUppercase(int mUsnIsUppercase) {
        this.mUsnIsUppercase = mUsnIsUppercase;
    }

    public void setTextColor(String mTextColor) {
        this.mTextColor = mTextColor;
    }

    public void setUsnTextColor(int mUsnTextColor) {
        this.mUsnTextColor = mUsnTextColor;
    }

    public void setBackColor(String mBackColor) {
        this.mBackColor = mBackColor;
    }

    public void setUsnBackColor(int mUsnBackColor) {
        this.mUsnBackColor = mUsnBackColor;
    }

    public String getCreator() {
        return mCreator;
    }

    public void setCreator(String creator) {
        mCreator = creator;
    }

    /*
     * getters for class fields
     */
    public long getUsn() {
        return mUsn;
    }

    public UUID getId() {
        return mId;
    }

    @Override
    public int getIdTask() {
        return 0;
    }

    public int getOrder() {
        return mOrder;
    }

    public int getUsnOrder() {
        return mUsnOrder;
    }

    public int getUsnName() {
        return mUsnName;
    }

    public boolean isUppercase() {
        return mIsUppercase;
    }

    public int getUsnIsUppercase() {
        return mUsnIsUppercase;
    }

    public String getTextColor() {
        return mTextColor;
    }

    public int getUsnTextColor() {
        return mUsnTextColor;
    }

    public String getBackColor() {
        return mBackColor;
    }

    public int getUsnBackColor() {
        return mUsnBackColor;
    }

    /*
     * check content of the soap object
     */
    private String checkSoap(String str) {
        if (str.equals("anyType{}")) {
            return null;
        }
        return str;
    }

    /*
     * check UUID of the soap object
     */
    private UUID checkSoapUUID(String str) {
        if (str.equals("anyType{}")) {
            return null;
        }
        return UUID.fromString(str);
    }

    public String getName() {
        return mName;
    }

    @Override
    public void fillKeyValue(String key, String value) {
        if (FIELD_UID.equalsIgnoreCase(key)) {
            if (Marker.DEFAULT_MARKER_STRING.equalsIgnoreCase(value)) {
                mId = Marker.DEFAULT_MARKER_UUID;
            } else {
                mId = UUID.fromString(value);
            }
        }

        else if (FIELD_ORDER.equalsIgnoreCase(key)) {
            mOrder = Integer.parseInt(value);
        }

        else if (FIELD_NAME.equalsIgnoreCase(key)) {
            mName = value;
        }

        else if (FIELD_IS_UPPERCASE.equalsIgnoreCase(key)) {
            mIsUppercase = BaseSOAP.equalsOne(value);
        }

        else if (FIELD_TEXT_COLOR.equalsIgnoreCase(key)) {
            mTextColor = value;
        }

        else if (FIELD_BACK_COLOR.equalsIgnoreCase(key)) {
            mBackColor = value;
        }

        else if (FIELD_USN.equalsIgnoreCase(key)) {
            mUsn = Long.parseLong(value);
        }

        else if (FIELD_USN_ORDER.equalsIgnoreCase(key)) {
            mUsnOrder = Integer.parseInt(value);
        }

        else if (FIELD_USN_NAME.equalsIgnoreCase(key)) {
            mUsnName = Integer.parseInt(value);
        }

        else if (FIELD_USN_IS_UPPERCASE.equalsIgnoreCase(key)) {
            mUsnIsUppercase = Integer.parseInt(value);
        }

        else if (FIELD_USN_TEXT_COLOR.equalsIgnoreCase(key)) {
            mUsnTextColor = Integer.parseInt(value);
        }

        else if (FIELD_USN_BACK_COLOR.equalsIgnoreCase(key)) {
            mUsnBackColor = Integer.parseInt(value);
        }
        else if (FIELD_EMAIL_CREATOR.equalsIgnoreCase(key)) {
            mCreator = value;
        }
    }

    @Override
    public void getLionEntity(StringBuilder sb) {
        sb.append(BaseSOAP.getOpen(SERVER_CLASS));

        final String uid;

        uid = getId().toString();
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_UID, uid));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_ORDER, getOrder()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_NAME, getName()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_IS_UPPERCASE, isUppercase()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_TEXT_COLOR, getTextColor()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_BACK_COLOR, getBackColor()));

        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN, getUsn()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_NAME, getUsnName()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_ORDER, getUsnOrder()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_IS_UPPERCASE, getUsnIsUppercase()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_TEXT_COLOR, getUsnTextColor()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_BACK_COLOR, getUsnBackColor()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_EMAIL_CREATOR, getCreator()));

        sb.append(BaseSOAP.getClose(SERVER_CLASS));
    }

    public static final String[] COLUMNS = { FIELD_UID, FIELD_BACK_COLOR, FIELD_TEXT_COLOR, FIELD_NAME, FIELD_IS_UPPERCASE, ORDERS, FIELD_USN,
            FIELD_USN_BACK_COLOR, FIELD_USN_IS_UPPERCASE, FIELD_USN_NAME, FIELD_USN_ORDER, FIELD_USN_TEXT_COLOR };

    @Override
    public String getServerClass() {
        return SERVER_CLASS;
    }

    @Override
    public ContentValues getContentValues(ContentValues cv) {
        return null;
    }

    @Override
    public int compareTo(Marker another) {
        try {
            if (getOrder() > another.getOrder()) {
                return -1;
            }

            if (getOrder() < another.getOrder()) {
                return 1;
            }

            if (getName() != null && another != null) {
                return getName().compareTo(another.getName());
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public static void updateTaskMarkerOrder( String markerUid, int markerOrder, Context context) {
        StringBuilder sb = new StringBuilder();
        final ContentValues cvTask = new ContentValues();
        final String selectionTask = LeaderTaskProviderMetaData.SelectionKeeper.equals(sb, LionMetaData.LTaskContract.UidMarker, markerUid);
        cvTask.put(LionMetaData.LTaskContract.MarkerOrder, markerOrder);
        context.getContentResolver().update(LionMetaData.LTaskContract.CONTENT_URI, cvTask, selectionTask, null);
    }

    public static int getMarkerOrderFromLowerUid(Context context, String markerUid) {
        return MarkerCache.getInstance(context).getOrderForMarker(markerUid.toLowerCase());
    }
}
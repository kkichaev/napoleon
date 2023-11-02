package com.ashberrysoft.leadertask.domains.ordinary;

import android.content.ContentValues;
import android.text.TextUtils;

import com.ashberrysoft.leadertask.data_providers.network.BaseSOAP;
import com.ashberrysoft.leadertask.enums.ETreeDataNodeLevel;
import com.ashberrysoft.leadertask.xml_handlers.BaseLionEntityInterface;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.v2soft.AndLib.dao.TreeDataContainer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@DatabaseTable(tableName = ContactsGroup.TABLE_NAME)
public class ContactsGroup extends TreeDataContainer<ContactsGroup> implements SlidingMenuTreeDataContainer, Serializable, IEntity,
        Comparable<ContactsGroup>, BaseLionEntityInterface {

    private static final long serialVersionUID = 1L;

    public static final String TABLE_NAME = "contact_group";
    public static final String FIELD_UID = "UID";
    public static final String FIELD_CREATOR = "EmailCreator";
    public static final String FIELD_NAME = "Name";
    public static final String FIELD_UID_PARENT = "UIDParent";
    public static final String FIELD_COMMENT = "Comment";
    public static final String FIELD_ORDER = "Order";
    public static final String FIELD_COLLAPSED = "Collapsed";
    public static final String FIELD_LIST_MEMBERS = "Emails";
    public static final String FIELD_USN = "__usn_entity";
    public static final String FIELD_USN_NAME = "__usn_field_name";
    public static final String FIELD_USN_UID_PARENT = "__usn_field_uid_parent";
    public static final String FIELD_USN_COMMENT = "__usn_field_comment";
    public static final String FIELD_USN_ORDER = "__usn_field_order";
    public static final String FIELD_USN_COLLAPSED = "__usn_field_collapsed";
    public static final String FIELD_USN_LIST_MEMBERS = "__usn_field_list_members";

    public static final String SERVER_CLASS = "LionContactGroup";

    /**
     * USN – номер изменения элемента (число, начиная с 0)
     */
    @DatabaseField(columnName = FIELD_USN)
    private long mUsn;

    /**
     * UID - уникальный идентификатор элемента (текст)
     */
    @DatabaseField(columnName = FIELD_UID, id = true)
    private UUID mId;

    /**
     * Creator – создатель (логин создателя)
     */
    @DatabaseField(columnName = FIELD_CREATOR, index = true)
    private String mCreator;

    /**
     * UIDParent – уникальный идентификатор родителя (текст, может быть пустой)
     */
    @DatabaseField(columnName = FIELD_UID_PARENT)
    private UUID mParentId;

    /**
     * USN_ UIDParent – номер изменения родителя (число, начиная с 0)
     */
    @DatabaseField(columnName = FIELD_USN_UID_PARENT)
    private int mUsnParent;

    /**
     * Collapsed – свернут ли элемент (0 или 1)
     */
    @DatabaseField(columnName = FIELD_COLLAPSED)
    private boolean mCollapsed;

    /**
     * USN_Collapsed – номер изменения поля свернут (число, начиная с 0)
     */
    @DatabaseField(columnName = FIELD_USN_COLLAPSED)
    private int mUsnCollapsed;

    /**
     * Order – порядок элемента в дереве/списке (число, начиная с 1)
     */
    @DatabaseField(columnName = FIELD_ORDER)
    private int mOrder;

    /**
     * USN_Order – номер изменения порядка (число, начиная с 0)
     */
    @DatabaseField(columnName = FIELD_USN_ORDER)
    private int mUsnOrder;

    /**
     * Name – заголовок элемента (текст)
     */
    @DatabaseField(columnName = FIELD_NAME, index = true, defaultValue = "")
    private String mName;

    /**
     * USN_Name – номер изменения заголовка (число, начиная с 0)
     */
    @DatabaseField(columnName = FIELD_USN_NAME)
    private int mUsnName;

    /**
     * Comment – комментарий элемента (текст)
     */
    @DatabaseField(columnName = FIELD_COMMENT)
    private String mComment;

    /**
     * USN_Comment – номер изменения комментария (число, начиная с 0)
     */
    @DatabaseField(columnName = FIELD_USN_COMMENT)
    private int mUsnComment;

    /**
     * SharedUsers – список логинов пользователей которым дан доступ к проекту
     */
    @DatabaseField(columnName = FIELD_LIST_MEMBERS)
    private String mSharedUsers;

    /**
     * USN_SharedUsers – номер изменения поля SharedUsers (число, начиная с 0)
     */
    @DatabaseField(columnName = FIELD_USN_LIST_MEMBERS)
    private int mUsnSharedUsers;


    @DatabaseField(foreign = true)
    private ContactsGroup mParent;

    private int mLevel;

    public ContactsGroup() {
        mChilds = new ArrayList<ContactsGroup>(0);
    }

    public ContactsGroup(Map<String, String> map) {
        setUsn(Integer.parseInt(map.get(FIELD_USN)));
        setId(checkSoapUUID(map.get(FIELD_UID)));
        setCreator(checkSoap(map.get(FIELD_CREATOR)));
        if (map.containsKey(FIELD_UID_PARENT))
            setParentId(checkSoapUUID(map.get(FIELD_UID_PARENT)));
        setUsnParent(Integer.parseInt(map.get(FIELD_USN_UID_PARENT)));
        setCollapsed((map.get(FIELD_COLLAPSED)).equals("1"));
        setUsnCollapsed(Integer.parseInt(map.get(FIELD_USN_COLLAPSED)));
        setOrder(Integer.parseInt(map.get(FIELD_ORDER)));
        setUsnOrder(Integer.parseInt(map.get(FIELD_USN_ORDER)));
        if (map.containsKey(FIELD_NAME))
            setName(checkSoap(map.get(FIELD_NAME)));
        setUsnName(Integer.parseInt(map.get(FIELD_USN_NAME)));
        if (map.containsKey(FIELD_COMMENT))
            setComment(checkSoap(map.get(FIELD_COMMENT)));
        setUsnComment(Integer.parseInt(map.get(FIELD_USN_COMMENT)));
        if (map.containsKey(FIELD_LIST_MEMBERS)) {
            setSharedUsers(map.get(FIELD_LIST_MEMBERS));
        }

        setUsnSharedUsers(Integer.parseInt(map.get(FIELD_USN_LIST_MEMBERS)));
    }

    private String checkSoap(String str) {
        if (str.equals("anyType{}")) {
            return null;
        }
        return str;
    }

    private UUID checkSoapUUID(String str) {
        if (str.equals("anyType{}")) {
            return null;
        }
        return UUID.fromString(str);
    }

    public ContactsGroup getParent() {
        return mParent;
    }

    public void setParent(ContactsGroup parent) {
        mParent = parent;
    }

    public void addChild(ContactsGroup contactsGroup) {
        mChilds.add(contactsGroup);
        contactsGroup.setParent(this);
        contactsGroup.setIndent(mLevel + 1);
    }

    @Override
    public int getNodeLevel() {
        return ETreeDataNodeLevel.PROJECT.ordinal();
    }

    @Override
    public boolean isExpandable() {
        return !mChilds.isEmpty();
    }

    @Override
    public int getIndent() {
        return mLevel;
    }

    public void setIndent(int level) {
        mLevel = level;
    }

    public int getUsnSharedUsers() {
        return mUsnSharedUsers;
    }

    public void setUsnSharedUsers(int mUsnSharedUsers) {
        this.mUsnSharedUsers = mUsnSharedUsers;
    }

    public UUID getId() {
        return mId;
    }

    @Override
    public int getIdTask() {
        return 0;
    }

    public void setId(UUID mId) {
        this.mId = mId;
    }

    public long getUsn() {
        return mUsn;
    }

    public void setUsn(long mUsn) {
        this.mUsn = mUsn;
    }

    public void setUsnPlusPlus() {
        mUsn++;
    }

    public String getCreator() {
        return mCreator;
    }

    public void setCreator(String mCreator) {
        this.mCreator = mCreator;
    }

    public UUID getParentId() {
        return mParentId;
    }

    public void setParentId(UUID mParentId) {
        this.mParentId = mParentId;
    }

    public int getUsnParent() {
        return mUsnParent;
    }

    public void setUsnParent(int mUsnParent) {
        this.mUsnParent = mUsnParent;
    }

    public boolean isCollapsed() {
        return mCollapsed;
    }

    @Override
    public boolean isExpanded() {
        return !mCollapsed;
    }

    public void setCollapsed(boolean mCollapsed) {
        this.mCollapsed = mCollapsed;
        isExpanded = !mCollapsed;
    }

    public int getUsnCollapsed() {
        return mUsnCollapsed;
    }

    public void setUsnCollapsed(int mUsnCollapsed) {
        this.mUsnCollapsed = mUsnCollapsed;
    }

    public int getOrder() {
        return mOrder;
    }

    public void setOrder(int mOrder) {
        this.mOrder = mOrder;
    }

    public int getUsnOrder() {
        return mUsnOrder;
    }

    public void setUsnOrder(int mUsnOrder) {
        this.mUsnOrder = mUsnOrder;
    }

    public String getSharedUsers() {
        return mSharedUsers;
    }

    public void setSharedUsers(String mSharedUsers) {
        this.mSharedUsers = mSharedUsers;
    }

    public int getUsnComment() {
        return mUsnComment;
    }

    public void setUsnComment(int mUsnComment) {
        this.mUsnComment = mUsnComment;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String mComment) {
        this.mComment = mComment;
    }

    public int getUsnName() {
        return mUsnName;
    }

    public void setUsnName(int mUsnName) {
        this.mUsnName = mUsnName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public String getFilterId() {
        return mId.toString();
    }

    @Override
    public int compareTo(ContactsGroup enother) {
        if (this.getOrder() > enother.getOrder()) {
            return 1;
        }

        else if (this.getOrder() < enother.getOrder()) {
            return -1;
        }

        return 0;
    }

    @Override
    public void fillKeyValue(String key, String value) {
        if (FIELD_UID.equalsIgnoreCase(key)) {
            mId = UUID.fromString(value);
        }

        else if (FIELD_CREATOR.equalsIgnoreCase(key)) {
            mCreator = value;
        }

        else if (FIELD_NAME.equalsIgnoreCase(key)) {
            mName = value;
        }

        else if (FIELD_UID_PARENT.equalsIgnoreCase(key)) {
            if (!TextUtils.isEmpty(value)) {
                mParentId = UUID.fromString(value);
            }
        }

        else if (FIELD_COMMENT.equalsIgnoreCase(key)) {
            mComment = value;
        }

        else if (FIELD_ORDER.equalsIgnoreCase(key)) {
            mOrder = Integer.parseInt(value);
        }

        else if (FIELD_COLLAPSED.equalsIgnoreCase(key)) {
            mCollapsed = BaseSOAP.equalsOne(value);
        }

        else if (FIELD_LIST_MEMBERS.equalsIgnoreCase(key)) {
            mSharedUsers = value;
        }

        else if (FIELD_USN.equalsIgnoreCase(key)) {
            mUsn = Long.parseLong(value);
        }

        else if (FIELD_USN_NAME.equalsIgnoreCase(key)) {
            mUsnName = Integer.parseInt(value);
        }

        else if (FIELD_USN_UID_PARENT.equalsIgnoreCase(key)) {
            mUsnParent = Integer.parseInt(value);
        }

        else if (FIELD_USN_COMMENT.equalsIgnoreCase(key)) {
            mUsnComment = Integer.parseInt(value);
        }

        else if (FIELD_USN_ORDER.equalsIgnoreCase(key)) {
            mUsnOrder = Integer.parseInt(value);
        }

        else if (FIELD_USN_COLLAPSED.equalsIgnoreCase(key)) {
            mUsnCollapsed = Integer.parseInt(value);
        }

        else if (FIELD_USN_LIST_MEMBERS.equalsIgnoreCase(key)) {
            mUsnSharedUsers = Integer.parseInt(value);
        }
    }

    @Override
    public void getLionEntity(StringBuilder sb) {
        sb.append(BaseSOAP.getOpen(SERVER_CLASS));

        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_UID, getId()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_CREATOR, getCreator()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_NAME, getName()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_UID_PARENT, getParentId()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_COMMENT, getComment()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_ORDER, getOrder()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_COLLAPSED, isCollapsed()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_LIST_MEMBERS, getSharedUsers()));

        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN, getUsn()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_NAME, getUsnName()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_UID_PARENT, getUsnParent()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_COMMENT, getUsnComment()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_ORDER, getUsnOrder()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_COLLAPSED, getUsnCollapsed()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_LIST_MEMBERS, getUsnSharedUsers()));

        sb.append(BaseSOAP.getClose(SERVER_CLASS));
    }

    @Override
    public String getServerClass() {
        return SERVER_CLASS;
    }

    @Override
    public ContentValues getContentValues(ContentValues cv) {
        return null;
    }
}
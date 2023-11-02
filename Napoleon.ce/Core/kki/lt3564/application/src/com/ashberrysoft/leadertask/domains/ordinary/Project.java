package com.ashberrysoft.leadertask.domains.ordinary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import android.content.ContentValues;
import android.text.TextUtils;

import com.ashberrysoft.leadertask.data_providers.network.BaseSOAP;
import com.ashberrysoft.leadertask.enums.ETreeDataNodeLevel;
import com.ashberrysoft.leadertask.xml_handlers.BaseLionEntityInterface;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.v2soft.AndLib.dao.TreeDataContainer;

@DatabaseTable(tableName = Project.TABLE_NAME)
public class Project extends TreeDataContainer<Project> implements SlidingMenuTreeDataContainer, Serializable, IEntity,
        Comparable<Project>, BaseLionEntityInterface {

    private static final long serialVersionUID = 1L;

    public static final String TABLE_NAME = "projects";
    public static final String FIELD_UID = "UID";
    public static final String FIELD_CREATOR = "EmailCreator";
    public static final String FIELD_NAME = "Name";
    public static final String FIELD_UID_PARENT = "UIDParent";
    public static final String FIELD_COMMENT = "Comment";
    public static final String FIELD_ORDER = "Order";
    public static final String FIELD_COLLAPSED = "Collapsed";
    public static final String FIELD_GROUP = "Group";
    public static final String FIELD_SHOW = "Show";
    public static final String FIELD_FAVORITE = "Favorite";
    public static final String FIELD_IS_CLOSED = "IsClosed";
    public static final String FIELD_QUIET = "Quiet";
    public static final String FIELD_LIST_MEMBERS = "Emails";
    public static final String FIELD_USN = "__usn_entity";
    public static final String FIELD_USN_NAME = "__usn_field_name";
    public static final String FIELD_USN_UID_PARENT = "__usn_field_uid_parent";
    public static final String FIELD_USN_COMMENT = "__usn_field_comment";
    public static final String FIELD_USN_ORDER = "__usn_field_order";
    public static final String FIELD_USN_COLLAPSED = "__usn_field_collapsed";
    public static final String FIELD_USN_GROUP = "__usn_field_group";
    public static final String FIELD_USN_SHOW = "__usn_field_show";
    public static final String FIELD_USN_FAVORITE = "__usn_field_favorite";
    public static final String FIELD_USN_ISCLOSED = "__usn_field_isclosed";
    public static final String FIELD_USN_QUIET = "__usn_field_quiet";
    public static final String FIELD_USN_LIST_MEMBERS = "__usn_field_list_members";

    public static final String SERVER_CLASS = "LionProject";

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
    @DatabaseField
    private int mUsnParent;

    /**
     * Collapsed – свернут ли элемент (0 или 1)
     */
    @DatabaseField(columnName = FIELD_COLLAPSED)
    private boolean mCollapsed;

    /**
     * USN_Collapsed – номер изменения поля свернут (число, начиная с 0)
     */
    @DatabaseField
    private int mUsnCollapsed;

    /**
     * Order – порядок элемента в дереве/списке (число, начиная с 1)
     */
    @DatabaseField(columnName = FIELD_ORDER)
    private int mOrder;

    /**
     * USN_Order – номер изменения порядка (число, начиная с 0)
     */
    @DatabaseField
    private int mUsnOrder;

    /**
     * Name – заголовок элемента (текст)
     */
    @DatabaseField(columnName = FIELD_NAME, index = true, defaultValue = "")
    private String mName;

    /**
     * USN_Name – номер изменения заголовка (число, начиная с 0)
     */
    @DatabaseField
    private int mUsnName;

    /**
     * Comment – комментарий элемента (текст)
     */
    @DatabaseField
    private String mComment;

    /**
     * USN_Comment – номер изменения комментария (число, начиная с 0)
     */
    @DatabaseField
    private int mUsnComment;

    /**
     * IsFavorite – избранный (0 или 1)
     */
    @DatabaseField
    private boolean mFaforite;

    /**
     * USN_ IsFavorite – номер изменения поля избранный (число, начиная с 0)
     */
    @DatabaseField
    private int mUsnFavorite;

    /**
     * IsGroup – группировать (0 или 1)
     */
    @DatabaseField
    private boolean mGroup;

    /**
     * USN_ IsGroup – номер изменения поля группировать (число, начиная с 0)
     */
    @DatabaseField
    private int mUsnGroup;

    /**
     * IsShow – отображать в навигаторе (0 или 1)
     */
    @DatabaseField(columnName = FIELD_SHOW)
    private boolean mShow;

    /**
     * USN_ IsShow – номер изменения поля отображать в навигаторе (число, начиная с 0)
     */
    @DatabaseField
    private int mUsnShow;

    /**
     * IsClosed – закрыт (0 или 1)
     */
    @DatabaseField
    private boolean mClosed;
    /**
     * USN_IsClosed – номер изменения поля закрыт (число, начиная с 0)
     */
    @DatabaseField
    private int mUsnClosed;

    /**
     * Не следить (0 или 1)
     */
    @DatabaseField(columnName = FIELD_QUIET)
    private boolean mQuiet;


    @DatabaseField(columnName = FIELD_USN_QUIET)
    private int mUsnQuiet;

    /**
     * SharedUsers – список логинов пользователей которым дан доступ к проекту
     */
    @DatabaseField(columnName = FIELD_LIST_MEMBERS)
    private String mSharedUsers;

    /**
     * USN_SharedUsers – номер изменения поля SharedUsers (число, начиная с 0)
     */
    @DatabaseField
    private int mUsnSharedUsers;

    @DatabaseField(foreign = true)
    private Project mParent;

    private int mLevel;

    public Project() {
        mChilds = new ArrayList<Project>(0);
        mShow = true;
    }

    public Project(Map<String, String> map) {
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
        setFavorite((map.get(FIELD_FAVORITE)).equals("1"));
        setUsnFavorite(Integer.parseInt(map.get(FIELD_USN_FAVORITE)));
        setGroup((map.get(FIELD_GROUP)).equals("1"));
        setUsnGroup(Integer.parseInt(map.get(FIELD_USN_GROUP)));
        setShow((map.get(FIELD_SHOW)).equals("1"));
        setUsnShow(Integer.parseInt(map.get(FIELD_USN_SHOW)));
        setClosed((map.get(FIELD_IS_CLOSED)).equals("1"));
        setUsnClosed(Integer.parseInt(map.get(FIELD_USN_ISCLOSED)));
        setQuiet((map.get(FIELD_QUIET)).equals("1"));
        setUsnQuiet(Integer.parseInt(map.get(FIELD_USN_QUIET)));

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

    public Project getParent() {
        return mParent;
    }

    public void setParent(Project parent) {
        mParent = parent;
    }

    public void addChild(Project project) {
        mChilds.add(project);
        project.setParent(this);
        project.setIndent(mLevel + 1);
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

    public int getUsnQuiet() {
        return mUsnQuiet;
    }

    public void setUsnQuiet(int mUsnQuiet) {
        this.mUsnQuiet = mUsnQuiet;
    }


    public int getUsnClosed() {
        return mUsnClosed;
    }

    public void setUsnClosed(int mUsnClosed) {
        this.mUsnClosed = mUsnClosed;
    }
    public boolean isClosed() {
        return mClosed;
    }

    public void setClosed(boolean mClosed) {
        this.mClosed = mClosed;
    }

    public boolean isQuiet() {
        return mQuiet;
    }

    public void setQuiet(boolean mQuiet) {
        this.mQuiet = mQuiet;
    }

    public int getUsnShow() {
        return mUsnShow;
    }

    public void setUsnShow(int mUsnShow) {
        this.mUsnShow = mUsnShow;
    }

    public boolean isShow() {
        return mShow;
    }

    public void setShow(boolean mShow) {
        this.mShow = mShow;
    }

    public int getUsnGroup() {
        return mUsnGroup;
    }

    public void setUsnGroup(int mUsnGroup) {
        this.mUsnGroup = mUsnGroup;
    }

    public boolean isGroup() {
        return mGroup;
    }

    public void setGroup(boolean mGroup) {
        this.mGroup = mGroup;
    }

    public int getUsnFavorite() {
        return mUsnFavorite;
    }

    public void setUsnFavorite(int mUsnFavorite) {
        this.mUsnFavorite = mUsnFavorite;
    }

    public boolean isFavorite() {
        return mFaforite;
    }

    public void setFavorite(boolean mFaforite) {
        this.mFaforite = mFaforite;
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
    public int compareTo(Project enother) {
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

        else if (FIELD_GROUP.equalsIgnoreCase(key)) {
            mGroup = BaseSOAP.equalsOne(value);
        }

        else if (FIELD_SHOW.equalsIgnoreCase(key)) {
            mShow = BaseSOAP.equalsOne(value);
        }

        else if (FIELD_FAVORITE.equalsIgnoreCase(key)) {
            mFaforite = BaseSOAP.equalsOne(value);
        }

        else if (FIELD_IS_CLOSED.equalsIgnoreCase(key)) {
            mClosed = BaseSOAP.equalsOne(value);
        }

        else if (FIELD_QUIET.equalsIgnoreCase(key)) {
            mQuiet = BaseSOAP.equalsOne(value);
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

        else if (FIELD_USN_GROUP.equalsIgnoreCase(key)) {
            mUsnGroup = Integer.parseInt(value);
        }

        else if (FIELD_USN_SHOW.equalsIgnoreCase(key)) {
            mUsnShow = Integer.parseInt(value);
        }

        else if (FIELD_USN_FAVORITE.equalsIgnoreCase(key)) {
            mUsnFavorite = Integer.parseInt(value);
        }

        else if (FIELD_USN_ISCLOSED.equalsIgnoreCase(key)) {
            mUsnClosed = Integer.parseInt(value);
        }

        else if (FIELD_USN_QUIET.equalsIgnoreCase(key)) {
            mUsnQuiet = Integer.parseInt(value);
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
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_GROUP, isGroup()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_SHOW, isShow()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_FAVORITE, isFavorite()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_IS_CLOSED, isClosed()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_QUIET, isQuiet()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_LIST_MEMBERS, getSharedUsers()));

        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN, getUsn()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_NAME, getUsnName()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_UID_PARENT, getUsnParent()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_COMMENT, getUsnComment()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_ORDER, getUsnOrder()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_COLLAPSED, getUsnCollapsed()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_GROUP, getUsnGroup()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_SHOW, getUsnShow()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_FAVORITE, getUsnFavorite()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_ISCLOSED, getUsnClosed()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_QUIET, getUsnQuiet()));
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
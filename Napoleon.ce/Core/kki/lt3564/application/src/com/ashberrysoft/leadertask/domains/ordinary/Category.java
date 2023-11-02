package com.ashberrysoft.leadertask.domains.ordinary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Intent;
import android.text.TextUtils;

import com.ashberrysoft.leadertask.data_providers.network.BaseSOAP;
import com.ashberrysoft.leadertask.enums.ETreeDataNodeLevel;
import com.ashberrysoft.leadertask.xml_handlers.BaseLionEntityInterface;
import com.j256.ormlite.field.DatabaseField;
import com.v2soft.AndLib.dao.TreeDataContainer;

public class Category extends TreeDataContainer<Category> implements SlidingMenuTreeDataContainer, Serializable,
        IEntity, Comparable<Category>, BaseLionEntityInterface {

    private static final long serialVersionUID = 1L;
    public static final String NO_COLOR = "-1";

    public static final String FIELD_UID = "UID";
    public static final String FIELD_UID_PARENT = "UIDParent";
    public static final String FIELD_COLLAPSED = "Collapsed";
    public static final String FIELD_ORDER = "Order";
    public static final String FIELD_NAME = "Name";
    public static final String FIELD_COMMENT = "Comment";
    public static final String FIELD_GROUP = "Group";
    public static final String FIELD_FAVORITE = "Favorite";
    public static final String FIELD_SHOW = "Show";
    public static final String FIELD_USN = "__usn_entity";
    public static final String FIELD_USN_UID_PARENT = "__usn_field_uid_parent";
    public static final String FIELD_USN_COLLAPSED = "__usn_field_collapsed";
    public static final String FIELD_USN_ORDER = "__usn_field_order";
    public static final String FIELD_USN_NAME = "__usn_field_name";
    public static final String FIELD_USN_COMMENT = "__usn_field_comment";
    public static final String FIELD_USN_FAVORITE = "__usn_field_favorite";
    public static final String FIELD_USN_GROUP = "__usn_field_group";
    public static final String FIELD_USN_SHOW = "__usn_field_show";
    public static final String FIELD_TASK_ALL = "taskAll";
    public static final String FIELD_TASK_ALL_COMPLETED = "taskAllCompleted";
    public static final String FIELD_TASK_UNREAD = "taskUnRead";
    public static final String FIELD_TASK_UNREAD_COMPLETED = "taskUnReadCompleted";
    public static final String SERVER_CLASS = "LionTag";
    public static final String FIELD_EMAIL_CREATOR = "EmailCreator";
    public static final String FIELD_COLOR = "BackColor";
    public static final String FIELD_USN_FIELD_COLOR = "__usn_field_backcolor";

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
    @DatabaseField(columnName = FIELD_NAME, index = true)
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
    private boolean mFavorite;

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
     * IsShow – отображать в навигаторе (0 или 1) <br>
     * <b>WARNING!</b> Используется поле "mShow", а не Category.FIELD_SHOW
     * 
     */
    @DatabaseField
    private boolean mShow;

    /**
     * USN_ IsShow – номер изменения поля отображать в навигаторе (число, начиная с 0)
     */
    @DatabaseField
    private int mUsnShow;

    @DatabaseField
    private String mCreator;

    @DatabaseField
    private String mColor;

    @DatabaseField
    private int mUsnColor;

    private Category mParent;
    private int mLevel;

    // default constructor
    public Category() {
        mChilds = new ArrayList<Category>(0);
        mShow = true;
    }

    // parameterized constructor
    public Category(Map<String, String> map) {
        setUsn(Integer.parseInt(map.get(FIELD_USN)));
        setId(checkSoapUUID(map.get(FIELD_UID)));
        if (map.containsKey(FIELD_UID_PARENT))
            setParentId(checkSoapUUID(map.get(FIELD_UID_PARENT)));
        setUsnParent(Integer.parseInt(map.get(FIELD_USN_UID_PARENT)));
        setCollapsed((map.get(FIELD_COLLAPSED)).equals("1"));
        setUsnCollapsed(Integer.parseInt(map.get(FIELD_USN_COLLAPSED)));
        setOrder(Integer.parseInt(map.get(FIELD_ORDER)));
        setUsnOrder(Integer.parseInt(map.get(FIELD_USN_ORDER)));
        setName(checkSoap(map.get(FIELD_NAME)));
        setUsnName(Integer.parseInt(map.get(FIELD_USN_NAME)));
        if (map.containsKey(FIELD_COMMENT))
            setComment(checkSoap(map.get(FIELD_COMMENT)));
        setUsnComment(Integer.parseInt(map.get(FIELD_USN_COMMENT)));
        setFaforite((map.get(FIELD_FAVORITE)).equals("1"));
        setUsnFavorite(Integer.parseInt(map.get(FIELD_USN_FAVORITE)));
        setGroup((map.get(FIELD_GROUP)).equals("1"));
        setUsnGroup(Integer.parseInt(map.get(FIELD_USN_GROUP)));
        setShow((map.get(FIELD_SHOW)).equals("1"));
        setUsnShow(Integer.parseInt(map.get(FIELD_USN_SHOW)));
        setCreator(map.get(FIELD_EMAIL_CREATOR));
        if (map.containsKey(FIELD_COLOR))
            setColor(checkSoap(map.get(FIELD_COLOR)));
        setUsnColor(Integer.parseInt(map.get(FIELD_USN_FIELD_COLOR)));
    }

    /*
     * setterts for class fields
     */
    public void setParent(Category parent) {
        mParent = parent;
    }

    public void setLevel(int level) {
        mLevel = level;
    }

    public void setUsn(long mUsn) {
        this.mUsn = mUsn;
    }

    public void setUsnPlusPlus() {
        mUsn++;
    }

    public Category setId(UUID mId) {
        this.mId = mId;
        return this;
    }

    public void setParentId(UUID mParentId) {
        this.mParentId = mParentId;
    }

    public void setUsnParent(int mUsnParent) {
        this.mUsnParent = mUsnParent;
    }

    public void setCollapsed(boolean mCollapsed) {
        this.mCollapsed = mCollapsed;
        isExpanded = !mCollapsed;
    }

    public void setUsnCollapsed(int mUsnCollapsed) {
        this.mUsnCollapsed = mUsnCollapsed;
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

    public void setComment(String mComment) {
        this.mComment = mComment;
    }

    public void setUsnComment(int mUsnComment) {
        this.mUsnComment = mUsnComment;
    }

    public void setFaforite(boolean mFaforite) {
        this.mFavorite = mFaforite;
    }

    public void setUsnFavorite(int mUsnFavorite) {
        this.mUsnFavorite = mUsnFavorite;
    }

    public void setGroup(boolean mGroup) {
        this.mGroup = mGroup;
    }

    public void setUsnGroup(int mUsnGroup) {
        this.mUsnGroup = mUsnGroup;
    }

    public void setShow(boolean mShow) {
        this.mShow = mShow;
    }

    public void setUsnShow(int mUsnShow) {
        this.mUsnShow = mUsnShow;
    }

    /*
     * getters for class fields
     */
    public Category getParent() {
        return mParent;
    }

    @Override
    public int getIndent() {
        return mLevel;
    }

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

    public UUID getParentId() {
        return mParentId;
    }

    public int getUsnParent() {
        return mUsnParent;
    }

    public boolean isCollapsed() {
        return mCollapsed;
    }

    @Override
    public boolean isExpanded() {
        return !mCollapsed;
    }

    public int getUsnCollapsed() {
        return mUsnCollapsed;
    }

    public int getOrder() {
        return mOrder;
    }

    public int getUsnOrder() {
        return mUsnOrder;
    }

    @Override
    public String getName() {
        return mName;
    }

    public int getUsnName() {
        return mUsnName;
    }

    public String getComment() {
        return mComment;
    }

    public int getUsnComment() {
        return mUsnComment;
    }

    public boolean isFaforite() {
        return mFavorite;
    }

    public int getUsnFavorite() {
        return mUsnFavorite;
    }

    public boolean isGroup() {
        return mGroup;
    }

    public int getUsnGroup() {
        return mUsnGroup;
    }

    public boolean isShow() {
        return mShow;
    }

    public int getUsnShow() {
        return mUsnShow;
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

    /*
     * add new subcategory
     */
    public void addChild(Category category) {
        mChilds.add(category);
        category.setParent(this);
        category.setLevel(mLevel + 1);
    }

    /*
     * get ordinal value of enum costant
     */
    @Override
    public int getNodeLevel() {
        return ETreeDataNodeLevel.CATEGORY.ordinal();
    }

    /*
     * does current category have subcategories?
     */
    @Override
    public boolean isExpandable() {
        return !mChilds.isEmpty();
    }

    @Override
    public int compareTo(Category another) {
        if (getOrder() > another.getOrder()) {
            return 1;

        } else if (getOrder() < another.getOrder()) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return mName + "(" + mId.toString() + ")";
    }

    /**
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     */
    @Override
    public int hashCode() {
        return mId.hashCode();
    }

    /**
     * @author V.Shcryabets<vshcryabets@gmail.com>
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o instanceof Category) {
            return ((Category) o).mId.equals(mId);
        }

        return super.equals(o);
    }

    @Override
    public String getFilterId() {
        return mId.toString();
    }

    @Override
    public void getLionEntity(StringBuilder sb) {
        sb.append(BaseSOAP.getOpen(SERVER_CLASS));

        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN, getUsn()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_NAME, getUsnName()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_UID_PARENT, getUsnParent()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_COMMENT, getUsnComment()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_ORDER, getUsnOrder()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_COLLAPSED, getUsnCollapsed()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_GROUP, getUsnGroup()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_SHOW, getUsnShow()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_FAVORITE, getUsnFavorite()));

        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_UID, getId()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_UID_PARENT, getParentId()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_ORDER, getOrder()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_COLLAPSED, isCollapsed()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_GROUP, isGroup()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_SHOW, isShow()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_FAVORITE, isFaforite()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_NAME, getName()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_COMMENT, getComment()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_COLOR, getColor()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_USN_FIELD_COLOR, getUsnColor()));
        sb.append(BaseSOAP.getXmlnsValueLine(FIELD_EMAIL_CREATOR, getCreator()));

        sb.append(BaseSOAP.getClose(SERVER_CLASS));
    }

    @Override
    public void fillKeyValue(String key, String value) {
        if (FIELD_USN.equalsIgnoreCase(key)) {
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

        if (FIELD_UID.equalsIgnoreCase(key)) {
            mId = UUID.fromString(value);
        }

        else if (FIELD_UID_PARENT.equalsIgnoreCase(key)) {
            if (!TextUtils.isEmpty(value)) {
                mParentId = UUID.fromString(value);
            }
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
            mFavorite = BaseSOAP.equalsOne(value);
        }

        else if (FIELD_NAME.equalsIgnoreCase(key)) {
            mName = value;
        }

        else if (FIELD_COMMENT.equalsIgnoreCase(key)) {
            mComment = value;
        }
        else if (FIELD_COLOR.equalsIgnoreCase(key)) {
            mColor = value;
        }
        else if (FIELD_EMAIL_CREATOR.equalsIgnoreCase(key)) {
            mCreator = value;
        }
        else if (FIELD_USN_FIELD_COLOR.equalsIgnoreCase(key)) {
            mUsnColor = Integer.parseInt(value);
        }
    }

    @Override
    public String getServerClass() {
        return SERVER_CLASS;
    }

    @Override
    public ContentValues getContentValues(ContentValues cv) {
        return null;
    }

    public static final Comparator<Category> COMPARATOR = new Comparator<Category>() {
        @Override
        public int compare(Category lhs, Category rhs) {
            return lhs.getId().compareTo(rhs.getId());
        }
    };

    public String getColor() {
        return mColor;
    }

    public void setColor(String color) {
        mColor = color;
    }

    public String getCreator() {
        return mCreator;
    }

    public void setCreator(String creator) {
        mCreator = creator;
    }

    public int getUsnColor() {
        return mUsnColor;
    }

    public void setUsnColor(int usnColor) {
        mUsnColor = usnColor;
    }

}
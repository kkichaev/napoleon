package com.ashberrysoft.leadertask.domains.ordinary;

import java.io.Serializable;
import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Класс, для связи задачи с категорией.
 * 
 * @author Tetiana Diachuk (diacht@gmail.com)
 * 
 */
@DatabaseTable(tableName = "task_category")
public class TaskCategory implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String FIELD_CATEGORY_UID = "CategoryUID";
    public static final String FIELD_TASK_UID = "TaskUID";

    @DatabaseField(generatedId = true)
    private int mId;

    /**
     * UID - уникальный идентификатор категории элемента (текст)
     */
    @DatabaseField(columnName = FIELD_CATEGORY_UID, index = true, uniqueCombo = true)
    private UUID mCategoryUID;

    /**
     * TaskUID – идентификатор задачи (текст)
     */
    @DatabaseField(columnName = FIELD_TASK_UID, index = true, uniqueCombo = true)
    private UUID mTaskUID;

    public TaskCategory() {}

    // parameterized constructor
    public TaskCategory(UUID id, UUID task_id) {
        setCategoryUID(id);
        setTaskUID(task_id);
    }

    /*
     * setterts for class fields
     */
    public void setCategoryUID(UUID mId) {
        this.mCategoryUID = mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public void setTaskUID(UUID mTaskUID) {
        this.mTaskUID = mTaskUID;
    }

    /*
     * getters for class fields
     */
    public UUID getCategoryUID() {
        return mCategoryUID;
    }

    public UUID getTaskUID() {
        return mTaskUID;
    }

    public int getId() {
        return mId;
    }
}
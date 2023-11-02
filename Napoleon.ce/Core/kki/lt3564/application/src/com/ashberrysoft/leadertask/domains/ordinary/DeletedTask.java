package com.ashberrysoft.leadertask.domains.ordinary;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
/**
 * Данные про удаление задачи 
 *
 */
@DatabaseTable(tableName="deleted_tasks")
public class DeletedTask implements Serializable{
	private static final long serialVersionUID = 1L;

	/**
     * уникальный идентификатор удаленного элемента (текст)
     */
    @DatabaseField(id = true)
    private UUID mId;
    
    /**
     * дата-время удаления элемента (дата-время)
     */
    @DatabaseField
    private Date mDeleteDate;
    
    public DeletedTask() {}
    
    public UUID getId() {
        return mId;
    }
    
    public void setId(UUID id) {
        mId = id;
    }
    
    public Date getDeleteDate() {
        return mDeleteDate;
    }
    
    public void setDeleteDate(Date date) {
        mDeleteDate = date;
    }
}

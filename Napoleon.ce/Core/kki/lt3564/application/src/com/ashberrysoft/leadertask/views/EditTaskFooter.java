package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.ashberrysoft.leadertask.R;

/**
 * Отображение кнопок в редактировании задачи
 * 
 * @author Tetiana Diachuk (diacht@gmail.com)
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * @deprecated
 */
public class EditTaskFooter extends LinearLayout {
//    private HashMap<UUID, Connection> mCategoriesUuid;
//    private HashMap<UUID, Connection> mProjectsUuid;
//    private HashMap<String, String> mContacts;

//    public interface HeaderCallBack {
//        void pressBack();
//    }

    public EditTaskFooter(Context context, AttributeSet attrs) {
        this(context, attrs, false);
    }
    
    public EditTaskFooter(Context context, AttributeSet attrs, boolean showProject) {
        super(context, attrs);
//        mCategoriesUuid = new HashMap<UUID, Connection>();
//        mProjectsUuid = new HashMap<UUID, Connection>();
//        mContacts = new HashMap<String, String>();
        inflate(context, R.layout.edit_task_footer, this);
        /**
         * Если у задачи заказчик не текущий пользователь, то кнопка выбора проекта не отображается.
         * 
         */
        findViewById(R.id.btn_project).setVisibility( showProject ? View.VISIBLE : View.GONE );
    }

    // set task categories
//    public void setCategories(HashMap<UUID, Connection> categoriesUuid) {
//        mCategoriesUuid = categoriesUuid;
//    }
//
//    // set task project
//    public void setProjects(HashMap<UUID, Connection> projectsUuid) {
//        mProjectsUuid = projectsUuid;
//    }
//
//    // set task contacts
//    public void setContacts(HashMap<String, String> contacts) {
//        mContacts = contacts;
//    }

    // get task categories
//    public String getTaskCategories() {
//        return convertCategoriesToString(mCategoriesUuid);
//    }
//
//    // get task project
//    public UUID getTaskProject() {
//        Set<Map.Entry<UUID, Connection>> entrySet = mProjectsUuid.entrySet();
//        Iterator<Map.Entry<UUID, Connection>> iterator = entrySet.iterator();
//        return iterator.hasNext() ? iterator.next().getKey() : null;
//    }
//
//    // get task contacts
//    public String getTaskContacts() {
//        return convertContactsToString(mContacts);
//    }

//    public String convertCategoriesToString(HashMap<UUID, Connection> content) {
//        StringBuilder builder = new StringBuilder();
//        Set<Map.Entry<UUID, Connection>> entrySet = content.entrySet();
//        Iterator<Map.Entry<UUID, Connection>> iterator = entrySet.iterator();
//        while (iterator.hasNext()) {
//            if (!builder.toString().isEmpty())
//                builder.append("..");
//            builder.append(iterator.next().getValue().getUuid().toString());
//        }
//        return builder.toString();
//    }
//
//    public String convertContactsToString(HashMap<String, String> content) {
//        StringBuilder builder = new StringBuilder();
//        Set<Map.Entry<String, String>> entrySet = content.entrySet();
//        Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
//        while (iterator.hasNext()) {
//            if (!builder.toString().isEmpty())
//                builder.append(",,");
//            builder.append(iterator.next().getValue());
//        }
//        return builder.toString();
//    }

}

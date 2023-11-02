package com.ashberrysoft.leadertask.modern.helper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;

import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.xml_handlers.BaseLionEntityInterface;

public class ProjectCategoryTreeHelper {

    private final DbHelper mDbHelper;

    public ProjectCategoryTreeHelper(Context context) {
        mDbHelper = DbHelper.getInstance(context);
    }

    public List<Project>  getProjectsTree() {
        final List<Project> projects;
        try {
            projects = mDbHelper.getProjectDao().queryBuilder().orderBy(Project.FIELD_ORDER, true).query();

        } catch (SQLException e) {
            Utils.toLog(e);
            return new ArrayList<>(0);
        }

        final List<Project> tree = new ArrayList<>();
        for (Project project : projects) {
            if (project.getParentId() == null || !isFindedInList(projects, project.getParentId())) {
                tree.add(project);
                recursiveProjectsTreeSearch(project, projects);
            }
        }

        return tree;
    }

    private static void recursiveProjectsTreeSearch(Project parent, List<Project> projects) {
        for (Project project : projects) {
            if (parent.getId().equals(project.getParentId())) {
                parent.addChild(project);
                recursiveProjectsTreeSearch(project, projects);
            }
        }
    }

    public List<Category> getCategoriesTree() {
        final List<Category> categories;
        try {
            categories = mDbHelper.getCategoryDao().queryBuilder().orderBy(Category.FIELD_ORDER, true).query();

        } catch (SQLException e) {
            Utils.toLog(e);
            return new ArrayList<>(0);
        }

        final List<Category> tree = new ArrayList<>();
        for (Category category : categories) {
            if (category.getParentId() == null || !isFindedInList(categories, category.getParentId())) {
                tree.add(category);
                recursiveCategoriesTreeSearch(category, categories);
            }
        }

        return tree;
    }

    private static boolean isFindedInList(List <? extends BaseLionEntityInterface> list, UUID uuid)
    {
        for (BaseLionEntityInterface entity : list) {
            if (entity.getId().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    private static void recursiveCategoriesTreeSearch(Category parent, List<Category> categories) {
        for (Category category : categories) {
            if (parent.getId().equals(category.getParentId())) {
                parent.addChild(category);
                recursiveCategoriesTreeSearch(category, categories);
            }
        }
    }
}
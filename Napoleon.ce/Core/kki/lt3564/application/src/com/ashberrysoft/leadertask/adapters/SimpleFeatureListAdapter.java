package com.ashberrysoft.leadertask.adapters;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ashberrysoft.leadertask.activities.FeaturesActivity.FeatureType;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.ContactContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.modern.loader.BaseCollapsibleTaskLoader;
import com.ashberrysoft.leadertask.views.ListItemSimpleFeatureView;
import com.ashberrysoft.leadertask.views.ListItemSimpleFeatureView.OnSimpleFeatureViewListener;
import com.v2soft.AndLib.dao.ITreePureNode;

import static android.R.attr.data;
import static com.ashberrysoft.leadertask.R.drawable.settings;
import static com.ashberrysoft.leadertask.R.id.categories;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class SimpleFeatureListAdapter<DATA> extends BaseAdapter {

    // BASE
    private final Context mContext;
    private final FeatureType mFeatureType;

    // VALUE's
    private List<DATA> mData = new ArrayList<DATA>(0);

    // LISTENER
    private final OnSimpleFeatureViewListener<DATA> mListener;

    public SimpleFeatureListAdapter(final Context context, final FeatureType type,
            final OnSimpleFeatureViewListener<DATA> listener) {
        mContext = context;
        mFeatureType = type;
        mListener = listener;
    }

    public void setData(List<DATA> data) {
        mData.clear();
        mData.addAll(data);
    }

    public void clear() {
        mData.clear();
    }

    public static List<ITreePureNode> getListCategories(LTSettings settings, DbHelper dbHelper) {
        final List<ITreePureNode> data = new ArrayList<ITreePureNode>();
        settings.setLastFeatureOrder(0);

        final List<Category> categories;

        categories = dbHelper.getAllMyCategories();

        Collections.sort(categories);

        for (Category c : categories) {
            if (c.getParentId() == null ) {
                settings.setLastFeatureOrder(c.getOrder());
                data.add(c);
                processListSubCategories(data, c, categories);
            } else {
                boolean parentIReal = false;
                for (Category c2 : categories) {
                    if (c.getParentId().equals(c2.getId())) {
                        parentIReal = true;
                        break;
                    }
                }

                if (!parentIReal) {
                    settings.setLastFeatureOrder(c.getOrder());
                    data.add(c);
                    processListSubCategories(data, c, categories);
                }
            }

        }

        return data;
    }

    private static void processListSubCategories(List<ITreePureNode> data, Category parent, List<Category> categories) {
        for (Category c : categories) {
            if (parent.getId().equals(c.getParentId())) {
                data.add(c);
                parent.addChild(c);
                processListSubCategories(data, c, categories);
            }
        }
    }

    public static List<ITreePureNode> getListProjects(LTSettings settings, DbHelper dbHelper) {
        final List<ITreePureNode> data = new ArrayList<ITreePureNode>();
        settings.setLastFeatureOrder(0);

        final List<Project> projects;
        try {
            projects = dbHelper.getProjectDao().queryForAll();
        } catch (SQLException e) {
            return data;
        }
        Collections.sort(projects);

        for (Project p : projects) {
            if (TextUtils.isEmpty(p.getName()) || !settings.getUserName().equals(p.getCreator())) {
                continue;
            } else {
                if (p.getParentId() == null) {
                    settings.setLastFeatureOrder(p.getOrder());
                    data.add(p);
                    processListSubProjects(data, settings, p, projects);
                }
            }
        }

        return data;
    }

    public static List<ITreePureNode> getListContactGroups(LTSettings settings, DbHelper dbHelper) {
        final List<ITreePureNode> data = new ArrayList<ITreePureNode>();
        settings.setLastFeatureOrder(0);

        List<ContactsGroup> listContactsGroups = new ArrayList<ContactsGroup>() {};
        List<ContactsGroup> listMyContactsGroups = dbHelper.getMyContactsGroups();
        List<ContactsGroup> listSharedContactsGroups = dbHelper.getSharedContactsGroups();

        listContactsGroups.addAll(listMyContactsGroups);
        listContactsGroups.addAll(listSharedContactsGroups);

        for (ContactsGroup p : listContactsGroups) {
            if (TextUtils.isEmpty(p.getName())) {
                continue;
            } else {
                if (p.getParentId() == null) {
                    settings.setLastFeatureOrder(p.getOrder());
                    data.add(p);
                    processListSubContactGroups(data, settings, p, listContactsGroups);
                }
            }
        }

        return data;
    }

    public static List<ITreePureNode> getListContacts(LTSettings settings, DbHelper dbHelper) {
        final List<ITreePureNode> data = new ArrayList<ITreePureNode>();
        settings.setLastFeatureOrder(0);

        List<Contact> contacts = new ArrayList<>();
        contacts = dbHelper.getAllContactsWithOrder(LTSettings.getInstance().getContactsOrder());
        if (contacts == null) {
            contacts = new ArrayList<>();
        }
        Collections.sort(contacts);

        for (Contact p : contacts) {
            if (TextUtils.isEmpty(p.getTitle())) {
                continue;
            } else {
                if (p.getUidParent() == null) {
                    settings.setLastFeatureOrder(p.getOrder());
                    data.add(p);
                    processListSubContacts(data, settings, p, contacts);
                }
            }
        }

        return data;
    }

    public static List<ITreePureNode> getListContactsWithSearch(LTSettings settings, DbHelper dbHelper, String search) {
        final List<ITreePureNode> data = new ArrayList<ITreePureNode>();
        settings.setLastFeatureOrder(0);

        List<Contact> contacts = new ArrayList<>();
        contacts = dbHelper.getContactsWithSearch(search);

        Collections.sort(contacts);

        for (Contact p : contacts) {
            if (TextUtils.isEmpty(p.getTitle())) {
                continue;
            } else {
                data.add(p);
            }
        }

        return data;
    }

    private static void processListSubContacts(List<ITreePureNode> data, LTSettings settings, Contact parent,
                                               List<Contact> contacts) {
        for (Contact p : contacts) {
            if (parent.getId().equals(p.getUidParent())) {
                if (TextUtils.isEmpty(p.getTitle())) {
                    continue;
                } else {
                    data.add(p);
                    parent.addChild(p);
                    processListSubContacts(data, settings, p, contacts);
                }
            }
        }
    }

    private static void processListSubProjects(List<ITreePureNode> data, LTSettings settings, Project parent,
            List<Project> projects) {
        for (Project p : projects) {
            if (parent.getId().equals(p.getParentId())) {
                if (TextUtils.isEmpty(p.getName()) || !settings.getUserName().equals(p.getCreator())) {
                    continue;
                } else {
                    data.add(p);
                    parent.addChild(p);
                    processListSubProjects(data, settings, p, projects);
                }
            }
        }
    }

    private static void processListSubContactGroups(List<ITreePureNode> data, LTSettings settings, ContactsGroup parent,
                                               List<ContactsGroup> contactsGroups) {
        for (ContactsGroup p : contactsGroups) {
            if (parent.getId().equals(p.getParentId())) {
                if (TextUtils.isEmpty(p.getName())) {
                    continue;
                } else {
                    data.add(p);
                    parent.addChild(p);
                    processListSubContactGroups(data, settings, p, contactsGroups);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View cV, ViewGroup parent) {
        final ListItemSimpleFeatureView<DATA> v = cV != null ? (ListItemSimpleFeatureView<DATA>) cV
                : new ListItemSimpleFeatureView<DATA>(mContext, mFeatureType, mListener);

        v.setData(getItem(position), position-1 > 0 ? getItem(position-1) : null, position+1 < getCount() ? getItem(position+1) : null);
        v.setPosition(position);

        return v;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public DATA getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<DATA> getData() {
        return mData;
    }
}
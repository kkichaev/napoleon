package com.ashberrysoft.leadertask.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.FeaturesActivity;
import com.ashberrysoft.leadertask.activities.SearchActivity;
import com.ashberrysoft.leadertask.adapters.CustomSearchListAdapter;
import com.ashberrysoft.leadertask.adapters.SimpleFeatureListAdapter;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.data_providers.network.SynchronizationTask;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.modern.activity.EditTaskActivity;
import com.ashberrysoft.leadertask.modern.activity.EditTaskActivity.OnUpdateSearchAdapter;
import com.ashberrysoft.leadertask.modern.cache.TaskMessageCache;
import com.ashberrysoft.leadertask.modern.dialog.TaskPerformerDialog;
import com.ashberrysoft.leadertask.modern.dialog.TaskTermDialog;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.fragment.TasksFragment;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.ListItemSearchView.OnSearchViewListener;
import com.software.shell.fab.ActionButton;
import com.v2soft.AndLib.dao.ITreePureNode;

import java.util.Date;

import static com.ashberrysoft.leadertask.activities.SearchActivity.SEARCH_FRAGMENT_TAG;
import static com.ashberrysoft.leadertask.modern.fragment.MenuFragment.EXTRA_MENU_ITEM;
import static com.ashberrysoft.leadertask.modern.fragment.TasksFragment.hasParent;
import static com.ashberrysoft.leadertask.modern.fragment.TasksFragment.mMenuItem;
import static com.ashberrysoft.leadertask.modern.fragment.TasksFragment.mTempTask;

public class SearchFragment extends Fragment implements OnSearchViewListener<ITreePureNode>, OnUpdateSearchAdapter {

    private static final String CLASS_PATH = SearchFragment.class.getName();
    private static final String KEY_SAVED_QUERY = CLASS_PATH + "KEY_SAVED_QUERY";
    private static final String EXTRA_PARENT_TASK = CLASS_PATH + "EXTRA_PARENT_TASK";

    // VALUE's
    private SearchView mSearchView;
    private String mCurrentQuery;
    private ListView mMainListView;
    private FrameLayout mHeaderLvContact;
    private ProgressDialog mProgress;
    private Handler mHandler;
    private long startTime;
    public LTask mOldTask;
    public LTask mTempTask;
    public int mPosition;
    public boolean mHasChilds;
    private LTask mParent;
    private MenuItem mSearchItem;
    private Bundle mBundle;
    public static ActionButton mActionButton;


    // ADAPTER
    private CustomSearchListAdapter<ITreePureNode> mSearchAdapter;

    public static SearchFragment newInstance(LTask parent) {
        final Bundle b = new Bundle();
        b.putSerializable(EXTRA_PARENT_TASK, parent);
        final SearchFragment f = new SearchFragment();
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        return inflater.inflate(R.layout.task_search_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        mBundle = b == null ? getArguments() : b;
        mParent = (LTask) mBundle.getSerializable(EXTRA_PARENT_TASK);
        mCurrentQuery = mBundle.getString(KEY_SAVED_QUERY);
        mHandler = new Handler();
        mSearchAdapter = new CustomSearchListAdapter<ITreePureNode>(getActivity(), this);
        setHasOptionsMenu(true);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMainListView = (ListView) view.findViewById(R.id.list_search) ;
        mMainListView.setAdapter(mSearchAdapter);
        mActionButton = (ActionButton) view.findViewById(R.id.action_button_search);

        if (mParent != null) {
            mActionButton.setVisibility(View.VISIBLE);
        } else {
            mActionButton.setVisibility(View.GONE);
        }

        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            addNewTask();
            }
        });

        adapterNotifyDataSetChanged();
        registerForContextMenu(mMainListView);
    }

    public void addNewTask() {
        String performer = null;
        long term = 0;
        String parentId = null;
        String projectId = null;
        String categoryId = null;
        String colorId = null;

        parentId = mParent.getUid();
        projectId = mParent.getUidProject();

        /*switch (mMenuItem.getMenuItemType()) {
            case BY_ME:
            case EMP:
                performer = mMenuItem.getUid();
                break;

            default:
                break;
        }*/

        final LTask task = TaskHelper.createNewTaskWithParams(LTSettings.getInstance().getUserName(), performer, term, parentId, projectId, categoryId, colorId);
        startActivity(EditTaskActivity.newInstance(getActivity(), task, true, false));
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_subtasks_search:
                onSaveInstanceState(mBundle);
                Fragment f = SearchFragment.newInstance(mTempTask);
                ((SearchActivity)getActivity()).startFragmentWithTag(f, SEARCH_FRAGMENT_TAG);
                return true;

            case R.id.menu_properties_search:
                startActivity(EditTaskActivity.newInstance(getActivity(), mTempTask, false, this));
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putString(KEY_SAVED_QUERY, mSearchView.getQuery().toString());
        b.putSerializable(EXTRA_PARENT_TASK, mParent);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mSearchItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        if (mParent != null) {
            updateAdapterDataAfterSearch("");
            mSearchItem.setVisible(false);
            hideKeyboard(getActivity());
        } else {
            mSearchView.setQueryHint(getString(R.string.search));
            mSearchView.setIconified(false);
            mSearchView.setFocusable(true);
            mSearchView.requestFocusFromTouch();
            if (!TextUtils.isEmpty(mCurrentQuery)) {
                mSearchView.setQuery(mCurrentQuery, true);
            }

            mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    resetAdapterData();
                    return false;
                }
            });

            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //нажали поиск
                    if (query.length() > 0) {
                        updateAdapterDataAfterSearch(query);
                        return true;
                    } else {
                        resetAdapterData();
                        return false;
                    }
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //посимвольно
                    if (newText.length() > 0) {
                        //updateAdapterDataAfterSearch(newText);
                        return true;
                    } else {
                        resetAdapterData();
                        return false;
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mParent = (LTask) mBundle.getSerializable(EXTRA_PARENT_TASK);
        String text = mBundle.getString(KEY_SAVED_QUERY);
        mCurrentQuery = text;
        if (!TextUtils.isEmpty(mCurrentQuery)) {
            if (mSearchView != null) {
                mSearchView.setQuery(mCurrentQuery, true);
            }
        } else {
            if (mParent != null) {
                updateAdapterDataAfterSearch("");
            }
        }

    }

    private void resetAdapterData() {
        mSearchAdapter.clear();
        adapterNotifyDataSetChanged();
    }

    private void updateAdapterDataAfterSearch(final String search) {
        setBlock(true);
        startTime = System.currentTimeMillis();
        Thread searchThread = new Thread(new Runnable() {
            public void run() {
                List <ITreePureNode> list = new ArrayList<>();
                List<LTask> dataTasks;
                if (mParent == null) {
                    dataTasks = CustomSearchListAdapter.getListTasksAfterSearch(LTSettings.getInstance(), DbHelper.getInstance(getActivity().getApplicationContext()), search);
                    mSearchAdapter.setData(dataTasks, LTSettings.getInstance().isContactsEnabled() == false ?  list : CustomSearchListAdapter.getListContactsAfterSearch(LTSettings.getInstance(), DbHelper.getInstance(getActivity().getApplicationContext()), search));
                    if (mSearchAdapter.getCount() == 0) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Utils.showToast(getActivity(), getString(R.string.search_not_elements));
                            }
                        });
                    }
                } else {
                    dataTasks = CustomSearchListAdapter.getListTasksWithParentUID(LTSettings.getInstance(), DbHelper.getInstance(getActivity().getApplicationContext()), mParent.getUid());
                    mSearchAdapter.setData(dataTasks, list);
                }


                setBlock(false);
            }
        });
        searchThread.start();

    }

    private void setBlock(boolean value) {
        if (value) {
            if (mProgress == null) {
                mProgress = new ProgressDialog(getActivity());
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.setMessage(getString(R.string.blocking_process));
            }
            mProgress.show();

        } else {
            mHandler.post(mSetBlockFalse);
        }
    }

    private final Runnable mSetBlockFalse = new Runnable() {
        @Override
        public void run() {
            if (mProgress != null) {
                adapterNotifyDataSetChanged();
                mProgress.dismiss();
                Log.e("Tedorius", "Общее время - " + (System.currentTimeMillis() - startTime));
                mProgress = null;
            }
        }
    };

    private void adapterNotifyDataSetChanged() {
        mSearchAdapter.notifyDataSetChanged();
    }

    private void openContactContact(Contact contact) {
        //startFragment(PropertiesContactFragment.newInstance(contact));
    }

    private int recursiveChildsCount(Contact parent) {
        int count = 0;
        if (parent == null || parent.getSubnodes() == null || parent.getSubnodes().isEmpty()) {
            return count;
        }

        for (Contact child : parent.getSubnodes()) {
            count++;
            count += recursiveChildsCount(child);
        }
        return count;
    }

    public boolean isSearchShowing() {
        return mSearchView.isIconified() ? false : true;
    }

    public void closeSearchView() {
        mSearchView.clearFocus();
        mSearchView.setIconified(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getFragmentManager().getBackStackEntryCount() > 1) {
                    getFragmentManager().popBackStack();
                } else {
                    getActivity().finish();
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onSearchContactViewClick(Contact contact, int position) {
        //Utils.showToast(getActivity(), "click contacts");
        Fragment f = PropertiesContactFragment.newInstance(contact);
        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(FeaturesActivity.FRAGMENT_CONTAINER, f);
        ft.addToBackStack(f.getClass().getName());
        ft.commit();
    }

    @Override
    public void onSearchContactViewLongClick(View v, ITreePureNode iTreePureNode, int position) {
        //Utils.showToast(getActivity(), "long click contacts");
    }

    @Override
    public void onSearchTaskViewClick(LTask task,int position, boolean hasChilds) {
        mHasChilds = hasChilds;
        mTempTask = task;
        if (mHasChilds) {
            onSaveInstanceState(mBundle);
            Fragment f = SearchFragment.newInstance(mTempTask);
            ((SearchActivity)getActivity()).startFragmentWithTag(f, SEARCH_FRAGMENT_TAG);
        } else {
            mOldTask = task;
            mPosition = position;
            startActivity(EditTaskActivity.newInstance(getActivity(), mTempTask, false, this));
        }
    }

    @Override
    public void onSearchTaskViewLongClick(View v, LTask task, int position, boolean hasChilds) {
        mHasChilds = true;
        mTempTask = task;
        //if (mHasChilds) {
            getActivity().openContextMenu(v);
        //}

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.search_activity_menu, menu);

    }

    @Override
    public void onUpdateTaskInAdapter(LTask updatedTask) {
        if (mOldTask != null) {
            mSearchAdapter.updateTask(mOldTask, updatedTask, mPosition);
            mSearchAdapter.notifyDataSetChanged();
            mOldTask = null;
        }
    }

}
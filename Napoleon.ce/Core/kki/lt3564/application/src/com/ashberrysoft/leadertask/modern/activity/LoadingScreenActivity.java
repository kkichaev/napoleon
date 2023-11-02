package com.ashberrysoft.leadertask.modern.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.ArraySet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.LoginActivity;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.data_providers.network.SynchronizationTask;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.modern.adapter.PreviewAdapter;
import com.ashberrysoft.leadertask.modern.cache.EmployeeCache;
import com.ashberrysoft.leadertask.modern.cache.MarkerCache;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.PreviewFragment;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.PreviewFragment2;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.PreviewFragment3;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.PreviewFragment4;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.PreviewFragment5;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.PreviewFragment6;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.PreviewFragmentPhone;
import com.ashberrysoft.leadertask.modern.helper.PreCreateActivityParamsHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSaveHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;
import com.ashberrysoft.leadertask.modern.helper.TaskSeriesHelper;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.modern.helper.UpdateFeatureLinkHelper;
import com.ashberrysoft.leadertask.utils.Utils;
import com.astuetz.PagerSlidingTabStrip;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import static android.R.attr.action;
import static com.ashberrysoft.leadertask.R.string.task;
import static com.ashberrysoft.leadertask.instance_sync.LeaderTaskSyncService.mApp;

public class LoadingScreenActivity extends AppCompatActivity {

    private Calendar mCalendar;

    public static Intent newInstance(Context context) {
        final Intent intent = new Intent(context, LoadingScreenActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle b) {
        PreCreateActivityParamsHelper.setActivityParams(this);
        super.onCreate(b);
        setContentView(R.layout.loading_screen_activity);
        final TextView textView = (TextView) findViewById(R.id.text_prop) ;

        mCalendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
        new load(textView).execute();
    }

    private class load extends AsyncTask<String, Void, Void> {
        TextView mTextView;

        public load(TextView textView) {
            mTextView = textView;
        }


        @Override
        protected Void doInBackground(String... params) {
            if (mApp == null)
                mApp = (LTApplication)getApplicationContext();

            if (mApp != null &&
                    mApp.getSettings() != null &&
                    mApp.getSettings().isNeedToAddUnboardingTasks()) {
                try {
                    while (LTSettings.isNeedToRunLoadingScreen == true) {
                        if (LTSettings.isNeedToRunLoadingScreen == true) {
                            Thread.sleep(50);
                        }
                    }

                    startSliding();
                    LTSettings.getInstance().setNeedToAddUnboardingTasks(false);
                    LTSettings.isNeedToRunLoadingScreen = false;
                    LTSettings.isNeedDownLoadEmpFotos = false;
                    LTSettings.getInstance().setIsNeedToShowLoadingScreen(false);
                    startActivity(SlidingActivity.newInstance(LoadingScreenActivity.this));
                    finish();
                } catch (Exception e) {

                } finally {

                }
            } else {
                try {
                    while (LTSettings.isNeedToRunLoadingScreen == true) {
                        if (LTSettings.isNeedToRunLoadingScreen == true) {
                            Thread.sleep(50);
                        }
                    }

                    LTSettings.isNeedDownLoadEmpFotos = true;
                    Utils.startSyncAlways((LTApplication) getApplicationContext());

                    LTSettings.getInstance().setIsNeedToAddUnboardingCatMar(false);
                    /*runOnUiThread(new Runnable() {
                      public void run() {
                          mTextView.setText(getResources().getString(R.string.loading_text2));
                      }
                    });*/

                    // если все фотки сотрудников скачаны
                    while (LTSettings.isNeedDownLoadEmpFotos == true) {
                        if (LTSettings.isNeedDownLoadEmpFotos == true) {
                            Thread.sleep(500);
                        }
                    }

                    LTSettings.getInstance().setIsNeedToShowLoadingScreen(false);
                    startActivity(SlidingActivity.newInstance(LoadingScreenActivity.this));

                    finish();
                } catch (Exception e) {

                } finally {
                    finish();
                }
            }
            return null;
        }
    }

    private void startSliding() {        
        try {
            Emp mEmp = null;
            List<Emp> emps = DbHelper.getListEmps(LoadingScreenActivity.this);
            for (Emp temp : emps) {
                if (temp.getLogin().equals(LTSettings.getInstance().getUserName())) {
                    mEmp = temp;
                    break;
                }
            }
            //

            saveProject(getString(R.string.preview_slide2_check1), true);
            saveProject(getString(R.string.preview_slide2_check2),  true);
            saveProject(getString(R.string.preview_slide2_check3),  true);
            saveProject(getString(R.string.preview_slide2_check4),  true);
            saveProject(getString(R.string.preview_slide2_check5),  true);
            


            final boolean hasCustomLocale = LTSettings.getInstance().getLanguageLocale() != null;
            final Locale appLocale = hasCustomLocale ?LTSettings.getInstance().getLanguageLocale() : Locale.getDefault();

            saveProjectUnboarding();
           

        } finally {
            Utils.startSync(mApp);
        }        
    }

    private void saveProject(String name, boolean add) {
        if (add) {
            Project project = new Project();
            project.setName(name);

            project.setSharedUsers(null);

            project.setUsn(0);
            project.setUsnName(project.getUsnName() + 1);
            project.setUsnSharedUsers(project.getUsnSharedUsers() + 1);
            project.setUsnComment(project.getUsnComment() + 1);

            project.setId(UUID.randomUUID());
            project.setCreator(LTSettings.getInstance().getUserName());

            project.setOrder(getOrder(getApplicationContext()) + 1);
            project.setUsnOrder(project.getUsnOrder() + 1);
            try {
                DbHelper.getInstance(mApp).getProjectDao().create(project);

            } catch (SQLException e) {
                Utils.toLog(e);
            }

            final UpdateFeatureLinkHelper linkHelper = new UpdateFeatureLinkHelper(mApp);
            linkHelper.createTotalLink(project);
        }
    }

    private void saveProjectUnboarding() {

        Category category1 = saveCategory(this.getResources().getString(R.string.unboarding_default_category1), 1, "#feec04");
        saveCategory(this.getResources().getString(R.string.unboarding_default_category2), 2, "#df0c0c");
        Category category3 =  saveCategory(this.getResources().getString(R.string.unboarding_default_category3), 3, "#008992");
        saveCategory(this.getResources().getString(R.string.preview_color_add1), 4, "#5a0046");
        saveCategory(this.getResources().getString(R.string.preview_color_add2), 5, "#a05ab9");
        saveCategory(this.getResources().getString(R.string.preview_color_add3), 6, "#465069");

        // создать цвета по анбордингу
        UUID green = saveMarker(this.getResources().getString(R.string.unboarding_default_marker1), 1, "#b6d7a8");
        UUID yellow = saveMarker(this.getResources().getString(R.string.unboarding_default_marker2), 2, "#ffe599");
        saveMarker(this.getResources().getString(R.string.unboarding_default_marker3), 3, "#f9cb9c");
        UUID red = saveMarker(this.getResources().getString(R.string.unboarding_default_marker4), 4, "#dd7e6b");
        UUID blue = saveMarker(this.getResources().getString(R.string.unboarding_default_marker5), 5, "#a4c2f4");

        final LTask taskBlue = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, null, null,null, blue.toString().toUpperCase());
        taskBlue.setName(getResources().getString(R.string.color_task_name1));
        taskBlue.setMarkerOrder(Marker.getMarkerOrderFromLowerUid(mApp, blue.toString().toUpperCase()));
        saveTask(taskBlue);

        final LTask taskRed = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, null, null, null, red.toString().toUpperCase());
        taskRed.setName(getResources().getString(R.string.color_task_name2));
        taskRed.setMarkerOrder(Marker.getMarkerOrderFromLowerUid(mApp, red.toString().toUpperCase()));
        saveTask(taskRed);

        final LTask taskCategory = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, null, null, category1.getId().toString().toUpperCase(), null);
        taskCategory.setName(getResources().getString(R.string.category_task_name1));
        saveTask(taskCategory);

        MarkerCache.getInstance(mApp).refreshCache();

        //
        final Set<Category> categories = new ArraySet<>();
        categories.add(category1);
        categories.add(category3);

        final List<Category> list = new ArrayList<>(categories.size());
        for (Iterator<Category> iterator = categories.iterator(); iterator.hasNext();) {
            list.add(iterator.next());
        }
        Collections.sort(list, Category.COMPARATOR);
        String categoriesForTask = TaskHelper.getStringFromCategories(list);
        //

//        final LTask pro_main0 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, null, null, null, red.toString().toUpperCase());
//        pro_main0.setName(getResources().getString(R.string.pro_main0));
//        pro_main0.setComment(getResources().getString(R.string.pro_main01));
//        setParams(pro_main0);
//        saveTask(pro_main0);

        // 1

        final LTask main1task = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, null, null, null, null);
        main1task.setName(getResources().getString(R.string.pro_main1));
        main1task.setCategories(categoriesForTask);
        setParams(main1task);
        saveTask(main1task);

        final LTask pro_main1_2 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main1task.getUid(), null, null, null);
        pro_main1_2.setName(getResources().getString(R.string.pro_main1_2));
        saveTask(pro_main1_2);

        final LTask pro_main1_3 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main1task.getUid(), null, null, null);
        pro_main1_3.setName(getResources().getString(R.string.pro_main1_3));
        pro_main1_3.setComment(getResources().getString(R.string.pro_main1_31));
        saveTask(pro_main1_3);

        final LTask pro_main1_4 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main1task.getUid(), null, null, null);
        pro_main1_4.setName(getResources().getString(R.string.pro_main1_4));
        saveTask(pro_main1_4);

        final LTask pro_main1_4_1 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, pro_main1_4.getUid(), null, null, null);
        pro_main1_4_1.setName(getResources().getString(R.string.pro_main1_4_1));
        saveTask(pro_main1_4_1);

        final LTask pro_main1_5 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main1task.getUid(), null, null, null);
        pro_main1_5.setName(getResources().getString(R.string.pro_main1_5));
        pro_main1_5.setComment(getResources().getString(R.string.pro_main1_51));
        saveTask(pro_main1_5);

        final LTask pro_main1_6 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main1task.getUid(), null, null, null);
        pro_main1_6.setName(getResources().getString(R.string.pro_main1_6));
        pro_main1_6.setComment(getResources().getString(R.string.pro_main1_61));
        saveTask(pro_main1_6);

        final LTask pro_main1_7 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main1task.getUid(), null, null, null);
        pro_main1_7.setName(getResources().getString(R.string.pro_main1_7));
        pro_main1_7.setComment(getResources().getString(R.string.pro_main1_71));
        saveTask(pro_main1_7);

        final LTask pro_main1_8 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main1task.getUid(), null, null, null);
        pro_main1_8.setName(getResources().getString(R.string.pro_main1_8));
        pro_main1_8.setComment(getResources().getString(R.string.pro_main1_81));
        saveTask(pro_main1_8);

        final LTask pro_main1_9 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main1task.getUid(), null, null, null);
        pro_main1_9.setName(getResources().getString(R.string.pro_main1_9));
        pro_main1_9.setComment(getResources().getString(R.string.pro_main1_91));
        saveTask(pro_main1_9);

        final LTask pro_main1_10 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main1task.getUid(), null, null, null);
        pro_main1_10.setName(getResources().getString(R.string.pro_main1_10));
        pro_main1_10.setComment(getResources().getString(R.string.pro_main1_101));
        saveTask(pro_main1_10);

        final LTask pro_main1_11 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main1task.getUid(), null, null, null);
        pro_main1_11.setName(getResources().getString(R.string.pro_main1_11));
        pro_main1_11.setComment(getResources().getString(R.string.pro_main1_111));
        saveTask(pro_main1_11);

        final LTask pro_main1_12 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main1task.getUid(), null, null, null);
        pro_main1_12.setName(getResources().getString(R.string.pro_main1_12));
        pro_main1_12.setComment(getResources().getString(R.string.pro_main1_121));
        saveTask(pro_main1_12);

        // 2

        final LTask main2task = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, null, null, null, green.toString().toUpperCase());
        main2task.setName(getResources().getString(R.string.pro_main2));
        setParams(main2task);

        saveTask(main2task);

        final LTask pro_main2_1 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main2task.getUid(), null, null, null);
        pro_main2_1.setName(getResources().getString(R.string.pro_main2_1));
        pro_main2_1.setComment(getResources().getString(R.string.pro_main2_11));
        saveTask(pro_main2_1);

        final LTask pro_main2_2 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main2task.getUid(), null, null, null);
        pro_main2_2.setName(getResources().getString(R.string.pro_main2_2));
        pro_main2_2.setComment(getResources().getString(R.string.pro_main2_22));
        saveTask(pro_main2_2);

        final LTask pro_main2_3 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main2task.getUid(), null, null, null);
        pro_main2_3.setName(getResources().getString(R.string.pro_main2_3));
        pro_main2_3.setComment(getResources().getString(R.string.pro_main2_33));
        saveTask(pro_main2_3);

        final LTask pro_main2_4 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main2task.getUid(), null, null, null);
        pro_main2_4.setName(getResources().getString(R.string.pro_main2_4));
        pro_main2_4.setComment(getResources().getString(R.string.pro_main2_44));
        saveTask(pro_main2_4);

        final LTask pro_main2_5 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main2task.getUid(), null, null, null);
        pro_main2_5.setName(getResources().getString(R.string.pro_main2_5));
        pro_main2_5.setComment(getResources().getString(R.string.pro_main2_55));
        saveTask(pro_main2_5);

        final LTask pro_main2_6 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main2task.getUid(), null, null, null);
        pro_main2_6.setName(getResources().getString(R.string.pro_main2_6));
        pro_main2_6.setComment(getResources().getString(R.string.pro_main2_66));
        saveTask(pro_main2_6);

        final LTask pro_main2_7 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main2task.getUid(), null, null, null);
        pro_main2_7.setName(getResources().getString(R.string.pro_main2_7));
        pro_main2_7.setComment(getResources().getString(R.string.pro_main2_77));
        saveTask(pro_main2_7);

        final LTask pro_main2_8 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main2task.getUid(), null, null, null);
        pro_main2_8.setName(getResources().getString(R.string.pro_main2_8));
        pro_main2_8.setComment(getResources().getString(R.string.pro_main2_88));
        saveTask(pro_main2_8);

        // 3

        final LTask main3task = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, null, null, null, yellow.toString().toUpperCase());
        main3task.setName(getResources().getString(R.string.pro_main3));
        setParams(main3task);
        saveTask(main3task);

        final LTask pro_main3_1 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main3task.getUid(), null, null, null);
        pro_main3_1.setName(getResources().getString(R.string.pro_main3_1));
        pro_main3_1.setComment(getResources().getString(R.string.pro_main3_11));
        saveTask(pro_main3_1);

        final LTask pro_main3_2 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main3task.getUid(), null, null, null);
        pro_main3_2.setName(getResources().getString(R.string.pro_main3_2));
        pro_main3_2.setComment(getResources().getString(R.string.pro_main3_22));
        saveTask(pro_main3_2);

        final LTask pro_main3_3 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main3task.getUid(), null, null, null);
        pro_main3_3.setName(getResources().getString(R.string.pro_main3_3));
        pro_main3_3.setComment(getResources().getString(R.string.pro_main3_33));
        saveTask(pro_main3_3);

        final LTask pro_main3_4 = TaskHelper.createNewTaskWithParams(mApp.getSettings().getUserName(), null, 0, main3task.getUid(), null, null, null);
        pro_main3_4.setName(getResources().getString(R.string.pro_main3_4));
        pro_main3_4.setComment(getResources().getString(R.string.pro_main3_44));
        saveTask(pro_main3_4);
    }

    private void setParams(LTask task) {
        long begin = setTimeTo(TimeHelper.currentTimeMillisWithoutTimeZone(), true);
        long end = setTimeTo(TimeHelper.currentTimeMillisWithoutTimeZone(), false);

        task.setTermBegin(begin);
        task.setTermBeginCustomer(begin);
        task.setTermEnd(end);
        task.setTermEndCustomer(end);
    }

    private long setTimeTo(long date, boolean startOfDay) {
        mCalendar.setTimeInMillis(date == 0 ? System.currentTimeMillis() : date);
        return TimeHelper.roundCalendar(mCalendar, startOfDay).getTimeInMillis();
    }

    private void saveTask(LTask taskNew) {
        new TaskSaveHelper(false, mApp, taskNew, true, null, null, 0,//
                new ArrayList<TaskFile>(0), new ArrayList<TaskFile>(0), false).run();
    }

    private UUID saveMarker(String name, int order, String hexColorBg) {
        Marker mMarker = new Marker();
        UUID uuid = UUID.randomUUID();
        mMarker.setUsn(0);

        mMarker.setName(name);
        mMarker.setId(uuid);
        mMarker.setCreator(LTSettings.getInstance().getUserName());
        mMarker.setOrder(order);
        mMarker.setBackColor(hexColorBg.toLowerCase());
        mMarker.setTextColor("#000");

        try {
            DbHelper.getInstance(this).getMarkerDao().create(mMarker);
            final UpdateFeatureLinkHelper linkHelper = new UpdateFeatureLinkHelper(this);
            linkHelper.createTotalLink(mMarker);

            getContentResolver().notifyChange(LionMetaData.LTaskContract.CONTENT_URI, null);
        } catch (SQLException e) {
            Utils.toLog(e);
        }

        MarkerCache.getInstance(mApp).updateCache(mMarker);
        return uuid;
    }

    private Category saveCategory(String name, int order, String hexColorBg) {
        Category category = new Category();
        UUID uuid = UUID.randomUUID();
        category.setUsn(0);

        category.setName(name);
        category.setId(uuid);
        category.setCreator(LTSettings.getInstance().getUserName());
        category.setOrder(order);
        category.setColor(hexColorBg);

        try {
            DbHelper.getInstance(this).getCategoryDao().create(category);
        } catch (SQLException e) {
            Utils.toLog(e);
        }
        return category;
    }

    private int getOrder(Context context) {
        int order = 0;

        final List<Project> projects;
        try {
            projects = DbHelper.getInstance(context).getProjectDao().queryForAll();
        } catch (SQLException e) {
            return order;
        }
        Collections.sort(projects);

        for (Project p : projects) {
            if (TextUtils.isEmpty(p.getName()) || !LTSettings.getInstance().getUserName().equals(p.getCreator())) {
                continue;
            } else {
                if (p.getParentId() == null) {
                    if (order < p.getOrder()) {
                        order = p.getOrder();
                    }
                }
            }
        }
        return order;
    }

    @Override
    public void onBackPressed()
    {

    }
}
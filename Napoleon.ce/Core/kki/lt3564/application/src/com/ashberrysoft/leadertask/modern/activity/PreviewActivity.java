package com.ashberrysoft.leadertask.modern.activity;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.ArraySet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.data_providers.network.OkHttpConnection;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.modern.adapter.PreviewAdapter;
import com.ashberrysoft.leadertask.modern.cache.MarkerCache;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.PreviewFragment;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.PreviewFragment2;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.PreviewFragment3;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.PreviewFragment4;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.PreviewFragment5;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.PreviewFragment6;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.PreviewFragmentPhone;
import com.ashberrysoft.leadertask.modern.helper.FullTasksResetHelper;
import com.ashberrysoft.leadertask.modern.helper.PreCreateActivityParamsHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskLinkReset;
import com.ashberrysoft.leadertask.modern.helper.TaskSaveHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSeriesHelper;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.modern.helper.UpdateFeatureLinkHelper;
import com.ashberrysoft.leadertask.modern.view.NonSwipeableViewPager;
import com.ashberrysoft.leadertask.providers.SyncProvider;
import com.ashberrysoft.leadertask.service.AuthService;
import com.ashberrysoft.leadertask.utils.LTPowerManager;
import com.ashberrysoft.leadertask.utils.Utils;
import com.astuetz.PagerSlidingTabStrip;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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

import static com.ashberrysoft.leadertask.instance_sync.LeaderTaskSyncService.mApp;

public class PreviewActivity extends BaseActivity {
    // VIEW
    public NonSwipeableViewPager mViewPager;
    private TextView mPage1;
    private TextView mPage2;
    private TextView mPage3;
    private TextView mPage4;
    private TextView mPage5;
    private TextView mPage6;
    private TextView mPage7;
    private TextView mPage8;
    private static int mPageCheked = 0;
    public static String mUserName;
    private Button mIntroButtonNext;
    private boolean isAlreadyStartSliding = false;
    private Calendar mCalendar;

    private static boolean ONLY_FIRST = true;

    // ADAPTER
    public PreviewAdapter mAdapter;

    @Override
    public int getContainerId() {
        return 0;
    }

    public static Intent newInstance(Context context, String userName) {
        final Intent intent = new Intent(context, PreviewActivity.class);
        mUserName = userName;
        return intent;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle b) {

        PreCreateActivityParamsHelper.setActivityParams(this);
        super.onCreate(b);
        setContentView(R.layout.activity_preview);
        mViewPager = (NonSwipeableViewPager) findViewById(R.id.view_pager_intro);
        mPage1 = (TextView) findViewById(R.id.text1);
        mPage2 = (TextView) findViewById(R.id.text2);
        mPage3 = (TextView) findViewById(R.id.text3);
        mPage4 = (TextView) findViewById(R.id.text4);
        mPage5 = (TextView) findViewById(R.id.text5);
        mPage6 = (TextView) findViewById(R.id.text6);
        mPage7 = (TextView) findViewById(R.id.text7);
        mPage8 = (TextView) findViewById(R.id.text8);
        mIntroButtonNext = (Button) findViewById(R.id.btn_next);
        mPageCheked = 0;
        mCalendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);

        mAdapter = new PreviewAdapter(this);
        mViewPager.setAdapter(mAdapter);
        if(mPageCheked == 0) {
            mViewPager.setCurrentItem(mPageCheked);
            mPage1.setTextColor(getResources().getColor(R.color.intro_page_opened_color));
            setViewHeight(mPage1, true);
        }
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs_intro);

        tabs.setViewPager(mViewPager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPageCheked = position;
                resetCheckedPage();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        resetCheckedPage();

        // TODO: 31.05.2018 СКРЫТЬ КРУЖКИ
        LinearLayout dots = (LinearLayout) findViewById(R.id.linearLayout3);

        if (ONLY_FIRST) {
            dots.setVisibility(View.GONE);
            mIntroButtonNext.setText(getString(R.string.intro_start));
            mIntroButtonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startSliding();
                }
            });
        } else {
            mIntroButtonNext.setText(getResources().getString(R.string.intro_forward));
            dots.setVisibility(View.VISIBLE);

            mIntroButtonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mPageCheked==7) {
                        startSliding();
                    }
                    mViewPager.setCurrentItem(mPageCheked <=8 ?  mPageCheked + 1 : 7);

                    if (mPageCheked == 3) {
                        PreviewFragment3 p3 = (PreviewFragment3) mAdapter.getItem(2);

                        if (p3.mEditText2.getText().toString().trim().isEmpty() && p3.mEditText4.getText().toString().trim().isEmpty()) {
                            goToSixSlide();
                        }
                    }
                }
            });
        }


    }

    private void setViewHeight(TextView v, boolean isChecked) {

        if (isChecked) {
            float scaleRatio = getResources().getDisplayMetrics().density;
            float dimenPix = getResources().getDimension(R.dimen.text_size_very_large);
            float dimenOrginal = dimenPix/scaleRatio;
            v.setTextSize(dimenOrginal);
        } else {
            float scaleRatio = getResources().getDisplayMetrics().density;
            float dimenPix = getResources().getDimension(R.dimen.text_size_large);
            float dimenOrginal = dimenPix/scaleRatio;
            v.setTextSize(dimenOrginal);
        }
    }

    public void goToSixSlide() {
        mPage4.setTextColor(getResources().getColor(R.color.intro_page_no_opened_color));
        setViewHeight(mPage4, false);
        mViewPager.setCurrentItem(5);
        mIntroButtonNext.setText(getString(R.string.intro_forward));
    }

    public void goToSixSlide2() {
        mPage3.setTextColor(getResources().getColor(R.color.intro_page_no_opened_color));
        setViewHeight(mPage3, false);
        mViewPager.setCurrentItem(5);
        mIntroButtonNext.setText(getString(R.string.intro_forward));
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.fixActivityForAnalytics(getApp(), "Intro");
    }

    private void resetCheckedPage() {
        PreviewFragment3 p3 = (PreviewFragment3) mAdapter.getItem(2);
        PreviewFragment4 p4 = (PreviewFragment4) mAdapter.getItem(4);
        PreviewFragment5 p5 = (PreviewFragment5) mAdapter.getItem(5);
        PreviewFragment6 p6 = (PreviewFragment6) mAdapter.getItem(6);
        switch (mPageCheked) {
            case 0:
                mPage1.setTextColor(getResources().getColor(R.color.intro_page_opened_color));
                mPage2.setTextColor(getResources().getColor(R.color.intro_page_no_opened_color));
                setViewHeight(mPage1, true);
                setViewHeight(mPage2, false);
                break;
            case 1:
                mIntroButtonNext.setText(getString(R.string.intro_forward));
                mPage1.setTextColor(getResources().getColor(R.color.intro_page_no_opened_color));
                mPage2.setTextColor(getResources().getColor(R.color.intro_page_opened_color));
                mPage3.setTextColor(getResources().getColor(R.color.intro_page_no_opened_color));
                setViewHeight(mPage2, true);
                setViewHeight(mPage1, false);
                setViewHeight(mPage3, false);
                Utils.hideInputNew(p3.mEditText1);
                Utils.hideInputNew(p3.mEditText2);
                Utils.hideInputNew(p3.mEditText3);
                Utils.hideInputNew(p3.mEditText4);

                break;
            case 2:
                mIntroButtonNext.setText(getString(R.string.preview_add));
                mPage2.setTextColor(getResources().getColor(R.color.intro_page_no_opened_color));
                mPage3.setTextColor(getResources().getColor(R.color.intro_page_opened_color));
                mPage4.setTextColor(getResources().getColor(R.color.intro_page_no_opened_color));
                setViewHeight(mPage3, true);
                setViewHeight(mPage2, false);
                setViewHeight(mPage4, false);
                Utils.hideInputNew(p4.mEditText);
                break;
            case 3:
                Utils.hideInputNew(p3.mEditText1);
                Utils.hideInputNew(p3.mEditText2);
                Utils.hideInputNew(p3.mEditText3);
                Utils.hideInputNew(p3.mEditText4);
                p4.setUsers(getEmps(), PreviewActivity.this);

                mIntroButtonNext.setText(getString(R.string.intro_forward));
                mPage3.setTextColor(getResources().getColor(R.color.intro_page_no_opened_color));
                mPage4.setTextColor(getResources().getColor(R.color.intro_page_opened_color));
                mPage5.setTextColor(getResources().getColor(R.color.intro_page_no_opened_color));
                setViewHeight(mPage4, true);
                setViewHeight(mPage3, false);
                setViewHeight(mPage5, false);
                break;

            case 4:

                p4.setUsers(getEmps(), PreviewActivity.this);

                Utils.hideInputNew(p4.mEditText);

                mIntroButtonNext.setText(getString(R.string.intro_forward));
                mPage4.setTextColor(getResources().getColor(R.color.intro_page_no_opened_color));
                mPage5.setTextColor(getResources().getColor(R.color.intro_page_opened_color));
                mPage6.setTextColor(getResources().getColor(R.color.intro_page_no_opened_color));
                setViewHeight(mPage5, true);
                setViewHeight(mPage4, false);
                setViewHeight(mPage6, false);
                break;
            case 5:
                Utils.hideInputNew(p5.mEditText);
                mPage5.setTextColor(getResources().getColor(R.color.intro_page_no_opened_color));
                mPage6.setTextColor(getResources().getColor(R.color.intro_page_opened_color));
                mPage7.setTextColor(getResources().getColor(R.color.intro_page_no_opened_color));
                setViewHeight(mPage6, true);
                setViewHeight(mPage5, false);
                setViewHeight(mPage7, false);
                break;
            case 6:
                Utils.hideInputNew(p4.mEditText);
                PreviewFragment4 p44 = (PreviewFragment4) mAdapter.getItem(4);
                p44.setUsers(getEmps(), PreviewActivity.this);
                mPage6.setTextColor(getResources().getColor(R.color.intro_page_no_opened_color));
                mPage7.setTextColor(getResources().getColor(R.color.intro_page_opened_color));
                mPage8.setTextColor(getResources().getColor(R.color.intro_page_no_opened_color));
                setViewHeight(mPage7, true);
                setViewHeight(mPage6, false);
                setViewHeight(mPage8, false);
                mIntroButtonNext.setText(getResources().getString(R.string.intro_forward));
                Utils.hideInputNew(p5.mEditText);
                break;
            case 7:
                Utils.hideInputNew(p6.mEditText);
                mPage7.setTextColor(getResources().getColor(R.color.intro_page_no_opened_color));
                mPage8.setTextColor(getResources().getColor(R.color.intro_page_opened_color));
                setViewHeight(mPage8, true);
                setViewHeight(mPage7, false);
                mIntroButtonNext.setText(getString(R.string.preview_cansel));
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }

    public String[] getEmps () {
        PreviewFragment3 p3 = (PreviewFragment3) mAdapter.getItem(2);
        String[] allCheckBoxes = new String[]{};
        allCheckBoxes = new String[]{p3.mEditText2.getText().toString().trim(), p3.mEditText4.getText().toString().trim()};
        return allCheckBoxes;
    }

    private void startSliding() {
        if (!isAlreadyStartSliding) {
            if (Utils.isNetworkAvailable(getApp())) {
                isAlreadyStartSliding = true;
                setBlocking(true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Emp mEmp = null;
                            List<Emp> emps = DbHelper.getListEmps(PreviewActivity.this);
                            for (Emp temp : emps) {
                                if (temp.getLogin().equals(LTSettings.getInstance().getUserName())) {
                                    mEmp = temp;
                                    break;
                                }
                            }
                            //

                            PreviewFragment p1 = (PreviewFragment) mAdapter.getItem(0);
                            if (ONLY_FIRST) {
                                saveProject(getString(R.string.preview_slide2_check1), true);
                                saveProject(getString(R.string.preview_slide2_check2),  true);
                                saveProject(getString(R.string.preview_slide2_check3),  true);
                                saveProject(getString(R.string.preview_slide2_check4),  true);
                                saveProject(getString(R.string.preview_slide2_check5),  true);
                            } else {
                                PreviewFragment2 p2 = (PreviewFragment2) mAdapter.getItem(1);
                                PreviewFragment3 p3 = (PreviewFragment3) mAdapter.getItem(2);
                                PreviewFragmentPhone pPhone = (PreviewFragmentPhone) mAdapter.getItem(3);
                                PreviewFragment4 p4 = (PreviewFragment4) mAdapter.getItem(4);
                                PreviewFragment5 p5 = (PreviewFragment5) mAdapter.getItem(5);
                                PreviewFragment6 p6 = (PreviewFragment6) mAdapter.getItem(6);
                                boolean[] p2CheckBoxes = p2.getAllCheckBoxes();

                                String[] p4AssignedTask = p4.getAssignedTask();
                                String p5TodayTaskName = p5.getTodayTaskName();
                                String p6EverydayTaskName = p6.getEverydayTaskName();

                                saveProject(getString(R.string.preview_slide2_check1), p2CheckBoxes[0]);
                                saveProject(getString(R.string.preview_slide2_check2), p2CheckBoxes[1]);
                                saveProject(getString(R.string.preview_slide2_check3), p2CheckBoxes[2]);
                                saveProject(getString(R.string.preview_slide2_check4), p2CheckBoxes[3]);
                                saveProject(getString(R.string.preview_slide2_check5), p2CheckBoxes[4]);

                                int i = 1;
                                if (!p3.mEditText2.getText().toString().trim().isEmpty()) {
                                    i++;
                                    addUser(p3.mEditText1.getText().toString().trim(), p3.mEditText2.getText().toString().trim(), i, p4AssignedTask);
                                }

                                if (!p3.mEditText4.getText().toString().trim().isEmpty()) {
                                    i++;
                                    addUser(p3.mEditText3.getText().toString().trim(), p3.mEditText4.getText().toString().trim(), i, p4AssignedTask);
                                }

                                final LTask todayTask = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, TimeHelper.currentTimeMillisWithoutTimeZone(), null, null, null, null);
                                todayTask.setName(p5TodayTaskName);
                                if (!p5TodayTaskName.isEmpty()) {
                                    saveTask(todayTask);
                                }

                                final LTask everydayTask = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, TimeHelper.currentTimeMillisWithoutTimeZone(), null, null, null, null);
                                TaskSeriesHelper.resetTaskSeries(everydayTask, true);
                                everydayTask.setSeriesType(1);
                                everydayTask.setName(p6EverydayTaskName);
                                if (!p6EverydayTaskName.isEmpty()) {
                                    saveTask(everydayTask);
                                }

                                String newPhone = pPhone.getPhone();

                                if (!newPhone.isEmpty()) {
                                    int zoneInt = TimeZone.getDefault().getRawOffset() / 60 / 60 / 1000;
                                    String zone = "" + (zoneInt > 0 ? "+" + zoneInt : "" + zoneInt);
                                    if (newPhone != null && !newPhone.isEmpty()) {
                                        newPhone = newPhone + " (TimeZone: " + zone + ")";
                                    }

                                    final ContentValues cv = new ContentValues();
                                    cv.put(LeaderTaskProviderMetaData.EmpContract.USN_ENTITY, 0);
                                    cv.put(LeaderTaskProviderMetaData.EmpContract.PHONE, newPhone);
                                    cv.put(LeaderTaskProviderMetaData.EmpContract.USN_FIELD_PHONE, mEmp.getUsnFieldPhone() + 1);

                                    getContentResolver().update(LeaderTaskProviderMetaData.EmpContract.CONTENT_URI, cv, LeaderTaskProviderMetaData.EmpContract.selectionUid(mEmp.getUid()), null);
                                }
                            }


                            final boolean hasCustomLocale = getSettings().getLanguageLocale() != null;
                            final Locale appLocale = hasCustomLocale ? getSettings().getLanguageLocale() : Locale.getDefault();

                            saveProjectUnboarding();

                            if (!p1.mEditText1.getText().toString().trim().isEmpty()) {
                                final ContentValues cv = new ContentValues();
                                cv.put(LeaderTaskProviderMetaData.EmpContract.USN_ENTITY, 0);
                                cv.put(LeaderTaskProviderMetaData.EmpContract.TITLE, p1.mEditText1.getText().toString().trim());
                                cv.put(LeaderTaskProviderMetaData.EmpContract.USN_FIELD_TITLE, mEmp.getUsnFieldTitle() + 1);
                                getContentResolver().update(LeaderTaskProviderMetaData.EmpContract.CONTENT_URI, cv, LeaderTaskProviderMetaData.EmpContract.selectionUid(mEmp.getUid()), null);

                            }

                        } finally {
//                            getSettings().setIsNeedToShowLoadingScreen(false);
                            Utils.startSync(getApp());

                            PreviewActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setBlocking(false);
                                    startActivity(SlidingActivity.newInstance(getApplicationContext()));
                                    PreviewActivity.this.finish();
                                }
                            });
                        }
                    }
                }).start();

            } else {
                Toast.makeText(getApp(), R.string.error_internet_access, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveTask(LTask taskNew) {
        new TaskSaveHelper(false, getApp(), taskNew, true, null, null, 0,//
                new ArrayList<TaskFile>(0), new ArrayList<TaskFile>(0), false).run();
    }

    private void addUser(final String name, final String email, final int order, final String[] p4AssignedTask) {
        if (Utils.isNetworkAvailable(getApp())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Add your data
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);;
                        nameValuePairs.add(new BasicNameValuePair("login", getSettings().getUserProfile().getName()));
                        nameValuePairs.add(new BasicNameValuePair("password", getSettings().getUserProfile().getPassword()));
                        nameValuePairs.add(new BasicNameValuePair("name", name));
                        nameValuePairs.add(new BasicNameValuePair("email", email));
                        nameValuePairs.add(new BasicNameValuePair("userpassword", ""));


                        String message = OkHttpConnection.postWithParams(nameValuePairs, LTSettings.getInstance().getSyncAddEmp());

                        message = message.substring(10, message.length()-2);
                        if (message.equals("0") || message.equals("") || message.isEmpty()) {
                            // збс
                            updateListAfterUserAdd(email, order, getApp());
                            if (p4AssignedTask[1].equals(email)) {
                                final LTask assignedTask = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), p4AssignedTask[1], 0, null, null, null, null);
                                assignedTask.setName(p4AssignedTask[0]);
                                if (!p4AssignedTask[1].equals(getString(R.string.preview_slide4_choose))) {
                                    if (!p4AssignedTask[0].isEmpty()) {
                                        saveTask(assignedTask);
                                    }
                                }
                            }

                            new FullTasksResetHelper(getApp(), false);
                        } else {
                            // ошибка
                        }
                    } catch (Exception e) {

                    }
                }
            }).start();
        } else {
            Toast.makeText(getApp(), R.string.error_internet_access, Toast.LENGTH_SHORT).show();
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
                DbHelper.getInstance(getApp()).getProjectDao().create(project);

            } catch (SQLException e) {
                Utils.toLog(e);
            }

            final UpdateFeatureLinkHelper linkHelper = new UpdateFeatureLinkHelper(getApp());
            linkHelper.createTotalLink(project);
        }
    }

    private long setTimeTo(long date, boolean startOfDay) {
        mCalendar.setTimeInMillis(date == 0 ? System.currentTimeMillis() : date);
        return TimeHelper.roundCalendar(mCalendar, startOfDay).getTimeInMillis();
    }

    private void setParams(LTask task) {
        long begin = setTimeTo(TimeHelper.currentTimeMillisWithoutTimeZone(), true);
        long end = setTimeTo(TimeHelper.currentTimeMillisWithoutTimeZone(), false);

        task.setTermBegin(begin);
        task.setTermBeginCustomer(begin);
        task.setTermEnd(end);
        task.setTermEndCustomer(end);
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

        final LTask taskBlue = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, null, null,null, blue.toString().toUpperCase());
        taskBlue.setName(getResources().getString(R.string.color_task_name1));
        taskBlue.setMarkerOrder(Marker.getMarkerOrderFromLowerUid(getApp(), blue.toString().toUpperCase()));
        saveTask(taskBlue);

        final LTask taskRed = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, null, null, null, red.toString().toUpperCase());
        taskRed.setName(getResources().getString(R.string.color_task_name2));
        taskRed.setMarkerOrder(Marker.getMarkerOrderFromLowerUid(getApp(), red.toString().toUpperCase()));
        saveTask(taskRed);

        final LTask taskCategory = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, null, null, category1.getId().toString().toUpperCase(), null);
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

//        final LTask pro_main0 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, null, null, null, red.toString().toUpperCase());
//        pro_main0.setName(getResources().getString(R.string.pro_main0));
//        pro_main0.setComment(getResources().getString(R.string.pro_main01));
//        setParams(pro_main0);
//        saveTask(pro_main0);

        // 1

        final LTask main1task = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, null, null, null, null);
        main1task.setName(getResources().getString(R.string.pro_main1));
        main1task.setCategories(categoriesForTask);
        setParams(main1task);
        saveTask(main1task);

        final LTask pro_main1_2 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main1task.getUid(), null, null, null);
        pro_main1_2.setName(getResources().getString(R.string.pro_main1_2));
        saveTask(pro_main1_2);

        final LTask pro_main1_3 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main1task.getUid(), null, null, null);
        pro_main1_3.setName(getResources().getString(R.string.pro_main1_3));
        pro_main1_3.setComment(getResources().getString(R.string.pro_main1_31));
        saveTask(pro_main1_3);

        final LTask pro_main1_4 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main1task.getUid(), null, null, null);
        pro_main1_4.setName(getResources().getString(R.string.pro_main1_4));
        saveTask(pro_main1_4);

        final LTask pro_main1_4_1 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, pro_main1_4.getUid(), null, null, null);
        pro_main1_4_1.setName(getResources().getString(R.string.pro_main1_4_1));
        saveTask(pro_main1_4_1);

        final LTask pro_main1_5 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main1task.getUid(), null, null, null);
        pro_main1_5.setName(getResources().getString(R.string.pro_main1_5));
        pro_main1_5.setComment(getResources().getString(R.string.pro_main1_51));
        saveTask(pro_main1_5);

        final LTask pro_main1_6 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main1task.getUid(), null, null, null);
        pro_main1_6.setName(getResources().getString(R.string.pro_main1_6));
        pro_main1_6.setComment(getResources().getString(R.string.pro_main1_61));
        saveTask(pro_main1_6);

        final LTask pro_main1_7 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main1task.getUid(), null, null, null);
        pro_main1_7.setName(getResources().getString(R.string.pro_main1_7));
        pro_main1_7.setComment(getResources().getString(R.string.pro_main1_71));
        saveTask(pro_main1_7);

        final LTask pro_main1_8 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main1task.getUid(), null, null, null);
        pro_main1_8.setName(getResources().getString(R.string.pro_main1_8));
        pro_main1_8.setComment(getResources().getString(R.string.pro_main1_81));
        saveTask(pro_main1_8);

        final LTask pro_main1_9 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main1task.getUid(), null, null, null);
        pro_main1_9.setName(getResources().getString(R.string.pro_main1_9));
        pro_main1_9.setComment(getResources().getString(R.string.pro_main1_91));
        saveTask(pro_main1_9);

        final LTask pro_main1_10 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main1task.getUid(), null, null, null);
        pro_main1_10.setName(getResources().getString(R.string.pro_main1_10));
        pro_main1_10.setComment(getResources().getString(R.string.pro_main1_101));
        saveTask(pro_main1_10);

        final LTask pro_main1_11 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main1task.getUid(), null, null, null);
        pro_main1_11.setName(getResources().getString(R.string.pro_main1_11));
        pro_main1_11.setComment(getResources().getString(R.string.pro_main1_111));
        saveTask(pro_main1_11);

        final LTask pro_main1_12 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main1task.getUid(), null, null, null);
        pro_main1_12.setName(getResources().getString(R.string.pro_main1_12));
        pro_main1_12.setComment(getResources().getString(R.string.pro_main1_121));
        saveTask(pro_main1_12);

        // 2

        final LTask main2task = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, null, null, null, green.toString().toUpperCase());
        main2task.setName(getResources().getString(R.string.pro_main2));
        setParams(main2task);

        saveTask(main2task);

        final LTask pro_main2_1 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main2task.getUid(), null, null, null);
        pro_main2_1.setName(getResources().getString(R.string.pro_main2_1));
        pro_main2_1.setComment(getResources().getString(R.string.pro_main2_11));
        saveTask(pro_main2_1);

        final LTask pro_main2_2 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main2task.getUid(), null, null, null);
        pro_main2_2.setName(getResources().getString(R.string.pro_main2_2));
        pro_main2_2.setComment(getResources().getString(R.string.pro_main2_22));
        saveTask(pro_main2_2);

        final LTask pro_main2_3 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main2task.getUid(), null, null, null);
        pro_main2_3.setName(getResources().getString(R.string.pro_main2_3));
        pro_main2_3.setComment(getResources().getString(R.string.pro_main2_33));
        saveTask(pro_main2_3);

        final LTask pro_main2_4 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main2task.getUid(), null, null, null);
        pro_main2_4.setName(getResources().getString(R.string.pro_main2_4));
        pro_main2_4.setComment(getResources().getString(R.string.pro_main2_44));
        saveTask(pro_main2_4);

        final LTask pro_main2_5 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main2task.getUid(), null, null, null);
        pro_main2_5.setName(getResources().getString(R.string.pro_main2_5));
        pro_main2_5.setComment(getResources().getString(R.string.pro_main2_55));
        saveTask(pro_main2_5);

        final LTask pro_main2_6 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main2task.getUid(), null, null, null);
        pro_main2_6.setName(getResources().getString(R.string.pro_main2_6));
        pro_main2_6.setComment(getResources().getString(R.string.pro_main2_66));
        saveTask(pro_main2_6);

        final LTask pro_main2_7 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main2task.getUid(), null, null, null);
        pro_main2_7.setName(getResources().getString(R.string.pro_main2_7));
        pro_main2_7.setComment(getResources().getString(R.string.pro_main2_77));
        saveTask(pro_main2_7);

        final LTask pro_main2_8 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main2task.getUid(), null, null, null);
        pro_main2_8.setName(getResources().getString(R.string.pro_main2_8));
        pro_main2_8.setComment(getResources().getString(R.string.pro_main2_88));
        saveTask(pro_main2_8);

        // 3

        final LTask main3task = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, null, null, null, yellow.toString().toUpperCase());
        main3task.setName(getResources().getString(R.string.pro_main3));
        setParams(main3task);
        saveTask(main3task);

        final LTask pro_main3_1 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main3task.getUid(), null, null, null);
        pro_main3_1.setName(getResources().getString(R.string.pro_main3_1));
        pro_main3_1.setComment(getResources().getString(R.string.pro_main3_11));
        saveTask(pro_main3_1);

        final LTask pro_main3_2 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main3task.getUid(), null, null, null);
        pro_main3_2.setName(getResources().getString(R.string.pro_main3_2));
        pro_main3_2.setComment(getResources().getString(R.string.pro_main3_22));
        saveTask(pro_main3_2);

        final LTask pro_main3_3 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main3task.getUid(), null, null, null);
        pro_main3_3.setName(getResources().getString(R.string.pro_main3_3));
        pro_main3_3.setComment(getResources().getString(R.string.pro_main3_33));
        saveTask(pro_main3_3);

        final LTask pro_main3_4 = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), null, 0, main3task.getUid(), null, null, null);
        pro_main3_4.setName(getResources().getString(R.string.pro_main3_4));
        pro_main3_4.setComment(getResources().getString(R.string.pro_main3_44));
        saveTask(pro_main3_4);

        //

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

    public static void updateListAfterUserAdd(String userName, int order, Context context) {
        try {
            Employee employee = new Employee();
            employee.setEmail(userName);
            employee.setName(userName);

            Emp emp = new Emp();
            emp.setLogin(employee.getEmail());
            emp.setUid(UUID.randomUUID());
            emp.setTitle(employee.getName());
            emp.setOrder(order);
            context.getContentResolver().insert(LeaderTaskProviderMetaData.EmpContract.CONTENT_URI, emp.getContentValues(null));
            context.getContentResolver().insert(LeaderTaskProviderMetaData.EmployeeContract.CONTENT_URI, employee.getContentValues(null));

            context.getContentResolver().notifyChange(LeaderTaskProviderMetaData.EmpContract.CONTENT_URI, null);
            context.getContentResolver().notifyChange(LeaderTaskProviderMetaData.EmployeeContract.CONTENT_URI, null);
        } finally {

        }
    }
}
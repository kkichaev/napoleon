package com.ashberrysoft.leadertask.modern.fragment;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.FeaturesActivity;
import com.ashberrysoft.leadertask.activities.SettingsActivity;
import com.ashberrysoft.leadertask.adapters.SimpleFeatureListAdapter;
import com.ashberrysoft.leadertask.application.BroadcastAction;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SyncInfoContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CategoryTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.MenuItemContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ProjectTotalLinkContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.data_providers.network.OkHttpConnection;
import com.ashberrysoft.leadertask.day_calendar.DayCalendarActivity;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo.SyncInfoErrorType;
import com.ashberrysoft.leadertask.domains.ordinary.UidToDelete;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.interfaces.ObjectsReceiver;
import com.ashberrysoft.leadertask.modern.activity.AccountActivity;
import com.ashberrysoft.leadertask.modern.activity.SlidingActivity;
import com.ashberrysoft.leadertask.modern.adapter.MenuAdapter;
import com.ashberrysoft.leadertask.modern.cache.EmployeeCache;
import com.ashberrysoft.leadertask.modern.dialog.AddCategoryDialog;
import com.ashberrysoft.leadertask.modern.dialog.AddMarkerDialog;
import com.ashberrysoft.leadertask.modern.dialog.AddProjectDialog;
import com.ashberrysoft.leadertask.modern.domains.link.CategoryTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.ProjectTotalLink;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.domains.menu.BaseMenuItem;
import com.ashberrysoft.leadertask.modern.domains.menu.CalendarMenuItem;
import com.ashberrysoft.leadertask.modern.helper.FullTasksResetHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskDeleteHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskLinkReset;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.modern.helper.UpdateFeatureLinkHelper;
import com.ashberrysoft.leadertask.modern.loader.MenuLoader;
import com.ashberrysoft.leadertask.modern.loader.MenuLoader.OnMenuLoaderResult;
import com.ashberrysoft.leadertask.modern.view.list_item.BaseMenuListItemView.OnMenuListItemListener;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.LTCalendarView;
import com.ashberrysoft.leadertask.views.LTCalendarView.OnCalendarDateSelectedListener;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.v2soft.AndLib.dao.ITreePureNode;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import static com.ashberrysoft.leadertask.R.string.open;
import static com.ashberrysoft.leadertask.application.Config.IN_APP_ID;
import static com.ashberrysoft.leadertask.modern.activity.SlidingActivity.mAmount;
import static com.ashberrysoft.leadertask.modern.activity.SlidingActivity.mCurrency;

public class MenuFragment extends BaseSyncStatusFragment //
        implements ObjectsReceiver, OnMenuListItemListener, OnCalendarDateSelectedListener, OnMenuLoaderResult {

    public static final String ACTION_MENU_ITEM = "com.ashberrysoft.leadertask.fragment.MenuFragment.ACTION_MENU_ITEM";
    public static final String EXTRA_MENU_ITEM = "com.ashberrysoft.leadertask.fragment.MenuFragment.EXTRA_MENU_ITEM";

    public static final String ACTION_UPDATE_ACTION_BAR = "ACTION_UPDATE_ACTION_BAR";

    public static final String CLASS_PATH = MenuFragment.class.getSimpleName();

    private MenuInflater mMenuInflater;

    public static String lastCheckedMenuItemUUID;
    public static BaseMenuItem lastClickedHeader = null;

    // VIEW's
    public LTCalendarView mLTCalendar;

    // VALUE's
    private Calendar mCalendar;
    private MenuLoader mMenuLoader;
    ImageView mImage;
    ImageView mImagePremium;
    TextView userName;
    private ListView mListView;
    private ListView mListViewProjects;
    private ListView mListViewProjectsAvailable;
    private ListView mListViewForMe;
    private ListView mListViewByMe;
    private ListView mListViewCategories;
    private ListView mListViewColors;
    private ListView mListViewEmps;
    private LinearLayout mFooter;
    private RelativeLayout mHeader;
    private RelativeLayout mDayCalendar;
    private View ThisView;

    // ADAPTER
    private MenuAdapter mAdapter;

    private MenuAdapter mAdapterByMe;
    private MenuAdapter mAdapterForMe;
    private MenuAdapter mAdapterProject;
    private MenuAdapter mAdapterProjectAvailable;
    private MenuAdapter mAdapterCategories;
    private MenuAdapter mAdapterColors;
    private MenuAdapter mAdapterEmps;
    private IInAppBillingService mBillingService;
    private ServiceConnection mConnection;
    private Category mCheckedCategory;
    private Marker mCheckedColor;
    private Project mCheckedProject;
    private Context mContext;

    private int mDpFull = 302;
    private int mDp = 118;


    private int mDpFullWeekCount = 294;
    private int mDpWeekCount = 117;


    public static MenuFragment newInstance() {
        return new MenuFragment();
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        mCalendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
        mMenuLoader = MenuLoader.getInstance(getActivity());
        mMenuInflater = getActivity().getMenuInflater();

        mAdapter = new MenuAdapter(getActivity(), this, this);
        //
        mAdapterByMe = new MenuAdapter(getActivity(), this, this);
        mAdapterForMe = new MenuAdapter(getActivity(), this, this);
        mAdapterProject = new MenuAdapter(getActivity(), this, this);
        mAdapterProjectAvailable = new MenuAdapter(getActivity(), this, this);
        mAdapterCategories = new MenuAdapter(getActivity(), this, this);
        mAdapterColors = new MenuAdapter(getActivity(), this, this);
        mAdapterEmps = new MenuAdapter(getActivity(), this, this);
        //
        mAdapter.setData(mMenuLoader.process(this));

        EmployeeCache.getInstance((getActivity())).refreshCache();
        mContext = getContext();

        IntentFilter s_intentFilter = new IntentFilter();
        s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
        s_intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        s_intentFilter.addAction(Intent.ACTION_TIME_CHANGED);

        getActivity().registerReceiver(new DayChangedReceiver(), s_intentFilter);

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBillingService = IInAppBillingService.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBillingService = null;
            }
        };

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        getActivity().bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        ThisView = inflater.inflate(R.layout.fragment_sliding_menu, container, false);
        return ThisView;
    }

    public void resetCalendarView() {
        if (getSettings().isCalendarInNavigator()) {
            mListView.removeHeaderView(mHeader);
            mListView.removeHeaderView(mLTCalendar);
            mListView.removeHeaderView(mDayCalendar);

            mLTCalendar = new LTCalendarView(getActivity(), this, true);

            final BaseMenuItem item = getSettings().getMenuItem();
            boolean other = false;

            switch (item.getMenuItemType()) {
                case TODAY:
                    mCalendar.setTimeInMillis(TimeHelper.currentTimeMillisWithoutTimeZone());
                    break;

                case CALENDAR_DAY:
                    mCalendar.setTimeInMillis(item.getUniqueId());
                    break;

                default:
                    other = true;
                    break;
            }

            if (getSettings().isOneWeekInNav()) {
                mLTCalendar.setDate(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar, mCalendar.get(Calendar.WEEK_OF_YEAR));
            } else {
                mLTCalendar.setDate(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar, -1);
            }
            if (other) {
                mLTCalendar.setControlDate(true, null);
            }

            mHeader = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.drawer_header, null);
            mImage = (ImageView) mHeader.findViewById(R.id.image_view);
            mImagePremium = (ImageView) mHeader.findViewById(R.id.premium);
            if (getSettings().getVerifyKey() == "") {
                mImagePremium.setVisibility(View.GONE);
            } else {
                mImagePremium.setVisibility(View.VISIBLE);
            }
            userName = (TextView) mHeader.findViewById(R.id.text_view);
            try {
                userName.setText(EmployeeCache.getInstance(getActivity()).find(getSettings().getUserName()));
                RoundedBitmapDrawable roundedBitmapDrawable = Utils.getFotoBitmapFromFolder((LTApplication) getActivity().getApplicationContext(), getSettings().getUserName());
                if (roundedBitmapDrawable != null) {
                    mImage.setImageDrawable(roundedBitmapDrawable);
                } else {
                    mImage.setImageResource(R.drawable.emp_simple);
                }
            } catch (Exception e) {
                mImage.setImageResource(R.drawable.emp_simple);
            }


            mHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(AccountActivity.newInstance(getActivity()));
                }
            });


            mListView.addHeaderView(mHeader, null, true); // сначала в хидере юзер с настрокайками

            mListView.addHeaderView(mLTCalendar, null, false); //потом календарь

            mDayCalendar = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.drawer_footer_dayli, null);
            mDayCalendar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(DayCalendarActivity.newInstance(getActivity()));
                }
            });
            mDayCalendar.setVisibility(View.VISIBLE);
            mListView.addHeaderView(mDayCalendar, null, true);

            if (mHeader != null) {
                mHeader.post(new Runnable() {
                    @Override
                    public void run() {
                        updateListHight();
                    }
                });
            }
        }
        if (ThisView != null) {
            Integer dislay;
            if (Utils.isLandOrientation(getApp())) {
                dislay = getResources().getDimensionPixelSize(R.dimen.slidingmenu_minimum_andweek);
            } else {
                dislay = Utils.getDisplayWidth(getApp());
            }
            final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(dislay, LinearLayout.LayoutParams.WRAP_CONTENT);
            MenuFragment.this.getView().setLayoutParams(lp);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(View v, Bundle b) {
        super.onViewCreated(v, b);

        {
            mListView = (ListView) v.findViewById(R.id.slm_list_view);
            mListViewByMe = (ListView) v.findViewById(R.id.slm_list_view1);
            mListViewForMe = (ListView) v.findViewById(R.id.slm_list_view2);
            mListViewProjects = (ListView) v.findViewById(R.id.slm_list_view3);
            mListViewProjectsAvailable = (ListView) v.findViewById(R.id.slm_list_view4);
            mListViewCategories = (ListView) v.findViewById(R.id.slm_list_view5);
            mListViewColors = (ListView) v.findViewById(R.id.slm_list_view6);
            mListViewEmps = (ListView) v.findViewById(R.id.slm_list_view7);
            mFooter = (LinearLayout) v.findViewById(R.id.main);

            if (getSettings().isCalendarInNavigator()) {
                mLTCalendar = new LTCalendarView(getActivity(), this, true);

                final BaseMenuItem item = getSettings().getMenuItem();
                boolean other = false;

                switch (item.getMenuItemType()) {
                    case TODAY:
                        mCalendar.setTimeInMillis(TimeHelper.currentTimeMillisWithoutTimeZone());
                        break;

                    case CALENDAR_DAY:
                        mCalendar.setTimeInMillis(item.getUniqueId());
                        break;

                    default:
                        other = true;
                        break;
                }

                if (getSettings().isOneWeekInNav()) {
                    mLTCalendar.setDate(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar, mCalendar.get(Calendar.WEEK_OF_YEAR));
                } else {
                    mLTCalendar.setDate(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar, -1);
                }
                if (other) {
                    mLTCalendar.setControlDate(true, null);
                }

                mHeader = (RelativeLayout) getLayoutInflater(b).inflate(R.layout.drawer_header, null);
                mImage = (ImageView) mHeader.findViewById(R.id.image_view);
                mImagePremium = (ImageView) mHeader.findViewById(R.id.premium);
                if (getSettings().getVerifyKey() == "") {
                    mImagePremium.setVisibility(View.GONE);
                } else {
                    mImagePremium.setVisibility(View.VISIBLE);
                }
                userName = (TextView) mHeader.findViewById(R.id.text_view);

                try {
                    userName.setText(EmployeeCache.getInstance(getActivity()).find(getSettings().getUserName()));
                    RoundedBitmapDrawable roundedBitmapDrawable = Utils.getFotoBitmapFromFolder((LTApplication) getActivity().getApplicationContext(), getSettings().getUserName());
                    if (roundedBitmapDrawable != null) {
                        mImage.setImageDrawable(roundedBitmapDrawable);
                    } else {
                        mImage.setImageResource(R.drawable.emp_simple);
                    }
                } catch (Exception e) {
                    mImage.setImageResource(R.drawable.emp_simple);
                }

                mHeader.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(AccountActivity.newInstance(getActivity()));
                    }
                });

                mListView.addHeaderView(mHeader, null, true); // сначала в хидере юзер с настрокайками

                mListView.addHeaderView(mLTCalendar, null, false); //потом календарь

                mDayCalendar = (RelativeLayout) getLayoutInflater(b).inflate(R.layout.drawer_footer_dayli, null);
                mDayCalendar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(DayCalendarActivity.newInstance(getActivity()));
                    }
                });
                mDayCalendar.setVisibility(View.VISIBLE);
                mListView.addHeaderView(mDayCalendar, null, true);

            }

            mListView.setAdapter(mAdapter);

            RelativeLayout headerMarket = (RelativeLayout) getLayoutInflater(b).inflate(R.layout.drawer_footer_market, null);
            //RelativeLayout headerDivider = (RelativeLayout) getLayoutInflater(b).inflate(R.layout.drawer_footer_divider, null);
            headerMarket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMarket();
                }
            });



            mListViewByMe.setAdapter(mAdapterByMe);
            mListViewForMe.setAdapter(mAdapterForMe);
            mListViewProjects.setAdapter(mAdapterProject);
            mListViewProjectsAvailable.setAdapter(mAdapterProjectAvailable);
            mListViewCategories.setAdapter(mAdapterCategories);
            mListViewColors.setAdapter(mAdapterColors);
            mListViewEmps.setAdapter(mAdapterEmps);


            int i = 0;
            //mFooter.addView(headerDivider);
            //headerDivider.setVisibility(View.VISIBLE);
            final boolean hasCustomLocale = getSettings().getLanguageLocale() != null;
            final Locale appLocale = hasCustomLocale ?  getSettings().getLanguageLocale() : Locale.getDefault();
            if (appLocale.getLanguage().equals("ru") && LTSettings.getInstance().getVerifyKey() != "") {
                i++;
                headerMarket.setVisibility(View.VISIBLE);
                mFooter.addView(headerMarket);
            }

            RelativeLayout headerSettings = (RelativeLayout) getLayoutInflater(b).inflate(R.layout.drawer_footer_settings, null);
            headerSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(SettingsActivity.newInstance(getActivity()));
                }
            });
            i++;
            headerSettings.setVisibility(View.VISIBLE);
            mFooter.addView(headerSettings);

            if (getSettings().getVerifyKey() == "" || TimeHelper.getInstance().getIntDifferencesDateInDays(getSettings().getVerifyEndDateInLong(), TimeHelper.currentTimeMillisWithoutTimeZone()) < 8) { // если юзер в триале
                RelativeLayout header = (RelativeLayout) getLayoutInflater(b).inflate(R.layout.drawer_footer, null);
                header.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openBuy();
                    }
                });
                header.setVisibility(View.VISIBLE);
                i++;
                mFooter.addView(header);
            }

            //if (getSettings().getVerifyEmailDirector().equals("ivan.abramovsky@gmail.com")) {

            //}
            if (mHeader != null) {
                mHeader.post(new Runnable() {
                    @Override
                    public void run() {
                        updateListHight();
                    }
                });
            }
        }
    }

    private void updateListHight() {
        mAdapter.notifyDataSetChanged();
        //
        MenuAdapter adapter = null;
        ListView listView = null;
        adapter = mAdapter;
        listView = mListView;
        mAdapter.notifyDataSetChanged();

        float density = mContext.getResources().getDisplayMetrics().density;
        float needHeight;
        float dp = (getSettings().isShowWeekCountInCalendar() ? mDpWeekCount :  mDp) * density;
        float dpFull = (getSettings().isShowWeekCountInCalendar() ? mDpFullWeekCount :  mDpFull)  * density;


        if (getSettings().isOneWeekInNav()) {
            needHeight = dp;
        } else {
            needHeight = dpFull;
        }

        if (adapter != null && listView != null && getActivity() != null) {
            mHeader.requestLayout();
            float dimenPix = mContext.getResources().getDimension(R.dimen.univ_ab_height);
            float hh = mContext.getResources().getDimension(R.dimen.header_height);
            float dimenOrginal = ((dimenPix) * adapter.getCount()) + hh + needHeight;

            final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, Math.round(dimenOrginal));
            listView.setLayoutParams(lp);
        }
    }

    private void fixTextView(TextView tv, Activity activity) {
        SpannableString current=(SpannableString)tv.getText();
        URLSpan[] spans = current.getSpans(0, current.length(), URLSpan.class);

        for (URLSpan span : spans) {
            int start=current.getSpanStart(span);
            int end=current.getSpanEnd(span);

            current.removeSpan(span);
            current.setSpan(new DefensiveURLSpan(span.getURL(), activity), start, end, 0);
        }
    }

    private void showMarket() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LinearLayout marketView = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.market_dialog, null);
        Button button1 = (Button) marketView.findViewById(R.id.product1);
        Button button2 = (Button) marketView.findViewById(R.id.product2);
        Button button3 = (Button) marketView.findViewById(R.id.product3);
        Button button4 = (Button) marketView.findViewById(R.id.product4);
        Button button5 = (Button) marketView.findViewById(R.id.product5);
        Button button6 = (Button) marketView.findViewById(R.id.product6);
        Button button7 = (Button) marketView.findViewById(R.id.product7);

        String addins = "";
        boolean isProduct2AlreadyBuy = false;
        boolean isProduct3AlreadyBuy = false;
        boolean isProduct4AlreadyBuy = false;
        boolean isProduct5AlreadyBuy = false;
        boolean isProduct6AlreadyBuy = false;
        boolean isProduct7AlreadyBuy = false;

        try {
            addins = LTSettings.getInstance().getVerifyAddins();
            isProduct2AlreadyBuy = addins.contains("173b8059-d4be-4491-b668-065a5989660f");
            isProduct3AlreadyBuy = addins.contains("2ab7f28c-1ef1-4248-b37c-7cbb9e6be897");
            isProduct4AlreadyBuy = addins.contains("87805a25-c993-4285-b66f-7ebd1573af25");
            isProduct5AlreadyBuy = addins.contains("31eb73b6-db48-4db4-ad03-8406a643f436");
            isProduct6AlreadyBuy = addins.contains("93509be9-7aaa-4014-9a9d-876f284f77d3");
            isProduct7AlreadyBuy = addins.contains("59fb1fd2-ccd5-496b-8078-a27bdffc9549");
        } catch (Exception e) {

        }

        if (!isProduct2AlreadyBuy) {
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.leadertask.com/products/secrets-of-time-management-ebook"));
                    startActivity(browser);
                }
            });
        } else {
            button2.setBackground(getResources().getDrawable(R.drawable.selector_bg_btn_blue2));
            button2.setText(getResources().getString(open));
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = "Большое спасибо за то, что Вы приобрели книгу 'Секреты Управления Временем'. \n" +
                            "Книга доступна для вас по адресу: <A HREF='www.leadertask.com/download/Leadertask_Book.zip'>www.leadertask.com/download/Leadertask_Book.zip </A>.\n" +
                            "Пароль архива: LeaderTask_book_2802_76500012";
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Секреты Управления Временем");
                    LinearLayout view = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.market_product_show, null);
                    TextView textView = (TextView) view.findViewById(R.id.main_text);
                    textView.setText(Html.fromHtml(text));
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                    fixTextView(textView, getActivity());

                    builder.setView(view);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).show();
                }
            });
        }
        //

        if (!isProduct3AlreadyBuy) {
            button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.leadertask.com/products/turbo-time-management-video-course"));
                    startActivity(browser);
                }
            });
        } else {
            button3.setBackground(getResources().getDrawable(R.drawable.selector_bg_btn_blue2));
            button3.setText(getResources().getString(open));
            button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = "Большое спасибо за то, что Вы приобрели Видеокурс 'Турбо Тайм-Менеджмент'. \n" +
                            "Скачайте курс здесь: <A HREF='drive.google.com/file/d/0BzL-jrYMDZy5ZXFyMHlMQUJTTlk/view?usp=sharing'>drive.google.com/file/d/0BzL-jrYMDZy5ZXFyMHlMQUJTTlk/view?usp=sharing </A>.\n" +
                            "Пароль архива: 7fjayb6wpbd7";
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Видеокурс 'Турбо Тайм-Менеджмент'");
                    LinearLayout view = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.market_product_show, null);
                    TextView textView = (TextView) view.findViewById(R.id.main_text);
                    textView.setText(Html.fromHtml(text));
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                    fixTextView(textView, getActivity());

                    builder.setView(view);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).show();
                }
            });
        }
        //

        if (!isProduct4AlreadyBuy) {
            button4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.leadertask.com/products/base-of-useful-advices"));
                    startActivity(browser);
                }
            });
        } else {
            button4.setBackground(getResources().getDrawable(R.drawable.selector_bg_btn_blue2));
            button4.setText(getResources().getString(open));
            button4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = "Большое спасибо за то, что Вы приобрели 'Базу Полезных Советов'. \n" +
                            "Скачайте базу полезных советов здесь: <A HREF='leadertask.com/download/baza-poleznih-sovetov.zip'>leadertask.com/download/baza-poleznih-sovetov.zip</A>.\n" +
                            "Пароль архива: bc346rctb075";
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("База Полезных Советов");
                    LinearLayout view = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.market_product_show, null);
                    TextView textView = (TextView) view.findViewById(R.id.main_text);
                    textView.setText(Html.fromHtml(text));
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                    fixTextView(textView, getActivity());

                    builder.setView(view);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).show();
                }
            });
        }
        //

        if (!isProduct5AlreadyBuy) {
            button5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.leadertask.com/products/three-time-management-systems"));
                    startActivity(browser);
                }
            });
        } else {
            button5.setBackground(getResources().getDrawable(R.drawable.selector_bg_btn_blue2));
            button5.setText(getResources().getString(open));
            button5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = "Большое спасибо за то, что Вы приобрели 'Главные методики Тайм-Менеджмента за 60мин'. \n" +
                            "Скачайте руководство здесь: <A HREF='www.leadertask.com/download/three-tm-systems.zip'>www.leadertask.com/download/three-tm-systems.zip </A>.\n" +
                            "Пароль архива: df8923j7fg4rh943rgf";
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Главные методики Тайм-Менеджмента за 60мин");
                    LinearLayout view = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.market_product_show, null);
                    TextView textView = (TextView) view.findViewById(R.id.main_text);
                    textView.setText(Html.fromHtml(text));
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                    fixTextView(textView, getActivity());;

                    builder.setView(view);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).show();
                }
            });
        }

        if (!isProduct6AlreadyBuy) {
            button6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.leadertask.com/products/employee"));
                    startActivity(browser);
                }
            });
        } else {
            button6.setBackground(getResources().getDrawable(R.drawable.selector_bg_btn_blue2));
            button6.setText(getResources().getString(open));
            button6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = "Большое спасибо за то, что Вы приобрели 'Сотрудники: Найм, мотивация, рост'. \n" +
                            "Скачайте курс здесь: <A HREF='www.leadertask.com/download/kurs_sotrudniki.zip'>www.leadertask.com/download/kurs_sotrudniki.zip </A>.\n" +
                            "Пароль архива: I_NeedBestOfTheBest";
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Сотрудники: Найм, мотивация, рост");
                    LinearLayout view = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.market_product_show, null);
                    TextView textView = (TextView) view.findViewById(R.id.main_text);
                    textView.setText(Html.fromHtml(text));
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                    fixTextView(textView, getActivity());;

                    builder.setView(view);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).show();
                }
            });
        }

        if (!isProduct7AlreadyBuy) {
            button7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.leadertask.com/products/achilles"));
                    startActivity(browser);
                }
            });
        } else {
            button7.setBackground(getResources().getDrawable(R.drawable.selector_bg_btn_blue2));
            button7.setText(getResources().getString(open));
            button7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = "Большое спасибо за то, что Вы приобрели Практикум 'Ахиллесова пята'. \n" +
                            "Скачайте практикум здесь: <A HREF='www.leadertask.com/download/achilles.zip'>www.leadertask.com/download/achilles.zip</A>.\n" +
                            "Пароль архива: Achilles_Will_Rise";
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Сотрудники: Найм, мотивация, рост");
                    LinearLayout view = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.market_product_show, null);
                    TextView textView = (TextView) view.findViewById(R.id.main_text);
                    textView.setText(Html.fromHtml(text));
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                    fixTextView(textView, getActivity());;

                    builder.setView(view);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).show();
                }
            });
        }

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.leadertask.com/products/time-management-video-course"));
                startActivity(browser);
            }
        });

        builder.setView(marketView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        }).show();
    }

    private static class DefensiveURLSpan extends URLSpan {
        String mUrl = "";
        Activity mActivity;
        public DefensiveURLSpan(String url, Activity activity) {
            super(url);
            mUrl = url;
            mActivity = activity;
        }

        @Override
        public void onClick(View widget) {
            try {
                super.onClick(widget);
            }
            catch (ActivityNotFoundException e) {
                // do something useful here
            }
        }

        @Override
        public String getURL()
        {
            String url = super.getURL();
            if (!url.toLowerCase().startsWith("http"))
                url = "http://" + url;

            return url;
        }
    }

    private void openBuy() {
        final View v = LayoutInflater.from(mContext).inflate(R.layout.premium_dialog, null);
        final AlertDialog.Builder ad = new AlertDialog.Builder(mContext,android.R.style.Theme_Black_NoTitleBar_Fullscreen);;
        View button = (View) v.findViewById(R.id.want_to_buy);
        View buttonB = (View) v.findViewById(R.id.back);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSettings().iCanBuyLeadertask) {
                    ArrayList skuList = new ArrayList();
                    skuList.add(IN_APP_ID);
                    Bundle querySkus = new Bundle();
                    querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
                    Bundle skuDetails;
                    try {
                        Bundle ownedItems = mBillingService.getPurchases(3, getApp().getPackageName(), "inapp", null);
                        // Check response
                        int responseCode = ownedItems.getInt("RESPONSE_CODE");
                        if (responseCode != 0) {
                        }
                        // Get the list of purchased items
                        ArrayList<String> purchaseDataList =
                                ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                        for (String purchaseData : purchaseDataList) {
                            JSONObject o = new JSONObject(purchaseData);
                            String purchaseToken = o.optString("token", o.optString("purchaseToken"));
                            // Consume purchaseToken, handling any errors
                            mBillingService.consumePurchase(3,  getApp().getPackageName(), purchaseToken);
                        }
                        skuDetails = mBillingService.getSkuDetails(3,  getApp().getPackageName(), "inapp", querySkus);
                        int response = skuDetails.getInt("RESPONSE_CODE");
                        if (response == 0) {
                            ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                            for (String thisResponse : responseList) {
                                JSONObject object = new JSONObject(thisResponse);
                                String sku = object.getString("productId");
                                String amount = object.getString("price");
                                StringBuilder sb = new StringBuilder();
                                for (int i=0; i < amount.length(); i++) {
                                    char c = amount.charAt(i);
                                    if (Character.isDigit(c)) {
                                        sb.append(c);
                                    } else {
                                        if (c == ",".charAt(0)){
                                            sb.append(c);
                                        } else {
                                            if (c == ".".charAt(0)){
                                                sb.append(",");
                                            }
                                        }
                                    }
                                }
                                mAmount = sb.toString();
                                mCurrency = object.getString("price_currency_code");
                                if (sku.equals(IN_APP_ID)) {
                                    Bundle buyIntentBundle = mBillingService.getBuyIntent(3, getApp().getPackageName(), sku, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
                                    if ((int) buyIntentBundle.get("RESPONSE_CODE") == 0) { // если можно купить
                                        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                                        getActivity().startIntentSenderForResult(pendingIntent.getIntentSender(), 1002, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {

                    }
                } else {
                    Utils.openBrowserToBuy(getSettings(), getActivity());
                }
            }
        });
        ad.setView(v);
        ad.setCancelable(true);
        final AlertDialog dialog = ad.create();

        buttonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    public void resetMyFoto() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        userName.setText(EmployeeCache.getInstance(getActivity()).find(getSettings().getUserName()));
                        RoundedBitmapDrawable roundedBitmapDrawable = Utils.getFotoBitmapFromFolder((LTApplication) getActivity().getApplicationContext(), getSettings().getUserName());
                        if (roundedBitmapDrawable != null) {
                            mImage.setImageDrawable(roundedBitmapDrawable);
                        } else {
                            mImage.setImageResource(R.drawable.emp_simple);
                        }
                    } catch (Exception e) {
                        try {
                            mImage.setImageResource(R.drawable.emp_simple);
                        } catch (Exception ex) {

                        }
                    }
                }
            });
        }
    }

    @Override
    protected Boolean showSlidingMenu() {
        return null;
    }

    @Override
    public void onMenuLongClick(View v, final BaseMenuItem menuItem, int i) {
        final String[] items = {getString(R.string.menu_dell), getString(R.string.menu_properties)};
        final String[] item = { getString(R.string.menu_properties)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);

        switch (menuItem.getMenuItemType()) {
            case EMP:
            case FOR_ME:
            case BY_ME:
                builder.setTitle(menuItem.getName());
                if (LTSettings.getInstance().getUserName().equals(LTSettings.getInstance().getVerifyEmailDirector())) {
                    // может удалить
                    if (menuItem.getUid().equals(getSettings().getUserName())) {
                        // себя не может
                        builder.setItems(item, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(FeaturesActivity.newInstance(getActivity(), FeaturesActivity.FeatureType.EMP, menuItem.getUid()));
                                dialog.cancel();
                            }
                        });
                    } else {
                        // других может удалить
                        builder.setTitle(menuItem.getName());
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    AlertDialog.Builder builderAlert = new AlertDialog.Builder(getActivity());
                                    builderAlert.setMessage(getString(R.string.menu_dell) + " " + menuItem.getName() + " ?");
                                    builderAlert.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //
                                            if (Utils.isNetworkAvailable(getApp())) {
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            // Add your data
                                                            List<NameValuePair> nameValuePairs = new ArrayList<>();
                                                            nameValuePairs.add(new BasicNameValuePair("session", LTSettings.getInstance().getSessionUUID()));
                                                            nameValuePairs.add(new BasicNameValuePair("login", LTSettings.getInstance().getUserProfile().getName()));
                                                            nameValuePairs.add(new BasicNameValuePair("password", LTSettings.getInstance().getUserProfile().getPassword()));
                                                            nameValuePairs.add(new BasicNameValuePair("email", menuItem.getUid()));

                                                            String message = OkHttpConnection.postWithParams(nameValuePairs, LTSettings.getInstance().getSyncDelEmp());
                                                            message = message.substring(10, message.length() - 2);
                                                            if (message.equals("0") || message.equals("") || message.isEmpty()) {
                                                                // збс
                                                                Utils.startSync(getApp());
                                                            } else {
                                                                // ошибка

                                                            }
                                                            Thread.sleep(3000);
                                                            new FullTasksResetHelper(getApp(), false);
                                                        } catch (Exception e) {

                                                        }
                                                    }
                                                }).start();
                                            } else {
                                                Toast.makeText(getApp(), R.string.error_internet_access, Toast.LENGTH_SHORT).show();
                                            }
                                            dialog.cancel();
                                        }
                                    });
                                    builderAlert.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    builderAlert.show();
                                } else {
                                    startActivity(FeaturesActivity.newInstance(getActivity(), FeaturesActivity.FeatureType.EMP, menuItem.getUid()));
                                }
                                dialog.cancel();
                            }
                        });

                    }
                    //
                } else {
                    // если не директор, не может удалить
                    builder.setItems(item, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(FeaturesActivity.newInstance(getActivity(), FeaturesActivity.FeatureType.EMP, menuItem.getUid()));
                            dialog.cancel();
                        }
                    });
                }
                break;

            case PROJECTS:
            case PROJECTS_SHARED:
                builder.setTitle(getActivity().getResources().getString(R.string.default_project) +" '"+menuItem.getName()+"'");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            //
                            AlertDialog.Builder builderAlert = new AlertDialog.Builder(getActivity());
                            builderAlert.setTitle(getActivity().getResources().getString(R.string.d_project_remove_title));
                            builderAlert.setMessage(getActivity().getResources().getString(R.string.d_project_remove_message));
                            builderAlert.setNegativeButton(getActivity().getResources().getString(R.string.txt_just_no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builderAlert.setPositiveButton(getActivity().getString(R.string.txt_just_yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String uid = menuItem.getUid();
                                    try {
                                        mCheckedProject = null;
                                        mCheckedProject = getDbHelper().getProjectByUUId(UUID.fromString(uid));
                                    } catch (Exception e) {
                                        mCheckedProject = null;
                                    } finally {

                                    }
                                    new Thread(mRemoveRunProject).start();
                                    dialog.dismiss();
                                }
                            });
                            builderAlert.show();
                            //
                        } else {
                            startActivity(FeaturesActivity.newInstance(getActivity(), FeaturesActivity.FeatureType.PROJECT, menuItem.getUid()));
                        }
                        dialog.cancel();
                    }
                });
                break;

            case AVAILABLE_PROJECTS:
                builder.setTitle(getActivity().getResources().getString(R.string.default_project) +" '"+menuItem.getName()+"'");
                builder.setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(FeaturesActivity.newInstance(getActivity(), FeaturesActivity.FeatureType.PROJECT, menuItem.getUid()));
                        dialog.cancel();
                    }
                });
                break;

            case CATEGORIES:
                builder.setTitle(getActivity().getResources().getString(R.string.default_category) +" '"+menuItem.getName()+"'");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            //
                            AlertDialog.Builder builderAlert = new AlertDialog.Builder(getActivity());
                            builderAlert.setTitle(getActivity().getResources().getString(R.string.d_category_remove_title));
                            builderAlert.setMessage(getActivity().getResources().getString(R.string.d_category_remove_message));
                            builderAlert.setNegativeButton(getActivity().getResources().getString(R.string.txt_just_no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builderAlert.setPositiveButton(getActivity().getString(R.string.txt_just_yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String uid = menuItem.getUid();
                                    try {
                                        mCheckedCategory = null;
                                        mCheckedCategory = getDbHelper().getCategoryByUUId(UUID.fromString(uid));
                                    } catch (Exception e) {
                                        mCheckedCategory = null;
                                    } finally {

                                    }
                                    new Thread(mRemoveRunCategory).start();
                                    dialog.dismiss();
                                }
                            });
                            builderAlert.show();
                            //
                        } else {
                            startActivity(FeaturesActivity.newInstance(getActivity(), FeaturesActivity.FeatureType.CATEGORY, menuItem.getUid()));
                        }
                        dialog.cancel();
                    }
                });
                break;

            case COLOR:
                builder.setTitle(getActivity().getResources().getString(R.string.default_marker) +" '"+menuItem.getName()+"'");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            //
                            AlertDialog.Builder builderAlert = new AlertDialog.Builder(getActivity());
                            builderAlert.setTitle(getActivity().getResources().getString(R.string.d_color_remove_title));
                            builderAlert.setMessage(getActivity().getResources().getString(R.string.d_color_remove_message));
                            builderAlert.setNegativeButton(getActivity().getResources().getString(R.string.txt_just_no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builderAlert.setPositiveButton(getActivity().getString(R.string.txt_just_yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String uid = menuItem.getUid();
                                    try {
                                        mCheckedColor = null;
                                        mCheckedColor = getDbHelper().getMarkerByUUId(UUID.fromString(uid));
                                    } catch (Exception e) {
                                        mCheckedColor = null;
                                    } finally {

                                    }
                                    new Thread(mRemoveRunColor).start();
                                    dialog.dismiss();
                                }
                            });
                            builderAlert.show();
                            //
                        } else {
                            startActivity(FeaturesActivity.newInstance(getActivity(), FeaturesActivity.FeatureType.MARKER, menuItem.getUid()));
                        }
                        dialog.cancel();
                    }
                });
                break;

            default:
                return;
        }

        Dialog dialog = builder.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        Integer dislay = Utils.getDisplayWidth(getApp()) - getResources().getDimensionPixelSize(R.dimen.slidingmenu_to_small2);

        lp.width = dislay;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    private Runnable mRemoveRunColor = new Runnable() {
        @Override
        public void run() {
            MenuFragment.lastCheckedMenuItemUUID = null;
            getSettings().setMenuItem(MenuItemType.TODAY);
            final Intent intent = new Intent(ACTION_MENU_ITEM);
            intent.putExtra(MenuFragment.EXTRA_MENU_ITEM, getSettings().getMenuItem());
            sendLocalBroadcast(intent);

            removeColor();
            Utils.startSync(getApp());
        }
    };

    private void removeColor() { // TODO: 19.12.2017 переделать
        try {
            getApp().getContentResolver().insert(LeaderTaskProviderMetaData.UidToDeleteContract.CONTENT_URI, UidToDelete.getContentValues(mCheckedColor));

            getDbHelper().getMarkerDao().delete(mCheckedColor);

            getApp().getContentResolver().notifyChange(LionMetaData.LTaskContract.CONTENT_URI, null);

        } catch (SQLException e) {
            Utils.toLog(e);
        }

        UpdateFeatureLinkHelper.deleteTotalLink(getApp(), mCheckedColor);
    }

    private Runnable mRemoveRunCategory = new Runnable() {
        @Override
        public void run() {
            MenuFragment.lastCheckedMenuItemUUID = null;
            getSettings().setMenuItem(MenuItemType.TODAY);
            final Intent intent = new Intent(ACTION_MENU_ITEM);
            intent.putExtra(MenuFragment.EXTRA_MENU_ITEM, getSettings().getMenuItem());
            sendLocalBroadcast(intent);

            removeCategory();
            Utils.startSync(getApp());
        }
    };

    private void removeCategory() {
        try {
            updateOrdersToIndentCategory(mCheckedCategory.getParent());

            getApp().getContentResolver().insert(LeaderTaskProviderMetaData.UidToDeleteContract.CONTENT_URI, UidToDelete.getContentValues(mCheckedCategory));

            getDbHelper().getCategoryDao().delete(mCheckedCategory);


            getApp().getContentResolver().notifyChange(LionMetaData.LTaskContract.CONTENT_URI, null);

        } catch (SQLException e) {
            Utils.toLog(e);
        }

        UpdateFeatureLinkHelper.deleteTotalLink(getApp(), mCheckedCategory);
    }

    private void updateOrdersToIndentCategory(Category oldParent) {
        final List<ITreePureNode> categoriesAll = SimpleFeatureListAdapter.getListCategories(getSettings(), DbHelper.getInstance(getActivity()));
        final List<Category> categories;
        if (oldParent == null) {
            categories = new ArrayList<Category>();
            for (ITreePureNode i : categoriesAll) {
                final Category p = (Category) i;
                if (p.getParentId() == null) {
                    categories.add(p);
                }
            }
        } else {
            categories = oldParent.getSubnodes();
        }

        categories.remove(mCheckedCategory);

        for (int i = 0; i < categories.size(); i++) {
            final Category p = categories.get(i);
            p.setUsnPlusPlus();
            p.setOrder(i + 1);
            p.setUsnOrder(p.getUsnOrder() + 1);
        }

        getDbHelper().updateCategories(categories);
    }

    private Runnable mRemoveRunProject = new Runnable() {
        @Override
        public void run() {
            MenuFragment.lastCheckedMenuItemUUID = null;
            getSettings().setMenuItem(MenuItemType.TODAY);
            final Intent intent = new Intent(ACTION_MENU_ITEM);
            intent.putExtra(MenuFragment.EXTRA_MENU_ITEM, getSettings().getMenuItem());
            sendLocalBroadcast(intent);

            delMyTasks();
            removeProject();
            UpdateFeatureLinkHelper.updateProjectTotalLink(getApp());
            Utils.startSync(getApp());
        }
    };

    private void delMyTasks() {
        if (mCheckedProject != null) {
            // выдрать все свои задачи из проекта и удалить их
            StringBuilder mSb = new StringBuilder();
            ArrayList<LTask> myTasksFromProject = new ArrayList<>();
            Cursor cursorTasks = null;
            try {
                Utils.clearStringBuilder(mSb);
                cursorTasks = getActivity().getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null, LeaderTaskProviderMetaData.SelectionKeeper.equals(mSb, LionMetaData.LTaskContract.UidProject, mCheckedProject.getId().toString().toUpperCase()), null, null);
                if (cursorTasks.getCount() > 0) {
                    while (cursorTasks.moveToNext()) {
                        myTasksFromProject.add(new LTask(cursorTasks));
                    }
                }
            } catch (Exception e) {
            } finally {
                if (cursorTasks != null) {
                    cursorTasks.close();
                }
            }
            //
            for (LTask task : myTasksFromProject) {
                if (task.getEmailCustomer().equals(getSettings().getUserName())) {
                    new TaskDeleteHelper(getApp(), task, true).start();
                }
            }
        }
    }

    private void removeProject() {
        try {
            updateOrdersToIndentProject(mCheckedProject.getParent());

            getApp().getContentResolver().insert(LeaderTaskProviderMetaData.UidToDeleteContract.CONTENT_URI,
                    UidToDelete.getContentValues(mCheckedProject));
            DbHelper.getInstance(getActivity()).getProjectDao().delete(mCheckedProject);

        } catch (SQLException e) {
            Utils.toLog(e);
        }

        UpdateFeatureLinkHelper.deleteTotalLink(getApp(), mCheckedProject);

    }

    private void updateOrdersToIndentProject(Project oldParent) {
        final List<ITreePureNode> projectsAll = SimpleFeatureListAdapter.getListProjects(getSettings(), DbHelper.getInstance(getActivity()));
        final List<Project> projects;
        if (oldParent == null) {
            projects = new ArrayList<Project>();
            for (ITreePureNode i : projectsAll) {
                final Project p = (Project) i;
                if (p.getParentId() == null) {
                    projects.add(p);
                }
            }
        } else {
            projects = oldParent.getSubnodes();
        }

        projects.remove(mCheckedProject);

        for (int i = 0; i < projects.size(); i++) {
            final Project p = projects.get(i);
            p.setUsnPlusPlus();
            p.setOrder(i + 1);
            p.setUsnOrder(p.getUsnOrder() + 1);
        }

        DbHelper.getInstance(getActivity()).updateProjects(projects);
    }

    @Override
    public void onMenuClick(BaseMenuItem menuItem, int i) {
        boolean isAddProject = false;
        boolean isAddCategory = false;
        boolean isAddEmp = false;
        boolean isAddColor = false;
        if (mLTCalendar != null) {
            SlidingActivity activity = (SlidingActivity) getActivity();
            activity.swapToolbarModeToCheck(false);
            switch (menuItem.getMenuItemType()) {
                case TODAY:
                    mCalendar.setTimeInMillis(TimeHelper.currentTimeMillisWithoutTimeZone());
                case CALENDAR_DAY:
                    getSettings().setFilterSelectedDate(mCalendar.getTimeInMillis());
                    if (getSettings().isOneWeekInNav()) {
                        mLTCalendar.setDate(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar, mCalendar.get(Calendar.WEEK_OF_YEAR));
                    } else {
                        mLTCalendar.setDate(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar, -1);
                    }
                    break;
                /*case ADD_PROJECT:
                    isAddProject = true;
                    break;
                case ADD_CATEGORY:
                    isAddCategory = true;
                    break;

                case ADD_EMP:
                    isAddEmp = true;
                    break;

                case ADD_COLOR:
                    isAddColor = true;
                    break;*/
                default:
                    mLTCalendar.setControlDate(false, null);
                    break;
            }
        }

        /*if (isAddProject) {
            AddProjectDialog.newInstance(this).showDialog(this.getFragmentManager());
        } else if (isAddCategory) {
            AddCategoryDialog.newInstance(this).showDialog(this.getFragmentManager());
        } else if (isAddColor) {
            AddMarkerDialog.newInstance(this).showDialog(getFragmentManager());
        } else if (isAddEmp) {
            Utils.iWantToAddUsers(getActivity(), this);
        } else {*/
            getSettings().setMenuItem(menuItem);
            final Intent intent = new Intent(ACTION_MENU_ITEM);
            intent.putExtra(EXTRA_MENU_ITEM, menuItem);
            sendLocalBroadcast(intent);
        //}

        if (menuItem.getMenuItemType() == MenuItemType.TODAY) {
            mAdapter.getView(i, null, mListView).setBackgroundColor(getResources().getColor(R.color.checked_menu_color));
        }
        String lastUID = lastCheckedMenuItemUUID;
        lastCheckedMenuItemUUID = menuItem.getUid();

        try {
            switch (menuItem.getMenuItemType()) {
                case BY_ME:
                    mAdapterByMe.getViewByUUID(lastUID, null, mListViewByMe).setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_item));

                    break;

                case FOR_ME:
                    mAdapterForMe.getViewByUUID(lastUID, null, mListViewForMe).setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_item));
                    break;

                case PROJECTS:
                    mAdapterProject.getViewByUUID(lastUID, null, mListViewProjects).setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_item));
                    break;

                case AVAILABLE_PROJECTS:
                    mAdapterProjectAvailable.getViewByUUID(lastUID, null, mListViewProjectsAvailable).setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_item));
                    break;

                case CATEGORIES:
                    mAdapterCategories.getViewByUUID(lastUID, null, mListViewCategories).setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_item));
                    break;

                case COLOR:
                    mAdapterColors.getViewByUUID(lastUID, null, mListViewColors).setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_item));
                    break;

                case EMP:
                    mAdapterEmps.getViewByUUID(lastUID, null, mListViewEmps).setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_item));
                    break;

                default:
                    break;
            }

            if(mAdapter.getViewByUUID(lastUID, null, mListView) != null) {
                mAdapter.getViewByUUID(lastUID, null, mListView).setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_item));
            }
        } catch (Exception e) {

        } finally {
            mAdapter.notifyDataSetChanged();
            mAdapterByMe.notifyDataSetChanged();
            mAdapterForMe.notifyDataSetChanged();
            mAdapterProject.notifyDataSetChanged();
            mAdapterProjectAvailable.notifyDataSetChanged();
            mAdapterCategories.notifyDataSetChanged();
            mAdapterColors.notifyDataSetChanged();
            mAdapterEmps.notifyDataSetChanged();
        }
    }

    @Override
    public void onFragmentResult(Object object, int requestCode) {
        switch (requestCode) {
            case AddProjectDialog.CODE:
                mMenuLoader.justAddNewProject = true;
                break;
            case AddCategoryDialog.CODE:
                mMenuLoader.justAddNewCategory = true;
                break;
            case AddMarkerDialog.CODE:
                mMenuLoader.justAddNewColor = true;
                break;
            default:
                super.onFragmentResult(object, requestCode);
        }
    }



    @Override
    public void onDropDownClick(BaseMenuItem menuItem, boolean opened) {
        final ContentValues cv = new ContentValues(1);
        cv.put(MenuItemContract.Opened, opened);

        switch (menuItem.getMenuItemType()) {
            case PROJECTS:
            case PROJECTS_SHARED:
            case AVAILABLE_PROJECTS:
                final ProjectTotalLink project = (ProjectTotalLink) menuItem;

                try {
                    final Dao<Project, UUID> dao = getDbHelper().getProjectDao();

                    final UpdateBuilder<Project, UUID> update = dao.updateBuilder();
                    update.setWhere(update.where().eq(Project.FIELD_UID, UUID.fromString(project.getUid())));
                    update.updateColumnValue(Project.FIELD_COLLAPSED, !opened);

                    dao.update(update.prepare());

                } catch (SQLException e) {
                    Utils.toLog(e);
                }

                getApp().getContentResolver().update(ProjectTotalLinkContract.CONTENT_URI, cv, SelectionKeeper.equals(null, ProjectTotalLinkContract._ID, project.getId()), null);
                getApp().getContentResolver().notifyChange(ProjectTotalLinkContract.CONTENT_URI, null);
                break;

            case CATEGORIES:
                final CategoryTotalLink category = (CategoryTotalLink) menuItem;

                try {
                    final Dao<Category, UUID> dao = getDbHelper().getCategoryDao();

                    final UpdateBuilder<Category, UUID> update = dao.updateBuilder();
                    update.setWhere(update.where().eq(Category.FIELD_UID, UUID.fromString(category.getUid())));
                    update.updateColumnValue(Category.FIELD_COLLAPSED, !opened);

                    dao.update(update.prepare());

                } catch (SQLException e) {
                    Utils.toLog(e);
                }

                getApp().getContentResolver().update(CategoryTotalLinkContract.CONTENT_URI, cv, SelectionKeeper.equals(null, CategoryTotalLinkContract._ID, category.getId()), null);
                getApp().getContentResolver().notifyChange(CategoryTotalLinkContract.CONTENT_URI, null);
                break;

            default:
                break;
        }
    }

    @Override
    public void onDropDownClickHeader(BaseMenuItem menuItem, boolean opened) {
        lastClickedHeader = menuItem;
        switch (menuItem.getMenuItemType()) {
            case HEADER_AVAILABLE_PROJECTS:
                getSettings().setDropMenuHeaders(menuItem, opened);
                mMenuLoader.updateItemsListProjectAvailable();
            case HEADER_BY_ME:
                getSettings().setDropMenuHeaders(menuItem, opened);
                mMenuLoader.updateItemsListByMe();
            case HEADER_CATEGORIES:
                getSettings().setDropMenuHeaders(menuItem, opened);
                mMenuLoader.updateItemsListCategories();
            case HEADER_FOR_ME:
                getSettings().setDropMenuHeaders(menuItem, opened);
                mMenuLoader.updateItemsListForMe();
            case HEADER_COLORS:
                getSettings().setDropMenuHeaders(menuItem, opened);
                mMenuLoader.updateItemsListColors();
            case HEADER_EMPS:
                getSettings().setDropMenuHeaders(menuItem, opened);
                mMenuLoader.updateItemsListEmp();
                break;
            case HEADER_PROJECTS:
                getSettings().setDropMenuHeaders(menuItem, opened);
                mMenuLoader.updateItemsListProject();
                break;

            default:
                break;
        }
    }

    @Override
    public void onDateSelected(Date date) {
        if (TimeHelper.getInstance().isToday(date.getTime())) {
            onMenuClick(MenuItemType.TODAY, 0);

        } else {
            mCalendar.setTime(date);
            onMenuClick(new CalendarMenuItem(date.getTime()), 0);
        }
    }

    @Override
    public void fillLostData(List<Calendar> lostData) {
        LTCalendarView.fillCalendarData(getApp(), lostData);
    }

    @Override
    public void restartLoaderCallback() {
        if (mLTCalendar != null) {
            getLoaderManager().restartLoader(mLTCalendar.getLoaderCallbackId(), null, mLTCalendar);
        }
    }

    @Override
    public void onMenuResult(List<BaseMenuItem> list) {
        mAdapter.setData(list);
        mAdapter.notifyDataSetChanged();
        if (mHeader != null) {
            mHeader.post(new Runnable() {
                @Override
                public void run() {
                   updateListHight();
                }
            });
        }

        final ContentValues cv = new ContentValues(1);
        cv.put(SyncInfoContract.MENU_STATUS, SyncInfoErrorType.ENDED.ordinal());

        SyncInfo.updateSynchronizationInfo(getApp(), cv);
    }

    @Override
    public void onMenuResultFor(List<BaseMenuItem> list, MenuItemType item) {
        //
        MenuAdapter adapter = null;
        ListView listView = null;
        switch (item) {

            case BY_ME:
                adapter = mAdapterByMe;
                listView = mListViewByMe;
                mAdapterByMe.setData(list);
                mAdapterByMe.notifyDataSetChanged();
                break;

            case FOR_ME:
                adapter = mAdapterForMe;
                listView = mListViewForMe;
                mAdapterForMe.setData(list);
                mAdapterForMe.notifyDataSetChanged();
                break;

            case PROJECTS:
                adapter = mAdapterProject;
                mAdapterProject.setData(list);
                mAdapterProject.notifyDataSetChanged();
                listView = mListViewProjects;
                break;

            case AVAILABLE_PROJECTS:
                adapter = mAdapterProjectAvailable;
                listView = mListViewProjectsAvailable;
                mAdapterProjectAvailable.setData(list);
                mAdapterProjectAvailable.notifyDataSetChanged();
                break;

            case CATEGORIES:
                adapter = mAdapterCategories;
                listView = mListViewCategories;
                mAdapterCategories.setData(list);
                mAdapterCategories.notifyDataSetChanged();
                break;

            case COLOR:
                adapter = mAdapterColors;
                listView = mListViewColors;
                mAdapterColors.setData(list);
                mAdapterColors.notifyDataSetChanged();
                break;

            case EMP:
                adapter = mAdapterEmps;
                listView = mListViewEmps;
                mAdapterEmps.setData(list);
                mAdapterEmps.notifyDataSetChanged();
                break;

            default:
                break;
        }
        //

        if (adapter != null && listView != null && getActivity() != null) {
            float dimenPix = getActivity().getResources().getDimension(R.dimen.univ_ab_height);
            float dimenOrginal = (dimenPix) * adapter.getCount();

            final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, Math.round(dimenOrginal));
            listView.setLayoutParams(lp);
        }
    }

    @Override
    public void onSyncStatusChange(SyncInfo si) {
        switch (si.getMenuStatus()) {
            case NONE:
                final ContentValues cv = new ContentValues(1);
                cv.put(SyncInfoContract.MENU_STATUS, SyncInfoErrorType.IN_PROGRESS.ordinal());
                SyncInfo.updateSynchronizationInfo(getApp(), cv);
                mMenuLoader.restartLoader();
                break;

            default:
                break;
        }
    }

    @Override
    protected IntentFilter getIntentFilter() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastAction.MIDNIGHT_NOTIFY);
        filter.addAction(TasksFragment.ACTION_CALENDAR_ITEM);
        return filter;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    protected void onBroadcastReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case BroadcastAction.MIDNIGHT_NOTIFY:
                if (mLTCalendar != null) {
                    mLTCalendar.notifyDataSetChanged();
                    sendLocalBroadcast(new Intent(ACTION_UPDATE_ACTION_BAR));
                }
                break;

            case TasksFragment.ACTION_CALENDAR_ITEM:
                if (mLTCalendar != null) {
                    final BaseMenuItem item = (BaseMenuItem) intent.getSerializableExtra(MenuFragment.EXTRA_MENU_ITEM);
                    switch (item.getMenuItemType()) {
                        case TODAY:
                            mCalendar.setTimeInMillis(TimeHelper.currentTimeMillisWithoutTimeZone());
                            break;

                        case CALENDAR_DAY:
                            mCalendar.setTimeInMillis(item.getUniqueId());
                            break;

                        default:
                            return;
                    }

                    getSettings().setFilterSelectedDate(mCalendar.getTimeInMillis());
                    mLTCalendar.setControlDate(true, mCalendar.getTime());
                }
                break;

            default:
                super.onBroadcastReceive(context, intent);
                break;
        }
    }

    private void checkIsDateChanges() {
        mCalendar.setTimeInMillis(TimeHelper.getInstance().lastCheckedTime);

        final int year = mCalendar.get(Calendar.YEAR);
        final int month = mCalendar.get(Calendar.MONTH);
        final int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        mCalendar.setTimeInMillis(TimeHelper.getInstance().currentTimeMillisWithoutTimeZone());

        if (year != mCalendar.get(Calendar.YEAR) || month != mCalendar.get(Calendar.MONTH) || dayOfMonth != mCalendar.get(Calendar.DAY_OF_MONTH)) {
            LTSettings.getInstance().setFilterSelectedDate(TimeHelper.getInstance().currentTimeMillisWithoutTimeZone());
            if (mLTCalendar.getChosenDate() != null) {
                mLTCalendar.setControlDate(true, new Date(mLTCalendar.getChosenDate().getTimeInMillis()));
            }
            else {
                mLTCalendar.setControlDate(true, null);
            }
            if(LTSettings.getInstance().getMenuItem().getMenuItemType() == MenuItemType.TODAY || LTSettings.getInstance().getMenuItem().getMenuItemType() == MenuItemType.CALENDAR_DAY) {
                //проверка на сегодня или не сегодня
                //если выбрано было завтра и наступило сегодня то поменять на сегодня
                //иначе(был выбран другой день)

                if (TimeHelper.getInstance().getNearestDayS(mLTCalendar.getChosenDate().getTimeInMillis(), true) == getApp().getString(R.string.task_today)) {
                    LTSettings.getInstance().setMenuItem(MenuItemType.TODAY);
                } else {
                    LTSettings.getInstance().setMenuItem(new CalendarMenuItem(mLTCalendar.getChosenDate().getTimeInMillis()));
                }
                final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.main_fragment, TasksFragment.newInstance(LTSettings.getInstance().getMenuItem(), null));
                ft.commitAllowingStateLoss();
            }
            mLTCalendar.notifyDataSetChanged();
        }
    }

    public class DayChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_TIME_TICK:
                    try {
                        boolean isTime = false;
                        String yourTime = "0:00";

                        try {
                            String today = (String) android.text.format.DateFormat.format("HH:mm", new java.util.Date());

                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                            Date date1 = sdf.parse(yourTime);
                            Date date2 = sdf.parse(today);
                            isTime = date1.equals(date2);
                        } catch (Exception e) {

                        }
                        if (isTime) {
                            checkIsDateChanges();
                            TimeHelper.getInstance().setLastTimeEveryMinute(TimeHelper.getInstance().currentTimeMillisWithoutTimeZone());
                            new TaskLinkReset(mContext).runAll();
                            MenuLoader.getInstance(getApp()).restartLoader();
                            Utils.updateTodayWidget(mContext);
                        }
                    } finally {

                    }
                    break;

                case Intent.ACTION_TIME_CHANGED:
                case Intent.ACTION_TIMEZONE_CHANGED:
                    checkIsDateChanges();
                    TimeHelper.getInstance().setLastTimeEveryMinute(TimeHelper.getInstance().currentTimeMillisWithoutTimeZone());
                    new TaskLinkReset(mContext).runAll();
                    MenuLoader.getInstance(getApp()).restartLoader();

                    Utils.updateTodayWidget(mContext);
                    break;

                default:
                    break;

            }
        }
    }
}
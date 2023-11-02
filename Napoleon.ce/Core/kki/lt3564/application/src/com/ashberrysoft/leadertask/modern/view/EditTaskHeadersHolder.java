package com.ashberrysoft.leadertask.modern.view;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.CommunicationAdapter;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.data_providers.TaskSeriesCalculator.SeriesType;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.Status;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.modern.activity.EditTaskActivity;
import com.ashberrysoft.leadertask.modern.adapter.EmailsAdapter;
import com.ashberrysoft.leadertask.modern.cache.EmployeeCache;
import com.ashberrysoft.leadertask.modern.cache.MarkerCache;
import com.ashberrysoft.leadertask.modern.domains.link.ProjectTotalLink;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSeriesHelper;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.modern.loader.MenuLoader;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.dao.ITreePureNode;
import static com.ashberrysoft.leadertask.R.id.categories;
import static com.ashberrysoft.leadertask.R.id.comment;
import static com.ashberrysoft.leadertask.R.id.media_actions;
import static java.security.AccessController.getContext;

@SuppressLint("InflateParams")
public class EditTaskHeadersHolder implements View.OnClickListener{

    private static final String CLASS_PATH = EditTaskHeadersHolder.class.getName();
    private static final String EXTRA_TITLE = CLASS_PATH + "EXTRA_TITLE";
    private static final String EXTRA_COMMENT = CLASS_PATH + "EXTRA_COMMENT";

    // BASE
    private final Context mContext;

    private final ImageView mStatus;
    // VIEW's
    private final View mStatusTitle;
    private final View mDividerComment;
    private EditText mEtTitle;
    private TextView mTvTitle;
    private final TextView mStatusText;
    private final TextView mTextParent;
    private final LinearLayout mTaskParent;
    private final LinearLayout mStatusLayout;

    //private View mDividerExec;
    //private View mDividerRepeat;
    //private View mDividerProject;
    //private View mDividerMarker;
    //private View mDividerCategories;
    private final EditText mEtComment;
    private final TextView mTvComment;

    private final ImageTextHolder mPerformer;
    private final ImageTextHolder mTakeOnExec;
    private final ImageTextHolder mReassing;
    private final ImageTextHolder mTerm;
    private final ImageTextHolder mChronometry;
    private ImageTextHolder mTermRepeat;

    private RelativeLayout mMarker;
    private ImageTextHolder mProject;
    private ImageTextHolder mCategories;
    private ImageTextHolder mContacts;
    private ListView mListViewConnection;
    private Project currentProject;
    private ImageTextHolder mEmails;


    // VALUE's
    private final LayoutInflater mInflater;
    private final LTSettings mSettings;
    private final TimeHelper mTimeHelper;

    private final MarkerCache mMarkerCache;
    private final EmployeeCache mEmployeeCache;
    private final MenuLoader mMenuLoader;

    private final int mColorText;
    private final StringBuilder mStringBuilder;
    private boolean isMyTask = true;
    private ArrayList <LTask> mTaskFromParseLink = new ArrayList<>();
    private int mTaskFromParseLinkCount = -1;
    private boolean LinkIsClicked;
    private LTask mTask;
    private LTask mParentTask;
    private boolean mIsNewTask = false;

    private ListView mListView;

    private CommunicationAdapter mAdapter;

    private OnClickListener mOutsideClickListener;
    // LISTENER
    private OnMarkerClick mListener;

    public interface OnMarkerClick {

        void onClickMarker(String uid);

    }

    public EditTaskHeadersHolder(Context context, boolean isNewTask, OnMarkerClick listener) {
        mContext = context;
        mListener = listener;
        LinkIsClicked = false;
        mIsNewTask = isNewTask;
        mInflater = LayoutInflater.from(mContext);
        mSettings = LTSettings.getInstance(mContext);
        mTimeHelper = TimeHelper.getInstance();

        mMarkerCache = MarkerCache.getInstance(mContext);
        mEmployeeCache = EmployeeCache.getInstance(mContext);
        mMenuLoader = MenuLoader.getInstance(mContext);
        mColorText = mContext.getResources().getColor(R.color.properties_text_color);
        //mColorDivider = mContext.getResources().getColor(R.color.divider_gray);
        mStringBuilder = new StringBuilder();

        mStatusTitle = mInflater.inflate(R.layout.view_edit_task_status_title, null);
        mDividerComment = (View) mStatusTitle.findViewById(R.id.divider_comment);
        mStatus = (ImageView) mStatusTitle.findViewById(R.id.status);
        mStatusLayout = (LinearLayout) mStatusTitle.findViewById(R.id.status_layout);
        mStatusText = (TextView) mStatusTitle.findViewById(R.id.tv_status);
        //mEtTitle = (EditText) mStatusTitle.findViewById(R.id.title);
        //mTvTitle = (TextView) mStatusTitle.findViewById(R.id.tv_title);
        mTextParent = (TextView) mStatusTitle.findViewById(R.id.task_parent);
        mTaskParent = (LinearLayout) mStatusTitle.findViewById(R.id.parent);
        mEtComment = (EditText) mStatusTitle.findViewById(comment);
        mTvComment = (TextView) mStatusTitle.findViewById(R.id.tv_comment);
        mMarker = (RelativeLayout) mStatusTitle.findViewById(R.id.marker_layout);
        mEtTitle = (EditText) mStatusTitle.findViewById(R.id.toolbar_edit_name);
        mTvTitle = (TextView) mStatusTitle.findViewById(R.id.toolbar_text_name);
        //mEtTitle.setSingleLine(false);
        //mTvTitle.setSingleLine(false);
        mEtComment.setSingleLine(false);
        mTvComment.setSingleLine(false);


        mTvComment.setKeyListener(null);
        mTvTitle.setKeyListener(null);
        // TODO: 29.09.2017
        mEtComment.setSingleLine(false);
        mTvComment.setSingleLine(false);
        if(mIsNewTask) {
            mEtTitle.setVisibility(View.VISIBLE);
            mTvTitle.setVisibility(View.GONE);
            Utils.showInput(mEtTitle);
            mEtTitle.requestFocus();
        }
        else {
            mEtTitle.setVisibility(View.GONE);
            mTvTitle.setVisibility(View.VISIBLE);
        }
        mEtComment.setVisibility(View.GONE);
        mTvComment.setVisibility(View.VISIBLE);
        //
        //

        mPerformer = new ImageTextHolder(mInflater, mColorText, R.id.performer, R.string.menu_assign);
        mTakeOnExec = new ImageTextHolder(mInflater, mColorText, R.id.take_on_exec, R.string.take_on_exec);
        mReassing = new ImageTextHolder(mInflater, mColorText, R.id.prop_reassing, R.string.menu_re_assign);
        mTerm = new ImageTextHolder(mInflater, mColorText, R.id.term, R.string.task_term);
        mChronometry = new ImageTextHolder(mInflater, mColorText, R.id.chronometry, R.string.chronometry);
        mTermRepeat = new ImageTextHolder(mInflater, mColorText, R.id.term_repeat, R.string.term_repeat);
        mTermRepeat.setImage(R.drawable.status_repeat_default);

        //mEtTitle.setTextColor(mColorText);
        //mTvTitle.setTextColor(mColorText);
        mStatusText.setTextColor(mColorText);
        mEtComment.setTextColor(mColorText);
        mTvComment.setTextColor(mColorText);
    }

    public void addHeaders(ListView lv) {
        boolean isCustomer = mSettings.getUserName().equals(mTask.getEmailCustomer());
        mListView = lv;
        final LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, mContext.getResources().getDimensionPixelSize(R.dimen.divider_small));
        //mDividerRepeat.setLayoutParams(lp);
        //mDividerExec.setLayoutParams(lp);
        mListView.addHeaderView(mStatusTitle, null, false);
        //addDivider(mListView, lp);
        /*mListView.addHeaderView(mComment, null, false);
        addDivider(mListView, lp);*/
        mListView.addHeaderView(mPerformer.get(), null, false);
        //addDivider(mListView, lp);
        if (!mSettings.getUserName().equals(mTask.getEmailCustomer())) {
            if (!mSettings.getUserName().equals(mTask.getEmailCustomer()) && mTask.getEmailPerformer().equals(mTask.getEmailCustomer())) {
                mListView.addHeaderView(mTakeOnExec.get(), null, false);
                //mListView.addHeaderView(mDividerExec, null, false);
                // не моя задача и не я исполнитель
                mTakeOnExec.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle(mContext.getString(R.string.take_on_exec) + "?");
                        builder.setNegativeButton(mContext.getString(R.string.txt_just_no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.setPositiveButton(mContext.getString(R.string.txt_just_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setPerformerAfterExec();
                                deleteExecContainer();
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                });
            } else {
                // если мне задачу поручили - можно перепоручить
                if (mSettings.getUserName().equals(mTask.getEmailPerformer())) {
                    // я исполнитель но не создатель
                    mListView.addHeaderView(mReassing.get(), null, false);
                    mReassing.setOnClickListener(this);
                }
            }
        }
        mListView.addHeaderView(mTerm.get(), null, false);
        //addDivider(mListView, lp);
        if (!mTerm.getText().equals(null) && mTask.getTermBegin() != 0 ) {
            if (mTask.getEmailCustomer().equals(mSettings.getUserName())) {
                mListView.addHeaderView(mTermRepeat.get(), null, false);
                //mListView.addHeaderView(mDividerRepeat, null, false);
            }
        }
        if (mSettings.isShowChrono()) {
            if (mChronometry != null) {
                if (mTask.getEmailCustomer().equals(mSettings.getUserName()) || mTask.getEmailPerformer().equals(mSettings.getUserName())) {
                    mListView.addHeaderView(mChronometry.get(), null, false);
                }
            }
        }

        if (mProject != null && isCustomer) {
            mListView.addHeaderView(mProject.get(), null, false);
            //mDividerProject.setLayoutParams(lp);
            //mListView.addHeaderView(mDividerProject, null, false);
            mProject.setOnClickListener(this);
        } else {
            if (mProject != null && !isCustomer) {
                if (currentProject != null) {
                    if (currentProject.getSharedUsers() != null) {
                        if (currentProject.getSharedUsers().contains(mSettings.getUserName())) {
                            mListView.addHeaderView(mProject.get(), null, false);
                        }
                    } else {
                        if (currentProject.getCreator() != null) {
                            if (currentProject.getCreator().equals(mSettings.getUserName())) {
                                mListView.addHeaderView(mProject.get(), null, false);
                            }
                        }
                    }
                }
            }
        }

        if (mMarker != null && isCustomer) {
            mMarker.setVisibility(View.VISIBLE);
        } else {
            mMarker.setVisibility(View.GONE);
        }
        if (mCategories != null && isCustomer) {
            mListView.addHeaderView(mCategories.get(), null, false);
            //mDividerCategories.setLayoutParams(lp);
            //mListView.addHeaderView(mDividerCategories, null, false);
        }

        if (isCustomer || (mTask.getEmails() != null && mTask.getEmails().length() > 0))
            mListView.addHeaderView(mEmails.get(), null, false);

        if (isCustomer)
            mEmails.setOnClickListener(this);

    }

    public void addHeadersContacts(ListView lv) {
        boolean isCustomer = mSettings.getUserName().equals(mTask.getEmailCustomer());
        final LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, mContext.getResources().getDimensionPixelSize(R.dimen.divider_small));
        //
        if (LTSettings.getInstance().isContactsEnabled()) {
            if (mContacts != null && isCustomer) {
                lv.addHeaderView(mContacts.get(), null, false);
            }
            if (mContacts != null && isCustomer) {
                lv.addHeaderView(mListViewConnection, null, false);
            }
        }
    }

    private void addDivider(ListView lv, LayoutParams lp) {
        final View v = new View(mContext);
        //v.setBackgroundColor(mColorDivider);
        v.setLayoutParams(lp);

        lv.addHeaderView(v, null, false);
    }

    private void addDividerFooter(ListView lv, LayoutParams lp) {
        final View v = new View(mContext);
        //v.setBackgroundColor(mColorDivider);
        v.setLayoutParams(lp);

        lv.addFooterView(v, null, false);
    }

    public void setData(LTask task, Bundle b) {
        mTask = task;
        prepareHeaders();

        if (mSettings.getUserName().equals(task.getEmailCustomer())) {
            isMyTask = true;
            mEtTitle.addTextChangedListener(new TaskTextWatcher(task, true));
            mEtComment.addTextChangedListener(new TaskTextWatcher(task, false));
        } else {
            isMyTask = false;
            mPerformer.disable();
        }

        if (isMyTask) {
            mEtTitle.addTextChangedListener(new TaskTextWatcher(task, true));
            mEtComment.addTextChangedListener(new TaskTextWatcher(task, false));
        } else {
            mEtTitle.setEnabled(false);
            mEtComment.setEnabled(false);

            mTvTitle.setCustomSelectionActionModeCallback(new StyleCallback());
            mTvComment.setCustomSelectionActionModeCallback(new StyleCallback());


            mTvTitle.setEnabled(true);
            mTvTitle.setVisibility(View.VISIBLE);
            mTvTitle.setMovementMethod(LinkMovementMethod.getInstance());
            mTvTitle.setFocusable(true);
            mTvTitle.setFocusableInTouchMode(true);

                    mTvComment.setEnabled(true);
            mTvComment.setVisibility(View.VISIBLE);
            mTvComment.setMovementMethod(LinkMovementMethod.getInstance());
            mTvComment.setFocusable(true);
            mTvTitle.setFocusableInTouchMode(true);
        }

        if (!mIsNewTask) {
            mEtTitle.setVisibility(View.GONE);
            mTvTitle.setVisibility(View.VISIBLE);
        }
        mEtComment.setVisibility(View.GONE);

        if (isMyTask) {
            mTvTitle.setEnabled(true);
            mTvTitle.setMovementMethod(LinkMovementMethod.getInstance());
            mTvTitle.setFocusable(false);
            mTvTitle.setFocusableInTouchMode(false);

            mTvComment.setEnabled(true);
            mTvComment.setVisibility(View.VISIBLE);
            mTvComment.setMovementMethod(LinkMovementMethod.getInstance());
            mTvComment.setFocusable(false);
            mTvTitle.setFocusableInTouchMode(false);
        }

        setParent(task);
        setCommentVisibility(task);
        setStatus(task);
        if (b == null) {
            setTitle(task.getName());
            setComment(task.getComment());

        } else {
            setTitle(b.getString(EXTRA_TITLE));
            setComment(b.getString(EXTRA_COMMENT));
        }
        setPerformer(task);
        setTerm(task);
        setChronometry(mTask);
        setMarker(task.getUidMarker() == null ? task.getUidMarker() : task.getUidMarker().toUpperCase());
        if (mProject != null) {
            setProject(task.getUidProject());
        }
        if (mCategories != null) {
            setCategories(task.getCategories());
        }
        if (mContacts != null) {
            setContacts(task.getContacts());
        }
        if (mEmails != null) {
            setEmails(task.getEmails());
        }

    }

    public void setEmails(String emails) {
        mEmails.setText(null);
        mEmails.setImage(R.drawable.access_prop_empty);

        if (emails != null && mEmails != null){
            if (emails.trim().length() > 0) {
                String[] arr = emails.split(SharedStrings.SPLIT_DOT_DOBLE);
                Set<String> set = new HashSet<>(Arrays.asList(arr));

                StringBuilder sb = new StringBuilder();

                SQLiteDatabase db = DbHelper.getInstance(mContext).getReadableDatabase();
                Cursor c = null;

                try {
                    c = db.query(LeaderTaskProviderMetaData.EmployeeContract.TABLE_NAME, new String[]{LeaderTaskProviderMetaData.EmployeeContract.NAME,
                            LeaderTaskProviderMetaData.EmployeeContract.EMAIL}, null, null, null, null, LeaderTaskProviderMetaData.EmployeeContract.NAME);

                    while (c.moveToNext()) {
                        if (set.contains(c.getString(c.getColumnIndex(LeaderTaskProviderMetaData.EmployeeContract.EMAIL)))) {
                            if (sb.length() > 0)
                                sb.append(", ");

                            sb.append(c.getString(c.getColumnIndex(LeaderTaskProviderMetaData.EmployeeContract.NAME)));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (c != null)
                        c.close();
                }


                if (sb.length() > 0) {
                    mEmails.setText(sb.toString());
                    mEmails.setImage(R.drawable.access_prop_set);
                }else
                    mEmails.setImage(R.drawable.access_prop_empty);
            }
        }
    }

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            int min = mTask.getPlan();
            if (mTask.getStatus() == Status.TASK_IN_WORK.getStatusCode()) {
                // добавить тайм тикер чтобы обновлять
                int wasInWork = mTask.getTime() + (int) ((TimeHelper.getInstance().currentTimeMillisWithoutTimeZone() - mTask.getInWorkTime()) / 1000); // сек

                if (getContext() == null) {

                    timerHandler.removeCallbacks(timerRunnable);
                    return;
                }

                mChronometry.setText(Utils.getTextInWork(mContext, wasInWork, min, mTask.getStatus()));
            }

            timerHandler.postDelayed(this, 1000);
        }
    };

    private boolean showChronometry(boolean completed) {
        //if (!completed) {
            if (mTask.getTime() != 0 || mTask.getPlan() != 0) {
                return true;
            }
        //}
        return false;
    }

    public void setChronometry(LTask task) {
        timerHandler.removeCallbacks(timerRunnable);
        if (task.getPlan() == 0) {
            mChronometry.setImage(R.drawable.ic_timer_big_default);
        } else {
            mChronometry.setImage(R.drawable.ic_timer_big);
        }

        boolean showChronometry = task.getTime() != 0 || task.getPlan() != 0; //showChronometry(false);

        timerHandler.removeCallbacks(timerRunnable);
        int min = task.getPlan();
        String text = "";
        int wasInWork = task.getTime()+(int)((TimeHelper.getInstance().currentTimeMillisWithoutTimeZone() - task.getInWorkTime()) / 1000); // сек

        if (showChronometry) {
            if (task.getStatus() == Status.TASK_IN_WORK.getStatusCode()) {
                if (task.getInWorkTime() == 0) {
                    wasInWork = 0;
                }
                text = Utils.getTextInWork(mContext, wasInWork, min, task.getStatus());
                timerHandler.postDelayed(timerRunnable, 1000);

                //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            } else {
                wasInWork = task.getTime();
                text = Utils.getTextInWork(mContext, wasInWork, min, task.getStatus());
            }
        } else {
            if (task.getStatus() == Status.TASK_IN_WORK.getStatusCode()) {
                text = Utils.getTextInWork(mContext, wasInWork, min, task.getStatus());

                timerHandler.postDelayed(timerRunnable, 1000);
            }
        }

        mTask = task;

        mChronometry.setText(text);

    }


    private void deleteExecContainer() {
        if (mListView != null) {
            if (mTakeOnExec != null) {
                mListView.removeHeaderView(mTakeOnExec.get());
            }
        }
    }

    private void prepareHeaders() {

        mProject = new ImageTextHolder(mInflater, mColorText, R.id.project, R.string.default_project);
        mProject.setImage(R.drawable.project);

        mCategories = new ImageTextHolder(mInflater, mColorText, categories, R.string.task_category);
        mCategories.setImage(R.drawable.category);

        mContacts = new ImageTextHolder(mInflater, mColorText, R.id.contacts, R.string.contacts);
        mContacts.setImage(R.drawable.c_nobody);

        mEmails = new ImageTextHolder(mInflater, mColorText, R.id.access, R.string.task_access);
        mEmails.setImage(R.drawable.access);

        mListViewConnection = (ListView) mInflater.inflate(R.layout.edit_task_communication, null);
        mAdapter = new CommunicationAdapter(mContext, null, false);
    }

    public void setStatus(LTask task) {
        final TaskStatus status = TaskStatus.getTaskStatus(task);

        if (task.getSeriesType() == SeriesType.NONE.ordinal()) {
            mStatus.setImageResource(mSettings.isThemeDark() ? status.getResIdWhite() : status.getResId());

        } else {
            mStatus.setImageResource(mSettings.isThemeDark() ? status.getSeriesWhiteResId() : status.getSeriesResId());
        }

        mStatusText.setText(status.getTextId());
        setChronometry(task);
    }

    public void setCommentVisibility(LTask task) {
        if (!task.getEmailCustomer().equals(mSettings.getUserName())) {
            if (task.getComment() == null) {
                mEtComment.setVisibility(View.GONE);
                mTvComment.setVisibility(View.GONE);
                if (mTaskParent.getVisibility() == View.GONE) {
                    mDividerComment.setVisibility(View.GONE);
                }
            }
        }
    }


    public void setParent(LTask task) {
        if (task.getUIDParent() != null) {
            mParentTask = TaskHelper.getTask(mContext, task.getUIDParent());
            if (mParentTask != null && mTextParent != null) {
                mTextParent.setText(mParentTask.getName());
            }
            mTaskParent.setVisibility(View.VISIBLE);
            mTaskParent.setClickable(true);
            mTaskParent.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    LTSettings.getInstance().setLinkTask(mParentTask);
                    EditTaskActivity EditActivity = (EditTaskActivity) mContext;
                    ((EditTaskActivity) EditActivity).notifyAdapterChange();
                }
            });

        }
        else {
            mTaskParent.setVisibility(View.GONE);
        }
    }

    public void setTitle(String title) {
        if (title != null) {
            if (title.length() < 3000) {
                mTvTitle.setText(SeachLinks(title));
            } else {
                mTvTitle.setText(title);
            }
        } else {
            mTvTitle.setText(title);
        }
        mEtTitle.setText(title);
    }

    public void setComment(String comment) {
        if (comment != null) {
            if (comment.length() < 3000) {
                mTvComment.setText(SeachLinks(comment));
            } else {
                mTvComment.setText(comment);
            }
        } else {
            mTvComment.setText(comment);
        }
        mEtComment.setText(comment);
    }

    public void setPerformer(LTask task) {
        final boolean customer = mSettings.getUserName().equals(task.getEmailCustomer());
        final boolean performer = mSettings.getUserName().equals(task.getEmailPerformer());
        mPerformer.setCustomDrawableToInvisible();
        if (TextUtils.isEmpty(task.getEmailPerformer()) || customer && performer) {
            mPerformer.setText(null);
            mPerformer.setImage(R.drawable.emp_simple);

        } else {
            String findName;
            String customerData = "";
            if (customer && !performer) {
                setPerformerImage(task.getEmailPerformer(), 0);
                findName = task.getEmailPerformer();

            } else if (!customer && performer) {
                setPerformerImage(task.getEmailCustomer(), 1);
                findName = task.getEmailCustomer();

            } else {
                setPerformerImage(task.getEmailCustomer(), 2);
                findName = task.getEmailCustomer();
            }

            if (mSettings.getUserName().equals(task.getEmailPerformer()) || mSettings.getUserName().equals(task.getEmailCustomer())) { // если задача касается меня
                if (task.getEmailPerformer() != null) {
                    customerData = " (" + TimeHelper.getInstance().getDateForSyncOrSimple(new Date(task.getPerformTime()), Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE), mContext, true, false) + ")";
                }
            }

            mPerformer.setText(mEmployeeCache.find(findName).toString()+customerData);
        }
    }

    private void setPerformerImage(String uid , int type) {
        try {
            Emp emp = DbHelper.getInstance(mContext).getEmpByLogin(uid);
            LTApplication mApp = (LTApplication) mContext.getApplicationContext();
            RoundedBitmapDrawable roundedBitmapDrawable = Utils.getFotoBitmapFromFolder(mApp, emp.getLogin());
            if (roundedBitmapDrawable != null) {
                mPerformer.setImageDrawable(roundedBitmapDrawable);
                switch (type) {
                    case 0:
                        mPerformer.setImageCustomDrawable(R.drawable.emp_circle_from_me);
                        break;

                    case 1:
                        mPerformer.setImageCustomDrawable(R.drawable.emp_circle_to_me);
                        break;

                    case 2:
                    default:
                        mPerformer.setImageCustomDrawable(R.drawable.emp_circle_simple);
                        break;
                }
            } else {
                switch (type) {
                    case 0:
                        mPerformer.setImage(R.drawable.emp_from_me);
                        break;

                    case 1:
                        mPerformer.setImage(R.drawable.emp_to_me);
                        break;

                    case 2:
                    default:
                        mPerformer.setImage(R.drawable.emp_simple);
                        break;
                }
            }
        }
        catch (Exception e) {
            mPerformer.setImage(R.drawable.emp_from_me);
        }
    }

    public void setTerm(LTask task) {
        final boolean performer = mSettings.getUserName().equals(task.getEmailPerformer()); // мне поручили
        String term = mTimeHelper.taskTermFormatter(task, !performer, true);
        if (mListView != null && task.getTermBegin() == 0) {
            mListView.removeHeaderView(mTermRepeat.get());
        }
        if(performer == false)
        {
            if (!TextUtils.isEmpty(term))
            {
                setTermDate(term, !performer, task);
            }
            else//срока нет
            {
                term = mTimeHelper.taskTermFormatter(task, false, true);
                if (TextUtils.isEmpty(term))
                {
                    setTermEmpty();
                }
                else
                {
                    setTermDate(term, performer, task);
                }
            }
        }
        else
        {
            term = mTimeHelper.taskTermFormatter(task, true, true);
            if (TextUtils.isEmpty(term)) {
                term = mTimeHelper.taskTermFormatter(task, false, true);
                if (!TextUtils.isEmpty(term))
                {
                    setTermDate(term, !performer, task);
                }
                else
                {
                    setTermEmpty();
                }
            }
            else {
                setTermDate(term, performer, task);
            }
        }
    }

    private void setTermEmpty() {
        mTerm.setImage(R.drawable.term_gray_small);
        mTerm.setText(null);
        TaskSeriesHelper.resetTaskSeries(mTask, false);
    }

    private void setTermDate(String term, boolean orange, LTask task) {
        boolean isCustomer = mSettings.getUserName().equals(task.getEmailCustomer());
        Utils.clearStringBuilder(mStringBuilder);
        mStringBuilder.append(term);

        mTerm.setImage(orange ? R.drawable.term_orange_small_l : R.drawable.term_red_big);
        mTerm.setText(mStringBuilder);

        Utils.clearStringBuilder(mStringBuilder);
        TaskHelper.appendSeriesString(mContext, mStringBuilder, task, false);
        mTermRepeat.setText(mStringBuilder);

        // TODO: 13.04.2016 Тут сделать показ пункта "Повторить"
        if (mListView != null && mListView.findViewById(mTermRepeat.get().getId()) == null && mTask.getEmailCustomer().equals(mSettings.getUserName())) {
            if (mProject != null && mProject.get() != null && isCustomer) {
                mListView.removeHeaderView(mProject.get());
            }

            if (mCategories != null && mCategories.get() != null && isCustomer) {
                mListView.removeHeaderView(mCategories.get());
            }
            if (mContacts != null && mContacts.get() != null && isCustomer) {
                mListView.removeHeaderView(mContacts.get());
            }

            //
            mListView.addHeaderView(mTermRepeat.get());
            //
            if (mProject != null && mProject.get() != null && isCustomer) {
                mListView.addHeaderView(mProject.get());
            }

            if (mCategories != null && mCategories.get() != null && isCustomer) {
                mListView.addHeaderView(mCategories.get());
            }

            if (mContacts != null && mContacts.get() != null && isCustomer) {
                mListView.addHeaderView(mContacts.get());
            }
        }
        //
    }

    public void setProject(String uid) {
        final ProjectTotalLink project = mMenuLoader.findProjectTask(uid);
        mProject.setText(project != null ? project.getName() : null);
        if (project != null) {
            try {
                currentProject = DbHelper.getInstance(mContext).getProjectByUUId(UUID.fromString(uid));
                if(currentProject.getCreator().equals(mSettings.getUserName())) {
                    if (currentProject.getSharedUsers() != null) {
                        mProject.setImage(R.drawable.project_shared);
                    }
                    else {
                        mProject.setImage(R.drawable.project);
                    }
                }
                else {
                    mProject.setImage(R.drawable.project_available);
                }
            }
            catch (Exception e) {
                mProject.setImage(R.drawable.project_simple);
            }
        }
        else {
            mProject.setImage(R.drawable.project_simple);
        }
    }

    public void setProject(Project project) {
        mProject.setText(project != null ? project.getName() : null);
        if (project != null) {
            try {
                //currentProject = DbHelper.getInstance(mContext).getProjectByUUId(UUID.fromString(project.getId().toString()));
                currentProject = DbHelper.getInstance(mContext).getProjectByUUId(UUID.fromString(project.getId().toString()));
                if(currentProject.getCreator().equals(mSettings.getUserName())) {
                    if (currentProject.getSharedUsers() != null) {
                        mProject.setImage(R.drawable.project_shared);
                    }
                    else {
                        mProject.setImage(R.drawable.project);
                    }
                }
                else {
                    mProject.setImage(R.drawable.project_available);
                }
            }
            catch (Exception e) {
                mProject.setImage(R.drawable.project_simple);
            }
        }
        else {
            mProject.setImage(R.drawable.project_simple);
        }
    }

    public void setMarker(String marker) {
        boolean checkChecked = false;
        int miniPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, mContext.getResources().getDisplayMetrics());
        int bigPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, mContext.getResources().getDisplayMetrics());
        Marker checkedMarker = null;
        if (marker != null && !marker.isEmpty()) {
            checkedMarker = mMarkerCache.find(marker.toLowerCase().hashCode());
        }

        final List<Marker> dataAll = mMarkerCache.getAll();
        final List<Marker> markers = new ArrayList<>();
        for (Marker c : dataAll) {
            if (LTSettings.getInstance().getUserName().equals(c.getCreator()) ) {
                markers.add(c);
            }
        }
        //
        RelativeLayout z_layout = (RelativeLayout) mStatusTitle.findViewById(R.id.marker_z_layout);

        RelativeLayout a_layout = (RelativeLayout) mStatusTitle.findViewById(R.id.marker_a_layout);
        RelativeLayout b_layout = (RelativeLayout) mStatusTitle.findViewById(R.id.marker_b_layout);
        RelativeLayout c_layout = (RelativeLayout) mStatusTitle.findViewById(R.id.marker_c_layout);
        RelativeLayout d_layout = (RelativeLayout) mStatusTitle.findViewById(R.id.marker_d_layout);
        RelativeLayout e_layout = (RelativeLayout) mStatusTitle.findViewById(R.id.marker_e_layout);
        RelativeLayout more = (RelativeLayout) mStatusTitle.findViewById(R.id.marker_all);
        more.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickMarker("all");
            }
        });

        z_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //android.util.Log.v("Tedorius","Сбросить маркер на дефолтный");
                mListener.onClickMarker("");
            }
        });

        //
        ImageView imageViewZero = (ImageView) mStatusTitle.findViewById(R.id.marker_zero);
        ImageView imageViewZeroS = (ImageView) mStatusTitle.findViewById(R.id.marker_zero_s);
        ImageView imageViewA = (ImageView) mStatusTitle.findViewById(R.id.marker_a);
        ImageView imageViewAS = (ImageView) mStatusTitle.findViewById(R.id.marker_a_s);
        ImageView imageViewB = (ImageView) mStatusTitle.findViewById(R.id.marker_b);
        ImageView imageViewBS = (ImageView) mStatusTitle.findViewById(R.id.marker_b_s);
        ImageView imageViewC = (ImageView) mStatusTitle.findViewById(R.id.marker_c);
        ImageView imageViewCS = (ImageView) mStatusTitle.findViewById(R.id.marker_c_s);
        ImageView imageViewD = (ImageView) mStatusTitle.findViewById(R.id.marker_d);
        ImageView imageViewDS = (ImageView) mStatusTitle.findViewById(R.id.marker_d_s);
        ImageView imageViewE = (ImageView) mStatusTitle.findViewById(R.id.marker_e);
        ImageView imageViewES = (ImageView) mStatusTitle.findViewById(R.id.marker_e_s);

        ImageView imageViewz = (ImageView) mStatusTitle.findViewById(R.id.check_zero);
        ImageView imageViewa = (ImageView) mStatusTitle.findViewById(R.id.check_a);
        ImageView imageViewb = (ImageView) mStatusTitle.findViewById(R.id.check_b);
        ImageView imageViewc = (ImageView) mStatusTitle.findViewById(R.id.check_c);
        ImageView imageViewd = (ImageView) mStatusTitle.findViewById(R.id.check_d);
        ImageView imageViewe = (ImageView) mStatusTitle.findViewById(R.id.check_e);

        if (checkedMarker == null) {
            checkChecked = true;
            z_layout.setPadding(miniPadding,miniPadding,miniPadding,miniPadding);
            imageViewz.setVisibility(View.VISIBLE);
        } else {
            z_layout.setPadding(bigPadding,bigPadding,bigPadding,bigPadding);
            imageViewz.setVisibility(View.GONE);
        }

        Drawable drawableZero = mContext.getResources().getDrawable(R.drawable.buttonshape);
        Drawable drawableZeroS = mContext.getResources().getDrawable(R.drawable.buttonshape_stroke);

        drawableZero.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP));
        drawableZeroS.setColorFilter(new PorterDuffColorFilter(mContext.getResources().getColor(R.color.properties_text_color), PorterDuff.Mode.SRC_ATOP));

        Drawable drawable1 = mContext.getResources().getDrawable(R.drawable.buttonshape);
        Drawable drawable12 = mContext.getResources().getDrawable(R.drawable.buttonshape_stroke);
        Drawable drawable2 = mContext.getResources().getDrawable(R.drawable.buttonshape);
        Drawable drawable22 = mContext.getResources().getDrawable(R.drawable.buttonshape_stroke);
        Drawable drawable3 = mContext.getResources().getDrawable(R.drawable.buttonshape);
        Drawable drawable32 = mContext.getResources().getDrawable(R.drawable.buttonshape_stroke);
        Drawable drawable4 = mContext.getResources().getDrawable(R.drawable.buttonshape);
        Drawable drawable42 = mContext.getResources().getDrawable(R.drawable.buttonshape_stroke);
        Drawable drawable5 = mContext.getResources().getDrawable(R.drawable.buttonshape);
        Drawable drawable52 = mContext.getResources().getDrawable(R.drawable.buttonshape_stroke);
        //
        imageViewZero.setImageDrawable(drawableZero);
        imageViewZeroS.setImageDrawable(drawableZeroS);
        if (markers != null || !markers.isEmpty()) {
            for (int i=0; i < 5; i++) {
                if (markers.size() > i ) {
                    final Marker tempMarker = markers.get(i);
                    if (tempMarker != null) {
                        boolean isChecked = false;
                        if (checkedMarker != null) {
                            isChecked = checkedMarker.getId().toString().toLowerCase().equals(tempMarker.getId().toString().toLowerCase());
                        }
                        int textColor = mContext.getResources().getColor(R.color.properties_text_color);
                        try {
                            textColor = (tempMarker.getTextColor() == null || Marker.NO_COLOR.equals(tempMarker.getTextColor())) == true ? mContext.getResources().getColor(R.color.properties_text_color) : Color.parseColor(tempMarker.getTextColor());
                        } catch (Exception e) {

                        }
                        String colorStr = tempMarker.getBackColor();
                        if (colorStr != null) {
                            if (!colorStr.contains("#")) {
                                colorStr = "#" + colorStr;
                            }
                        }
                        int bgColor = (colorStr == null || Marker.NO_COLOR.equals(colorStr)) == true ? Color.WHITE : Color.parseColor(colorStr);
                        RelativeLayout container = null;
                        ImageView image = null;
                        ImageView imageS = null;
                        ImageView imageChecked = null;
                        Drawable d1 = null;
                        Drawable d2 = null;
                        switch (i) {
                            case 0:
                                image = imageViewA;
                                imageS = imageViewAS;
                                d1 = drawable1;
                                d2 = drawable12;
                                container = a_layout;
                                imageChecked = imageViewa;
                            break;

                            case 1:
                                image = imageViewB;
                                imageS = imageViewBS;
                                d1 = drawable2;
                                d2 = drawable22;
                                container = b_layout;
                                imageChecked = imageViewb;
                            break;

                            case 2:
                                image = imageViewC;
                                imageS = imageViewCS;
                                d1 = drawable3;
                                d2 = drawable32;
                                container = c_layout;
                                imageChecked = imageViewc;
                            break;

                            case 3:
                                image = imageViewD;
                                imageS = imageViewDS;
                                d1 = drawable4;
                                d2 = drawable42;
                                container = d_layout;
                                imageChecked = imageViewd;
                            break;

                            case 4:
                                image = imageViewE;
                                imageS = imageViewES;
                                d1 = drawable5;
                                d2 = drawable52;
                                container = e_layout;
                                imageChecked = imageViewe;
                            break;

                            default:
                            break;
                        }

                        d1.setColorFilter(new PorterDuffColorFilter(bgColor, PorterDuff.Mode.SRC_ATOP));
                        d2.setColorFilter(new PorterDuffColorFilter(textColor, PorterDuff.Mode.SRC_ATOP));
                        container.setVisibility(View.VISIBLE);
                        image.setImageDrawable(d1);
                        imageS.setImageDrawable(d2);

                        container.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //android.util.Log.v("Tedorius","Клик по маркеру : "+tempMarker.getId().toString());
                                mListener.onClickMarker(tempMarker.getId().toString().toUpperCase());
                            }
                        });

                        if (isChecked) {
                            checkChecked = true;
                            container.setPadding(miniPadding,miniPadding,miniPadding,miniPadding);
                            imageChecked.setVisibility(View.VISIBLE);
                        } else {
                            container.setPadding(bigPadding,bigPadding,bigPadding,bigPadding);
                            imageChecked.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
        if (markers != null || !markers.isEmpty()) {
            if (!checkChecked) {
                for (final Marker m : markers) {
                    if (m != null) {
                        if (checkedMarker != null) {
                            if (checkedMarker.getId().toString().toLowerCase().equals(m.getId().toString().toLowerCase())) {
                                try {
                                    int textColor = (m.getTextColor() == null || Marker.NO_COLOR.equals(m.getTextColor())) == true ? mContext.getResources().getColor(R.color.properties_text_color) : Color.parseColor(m.getTextColor());
                                    String colorStr = m.getBackColor();
                                    if (colorStr != null) {
                                        if (!colorStr.contains("#")) {
                                            colorStr = "#" + colorStr;
                                        }
                                    }
                                    int bgColor = (colorStr == null || Marker.NO_COLOR.equals(colorStr)) == true ? Color.WHITE : Color.parseColor(colorStr);

                                    ImageView image = imageViewE;
                                    ImageView imageS = imageViewES;
                                    Drawable d1 = drawable5;
                                    Drawable d2 = drawable52;
                                    RelativeLayout container = e_layout;
                                    ImageView imageChecked = imageViewe;

                                    d1.setColorFilter(new PorterDuffColorFilter(bgColor, PorterDuff.Mode.SRC_ATOP));
                                    d2.setColorFilter(new PorterDuffColorFilter(textColor, PorterDuff.Mode.SRC_ATOP));
                                    container.setVisibility(View.VISIBLE);
                                    image.setImageDrawable(d1);
                                    imageS.setImageDrawable(d2);

                                    container.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //android.util.Log.v("Tedorius","Клик по маркеру : "+tempMarker.getId().toString());
                                            mListener.onClickMarker(m.getId().toString().toUpperCase());
                                        }
                                    });

                                    checkChecked = true;
                                    container.setPadding(miniPadding, miniPadding, miniPadding, miniPadding);
                                    imageChecked.setVisibility(View.VISIBLE);
                                } catch (Exception e) {

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void setCategories(String categories) {
        final String[] uids = TaskHelper.getCategoriesFromString(categories);
        if (uids.length <= 0) {
            mCategories.setText(null);
            return;
        }

        Utils.clearStringBuilder(mStringBuilder);
        //
        boolean first = true;
        try {
            List<ITreePureNode> allCategories =  getListAllCategories(DbHelper.getInstance(mContext));
            for (ITreePureNode categoryITree : allCategories) {
                for (int i = 0; i < uids.length; i++) {
                    if(uids[i].toLowerCase().equals(((Category)categoryITree).getId().toString().toLowerCase())) {
                        if (first) {
                            first = false;

                        } else {
                            mStringBuilder.append(SharedStrings.COMMA_C);
                            mStringBuilder.append(SharedStrings.SPACE_C);
                        }

                        mStringBuilder.append(((Category)categoryITree).getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mCategories.setText(mStringBuilder);
    }

    public List<ITreePureNode> getListAllCategories(DbHelper dbHelper) {
        final List<ITreePureNode> data = new ArrayList<ITreePureNode>();

        final List<Category> categories;
        try {
            categories = dbHelper.getCategoryDao().queryForAll();
        } catch (SQLException e) {
            return data;
        }
        Collections.sort(categories);

        for (Category c : categories) {
            if (c.getParentId() == null && LTSettings.getInstance().getUserName().equals(c.getCreator()) ) {
                data.add(c);
                processListSubCategories(data, c, categories);
            }
        }

        return data;
    }

    private void processListSubCategories(List<ITreePureNode> data, Category parent, List<Category> categories) {
        for (Category c : categories) {
            if (parent.getId().equals(c.getParentId())) {
                data.add(c);
                parent.addChild(c);
                processListSubCategories(data, c, categories);
            }
        }
    }

    public void setContacts(String contacts) {
        final String[] uids = TaskHelper.getContactsFromString(contacts);
        List <String> allComm = new ArrayList<String>(0);
        if (uids.length <= 0) {
            mContacts.setText(null);
            mAdapter.setData(allComm);
            resetAdapter();
            return;
        }

        Utils.clearStringBuilder(mStringBuilder);
        //
        boolean first = true;
        try {
            List<Contact> allContacts = DbHelper.getInstance(mContext).getAllContactsForView();
            for (Contact contact : allContacts) {
                for (int i = 0; i < uids.length; i++) {
                    if (uids[i] != null && contact.getId() != null) {
                        if (uids[i].toLowerCase().equals(contact.getId().toString())) {
                            List<String> list = getCommunications(contact);
                            if (list != null) {
                                allComm.addAll(list);
                            }
                            if (first) {
                                first = false;

                            } else {
                                mStringBuilder.append(SharedStrings.COMMA_C);
                                mStringBuilder.append(SharedStrings.SPACE_C);
                            }

                            mStringBuilder.append(contact.getTitle());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mContacts.setText(mStringBuilder);
        mAdapter.setData(allComm);
        resetAdapter();
    }

    private void resetAdapter() {
        mListViewConnection.setAdapter(mAdapter);

        final int h = mContext.getResources().getDimensionPixelSize(R.dimen.univ_ab_height);
        final AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, h*mAdapter.getCount()+mContext.getResources().getDimensionPixelSize(R.dimen.divider_small)*mAdapter.getCount());
        mListViewConnection.setLayoutParams(lp);
    }

    private List<String> getCommunications(Contact contact) {
        String base64String = contact.getCommunications();
        if (base64String != null) {
            boolean emailFounded = false;
            boolean phoneFounded = false;
            List<String> allComm = new ArrayList<String>();
            int lastIndexFounded = 0;
            byte[] data = Base64.decode(base64String, Base64.DEFAULT);
            String utf8Text = "";
            try {
                utf8Text = new String(data, "UTF-8");
                for (int i = 0; i < utf8Text.length(); i++) {
                    int indexMain = utf8Text.indexOf("\n", i);
                    if (indexMain != -1) {
                        String subString = utf8Text.substring(lastIndexFounded == 0 ? lastIndexFounded : lastIndexFounded + 1, indexMain);
                        if (subString.substring(0, 4).contains("eml") && !emailFounded) {
                            emailFounded = true;
                            allComm.add(subString.substring(0, subString.lastIndexOf("\t") + 1) + contact.getTitle());
                        } else if (subString.substring(0, 4).contains("tel") && !phoneFounded) {
                            phoneFounded = true;
                            allComm.add(subString.substring(0, subString.lastIndexOf("\t") + 1) + contact.getTitle());
                        }
                        lastIndexFounded = indexMain;
                        i = indexMain;
                    } else {
                        String subString = utf8Text.substring(i, utf8Text.length());
                        if (subString.substring(0, 4).contains("eml") && !emailFounded) {
                            allComm.add(subString.substring(0, subString.lastIndexOf("\t") + 1) + contact.getTitle());
                        } else if (subString.substring(0, 4).contains("tel") && !phoneFounded) {
                            allComm.add(subString.substring(0, subString.lastIndexOf("\t") + 1) + contact.getTitle());
                            ;
                        }
                        break;
                    }

                }
            } catch (UnsupportedEncodingException e) {
            }

            return allComm;
        }
        return null;
    }

    private void setPerformerAfterExec() {
        mTask.setEmailPerformer(mSettings.getUserName().toLowerCase());
        mTask.setUsnFieldEmailPerformer(mTask.getUsnFieldEmailPerformer() + 1);

        if(!mTask.getEmailPerformer().equals(mSettings.getUserName()) &&
                mTask.getEmailCustomer().equals(mSettings.getUserName())) {
            if(mTask.getPerformerReaded() != false) {
                mTask.setPerformerReaded(false);
                mTask.setUsnFieldPerformerReaded(mTask.getUsnFieldPerformerReaded()+1);
            }
        }
        else {
            if(mTask.getPerformerReaded() != true) {
                mTask.setPerformerReaded(true);
                mTask.setUsnFieldPerformerReaded(mTask.getUsnFieldPerformerReaded()+1);
            }
        }

        mTask.setPerformTime(System.currentTimeMillis());
        mTask.setUsnFieldPerformtime(mTask.getUsnFieldPerformtime() + 1);

        setPerformer(mTask);
    }

    public void setViewsOnClickListener(OnClickListener listener) {
        mOutsideClickListener = listener;
        mStatusLayout.setOnClickListener(this);

        if(isMyTask) {
            mTvTitle.setOnClickListener(this);
            mTvComment.setOnClickListener(this);
            //mTvTitle.setOnLongClickListener(this);
            //mTvComment.setOnLongClickListener(this);
        }
        else {
            //mTvTitle.setLongClickable(false);
            //mTvComment.setLongClickable(false);
        }

        mPerformer.setOnClickListener(this);
        mTerm.setOnClickListener(this);
        mChronometry.setOnClickListener(this);

        if (mCategories != null ) {
            mCategories.setOnClickListener(this);
        }

        if (mContacts != null ) {
            mContacts.setOnClickListener(this);
        }

        if (mTermRepeat != null ) {
            mTermRepeat.setOnClickListener(this);
        }
    }

    public void onSavedInstanceState(Bundle b) {
        b.putSerializable(EXTRA_TITLE, mEtTitle.getText().toString());
        b.putSerializable(EXTRA_COMMENT, mEtComment.getText().toString());
    }

    public String getComment() {
        final String comment = mEtComment.getText().toString();
        return TextUtils.isEmpty(comment) ? null : comment.trim();
    }

    public boolean checkTaskName() {
        String taskName = mEtTitle.getText().toString().trim();
        if (taskName.length() != 0) {

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_text_name:
                if(LinkIsClicked==false) {
                    stopTextView(mTvTitle, mEtTitle);
                    startTextView(mTvComment, mEtComment);
                }
                else {
                    LinkIsClicked = false;
                }
                break;

            case R.id.tv_comment:
                if(LinkIsClicked==false) {
                    stopTextView(mTvComment, mEtComment);
                    startTextView(mTvTitle, mEtTitle);
                }
                else {
                    LinkIsClicked = false;
                }
                break;
            default:
                startTextView(mTvTitle, mEtTitle);
                startTextView(mTvComment, mEtComment);
                Utils.hideInput(v);
                mOutsideClickListener.onClick(v);
        }
    }

    private void stopTextView(TextView tv, EditText et) {
        if (tv.getVisibility() != View.GONE) {
            tv.setVisibility(View.GONE);
            et.setVisibility(View.VISIBLE);
            et.requestFocus();
            et.setSelection(et.getText().length());
            Utils.showInput(et);
        }

    }

    private void startTextView(TextView tv, EditText et) {
        if (et.getVisibility() != View.GONE) {
            tv.setText(SeachLinks(et.getText().toString()));
            et.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
        }
    }

    public void outsideClick(View v) {
        Utils.hideInput(v);
        mOutsideClickListener.onClick(v);
    }

    private static final class ImageTextHolder {

        private final View mLayout;
        private final ImageView mImageView;
        private final ImageView mImageViewCustom;
        private final TextView mTextView;

        public ImageTextHolder(LayoutInflater inflater, int textColor, int id, int hintId) {
            mLayout = inflater.inflate(R.layout.view_edit_task_image_text, null);
            mLayout.setId(id);

            mImageView = (ImageView) mLayout.findViewById(R.id.image_view);
            mImageViewCustom = (ImageView) mLayout.findViewById(R.id.iv_img_custom);
            mTextView = (TextView) mLayout.findViewById(R.id.text_view);

            mTextView.setTextColor(textColor);
            mTextView.setHint(hintId);
        }

        public void setOnClickListener(OnClickListener listener) {
            mLayout.setOnClickListener(listener);
        }

        public void setText(CharSequence text) {
            mTextView.setText(text);
        }

        public String getText() {
            return mTextView.getText().toString();
        }

        public void setImage(int imgId) {
            mImageView.setImageResource(imgId);
        }

        public void setImageDrawable(Drawable drawable) {
            mImageView.setImageDrawable(drawable);
        }

        public void setImageCustomDrawable(int imgId) {
            mImageViewCustom.setVisibility(View.VISIBLE);
            mImageViewCustom.setImageResource(imgId);
        }

        public void setCustomDrawableToInvisible() {
            mImageViewCustom.setVisibility(View.INVISIBLE);
        }

        public View get() {
            return mLayout;
        }

        public void disable() {
            mLayout.setEnabled(false);
        }

        public void setToInvisible() {
            mLayout.setVisibility(View.GONE);
        }

        public void setToVisible() {
            mLayout.setVisibility(View.VISIBLE);
        }
    }

    private static final class TaskTextWatcher implements TextWatcher {

        // BASE
        private final LTask mTask;
        /** false is comment */
        private final boolean mName;

        // VALUE
        private boolean mUsnChanged;

        public TaskTextWatcher(LTask task, boolean name) {
            mTask = task;
            mName = name;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            final String string = String.valueOf(s);

            if (mName) {
                if (!string.equals(mTask.getName())) {
                    mTask.setName(string.trim());
                    if (!mUsnChanged) {
                        mUsnChanged = true;
                        mTask.setUsnFieldName(mTask.getUsnFieldName() + 1);
                    }
                }

            } else if (!string.equals(mTask.getComment())) {
                if (string.length() == 0 && mTask.getComment() == null) {
                    return;
                }

                mTask.setComment(string.trim());
                if (!mUsnChanged) {
                    mUsnChanged = true;
                    mTask.setUsnFieldComment(mTask.getUsnFieldComment() + 1);
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //поиск ссылки lt://planning?{.....}
    private SpannableStringBuilder SeachLinks(String full_text) //!
    {
        int LinkSize = 52;

        if(full_text != null) {
            full_text.trim();
            SpannableStringBuilder sb = new SpannableStringBuilder(full_text);

            for(int i=0;i<=sb.length()-LinkSize;i++)
            {
                int IndexBeginning = sb.toString().lastIndexOf("lt://planning?{", i);
                if (IndexBeginning != -1 && sb.length()-IndexBeginning >= LinkSize && IndexBeginning >= i) // последнее изменение -IndexBeginning
                {
                    final String link_in_text = sb.toString().substring(IndexBeginning, IndexBeginning + LinkSize);
                    if(link_in_text.indexOf("}", LinkSize-1) != -1)
                    {
                        i+=51;
                        String link = link_in_text.substring(15, 51);
                        LTask linkTask = TaskHelper.getTask(mContext, link);

                        mTaskFromParseLink.add(linkTask);
                        mTaskFromParseLinkCount++;

                        sb.setSpan(setClickableSpanClick(), IndexBeginning, IndexBeginning+52, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    }
                }
            }
            return sb;

        }
        return new SpannableStringBuilder("");
    }

    private ClickableSpan setClickableSpanClick() {
        final int index;
        index = mTaskFromParseLinkCount;
        ClickableSpan cs = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                LinkIsClicked = true;
                if (mTaskFromParseLink.get(index) != null) {
                    LTSettings.getInstance().setLinkTask(mTaskFromParseLink.get(index));
                    EditTaskActivity EditActivity = (EditTaskActivity) mContext;
                    ((EditTaskActivity) EditActivity).notifyAdapterChange();
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.task_not_found), Toast.LENGTH_SHORT).show();
                }
            }
        };
        return cs;
    }

    class StyleCallback implements android.view.ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
            menu.removeItem(android.R.id.cut);
            menu.removeItem(android.R.id.paste);
            menu.removeItem(android.R.id.selectAll);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
            /*TextView textView = mTvTitle.isFocused() ? mTvTitle : mTvComment;
            int start = textView.getSelectionStart();
            int end = textView.getSelectionEnd();

            switch(item.getItemId()) {

                case android.R.id.copy:
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", ""+textView.getText().subSequence(start,end));
                    clipboard.setPrimaryClip(clip);
                return false;
            }*/
            return false;
        }

        @Override
        public void onDestroyActionMode(android.view.ActionMode mode) {

        }
    }
}
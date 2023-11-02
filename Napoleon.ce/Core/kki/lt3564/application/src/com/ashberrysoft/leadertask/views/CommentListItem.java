package com.ashberrysoft.leadertask.views;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.CommentsAdapter;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.modern.activity.EditTaskActivity;
import com.ashberrysoft.leadertask.modern.cache.EmployeeCache;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.fragment.EditTaskFragment;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.ui.views.IDataView;

import static com.ashberrysoft.leadertask.modern.fragment.EditTaskFragment.isShowAll;

public class CommentListItem extends LinearLayout implements IDataView<TaskMessage> {

    private static final SimpleDateFormat SDF_12_HOUR = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private static final SimpleDateFormat SDF_24_HOUR = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private static final SimpleDateFormat SDF_WEEK = new SimpleDateFormat("EEEE", Locale.getDefault());

    private TextView mTextDayWeek, mTextDate, mTextTime;
    private TextView mTextUser, mTextComments;
    private RelativeLayout mLayoutDate;
    private LTApplication mApp;
    private View  mLineStart;
    private View  mLineStart2;
    private ImageView  imageView;
    private ImageView  imageViewCustom;
    private FrameLayout imageContainer;

    private TaskMessage mData;

    private ArrayList<LTask> mTaskFromParseLink = new ArrayList<>();
    private int mTaskFromParseLinkCount = -1;

    private final EmployeeCache mEmployeeCache;

    public CommentListItem(Context context) {
        this(context, null);
    }

    public CommentListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.list_item_comments, this);

        mTextDayWeek = (TextView) findViewById(R.id.day_of_week);
        mTextDate = (TextView) findViewById(R.id.text_date);
        mTextTime = (TextView) findViewById(R.id.time);
        mTextUser = (TextView) findViewById(R.id.user);
        mTextComments = (TextView) findViewById(R.id.text_comments);
        mLayoutDate = (RelativeLayout) findViewById(R.id.date);
        mApp = (LTApplication) context.getApplicationContext();
        mLineStart = (View) findViewById(R.id.line_comments_start);
        mLineStart2 = (View) findViewById(R.id.line_comments_start2);
        imageView = (ImageView) findViewById(R.id.image_view);
        imageViewCustom = (ImageView) findViewById(R.id.iv_img_custom);
        imageContainer = (FrameLayout) findViewById(R.id.frameLayout);
        mEmployeeCache = EmployeeCache.getInstance(getContext());
    }

    @Override
    public void setData(TaskMessage data) {
        mData = data;
        TaskMessage previousMessage = null;
        if (!isShowAll) {
            for (int i = EditTaskActivity.mTaskMessages.size()-2; i < EditTaskActivity.mTaskMessages.size(); i++) {
                if (data.equals(EditTaskActivity.mTaskMessages.get(i))) {
                    if (i == EditTaskActivity.mTaskMessages.size()-2) {
                        previousMessage = null;
                    } else {
                        previousMessage = EditTaskActivity.mTaskMessages.get(i - 1);
                    }
                    break;
                }
            }
        } else {
            for (int i = 1; i < EditTaskActivity.mTaskMessages.size(); i++) {
                if (data.equals(EditTaskActivity.mTaskMessages.get(i))) {
                    previousMessage = EditTaskActivity.mTaskMessages.get(i - 1);
                    break;
                }
            }
        }



        mLayoutDate.setVisibility(View.VISIBLE);
        mTextUser.setVisibility(View.VISIBLE);
        mLineStart.setVisibility(View.GONE);
        mLineStart2.setVisibility(View.GONE);

        final String message = data.getMessage();
        if (message != null && message.length() > 0) {
            mTextComments.setText(SeachLinks(message));
        } else {
            mTextComments.setText(message);
        }

        mTextComments.setClickable(true);
        mTextComments.setLongClickable(true);
        mTextComments.setFocusable(true);
        mTextComments.setTextIsSelectable(true);
        mTextComments.setCursorVisible(false);

        mTextComments.setCustomSelectionActionModeCallback(new StyleCallback());

        mTextTime.setText(DateFormat.is24HourFormat(getContext()) ? SDF_24_HOUR.format(data.getDateCreate()) : SDF_12_HOUR.format(data.getDateCreate()));

        String week = SDF_WEEK.format(data.getDateCreate());
        String date = SDF_DATE.format(data.getDateCreate());
        Date dToday = new Date();
        String dateToday = SDF_DATE.format(dToday);

        Calendar cl = Calendar.getInstance();
        cl.add(Calendar.DATE, -1);

        Date dYesterday = new Date(cl.getTimeInMillis());
        String dateYesterday = SDF_DATE.format(dYesterday);
        if (date.equals(dateToday)) {
            week = getContext().getString(R.string.task_today);
        } else {
            if (date.equals(dateYesterday)) {
                week = getContext().getString(R.string.task_yesterday);
            }
        }

        if (previousMessage == null) {
            mTextDate.setText(date);
            mTextDayWeek.setText(new String(week.toString().substring(0, 1).toUpperCase(Locale.getDefault()) + week.toString().substring(1) + " - "));
            mLineStart.setVisibility(View.VISIBLE);
            mLineStart2.setVisibility(View.VISIBLE);
        } else {
            String dateLast = SDF_DATE.format(previousMessage.getDateCreate());
            String customerLast = previousMessage.getCreator();
            boolean dayNotChanges = dateLast.equals(date);
            if (dayNotChanges) {
                mLayoutDate.setVisibility(View.GONE);
                mLineStart.setVisibility(View.GONE);
                mLineStart2.setVisibility(View.GONE);
            } else {
                mLayoutDate.setVisibility(View.VISIBLE);
                mLineStart.setVisibility(View.VISIBLE);
                mLineStart2.setVisibility(View.VISIBLE);
                mTextDate.setText(date);
                mTextDayWeek.setText(new String(week.toString().substring(0, 1).toUpperCase(Locale.getDefault()) + week.toString().substring(1) + " - "));
            }

            if (customerLast.equals(data.getCreator()) && dayNotChanges) {
                mTextUser.setVisibility(View.GONE);
            }
        }
        //
        if (mApp.getSettings().getUserName().equals(data.getCreator())) {
            imageContainer.setVisibility(View.GONE);
            mTextUser.setVisibility(View.GONE);
            mTextComments.setBackgroundResource(R.drawable.selector_my_comment);
        } else {
            imageContainer.setVisibility(View.VISIBLE);
            mTextUser.setText(mEmployeeCache.find(data.getCreator()));
            mTextComments.setBackgroundResource(R.drawable.selector_comment);
            LTApplication mApp = (LTApplication) getContext().getApplicationContext();
            RoundedBitmapDrawable roundedBitmapDrawable = Utils.getFotoBitmapFromFolder(mApp, data.getCreator());
            if (roundedBitmapDrawable != null) {
                imageView.setImageDrawable(roundedBitmapDrawable);
                imageViewCustom.setVisibility(View.VISIBLE);
                imageViewCustom.setImageResource(R.drawable.emp_circle_simple);
            } else {
                imageView.setImageResource(R.drawable.emp_simple);
                imageViewCustom.setVisibility(View.GONE);
            }
        }
        //
    }

    @Override
    public TaskMessage getData() {
        return mData;
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
                        LTask linkTask = TaskHelper.getTask(getContext(), link);

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
                if (mTaskFromParseLink.get(index) != null) {
                    LTSettings.getInstance().setLinkTask(mTaskFromParseLink.get(index));
                    EditTaskActivity EditActivity = (EditTaskActivity) getContext();
                    (EditActivity).notifyAdapterChange();
                } else {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.task_not_found), Toast.LENGTH_SHORT).show();
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
            int start = mTextComments.getSelectionStart();
            int end = mTextComments.getSelectionEnd();

            switch(item.getItemId()) {

                case android.R.id.copy:
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext().getSystemService(getContext().CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", ""+mTextComments.getText().subSequence(start,end));
                    clipboard.setPrimaryClip(clip);
                    return false;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(android.view.ActionMode mode) {

        }
    }
}

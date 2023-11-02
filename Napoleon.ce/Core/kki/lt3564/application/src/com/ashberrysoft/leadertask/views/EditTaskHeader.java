package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.HomeActivity;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.ui.views.IDataView;

/**
 * Отображение полей в редактировании задачи
 * 
 * @author Tetiana Diachuk (diacht@gmail.com)
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * @deprecated
 */
public class EditTaskHeader extends LinearLayout implements TextWatcher, IDataView<Task> {
    private EditText mTaskTitle, mTaskComment;
    private ImageView mStatusIcon, mTermIcon, mPerformerIcon;
    private TextView mStatusTitle, mTermTitle, mPerformerTitle;
    private Task mTask;
    private LTSettings mSettings;

    public interface HeaderCallBack {
        void pressBack();
    }

    public EditTaskHeader(Context context) {
        super(context);
        inflate(context, R.layout.edit_task_header, this);

        // get Settings instance
        final LTApplication app = (LTApplication) context.getApplicationContext();
        mSettings = app.getSettings();
        mTaskTitle = (EditText) findViewById(R.id.task_new);
        mTaskComment = (EditText) findViewById(R.id.task_comment);
        mStatusIcon = (ImageView) findViewById(R.id.status_icon);
        mTermIcon = (ImageView) findViewById(R.id.term_icon);
        mPerformerIcon = (ImageView) findViewById(R.id.performer_icon);
        mStatusTitle = (TextView) findViewById(R.id.status_title);
        mTermTitle = (TextView) findViewById(R.id.term_title);
        mPerformerTitle = (TextView) findViewById(R.id.performer_title);

        mTaskTitle.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (getContext() != null) {
                        ((HomeActivity) getContext()).getWindow().setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    }
                }
                return false;
            }
        });

        mTaskComment.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (getContext() != null) {
                        ((HomeActivity) getContext()).getWindow().setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    }
                }
                return false;
            }
        });
    }

    public void setEditTextEnabled(boolean enabled) {
        mTaskTitle.setEnabled(enabled);
        mTaskComment.setEnabled(enabled);
    }

    public void setColor() {
        if (mSettings.isThemeDark()) {
            ((TextView) findViewById(R.id.task_connection)).setTextColor(Color.WHITE);
        } else {
            ((TextView) findViewById(R.id.task_connection)).setTextColor(Color.BLACK);
        }
    }

    // set task title
    public void setTaskTitle(String title) {
        mTaskTitle.setText(title);
    }

    // get task title
    public String getTaskTitle() {
        return mTask.getName();
    }

    // set task comment
    public void setTaskComment(String comment) {
        mTaskComment.setText(comment);
    }

    // get task comment
    public String getTaskComment() {
        return mTask.getComment();
    }

    public static int getStatusDrawable(Task task) {
        int status = task.getStatus();
        int resourceId = 0;
        if (status == 0)
            resourceId = R.drawable.status0;
        else if (status == 1)
            resourceId = R.drawable.status1;
        else if (status == 3)
            resourceId = R.drawable.status3;
        else if (status == 4)
            resourceId = R.drawable.status4;
        else if (status == 5)
            resourceId = R.drawable.status5;
        else if (status == 6)
            resourceId = R.drawable.status6;
        else if (status == 7)
            resourceId = R.drawable.status7;
        else if (status == 8)
            resourceId = R.drawable.status8;
        else if (status == 9)
            resourceId = R.drawable.status9;
        return resourceId;
    }

    public static int getStatusText(Task task) {
        int status = task.getStatus();
        int statusText = 0;
        if (status == 0)
            statusText = R.string.task_not_begin;
        else if (status == 1)
            statusText = R.string.task_completed;
        else if (status == 3)
            statusText = R.string.note;
        else if (status == 4)
            statusText = R.string.task_in_work;
        else if (status == 5)
            statusText = R.string.task_ready;
        else if (status == 6)
            statusText = R.string.task_paused;
        else if (status == 7)
            statusText = R.string.task_cancelled;
        else if (status == 8)
            statusText = R.string.task_rejected;
        else if (status == 9)
            statusText = R.string.task_refine;
        return statusText;
    }

    public void setTerm() {
        // Кнопка выбора срока – по нажатию
        // открывается диалог выбора срока. На
        // кнопке серая иконка и надпись «Срок»,
        // если срок не установлен. Если установлен
        // то срок иконки и надпись «Срок: » текст
        // срока
        String text = Utils.taskTermFormatter(getContext(), mTask, true);
        if (text.equals("")) {
            mTermTitle.setText(getContext().getResources().getString(R.string.task_term));
            mTermIcon.setImageResource(R.drawable.term_gray_big);
        } else {
            // Due to https://redmine.ashberrysoft.com/issues/1895
            // if(mTask.getCustomer().equals(currentUser))
            mTermIcon.setImageResource(R.drawable.term_orange_big);
            // else if(mTask.getPerformer().equals(currentUser))
            // mTermIcon.setImageResource(R.drawable.time_me);
            // else
            // mTermIcon.setImageResource(R.drawable.time);
            mTermTitle.setText(getContext().getResources().getString(R.string.term) + text);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mTask.setName(mTaskTitle.getText().toString());
        mTask.setComment(mTaskComment.getText().toString());
    }

    @Override
    public void setData(Task data) {
        mTask = data;
        mTaskTitle.setText(mTask.getName());
        mTaskComment.setText(mTask.getComment());
        mTaskTitle.addTextChangedListener(this);
        mTaskComment.addTextChangedListener(this);
        mStatusIcon.setImageResource(getStatusDrawable(mTask));
        mStatusTitle.setText(getStatusText(mTask));
        setTerm();
        // Кнопка поручить – по нажатию
        // открывается диалог поручить. Если
        // у задачи заказчик не текущий
        // пользователь, то кнопка не
        // отображается.
        if (mSettings.getUserName().equals(mTask.getCustomer())) {
            findViewById(R.id.btn_set_performer).setVisibility(View.VISIBLE);
            // set new performer that received from InstructDialog
            final Resources resources = getContext().getResources();
            if (mTask.getPerformer().equals("") || mSettings.getUserName().equals(mTask.getPerformer())) {
                // Если исполнитель у задачи текущий
                // пользователь, то отображается
                // серая иконка и надпись
                // «Поручить». Если не текущий, то
                // зеленая иконка и надпись
                // «Исполнитель: емайл»
                mPerformerTitle.setText(R.string.task_assign);
                mPerformerIcon.setImageDrawable(resources.getDrawable(R.drawable.commit_edit_task));
            } else {
                mPerformerTitle.setText(resources.getString(R.string.task_performer) + " " + mTask.getPerformer());
                mPerformerIcon.setImageDrawable(resources.getDrawable(R.drawable.commit_i_edit_task));
            }
        } else {
            findViewById(R.id.btn_set_performer).setVisibility(View.GONE);
        }
    }

    @Override
    public Task getData() {
        // mTask.setName(mTaskTitle.getText().toString());
        // mTask.setComment(mTaskComment.getText().toString());
        return mTask;
    }

}

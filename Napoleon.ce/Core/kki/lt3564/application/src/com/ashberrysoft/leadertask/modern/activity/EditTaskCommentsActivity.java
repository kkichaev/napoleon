package com.ashberrysoft.leadertask.modern.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.Status;
import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.fragment.EditTaskConversationFragment;
import com.ashberrysoft.leadertask.utils.Utils;

import java.util.List;

import static com.ashberrysoft.leadertask.domains.ordinary.Status.TASK_REJECTED;

public class EditTaskCommentsActivity extends BaseActivity {

    public static final String EXTRA_TASK = "EXTRA_TASK";
    public static final String EXTRA_TASK_MESSAGES = "EXTRA_TASK_MESSAGES";
    public static final String EXTRA_TASK_MESSAGES_SAVE_FROM = "EXTRA_TASK_MESSAGES_SAVE_FROM";
    public static final int COMMENTS_CONTAINER = R.id.main_container_comments;
    EditTaskConversationFragment mCommentsFragment;
    private static EditTaskActivity mActivity;


    // VIEW
    private RelativeLayout mViewPager;

    // VALUE's
    private static LTask mTask;
    private static List<TaskMessage> mTaskMessages;

    private Toolbar mToolbar;
    private TextView mTitleToolbar;

    public static Intent newInstance(Context context, LTask task, List<TaskMessage> messages) {
        final Intent intent = new Intent(context, EditTaskCommentsActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        mTask = task;
        mTaskMessages = messages;
        mActivity = (EditTaskActivity) context;

        return intent;
    }



    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        setContentView(R.layout.activity_edit_task_comments);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitleToolbar = (TextView) findViewById(R.id.toolbar_text_name);

        mViewPager = (RelativeLayout) findViewById(R.id.main_container_comments);
        if (mViewPager != null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            mCommentsFragment = EditTaskConversationFragment.newInstance(mTask, mTaskMessages);
            ft.replace(COMMENTS_CONTAINER, mCommentsFragment, EditTaskConversationFragment.CLASS_PATH);
            ft.commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!getSettings().ismOnBackpressedSave()) {
            //getMenuInflater().inflate(R.menu.add_task_menu, menu);
        }
        else{
            //getMenuInflater().inflate(R.menu.edit_task_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        case R.id.save_task:
        case R.id.dont_save:
            saveComments();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onUserLeaveHint()
    {
        super.onUserLeaveHint();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                saveComments();
                return true;
        }
        return false;
    }

    @Override
    public int getContainerId() {
        return 0;
    }

    public LTask getTask() {
        return mTask;
    }

    public List<TaskMessage> getTaskMessages() {
        return mTaskMessages;
    }

    public void hideInput() {
        Utils.hideInput(mViewPager);
    }

    public void saveComments() {

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        final Drawable upArrow = getResources().getDrawable(R.drawable.baseline_arrow_back_white_24);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);


        mTitleToolbar.setText(R.string.properties_comments);
        mToolbar.setNavigationIcon(upArrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveComments();
            }
        });
    }
}
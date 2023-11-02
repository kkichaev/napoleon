package com.ashberrysoft.leadertask.modern.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.Status;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSaveHelper;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.ToastController;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.CustomEditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static com.ashberrysoft.leadertask.R.string.task_today;


public class AddNewTaskWidgetActivity extends BaseActivity {
    private LinearLayout mFileContainer;
    private LinearLayout mTodayContainer;
    private CustomEditText mEditText;
    private TextView mTextToday;
    private TextView mFileName;
    private ProgressBar mProgressBar;
    private ImageView imageViewMic;
    private Button imageView;
    boolean isAddToToday = true;
    private TaskFile mTaskFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        //displaySpeechRecognizer();
        //
        final LTApplication mApp = (LTApplication) getApplication().getApplicationContext();


        if (getIntent() != null && getIntent().getType() != null) {
            if (LTSettings.getInstance().getUserProfile().isValid()) {
                if (getIntent().getAction() == "com.google.android.gms.actions.CREATE_NOTE"){
                    String taskName = getIntent().getStringExtra("android.intent.extra.TEXT");
                    if (taskName != null && !taskName.isEmpty()) {
                        final LTask task = TaskHelper.createNewTaskWithParams(LTSettings.getInstance().getUserName(), LTSettings.getInstance().getUserName(), 0, null, null, null, null);
                        task.setName(taskName);
                        task.setStatus(Status.TASK_NOT_BEGIN.getStatusCode());

                        final List<TaskFile> taskFiles = new ArrayList<>();
                        if (mTaskFile != null) {
                            taskFiles.add(mTaskFile);
                        }
                        new TaskSaveHelper(false, AddNewTaskWidgetActivity.this, task, true, null, null, 0, taskFiles, new ArrayList<TaskFile>(0), false).run();
                        Toast.makeText(AddNewTaskWidgetActivity.this, com.ashberrysoft.leadertask.R.string.t_task_input, Toast.LENGTH_SHORT).show();
                    }
                    finish();
                } else {
                    if (getIntent().getAction() == "android.intent.action.INSERT"){
                        finish();
                    } else {
                        if (getIntent().getType().indexOf("image") != -1) {
                            final Uri uri = (Uri) getIntent().getExtras().get("android.intent.extra.STREAM");
                            final String path = getImagePathFromInputStreamUri(uri, AddNewTaskWidgetActivity.this);

                            try {
                                final File src = new File(path.replace(SharedStrings.CONTENT_FILE, SharedStrings.EMPTY));
                                final File dst = new File(mApp.getAppFolder(), src.getName());

                                if (!src.equals(dst)) {
                                    Utils.FileWorker.copyFile(src, dst);
                                }

                                if (dst != null && dst instanceof File) {
                                    setTaskFile(dst);
                                } else {
                                    Utils.showToast(AddNewTaskWidgetActivity.this, R.string.t_error_file_saving);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            if (getIntent().getAction() == Intent.ACTION_SEND) {
                                if (SharedStrings.MIME_TYPE_PLAIN.equals(getIntent().getType())) {
                                    String taskName = getIntent().getStringExtra("android.intent.extra.TEXT");
                                    if (taskName != null && !taskName.isEmpty()) {
                                        final LTask task = TaskHelper.createNewTaskWithParams(LTSettings.getInstance().getUserName(), LTSettings.getInstance().getUserName(), 0, null, null, null, null);
                                        task.setName(taskName);
                                        task.setStatus(Status.TASK_NOT_BEGIN.getStatusCode());

                                        new TaskSaveHelper(false, AddNewTaskWidgetActivity.this, task, true, null, null, 0, new ArrayList<TaskFile>(0), new ArrayList<TaskFile>(0), false).run();
                                        Toast.makeText(AddNewTaskWidgetActivity.this, com.ashberrysoft.leadertask.R.string.t_task_input, Toast.LENGTH_SHORT).show();
                                    }
                                    finish();
                                }
                            } else {
                                Utils.showToast(AddNewTaskWidgetActivity.this, R.string.t_error_file_saving);
                            }
                        }
                    }
                }
            } else {
                Utils.showToast(mApp, R.string.t_error_no_auth);
            }
        }
        //
        displayDialog();
    }

    private void setTaskFile(File mFile) {
        final LTSettings settings = LTSettings.getInstance();

        final UUID taskUid = UUID.randomUUID();

        final LTask task = TaskHelper.createNewTaskWithParams(settings.getUserName(), settings.getUserName(), 0, null, null, null, null);

        task.setUid(String.valueOf(taskUid).toUpperCase());

        task.setName(mFile.getName());

        mTaskFile = new TaskFile(null, taskUid, null, mFile.getName(), mFile.length(), task.getEmailCustomer(), 1);
    }

    public String getPath(Uri uri, Activity activity) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.MediaColumns.DATA};
            cursor = activity.getContentResolver().query(uri, projection, null, null, null);
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "";
    }

    @Override
    public int getContainerId() {
        return 0;
    }

    @Override
    public void onBackPressed() {
        finish();
        //super.onBackPressed();

    }

    private void saveTask(CustomEditText editText) {
        if (LTSettings.getInstance().getUserProfile().isValid()) {
            if (editText.getText().toString().trim().length() > 0) {
                long term = 0;
                String textToast= "";
                //
                if (isAddToToday) {
                    term = TimeHelper.currentTimeMillisWithoutTimeZone();
                    textToast = getString(R.string.task_added) +" "+ getString(R.string.in) +" "+ (getString(R.string.task_today)).toLowerCase();
                } else {
                    term = TimeHelper.currentTimeMillisWithoutTimeZone()+86400000;
                    textToast = getString(R.string.task_added) +" "+ getString(R.string.in) +" "+ (getString(R.string.task_tomorrow)).toLowerCase();
                }
                //
                final LTask task = TaskHelper.createNewTaskWithParams(LTSettings.getInstance().getUserName(), LTSettings.getInstance().getUserName(), 0, null, null, null, null);
                task.setName(editText.getText().toString().trim());

                Utils.parsingTaskName(task, getApp());

                final List<TaskFile> taskFiles = new ArrayList<>();
                if (mTaskFile != null) {
                    taskFiles.add(mTaskFile);
                }
                new TaskSaveHelper(false, AddNewTaskWidgetActivity.this, task, true, null, null, 0, taskFiles, new ArrayList<TaskFile>(0), false).run();

                //ToastController.getInstance(AddNewTaskWidgetActivity.this).showToast(textToast);
                ToastController.getInstance(AddNewTaskWidgetActivity.this).showToast(getString(R.string.task_added));

                finish();
            } else {
                Utils.showToast(AddNewTaskWidgetActivity.this, R.string.error_empty_task_title);
            }
        } else {
            Utils.showToast(AddNewTaskWidgetActivity.this, R.string.t_error_no_auth);
            finish();
        }
    }



    private void displayDialog() {
        if (LTSettings.getInstance().getUserProfile().isValid()) {
            final View v = LayoutInflater.from(this).inflate(R.layout.add_task_wdget_dialog, null);

            mEditText = (CustomEditText) v.findViewById((R.id.task_name));
            mFileContainer = (LinearLayout) v.findViewById((R.id.file_container));
            mTodayContainer = (LinearLayout) v.findViewById((R.id.today_container));

            mTextToday = (TextView) v.findViewById((R.id.today));
            mFileName = (TextView) v.findViewById((R.id.file_name));
            if (mTaskFile != null){
                mFileContainer.setVisibility(View.VISIBLE);
                mFileName.setText(mTaskFile.getFileName());
            } else {
                mFileContainer.setVisibility(View.GONE);
            }
            mTodayContainer.setVisibility(View.GONE);
            mTodayContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isAddToToday = !isAddToToday;
                    mTextToday.setText(isAddToToday ? getString(task_today) : getString(R.string.task_tomorrow));
                }
            });
            imageView = (Button) v.findViewById((R.id.add_task));
            imageViewMic = (ImageView) v.findViewById((R.id.add_task_mic));
            mProgressBar = (ProgressBar) v.findViewById((R.id.progressBar));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveTask(mEditText);
                }
            });
            imageViewMic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int permission = ActivityCompat.checkSelfPermission(AddNewTaskWidgetActivity.this, Manifest.permission.RECORD_AUDIO);

                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                                AddNewTaskWidgetActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                1
                        );
                    } else {
                        displaySpeechRecognizer();
                    }
                }
            });

            mEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            mEditText.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

            mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        saveTask(mEditText);
                    }
                    return true;
                }
            });

            //проверка на есть ли распознование голоса в телефоне
            boolean available = SpeechRecognizer.isRecognitionAvailable(this);
            //boolean available = false;
            if (available) {
                mEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() == 0) {
                            imageView.setVisibility(View.GONE);
                            imageViewMic.setVisibility(View.VISIBLE);
                        } else {
                            imageViewMic.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            } else {
                imageViewMic.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            }

            final AlertDialog.Builder ad = new AlertDialog.Builder(getDialogContext())
                    .setView(v);

            AlertDialog alertToShow = ad.create();
            alertToShow.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    finish();
                }
            });
            alertToShow.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            alertToShow.show();
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = alertToShow.getWindow();
            lp.copyFrom(window.getAttributes());
            //This makes the dialog take up the full width
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }
        else {
            Utils.showToast(AddNewTaskWidgetActivity.this, R.string.t_error_no_auth);
            finish();
        }
    }

    private void displaySpeechRecognizer() {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            //intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"com.domain.app");

            final SpeechRecognizer recognizer = SpeechRecognizer.createSpeechRecognizer(this.getApplicationContext());
            RecognitionListener listener = new RecognitionListener() {
                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> voiceResults = results
                            .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    Log.d(TAG, voiceResults.get(0));
                    String spokenText = voiceResults.get(0);
                    // Do something with spokenText
                    spokenText = spokenText.substring(0,1).toUpperCase() + spokenText.substring(1);
                    mEditText.setText(spokenText);
                    mEditText.setSelection(mEditText.getText().length());
                    //recognizer.stopListening();
                    mProgressBar.setVisibility(View.GONE);
                    mEditText.setEnabled(true);
                    showKeyBoard();
                    mEditText.setHint(getResources().getString(R.string.error_empty_task_title));
                    imageViewMic.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onError(int error) {
                    if (mEditText.length() > 0) {
                        mProgressBar.setVisibility(View.GONE);
                        mEditText.setEnabled(true);
                        showKeyBoard();
                        mEditText.setHint(getResources().getString(R.string.error_empty_task_title));
                        imageViewMic.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                    } else {

                        mProgressBar.setVisibility(View.GONE);
                        mEditText.setEnabled(true);
                        showKeyBoard();
                        mEditText.setHint(getResources().getString(R.string.error_empty_task_title));
                        imageViewMic.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onBeginningOfSpeech() {
                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }
            };
            recognizer.setRecognitionListener(listener);

            recognizer.startListening(intent);
            mProgressBar.setVisibility(View.VISIBLE);
            imageViewMic.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            mEditText.setHint(getResources().getString(R.string.speech_dialog_title)+"...");
            mEditText.setEnabled(false);
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_speeching, Toast.LENGTH_SHORT).show();
            mProgressBar.setVisibility(View.GONE);
            mEditText.setEnabled(true);
            showKeyBoard();
            mEditText.setHint(getResources().getString(R.string.error_empty_task_title));
            imageViewMic.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);

        } finally {

        }

    }

    private void showKeyBoard() {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mEditText.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mEditText.requestFocus();
                imm.showSoftInput(mEditText, 0);
            }
        }, 0);
    }

    private Context getDialogContext() {
        final Context context;
        context = new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light);

        return context;
    }

    public static String getImagePathFromInputStreamUri(Uri uri, Activity activity) {
        InputStream inputStream = null;
        String filePath = null;

        if (uri.getAuthority() != null) {
            try {
                inputStream = activity.getContentResolver().openInputStream(uri); // context needed
                File photoFile = createTemporalFileFrom(inputStream, activity, uri);

                filePath = photoFile.getPath();

            } catch (FileNotFoundException e) {
                // log
            } catch (IOException e) {
                // log
            }finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return filePath;
    }

    private static File createTemporalFileFrom(InputStream inputStream, Activity activity, Uri mUri) throws IOException {
        File targetFile = null;

        if (inputStream != null) {
            int read;
            byte[] buffer = new byte[8 * 1024];

            targetFile = createTemporalFile(activity, mUri);
            OutputStream outputStream = new FileOutputStream(targetFile);

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();

            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return targetFile;
    }

    private static File createTemporalFile(Activity activity, Uri mUri) {
        return new File(activity.getExternalCacheDir(), getFileName(mUri, activity)); // context needed
    }

    public static String getFileName(Uri uri, Activity activity) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}

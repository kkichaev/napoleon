package com.ashberrysoft.leadertask.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.UUID;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerTabStrip;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.data_providers.network.OkHttpConnection;
import com.ashberrysoft.leadertask.data_providers.network.SynchronizationTask;
import com.ashberrysoft.leadertask.dialogs.ErrorDialog;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.enums.LeaderTaskLanguage;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.instance_sync.LeaderTaskSyncService;
import com.ashberrysoft.leadertask.instance_sync.MyInstanceIDListenerService;
import com.ashberrysoft.leadertask.interfaces.CursorFiller;
import com.ashberrysoft.leadertask.modern.activity.BaseActivity;
import com.ashberrysoft.leadertask.modern.activity.SlidingActivity;
import com.ashberrysoft.leadertask.modern.activity.TodayTasksWidget;
import com.ashberrysoft.leadertask.modern.cache.CompletedCache;
import com.ashberrysoft.leadertask.modern.cache.EmployeeCache;
import com.ashberrysoft.leadertask.modern.cache.LTaskCache;
import com.ashberrysoft.leadertask.modern.cache.MarkerCache;
import com.ashberrysoft.leadertask.modern.cache.TaskFileCache;
import com.ashberrysoft.leadertask.modern.cache.TaskLinkCache;
import com.ashberrysoft.leadertask.modern.cache.TaskMessageCache;
import com.ashberrysoft.leadertask.modern.dialog.AddEmpDialog;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.CompletedTask;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskLinkReset;
import com.ashberrysoft.leadertask.modern.helper.TaskNotifyHelper;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.modern.loader.MenuLoader;
import com.ashberrysoft.leadertask.modern.service.TodayWidgetAdapterService;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.GoogleApiClient;
/*import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;*/
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.json.simple.JSONArray;

import static com.ashberrysoft.leadertask.application.Config.NETWROK_ACCEPT_INVITE;
import static com.ashberrysoft.leadertask.enums.MenuItemType.HEADER_AVAILABLE_PROJECTS;
import static com.ashberrysoft.leadertask.enums.MenuItemType.HEADER_BY_ME;
import static com.ashberrysoft.leadertask.enums.MenuItemType.HEADER_CATEGORIES;
import static com.ashberrysoft.leadertask.enums.MenuItemType.HEADER_FOR_ME;
import static com.ashberrysoft.leadertask.enums.MenuItemType.HEADER_PROJECTS;
import static com.ashberrysoft.leadertask.modern.activity.SlidingActivity.mSlidingMenu;
import static com.ashberrysoft.leadertask.modern.domains.lion.LTask.MY_TASK_USER_ORDER;
import static com.ashberrysoft.leadertask.modern.domains.lion.LTask.MY_TASK_USER_ORDER_DESC;
import static com.ashberrysoft.leadertask.modern.fragment.MenuFragment.lastCheckedMenuItemUUID;

public final class Utils {
    public static final SimpleDateFormat sdfDate = getSimpleDateFormat();
    private static Tracker mTracker; //трекер аналитики гугла
    private static String googleAnalyticsId = "UA-266992-45";
    private static boolean startApp;
    public static String TMP_FOTO_FILE_NAME = "tmp_foto_file_name";
    private static MediaPlayer mediaPlayer;
    private static GoogleApiClient mGoogleApiClient;
    private static boolean isShowingInviteDialog = false;

    // public static SimpleDateFormat sdfTime;// = new
    // SimpleDateFormat("hh:mm a", Locale.getDefault());

    private static SimpleDateFormat getSimpleDateFormat() {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        return sdf;
        // sdfTime.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private static SimpleDateFormat getSimpleDateFormat2(String format) {
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeHelper.DEFAULT_TIME_ZONE);

        return sdf;
    }

    /**
     * Convert density independent pixels (dips) to pixels
     * 
     * @param context
     *            - application context
     * @param dips
     * @return pixels for current display size
     */
    public static int convertDipToPixels(Context context, float dips) {
        return (int) (dips * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * Convert pixels to density independent pixels (dips)
     * 
     * @param context
     *            - application context
     * @param pixels
     * @return dips for current display size
     */
    public static int convertPixelsToDips(Context context, float pixels) {
        return (int) (pixels / context.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * Convert density independent pixels to pixels
     * 
     * @param dp
     * @return pixels for current display size
     */
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density + 0.5f);
    }


    public static String getDayOfDate(Context context, Calendar calendar) {
        final Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, +1);
        final Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        if (compareDays(Calendar.getInstance(), calendar)) {
            return context.getResources().getString(R.string.task_today);
        } else if (compareDays(tomorrow, calendar)) {
            return context.getResources().getString(R.string.task_tomorrow);
        } else if (compareDays(yesterday, calendar)) {
            return context.getResources().getString(R.string.task_yesterday);
        }
        return null;
    }

    /**
     * @return Yesterday/Today/Tomorrow prefixes
     */
    public static String getDayOfDate(Context context, Date date) {
        final Calendar dateCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        dateCal.setTime(date);
        return getDayOfDate(context, dateCal);
    }

    /**
     * Compare two days and return true only if it is the same day.
     * 
     * @param cal1
     * @param cal2
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     * @return return true only if it is the same day.
     */
    public static boolean compareDays(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static void showToast(Context context, CharSequence s) {
        ToastController.getInstance(context).showToast(s);
    }

    public static void showToast(Context context, int id) {
        showToast(context, context.getString(id));
    }

    // method for extracting day of week from received Date instance
    public static String getDay(Context context, Date date) {
        if (date != null) {
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MMM.yyyy", Locale.getDefault());
            sdfDate.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date dToday = new Date();
            String dateToday = sdfDate.format(dToday);

            Calendar cl = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            cl.add(Calendar.DATE, -1);

            Calendar cl1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            cl1.add(Calendar.DATE, +1);

            Date dYesterday = new Date(cl.getTimeInMillis());
            Date dTomorrow = new Date(cl1.getTimeInMillis());
            String dateTomorrow = sdfDate.format(dTomorrow);
            String dateYesterday = sdfDate.format(dYesterday);

            if (sdfDate.format(date).equals(dateToday)) {
                return context.getResources().getString(R.string.task_today);
            } else {
                if (sdfDate.format(date).equals(dateTomorrow)) {
                    return context.getResources().getString(R.string.task_tomorrow);
                } else {
                    if (sdfDate.format(date).equals(dateYesterday)) {
                        return context.getResources().getString(R.string.task_yesterday);
                    }
                }
            }

        }
        SimpleDateFormat sdfWeek = new SimpleDateFormat("dd MMM", Locale.getDefault());
        sdfWeek.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdfWeek.format(date);
    }

    /**
     * @author "Alexander Slobodchukov (alexander.slobodchukov@gmail.com)"
     * @param context
     * @param date
     * @return
     */
    public static String getNearbyDays(Context context, Date date) {
        if (date != null) {
            final SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MMM.yyyy", Locale.getDefault());
            sdfDate.setTimeZone(TimeZone.getTimeZone("GMT"));
            final Date dToday = new Date();
            final String dateToday = sdfDate.format(dToday);

            final Calendar cl = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            cl.add(Calendar.DATE, -1);

            final Calendar cl1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            cl1.add(Calendar.DATE, +1);

            final Date dYesterday = new Date(cl.getTimeInMillis());
            final Date dTomorrow = new Date(cl1.getTimeInMillis());
            final String dateTomorrow = sdfDate.format(dTomorrow);
            final String dateYesterday = sdfDate.format(dYesterday);

            if (sdfDate.format(date).equals(dateToday)) {
                return context.getResources().getString(R.string.task_today) + ": ";
            } else {
                if (sdfDate.format(date).equals(dateTomorrow)) {
                    return context.getResources().getString(R.string.task_tomorrow) + ": ";
                } else {
                    if (sdfDate.format(date).equals(dateYesterday)) {
                        return context.getResources().getString(R.string.task_yesterday) + ": ";
                    }
                }
            }

        }
        return "";
    }

    public static String amputationMonth(Date date) {
        // final String res = /*.substring(0, 6)*/;
        return sdfDate.format(date);
    }

    /**
     * Prepare task terms string.
     * 
     * @param task
     * @return task terms string.
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     */
    public static String taskTermFormatter(Context context, Task task, boolean isPerformer) {

        final StringBuilder term = new StringBuilder();
        final Date termBegin;
        final Date termEnd;

        if (isPerformer) {
            termBegin = task.getTermBegin();
            termEnd = task.getTermEnd();
        } else {
            termBegin = task.getTermCustomerBegin();
            termEnd = task.getTermCustomerEnd();
        }

        if (termBegin != null && termEnd != null) {
            final Calendar begin = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            final Calendar end = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            begin.setTime(termBegin);
            end.setTime(termEnd);
            if (!compareDays(begin, end)) {

                String day = getDayOfDate(context, termBegin);
                term.append((day == null ? sdfDate.format(termBegin) : day));
                term.append(" - ");
                day = getDayOfDate(context, termEnd);
                term.append((day == null ? sdfDate.format(termEnd) : day));
            } else {
                if (wholeDayTask(task, isPerformer)) {
                    final String day = getDayOfDate(context, termBegin);
                    term.append((day == null ? sdfDate.format(termBegin) : day));
                } else {
                    SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");

                    final String day = getDayOfDate(context, termBegin);
                    term.append((day == null ? sdfDate.format(termBegin) : day));
                    term.append(", ");
                    if (DateFormat.is24HourFormat(context)) {
                        sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    } else {
                        sdfTime = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                    }
                    sdfTime.setTimeZone(TimeZone.getTimeZone("GMT"));
                    term.append(sdfTime.format(termBegin));
                }
            }
        }

        return term.toString();
    }

    public static boolean wholeDayTask(Task task, boolean isPerformer) {
        final Date termBegin;
        final Date termEnd;

        if (isPerformer) {
            termBegin = task.getTermBegin();
            termEnd = task.getTermEnd();
        } else {
            termBegin = task.getTermCustomerBegin();
            termEnd = task.getTermCustomerEnd();
        }

        final Calendar begin = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        final Calendar end = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        begin.setTime(termBegin);
        end.setTime(termEnd);

        if (compareDays(begin, end)) {
            return begin.get(Calendar.HOUR_OF_DAY) == 0//
                    && begin.get(Calendar.MINUTE) == 0//
                    && begin.get(Calendar.SECOND) == 0//
                    && end.get(Calendar.HOUR_OF_DAY) == 23//
                    && end.get(Calendar.MINUTE) == 59//
                    && end.get(Calendar.SECOND) == 59;
        }

        return false;
    }

    /**
     * РїРѕР»СѓС‡РµРЅРёРµ РґР°РЅРЅРѕР№ РґР°С‚С‹ СЃ СѓС‡РµС‚РѕРј С‚РµРєСѓС‰РµРіРѕ С‡Р°СЃРѕРІРѕРіРѕ РїРѕСЏСЃР° РІ GMT
     * 
     * @author Tetiana Diachuk (diacht@gmail.com)
     * @return
     */
    public static Calendar getCalendarDateGMT(Date date1) {
        Date date = new Date(date1.getTime() + Calendar.getInstance().getTimeZone().getRawOffset() + Calendar.getInstance().getTimeZone().getDSTSavings());

        Calendar today = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        today.setTime(date);
        return today;
    }

    public static Dialog getSimpleDialog(Context context, OnClickListener listener, CharSequence title, CharSequence message) {
        final AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setCancelable(true);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setPositiveButton(R.string.btn_ok, listener);
        ad.setNegativeButton(R.string.btn_cancel, listener);

        return ad.show();
    }

    public static Dialog getSimpleDialog(Context context, OnClickListener listener, int title, int message) {
        return getSimpleDialog(context, listener, context.getString(title), context.getString(message));
    }

    public static Dialog getSimpleDialog(Context context, OnClickListener listener, CharSequence title, int message) {
        return getSimpleDialog(context, listener, title, context.getString(message));
    }

    public static Dialog getSimpleDialog(Context context, OnClickListener listener, int title, CharSequence message) {
        return getSimpleDialog(context, listener, context.getString(title), message);
    }

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    public static int whatColorToUse(int color) {
        final int equation = (299 * Color.red(color) + 587 * Color.green(color) + 114 * Color.blue(color)) / 1000;
        return equation >= 125 ? Color.BLACK : Color.WHITE;
    }

    public static final class TaskUtils {

        private static final Calendar WORK_CALENDAR = Calendar.getInstance();

        public static boolean isCompleted(Task task, String user) {
            return isCompleted(task.getStatus(), task.getCustomer(), user);
            // final int status = task.getStatus();
            // return status == 1 || status == 7 ||
            // (!user.equals(task.getCustomer()) && (status == 5 || status ==
            // 8));
        }

        public static boolean isCompleted(int status, String customer, String user) {
            return status == 1 || status == 7 || (!user.equals(customer) && (status == 5 || status == 8));
        }

        public static void setCalendarToBaseFormat(Calendar c) {
            c.set(Calendar.HOUR_OF_DAY, 12);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
        }

        public static long setCalendarToBaseFormat(long date) {
            synchronized (WORK_CALENDAR) {
                WORK_CALENDAR.setTimeInMillis(date);
                setCalendarToBaseFormat(WORK_CALENDAR);

                return WORK_CALENDAR.getTimeInMillis();
            }
        }

        public static long getStartOfDayInMillis(Calendar c) {
            synchronized (WORK_CALENDAR) {
                WORK_CALENDAR.setTimeInMillis(c.getTimeInMillis());

                WORK_CALENDAR.set(Calendar.HOUR_OF_DAY, 0);
                WORK_CALENDAR.set(Calendar.MINUTE, 0);
                WORK_CALENDAR.set(Calendar.SECOND, 0);
                WORK_CALENDAR.set(Calendar.MILLISECOND, 0);

                return WORK_CALENDAR.getTimeInMillis();
            }
        }

        public static long getEndOfDayInMillis(Calendar c) {
            synchronized (WORK_CALENDAR) {
                WORK_CALENDAR.setTimeInMillis(c.getTimeInMillis());

                WORK_CALENDAR.set(Calendar.HOUR_OF_DAY, 23);
                WORK_CALENDAR.set(Calendar.MINUTE, 59);
                WORK_CALENDAR.set(Calendar.SECOND, 59);
                WORK_CALENDAR.set(Calendar.MILLISECOND, 999);

                return WORK_CALENDAR.getTimeInMillis();
            }
        }
    }

    /**
     * Class that work with app folders and media files
     * 
     * @author Tregub Artem tregub.artem@gmail.com
     */
    public static final class FileWorker {

        public enum FileType {
            OTHER(SharedStrings.EMPTY), PICTURE("jpg"), AUDIO("mp3"), AUDIO_TEMP("wav");

            private String mType;

            private FileType(String type) {
                mType = type;
            }

            public String getType() {
                return mType;
            }

            public static FileType getFileType(String fileName) {
                if (SharedStrings.MIME_TYPE_JPEG.equals(fileName)) {
                    return PICTURE;
                }

                final String[] split = fileName.split(SharedStrings.SPLIT_DOT);
                if (split.length > 1) {
                    final String last = split[split.length - 1].toLowerCase();
                    for (FileType type : FileType.values()) {
                        if (last.equals(type.getType())) {
                            return type;
                        }
                    }
                }

                return OTHER;
            }
        }

        public static final SimpleDateFormat SDF_FILE_NAME = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss.", Locale.getDefault());
        public static final String NOMEDIA_FILE_NAME = ".nomedia";
        public static final String APP_FOLDER_LOG = ".logs";
        public static final String APP_FOLDER_ZIP = ".zips";
        private static final String DM_IO_EXCEPTION = "File not exist : ";
        private static final String SPLIT_DOT = "\\.";

        private static File getSystemDirectory(Context context) {
            final File external = context.getExternalFilesDir(null);
            if (external != null && external.exists()) {
                return external;
            }
            return context.getFilesDir();
        }

        public static File initializateAppFolder(Context context) {
            final File appFolder = getSystemDirectory(context);
            appFolder.mkdirs();
            try {
                new File(appFolder, NOMEDIA_FILE_NAME).createNewFile();
            } catch (IOException e) {}
            return appFolder;
        }

        public static File createAppLogsFolder(File appFolde) {
            final File appLogs = new File(appFolde, APP_FOLDER_LOG);
            appLogs.mkdirs();

            return appLogs;
        }
        public static File createAppZipsFolder(File appFolde) {
            final File appZips = new File(appFolde, APP_FOLDER_ZIP);
            appZips.mkdirs();

            return appZips;
        }

        public static String getNewFileName(FileType type) {
            return SDF_FILE_NAME.format(new Date()) + type.getType();
        }

        public static String getNewEmpFotoFileName() {
            return TMP_FOTO_FILE_NAME;
        }


        public static String getNewCurrentPictureFileName() {
            return getNewFileName(FileType.PICTURE);
        }

        public static File copyFile(String fileName, File appFolder) throws IOException {
            final File src = new File(appFolder, fileName);
            final File dst = new File(appFolder, src.getName());

            return copyFile(src, dst);
        }

        public static File copyFile(String fileName, File appFolder, String fileNameNew) throws IOException {
            final File src = new File(appFolder, fileName);
            final File dst = new File(appFolder, fileNameNew);

            return copyFile(src, dst);
        }

        public static File copyEmpFotoFile(String pathSrc, File appFolder) throws IOException {
            final File src = new File(pathSrc);
            final File dst = new File(appFolder, getNewEmpFotoFileName());

            return copyFile(src, dst);
        }

        public static File copyEmpFotoFile(File src, File appFolder) throws IOException {
            final File dst = new File(appFolder, getNewEmpFotoFileName());

            return copyFile(src, dst);
        }

        public static File copyFile(FileType type, String pathSrc, File appFolder) throws IOException {
            final File src = new File(pathSrc);
            final File dst = new File(appFolder, src.getName());

            return copyFile(src, dst);
        }

        public static File copyAnyFile(String pathSrc, File appFolder) throws IOException {
            final File src = new File(pathSrc);
            final File dst = new File(appFolder, src.getName());

            return copyFile(src, dst);
        }

        public static File copyFile(File src, File dst) throws IOException {
            if (!src.exists()) {
                throw new IOException(DM_IO_EXCEPTION + src.getAbsolutePath());
            }

            FileInputStream streamSrc = null;
            FileOutputStream streamDst = null;
            FileChannel channelSrc = null;
            FileChannel channelDst = null;
            try {
                streamSrc = new FileInputStream(src);
                streamDst = new FileOutputStream(dst);
                channelSrc = streamSrc.getChannel();
                channelDst = streamDst.getChannel();

                channelDst.transferFrom(channelSrc, 0, channelSrc.size());
            } finally {
                if (channelSrc != null) {
                    channelSrc.close();
                }
                if (channelDst != null) {
                    channelDst.close();
                }
                if (streamSrc != null) {
                    streamSrc.close();
                }
                if (streamDst != null) {
                    streamDst.close();
                }
            }

            if (dst.exists()) {
                return dst;
            } else {
                throw new IOException(DM_IO_EXCEPTION + dst.getAbsolutePath());
            }
        }

        public static String getFileSize(Context context, long size) {
            final int length = String.valueOf(size).length();
            if (length < 4) {
                return size + context.getString(R.string.unit_bytes);
            } else if (length < 7) {
                return size / 1024 + context.getString(R.string.unit_Kbytes);
            } else {
                return size / 1048576 + context.getString(R.string.unit_Mbytes);
            }
        }

        public static String getFileMimeType(File file) {
            final String[] split = file.getAbsolutePath().split(SPLIT_DOT);
            split[3] = split[3].toLowerCase(); // все форматы в нижний регистр
            final String extension = split[split.length - 1];

            if (extension != null) {
                final MimeTypeMap mime = MimeTypeMap.getSingleton();
                return mime.getMimeTypeFromExtension(extension);
            }
            return null;
        }

        public static boolean makeFile(File dst, InputStream is) throws IOException {
            if (dst == null || is == null) {
                return false;
            }

            dst.delete();

            OutputStream os = null;
            try {
                os = new FileOutputStream(dst);

                int read = 0;
                final byte[] buffer = new byte[1024];
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
            } finally {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            }

            return true;
        }

        public static boolean fromUriToFile(Context context, Uri uri, File file) {
            BufferedOutputStream os = null;
            BufferedInputStream is = null;

            try {
                os = new BufferedOutputStream(new FileOutputStream(file));
                is = new BufferedInputStream(context.getContentResolver().openInputStream(uri));
                final byte[] bytes = new byte[1024];

                int read;
                while ((read = is.read(bytes)) != -1) {
                    os.write(bytes, 0, read);
                }

                return true;

            } catch (Exception e) {
                toLog(e);
                return false;

            } finally {
                if (os != null) {
                    try {
                        os.close();

                    } catch (IOException e) {
                        toLog(e);
                    }
                }

                if (is != null) {
                    try {
                        is.close();

                    } catch (IOException e) {
                        toLog(e);
                    }
                }
            }
        }
    }

    private static final String LOG_TAG = "ltLog";
    public static final String LOG_NULL = "null";

    public static void toLog(String message) {
        try {
            Log.d(LOG_TAG, message);

        } catch (Exception e) {
            Log.d(LOG_TAG, LOG_NULL);
        }
    }

    public static void toLog(Object object) {
        toLog(String.valueOf(object));
    }

    public static void toLog(Locale l) {
        final StringBuilder sb = new StringBuilder();

        sb.append("Locale = " + l.getCountry());
        sb.append(SharedStrings.NEW_LINE_C);
        sb.append("\tgetDisplayCountry = " + l.getDisplayCountry());
        sb.append(SharedStrings.NEW_LINE_C);
        sb.append("\tgetDisplayLanguage = " + l.getDisplayLanguage());
        sb.append(SharedStrings.NEW_LINE_C);
        sb.append("\tgetDisplayName = " + l.getDisplayName());
        sb.append(SharedStrings.NEW_LINE_C);
        sb.append("\tgetDisplayVariant = " + l.getDisplayVariant());
        sb.append(SharedStrings.NEW_LINE_C);
        sb.append("\tgetLanguage = " + l.getLanguage());
        sb.append(SharedStrings.NEW_LINE_C);
        sb.append("\tgetVariant = " + l.getVariant());

        toLog(sb);
    }

    public static void toLog(Throwable e) {
        final StringBuilder sb = new StringBuilder();

        sb.append(e.getMessage());
        sb.append(SharedStrings.NEW_LINE_C);
        sb.append(e.getClass().getName());
        for (StackTraceElement s : e.getStackTrace()) {
            sb.append(SharedStrings.NEW_LINE_C);
            sb.append(s.toString());
        }

        toLog(sb);
    }

    public static void toLog(Task t) {
        Utils.toLog(t.getName() + " : " + t.getId().toString());
        Utils.toLog("getSubTasksCount = " + t.getSubTasksCount());
        Utils.toLog("getSubTasksCountNotRead = " + t.getSubTasksCountNotRead());
        Utils.toLog("getSubTasksCountNotMade = " + t.getSubTasksCountNotMade());
        Utils.toLog("getSubTasksCountNotMadeAndNotRead = " + t.getSubTasksCountNotMadeAndNotRead());
    }

    public static void toLog(Intent intent) {
        final StringBuilder sb = new StringBuilder(String.valueOf(intent));

        if (!TextUtils.isEmpty(intent.getAction())) {
            sb.append(SharedStrings.NEW_LINE_C);
            sb.append("\t getAction = ");
            sb.append(intent.getAction());
        }
        final String uri = intent.getData() == null ? null : intent.getData().toString();
        if (!TextUtils.isEmpty(uri)) {
            sb.append(SharedStrings.NEW_LINE_C);
            sb.append("\tgetData = ");
            sb.append(uri);
        }

        toLog(sb);
        toLog(intent.getExtras());
    }

    public static void toLog(Bundle b) {
        if (b == null || b.isEmpty()) {
            return;
        }

        final StringBuilder sb = new StringBuilder("Bundle");
        for (String key : b.keySet()) {
            sb.append(SharedStrings.NEW_LINE_C);
            sb.append(key);
            sb.append(SharedStrings.TAB_C);
            sb.append(b.get(key));
        }
        toLog(sb);
    }

    public static void toLog(ContentValues cv) {
        final StringBuilder sb = new StringBuilder();
        sb.append("\tContentValues\n");

        if (cv != null) {
            for (String key : cv.keySet()) {
                sb.append(key);
                sb.append(SharedStrings.COLON_C);
                sb.append(cv.get(key));
                sb.append(SharedStrings.NEW_LINE_C);
            }
        }

        sb.append(SharedStrings.NEW_LINE_C);
        toLog(sb);
    }

    public static void toLog(ContentValues[] cvs) {
        for (ContentValues cv : cvs) {
            toLog(cv);
        }
    }

    public static void toLog(Calendar calendar) {
        final StringBuilder sb = new StringBuilder();

        sb.append(SharedStrings.SPACE_C);
        sb.append(calendar.get(Calendar.YEAR));

        sb.append(SharedStrings.SPACE_C);
        sb.append(calendar.get(Calendar.MONTH));

        sb.append(SharedStrings.SPACE_C);
        sb.append(calendar.get(Calendar.DAY_OF_MONTH));

        sb.append(SharedStrings.SPACE_C);
        sb.append(calendar.get(Calendar.HOUR_OF_DAY));

        sb.append(SharedStrings.SPACE_C);
        sb.append(calendar.get(Calendar.MINUTE));

        sb.append(SharedStrings.SPACE_C);
        sb.append(calendar.get(Calendar.SECOND));

        sb.append(SharedStrings.SPACE_C);
        sb.append(calendar.get(Calendar.MILLISECOND));

        toLog(sb);
    }

    public static void toLog(Cursor c) {
        final StringBuilder sb = new StringBuilder();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            sb.append("< < < < < < <\n");
            for (int i = 0; i < c.getColumnCount(); i++) {
                final String columnName = c.getColumnName(i);

                sb.append(columnName);
                sb.append(SharedStrings.TAB_C);
                sb.append(c.getString(c.getColumnIndex(columnName)));
                sb.append(SharedStrings.NEW_LINE_C);
            }
            sb.append(" > > >\n \n");
        }

        toLog(sb);
    }

    public static void toLog(ContentProviderResult[] results) {
        for (ContentProviderResult result : results) {
            toLog(result);
        }
    }

    public static void toLog(ContentProviderResult result) {
        final StringBuilder sb = new StringBuilder();

        sb.append("uri");
        sb.append(result.uri);
        sb.append(SharedStrings.NEW_LINE_C);
        sb.append("count");
        sb.append(result.count);

        toLog(sb);
    }

    public static void hideInput(Context context, View v) {
        final InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static void hideInput(View v) {
        hideInput(v.getContext(), v);
    }

    public static void hideInputNew(View v) {
        if (v != null) {
            hideInput(v.getContext(), v);
        }
    }

    public static void showInput(Context context, View v) {
        final InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(v, 0);
    }

    public static void showInput(View v) {
        showInput(v.getContext(), v);
    }

    public static void clearStringBuilder(StringBuilder sb) {
        if (sb.length() > 0) {
            sb.delete(0, sb.length());
        }
    }

    public static long getCurrentTimeWithSavings() {
        final TimeZone tz = Calendar.getInstance().getTimeZone();
        return System.currentTimeMillis() + tz.getRawOffset() + tz.getDSTSavings();
    }

    /**
     * РљР»Р°СЃСЃ РґР»СЏ СЂР°Р±РѕС‚С‹ СЃРѕ СЃРїРёСЃРєРѕРј РјРµС‚РѕРє Р·Р°РґР°С‡Рё
     * 
     * @author Tregub Artem tregub.artem@gmail.com
     * */
    public static class JsonTaskLabelsUtils {

        public static String convertListLabelHashCodesToString(List<Integer> labelHashCodes) {
            final JsonArray ja = new JsonArray();
            for (Integer i : labelHashCodes) {
                ja.add(new JsonPrimitive(i));
            }

            return ja.toString();
        }

        public static List<Integer> convertStringToListLabel(String string) {
            if (!TextUtils.isEmpty(string)) {
                final JsonElement je = new JsonParser().parse(string);

                if (je.isJsonArray()) {
                    final JsonArray ja = je.getAsJsonArray();

                    final List<Integer> labels = new ArrayList<>(ja.size());
                    final Iterator<JsonElement> iterator = ja.iterator();
                    while (iterator.hasNext()) {
                        labels.add(iterator.next().getAsInt());
                    }

                    return labels;
                }
            }

            return new ArrayList<>(0);
        }
    }

    public static void changeLocale(Resources res, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }

        final Configuration configuration = res.getConfiguration();
        configuration.locale = locale;

        res.updateConfiguration(configuration, res.getDisplayMetrics());
    }

    public static String getColor(int color) {
        return String.format(SharedStrings.FORMAT_COLOR_STRING, 0xFFFFFF & color);
    }

    public static String getFilePathFromUri(Context context, Uri uri) throws Exception {
        final String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            cursor.moveToFirst();

            return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static boolean isLandOrientation(Context context) {
        final Point display = new Point();
        {
            final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getSize(display);
        }

        return display.x > display.y;
    }

    public static int getDisplayWidth(Context context) {
        final Point display = new Point();
        {
            final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getSize(display);
        }

        return display.x;
    }

    public static void setBlackWhiteTheme(View v) {
        final LTSettings settings = LTSettings.getInstance(v.getContext());

        if (v instanceof TextView) {
            ((TextView) v).setTextColor(settings.isThemeDark() ? Color.WHITE : Color.BLACK);

        } else if (v instanceof PagerTabStrip) {
            final int colorText = settings.isThemeDark() ? Color.WHITE : Color.BLACK;
            final PagerTabStrip strip = (PagerTabStrip) v;

            strip.setTextColor(colorText);
            strip.setTabIndicatorColor(colorText);

        } else {
            v.setBackgroundColor(settings.isThemeDark() ? Color.BLACK : Color.WHITE);
        }
    }

    public static void requestSelection(EditText editText) {
        editText.setSelection(editText.length());
        editText.requestFocus();
    }

    public static <T> boolean equals(T first, T second) {
        return first == second || (first != null && second != null && first.equals(second));
    }

    public static boolean changeVisibility(View v, int visibility) {
    	if (v!=null)
    	{
	        final boolean visibilityChanged = v.getVisibility() != visibility;
	        if (visibilityChanged) {
	            v.setVisibility(visibility);
	        }
	        return visibilityChanged;
    	}
    	else
    	{
    		return false;
    	}
    }

    private static final Calendar CALENDAR = Calendar.getInstance();

    public static boolean isToday(long date) {
        CALENDAR.setTimeInMillis(date);

        final int year = CALENDAR.get(Calendar.YEAR);
        final int month = CALENDAR.get(Calendar.MONTH);
        final int dayOfMonth = CALENDAR.get(Calendar.DAY_OF_MONTH);

        CALENDAR.setTimeInMillis(System.currentTimeMillis());

        return year == CALENDAR.get(Calendar.YEAR) && //
                month == CALENDAR.get(Calendar.MONTH) && //
                dayOfMonth == CALENDAR.get(Calendar.DAY_OF_MONTH);
    }

    public static String getRealPathFromURI(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static List<UUID> convertStringToUUIDs(String... uids) {
        final List<UUID> uuids = new ArrayList<>(uids.length);

        for (String uid : uids) {
            try {
                uuids.add(UUID.fromString(uid));

            } catch (Exception e) {}
        }

        return uuids;
    }

    public static JsonElement getJeFromJo(JsonObject jo, String key) {
        return jo.has(key) ? jo.get(key) : null;
    }

    public static String getStringFromJo(JsonObject jo, String key) {
        final JsonElement je = getJeFromJo(jo, key);

        if (je != null && !je.isJsonNull()) {
            return je.getAsString();
        }
        return null;
    }

    public static long currentMemoryUsage() {
        final Runtime runtime = Runtime.getRuntime();
        return runtime.maxMemory() - runtime.freeMemory();
    }

    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static String[] toArray(String s) {
        return s == null ? EMPTY_STRING_ARRAY : new String[] { s };
    }

    public static <T extends CursorFiller> ContentProviderOperation.Builder getIncertOperation(T value) {
        return ContentProviderOperation.newInsert(value.getContentUri()).//
                withValues(value.getContentValues(null));
    }

    public static <T extends CursorFiller> ContentValues[] getContentValues(Collection<T> values) {
        final ContentValues[] cvs = new ContentValues[values.size()];
        int count = 0;

        for (T value : values) {
            cvs[count++] = value.getContentValues(null);
        }

        return cvs;
    }

    public static ContentValues[] getArray(Collection<ContentValues> values) {
        final ContentValues[] cvs = new ContentValues[values.size()];
        int count = 0;

        for (ContentValues value : values) {
            cvs[count++] = value;
        }

        return cvs;
    }

    public static <T> List<T> returnNotNull(List<T> values) {
        return values == null ? new ArrayList<T>(0) : values;
    }

    public static int getLeaderTaskLauncherResource()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(TimeHelper.currentTimeMillisWithoutTimeZone());
        int dateMonth = calendar.get(Calendar.MONTH)+1;
        int dateMonthDay = calendar.get(Calendar.DAY_OF_MONTH);

        if(dateMonth == 12 && dateMonthDay >= 25 || dateMonth == 1 && dateMonthDay <= 7)
        {
            return R.drawable.icon_lt_newyear;
        }
        else if (dateMonth == 10 && dateMonthDay == 30 || dateMonth == 10 && dateMonthDay == 31)
        {
            return R.drawable.icon_lt_haloween;
        }
        else if (dateMonth == 4 && (dateMonthDay == 11 || dateMonthDay == 12)) {
            return R.drawable.icon_lt_cosmonautic;
        }
        else if (dateMonth == 5 && (dateMonthDay == 7 || dateMonthDay == 8 || dateMonthDay == 9)){
            return R.drawable.icon_lt_9may;
        }
        return R.drawable.icon_lt;
    }

    public static RoundedBitmapDrawable getBitmapFromFolder(final LTApplication mApp, String fileName) {
        try {
            final File cacheImgFile = new File(mApp.getAppFolder() + "/cache_" + fileName);
            if (cacheImgFile.exists()) { // если есть уменьшенная закешированная фотка
                return getBitmap(BitmapFactory.decodeFile(cacheImgFile.getAbsolutePath()), mApp);
            } else {
                // сделать уменьшенную копию файла из обычной
                final File imgFile = new File(mApp.getAppFolder() + "/" + fileName);
                if (imgFile.exists()) { // если есть ОБЫЧНОЕ ФОТО
                    try {
                        // делаем уменьшенную закешированную копию
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // сделать закешированную фотку (шапку на всю ширину и на длину в 0,534)
                                createCachedFile(imgFile, cacheImgFile.getAbsolutePath());
                            }
                        }).start();
                        return getBitmap(customDecodeFilePreview(imgFile), mApp);
                    } catch (Exception e) {
                        // если не получилось сделать уменьшенную копию и вызвать ее - берем обычную фотку
                        return getBitmap(customDecodeFilePreview(imgFile), mApp);
                    }
                }
                else {
                    return null;
                }
            }
        }
        catch (Exception e) {
            return null;
        }

    }

    public static void exifRotate(String photoPath) {
        boolean needRotate = false;

        try {
            File imgFile = new File(photoPath);
            //
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(photoPath, options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            //
            Bitmap bitmap = customDecodeFile(imgFile, imageWidth, imageHeight);
            Bitmap rotatedBitmap = null;
            try {
                ExifInterface ei = new ExifInterface(photoPath);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                switch (orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(bitmap, 90);
                        needRotate = true;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(bitmap, 180);
                        needRotate = true;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(bitmap, 270);
                        needRotate = true;
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = bitmap;
                }
            } catch (Exception e) {

            } finally {
                if (needRotate && rotatedBitmap != null) {
                    try {
                        OutputStream os = null;
                        try {
                            imgFile.delete();
                            File file = new File(photoPath);
                            file.createNewFile();
                            os = new BufferedOutputStream(new FileOutputStream(file));
                            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                        } catch (Exception e) {

                        } finally {
                            try {
                                os.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static RoundedBitmapDrawable getFullFotoBitmapFromFolder(final LTApplication mApp, String fileName) {
        try {
            final File imgFile = new File(mApp.getAppFolder() + "/" + fileName);
            if (imgFile.exists()) {
                return getCircleBitmap(customDecodeFile(imgFile, 400, 400), mApp);
            }
            else {
                return null;
            }
        }
        catch (Exception e) {
            return null;
        }
    }
        //

    public static RoundedBitmapDrawable getFotoFromFolder(final LTApplication mApp, String fileName) {
        //
        try {
            final File imgFile = new File(mApp.getAppFolder() + "/" + fileName);
            if (imgFile.exists()) {
                return getCircleBitmap(customDecodeFile(imgFile, 400, 400), mApp);
            }
            else {
                return null;
            }
        }
        catch (Exception e) {
            return null;
        }
    }

    public static RoundedBitmapDrawable getFotoBitmapFromFolder(final LTApplication mApp, String fileName) {
        //
        try {
            final File cacheImgFile = new File(mApp.getAppFolder() + "/cache_" + fileName);
            if (cacheImgFile.exists()) { // если есть уменьшенная закешированная фотка
                return getCircleBitmap(BitmapFactory.decodeFile(cacheImgFile.getAbsolutePath()), mApp);
            } else {
                // сделать уменьшенную и КВАДРАТНУЮ копию файла из обычной
                final File imgFile = new File(mApp.getAppFolder() + "/" + fileName);
                if (imgFile.exists()) { // если есть ОБЫЧНОЕ ФОТО
                    try {
                        // делаем уменьшенную закешированную копию
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                createCachedPhotoFile(imgFile, cacheImgFile.getAbsolutePath(), 200, 200);
                            }
                        }).start();
                        return getCircleBitmap(customDecodeFile(imgFile, 200, 200), mApp);
                    } catch (Exception e) {
                        // если не получилось сделать уменьшенную копию и вызвать ее - берем обычную фотку
                        return getCircleBitmap(customDecodeFile(imgFile, 200, 200), mApp);
                    }
                }
                else {
                    return null;
                }
            }
        }
        catch (Exception e) {
            return null;
        }
    }

    private static void createCachedPhotoFile(File mainFile, String pathCachedPhoto, int w, int h) {
        Bitmap bitmap = customDecodeFile(mainFile, w, h);
        final Bitmap rectBitmap;
        if (bitmap != null) {
            if (bitmap.getWidth() >= bitmap.getHeight()) {

                rectBitmap = Bitmap.createBitmap(
                        bitmap,
                        bitmap.getWidth() / 2 - bitmap.getHeight() / 2,
                        0,
                        bitmap.getHeight(),
                        bitmap.getHeight()
                );

            } else {

                rectBitmap = Bitmap.createBitmap(
                        bitmap,
                        0,
                        bitmap.getHeight() / 2 - bitmap.getWidth() / 2,
                        bitmap.getWidth(),
                        bitmap.getWidth()
                );
            }
            bitmap = null;

            if (rectBitmap != null) {
                if (rectBitmap.getWidth() > 100) {
                    Bitmap output = Bitmap.createScaledBitmap(rectBitmap, 100, 100, false);
                    OutputStream os = null;
                    try {
                        File file = new File(pathCachedPhoto);
                        file.createNewFile();
                        os = new BufferedOutputStream(new FileOutputStream(file));
                        output.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    } catch (Exception e) {

                    } finally {
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private static void createCachedFile(File mainFile, String pathCachedPhoto) {
        OutputStream os = null;
        try {

            Bitmap bitmap = customDecodeFilePreview(mainFile);
            final Bitmap rectBitmap;
            Double d = bitmap.getWidth()/2.63;
            rectBitmap = Bitmap.createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.getWidth(),
                    d.intValue()
            );

            bitmap = null;

            File file = new File(pathCachedPhoto);
            file.createNewFile();
            os = new BufferedOutputStream(new FileOutputStream(file));
            rectBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        } catch (Exception e) {

        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*public static void sendListToWear(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        //Log.d(TAG, "onConnected: " + connectionHint);
                        // Now you can use the data layer API
                        connect();
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        //Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        //Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();

        //mGoogleApiClient.disconnect();
    }

    private static void connect() {
        String [] contents = new String[]{"data1", "data2", "data3"};
        PutDataMapRequest dataMap = PutDataMapRequest.create ("/myapp/myevent");
        dataMap.getDataMap().putStringArray("contents", contents);

        PutDataRequest request = dataMap.asPutDataRequest();

        DataApi.DataItemResult dataItemResult = Wearable.DataApi
                .putDataItem(mGoogleApiClient, request).await();


        //Log.d ("[DEBUG] SendDataCoolTask - doInBackground", "/myapp/myevent" status, "+getStatus());
    }*/

    private static RoundedBitmapDrawable getBitmap(Bitmap bitmap, Context context) {
        final Bitmap rectBitmap;
        Double d = bitmap.getWidth()/2.63;
        Double a = bitmap.getWidth()*0.01;
        rectBitmap = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.getWidth(),
                d.intValue()
        );

        bitmap = null;

        Bitmap output = rectBitmap.copy(Bitmap.Config.RGB_565, true);

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), output);
        //setting radius
        roundedBitmapDrawable.setCornerRadius(a.intValue());
        roundedBitmapDrawable.setAntiAlias(true);
        //

        return roundedBitmapDrawable;
    }

    private static RoundedBitmapDrawable getCircleBitmap(Bitmap bitmap, Context context) {
        final Bitmap rectBitmap;
        if (bitmap.getWidth() >= bitmap.getHeight()){

            rectBitmap = Bitmap.createBitmap(
                    bitmap,
                    bitmap.getWidth()/2 - bitmap.getHeight()/2,
                    0,
                    bitmap.getHeight(),
                    bitmap.getHeight()
            );

        }else{

            rectBitmap = Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.getHeight()/2 - bitmap.getWidth()/2,
                    bitmap.getWidth(),
                    bitmap.getWidth()
            );
        }
        bitmap = null;

        Bitmap output = rectBitmap.copy(Bitmap.Config.RGB_565, true);
        //get bitmap of the image
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), output);
        //setting radius
        roundedBitmapDrawable.setCornerRadius(Math.max(output.getWidth(), output.getHeight()) / 2.0f);
        roundedBitmapDrawable.setAntiAlias(true);
        //

        return roundedBitmapDrawable;
    }

    //ФИКСАЦИЯ ДЛЯ ГУГЛ АНАЛИТИКИ ЭКРАНА
    public static void fixActivityForAnalytics(final Context context, final String activity) {
        Thread t = new Thread(){
            public void run(){
                if(!startApp) {
                    fixUserIdForAnalytics(context);
                }
                Tracker tracker = getDefaultTracker(context);
                tracker.setScreenName(activity);
                tracker.send(new HitBuilders.ScreenViewBuilder().build());
            }
        };
        t.start();
    }


    //ФИКСАЦИЯ ДЛЯ ГУГЛ АНАЛИТИКИ СОБЫТИЯ
    public static void fixEventForAnalytics(final Context context, final String category, final String action) {
        Thread t = new Thread(){
            public void run(){
                Tracker tracker = getDefaultTracker(context);
                tracker.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).build());
            }
        };
        t.start();
    }

    public static void fixUserIdForAnalytics(final Context context) {
        Thread t = new Thread(){
            public void run(){
                try {
                    Tracker tracker = getDefaultTracker(context);
                    if (tracker != null) {
                        tracker.send(new HitBuilders.AppViewBuilder()
                                .setCustomDimension(1, tracker.get("&cid"))
                                .build());
                        startApp = true;
                    }
                } catch (Exception e) {

                }
            }
        };
        t.start();

    }

    public static void fixUserUIDForAnalytics(final Context context, final String userId) {
                Thread t = new Thread(){
                    public void run(){
                        Tracker tracker = getDefaultTracker(context);
                        tracker.set("&uid", userId);
                        tracker.send(new HitBuilders.AppViewBuilder()
                                        .setCustomDimension(2, tracker.get("&uid"))
                                        .build());
                    }
                };
        t.start();
    }

    private static Tracker getDefaultTracker(Context context) {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            analytics.setLocalDispatchPeriod(1); // 1800 - пол часа
            mTracker = analytics.newTracker(googleAnalyticsId);
        }
        return mTracker;
    }

    public static Bitmap getCategoryDrawable(LTApplication mApp, String categoryColor) {
        try {
            if (categoryColor == null) {
                categoryColor = "#808080";
            }

            File cacheImgFile = new File(mApp.getAppFolder() + "/color_category_cache_" + categoryColor);
            if (cacheImgFile.exists()) { // если есть уменьшенная закешированная фотка
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                return BitmapFactory.decodeFile(cacheImgFile.getAbsolutePath(), options);
            } else {
                // сделать кеш-файл для категории
                Bitmap categoryBitmap = setCategoryBitmapColor(mApp, categoryColor);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                categoryBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

                File f = new File(mApp.getAppFolder() + "/color_category_cache_" + categoryColor);
                f.createNewFile();
                //write the bytes in file
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());

                // remember close de FileOutput
                fo.close();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                return BitmapFactory.decodeFile(cacheImgFile.getAbsolutePath(), options);
                //
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap getColorDrawable(LTApplication mApp, String colorColor) {
        try {
            if (colorColor == null) {
                colorColor = "#808080";
            }

            File cacheImgFile = new File(mApp.getAppFolder() + "/color_color_cache_" + colorColor);
            if (cacheImgFile.exists()) { // если есть уменьшенная закешированная фотка
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                return BitmapFactory.decodeFile(cacheImgFile.getAbsolutePath(), options);
            } else {
                // сделать кеш-файл для категории
                Bitmap colorBitmap = setColorBitmapColor(mApp, colorColor);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                colorBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

                File f = new File(mApp.getAppFolder() + "/color_color_cache_" + colorColor);
                f.createNewFile();
                //write the bytes in file
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());

                // remember close de FileOutput
                fo.close();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                return BitmapFactory.decodeFile(cacheImgFile.getAbsolutePath(), options);
                //
            }
        } catch (Exception e) {
            return null;
        }
    }


    public static String getTextInWork(Context context, int wasInWork, int min, int status) {
        String text = "";
        String inWork = "";
        //if (status == Status.TASK_IN_WORK.getStatusCode()) {
            inWork = Utils.inWork(context, wasInWork);
        //}
        if (min > 0) {
            if (min / 60 < 60) { // если меньше часа
                //устанавливаем минуты
                text = (inWork.isEmpty() ?"" : inWork+"/" )+ Utils.inWork(context, min);
            } else if (min / 60 / 60 < 8) { // если меньше чем 8 часов но больше чем час
                // устанавливаем часы
                text = (inWork.isEmpty() ?"" : inWork+"/" )+ Utils.inWork(context, min);
            } else {
                // устанавливаем дни
                text = (inWork.isEmpty() ?"" : inWork+"/" )+ Utils.inWork(context, min);
            }
        } else {
            text = inWork;
        }
        return text;
    }

    public static String inWork (Context context, int wasInWork) {
        String text = "";
        int ost = wasInWork;
        boolean hasAnysing = false;
        boolean nothingWrote = true;
        if (wasInWork > 0) {
            if (ost / 60 / 60 >= 8) { // если больше 8 часов
                // устанавливаем дни
                int value = ost / 60 / 60 / 8;
                text = text + (nothingWrote ? "" : " ") + value + "" + context.getResources().getString(R.string.chronometry30);
                ost = ost - value * 60 * 60 * 8;
                hasAnysing = true;
                nothingWrote = false;
            }
            if (ost / 60 / 60 < 8 && ost / 60 / 60 >= 1) { // если меньше чем 8 часов но больше чем час
                // устанавливаем часы
                int value = ost / 60 / 60;

                text = text + (nothingWrote ? "" : " ") + value + "" + context.getResources().getString(R.string.chronometry20);
                ost = ost - value * 60 * 60;
                hasAnysing = true;
                nothingWrote = false;
            }
            if (ost / 60 < 60 && ost >= 60) { // если меньше часа
                //устанавливаем минуты
                int value = ost / 60;

                text = text + (nothingWrote ? "" : " ") + value + "" + context.getResources().getString(R.string.chronometry10);
                ost = ost - value * 60;
                hasAnysing = true;
                nothingWrote = false;
            }
            if (ost < 60 ) { // если меньше минуты
                //устанавливаем секунды
                if (!hasAnysing) {
                    int value = ost;
                    text = text + (nothingWrote ? "" : " ") + value + "" + context.getResources().getString(R.string.chronometry00);
                }
            }
        } else {
            text = "0" /*+ context.getResources().getString(R.string.chronometry00)*/;
        }
        return text;
    }


    //вывод хронометража в свойствах задачи
    public static String getTextInWorkEdit(Context context, int wasInWork, int min, int status) {
        String text = "";
        String inWork = "";
        //if (status == Status.TASK_IN_WORK.getStatusCode()) {
            inWork = Utils.inWorkEdit(context, wasInWork);
        //}
        if (min > 0) {
            if (min / 60 < 60) { // если меньше часа
                //устанавливаем минуты
                text =  inWork+" / " + Utils.inWorkEdit(context, min);
            } else if (min / 60 / 60 < 8) { // если меньше чем 8 часов но больше чем час
                // устанавливаем часы
                text = inWork+" / " + Utils.inWorkEdit(context, min);
            } else {
                // устанавливаем дни
                text =  inWork+" / " + Utils.inWorkEdit(context, min);
            }
        } else {
            text = inWork;
        }
        return text;
    }

    public static String inWorkEdit (Context context, int wasInWork) {
        String text = "";
        int ost = wasInWork;
        boolean hasAnysing = false;
        boolean nothingWrote = true;
        if (wasInWork > 0) {
            if (ost / 60 / 60 >= 8) { // если больше 8 часов
                // устанавливаем дни
                int value = ost / 60 / 60 / 8;
                text = text + (nothingWrote ? "" : " ") + value + "" + context.getResources().getString(R.string.chronometry30);
                ost = ost - value * 60 * 60 * 8;
                hasAnysing = true;
                nothingWrote = false;
            }
            if (ost / 60 / 60 < 8 && ost / 60 / 60 >= 1) { // если меньше чем 8 часов но больше чем час
                // устанавливаем часы
                int value = ost / 60 / 60;

                text = text + (nothingWrote ? "" : " ") + value + "" + context.getResources().getString(R.string.chronometry20);
                ost = ost - value * 60 * 60;
                hasAnysing = true;
                nothingWrote = false;
            }
            if (ost / 60 < 60 && ost >= 60) { // если меньше часа
                //устанавливаем минуты
                int value = ost / 60;

                text = text + (nothingWrote ? "" : " ") + value + "" + context.getResources().getString(R.string.chronometry10);
                ost = ost - value * 60;
                hasAnysing = true;
                nothingWrote = false;
            }
            if (ost < 60 ) { // если меньше минуты
                //устанавливаем секунды
                if (!hasAnysing) {
                    int value = ost;
                    text = text + (nothingWrote ? "" : " ") + value + "" + context.getResources().getString(R.string.chronometry00);
                }
            }
        } else {
            text = "0 ";
        }
        return text;
    }

    public static String inWorkDialog (Context context, int wasInWork) {
        String textD = "";
        String textH = "";
        String textM = "";
        String textS = "";
        int ost = wasInWork;
        boolean hasAnysing = false;
        if (wasInWork > 0) {
            if (ost / 60 / 60 >= 8) { // если больше 8 часов
                // устанавливаем дни
                int value = ost / 60 / 60 / 8;
                textD = value + " " + context.getResources().getString(R.string.chronometry3);
                ost = ost - value * 60 * 60 * 8;
                hasAnysing = true;
            } else {
                textD = "0 "+ context.getResources().getString(R.string.chronometry3);
            }
            if (ost / 60 / 60 < 8 && ost / 60 / 60 >= 1) { // если меньше чем 8 часов но больше чем час
                // устанавливаем часы
                int value = ost / 60 / 60;

                textH = value + " " + context.getResources().getString(R.string.chronometry2);
                ost = ost - value * 60 * 60;
                hasAnysing = true;
            } else {
                textH = "0 "+ context.getResources().getString(R.string.chronometry2);
            }
            if (ost / 60 < 60 && ost >= 60) { // если меньше часа
                //устанавливаем минуты
                int value = ost / 60;

                textM = value + " " + context.getResources().getString(R.string.chronometry1);
                ost = ost - value * 60;
                hasAnysing = true;
            } else {
                textM = "0 "+ context.getResources().getString(R.string.chronometry1);
            }
            if (ost < 60 ) { // если меньше минуты
                //устанавливаем секунды
                if (!hasAnysing) {
                    int value = ost;
                    textS = value + " " + context.getResources().getString(R.string.chronometry4);
                }
            }
        }
        return textD+"  "+textH+"  "+textM+"  "+textS;
    }

    public static String escapeCharacter( String str ) {
        String[] onReadableCharacter  = { "&gt;", "&lt;", "&amp;", "&quot;", "&apos;" };
        String[] escapeCharacters  = {">", "<", "&", "\"\"", "'"};
        for (int i = 0; i < escapeCharacters.length; i++) {
            str = str.replace(escapeCharacters[i], onReadableCharacter[i]);
        } return str;
    }

    public static void startSync(final LTApplication mApp) {
        if (!LTSettings.getInstance().isAutonomyMode()) {
            if (!mApp.isSync()) {
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            final LTSettings settings = LTSettings.getInstance();
                            if (settings.getUserProfile().isValid()) {
                                mApp.setSyncingOngoingNow(true);
                                new SynchronizationTask(mApp, settings.getUserProfile()).run();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            } else {
                LeaderTaskSyncService.mIsNeedToResync = true;
            }
        } else {
            LeaderTaskSyncService.syncWear();
        }
        //
    }

    public static void startSyncAlways(final LTApplication mApp) {
        //
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                try {
                    final LTSettings settings = LTSettings.getInstance();
                    if (settings.getUserProfile().isValid()) {
                        mApp.setSyncingOngoingNow(true);
                        new SynchronizationTask(mApp, settings.getUserProfile()).run();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    });

            thread.start();
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static List <String> showNewAssignedTaskForMe(Context context, List <LTask> tasks) {
        List <String> tasksNeedNotify = new ArrayList<>();
        for (LTask task : tasks) {
            if (task.getStatus() != 1 && task.getStatus() != 7 && task.getStatus() != 8) { // Если задача не завершена
                boolean isNewTask = true;
                //
                //ищем задачу
                Cursor cursorTask = null;
                try {
                    cursorTask = context.getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null, LeaderTaskProviderMetaData.SelectionKeeper.equals(new StringBuilder(), LionMetaData.LTaskContract.Uid, task.getUid()), null, null);
                    if (cursorTask.getCount() > 0) {
                        isNewTask = false;
                    }
                } finally {
                    if (cursorTask != null) {
                        cursorTask.close();
                    }
                }

                if (isNewTask) {
                    if (task.getStatus() != 5 && task.getEmailPerformer().equals(LTSettings.getInstance().getUserName()) &&
                            !task.getEmailCustomer().equals(LTSettings.getInstance().getUserName())) {
                        if (!task.getReaded()) {
                            // если задача непрочитана ранее
                            tasksNeedNotify.add(task.getUid());
                        }
                    }
                }
            }
        }
        return tasksNeedNotify;
    }

    public static List <String> showCancelledTaskByMe(Context context, List <LTask> tasks) {
        List <String> tasksNeedNotify = new ArrayList<>();
        for (LTask task : tasks) {
            if (task.getStatus() != 1 && task.getStatus() != 7 && task.getStatus() != 8) { // Если задача не завершена
                boolean isNewTask = true;
                //
                //ищем задачу
                Cursor cursorTask = null;
                try {
                    cursorTask = context.getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null, LeaderTaskProviderMetaData.SelectionKeeper.equals(new StringBuilder(), LionMetaData.LTaskContract.Uid, task.getUid()), null, null);
                    if (cursorTask.getCount() > 0) {
                        isNewTask = false;
                    }
                } finally {
                    if (cursorTask != null) {
                        cursorTask.close();
                    }
                }

                if (!isNewTask) {
                    if (task.getStatus() == 5 && task.getEmailCustomer().equals(LTSettings.getInstance().getUserName()) &&
                            !task.getEmailPerformer().equals(LTSettings.getInstance().getUserName())) {
                        if (!task.getReaded()) {
                            // если задача непрочитана ранее
                            tasksNeedNotify.add(task.getUid());
                        }
                    }
                }
            }
        }
        return tasksNeedNotify;
    }

    public static List <String> showNewCommentForTask(Context context, List <TaskMessage> tempMessages) {
        List <String> tasksNeedNotify = new ArrayList<>();
        for (TaskMessage message : tempMessages) {
            if (TimeHelper.getInstance().isToday(message.getDateCreate().getTime())) { // если коммент был сегодня
                boolean isMyComment = message.getCreator().equals(LTSettings.getInstance().getUserName());
                if (!isMyComment) {
                    TaskMessageCache messageCache = TaskMessageCache.getInstance(context);
                    String uidTask = message.getTaskUID().toString().toLowerCase();

                    LTask task = null;
                    //
                    //ищем задачу
                    Cursor cursorTask = null;
                    try {
                        cursorTask = context.getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null, LeaderTaskProviderMetaData.SelectionKeeper.equals(new StringBuilder(), LionMetaData.LTaskContract.Uid, uidTask.toUpperCase()), null, null);
                        if (cursorTask.getCount() > 0) {
                            cursorTask.moveToFirst();
                            task = new LTask(cursorTask);
                        }
                    } finally {
                        if (cursorTask != null) {
                            cursorTask.close();
                        }
                    }
                    //
                    if (task != null) {
                        CompletedTask completedTask = CompletedCache.getInstance(context).find(task.getUid());
                        if (completedTask == null) {
                            // если задача не завершена
                            if (!task.getReaded()) {
                                // если задача непрочитана ранее
                                boolean isFromMeTask = task.getEmailCustomer().equals(LTSettings.getInstance().getUserName());
                                boolean isForMe = task.getEmailPerformer().equals(LTSettings.getInstance().getUserName());
                                if (isFromMeTask || isForMe) {
                                    // если задача поручена мне или порена кому-то от меня - точно выводим напоминание
                                    tasksNeedNotify.add(task.getUid());
                                } else {
                                    LTaskCache.getInstance(context).refreshCache(task);
                                    int uidHash = TaskHelper.getHashFromUid(task.getUid().toLowerCase());
                                    List<TaskMessage> messages = messageCache.find(uidHash);
                                    // если задача меня не касается - проверяем был ли среди други комментов мой
                                    for (TaskMessage msg : messages) {
                                        if (msg.getCreator().equals(LTSettings.getInstance().getUserName())) {
                                            // если был мой коммент
                                            tasksNeedNotify.add(task.getUid());
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
        return tasksNeedNotify;
    }

    public static Bitmap setCategoryBitmapColor(LTApplication mApp, String color){
        Bitmap src = BitmapFactory.decodeResource(mApp.getResources(), R.drawable.category_custom);
        Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(),  src.getConfig());
        int currentColor = Color.parseColor(color);
        int r = Color.red(currentColor);
        int g = Color.green(currentColor);
        int b = Color.blue(currentColor);

        for(int x = 0; x < src.getWidth(); x++){
            for(int y = 0; y < src.getHeight(); y++){
                // получим каждый пиксель
                int pixelColor = src.getPixel(x, y);
                // получим информацию о прозрачности
                int pixelAlpha = Color.alpha(pixelColor);
                // получим цвет каждого пикселя
                int pixelRed = Color.red(pixelColor);
                int pixelGreen = Color.green(pixelColor);
                int pixelBlue = Color.blue(pixelColor);


                // перемешаем цвета
                int newPixel;
                // полученный результат вернём в Bitmap
                if (pixelRed == 0 && pixelGreen == 0 && pixelBlue == 0) {
                    newPixel= Color.argb(pixelAlpha, r, g, b);
                }
                else {
                    newPixel= Color.argb(pixelAlpha, pixelRed, pixelGreen, pixelBlue);
                }
                dest.setPixel(x, y, newPixel);
            }
        }
        return dest;
    }

    public static Bitmap setColorBitmapColor(LTApplication mApp, String color){
        Bitmap src = BitmapFactory.decodeResource(mApp.getResources(), R.drawable.marker_black2);
        Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(),  src.getConfig());
        int currentColor = Color.parseColor(color);
        int r = Color.red(currentColor);
        int g = Color.green(currentColor);
        int b = Color.blue(currentColor);

        for(int x = 0; x < src.getWidth(); x++){
            for(int y = 0; y < src.getHeight(); y++){
                // получим каждый пиксель
                int pixelColor = src.getPixel(x, y);
                // получим информацию о прозрачности
                int pixelAlpha = Color.alpha(pixelColor);
                // получим цвет каждого пикселя
                int pixelRed = Color.red(pixelColor);
                int pixelGreen = Color.green(pixelColor);
                int pixelBlue = Color.blue(pixelColor);


                // перемешаем цвета
                int newPixel;
                // полученный результат вернём в Bitmap
                if (pixelRed == 0 && pixelGreen == 0 && pixelBlue == 0) {
                    newPixel= Color.argb(pixelAlpha, r, g, b);
                }
                else {
                    newPixel= Color.argb(pixelAlpha, pixelRed, pixelGreen, pixelBlue);
                }
                dest.setPixel(x, y, newPixel);
            }
        }
        return dest;
    }

    public static String getUTF8stringFromBase64 (String base64String) {
        byte[] data = Base64.decode(base64String, Base64.DEFAULT);
        String utf8Text ="";
        try {
            utf8Text = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return utf8Text;
    }

    public static void timeChecker(String pointName) {
        if (IPCConstants.DEBUG) {
            ArrayList<String> allTimersNames = LTSettings.getInstance().getAllTimersNames();
            ArrayList<String> allTimers = LTSettings.getInstance().getAllTimers();
            if (allTimersNames.contains(pointName)) {
                int index = allTimersNames.indexOf(pointName);
                // запись в логер
                android.util.Log.v("Tedorius", pointName + " - " + (System.currentTimeMillis() - Long.decode(allTimers.get(index))) / 1000.0 + " sec");
                allTimersNames.remove(index);
                allTimers.remove(index);
            } else {
                allTimersNames.add(pointName);
                allTimers.add("" + System.currentTimeMillis());
            }
        }
    }

    public static Bitmap customDecodeFile(File f,int WIDTH,int HIGHT){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //The new size we want to scale to
            final int REQUIRED_WIDTH=WIDTH;
            final int REQUIRED_HIGHT=HIGHT;
            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_WIDTH && o.outHeight/scale/2>=REQUIRED_HIGHT)
                scale*=2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }

    public static Bitmap customDecodeFilePreview(File f){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //The new size we want to scale to
            int REQUIRED_WIDTH=o.outWidth;
            if (REQUIRED_WIDTH > 700) {
                REQUIRED_WIDTH = 700;
            }
            int scale = 1;
            if(o.outWidth > REQUIRED_WIDTH) {
                scale = o.outWidth/REQUIRED_WIDTH;
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }

    public static void playAudio(Context context, final int type){
        if (LTSettings.getInstance().isSoundEnabled()) { // если звук включен

            final LTApplication mApp = (LTApplication) context.getApplicationContext();
            final AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String fileName = "";
                    switch (type) {
                        case 0:
                            fileName = "start";
                            break;
                        case 1:
                            fileName = "taskcompleted";
                            break;
                        default:
                            break;
                    }

                    int resID = mApp.getResources().getIdentifier(fileName, "raw", mApp.getPackageName());
                    mediaPlayer = MediaPlayer.create(mApp, resID);

                    switch (am.getRingerMode()) {
                        case AudioManager.RINGER_MODE_VIBRATE:
                        case AudioManager.RINGER_MODE_SILENT:
                            mediaPlayer.setVolume(0, 0);
                            break;
                        default:
                            mediaPlayer.setVolume(1.0f, 1.0f);
                            break;
                    }

                    mediaPlayer.start();
                }
            }).run();
        }
    }

    public static void openBrowserToBuy(final LTSettings mSettings, final Activity activity) {
        if (Utils.isNetworkAvailable(activity)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String lang = "";
                        final boolean hasCustomLocale = mSettings.getLanguageLocale() != null;
                        List<Locale> mLocales = new ArrayList<>(LeaderTaskLanguage.values().length + 1);

                        mLocales.add(Locale.getDefault());
                        for (LeaderTaskLanguage l : LeaderTaskLanguage.values()) {
                            mLocales.add(l.getLocale());
                        }

                        final Locale appLocale = hasCustomLocale ? mSettings.getLanguageLocale() : Locale.getDefault();
                        final String[] strings = new String[mLocales.size()];
                        Locale locale;

                        for (int i = 1; i < strings.length; i++) {
                            locale = mLocales.get(i);
                            if (appLocale.getLanguage().equals(locale.getLanguage())) {
                                lang = appLocale.getLanguage();
                            }
                        }
                        //
                        try {
                            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                            nameValuePairs.add(new BasicNameValuePair("secret", "e5be3dbb48687d99e3effe2f448603b793589b62d2d49233620feeb345d1b2df"));
                            nameValuePairs.add(new BasicNameValuePair("uuid", mSettings.getVerifyUserId()));
                            nameValuePairs.add(new BasicNameValuePair("type", "buy"));
                            nameValuePairs.add(new BasicNameValuePair("lang", lang));

                            String string = OkHttpConnection.postWithParams(nameValuePairs, "https://www.leadertask.ru/getlink");
                            //
                            final JSONObject jObject = new JSONObject((string.substring(string.indexOf("{"), string.lastIndexOf("}") + 1)));
                            String url = jObject.get("url").toString();
                            //
                            if (url.length() > 0) {
                                // збс
                                final Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(url) /*Uri.parse("http"+url.substring(5, url.length()))*/);
                                activity.startActivity(browser);
                            } else {
                                final Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.link_buy)));
                                activity.startActivity(browser);
                            }
                        } catch (Exception e) {
                            final Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.link_buy)));
                            activity.startActivity(browser);
                        }
                    } catch (Exception e) {
                        final Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.link_buy)));
                        activity.startActivity(browser);
                    }
                }
            }).start();
        } else {
            Toast.makeText(activity, R.string.error_internet_access, Toast.LENGTH_SHORT).show();
        }
    }

    public static void openBrowserToBuyNewUser(final LTSettings mSettings, final Activity activity, final ProgressDialog progressDialog) {
        if (Utils.isNetworkAvailable(activity)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String lang = "";
                        final boolean hasCustomLocale = mSettings.getLanguageLocale() != null;
                        List<Locale> mLocales = new ArrayList<>(LeaderTaskLanguage.values().length + 1);

                        mLocales.add(Locale.getDefault());
                        for (LeaderTaskLanguage l : LeaderTaskLanguage.values()) {
                            mLocales.add(l.getLocale());
                        }

                        final Locale appLocale = hasCustomLocale ? mSettings.getLanguageLocale() : Locale.getDefault();
                        final String[] strings = new String[mLocales.size()];
                        Locale locale;

                        for (int i = 1; i < strings.length; i++) {
                            locale = mLocales.get(i);
                            if (appLocale.getLanguage().equals(locale.getLanguage())) {
                                lang = appLocale.getLanguage();
                            }
                        }
                        //
                        try {
                            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                            nameValuePairs.add(new BasicNameValuePair("secret", "e5be3dbb48687d99e3effe2f448603b793589b62d2d49233620feeb345d1b2df"));
                            nameValuePairs.add(new BasicNameValuePair("uuid", mSettings.getVerifyUserId()));
                            nameValuePairs.add(new BasicNameValuePair("type", "add"));
                            nameValuePairs.add(new BasicNameValuePair("lang", lang));

                            String string = OkHttpConnection.postWithParams(nameValuePairs, "https://www.leadertask.ru/getlink");
                            //
                            final JSONObject jObject = new JSONObject((string.substring(string.indexOf("{"), string.lastIndexOf("}") + 1)));
                            final String url = jObject.get("url").toString();
                            //

                            progressDialog.cancel();
                            if (url.length() > 0) {
                                // збс
                                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                builder.setMessage(activity.getResources().getString(R.string.emp_max));
                                builder.setPositiveButton(activity.getResources().getString(R.string.emp_max_do), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        final Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("http"+url.substring(5, url.length())));
                                        activity.startActivity(browser);
                                    }
                                });
                                builder.setNegativeButton(activity.getResources().getString(R.string.txt_just_no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        builder.show();
                                    }
                                });

                            }
                        } catch (Exception e) {
                            progressDialog.cancel();
                        }
                    } catch (Exception e) {
                        progressDialog.cancel();
                    }
                }
            }).start();
        } else {
            Toast.makeText(activity, R.string.error_internet_access, Toast.LENGTH_SHORT).show();
            progressDialog.cancel();
        }
    }

    public static void iWantToAddUsers(Activity activity, Fragment fragment) {
        LTSettings mSettings = LTSettings.getInstance();
        if (mSettings.getVerifyKey() != "") {
            // если платный
            int EmployeesCount = 0;
            Cursor c = null;
            try {
                c = activity.getContentResolver().query(LeaderTaskProviderMetaData.EmployeeContract.CONTENT_URI,//
                        null, null, null, LeaderTaskProviderMetaData.EmployeeContract.DEFAULT_SORT);
                if (c != null) {
                    EmployeesCount = c.getCount();
                }
            } finally {
                if (c != null) {
                    c.close();
                }
                if (Integer.parseInt(mSettings.getVerifyCount()) <= EmployeesCount) {
                    //количество у него в лицензии пользователей меньше или равно текущим пользователям в организации то предлагать докупить пользователей
                    ProgressDialog dialog = ProgressDialog.show(activity, "",activity.getResources().getString(R.string.data_loading)+"...", true);

                    Utils.openBrowserToBuyNewUser(mSettings, activity, dialog);
                } else {
                    AddEmpDialog.newInstance(fragment).showDialog(fragment.getFragmentManager());
                }
            }
        } else {
            // если в триале
            AddEmpDialog.newInstance(fragment).showDialog(fragment.getFragmentManager());
        }
    }

    private class APIRequest extends AsyncTask<Void, Void, Object> {

        @Override
        protected Object doInBackground(Void... params) {

            // CASE 3: For form-urlencoded parameter
            String url = "http://10.0.2.2/api/token";
            HttpURLConnection urlConnection = null;
            Map<String, String> stringMap = new HashMap<>();
            stringMap.put("grant_type", "password");
            stringMap.put("username", "username");
            stringMap.put("password", "password");
            String requestBody = Utils.buildPostParameters(stringMap);
            try {
                urlConnection = (HttpURLConnection) Utils.makeRequest("POST", url, null, "application/x-www-form-urlencoded", requestBody);
                JSONObject jsonObject = new JSONObject();
                try {
                    if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        //...
                    } else {
                        //...
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return jsonObject;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object response) {
            super.onPostExecute(response);
            if (response instanceof String) {
                //...
            } else if (response instanceof JSONObject) {
                //...
            } else {
                //...
            }
        }
    }

    public static String buildPostParameters(Object content) {
        String output = null;
        if ((content instanceof String) ||
                (content instanceof JSONObject) ||
                (content instanceof JSONArray)) {
            output = content.toString();
        } else if (content instanceof Map) {
            Uri.Builder builder = new Uri.Builder();
            HashMap hashMap = (HashMap) content;
            if (hashMap != null) {
                Iterator entries = hashMap.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry entry = (Map.Entry) entries.next();
                    builder.appendQueryParameter(entry.getKey().toString(), entry.getValue().toString());
                    entries.remove(); // avoids a ConcurrentModificationException
                }
                output = builder.build().getEncodedQuery();
            }
        }

        return output;
    }

    public static URLConnection makeRequest(String method, String apiAddress, String accessToken, String mimeType, String requestBody) throws IOException {
        URL url = new URL(apiAddress);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(!method.equals("GET"));
        urlConnection.setRequestMethod(method);

        urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);

        urlConnection.setRequestProperty("Content-Type", mimeType);
        OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
        writer.write(requestBody);
        writer.flush();
        writer.close();
        outputStream.close();

        urlConnection.connect();

        return urlConnection;
    }

    public static class checkIsDateChanged extends AsyncTask<Void, Void, Void> {
        Context mContext;

        public checkIsDateChanged (Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            final String FILE_NAME = "syncFull.log";

            try {
                long lastDate = LTSettings.getInstance().getLastDay();
                if (lastDate == 0) {
                    // первый раз
                    LTApplication mApp = (LTApplication) mContext.getApplicationContext();

                    File mFile = new File(mApp.getAppFolderLogs(), FILE_NAME);
                    mFile.delete();
                    mFile = new File(mApp.getAppFolderLogs(), FILE_NAME);
                    StringBuilder mStringBuilder = new StringBuilder();
                    //
                    final long dayStart;
                    final Calendar calendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
                    calendar.setTimeInMillis(TimeHelper.currentTimeMillisWithoutTimeZone());

                    TimeHelper.roundCalendar(calendar, true);
                    dayStart = calendar.getTimeInMillis();
                    LTSettings.getInstance().setLastDay(dayStart);
                    //

                    mStringBuilder.append(Task.SDF.format(new Date(dayStart)));
                    byte[] bytes = mStringBuilder.toString().getBytes();
                    OutputStream mOutputStream = null;
                    try {
                        mOutputStream = new FileOutputStream(mFile, true);
                        mOutputStream.write(bytes);
                        mOutputStream.close();
                        mOutputStream = null;

                    } catch (Exception ex) {

                    } finally {

                    }
                } else {
                    // уже записывали, проверяем
                    final long dayStart;
                    final Calendar calendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
                    calendar.setTimeInMillis(TimeHelper.currentTimeMillisWithoutTimeZone());

                    TimeHelper.roundCalendar(calendar, true);
                    dayStart = calendar.getTimeInMillis();
                    if (dayStart - LTSettings.getInstance().getLastDay() > 0) {
                        LTApplication mApp = (LTApplication) mContext.getApplicationContext();

                        File mFile = new File(mApp.getAppFolderLogs(), FILE_NAME);
                        mFile.delete();
                        mFile = new File(mApp.getAppFolderLogs(), FILE_NAME);
                        StringBuilder mStringBuilder = new StringBuilder();
                        //
                        LTSettings.getInstance().setLastDay(dayStart);
                        //

                        mStringBuilder.append(Task.SDF.format(new Date(dayStart)));
                        byte[] bytes = mStringBuilder.toString().getBytes();
                        OutputStream mOutputStream = null;
                        try {
                            mOutputStream = new FileOutputStream(mFile, true);
                            mOutputStream.write(bytes);
                            mOutputStream.close();
                            mOutputStream = null;

                        } catch (Exception ex) {

                        } finally {

                        }
                    }
                }
            } catch(Exception e) {}

            return null;
        }
    }

    public static void writeToFullLog(Throwable e, Context context) {
        final String FILE_NAME = "syncFull.log";
        LTApplication mApp = (LTApplication) context.getApplicationContext();

        File mFile = new File(mApp.getAppFolderLogs(), FILE_NAME);
        StringBuilder mStringBuilder = new StringBuilder();

        mStringBuilder.append(e.getMessage());
        mStringBuilder.append(SharedStrings.NEW_LINE_C);
        mStringBuilder.append(e.getClass().getName());
        for (StackTraceElement s : e.getStackTrace()) {
            mStringBuilder.append(SharedStrings.NEW_LINE_C);
            mStringBuilder.append(s.toString());
        }
        mStringBuilder.append(SharedStrings.NEW_LINE_C);
        byte[] bytes = mStringBuilder.toString().getBytes();
        OutputStream mOutputStream = null;
        try {
            mOutputStream = new FileOutputStream(mFile, true);
            mOutputStream.write(bytes);
            mOutputStream.close();
            mOutputStream = null;

        } catch (Exception ex) {

        } finally {

        }
    }

    public static void writeToFullLog(String text, Context context) {
        final String FILE_NAME = "syncFull.log";
        LTApplication mApp = (LTApplication) context.getApplicationContext();

        File mFile = new File(mApp.getAppFolderLogs(), FILE_NAME);
        StringBuilder mStringBuilder = new StringBuilder();

        mStringBuilder.append(text);
        mStringBuilder.append(SharedStrings.NEW_LINE_C);
        byte[] bytes = mStringBuilder.toString().getBytes();
        OutputStream mOutputStream = null;
        try {
            mOutputStream = new FileOutputStream(mFile, true);
            mOutputStream.write(bytes);
            mOutputStream.close();
            mOutputStream = null;

        } catch (Exception ex) {

        } finally {

        }
    }

    public static void parsingTaskName(LTask task, Context context) {
        //ПАРСИНГ
        String taskName = task.getName().toLowerCase();
        //поручить
        String assignTo = Utils.parsingTaskNameAssign(taskName, context);
        if (assignTo != null) {
            task.setEmailPerformer(assignTo);
        }

        //ближайшая дата(сегодня,завтра,послезавтра)
        long parceTermNearDay = Utils.parsingTaskNameDateToday(taskName, context);
        if (parceTermNearDay != 0) {
            long parseTime = parsingTaskNameTime(taskName, context);
            if (parseTime != 0) {
                // если просто завтра, сегодня со временем
                task.setTermBegin(setTimeTo(parceTermNearDay, true) + parseTime);
                task.setTermEnd(setTimeTo(parceTermNearDay, true) + parseTime);
            } else {
                // если просто завтра, сегодня
                task.setTermBegin(setTimeTo(parceTermNearDay, true));
                task.setTermEnd(setTimeTo(parceTermNearDay, false));
            }

        } else {
            //если ничего нет на ближайшую дату
            long parceTermWeek = Utils.parsingTaskNameDateWeek(taskName, context);
            if (parceTermWeek != 0) {
                //проверка на дни недели
                long parseTime = parsingTaskNameTime(taskName, context);
                if (parseTime != 0) {
                    // если день недели со временем
                    task.setTermBegin(setTimeTo(parceTermWeek, true) + parseTime);
                    task.setTermEnd(setTimeTo(parceTermWeek, true) + parseTime);
                } else {
                    // если просто день недели
                    task.setTermBegin(setTimeTo(parceTermWeek, true));
                    task.setTermEnd(setTimeTo(parceTermWeek, false));
                }

            } else {
                long parceTermMonth= Utils.parsingTaskNameDateMonth(taskName, context);
                if (parceTermMonth != 0) {
                    //проверка на месяцы
                    long parseTime = parsingTaskNameTime(taskName, context);
                    if (parseTime != 0) {
                        // если месфц со временем
                        task.setTermBegin(setTimeTo(parceTermMonth, true) + parseTime);
                        task.setTermEnd(setTimeTo(parceTermMonth, true) + parseTime);
                    } else {
                        // если просто месяц
                        task.setTermBegin(setTimeTo(parceTermMonth, true));
                        task.setTermEnd(setTimeTo(parceTermMonth, false));
                    }
                }
            }
        }


    }

    public static String parsingTaskNameAssign(String text, Context context) {
        String emailAssign = null;
        //String text = " Поручить славе задачу";
        String assign = context.getResources().getString(R.string.task_assign);
        assign = assign.toLowerCase();
        Scanner sc = new Scanner(text);
        if (sc.findInLine(assign) != null) {
            // если в тексте есть слово поручить то
            // ищем следующее слово после поручить
            String performer = sc.next();
//            android.util.Log.v("Tedorius2","performer: "+performer);

            final List<Employee> list = DbHelper.getListEmployeesForNavNew(context);
            for (Employee employee: list) {
                // ищем имя сотрудника
                Scanner empName = new Scanner(""+EmployeeCache.getInstance(context).find(employee.getEmail()));
                while (empName.hasNext()) {
                    String word = empName.next();
                    if (word !=null && !word.isEmpty()) {
                        word = word.toLowerCase();
                        int index = word.indexOf(performer);
                        if (index != -1 && index == 0) {
//                            android.util.Log.v("Tedorius2","нашли "+ employee.getEmail());
                            emailAssign = employee.getEmail();
                        } else {
                            int indexHalf = word.indexOf(performer.substring(0, performer.length()/2));
                            if (indexHalf != -1 && indexHalf == 0) {
//                                android.util.Log.v("Tedorius2","нашли "+ employee.getEmail());
                                emailAssign = employee.getEmail();
                            }
                        }
                    }
                }
            }
        }
        return emailAssign;
    }

    public static long parsingTaskNameDateToday(String text, Context context) {
        long term = 0;

        String today = context.getResources().getString(R.string.task_today);
        String tomorrow = context.getResources().getString(R.string.task_tomorrow);
        String afterTomorrow = context.getResources().getString(R.string.after_tomorrow);

        today = today.toLowerCase();
        tomorrow = tomorrow.toLowerCase();
        afterTomorrow = afterTomorrow.toLowerCase();

        Scanner sc = new Scanner(text);
        if (sc.findInLine(today) != null) {
            term = TimeHelper.currentTimeMillisWithoutTimeZone();
        }

        if (sc.findInLine(afterTomorrow) != null) {
            term = TimeHelper.currentTimeMillisWithoutTimeZone()+172800000;
        }

        if (sc.findInLine(tomorrow) != null) {
            term = TimeHelper.currentTimeMillisWithoutTimeZone()+86400000;
        }

        return term;
    }

    private static long setTimeTo(long date, boolean startOfDay) {
        Calendar mCalendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
        mCalendar.setTimeInMillis(date == 0 ? System.currentTimeMillis() : date);
        return TimeHelper.roundCalendar(mCalendar, startOfDay).getTimeInMillis();
    }

    public static long parsingTaskNameTime (String text, Context context) {
        long term = 0;
        String in = context.getResources().getString(R.string.innew);
        in = in.toLowerCase();
        Scanner sc = new Scanner(text);

        while (sc.findInLine(in) != null) {
            // если есть слово в
            // то надо выбрать следующее и проверить будет ли там цифра
            String time = sc.next();
            if (time != null && !time.isEmpty()) {
                //String time = "530pm";
                SimpleDateFormat SDF_24H = getSimpleDateFormat2("HH:mm");

                SimpleDateFormat SDF_24H2 = getSimpleDateFormat2("HH");

                SimpleDateFormat dateFormat = SDF_24H;
                SimpleDateFormat dateFormat2 = SDF_24H2;

                SimpleDateFormat dateFormatOut = SDF_24H;
                try {
                    Date date = dateFormat.parse(time);
                    date.setHours(date.getHours()+date.getTimezoneOffset()/60);

                    term = date.getHours()*60*60*1000+date.getMinutes()*60*1000;
                    //String out = dateFormatOut.format(date);

                    //android.util.Log.v("Tedorius2", "время 1 " + out);

                    //Log.e("Time", out);
                } catch (ParseException e) {
                    try {
                        Date date = dateFormat2.parse(time);
                        date.setHours(date.getHours()+date.getTimezoneOffset()/60);

                        term = date.getHours()*60*60*1000+date.getMinutes()*60*1000;

                        //String out = dateFormatOut.format(date);

                        //android.util.Log.v("Tedorius2", "время 2 " + out);
                        //Log.e("Time", out);
                    } catch (ParseException ex) {

                    }
                }
                //

            }
        }
        return term;
    }

    public static long parsingTaskNameDateWeek(String text, Context context) {
        long term = 0;

        String Monday = context.getResources().getString(R.string.Monday2);
        String Tuesday = context.getResources().getString(R.string.Tuesday2);
        String Wednesday = context.getResources().getString(R.string.Wednesday2);
        String Thursday = context.getResources().getString(R.string.Thursday2);
        String Friday = context.getResources().getString(R.string.Friday2);
        String Saturday = context.getResources().getString(R.string.Saturday2);
        String Sunday = context.getResources().getString(R.string.Sunday2);


        Monday = Monday.toLowerCase();
        Tuesday = Tuesday.toLowerCase();
        Wednesday = Wednesday.toLowerCase();
        Thursday = Thursday.toLowerCase();
        Friday = Friday.toLowerCase();
        Saturday = Saturday.toLowerCase();
        Sunday = Sunday.toLowerCase();

        Scanner sc = new Scanner(text);

        String array[] = new String[8];
        array[0] = "";
        array[1] = Sunday;
        array[2] = Monday;
        array[3] = Tuesday;
        array[4] = Wednesday;
        array[5] = Thursday;
        array[6] = Friday;
        array[7] = Saturday;

        for (int i=1; i<array.length ; i++) {
            if (sc.findInLine(array[i]) != null) {
                TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone(SharedStrings.GMT);
                Calendar calendarNow = Calendar.getInstance(DEFAULT_TIME_ZONE);
                Calendar calendar = Calendar.getInstance(DEFAULT_TIME_ZONE);
                Date date2 = new Date(System.currentTimeMillis());
                calendarNow.setTime(date2);
                calendar.setTime(date2);
                calendar.set(Calendar.DAY_OF_WEEK, i);
                if (new Date(calendarNow.getTimeInMillis()).getTime() < new Date(calendar.getTimeInMillis()).getTime()) {
                    //тогда устанавливаем
                    Date date = new Date(calendar.getTimeInMillis());
                    date.setHours(date.getHours()+date.getTimezoneOffset()/60);
                    term = date.getTime();
                } else {
                    // если прошел или сегодня то +1 неделя
                    calendar.add(Calendar.WEEK_OF_MONTH, 1);
                    Date date = new Date(calendar.getTimeInMillis());
                    date.setHours(date.getHours()+date.getTimezoneOffset()/60);
                    term = date.getTime();
                }
            }
        }

        return term;
    }

    public static long parsingTaskNameDateMonth(String text, Context context) {
        long term = 0;

        final String[] monthNames = context.getResources().getStringArray(R.array.months_full);

        for (int i=0; i<monthNames.length ; i++) {
            Scanner sc = new Scanner(text);
            String month = "";
            if (i==4) {
                month = context.getResources().getString(R.string.may);
            } else {
                month = monthNames[i].substring(0, monthNames[i].length() - 1);
            }
            month = month.toLowerCase();
            //
            String prev = "";

            while(sc.hasNext())
            {
                String word = sc.next();

                if (word.contains(month)) {
                    int dateM = 0;
                    try {
                        dateM = Integer.parseInt(prev);
                    } catch (Exception e) {

                    }
                    if (dateM != 0) {
                        TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone(SharedStrings.GMT);

                        Calendar calendarNow = Calendar.getInstance(DEFAULT_TIME_ZONE);
                        Calendar calendar = Calendar.getInstance(DEFAULT_TIME_ZONE);
                        Date date2 = new Date(System.currentTimeMillis());
                        calendarNow.setTime(date2);
                        calendar.setTime(date2);
                        calendar.set(Calendar.MONTH, i);
                        calendar.set(Calendar.DAY_OF_MONTH, dateM);

                        if (new Date(calendarNow.getTimeInMillis()).getTime() <= new Date(calendar.getTimeInMillis()).getTime()) {
                            //тогда устанавливаем
                            Date date = new Date(calendar.getTimeInMillis());
                            date.setHours(date.getHours()+date.getTimezoneOffset()/60);
                            term = date.getTime();
                        } else {
                            // если прошел или сегодня то +1 год
                            calendar.add(Calendar.YEAR, 1);
                            Date date = new Date(calendar.getTimeInMillis());
                            date.setHours(date.getHours()+date.getTimezoneOffset()/60);
                            term = date.getTime();
                        }
                    }
                }

                prev = word;
            }
        }

        return term;
    }

    public static void resetUserOrder(Context context) {
        // тут проход по бд для определения поля UserOrder
        final SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        Cursor c = null;

        Utils.clearStringBuilder(new StringBuilder());
        try {
            if (LTSettings.getInstance().isAddingTasksToTop()) {
                c = db.rawQuery("UPDATE LionTask SET UserOrder = '"+MY_TASK_USER_ORDER_DESC+"' WHERE UserOrder = '"+MY_TASK_USER_ORDER+"'", null);
            } else {
                c = db.rawQuery("UPDATE LionTask SET UserOrder = '"+MY_TASK_USER_ORDER+"' WHERE UserOrder = '"+MY_TASK_USER_ORDER_DESC+"'", null);
            }
            c.moveToFirst();

        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }
    }

    public static void setSomeSetting (String name, String usnName, boolean value) {
        String oldSettings = LTSettings.getInstance().getSettingsJson();
        JSONObject object = null;
        JSONObject objectNew = null;
        try {
            object = new JSONObject(oldSettings);
            objectNew = new JSONObject(oldSettings);

            objectNew.remove(name);
            objectNew.remove(usnName);
            objectNew.put(name, value ? 1 : 0);
            objectNew.put(usnName, object.isNull(usnName) ? 1 : object.getInt(usnName)+1);
            LTSettings.getInstance().setSettingsJson(objectNew.toString());
        } catch (Exception e) {

        }
    }

    public static void setSomeStringsSetting (String name, String usnName, String value) {
        String oldSettings = LTSettings.getInstance().getSettingsJson();
        JSONObject object = null;
        JSONObject objectNew = null;
        try {
            if (oldSettings.isEmpty()) {
                object = new JSONObject();
                objectNew = new JSONObject();
            } else {
                object = new JSONObject(oldSettings);
                objectNew = new JSONObject(oldSettings);

                objectNew.remove(name);
                objectNew.remove(usnName);
            }



            objectNew.put(name, value);
            objectNew.put(usnName, object.isNull(usnName) ? 1 : object.getInt(usnName)+1);
            LTSettings.getInstance().setSettingsJson(objectNew.toString());
        } catch (Exception e) {

        }
    }

    public static void setDefaultSetting() {
        //Utils.setSomeStringsSetting("language", "__usn_field_language", context.getResources().getString(R.string.currlang));
        Utils.setSomeStringsSetting("cal_work_time", "__usn_field_cal_work_time", LTSettings.getInstance().getMinHour()+":0-"+LTSettings.getInstance().getMaxHour()+":0");
        Utils.setSomeStringsSetting("reminders_in_n_minutes", "__usn_field_reminders_in_n_minutes", ""+LTSettings.getInstance().getNotifyPreTime());
        Utils.setSomeSetting("stopwatch", "__usn_field_stopwatch", LTSettings.getInstance().isShowChrono());
        Utils.setSomeSetting("add_task_to_begin", "__usn_field_add_task_to_begin", LTSettings.getInstance().isAddingTasksToTop());
        Utils.setSomeSetting("cal_show_week_number", "__usn_field_cal_show_week_number", LTSettings.getInstance().isShowWeekCountInCalendar());
        Utils.setSomeSetting("cal_number_of_first_week", "__usn_field_cal_number_of_first_week", LTSettings.getInstance().isWeekCountFromFirstJan());
        Utils.setSomeSetting("nav_show_summary", "__usn_field_nav_show_summary", LTSettings.getInstance().showTaskCountInNavigator());
        Utils.setSomeSetting("nav_show_overdue", "__usn_field_nav_show_overdue", !LTSettings.getInstance().isOverdueInToday());
        Utils.setSomeSetting("nav_show_tags", "__usn_field_nav_show_tags", LTSettings.getInstance().showCategoriesInNavigator());
        Utils.setSomeSetting("nav_show_markers", "__usn_field_nav_show_markers", LTSettings.getInstance().showColorsInNavigator());
        Utils.setSomeSetting("nav_show_emps", "__usn_field_nav_show_emps", LTSettings.getInstance().isEmpsInNavigator());

    }

    public static void setSettingsIfNeed(String newSettings) {
        String oldSettings = LTSettings.getInstance().getSettingsJson();
        try {
            Context mContext = LeaderTaskSyncService.mApp.getApplicationContext();
            LTSettings mSettings = LTSettings.getInstance();
            if (mContext != null) {
                JSONObject manJson = new JSONObject(newSettings);
                JSONObject manJsonOld = new JSONObject();
                if (!oldSettings.isEmpty()) {
                    manJsonOld = new JSONObject(oldSettings);
                }

                if (oldSettings.isEmpty() || manJson.getInt("__usn_field_add_task_to_begin") > manJsonOld.getInt("__usn_field_add_task_to_begin")) {
                    mSettings.setAddingTasksToTop(manJson.getInt("add_task_to_begin") == 1 ? true : false);
                    resetUserOrder(mContext);
                }

                if (oldSettings.isEmpty() || manJson.getInt("__usn_field_cal_number_of_first_week") > manJsonOld.getInt("__usn_field_cal_number_of_first_week")) {
                    mSettings.setWeekCountFromFirstJan(manJson.getInt("cal_number_of_first_week") == 1 ? true : false);
                    resetLoader(mContext);
                    //MenuLoader.getInstance(mContext).resetCalendar(); //////////////////////////////////////////////////////////////////////////////////////////
                }

                if (oldSettings.isEmpty() || manJson.getInt("__usn_field_cal_show_week_number") > manJsonOld.getInt("__usn_field_cal_show_week_number")) {
                    mSettings.setShowWeekCountInCalendar(manJson.getInt("cal_show_week_number") == 1 ? true : false);

                    final int slidingCustomWidth;
                    Integer mSlidingWidth;
                    Integer dislay = Utils.getDisplayWidth(mContext);
                    /*if (mSettings.isShowWeekCountInCalendar()) {
                        slidingCustomWidth = mContext.getResources().getDimensionPixelSize(R.dimen.slidingmenu_minimum_andweek);
                    } else {
                        slidingCustomWidth = mContext.getResources().getDimensionPixelSize(R.dimen.slidingmenu_minimum);
                    }*/

                    slidingCustomWidth = dislay;

                    if (mSettings.setSmallScreen(slidingCustomWidth >= dislay)) {
                        mSlidingWidth = dislay - mContext.getResources().getDimensionPixelSize(R.dimen.slidingmenu_to_small);
                    } else {
                        mSlidingWidth = slidingCustomWidth;
                    }

                    mSettings.setLTCalendarWidth(mSlidingWidth);

                    resetLoader(mContext);
                    //MenuLoader.getInstance(mContext).resetCalendar(); ///////////////////////////////////////////////////////////////////////////////////////////////
                    //
                    if (mSlidingMenu != null) {
                        //mSlidingMenu.setBehindOffset(dislay - mSettings.getLTCalendarWidth());

                        mSlidingMenu.setMode(SlidingMenu.LEFT);
//                        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                        mSlidingMenu.setTouchModeAbove(SlidingMenu.LEFT);
                        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
                        mSlidingMenu.setShadowDrawable(R.drawable.shadow);
                        mSlidingMenu.setFadeDegree(0.35f);
                        mSlidingMenu.showContent();
                    }
                }

                if (oldSettings.isEmpty() || manJson.getInt("__usn_field_nav_show_tags") > manJsonOld.getInt("__usn_field_nav_show_tags")) {
                    mSettings.setShowCategoriesInNavigator(manJson.getInt("nav_show_tags") == 1 ? true : false);
                    resetLoader(mContext);
                }

                if (oldSettings.isEmpty() || manJson.getInt("__usn_field_nav_show_overdue") > manJsonOld.getInt("__usn_field_nav_show_overdue")) {
                    mSettings.setOverdueInToday(manJson.getInt("nav_show_overdue") == 0 ? true : false);

                    new TaskLinkReset(mContext).resetTodayTasks(mContext);

                    resetLoader(mContext);

                    mContext.getContentResolver().notifyChange(LionMetaData.LTaskContract.CONTENT_URI, null);
                }

                if (oldSettings.isEmpty() || manJson.getInt("__usn_field_nav_show_summary") > manJsonOld.getInt("__usn_field_nav_show_summary")) {
                    mSettings.setShowTaskCountInNavigator(manJson.getInt("nav_show_summary") == 1 ? true : false);
                    resetLoader(mContext);
                }


                if (oldSettings.isEmpty() || manJson.getInt("__usn_field_nav_show_emps") > manJsonOld.getInt("__usn_field_nav_show_emps")) {
                    mSettings.setShowEmpsInNavigator(manJson.getInt("nav_show_emps") == 1 ? true : false);
                    resetLoader(mContext);
                }

                if (oldSettings.isEmpty() || manJson.getInt("__usn_field_nav_show_markers") > manJsonOld.getInt("__usn_field_nav_show_markers")) {
                    mSettings.setShowColorsInNavigator(manJson.getInt("nav_show_markers") == 1 ? true : false);
                    resetLoader(mContext);
                }

                if (oldSettings.isEmpty() || manJson.getInt("__usn_field_stopwatch") > manJsonOld.getInt("__usn_field_stopwatch")) {
                    mSettings.setShowChrono(manJson.getInt("stopwatch") == 1 ? true : false);
                    mSettings.setToRebootAfterChanges(true);
                }

/*
                if (oldSettings.isEmpty() || manJson.getInt("__usn_field_language") > manJsonOld.getInt("__usn_field_language")) {
                    if (!manJson.getString("language").equals(mContext.getResources().getString(R.string.currlang))) {
                        mSettings.setToRebootAfterChanges(true);
                        //
                        List<Locale> mLocales = new ArrayList<>(LeaderTaskLanguage.values().length + 1);

                        mLocales.add(Locale.getDefault());
                        for (LeaderTaskLanguage l : LeaderTaskLanguage.values()) {
                            mLocales.add(l.getLocale());
                        }
                        Locale newLocale = null;
                        for (Locale l : mLocales) {
                            if (manJson.getString("language").substring(0, 2).equals(l.toString())) {
                                newLocale = l;
                                //android.util.Log.v("Tedorius2", "нашли: " + newLocale);
                                break;
                            }
                        }
                        if (newLocale != null) {
                            //android.util.Log.v("Tedorius2", "присвоили язык");
                            mSettings.setLanguageLocale(newLocale);
                            mSettings.setToRebootAfterChanges(true);

                            Intent intent = SlidingActivity.newInstance(mContext);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                           mContext.startActivity(intent);
                        }
                    }
                    //
                }*/

                if (oldSettings.isEmpty() || manJson.getInt("__usn_field_reminders_in_n_minutes") > manJsonOld.getInt("__usn_field_reminders_in_n_minutes")) {
                    mSettings.setNotifyPreTime(manJson.getInt("reminders_in_n_minutes"));
                    TaskNotifyHelper.getInstance(mContext).convertTasksToNotify();
                }

                if (oldSettings.isEmpty() || manJson.getInt("__usn_field_cal_work_time") > manJsonOld.getInt("__usn_field_cal_work_time")) {
                    String time = manJson.getString("cal_work_time");
                    if (!time.isEmpty()) {
                        mSettings.setMinHour(Integer.parseInt(time.substring(0 , time.indexOf(":"))));
                        mSettings.setMaxHour(Integer.parseInt(time.substring(time.indexOf("-") +1 , time.lastIndexOf(":"))));
                    }
                }

            }
        } catch (Exception e) {
            int m = 0;
            m++;
        } finally {
            LTSettings.getInstance().setSettingsJson(newSettings);
        }
    }
    
    public static void resetLoader(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

        if (LTApplication.mBackStackActivities.containsKey(cn.getClassName()) ) {
            final BaseActivity activity = LTApplication.mBackStackActivities.get(cn.getClassName());
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    MenuLoader.getInstance(context).restartLoader();
                }
            });
        }
    }

    public static final class ResetDataThread extends Thread {

        private final WeakReference<Activity> mActivity;
        private final boolean mLogOut;
        private final LTApplication mApp;

        public ResetDataThread(Activity activity, boolean logOut) {
            super(ResetDataThread.class.getSimpleName());

            mActivity = new WeakReference<Activity>(activity);
            mLogOut = logOut;

            mApp = (LTApplication) activity.getApplicationContext();
        }

        private boolean isMyServiceRunning(Class<?> serviceClass) {
            ActivityManager manager = (ActivityManager) mApp.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void run() {
            super.run();

            mApp.cancelSynchronize();
            mApp.clearAppFolderLogs();
            mApp.clearAppFolder();

            SharedPreferences preferences = mApp.getSharedPreferences(LTSettings.PREFS_NAME, 0);
            preferences.edit().remove(LTSettings.KEY_LAST_SYNC).commit();
            preferences.edit().remove(LTSettings.KEY_EMPLOYEE_VERIFY_COUNT).commit();
            preferences.edit().remove(LTSettings.KEY_VERIFY_END_DATE).commit();
            LTSettings.getInstance().setHasNotTasks();
            LTSettings.getInstance().setIsNeedToAddUnboardingCatMar(true);
//            LTSettings.getInstance().setIsNeedToShowLoadingScreen(false);
            //LTSettings.getInstance().setNeedToAddUnboardingTasks(true);

            LTSettings.getInstance().setDropMenuHeaders(HEADER_AVAILABLE_PROJECTS, false);
            LTSettings.getInstance().setDropMenuHeaders(HEADER_BY_ME, false);
            LTSettings.getInstance().setDropMenuHeaders(HEADER_CATEGORIES, false);
            LTSettings.getInstance().setDropMenuHeaders(HEADER_FOR_ME, false);
            LTSettings.getInstance().setDropMenuHeaders(HEADER_PROJECTS, false);



            MenuLoader.getInstance(mApp).resetMenu();
            TaskNotifyHelper.getInstance(mApp).clearAllTaskNotifies(true);

            CompletedCache.getInstance(mApp).clear();
            EmployeeCache.getInstance(mApp).clear();
            LTaskCache.getInstance(mApp).clear();
            MarkerCache.getInstance(mApp).clear();
            TaskFileCache.getInstance(mApp).clear();
            TaskLinkCache.getInstance(mApp).clear();
            TaskMessageCache.getInstance(mApp).clear();

            final LTSettings settings = LTSettings.getInstance();
            settings.setMaximumOrder(0);
            settings.setMaximumVertical(0);
            settings.setToRebootAfterChanges(false);
            settings.setMenuItem(MenuItemType.TODAY);
            lastCheckedMenuItemUUID = null;
            settings.setSessionUUID(null);

            settings.setShowEmpsInNavigator(true);
            settings.setShowCategoriesInNavigator(true);
            settings.setShowColorsInNavigator(true);
            settings.setOverdueInToday(true);
            settings.setShowTaskCountInNavigator(true);
            settings.setNotifyPreTime(0);

            settings.setNeedToPutSettings(true);

            MyInstanceIDListenerService.delToken(mApp);
            LTSettings.needToShowToastAfterAddTask = false;
            LTSettings.needToShowToastAfterAddProject = false;
            LTSettings.needToShowToastAfterAddUser = false;
            LTSettings.needToShowToastAfterAssign = false;

            settings.cleanDataBase();


            LeaderTaskSyncService.webSync();
            LeaderTaskSyncService.closeNotify();

            if (mLogOut) {
                settings.setNeedPasswordToStart(false);
                settings.setNeedPinToStart(false);
                settings.setNeedFingerToStart(false);
                settings.clearUserData(getRunnable());
                LeaderTaskSyncService.syncWear();

            } else {
                final Activity activity = mActivity.get();
                if (activity != null) {
                    activity.finish();
                    activity.startActivity(SlidingActivity.newInstance(activity));
                }
            }
        }

        private Runnable getRunnable() {
            return new Runnable() {
                @Override
                public void run() {
                    final Activity activity = mActivity.get();
                    if (activity != null) {
                        activity.finish();
                        activity.startActivity(SlidingActivity.newInstanceActionLogin(activity));
                    }
                }
            };
        }
    }

    public static void showInviteDialog(final Context context, final String name, final String email, final String org, final String inviteUUID) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

        if (LTApplication.mBackStackActivities.containsKey(cn.getClassName()) && !isShowingInviteDialog) {
            final BaseActivity activity = LTApplication.mBackStackActivities.get(cn.getClassName());
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final AlertDialog.Builder ad = new AlertDialog.Builder(activity);
                    ad.setTitle(activity.getString(R.string.invitation));
                    ad.setMessage((!name.isEmpty() ? name : email)+" "+activity.getResources().getString(R.string.invitation_mes)/*+(org == null ? "" :org.isEmpty() ? "" : "\'"+org+"\'")*/);
                    ad.setPositiveButton(R.string.accept, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // ПРИНЯТЬ
                                    List<NameValuePair> nameValuePairs = new ArrayList<>();
                                    nameValuePairs.add(new BasicNameValuePair("session", LTSettings.getInstance().getSessionUUID()));
                                    nameValuePairs.add(new BasicNameValuePair("login", LTSettings.getInstance().getUserProfile().getName()));
                                    nameValuePairs.add(new BasicNameValuePair("password", LTSettings.getInstance().getUserProfile().getPassword()));
                                    nameValuePairs.add(new BasicNameValuePair("invite", inviteUUID.toUpperCase()));
                                    nameValuePairs.add(new BasicNameValuePair("accept", "true"));

                                    String message = OkHttpConnection.postWithParams(nameValuePairs, NETWROK_ACCEPT_INVITE);
                                    String error = "";
                                    try {
                                        JSONObject jsonObject = new JSONObject(message);
                                        error = jsonObject.get("error").toString();
                                        if (!error.isEmpty()) {
                                            final String finalError = error;
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ToastController.getInstance(activity).showToast(activity.getResources().getString(R.string.invitation_no));
                                                }
                                            });
                                        }
                                    } catch (Exception e) {

                                    }
                                    if (error.isEmpty()) {
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastController.getInstance(activity).showToast(activity.getResources().getString(R.string.invitation_yes));
                                            }
                                        });
                                        //Utils.startSyncAlways((LTApplication) context);
                                        //MenuLoader.getInstance(context).restartLoader();
                                    }

                                    LTSettings.getInstance().setNeedShowInvite(false);
                                }
                            }).start();

                            isShowingInviteDialog = false;
                        }
                    });
                    ad.setNegativeButton(R.string.decline, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // ОТКАЗАТЬСЯ
                                    List<NameValuePair> nameValuePairs = new ArrayList<>();
                                    nameValuePairs.add(new BasicNameValuePair("session", LTSettings.getInstance().getSessionUUID()));
                                    nameValuePairs.add(new BasicNameValuePair("login", LTSettings.getInstance().getUserProfile().getName()));
                                    nameValuePairs.add(new BasicNameValuePair("password", LTSettings.getInstance().getUserProfile().getPassword()));
                                    nameValuePairs.add(new BasicNameValuePair("invite", inviteUUID.toUpperCase()));
                                    nameValuePairs.add(new BasicNameValuePair("accept", "false"));

                                    String message = OkHttpConnection.postWithParams(nameValuePairs, NETWROK_ACCEPT_INVITE);

                                    try {
                                        JSONObject jsonObject = new JSONObject(message);
                                        final String error = jsonObject.get("error").toString();
                                        if (!error.isEmpty()) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ToastController.getInstance(activity).showToast("Ошибка отказа: "+error);
                                                }
                                            });
                                        }
                                    } catch (Exception e) {

                                    }

                                    LTSettings.getInstance().setNeedShowInvite(false);

                                }
                            }).start();
                            isShowingInviteDialog = false;
                        }
                    });
                    Dialog dialog = ad.create();
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    isShowingInviteDialog = true;

                }
            });
        }
    }

    public static void showToastsInviteAccepted(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

        if (LTApplication.mBackStackActivities.containsKey(cn.getClassName()) && !isShowingInviteDialog) {
            final BaseActivity activity = LTApplication.mBackStackActivities.get(cn.getClassName());
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    boolean wasOne = false;
                    for (Employee employee : LTSettings.allInvitedAcceptedUsers) {
                        String name = "";
                        if (employee.getName() != null) {
                            name = employee.getName();
                        }
                        //ToastController.getInstance(activity).showToast(activity.getResources().getString(R.string.member)+" "+(name.isEmpty() ? employee.getEmail() : name)+" "+activity.getResources().getString(R.string.member2));
                        wasOne = true;
                    }
                    LTSettings.allInvitedAcceptedUsers.clear();

                    if (LTSettings.needToShowAddMessage && !wasOne) { // если юзер сразу после синхры не добавился - то выводим сообщение
//                        ErrorDialog.newInstance(
//                                activity.getResources().getString(R.string.add_new_user_message2))
//                                .showDialog(activity.getSupportFragmentManager());
                        showPhone(activity);
                    } else {
                        if (wasOne) {
                            showPhone(activity);
                        }
                    }
                    LTSettings.needToShowAddMessage = false;
                    //


                }
            });
        }

        //
    }

    private static void showPhone(BaseActivity activity) {
        Emp mEmp = null;
        List<Emp> emps = DbHelper.getListEmps(activity);
        for (Emp temp: emps) {
            if (temp.getLogin().equals(LTSettings.getInstance().getUserName())) {
                mEmp = temp;
                break;
            }
        }

        if (mEmp != null && (mEmp.getPhone() == null || mEmp.getPhone().isEmpty())) {
            final boolean hasCustomLocale = LTSettings.getInstance().getLanguageLocale() != null;
            final Locale appLocale = hasCustomLocale ?  LTSettings.getInstance().getLanguageLocale() : Locale.getDefault();
            if (appLocale.getLanguage().equals("ru")) {
                Utils.showPhoneDialog(activity, mEmp);
            }
        }
    }

    public static void showPhoneDialog (final Activity activity, final Emp mEmp) {
        final View v = LayoutInflater.from(activity).inflate(R.layout.add_phone_dialog, null);
        final EditText mEditText = (EditText) v.findViewById((R.id.phone));

        final AlertDialog.Builder ad = new AlertDialog.Builder(activity);

        ad.setView(v);
        ad.setCancelable(true);
        ad.setPositiveButton(R.string.btn_ok, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String phone = mEditText.getText().toString().trim();
                if (!phone.isEmpty()) {
                    if (mEmp != null) {
                        final ContentValues cv = new ContentValues();
                        cv.put(LeaderTaskProviderMetaData.EmpContract.USN_ENTITY, 0);
                        cv.put(LeaderTaskProviderMetaData.EmpContract.PHONE, phone);
                        cv.put(LeaderTaskProviderMetaData.EmpContract.USN_FIELD_PHONE, mEmp.getUsnFieldPhone() + 1);

                        activity.getContentResolver().update(LeaderTaskProviderMetaData.EmpContract.CONTENT_URI, cv, LeaderTaskProviderMetaData.EmpContract.selectionUid(mEmp.getUid()), null);
                        Utils.startSync(((LTApplication) activity.getApplicationContext()));
                    }
                }
            }
        });

        ad.setNegativeButton(R.string.btn_cancel, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = ad.create();
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                alertDialog.show();
            }
        });
    }

    public static void showUnbordingToasts(final Context context, final int type) {
        /*try {
            // если юзер в триале
            if (TimeHelper.getInstance().getIntDifferencesDateInDays(LTSettings.getInstance().getVerifyEndDateInLong(), TimeHelper.currentTimeMillisWithoutTimeZone()) < 8) {
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

                if (LTApplication.mBackStackActivities.containsKey(cn.getClassName())) {
                    final BaseActivity activity = LTApplication.mBackStackActivities.get(cn.getClassName());
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        switch (type) {
                            case 0:
                                ToastController.getInstance(activity).showToast(activity.getResources().getString(R.string.toast_unboarding1));
                                break;

                            case 1:
                                ToastController.getInstance(activity).showToast(activity.getResources().getString(R.string.toast_unboarding2));
                                break;

                            case 2:
                                ToastController.getInstance(activity).showToast(activity.getResources().getString(R.string.toast_unboarding3));
                                break;

                            case 3:
                                ToastController.getInstance(activity).showToast(activity.getResources().getString(R.string.toast_unboarding4));
                                break;

                            default:
                                break;
                        }
                        }
                    });
                }
            }
        } catch (Exception e) {

        }*/
    }
    //
    public static void downloadAndSaveGooglePhoto(Context context) {
        final ContentValues cv = new ContentValues();
        try {
            Emp mEmp = DbHelper.getInstance(context).getEmpByLogin(LTSettings.getInstance().getUserName());

            //save new foto
            final File src = new File(((LTApplication) context).getAppFolder(), LTSettings.getInstance().getUserName());
            final File dst = new File(((LTApplication) context).getAppFolder(), Utils.TMP_FOTO_FILE_NAME);

            if (dst != null && dst.exists()) {
                try {
                    Utils.FileWorker.copyFile(dst, src);
                    cv.put(LeaderTaskProviderMetaData.EmpContract.USN_ENTITY, 0);
                    cv.put(LeaderTaskProviderMetaData.EmpContract.USN_FIELD_FOTO, mEmp.getUsnFieldFoto() + 1);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    clearCacheFoto(context, LTSettings.getInstance().getUserName());
                    clearCacheFoto(context, dst.getName());

                    dst.delete();
                }
            }
            //
            ((LTApplication) context).getContentResolver().update(LeaderTaskProviderMetaData.EmpContract.CONTENT_URI, cv, LeaderTaskProviderMetaData.EmpContract.selectionUid(mEmp.getUid()), null);
            //
            LTSettings.getInstance().setNeedToDownloadPhotoGoogleFacebook(false);
            LTSettings.getInstance().setDownloadUriGoogleFacebook("");

            MenuLoader.getInstance(context).restartLoader();
        } catch (Exception e) {

        }
    }


    private static void clearCacheFoto(Context context, String fileName) {
        try {
            File cacheImgFile = new File(((LTApplication)context).getAppFolder() + "/cache_" + fileName);
            if (cacheImgFile.exists()) {
                cacheImgFile.delete();
            }
        } catch (Exception e) {

        }
    }


    public static void googleImageDownload(final Context context, final String url){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

        if (LTApplication.mBackStackActivities.containsKey(cn.getClassName()) && !isShowingInviteDialog) {
            final BaseActivity activity = LTApplication.mBackStackActivities.get(cn.getClassName());
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Picasso.get().load(url).into(getTarget(context));
                }
            });
        }
    }

    //target to save
    private static Target getTarget(final Context context){
        Target target = new Target(){

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        File file = new File(((LTApplication)context).getAppFolder() + "/" + Utils.TMP_FOTO_FILE_NAME);
                        try {
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                            ostream.flush();
                            ostream.close();

                            downloadAndSaveGooglePhoto(context);
                        } catch (IOException e) {
                            Log.e("IOException", e.getLocalizedMessage());
                        }
                    }
                }).start();

            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        return target;
    }

    public static void updateTodayWidget(Context context) {
        try {
            if (context != null) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_list_leadertsk);
                ComponentName thisWidget = new ComponentName(context, TodayTasksWidget.class);
                remoteViews.setTextViewText(R.id.text_today, TimeHelper.getInstance().getCuteDateTitleS(new Date(System.currentTimeMillis())));


                int widgetIDs[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, TodayTasksWidget.class));

                for (int id : widgetIDs) {
                    Intent adapter = new Intent(context, TodayWidgetAdapterService.class);
                    adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
                    remoteViews.setRemoteAdapter(R.id.list_tasks_widget, adapter);

                    AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(id, R.id.list_tasks_widget);
                }


                appWidgetManager.updateAppWidget(thisWidget, remoteViews);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
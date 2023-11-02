package com.ashberrysoft.leadertask;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Utils {

    public static final String KEYKEY = "/todaytasks";
    public static final String COUNT_KEY = "com.ashberrysoft.leadertask.tasks";
    public static final String COUNT_KEY2 = "com.ashberrysoft.leadertask.tasksuuids";
    public static final String COUNT_KEY3 = "com.ashberrysoft.leadertask.emails";
    public static final String COUNT_KEY10 = "com.ashberrysoft.leadertask.emailsemails";

    public static final String COUNT_KEY4 = "com.ashberrysoft.leadertask.taskstome";
    public static final String COUNT_KEY5 = "com.ashberrysoft.leadertask.taskstomeids";
    public static final String COUNT_KEY6 = "com.ashberrysoft.leadertask.taskstomeuidcustomer";
    public static final String COUNT_KEY8 = "com.ashberrysoft.leadertask.taskstodaycustomer";
    public static final String COUNT_KEY9 = "com.ashberrysoft.leadertask.taskstodayperformers";
    public static final String COUNT_KEY_LOGIN = "com.ashberrysoft.leadertask.login";
    public static final String COUNT_KEY7 = "com.ashberrysoft.leadertask.myemail";


    public static String getTaskUidFromIdInToday(int id, Context context) {
        String uid = "";
        JSONArray resultJsonTasksUids = null;

        SharedPreferences prefs = context.getSharedPreferences("tasks", 0);
        String jsonPrefNames = prefs.getString("taskstodayuidnew", "");
        try {
            resultJsonTasksUids = new JSONArray(jsonPrefNames);
            for (int i=0;i<resultJsonTasksUids.length(); i++) {
                if (i == id) {
                    uid = resultJsonTasksUids.get(i).toString();
                }
            }
        } catch (Exception e) {}

        return uid;
    }

    public static String getTaskUidFromIdInForMe(int id, Context context) {
        String uid = "";
        JSONArray resultJsonTasksUids = null;

        SharedPreferences prefs = context.getSharedPreferences("tasks", 0);
        String jsonPrefNames = prefs.getString("tasksformeuidnew", "");
        try {
            resultJsonTasksUids = new JSONArray(jsonPrefNames);
            for (int i=0;i<resultJsonTasksUids.length(); i++) {
                if (i == id) {
                    uid = resultJsonTasksUids.get(i).toString();
                }
            }
        } catch (Exception e) {}

        return uid;
    }

    public static String getTaskCustomerFromIdInForMe(int id, Context context) {
        String email = "";
        JSONArray resultJsonTasksUids = null;

        SharedPreferences prefs = context.getSharedPreferences("tasks", 0);
        String jsonPrefNames = prefs.getString("tasksemailscustomernew", "");
        try {
            resultJsonTasksUids = new JSONArray(jsonPrefNames);
            for (int i=0;i<resultJsonTasksUids.length(); i++) {
                if (i == id) {
                    email = resultJsonTasksUids.get(i).toString();
                }
            }
        } catch (Exception e) {}
        return email;
    }

    public static String getTaskCustomerFromIdInToday(int id, Context context) {
        String email = "";
        JSONArray resultJsonTasksUids = null;

        SharedPreferences prefs = context.getSharedPreferences("tasks", 0);
        String jsonPrefNames = prefs.getString("taskstodaycustomers", "");
        try {
            resultJsonTasksUids = new JSONArray(jsonPrefNames);
            for (int i=0;i<resultJsonTasksUids.length(); i++) {
                if (i == id) {
                    email = resultJsonTasksUids.get(i).toString();
                }
            }
        } catch (Exception e) {}
        return email;
    }

    public static String getTaskPerformerFromIdInToday(int id, Context context) {
        String email = "";
        JSONArray resultJsonTasksUids = null;

        SharedPreferences prefs = context.getSharedPreferences("tasks", 0);
        String jsonPrefNames = prefs.getString("taskstodayperformers", "");
        try {
            resultJsonTasksUids = new JSONArray(jsonPrefNames);
            for (int i=0;i<resultJsonTasksUids.length(); i++) {
                if (i == id) {
                    email = resultJsonTasksUids.get(i).toString();
                }
            }
        } catch (Exception e) {}
        return email;
    }

    public static void saveSyncInfoForMe(Context context, ArrayList <String> tasksNames, ArrayList <String> tasksUUIDs, ArrayList <String> customers) {
        if (customers != null && tasksUUIDs != null && tasksNames != null) {
            JSONArray resultJsonTasksNames = new JSONArray();
            JSONArray resultJsonTasksUUIDs = new JSONArray();
            JSONArray resultJsonEmailsCustomers = new JSONArray();
            try {
                for (int i = 0; i < tasksNames.size(); i++) {
                    resultJsonTasksNames.put(tasksNames.get(i));
                    resultJsonTasksUUIDs.put(tasksUUIDs.get(i));
                    resultJsonEmailsCustomers.put(customers.get(i));
                }
            } catch (Exception e) {
            }
            //
            SharedPreferences prefs = context.getSharedPreferences("tasks", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("tasksformenew", resultJsonTasksNames.toString());
            editor.putString("tasksformeuidnew", resultJsonTasksUUIDs.toString());
            editor.putString("tasksemailscustomernew", resultJsonEmailsCustomers.toString());
            editor.commit();
        }
    }



    public static boolean getLogIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("tasks", 0);
        return prefs.getBoolean("log_in_2", false);
    }

    public static String getUserName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("tasks", 0);
        return prefs.getString("userName", "");
    }

    public static void saveLogIn(Context context, boolean logIn) {
        SharedPreferences prefs = context.getSharedPreferences("tasks", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("log_in_2", logIn);
        editor.commit();
    }

    public static void saveUserName(Context context, String userName) {
        SharedPreferences prefs = context.getSharedPreferences("tasks", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userName", userName);
        editor.commit();
    }

    public static void saveSyncInfoToday(Context context, ArrayList <String> tasksNames, ArrayList <String> tasksUUIDs, ArrayList <String> tasksCustomers, ArrayList <String> tasksPerformers) {
        if (tasksNames != null && tasksUUIDs != null) {
            JSONArray resultJsonTasksNames = new JSONArray();
            JSONArray resultJsonTasksUUIDs = new JSONArray();
            JSONArray resultJsonTasksCustomers = new JSONArray();
            JSONArray resultJsonTasksPerformers = new JSONArray();
            try {
                for (int i = 0; i < tasksNames.size(); i++) {
                    resultJsonTasksNames.put(tasksNames.get(i));
                    resultJsonTasksUUIDs.put(tasksUUIDs.get(i));
                    resultJsonTasksCustomers.put(tasksCustomers.get(i));
                    resultJsonTasksPerformers.put(tasksPerformers.get(i));
                }
            } catch (Exception e) {
            }
            //
            SharedPreferences prefs = context.getSharedPreferences("tasks", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("taskstodaynew", resultJsonTasksNames.toString());
            editor.putString("taskstodayuidnew", resultJsonTasksUUIDs.toString());
            editor.putString("taskstodaycustomers", resultJsonTasksCustomers.toString());
            editor.putString("taskstodayperformers", resultJsonTasksPerformers.toString());
            editor.commit();
        }
    }

    public static void saveSyncInfoAllUsers(Context context, ArrayList <String> names, ArrayList <String> emails) {
        if (names != null && emails != null) {
            Set<String> prefNames = new HashSet<>();
            Set<String> prefEmails = new HashSet<>();
            for (String name : names) {
                prefNames.add(name);
            }
            for (String email : emails) {
                prefEmails.add(email);
            }
            SharedPreferences prefs = context.getSharedPreferences("tasks", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putStringSet("emails", prefNames);
            editor.putStringSet("emailsemails", prefEmails);
            editor.commit();
        }
    }

    public static  ArrayList <String> getNames (Context context) {
        ArrayList <String> emails= new ArrayList<>();

        SharedPreferences prefs = context.getSharedPreferences("tasks", 0);
        SharedPreferences.Editor editor = prefs.edit();
        Set <String> prefEmails = prefs.getStringSet("emails", new HashSet<String>());

        for (String name: prefEmails) {
            emails.add(name);
        }
        editor.commit();

        return emails;
    }

    public static  ArrayList <String> getEmails (Context context) {
        ArrayList <String> emails= new ArrayList<>();

        SharedPreferences prefs = context.getSharedPreferences("tasks", 0);
        SharedPreferences.Editor editor = prefs.edit();
        Set <String> prefEmails = prefs.getStringSet("emailsemails", new HashSet<String>());

        for (String name: prefEmails) {
            emails.add(name);
        }
        editor.commit();

        return emails;
    }

    public static  ArrayList <String> getTasksToday (Context context) {
        ArrayList <String> tasksNames= new ArrayList<>();
        JSONArray resultJsonTasksNames = null;

        SharedPreferences prefs = context.getSharedPreferences("tasks", 0);
        String jsonPrefNames = prefs.getString("taskstodaynew", "");
        try {
            resultJsonTasksNames = new JSONArray(jsonPrefNames);
            for (int i=0;i<resultJsonTasksNames.length(); i++) {
                tasksNames.add(resultJsonTasksNames.get(i).toString());
            }
        } catch (Exception e) {}


        return tasksNames;
    }

    public static  ArrayList <String> getForMeTasks (Context context) {
        ArrayList <String> tasksNames= new ArrayList<>();
        JSONArray resultJsonTasksNames = null;

        SharedPreferences prefs = context.getSharedPreferences("tasks", 0);
        String jsonPrefNames = prefs.getString("tasksformenew", "");
        try {
            resultJsonTasksNames = new JSONArray(jsonPrefNames);
            for (int i=0;i<resultJsonTasksNames.length(); i++) {
                tasksNames.add(resultJsonTasksNames.get(i).toString());
            }
        } catch (Exception e) {}

        return tasksNames;
    }

    public static void saveBitmapIntoDevice(Bitmap bitmap, String imageName, Context context) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(getOutputMediaFile(context,imageName));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File getOutputMediaFile(Context context, String imageName){

        File mediaStorageDir = new File(context.getExternalFilesDir(null)
                + "/Android/data/"
                + context.getApplicationContext().getPackageName()
                + "/Files");

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        File mediaFile;
        String mImageName="IMG_" + imageName + ".png";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public static RoundedBitmapDrawable getFotoBitmapFromFolder(ListActivity mApp, String fileName) {
        //
        try {
            final File cacheImgFile = new File(mApp.getExternalFilesDir(null)+ "/cache_" + fileName);
            if (cacheImgFile.exists()) { // если есть уменьшенная закешированная фотка
                return getCircleBitmap(BitmapFactory.decodeFile(cacheImgFile.getAbsolutePath()), mApp);
            } else {
                // сделать уменьшенную и КВАДРАТНУЮ копию файла из обычной
                final File imgFile = new File(mApp.getExternalFilesDir(null) + "/" + fileName);
                //return  Drawable.createFromPath(imgFile.getPath());

                if (imgFile.exists()) { // если есть ОБЫЧНОЕ ФОТО
                    return getCircleBitmap(customDecodeFile(imgFile, 200, 200), mApp);
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

}

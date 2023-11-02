package com.grsoft.manager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.grsoft.com.grsoft.database.MessageTokenSender;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.WriteServiceBase;

import java.util.ArrayList;
import java.util.List;

public class MainEx extends ManagerNew {
    public static final String CHANNEL_ID = "chid";
    static final String TAG = "MainEx";
    public String notifyToken;
    static MainEx instance;

    public static void setNewToken(String token) {
        if(instance != null) {
            instance.onNewToken(token);
        }
    }

    void onNewToken(String token) {
        notifyToken = token;

        Thread t = new Thread(() -> {
            sendToServer(token);
        });
        t.start();
    }

    void sendToServer(String token) {
        Config config = ConfigManager.getConfig();
        if(config.login.length() > 0) {
            LoginData ld = new LoginData(config.login, config.passw, "", this, "", "");

            List<ObjectExportListener> snd = new ArrayList<>();
            snd.add(new MessageTokenSender(token));

            WriteServiceBase wr = RWServiceFactory.instance.createWriteService(snd);
            wr.write(this, ld);
        }
    }


    @Override
    protected void setSending(List<ObjectListener> toSend) {
        super.setSending(toSend);
        if(notifyToken != null) {
            toSend.add(new MessageTokenSender(notifyToken));
        }
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                    AlertDialog.Builder b = new AlertDialog.Builder(this);
                    b.setTitle(R.string.alert_info);
                    b.setMessage(R.string.alert_message);
                    b.setPositiveButton(android.R.string.ok, null);
                    b.create().show();
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setTitle(R.string.alert_info);
                b.setMessage(R.string.alert_message_text);
                b.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                    dialogInterface.dismiss();
                });
                b.setNegativeButton(R.string.no_thx, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    void initNotify() {
        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        task.getException().printStackTrace();
                        return;
                    }
                    onNewToken(task.getResult());
                });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        askNotificationPermission();
        createNotificationChannel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initNotify();
    }
}

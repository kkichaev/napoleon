package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Banner;

import java.io.File;
import java.util.List;

public class BannerView extends Activity {
    public interface FinishHandler {
        void onFinish(Context context);
    }

    static FinishHandler handler;
    static List<Banner> banners;

    int curBanner = 0;

    public static void open(Context context, int place, FinishHandler handler) {
        if(BuildConfig.DEBUG)
            return;

        String filter = String.format("((place & %d) <> 0)", place);
        banners = DbReader.fetch(Banner.class, filter, "pos");
        if(banners.size() == 0 && handler != null) {
            handler.onFinish(context);
            return;
        }

        BannerView.handler = handler;
        Intent i = new Intent(context, BannerView.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.banner_view);

        showBanner();

        findViewById(R.id.close).setOnClickListener(v -> showBanner());
    }

    private void showBanner() {
        if(curBanner >= banners.size()) {
            if(handler != null) {
                handler.onFinish(getApplicationContext());
            }
            finish();
            return;
        }

        Banner b = banners.get(curBanner++);
        String fileName = new String(b.pic);
        Bitmap img = BitmapFactory.decodeFile(fileName);

        findViewById(R.id.counter).setVisibility(View.VISIBLE);
        findViewById(R.id.close).setVisibility(View.GONE);
        ((ImageView)findViewById(R.id.banner)).setImageBitmap(img);

        Thread t = new Thread(() -> {
            try {
                int count = b.duration;
                while(count > 0) {
                    final int ctr = count;
                    runOnUiThread(() -> {
                        ((TextView)findViewById(R.id.counter)).setText(Integer.toString(ctr));
                    });
                    Thread.sleep(1000L);
                    count--;
                }
                runOnUiThread(() -> {
                    findViewById(R.id.counter).setVisibility(View.GONE);
                    findViewById(R.id.close).setVisibility(View.VISIBLE);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
    }
}

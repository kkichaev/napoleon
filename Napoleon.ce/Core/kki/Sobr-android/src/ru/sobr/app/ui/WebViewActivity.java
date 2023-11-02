package ru.sobr.app.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created with IntelliJ IDEA.
 * User: ryashentsev
 * Date: 02.07.13
 * Time: 18:06
 * To change this template use File | Settings | File Templates.
 */
public class WebViewActivity extends Activity {

    public static final String URL = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = getIntent().getStringExtra(URL);
        if (url == null) {
            finish();
            return;
        }
        Log.d("sobr", "onCreate " + url);
        WebView webView = new WebView(this);
        setContentView(webView);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("sobr", "onPageStarted " + url);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("sobr", "shouldOverrideUrlLoading " + url);
                view.loadUrl(url);
                return true;
            }
        });

        webView.loadUrl(url);
    }
}

package com.crq.oprosnik;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webView);

        NavigateToFirstPage();
    }


    public void NavigateToFirstPage() {

        webView.clearCache(true);
        webView.clearHistory();

        setupWebView();
        webView.canGoBack();

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        CookieManager.getInstance().setAcceptFileSchemeCookies(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                handler.proceed(); // Ignore SSL certificate errors
                super.onReceivedSslError(view, handler, error);

            }

            @Override
            public void onReceivedHttpError(WebView view,
                                            WebResourceRequest request, WebResourceResponse errorResponse) {
                Toast.makeText(MainActivity.this,
                        "Http Error" + errorResponse.getStatusCode() + ": " + request.getUrl().toString(),
                        Toast.LENGTH_SHORT).show();

                super.onReceivedHttpError(view, request, errorResponse);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView w, String url) {
                if (url.contains("/home/closewindow")) { // magic url part
                    finish();
                    return false;
                }
                else
                    w.loadUrl(url);
                return true;
            }
        });

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
        }

        String android_id = telephonyManager.getDeviceId();

        url = "https://kassa.norana.ru/survey/get?deviceId=" + android_id;

        webView.loadUrl(url);

        if (!isConnected(MainActivity.this)) {
            Toast.makeText(this, "Нет соединения с интернетом", Toast.LENGTH_SHORT).show();
            webView.loadUrl("file:///android_asset/html/offline.html");
        }

        webView.addJavascriptInterface(this, "appNoranaUtils");
    }

    @JavascriptInterface
    public void reloadFromBegin() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @JavascriptInterface
    public void closeWindow() {
        finish();
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(false);
        settings.setCacheMode(android.webkit.WebSettings.LOAD_NO_CACHE);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setJavaScriptEnabled(true);

        settings.setAppCacheEnabled(true);
        settings.setGeolocationDatabasePath(this.getFilesDir().getPath());
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadWithOverviewMode(true);

        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

    }

    public static boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != cm) {
            NetworkInfo info = cm.getActiveNetworkInfo();

            return (info != null && info.isConnected());
        }
        return false;
    }
}


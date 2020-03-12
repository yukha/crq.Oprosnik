package com.crq.oprosnik;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new SSLTolerantWebViewClient());

        setupWebView();

        webView.loadUrl("http://cloudsurvey.survstat.ru/?p=lgstr3998d44f&mode=1");
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(false);
        settings.setCacheMode(android.webkit.WebSettings.LOAD_NO_CACHE);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setJavaScriptEnabled(true);

        //settings.setAppCacheEnabled(true);
        //settings.setGeolocationDatabasePath(this.getFilesDir().getPath());
//        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
//        settings.setAllowFileAccess(true);
        settings.setUseWideViewPort(true);
//        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadWithOverviewMode(true);

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return false;
            }
        });
    }
    private static class SSLTolerantWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors
            super.onReceivedSslError(view, handler, error);

        }
    }
}


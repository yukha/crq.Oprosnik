package com.crq.oprosnik;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.Objects;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webView);

        webView.setWebContentsDebuggingEnabled(true);

        webView.clearCache(true);
        webView.clearHistory();

        webView.setWebViewClient(new SSLTolerantWebViewClient());

        setupWebView();
        webView.canGoBack();

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        CookieManager.getInstance().setAcceptFileSchemeCookies(true);

        // webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient(){
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Toast.makeText(MainActivity.this, "navigate " + url, Toast.LENGTH_SHORT ).show();
//                view.loadUrl(url);
//                return false;
//            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error){
                // Toast.makeText(MainActivity.this, "WebView error " + error.getErrorCode(), Toast.LENGTH_SHORT ).show();
            }

            @Override
            public void onReceivedHttpError(WebView view,
                                            WebResourceRequest request, WebResourceResponse errorResponse) {

                Toast.makeText(MainActivity.this,
                        "Http Error" + errorResponse.getStatusCode() + ": " + request.getUrl().toString(),
                        Toast.LENGTH_SHORT).show();


                super.onReceivedHttpError(view, request, errorResponse);
            }

//            @Override
//            public void onLoadResource(WebView view, String url) {
//                if(Pattern.matches("http[s]?://[^\\.]*\\.?survstat\\.ru.*", url))
//                    return;
//                if(Pattern.matches("http.?://fonts\\.gstatic\\.com.*", url))
//                    return;
//                if(Pattern.matches("http.?://fonts\\.googleapis\\.com.*", url))
//                    return;
//
//                Toast.makeText(MainActivity.this,
//                        "Forbidden url: " + url,
//                        Toast.LENGTH_SHORT).show();
//            }
        });

        webView.loadUrl("http://cloudsurvey5.survstat.ru/Survey/Adaptive?p=lgstr3998d44f&mode=1");

        if (!isConnected(MainActivity.this)) {
            Toast.makeText(MainActivity.this, "Offline ", Toast.LENGTH_SHORT).show();
        }
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

    private static class SSLTolerantWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors
            super.onReceivedSslError(view, handler, error);

        }
    }
}


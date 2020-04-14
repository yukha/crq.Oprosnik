package com.crq.oprosnik;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webView);

        // webView.setWebContentsDebuggingEnabled(true);

        webView.clearCache(true);
        webView.clearHistory();

        webView.setWebViewClient(new SSLTolerantWebViewClient());

        setupWebView();
        webView.canGoBack();

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        CookieManager.getInstance().setAcceptFileSchemeCookies(true);

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onReceivedHttpError(WebView view,
                                            WebResourceRequest request, WebResourceResponse errorResponse) {
                Toast.makeText(MainActivity.this,
                        "Http Error" + errorResponse.getStatusCode() + ": " + request.getUrl().toString(),
                        Toast.LENGTH_SHORT).show();

                super.onReceivedHttpError(view, request, errorResponse);
            }
        });


        @SuppressLint("HardwareIds") String android_id = Secure.getString( getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        // webView.loadUrl("https://kassa.norana.ru/survey/get?deviceId=" + android_id);

        webView.loadUrl("https://default.norana.ru/?deviceId=" + android_id);

        if (!isConnected(MainActivity.this)) {
            Toast.makeText(MainActivity.this, "Offline ", Toast.LENGTH_SHORT).show();
        }

        notification();
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

    private void notification(){
        Context context = getApplicationContext();
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);



        String channelId = context.getString(R.string.default_notification_channel_id);
        // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_stat_check_circle_outline)
                        .setContentTitle("Текст заголовка")
                        .setContentText("Новый опрос, короны и вирусы!")
                        .setAutoCancel(true)
                        // .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(notificationManager == null)
        {
            Toast.makeText(context, "NotificationManager is null", Toast.LENGTH_SHORT).show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId("com.crq.oprosnik");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "com.crq.oprosnik",
                    "Oprosnik",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Notification n = notificationBuilder.build();

        // Toast.makeText(context,"до этого места работает",      Toast.LENGTH_SHORT).show();
        try {
            notificationManager.notify(0, n);
        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),      Toast.LENGTH_SHORT).show();
        }
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


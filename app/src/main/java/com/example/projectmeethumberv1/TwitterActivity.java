package com.example.projectmeethumberv1;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class TwitterActivity extends AppCompatActivity {

    private final static String HUMBER_TWITTER_URL = "https://twitter.com/humbercollege";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_webview);

        WebView webView = findViewById(R.id.twitter_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(HUMBER_TWITTER_URL);

        WebViewClient webViewClient = new WebViewClient() {
            @SuppressWarnings("depricated")
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            @TargetApi(Build.VERSION_CODES.N)
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        };
        webView.setWebViewClient(webViewClient);
    }
}

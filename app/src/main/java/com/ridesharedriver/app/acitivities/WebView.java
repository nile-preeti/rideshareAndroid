package com.ridesharedriver.app.acitivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.ridesharedriver.app.R;

public class WebView extends AppCompatActivity {
    android.webkit.WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView= (android.webkit.WebView) findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowContentAccess(true);

        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl("http://maps.google.com/maps?saddr=28.6280,77.3649&daddr=28.5703,77.3218");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {

            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
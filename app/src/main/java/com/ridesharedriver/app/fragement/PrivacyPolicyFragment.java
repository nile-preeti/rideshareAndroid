package com.ridesharedriver.app.fragement;

import static com.ridesharedriver.app.Server.Server.BASE_URL;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ridesharedriver.app.R;
import com.ridesharedriver.app.acitivities.HomeActivity;
import com.ridesharedriver.app.custom.SetCustomFont;


//privacy policy
public class PrivacyPolicyFragment extends Fragment {

    private View rootView;
    private static final String URL = BASE_URL + "app-privacy-policy"; // For App

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_privacy_policy, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar("Privacy Policy");
        WebView webView = rootView.findViewById(R.id.webview_privacy_policy);
        // Enable Javascript
        WebSettings webSettings = webView.getSettings();

        webView.setWebViewClient(new WebViewClient());
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(false);
        webView.setWebViewClient(new WebClient());
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webView.loadUrl(URL);
        SetCustomFont font = new SetCustomFont();

        font.overrideFonts(requireContext(), rootView);

        return rootView;
    }

    //web view hosting
    private class WebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            // Do something with the event here
            return true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            try {
                if (url.startsWith("mailto:")) {
                    String[] emailsend = url.split(":");
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailsend[1]});
                    intent.setType("message/rfc822");
                    startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                }

                if (url.startsWith("http")) {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            if (Uri.parse(url).getHost().equals(URL)) {
//                // This is my web site, so do not override; let my WebView load the page
//                return false;
//            }

            // reject anything other
            return true;
        }
    }
}
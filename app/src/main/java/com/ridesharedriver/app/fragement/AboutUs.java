package com.ridesharedriver.app.fragement;

import static com.ridesharedriver.app.Server.Server.BASE_URL;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

public class AboutUs extends Fragment {
    private View rootView;
    final static String URL = BASE_URL+"app-about-us";
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
        rootView = inflater.inflate(R.layout.fragment_about_us, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar("About Us");
        WebView webView = rootView.findViewById(R.id.webview_about_us);
        // Enable Javascript
        WebSettings webSettings = webView.getSettings();

        webView.setWebViewClient(new WebViewClient());
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(false);
        webView.setWebViewClient(new WebViewClient());
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webView.loadUrl(URL);
        SetCustomFont font = new SetCustomFont();

        font.overrideFonts(requireContext(), rootView);

        return rootView;
    }
}
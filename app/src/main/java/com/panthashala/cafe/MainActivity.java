package com.panthashala.cafe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private WebView web;
    final String webUrl = "https://panthashalacafe.com/";

    private SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout relativeLayout;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        web = (WebView) findViewById(R.id.myweb);
        web.loadUrl(webUrl);

        WebSettings mywebsettings = web.getSettings();
        mywebsettings.setJavaScriptEnabled(true);

        web.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                internetcheck();
                super.onReceivedError(view, request, error);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    return false;
                }

                // Otherwise allow the OS to handle things like tel, mailto, etc.
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });

        //WebView
        web.getSettings().setLoadsImagesAutomatically(true);
        web.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        //web.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //web.getSettings().setAppCacheEnabled(true);
        web.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mywebsettings.setDomStorageEnabled(true);
        //noinspection deprecation
        mywebsettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mywebsettings.setUseWideViewPort(true);
        mywebsettings.setSavePassword(true);
        mywebsettings.setSaveFormData(true);
        mywebsettings.setEnableSmoothTransition(true);

        //ProgressBar
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        web.setWebChromeClient(new WebChromeClient() {

            // page loading progress, gone when fully loaded
            public void onProgressChanged(WebView view, int progress) {


                if (progress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }

                if (progress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                }
                progressBar.setProgress(progress);
            }
        });

        //pull to refresh
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
                web.reload();
            },1000);
        });

        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark),
                getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark)


        );

        //internet connection check
        relativeLayout = (RelativeLayout) findViewById(R.id.nonet);
        internetcheck();
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) {
            web.goBack();
        } else {


            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

            builder.setMessage("Are you sure you want to Exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialogInterface, i) -> finish())

                    .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());
            android.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
    public void internetcheck(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobiledata = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if(mobiledata.isConnected()){
            web.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
            web.reload();
        }

        else if(wifi.isConnected()){

            web.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
            web.reload();
        }

        else{

            web.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);

        }

    }

}
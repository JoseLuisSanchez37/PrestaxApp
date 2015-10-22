package com.prestax.app.Activities;

import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.prestax.app.Listeners.OnErrorLoadPayPal;
import com.prestax.app.Networking.LoadingDialog;

/**
 * Created by jose.sanchez on 10/10/2015.
 */
public class PayPalWeb extends WebViewClient {

    private OnErrorLoadPayPal listener;
    private LoadingDialog progressBar;

    public PayPalWeb(LoadingDialog progressBar, OnErrorLoadPayPal listener) {
        this.progressBar=progressBar;
        this.listener = listener;
        progressBar.show();
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // TODO Auto-generated method stub
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        // TODO Auto-generated method stub
        super.onPageFinished(view, url);
        progressBar.dismiss();
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        progressBar.dismiss();
        listener.onError(error.toString());
    }

}

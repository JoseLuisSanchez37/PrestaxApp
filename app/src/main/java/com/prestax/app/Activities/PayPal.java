package com.prestax.app.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.prestax.app.Listeners.OnErrorLoadPayPal;
import com.prestax.app.Networking.LoadingDialog;
import com.prestax.app.R;

public class PayPal extends Activity implements OnErrorLoadPayPal {

    public static final String TYPE = "type";
    public static final int PAGO_TOTAL = 1;
    public static final int PAGO_MENSUAL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_pay_pal);

        int type = getIntent().getExtras().getInt(TYPE);
        loadPayPal(type);

    }

    private void loadPayPal(int type) {
        WebView webView = (WebView) findViewById(R.id.web_paypal);
        webView.getSettings().setJavaScriptEnabled(true);

        String url = getString(R.string.url_paypal);
        if (type == PAGO_TOTAL)
            url = url + "&" + getString(R.string.type_paypal_total);
        else if(type == PAGO_MENSUAL)
            url = url + "&" + getString(R.string.type_paypal_month);

        webView.loadUrl(url);
        webView.setWebViewClient(new PayPalWeb(new LoadingDialog(this), this));

    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

}

package tkmce.hestia20.eventDetailed;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import tkmce.hestia20.R;

public class PaymentActivity extends AppCompatActivity {

    private String post_data;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Bundle bundle = getIntent().getExtras();
        progressBar = findViewById(R.id.pb_load);
        progressBar.getProgressDrawable().setColorFilter(
                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);

        if (bundle != null) {
            post_data = bundle.getString("data");
        } else {
            Toast.makeText(getApplicationContext(), "Exception Occurred", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

        WebView webView = findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new myWebClient());

        String postData = null;
        try {
            postData = "json_data=" + URLEncoder.encode(post_data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            assert postData != null;
            String PAYMENT_URL = "https://www.hestia.live/payment/prepay.php";
            webView.postUrl(PAYMENT_URL, postData.getBytes());
        } catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(), "Exception", Toast.LENGTH_SHORT).show();
        }
    }

    private class myWebClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            String FINISH_URL = "hestia.live/payment/finish";
            if (url.contains(FINISH_URL)) {
                finish();
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
    }

    private boolean backPressed = false;
    @Override
    public void onBackPressed() {
        if (backPressed) {
            super.onBackPressed();
        } else {
            Toast.makeText(getApplicationContext(), R.string.back_press, Toast.LENGTH_SHORT).show();
            backPressed = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressed = false;
                }
            }, 1500);
        }
    }
}
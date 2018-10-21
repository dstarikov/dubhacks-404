package dubhacks404.dubhacks_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

    private WebView dataView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

//        dataView = (WebView) findViewById(R.id.dataView_webView);
//
//        dataView.setWebViewClient(new WebViewClient());
//        WebSettings settings = dataView.getSettings();
//        settings.setJavaScriptEnabled(true);
//        settings.setDomStorageEnabled(true);
//        settings.setLoadWithOverviewMode(true);
//        settings.setUseWideViewPort(true);
//        settings.setBuiltInZoomControls(true);
//        settings.setDisplayZoomControls(false);
//        settings.setSupportZoom(true);
//        settings.setDefaultTextEncodingName("utf-8");
//        Intent intent = getIntent();
//        Log.e("Web", "Base URL " + intent.getStringExtra("base"));
//        Log.e("Web", "Response " + intent.getStringExtra("response"));
//        dataView.loadDataWithBaseURL(intent.getStringExtra("base"), intent.getStringExtra("response"),null,null,null);
    }



}

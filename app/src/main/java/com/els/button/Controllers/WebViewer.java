package com.els.button.Controllers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.els.button.R;


/**
 * Created by cameron on 3/29/16.
 */
public class WebViewer extends AppCompatActivity {
    // instance variables set by calling activity
    public String sheet;
    public String id;
    public String pin;
    public String host;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.webViewToolbar);
        setSupportActionBar(toolbar);

        // set the address for the host

        Log.d("WebViewer", "In on create");
        Bundle bundle = getIntent().getExtras();
        this.sheet = bundle.getString("sheet");
        this.id = bundle.getString("id");
        this.pin = bundle.getString("pin");
        this.host = bundle.getString("host");

        Log.d("WebViewer", "Sheet: " + sheet + ", id: " + id + ", pin: " + pin + ", host: " + host);


        WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.loadUrl(getDisplayClientLocation() + "?id=" + id + "&pin=" + pin + "&sheet=" + sheet + "#");
        Log.d("WebViewer", "url: " + myWebView.getUrl());
        myWebView.setWebViewClient(new ELSWebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web_view, menu);
        Log.d("WebViewer", "on create options menu");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Log.d("WebViewer", "on options item selected");
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Use When the user clicks a link from a web page in your WebView
    private class ELSWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals("www.centerend.com")) {
                return false;

            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }

    private String getServerURL() {
        return "http://" + host + ":8080/ContentServer/ContentServer";
    }

    private String getDisplayClientLocation() {
        return "http://" + host + ":8091/DisplayClient/NewDisplay.html";
    }

}



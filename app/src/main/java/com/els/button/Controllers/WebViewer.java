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
    String sheet;
    String id;
    String pin;

    //TODO: Change this ip address!
    String HOST = "192.168.2.3";
    String SERVER_LOCATION = "http://" + HOST + ":8080/ContentServer/ContentServer";
    String DISPLAY_CLIENT_LOCATION = "http://" + HOST + ":8091/DisplayClient/NewDisplay.html";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.webViewToolbar);
        setSupportActionBar(toolbar);

        Log.d("WebViewer", "In on create");
        Bundle bundle = getIntent().getExtras();
        this.sheet = bundle.getString("sheet");
        this.id = bundle.getString("id");
        this.pin = bundle.getString("pin");

        Log.d("WebViewer", "Sheet: " + sheet + ", id: " + id + ", pin: " + pin);


        WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.loadUrl(DISPLAY_CLIENT_LOCATION + "?id=" + id + "&pin=" + pin + "&sheet=" + sheet + "#");//"http://10.144.50.214:8091/DisplayClient/NewDisplay.html");
        Log.d("WebViewer", "url: " + myWebView.getUrl());
        myWebView.setWebViewClient(new MyWebViewClient());
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
    private class MyWebViewClient extends WebViewClient {
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

}



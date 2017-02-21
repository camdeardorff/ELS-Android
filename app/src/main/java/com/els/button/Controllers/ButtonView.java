package com.els.button.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.els.button.Models.ELSEntity;
import com.els.button.Models.ELSIoT;
import com.els.button.Models.ELSLimri;
import com.els.button.Models.ELSLimriButtonPressAction;
import com.els.button.Models.InventoryListAdapter;
import com.els.button.Models.InventoryListAdapterDelegate;
import com.els.button.Models.UrlBuilder;
import com.els.button.Networking.Callbacks.ELSRestRequestCallback;
import com.els.button.Networking.ELSRest;
import com.els.button.R;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class ButtonView extends AppCompatActivity implements InventoryListAdapterDelegate {

    // instance variable for host ip, the value is retrieved from the strings file
    private static String hostIp = ""; //"192.168.0.29";
    private static InventoryListAdapter listAdapter = null;

    static final int NEW_INVENTORY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.buttonViewToolbar);
        setSupportActionBar(toolbar);

        // get the host ip address from the strings file
        hostIp = getString(R.string.HOST_IP);
        Log.d("ButtonView", "host ip from strings: " + hostIp);
        updateList();

        ELSRest rest = new ELSRest(hostIp, "0987654321", "1111");
        rest.login(new ELSRestRequestCallback() {
            @Override
            public void onSuccess(Document document, Boolean result) {
                Log.d("ButtonView", "Test Login success");
            }

            @Override
            public void onFailure() {
                Log.d("ButtonView", "Test Login failure");
            }
        });


    }


    private void updateList() {
        final ListView listView = (ListView) findViewById(R.id.listView);

        ArrayList<ELSEntity> inventories = this.getInventories();


        if (this.listAdapter == null) {
            listAdapter = new InventoryListAdapter(this, this, inventories);
            listView.setAdapter(listAdapter);
        } else {
            listAdapter.clear();
            listAdapter.addAll(inventories);
            listAdapter.notifyDataSetChanged();
        }

        // Get a handler that can be used to post to the main thread
        final Handler mainHandler = new Handler(this.getMainLooper());


        for (ELSEntity elsEntity : inventories) {
            if (elsEntity.getClass() == ELSLimri.class) {
                final ELSLimri limri = (ELSLimri) elsEntity;

                limri.updateStatus(this.hostIp, new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message message) {

                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                limri.save();
                                listAdapter.notifyDataSetChanged();
                            }
                        };
                        mainHandler.post(myRunnable);
                        return false;
                    }
                });
            }
        }
    }


    private ArrayList<ELSEntity> getInventories() {
        Log.d("ButtonView", "get inventories");
        ArrayList<ELSEntity> inventories = new ArrayList<ELSEntity>();

        ArrayList<ELSLimri> limriInventories = new ArrayList<ELSLimri>(SQLite.select().from(ELSLimri.class).queryList());

        inventories.addAll(limriInventories);
        inventories.addAll(SQLite.select().from(ELSIoT.class).queryList());

        Collections.sort(inventories, new Comparator<ELSEntity>() {
            @Override
            public int compare(ELSEntity elsEntity, ELSEntity t1) {
                return elsEntity.dateAdded.compareTo(t1.dateAdded);
            }
        });

        return inventories;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_button_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_create_new) {
            Intent intent = new Intent(this, InventoryConfigurator.class);
            intent.putExtra("host", hostIp);
            startActivityForResult(intent, NEW_INVENTORY_REQUEST);
        } else if (id == R.id.action_refresh) {
            this.updateList();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ButtonView", "onActivityResult");
        updateList();

    }

    @Override
    public void limriButtonWasPressedWithLimriInfo(ELSLimri elsLimri, ELSLimriButtonPressAction action) {

        UrlBuilder urlBuilder = new UrlBuilder(hostIp);
        String url = urlBuilder.create(elsLimri, action);

        Log.d("ButtonView", "url from url builder: " + url);

        if (url != null) {
            Log.d("ButtonView","limriButtonWasPressedWithLimriInfo");
            Intent intent = new Intent(this, WebViewer.class);

            intent.putExtra("url", url);
            startActivity(intent);
        }
    }

    @Override
    public void iotButtonWasPressedWithIotInfoAndSetQuestionValue(final ELSIoT elsIoT, final String value) {
        Log.d("ButtonView", "iotButtonWasPressedWithSetQuestionValue");

        final ELSRest rest = new ELSRest(hostIp, elsIoT.getInventoryID(), elsIoT.getPin());
        rest.login(new ELSRestRequestCallback() {
            @Override
            public void onSuccess(Document document, Boolean result) {
                Log.d("ButtonView", "Login was successful");
                //make a hashmap for the questions and answers to send
                HashMap<String, String> questionAndAnswer = new HashMap<String, String>();
                questionAndAnswer.put(elsIoT.getqID(), value);
                //send the questions and answers to the system in a set question

                rest.setQuestion(questionAndAnswer, new ELSRestRequestCallback() {
                    @Override
                    public void onSuccess(Document document, Boolean result) {
                        Log.d("ButtonView", "set questions was a success!");
                        rest.logout(null);
                    }

                    @Override
                    public void onFailure() {
                        Log.d("ButtonView", "set questions was a failure");
                        rest.logout(null);
                    }
                });

            }

            @Override
            public void onFailure() {
                Log.d("ButtonView", "log in was a failure");
            }
        });
    }
}
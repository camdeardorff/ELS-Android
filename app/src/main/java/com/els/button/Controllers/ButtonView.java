package com.els.button.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.els.button.Models.ELSEntity;
import com.els.button.Models.ELSIoT;
import com.els.button.Models.ELSLimri;
import com.els.button.Models.InventoryListAdapter;
import com.els.button.Models.InventoryListAdapterDelegate;
import com.els.button.Networking.ELSRest;
import com.els.button.R;
import com.raizlabs.android.dbflow.sql.language.SQLite;

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
    }


    private void updateList() {
        final ListView listView = (ListView) findViewById(R.id.listView);

        if (this.listAdapter == null) {
            listAdapter = new InventoryListAdapter(this, this, this.getInventories());
            listView.setAdapter(listAdapter);
        } else {
            listAdapter.clear();
            listAdapter.addAll(this.getInventories());
            listAdapter.notifyDataSetChanged();
        }
    }


    private ArrayList<ELSEntity> getInventories() {
        Log.d("ButtonView", "get inventories");
        ArrayList<ELSEntity> inventories = new ArrayList<ELSEntity>();

        ArrayList<ELSLimri> limriInventories = new ArrayList<ELSLimri>(SQLite.select().from(ELSLimri.class).queryList());

        for (ELSLimri inventory : limriInventories) {

            ELSRest rest = new ELSRest(hostIp, inventory.getInventoryID(), inventory.getPin());
            if (rest.login()) {
                inventory.update(rest.getInventoryStatus(inventory.getStatusSheet()));
            }
        }

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
        } else if (id == R.id.action_settings) {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ButtonView", "onActivityResult");
        updateList();

    }

    @Override
    public void limriButtonWasPressedWithLimriInfo(ELSLimri elsLimri) {
        Log.d("ButtonView","limriButtonWasPressedWithLimriInfo");
        Intent intent = new Intent(this, WebViewer.class);

        intent.putExtra("sheet", elsLimri.getStatusSheet());
        intent.putExtra("id", elsLimri.getInventoryID());
        intent.putExtra("pin", elsLimri.getPin());
        Log.d("ButtonView", "putting host as: " + hostIp );
        intent.putExtra("host", hostIp);
        startActivity(intent);

    }

    @Override
    public void iotButtonWasPressedWithIotInfoAndSetQuestionValue(ELSIoT elsIoT, String value) {
        Log.d("ButtonView", "iotButtonWasPressedWithSetQuestionValue");

        ELSRest comm = new ELSRest(hostIp, elsIoT.getInventoryID(), elsIoT.getPin());
        if (comm.login()) {
            Log.d("ButtonView", "Login was successful");
            //make a hashmap for the questions and answers to send
            HashMap<String, String> questionAndAnswer = new HashMap<String, String>();
            questionAndAnswer.put(elsIoT.getqID(), value);
            //send the questions and answers to the system in a set question
            if (comm.setQuestion(questionAndAnswer)) {
                //SUCCESS
                Log.d("ButtonView", "set questions was a success!");
            } else {
                Log.d("ButtonView", "set questions was a failure");

            }
            comm.logout();
        } else {
            Log.d("ButtonView", "log in was a failure");
        }
    }
}
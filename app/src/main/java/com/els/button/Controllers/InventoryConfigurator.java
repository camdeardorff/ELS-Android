package com.els.button.Controllers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.els.button.Models.ELSLimri;
import com.els.button.R;

public class InventoryConfigurator extends AppCompatActivity {

    final int SUCCESSFUL_INVENTORY_CREATION = 1;
    final int CANCELED_INVENTORY_CREATION_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_configurator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        final String host = bundle.getString("host");

        final TextView iidTextView = (TextView) findViewById(R.id.activity_inventory_configurator_iid_textview);
        final TextView pinTextView = (TextView) findViewById(R.id.activity_inventory_configurator_pin_textview);

        Button createButton = (Button) findViewById(R.id.activity_inventory_configurator_button_done);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String iid = iidTextView.getText().toString();
                String pin = pinTextView.getText().toString();

                Log.d("InventoryConfigurator", "iid: " + iid);
                Log.d("InventoryConfigurator", "pin: " + pin);

                if (!iid.isEmpty() && !pin.isEmpty()) {
                    ELSLimri newLimri = new ELSLimri(iid, pin, "Title", "Description", "status");
                    newLimri.save();
                    Log.d("InventoryConfigurator", "saved new elslimri");
//                    finishActivity(SUCCESSFUL_INVENTORY_CREATION);
                    setResult(SUCCESSFUL_INVENTORY_CREATION);
                    finish();
                } else {
                    // report errors
                    Log.d("InventoryConfigurator", "iid and pin are empty");
                }


            }
        });

        Button cancelButton = (Button) findViewById(R.id.activity_inventory_configurator_button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//               finishActivity(CANCELED_INVENTORY_CREATION_REQUEST);
                setResult(CANCELED_INVENTORY_CREATION_REQUEST);
                finish();
            }
        });
    }




}

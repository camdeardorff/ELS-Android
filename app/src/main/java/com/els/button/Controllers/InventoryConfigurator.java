package com.els.button.Controllers;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.els.button.Models.ELSLimri;
//import com.els.button.Models.ELSLimriButton;
import com.els.button.Models.ELSLimriButton;
import com.els.button.Models.ELSLimriButtonPressAction;
import com.els.button.Models.ELSLimriColor;
import com.els.button.Networking.ELSRest;
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
                    ELSRest rest = new ELSRest(host, iid, pin);
                    if (rest.login()) {
                        // create the new inventory
                        ELSLimri newLimri = new ELSLimri(iid, pin, "Title", "Description", "Button");
                        // create the button for this inventory and save it
                        ELSLimriButton newButton = new ELSLimriButton("btn 1", ELSLimriColor.GREEN, ELSLimriButtonPressAction.NOTHING, "");
                        newButton.save();
                        // associate the button with the inventory and save
                        newLimri.setButton(newButton);
                        newLimri.save();

                        newLimri.updateStatus(rest.getInventoryStatus(newLimri.getStatusSheet()));
                        Log.d("InventoryConfigurator", "saved new elslimri");
                        setResult(SUCCESSFUL_INVENTORY_CREATION);
                        finish();
                    } else {
                        // 1. Instantiate an AlertDialog.Builder with its constructor
                        AlertDialog.Builder builder = new AlertDialog.Builder(InventoryConfigurator.this);

                        // 2. Chain together various setter methods to set the dialog characteristics
                        builder.setMessage(R.string.activity_inventory_configurator_alert_message)
                                .setTitle(R.string.activity_inventory_configurator_alert_title);

                        // 3. Get the AlertDialog from create()
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }

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
                setResult(CANCELED_INVENTORY_CREATION_REQUEST);
                finish();
            }
        });
    }
}

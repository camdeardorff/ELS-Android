package com.els.button.Controllers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.els.button.Interfaces.StatusUpdateResult;
import com.els.button.Models.ELSLimri;
import com.els.button.Models.ELSLimriButton;
import com.els.button.Models.ELSLimriButtonPressAction;
import com.els.button.Models.ELSLimriColor;
import com.els.button.Networking.Callbacks.ELSRestRequestCallback;
import com.els.button.Networking.ELSRest;
import com.els.button.R;

import org.w3c.dom.Document;

//import com.els.button.Models.ELSLimriButton;

public class InventoryConfigurator extends AppCompatActivity {

    final int SUCCESSFUL_INVENTORY_CREATION = 1;
    final int UNSUCCESSFUL_INVENTORY_CREATION = 0;
    final int CANCELED_INVENTORY_CREATION_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_configurator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final TextView iidTextView = (TextView) findViewById(R.id.activity_inventory_configurator_iid_textview);
        final TextView pinTextView = (TextView) findViewById(R.id.activity_inventory_configurator_pin_textview);

        Button createButton = (Button) findViewById(R.id.activity_inventory_configurator_button_done);
        if (createButton != null) {
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!isConnected()) {
                        showConnectivityAlert();
                    } else if (iidTextView != null && pinTextView != null) {


                        final String iid = iidTextView.getText().toString();
                        final String pin = pinTextView.getText().toString();

                        Log.d("InventoryConfigurator", "iid: " + iid);
                        Log.d("InventoryConfigurator", "pin: " + pin);


                        // Get a handler that can be used to post to the main thread
                        final Handler mainHandler = new Handler(getMainLooper());
                        final String defaultServer = getString(R.string.CONTENT_SERVER);
                        final String defaultSheet = getString(R.string.default_sheet_name);

                        if (!iid.isEmpty() && !pin.isEmpty()) {
                            final ELSRest rest = new ELSRest(defaultServer, iid, pin);

                            rest.login(new ELSRestRequestCallback() {
                                @Override
                                public void onSuccess(Document document, Boolean result) {

                                    if (result) {
                                        // create the new inventory
                                        final ELSLimri newLimri = new ELSLimri(defaultServer, iid, pin, "Title", "Description", defaultSheet);
                                        // create the button for this inventory and save it
                                        final ELSLimriButton newButton = new ELSLimriButton("btn 1", ELSLimriColor.GREEN, ELSLimriColor.BLACK, ELSLimriButtonPressAction.NOTHING, "", true, 1);
                                        newButton.save();
                                        // associate the button with the inventory and save
                                        newLimri.setButton(newButton);
                                        newLimri.save();

                                        newLimri.updateStatus(new StatusUpdateResult() {
                                            @Override
                                            public void success() {
                                                newLimri.save();
                                                Log.d("InventoryConfigurator", "saved new elslimri");
                                                exit(SUCCESSFUL_INVENTORY_CREATION);
                                            }

                                            @Override
                                            public void failureFromCredentials() {
                                                newButton.delete();
                                                newLimri.delete();
                                                showFailureMessage();
                                            }

                                            @Override
                                            public void failureFromConnectivity() {
                                                newButton.delete();
                                                newLimri.delete();
                                                showFailureMessage();
                                            }
                                        });

                                    } else {
                                        // failed, got the sheet but was not logged in.
                                        Runnable myRunnable = new Runnable() {
                                            @Override
                                            public void run() {
                                                showFailureMessage();
                                            }
                                        };
                                        mainHandler.post(myRunnable);
                                    }
                                }

                                @Override
                                public void onFailure() {
                                    // failed, did not get the sheet... server?
                                    Runnable myRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            showServerConnectivityAlert();
                                        }
                                    };
                                    mainHandler.post(myRunnable);
                                }
                            });
                        }
                    }
                }
            });
        }

        Button cancelButton = (Button) findViewById(R.id.activity_inventory_configurator_button_cancel);
        if (cancelButton != null) {
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(CANCELED_INVENTORY_CREATION_REQUEST);
                    finish();
                }
            });
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void showFailureMessage() {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(InventoryConfigurator.this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.activity_inventory_configurator_alert_message)
                .setTitle(R.string.activity_inventory_configurator_alert_title);

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showConnectivityAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_network_unreachable_message)
                .setTitle(R.string.alert_network_unreachable_title);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showServerConnectivityAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_server_unreachable_message)
                .setTitle(R.string.alert_server_unreachable_title);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void exit(Integer result) {
        setResult(result);
        finish();
    }
}

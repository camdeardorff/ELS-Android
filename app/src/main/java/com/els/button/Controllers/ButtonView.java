package com.els.button.Controllers;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.els.button.Interfaces.ELSActionManagerCallback;
import com.els.button.Interfaces.InventoryListAdapterDelegate;
import com.els.button.Interfaces.StatusUpdateResult;
import com.els.button.Models.ELSActionManager;
import com.els.button.Models.ELSEntity;
import com.els.button.Models.ELSIoT;
import com.els.button.Models.ELSLimri;
import com.els.button.Models.ELSLimriButton;
import com.els.button.Models.ELSLimriButtonPressAction;
import com.els.button.Models.ELSLimriColor;
import com.els.button.Models.InventoryListAdapter;
import com.els.button.Networking.Callbacks.ELSRestRequestCallback;
import com.els.button.Networking.ELSRest;
import com.els.button.R;
import com.els.button.Views.ELSInventoryAttributesDialog;
import com.els.button.Views.ELSInventoryOptionsDialog;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class ButtonView extends AppCompatActivity implements InventoryListAdapterDelegate, ELSInventoryAttributesDialog.Listener, ELSInventoryOptionsDialog.Listener {

    // instance variable for host ip, the value is retrieved from the strings file
    private static String contentServer;
    private static String displayClient;
    private static InventoryListAdapter listAdapter = null;

    private static final String ATTRIBUTE_DIALOG_IDENTIFIER = "ATTRIBUTE_DIALOG_IDENTIFIER";
    private static final String OPTIONS_DIALOG_IDENTIFIER = "OPTIONS_DIALOG_IDENTIFIER";

    static final int NEW_INVENTORY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.buttonViewToolbar);
        setSupportActionBar(toolbar);

        // get the host ip address from the strings file
        contentServer = getString(R.string.CONTENT_SERVER);
        displayClient = getString(R.string.DISPLAY_CLIENT);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.newInventoryFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ELSInventoryAttributesDialog attributesDialog = new ELSInventoryAttributesDialog();
                attributesDialog.setTitle(getString(R.string.add_inventory_title));
                attributesDialog.setMessage(getString(R.string.add_inventory_message));

                attributesDialog.show(getFragmentManager(), ATTRIBUTE_DIALOG_IDENTIFIER);
            }
        });


        if (isConnected()) {
            updateList();
        } else {
            showNetworkConnectivityAlert();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isConnected()) {
            updateList();
        } else {
            showNetworkConnectivityAlert();
        }
    }

    // updates the list view with inventories from the database
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

                limri.updateStatus(new StatusUpdateResult() {
                    @Override
                    public void success() {
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                limri.save();
                                listAdapter.notifyDataSetChanged();
                            }
                        };
                        mainHandler.post(myRunnable);
                    }

                    @Override
                    public void failureFromCredentials() {
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                showReconfigureInventoryAlert();
                            }
                        };
                        mainHandler.post(myRunnable);
                    }

                    @Override
                    public void failureFromConnectivity() {
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

    // get the list of inventories from the database
    private ArrayList<ELSEntity> getInventories() {
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

    // checks connectivity status, returns true if connected to internet
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // abstraction over alert dialog
    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(title);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showNetworkConnectivityAlert() {
        showAlert(getString(R.string.alert_network_unreachable_title),
                getString(R.string.alert_network_unreachable_message));
    }

    private void showServerConnectivityAlert() {
        showAlert(getString(R.string.alert_server_unreachable_title),
                getString(R.string.alert_server_unreachable_message));
    }

    private void showFailureAlert() {
        showAlert(getString(R.string.activity_inventory_configurator_alert_title),
                getString(R.string.activity_inventory_configurator_alert_message));
    }

    private void showReconfigureInventoryAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_reconfigure_inventory_message)
                .setTitle(R.string.alert_reconfigure_inventory_title);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("ButtonView", "reconfigure alert update button pressed");
            }
        });
        builder.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("ButtonView", "reconfigure alert remove button pressed");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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

         if (id == R.id.action_refresh) {
            if (isConnected()) {
                updateList();
            } else {
                showNetworkConnectivityAlert();
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void limriButtonWasPressed(ELSLimri elsLimri, ELSLimriButtonPressAction action) {
        // do all actions asynchronously sequentially
        ELSRest rest = new ELSRest(contentServer, elsLimri.getInventoryID(), elsLimri.getPin());

        final Context context = this;
        ELSActionManager actionManager = new ELSActionManager(context, elsLimri);
        actionManager.execute(new ELSActionManagerCallback() {
            @Override
            public void loadURL(String url) {
                if (url != null) {
                    Intent intent = new Intent(context, WebViewer.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                }
            }

            @Override
            public void refresh() {
                updateList();
            }
        });

    }

    @Override
    public void limriConfigurationButtonWasPressed(ELSLimri elsLimri) {
        ELSInventoryOptionsDialog inventoryOptionsDialog = new ELSInventoryOptionsDialog();
        inventoryOptionsDialog.setLimri(elsLimri);
        inventoryOptionsDialog.show(getFragmentManager(), OPTIONS_DIALOG_IDENTIFIER);
    }

    @Override
    public void iotButtonWasPressed(final ELSIoT elsIoT, final String value) {

        final ELSRest rest = new ELSRest(contentServer, elsIoT.getInventoryID(), elsIoT.getPin());
        rest.login(new ELSRestRequestCallback() {
            @Override
            public void onSuccess(Document document, Boolean result) {
                //make a hashmap for the questions and answers to send
                HashMap<String, String> questionAndAnswer = new HashMap<String, String>();
                questionAndAnswer.put(elsIoT.getqID(), value);
                //send the questions and answers to the system in a set question

                rest.setQuestion(questionAndAnswer, new ELSRestRequestCallback() {
                    @Override
                    public void onSuccess(Document document, Boolean result) {
                        rest.logout(null);
                    }

                    @Override
                    public void onFailure() {
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


    @Override
    public void onDeleteButtonClick(DialogFragment dialog, ELSLimri limri) {
        // delete the inventory
        limri.delete();
        // dismiss the dialog
        dialog.dismiss();
        // update the list
        updateList();
    }

    @Override
    public void onEditButtonClick(DialogFragment dialog, ELSLimri limri) {
        // open up the edit dialog

        dialog.dismiss();

        ELSInventoryAttributesDialog attributesDialog = new ELSInventoryAttributesDialog();

        attributesDialog.setPreFillInventoryID(limri.getInventoryID());
        attributesDialog.setPreFillPin(limri.getPin());
        attributesDialog.setElsLimri(limri);
        attributesDialog.setTitle(getString(R.string.reconfigure_inventory_title));
        attributesDialog.setMessage(getString(R.string.reconfigure_inventory_message));

        attributesDialog.show(getFragmentManager(), ATTRIBUTE_DIALOG_IDENTIFIER);
    }

    @Override
    public void onAttributeUpdate(DialogFragment dialog, final ELSLimri limri, final String iid, final String pin) {

        // Get a handler that can be used to post to the main thread
        final Handler mainHandler = new Handler(getMainLooper());

        Boolean createNewLimri = limri == null;
        if (createNewLimri) {

            // check that both pin and id have some value
            if (!iid.isEmpty() && !pin.isEmpty()) {
                final String defaultServer = getString(R.string.CONTENT_SERVER);
                final String defaultSheet = getString(R.string.DEFAULT_SHEET_NAME);
                // rest object to authenticate with the default server
                final ELSRest rest = new ELSRest(defaultServer, iid, pin);
                // try to log in
                rest.login(new ELSRestRequestCallback() {
                    @Override
                    public void onSuccess(Document document, Boolean result) {
                        // successful request, check request
                        if (result) {
                            // successful login
                            // create the new inventory
                            final ELSLimri newLimri = new ELSLimri(defaultServer, iid, pin, "Title", "Description", defaultSheet);
                            // create the button for this inventory and save it
                            final ELSLimriButton newButton = new ELSLimriButton("btn 1", ELSLimriColor.GREEN, ELSLimriColor.BLACK, ELSLimriButtonPressAction.NOTHING, "", true, 1);
                            newButton.save();
                            // associate the button with the inventory and save
                            newLimri.setButton(newButton);
                            newLimri.save();
                            // update the status of the inventory
                            newLimri.updateStatus(new StatusUpdateResult() {
                                @Override
                                public void success() {
                                    Runnable myRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            // successful update, save and update the list
                                            newLimri.save();
                                            updateList();
                                        }
                                    };
                                    mainHandler.post(myRunnable);
                                }

                                @Override
                                public void failureFromCredentials() {
                                    Runnable myRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            // could not log in upon update?
                                            // something really went wrong, delete the row and show the failure alert
                                            newButton.delete();
                                            newLimri.delete();
                                            showFailureAlert();
                                        }
                                    };
                                    mainHandler.post(myRunnable);
                                }

                                @Override
                                public void failureFromConnectivity() {
                                    Runnable myRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            // could not connect
                                            newButton.delete();
                                            newLimri.delete();
                                            showServerConnectivityAlert();
                                        }
                                    };
                                    mainHandler.post(myRunnable);
                                }
                            });

                        } else {
                            // failed, got the sheet but was not logged in.
                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    showFailureAlert();
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

        } else {
            // not new, reconfiguring
            if (!iid.isEmpty() && !pin.isEmpty()) {
                final ELSRest rest = new ELSRest(limri.getServerLocation(), iid, pin);
                // log in with new credentials
                rest.login(new ELSRestRequestCallback() {
                    @Override
                    public void onSuccess(Document document, Boolean result) {
                        // request successful
                        if (result) {
                            // logged in successfully
                            limri.updateStatus(new StatusUpdateResult() {
                                @Override
                                public void success() {
                                    Runnable myRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            // save the inventory and update the list
                                            limri.save();
                                            updateList();
                                        }
                                    };
                                    mainHandler.post(myRunnable);
                                }

                                @Override
                                public void failureFromCredentials() {
                                    Runnable myRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            // failed because of bad credentials
                                            // show the alert once again
                                            ELSInventoryAttributesDialog signInDialog = new ELSInventoryAttributesDialog();

                                            signInDialog.setPreFillInventoryID(limri.getInventoryID());
                                            signInDialog.setPreFillPin(limri.getPin());
                                            signInDialog.setElsLimri(limri);
                                            signInDialog.setTitle(getString(R.string.reconfigure_inventory_title));
                                            signInDialog.setMessage(getString(R.string.reconfigure_inventory_failure_message));

                                            signInDialog.show(getFragmentManager(), ATTRIBUTE_DIALOG_IDENTIFIER);
                                        }
                                    };
                                    mainHandler.post(myRunnable);
                                }

                                @Override
                                public void failureFromConnectivity() {
                                    Runnable myRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            showNetworkConnectivityAlert();
                                        }
                                    };
                                    mainHandler.post(myRunnable);
                                }
                            });


                        } else {
                            // failed login
                            // bad credentials, try again
                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    ELSInventoryAttributesDialog signInDialog = new ELSInventoryAttributesDialog();

                                    signInDialog.setPreFillInventoryID(limri.getInventoryID());
                                    signInDialog.setPreFillPin(limri.getPin());
                                    signInDialog.setElsLimri(limri);
                                    signInDialog.setTitle(getString(R.string.reconfigure_inventory_title));
                                    signInDialog.setMessage(getString(R.string.reconfigure_inventory_failure_message));

                                    signInDialog.show(getFragmentManager(), ATTRIBUTE_DIALOG_IDENTIFIER);
                                }
                            };
                            mainHandler.post(myRunnable);
                        }
                    }

                    @Override
                    public void onFailure() {
                        // could not connect to server
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

}
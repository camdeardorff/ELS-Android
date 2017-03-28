package com.els.button.Controllers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.els.button.Interfaces.StatusUpdateResult;
import com.els.button.Models.ELSEntity;
import com.els.button.Models.ELSIoT;
import com.els.button.Models.ELSLimri;
import com.els.button.Models.ELSLimriButtonPressAction;
import com.els.button.Models.InventoryListAdapter;
import com.els.button.Models.InventoryListAdapterDelegate;
import com.els.button.Models.UrlBuilder;
import com.els.button.Networking.Callbacks.ELSRestRequestCallback;
import com.els.button.Networking.ELSRest;
import com.els.button.Networking.Models.ELSInventoryStatusAction;
import com.els.button.R;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class ButtonView extends AppCompatActivity implements InventoryListAdapterDelegate {

    // instance variable for host ip, the value is retrieved from the strings file
    private static String contentServer;
    private static String displayClient;
    private static InventoryListAdapter listAdapter = null;

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

        if (isConnected()) {
            updateList();
        } else {
            showNetworkConnectivityAlert();
        }

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

                limri.updateStatus(this.contentServer, new StatusUpdateResult() {
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

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void showNetworkConnectivityAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_network_unreachable_message)
                .setTitle(R.string.alert_network_unreachable_title);
        AlertDialog dialog = builder.create();
        dialog.show();
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

    private void showServerConnectivityAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_server_unreachable_message)
                .setTitle(R.string.alert_server_unreachable_title);
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

        if (id == R.id.action_create_new) {
            // create intent to go to the Inventory Configurator activity
            Intent intent = new Intent(this, InventoryConfigurator.class);
            intent.putExtra("host", contentServer);
            // start the activity
            startActivityForResult(intent, NEW_INVENTORY_REQUEST);

        } else if (id == R.id.action_refresh) {
            if (isConnected()) {
                updateList();
            } else {
                showNetworkConnectivityAlert();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isConnected()) {
            updateList();
        } else {
            showNetworkConnectivityAlert();
        }
    }


    //TODO: move this to it's own class
    public void testSequentialAsync(final ELSRest rest, final ELSLimri elsLimri,
                                    final int index, final ELSRestRequestCallback callback) {

        // Get a handler that can be used to post to the main thread
        final Handler mainHandler = new Handler(this.getMainLooper());
        final ArrayList<ELSInventoryStatusAction> actions = elsLimri.getButton().getActions();

        Log.d("ButtonView", "sequential async at index: " + index);

        if (index < actions.size()) {

            ELSInventoryStatusAction action = actions.get(index);
            ELSLimriButtonPressAction actionType = action.getType();

            if (actionType == ELSLimriButtonPressAction.INC_QUESTION) {
                // always async
                Log.d("ButtonView", "incriment question");

                rest.incQuestion(action.getLocation(), new ELSRestRequestCallback() {
                    @Override
                    public void onSuccess(Document document, Boolean result) {
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                testSequentialAsync(rest, elsLimri, index + 1, callback);
                            }
                        };
                        mainHandler.post(myRunnable);
                    }

                    @Override
                    public void onFailure() {
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                testSequentialAsync(rest, elsLimri, index + 1, callback);
                            }
                        };
                        mainHandler.post(myRunnable);
                    }
                });


            } else if (actionType == ELSLimriButtonPressAction.SET_QUESTION) {
                // always async
                Log.d("ButtonView", "set question");

                HashMap<String, String> response = new HashMap<String, String>();
                response.put(action.getLocation(), action.getValue());

                rest.setQuestion(response, new ELSRestRequestCallback() {
                    @Override
                    public void onSuccess(Document document, Boolean result) {
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                testSequentialAsync(rest, elsLimri, index + 1, callback);
                            }
                        };
                        mainHandler.post(myRunnable);
                    }

                    @Override
                    public void onFailure() {
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                testSequentialAsync(rest, elsLimri, index + 1, callback);
                            }
                        };
                        mainHandler.post(myRunnable);
                    }
                });


            } else if (actionType == ELSLimriButtonPressAction.LOAD_SHEET) {
                Log.d("ButtonView", "load sheet");

                // loud or quiet
                if (action.getDisplay()) {

                    UrlBuilder urlBuilder = new UrlBuilder(contentServer, displayClient);
                    String url = urlBuilder.create(elsLimri, action);

                    Log.d("ButtonView", "actions count: " + elsLimri.getButton().getActions().size());

                    Log.d("ButtonView", "url from url builder: " + url);

                    if (url != null) {
                        Log.d("ButtonView", "limriButtonWasPressed");
                        Intent intent = new Intent(this, WebViewer.class);

                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                    callback.onSuccess(null, null);
                } else {


                    rest.getSheet(action.getLocation(), new ELSRestRequestCallback() {
                        @Override
                        public void onSuccess(Document document, Boolean result) {
                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    testSequentialAsync(rest, elsLimri, index + 1, callback);
                                }
                            };
                            mainHandler.post(myRunnable);
                        }

                        @Override
                        public void onFailure() {
                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    testSequentialAsync(rest, elsLimri, index + 1, callback);
                                }
                            };
                            mainHandler.post(myRunnable);
                        }
                    });
                }

            } else if (actionType == ELSLimriButtonPressAction.LOAD_URL) {
                Log.d("ButtonView", "load url");

                if (action.getDisplay()) {
                    UrlBuilder urlBuilder = new UrlBuilder(contentServer, displayClient);
                    String url = urlBuilder.create(elsLimri, action);

                    Log.d("ButtonView", "actions count: " + elsLimri.getButton().getActions().size());

                    Log.d("ButtonView", "url from url builder: " + url);

                    if (url != null) {
                        Log.d("ButtonView", "limriButtonWasPressed");
                        Intent intent = new Intent(this, WebViewer.class);

                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                    callback.onSuccess(null, null);
                } else {


                    rest.loadArbitraryUrl(action.getLocation(), new ELSRestRequestCallback() {
                        @Override
                        public void onSuccess(Document document, Boolean result) {
                            testSequentialAsync(rest, elsLimri, index + 1, callback);
                        }

                        @Override
                        public void onFailure() {
                            testSequentialAsync(rest, elsLimri, index + 1, callback);
                        }
                    });
                }

            } else if (actionType == ELSLimriButtonPressAction.REFRESH) {
                Log.d("ButtonView", "refresh");

                updateList();
                testSequentialAsync(rest, elsLimri, index + 1, callback);
            } else if (actionType == ELSLimriButtonPressAction.NOTHING) {
                Log.d("ButtonView", "nothing");

                testSequentialAsync(rest, elsLimri, index + 1, callback);

            }
        } else {
            Log.d("ButtonView", "jumping out");

            callback.onSuccess(null, null);

        }
    }

    @Override
    public void limriButtonWasPressed(ELSLimri elsLimri, ELSLimriButtonPressAction action) {


        // do all actions asynchronously sequentially

        ELSRest rest = new ELSRest(contentServer, elsLimri.getInventoryID(), elsLimri.getPin());
        Log.d("ButtonView", "start test sequentail async");

        testSequentialAsync(rest, elsLimri, 0, new ELSRestRequestCallback() {
            @Override
            public void onSuccess(Document document, Boolean result) {
                Log.d("ButtonView", "On success");
            }

            @Override
            public void onFailure() {
                Log.d("ButtonView", "On failure");
            }
        });

    }

    @Override
    public void iotButtonWasPressed(final ELSIoT elsIoT, final String value) {
        Log.d("ButtonView", "iotButtonWasPressedWithSetQuestionValue");

        final ELSRest rest = new ELSRest(contentServer, elsIoT.getInventoryID(), elsIoT.getPin());
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
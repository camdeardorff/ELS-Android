package com.els.button.Models;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.els.button.Interfaces.ELSActionManagerCallback;
import com.els.button.Networking.Callbacks.ELSRestRequestCallback;
import com.els.button.Networking.ELSRest;
import com.els.button.Networking.Models.ELSInventoryStatusAction;
import com.els.button.R;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Cam on 4/4/17.
 * When given a list of ELSLimriButtonActions and the data to support those actions
 * this model will execute those actions sequentially in an asynchronous manner.
 */

public class ELSActionManager {

    private Context context;
    private ELSLimri limri;

    public ELSActionManager(Context context, ELSLimri limri) {
        this.context = context;
        this.limri = limri;
    }


    public void execute(final ELSActionManagerCallback callback) {
        ELSRest rest = new ELSRest(this.limri.getServerLocation(), this.limri.getInventoryID(), this.limri.getPin());
        sequentialAsync(rest, this.limri, 0, callback);
    }

    private void sequentialAsync(final ELSRest rest, final ELSLimri elsLimri,
                                    final int index, final ELSActionManagerCallback callback) {

        // Get a handler that can be used to post to the main thread
        final Handler mainHandler = new Handler(this.context.getMainLooper());
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
                                sequentialAsync(rest, elsLimri, index + 1, callback);
                            }
                        };
                        mainHandler.post(myRunnable);
                    }

                    @Override
                    public void onFailure() {
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                sequentialAsync(rest, elsLimri, index + 1, callback);
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
                                sequentialAsync(rest, elsLimri, index + 1, callback);
                            }
                        };
                        mainHandler.post(myRunnable);
                    }

                    @Override
                    public void onFailure() {
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                sequentialAsync(rest, elsLimri, index + 1, callback);
                            }
                        };
                        mainHandler.post(myRunnable);
                    }
                });


            } else if (actionType == ELSLimriButtonPressAction.LOAD_SHEET) {
                Log.d("ButtonView", "load sheet");

                // loud or quiet
                if (action.getDisplay()) {

                    UrlBuilder urlBuilder = new UrlBuilder(limri.serverLocation, context.getString(R.string.DISPLAY_CLIENT));
                    String url = urlBuilder.create(elsLimri, action);
                    // tell the caller to load the url
                    callback.loadURL(url);
                    // don't move to the next task.
                } else {
                    rest.getSheet(action.getLocation(), new ELSRestRequestCallback() {
                        @Override
                        public void onSuccess(Document document, Boolean result) {
                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    sequentialAsync(rest, elsLimri, index + 1, callback);
                                }
                            };
                            mainHandler.post(myRunnable);
                        }

                        @Override
                        public void onFailure() {
                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    sequentialAsync(rest, elsLimri, index + 1, callback);
                                }
                            };
                            mainHandler.post(myRunnable);
                        }
                    });
                }

            } else if (actionType == ELSLimriButtonPressAction.LOAD_URL) {
                Log.d("ButtonView", "load url");

                if (action.getDisplay()) {
                    UrlBuilder urlBuilder = new UrlBuilder(limri.serverLocation, context.getString(R.string.DISPLAY_CLIENT));
                    String url = urlBuilder.create(elsLimri, action);
                    // tell the caller to load the url
                    callback.loadURL(url);
                    // don't move to the next task.
                } else {
                    rest.loadArbitraryUrl(action.getLocation(), new ELSRestRequestCallback() {
                        @Override
                        public void onSuccess(Document document, Boolean result) {
                            sequentialAsync(rest, elsLimri, index + 1, callback);
                        }

                        @Override
                        public void onFailure() {
                            sequentialAsync(rest, elsLimri, index + 1, callback);
                        }
                    });
                }

            } else if (actionType == ELSLimriButtonPressAction.REFRESH) {
                Log.d("ButtonView", "refresh");

                // tell the caller to load the url
                callback.refresh();
                // don't move to the next task.

            } else if (actionType == ELSLimriButtonPressAction.NOTHING) {
                sequentialAsync(rest, elsLimri, index + 1, callback);
            }
        } else {
            Log.d("ButtonView", "jumping out");
            // tell the caller to refresh
            callback.refresh();
            // don't move to the next task.
        }
    }



}

package com.els.button.Views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.els.button.Models.ELSLimri;
import com.els.button.R;

/**
 * Created by Cam on 4/4/17.
 */

public class ELSInventoryAttributesDialog extends DialogFragment {

    private Listener listener;
    private String preFillInventoryID = "";
    private String preFillPin = "";
    private String title = "";
    private String message = "";
    private ELSLimri elsLimri;

    public interface Listener {
        public void onAttributeUpdate(DialogFragment dialog, ELSLimri elsLimri, String iid, String pin);
    }

    public void setPreFillInventoryID(String preFillInventoryID) {
        this.preFillInventoryID = preFillInventoryID;
    }

    public void setPreFillPin(String preFillPin) {
        this.preFillPin = preFillPin;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setElsLimri(ELSLimri elsLimri) {
        this.elsLimri = elsLimri;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (Listener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement Listener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_signin, null))
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.done, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });

        final Dialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // on click
                        if (getDialog() != null) {
                            String iid = "";
                            String pin = "";
                            // get the iid and pin the user entered
                            if (getDialog().findViewById(R.id.inventoryID) != null) {
                                EditText inventoryIDEditText = (EditText) getDialog().findViewById(R.id.inventoryID);
                                iid = inventoryIDEditText.getText().toString();
                            }
                            if (getDialog().findViewById(R.id.inventoryPIN) != null) {
                                EditText pinEditText = (EditText) getDialog().findViewById(R.id.inventoryPIN);
                                pin = pinEditText.getText().toString();
                            }
                            // if both iid and pin have some value then
                            if (!iid.isEmpty() && !pin.isEmpty()) {
                                //Dismiss once everything is OK.
                                listener.onAttributeUpdate(ELSInventoryAttributesDialog.this, elsLimri, iid, pin);
                                dialog.dismiss();
                            }
                        }
                    }
                });
            }
        });

        return dialog;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (!preFillInventoryID.equals("")) {
            EditText inventoryIDEditText = (EditText) getDialog().findViewById(R.id.inventoryID);
            inventoryIDEditText.setText(preFillInventoryID);
        }
        if (!preFillPin.equals("")) {
            EditText pinEditText = (EditText) getDialog().findViewById(R.id.inventoryPIN);
            pinEditText.setText(preFillPin);
        }
    }
}

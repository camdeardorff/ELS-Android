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

import com.els.button.Models.ELSLimri;
import com.els.button.R;

/**
 * Created by Cam on 4/6/17.
 */

public class ELSInventoryOptionsDialog extends DialogFragment {
    private Listener listener;
    private ELSLimri limri;

    public interface Listener {
        public void onEditButtonClick(DialogFragment dialog, ELSLimri limri);
        public void onDeleteButtonClick(DialogFragment dialog, ELSLimri limri);
    }

    public void setLimri(ELSLimri limri) {
        this.limri = limri;
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
        builder.setView(inflater.inflate(R.layout.dialog_edit_delete, null));


        final Dialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                if (dialog.findViewById(R.id.dialog_edit) != null) {
                    Button editButton = (Button) dialog.findViewById(R.id.dialog_edit);
                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.onEditButtonClick(ELSInventoryOptionsDialog.this, limri);
                        }
                    });
                }

                if (dialog.findViewById(R.id.dialog_delete) != null) {
                    Button deleteButton = (Button) dialog.findViewById(R.id.dialog_delete);
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.onDeleteButtonClick(ELSInventoryOptionsDialog.this, limri);
                        }
                    });
                }

                if (dialog.findViewById(R.id.dialog_cancel) != null) {
                    Button cancelButton = (Button) dialog.findViewById(R.id.dialog_cancel);
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }

            }
        });

        return dialog;
    }
}

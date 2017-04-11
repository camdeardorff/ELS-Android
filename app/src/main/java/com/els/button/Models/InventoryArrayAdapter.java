package com.els.button.Models;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.els.button.Interfaces.InventoryListAdapterDelegate;
import com.els.button.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cameron on 3/29/16.
 */
public class InventoryArrayAdapter extends ArrayAdapter<ELSEntity> {
    // View lookup cache
    Context context;
    ArrayList<ELSEntity> ELSEntityList;
    InventoryListAdapterDelegate delegate;

    static final int LIMRI_TYPE = 0;
    static final int IOT_TYPE = 1;

    private static class LimriViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        ImageView iconImageView;
        Button button;
        ImageButton expandButton;
        ImageButton configureButton;
    }

    private static class IoTViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        LinearLayout buttonHolder;
        List<Button> buttons;

    }


    public InventoryArrayAdapter(Context context, InventoryListAdapterDelegate listenterDelegate, ArrayList<ELSEntity> ELSEntityList) {
        super(context, R.layout.inventory_overview_layout, ELSEntityList);

        this.context = context;
        this.delegate = listenterDelegate;
        this.ELSEntityList = ELSEntityList;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {



        /*
        BugFixed: using 'instanceOf()' did not work in the comparisons below.
         */
        if (getItem(position).getClass() == ELSLimri.class) {
            return LIMRI_TYPE;
        } else if (getItem(position).getClass() == ELSIoT.class) {
            return IOT_TYPE;
        } else {
            return -1;
        }

    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        ELSEntity element = getItem(position);

        int type = getItemViewType(position);

        if (type == LIMRI_TYPE) {
            //make a copy of the convert view, since we know it's type is 1 we make it a name it as
            //limriView
            View limriView = convertView;
            //this is the holder for the view, if this specific view has alread been made then we will
            //reuse this view's information. Otherwise we will populate this holder with the correct information
            //for next time
            LimriViewHolder limriHolder;
            //get the data from the element, cast it to the limri type
            final ELSLimri limriData = (ELSLimri) element;

            //if the convert view (previously made version of this exact cell) is null then we should
            //inflate it with the layout we want, create a new holder for this cell, and then set this
            //convert view's layout to the one we had just inflated
            if (limriView == null) {
                //create a new inflater and inflate the view
                LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //inflate this view with the inventory overview layout.
                limriView = layoutInflater.inflate(R.layout.inventory_overview_layout, parent, false);

                //create a new limri view holder so we can reuse this information for next time. efficiency
                limriHolder = new LimriViewHolder();
                //set all of the holder properties with the layout elements, we will fill the layout elements later
                limriHolder.titleTextView = (TextView) limriView.findViewById(R.id.limriInventoryName);
                limriHolder.descriptionTextView = (TextView) limriView.findViewById(R.id.limriInventoryDescription);
                limriHolder.button = (Button) limriView.findViewById(R.id.limriButton);
                limriHolder.iconImageView = (ImageView) limriView.findViewById(R.id.inventoryIcon);
                limriHolder.configureButton = (ImageButton) limriView.findViewById(R.id.configure_button);
                limriHolder.expandButton = (ImageButton) limriView.findViewById(R.id.expandButton);
                //set the limri view's tag to this holder
                limriView.setTag(limriHolder);
            } else {
                //the view is not null! take the reused content from the view and put it into the holder
                //this is necessary for when we populate the display elements with content. The content
                //could have changed so we do that every time. This is all standard stuff
                limriHolder = (LimriViewHolder) limriView.getTag();
            }


            // TITLE
            limriHolder.titleTextView.setText(Html.fromHtml(limriData.title));

            // DESCRIPTION
            limriHolder.descriptionTextView.setText(Html.fromHtml(limriData.description));
            // if the description is empty then hide the expand button
            if (Html.fromHtml(limriData.description).length() > 0) {
                limriHolder.expandButton.setVisibility(View.VISIBLE);
            } else {
                limriHolder.expandButton.setVisibility(View.GONE);
            }

            // CONFIGURE BUTTON
            limriHolder.configureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delegate.limriConfigurationButtonWasPressed(limriData);
                }
            });

            // EXPAND BUTTON
            // references to the ui elements for the on click listener
            final TextView description = limriHolder.descriptionTextView;
            final ImageButton expandButton = limriHolder.expandButton;
            // when the button is clicked hide/show and rotate
            limriHolder.expandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (description.getVisibility() == View.GONE) {
                        description.setVisibility(View.VISIBLE);
                        expandButton.setRotation(180);
                    } else {
                        description.setVisibility(View.GONE);
                        expandButton.setRotation(0);
                    }
                }
            });

            // IMAGE VIEW
            if (limriData.getIcon() != null && limriData.getIcon() != ELSLimriIcon.NONE) {
                limriHolder.iconImageView.setImageResource(limriData.getIcon().getLiteralIcon());
            }

            // BUTTON
            // set text, text color, and background color
            limriHolder.button.setText(Html.fromHtml(limriData.getButton().getTitle()));
            limriHolder.button.setTextColor(ContextCompat.getColor(context, limriData.getButton().getTextColor().getLiteralColor()));
            limriHolder.button.setBackgroundColor(ContextCompat.getColor(context, limriData.getButton().getColor().getLiteralColor()));
            // put a gradient on the button (mostly for corner radius and color)
            GradientDrawable gd = new GradientDrawable();
            if (limriData.getButton().getCornerRadius() != null) {
                gd.setCornerRadius(limriData.getButton().getCornerRadius());
            }
            gd.setColor(ContextCompat.getColor(context, limriData.getButton().getColor().getLiteralColor()));
            limriHolder.button.setBackground(gd);

            limriHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //when it is clicked tell the delegate that this button was pressed. It will transition
                    //to another activity. This is the wrong layer to be doing that kind of stuff
                    delegate.limriButtonWasPressed(limriData, limriData.getButton().getAction());
                }
            });

            if (limriData.getButton().getActions() != null
                    && limriData.getButton().getActions().size() > 0) {
                limriHolder.button.setVisibility(View.VISIBLE);
            } else {
                limriHolder.button.setVisibility(View.GONE);
            }

            //finally return the view we have been messing with
            return limriView;


        } else if (type == IOT_TYPE) {
            //copy the convert view and name it appropriatly as an iotView
            View iotView = convertView;
            //declare a holder for this view
            IoTViewHolder ioTHolder;
            //cast the element data to the correct form ELSIoT
            final ELSIoT iotData = (ELSIoT) element;

            //if the convert view is empty then we need to inflate the layout and copy it's ui elements
            //into the view holder so we can use it again sometime. this is the recommended way of doing
            //this since it increases the efficiency
            //Basically we are going to set the tag of this view or get the tag of this view. (look at the last line of those cases)
            if (iotView == null) {
                //create a new inflater object
                LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //inflate our new view to the iot overview layout
                iotView = layoutInflater.inflate(R.layout.iot_overview_layout, parent, false);

                //initialize the holder as an IoT holder
                ioTHolder = new IoTViewHolder();
                //copy the title and description ui elements to the holder for use later
                ioTHolder.titleTextView = (TextView) iotView.findViewById(R.id.iotTitle);
                ioTHolder.descriptionTextView = (TextView) iotView.findViewById(R.id.iotDescription);
                ioTHolder.buttonHolder = (LinearLayout) iotView.findViewById(R.id.iotButtonContainer);

                LinearLayout buttonContainer = (LinearLayout) iotView.findViewById(R.id.iotButtonContainer);
                //do a loop over the buttons and add them to the view here

                List<Button> buttonList = new ArrayList<Button>();
                for (final ELSIoTButton btn : iotData.buttons) {
                    Button button = new Button(context);
                    button.setText(Html.fromHtml(btn.getTitle()));
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //when it is clicked tell the delegate that this button was pressed. It will transition
                            //to another activity. This is the wrong layer to be doing that kind of stuff
                            delegate.iotButtonWasPressed(iotData, btn.getValue());
                        }
                    });
                    buttonList.add(button);
                    buttonContainer.addView(button);
                }
                ioTHolder.buttons = buttonList;


                //set the actual view's tag to be this holder. this way we can get the holder and reuse
                //it's elements. faster than creating new ones
                iotView.setTag(ioTHolder);
            } else {
                //the iotview was not empty. get the tag (which should be a IoTViewHolder and set the
                //holder we declared earlier to be the previously set tag
                ioTHolder = (IoTViewHolder) iotView.getTag();
            }


            //set the title of both the view and description to the correct values
            ioTHolder.titleTextView.setText(iotData.title);
            ioTHolder.descriptionTextView.setText(iotData.description);
            //do a loop over the buttons and add them to the view here

            return iotView;

        }
        return convertView;
    }
}
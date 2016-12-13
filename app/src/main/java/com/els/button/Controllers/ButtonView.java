package com.els.button.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.els.button.Models.ELSIoT;
import com.els.button.Models.InventoryListAdapter;
import com.els.button.Models.InventoryListAdapterDelegate;
import com.els.button.Networking.ELSRest;
import com.els.button.R;
import com.els.button.Models.ELSEntity;
import com.els.button.Models.ELSLimri;

import org.w3c.dom.Document;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


public class ButtonView extends AppCompatActivity implements InventoryListAdapterDelegate {

    public static final String HOST_IP = "149.143.3.182";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.buttonViewToolbar);
        setSupportActionBar(toolbar);

        /**
         * CURRENT STATUS
         * Button view and web view are both functional and displaying properly
         * webview goes to correct page
         * buttonview goes to webview after button clicked
         *
         * NOT YET COMPLETE: hooking the buttons into the ELSRest class!
         * buttons need to be refreshed on load with a login and a get status
         * functionality coming soon
         *
         * THE ELS Rest class is complete. the only none functional component is the get question
         * function.... Next steps are to hook the buttonview buttons up with the completed rest class
         * and then refresh them on load.
         */



        ArrayList<ELSEntity> buttonList = new ArrayList<ELSEntity>();

        ELSLimri inventory1 = new ELSLimri("0987654321", "2222", "Couple 1", "The first couple", "W201");
        buttonList.add(inventory1);

        ELSLimri inventory2 = new ELSLimri("0987654321", "2222", "Couple 2", "The second couple", "W202");
        buttonList.add(inventory2);

        ELSLimri inventory3 = new ELSLimri("0987654321", "2222", "Couple 3", "The third couple", "W203");
        buttonList.add(inventory3);

        ELSLimri inventory4 = new ELSLimri("0987654321", "2222", "Couple 4", "The fourth couple", "W204");
        buttonList.add(inventory4);

        ELSLimri inventory5 = new ELSLimri("0987654321", "2222", "Couple 5", "The fifht couple", "W205");
        buttonList.add(inventory5);

        ELSLimri inventory6 = new ELSLimri("0987654321", "2222", "Couple 6", "The sixth couple", "W206");
        buttonList.add(inventory6);




        Map<String,String> buttonAndTitle = new HashMap<String, String>();
        buttonAndTitle.put("2", "on");
        buttonAndTitle.put("1", "off");

        ELSIoT inventory7 = new ELSIoT("0987654321", "1111", "qW201D1", "Test iot", "This is a test of the iot overview", buttonAndTitle);
        buttonList.add(inventory7);



        InventoryListAdapter listAdapter;
        listAdapter = new InventoryListAdapter(this, this, buttonList);



        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(listAdapter);


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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //method to convert Document to String
    public String getStringFromDocument(Document doc)
    {
        try
        {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        }
        catch(TransformerException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void limriButtonWasPressedWithLimriInfo(ELSLimri elsLimri) {
        Log.d("ButtonView","limriButtonWasPressedWithLimriInfo");
        Intent intent = new Intent(this, WebViewer.class);

        intent.putExtra("sheet", elsLimri.statusSheet);
        intent.putExtra("id", elsLimri.inventoryID);
        intent.putExtra("pin", elsLimri.pin);
        startActivity(intent);
    }

    @Override
    public void iotButtonWasPressedWithIotInfoAndSetQuestionValue(ELSIoT elsIoT, String value) {
        Log.d("ButtonView", "iotButtonWasPressedWithSetQuestionValue");

        ELSRest comm = new ELSRest(HOST_IP, elsIoT.inventoryID, elsIoT.pin);
        if (comm.login()) {
            Log.d("ButtonView", "Login was successful");
            //make a hashmap for the questions and answers to send
            HashMap<String, String> questionAndAnswer = new HashMap<String, String>();
            questionAndAnswer.put(elsIoT.qID, value);
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
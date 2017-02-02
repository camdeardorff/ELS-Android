package com.els.button.Networking;

import android.os.AsyncTask;
import android.util.Log;

import com.els.button.Models.ELSLimriButtonPressAction;
import com.els.button.Models.ELSLimriColor;
import com.els.button.Networking.Models.ELSInventoryStatus;
import com.els.button.Networking.Models.ELSInventoryStatusAction;
import com.els.button.Networking.Models.ELSInventoryStatusAppearance;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Created by cameron on 2/16/16.
 * Class: ELSRest
 * Purpose: focul point of communication between the content server and mobile applications
 */
public class ELSRest {
    private Document xmlDocument;
    private String hostIP;
    private String id;
    private String pin;
    //TODO: make this truly a random session id
    String sessionID = "0987654321";

    /**
     * Initializer - initilizes ELSRest object
     * @param host - the ip address of the server
     * @param id - the inventory id for the account
     * @param pin - the corresponding pin to the inventory id
     */
    public ELSRest(String host, String id, String pin) {
        this.hostIP = host;
        this.id = id;
        this.pin = pin;
    }

    /**
     * Function - login - logs into the server with the given host address, id, and pin from the initializer.
     * @return - boolean - returns true if the login attempt was successful. False if it was unsuccessful
     */
    public boolean login() {
        //plug and play string for the params
        String command = "command=StartSession&sessionid=" + id + sessionID + "&inventoryid=" + id + "&pin=" + pin;

        //create new request to the server and execute with the command
        Request request = new Request(hostIP);
        request.execute(command);

        //create a string initialized to null before trying to get the value
        //request.get() can cause an exception. Try for the value and deal with exceptions
        String xmlString = null;
        try {
            xmlString = request.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (xmlString != null) {


            //create a document with the string result from the request
            Document xml = getXmlDocument(xmlString);
            //xpath into the document to get the response from the result attribute (see loginResponsePath)
            String loginResponsePath = "//reply/@result";
            String result = xPathForString(xml, loginResponsePath);

            //if successful the value will be "success"
            if (result.equals("success")) {
                return true;
            }
        }
        return false;
    }


    /**
     * Logs the user out of the server.
     * @return true for a successful attempt and false for unsuccessful.
     */
    public boolean logout() {
        //plug and play string for the params
        String command = "command=StartSession&sessionid=" + id + sessionID + "&inventoryid=" + id + "&pin=" + pin;

        //create new request object to the server and execute with the command
        Request request = new Request(hostIP);
        request.execute(command);

        //create a string initialized to null before trying to get the value
        //request.get() can cause an exception. Try for the value and deal with exceptions
        String xmlString = null;
        try {
            xmlString = request.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (xmlString != null) {

            //create a document with the string result from the request
            Document xml = getXmlDocument(xmlString);
            //xpath into the document to get the response from the result attribute (see loginResponsePath)
            String loginResponsePath = "//reply/@result";
            String result = xPathForString(xml, loginResponsePath);

            //if successful the value will be "success"
            Log.d("ELSRest", "logout result is: " + result);
            if (result.equals("success")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sends a request to the server to get a sheet using the id and pin given at initialization. On
     * server reply the reply is processed into a document and returned.
     * @param sheetName - sheet to retrieve from the server.
     * @return A document from the server with a corresponding sheetName
     */
    public Document getSheet(String sheetName) {

        //plug and play string for the params
        String command = "command=Sheet&sessionid=" + id + sessionID + "&args={\"sheet\":\"" + sheetName + "\"}";

        //create new request object to the server and execute with the command
        Request request = new Request(hostIP);
        request.execute(command);

        //create a string initialized to null before trying to get the value
        //request.get() can cause an exception. Try for the value and deal with exceptions
        String xmlString = null;
        try {
            xmlString = request.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //create a document with the string result from the request
        Document xml = getXmlDocument(xmlString);
        //xpath into the document to get the response from the result attribute (see loginResponsePath)
        return xml;
    }

    public Node getQuestion(String sheetName, String qID) {
        Document sheet = getSheet(sheetName);
//        String result = xPathForString(sheet, "//item[@id='" + qID + "']");
//        Log.d("ELSRest","result from getQuestion is: " + result);

        return xPathForNode(sheet, "//item[@id='" + qID + "']");
//        sheet.getElementById(qID);
//        return sheet.getFirstChild();
    }



    /**
     * Sends a post to the server to set a question id in the database to a value.
     * @param qidsAndResponses - a map key value pairs where the keys are the question ids and the value
     *                         is the value to set that question id to.
     * @return returns a boolean value in accordance with the success of the post. success = true.
     * @test use a map with just "qW201D1", "2" as the values. it works!
     */
    public boolean setQuestion(HashMap<String, String> qidsAndResponses) {

        //make sure we are not sending something that is sure not to work
        if (!qidsAndResponses.isEmpty()) {
             /*
                in this function the map is processed into two string renditions of arrays "["_", "_"]"\
                from there they are pushed straight into the command, a pre-prepared request to the server
            */

            //create two string builder objects starting with [
            StringBuilder qidBuilder = new StringBuilder().append("[");
            StringBuilder responseBuilder = new StringBuilder().append("[");

            //loop through every key value pair in the qidAndResponses hashmap
            for (Map.Entry<String, String> entry : qidsAndResponses.entrySet()) {
                //append the key to the qid and value to the response
                qidBuilder.append("\"" + entry.getKey() + "\",");
                responseBuilder.append("\"" + entry.getValue() + "\",");
            }

            //remove the last comma from each builder by setting their length back one
            qidBuilder.setLength(qidBuilder.length() - 1);
            responseBuilder.setLength(responseBuilder.length() - 1);
            //put the finishing bracket on each builder
            qidBuilder.append("]");
            responseBuilder.append("]");
            //get the string value from the builders
            String qids = qidBuilder.toString();
            String responses = responseBuilder.toString();

            //plug and play command. a pre-prepared statement.
            String command = "command=SetQuestions&sessionid=" + id + sessionID + "&args={\"questionids\":" + qids + ",\"responses\":" + responses + "}";
            Log.d("ELSRest", command);

            //create new request object to the server and execute with the command
            Request request = new Request(hostIP);
            request.execute(command);

            //create a string initialized to null before trying to get the value
            //request.get() can cause an exception. Try for the value and deal with exceptions
            String xmlString = null;
            try {
                xmlString = request.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            Log.d("ELSRest", xmlString);
            //create a document with the string result from the request
            Document xml = getXmlDocument(xmlString);

            //xpath into the document to get the response from the result attribute (see loginResponsePath)
            String loginResponsePath = "//reply/@result";
            String result = xPathForString(xml, loginResponsePath);

            //if successful the value will be "success"
            Log.d("ELSRest", "setQuestion result is: " + result);
            if (result.equals("success")) {
                return true;
            }
        }
        return false;
    }


    public ELSInventoryStatus getInventoryStatus(String statusSheet) {

        if (this.login()) {
            Document sheet = this.getSheet(statusSheet);
            Log.d("ELSRest", "status sheet: ");
            System.out.println(convertDocumentToString(sheet));

            String status = xPathForString(sheet, "//appearance/status/color");
            Log.d("ELSRest", "appearance status: " + status);

            String buttonText = xPathForString(sheet, "//appearance/button/text");
            Log.d("ELSRest", "button text: " + buttonText);

            String buttonColor = xPathForString(sheet, "//appearance/button/color");
            Log.d("ELSRest", "button color: " + buttonColor);

            ELSInventoryStatusAppearance appearance = new ELSInventoryStatusAppearance(status, buttonText, ELSLimriColor.fromStringLiteral(buttonColor));


            String type = xPathForString(sheet, "//action/type");
            Log.d("ELSRest", "action type: " + type);

            String location = xPathForString(sheet, "//action/location");
            Log.d("ELSRest", "actionLocation: " + location);


            ELSInventoryStatusAction action = new ELSInventoryStatusAction(ELSLimriButtonPressAction.fromStringLiteral(type), location);


            String title = xPathForString(sheet, "//title");
            Log.d("ELSRest", "title: " + title);

            String description = xPathForString(sheet, "//description");
            Log.d("ELSRest", "description: " + description);

            String nextStatusSheet = xPathForString(sheet, "//statusSheet");
            Log.d("ELSRest", "nextStatusSheet: " + nextStatusSheet);

            return new ELSInventoryStatus(title, description, nextStatusSheet, action, appearance);

        } else {
            return null;
        }
    }


    /**
     * Function: getXmlDocument - turns a string that resembles an xmldocument into an actual xml
     * document.
     * @param xmlString - this string should resemble an xmldocument in its string form
     * @return XML Document corresponding to the string passed in.
     */
    private Document getXmlDocument(String xmlString) {
        //create the factory for building the document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //set the builder and document to null before trying to get their values
        DocumentBuilder builder = null;
        Document doc = null;
        try {
            //make a new builder from the factory
            builder = factory.newDocumentBuilder();
            //create the document with the biilder and the xmlString parameter
            doc = builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    /**
     * Function - xPath - function to do xpaths on an xml document. reduces a whole lot of repeated
     * code.
     * @param doc - xml document to find values in
     * @param path - a path to test on the document in string form
     * @return the corresponding value the path points to in the document.
     */
    private String xPathForString(Document doc, String path) {
        XPathFactory xpFactory = XPathFactory.newInstance();
        XPath xPath = xpFactory.newXPath();
        String val = null;

        try {
            val = xPath.evaluate(path, doc);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return val;
    }

    private Node xPathForNode(Document doc, String path) {
        XPathFactory xpFactory = XPathFactory.newInstance();
        XPath xPath = xpFactory.newXPath();
        NodeList nl = null;
        try {
            nl = (NodeList) xPath.evaluate(path, doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return nl.item(0);

    }

    private static String convertDocumentToString(Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            // below code to remove XML declaration
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString();
            return output;
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Class: Request - sends requests to a server and gets replies. Extends AsyncTask
     */
    private class Request extends AsyncTask<String, Void, String> {
        private String hostIP;

        /**
         * Initializer
         * @param hostIP - the ip address to send the request to
         */
        Request(String hostIP) {
            this.hostIP = hostIP;
        }


        @Override
        protected String doInBackground(String... strings) {
            //send a request to the server with the first string parameter
            String reply = sendRequest(strings[0]);
            //if the reply is empty print an error.
            //TODO: notify the user to connect to the internet. Consider a reachability on startp
            if (reply == null) {
                Log.d("Request", "This is an error saying that there is no xml, check the ip address string");
            }
            return reply;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }


        /**
         * Function: sendRequest - sends an http request to the host given in the initializer with
         * url parameters provided. It then returns the string value of the request.
         * @param urlParameters - the url parameters to send to the server.
         * @return string value of the response back from the server.
         */
        private String sendRequest(String urlParameters) {
            //try catch with creating a new url.
            URL url = null;
            try {
                //prepared string to go to host address's content server
                url = new URL("http://" + hostIP + ":8080/ContentServer/ContentServer");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            //create return string before entering the try catch
            String returnString = null;

            try {
                Log.d("Request", "Url is: " + url);
                //create http connection object with an open connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //set the request method to post
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                //create a data stream with the output stream from the connection's request
                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters);
                //tidy up the stream
                dStream.flush();
                dStream.close();
                //not used but useful for debugging
                int responseCode = connection.getResponseCode();

                //read the info in through the buffer, intialize a new string and create a string builder
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();

                //go line by line through the buffer and keep adding onto the string builder
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }
                //close the buffer and set the returns string to the final built string
                br.close();
                returnString = responseOutput.toString();

                //catch any exceptions and print
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return returnString;
        }
    }//end private inner class REQUEST
}//end public outer class ELSREST
package com.els.button.Networking;

import android.util.Log;

import com.els.button.Models.ELSLimriButtonPressAction;
import com.els.button.Models.ELSLimriColor;
import com.els.button.Networking.Callbacks.ELSRestInventoryStatusRequestCallback;
import com.els.button.Networking.Callbacks.ELSRestRequestCallback;
import com.els.button.Networking.Models.ELSInventoryStatus;
import com.els.button.Networking.Models.ELSInventoryStatusAction;
import com.els.button.Networking.Models.ELSInventoryStatusAppearance;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

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
    private Boolean loggedIn;

    /**
     * Initializer - initilizes ELSRest object
     *
     * @param host - the ip address of the server
     * @param id   - the inventory id for the account
     * @param pin  - the corresponding pin to the inventory id
     */
    public ELSRest(String host, String id, String pin) {
        this.hostIP = host;
        this.id = id;
        this.pin = pin;
        this.loggedIn = false;
    }


    private void log(final Boolean login, final ELSRestRequestCallback callback) {
        // TODO: integrate into url builder
        String command;
        if (login) {
            command = "command=StartSession&inventoryid=" + id + "&pin=" + pin;
        } else {
            command = "command=EndSession&inventoryid=" + id + "&pin=" + pin;
        }

//        Log.d("ELSRest", "command: " + command);
        OkRequest request = new OkRequest(this.hostIP);
        request.execute(command, new OkRequestCallback() {
            @Override
            public void onSuccess(String xml) {
                //create a document with the string result from the request
                Document doc = getXmlDocument(xml);

                //xpath into the document to get the response from the result attribute (see loginResponsePath)
                String loginResponsePath = "//reply/@result";
                String result = xPathForString(doc, loginResponsePath);

                if (result != null) {
                    Boolean success = result.equals("success");

                    //if successful the value will be "success"
                    callback.onSuccess(doc, success);
                } else {
                    // did not get an expected response from the server
                    callback.onFailure();
                }


            }

            @Override
            public void onError() {
                callback.onFailure();
            }
        });
    }

    public void login(final ELSRestRequestCallback callback) {
        log(true, callback);
    }

    public void logout(final ELSRestRequestCallback callback) {
        log(false, callback);
    }


    /**
     * Sends a request to the server to get a sheet using the id and pin given at initialization. On
     * server reply the reply is processed into a document and returned.
     *
     * @param sheetName - sheet to retrieve from the server.
     * @return A document from the server with a corresponding sheetName
     */
    public void getSheet(String sheetName, final ELSRestRequestCallback callback) {

        //plug and play string for the params
        final String command = "command=SheetSession&inventoryid=" + id + "&pin=" + pin + "&args={\"sheet\":\"" + sheetName + "\"}";

        this.login(new ELSRestRequestCallback() {
            @Override
            public void onSuccess(Document document, Boolean result) {
                OkRequest request = new OkRequest(hostIP);
                request.execute(command, new OkRequestCallback() {
                    @Override
                    public void onSuccess(String xml) {
                        //create a document with the string result from the request
                        Document doc = getXmlDocument(xml);
                        //xpath into the document to get the response from the result attribute (see loginResponsePath)
                        String loginResponsePath = "//reply/@result";
                        String result = xPathForString(doc, loginResponsePath);

                        //if successful the value will be "success"
                        callback.onSuccess(doc, result.equals("success"));
                    }

                    @Override
                    public void onError() {
                        callback.onFailure();
                    }
                });
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });



    }


    /**
     * Sends a post to the server to set a question id in the database to a value.
     *
     * @param qidsAndResponses - a map key value pairs where the keys are the question ids and the value
     *                         is the value to set that question id to.
     * @return returns a boolean value in accordance with the success of the post. success = true.
     * @test use a map with just "qW201D1", "2" as the values. it works!
     */
    public void setQuestion(HashMap<String, String> qidsAndResponses, final ELSRestRequestCallback callback) {

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
                qidBuilder.append("\"").append(entry.getKey()).append("\",");
                responseBuilder.append("\"").append(entry.getValue()).append("\",");
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
            String command = "command=SetQuestions&inventoryid=" + id + "&pin=" + pin + "&args={\"questionids\":" + qids + ",\"responses\":" + responses + "}";
            Log.d("ELSRest", command);

            OkRequest request = new OkRequest(this.hostIP);
            request.execute(command, new OkRequestCallback() {
                @Override
                public void onSuccess(String xml) {
                    //create a document with the string result from the request
                    Document doc = getXmlDocument(xml);
                    //xpath into the document to get the response from the result attribute (see loginResponsePath)
                    String loginResponsePath = "//reply/@result";
                    String result = xPathForString(doc, loginResponsePath);

                    //if successful the value will be "success"
                    callback.onSuccess(doc, result.equals("success"));
                }

                @Override
                public void onError() {
                    callback.onFailure();
                }
            });
        } else {
            callback.onFailure();
        }
    }



    /**
     * Sends a post to the server to set a question id in the database to a value.
     *
     * @param qid - the question id of which to increment the value of
     * wget -np -q -O- --post-data='command=IncQuestionSession&inventoryid=0987654321&pin=2222&args={"questionid":"q200"}' http://localhost:8080/ContentServer/ContentServer

     */
    public void incQuestion(String qid, final ELSRestRequestCallback callback) {

        //make sure we are not sending something that is sure not to work
        if (!qid.isEmpty()) {

            //plug and play command. a pre-prepared statement.
            String command = "command=IncQuestionSession&inventoryid=" + id + "&pin=" + pin + "&args={\"questionids\":" + qid + "}";
            Log.d("ELSRest", command);

            OkRequest request = new OkRequest(this.hostIP);
            request.execute(command, new OkRequestCallback() {
                @Override
                public void onSuccess(String xml) {
                    //create a document with the string result from the request
                    Document doc = getXmlDocument(xml);
                    //xpath into the document to get the response from the result attribute (see loginResponsePath)
                    String loginResponsePath = "//reply/@result";
                    String result = xPathForString(doc, loginResponsePath);

                    //if successful the value will be "success"
                    callback.onSuccess(doc, result.equals("success"));
                }

                @Override
                public void onError() {
                    callback.onFailure();
                }
            });
        } else {
            callback.onFailure();
        }
    }

    public void loadArbitraryUrl(String url, final ELSRestRequestCallback callback) {
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(null, null);
            }
        });
    }


    /**
     * Gets the status sheet for an inventory and gets the inventory status specific information to update
     * the local state.
     *
     * @param statusSheet: sheet name to request
     * @param callback:    ELSRestInventoryStatusRequestCallback to tell the caller what the result of the request was.
     */
    public void getInventoryStatus(final String statusSheet, final ELSRestInventoryStatusRequestCallback callback) {

        // get the status sheet
        this.getSheet(statusSheet, new ELSRestRequestCallback() {
            @Override
            public void onSuccess(Document document, Boolean result) {
                if (result) {

                    // get appearance info
                    String status = xPathForString(document, "//appearance/status/color");
                    String buttonText = xPathForString(document, "//appearance/button/title/text");
                    String buttonTextColor = xPathForString(document, "//appearance/button/title/color");
                    String buttonColor = xPathForString(document, "//appearance/button/color");

                    // get the border radius
                    String buttonBorderRadius = xPathForString(document, "//appearance/button/border/radius");
                    if (buttonBorderRadius.equals("")) {
                        buttonBorderRadius = "-1";
                    }
                    // put together all of the appearance data
                    ELSInventoryStatusAppearance appearance = new ELSInventoryStatusAppearance(
                            status,
                            buttonText,
                            ELSLimriColor.fromStringLiteral(buttonTextColor),
                            ELSLimriColor.fromStringLiteral(buttonColor),
                            Integer.parseInt(buttonBorderRadius));



                    // get action data
                    String type = xPathForString(document, "//action/type");
                    String location = xPathForString(document, "//action/location");
                    Boolean display = Boolean.parseBoolean(xPathForString(document, "//action/display"));

                    NodeList actionNodes = xPathForNodeSet(document, "//actions/action");
                    ArrayList<ELSInventoryStatusAction> actions = getActionsFromNodeList(actionNodes);

                    String title = xPathForString(document, "//title");
                    String description = xPathForSubtreeString(document, "//description");
                    String nextStatusSheet = xPathForString(document, "//statusSheet");
                    String nextServerLocation = xPathForString(document, "//serverLocation");

                    callback.onSuccess(new ELSInventoryStatus(title, description, nextStatusSheet, nextServerLocation, actions, appearance));
                } else {
                    // the request was successful but it's message was bad
                    // fail because of a bad login
                    callback.onFailure(true);
                }
            }

            @Override
            public void onFailure() {
                // couldn't get the sheet, (not login credentials)
                callback.onFailure(false);
            }
        });
    }


    private ArrayList<ELSInventoryStatusAction> getActionsFromNodeList(NodeList nodeList) {

        ArrayList<ELSInventoryStatusAction> actions = new ArrayList<ELSInventoryStatusAction>();
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();

        for (int i=0; i<nodeList.getLength(); i++) {
            Node actionNode = nodeList.item(i);
            String type;
            String location;
            String value;
            Boolean display;
            try {
                type = xPath.evaluate("type", actionNode);
                location = xPath.evaluate("location", actionNode);
                display = Boolean.parseBoolean(xPath.evaluate("display", actionNode));
                value = xPath.evaluate("value", actionNode);
                actions.add(new ELSInventoryStatusAction(ELSLimriButtonPressAction.fromStringLiteral(type), location, value, display));
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
        }

        return actions;
    }




    /**
     * Function: getXmlDocument - turns a string that resembles an xmldocument into an actual xml
     * document.
     *
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
            //create the document with the builder and the xmlString parameter
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
     *
     * @param doc  - xml document to find values in
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

    private NodeList xPathForNodeSet(Document doc, String path) {
        XPathFactory xpFactory = XPathFactory.newInstance();
        XPath xPath = xpFactory.newXPath();
        NodeList nl = null;
        try {
            nl = (NodeList) xPath.evaluate(path, doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return nl;
    }

    private String xPathForSubtreeString(Document document, String path){

        XPathFactory xpFactory = XPathFactory.newInstance();
        XPath xPath = xpFactory.newXPath();
        Node node = null;
        try {
            node = (Node) xPath.evaluate(path, document, XPathConstants.NODE);
        } catch (XPathExpressionException e) {

        }
        StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = tf.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        try {
            transformer.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return sw.toString();

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


    private interface OkRequestCallback {
        void onSuccess(String xml);

        void onError();
    }

    private class OkRequest {
        private String serverLocation;

        OkRequest(String hostIP) {
            this.serverLocation = hostIP;
        }

        public void execute(String command, final OkRequestCallback callback) {

            Log.d("OkRequest", "execute");

            OkHttpClient client = new OkHttpClient();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(serverLocation + "?" + command)
                    .build();

            Log.d("OkRequest", "request built " + request.toString());

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    callback.onError();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);


                    BufferedReader br = new BufferedReader(response.body().charStream());
                    String line;
                    StringBuilder responseOutput = new StringBuilder();

                    //go line by line through the buffer and keep adding onto the string builder
                    while ((line = br.readLine()) != null) {
                        responseOutput.append(line);
                    }
                    //close the buffer and set the returns string to the final built string
                    br.close();
                    String returnString = responseOutput.toString();
//                    Log.d("ELSRest", returnString);
                    callback.onSuccess(returnString);
                }
            });
        }
    }
}
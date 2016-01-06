package com.alchemyapi.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AlchemyAPI {

    private String _apiKey;
    private String _requestUri = "http://access.alchemyapi.com/calls/";

    private AlchemyAPI() {
    }


    static public AlchemyAPI GetInstanceFromFile(String keyFilename)
        throws FileNotFoundException, IOException
    {
        AlchemyAPI api = new AlchemyAPI();
        api.LoadAPIKey(keyFilename);

        return api;
    }

    static public AlchemyAPI GetInstanceFromString(String apiKey)
    {
        AlchemyAPI api = new AlchemyAPI();
        api.SetAPIKey(apiKey);

        return api;
    }

    public void LoadAPIKey(String filename) throws IOException, FileNotFoundException
    {
       /* if (null == filename || 0 == filename.length())
            throw new IllegalArgumentException("Empty API key file specified.");
       // URL url = new URL(new URL("file:"), filename);
        File myFile = new File("word.txt");
        
        System.out.println("Attempting to read from file in: "+myFile.getCanonicalPath());
        File file = new File(filename);
        FileInputStream fis = new FileInputStream(file);

        BufferedReader breader = new BufferedReader(new InputStreamReader(fis));*/

        _apiKey = "ae37d5e43fed304596095d472f72cf56016ba199";

        

        if (null == _apiKey || _apiKey.length() < 5)
            throw new IllegalArgumentException("Too short API key.");
    }

    public void SetAPIKey(String apiKey) {
        _apiKey = apiKey;

        if (null == _apiKey || _apiKey.length() < 5)
            throw new IllegalArgumentException("Too short API key.");
    }

    public void SetAPIHost(String apiHost) {
        if (null == apiHost || apiHost.length() < 2)
            throw new IllegalArgumentException("Too short API host.");

        _requestUri = "http://" + apiHost + ".alchemyapi.com/calls/";
    }

 
   
   public Document ImageGetRankedImageKeywords(AlchemyAPI_ImageParams params)
    throws IOException, SAXException,
           ParserConfigurationException, XPathExpressionException
    {
        URL url = new URL(_requestUri + "image/ImageGetRankedImageKeywords?" + 
            "apikey=" + this._apiKey + params.getParameterString());
        System.out.println(url.toString());

        HttpURLConnection handle = (HttpURLConnection) url.openConnection();
        handle.setDoOutput(true);

        byte[] image = params.getImage();
        handle.addRequestProperty("Content-Length", Integer.toString(image.length));

        DataOutputStream ostream = new DataOutputStream(handle.getOutputStream());
        ostream.write(image);
        ostream.close();

        return doRequest(handle, params.getOutputMode());
    }
   private Document doRequest(HttpURLConnection handle, String outputMode)
	        throws IOException, SAXException,
	               ParserConfigurationException, XPathExpressionException
	    {
	        DataInputStream istream = new DataInputStream(handle.getInputStream());
	        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(istream);

	        istream.close();
	        handle.disconnect();

	        XPathFactory factory = XPathFactory.newInstance();
	        System.out.println("Document"+doc);
	        if(AlchemyAPI_Params.OUTPUT_XML.equals(outputMode)) {
	        	String statusStr = getNodeValue(factory, doc, "/results/status/text()");
	        	if (null == statusStr || !statusStr.equals("OK")) {
	        		String statusInfoStr = getNodeValue(factory, doc, "/results/statusInfo/text()");
	        		if (null != statusInfoStr && statusInfoStr.length() > 0)
	        			throw new IOException("Error making API call: " + statusInfoStr + '.');

	        		throw new IOException("Error making API call: " + statusStr + '.');
	        	}
	        }
	        else if(AlchemyAPI_Params.OUTPUT_RDF.equals(outputMode)) {
	        	String statusStr = getNodeValue(factory, doc, "//RDF/Description/ResultStatus/text()");
	        	if (null == statusStr || !statusStr.equals("OK")) {
	        		String statusInfoStr = getNodeValue(factory, doc, "//RDF/Description/ResultStatus/text()");
	        		if (null != statusInfoStr && statusInfoStr.length() > 0)
	        			throw new IOException("Error making API call: " + statusInfoStr + '.');

	        		throw new IOException("Error making API call: " + statusStr + '.');
	        	}
	        }

	        return doc;
	    }

	    private String getNodeValue(XPathFactory factory, Document doc, String xpathStr)
	        throws XPathExpressionException
	    {
	        XPath xpath = factory.newXPath();
	        XPathExpression expr = xpath.compile(xpathStr);
	        Object result = expr.evaluate(doc, XPathConstants.NODESET);
	        NodeList results = (NodeList) result;

	        if (results.getLength() > 0 && null != results.item(0))
	            return results.item(0).getNodeValue();

	        return null;
	    }
	}


   
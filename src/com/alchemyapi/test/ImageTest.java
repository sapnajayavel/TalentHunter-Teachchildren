package com.alchemyapi.test;

import com.alchemyapi.api.*;


import org.xml.sax.SAXException;
import org.w3c.dom.Document;

import java.io.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.net.URLEncoder;

class ImageTest {
    public static void main(String[] args)
        throws IOException, SAXException,
               ParserConfigurationException, XPathExpressionException
    {
    	File myFile = new File("word.txt");
        System.out.println("Attempting to read from file in: "+myFile.getCanonicalPath());
        // Create an AlchemyAPI object.
        AlchemyAPI alchemyObj = AlchemyAPI.GetInstanceFromFile("api_key.txt");

        // Extract image for a web URL.
        Document doc ;
        byte[] imageByteArray = readFile("/app/14131518914162.jpg");

        AlchemyAPI_ImageParams imageParams = new AlchemyAPI_ImageParams();
        imageParams.setImage(imageByteArray);
        imageParams.setImagePostMode(AlchemyAPI_ImageParams.RAW);
        //imageParams.setOutputMode("json");
        doc = alchemyObj.ImageGetRankedImageKeywords(imageParams);
        System.out.println(getStringFromDocument(doc));
    }

    // utility function
    private static byte[] readFile(String file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(new File(file), "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }

    // utility method
    private static String getStringFromDocument(Document doc) {
        try {
        	XPathFactory xpathFact = XPathFactory.newInstance();
        	XPath xpath = xpathFact.newXPath();

        	
        	try {
        		String text123 = (String) xpath.evaluate("/results/imageKeywords[1]/keyword/text", doc, XPathConstants.STRING);
				System.out.println("Keyword"+text123);
	            
			} catch (XPathExpressionException e) {
				
				e.printStackTrace();
			}
        	StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            DOMSource domSource = new DOMSource(doc);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
        	return writer.toString();
            
        } catch (TransformerException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

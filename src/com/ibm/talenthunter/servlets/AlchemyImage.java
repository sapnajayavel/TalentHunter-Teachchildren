package com.ibm.talenthunter.servlets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.json.JSONObject;
import org.w3c.dom.Document;

import com.alchemyapi.api.AlchemyAPI;
import com.alchemyapi.api.AlchemyAPI_ImageParams;

public class AlchemyImage {

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
	
	public static JSONObject getImageKeyword(String filename)
	{
		JSONObject jObject = null;
        try {
        	AlchemyAPI alchemyObj = AlchemyAPI.GetInstanceFromFile("api_key.txt");
            Document doc ;
            File f= new File("sample.txt");
            System.out.println(f.getCanonicalPath());

            byte[] imageByteArray = readFile("app/"+filename);
            AlchemyAPI_ImageParams imageParams = new AlchemyAPI_ImageParams();
            imageParams.setImage(imageByteArray);
            imageParams.setImagePostMode(AlchemyAPI_ImageParams.RAW);
            
			doc = alchemyObj.ImageGetRankedImageKeywords(imageParams);
			String keyword=getKeywordFromDocument(doc);
			System.out.println("Keyword:"+getKeywordFromDocument(doc));
			ConceptInsightsSample cis=new ConceptInsightsSample();
			jObject=cis.getResponse(convertKeyword(keyword));

		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return jObject;
	}
	private static String getKeywordFromDocument(Document doc) {
    	String keyword = "";
        try {
        	XPathFactory xpathFact = XPathFactory.newInstance();
        	XPath xpath = xpathFact.newXPath();
        	keyword = (String) xpath.evaluate("/results/imageKeywords[1]/keyword/text", doc, XPathConstants.STRING);
			
	            
			} catch (XPathExpressionException e) {
				
				e.printStackTrace();
			}
        return keyword;
    }
    
    private static String convertKeyword(String keyword)
    {
    	return Character.toUpperCase(keyword.charAt(0)) + keyword.substring(1);
    }
    
}

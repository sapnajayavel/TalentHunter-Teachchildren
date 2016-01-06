package com.ibm.talenthunter.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.alchemyapi.api.AlchemyAPI;
import com.alchemyapi.api.AlchemyAPI_ImageParams;


/**
 * Servlet implementation class AlchemyImageTagging
 */
@WebServlet("/AlchemyImageTagging")
public class AlchemyImageTagging extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AlchemyImageTagging() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	
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
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		
		AlchemyAPI alchemyObj = AlchemyAPI.GetInstanceFromFile("api_key.txt");

        // Extract image for a web URL.
        Document doc ;
        byte[] imageByteArray = readFile("C://Users//916789//Desktop//images_bak.jpg");

        /**
         * File Reading
         * TV 
         * FIXME 
         */
        
        AlchemyAPI_ImageParams imageParams = new AlchemyAPI_ImageParams();
        imageParams.setImage(imageByteArray);
        imageParams.setImagePostMode(AlchemyAPI_ImageParams.RAW);
        try {
			doc = alchemyObj.ImageGetRankedImageKeywords(imageParams);
			String keyword=getKeywordFromDocument(doc);
			System.out.println("Keyword:"+getKeywordFromDocument(doc));
			ConceptInsightsSample cis=new ConceptInsightsSample();
			System.out.println(cis.getResponse(keyword));
			response.setContentType("application/json");
			//response.getWriter().write(cis.getResponse(convertKeyword(keyword)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
 
		
	}

    // utility method
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

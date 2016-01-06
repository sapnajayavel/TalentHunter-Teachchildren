package com.ibm.talenthunter.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;import org.json.JSONTokener;

import com.ibm.json.java.JSONArray;
import com.ibm.nosql.json.api.*; 
import com.ibm.nosql.json.util.*;

 



/**
 * Servlet parameters
 */
@WebServlet(name = "oauth", urlPatterns = { "/oauth/*", "/oauth" }, initParams = {
		// clientId is 'Consumer Key' in the Remote Access UI
		@WebInitParam(name = "clientId", value = "75j73k2rr8u6nd"),
		// clientSecret is 'Consumer Secret' in the Remote Access UI
		@WebInitParam(name = "clientSecret", value = "NnxLzdrafxINozRE"),
		// This must be identical to 'Callback URL' in the Remote Access UI
		@WebInitParam(name = "redirectUri", value = "http://talenthunterapp.mybluemix.net/oauth/redirection"),
		@WebInitParam(name = "environment", value = "https://www.linkedin.com/uas/oauth2/authorization"), })
public class OAuthServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String INSTANCE_URL = "INSTANCE_URL";

	private String clientId = null;
	private String clientSecret = null;
	private String redirectUri = null;
	private String environment = null;
	private String authUrl = null;
	private String tokenUrl = null;
	
	private static Logger logger = Logger.getLogger(OAuthServlet.class.getName());

	private String serviceName = "SQL Database-ap";

	// If running locally complete the variables below with the information in VCAP_SERVICES
	private String databaseHost = "localhost";
	private int port = 50000;
	private String databaseName = "mydb";
	private String user = "myuser";
	private String password = "mypass";
	private String url = "myurl";
	
	String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
	  BasicDBObject sqldb;
	  BasicDBObject credentials;
	  String databaseUrl;

	public void init() throws ServletException {
		super.init();
		
		clientId = this.getInitParameter("clientId");
		clientSecret = this.getInitParameter("clientSecret");
		redirectUri = this.getInitParameter("redirectUri");
		environment = this.getInitParameter("environment");
		try {
			authUrl = environment
					+ "?response_type=code&client_id="
					+ clientId + "&redirect_uri="
					+ URLEncoder.encode(redirectUri, "UTF-8")+"&state=987654321&scope=r_basicprofile";
		} catch (UnsupportedEncodingException e) {
			throw new ServletException(e);
		}

		tokenUrl = environment + "/services/oauth2/token";
	}

	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		String accessToken = (String) request.getSession().getAttribute(ACCESS_TOKEN);

		if (accessToken == null) {
				String instanceUrl = null;

			if (request.getRequestURI().endsWith("oauth")) {
				// we need to send the user to authorize
				response.sendRedirect(authUrl);
				return;
			} 
			else {
				System.out.println("Auth successful - got callback");

				String code = request.getParameter("code");

				
				HttpClient httpclient = HttpClients.createDefault();
				
				HttpPost httppost = new HttpPost("https://www.linkedin.com/uas/oauth2/accessToken");
				InputStream instream;

				// Request parameters and other properties.
				List<NameValuePair> params = new ArrayList<NameValuePair>(2);
				params.add(new BasicNameValuePair("code", code));
				params.add(new BasicNameValuePair("grant_type", "authorization_code"));
				params.add(new BasicNameValuePair("client_id", clientId));
				params.add(new BasicNameValuePair("client_secret", clientSecret));
				params.add(new BasicNameValuePair("redirect_uri", redirectUri));
				httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

				//Execute and get the response.
				

				try {
					HttpResponse resp = httpclient.execute(httppost);
					HttpEntity entity = resp.getEntity();
					System.out.println("Post parameters : " + httppost.getEntity());
					System.out.println("Response Code : " + 
			                                    resp.getStatusLine().getStatusCode());
					if (entity != null) {
					     instream = entity.getContent();
					  /* InputStreamReader str=new InputStreamReader(instream);
					   BufferedReader buffReader = new BufferedReader(str);
					while(buffReader.readLine()!=null)
					{
						System.out.println(buffReader.readLine());
					}*/
					BufferedReader rd = new BufferedReader(
		                       new InputStreamReader(resp.getEntity().getContent()));
		 
				        StringBuffer result = new StringBuffer();
						String line = "";
						while ((line = rd.readLine()) != null) {
							result.append(line);
						}
		 
						System.out.println(result.toString());
		  
					 /* String line = buffReader.readLine();
			           System.out.println("Line read :"+line);*/
			          
			         
					    	JSONObject authResponse = new JSONObject(result.toString());
							System.out.println("Auth response: "
									+ authResponse.toString(2));
							accessToken = authResponse.getString("access_token");
							System.out.println("Got access token: " + accessToken);	
							sendGet(accessToken);
							instream.close();
					}


				} 
				
				catch (Exception e) {
					e.printStackTrace();
					throw new ServletException(e);
				}
				finally {
					
					httppost.releaseConnection();
				}
			}

			// Set a session attribute so that other servlets can get the access
			// token
			request.getSession().setAttribute(ACCESS_TOKEN, accessToken);

			// We also get the instance URL from the OAuth response, so set it
			// in the session too
			request.getSession().setAttribute(INSTANCE_URL, instanceUrl);
			
		}

		response.sendRedirect(request.getContextPath() + "/redirect");
	}
	
	private void sendGet(String accessToken) throws Exception {
		 
		String url = "https://api.linkedin.com/v1/people/~?format=json";
 
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
 
		// add request header
		request.addHeader("Connection", "Keep-Alive");
		request.addHeader("Authorization", "Bearer "+accessToken);
		HttpResponse response = client.execute(request);
 
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + 
                       response.getStatusLine().getStatusCode());
 
		BufferedReader rd = new BufferedReader(
                       new InputStreamReader(response.getEntity().getContent()));
 
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
 
		System.out.println(result.toString());
		JSONObject resultObj=new JSONObject(result.toString());
		System.out.println(resultObj);
		System.out.println(resultObj.get("firstName"));
 
	}
	
	
 
}

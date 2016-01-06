package com.ibm.talenthunter.servlets;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

/**
 * Servlet implementation class ConceptInsights
 */
@WebServlet("/ConceptInsights")
public class ConceptInsights extends HttpServlet {
	private static Logger logger = Logger.getLogger(ConceptInsights.class.getName());
	private static final long serialVersionUID = 1L;
	
	private String serviceName = "concept_insights";
	private String baseURL = "https://gateway.watsonplatform.net/concept-insights-beta/api";
	private static String username = "71587d12-4177-48ad-819a-3029e89ba873";
	private static String password = "jbJzHDiDekDL"; 
	private Executor executor = Executor.newInstance().auth(username, password);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConceptInsights() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		// create the request
		//String text = req.getParameter("text");
		String text="animal";
		System.out.println("in doGet()");
		
		try {
			URI profileURI = new URI(baseURL+"/v1/graph/wikipedia/en-20120601/"+text).normalize();
			Request profileRequest = Request.Get(profileURI);
			System.out.println();
			Response response = executor.execute(profileRequest);
			HttpResponse httpResponse = response.returnResponse();
			resp.setStatus(httpResponse.getStatusLine().getStatusCode());
			String result = EntityUtils.toString(httpResponse.getEntity());
			org.json.JSONObject insightsObject = new org.json.JSONObject(result);
			resp.setContentType("application/json");
			resp.getWriter().write(insightsObject.toString());

		} catch (Exception e) {
			
			resp.setStatus(HttpStatus.SC_BAD_GATEWAY);
		}
		//doPost(req,resp);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		// create the request
		//String text = req.getParameter("text");
		String text="IBM";
		System.out.println("in doPost");
		
		try {
			URI profileURI = new URI(baseURL+"/v1/graph/wikipedia/en-20120601/"+text).normalize();
			Request profileRequest = Request.Get(profileURI);
			System.out.println();
			Executor executor = Executor.newInstance().auth(username, password);
			//Executor executor2 =
			Response response = executor.execute(profileRequest);
			HttpResponse httpResponse = response.returnResponse();
			resp.setStatus(httpResponse.getStatusLine().getStatusCode());
			String result = EntityUtils.toString(httpResponse.getEntity());
			org.json.JSONObject insightsObject = new org.json.JSONObject(result);
			resp.setContentType("application/json");
			resp.getWriter().write(insightsObject.toString());

		} catch (Exception e) {
			
			resp.setStatus(HttpStatus.SC_BAD_GATEWAY);
		}
	}
	
	/**
	 * If exists, process the VCAP_SERVICES environment variable in order to get
	 * the username, password and baseURL
	 */
	private void processVCAPServices() {
		logger.info("Processing VCAP_SERVICES");
		JSONObject sysEnv = getVCAPServices();
		if (sysEnv == null)
			return;
		logger.info("Looking for: " + serviceName);

		for (Object key : sysEnv.keySet()) {
			String keyString = (String) key;
			logger.info("found key: " + key);
			if (keyString.startsWith(serviceName)) {
				JSONArray services = (JSONArray) sysEnv.get(key);
				JSONObject service = (JSONObject) services.get(0);
				JSONObject credentials = (JSONObject) service
						.get("credentials");
				baseURL = (String) credentials.get("url");
				username = (String) credentials.get("username");
				password = (String) credentials.get("password");
				logger.info("baseURL  = " + baseURL);
				logger.info("username = " + username);
				logger.info("password = " + password);
			} else {
				logger.info("Doesn't match /^" + serviceName + "/");
			}
		}
	}

	/**
	 * Gets the <b>VCAP_SERVICES</b> environment variable and return it as a
	 * JSONObject.
	 * 
	 * @return the VCAP_SERVICES as Json
	 */
	private JSONObject getVCAPServices() {
		String envServices = System.getenv("VCAP_SERVICES");
		if (envServices == null)
			return null;
		JSONObject sysEnv = null;
		try {
			sysEnv = JSONObject.parse(envServices);
		} catch (IOException e) {
			// Do nothing, fall through to defaults
			logger.log(Level.SEVERE,
					"Error parsing VCAP_SERVICES: " + e.getMessage(), e);
		}
		return sysEnv;
	}


}

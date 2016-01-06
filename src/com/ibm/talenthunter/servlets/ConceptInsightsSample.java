package com.ibm.talenthunter.servlets;

import java.io.IOException;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.util.EntityUtils;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class ConceptInsightsSample {
	private static Logger logger = Logger.getLogger(ConceptInsightsSample.class.getName());
	private static final long serialVersionUID = 1L;
	private String serviceName = "concept_insights";
	private String baseURL = "https://gateway.watsonplatform.net/concept-insights-beta/api";
	private static String username = "71587d12-4177-48ad-819a-3029e89ba873";
	private static String password = "jbJzHDiDekDL"; 
	private static Executor executorNew = Executor.newInstance().auth(username, password);
	public ConceptInsightsSample()
	{
		processVCAPServices();
	}
  public org.json.JSONObject getResponse(String keyword)
  {
		String result="";
		org.json.JSONObject insightsObject=null;
		System.out.println("Keyword"+keyword);
		try {
			URI profileURI = new URI(baseURL+"/v1/graph/wikipedia/en-20120601/"+keyword).normalize();
			Request profileRequest = Request.Get(profileURI);
			System.out.println();
			Response response = executorNew.                                                                      execute(profileRequest);
			HttpResponse httpResponse = response.returnResponse();
			result = EntityUtils.toString(httpResponse.getEntity());
			insightsObject = new org.json.JSONObject(result);
			System.out.println("insightsObject"+insightsObject.toString());
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return insightsObject;
		
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

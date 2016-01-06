package com.ibm.talenthunter.servlets;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;


/**
 * Servlet implementation class TextToSpeech
 */
@MultipartConfig
@WebServlet("/TextToSpeech")
public class TextToSpeech extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(TextToSpeech.class.getName());

	private String serviceName = "text_to_speech";

	// If running locally complete the variables below with the information in VCAP_SERVICES
	private String baseURL = "https://stream.watsonplatform.net/text-to-speech-beta/api";
	private String username = "0c1448fe-aded-4f4b-9d61-37fdc8d93faa";
	private String password = "s09KrzmEhISH";
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest req, HttpServletResponse resp)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		if (req.getParameter("text") == null ) {
			resp.setStatus(HttpStatus.SC_EXPECTATION_FAILED);
		} else {
			String text = req.getParameter("text");
			//String text = "madhan vaila dosai";
			
			req.setCharacterEncoding("UTF-8");
			try {
				String queryStr = "text="+URLEncoder.encode(text,"UTF-8")+"&voice=VoiceEnUsMichael";
				String url = baseURL + "/v1/synthesize";
				
				if (queryStr != null) {
					url += "?" + queryStr;
				}
				URI uri = new URI(url).normalize();
				System.out.println("URL"+uri);
				Request newReq = Request.Get(uri);
				newReq.addHeader("Accept", "audio/ogg; codecs=opus");
				newReq.addHeader("Content-Type","audio/ogg");
				Executor executor = Executor.newInstance().auth(username, password);
				Response response = executor.execute(newReq);
				
				resp.setHeader("content-disposition", "download; filename=transcript.mp3");
				resp.setHeader("Content-Type","audio/mp3");
				resp.setHeader("Accept", "audio/mp3; codecs=mp3");
				ServletOutputStream servletOutputStream = resp.getOutputStream();
				response.returnResponse().getEntity()
				.writeTo(servletOutputStream);
				servletOutputStream.flush();
				servletOutputStream.close();
			} catch (Exception e) {
				// Log something and return an error message
				logger.log(Level.SEVERE, "got error: " + e.getMessage(), e);
				resp.setStatus(HttpStatus.SC_BAD_GATEWAY);
			}
		}
	}

	
	private JSONObject getVcapServices() {
		String envServices = System.getenv("VCAP_SERVICES");
		if (envServices == null) return null;
		JSONObject sysEnv = null;
		try {
			sysEnv = JSONObject.parse(envServices);
		} catch (IOException e) {
			// Do nothing, fall through to defaults
			logger.log(Level.SEVERE, "Error parsing VCAP_SERVICES: "+e.getMessage(), e);
		}
		return sysEnv;
	}

	@Override
	public void init() throws ServletException {
		super.init();
		processVCAP_Services();
	}
	/**
	 * If exists, process the VCAP_SERVICES environment variable in order to get the
	 * username, password and baseURL
	 */
	private void processVCAP_Services() {
		logger.info("Processing VCAP_SERVICES");
		JSONObject sysEnv = getVcapServices();
		if (sysEnv == null) return;
		logger.info("Looking for: "+ serviceName );

		for (Object key : sysEnv.keySet()) {
			String keyString = (String) key;
			logger.info("found key: " + key);
			if (keyString.startsWith(serviceName)) {
				JSONArray services = (JSONArray)sysEnv.get(key);
				JSONObject service = (JSONObject)services.get(0);
				JSONObject credentials = (JSONObject)service.get("credentials");
				baseURL  = (String)credentials.get("url");
				username = (String)credentials.get("username");
				password = (String)credentials.get("password");
				logger.info("baseURL  = "+baseURL);
				logger.info("username = "+username);
				logger.info("password = "+password);
			} else {
				logger.info("Doesn't match /^"+serviceName+"/");
			}
		}
	}

}

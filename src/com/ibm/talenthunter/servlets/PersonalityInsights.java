package com.ibm.talenthunter.servlets;

import java.io.IOException;









import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
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
import org.json.JSONException;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.ibm.talenthunter.constants.TalentHunterConstants;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;


/**
 * Servlet implementation class PersonalityInsights
 */
@WebServlet("/PersonalityInsights")
@MultipartConfig
public class PersonalityInsights extends HttpServlet {

	private static Logger logger = Logger.getLogger(PersonalityInsights.class.getName());
	private static final long serialVersionUID = 1L;

	private String serviceName = "personality_insights";
	private String baseURL = "https://gateway.watsonplatform.net/personality-insights/api";
	private String username = "136c5110-e7ed-440c-ab44-c92c41283b8c";
	private String password = "AQfynWT5sjAe";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		doPost(req,resp);
	}

	/**
	 * Create and POST a request to the Personality Insights service
	 * 
	 * @param req
	 *            the Http Servlet request
	 * @param resp
	 *            the Http Servlet pesponse
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		logger.info("doPost");
		req.setCharacterEncoding("UTF-8");
		double aggregateScore = 0.0;
		// create the request
		//String text = req.getParameter("text");
		String text="Call me Ishmael. Some years ago-never mind how long precisely-having little or no money in my purse, and nothing particular to interest me on shore, I thought I would sail about a little and see the watery part of the world. It is a way I have of driving off the spleen and regulating the circulation. Whenever I find myself growing grim about the mouth; whenever it is a damp, drizzly November in my soul; whenever I find myself involuntarily pausing before coffin warehouses, and bringing up the rear of every funeral I meet; and especially whenever my hypos get such an upper hand of me, that it requires a strong moral principle to prevent me from deliberately stepping into the street, and methodically knocking people's hats off-then, I account it high time to get to sea as soon as I can. This is my substitute for pistol and ball. With a philosophical flourish Cato throws himself upon his sword; I quietly take to the ship. There is nothing surprising in this. If they but knew it, almost all men in their degree, some time or other, cherish very nearly the same feelings towards the ocean with me.There now is your insular city of the Manhattoes, belted round by wharves as Indian isles by coral reefs-commerce surrounds it with her surf. Right and left, the streets take you waterward. Its extreme downtown is the battery, where that noble mole is washed by waves, and cooled by breezes, which a few hours previous were out of sight of land. Look at the crowds of water-gazers there.Circumambulate the city of a dreamy Sabbath afternoon. Go from Corlears Hook to Coenties Slip, and from thence, by Whitehall, northward. What do you see?-Posted like silent sentinels all around the town, stand thousands upon thousands of mortal men fixed in ocean reveries. Some leaning against the spiles; some seated upon the pier-heads; some looking over the bulwarks of ships from China; some high aloft in the rigging, as if striving to get a still better seaward peep. But these are all landsmen; of week days pent up in lath and plaster-tied to counters, nailed to benches, clinched to desks. How then is this? Are the green fields gone? What do they here?";
		TalentHunterConstants scoreArray = new TalentHunterConstants();
		
		
		try {
			URI profileURI = new URI(baseURL + "/v2/profile").normalize();

			Request profileRequest = Request.Post(profileURI)
					.addHeader("Accept", "application/json")
					.bodyString(text, ContentType.TEXT_PLAIN);
			//HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			
			Executor executor = Executor.newInstance().auth(username, password);
			Response response = executor.execute(profileRequest);
			HttpResponse httpResponse = response.returnResponse();
			resp.setStatus(httpResponse.getStatusLine().getStatusCode());
			String result = EntityUtils.toString(httpResponse.getEntity());
			
			//Parsing the json response
			org.json.JSONObject insightsObject = new org.json.JSONObject(result);
			org.json.JSONArray getBigFive= insightsObject.getJSONObject("tree").getJSONArray("children");
			
			org.json.JSONObject getBigFiveChild = (org.json.JSONObject) getBigFive.get(0);
			org.json.JSONArray getBigFiveChildPoints = getBigFiveChild.getJSONArray("children");
			
			
			org.json.JSONObject getBigFiveChildFinal = (org.json.JSONObject) getBigFiveChildPoints.get(0);
			org.json.JSONArray getBigFiveChildPointsArray = getBigFiveChildFinal.getJSONArray("children");
			
			
			//Db insertion
			 MongoClient mongo = new MongoClient(new MongoClientURI("mongodb://IbmCloud_p8t6c4s0_fpppr5u2_v5mhdpri:h6Hi9UzIpGFPlh25rDWad02OyCzjTEKI@ds055210.mongolab.com:55210/IbmCloud_p8t6c4s0_fpppr5u2"));

	          DB db = mongo.getDB("IbmCloud_p8t6c4s0_fpppr5u2");

	          DBCollection table = db.getCollection("JobSeekers");
	      


	          BasicDBObject document = new BasicDBObject();
	          document.put("id", 1);
			for(int i=0;i<getBigFiveChildPointsArray.length();i++)
			{
				double multiplier = scoreArray.CHARACTERISTICSCORE.get(getKeypoint(getBigFiveChildPointsArray.get(i)));
				System.out.println("multiplier"+multiplier);
				document.put(getKeypoint(getBigFiveChildPointsArray.get(i)), Math.round(getScore(getBigFiveChildPointsArray.get(i))*multiplier*100.0)/100.0);
				aggregateScore += getScore(getBigFiveChildPointsArray.get(i))*multiplier;
				System.out.println(i+"..........."+getBigFiveChildPointsArray.get(i));
			}
			document.put("Aggregate", Math.round(aggregateScore*0.5*100.0) / 100.0);
			table.insert(document);
			BasicDBObject searchQuery = new BasicDBObject();
	          searchQuery.put("id", 1);

	          DBCursor cursor = table.find(searchQuery);

	          while (cursor.hasNext()) {
	              System.out.println( "Inserted: " + cursor.next());
	          }
			resp.setContentType("application/json");
			resp.getWriter().write(insightsObject.toString());

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Service error: " + e.getMessage(), e);
			resp.setStatus(HttpStatus.SC_BAD_GATEWAY);
		}
	}

	@Override
	public void init() throws ServletException {
		super.init();
		processVCAPServices();
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
	
	public static Double getScore(Object obj)
	{
		org.json.JSONObject keypoint = (org.json.JSONObject) obj;
		try {
			return Math.round((((Double)keypoint.get("percentage"))*100) * 100.0) / 100.0;
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getKeypoint(Object obj)
	{
		org.json.JSONObject keypoint = (org.json.JSONObject) obj;
		try {
			return (String)keypoint.get("id").toString();
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		return null;
	}
	
}
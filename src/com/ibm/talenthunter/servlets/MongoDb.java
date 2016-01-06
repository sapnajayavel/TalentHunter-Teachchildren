package com.ibm.talenthunter.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;



/**
 * Servlet implementation class MongoDb
 */
@WebServlet("/MongoDb")
public class MongoDb extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MongoDb() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */


    protected void doGet(HttpServletRequest request,
  			HttpServletResponse response) throws ServletException, IOException {
      PrintWriter out = response.getWriter();
      try{
      /*    String connURL = getServiceURI();*/
          MongoClient mongo = new MongoClient(new MongoClientURI("mongodb://IbmCloud_p8t6c4s0_fpppr5u2_v5mhdpri:h6Hi9UzIpGFPlh25rDWad02OyCzjTEKI@ds055210.mongolab.com:55210/IbmCloud_p8t6c4s0_fpppr5u2"));

          DB db = mongo.getDB("IbmCloud_p8t6c4s0_fpppr5u2");

          DBCollection table = db.getCollection("Employees");
      
          BasicDBObject searchQuery = new BasicDBObject();
          searchQuery.put("name", "Vigneshwaran Thulasi Doss");

          DBCursor cursor = table.find(searchQuery);

          while (cursor.hasNext()) {
              out.println( "Inserted: " + cursor.next());
          }

          out.println("Success!!");

      } catch (Exception e) {
          out.println("Failed: " + e.getMessage());
          e.printStackTrace();
      }
    }

 /*   public String getServiceURI() throws Exception {
      CloudEnvironment environment = new CloudEnvironment();
      if ( environment.getServiceDataByLabels("mongolab").size() == 0 ) {
          throw new Exception( "No MongoDB service is bound to this app!!" );
      } 

      Map credential = (Map)((Map)environment.getServiceDataByLabels("mongolab").get(0)).get( "credentials" );
      System.out.println("credentials"+(String)credential.get( "uri" ));
   
      return (String)credential.get( "uri" );
    }*/
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}

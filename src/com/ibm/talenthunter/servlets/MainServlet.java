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
 * Servlet implementation class MainServlet
 */
@WebServlet("/MainServlet")
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
	    try{
	        String connURL = getServiceURI();
	        MongoClient mongo = new MongoClient(new MongoClientURI(connURL));

	        DB db = mongo.getDB("db");

	        DBCollection table = db.getCollection("user");


	        BasicDBObject document = new BasicDBObject();
	        document.put("name", "Tom");
	        document.put("age", 30);
	        document.put("createdDate", new Date());
	        table.insert(document);

	        BasicDBObject searchQuery = new BasicDBObject();
	        searchQuery.put("name", "Tom");

	        DBCursor cursor = table.find(searchQuery);

	        while (cursor.hasNext()) {
	            out.println( "Inserted: " + cursor.next());
	        }

	        BasicDBObject query = new BasicDBObject();
	        query.put("name", "Tom");

	        BasicDBObject newDocument = new BasicDBObject();
	        newDocument.put("name", "Tina");

	        BasicDBObject updateObj = new BasicDBObject();
	        updateObj.put("$set", newDocument);

	        table.update(query, updateObj);

	        BasicDBObject searchQuery2 = new BasicDBObject().append("name", "Tina");

	        DBCursor cursor2 = table.find(searchQuery2);

	        while (cursor2.hasNext()) {
	            out.println( "Updated: " + cursor2.next());
	        }

	        out.println("Success!!");

	    } catch (Exception e) {
	        out.println("Failed: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

	public String getServiceURI()  {
	    /*public String getServiceURI() throws Exception {
	     * CloudEnvironment environment = new CloudEnvironment();
	    if ( environment.getServiceDataByLabels("mongodb-2.4").size() == 0 ) {
	        throw new Exception( "No MongoDB service is bound to this app!!" );
	    } 

	    Map credential = (Map)((Map)environment.getServiceDataByLabels("mongodb-2.4").get(0)).get( "credentials" );
	 
	    return (String)credential.get( "url" );*/
		return "";
	  }

}

package com.ibm.talenthunter.servlets;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import com.ibm.nosql.json.api.BasicDBList;
import com.ibm.nosql.json.api.BasicDBObject;
import com.ibm.nosql.json.util.JSON;

/**
 * Servlet implementation class SQLdb
 */
@WebServlet("/SQLdb")
public class SQLdb extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SQLdb() {
        super();
        // TODO Auto-generated constructor stub
    }

	

	// set defaults
	private String databaseHost = "localhost";
	private int port = 50000;
	private String databaseName = "mydb";
	private String user = "myuser";
	private String password = "mypass";
	private String url = "myurl";
	@Resource(lookup = "jdbc/SQLDB")
	private DataSource myDataSource;
	
	private boolean processVCAP(PrintWriter writer) {
		// VCAP_SERVICES is a system environment variable
		// Parse it to obtain the for DB2 connection info
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		writer.println("VCAP_SERVICES content: " + VCAP_SERVICES);

		if (VCAP_SERVICES != null) {
			// parse the VCAP JSON structure
			BasicDBObject obj = (BasicDBObject) JSON.parse(VCAP_SERVICES);
			String thekey = null;
			Set<String> keys = obj.keySet();
			writer.println("Searching through VCAP keys");
			// Look for the VCAP key that holds the SQLDB information
			for (String eachkey : keys) {
				writer.println("Key is: " + eachkey);
				// Just in case the service name gets changed to lower case in the future, use toUpperCase
				if (eachkey.toUpperCase().contains("SQLDB")) {
					thekey = eachkey;
				}
			}
			if (thekey == null) {
				writer.println("Cannot find any SQLDB service in the VCAP; exiting");
				return false;
			}
			BasicDBList list = (BasicDBList) obj.get(thekey);
			obj = (BasicDBObject) list.get("0");
			writer.println("Service found: " + obj.get("name"));
			// parse all the credentials from the vcap env variable
			obj = (BasicDBObject) obj.get("credentials");
			databaseHost = (String) obj.get("host");
			databaseName = (String) obj.get("db");
			port = (int)obj.get("port");
			user = (String) obj.get("username");
			password = (String) obj.get("password");
			url = (String) obj.get("jdbcurl");
		} else {
			writer.println("VCAP_SERVICES is null");
			return false;
		}
		writer.println();
		writer.println("database host: " + databaseHost);
		writer.println("database port: " + port);
		writer.println("database name: " + databaseName);
		writer.println("username: " + user);
		writer.println("password: " + password);
		writer.println("url: " + url);
		return true;
	}
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		response.setStatus(200);
		PrintWriter writer = response.getWriter();
		writer.println("IBM SQL Database, Java Demo Application using DB2 drivers");
		writer.println("Servlet: " + this.getClass().getName());
		writer.println();
		writer.println("Host IP:" + InetAddress.getLocalHost().getHostAddress());
		
		processVCAP(writer);

		// process the VCAP env variable and set all the global connection parameters
		if (processVCAP(writer)) {
	
			// Connect to the Database
			Connection con = null;
			Statement stmt=null;
			try {
				writer.println();
				writer.println("Connecting to the database");	
				
				con=myDataSource.getConnection();
				writer.println();
				con.setAutoCommit(false);
				stmt = con.createStatement();
				/*stmt.executeUpdate("CREATE TABLE MYTABLE (NAME VARCHAR(20), ID INTEGER)");*/
				stmt.executeUpdate("INSERT INTO  MYTABLE (NAME, ID) VALUES ('BlueMix', 123)");
				writer.println("inserting");
				writer.println(
						"Test Data inserted into "
								+ con.getMetaData().getDatabaseProductName());
				
				String query = "SELECT * FROM MYTABLE"
						+ " WHERE NAME LIKE \'Blue%\'";
				ResultSet rs = stmt.executeQuery(query);
				writer.println("Executing: " + query);
	
				while (rs.next()) {
					String id = rs.getString(1);
					writer.println("  Found Employee: " + id);
				}
				// Close the ResultSet
				rs.close();
			} catch (SQLException e) {
				writer.println("Error connecting to database");
				writer.println("SQL Exception: " + e.getMessage());
				return;
			} 
	
			
			// Close everything off
			try {
				// Close the Statement
				stmt.close();
				// Connection must be on a unit-of-work boundary to allow close
				con.commit();
				// Close the connection
				con.close();
				writer.println("Finished");
	
			} catch (SQLException e) {
				writer.println("Error closing things off");
				writer.println("SQL Exception: " + e);
			}
		}
		writer.close();
	}

	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}
}

package com.ibm.talenthunter.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.talenthunter.util.DbHelper;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

/**
 * Servlet implementation class GetAllJobSeekers
 */
@WebServlet("/GetAllJobSeekers")
public class GetAllJobSeekers extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAllJobSeekers() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			DB db = DbHelper.getDbConnection();
			DBCollection table = db.getCollection("JobSeeker");
			
			DBCursor cursor = table.find();
	        int i=1;
	        
	        org.json.JSONObject employees=new org.json.JSONObject();
	        org.json.JSONArray employeesList=new org.json.JSONArray();
	        while (cursor.hasNext()) { 
	           employees = new org.json.JSONObject(cursor.next().toString());
	           employeesList.put(employees);
	        }
	        response.setContentType("application/json");
			response.getWriter().write(employeesList.toString());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}

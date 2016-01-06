package com.ibm.talenthunter.servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.talenthunter.util.DbHelper;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * Servlet implementation class RetrieveCompatibleJobSeeker
 */
@WebServlet("/RetrieveCompatibleJobSeeker")
public class RetrieveCompatibleJobSeeker extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RetrieveCompatibleJobSeeker() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			String employeeId = request.getParameter("empId");
			DB db = DbHelper.getDbConnection();
			int empAggregate = getEmployeetAggregate(employeeId,db);
			int minempAggregate = empAggregate-4;
			int maxempAggregate = empAggregate+4;
			System.out.println("minempAggregate"+minempAggregate+"maxempAggregate"+maxempAggregate);
			ArrayList<String> employeeskillList = getEmployeeSkillSet(employeeId,db);
			DBCollection table = db.getCollection("JobSeeker");
			DBCursor cursor = table.find();
			int seekerAggregate = 0;
			BasicDBList seekerSkill = new BasicDBList();
			DBObject resultCursor ;
			ArrayList<String> skillList = new ArrayList<String>();
			org.json.JSONObject seeker=new org.json.JSONObject();
		    org.json.JSONArray seekerArray=new org.json.JSONArray();
	        while (cursor.hasNext()) { 
	           resultCursor=cursor.next();
	           seekerAggregate = (int) resultCursor.get("aggregate");
	           System.out.println("seekerAggregate"+seekerAggregate);
	           seekerSkill = (BasicDBList) resultCursor.get("skillSet");
	           for(int i=0;i<seekerSkill.size();i++)
	           {
	        	   skillList.add((String) ((BasicDBObject)seekerSkill.get(i)).get("name"));
	           }
	           int count = compareskills(employeeskillList, skillList);
	           System.out.println("Count"+count);
	           if(seekerAggregate >= minempAggregate && seekerAggregate <= maxempAggregate &&  count >= 2)
	           {
	        	   System.out.println("Found job seeker");
	        	   seeker = new org.json.JSONObject(resultCursor.toString());
	        	   seekerArray.put(seeker);
	           }
	           System.out.println(seekerSkill);
	           skillList = new ArrayList<String>();
	           
	        }
	        response.setContentType("application/json");
			response.getWriter().write(seekerArray.toString());
			
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

	public int getEmployeetAggregate(String id,DB db)
	{
		DBCollection table = db.getCollection("Employees");
		BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("id", new Integer(id));
		DBCursor cursor = table.find(searchQuery);
        int empAggregateScore = 0;
        while (cursor.hasNext()) { 
        	empAggregateScore= (int) cursor.next().get("aggregate");
        	 
        }
        return empAggregateScore;
	}
	
	public ArrayList<String> getEmployeeSkillSet(String id,DB db)
	{
		DBCollection table = db.getCollection("Employees");
		BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("id", new Integer(id));
		DBCursor cursor = table.find(searchQuery);
		ArrayList<String> employeeSkillList = new ArrayList<String>();
		BasicDBList employeeSkill = new BasicDBList();
        while (cursor.hasNext()) { 
        	
        	employeeSkill = (BasicDBList) cursor.next().get("skillSet");
        	System.out.println(employeeSkill);
        	for(int i=0;i<employeeSkill.size();i++)
	           {
        		employeeSkillList.add((String) ((BasicDBObject)employeeSkill.get(i)).get("name"));
	           }
        	 
        }
        return employeeSkillList;
	}
	
	public int compareskills(ArrayList<String> source,ArrayList<String> target)
	{
		int count = 0;
		for (int i = 0; i < target.size(); i++) {
			if(source.contains(target.get(i))){
				count ++;
			}
		}
		
		return count;
	}
}

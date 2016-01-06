package com.ibm.talenthunter.servlets;


import com.ibm.talenthunter.util.DbHelper;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class InsertEmployeeData {

	public static void main(String[] args) {
		DB db = DbHelper.getDbConnection();
		DBCollection table = db.getCollection("Employees");
        BasicDBObject document = new BasicDBObject();
        document.put("id", 105);
        document.put("name", "Sapna J");
        document.put("picurl", "https://media.licdn.com/mpr/mpr/shrinknp_400_400/AAEAAQAAAAAAAAIcAAAAJGJlOWE2MTAwLTU3ODAtNDBhMC1iODE2LWYzZjJmMjU3Mzg1Mw.jpg");
        document.put("experience", "1");
        document.put("summary", "I am a great computer enthusiast and looking forward to establish as a competent software engineer in a reputed organisation.Persistent, Creative and Adaptable person with always ready to learn new technologies and take up challenges.Team leader--like to be put on team leader roles and have proved to be capable of it.-have been assigned the role of class representative and placement coordinator during the course of study");
        
        //Personal details
        BasicDBList personal = new BasicDBList();
        BasicDBObject personalObj = new BasicDBObject();
        personalObj.put("phone","9380453949");
        personalObj.put("email", "sapna.jayavel@avnet.com");
        personalObj.put("dob", "08/11/1992");
        personalObj.put("age", 22);
        personalObj.put("marital status", "Single");
        personal.add(personalObj);
        
        //Skill set
        BasicDBList skills = new BasicDBList();
        BasicDBObject skills1 = new BasicDBObject();
        skills1.put("id",100);
        skills1.put("name", "Javascript");
        BasicDBObject skills2 = new BasicDBObject();
        skills2.put("id",101);
        skills2.put("name", "Commerce");
        BasicDBObject skills3 = new BasicDBObject();
        skills3.put("id",102);
        skills3.put("name", "BigData");
        BasicDBObject skills4 = new BasicDBObject();
        skills4.put("id",103);
        skills4.put("name", "Hybris");
        skills.add(skills1);
        skills.add(skills2);
        skills.add(skills3);
        skills.add(skills4);
        
        //Project Details
        BasicDBList projects = new BasicDBList();
        BasicDBObject projects1 = new BasicDBObject();
        projects1.put("id",100);
        projects1.put("name", "The Childrens Place -Commerce");
        BasicDBObject projects2 = new BasicDBObject();
        projects2.put("id",101);
        projects2.put("name", "The Childrens Place -Portal");
        BasicDBObject projects3 = new BasicDBObject();
        projects3.put("id",102);
        projects3.put("name", "Webservice Development for AMS");
        
        BasicDBObject projects4 = new BasicDBObject();
        projects4.put("id",102);
        projects4.put("name", "Hybris abigail");
        projects.add(projects1);
        projects.add(projects2);
        projects.add(projects3);
        projects.add(projects4);
 
        
        //Personality details
        BasicDBList personality = new BasicDBList();
        BasicDBObject personalityObj = new BasicDBObject();
        personalityObj.put("Openness",25);
        personalityObj.put("Conscientiousness", 22);
        personalityObj.put("Extraversion", 17);
        personalityObj.put("Agreeableness", 12);
        personalityObj.put("Emotional Range", 5);
        personality.add(personalityObj);
        
        document.put("personalDetails", personal);
        document.put("skillSet", skills);
        document.put("projectDetails", projects);
        //Aggregate score
        document.put("aggregate",38);
        table.insert(document);
   
        
        
	}
	
	
	public void dropCollection(String collectionName,DB db)
	{
        DBCollection table = db.getCollection(collectionName);
        table.drop();
	}
}

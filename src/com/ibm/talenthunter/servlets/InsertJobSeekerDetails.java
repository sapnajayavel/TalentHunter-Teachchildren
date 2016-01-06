package com.ibm.talenthunter.servlets;

import com.ibm.talenthunter.util.DbHelper;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class InsertJobSeekerDetails {
	public static void main(String[] args) {
		DB db = DbHelper.getDbConnection();
		DBCollection table = db.getCollection("JobSeeker");
        BasicDBObject document = new BasicDBObject();
        document.put("id", 103);
        document.put("name", "Manikanta tankala");
        document.put("picurl", "https://media.licdn.com/mpr/mpr/shrinknp_400_400/p/8/000/2c1/229/2e15f1d.jpg");
        document.put("experience", "4");
        document.put("summary", "Hardworking and good human. Trust, value and satisfaction are my core values around which I make myself. Created a company CreatusLabs for making windows phone apps with more than 21 apps in market, certified with 101k downloads. Been successful developer and marketer. Have quite a good social life, hanging out with friends and good interest in social work.I believe in giving back to the society");
        
        //Personal details
        BasicDBList personal = new BasicDBList();
        BasicDBObject personalObj = new BasicDBObject();
        personalObj.put("phone","8323268919");
        personalObj.put("email", "mani.tankala@gmail.com");
        personalObj.put("dob", "12/5/1989");
        personalObj.put("age", 26);
        personalObj.put("marital status", "Single");
        personal.add(personalObj);
        
        //Skill set
        BasicDBList skills = new BasicDBList();
        BasicDBObject skills1 = new BasicDBObject();
        skills1.put("id",100);
        skills1.put("name", "Portal");
        BasicDBObject skills2 = new BasicDBObject();
        skills2.put("id",101);
        skills2.put("name", "Java");
        BasicDBObject skills3 = new BasicDBObject();
        skills3.put("id",102);
        skills3.put("name", "Big Data");
        skills.add(skills1);
        skills.add(skills2);
        skills.add(skills3);
        
        //Project Details
        BasicDBList projects = new BasicDBList();
        BasicDBObject projects1 = new BasicDBObject();
        projects1.put("id",100);
        projects1.put("name", "L1/L2 eCommerce Support");
        BasicDBObject projects2 = new BasicDBObject();
        projects2.put("id",101);
        projects2.put("name", "The Children's Place");
        BasicDBObject projects3 = new BasicDBObject();
        projects3.put("id",102);
        projects3.put("name", "PuppetConf comp ");
        BasicDBObject projects4 = new BasicDBObject();
        projects4.put("id",103);
        projects4.put("name", "J2EE Development");
        projects.add(projects1);
        projects.add(projects2);
        projects.add(projects3);
        projects.add(projects4);
        
        //Personality details
        BasicDBList personality = new BasicDBList();
        BasicDBObject personalityObj = new BasicDBObject();
        personalityObj.put("Openness",20);
        personalityObj.put("Conscientiousness", 19);
        personalityObj.put("Extraversion", 17.5);
        personalityObj.put("Agreeableness", 12);
        personalityObj.put("Emotional Range", 5.2);
        personality.add(personalityObj);
        
        document.put("personalDetails", personal);
        document.put("skillSet", skills);
        document.put("projectDetails", projects);
        //Aggregate score
        document.put("aggregate",40);
        table.insert(document);

	}

}

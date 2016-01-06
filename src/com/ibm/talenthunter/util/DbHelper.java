package com.ibm.talenthunter.util;

import java.net.UnknownHostException;

import java.util.Map;


import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public  class  DbHelper {
	static DB db= null;
	public static DB getDbConnection(){
	
		if(db==null){
		
		
		          MongoClient mongo1 = new MongoClient(new MongoClientURI("mongodb://IbmCloud_p8t6c4s0_fpppr5u2_v5mhdpri:h6Hi9UzIpGFPlh25rDWad02OyCzjTEKI@ds055210.mongolab.com:55210/IbmCloud_p8t6c4s0_fpppr5u2"));
				db = mongo1.getDB("IbmCloud_p8t6c4s0_fpppr5u2");
		        	
		}
		
         return db;
	}
	
	 /*public static String getServiceURI() throws Exception {
	      CloudEnvironment environment = new CloudEnvironment();
	      if ( environment.getServiceDataByLabels("mongolab").size() == 0 ) {
	          throw new Exception( "No MongoDB service is bound to this app!!" );
	      } 

	      Map credential = (Map)((Map)environment.getServiceDataByLabels("mongolab").get(0)).get( "credentials" );
	      System.out.println("credentials"+(String)credential.get( "uri" ));
	   
	      return (String)credential.get( "uri" );
	    }*/
	
	
}

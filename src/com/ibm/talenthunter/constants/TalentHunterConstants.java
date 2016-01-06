package com.ibm.talenthunter.constants;

import java.util.Hashtable;

public class TalentHunterConstants {
	
	public final Hashtable<String,Double> CHARACTERISTICSCORE = new Hashtable<String,Double>() {{
	    put("Openness",      0.25);
	    put("Conscientiousness",      0.2);
	    put("Extraversion",     0.25);
	    put("Agreeableness", 0.2);
	    put("Neuroticism",    0.1);
	 }};
	 
}

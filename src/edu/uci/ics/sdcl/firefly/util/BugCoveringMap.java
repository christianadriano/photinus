package edu.uci.ics.sdcl.firefly.util;

import java.util.HashMap;

public class BugCoveringMap {

	public static  HashMap<String,String> initialize(){
		//Obtain bug covering question list
		PropertyManager manager = PropertyManager.initializeSingleton();
		HashMap<String, String> bugCoveringMap = new HashMap<String,String>();
		String[] listOfBugPointingQuestions = manager.bugCoveringList.split(";");
		for(String questionID:listOfBugPointingQuestions){
			bugCoveringMap.put(questionID,questionID);
		}
		return bugCoveringMap;
	}

}

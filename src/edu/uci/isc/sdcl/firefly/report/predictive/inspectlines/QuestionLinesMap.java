package edu.uci.isc.sdcl.firefly.report.predictive.inspectlines;

import java.util.HashMap;
import java.util.Iterator;

public class QuestionLinesMap {

	String fileName; //Same as Java method, e.g., HIT01_8
	
	Integer questionID; //Same as MicrotaskID
	
	boolean isBugCovering;
	
	public HashMap<String,String> allLines; //all Lines numbers
	
	public HashMap<String,String> faultyLines; //only lines with the actual fault
	
	public HashMap<String,String> nonFaultyLines; //lines with no fault
	
	public HashMap<String,String> nearFaultyLines; //lines that are covered by the same faulty question

	public QuestionLinesMap(String fileName, Integer questionID,HashMap<String,String> allCoveredLinesMap, 
			HashMap<String,String> faultyLinesMap){
		this.fileName = fileName;
		this.questionID = questionID;
		this.allLines = (HashMap<String, String>) allCoveredLinesMap.clone();
		
		if(faultyLinesMap!=null){
			this.isBugCovering = true;
			this.faultyLines = (HashMap<String, String>) faultyLinesMap.clone();
			computeNearFaultyLines();
		}
		else{
			this.isBugCovering = false;
			this.nonFaultyLines = this.allLines;
		}
	}
	
	/** Computes the non-faulty lines and the nearFaulty-lines 
	 *  Near faulty lines exist solely if the question is bug covering.
	 * 
	 * */
	
	public void computeNearFaultyLines(){
		
		this.nearFaultyLines = (HashMap<String, String>) this.allLines.clone();
		
		Iterator<String> iterFaulty = this.faultyLines.keySet().iterator();
		while(iterFaulty.hasNext()){
			String line = iterFaulty.next();
			nearFaultyLines.remove(line);
		}
		
	}
	
	public int getLOCs(){
		if(allLines!=null)
			return allLines.size();
		else 
			return 0;
	}
	
	
	public static void test(){
		HashMap<String, String> allLinesMap = new HashMap<String,String>();
		allLinesMap.put("272","272");
		allLinesMap.put("273","273");
		allLinesMap.put("279","279");
		allLinesMap.put("280","280");
		allLinesMap.put("286","286");
		allLinesMap.put("288","288");
		allLinesMap.put("290","290");
		HashMap<String, String> faltyLinesMap = new HashMap<String,String>();
		faltyLinesMap.put("279","279");
		
		QuestionLinesMap map =  new QuestionLinesMap("HIT01_8", 1, allLinesMap,faltyLinesMap);
		System.out.println("Expected true: " + map.nearFaultyLines.containsKey("273"));
		System.out.println("Expected false: " + map.nearFaultyLines.containsKey("279"));
		System.out.println("Expected true: " + map.faultyLines.containsKey("279"));
		System.out.println("Expected size 6 = " + map.nearFaultyLines.size());
		System.out.println("Expected size 7 = " + map.allLines.size());
		
	}
	
	public static void main(String args[]){
	
		System.out.println("Running test on QuestionLinesMap class");
		QuestionLinesMap.test();
		 
	}
	
	
	
}



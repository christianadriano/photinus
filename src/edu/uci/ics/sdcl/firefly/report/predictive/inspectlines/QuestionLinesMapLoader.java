package edu.uci.ics.sdcl.firefly.report.predictive.inspectlines;

 
import java.util.ArrayList;
import java.util.HashMap;

import edu.uci.ics.sdcl.firefly.util.ReadWriteFile;

/**
 * Loads all information about lines covered by each question
 * 
 * @author adrianoc
 *
 */
public class QuestionLinesMapLoader {

	private String sourceFileName = "QuestionLinesMapping.csv";
	
	private String path = "C://firefly//InspectLinesAnalysis//";
	
	/** Keeps the number of lines per question */
	public static HashMap<String, Integer> questionLineCount;

	/** 
	 * The list is loaded from a text file 
	 * @return list of QuestionLineData for the variables 
	 */
	public HashMap<String, QuestionLinesMap>  loadList(){
		ArrayList<String> fileContentList = ReadWriteFile.readToBuffer(this.path, this.sourceFileName);
		return loadContent(fileContentList);
	}
	
	
	
	private HashMap<String, QuestionLinesMap>  loadContent(ArrayList<String> buffer){
		
		HashMap<String, QuestionLinesMap> questionLinesMapping = new HashMap<String, QuestionLinesMap>();

		//Header : bugID, ID,	LineID,	isBugCovering,	question,	type,	faulty_lines,	all_Lines,

		QuestionLinesMapLoader.questionLineCount = new HashMap<String, Integer>();
		
		buffer.remove(0); //removes header
		for(String line : buffer){
			String[] tokens = line.split(",");
			String fileName = new String(tokens[0]);
			Integer questionID = new Integer(tokens[1]);
			String faulty_LinesList = new String (tokens[5]);
			HashMap<String,String> faultLinesMap=null;
			if(faulty_LinesList.trim().length()>0)
				faultLinesMap = loadMap(faulty_LinesList);
			
			String all_LinesList = new String(tokens[6]);
			HashMap<String,String> allLinesMap=null;
			if(all_LinesList.trim().length()>0){
				allLinesMap = loadMap(all_LinesList);
				questionLineCount.put(questionID.toString(), allLinesMap.size());
			}
			else
				System.err.println("ERROR lines map empty for bugID: "+questionID);
			
			QuestionLinesMap questionLinesMap =  new QuestionLinesMap(fileName,questionID, allLinesMap,faultLinesMap);
			questionLinesMapping.put(questionID.toString(),questionLinesMap);
		}
		
		return questionLinesMapping;
	}

	private HashMap<String, String> loadMap(String list) {

		HashMap<String,String> map =  new HashMap<String,String>();

		String[] tokens = list.split(";");
		for(String line:tokens){
			map.put(line, line);
		}
		return map;
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
		
			QuestionLinesMapLoader loader = new QuestionLinesMapLoader();
			HashMap<String, QuestionLinesMap> allMaps = loader.loadList();
			QuestionLinesMap actualMap =  allMaps.get("1");
			
			System.out.println("Expected true: " + actualMap.nearFaultyLines.containsKey("273"));
			System.out.println("Expected false: " + actualMap.nearFaultyLines.containsKey("279"));
			System.out.println("Expected true: " + actualMap.faultyLines.containsKey("279"));
			System.out.println("Expected size 6 = " + actualMap.nearFaultyLines.size());
			System.out.println("Expected size 7 = " + actualMap.allLines.size());
		 
		
	}
	
	public static void main(String args[]){
		
		System.out.println("Running test on LoadQuestionLinesMap class");
		QuestionLinesMapLoader.test();
		 
	}
	
	
}

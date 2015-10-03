package edu.uci.ics.sdcl.firefly.report.predictive.spectra;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import edu.uci.ics.sdcl.firefly.util.ReadWriteFile;

/**
 * This class keeps the list of lines where variables are used in the Java methods HIT01_8, HIT02_24, HIT03_6, HIT04
 * 
 * Data is keept in a file with the following format:
 * JavaMethod;QuestionID;1,2,3,4,5
 * 
 * @author adrianoc
 *
 */
public class VariableQuestionLines {

	private String sourceFileName = "variableQuestionLines.txt";
	
	private String path = "C://firefly//SpectraAnalysis//";
	
	
	/** fileName, Map of QuestionID with list of lines */
	private HashMap<String, HashMap<String, ArrayList<Integer>>> questionLinesMap = new HashMap<String, HashMap<String, ArrayList<Integer>>>(); 
	
	public VariableQuestionLines(){
		setupData();
	}
	
	
	
	public void setupData(){
		
		ArrayList<String> fileContentList = ReadWriteFile.readToBuffer(this.path, this.sourceFileName);
		
		
			
	}
	
	
	private HashMap<String,HashMap<String,ArrayList<QuestionLinesData>>>  buildDataStructure(ArrayList<String> buffer){
		
		HashMap<String,HashMap<String,ArrayList<QuestionLinesData>>>  questionLineMap = 
				new HashMap<String,HashMap<String,ArrayList<QuestionLinesData>>> ();
		
		for(String line : buffer){
			
			String[] tokens = line.split(":");
			String fileName = tokens[0];
			String questionID = tokens[1];
			String lineNumberList = tokens[2];
			
			//Lines are 
			String[] lineNumberTokens = lineNumberList.split(",");
			ArrayList<QuestionLinesData> list = new ArrayList<QuestionLinesData>();
			QuestionLinesData lineNumberData =  new QuestionLinesData(fileName,questionID);
			
			for(String lineNumber : lineNumberTokens){
				lineNumberData.lineNumbers.add(lineNumber);
			}
			
			list.add(lineNumberData);
			questionLineMap = updateMap(questionLineMap, lineNumberData);
		}
	}
	
	
	private HashMap<String,HashMap<String,ArrayList<QuestionLinesData>>> updateMap(
			HashMap<String,HashMap<String,ArrayList<QuestionLinesData>>> map,
			QuestionLinesData lineData){
		
		HashMap<String,ArrayList<QuestionLinesData>> fileNameMap = map.get(lineData.fileName);
		
		for(ArrayList<QuestionLinesData> lineNumberList : fileNameMap.values()){
			
		}
	}
	
	
	
}

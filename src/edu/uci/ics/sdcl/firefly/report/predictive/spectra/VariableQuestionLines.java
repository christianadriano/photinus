package edu.uci.ics.sdcl.firefly.report.predictive.spectra;

import java.util.ArrayList;
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

	/** 
	 * The list is loaded from a text file 
	 * @return list of QuestionLineData for the variables 
	 */
	public ArrayList<QuestionLinesData>  loadList(){
		ArrayList<String> fileContentList = ReadWriteFile.readToBuffer(this.path, this.sourceFileName);
		return initializeMap(fileContentList);
	}
	
	
	private ArrayList<QuestionLinesData>  initializeMap(ArrayList<String> buffer){
		
		ArrayList<QuestionLinesData> questionLinesDataList = new ArrayList<QuestionLinesData>();
		
		for(String line : buffer){
			
			String[] tokens = line.split(":");
			String fileName = tokens[0];
			String questionID = tokens[1];
			String lineNumberList = tokens[2];
			
			//Lines are 
			String[] lineNumberTokens = lineNumberList.split(",");
			QuestionLinesData lineNumberData =  new QuestionLinesData(fileName,questionID);
			
			for(String lineNumber : lineNumberTokens){
				lineNumberData.lineNumbers.add(lineNumber);
			}
			
			questionLinesDataList.add(lineNumberData);
		}
		return questionLinesDataList;
	}

}

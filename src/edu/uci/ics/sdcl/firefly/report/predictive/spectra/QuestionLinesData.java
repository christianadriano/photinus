package edu.uci.ics.sdcl.firefly.report.predictive.spectra;

import java.util.ArrayList;

public class QuestionLinesData {

	String fileName;
	
	String questionID;
	
	ArrayList<String> lineNumbers;

	public QuestionLinesData(String fileName, String questionID){
		this.fileName = fileName;
		this.questionID = questionID;
	}
	
}

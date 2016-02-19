package edu.uci.ics.sdcl.firefly.report.predictive.spectra;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uci.ics.sdcl.firefly.util.ReadWriteFile;
import edu.uci.isc.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMap;

/**
 * This class loads the number of lines each question covers
 * 
 * @author adrianoc
 *
 */
public class QuestionLOCs {

	private String sourceFileName = "QuestionTypeLOCs.csv";

	private String path = "C://firefly//SpectraAnalysis//";

	/** 
	 * The list is loaded from a text file 
	 * @return list of QuestionLineData for the variables 
	 */
	public HashMap<String,Integer>   loadList(){
		ArrayList<String> fileContentList = ReadWriteFile.readToBuffer(this.path, this.sourceFileName);
		return initializeMap(fileContentList);
	}

	private HashMap<String,Integer>  initializeMap(ArrayList<String> buffer){

		HashMap<String, Integer> ID_LOCs_Map = new HashMap<String, Integer>();

		buffer.remove(0); //removes header
		for(String line : buffer){
			String[] tokens = line.split(",");
			Integer questionID = new Integer(tokens[0]);
			Integer LOCs = new Integer(tokens[4]);
			System.out.println(questionID.toString()+":"+ LOCs.toString());
			ID_LOCs_Map.put(questionID.toString(),LOCs);
		}
		return ID_LOCs_Map;
	}
 
}

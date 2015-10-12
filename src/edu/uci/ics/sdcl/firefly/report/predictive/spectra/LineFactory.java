package edu.uci.ics.sdcl.firefly.report.predictive.spectra;

import java.util.ArrayList;
import java.util.TreeMap;

import edu.uci.ics.sdcl.firefly.util.ReadWriteFile;


/**
 * Converts WorkerSessions that are by question to be by line.
 * This involves the following steps:
 * 
 * 1- Identify all lines covered by a question (input file, for now)
 * 2- Ignore empty lines, Javadoc lines, or lines with opening or closing curly brackets
 * 2- Creates a new microtask for each line
 * 
 * TODO:
 * - Bug covering IDs will have to be configurable in the PropertyManager
 * - Change FileSessionDTO to be set for either Lines or Questions
 * - automatically dentify all lines covered by a question (input file, for now)
 * - Ignore empty lines, Javadoc lines, or lines with opening or closing curly brackets
 * 
 * Two alternatives to analyze:
 * 1. Fewer and contigous coverage first (review current set)
 * Method invocation first
 * Inner block 
 * Outer blocks
 * Dataflow for last
 * 
 * Remember to review line count per question.
 * 
 * 2. Shallower lines first (invert the current set)
 * 
 * @author adrianoc
 *
 */
public class LineFactory{

	public LineFactory(){
		
		
	}
	
	public void generateLinetasks(){
	
		ArrayList<String> buffer = ReadWriteFile.readToBuffer(path, fileName);
		TreeMap<String, Integer> map = importQuestionLineMapping(buffer);
	}
	
	private TreeMap<String,ArrayList<String>> importQuestionLineMapping(ArrayList<String> buffer){
		
		for(String line)
		
	}
	
	
}

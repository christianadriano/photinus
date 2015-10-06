package edu.uci.ics.sdcl.firefly.report.predictive.spectra;


/**
 * Converts WorkerSessions that are by question to be by line.
 * This involves the following steps:
 * 
 * 1- Identify all lines covered by a question 
 * 2- Ignore empty lines, Javadoc lines, or lines with opening or closing curly brackets
 * 2- Creates a workerSession for each line (which involves a new WorkerSession IDs)
 * 3- Clone all microtasks from the original session to be used in the new WorkerSessions
 * 4- 
 * 
 * TODO:
 * - Bug covering IDs will have to be configurable in the PropertyManager
 * - Change FileSessionDTO to be set for either Lines or Questions
 * 
 * @author adrianoc
 *
 */
public class QuestionLineFactory{

	public QuestionLineFactory(){
		
		
	}
	
	
	
}

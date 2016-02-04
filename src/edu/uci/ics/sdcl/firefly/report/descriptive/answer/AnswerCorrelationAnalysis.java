package edu.uci.ics.sdcl.firefly.report.descriptive.answer;

import java.util.HashMap;
import java.util.Iterator;

import edu.uci.ics.sdcl.firefly.WorkerSession;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;

/**
 * Generate files to investigate correlations among: 
 * size (LOCS) x difficulty level, 
 * size x confidence
 * size x accuracy
 * duration x size
 * duration x difficulty
 * duration x confidence
 * difficult x accuracy
 * confidence x accuracy
 * question type x difficulty or confidence or accuracy
 * 
 * Also:
 *  generates files to look at how workers chose difficulty/confidence levels for their answers
 * to the same JavaMethod (WorkerSession).
 * 
 *  generate files only with first worker answers or second, or third. This goal is to measure the maturation effect
 *  not only in answer duration, but also in difficulty, confidence, and accuracy.
 * 
 * @author adrianoc
 *
 */
public class AnswerCorrelationAnalysis {
	
	HashMap<String, WorkerSession> workerSessionMap;
	
	//HashMap<String, ArrayList<WorkerSession>> 
	
	public void loadWorkerSessions(){
		FileSessionDTO dto = new FileSessionDTO();
		this.workerSessionMap = (HashMap<String, WorkerSession>) dto.getSessions();
		
		Iterator<String> iter = workerSessionMap.keySet().iterator();
		while(iter.hasNext()){
			String sessionID = iter.next();
			
			
		}
	}

	


}

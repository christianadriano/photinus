package edu.uci.ics.sdcl.firefly.report.descriptive.answer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.WorkerSession;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;
import edu.uci.ics.sdcl.firefly.report.predictive.QuestionTypeAnalysis.QuestionTypeOutput;
import edu.uci.ics.sdcl.firefly.util.BugCoveringMap;


/**
 * Do workers select same level of difficulty and confidence for questions about the same bug?
 * This class produces an analysis to answer this question.
 * 
 * @author adrianoc
 *
 */
public class WorkerPerceptionAnalysis {

	HashMap<String, WorkerSession> workerSessionMap;

	HashMap<String, ArrayList<WorkerSessionTuple>> sessionByBugMap = new HashMap<String, ArrayList<WorkerSessionTuple>>();

	/** Map used to control which worker was already sample for each bug. This is necessary to 
	 * avoid that a worker participates in more than one bug. ANOVA analysis requires this for
	 * the independence of samples assumption.
	 */
	HashMap<String, String> bugWorkerMap = new HashMap<String, String>();

	public void loadWorkerSessions(boolean independentSamples){
		FileSessionDTO dto = new FileSessionDTO();
		this.workerSessionMap = (HashMap<String, WorkerSession>) dto.getSessions();

		Iterator<String> iter = workerSessionMap.keySet().iterator();
		while(iter.hasNext()){
			String sessionID = iter.next();
			WorkerSession session = workerSessionMap.get(sessionID);

			HashMap<String,String> bugCoveringMap = BugCoveringMap.initialize();
			WorkerSessionTuple tuple = new WorkerSessionTuple(session,bugCoveringMap);
			addTuple(tuple, independentSamples);
		}
	}


	private void addTuple(WorkerSessionTuple tuple, boolean independentSamples){

		if((!independentSamples) || (!isWorkerInSamples(tuple.workerID))){
			//Either is not indepenentSamples or it is, then the worker should have not participated yet in the sampling

			ArrayList<WorkerSessionTuple> tupleList = sessionByBugMap.get(tuple.bugID);
			if(tupleList==null){
				tupleList = new ArrayList<WorkerSessionTuple>();
			}
			tupleList.add(tuple);
			sessionByBugMap.put(tuple.bugID,tupleList);
		}
	}


	private boolean isWorkerInSamples(String workerID){

		if(this.bugWorkerMap.containsKey(workerID)){
			return true;
		}
		else{
			bugWorkerMap.put(workerID, workerID); 
			return false;
		}
	}

	/** Prints the counts of answers per worker that have identical difficulty or confidence levels 
	 * output:
	 *  Header <bug1_difficulty, bug1_confidence, bug2_difficulty, bug2_confidence, ...>
	 *  Line:  <worker1-bug1_difficultyCount, worker1-bug1_confidenceCount, ....>
	 */
	public void printIdenticalCounts(){

		String destination = "C://firefly//AnswerCorrelationAnalysis//WorkerPerceptionsCount.csv";
		BufferedWriter log;

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header

			Iterator<String> iter = this.sessionByBugMap.keySet().iterator();
			while(iter.hasNext()){
				String bugID = iter.next();
				StringBuffer buffer = new StringBuffer(bugID+"_difficulty,");
				ArrayList<WorkerSessionTuple> tupleList = this.sessionByBugMap.get(bugID);
				for(WorkerSessionTuple tuple : tupleList){
					buffer.append(tuple.identicalDifficultyLevels.toString()+",");
				}
				log.write(buffer.toString()+"\n");

				buffer = new StringBuffer(bugID+"_confidence,");
				tupleList = this.sessionByBugMap.get(bugID);
				for(WorkerSessionTuple tuple : tupleList){
					buffer.append(tuple.identicalConfidenceLevels.toString()+",");
				}
				log.write(buffer.toString()+"\n");
			}

			log.close();
			System.out.println("file written at: "+destination);
		} 
		catch (Exception e) {
			System.out.println("ERROR while processing file:" + destination);
			e.printStackTrace();
		}
	}


	/** Prints the difficulty levels for the answer for each Java method
	 *  Header <bug1_difficulty, worker1-bug1_difficulty, worker2-bug1_difficulty, etc.
	 *  @param answerIndex which answer per worker
	 */
	public void printDifficultyLevels(int[] answerIndex){

		String destination = "C://firefly//AnswerCorrelationAnalysis//BugDifficultyLevels_Independent_Transposed.csv";
		BufferedWriter log;

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header

			Iterator<String> iter = this.sessionByBugMap.keySet().iterator();
			while(iter.hasNext()){
				String bugID = iter.next();

				StringBuffer buffer = new StringBuffer(bugID+"_difficulty,");
				ArrayList<WorkerSessionTuple> tupleList  = this.sessionByBugMap.get(bugID);
				for(WorkerSessionTuple tuple : tupleList){
					for(int index : answerIndex){
						if(index<tuple.difficultyList.size()){
							Integer difficulty = tuple.difficultyList.get(index);
							buffer.append(difficulty.toString()+",");
						}
					}
				}
				log.write(buffer.toString()+"\n");
			}

			log.close();
			System.out.println("file written at: "+destination);
		} 
		catch (Exception e) {
			System.out.println("ERROR while processing file:" + destination);
			e.printStackTrace();
		}
	}


	/** Prints the difficulty levels for the answer for each Java method
	 *  Header <bug1_difficulty, worker1-bug1_difficulty, worker2-bug1_difficulty, etc.
	 *  @param answerIndex which answer per worker
	 */
	public void printConfidenceLevels(int[] answerIndex){

		String destination = "C://firefly//AnswerCorrelationAnalysis//BugConfidenceLevels_All.csv";
		BufferedWriter log;

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header

			Iterator<String> iter = this.sessionByBugMap.keySet().iterator();
			while(iter.hasNext()){
				String bugID = iter.next();

				StringBuffer buffer = new StringBuffer(bugID+"_confidence,");
				ArrayList<WorkerSessionTuple> tupleList  = this.sessionByBugMap.get(bugID);
				for(WorkerSessionTuple tuple : tupleList){
					for(int index : answerIndex){
						if(index<tuple.confidenceList.size()){
							Integer confidence = tuple.confidenceList.get(index);
							buffer.append(confidence.toString()+",");
						}
					}
				}
				log.write(buffer.toString()+"\n");
			}

			log.close();
			System.out.println("file written at: "+destination);
		} 
		catch (Exception e) {
			System.out.println("ERROR while processing file:" + destination);
			e.printStackTrace();
		}
	}

	public static void main(String[] args){

		int answerIndex[] = {0,1,2}; //{0,1,2} ;

		WorkerPerceptionAnalysis analysis =  new WorkerPerceptionAnalysis();
		analysis.loadWorkerSessions(true);
		//analysis.printIdenticalCounts();
		analysis.printDifficultyLevels(answerIndex); //print only first answer for each worker.
		//analysis.printConfidenceLevels(answerIndex);
	}

}


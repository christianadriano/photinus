package edu.uci.ics.sdcl.firefly.report.descriptive.answer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Microtask;
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

	public void loadWorkerSessions(){
		FileSessionDTO dto = new FileSessionDTO();
		this.workerSessionMap = (HashMap<String, WorkerSession>) dto.getSessions();

	//	System.out.println("number of sessions:"+this.workerSessionMap.size());
	//	printAnswerCount(workerSessionMap);
	//	printAnswerCountFromMicrotasks();
	//	checkAnswersPerWorker(workerSessionMap);
		
		Iterator<String> iter = workerSessionMap.keySet().iterator();
		while(iter.hasNext()){
			String sessionID = iter.next();
			WorkerSession session = workerSessionMap.get(sessionID);

			String bugID = session.getFileName();
			HashMap<String,String> bugCoveringMap = BugCoveringMap.initialize();
			WorkerSessionTuple tuple = new WorkerSessionTuple(session,bugCoveringMap);
			addTuple(tuple);
		}
	}
	
	private void checkAnswersPerWorker(
			HashMap<String, WorkerSession> workerSessionMap2) {
		int count = 0;
		Iterator<String> iter = workerSessionMap2.keySet().iterator();
		while(iter.hasNext()){
			String sessionID = iter.next();
			WorkerSession session = workerSessionMap2.get(sessionID);
			String workerID  = session.getWorkerId();
			Vector<Microtask> taskList = session.getMicrotaskList();
			for(Microtask task: taskList){
				int answerCount = task.getAnswerCountByUserId(workerID);
				if(answerCount>1 || answerCount ==0){
					System.out.println("workeID:"+workerID+" has "+answerCount+" for task: "+task.getID().toString());
				}
				count = count + answerCount;
			}
		}
		System.out.println("Total counted answers: "+count);
	}

	private void printAnswerCount(
			HashMap<String, WorkerSession> workerSessionMap2) {
		int count=0;
		Iterator<String> iter = workerSessionMap2.keySet().iterator();
		while(iter.hasNext()){
			String sessionID = iter.next();
			WorkerSession session = workerSessionMap2.get(sessionID);
			String workerID  = session.getWorkerId();
			Vector<Microtask> taskList = session.getMicrotaskList();
			for(Microtask task: taskList){
				if(task.getAnswerByUserId(workerID)!=null);{
					count++;
				}
			}
		}
		System.out.println("Total answers: "+count);
	}
	
	private void printAnswerCountFromMicrotasks(){
		FileSessionDTO dto = new FileSessionDTO();
		HashMap<String, Microtask> microtaskMap = (HashMap<String, Microtask>) dto.getMicrotasks();
		int count=0;
		Iterator<String> iter = microtaskMap.keySet().iterator();
		while(iter.hasNext()){
			String microtaskID = iter.next();
			Microtask task = microtaskMap.get(microtaskID);
			count = count + task.getAnswerList().size();
		}
		System.out.println("Total microtask answers: "+count);		
	}

	private void addTuple(WorkerSessionTuple tuple){
		ArrayList<WorkerSessionTuple> tupleList = sessionByBugMap.get(tuple.bugID);
		if(tupleList==null){
			tupleList = new ArrayList<WorkerSessionTuple>();
		}
		tupleList.add(tuple);
		sessionByBugMap.put(tuple.bugID,tupleList);
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
	 */
	public void printDifficultyLevels(){

		String destination = "C://firefly//AnswerCorrelationAnalysis//BugDifficultyLevels.csv";
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
					for(Integer difficulty: tuple.difficultyList){
						buffer.append(difficulty.toString()+",");
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
	 */
	public void printConfidenceLevels(){

		String destination = "C://firefly//AnswerCorrelationAnalysis//BugConfidenceLevels.csv";
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
					for(Integer difficulty: tuple.confidenceList){
						buffer.append(difficulty.toString()+",");
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
		WorkerPerceptionAnalysis analysis =  new WorkerPerceptionAnalysis();
		analysis.loadWorkerSessions();
		//analysis.printIdenticalCounts();
		//analysis.printDifficultyLevels();
		analysis.printConfidenceLevels();

	}

}


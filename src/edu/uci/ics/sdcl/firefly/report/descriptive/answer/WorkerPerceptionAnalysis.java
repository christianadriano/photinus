package edu.uci.ics.sdcl.firefly.report.descriptive.answer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.WorkerSession;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;
import edu.uci.ics.sdcl.firefly.report.predictive.spectra.QuestionLOCs;
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
	HashMap<String, Integer>  IDLOCMap = new HashMap<String, Integer> (); 

	HashMap<String, ArrayList<WorkerSessionTuple>> sessionByBugMap = new HashMap<String, ArrayList<WorkerSessionTuple>>();

	ArrayList<WorkerSessionTuple> sessionTupleList =  new ArrayList<WorkerSessionTuple>();
	
	/** Map used to control which worker was already sample for each bug. This is necessary to 
	 * avoid that a worker participates in more than one bug. ANOVA analysis requires this for
	 * the independence of samples assumption.
	 */
	HashMap<String, String> bugWorkerMap = new HashMap<String, String>();

	public void loadWorkerSessions(boolean independentSamples){
		FileSessionDTO dto = new FileSessionDTO();
		this.workerSessionMap = (HashMap<String, WorkerSession>) dto.getSessions();
	
		Iterator<String> iter = workerSessionMap.keySet().iterator();
		HashMap<String,Microtask> microtaskWithLOCS_Map = loadMicrotaskLocs();
		while(iter.hasNext()){
			String sessionID = iter.next();
			WorkerSession session = workerSessionMap.get(sessionID);
			
			HashMap<String,String> bugCoveringMap = BugCoveringMap.initialize();
			WorkerSessionTuple tuple = new WorkerSessionTuple(session,bugCoveringMap,microtaskWithLOCS_Map);
			addTuple(tuple, independentSamples);
		}
	}


	private HashMap<String,Microtask>  loadMicrotaskLocs(){
		FileSessionDTO dto = new FileSessionDTO();
		QuestionLOCs lineCounter = new QuestionLOCs();
		HashMap<String, Integer>  IDLOCMap = lineCounter.loadList();
		HashMap<String,Microtask> microtaskMap =  (HashMap<String, Microtask>) dto.getMicrotasks();
		return addQuestionSizeData(microtaskMap,  IDLOCMap);
	}

	/** Sets the  lines covered attribute of each microtask
	 *  @param microtaskMap all microtasks with their respective answers
	 *  @param ID_LOC_Map the lines covered by each microtask. These data come from a file.
	 *  */
	private HashMap<String, Microtask> addQuestionSizeData(HashMap<String, Microtask> microtaskMap, 
			HashMap<String,Integer> ID_LOC_Map){

		HashMap<String, Microtask> microtaskResultMap = new HashMap<String, Microtask>();

		Iterator<String> iter = microtaskMap.keySet().iterator();
		while(iter.hasNext()){
			String taskID = iter.next();
			Microtask task = microtaskMap.get(taskID);
			Integer LOCS= ID_LOC_Map.get(taskID);
			task.setLOC_CoveredByQuestion(LOCS);
			microtaskResultMap.put(taskID, task);
		}
		return microtaskResultMap;
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
			sessionTupleList.add(tuple);
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

	//----------------------------------------------------------------------------------------
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

	//----------------------------------------------------------------------------------------
	/** Prints the difficulty levels for the answer for each Java method
	 *  Header <bug1_difficulty, worker1-bug1_difficulty, worker2-bug1_difficulty, etc.
	 *  @param answerIndex which answers per worker (first, second, third, all, etc).
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

	//----------------------------------------------------------------------------------------
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

	//----------------------------------------------------------------------------------------
	/** Prints the durations the answers for each Java method
	 *  Header <bug1_difficulty, worker1-bug1_difficulty, worker2-bug1_difficulty, etc.
	 * @param answerIndex which answers per worker (first, second, third, all, etc).
	 * @param upperOutlier 
	 * @param lowerOutlier 
	 */
	public void printDurations(int[] answerIndex, double lowerOutlier, double upperOutlier){

		String destination = "C://firefly//AnswerCorrelationAnalysis//BugDurations_Independent_1st.csv";
		BufferedWriter log;

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header

			Iterator<String> iter = this.sessionByBugMap.keySet().iterator();
			while(iter.hasNext()){
				String bugID = iter.next();

				StringBuffer buffer = new StringBuffer(bugID+"_duration,");
				ArrayList<WorkerSessionTuple> tupleList  = this.sessionByBugMap.get(bugID);
				for(WorkerSessionTuple tuple : tupleList){
					for(int index : answerIndex){
						if(index<tuple.durationList.size()){
							Double duration = tuple.durationList.get(index);
							if(duration.doubleValue()<=upperOutlier && duration.doubleValue()>=lowerOutlier)
								buffer.append(duration.toString()+",");
							else
								System.out.println("Duration: "+duration.toString());
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

	//----------------------------------------------------------------------------------------------

	/** Prints the paired data for each worker by each Java Method. This data is used to look for correlations
	 *  @param answerIndex which answers per worker (first, second, third, all, etc).
	 *  @param bugID prints each Java method data in a different file
	 *  @param questionType the type of question (METHOD_INVOCATION, IF_CONDITIONAL, VARIABLE_DECLARATION), if null, then 
	 *  print all types.
	 */
	private void printPairedConfidenceDifficultySizeDuration(int[] answerIndex, String bugID, String questionType){

		String destination = "C://firefly//AnswerCorrelationAnalysis//Paired1stAnswers//Bug_"+bugID+"_"+questionType+".csv";		
		BufferedWriter log;

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header

			Iterator<String> iter = this.sessionByBugMap.keySet().iterator();	

			//HEADER
			StringBuffer buffer = new StringBuffer(bugID+"_TaskID,");
			buffer.append(bugID+"_confidence,");
			buffer.append(bugID+"_difficulty,");
			buffer.append(bugID+"_duration,");
			buffer.append(bugID+"_size,");
			buffer.append(bugID+"_questionType,");
			log.write(buffer.toString()+"\n");
			buffer = new StringBuffer();

			ArrayList<WorkerSessionTuple> tupleList  = this.sessionByBugMap.get(bugID);
			for(WorkerSessionTuple tuple : tupleList){
				for(int index : answerIndex){

					if(index<tuple.answerList.size()){
						String tupleQuestionType = tuple.questionTypeList.get(index);

						if(questionType.isEmpty() || tupleQuestionType.matches(questionType)){
							String taskID = tuple.taskIDList.get(index);
							buffer.append(taskID+",");
							Integer confidence = tuple.confidenceList.get(index);
							buffer.append(confidence.toString()+",");
							Integer difficulty = tuple.difficultyList.get(index);
							buffer.append(difficulty.toString()+",");
							Double duration =  tuple.durationList.get(index);
							buffer.append(duration.toString()+",");
							Integer locs =  tuple.sizeList.get(index);
							buffer.append(locs.toString()+",");

							buffer.append(tupleQuestionType+",");

							log.write(buffer.toString()+"\n");
							buffer = new StringBuffer();
						}
					}
				}
			}


			log.close();
			System.out.println("file written at: "+destination);
		} 
		catch (Exception e) {
			System.out.println("ERROR while processing file:" + destination);
			e.printStackTrace();
		}
	}	

	public void printPairedForAllBugs(int[] answerIndex){
		String questionType = ""; //"IF_CONDITIONAL";//"VARIABLE_DECLARATION";// // "METHOD_INVOCATION";
		Iterator<String> iter = this.sessionByBugMap.keySet().iterator();
		while(iter.hasNext()){
			String bugID = iter.next();
			this.printPairedConfidenceDifficultySizeDuration(answerIndex, bugID, questionType);
		}

	}



	//-------------------------------------------------------------
	public static void main(String[] args){

		int answerIndex[] = {0}; //{0,1,2} ;

		WorkerPerceptionAnalysis analysis =  new WorkerPerceptionAnalysis();
		analysis.loadWorkerSessions(false);

		//analysis.printIdenticalCounts();

		//analysis.printDifficultyLevels(answerIndex); //print only first answer for each worker.

		//analysis.printConfidenceLevels(answerIndex);

		//DURATIONS removing Outliers
		//double lowerOutlier =30; //30 seconds
		//double upperOutlier =5*60; //5 minutes
		//analysis.printDurations(answerIndex,lowerOutlier,upperOutlier); //Only the first answer

		analysis.printPairedForAllBugs(answerIndex);
	}

}


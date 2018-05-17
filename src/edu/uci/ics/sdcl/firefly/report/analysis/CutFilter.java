package edu.uci.ics.sdcl.firefly.report.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.WorkerSession;
import edu.uci.ics.sdcl.firefly.report.voting.AnalysisPath;
import edu.uci.ics.sdcl.firefly.report.voting.LogData;
import edu.uci.ics.sdcl.firefly.report.voting.Result;

/** Cuts the data and apply filters
 * 
 * @author adrianoc
 *
 */
public class CutFilter {
	
	LogData data;

	public HashMap<String, Result> bugReportResultMap;
	public HashMap<String, String> discardedWorkerMap;
	public HashMap<String, String> activeWorkerMap; //Workers whose answers were included in the analysis.
	
	public HashMap<String, Microtask> scoreFilterCutMicrotaskMap; //Remove all microtasks from workers with Score below and cut to the first N answers for each microtask
	
	//Sessions that are considered spurious
	public	HashMap<String,String> batch1RejectMap = new HashMap<String,String>();

	public	HashMap<String,String> batch2RejectMap = new HashMap<String,String>();

	AnalysisPath analysisPath;
	
	public CutFilter(){
		this.analysisPath = AnalysisPath.getInstance(); 
		this.initializeLogs();
	}

	private void initializeLogs(){
		LogData data = new LogData(false, 0);
		String path = analysisPath.rawDataLogPath;
		data.processLogProduction1(path);
		data.processLogProduction2(path);

		System.out.println("Logs loaded! Totals are:");
		System.out.println("Consents: "+data.getConsents());
		System.out.println("SkillTests: "+data.getSkillTests());
		System.out.println("Surveys: "+data.getSurveys());
		System.out.println("Sessions Opened: "+data.getOpenedSessions());
		System.out.println("Sessions Closed: "+data.getClosedSessions());
		System.out.println("Answers in Map: "+data.getNumberOfMicrotasks());
		System.out.println("Microtasks in List: "+data.microtaskList.size());
		System.out.println("Workers in Map: "+data.workerMap.size());
		
		this.data = data;
	}
	

	//----------------------------------------------------------------------------------------------------------
	
	
	/**
	 * Discard workers below certain Score workers, then cut.
	 * @param cutLevel  initially 10, but could be larger.
	 * @param score
	 * @return
	 */
	HashMap<String, Microtask> filterCutMicrotasks(int cutLevel, int score ){
		
		HashMap<String, Microtask> cutMap = new HashMap<String,Microtask>();
		
		Iterator<String> iter=data.microtaskMap.keySet().iterator();
		int  totalAnswers=0;
		while(iter.hasNext()){
			String taskID = iter.next();
			Microtask task = data.microtaskMap.get(taskID);
			Microtask newTask = task.getSimpleVersion();
			newTask.setAnswerList(new Vector<Answer>());//Remove all answers
			int validMicrotaskAnswers=0;
			Vector<Answer> answerList = task.getAnswerList();
			for(int i=0;i<answerList.size();i++){
				Answer answer = answerList.get(i);					
				String workerId = answer.getWorkerId();
				Worker worker = data.workerMap.get(workerId);
				if(worker!=null && worker.getGrade()>score){
					if(newTask.getAnswerList().size()<cutLevel){//Add only answers if we didn't have reached the cut level
						newTask.addAnswer(answer);
						validMicrotaskAnswers++;
					}
				}
				//else discard answer from worker
			}
			cutMap.put(taskID, newTask);
		//	System.out.println(taskID+":"+validMicrotaskAnswers);
			totalAnswers = totalAnswers + validMicrotaskAnswers;
		}
		System.out.println("Cut at: " + cutLevel + ", answers after cut:"+totalAnswers);
		//System.out.println("----------------------------");
		return cutMap;
	}
	
	public void printActiveWorkerMap(){
		System.out.println("Active WorkerMap");
		Iterator<String> iter = this.activeWorkerMap.keySet().iterator();
		System.out.println("worker"); //| score | number of answers ");
		while(iter.hasNext()){
			String workerID = iter.next();
			Worker worker = this.data.workerMap.get(workerID);
			//discovers grade score
			Integer score = worker.getGrade();
			//discover number of answers
			String sessionID = worker.getSessionId();
			WorkerSession session = this.data.sessionMap.get(sessionID);
			Vector<Microtask> taskList = session.getMicrotaskList();
			int count =0;
			if(taskList!=null)
				 count = taskList.size();
			System.out.print("'"+workerID+"',");//,"+score+"|"+count);
		}
	}
	
	
	//----------------------------------------------------------------------------------------------------------
/**
 * 
 * @param filterCut means that must use the microtasks from scoreFilterCutMicrotaskMap which were filtered and cut.
 * @param minimumDuration
 * @param maxDuration
 * @param minimumGrade
 * @param maxGrade
 * @param numberOfICanTell
 * @param lowerNumberOfICantTell
 * @return A hashmap with two type of datastructure. One has the list of answers for each microtaskID, the other has an array with the amount of answers and amount of workers
 */
	FilterContent writeAnswerLabels_Filtered_by_DURATION_GRADE_IDK(HashMap<String, Microtask> microtaskMap, Double minimumDuration, Double maxDuration, 
			Integer minimumGrade, Integer maxGrade, Integer numberOfICanTell, Integer lowerNumberOfICantTell){

		HashMap<String, ArrayList<String>> answerMap = new HashMap<String, ArrayList<String>>();

		//System.out.println("Size of microtask Map: "+ data.microtaskMap.size());
		Iterator<String> iter = microtaskMap.keySet().iterator();
		
		discardedWorkerMap = new HashMap<String, String>();//just to analyze who are the workers in terms of skill test score
		activeWorkerMap = new HashMap<String, String>();
		int answerCount=0;
		ArrayList<String> contentList;
		//System.out.println("Worker ID| option | duration");
		while(iter.hasNext()){
			contentList = new ArrayList<String>();
			String id = iter.next();
			Microtask task = microtaskMap.get(id);
			Vector<Answer> answerList = task.getAnswerList();
			for(int i=0;i<answerList.size();i++){
				Answer answer = answerList.get(i);					
				String workerId = answer.getWorkerId();
				Integer count = data.workerICantTellMap.get(workerId);
				Worker worker = data.workerMap.get(workerId);
				Integer grade = worker.getGrade();	
				Double duration = new Double(answer.getElapsedTime());
				if(count!=null && count.intValue()<numberOfICanTell && count.intValue()>lowerNumberOfICantTell && 
						grade!=null && grade>=minimumGrade && grade<=maxGrade && 
						duration>=minimumDuration&& duration<=maxDuration){
					//System.out.println(workerId+"|"+answer.getOption()+"|"+duration.toString());
					answerCount++; 
					
					activeWorkerMap.put(workerId, workerId);
					contentList.add(answer.getOption());
				}
				else{
					//System.out.println("Worker "+ workerId+" discarded, I Cannot Tell count= "+count+", grade="+grade+", duration="+duration);
					this.discardedWorkerMap.put(workerId,workerId);
				}
			}
			answerMap.put(task.getID().toString(),contentList);
		}
		System.out.println("valid answers=" + answerCount+ ", active workers: "+activeWorkerMap.size());
		
		FilterContent filterContent = new FilterContent(minimumDuration,minimumGrade, numberOfICanTell);
		
		filterContent.answerMap = answerMap;
		filterContent.activeWorkers = activeWorkerMap.size();
		filterContent.validAnswers = answerCount;
		return filterContent;
	}
	
	//-----------------------------------------------------------------------------------------------------------------

	
	
}

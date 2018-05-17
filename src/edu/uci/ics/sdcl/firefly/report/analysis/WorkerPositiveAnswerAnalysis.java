package edu.uci.ics.sdcl.firefly.report.analysis;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.report.voting.LogData;

/**
 * Compute the positive answers from workers how accurate each worker is.
 * For that I would have to compute TP, TN, FP, FN for each worker.
 * @author adrianoc
 *
 */
public class WorkerPositiveAnswerAnalysis {

	static String samsungPath = "C:\\Users\\adrianoc\\Dropbox (PE-C)\\3.Research\\2.Fall2014-Experiments\\";
	static String dellPath = "C:\\Users\\Christian Adriano\\Dropbox (PE-C)\\3.Research\\2.Fall2014-Experiments\\";
	static String currentPath = samsungPath;

	/** 92 workers who produced the best data point of 57% precision, 71% recall from cutting at 17 answers/questions */
	private String[] filteredWorkers = {"2-490","1-1280","1-540","1-1051","1-2183","1-544","1-700","2-1631","1-1758",
			"1-1755","2-1231","2-1279","1-743","2-302","1-780","1-692","1-1819","2-604","1-742","2-445","1-748","2-1543",
			"1-434","1-2096","1-1644","1-1312","1-1814","2-400","2-1024","1-338","2-1168","2-204","1-656","1-1567","1-1159",
			"1-2155","1-132","1-1612","1-705","2-1268","1-232","1-1474","2-1316","1-1025","1-235","2-2266","1-1026","2-298",
			"1-1850","1-91","1-2160","1-349","2-1517","1-389","2-567","2-1709","2-38","1-579","2-127","2-2231","1-931","1-764",
			"1-763","1-451","1-1589","2-1995","1-297","2-1772","2-702","2-701","2-1567",
			"1-399","1-2127","2-1737","1-635","1-2019","1-87","1-1725","1-85","2-220","2-1532","1-490","1-1634","1-259","1-1140",
			"1-1208","1-1101","1-1293","1-1699","1-110","1-1967","1-1300"};

	private HashMap<String, Worker> filteredWorkersMap ;

	/** Auxiliary class */
	public class WorkerEfficacy{
		Integer TP=0;
		Integer FP=0;
		Integer TN=0;
		Integer FN=0;
		Integer IDontKnow=0;

		Integer BugPointingQuestions=0;
		Integer NotBugPointingQuestions=0;
		Integer positiveAnswers=0; //yes and probably yes
		Integer totalAnswers=0; //from one worker
	}
	
	LogData data;

	public HashMap<String,WorkerEfficacy> workerEfficacyMap; //map workers and the TP (true positive), FP, TN, FN

	public WorkerPositiveAnswerAnalysis(LogData data){
		this.data = data;

		this.workerEfficacyMap = new HashMap<String,WorkerEfficacy>();
		this.filteredWorkersMap = new HashMap<String,Worker>();
		for(String workerId: this.filteredWorkers){
			Worker worker = data.workerMap.get(workerId);
			this.filteredWorkersMap.put(workerId,worker);
		}

	}


	private static WorkerPositiveAnswerAnalysis initializeLogs(){

		LogData data = new LogData(false, 0);

		String path = currentPath;
		path = path + "\\RawDataLogs\\";

		data.processLogProduction1(path);
		data.processLogProduction2(path);

		System.out.println("Logs loaded! Totals are:");
		System.out.println("Consents: "+data.getConsents());
		System.out.println("SkillTests: "+data.getSkillTests());
		System.out.println("Surveys: "+data.getSurveys());
		System.out.println("Sessions Opened: "+data.getOpenedSessions());
		System.out.println("Sessions Closed: "+data.getClosedSessions());
		System.out.println("Answers in Map: "+data.getNumberOfMicrotasks());
		System.out.println("Workers in Map: "+data.workerMap.size());

		WorkerPositiveAnswerAnalysis analysis = new WorkerPositiveAnswerAnalysis(data);
		return analysis;
	}


	/** This method ignores answers that were not included in the filter */
	private void calculate_TP_FP_TN_FN_FilteredWorkers(HashMap<String, Microtask> microtaskMap){

		Iterator <String> iter = microtaskMap.keySet().iterator();

		while(iter.hasNext()){
			String taskId = iter.next();
			Microtask task = microtaskMap.get(taskId);

			Vector<Answer> answerList = task.getAnswerList();
			if(answerList!=null){
				for(Answer answer: answerList){
					String workerId = answer.getWorkerId();
					if(this.filteredWorkersMap.containsKey(workerId)){
						if(data.yesMap.containsKey(task.getID().toString())){
							incrementTPFN(workerId,answer);
						}
						else{
							incrementFPTN(workerId,answer);
						}
						incrementWorkerYes_ProbablyYesCount(answer,workerId);
						incrementWorkerAnswerCount(workerId);
					}
				}
			}
		}
	}

	/** This method ignores answers that were not included in the filter */
	private void calculate_TP_FP_TN_FN_ALLWorkers(HashMap<String, Microtask> microtaskMap){

		Iterator <String> iter = microtaskMap.keySet().iterator();

		while(iter.hasNext()){
			String taskId = iter.next();
			Microtask task = microtaskMap.get(taskId);

			Vector<Answer> answerList = task.getAnswerList();
			if(answerList!=null){
				for(Answer answer: answerList){
					String workerId = answer.getWorkerId();

					if(data.yesMap.containsKey(task.getID().toString())){
						incrementTPFN(workerId,answer);
					}
					else{
						incrementFPTN(workerId,answer);
					}
					incrementWorkerYes_ProbablyYesCount(answer,workerId);
					incrementWorkerAnswerCount(workerId);

				}
			}
		}
	}	

	private void incrementWorkerYes_ProbablyYesCount(Answer answer, String workerId) {
		String option = answer.getOption();
		if(option.matches(Answer.YES)){

			WorkerEfficacy efficacy = this.workerEfficacyMap.get(workerId);
			if(efficacy==null){
				efficacy = new WorkerEfficacy();
				efficacy.positiveAnswers = new Integer(1);
			}
			else
				efficacy.positiveAnswers++;
			this.workerEfficacyMap.put(workerId, efficacy);	
		}
	}

	private void incrementWorkerAnswerCount(String workerId) {
		WorkerEfficacy efficacy = this.workerEfficacyMap.get(workerId);
		if(efficacy==null){
			efficacy = new WorkerEfficacy();
			efficacy.totalAnswers = new Integer(1);
		}
		else
			efficacy.totalAnswers++;
		this.workerEfficacyMap.put(workerId, efficacy);
	}


	private void incrementTPFN(String workerId,Answer answer){
		WorkerEfficacy efficacy = this.workerEfficacyMap.get(workerId);

		if(efficacy==null)
			efficacy = new WorkerEfficacy();

		efficacy.BugPointingQuestions++;
		if(answer.getOption().matches(Answer.YES))
			efficacy.TP++;
		else
			if(answer.getOption().matches(Answer.NO))
				efficacy.FN++;
			else
				efficacy.IDontKnow++;

		this.workerEfficacyMap.put(workerId, efficacy);
	}


	private void incrementFPTN(String workerId,Answer answer){
		WorkerEfficacy efficacy = this.workerEfficacyMap.get(workerId);

		if(efficacy==null)
			efficacy = new WorkerEfficacy();

		efficacy.NotBugPointingQuestions++;
		if(answer.getOption().matches(Answer.YES))
			efficacy.FP++;
		else
			if(answer.getOption().matches(Answer.NO))
				efficacy.TN++;
			else
				efficacy.IDontKnow++;

		this.workerEfficacyMap.put(workerId, efficacy);
	}


	public void printFilteredWorkers_Efficacy(){
		System.out.println("WorkerId|TP|FP|TN|FN|I Can't Tell|Yes + Prob Yes count | Total Answers | Skill Test Score");

		for(String workerId: this.filteredWorkers){
			Worker worker = data.workerMap.get(workerId);
			WorkerEfficacy efficacy = this.workerEfficacyMap.get(workerId);
			if(efficacy!=null)
				System.out.println(workerId+"|"+efficacy.TP +"|"+efficacy.FP+"|"+efficacy.TN+"|"+efficacy.FN+"|"+efficacy.IDontKnow+
						"|"+efficacy.positiveAnswers+"|"+efficacy.totalAnswers+"|"+worker.getGrade());
		}
	}


	public void printAllWorkers_Efficacy(){
		Iterator<String> iter = data.workerMap.keySet().iterator();
		System.out.println("WorkerId|TP|FP|TN|FN|I Can't Tell|Yes + Prob Yes count | Total Answers | Skill Test Score");

		while(iter.hasNext()){
			String workerId = iter.next();
			Worker worker = data.workerMap.get(workerId);
			WorkerEfficacy efficacy = this.workerEfficacyMap.get(workerId);
			if(efficacy!=null)
				System.out.println(workerId+"|"+efficacy.TP +"|"+efficacy.FP+"|"+efficacy.TN+"|"+efficacy.FN+"|"+efficacy.IDontKnow+
						"|"+efficacy.positiveAnswers+"|"+efficacy.totalAnswers+"|"+worker.getGrade());
		}
	}

	//----------------------------------------------------------------------------------------------------------

	public static void main(String[] args){

		WorkerPositiveAnswerAnalysis analysis = initializeLogs();

		CutFilter cutFilter = new CutFilter();
		HashMap<String, Microtask> cutMap = cutFilter.filterCutMicrotasks(204,1);

		String path =  currentPath;
		path = path +"\\DataAnalysis\\BaseDataInTime\\workerAnalysis\\";

		analysis.calculate_TP_FP_TN_FN_FilteredWorkers(cutMap);
		analysis.printFilteredWorkers_Efficacy();

		//analysis.calculate_TP_FP_TN_FN_ALLWorkers(cutMap);
		//analysis.printAllWorkers_Efficacy();


	}


}

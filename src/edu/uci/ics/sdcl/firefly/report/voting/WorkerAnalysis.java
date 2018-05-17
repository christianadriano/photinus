package edu.uci.ics.sdcl.firefly.report.voting;

import java.awt.Point;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.QuestionType;
import edu.uci.ics.sdcl.firefly.QuestionTypeFactory;
import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.WorkerSession;
import edu.uci.ics.sdcl.firefly.report.voting.LogAnalysis.Counter;

/**
 * This analysis was to compute the answers for 1 worker, 2 workers, 3 workers, ....
 * I have however done that in Excel by generation for each question the list of answers order in time.
 * 
 * My plan for this class is to look at how accurate each worker is.
 * For that I would have to compute TP, TN, FP, FN for each worker.
 * 
 * @author Christian Adriano
 *
 */
public class WorkerAnalysis {

	static String samsungPath = "C:\\Users\\adrianoc\\Dropbox (PE-C)\\3.Research\\2.Fall2014-Experiments\\";
	static String dellPath = "C:\\Users\\Christian Adriano\\Dropbox (PE-C)\\3.Research\\2.Fall2014-Experiments\\";
	static String currentPath = samsungPath;

	/**Workers that produced the best data set 55% precision, with 71% recall)*/
	public String[] cutAfterFilteredWorkers = { "2-490","1-1958","2-2011","1-700","1-1758","1-1755","2-302","2-1279",
			"1-780","1-692","2-604","2-1543","1-2096","2-1271","1-1644","2-2420","2-400","1-338","2-1407","1-1845","2-1168",
			"2-204","1-1159","1-2155","1-132","2-1268","1-705","1-1025","1-1026","2-298","1-1850","1-349","1-2160","2-1517",
			"2-567","1-2167","1-579","2-2231","1-1669","1-764","1-763","1-1685","1-451","2-1899","2-702","2-701","2-1737",
			"2-1239","1-635","1-1725","2-1733","2-220","2-1532","1-1144","1-1539","1-259","1-1140","2-2240","1-1208",
			"1-1293","1-1699","1-1280","1-540","1-1051","1-2183","2-1631","1-544","1-1913","2-1231","1-743","1-1819",
			"2-934","1-742","2-445","1-748","1-434","1-1312","2-1588","1-1814","2-1024","1-1991","1-1787","1-656","1-1567",
			"1-1901","1-1612","1-232","1-1474","2-252","2-1316","1-235","2-2266","2-2263","1-1574","1-1792","1-91","2-856",
			"2-2484","2-2482","1-389","2-1709","2-38","1-1463","2-127","2-1754","1-931","2-1977","2-2085","2-1995",
			"1-1589","2-1343","1-297","2-1772","2-1567","1-399","1-2127","1-2019","1-87","1-85","1-2018","1-2133",
			"2-2077","1-490","1-1634","1-1101","2-1986","1-110","1-1300","1-1967"};
	
	/** 92 workers who produced the best data point of 57% precision, 71% recall from cutting at 17 answers/questions */
	public String[] filteredWorkers = {"1-1958","2-490","2-2011","1-700","1-1758","1-1755","1-780","2-302","2-1279","1-692","2-604",
			"1-2096","2-1543","2-1271","1-1644","2-400","1-338","2-1407","1-1845","2-1168","2-204","1-1159","1-2155","1-132","1-705",
			"2-1268","1-1025","1-1026","2-298","1-1850","1-349","1-2160","2-1517","2-567","1-2167","1-579","2-2231","1-1669","1-764",
			"1-763","1-1685","1-451","2-1899","2-702","2-701","2-1737","2-1239","1-635","1-1725","2-1733","2-220","2-1532","1-1144",
			"1-1539","1-1140","1-259","2-2240","1-1208","1-1293","1-1699","1-1280","1-540","1-1051","1-2183","1-1913","1-544","2-1631",
			"2-1231","1-743","2-934","1-1819","1-742","1-748","2-445","1-434","1-1312","1-1814","2-1588","2-1024","1-1991","1-1787","1-1567",
			"1-656","1-1901","1-1612","1-232","1-1474","2-252","2-1316","1-235","2-2266","2-2263","1-1574","1-1792","1-91","2-856","2-2482",
			"1-389","2-1709","2-38","1-1463","2-127","2-1754","1-931","2-1977","2-2085",
		"2-1995","1-1589","2-1772","2-1343","1-297","2-1567","1-399","1-2127","1-2019","1-87","1-85","1-2018","1-2133","2-2077","1-490",
		"1-1634","1-1101","1-1300","1-110","1-1967","2-1986"};



	public class WorkerEfficacy{

		Integer TP=0;
		Integer FP=0;
		Integer TN=0;
		Integer FN=0;
		Integer IDontKnow=0;

		Integer BugPointingQuestions=0;
		Integer NotBugPointingQuestions=0;
	}

	private NumberFormat formatter = new DecimalFormat("#0.00"); 

	LogData data;

	public HashMap<String, Integer> originalWorkersMap; //map of all workers to their number of answers, no filter applied
	public HashMap<String,WorkerEfficacy> workerEfficacyMap; //map workers and the TP (true positive), FP, TN, FN

	public HashMap<String,Counter> counterMap;

	public HashMap<String, Integer> workerAnswerCountMap;
	public HashMap<String, Integer> workerYesCountMap;
	public HashMap<String, Integer> workerProbablyYesCountMap;
	public HashMap<String, Integer> workerCorrectAnswerMap;
	public HashMap<String, Double> workerPercentCorrectAnswerMap;
	public HashMap<String, Integer> yesCountForCorrectLevelMap;

	public HashMap<String, Integer> workerCorrectAnswersBugPointQuestionsMap; //Only count correct answers for bug pointing questions, ignore other questions.
	public HashMap<String, Double> workerPercentCorrectAnswerBugPointQuestionsMap;
	public HashMap<String, Integer> workerAnswerBugPointingQuestionCountMap;



	public HashMap<String, Integer> workerWrongAnswerMap;
	public HashMap<String, Integer> levelMap;
	public HashMap<String, Integer> percentLevelMap;


	public WorkerAnalysis(LogData data){
		this.data = data;
		counterMap = new HashMap<String,Counter>(); 
		this.originalWorkersMap = new HashMap<String, Integer>();
		this.workerEfficacyMap = new HashMap<String,WorkerEfficacy>();
	}


	private static WorkerAnalysis initializeLogs(){

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

		WorkerAnalysis analysis = new WorkerAnalysis(data);
		return analysis;
	}


	/** 
	 * MAIN METHOD FOR FIXED LIST OF WORKERS
	 * 
	 * Produces a data structure of the following:
	 * Map<String key, Map<String workerId, Worker workerObj> >  Key is the level of correct answers (from zero to 10) and
	 */
	private void computeCorrectAnswersPerWorker(String[] workerIdList){
		workerAnswerCountMap = new HashMap<String,Integer>();
		workerYesCountMap = new HashMap<String,Integer>();
		workerProbablyYesCountMap = new HashMap<String,Integer>();
		workerCorrectAnswerMap = new HashMap<String,Integer>();
		workerWrongAnswerMap = new  HashMap<String,Integer>();
		workerCorrectAnswersBugPointQuestionsMap = new  HashMap<String,Integer>();
		workerAnswerBugPointingQuestionCountMap = new HashMap<String,Integer>();

		for(String workerId : workerIdList){
			Worker worker = data.workerMap.get(workerId);
			String sessionId = worker.getSessionId();
			if(sessionId!=null){
				WorkerSession session = data.sessionMap.get(sessionId);
				Vector<Microtask> taskList = session.getMicrotaskList();
				if(taskList!=null){
					for(Microtask task: taskList){
						Vector<Answer>  answerList = task.getAnswerList();
						for(Answer answer: answerList){
							if(answer.getWorkerId().matches(workerId)){
								incrementAnswer(workerId);
								if(answer.getOption().matches(Answer.YES)){
									incrementYesAnswers(workerId);
								}//Check if answer is correct and increment
								checkIncrementCorrectAnswer(task.getID().toString(),answer.getOption(),workerId);
							}
							//else ignores.
						}
					}
				}
			}
		}
		//else do nothing, because session is empty.
		//	computePercentCorrectWorkerCountMap();
		//computeLevelCorrectWorkers();
		//computePercentageCorrectAnswersPerWorker();
		//computePercentCorrectBugPointingWorkerCountMap();
	}

	/** 
	 * MAIN METHOD FOR WORKERS AND THEIR ANSWERS SELECTED BY FILTER
	 * 
	 * Produces a data structure of the following:
	 * Map<String key, Map<String workerId, Worker workerObj> >  Key is the level of correct answers (from zero to 10) and
	 */
	private void computeCorrectAnswersPerWorker(Double duration, Integer score, Integer idkCount, String questionTypeStr){

		workerAnswerCountMap = new HashMap<String,Integer>();
		workerYesCountMap = new HashMap<String,Integer>();
		workerProbablyYesCountMap = new HashMap<String,Integer>();
		workerCorrectAnswerMap = new HashMap<String,Integer>();
		workerWrongAnswerMap = new  HashMap<String,Integer>();
		workerCorrectAnswersBugPointQuestionsMap = new  HashMap<String,Integer>();
		workerAnswerBugPointingQuestionCountMap = new HashMap<String,Integer>();

		QuestionTypeFactory questionTypeFactory = new QuestionTypeFactory();
		questionTypeFactory.generateQuestionTypes();

		Iterator<String> iter = data.workerMap.keySet().iterator();

		while(iter.hasNext()){
			String workerId = iter.next();
			Worker worker = data.workerMap.get(workerId);
			String sessionId = worker.getSessionId();
			if(sessionId!=null){
				WorkerSession session = data.sessionMap.get(sessionId);
				Vector<Microtask> taskList = session.getMicrotaskList();
				if(taskList!=null){
					for(Microtask task: taskList){
						if(isValidTaskType(task.getID(), questionTypeStr,questionTypeFactory.getQuestionTypeMap())){
							Vector<Answer>  answerList = task.getAnswerList();
							for(Answer answer: answerList){
								if(isAnswerValid(answer, duration, score, idkCount)){//Ignore answers that are out the range (duration, skill test score, and number of I can't tell answers)
									if(answer.getWorkerId().matches(workerId)){
										incrementAnswer(workerId);
										if(answer.getOption().matches(Answer.YES)){
											incrementYesAnswers(workerId);
										}//Check if answer is correct and increment
										checkIncrementCorrectAnswer(task.getID().toString(),answer.getOption(),workerId);
									}
									//else ignores.
								}
							}
						}
					}
				}
			}
		}
		//else do nothing, because session is empty.
		//computePercentCorrectWorkerCountMap();
		//computeLevelCorrectWorkers();
		//computePercentageCorrectAnswersPerWorker();
		//computePercentCorrectBugPointingWorkerCountMap();
	}

	private void computeTotalAnswersWorker(){
		Iterator<String> iter = data.workerMap.keySet().iterator();

		while(iter.hasNext()){
			String workerId = iter.next();
			Worker worker = data.workerMap.get(workerId);
			String sessionId = worker.getSessionId();
			if(sessionId!=null){
				WorkerSession session = data.sessionMap.get(sessionId);
				Vector<Microtask> taskList = session.getMicrotaskList();
				for(Microtask task: taskList){					 
					if(task!=null){
						Answer answer = task.getAnswerByUserId(workerId);
						if(answer!=null){
							//Increment answer count for worker.
							Integer answerCount = this.originalWorkersMap.get(workerId);

							if(answerCount==null) 
								answerCount = new Integer(1);
							else
								answerCount++;

							this.originalWorkersMap.put(workerId, answerCount);
						}
					}
				}
			}
		}
	}

	private boolean isValidTaskType(Integer id, String desiredQuestionTypeStr, HashMap<String,HashMap<Integer,QuestionType>> questionTypeMap){

		HashMap<Integer,QuestionType> idQuestionTypeMap = questionTypeMap.get(desiredQuestionTypeStr);
		if(idQuestionTypeMap!=null){
			if(idQuestionTypeMap.containsKey(id))
				return true;
			else 
				return false;
		}
		else 
			return false;
	}

	private void computeTP_FP_TN_FN_PerWorker(){
		Iterator<String> iter = data.workerMap.keySet().iterator();

		while(iter.hasNext()){
			String workerId = iter.next();
			Worker worker = data.workerMap.get(workerId);
			String sessionId = worker.getSessionId();
			if(sessionId!=null){
				WorkerSession session = data.sessionMap.get(sessionId);
				Vector<Microtask> taskList = session.getMicrotaskList();
				for(Microtask task: taskList){
					if(task!=null){
						if(data.yesMap.containsKey(task.getID().toString())){
							incrementTPFN(workerId,task.getAnswerByUserId(workerId));
						}
						else{
							incrementFPTN(workerId,task.getAnswerByUserId(workerId));
						}
					}
				}
			}
		}
	}	
	
	/** This method ignores answers that were not included in the filter */
	private void computeTP_FP_TN_FN_PerFilteredWorker(){

		QuestionTypeFactory questionTypeFactory = new QuestionTypeFactory();
		questionTypeFactory.generateQuestionTypes();

		for(String workerId: this.filteredWorkers){
			Worker worker = data.workerMap.get(workerId);
			String sessionId = worker.getSessionId();
			if(sessionId!=null){
				WorkerSession session = data.sessionMap.get(sessionId);
				Vector<Microtask> taskList = session.getMicrotaskList();
				for(Microtask task: taskList){
					if(task!=null){
						Answer answer = task.getAnswerByUserId(workerId);
						if(data.yesMap.containsKey(task.getID().toString())){
							incrementTPFN(workerId,answer);
						}
						else{
							incrementFPTN(workerId,answer);
						}
					}
				}
			}
		}
	}	

	/** This method ignores answers that were not included in the filter */
	private void computeTP_FP_TN_FN_PerFilteredWorker(Double duration, Integer score, Integer idkCount, String questionTypeStr){

		QuestionTypeFactory questionTypeFactory = new QuestionTypeFactory();
		questionTypeFactory.generateQuestionTypes();

		for(String workerId: this.filteredWorkers){
			Worker worker = data.workerMap.get(workerId);
			String sessionId = worker.getSessionId();
			if(sessionId!=null){
				WorkerSession session = data.sessionMap.get(sessionId);
				Vector<Microtask> taskList = session.getMicrotaskList();
				for(Microtask task: taskList){
					if((task!=null) && 	(isValidTaskType(task.getID(), questionTypeStr,questionTypeFactory.getQuestionTypeMap()))){
						Answer answer = task.getAnswerByUserId(workerId);
						if(isAnswerValid(answer, duration, score, idkCount)){//Ignore answers that are out the range (duration, skill test score, and number of I can't tell answers)
							if(data.yesMap.containsKey(task.getID().toString())){
								incrementTPFN(workerId,answer);
							}
							else{
								incrementFPTN(workerId,answer);
							}
						}
					}
				}
			}
		}
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


	private boolean isAnswerValid(Answer answer, Double minimumDuration, Integer minimumGrade, Integer numberOfICanTell){
		String workerId = answer.getWorkerId();
		Integer count = data.workerICantTellMap.get(workerId);
		Worker worker = data.workerMap.get(workerId);
		Integer grade = worker.getGrade();	
		Double duration = new Double(answer.getElapsedTime());
		return (count!=null && count.intValue()<numberOfICanTell && grade!=null && grade>=minimumGrade && duration>=minimumDuration);	
	}

	private void checkIncrementCorrectAnswer(String microtaskId, String option, String workerId) {
		String value = data.yesMap.get(microtaskId);
		if(value!=null){ //means that microtaskId is yes question.
			if(option.matches(Answer.YES)){
				incrementCorrectAnswer(workerId);
				incrementBugPointingCorrectAnswers(workerId);
			}
			else
				incrementWrongAnswer(workerId);
			this.incrementBugPointingAnswerCount(workerId);
		}
		else{
			if(option.matches(Answer.NO)){
				incrementCorrectAnswer(workerId);
			}
			else
				incrementWrongAnswer(workerId);
		}

	}

	private void incrementWrongAnswer(String workerId) {
		Integer answerCount = this.workerWrongAnswerMap.get(workerId);
		if(answerCount==null)
			answerCount = new Integer(1);
		else
			answerCount++;
		this.workerWrongAnswerMap.put(workerId, answerCount);	
	}


	private void incrementCorrectAnswer(String workerId) {
		Integer answerCount = this.workerCorrectAnswerMap.get(workerId);
		if(answerCount==null)
			answerCount = new Integer(1);
		else
			answerCount++;
		this.workerCorrectAnswerMap.put(workerId, answerCount);	
	}

	private void incrementBugPointingCorrectAnswers(String workerId) {
		Integer answerCount = this.workerCorrectAnswersBugPointQuestionsMap.get(workerId);
		if(answerCount==null)
			answerCount = new Integer(1);
		else
			answerCount++;
		this.workerCorrectAnswersBugPointQuestionsMap.put(workerId, answerCount);	
	}

	private void incrementProbablyYesAnswers(String workerId) {
		Integer answerCount = this.workerProbablyYesCountMap.get(workerId);
		if(answerCount==null)
			answerCount = new Integer(1);
		else
			answerCount++;
		this.workerProbablyYesCountMap.put(workerId, answerCount);		
	}

	private void incrementYesAnswers(String workerId) {
		Integer answerCount = this.workerYesCountMap.get(workerId);
		if(answerCount==null)
			answerCount = new Integer(1);
		else
			answerCount++;
		this.workerYesCountMap.put(workerId, answerCount);
	}

	/**
	 * Just increments the counter for number of answers given by a worker
	 * @param workerId
	 */
	private void incrementAnswer(String workerId) {
		Integer answerCount = this.workerAnswerCountMap.get(workerId);
		if(answerCount==null)
			answerCount = new Integer(1);
		else
			answerCount++;
		this.workerAnswerCountMap.put(workerId, answerCount);
	}

	/**
	 * Just increments the counter for number of answers given by a worker
	 * @param workerId
	 */
	private void incrementBugPointingAnswerCount(String workerId) {
		Integer answerCount = this.workerAnswerBugPointingQuestionCountMap.get(workerId);
		if(answerCount==null)
			answerCount = new Integer(1);
		else
			answerCount++;
		this.workerAnswerBugPointingQuestionCountMap.put(workerId, answerCount);
	}


	private void computePercentCorrectWorkerCountMap(){
		workerPercentCorrectAnswerMap = new HashMap<String,Double>();
		Iterator<String> iter = this.workerAnswerCountMap.keySet().iterator();

		while(iter.hasNext()){
			String workerId = iter.next();
			Integer answerCount = this.workerAnswerCountMap.get(workerId);
			Integer correctCount = this.workerCorrectAnswerMap.get(workerId);

			//System.out.println(workerId+"|"+answerCount+"|"+correctCount);
			if(correctCount==null) correctCount = 0;
			workerPercentCorrectAnswerMap.put(workerId, new Double(100*correctCount.doubleValue()/answerCount.doubleValue()));

		}
	}

	private void computePercentCorrectBugPointingWorkerCountMap(){
		workerPercentCorrectAnswerBugPointQuestionsMap = new HashMap<String,Double>();
		Iterator<String> iter = this.workerAnswerCountMap.keySet().iterator();

		while(iter.hasNext()){
			String workerId = iter.next();
			Integer answerCount = this.workerAnswerBugPointingQuestionCountMap.get(workerId);
			Integer correctCount = this.workerCorrectAnswersBugPointQuestionsMap.get(workerId);

			//System.out.println(workerId+"|"+answerCount+"|"+correctCount);
			if(correctCount==null) correctCount = 0;
			Double percent;
			if(answerCount!=null)
				percent = new Double(100*correctCount.doubleValue()/answerCount.doubleValue());
			else
				percent = 0.0;
			workerPercentCorrectAnswerBugPointQuestionsMap.put(workerId, percent );			

		}
	}


	/** 
	 * Computes the percentages of answers correct. The percentages follow in buckets of 10% size, 
	 * starting with zero and ending with 100%).
	 */
	private void computePercentageCorrectAnswersPerWorker(){
		this.percentLevelMap = new HashMap<String, Integer>();

		for(int level=0;level<110;level=level+10){
			percentLevelMap.put(new Integer(level).toString(), 0);
		}

		Iterator<String> iter = this.workerPercentCorrectAnswerMap.keySet().iterator();

		while(iter.hasNext()){
			String workerId = iter.next();
			Double workerCountPercent = this.workerPercentCorrectAnswerMap.get(workerId);

			double num = workerCountPercent.doubleValue()/10;
			int iPart = (int) num;
			Integer integerPart = new Integer(iPart*10);

			String workerCountStr = integerPart.toString();
			Integer count = percentLevelMap.get(workerCountStr);

			if(count!=null)
				count++;
			else
				count=1;

			percentLevelMap.put(workerCountStr,count);			
		}
	}


	private void computeLevelCorrectWorkers(){

		levelMap = new HashMap<String,Integer>();
		for(int level=0;level<11;level++){
			levelMap.put(new Integer(level).toString(), 0);
		}

		Iterator<String> iter = this.workerCorrectAnswerMap.keySet().iterator();

		while(iter.hasNext()){
			String workerId = iter.next();
			Integer workerCount = this.workerCorrectAnswerMap.get(workerId);
			String workerCountStr = workerCount.toString();
			Integer count = levelMap.get(workerCountStr);
			count++;
			levelMap.put(workerCountStr,count);			
		}
	}

	public void printCorrectAnswer_WorkerCount(boolean filtered){

		System.out.println("Workers who answered questions: "+ this.workerAnswerCountMap.size());
		System.out.println("Worker | Number of Correct answers");

		if(filtered){
			for(String workerId: this.filteredWorkers){
				Integer correctCount = this.workerCorrectAnswerMap.get(workerId);
				System.out.println(workerId+"|"+correctCount);
			}			
		}
		else{
			Iterator<String> iter = this.workerCorrectAnswerMap.keySet().iterator();

			while(iter.hasNext()){
				String workerId = iter.next();
				Integer correctCount = this.workerCorrectAnswerMap.get(workerId);
				System.out.println(workerId+"|"+correctCount);
			}
		}
	}


	public void printPercentCorrectAnswer_WorkerCount(){

		System.out.println("Workers who answered questions: "+ this.workerAnswerCountMap.size());

		Iterator<String> iter = this.percentLevelMap.keySet().iterator();

		while(iter.hasNext()){
			String level = iter.next();
			Integer workerCount = this.percentLevelMap.get(level);
			System.out.println(workerCount);//+"|"+ level);
		}


	}


	public void printWorkerCountPerLevelCorrectAnswers(){

		System.out.println("Workers who answered questions: "+ this.workerAnswerCountMap.size());

		Iterator<String> iter = this.levelMap.keySet().iterator();

		while(iter.hasNext()){
			String level = iter.next();
			Integer workerCount = this.levelMap.get(level);
			System.out.println(workerCount); //level);//"|"+
		}
	}


	public void printWorkerPercentCorrectAnswers(){

		System.out.println("Workers who answered questions: "+ this.workerAnswerCountMap.size());

		Iterator<String> iter = this.workerPercentCorrectAnswerMap.keySet().iterator();

		while(iter.hasNext()){
			String workerId = iter.next();
			Double percent = this.workerPercentCorrectAnswerMap.get(workerId);
			System.out.println(workerId+"|"+percent); //level);//"|"+
		}
	}

	public void printWorkerAnswerCount(){

		System.out.println("Workers who answered questions: "+ this.workerAnswerCountMap.size());
		System.out.println("WorkerId | Filtered Answer count | Total Answer count");
		Iterator<String> iter = this.workerAnswerCountMap.keySet().iterator();

		while(iter.hasNext()){
			String workerId = iter.next();
			Integer answerCount = this.workerAnswerCountMap.get(workerId);
			Integer originalCount = this.originalWorkersMap.get(workerId);
			System.out.println(workerId+"|"+answerCount);//+"|"+originalCount); //level);//"|"+
		}
	}


	public void printWorkerPercentCorrectAnswersBugPointingQuestions(){

		System.out.println("Workers who answered questions: "+ this.workerAnswerCountMap.size());

		Iterator<String> iter = this.workerPercentCorrectAnswerBugPointQuestionsMap.keySet().iterator();

		while(iter.hasNext()){
			String workerId = iter.next();
			Double percent = this.workerPercentCorrectAnswerBugPointQuestionsMap.get(workerId);
			System.out.println(workerId+"|"+percent); //level);//"|"+
		}
	}

	public void printWorkerYesCount (){

		System.out.println("Workers who answered questions: "+ this.workerAnswerCountMap.size());

		System.out.println("WorkerId | Yes Count");

		Iterator<String> iter = this.workerAnswerCountMap.keySet().iterator();

		while(iter.hasNext()){
			String workerId = iter.next();
			Integer yesCount = this.workerYesCountMap.get(workerId);
			if(yesCount==null) yesCount =0;
			Integer answerCount = this.workerAnswerCountMap.get(workerId);
			Double percent = this.workerPercentCorrectAnswerMap.get(workerId);
			Integer correctCount = this.workerCorrectAnswerMap.get(workerId);
			if(correctCount==null) correctCount =0;
			System.out.println(workerId+"|"+yesCount); //workerId+"|"+yesCount+"|"+answerCount+"|"+percent.toString()); //level);//"|"+
		}
	}


	//-------------------------------------------------------------------------------

	public void printWorkerProbablyYesCount(){

		System.out.println("Workers who answered questions: "+ this.workerAnswerCountMap.size());

		System.out.println("WorkerId | Probably_Yes Count");

		Iterator<String> iter = this.workerAnswerCountMap.keySet().iterator();

		while(iter.hasNext()){
			String workerId = iter.next();
			Integer yesCount = this.workerYesCountMap.get(workerId);
			if(yesCount==null) yesCount =0;
			Integer probablyYesCount = this.workerProbablyYesCountMap.get(workerId);
			if(probablyYesCount==null) probablyYesCount =0;
			Integer totalCount = yesCount + probablyYesCount;

			Integer answerCount = this.workerAnswerCountMap.get(workerId);
			Double percent = this.workerPercentCorrectAnswerMap.get(workerId);
			Integer correctCount = this.workerCorrectAnswerMap.get(workerId);
			if(correctCount==null) correctCount =0;
			System.out.println(workerId+"|"+probablyYesCount);  //workerId+"|"+yesCount+"|"+answerCount+"|"+percent.toString()); //level);//"|"+
		}
	}

	//----------------------------------------------------------------------------	
	public void printFilteredWorkerYesProbablyYes_and_Correct_Wrong_TP(){

		System.out.println("Workers selected questions: "+ this.workerAnswerCountMap.size());
		System.out.println("WorkerId | AnswerCount | Yes | ProbYes | CorrectCount | WrongCount | TP");

		for(String workerId: this.filteredWorkers){
			Integer yesCount = this.workerYesCountMap.get(workerId);
			if(yesCount==null) yesCount =0;
			Integer probablyYesCount = this.workerProbablyYesCountMap.get(workerId);
			if(probablyYesCount==null) probablyYesCount =0;

			Integer correctCount = this.workerCorrectAnswerMap.get(workerId);
			if(correctCount==null) correctCount =0;

			Integer wrongCount = this.workerWrongAnswerMap.get(workerId);
			if(wrongCount==null) wrongCount =0;


			Integer answerCount =  workerAnswerCountMap.get(workerId);
			if(answerCount==null) answerCount =0;


			WorkerEfficacy efficacy = workerEfficacyMap.get(workerId);
			Integer TP=0;
			if(efficacy!=null)
				TP = efficacy.TP;

			System.out.println(workerId+"|"+answerCount+"|"+yesCount+"|"+probablyYesCount+"|"+correctCount+"|"+wrongCount+"|"+TP); 
		}
	}

	/**
	 * Just increments the counter for number of answers given by a worker
	 * @param workerId

	 */
	private void incrementWorkerCountYesMap(String correctLevel){

		Integer yesCount = this.yesCountForCorrectLevelMap.get(correctLevel);
		if(yesCount==null)
			yesCount = new Integer(1);
		else
			yesCount++;
		this.yesCountForCorrectLevelMap.put(correctLevel, yesCount);
	}


	public void printWorkerPercentCorrectAnswers(int numberYes){

		yesCountForCorrectLevelMap = new HashMap<String, Integer>();
		Iterator<String> iter = this.workerYesCountMap.keySet().iterator();

		while(iter.hasNext()){
			String workerId = iter.next();
			Integer yesCount = workerYesCountMap.get(workerId);
			if(yesCount==numberYes){
				Integer workerCount = this.workerCorrectAnswerMap.get(workerId);
				this.incrementWorkerCountYesMap(workerCount.toString());
			}
			//else discard
		}
	}



	public void printWorkerSessionBugPointingQuestions(){
		Iterator<String> iter = data.workerMap.keySet().iterator();

		int answersToBugPointingQuestions = 0;

		while(iter.hasNext()){
			String workerId = iter.next();
			Worker worker = data.workerMap.get(workerId);
			String sessionId = worker.getSessionId();
			if(sessionId!=null){
				WorkerSession session = data.sessionMap.get(sessionId);
				Vector<Microtask> taskList = session.getMicrotaskList();
				if(taskList!=null){
					for(Microtask task: taskList){
						String value = data.yesMap.get(task.getID().toString());
						if(value!=null){ //means that microtaskId is yes question.
							Vector<Answer>  answerList = task.getAnswerList();
							for(Answer answer: answerList){
								if(answer.getWorkerId().matches(workerId)){
									answersToBugPointingQuestions++;
								}
								//else ignores.
							}//for
						}
						else{
							System.out.println("task is not bug pointing: "+task.getID());
						}
					}//for
				}//task list empty
			}//else do nothing, because session is empty.
		}//while



		System.out.println("answers to bug pointing questions: "+answersToBugPointingQuestions+"size:"+data.yesMap.size());


	}

	public void printWorkerEfficacy(boolean filtered){
		Iterator<String> iter;
		if(filtered)
			iter = this.workerAnswerCountMap.keySet().iterator();
		else	
			iter = data.workerMap.keySet().iterator();

		System.out.println("WorkerId|TP|FP|TN|FN|I Can't Tell|Bug Pointing| Not Bug Pointing | Skill Test Score");

		while(iter.hasNext()){
			String workerId = iter.next();
			Worker worker = data.workerMap.get(workerId);
			WorkerEfficacy efficacy = this.workerEfficacyMap.get(workerId);
			if(efficacy!=null)
				System.out.println(workerId+"|"+efficacy.TP +"|"+efficacy.FP+"|"+efficacy.TN+"|"+efficacy.FN+"|"+efficacy.IDontKnow+"|"+efficacy.BugPointingQuestions+"|"+efficacy.NotBugPointingQuestions+"|"+worker.getGrade());
		}
	}



	//---------------------------------------------------------------------------------------

	public static void main(String[] args){

		WorkerAnalysis analysis = initializeLogs();

		String path =  currentPath;
		path = path +"\\DataAnalysis\\BaseDataInTime\\workerAnalysis\\";


		//analysis.computeResultsPerWorkerCluster();

		//analysis.computeCorrectAnswersPerWorker();

		Double minimalDuration = new Double(10000.0);
		Integer minimalScore =new Integer(3);
		Integer eliminationLevelOfICanTell = new Integer(2);

		String questionTypeStr = QuestionType.METHOD_INVOCATION; //CONDITIONAL_BODY CONDITIONAL_STATEMENT; LOOP_BODY; LOOP_STATEMENT;METHOD_BODY;METHOD_DECLARATION;METHOD_INVOCATION;METHOD_PARAMETERS;
		//analysis.computeCorrectAnswersPerWorker(minimalDuration,minimalScore,eliminationLevelOfICanTell,questionTypeStr);
		analysis.computeTP_FP_TN_FN_PerFilteredWorker(minimalDuration,minimalScore,eliminationLevelOfICanTell,questionTypeStr);
		analysis.printWorkerEfficacy(true);
		//analysis.printFilteredWorkerYesProbablyYes_and_Correct_Wrong_TP();

		//analysis.computeCorrectAnswersPerWorker(analysis.filteredWorkers);
		//analysis.computeTP_FP_TN_FN_PerWorker();
		//analysis.printWorkerEfficacy(true);

		//analysis.computeTotalAnswersWorker();

		//analysis.printWorkerAnswerCount();
		//analysis.printCorrectAnswer_WorkerCount(true);
		//analysis.printWorkerProbablyYesCount();
		//analysis.printWorkerYesCount();
		//analysis.printprintWorkerPercentCorrectAnswers();

		//analysis.printWorkerYesCount();
		//analysis.printWorkerYesCount();

		//analysis.printWorkerSessionBugPointingQuestions();
		//analysis.printWorkerPercentCorrectAnswersBugPointingQuestions();
	}


}

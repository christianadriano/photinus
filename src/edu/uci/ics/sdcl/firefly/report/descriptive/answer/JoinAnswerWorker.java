package edu.uci.ics.sdcl.firefly.report.descriptive.answer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.FileDebugSession;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileConsentDTO;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;
import edu.uci.ics.sdcl.firefly.servlet.FileUploadServlet;
import edu.uci.ics.sdcl.firefly.storage.MicrotaskStorage;
import edu.uci.ics.sdcl.firefly.util.BugCoveringMap;

/**
 * Joins the data from the microtasks with the attributes of workers
 * Produces a CSV file with all the answers and respective worker's attributes
 * 
 * @author Christian Adriano
 *
 */
public class JoinAnswerWorker {

	protected ArrayList<Tuple> tupleList= new ArrayList<Tuple>();
	protected HashMap<String,String> bugCoveringMap = new HashMap<String,String>();

	public JoinAnswerWorker(){
		this.bugCoveringMap = BugCoveringMap.initialize();
	}
	
	/**
	 * Keeps track of the line that will be written into a csv file 
	 * @author Christian Adriano
	 * 
	 */
	public class Tuple{

		double TP=0.0;
		double TN=0.0;
		double FN=0.0;
		double FP=0.0;

		int counter=0; //sequential identifier of an answer
		String sessionID=null; //session within which the worker answered the questions, the pair <sessionID,workerID> is unique.
		String questionID=null;
		String javaMethod=null;
		String workerID=null;
		String workerScore=null;
		String workerProfession=null;
		String yearsOfExperience=null;
		String age=null;
		String gender=null;
		String whereLearnedToCode=null;
		String country=null;
		String programmingLanguage=null;

		String difficulty=null;
		String confidence=null;
		String explanation=null;
		double duration=0.0;
		String timestamp=null;
		String answerOption=null;
		String complexity="";
		String loc="";

		boolean isEmpty = false;	

		String answerOrder=null;

		public static final String header = "counter,Session.ID,FailingMethod,Question.ID,Answer.duration_seconds,Answer.timestamp,Answer.confidence,Answer.difficulty,TP,TN,FN,FP,Answer.option,Answer.order,"
				+ "Answer.explanation,Code.LOC,Code.complexity,Worker.ID,Worker.score,Worker.profession,Worker.yearsOfExperience,Worker.age,Worker.gender,Worker.whereLearnedToCode,Worker.country,Worker.programmingLanguage";


		public String toString(){

			if(questionID==null){
				isEmpty=true;
				return ", , , , , , , ";
			}
			else
				return counter+"," +sessionID+","+javaMethod+","+questionID+","+duration+","+timestamp+","+confidence+","+difficulty+","+TP+","+TN+","+FN+","+FP+","+answerOption+","+answerOrder+","+explanation+","+loc+","+complexity+","+workerID+","+workerScore+","+workerProfession+","+yearsOfExperience+","+age+","+gender+","+whereLearnedToCode+","+country+","+programmingLanguage;

		}
	}
	//----------------------------------------------------------------------------


	/** Builds the map of answers  
	 * @param microtaskMap
	 * @param workerMap 
	 */
	public void instantiateTuples(HashMap<String, Microtask> microtaskMap, HashMap<String, Worker> workerMap){

		for(Microtask microtask: microtaskMap.values()){
			Vector<Answer> answerList = microtask.getAnswerList();
			for(Answer answer : answerList){
				String order = new Integer(answer.getOrderInWorkerSession()).toString();
				Double duration = new Double(answer.getElapsedTime())/1000; //In seconds
				Tuple tuple = computeAnswerCorrectness(microtask.getID().toString(),answer.getOption());
				tuple.sessionID = answer.getSessionID();
				tuple.answerOrder = order;
				tuple.duration = duration;
				tuple.timestamp = answer.getTimeStamp();
				tuple.javaMethod = microtask.getFileName();
				tuple.answerOption = answer.getShortOption();
				tuple.confidence = new Integer(answer.getConfidenceOption()).toString();					
				tuple.difficulty = new Integer(answer.getDifficulty()).toString();
				tuple.explanation = answer.getExplanation();
				tuple.explanation = tuple.explanation.replace(",", ";");
				tuple.loc = microtask.getLOC_CoveredByQuestion().toString();
				tuple.complexity = microtask.getCyclomaticComplexity().toString();

				tuple.workerID = answer.getWorkerId();
				Worker worker = workerMap.get(tuple.workerID);
				tuple.workerProfession =  worker.getSurveyAnswer("Experience");

				if(tuple.workerProfession.contains("Other")){
					tuple.workerProfession = "Other";
				}
				
				tuple.workerScore = worker.getGrade().toString();
				tuple.yearsOfExperience = worker.getYearsProgramming();
				tuple.age = worker.getAge();
				tuple.gender = worker.getGender();
				tuple.country = worker.getCountry();

				tuple.programmingLanguage = worker.getLanguage();
				tuple.programmingLanguage = tuple.programmingLanguage.replace(",", ";");

				tuple.whereLearnedToCode = worker.getLearnedToProgram();
				tuple.whereLearnedToCode = tuple.whereLearnedToCode.replace(",", ";");

				this.tupleList.add(tuple);
			}
		}
	}


	private Tuple computeAnswerCorrectness(String questionID, String answerOption){

		Tuple tuple = new Tuple();

		if(this.bugCoveringMap.containsKey(questionID)){
			if(answerOption.compareTo(Answer.YES)==0)
				tuple.TP=1;
			else
				if(answerOption.compareTo(Answer.NO)==0) //Ignores IDK	
					tuple.FN=1;
		}
		else{
			if(answerOption.compareTo(Answer.NO)==0)
				tuple.TN=1;
			else
				if(answerOption.compareTo(Answer.YES)==0)	 //Ignores IDK				
					tuple.FP=1;
		}

		tuple.questionID = questionID;

		return tuple;
	}

	
	public void printLists(){	

		String destination = "C://firefly//answerList_data.csv";
		BufferedWriter log;

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header

			log.write(Tuple.header+"\n");

			int counter = 1; //counts the number of answers
			for(Tuple tuple: this.tupleList){
				tuple.counter = counter; //associates each answer with a unique sequential identifier
				log.write(tuple.toString()+"\n");
				counter++;
			}

			log.close();
			System.out.println("file written at: "+destination);
		} 
		catch (Exception e) {
			System.out.println("ERROR while processing file:" + destination);
			e.printStackTrace();
		}
	}

	public void printData(){
		FileUploadServlet servlet = new FileUploadServlet();
		servlet.bulkUpload();
		FileConsentDTO consentDTO =  new FileConsentDTO();
		HashMap<String, Worker> workerMap = consentDTO.getWorkers();
		instantiateTuples(initializedMicrotaskMap(),workerMap);
		printLists();//CSV FILE	
	}

	/**
	 * Retrieve answers from log file and the microtasks from the serialized files.
	 * Instantiate the attributes of size and complexity that are stored in the serialized microtasks
	 * 
	 * @return
	 */
	private HashMap<String, Microtask> initializedMicrotaskMap(){

		FileSessionDTO sessionDTO = new FileSessionDTO();
		HashMap<String,Microtask> microtaskMap =  (HashMap<String, Microtask>) sessionDTO.getMicrotasks();

		MicrotaskStorage microtaskStorage = MicrotaskStorage.initializeSingleton();
		Set<String> sessionNames = microtaskStorage.retrieveDebuggingSessionNames();
		Iterator<String> iter = sessionNames.iterator(); 
		while(iter.hasNext()){
			String key = iter.next();
			Hashtable<String,FileDebugSession> map = microtaskStorage.readAllDebugSessions();
			FileDebugSession  debugSession = map.get(key);
			Hashtable<Integer, Microtask> serializedMicrotaskMap = debugSession.getMicrotaskMap();
			microtaskMap = complementMicrotasks(microtaskMap,serializedMicrotaskMap);
		}
		return microtaskMap;
	}


	private HashMap<String, Microtask> complementMicrotasks(HashMap<String, Microtask> microtaskMap,
			Hashtable<Integer, Microtask> serializedMicrotaskMap) {

		Iterator<Integer> iter = serializedMicrotaskMap.keySet().iterator();
		while(iter.hasNext()){
			Integer key = iter.next();
			String keyStr = key.toString();
			Microtask microtask = microtaskMap.get(keyStr);
			if(microtask!=null){
				Microtask serializedMicrotask = serializedMicrotaskMap.get(key);
				microtask.setLOC_CoveredByQuestion(serializedMicrotask.getLOC_CoveredByQuestion());
				microtask.setCyclomaticComplexity(serializedMicrotask.getCyclomaticComplexity());
				microtaskMap.put(keyStr, microtask);
			}
		}
		return microtaskMap;
	}



	/** 
	 * Entry point to generate the CSV file 
	 * @param args
	 */
	public static void main(String args[]){

		JoinAnswerWorker joiner = new JoinAnswerWorker();
		joiner.printData();
	}


}



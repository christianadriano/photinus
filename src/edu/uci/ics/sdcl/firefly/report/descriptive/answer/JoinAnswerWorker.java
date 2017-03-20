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
import edu.uci.ics.sdcl.firefly.report.descriptive.answer.IDKAnswerAnalysis.Tuple;
import edu.uci.ics.sdcl.firefly.storage.MicrotaskStorage;

/**
 * Joins the data from the microtasks with the attributes of workers
 * Produces a CSV file with all the answers and respective worker's attributes
 * 
 * @author Christian Adriano
 *
 */
public class JoinAnswerWorker {

	protected ArrayList<Tuple> tupleList= new ArrayList<Tuple>();
	HashMap<String,String> bugCoveringMap = new HashMap<String,String>();

	/**
	 * Keeps track of the line that will be written to in a csv file 
	 * @author christian adriano
	 * 
	 */
	public class Tuple{

		double TP=0.0;
		double TN=0.0;
		double FN=0.0;
		double FP=0.0;

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
		String answerOption=null;
		String complexity="";
		String loc="";

		boolean isEmpty = false;	

		String answerOrder=null;

		public static final String header = "javaMethod,questionID,duration,confidence,difficulty,TP,TN,FN,FP,answerOption,answerOrder,explanation,locs,complexity,workerID,workerScore,workerProfession,yearsOfExperience,age,gender,whereLearnedToCode,country,programmingLanguage";


		public String toString(){

			if(questionID==null){
				isEmpty=true;
				return ", , , , , , , ";
			}
			else
				return javaMethod+","+questionID+","+duration+","+confidence+","+difficulty+","+TP+","+TN+","+FN+","+FP+","+answerOption+","+answerOrder+","+explanation+","+","+loc+","+","+complexity+","+workerID+","+workerScore+","+workerProfession+","+yearsOfExperience+","+age+","+gender+","+whereLearnedToCode+","+country+","+programmingLanguage;

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
				Tuple tuple = computeCorrectness(microtask.getID().toString(),answer.getOption());
				tuple.answerOrder = order;
				tuple.duration = duration;
				tuple.javaMethod = microtask.getFileName();
				tuple.answerOption = answer.getShortOption();
				tuple.confidence = new Integer(answer.getConfidenceOption()).toString();					
				tuple.difficulty = new Integer(answer.getDifficulty()).toString();
				tuple.explanation = answer.getExplanation();
				tuple.explanation = tuple.explanation.replace(",", ";");

				if(microtask.getCyclomaticComplexity()==null){
					System.out.println("Microtask null, key="+microtask.getID());
					System.out.println();
				}
				tuple.complexity = microtask.getCyclomaticComplexity().toString();
				tuple.loc = microtask.getLOC_CoveredByQuestion().toString();

				tuple.workerID = answer.getWorkerId();
				Worker worker = workerMap.get(tuple.workerID);
				tuple.workerProfession =  worker.getSurveyAnswer("Experience");
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




	private Tuple computeCorrectness(String questionID, String answerOption){

		Tuple tuple = new Tuple();

		if(this.bugCoveringMap.containsKey(questionID)){
			if(answerOption.compareTo(Answer.YES)==0)
				tuple.TP++;
			else
				if(answerOption.compareTo(Answer.NO)==0) //Ignores IDK	
					tuple.FN++;
		}
		else{
			if(answerOption.compareTo(Answer.NO)==0)
				tuple.TN++;
			else
				if(answerOption.compareTo(Answer.YES)==0)	 //Ignores IDK				
					tuple.FP++;
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

			for(Tuple tuple: this.tupleList){
				log.write(tuple.toString()+"\n");
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
		if(iter.hasNext()){
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




	public static void main(String args[]){

		JoinAnswerWorker joiner = new JoinAnswerWorker();
		joiner.printData();

	}


}



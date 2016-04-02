package edu.uci.ics.sdcl.firefly.report.descriptive.answer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileConsentDTO;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;
import edu.uci.ics.sdcl.firefly.util.PropertyManager;


/**
 *  Selects all first, second and third answers for each Java method
 *  Prints data from each answer :java method, questionID, workerID, workerScore, answerOption, duration, TP, TN, FP, FN, answer order (1st, 2nd, 3rd)
 *  
 * @author adrianoc
 *
 */

public class IDKAnswerAnalysis {


	ArrayList<Tuple> firstAnswerList;
	ArrayList<Tuple> secondAnswerList;
	ArrayList<Tuple> thirdAnswerList;
	ArrayList<Tuple> consolidatedList;

	HashMap<String,String> firstWorkerCount;
	HashMap<String,String> secondWorkerCount;
	HashMap<String,String> thirdWorkerCount;

	HashMap<String,String> bugCoveringMap;

	double minimumDuration_1stAnswer;
	double maximumDuration_1stAnswer ;

	double minimumDuration_2ndAnswer;
	double maximumDuration_2ndAnswer ;

	double minimumDuration_3rdAnswer;
	double maximumDuration_3rdAnswer ;

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

		String difficulty=null;
		String confidence=null;
		String explanation=null;
		double duration=0.0;
		String answerOption=null;

		boolean isEmpty = false;	

		String answerOrder=null;

		public static final String header = "javaMethod,questionID,workerID,workerScore,workerProfession,duration,confidence,difficulty,TP,TN,FN,FP,answerOption,answerOrder,explanation";


		public String toString(){

			if(questionID==null){
				isEmpty=true;
				return ", , , , , , , ";
			}
			else
				return javaMethod+","+questionID+","+workerID+","+workerScore+","+workerProfession+","+duration+","+confidence+","+difficulty+","+TP+","+TN+","+FN+","+FP+","+answerOption+","+answerOrder+","+explanation;

		}
	}

	public IDKAnswerAnalysis (double minimumDuration_1stAnswer, double maximumDuration_1stAnswer,
			double minimumDuration_2ndAnswer, double maximumDuration_2ndAnswer,
			double minimumDuration_3rdAnswer, double maximumDuration_3rdAnswer){

		this.minimumDuration_1stAnswer = minimumDuration_1stAnswer;
		this.maximumDuration_1stAnswer = maximumDuration_1stAnswer;

		this.minimumDuration_2ndAnswer = minimumDuration_2ndAnswer;
		this.maximumDuration_2ndAnswer = maximumDuration_2ndAnswer;

		this.minimumDuration_3rdAnswer = minimumDuration_3rdAnswer;
		this.maximumDuration_3rdAnswer = maximumDuration_3rdAnswer;

		//Obtain bug covering question list
		PropertyManager manager = PropertyManager.initializeSingleton();
		bugCoveringMap = new HashMap<String,String>();
		String[] listOfBugPointingQuestions = manager.bugCoveringList.split(";");
		for(String questionID:listOfBugPointingQuestions){
			this.bugCoveringMap.put(questionID,questionID);
		}

		//Initialize datastructures
		this.firstAnswerList = new ArrayList<Tuple>();
		this.secondAnswerList = new ArrayList<Tuple>();
		this.thirdAnswerList = new ArrayList<Tuple>();
		this.consolidatedList = new ArrayList<Tuple>();


		this.firstWorkerCount = new HashMap<String,String>();
		this.secondWorkerCount = new HashMap<String,String>();
		this.thirdWorkerCount = new HashMap<String,String>();
	}

	/** Builds the three maps of answer duration, which can be printed by 
	 * method 
	 * @param microtaskMap
	 * @param workerMap 
	 */
	public void buildDurationsByOrder(HashMap<String, Microtask> microtaskMap, HashMap<String, Worker> workerMap){

		for(Microtask microtask: microtaskMap.values()){
			Vector<Answer> answerList = microtask.getAnswerList();
			for(Answer answer : answerList){
				String order = new Integer(answer.getOrderInWorkerSession()).toString();
				Double duration = new Double(answer.getElapsedTime())/1000; //In seconds

				//if(validDuration(order, duration)){
					Tuple tuple = computeCorrectness(microtask.getID().toString(),answer.getOption());
					tuple.duration = duration;
					tuple.javaMethod = microtask.getFileName();
					tuple.answerOption = answer.getShortOption();
					tuple.workerID = answer.getWorkerId();
					Worker worker = workerMap.get(tuple.workerID);
					tuple.workerProfession =  worker.getSurveyAnswer("Experience");
					tuple.workerScore = worker.getGrade().toString();
					tuple.confidence = new Integer(answer.getConfidenceOption()).toString();					
					tuple.difficulty = new Integer(answer.getDifficulty()).toString();
					tuple.explanation = answer.getExplanation();
					tuple.explanation = tuple.explanation.replace(",", ";");
					addAnswer(order,tuple,answer.getWorkerId());
			//	}


			}
		}
	}

	private boolean validDuration(String order, Double duration){

		double max = 0.0;
		double min = 0.0;

		if(order.compareTo("1")==0){
			max = this.maximumDuration_1stAnswer;
			min = this.minimumDuration_1stAnswer;
		}
		else{
			if(order.compareTo("2")==0){
				max = this.maximumDuration_2ndAnswer;
				min = this.minimumDuration_2ndAnswer;
			}
			else{
				if(order.compareTo("3")==0){
					max = this.maximumDuration_3rdAnswer;
					min = this.minimumDuration_3rdAnswer;
				}
				else
					System.out.println("ERROR, INDEX LARGER THAN 3. In AnswerOrderCounter : "+order);
			}
		}

		return (duration<=max&& duration>min);

	}


	private void addAnswer(String order,Tuple tuple, String workerID){
		//System.out.println(order+":"+duration.toString());
		if(order.compareTo("1")==0){
			tuple.answerOrder = "1";
			this.firstAnswerList.add(tuple);
			this.firstWorkerCount.put(workerID, workerID);
			this.consolidatedList.add(tuple);
		}
		else{
			if(order.compareTo("2")==0){
				tuple.answerOrder = "2";
				this.secondAnswerList.add(tuple);
				this.consolidatedList.add(tuple);
				this.secondWorkerCount.put(workerID, workerID);
			}
			else{
				if(order.compareTo("3")==0){
					tuple.answerOrder = "3";
					this.thirdAnswerList.add(tuple);
					this.consolidatedList.add(tuple);
					this.thirdWorkerCount.put(workerID, workerID);
				}
				else
					System.out.println("ERROR, INDEX LARGER THAN 3. In AnswerOrderCounter : "+order);
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

		String destination = "C://firefly//IDK_LOC_Correlation//answerOrderIDK.csv";
		BufferedWriter log;

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header

			log.write(Tuple.header+"\n");
			
			for(Tuple tuple: this.consolidatedList){
				log.write(tuple.toString()+"\n");
			}

			log.close();
			System.out.println("file written at: "+destination);
		} 
		catch (Exception e) {
			System.out.println("ERROR while processing file:" + destination);
			e.printStackTrace();
		}
		printCountDistinctWorkers();
	}

	private Tuple getElement(ArrayList<Tuple> list, int i){

		if(list.size()>i)
			return list.get(i);
		else 
			return new Tuple();
	}

	private double computeListAccuracy(ArrayList<Tuple> tupleList){
		double TP=0.0;
		double TN=0.0;
		double FN=0.0;
		double FP=0.0;

		for(Tuple tuple: tupleList){
			TP = TP + tuple.TP;
			TN = TN + tuple.TN;
			FN = FN + tuple.FN;
			FP = FP + tuple.FP;

		}

		double accuracy = (TP+TN)/(TP+TN+FP+FN);

		return accuracy;
	}

	public void printCountDistinctWorkers(){
		
		HashMap<String, Integer> IDK_WorkerMap = new  HashMap<String, Integer>();
		HashMap<String, Integer> NO_WorkerMap = new  HashMap<String, Integer>();
		HashMap<String, Integer> YES_WorkerMap = new  HashMap<String, Integer>();
		
		for(Tuple tuple: this.consolidatedList){
			String workerID = tuple.workerID;
			String answerOption = tuple.answerOption;
			
			if(answerOption.matches("IDK")){
				IDK_WorkerMap = this.incrementMap(workerID, IDK_WorkerMap);
			}
			else
				if(answerOption.matches("NO")){
					NO_WorkerMap = this.incrementMap(workerID, NO_WorkerMap);
				}
				else
			if(answerOption.matches("YES")){
				YES_WorkerMap = this.incrementMap(workerID, YES_WorkerMap);
			}
		}
		
		HashMap<String, Integer> finalMap = new	HashMap<String, Integer>();
		System.out.println("YES from distinct workers:"+ YES_WorkerMap.size());
		System.out.println("NO from distinct workers:"+NO_WorkerMap.size());
		System.out.println("IDK from distinct workers:"+IDK_WorkerMap.size());
		
	}
	
	private HashMap<String, Integer> incrementMap (String workerID, HashMap<String, Integer> map){
		
		if(map.containsKey(workerID)){
			Integer counter = map.get(workerID);
			counter++;
			map.put(workerID, counter);
		}
		else{
			map.put(workerID, 1);
		}
		return map;
	}
	
	public void printQuartileAccuracy(){


		System.out.print("1stAnswer: "+computeListAccuracy(this.firstAnswerList)+"; ");
		System.out.print("2ndAnswer: "+computeListAccuracy(this.secondAnswerList)+"; ");
		System.out.println("3rdAnswer: "+computeListAccuracy(this.thirdAnswerList));



	}

	public int getTotalQuartileAnswers(){
		return  (this.firstAnswerList.size() + this.secondAnswerList.size() + this.thirdAnswerList.size());
	}

	public int getTotalQuartileWorkers(){
		return  (this.firstAnswerList.size() + this.secondAnswerList.size() + this.thirdAnswerList.size());
	}

	public void printNumberOfAnswersByOrder(){

		System.out.print("1stAnswer size: "+this.firstAnswerList.size()+"; ");
		System.out.print("2ndAnswer size: "+this.secondAnswerList.size()+"; ");
		System.out.println("3rdAnswer size: "+this.thirdAnswerList.size());
	}

	public void printNumberOfWorkersByOrder(){

		System.out.print("1stAnswer Worker Count: "+this.firstWorkerCount.size()+"; ");
		System.out.print("2ndAnswer Worker Count: "+this.secondWorkerCount.size()+"; ");
		System.out.println("3rdAnswer Worker Count: "+this.thirdWorkerCount.size());
	}


	public HashMap<String,String> getAllWorkersMap(){
		HashMap<String,String> allMap = new HashMap<String, String>();
		allMap.putAll(this.firstWorkerCount);
		allMap.putAll(this.secondWorkerCount);
		allMap.putAll(this.thirdWorkerCount);
		return allMap;
	}

	public static void main(String args[]){

		analyseAllAnswers();
		//analysesQuartiles();
	}

	public static void analyseAllAnswers(){

		IDKAnswerAnalysis order = new IDKAnswerAnalysis(0, 7200,0, 7200,0, 7200);
		FileSessionDTO sessionDTO = new FileSessionDTO();
		FileConsentDTO consentDTO =  new FileConsentDTO();
		HashMap<String, Worker> workerMap = consentDTO.getWorkers();
		HashMap<String,Microtask> microtaskMap =  (HashMap<String, Microtask>) sessionDTO.getMicrotasks();
		order.buildDurationsByOrder(microtaskMap, workerMap);
		order.printLists();//CSV FILE
	}

	public static void analysesQuartiles(){
		double[] firstAnswerQuartileVector = {0,167.4,333.4,683.9,3600};
		double[] secondAnswerQuartileVector = {0,69.9,134.0,266.4,3600};
		double[] thirdAnswerQuartileVector = {0,55.8,104.9,202.6,3600};

		HashMap<String, String> distinctWorkersMap = new HashMap<String, String>();
		for(int i=0;i<4;i++){
			IDKAnswerAnalysis order = new IDKAnswerAnalysis(firstAnswerQuartileVector[i], firstAnswerQuartileVector[i+1],
					secondAnswerQuartileVector[i], secondAnswerQuartileVector[i+1],
					thirdAnswerQuartileVector[i], thirdAnswerQuartileVector[i+1]);
			FileSessionDTO sessionDTO = new FileSessionDTO();
			FileConsentDTO consentDTO =  new FileConsentDTO();
			HashMap<String, Worker> workerMap = consentDTO.getWorkers();
			HashMap<String,Microtask> microtaskMap =  (HashMap<String, Microtask>) sessionDTO.getMicrotasks();
			order.buildDurationsByOrder(microtaskMap,workerMap);
			order.printLists();//CSV FILE

			System.out.println();
			System.out.print("Quartile "+i +" :");
			order.printQuartileAccuracy();
			System.out.println("Quartile "+i +" number of answers= " +order.getTotalQuartileAnswers());
			order.printNumberOfAnswersByOrder();
			System.out.println("Quartile "+i +" number of workers= " +order.getTotalQuartileWorkers());
			order.printNumberOfWorkersByOrder();

			distinctWorkersMap.putAll(order.getAllWorkersMap());
		}

		System.out.println("Distinct workers in quartile = "+distinctWorkersMap.size());
	}

}

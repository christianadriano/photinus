package edu.uci.ics.sdcl.firefly.report.predictive.montecarlo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;
import edu.uci.ics.sdcl.firefly.util.BugCoveringMap;

/** 
 * 
 * Samples N answers per questions 
 * The number of samples is configurable.
 * These sampled sets are used as input to the predictor class
 * 
 * @author adrianoc
 */
public class RandomSampler {

	private int numberOfSamples;
	private int sampleSize;  
	private int maximumSampleSize;
	private boolean isVariableSampleSize;

	private HashMap<String,String> bugCoveringMap;
	private SampledQuestions sampledQuestionsProfile;


	public RandomSampler(int sampleSize, int numberOfSamples, int maximumSampleSize, 
			boolean isVariableSampleSize, HashMap<String,String> bugCoveringMap){

		this.sampleSize = sampleSize;
		this.numberOfSamples = numberOfSamples;
		this.maximumSampleSize = maximumSampleSize;
		this.isVariableSampleSize = isVariableSampleSize;
		this.bugCoveringMap = bugCoveringMap;
	}


	public SampledQuestions getSampledQuestionsProfile() {
		return this.sampledQuestionsProfile;
	}



	/**
	 * 
	 * @param microtaskMap
	 * @return an ArrayList with all sampled microtaskMaps
	 */
	public  ArrayList<HashMap<String, Microtask>> generateMicrotaskMap(HashMap<String, Microtask> microtaskMap,boolean withoutReplacement){

		//List of microtaskMaps to be initialized with the actual sampled answers. List has typically 1000 or 10000 microtaskMaps
		ArrayList<HashMap<String, Microtask>> sampleMapsList = this.initializeSampledMaps(microtaskMap);	

		//Keeps a list of sample answer lists for each question. Typically a 1000 or 10000 answerlist for each question
		HashMap<String,ArrayList<Vector<Answer>>> sampledAnswerByQuestion = this.generateSamplesPerQuestion(microtaskMap,withoutReplacement);

		for(String questionID : sampledAnswerByQuestion.keySet()){
			ArrayList<Vector<Answer>> answerSamplesList = sampledAnswerByQuestion.get(questionID);
			//for each questionID, set all the new answerList sampled
			for(int i=0; i<answerSamplesList.size();i++){
				Vector<Answer> answerList = answerSamplesList.get(i);
				HashMap<String, Microtask> sampleMicrotaskMap = sampleMapsList.get(i);
				Microtask task = sampleMicrotaskMap.get(questionID);
				task.setAnswerList(answerList);
				sampleMicrotaskMap.put(questionID, task);
				sampleMapsList.set(i, sampleMicrotaskMap);
			}
		}
		return sampleMapsList;
	}

	/**
	 * 
	 * @param microtaskMap
	 * @return clones of the microtaskMap
	 */
	private ArrayList<HashMap<String, Microtask>> initializeSampledMaps(HashMap<String, Microtask> microtaskMap){

		ArrayList<HashMap<String, Microtask>> sampleMapsList = new ArrayList<HashMap<String, Microtask>> ();	

		for(int i=0; i<this.numberOfSamples;i++){
			HashMap<String, Microtask> sampleMicrotaskMap = this.cloneMap(microtaskMap);
			sampleMapsList.add(sampleMicrotaskMap);
		}

		return sampleMapsList;
	}

	/**
	 * 
	 * @param microtaskMap
	 * @return for each questionID a list of answer samples
	 */
	private HashMap<String,ArrayList<Vector<Answer>>> generateSamplesPerQuestion(HashMap<String, Microtask> microtaskMap, boolean withoutReplacement){

		HashMap<String,ArrayList<Vector<Answer>>> sampledAnswerByQuestionMap = new HashMap<String,ArrayList<Vector<Answer>>>();
		double oversamplingCount=0.0;
		double undersamplingCount=0.0;
		double countOf_OversampledQuestions_bugCovering=0.0;
		double countOf_UndersampledQuestions_bugCovering=0.0;
		double countOf_OversampledQuestions_NonBugCovering = 0.0;
		double countOf_UndersampledQuestions_NonBugCovering = 0.0;

		for(Microtask task: microtaskMap.values()){
			ArrayList<Vector<Answer>> sampleAnswerList = new ArrayList<Vector<Answer>>();
			String questionID = task.getID().toString();
			Vector<Answer> answerList = task.getAnswerList();

			//There is never oversampling with fixed sample size
			if(this.isVariableSampleSize && answerList.size()<=this.sampleSize){
				oversamplingCount++;
				if(this.bugCoveringMap.containsKey(questionID))
					countOf_OversampledQuestions_bugCovering++;
				else
					countOf_OversampledQuestions_NonBugCovering++;

				sampleAnswerList.add((Vector<Answer>)answerList.clone()); //Don't sample, just take the entire list of answers.
			}
			else if(answerList.size()<this.sampleSize){
				System.out.println("SHOULD NEVER ENTER HERE!!!!!!!!!!!!, sampleSize:"+this.sampleSize+", answerList.size:"+answerList.size());
			}
			else{
			
				//Sample answers
				if(withoutReplacement){
					sampleAnswerList = this.sampleWithout_Replacement(answerList);
				}
				else{
					sampleAnswerList = this.sampleWith_Replacement(answerList);
				}

				//counter for statistics
				undersamplingCount++;
				if(this.bugCoveringMap.containsKey(questionID))
					countOf_UndersampledQuestions_bugCovering++;
				else
					countOf_UndersampledQuestions_NonBugCovering++;			
			}

			sampledAnswerByQuestionMap.put(questionID, sampleAnswerList);
		}

		//System.out.println("SampleSize:"+this.sampleSize+", OversamplingCount:"+oversamplingCount+", normalsamplingCount:"+undersamplingCount);

		this.sampledQuestionsProfile = new SampledQuestions(this.sampleSize,  oversamplingCount, undersamplingCount, 0,
				countOf_OversampledQuestions_bugCovering,  countOf_OversampledQuestions_NonBugCovering,
				countOf_UndersampledQuestions_bugCovering,  countOf_UndersampledQuestions_NonBugCovering);

		return sampledAnswerByQuestionMap;
	}


	/**
	 * While dealing with filtered data, Microtasks will have different number of answers each.
	 * Therefore, we should set the maximum sample size to the size of the MAXIMUM number of answers per microtask.
	 * This way we avoid over-sampling microtasks that have fewer answers.
	 * @param microtaskMap
	 * @return the size of the microtasks with fewer answers
	 */
	public static int computeMaximum_NumberAnswers(HashMap<String, Microtask> microtaskMap){

		int maximumAnswersPerMicrotask = 0;
		for(Microtask task: microtaskMap.values()){
			Vector<Answer> answerList = task.getAnswerList();
			if(answerList.size()>maximumAnswersPerMicrotask)
				maximumAnswersPerMicrotask = answerList.size();
		}
		return maximumAnswersPerMicrotask;
	}


	/**
	 * While dealing with filtered data, Microtasks will have different number of answers each.
	 * Therefore, we should set the maximum sample size to the size of the MINIMUM number of answers per microtask.
	 * This way we avoid over-sampling microtasks that have fewer answers.
	 * @param microtaskMap
	 * @return the size of the microtasks with fewer answers
	 */
	public static int computeMinimum_NumberAnswers(HashMap<String, Microtask> microtaskMap){

		int minimumAnswersPerMicrotask = 20;
		for(Microtask task: microtaskMap.values()){
			Vector<Answer> answerList = task.getAnswerList();
			if(answerList.size()<minimumAnswersPerMicrotask)
				minimumAnswersPerMicrotask = answerList.size();
		}
		return minimumAnswersPerMicrotask;
	}

	private void printSamples(String questionID, ArrayList<Vector<Answer>> sampleAnswerList) {

		System.out.println(questionID+"--------------------------");
		for(int i=0;i<sampleAnswerList.size();i++){	
			System.out.println("-- Sample:"+i);
			Vector<Answer> sample = sampleAnswerList.get(i);
			for(int j=0; j<sample.size();j++){
				System.out.print(sample.get(j).getWorkerId()+"_");
			}
			System.out.println();		
		}
	}


	/**
	 * 
	 * @param answerList
	 * @return sampled answers out of the population, sampled without replacement.
	 */
	private ArrayList<Vector<Answer>> sampleWithout_Replacement(Vector<Answer> answerList){

		ArrayList<Vector<Answer>> samplesList = new ArrayList<Vector<Answer>>(); 

		HashMap<String,Integer> pickedAnswersMap = new HashMap<String,Integer>(); 

		for(int i=0;i<this.numberOfSamples;i++){
			Vector<Answer> sample = new Vector<Answer>();

			pickedAnswersMap = sampleIndex_WithoutReplacement(this.sampleSize,answerList.size());
			for(String key: pickedAnswersMap.keySet()){
				Answer answer = answerList.get(pickedAnswersMap.get(key).intValue());
				sample.add(answer);
			}
			samplesList.add(sample);
			//if(sampleSize==20)
			//	printSample(sample);
		}
		return samplesList;
	}


	/**
	 * This method is used to run a bootstrap sampling
	 * 
	 * @param answerList
	 * @return sampled answers out of the population, sampled WITH replacement.
	 */
	private ArrayList<Vector<Answer>> sampleWith_Replacement(Vector<Answer> answerList){

		ArrayList<Vector<Answer>> samplesList = new ArrayList<Vector<Answer>>(); 

		ArrayList<Integer> pickedAnswersList = new ArrayList<Integer>(); 

		for(int i=0;i<this.numberOfSamples;i++){
			Vector<Answer> sample = new Vector<Answer>();

			pickedAnswersList = sampleIndex_WithReplacement(this.sampleSize,answerList.size());
			for(Integer index: pickedAnswersList){
				Answer answer = answerList.get(index.intValue());
				sample.add(answer);
			}
			samplesList.add(sample);
			//if(sampleSize==20)
			//	printSample(sample);
		}
		return samplesList;
	}

	//---------- Sampling with and without

	private HashMap<String, Integer> sampleIndex_WithoutReplacement(int sampleSize,int populationSize) {

		//Create the list of indexes that will be sampled from
		ArrayList<String> indexList = new ArrayList<String>();
		for(int i=0;i<populationSize;i++){
			indexList.add(new Integer(i).toString());
		}

		HashMap<String,Integer> pickedAnswersMap = new HashMap<String,Integer>();

		Random rand = new Random(System.currentTimeMillis());

		int currentSize = populationSize;

		for(int i=0; i<sampleSize;i++){
			Integer index = rand.nextInt(currentSize);

			String indexStr = indexList.get(index.intValue());

			if(!pickedAnswersMap.containsKey(indexStr)){
				pickedAnswersMap.put(indexStr, index);
			}
			else{
				System.out.println("ERROR! index was repeated at sampleWithoutReplacement:"+ indexStr);
			}

			//Updates the list of indexes to sample from
			indexList.remove(index.intValue());
			currentSize--;
		}
		return pickedAnswersMap;
	}


	/**	
	 * Create a random list of unique indexes, so we can avoid picking the same answer twice 
	 * @param sampleSize
	 * @param populationSize
	 * @return
	 */
	private ArrayList<Integer> sampleIndex_WithReplacement (int sampleSize,int populationSize){

		ArrayList<Integer> pickedAnswersList = new ArrayList<Integer>();

		Random rand = new Random(System.currentTimeMillis());

		for(int i=0; i<sampleSize;i++){
			Integer index = rand.nextInt(populationSize);
			pickedAnswersList.add(index);
		}
		return pickedAnswersList;
	}
	
	private HashMap<String, Microtask> cloneMap(HashMap<String, Microtask> map){

		HashMap<String, Microtask> cloneMap = new HashMap<String, Microtask>();

		for(Microtask microtask : map.values()){
			Microtask cloneTask = microtask.getSimpleVersion();
			cloneMap.put(cloneTask.getID().toString(), cloneTask);
		}
		return cloneMap;
	}


	//--------- PRINT FUNCTIONS FOR TESTING

	private void printSample(Vector<Answer> sample) {
		String outcome = "";
		for(Answer answer: sample){
			outcome = outcome + ","+ answer.getOption();
		}
		System.out.println(outcome);
	}

	private void printMap(HashMap<String,Integer> pickedAnswersMap){
		String outcome="";
		Iterator<String> iter = pickedAnswersMap.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			outcome = outcome+","+key;
		}
		System.out.println(outcome);
	}

	
	//---------TESTS --------------------------------------------------------------------

	public void testSampleAnswerForQuestion(){

		//String option, int confidenceOption, String explanation, String workerId, 
		//String elapsedTime, String timeStamp, int difficulty, int orderInWorkerSession
		int confidence=0;
		int difficulty =0;
		int orderInWorkerSession=0;
		String explanation = "explanation";
		String timeStamp = null;
		String elapsedTime = "00:00:00.000";
		String sessionID="sessionID";

		Vector<Answer> answerList = new Vector<Answer>();

		for(int i=0; i<this.maximumSampleSize;i++){
			answerList.add(new Answer("YES", confidence, explanation, new Integer(i).toString(), 
					elapsedTime, timeStamp, difficulty, orderInWorkerSession,sessionID));
		}

		ArrayList<Vector<Answer>> answerSamplesList = this.sampleWithout_Replacement(answerList);
		for(int i=0;i<this.numberOfSamples;i++){
			Vector<Answer> sample = answerSamplesList.get(i);
			for(int j=0; j<sample.size();j++){
				System.out.print(sample.get(j).getWorkerId()+":");
			}
			System.out.println();
		}
	}

	public void testGenerateSamplesPerQuestion(){

		FileSessionDTO dto =  new FileSessionDTO();
		HashMap<String,Microtask> map = (HashMap<String, Microtask>) dto.getMicrotasks();

		this.generateMicrotaskMap(map, true);
	}

	private void testSampleWithoutReplacement(int sampleSize, int populationSize) {
		System.out.println("Sample without Replacement:");
		HashMap<String,Integer> pickedAnswersMap =  this.sampleIndex_WithoutReplacement(sampleSize, populationSize);
		Iterator<String> iterKeys = pickedAnswersMap.keySet().iterator();
		while(iterKeys.hasNext()){
			System.out.println((String)iterKeys.next());
		}
	}
	
	private void testSampleIndex_WithReplacement(int sampleSize, int populationSize) {
		System.out.println("Sample with Replacement:");
		ArrayList<Integer> pickedAnswersList =  this.sampleIndex_WithReplacement(sampleSize, populationSize);
		for(int i=0;i<pickedAnswersList.size();i++){
			System.out.println((String)pickedAnswersList.get(i).toString());
		}
	}

	/**
	 * Main method is only for testing purposes
	 * @param args
	 */
	public static void main(String args[]){
		HashMap<String,String> bugCoveringMap = BugCoveringMap.initialize();
		RandomSampler sampling = new RandomSampler(4,20,20,true,bugCoveringMap);
		//sampling.testSampleAnswerForQuestion();
		//sampling.testGenerateSamplesPerQuestion();

		//sampling.testSampleWithoutReplacement(5,9); //working
		//sampling.testSampleIndex_WithReplacement(5,9); //working
		
		Random rand = new Random(System.currentTimeMillis());

		for(int i=0; i<10;i++){
			Integer index = rand.nextInt(1);
			System.out.println("Index: "+index +" size=1");
		}
		
		
	}







}

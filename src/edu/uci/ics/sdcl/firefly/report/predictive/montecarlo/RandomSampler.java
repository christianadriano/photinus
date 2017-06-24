package edu.uci.ics.sdcl.firefly.report.predictive.montecarlo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;

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
	private int populationSize;
	private boolean isFixedSampleSize;

	private HashMap<String,Integer> indexMap= new HashMap<String,Integer>();


	public RandomSampler(int sampleSize, int numberOfSamples, int populationSize, boolean isFixedSampleSize){

		this.sampleSize = sampleSize;
		this.numberOfSamples = numberOfSamples;
		this.populationSize = populationSize;

		//Initializes the index used to sample answers
		for(int i=0;i<populationSize;i++){
			Integer indexInt = new Integer(i);
			indexMap.put(indexInt.toString(),indexInt);
		}
	}


	/**
	 * 
	 * @param microtaskMap
	 * @return an ArrayList with all sampled microtaskMaps
	 */
	public  ArrayList<HashMap<String, Microtask>> generateMicrotaskMap(HashMap<String, Microtask> microtaskMap){

		ArrayList<HashMap<String, Microtask>> sampleMapsList = this.initializeSampledMaps(microtaskMap);	

		HashMap<String,ArrayList<Vector<Answer>>> sampledAnswerByQuestion = this.generateSamplesPerQuestion(microtaskMap);

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
	private HashMap<String,ArrayList<Vector<Answer>>> generateSamplesPerQuestion(HashMap<String, Microtask> microtaskMap){

		HashMap<String,ArrayList<Vector<Answer>>> sampledAnswerByQuestion = new HashMap<String,ArrayList<Vector<Answer>>>();

		for(Microtask task: microtaskMap.values()){
			ArrayList<Vector<Answer>> sampleAnswerList = new ArrayList<Vector<Answer>>();
			String questionID = task.getID().toString();
			Vector<Answer> answerList = task.getAnswerList();
			if(!this.isFixedSampleSize && answerList.size()<=this.populationSize){
				sampleAnswerList.add((Vector<Answer>)answerList.clone());
			}
			else
				sampleAnswerList = this.sampleAnswersFixedSize(answerList);

			sampledAnswerByQuestion.put(questionID, sampleAnswerList);
		}
		return sampledAnswerByQuestion;
	}

	/**
	 * When dealing with filtered data, Microtasks will have different number of answers each.
	 * Therefore, we should set the maximum sample size to the size of the minimum number of answers per microtask.
	 * This was we avoid over-sampling microtasks that have fewer answers.
	 * @param microtaskMap
	 * @return the size of the microtasks with fewer answers
	 */
	public static int computeMaximumSampleSize(HashMap<String, Microtask> microtaskMap){

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
	private ArrayList<Vector<Answer>> sampleAnswersFixedSize(Vector<Answer> answerList){

		ArrayList<Vector<Answer>> samplesList = new ArrayList<Vector<Answer>>(); 

		HashMap<String,Integer> pickedAnswersMap = new HashMap<String,Integer>(); 

		for(int i=0;i<this.numberOfSamples;i++){
			Vector<Answer> sample = new Vector<Answer>();

			pickedAnswersMap = sampleWithoutReplacementIndexes(this.sampleSize,answerList.size());
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
	 * 	
	 * Create a random list of unique indexes, so we can avoid picking the same answer twice 
	 * @param sampleSize
	 * @param populationSize
	 * @return
	 */
	private HashMap<String,Integer> sampleWithoutReplacementIndexes (int sampleSize,int populationSize){

		HashMap<String,Integer> pickedAnswersMap = new HashMap<String,Integer>();

		Random rand = new Random();

		while(pickedAnswersMap.size()<sampleSize){

			Integer index = rand.nextInt(populationSize);
			String indexStr = index.toString();
			if(!pickedAnswersMap.containsKey(indexStr)){
				pickedAnswersMap.put(indexStr, index);
			}
		}

		//if(max==20){
		//	printMap(pickedAnswersMap);
		//}

		return pickedAnswersMap;
	}


	private void printSample(Vector<Answer> sample) {
		String outcome = "";
		for(int i=0;i<sample.size();i++){
			outcome = outcome + ","+ sample.get(i).getOption();
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

	private HashMap<String, Microtask> cloneMap(HashMap<String, Microtask> map){

		HashMap<String, Microtask> cloneMap = new HashMap<String, Microtask>();

		for(Microtask microtask : map.values()){
			Microtask cloneTask = microtask.getSimpleVersion();
			cloneMap.put(cloneTask.getID().toString(), cloneTask);
		}
		return cloneMap;
	}

	//-------------------------------------------------------------------------------------------------------------------------------

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

		for(int i=0; i<this.populationSize;i++){
			answerList.add(new Answer("YES", confidence, explanation, new Integer(i).toString(), 
					elapsedTime, timeStamp, difficulty, orderInWorkerSession,sessionID));
		}

		ArrayList<Vector<Answer>> answerSamplesList = this.sampleAnswersFixedSize(answerList);
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

		this.generateMicrotaskMap(map);
	}

	public static void main(String args[]){
		RandomSampler sampling = new RandomSampler(4,20,20,true);
		sampling.testSampleAnswerForQuestion();
		//sampling.testGenerateSamplesPerQuestion();
	}


}

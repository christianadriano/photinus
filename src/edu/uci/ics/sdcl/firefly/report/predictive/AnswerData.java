package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;


/** Holds all answers for each question for a Java Method */
public class AnswerData {

	/** name of the HIT (HIT01_8, HIT02_24, etc.) */
	String hitFileName;

	/** the total different workers in this data set */
	Integer workerCount;

	/** Total workers that remained after applying the combined filter */
	Integer differentWorkersAmongHITs;

	/** questionID, list of answer as YES, NO, IDK */
	public HashMap<String, ArrayList<String>> answerMap;

	/** list of bug-covering question */
	public HashMap<String, String> bugCoveringMap;

	public AnswerData(String hitFileName,
			HashMap<String, ArrayList<String>> answers,
			HashMap<String, String> bugCoveringMap, Integer workerCount, Integer differentWorkersAmongHITs) {
		this.hitFileName = hitFileName;
		this.answerMap = answers;
		this.bugCoveringMap = bugCoveringMap;
		this.workerCount = workerCount;
		this.differentWorkersAmongHITs = differentWorkersAmongHITs;
	}

	public String getHitFileName() {
		return hitFileName;
	}

	public Integer getWorkerCount(){
		return this.workerCount;
	}	

	public Integer getDifferentWorkersAmongHITs() {
		return this.differentWorkersAmongHITs;
	}

	public HashMap<String, ArrayList<String>> getAnswerMap() {
		return answerMap;
	}

	public HashMap<String, String> getBugCoveringMap() {
		return bugCoveringMap;
	}

	public Integer getTotalAnswers() {
		int answerCount = 0;
		for(String questionID: this.answerMap.keySet()){
			ArrayList<String> answerList = answerMap.get(questionID);
			answerCount = answerCount + answerList.size();
		}
		return answerCount;
	}


	public static int countCorrectYES(HashMap<String,ArrayList<String>> microtaskMap, HashMap<String, String> bugCoveringMap){

		int correctCount=0;
		Iterator<String> iter = microtaskMap.keySet().iterator();

		while(iter.hasNext()){
			String questionID = iter.next();
			ArrayList<String> answerList =  microtaskMap.get(questionID);
			if(bugCoveringMap.containsKey(questionID)){
				correctCount = correctCount + countOption(answerList, Answer.YES);
			}
		}
		return correctCount;	
	}

	public static int countCorrectNO(HashMap<String,ArrayList<String>> microtaskMap, HashMap<String, String> bugCoveringMap){

		int correctCount=0;
		Iterator<String> iter = microtaskMap.keySet().iterator();

		while(iter.hasNext()){
			String questionID = iter.next();
			ArrayList<String> answerList =  microtaskMap.get(questionID);
			if(!bugCoveringMap.containsKey(questionID)){
				correctCount = correctCount + countOption(answerList, Answer.NO);
			}
		}
		return correctCount;	
	}
	
	
	/**
	 *  Count the number of correct answers. 
	 *  If the questions is bug covering, a correct answer is a YES
	 *  If the question is not bug covering, a correct answer is either NO or IDK
	 *  
	 * @param microtakMap map with all microtask with their respective answers
	 * @param bugCoveringMap map indicating which questions are bug covering
	 * @param answerOption  Answer.YES, Answer.NO
	 * @return
	 */
	public static int countCorrectAnswers(HashMap<String,ArrayList<String>> microtaskMap, HashMap<String, String> bugCoveringMap, String answerOption){

		int correctCount=0;
		Iterator<String> iter = microtaskMap.keySet().iterator();

		while(iter.hasNext()){
			String questionID = iter.next();
			ArrayList<String> answerList =  microtaskMap.get(questionID);
			if(bugCoveringMap.containsKey(questionID) && answerOption.matches(Answer.YES)){
				correctCount = correctCount + countOption(answerList, Answer.YES);
			}
			else{
				correctCount = correctCount + countOption(answerList, Answer.NO);
			}	
		}
		return correctCount;	
	}

	/**
	 * 
	 * @param answerList
	 * @param answerOption Answer.YES, Answer.NO, Answer.IDK 
	 * @return number of answers of that type.
	 */
	public static int countOption(ArrayList<String> answerList, String answerOption){

		int count=0;
		for(String answer:answerList){
			if(answer.matches(answerOption)){
				count++;
			}
		}
		return count;
	}

	/**
	 * 
	 * @param answerList
	 * @param answerOption Answer.YES, Answer.NO, Answer.IDK 
	 * @return number of answers of that type.
	 */
	public static int count(HashMap<String, ArrayList<String>> answerMap, String answerOption){

		int count=0;
		Iterator<String> iter = answerMap.keySet().iterator();

		while(iter.hasNext()){
			String questionID = iter.next();
			ArrayList<String> answerList =  answerMap.get(questionID);
			count = count + countOption(answerList, answerOption);
		}
		return count;	
	}


}

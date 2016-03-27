package edu.uci.ics.sdcl.firefly.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;

/**
 * 
 * Utility methods to count answers, workers, elapsed time, and extract microtasks per file name
 * 
 * @author adrianoc
 *
 */
public class MicrotaskMapUtil {

	public static Answer getFirstAnswer(HashMap<String, Microtask> map){

		Answer currentAnswer=null;

		for(Microtask microtask:map.values()){
			Vector<Answer> answerList = microtask.getAnswerList();
			for(Answer answer:answerList){
				if(currentAnswer==null)
					currentAnswer=answer;
				else{
					Date answerDate = answer.getTimeStampDate(); 
					if(answerDate.compareTo(currentAnswer.getTimeStampDate())<0)
						currentAnswer = answer;
				}
			}	
		}
		return currentAnswer;
	}

	public static Answer getLastAnswer(HashMap<String,Microtask> map){

		Answer currentAnswer=null;

		for(Microtask microtask:map.values()){
			Vector<Answer> answerList = microtask.getAnswerList();
			for(Answer answer:answerList){
				if(currentAnswer==null)
					currentAnswer=answer;
				else{
					Date answerDate = answer.getTimeStampDate(); 
					if(answerDate.compareTo(currentAnswer.getTimeStampDate())>0)
						currentAnswer = answer;
				}
			}	
		}
		return currentAnswer;
	}

	public static double computeElapsedTime_Hours(Date startDate, Date endDate){
		double millisec = endDate.getTime() - startDate.getTime();
		return millisec /(3600 *1000);
	}

	public static Integer countWorkers(
			HashMap<String, Microtask> filteredMicrotaskMap, String fileName) {

		HashMap<String,String> workerMap = new HashMap<String, String>();
		for(Microtask task: filteredMicrotaskMap.values()){
			if(fileName==null || task.getFileName().compareTo(fileName)==0){
				for(Answer answer:task.getAnswerList()){
					String workerID = answer.getWorkerId();
					workerMap.put(workerID, workerID);
				}
			}
		}
		return workerMap.size();
	}

	public static Double countAnswers(HashMap<String, Microtask> map){

		double count=0.0;
		for(Microtask microtask: map.values()){
			count = count + microtask.getNumberOfAnswers();
		}
		return count;
	}

	public static Double computeElapsedTimeForAnswerLevels(HashMap<String, Microtask> map){

		Answer firstAnswer = getFirstAnswer(map);
		Answer lastAnswer = getLastAnswer(map);
		return computeElapsedTime_Hours(firstAnswer.getTimeStampDate(),lastAnswer.getTimeStampDate());
	}

	public static HashMap<String, ArrayList<String>>  extractAnswersForFileName(
			HashMap<String, Microtask> microtaskMap,String fileName){

		int answerCount = 0;
		HashMap<String, ArrayList<String>> resultMap = new HashMap<String, ArrayList<String>>();

		for(Microtask task:microtaskMap.values() ){
			//System.out.println("fileName: "+fileName+":"+task.getFileName());
			if(task.getFileName().compareTo(fileName)==0){
				resultMap.put(task.getID().toString(),task.getAnswerOptions());
				answerCount = answerCount+task.getAnswerOptions().size();
			}
		}
		//System.out.println(fileName+" has "+answerCount+" answers");
		return resultMap;
	}

	public static HashMap<String, Microtask> cloneMap(HashMap<String, Microtask> map){

		HashMap<String, Microtask> cloneMap = new HashMap<String, Microtask>();

		for(Microtask microtask: map.values()){
			Microtask cloneTask = microtask.getSimpleVersion();
			cloneMap.put(cloneTask.getID().toString(), cloneTask);
		}
		return cloneMap;
	}

	public static int getMaxAnswersPerQuestion(HashMap<String, Microtask> map){

		int maxAnswers=0;
		for(Microtask microtask: map.values()){
			maxAnswers = maxAnswers<microtask.getNumberOfAnswers()? microtask.getNumberOfAnswers(): maxAnswers;
		}
		return maxAnswers;
	}

	/**
	 * The largest number of answers that all questions have. 
	 * 
	 * @param map
	 * @return
	 */
	public static int getMaxCommonAnswersPerQuestion(HashMap<String, Microtask> map){

		int maxCommonAnswers=20;
		for(Microtask microtask: map.values()){
			maxCommonAnswers = maxCommonAnswers>microtask.getNumberOfAnswers()? microtask.getNumberOfAnswers(): maxCommonAnswers;
		}
		return maxCommonAnswers;
	}

	public static HashMap<String, Microtask> cutMapToMaximumAnswers(HashMap<String, Microtask> map, Integer maxCommonAnswers) {

		HashMap<String, Microtask> cutMap = new HashMap<String, Microtask>();

		for(Microtask microtask: map.values()){

			Vector<Answer> answerList = microtask.getAnswerList();
			if(answerList.size()>=maxCommonAnswers){
				answerList.subList(0, maxCommonAnswers-1);
			}
			else
				return null; //size should be at least of maxCommonAnswers

			microtask.setAnswerList(answerList);
			cutMap.put(microtask.getID().toString(), microtask);
		}
		return cutMap;
	}

	public static HashMap<String, Microtask> mergeMaps (HashMap<String, Microtask> originalMap, HashMap<String, Microtask> map1, 
			HashMap<String, Microtask> map2){

		HashMap<String, Microtask> mergedMap = new HashMap<String, Microtask>();

		for(Microtask microtask: originalMap.values()){		

			Integer microtaskID = microtask.getID();
			Vector<Answer> newAnswerList = new Vector<Answer>();

			Microtask microtask1 = map1.get(microtaskID.toString());
			if(microtask1!=null){
				Vector<Answer> answerList1 = microtask1.getAnswerList();
				if(answerList1.size()>0)
					newAnswerList.addAll(answerList1);
			}

			Microtask microtask2 = map2.get(microtaskID.toString());		
			if(microtask2!=null){
				Vector<Answer> answerList2 = microtask2.getAnswerList();
				if(answerList2.size()>0)
					newAnswerList.addAll(answerList2);
			}

			if(newAnswerList.size()>0){//Do not include questions that have no answers
				Microtask newMicrotask = microtask.getSimpleVersion();
				newMicrotask.setAnswerList(newAnswerList);
				mergedMap.put(microtaskID.toString(), newMicrotask);
			}
		}
		return mergedMap;
	}



}

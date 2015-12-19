package edu.uci.ics.sdcl.firefly.report.predictive;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.CodeElement;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;
import edu.uci.ics.sdcl.firefly.report.descriptive.Filter;
import edu.uci.ics.sdcl.firefly.report.predictive.AnswerConfidenceCounter.Output;
import edu.uci.ics.sdcl.firefly.report.predictive.spectra.QuestionLOCs;
import edu.uci.ics.sdcl.firefly.report.predictive.spectra.QuestionLinesData;
import edu.uci.ics.sdcl.firefly.util.PropertyManager;

public class QuestionTypeAnalysis {


	public class TypeOutput{

		String questionID="";
		boolean bugCovering=false;
		String questionType="";
		int LOCs=0;

		int TP=0;
		int TN=0;
		int FN=0;
		int FP=0;

		ArrayList<Double> durationList= new ArrayList<Double>();;

		ArrayList<Integer> confidenceList = new ArrayList<Integer>();
		ArrayList<Integer>  difficultyList = new ArrayList<Integer>(); 

		Double averageConfidence = 1.0;
		Double averageDifficulty = 1.0;
		Double averageDuration = 1.0;

		public String toString(){

			
			
			return (questionID+","+bugCovering+","+questionType+","+LOCs+ 
					arrayDoubleToString(this.durationList)+","+
					averageDuration.toString()+ 
					arrayIntToString(this.confidenceList)+","+
					averageConfidence.toString()+ 
					arrayIntToString(this.difficultyList)+","+
					averageDifficulty.toString()+
					","+TP+","+TN+","+FN+","+FP);
		}
		
		public String arrayIntToString(ArrayList<Integer> list){
			String itemList = "";
			for(Integer item: list){
				itemList = itemList + "," + item.toString();
			}
			return itemList;
		}
		
		public String arrayDoubleToString(ArrayList<Double> list){
			String itemList = "";
			for(Double item: list){
				itemList = itemList + "," + item.toString();
			}
			return itemList;
		}
		
		
		

		 
	}	


	private HashMap<String, Microtask> addQuestionSizeData(HashMap<String, Microtask> microtaskMap, 
			HashMap<String,Integer> ID_LOC_Map){

		HashMap<String, Microtask> microtaskResultMap = new HashMap<String, Microtask>();

		Iterator<String> iter = microtaskMap.keySet().iterator();
		while(iter.hasNext()){
			String taskID = iter.next();
			Microtask task = microtaskMap.get(taskID);
			Integer LOCS= ID_LOC_Map.get(taskID);
			task.setLOC_CoveredByQuestion(LOCS);
			microtaskResultMap.put(taskID, task);
		}

		return microtaskResultMap;
	}


	/** Builds the three maps of answer duration, which can be printed by 
	 * method 
	 * @param microtaskMap
	 */
	public ArrayList<TypeOutput> buildOutputList(HashMap<String, Microtask> microtaskMap){

		ArrayList<TypeOutput> list = new ArrayList<TypeOutput>();
		
		//Obtain bug covering question list
		PropertyManager manager = PropertyManager.initializeSingleton();
		HashMap<String,String> bugCoveringMap = new HashMap<String,String>();
		String[] listOfBugPointingQuestions = manager.bugCoveringList.split(";");
		for(String questionID:listOfBugPointingQuestions){
			bugCoveringMap.put(questionID,questionID);
		}

		for(Microtask microtask: microtaskMap.values()){
			Vector<Answer> answerList = microtask.getAnswerList();
			TypeOutput output = new TypeOutput();
			output.questionID = microtask.getID().toString();
			output.questionType = microtask.getQuestionType();
			output.LOCs = microtask.getLOC_CoveredByQuestion();

			if(bugCoveringMap.containsKey(output.questionID))
				output.bugCovering = true;
			else
				output.bugCovering = false;

			for(Answer answer : answerList){
				output.confidenceList.add(answer.getConfidenceOption());
				output.difficultyList.add(answer.getDifficulty());
				output.durationList.add(new Double(answer.getElapsedTime()));

				if(output.bugCovering){
					if(answer.getOption().compareTo(Answer.YES)==0){
						output.TP++;
					}
					else
						if(answer.getOption().compareTo(Answer.NO)==0){ //Ignore IDK
							output.FN++;
						}
				}
				else{//Non-bug covering
					if(answer.getOption().compareTo(Answer.YES)==0){
						output.FP++;
					}
					else
						if(answer.getOption().compareTo(Answer.NO)==0){ //Ignore IDK
							output.TN++;
						}
				}
			}//for loop Answerlist
			output.averageDifficulty = computeAverageInteger(output.difficultyList);
			output.averageConfidence = computeAverageInteger(output.confidenceList);
			output.averageDuration = computeAverageDouble(output.durationList);
			list.add(output);
		}
		return list;
	}


	private Double computeAverageDouble(ArrayList<Double> list) {
		Double average = 0.0;
		for(Double item : list){
			average = average + item;
		}
		average = average / list.size();
		return average;
	}


	private Double computeAverageInteger(ArrayList<Integer> list) {
		Double average = 0.0;
		for(Integer item : list){
			average = average + item;
		}
		average = average / list.size();
		return average;
	}


	public static void main(String[] args){	

		QuestionTypeAnalysis counter = new QuestionTypeAnalysis();
		FileSessionDTO sessionDTO = new FileSessionDTO();

		//Produce the list of filters
		ArrayList<FilterCombination> filterList = FilterGenerator.generateAnswerFilterCombinationList();
		FilterCombination combination =  filterList.get(0);
		Filter filter = combination.getFilter();

		HashMap<String,Microtask> microtaskMap =  (HashMap<String, Microtask>) sessionDTO.getMicrotasks();

		//HashMap<String, Microtask> filteredMicrotaskMap = (HashMap<String, Microtask>) filter.apply(microtaskMap);

		QuestionLOCs lineCounter = new QuestionLOCs();
		HashMap<String, Integer>  IDLOCMap = lineCounter.loadList();
		HashMap<String,Microtask> microtaskWithLOCS_Map = counter.addQuestionSizeData(microtaskMap,  IDLOCMap);
		ArrayList<TypeOutput> outputList = counter.buildOutputList(microtaskWithLOCS_Map);
		counter.printOutput(outputList);
	}

	public String header(){
		return ("questionID,bugCovering,questionType,LOCs,"+ 
			"1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,average duration,"+
			"1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,average confidence,"+
			"1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,average difficulty,"+
			"TP,TN,FN,FP");
	}

	private void printOutput(ArrayList<TypeOutput> outputList) {
		String destination = "C://firefly//SpectraAnalysis//QuestionTypeOutput.csv";
		BufferedWriter log;

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header

			log.write(header()+"\n");

			for(TypeOutput output: outputList){
				String line= output.toString();
				log.write(line+"\n");
			}

			log.close();
			System.out.println("file written at: "+destination);
		} 
		catch (Exception e) {
			System.out.println("ERROR while processing file:" + destination);
			e.printStackTrace();
		}

	}
}



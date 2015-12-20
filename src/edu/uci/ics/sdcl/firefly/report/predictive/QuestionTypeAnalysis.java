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


	public class QuestionTypeOutput{

		String questionID="";
		boolean bugCovering=false;
		String questionType="";
		int LOCs=0;

		Integer TP=0;
		Integer TN=0;
		Integer FN=0;
		Integer FP=0;
		Double accuracy =0.0;

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

		public void computeAccuracy(){
			accuracy = new Double ((TP.doubleValue()+TN.doubleValue())/(TP.doubleValue()+TN.doubleValue()+FN.doubleValue()+FP.doubleValue()));
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
	public ArrayList<QuestionTypeOutput> buildOutputList(HashMap<String, Microtask> microtaskMap){

		ArrayList<QuestionTypeOutput> list = new ArrayList<QuestionTypeOutput>();

		//Obtain bug covering question list
		PropertyManager manager = PropertyManager.initializeSingleton();
		HashMap<String,String> bugCoveringMap = new HashMap<String,String>();
		String[] listOfBugPointingQuestions = manager.bugCoveringList.split(";");
		for(String questionID:listOfBugPointingQuestions){
			bugCoveringMap.put(questionID,questionID);
		}

		for(Microtask microtask: microtaskMap.values()){
			Vector<Answer> answerList = microtask.getAnswerList();
			QuestionTypeOutput output = new QuestionTypeOutput();
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
			output.computeAccuracy();
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



	public String header(){
		return ("questionID,bugCovering,questionType,LOCs,"+ 
				"1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,average duration,"+
				"1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,average confidence,"+
				"1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,average difficulty,"+
				"TP,TN,FN,FP");
	}

	private void printOutput(ArrayList<QuestionTypeOutput> outputList) {
		String destination = "C://firefly//SpectraAnalysis//QuestionTypeOutput.csv";
		BufferedWriter log;

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header

			log.write(header()+"\n");

			for(QuestionTypeOutput output: outputList){
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

	public class OutputLists{
		String questionType="";
		ArrayList<Double> durationList = new ArrayList<Double>();
		ArrayList<Double> accuracyList = new ArrayList<Double>();
		ArrayList<Integer> confidenceList = new ArrayList<Integer>();
		ArrayList<Integer> difficultyList = new ArrayList<Integer>();
		ArrayList<Integer> LocList = new ArrayList<Integer>();

		public String doubleListToString(ArrayList<Double> list){
			String output="";
			for(Double item : list){
				output = output + "," + item.toString();
			}
			return output;
		}

		public String integerListToString(ArrayList<Integer> list){
			String output="";
			for(Integer item : list){
				output = output + "," + item.toString();
			}
			return output;
		}

	}

	/** The levels of difficulty, confidence, duration, accuracy, and LOC for each question type */
	private HashMap<String, OutputLists> buildLists(ArrayList<QuestionTypeOutput> QuestionTypeOutputList){

		String[] questionTypes =  {"IF_CONDITIONAL", "METHOD_INVOCATION", "VARIABLE_DECLARATION", "FOR_LOOP", "WHILE_LOOP"};

		HashMap<String, OutputLists> outputListsMap = new  HashMap<String, OutputLists>();

		for(String type : questionTypes){
			OutputLists outputLists = new OutputLists();
			outputLists.questionType = type;
			for(QuestionTypeOutput output : QuestionTypeOutputList){
				if(output.questionType.compareTo(type)==0){
					outputLists.durationList.addAll(output.durationList);
					outputLists.confidenceList.addAll(output.confidenceList);
					outputLists.difficultyList.addAll(output.difficultyList);
					outputLists.LocList.add(output.LOCs);
					outputLists.accuracyList.add(output.accuracy);
				}
			}
			outputListsMap.put(type, outputLists);
		}
		return outputListsMap;
	}


	/**Prints a file in which each line is a question type with all answers values for confidence, difficulty, duration, accuracy, and LOC */
	public void printList( HashMap<String, OutputLists> outputListsMap ) {
		String destination = "C://firefly//QuestionTypeAnalysis//QuestionTypeLISTSOutput.csv";
		BufferedWriter log;

		String[] questionTypes =  {"IF_CONDITIONAL", "METHOD_INVOCATION", "VARIABLE_DECLARATION", "FOR_LOOP", "WHILE_LOOP"};

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header

			for(String type: questionTypes){
				OutputLists outputLists = outputListsMap.get(type);
				String line= type +"_Accuracy"+ outputLists.doubleListToString(outputLists.accuracyList);
				log.write(line+"\n");
				line= type +"_Duration"+ outputLists.doubleListToString(outputLists.durationList);
				log.write(line+"\n");
				line= type +"_Confidence"+ outputLists.integerListToString(outputLists.confidenceList);
				log.write(line+"\n");
				line= type +"_Difficulty"+ outputLists.integerListToString(outputLists.difficultyList);
				log.write(line+"\n");
				line= type +"_LOC"+ outputLists.integerListToString(outputLists.LocList);
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
		ArrayList<QuestionTypeOutput> outputList = counter.buildOutputList(microtaskWithLOCS_Map);
		//counter.printOutput(outputList); 

		HashMap<String, OutputLists> map = counter.buildLists(outputList);
		counter.printList(map);

	}
}



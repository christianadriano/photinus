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
import edu.uci.ics.sdcl.firefly.util.BugCoveringMap;
import edu.uci.ics.sdcl.firefly.util.PropertyManager;

public class QuestionTypeAnalysis {

	public class QuestionTypeOutput{

		String bugID="";
		String questionID="";
		boolean bugCovering=false;
		String questionType="";
		int LOCs=0;

		Integer TP=0;
		Integer TN=0;
		Integer FN=0;
		Integer FP=0;
		Double accuracy =0.0;
		
		Double precision;
		Double recall;

		ArrayList<Double> durationList= new ArrayList<Double>();;

		ArrayList<Integer> confidenceList = new ArrayList<Integer>();
		ArrayList<Integer>  difficultyList = new ArrayList<Integer>(); 

		Double averageConfidence = 1.0;
		Double averageDifficulty = 1.0;
		Double averageDuration = 1.0;

		/** Completes duration list so it has 20 items. */
		private void topUpDurationList(){
			int durationListSize = durationList.size();
			if(durationListSize<20){
				while(durationListSize<20){
					this.durationList.add(new Double(-1.0));
					durationListSize++;
				}
			}
		}

		private ArrayList<Integer> topUpIntegerList(ArrayList<Integer> list){
			int listSize = list.size();
			if(listSize<20){
				while(listSize<20){
					list.add(new Integer(-1));
					listSize++;
				}
			}
			return list;
		}
		
		
		public String toString(){

			topUpDurationList();
			this.confidenceList = topUpIntegerList(this.confidenceList);
			this.difficultyList = topUpIntegerList(this.difficultyList);

			return (bugID+","+questionID+","+bugCovering+","+questionType+","+LOCs+ 
					arrayDoubleToString(this.durationList)+","+
					averageDuration.toString()+ 
					arrayIntToString(this.confidenceList)+","+
					averageConfidence.toString()+ 
					arrayIntToString(this.difficultyList)+","+
					averageDifficulty.toString()+
					","+TP+","+TN+","+FN+","+FP+","+accuracy+","+precision+","+recall);
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
		
		private void computePrecision(){
			Double tpD = new Double(this.TP);
			Double fpD =  new Double(this.FP);
			if((tpD+fpD) ==0) 
				this.precision =  0.0;
			else 
				this.precision = tpD/(tpD+fpD);
		}

		private void computeRecall(){
			Double tpD = new Double(this.TP);
			Double fnD =  new Double(this.FN);
			if((tpD+fnD) ==0) 
				this.recall = 0.0;
			else 
				this.recall = tpD/(tpD+fnD);
		}

		

	}	

	/** Map used to control which worker was already sample for each bug. This is necessary to 
	 * avoid that a worker participates in more than one bug. ANOVA analysis requires this for
	 * the independence of samples assumption.
	 */
	private HashMap<String, String> bugWorkerMap = new HashMap<String, String>();

	/** Sets the  lines covered attribute of each microtask
	 *  @param microtaskMap all microtasks with their respective answers
	 *  @param ID_LOC_Map the lines covered by each microtask. These data come from a file.
	 *  */
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
	 * @param microtaskMap all microtasks with their respective answers
	 * @param independentSamples if true means that each worker can only participate in one question type list.
	 */
	public ArrayList<QuestionTypeOutput> buildOutputList(HashMap<String, Microtask> microtaskMap, boolean independentSamples){

		ArrayList<QuestionTypeOutput> list = new ArrayList<QuestionTypeOutput>();

		//Obtain bug covering question list
		HashMap<String, String> bugCoveringMap = BugCoveringMap.initialize();

		for(Microtask microtask: microtaskMap.values()){
			Vector<Answer> answerList = microtask.getAnswerList();
			QuestionTypeOutput output = new QuestionTypeOutput();
			output.bugID = microtask.getFileName();
			output.questionID = microtask.getID().toString();
			output.questionType = microtask.getQuestionType();
			output.LOCs = microtask.getLOC_CoveredByQuestion();

			if(bugCoveringMap.containsKey(output.questionID))
				output.bugCovering = true;
			else
				output.bugCovering = false;

			for(Answer answer : answerList){

				if((!independentSamples) || (!isWorkerInSamples(answer.getWorkerId()))){
					//Either is not independentSamples or it is, then the worker should have not participated yet in the sampling
					output.confidenceList.add(answer.getConfidenceOption());
					output.difficultyList.add(answer.getDifficulty());
					output.durationList.add(new Double(answer.getElapsedTime()));
					output = this.computeConfusionMatrix(output, answer); //compute TP, FP, FN, TN
				}

			}//for loop Answerlist
			output.averageDifficulty = computeAverageInteger(output.difficultyList);
			output.averageConfidence = computeAverageInteger(output.confidenceList);
			output.averageDuration = computeAverageDouble(output.durationList);
			output.computeAccuracy();
			output.computePrecision();
			output.computeRecall();
			list.add(output);
		}
		return list;
	}

	private boolean isWorkerInSamples(String workerID){

		if(this.bugWorkerMap.containsKey(workerID)){
			return true;
		}
		else{
			bugWorkerMap.put(workerID, workerID); 
			return false;
		}
	}

	private QuestionTypeOutput computeConfusionMatrix(QuestionTypeOutput output, Answer answer){

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
		return output;
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
		return ("bugID,questionID,bugCovering,questionType,LOCs,"+ 
				"1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,average duration,"+
				"1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,average confidence,"+
				"1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,average difficulty,"+
				"TP,TN,FN,FP, accuracy, precision, recall");
	}

	private void printOutput(ArrayList<QuestionTypeOutput> outputList) {
		String destination = "C://firefly//SpectraAnalysis//QuestionTypeOutput_independent.csv";
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
					for(int i=0;i<output.durationList.size();i++){
						outputLists.durationList.add(output.durationList.get(i));
						outputLists.confidenceList.add(output.confidenceList.get(i));
						outputLists.difficultyList.add(output.difficultyList.get(i));
						outputLists.LocList.add(output.LOCs); //repeats the value to the duration and confidence lists.
						outputLists.accuracyList.add(output.accuracy); //repeats the value
					}
				}
			}
			outputListsMap.put(type, outputLists);
		}
		return outputListsMap;
	}


	/**Prints a file in which each line is a question type with all answers values for confidence, difficulty, duration, accuracy, and LOC */
	public void printList( HashMap<String, OutputLists> outputListsMap ) {
		String destination = "C://firefly//QuestionTypeAnalysis//QuestionTypeLISTSOutput_independent.csv";
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


	public HashMap<String,Microtask> getMicrotasks(boolean filtered){

		FileSessionDTO sessionDTO = new FileSessionDTO();
		HashMap<String,Microtask> microtaskMap =  (HashMap<String, Microtask>) sessionDTO.getMicrotasks();


		if(filtered){
			//Produce the list of filters
			ArrayList<FilterCombination> filterList = FilterGenerator.generateAnswerFilterCombinationList();
			FilterCombination combination =  filterList.get(0);
			Filter filter = combination.getFilter();
			HashMap<String, Microtask> filteredMicrotaskMap = (HashMap<String, Microtask>) filter.apply(microtaskMap);

			return filteredMicrotaskMap;
		}
		else {
			return microtaskMap;
		}
	}

	public static void main(String[] args){	

		QuestionTypeAnalysis counter = new QuestionTypeAnalysis();
		HashMap<String,Microtask> microtaskMap = counter.getMicrotasks(false); //DO NOT FILTER

		QuestionLOCs lineCounter = new QuestionLOCs();
		HashMap<String, Integer>  IDLOCMap = lineCounter.loadList();
		HashMap<String,Microtask> microtaskWithLOCS_Map = counter.addQuestionSizeData(microtaskMap,  IDLOCMap);
		ArrayList<QuestionTypeOutput> outputList = counter.buildOutputList(microtaskWithLOCS_Map,true); 
		counter.printOutput(outputList); 

		//Build lists of question type, duration, LOC, etc., to run ANOVA analysis
		//HashMap<String, OutputLists> map = counter.buildLists(outputList);
		//counter.printList(map);

	}
}



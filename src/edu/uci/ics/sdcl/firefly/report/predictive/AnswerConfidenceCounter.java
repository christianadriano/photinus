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
import edu.uci.ics.sdcl.firefly.report.predictive.spectra.QuestionLinesData;
import edu.uci.ics.sdcl.firefly.report.predictive.spectra.QuestionLOCs;
import edu.uci.ics.sdcl.firefly.util.PropertyManager;

public class AnswerConfidenceCounter {

	ArrayList<Output> correctnessList;
	HashMap<String,String> bugCoveringMap;

	HashMap<String,Integer> countCombinations;

	public class Output{

		int TP=0;
		int TN=0;
		int FN=0;
		int FP=0;

		double duration=0.0;

		int confidence=-1;
		int difficulty=-1;

		int explanationSize=-1;
		public int correct;
		public String filename;
		public String questionID;
		public String questionType;
		public int size;

		public String toString(){

			return (filename+","+questionID+","+confidence+","+difficulty+","+explanationSize+","+TP+","+TN+","+FN+","+FP+","+correct+","+questionType+","+size);
		}
	}


	public AnswerConfidenceCounter(){
		//Obtain bug covering question list
		PropertyManager manager = PropertyManager.initializeSingleton();
		this.bugCoveringMap = new HashMap<String,String>();
		String[] listOfBugPointingQuestions = manager.bugCoveringList.split(";");
		for(String questionID:listOfBugPointingQuestions){
			bugCoveringMap.put(questionID,questionID);
		}

		//Initialize datastructures
		this.correctnessList = new ArrayList<Output>();
		this.countCombinations = new HashMap<String,Integer> ();
		for(int confidence=0;confidence<6;confidence++)
			for(int difficulty=1;difficulty<6;difficulty++){
				String key ="["+confidence+";"+difficulty+"]";
				this.countCombinations.put(key, new Integer(0));
			}
	}



	/** Builds the three maps of answer duration, which can be printed by 
	 * method 
	 * @param microtaskMap
	 */
	public void buildMaps(HashMap<String, Microtask> microtaskMap){

		for(Microtask microtask: microtaskMap.values()){
			Vector<Answer> answerList = microtask.getAnswerList();
			for(Answer answer : answerList){
				Output output = new Output();
				output.confidence = answer.getConfidenceOption();
				output.difficulty = answer.getDifficulty();
				output = checkCorrectness(microtask.getID().toString(),answer.getOption(),output);
				output.filename = microtask.getFileName();
				output.questionID = microtask.getID().toString();
				output.questionType = microtask.getQuestionType();
				output.explanationSize = answer.getExplanation().length();
				this.correctnessList.add(output);
			}
		}

		for(Output output: this.correctnessList){

			String key ="["+output.confidence+";"+output.difficulty+"]";
			Integer correctCount = this.countCombinations.get(key);
			correctCount = correctCount + output.correct;
			this.countCombinations.put(key, correctCount);
		}
	}



	private Output checkCorrectness(String questionID, String answerOption, Output output) {

		if(this.bugCoveringMap.containsKey(questionID)){
			if(answerOption.compareTo(Answer.YES)==0)
				output.TP++;
			else
				if(answerOption.compareTo(Answer.NO)==0) //Ignores IDK	
					output.FN++;
		}
		else{
			if(answerOption.compareTo(Answer.NO)==0)
				output.TN++;
			else
				if(answerOption.compareTo(Answer.YES)==0)	 //Ignores IDK				
					output.FP++;
		}
		
		if(output.TP==0)
			output.correct = output.TN;
		else
			output.correct = output.TP;
		
		return output;
	}


	private String getHeader(){
		return "filename,questionID,confidence,difficulty,explanationSize,TP,TN,FN,FP,correct";
	}


	public void printConfidenceMap(){	

		String destination = "C://firefly//answerExplanation.csv";
		BufferedWriter log;

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header

			log.write(getHeader()+"\n");

			for(Output output: this.correctnessList){
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

	public void printCombinationMap(){
		for(String key: this.countCombinations.keySet()){
			Integer count = this.countCombinations.get(key);
			System.out.println(key+","+count.toString());
		}
	}

	
	
	public static void main(String[] args){

		
		
		AnswerConfidenceCounter counter = new AnswerConfidenceCounter();
		FileSessionDTO sessionDTO = new FileSessionDTO();
		
		//Produce the list of filters
		ArrayList<FilterCombination> filterList = FilterGenerator.generateAnswerFilterCombinationList();
		FilterCombination combination =  filterList.get(0);
		Filter filter = combination.getFilter();
	
		HashMap<String,Microtask> microtaskMap =  (HashMap<String, Microtask>) sessionDTO.getMicrotasks();
		
		HashMap<String, Microtask> filteredMicrotaskMap = (HashMap<String, Microtask>) filter.apply(microtaskMap);
		
			counter.buildMaps(filteredMicrotaskMap);
		counter.printConfidenceMap();

		//counter.printCombinationMap();
	}

}

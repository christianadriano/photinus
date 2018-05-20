package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMap;

public abstract class Consensus {
	
	protected HashMap<String, Double> questionYESCountMap;
	
	protected HashMap<String, Double> questionNoCountMap;
	
	protected AnswerData data;
	
	public Consensus() {}
	
	protected void initializeCountMaps(boolean isAbsoluteVoting) {
		if(isAbsoluteVoting){
			this.questionYESCountMap = this.computeAbsoluteNumberOfYES(data.getAnswerMap());
			this.questionNoCountMap = this.computeAbsoluteNumberOfNO(data.getAnswerMap());		
		}
		else{
			this.questionYESCountMap = this.computeRelativeNumberOfYES(data.getAnswerMap());
			this.questionNoCountMap = this.computeRelativeNumberOfNO(data.getAnswerMap());
		}
	}
	
	/**
	 * @param questionOptionsMap questionID and list of answer options (YES, NO, IDK)
	 * @return a map <questionID, number of YES's>
	 */
	private HashMap<String, Double> computeAbsoluteNumberOfYES(HashMap<String, ArrayList<String>> questionOptionsMap){

		HashMap<String, Double> questionYESCountMap= new HashMap<String, Double>(); 

		for(String questionID: questionOptionsMap.keySet()){
			ArrayList<String> optionList = questionOptionsMap.get(questionID);
			int counter = 0;
			for(String option : optionList){
				//System.out.println(option);
				if(option.compareTo(Answer.YES)==0)
					counter++;
			}
			//System.out.println("questionID: "+ questionID+"counter:"+counter);
			questionYESCountMap.put(questionID, new Double(counter));
		}
		return questionYESCountMap;
	}


	/**
	 * @param questionOptionsMap questionID and list of answer options (YES, NO, IDK)
	 * @return a map <questionID, number of YES's divided by total answer for the question>
	 */
	private HashMap<String, Double> computeRelativeNumberOfYES(HashMap<String, ArrayList<String>> questionOptionsMap){

		HashMap<String, Double> questionYESCountMap= new HashMap<String, Double>(); 

		for(String questionID: questionOptionsMap.keySet()){
			ArrayList<String> optionList = questionOptionsMap.get(questionID);
			int counter = 0;
			for(String option : optionList){
				//System.out.println(option);
				if(option.compareTo(Answer.YES)==0)
					counter++;
			}
			//System.out.println("questionID: "+ questionID+"counter:"+counter);
			questionYESCountMap.put(questionID, new Double(counter/optionList.size()));
		}
		return questionYESCountMap;
	}


	/**
	 * @param questionOptionsMap questionID and list of answer options (YES, NO, IDK)
	 * @return a map <questionID, number of NO's>
	 */
	private HashMap<String, Double> computeAbsoluteNumberOfNO(HashMap<String, ArrayList<String>> questionOptionsMap){

		HashMap<String, Double> questionNOCountMap= new HashMap<String, Double>(); 

		for(String questionID: questionOptionsMap.keySet()){
			ArrayList<String> optionList = questionOptionsMap.get(questionID);
			int counter = 0;
			for(String option : optionList){
				//System.out.println(option);
				if(option.compareTo(Answer.NO)==0)
					counter++;
			}
			//System.out.println("questionID: "+ questionID+"counter:"+counter);
			questionNOCountMap.put(questionID, new Double(counter));
		}
		return questionNOCountMap;
	}

	/**
	 * @param questionOptionsMap questionID and list of answer options (YES, NO, IDK)
	 * @return a map <questionID, number of NO's divided by total number of answers for the question>
	 */
	private HashMap<String, Double> computeRelativeNumberOfNO(HashMap<String, ArrayList<String>> questionOptionsMap){

		HashMap<String, Double> questionNOCountMap= new HashMap<String, Double>(); 

		for(String questionID: questionOptionsMap.keySet()){
			ArrayList<String> optionList = questionOptionsMap.get(questionID);
			int counter = 0;
			for(String option : optionList){
				//System.out.println(option);
				if(option.compareTo(Answer.NO)==0)
					counter++;
			}
			//System.out.println("questionID: "+ questionID+"counter:"+counter);
			questionNOCountMap.put(questionID, new Double(counter/optionList.size()));
		}
		return questionNOCountMap;
	}



	public abstract String getName();
	
	public abstract void setCalibration(Double calilbration);
	
	public abstract Double getCalibration();
	
	public abstract int getQuestionsBelowMinimalAnswers();
	
	public abstract void setData(AnswerData data);
	
	public abstract void setMinimumAnswersPerQuestion(double minimum);
	
	public abstract void setIncludeIDK(boolean includeIDK);
	
	public abstract Double scoreQuestions(AnswerData data);
	
	public abstract Double computeSignalStrength(AnswerData data);
	
	public abstract Double computeNumberOfWorkers(AnswerData data);

	public abstract Double getMinimumNumberYESAnswersThatLocatedFault();

	public abstract Double getTruePositives();
	
	public abstract Double getTrueNegatives();
	
	public abstract Double getFalsePositives();
	
	public abstract Double getFalseNegatives();

	public abstract HashMap<String, Integer> getTruePositiveLines(
			HashMap<String, QuestionLinesMap> lineMapping);
		 
	public abstract HashMap<String, Integer> getTrueNegativeLines(
			HashMap<String, QuestionLinesMap> lineMapping);

	public abstract HashMap<String, Integer> getFalsePositiveLines(
			HashMap<String, QuestionLinesMap> lineMapping);

	public abstract HashMap<String, Integer> getFalseNegativeLines(
			HashMap<String, QuestionLinesMap> lineMapping);
	
	public abstract HashMap<String, Integer> getNearPositiveLines(
			HashMap<String, QuestionLinesMap> lineMapping);

	/**
	 *  Lines considered correctly positive cannot be considered again as False positives 
	 * 
	 * @param correctPositiveMap any lines that should be considered as correctly flagged as positive (e.g., True or Near positive lines)
	 * @param falsePositiveMap
	 * @return
	 */
	public static HashMap<String,Integer> removeFalsePositiveDuplications(HashMap<String, Integer> correctPositiveMap,
			HashMap<String, Integer> falsePositiveMap ){
		if(correctPositiveMap!=null && falsePositiveMap!=null){
			HashMap<String, Integer> revisedFalsePositiveMap = new HashMap<String,Integer>();
			for (Map.Entry<String, Integer> entry : falsePositiveMap.entrySet()) {
				if(!correctPositiveMap.containsKey(entry.getKey())){
					revisedFalsePositiveMap.put(entry.getKey(),entry.getValue());
				}
			}
			return revisedFalsePositiveMap;
		}
		else
			return falsePositiveMap;
	}

	public abstract HashMap<String, HashMap<String, Integer>> getNearPositiveLinesQuestions(
			 HashMap<String, QuestionLinesMap> lineMapping);
	
}

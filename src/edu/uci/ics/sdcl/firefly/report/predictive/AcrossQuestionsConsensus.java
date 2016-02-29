package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMap;
import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMapLoader;

/**
 * 
 * WHAT ARE THRESHOLDS?
 * The Across-Questions Consensus method calculates the number of YES's answers that enable a fault to be located.
 * This number is called "Threshold". 
 * 
 * The threshold is the number of YES's that is larger than the one received by 
 * any non-bug-Covering questions and still smaller or equal to at
 * least one bug-covering question.
 * 
 * HOW MANY THRESHOLDS? WHY?
 * There are two possible thresholds with two different goals.
 * 
 * Goal of FirstThrehold:  maximize the true positives without producing any zero false positives, even at the cost of 
 * false negatives (bug covering questions below threshold). This goal recognizes that even in face of false negatives, 
 * we still can locate the fault, hence we want to minimize cost (false positives).
 * 
 * Goal of SecondThrehold: obtain at least one true positive with minimal number of false positives. This goal recognizes that 
 * the crowd did not draw a consensus over the fault, but at least two possible faults.
 *
 *HOW ARE THRESHOLDS CALCULATED?
 * FirstThreshold - Threshold at which most bug covering questions are above it and any non-bug covering question is above it.
 *  
 * If the maxNumber of YES's for Bug-Covering is not larger than the one for Non-Bug-Covering,
 * then returns the difference of YES's between the tow top Bug-Covering and Non-Bug-Covering questions.
 * This difference will be negative and show how many more YES's answers were necessary for the Bug-Covering to be
 * unambiguously distinguished from Non-Bug-Covering questions.
 *
 * Second Threshold - Threshold at which at least one bug covering question is above it and the fewest number of non-bug covering 
 * questions are above it.
 * 
 * There will be only one active thresholds, which will have the following attributes also calculated by this class:
 * - Number of True Positives
 * - Number of True Negatives
 * - Number of False Positives
 * - Number of False Negatives
 * - Strength of Signal
 * - Number of workers necessary for Threshold
 * 
 * @author adrianoc
 *
 */
public class AcrossQuestionsConsensus extends Consensus{

	private String name = "Across-questions consensus";
	
	private Integer maxYES=0;

	private HashMap<String, Integer> questionYESCountMap;

	AnswerData data;

	private Integer threshold=null;
	
	/** The number of top ranking questions which will be considered to locate a fault *
	 * Default is 2 */
	private int calibration=2; 

	public AcrossQuestionsConsensus(){
		super();
	}

	public String getName(){
		return this.name;
	}

	@Override
	public Boolean computeSignal(AnswerData data){
		
		this.data = data;
	
		this.questionYESCountMap = this.computeNumberOfAnswers(Answer.YES);
		threshold =  this.computeThreshold(this.calibration);
		
		if(threshold>0)
			return true;
		else
			return false;
	}
	
	@Override
	public void setCalibration(int calibrationLevel){
		this.calibration = calibrationLevel;
	}

	@Override
	public int getCalibration(){
		return this.calibration;
	}
	
	@Override
	public void setData(AnswerData data){
		this.data = data;
	}
	
	@Override
	/**
	 * @return if the threshold is positive, then returns the difference between Maximum YES's and the Threshold. If 
	 * threshold is zero, then return -1.0
	 */
	public Integer computeSignalStrength(AnswerData data) {

		if(this.threshold==null)
			this.computeSignal(data);

		if(threshold>0){	
			return this.maxYES - this.threshold;
		}
		else
			return -1;
	}

	@Override
	public Integer computeNumberOfWorkers(AnswerData data) {
		int maxAnswers=0; 
		for(ArrayList<String> answerList :  data.answerMap.values()){
			if(answerList.size()>maxAnswers)
				maxAnswers = answerList.size();
		}
		return maxAnswers;
	}

	
	@Override
	public Integer getTruePositives() {

		if(this.threshold<=0)
			return 0;
		else{
			int count=0;
			for(String questionID: this.questionYESCountMap.keySet()){
				if(data.bugCoveringMap.containsKey(questionID)){
					Integer yesCount = this.questionYESCountMap.get(questionID);
					if(yesCount!=null && yesCount>=this.threshold){
						count++;
					}
				}
			}
			return count;
		}
	}

	@Override
	public Integer getTrueNegatives() {

		if(this.threshold<=0)
			return 0;
		else{
			int count=0;
			for(String questionID: this.questionYESCountMap.keySet()){
				if(!data.bugCoveringMap.containsKey(questionID)){
					Integer yesCount = this.questionYESCountMap.get(questionID);
					if(yesCount<this.threshold){
						count++;
					}
				}
			}
			return count;
		}
	}

	@Override
	public Integer getFalsePositives() {

		int count=0;
		for(String questionID: this.questionYESCountMap.keySet()){
			if(!data.bugCoveringMap.containsKey(questionID)){
				Integer yesCount = this.questionYESCountMap.get(questionID);
				if(yesCount>=this.threshold || this.threshold<=0){
					count++;
				}
			}
		}
		return count;
	}

	@Override
	public Integer getFalseNegatives() {

		int count=0;
		for(String questionID: this.questionYESCountMap.keySet()){
			if(data.bugCoveringMap.containsKey(questionID)){
				Integer yesCount = this.questionYESCountMap.get(questionID);
				if(yesCount<this.threshold || this.threshold<=0){
					count++;
				}
			}
		}
		return count;
	}

  
	//----------------------------------------------------------------------------------------------------------

	/**
	 * @param answerOption the answer option (YES, NO, IDK)
	 * @return a map <questionID, number of answer of that option>
	 */
	private HashMap<String, Integer> computeNumberOfAnswers(String answerOption){

		HashMap<String, Integer> countMap = new HashMap<String, Integer>(); 

		for(String questionID: data.answerMap.keySet()){
			ArrayList<String> optionList = data.answerMap.get(questionID);
			int counter = 0;
			for(String option : optionList){
				//System.out.println(option);
				if(option.compareTo(answerOption)==0)
					counter++;
			}
			//System.out.println("counter:"+counter);
			countMap.put(questionID, new Integer(counter));
		}
		return countMap;
	}

	private Integer computeThreshold(int calibrationLevel){
		
		if(this.questionYESCountMap.isEmpty())
			return -1;
		
		TreeMap<String, Integer> sortedYESMap = this.sortByValue(this.questionYESCountMap);
		this.maxYES = this.questionYESCountMap.get(sortedYESMap.firstKey());
				
		Integer yesCount_at_Level=0;
		int i=1;
		for(String questionID: sortedYESMap.navigableKeySet()){
			Integer yesCount = this.questionYESCountMap.get(questionID);
			//System.out.println("questionID: "+questionID+":"+yesCount);
			i++;
			if(i>calibrationLevel) {
				yesCount_at_Level = new Integer(yesCount);
				break;
			}
		}
		
		if(yesCount_at_Level>0)
			return yesCount_at_Level;
		else
			return -1;
	}
	
	private  TreeMap<String, Integer> sortByValue(HashMap<String, Integer> map) {
		ValueComparator vc =  new ValueComparator(map);
		TreeMap<String,Integer> sortedMap = new TreeMap<String,Integer>(vc);
		sortedMap.putAll(map);
		return sortedMap;
	}

	public class ValueComparator implements Comparator<String> {
		 
	    Map<String, Integer> map;
	 
	    public ValueComparator(Map<String, Integer> base) {
	        this.map = base;
	    }
	 
	    public int compare(String a, String b) {
	        if (map.get(a) >= map.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys 
	    }
	}




	//--------------------------------------------------------------------------------------------------
	

	
	@Override
	public HashMap<String, Integer> getTruePositiveLines(HashMap<String, QuestionLinesMap> lineMapping){
	
		if(this.threshold<=0)
			return null; //Means that bug was not found
		else{
			HashMap<String, Integer> map =  new HashMap<String,Integer>();
			for(String questionID: this.questionYESCountMap.keySet()){
				if(data.bugCoveringMap.containsKey(questionID)){
					Integer yesCount = this.questionYESCountMap.get(questionID);
					if(yesCount!=null && yesCount>=this.threshold && this.threshold>0){
						QuestionLinesMap questionLinesMap =lineMapping.get(questionID);
						if(questionLinesMap==null || questionLinesMap.faultyLines==null) System.err.println("No mapping for questionID: "+questionID);
						map = loadLines(map,questionLinesMap.faultyLines);
					}
				}
			}
			return map;
		}
	}
	
	@Override
	public HashMap<String, Integer> getNearPositiveLines(HashMap<String, QuestionLinesMap> lineMapping){
	
		if(this.threshold<=0)
			return null; //Means that bug was not found
		else{
			HashMap<String, Integer> map =  new HashMap<String,Integer>();
			for(String questionID: this.questionYESCountMap.keySet()){
				if(data.bugCoveringMap.containsKey(questionID)){
					Integer yesCount = this.questionYESCountMap.get(questionID);
					if(yesCount!=null && yesCount>=this.threshold && this.threshold>0){
						QuestionLinesMap questionLinesMap =lineMapping.get(questionID);
						map = loadLines(map,questionLinesMap.nearFaultyLines);
					}
				}
			}
			return map;
		}
	}
	
	@Override
	public HashMap<String, Integer> getFalsePositiveLines(HashMap<String, QuestionLinesMap> lineMapping){
	 
			HashMap<String, Integer> map =  new HashMap<String,Integer>();
			for(String questionID: this.questionYESCountMap.keySet()){
				if(!data.bugCoveringMap.containsKey(questionID)){
					Integer yesCount = this.questionYESCountMap.get(questionID);
					if(yesCount!=null && yesCount>=this.threshold && this.threshold>0){
						QuestionLinesMap questionLinesMap =lineMapping.get(questionID);
						if(questionLinesMap.nonFaultyLines==null) 
							System.err.println("QuestionID: "+questionID +" is not failure related, but has a bug at same line");
						else
							map = loadLines(map,questionLinesMap.nonFaultyLines);
					}
				}
			}
			return map;
	}
	
	@Override
	public HashMap<String, Integer> getFalseNegativeLines(HashMap<String, QuestionLinesMap> lineMapping){
	 
			HashMap<String, Integer> map =  new HashMap<String,Integer>();
			for(String questionID: this.questionYESCountMap.keySet()){
				if(data.bugCoveringMap.containsKey(questionID)){
					Integer yesCount = this.questionYESCountMap.get(questionID);
					if(yesCount<this.threshold || this.threshold<=0){
						QuestionLinesMap questionLinesMap =lineMapping.get(questionID);
						map = loadLines(map,questionLinesMap.faultyLines);
					}
				}
			}
			return map;
	}
	
	@Override
	public HashMap<String, Integer> getTrueNegativeLines(HashMap<String, QuestionLinesMap> lineMapping){
	 
			HashMap<String, Integer> map =  new HashMap<String,Integer>();
			for(String questionID: this.questionYESCountMap.keySet()){
				if(!data.bugCoveringMap.containsKey(questionID)){
					Integer yesCount = this.questionYESCountMap.get(questionID);
					if(yesCount<this.threshold || this.threshold<=0){
						QuestionLinesMap questionLinesMap =lineMapping.get(questionID);
						map = loadLines(map,questionLinesMap.nonFaultyLines);
					}
				}
			}
			return map;
	}
	
 		
	private HashMap<String, Integer> loadLines(HashMap<String, Integer> map, HashMap<String, String> questionLineMapping) {
		
		HashMap<String, Integer> newMap = new HashMap<String, Integer>();
		
		Iterator<String> iter = questionLineMapping.keySet().iterator();
		while(iter.hasNext()){
			String line = iter.next();
			if(map.containsKey(line)){
				Integer count = map.get(line);
				count++;
				newMap.put(line, count);
			}
			else
				newMap.put(line, 1);
		}
		
		return newMap;
	}

	//--------------------------------------------------------------------------------------------------------------
	
	 

	public static void main(String[] args){

		HashMap<String,String> bugCoveringMap = new HashMap<String,String>();

		bugCoveringMap.put("1","1");//1 yes
		bugCoveringMap.put("3","3");//4 yes's

		HashMap<String, ArrayList<String>> answerMap = new HashMap<String, ArrayList<String>>();

		ArrayList<String> answerList = new ArrayList<String>();//2 yes's
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerList.add("IDK");
		answerList.add(Answer.YES);
		answerMap.put("0",answerList);

		answerList = new ArrayList<String>();//1 yes
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerMap.put("1",answerList);

		answerList = new ArrayList<String>();//3 yes's
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerMap.put("2",answerList);

		answerList = new ArrayList<String>();//4 yes's
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerMap.put("3",answerList);

		String hitFileName = "HIT00_0";

		AnswerData data = new AnswerData(hitFileName,answerMap,bugCoveringMap,4,4);

		AcrossQuestionsConsensus predictor = new AcrossQuestionsConsensus();

		System.out.println("expected: true, actual: "+ predictor.computeSignal(data).toString());
		System.out.println("expected: 3, actual: "+ predictor.getThreshold().toString());

		
	}

	@Override
	public Integer getThreshold(){
		return this.threshold;
	}


	//----------------------------------------------------------------------------------------------------------------
	
		
}

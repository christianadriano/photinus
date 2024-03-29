package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMap;
import edu.uci.ics.sdcl.firefly.report.predictive.voting.Scoring;

/**
 * Each question has a vote count which is basically Number of YES's minus the Number of NO's.
 * 
 * @author adrianoc
 *
 */
public class WithinQuestionConsensus extends Consensus{

	public String name = "Within-question consensus";

	private String scoringType = Scoring.ABSOLUTE_YES_Consensus; //default

	private boolean includeIDK = true;

	private Double minimumAnswersPerQuestion = 0.0;

	private Integer questionsBelowMinimalAnswers = 0;

	/** Difference between number of YES's and NO's. Default is 1.*/
	private Double calibration=0.0;

	private boolean isAbsoluteVoting=true;

	private Scoring scoringQuestions;


	/**
	 * 
	 * @param isAbsoluteVoting true counts the number of YES, false counts the number of YES divided by the total number of answers 
	 */
	public WithinQuestionConsensus(boolean isAbsoluteVoting){
		super();
		this.isAbsoluteVoting=isAbsoluteVoting;
		this.scoringQuestions = new Scoring();
	}

	/**
	 * 
	 * @param type one of the two consensus types available in the class (see static attributes)
	 * @param minimumYesCount null if type if Balance_YES_NO_Consensus, otherwise provide a non-negative integer.
	 * @param isAbsoluteVoting true counts the number of YES, false counts the number of YES divided by the total number of answers 
	 */
	public WithinQuestionConsensus(String type, Double calibration,boolean isAbsoluteVoting){
		super();
		this.calibration = calibration;
		this.isAbsoluteVoting = isAbsoluteVoting;
		this.scoringType = type;
		this.scoringQuestions = new Scoring();

		this.name = this.name + " " + type + "_" + this.calibration;;
	}

	/**
	 * 
	 * @param type one of the two consensus types available in the class (see static attributes)
	 * @param minimumYesCount null if type if Balance_YES_NO_Consensus, otherwise provide a non-negative integer.
	 * @param isAbsoluteVoting true counts the number of YES, false counts the number of YES divided by the total number of answers 
	 */
	public WithinQuestionConsensus(String type, 
			Double calibration, Double minimumAnswersPerQuestion, 
			boolean includeIDK, boolean isAbsoluteVoting){
		super();
		this.calibration = calibration;
		this.scoringType = type;

		this.minimumAnswersPerQuestion = minimumAnswersPerQuestion;
		this.includeIDK = includeIDK;
		this.scoringQuestions = new Scoring();
		
		this.name = this.name + " " + type + "_" + this.calibration;;
	}

	@Override
	public void setCalibration(Double calibration){
		this.calibration = calibration;
	}

	@Override
	public Double getCalibration(){
		return this.calibration;
	}

	@Override
	public void setData(AnswerData data){
		this.data = data;
	}

	/** The number of bug covering questions that were actually found */
	@Override
	public Double scoreQuestions(AnswerData data){
		this.data = data;
		
		this.initializeCountMaps(this.isAbsoluteVoting);

		if(this.scoringType.matches(Scoring.BALANCE_YES_NO_Consensus)){
			this.voteMap = this.scoringQuestions.scoreMajorityVote(questionYESCountMap,
					questionNoCountMap); 
		}
		else {
			if(this.scoringType.matches(Scoring.ABSOLUTE_YES_Consensus)){
				this.voteMap = this.scoringQuestions.scoreAbsolutePositiveVote(questionYESCountMap);
			}
			else 
				if(this.scoringType.matches(Scoring.PROPORTION_YES_NO_Consensus)) {
					this.voteMap = this.scoringQuestions.scoreProportionalVote(questionYESCountMap,
							questionNoCountMap); 
				}
		}
		return this.computeTruePositives();

	}


	@Override
	public Double computeSignalStrength(AnswerData data){
		if(voteMap==null)
			this.scoreQuestions(data);

		if(getTruePositives()==0)
			return -1.0;

		Double extraVotes=0.0;

		for(String questionID: data.bugCoveringMap.keySet()){

			Double vote = voteMap.get(questionID);
			if(vote!=null && vote>this.calibration){
				extraVotes = extraVotes + vote-this.calibration;
			}
		}

		return extraVotes;
	}


	@Override
	public Double computeNumberOfWorkers(AnswerData data) {
		double maxAnswers=0; 
		for(ArrayList<String> answerList :  data.answerMap.values()){
			if(answerList.size()>maxAnswers)
				maxAnswers = answerList.size();
		}
		return maxAnswers;
	}

	@Override
	/**
	 * 
	 * @return number of YES of the bug covering question that has the smallest 
	 * positive vote. If the fault was not found returns -1.
	 */
	public Double getMinimumNumberYESAnswersThatLocatedFault(){

		if (voteMap==null){
			if(this.scoreQuestions(data)==0)
				return -1.0;
		}

		double smallestVote = this.computeNumberOfWorkers(data); //starts with the maximum possible.
		String questionIDSmallestVote=null;

		//find the number of YES of the bug covering question that has the smallest positive vote
		for(String questionID: this.voteMap.keySet()){
			if(data.bugCoveringMap.containsKey(questionID)){
				Double vote = this.voteMap.get(questionID);
				if(vote!=null && vote>this.calibration && vote<smallestVote){
					smallestVote = vote;
					questionIDSmallestVote = new String(questionID);
				}
			}
		}

		if(questionIDSmallestVote!=null)
			return this.questionYESCountMap.get(questionIDSmallestVote);
		else 
			return -1.0;
	}

	/** Same result as function compute */
	@Override
	public Double getTruePositives(){
		if(this.voteMap!=null)
			return this.computeTruePositives();
		else
			return null;
	}

	@Override
	public Double getFalsePositives(){
		if(this.voteMap!=null)
			return computeFalsePositives();
		else
			return null;
	}

	@Override
	public Double getFalseNegatives(){
		if(this.voteMap!=null)
			return computeFalseNegatives();
		else
			return null;
	}

	@Override
	public Double getTrueNegatives(){
		if(this.voteMap!=null)
			return computeTrueNegatives();
		else
			return null;
	}

	/** Relies on matching the bugCovering list with the list of questions
	 * sent, which should be only the ones pertaining one HIT (e.g., HIT01_8).
	 * @return
	 */
	private Integer getNumberBugCoveringQuestions(){
		if(this.voteMap!=null)
			return countBugCovering();
		else
			return null;
	}

	@Override
	public String getName(){
		return this.name;
	}



	//----------------------------------------------------------------------------------------------------------

	
	/** Check if question has at list minimum answers to evaluate the rule
	 * The minimum can come from the calibration level, e.g., Y - N >4 , then need at least 4 answers to compute. 
	 * @param minimum number of answers
	 * @param includeIDK if true, means that we should count the IDK towards the minimum number of answers, false otherwise 
	 * @return
	 */
	private Boolean checkIfQuestionReceivedMinimumNumberOfAnswers(String questionID){

		if(this.minimumAnswersPerQuestion==0){
			return true;
		}
		else{
			ArrayList<String> answerList = data.answerMap.get(questionID);
			if(answerList.size()>=this.minimumAnswersPerQuestion){
				if(!this.includeIDK){
					double IDKCount = data.countOption(answerList, Answer.I_DONT_KNOW);

					if(answerList.size()-IDKCount<this.minimumAnswersPerQuestion){
						System.out.print("["+answerList.size()+"],"+IDKCount+" / ");
					}

					if((answerList.size()-IDKCount)>=this.minimumAnswersPerQuestion){
						return true;
					}
					else{
						this.questionsBelowMinimalAnswers++;
						return false;
					}
				}
				else{
					if((answerList.size())>=this.minimumAnswersPerQuestion){
						return true;
					}
					else{
						this.questionsBelowMinimalAnswers++;
						return false;
					}

				}
			}
			else
				return false;
		}
	}

	private Double computeTruePositives() {

		Double quantityTruePositives=0.0;

		for(String questionID: voteMap.keySet()){
			if(data.bugCoveringMap.containsKey(questionID)){
				Double vote = voteMap.get(questionID);
				if(vote!=null && vote>this.calibration && this.checkIfQuestionReceivedMinimumNumberOfAnswers(questionID))
					quantityTruePositives = quantityTruePositives +1;
			}
		}
		return quantityTruePositives;
	}


	private Double computeFalsePositives() {

		Double quantityFalsePositives=0.0;

		for(String questionID: voteMap.keySet()){
			if(!data.bugCoveringMap.containsKey(questionID)){
				Double vote = voteMap.get(questionID);
				if(vote!=null && vote>this.calibration && this.checkIfQuestionReceivedMinimumNumberOfAnswers(questionID))
					quantityFalsePositives = quantityFalsePositives +1;
			}
		}
		return quantityFalsePositives;
	}

	private Double computeFalseNegatives() {

		Double quantityFalseNegatives=0.0;

		for(String questionID: voteMap.keySet()){
			if(data.bugCoveringMap.containsKey(questionID)){
				Double vote = voteMap.get(questionID);
				if(vote!=null && vote<=this.calibration && this.checkIfQuestionReceivedMinimumNumberOfAnswers(questionID))
					quantityFalseNegatives = quantityFalseNegatives +1;
			}
		}
		return quantityFalseNegatives;
	}

	private Double computeTrueNegatives() {

		Double quantityTrueNegatives=0.0;

		for(String questionID: voteMap.keySet()){
			if(!data.bugCoveringMap.containsKey(questionID)){
				Double vote = voteMap.get(questionID);
				if(vote!=null && vote<=this.calibration && this.checkIfQuestionReceivedMinimumNumberOfAnswers(questionID))
					quantityTrueNegatives = quantityTrueNegatives +1;
			}
		}
		return quantityTrueNegatives;
	}

	private Integer countBugCovering(){

		Integer count=0;

		for(String questionID: voteMap.keySet()){
			if(data.bugCoveringMap.containsKey(questionID))			
				count ++;
		}
		return count;
	}

	private Double computeTruePositiveRate(){
		Double numberOfBugCovering = this.getNumberBugCoveringQuestions().doubleValue();
		Double numberOfTruePositives = this.getTruePositives().doubleValue();
		return numberOfTruePositives / numberOfBugCovering;
	}

	//-----------------------------------------------------------------------------------

	/** Used to test the Majority voting functions */
	public static void main(String[] args){

		HashMap<String,String> bugCoveringMap = new HashMap<String,String>();

		bugCoveringMap.put("1","1");// received one yes
		bugCoveringMap.put("3","3");// received four yes's

		HashMap<String, ArrayList<String>> answerMap = new HashMap<String, ArrayList<String>>();

		ArrayList<String> answerList = new ArrayList<String>();//2 yes's TRUE NEGATIVE
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerList.add("IDK");
		answerList.add(Answer.YES);
		answerMap.put("0",answerList);

		answerList = new ArrayList<String>();//1 yes BUG COVERING FALSE NEGATIVE
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerMap.put("1",answerList);

		answerList = new ArrayList<String>();//2 yes's NON-BUG COVERING FALSE POSITIVE
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerMap.put("2",answerList);

		answerList = new ArrayList<String>();//4 yes's BUG COVERING TRUE POSITIVE
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerMap.put("3",answerList);

		String hitFileName = "HIT00_0";

		AnswerData data = new AnswerData(hitFileName,answerMap,bugCoveringMap,4.0,4.0);

		WithinQuestionConsensus predictor = new WithinQuestionConsensus(true);
		predictor.setCalibration(-1.0);
		predictor.scoreQuestions(data);
		Double bugCoveringQuestionsLocated =  predictor.getTruePositives().doubleValue();
		Double totalBugCovering = 2.0;

		Double percentageFaults = new Double( bugCoveringQuestionsLocated/totalBugCovering) * 100;

		System.out.println("expected: 50% bug covering question located, actual: "+ percentageFaults.toString());


		Double falsePositives = predictor.getFalsePositives();
		System.out.println("expected: 1, actual: "+ falsePositives.toString());

		Double falseNegatives = predictor.getFalseNegatives();
		System.out.println("expected: 1, actual: "+ falseNegatives.toString());

		Double trueNegatives = predictor.getTrueNegatives();
		System.out.println("expected: 1, actual: "+ trueNegatives.toString());

		Double truePositives = predictor.getTruePositives();
		System.out.println("expected: 1, actual: "+ truePositives.toString());
	}

	@Override
	public HashMap<String, Integer> getTruePositiveLines(
			HashMap<String, QuestionLinesMap> lineMapping) {

		HashMap<String, Integer> map=  new HashMap<String,Integer>();

		for(String questionID: voteMap.keySet()){
			if(data.bugCoveringMap.containsKey(questionID)){
				Double vote = voteMap.get(questionID);
				if(vote!=null && vote>this.calibration && this.checkIfQuestionReceivedMinimumNumberOfAnswers(questionID)){
					QuestionLinesMap questionLinesMap =lineMapping.get(questionID);
					if(questionLinesMap==null || questionLinesMap.faultyLines==null) System.err.println("No mapping for questionID: "+questionID);
					map =  this.loadLines(map, questionLinesMap.faultyLines);
				}
			}
		}
		return map;
	}


	@Override
	public HashMap<String, Integer> getNearPositiveLines(
			HashMap<String, QuestionLinesMap> lineMapping) {

		HashMap<String, Integer> map =  new HashMap<String,Integer>();

		for(String questionID: this.questionYESCountMap.keySet()){
			if(data.bugCoveringMap.containsKey(questionID)){
				Double vote = voteMap.get(questionID);
				if(vote!=null && vote>this.calibration && this.checkIfQuestionReceivedMinimumNumberOfAnswers(questionID)){
					QuestionLinesMap questionLinesMap =lineMapping.get(questionID);
					map = loadLines(map,questionLinesMap.nearFaultyLines);
				}
			}
		}
		return map;
	}



	@Override
	public HashMap<String, Integer> getFalsePositiveLines(
			HashMap<String, QuestionLinesMap> lineMapping) {

		HashMap<String, Integer> map =  new HashMap<String,Integer>();

		for(String questionID: this.questionYESCountMap.keySet()){
			if(!data.bugCoveringMap.containsKey(questionID)){
				Double vote = voteMap.get(questionID);
				if(vote!=null && vote>this.calibration && this.checkIfQuestionReceivedMinimumNumberOfAnswers(questionID)){
					QuestionLinesMap questionLinesMap =lineMapping.get(questionID);
					if(questionLinesMap.nonFaultyLines!=null) 
						map = loadLines(map,questionLinesMap.nonFaultyLines);
					//else
					//System.err.println("QuestionID: "+questionID +" is not failure related, but has a bug at same line");

				}
			}
		}
		return map;
	}

	@Override
	public HashMap<String, Integer> getTrueNegativeLines(
			HashMap<String, QuestionLinesMap> lineMapping) {

		HashMap<String, Integer> map =  new HashMap<String,Integer>();

		for(String questionID: this.questionYESCountMap.keySet()){
			if(!data.bugCoveringMap.containsKey(questionID)){
				Double vote = voteMap.get(questionID);
				if(vote!=null && vote<=this.calibration && this.checkIfQuestionReceivedMinimumNumberOfAnswers(questionID)){
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
	public HashMap<String, Integer> getFalseNegativeLines(
			HashMap<String, QuestionLinesMap> lineMapping) {

		HashMap<String, Integer> map =  new HashMap<String,Integer>();

		for(String questionID: this.questionYESCountMap.keySet()){
			if(data.bugCoveringMap.containsKey(questionID)){
				Double vote = voteMap.get(questionID);
				if(vote!=null && vote<=this.calibration && this.checkIfQuestionReceivedMinimumNumberOfAnswers(questionID)){
					QuestionLinesMap questionLinesMap =lineMapping.get(questionID);
					map = loadLines(map,questionLinesMap.faultyLines);
				}
			}
		}
		return map;
	}





	private HashMap<String, Integer> loadLines(HashMap<String, Integer> map, HashMap<String, String> questionLineMapping) {

		HashMap<String, Integer> newMap = (HashMap<String, Integer>) map.clone();

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

	@Override
	public HashMap<String, HashMap<String,Integer>> getNearPositiveLinesQuestions(HashMap<String, QuestionLinesMap> lineMapping){

		HashMap<String, HashMap<String,Integer>> questionMap = new HashMap<String, HashMap<String,Integer>>();
		for(String questionID: this.questionYESCountMap.keySet()){
			if(data.bugCoveringMap.containsKey(questionID)){
				Double vote = voteMap.get(questionID);
				if(vote!=null && vote>this.calibration && this.checkIfQuestionReceivedMinimumNumberOfAnswers(questionID)){
					QuestionLinesMap questionLinesMap =lineMapping.get(questionID);
					HashMap<String, Integer> map = loadLines(new HashMap<String, Integer>(),questionLinesMap.nearFaultyLines);
					if(map.size()>0){
						questionMap.put(questionID, map);
					}
				}
			}
		}
		return questionMap;
	}


	public void setMinimumAnswersPerQuestion(double minimum){
		this.minimumAnswersPerQuestion = minimum;
	}

	public void setIncludeIDK(boolean includeIDK){
		this.includeIDK = includeIDK;
	}

	@Override
	public int getQuestionsBelowMinimalAnswers() {
		int value = this.questionsBelowMinimalAnswers;
		this.questionsBelowMinimalAnswers = 0;
		return value;
	}


}



//--------------------------------------------------------------------------------------------------------------





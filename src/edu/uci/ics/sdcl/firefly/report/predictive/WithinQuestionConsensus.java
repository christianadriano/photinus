package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.isc.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMap;

/**
 * Each question has a vote count which is basically Number of YES's minus the Number of NO's.
 * 
 * @author adrianoc
 *
 */
public class WithinQuestionConsensus extends Consensus{

	public static String name = "Within-question consensus";
	
	private HashMap<String, Integer> voteMap;
	
	private HashMap<String, Integer> questionYESCountMap;
	
	private AnswerData data;
	

	/** The number of bug covering questions that were actually found */
	@Override
	public Boolean computeSignal(AnswerData data){
		this.data = data;
		this.questionYESCountMap = this.computeNumberOfYES(data.getAnswerMap());
		HashMap<String, Integer> questionNoCountMap = this.computeNumberOfNO(data.getAnswerMap());
		this.voteMap = this.computeQuestionVoteMap(questionYESCountMap,questionNoCountMap); 
	
		if(this.computeNumberOfCorrectBugCoveringFound()>0)
			return true;
		else
			return false;
	}

	@Override
	public Double computeSignalStrength(AnswerData data){
		if(voteMap==null)
			this.computeSignal(data);

		if(getTruePositives()==0)
			return -1.0;
		
		Integer extraVotes=0;
		
		for(String questionID: data.bugCoveringMap.keySet()){
			
			Integer vote = voteMap.get(questionID);
			if(vote!=null && vote>0){
				extraVotes = extraVotes + vote-1;
			}
		}

		Double truePositiveRate = this.computeTruePositiveRate();

		return truePositiveRate * extraVotes;
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
	/**
	 * 
	 * @return number of YES of the bug covering question that has the smallest positive vote. If the fault was not found returns 0.
	 */
	public Integer getThreshold(){
		
		if (voteMap==null){
			if(!this.computeSignal(data))
				return -1;
		}
		
		int smallestVote = this.computeNumberOfWorkers(data); //starts with the maximum possible.
		String questionIDSmallestVote=null;
		
		//find the number of YES of the bug covering question that has the smallest positive vote
		for(String questionID: this.voteMap.keySet()){
			if(data.bugCoveringMap.containsKey(questionID)){
				Integer vote = this.voteMap.get(questionID);
				if(vote!=null && vote>0 && vote<smallestVote){
					smallestVote = vote;
					questionIDSmallestVote = new String(questionID);
				}
			}
		}
		
		if(questionIDSmallestVote!=null)
			return this.questionYESCountMap.get(questionIDSmallestVote);
		else 
			return -1;
	}
	
	/** Same result as function compute */
	@Override
	public Integer getTruePositives(){
		if(this.voteMap!=null)
			return this.computeNumberOfCorrectBugCoveringFound();
		else
			return null;
	}
	
	@Override
	public Integer getFalsePositives(){
		if(this.voteMap!=null)
			return computeFalsePositives();
		else
			return null;
	}
	
	@Override
	public Integer getFalseNegatives(){
		if(this.voteMap!=null)
			return computeFalseNegatives();
		else
			return null;
	}
	
	@Override
	public Integer getTrueNegatives(){
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
		return WithinQuestionConsensus.name;
	}


	public WithinQuestionConsensus(){
		super();
	}
	//----------------------------------------------------------------------------------------------------------

	/**
	 * @param questionOptionsMap questionID and list of answer options (YES, NO, IDK)
	 * @return a map <questionID, number of YES's>
	 */
	private HashMap<String, Integer> computeNumberOfYES(HashMap<String, ArrayList<String>> questionOptionsMap){

		HashMap<String, Integer> questionYESCountMap= new HashMap<String, Integer>(); 

		for(String questionID: questionOptionsMap.keySet()){
			ArrayList<String> optionList = questionOptionsMap.get(questionID);
			int counter = 0;
			for(String option : optionList){
				//System.out.println(option);
				if(option.compareTo(Answer.YES)==0)
					counter++;
			}
			//System.out.println("questionID: "+ questionID+"counter:"+counter);
			questionYESCountMap.put(questionID, new Integer(counter));
		}
		return questionYESCountMap;
	}


	/**
	 * @param questionOptionsMap questionID and list of answer options (YES, NO, IDK)
	 * @return a map <questionID, number of NO's>
	 */
	private HashMap<String, Integer> computeNumberOfNO(HashMap<String, ArrayList<String>> questionOptionsMap){

		HashMap<String, Integer> questionNOCountMap= new HashMap<String, Integer>(); 

		for(String questionID: questionOptionsMap.keySet()){
			ArrayList<String> optionList = questionOptionsMap.get(questionID);
			int counter = 0;
			for(String option : optionList){
				//System.out.println(option);
				if(option.compareTo(Answer.NO)==0)
					counter++;
			}
			//System.out.println("questionID: "+ questionID+"counter:"+counter);
			questionNOCountMap.put(questionID, new Integer(counter));
		}
		return questionNOCountMap;
	}

	/**
	 * Each question has a vote count which is basically Number of YES's minus the Number of NO's.
	 * 
	 * 
	 * @param questionYESCountMap
	 * @param questionNOCountMap
	 * @return map of questions and respective votes
	 */
	private HashMap<String,Integer> computeQuestionVoteMap(HashMap<String, Integer> questionYESCountMap,
			HashMap<String, Integer> questionNOCountMap) {

		HashMap<String,Integer> voteMap =  new HashMap<String,Integer>();
		for(String questionID : questionYESCountMap.keySet()){
			Integer yesCount = questionYESCountMap.get(questionID);
			Integer noCount = questionNOCountMap.get(questionID);
			Integer vote = yesCount - noCount;
			
			voteMap.put(questionID, vote);
		}
		return voteMap;
	}

	private Integer computeNumberOfCorrectBugCoveringFound() {
		
		Integer quantityLocated=0;
	
		for(String questionID: data.bugCoveringMap.keySet()){
			Integer vote = voteMap.get(questionID);
			if(vote!=null){
				if(vote>0)
					quantityLocated++;
			}
		}
		return quantityLocated;
	}
	
	
	private Integer computeFalsePositives() {
		
		Integer quantityFalsePositives=0;

		for(String questionID: voteMap.keySet()){
			if(!data.bugCoveringMap.containsKey(questionID)){
				Integer vote = voteMap.get(questionID);
				if(vote!=null && vote>0)
					quantityFalsePositives = quantityFalsePositives +1;
			}
		}
		return quantityFalsePositives;
	}

	private Integer computeFalseNegatives() {
		
		Integer quantityFalseNegatives=0;

		for(String questionID: voteMap.keySet()){
			if(data.bugCoveringMap.containsKey(questionID)){
				Integer vote = voteMap.get(questionID);
				if(vote!=null && vote<=0)
					quantityFalseNegatives = quantityFalseNegatives +1;
			}
		}
		return quantityFalseNegatives;
	}
	
	private Integer computeTrueNegatives() {
		
		Integer quantityTrueNegatives=0;

		for(String questionID: voteMap.keySet()){
			if(!data.bugCoveringMap.containsKey(questionID)){
				Integer vote = voteMap.get(questionID);
				if(vote!=null && vote<=0)
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
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerMap.put("2",answerList);
		
		answerList = new ArrayList<String>();//4 yes's BUG COVERING TRUE POSITIVE
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerMap.put("3",answerList);
		
		String hitFileName = "HIT00_0";
		
		AnswerData data = new AnswerData(hitFileName,answerMap,bugCoveringMap,4,4);
		
		WithinQuestionConsensus predictor = new WithinQuestionConsensus();
		
		Double bugCoveringQuestionsLocated =  predictor.getTruePositives().doubleValue();
		Double totalBugCovering = 2.0;
		
		Double percentageFaults = new Double( bugCoveringQuestionsLocated/totalBugCovering) * 100;
		
		System.out.println("expected: 50% bug covering question located, actual: "+ percentageFaults.toString());
		
		
		Integer falsePositives = predictor.getFalsePositives();
		System.out.println("expected: 1, actual: "+ falsePositives.toString());
		
		Integer falseNegatives = predictor.getFalseNegatives();
		System.out.println("expected: 1, actual: "+ falseNegatives.toString());
		
		Integer trueNegatives = predictor.getTrueNegatives();
		System.out.println("expected: 1, actual: "+ trueNegatives.toString());
	}

	@Override
	public HashMap<String, Integer> getTruePositiveLineCount(
			HashMap<String, QuestionLinesMap> lineMapping) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, Integer> getTrueNegativeLineCount(
			HashMap<String, QuestionLinesMap> lineMapping) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, Integer> getFalsePositiveLineCount(
			HashMap<String, QuestionLinesMap> lineMapping) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, Integer> getFalseNegativeLineCount(
			HashMap<String, QuestionLinesMap> lineMapping) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, Integer> getTruePositiveFaultyLineCount(
			HashMap<String, QuestionLinesMap> lineMapping) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, Integer> getTruePositiveNearFaultyLineCount(
			HashMap<String, QuestionLinesMap> lineMapping) {
		// TODO Auto-generated method stub
		return null;
	}


	
	
	
	
	
}

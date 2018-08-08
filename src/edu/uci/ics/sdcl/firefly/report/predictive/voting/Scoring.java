package edu.uci.ics.sdcl.firefly.report.predictive.voting;

import java.util.HashMap;

/**
 * Gives a score to each question based on their answers.
 * @author Christian Adriano
 *
 */
public class Scoring {

	/** Absolute number of YES */
	public static String ABSOLUTE_YES_Consensus="absolute YES consensus";

	/** Majority voting */
	public static String BALANCE_YES_NO_Consensus="balance YES NO consensus";

	/** 
	 * Proportional Positive to Negative voting
	 * Divides the number of YES by number of NO's, the idea it to make the metric less dependent on
	 * the total number of answers per question
	 */
	public static String PROPORTION_YES_NO_Consensus="proportion YES to NO consensus";


	/**
	 * Number of YES's minus the by Number of NO's
	 * 
	 * @param questionYESCountMap
	 * @param questionNOCountMap
	 * @return a map with the ID of the item and the score
	 */
	public HashMap<String, Double> scoreMajorityVote(
			HashMap<String, Double> questionYESCountMap,
			HashMap<String, Double> questionNOCountMap){

		HashMap<String,Double> voteMap =  new HashMap<String,Double>();
		for(String questionID : questionYESCountMap.keySet()){
			Double yesCount = questionYESCountMap.get(questionID);
			Double noCount = questionNOCountMap.get(questionID);
			Double vote = yesCount - noCount;

			voteMap.put(questionID, vote);
		}
		return voteMap;
	}

	/**
	 * Number of YES's divided by the number of NO's
	 * 
	 * @param questionYESCountMap
	 * @param questionNOCountMap
	 * @return a map with the ID of the item and the score
	 */
	public HashMap<String, Double> scoreProportionalVote(
			HashMap<String, Double> questionYESCountMap,
			HashMap<String, Double> questionNOCountMap){

		HashMap<String,Double> voteMap =  new HashMap<String,Double>();
		for(String questionID : questionYESCountMap.keySet()){
			Double yesCount = questionYESCountMap.get(questionID);
			Double noCount = questionNOCountMap.get(questionID);
			Double vote = 0.0;
			if (noCount!=0)
				vote = yesCount / noCount;
			else
				vote = 1.0;

			voteMap.put(questionID, vote);
		}
		return voteMap;
	}


	/**
	 * Each question has a vote count which is basically Number of YES's 
	 * minus the Number of NO's.
	 * 
	 * @param questionYESCountMap
	 * @param questionNOCountMap
	 * @return a map with the ID of the item and the score
	 */
	public HashMap<String,Double> scoreAbsolutePositiveVote(
			HashMap<String, Double> questionYESCountMap) {

		HashMap<String,Double> voteMap =  new HashMap<String,Double>();
		for(String questionID : questionYESCountMap.keySet()){
			Double yesCount = questionYESCountMap.get(questionID);

			Double vote = yesCount;

			voteMap.put(questionID, vote);
		}
		return voteMap;
	}


}

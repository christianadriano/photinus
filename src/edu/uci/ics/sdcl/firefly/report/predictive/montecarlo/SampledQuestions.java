package edu.uci.ics.sdcl.firefly.report.predictive.montecarlo;

import java.util.HashMap;

/**
 * Represents a sample of questions and attributes of the sample such 
 * - How many of the questions were oversampled
 * - How many of the questions were undersampled
 * - How many were not sampled at all
 * 
 * 
 * @author Christian Adriano
 *
 */
public class SampledQuestions {

	int countOf_OversampledQuestions=0;
	int countOf_UndersampledQuestions=0;
	
	int countOf_OversampledQuestions_bugCovering=0;
	int countOf_OversampledQuestions_NotbugCovering=0;
	
	int countOf_UndersampledQuestions_bugCovering=0;
	int countOf_UndersampledQuestions_NotbugCovering=0;
	
	int top_1_oversampledQuestionID=0;
	int top_2_oversampledQuestionID=0;
	int top_3_oversampledQuestionID=0;
	
	HashMap<String,Integer> rankOf_OversampledQuestions = new HashMap<String,Integer>();
	HashMap<String,Integer> rankOf_UndersampledQuestions = new HashMap<String,Integer>();
	
	public SampledQuestions(int countOf_OversampledQuestions, int countOf_UndersampledQuestions,
			int countOf_OversampledQuestions_bugCovering, int countOf_OversampledQuestions_NotbugCovering,
			int countOf_UndersampledQuestions_bugCovering, int countOf_UndersampledQuestions_NotbugCovering,
			int top_1_oversampledQuestionID, int top_2_oversampledQuestionID, int top_3_oversampledQuestionID) {
		super();
		this.countOf_OversampledQuestions = countOf_OversampledQuestions;
		this.countOf_UndersampledQuestions = countOf_UndersampledQuestions;
		this.countOf_OversampledQuestions_bugCovering = countOf_OversampledQuestions_bugCovering;
		this.countOf_OversampledQuestions_NotbugCovering = countOf_OversampledQuestions_NotbugCovering;
		this.countOf_UndersampledQuestions_bugCovering = countOf_UndersampledQuestions_bugCovering;
		this.countOf_UndersampledQuestions_NotbugCovering = countOf_UndersampledQuestions_NotbugCovering;
		this.top_1_oversampledQuestionID = top_1_oversampledQuestionID;
		this.top_2_oversampledQuestionID = top_2_oversampledQuestionID;
		this.top_3_oversampledQuestionID = top_3_oversampledQuestionID;
	}
	
	public void addOversampledQuestionRank(String questionID, Integer rank){
		this.rankOf_OversampledQuestions.put(questionID, rank);
	}
	
	public void addUndersampledQuestionRank(String questionID, Integer rank){
		this.rankOf_UndersampledQuestions.put(questionID, rank);
	}
	
}

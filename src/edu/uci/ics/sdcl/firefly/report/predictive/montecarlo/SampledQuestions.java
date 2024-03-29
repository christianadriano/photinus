package edu.uci.ics.sdcl.firefly.report.predictive.montecarlo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;

import edu.uci.ics.sdcl.firefly.report.predictive.DataPoint;

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

	Integer sampleSize=0;
	
	Double countOf_OversampledQuestions=0.0;
	Double countOf_UndersampledQuestions=0.0;
	Double countOf_NotSampledQuestions=0.0;
	Double total_SampledQuestions=0.0;
	
	Double countOf_OversampledQuestions_bugCovering=0.0;
	Double countOf_OversampledQuestions_NonBugCovering=0.0;
	
	Double countOf_UndersampledQuestions_bugCovering=0.0;
	Double countOf_UndersampledQuestions_NonBugCovering=0.0;
	
	String top_1_oversampledQuestionID;
	String top_2_oversampledQuestionID;
	String top_3_oversampledQuestionID;
	
	HashMap<String,Integer> rankOf_OversampledQuestions = new HashMap<String,Integer>();
	HashMap<String,Integer> rankOf_UndersampledQuestions = new HashMap<String,Integer>();

	Double percent_oversampledQuestions=0.0;
	Double percent_undersampledQuestions=0.0;
	Double percent_oversampledQuestions_bugCovering=0.0;
	Double percent_undersampledQuestions_bugCovering=0.0;
	
	public SampledQuestions(int sampleSize, double countOf_OversampledQuestions, double countOf_UndersampledQuestions,
			double countOf_NotSampledQuestions,
			double countOf_OversampledQuestions_bugCovering, double countOf_OversampledQuestions_NonBugCovering,
			double countOf_UndersampledQuestions_bugCovering, double countOf_UndersampledQuestions_NonBugCovering) {

		this.sampleSize = sampleSize;
		this.countOf_OversampledQuestions = countOf_OversampledQuestions;
		this.countOf_UndersampledQuestions = countOf_UndersampledQuestions;
		this.countOf_NotSampledQuestions = countOf_NotSampledQuestions;
		
		this.countOf_OversampledQuestions_bugCovering = countOf_OversampledQuestions_bugCovering;
		this.countOf_OversampledQuestions_NonBugCovering = countOf_OversampledQuestions_NonBugCovering;
		this.countOf_UndersampledQuestions_bugCovering = countOf_UndersampledQuestions_bugCovering;
		this.countOf_UndersampledQuestions_NonBugCovering = countOf_UndersampledQuestions_NonBugCovering;
		
		
		this.total_SampledQuestions = this.countOf_OversampledQuestions + this.countOf_UndersampledQuestions + this.countOf_NotSampledQuestions;
		this.percent_oversampledQuestions = this.countOf_OversampledQuestions / this.total_SampledQuestions;
		this.percent_undersampledQuestions = this.countOf_UndersampledQuestions / this.total_SampledQuestions;
		
		this.percent_oversampledQuestions_bugCovering = this.countOf_OversampledQuestions_bugCovering / this.countOf_OversampledQuestions;
		this.percent_undersampledQuestions_bugCovering = this.countOf_UndersampledQuestions_bugCovering / this.countOf_UndersampledQuestions;	
	}
	
	public void setRankedQuestions(String top_1_oversampledQuestionID, String top_2_oversampledQuestionID,
			String top_3_oversampledQuestionID){
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

	/**
	 * Used to print the content into a file. 
	 * @return
	 */
	public static String getHeader(){
		return("sampleSize, Oversampled Questions, % Oversampled Questions, " +
				"BugCovering Oversampled Questions, % BugCovering Oversampled Questions, " +
				"Undersampled Questions,  % Undersampled Questions, " +
				"BugCovering Undersampled Questions, % BugCovering Undersampled Questions, " +
				"NonBugCovering Oversampled Questions, NonBugCovering Undersampled Questions, " +
				"total_Sampled Questions, Not_Sampled Questions, " +
				"top_1_oversampled QuestionID, top_2_oversampled QuestionID, top_3_oversampled QuestionID");
	}
	
	
	public String toString(){
						
		return( this.sampleSize.toString() +","+
				this.countOf_OversampledQuestions.toString() +","+
				this.percent_oversampledQuestions.toString() +","+
				this.countOf_OversampledQuestions_bugCovering.toString() +","+
				this.percent_oversampledQuestions_bugCovering.toString() +","+
				this.countOf_UndersampledQuestions.toString() + ","+
				this.percent_undersampledQuestions.toString() + ","+
				this.countOf_UndersampledQuestions_bugCovering.toString() +","+
				this.percent_undersampledQuestions_bugCovering.toString() +","+
				this.countOf_OversampledQuestions_NonBugCovering.toString() +","+
				this.countOf_UndersampledQuestions_NonBugCovering.toString() +","+
				this.total_SampledQuestions.toString() +","+
				this.countOf_NotSampledQuestions.toString()+","+
				this.top_1_oversampledQuestionID +","+
				this.top_2_oversampledQuestionID +","+
				this.top_3_oversampledQuestionID);
		
	}

}

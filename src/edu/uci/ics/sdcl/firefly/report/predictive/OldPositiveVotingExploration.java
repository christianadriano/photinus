package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.uci.ics.sdcl.firefly.Answer;

/** 
 * Code here was used to explored different levels of threshold.
 * This WAS NOT used to computed consensus in the Across-questions
 * 
 * @author adrianoc
 *
 */
public class OldPositiveVotingExploration {
	

	
	
	

	
	//OLD CODE NOT USED ANYMORE.
	//THIS CODE WAS USED TO INVESTIGATE HOW MANY YES WOULD BE NEEDED TO UNAMBIGUOUSLY DISTINGUISH BUG-COVERING FROM NON BUG-COVERING QUESTIONS

//	
//	private Integer maxYES_BugCovering=0;
//
//	private Integer maxYES_NonBugCovering=0;
//	
//	private Integer firstThreshold=null;
//
//	private Integer secondThreshold=null;
//	
//	public Boolean oldComputeSignal(AnswerData data){
//
//		this.maxYES_BugCovering = 0;
//		this.maxYES_NonBugCovering = 0;
//
//		this.data = data;
//		this.questionYESCountMap = this.computeNumberOfAnswers(Answer.YES);
//		finalThreshold =  this.computeFirstThreshold();
//	//	System.out.println(data.getHitFileName()+":"+finalThreshold+":"+getTruePositives());
//
//		if(finalThreshold<0){
//			finalThreshold = this.computeSecondThreshold();
//		}
//
//		if(finalThreshold>0)
//			return true;
//		else
//			return false;
//	}
//
//	public Integer getThreshold(){
//		return this.finalThreshold;
//	}
//
//	public Integer getFirstThreshold(){
//		return this.firstThreshold;
//	}
//
//	public Integer getSecondThreshold(){
//		return this.secondThreshold;
//	}
//	
//	/**
//	 * The threshold is the number of YES's that is larger than the one received by 
//	 * any non-bug-Covering questions and still smaller or equal to at
//	 * least one bug-covering question.
//	 *  
//	 * If the maxNumber of YES's for Bug-Covering is not larger than the one for Non-Bug-Covering,
//	 * then returns the difference of YES's between the tow top Bug-Covering and Non-Bug-Covering questions.
//	 * This difference will be negative and show how many more YES's answers were necessary for the Bug-Covering to be
//	 * unambiguously distinguished from Non-Bug-Covering questions. 
//	 * 
//	 * @param questionYESCountMap
//	 * @param bugCoveringMap
//	 * @return if number of max
//	 */
//	private Integer computeFirstThreshold(){
//		//this.maxYES_BugCovering = 0;
//		//this.maxYES_NonBugCovering = 0;
//
//		//Compute Max YES among Bug Covering
//		for(String questionID: this.questionYESCountMap.keySet()){
//			int yesCount = this.questionYESCountMap.get(questionID).intValue();
//			//if(this.data.getHitFileName().compareTo("HIT06_51")==0)
//			//System.out.println(questionID+":"+yesCount);
//
//			if(data.bugCoveringMap.containsKey(questionID)){
//				if(maxYES_BugCovering < yesCount)
//					maxYES_BugCovering = yesCount;
//			}
//			else{
//				maxYES_NonBugCovering = (maxYES_NonBugCovering < yesCount) ? yesCount: maxYES_NonBugCovering;
//			}
//		}
//
//		//if(this.data.getHitFileName().compareTo("HIT06_51")==0){
//			//System.out.println(maxYES_BugCovering+":"+maxYES_NonBugCovering);
//
//		//}
//
//
//		if( maxYES_BugCovering > maxYES_NonBugCovering)
//			this.firstThreshold = maxYES_NonBugCovering +1;
//		else
//			this.firstThreshold = maxYES_BugCovering - maxYES_NonBugCovering;
//
//		return this.firstThreshold;
//	}
//
//	private Integer computeSecondThreshold(){
//
//		//Check if the maxYES_BugCovering is larger than at least one non-BugCovering question.		
//		if(isThere_NonBugCoveringBelow(this.maxYES_BugCovering))
//			this.secondThreshold = this.maxYES_BugCovering;
//		else
//			this.secondThreshold = -1; //There is not a viable threshold 
//
//		return this.secondThreshold;		
//	}
//
//	/** 
//	 * Looks for a non-bug covering question that has less YES's than the MaxYES's of 
//	 * bug covering questions.
//	 * 
//	 * @param maxYES_BugCovering
//	 * @return true if found a non-bug covering with more YES's than maxYes_Covering, otherwise, false.
//	 */
//	private boolean isThere_NonBugCoveringBelow(Integer maxYES_BugCovering) {
//
//		Iterator<String> iter = this.questionYESCountMap.keySet().iterator();
//		boolean found = false;
//
//		while(iter.hasNext() && !found){
//			String questionID= iter.next();
//			if(!data.bugCoveringMap.containsKey(questionID)){
//				Integer yesCount = this.questionYESCountMap.get(questionID);
//				if(yesCount<this.maxYES_BugCovering)
//					found=true;
//			}
//		}
//		return found;
//	}
	
	// NOT COMPLETED IMPLEMENTED.
		//------------------------------------------------------------------------------------------------------------------
		private Integer computeNextThreshold(HashMap<String, Integer> selectedQuestionYESCountMap){

			ArrayList<String> questionsSortedByYESCount = sortQuestionsByYES(selectedQuestionYESCountMap);

			Integer threshold=0;

			while(selectedQuestionYESCountMap.size()>0 && threshold<=0){

				questionsSortedByYESCount = sortQuestionsByYES(selectedQuestionYESCountMap);

				threshold = computeNextThreshold(selectedQuestionYESCountMap);
				if(threshold<=0)
					selectedQuestionYESCountMap = removeTopNonBugCovering(selectedQuestionYESCountMap);
			}

			return threshold;
		}

		private ArrayList<String> sortQuestionsByYES(HashMap<String, Integer> selectedQuestionYESCountMap){
			return null;
		}

		private HashMap<String, Integer> removeTopNonBugCovering(HashMap<String, Integer> selectedQuestionYESCountMap){
			return null;
		}
		//-----------------------------------------------------------------------------------------------------------------


}

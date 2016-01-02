package edu.uci.ics.sdcl.firefly.report.descriptive.answer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.WorkerSession;

/**
 * Computes a set of metrics for one worker session.
 * 
 * @author adrianoc
 *
 */
public class WorkerSessionTuple {

	/** counts the all difficulty levels of microtrask that are the same */
	public Integer identicalDifficultyLevels = 0; 
	
	/** same for confidence levels */
	public Integer identicalConfidenceLevels = 0;
	
	public ArrayList<Answer> answerList = new ArrayList<Answer>();
	
	public ArrayList<Integer> difficultyList = new ArrayList<Integer>();
	
	public ArrayList<Integer> confidenceList = new ArrayList<Integer>();
	
	/** Number of lines covered by question */
	public ArrayList<Integer> sizeList = new ArrayList<Integer>(); 
	
	public ArrayList<String> questionTypeList = new ArrayList<String>();
	
	public ArrayList<Boolean> correctnessList = new ArrayList<Boolean>();
	
	public String bugID = "";
	
	public String workerID = "";
	
	/**
	 * 
	 * @param session the microtasks for a worker
	 * @param bugCoveringMap Map with bug covering question IDs
	 */
	public WorkerSessionTuple(WorkerSession session, HashMap<String, String> bugCoveringMap){
		
		this.bugID = session.getFileName(); 
		this.workerID = session.getWorkerId();
		
		Vector<Microtask> microtaskList = session.getMicrotaskList();
		
		for(Microtask task : microtaskList){
			Answer answer = task.getAnswerByUserId(session.getWorkerId());
			answerList.add(answer);
			difficultyList.add(answer.getDifficulty());
			confidenceList.add(answer.getConfidenceOption());
			questionTypeList.add(task.getQuestionType());
			//sizeList.add(task.getLOC_CoveredByQuestion());//Check this.
	
			correctnessList.add(bugCoveringMap.containsKey(task.getID().toString()));
		}
		
		this.identicalDifficultyLevels = countIdenticalLevels(this.difficultyList);
		this.identicalConfidenceLevels = countIdenticalLevels(this.confidenceList);
	}

	public WorkerSessionTuple() {}

	/**
	 * Counts how many of the numbers are identical.
	 * @param list
	 * @return
	 */
	public int countIdenticalLevels(ArrayList<Integer> list) {
		
		ArrayList<Integer> columns = (ArrayList<Integer>) list.clone();
		
		int identicalCount =0;
		
		for(int i=0; i<list.size();i++){
			for (int j=0;j<columns.size();j++){
				if(j>i){//avoid diagonals and cells above the diagonal
					if(list.get(i).intValue() == columns.get(j).intValue())
						identicalCount ++;
				}
			}
		}
		return identicalCount;
	}

	
}

package edu.uci.ics.sdcl.firefly.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;

/** 
 * Validate whether sets of microtasks are comprised by unique answers.
 * 
 * The strategy is to index each answer by WorkerID and MicrotaskID
 * 
 * 
 * @author adrianoc
 *
 */
public class MicrotaskMapValidator {

	public class DuplicateOutcome{
		HashMap<String, Microtask> unionMap;
		boolean duplicatesFound=false;
	}


	private HashMap<String,Microtask> indexMicrotasks(HashMap<String,Microtask> taskMap){

		HashMap<String,Microtask> resultMap = new HashMap<String,Microtask>();

		Iterator<String> iter = taskMap.keySet().iterator();
		while(iter.hasNext()){
			String microtaskID = iter.next();
			Microtask task = taskMap.get(microtaskID);
			Vector<Answer> answerList = task.getAnswerList();
			for(Answer answer: answerList){
				String workerID = answer.getWorkerId();
				String answerDifficulty = new Integer(answer.getDifficulty()).toString();
				String index = "workerID="+workerID+", microtaskID="+microtaskID+", difficulty="+answerDifficulty;
				resultMap.put(index, task);
			}
		}
		return resultMap;
	}


	public boolean haveDuplicates(ArrayList<HashMap<String, Microtask>> mapList){

		boolean duplicatesFound=false;
		HashMap<String, Microtask> taskMap1 = indexMicrotasks(mapList.get(0));
		HashMap<String, Microtask> taskMap2 = indexMicrotasks(mapList.get(1));

		DuplicateOutcome outcome = printDuplicates(taskMap1,taskMap2);
		HashMap<String,Microtask> unionMap = outcome.unionMap;

		if(mapList.size()>2){

			for(int i=2;i<mapList.size();i++){
				HashMap<String, Microtask> indexMap = indexMicrotasks(mapList.get(i));
				outcome = printDuplicates(unionMap,indexMap); 
				unionMap = 	outcome.unionMap;
				if(outcome.duplicatesFound)
					duplicatesFound = true;
			}
		}
		return duplicatesFound;
	}

	private DuplicateOutcome printDuplicates(HashMap<String, Microtask> taskMap1,HashMap<String, Microtask> taskMap2){

		DuplicateOutcome outcome = new DuplicateOutcome();
		HashMap<String, Microtask> indexMap = new HashMap<String, Microtask> ();
		indexMap.putAll(taskMap1);

		Iterator<String> iter = taskMap2.keySet().iterator();
		while(iter.hasNext()){
			String index = iter.next();
			if(taskMap1.containsKey(index)){
				System.out.println("answer duplicate: "+index);
				outcome.duplicatesFound = true;
			}
			else{
				indexMap.put(index,taskMap2.get(index));
			}
		}
		outcome.unionMap = indexMap;
		return outcome;
	}


	/** Testing method */
	public static void main(String args[]){
		
		MicrotaskMapValidator validator =  new MicrotaskMapValidator();

		FileSessionDTO dto = new FileSessionDTO();
		HashMap<String, Microtask> microtaskMap = (HashMap<String, Microtask>) dto.getMicrotasks();
		Microtask microtask = microtaskMap.get("2");
		Vector<Answer> answerList = microtask.getAnswerList();
		answerList.remove(0);
		microtask.setAnswerList(answerList);

		HashMap<String, Microtask> taskMap1 = new  HashMap<String, Microtask>();
		taskMap1.put("2", microtask);
		//taskMap1.put("1", microtaskMap.get("1"));

		microtaskMap = (HashMap<String, Microtask>) dto.getMicrotasks();
		microtask = microtaskMap.get("2");
		answerList = microtask.getAnswerList();
		Vector<Answer> newAnswerList = new Vector<Answer>();
		for(int i=1;i<2;i++){
			newAnswerList.add(answerList.get(i));
		}
		microtask.setAnswerList(newAnswerList);
		HashMap<String, Microtask> taskMap2 = new  HashMap<String, Microtask>();
		taskMap2.put("2", microtask);		

		ArrayList<HashMap<String, Microtask>> list = new ArrayList<HashMap<String, Microtask>>(); 
		list.add(taskMap1);
		list.add(taskMap2);

		validator.haveDuplicates(list); //Should print only one answer duplicate

	}
}

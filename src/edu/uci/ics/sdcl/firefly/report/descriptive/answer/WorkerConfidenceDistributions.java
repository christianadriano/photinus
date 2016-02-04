package edu.uci.ics.sdcl.firefly.report.descriptive.answer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.uci.ics.sdcl.firefly.Answer;

public class WorkerConfidenceDistributions {

	String workerProfession="";
	String workerScore="";
	String bugID="";

	/** Distribution of correct answers by confidence level */
	HashMap<String,Integer> correctConfidenceMap = new HashMap<String,Integer>();

	/** Distribution of correct answers by difficulty level */
	HashMap<String,Integer> correctDifficultyMap = new HashMap<String,Integer>();

	/** Distribution of wrong answers by confidence level */
	HashMap<String,Integer> wrongConfidenceMap = new HashMap<String,Integer>();

	/** Distribution of wrong answers by difficulty level */
	HashMap<String,Integer> wrongDifficultyMap = new HashMap<String,Integer>();


	public void addTuple(WorkerSessionTuple tuple){

		for(Answer answer:tuple.correctAnswers){
			String confidenceLevel = new Integer(answer.getConfidenceOption()).toString();
			this.correctConfidenceMap = this.addToMap(confidenceLevel, correctConfidenceMap);
			
			String difficultyLevel = new Integer(answer.getDifficulty()).toString();
			this.correctDifficultyMap = this.addToMap(difficultyLevel, correctDifficultyMap); 
		}
		
		for(Answer answer:tuple.wrongAnswers){
			String confidenceLevel = new Integer(answer.getConfidenceOption()).toString();
			this.wrongConfidenceMap = this.addToMap(confidenceLevel, wrongConfidenceMap);
			
			String difficultyLevel = new Integer(answer.getDifficulty()).toString();
			this.wrongDifficultyMap = this.addToMap(difficultyLevel, wrongDifficultyMap); 
		}
	}

	public HashMap<String,Integer> addToMap(String level, HashMap<String,Integer> map){
		Integer count = map.get(level);
		if(count==null)
			count = 1;
		else
			count++;
		map.put(level, count);
		return map;
	}

	
	public ArrayList<StringBuffer> toStringList(){
		
		ArrayList<StringBuffer> list = new ArrayList<StringBuffer>();
		list.addAll(toArrayList("correctConfidence", this.correctConfidenceMap));
		list.addAll(toArrayList("wrongConfidence", this.wrongConfidenceMap));
		list.addAll(toArrayList("correctDifficulty", this.correctDifficultyMap));
		list.addAll(toArrayList("wrongDifficulty", this.wrongDifficultyMap));
		return list;
	}
	
	
	public ArrayList<StringBuffer> toArrayList(String mapName, HashMap<String, Integer> map){
		
		ArrayList<StringBuffer> list = new ArrayList<StringBuffer>();
		StringBuffer line = new StringBuffer(mapName+",");
		Iterator<String> iter = map.keySet().iterator();
		while(iter.hasNext()){
			String level = iter.next();
			Integer count = map.get(level);
			line.append(level+","+count.toString()+",");
		}
		list.add(line);
		
		return list;
	}
		
}

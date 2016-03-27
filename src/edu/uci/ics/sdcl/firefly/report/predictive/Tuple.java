package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.HashMap;

public class Tuple {

	public int confidence=-1;
	 
	public int difficulty=-1; 
	
	public int minExplanationSize=-1;
	public int maxExplanationSize=-1;
	
	
	public Tuple(int confidence, int difficulty){
		this.confidence =  confidence;
		this.difficulty = difficulty;
	}
	
	public String toString(){
		return "["+confidence+":"+difficulty+"]";
	}
	
	
	public static HashMap<String,Tuple> generateAllCombinations(int confidenceMax, int difficultyMax){
		
		HashMap<String,Tuple> map =  new HashMap<String,Tuple>();
		
		for(int i =0; i<=confidenceMax; i++){
			for(int j=1; j<=difficultyMax; j++){
				String key = "["+i+":"+j+"]";
				Tuple tuple = new Tuple(i,j);
				map.put(key,tuple);
			}
		}
		return map;
	}
	
	
}

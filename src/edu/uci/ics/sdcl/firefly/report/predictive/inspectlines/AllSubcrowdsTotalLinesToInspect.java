package edu.uci.ics.sdcl.firefly.report.predictive.inspectlines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.uci.ics.sdcl.firefly.util.ReadWriteFile;

/**
 * Read an input file with lines considered faulty by 30 subcrowds.
 * Count how many times each line was considered faulty.
 * @author adrianoc
 *
 */
public class AllSubcrowdsTotalLinesToInspect {

	String path = "C:/firefly/LineContentAnalysis/";
	String sourceFileName = "AllSubcrowdsLinesToInspect.csv";
	
	ArrayList<String> lineList;
	HashMap<String,Integer> map;
	
	public AllSubcrowdsTotalLinesToInspect(){
		this.map = new HashMap<String, Integer>();
	}
	
	public static void main(String args[]){
		
		AllSubcrowdsTotalLinesToInspect object =  new AllSubcrowdsTotalLinesToInspect();
		object.run();
		object.printMap();
	}

	private void run() {
		this.lineList = ReadWriteFile.readToBuffer(path, sourceFileName);		
		
		this.lineList.remove(0); //Remove header
		
		for(String line:lineList){
			String[] lineTokens = line.split(",");
			addLineNumbers(lineTokens[4]);
			addLineNumbers(lineTokens[6]);
			if(lineTokens.length>=9)
				addLineNumbers(lineTokens[8]);
		}
	}

	private void addLineNumbers(String line) {
		String[] lineTokens = line.split(";");
		
		for(String number:lineTokens){
			String token = number;
			if(!token.matches(""))
				add(token);
		}
	}

	private void add(String token) {
		Integer count = this.map.get(token);
		if(count !=null){
			count++;
		}
		else
			count = 1;
		this.map.put(token, count);
	}

	private void printMap(){
		Iterator<String> iterator = this.map.keySet().iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			Integer count = this.map.get(key);
			System.out.println(key+","+count);
		}
	}
	
	
}

package edu.uci.ics.sdcl.firefly.report.predictive.spectra;

import java.util.ArrayList;
import java.util.TreeMap;
import edu.uci.ics.sdcl.firefly.util.ReadWriteFile;

/** 
 * It uses data collected in a file.
 * 
 * @author adrianoc
 */
public class CountQuestionsPerLine {

	public void run(String path, String fileName){

		ArrayList<String> buffer = ReadWriteFile.readToBuffer(path, fileName);
		TreeMap<String, Integer> map = countQuestions(buffer);
		print(map);
	}

	private TreeMap<String, Integer> countQuestions(ArrayList<String> buffer){

		TreeMap<String, Integer> map = new TreeMap<String, Integer>();

		for(String item : buffer){
			String[] lineList = item.split(";");
			for(String lineNumber: lineList){
				lineNumber = lineNumber.trim();
				Integer count = map.get(lineNumber);
				if(count==null)
					map.put(lineNumber, 1);
				else{
					count++;
					map.put(lineNumber, count);
				}
			}
		}
		return map;
	}
	
	private void print(TreeMap<String, Integer> map){
				
		System.out.println("Line,Count");
		for(String key: map.keySet()){
			Integer count = map.get(key);
			System.out.println(key+","+count.toString());
		}
	}

	public static void main(String[] args){
		String path = "C://firefly//SpectraAnalysis//";
		String fileName = "linesCovered_bug";
		CountQuestionsPerLine counter =  new CountQuestionsPerLine();
		for(int i=1;i<=8;i++){
			counter.run(path, fileName+i+".txt");
		}
	}

}

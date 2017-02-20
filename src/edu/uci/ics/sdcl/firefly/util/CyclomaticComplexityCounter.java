package edu.uci.ics.sdcl.firefly.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Computes the cyclomatic complexity of a set of source code lines
 * 
 * Formula for cyclomatic complexity: 
 * https://www.leepoint.net/principles_and_practices/complexity/complexity-java-method.html
 *
 * @author Christian Adriano
 */
public class CyclomaticComplexityCounter {
	
	/**
	 * 
	 * @param source Java Source code to measure
	 * @return Cyclomatic Complexity of the source
	 */
	public int compute(ArrayList<String> lineList){
		int complexity=1;

		Pattern patif;
		String factors="if|else|for|while|case|catch|throw|throws|return|do-while|continue|break|default|finally|\\|\\|\\?|&&";

		patif = Pattern.compile(factors);

		for(String line: lineList){
			Matcher matif=patif.matcher(line);
			while (matif.find()){
				complexity++;
			}  
		}
		return complexity;             
	}

	public static void main(String args[]){
		CyclomaticComplexityCounter c = new CyclomaticComplexityCounter();
		ArrayList<String> lineList =  new ArrayList<String>();
		String line = "if( a||b && c for do-while throw throws default";
		lineList.add(line);
		line = "continue break else default while case catch";
		lineList.add(line);		
		System.out.println("complexity = "+  c.compute(lineList));
	}	    
}
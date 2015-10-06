package edu.uci.ics.sdcl.firefly.report.predictive.spectra;

/**
 * Ignore empty lines, Javadoc lines, or lines with opening or closing curly brackets
 * 
 * @author adrianoc
 *
 */
public class IdentifyInvalidLines {

	private boolean ignoreEmptyLine=true;
	private boolean ignoreLoneCurlyBrackets=true;
	private boolean ignoreComments=true;
	private boolean ignoreJavadoc=true;
	
	public IdentifyInvalidLines(boolean ignoreEmptyLine, boolean ignoreLoneCurlyBrackets,
			boolean ignoreComments, boolean ignoreJavadoc){
		this.ignoreJavadoc = ignoreJavadoc;
		this.ignoreComments = ignoreComments;
		this.ignoreEmptyLine = ignoreEmptyLine;
	}
	
	//TO DO read each file, evaluate which lines should be ignored
	
}

package edu.uci.ics.sdcl.firefly;

import java.io.Serializable;
import java.util.ArrayList;

public class CodeElement implements Serializable {

	public final static String FOR_LOOP = "FOR_LOOP"; 
	public final static String WHILE_LOOP = "WHILE_LOOP"; 
	public final static String DO_LOOP = "DO_LOOP";
	public final static String IF_CONDITIONAL = "IF_CONDITIONAL"; 
	public final static String SWITCH_CONDITIONAL = "SWITCH_CONDITIONAL"; 
//	public final static String METHOD_CALL = "METHOD_CALL"; 
	public final static String METHOD_DECLARATION = "METHOD_DECLARATION"; 
	public final static String METHOD_INVOCATION = "METHOD_INVOCATION"; 
//	public final static String METHOD_PARAMETERS = "METHOD_PARAMETERS"; 
//	public final static String METHOD_NAME = "METHOD_NAME";
//	public static final String RETURN_STATEMENT = "RETURN_STATEMENT";
	public static final String VARIABLE_DECLARATION = "VARIABLE_DECLARATION";
	public static final String COMMENT_STATEMENT = "COMMENT_STATEMENT";
	public static final Integer NO_NUMBER_ASSOCIATED = -1;

	protected String Type;
	/* finding starting and ending position */
	/* Element position */
	protected Integer elementStartingLine;	// line number for the element beginning (not the body)
	protected Integer elementStartingColumn;// column number for the element beginning
	protected Integer elementEndingLine;	// line number for the element ending
	protected Integer elementEndingColumn;	// column number for the element ending
	/* Body position */
	protected Integer bodyStartingLine;		// line number for the beginning of body
	protected Integer bodyStartingColumn;	// column number for the body (of element in case of method invocation)
	protected Integer bodyEndingLine;		// line number for the end of the body
	protected Integer bodyEndingColumn;		// column number for the end of the body
	
	protected ArrayList<String> sourceCodeLines = new ArrayList<String>();; //the source corresponding to the CodeElement
	
	//Metrics
	private Integer characterCount_metric;
	private Integer volumeHalstead_metric;
	private Integer lengthHasltead_metric;
	
	/* constructor for elements without body */
	public CodeElement(String Type, 
			Integer elementStartingLineArg, Integer elementStartingColumnArg, 
			Integer elementEndingLineArg, Integer elementEndingColumnArg){
		this.Type = Type;
		/* setting element position */
		this.elementStartingLine = elementStartingLineArg;
		this.elementStartingColumn = elementStartingColumnArg;
		this.elementEndingLine = elementEndingLineArg;
		this.elementEndingColumn = elementEndingColumnArg;
		/* setting body position */
		this.bodyStartingLine = CodeElement.NO_NUMBER_ASSOCIATED;		
		this.bodyStartingColumn = CodeElement.NO_NUMBER_ASSOCIATED;	
		this.bodyEndingLine = CodeElement.NO_NUMBER_ASSOCIATED;		
		this.bodyEndingColumn = CodeElement.NO_NUMBER_ASSOCIATED;	
	}
	/* constructor for elements with body */
	public CodeElement(String Type, 
			Integer elementStartingLineArg, Integer elementStartingColumnArg, 
			Integer elementEndingLineArg, Integer elementEndingColumnArg,
			Integer bodyStartingLineArg, Integer bodyStartingColumnArg,
			Integer bodyEndingLineArg, Integer bodyEndingColumnArg){
		this.Type = Type;
		/* setting element position */
		this.elementStartingLine = elementStartingLineArg;
		this.elementStartingColumn = elementStartingColumnArg;
		this.elementEndingLine = elementEndingLineArg;
		this.elementEndingColumn = elementEndingColumnArg;
		/* setting body position */
		this.bodyStartingLine = bodyStartingLineArg;		
		this.bodyStartingColumn = bodyStartingColumnArg;	
		this.bodyEndingLine = bodyEndingLineArg;		
		this.bodyEndingColumn = bodyEndingColumnArg;	
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}
	
	@Override
	public String toString()
	{
		return this.getType() + " @ line " + this.bodyStartingLine.toString();
	}
	/* getting positions */
	public Integer getElementStartingLine() {
		return elementStartingLine;
	}
	public Integer getElementStartingColumn() {
		return elementStartingColumn;
	}
	public Integer getElementEndingLine() {
		return elementEndingLine;
	}
	public Integer getElementEndingColumn() {
		return elementEndingColumn;
	}
	public Integer getBodyStartingLine() {
		return bodyStartingLine;
	}
	public Integer getBodyStartingColumn() {
		return bodyStartingColumn;
	}
	public Integer getBodyEndingLine() {
		return bodyEndingLine;
	}
	public Integer getBodyEndingColumn() {
		return bodyEndingColumn;
	}
	/* setting positions */
	public void setBodyEndingLine(Integer bodyEndingLine) {
		this.bodyEndingLine = bodyEndingLine;
	}
	public void setBodyEndingColumn(Integer bodyEndingColumn) {
		this.bodyEndingColumn = bodyEndingColumn;
	}
	public void setElementStartingLine(Integer elementStartingLine) {
		this.elementStartingLine = elementStartingLine;
	}
	public void setElementStartingColumn(Integer elementStartingColumn) {
		this.elementStartingColumn = elementStartingColumn;
	}
	public void setElementEndingLine(Integer elementEndingLine) {
		this.elementEndingLine = elementEndingLine;
	}
	public void setElementEndingColumn(Integer elementEndingColumn) {
		this.elementEndingColumn = elementEndingColumn;
	}
	public ArrayList<String> getSourceCodeLines() {
		return sourceCodeLines;
	}
	public void setSourceCodeLines(ArrayList<String> sourceCodeLines) {
		this.sourceCodeLines = sourceCodeLines;
	}
	
	public Integer getCharacterCount_metric() {
		return characterCount_metric;
	}
	public void setCharacterCount_metric(Integer characterCount_metric) {
		this.characterCount_metric = characterCount_metric;
	}	
	
}
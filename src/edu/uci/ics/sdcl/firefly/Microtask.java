package edu.uci.ics.sdcl.firefly;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.lite.MicrotaskLite;
import edu.uci.ics.sdcl.firefly.util.CyclomaticComplexityCounter;

public class Microtask implements Serializable
{	
	private static final long serialVersionUID = 1L;
	protected String question;
	private String failureDescription;
	private String testCase;
	private CodeSnippet method;
	private CodeElement codeElement;
	private String codeElementType;
	private String snippetHightlights;
	private String callerHightlights;
	private String calleeHightlights;

	protected Vector<Answer> answerList;

	private Integer startingLine;
	private Integer startingColumn;
	private Integer endingLine;
	private Integer endingColumn;
	protected Integer ID;
	private String calleeFileContent;
	private Integer calleeLOCS;
	private String callerFileContent;
	private Integer callerLOCS;
	protected String fileName;
	private Integer LOC_CoveredByQuestion; //lines of code covered by the question
	private Integer LOC_Trimmed; //removed comments, closing curly brackets, isolated else statement 

	private String questionType;

	/**
	 * 
	 * Represents a question about one element (see CodeElement class) in a code snippet. The element is 
	 * localized by the line and column parameters provided.
	 * 
	 * @param codeElementTypeArg
	 * @param methodArg
	 * @param codeElement
	 * @param questionArg
	 * @param startingLineNumber 
	 * @param startingColumnNumber
	 * @param endingLineNumber
	 * @param endingColumnNumber
	 * @param ID
	 */
	public Microtask(String codeElementType, CodeSnippet method, CodeElement codeElement, String question, Integer startingLineNumber, 
			Integer startingColumnNumber, Integer endingLineNumber, Integer endingColumnNumber, Integer ID, String failureDescription, String testCase)
	{
		this.setCodeSnippet(method);
		this.setQuestion(question);
		this.setCodeElementType(codeElementType);
		this.codeElement = codeElement;
		this.startingLine = startingLineNumber;
		this.startingColumn = startingColumnNumber;
		this.endingLine = endingLineNumber;
		this.endingColumn = endingColumnNumber;
		this.answerList = new Vector<Answer>();
		this.ID = ID;
		this.failureDescription = failureDescription;
		this.testCase = testCase;
		this.fileName = method.getFileName();
		//this.cyclomaticComplexity = this.computeCyclomaticComplexity(method);
		this.LOC_CoveredByQuestion = this.endingLine - this.startingLine + 1;
		this.codeElement.setSourceCodeLines(this.extractLines(method));
		this.computeComplexityMetrics();
	}

	
	private void computeComplexityMetrics() {
		this.codeElement.setCharacterCount_metric(this.computeCharacterLenght(this.codeElement.getSourceCodeLines())); 
	}

	/** Simplified version with only the data needed to write a Session Report */
	public Microtask(String question, Integer ID, Vector<Answer> answerList, String fileName)	{
		this.answerList = answerList;
		this.ID = ID;
		this.question = question;
		this.fileName = fileName; 
	}


	/** Light version used during microtask execution */
/*	public Microtask(int microtaskId, String question,
			String failureDescription, String testCase, String snippetHightlights,
			String callerHightlights, String calleeHightlights,
			int startingColumn, int endingLine, int endingColumn,
			String calleeFileContent, int calleeLOCS, String callerFileContent,
			int callerLOCS, String fileName) {

		this.ID = new Integer(microtaskId);
		this.question = question;
		this.failureDescription = failureDescription;
		this.testCase = testCase;
		this.snippetHightlights = snippetHightlights;
		this.callerHightlights = callerHightlights;
		this.calleeHightlights = calleeHightlights;
		this.startingColumn = startingColumn;
		this.endingLine = endingLine;
		this.endingColumn = endingColumn;
		this.calleeFileContent = calleeFileContent;
		this.calleeLOCS = calleeLOCS;
		this.callerFileContent = callerFileContent;
		this.callerLOCS = callerLOCS;
		this.fileName = fileName;
		
	}*/

	public Microtask getSimpleVersion(){
		Vector<Answer> answerListCopy = new Vector<Answer>();
		for(Answer answer: this.getAnswerList()){
			answerListCopy.add(answer);
		}
		return new Microtask(this.getQuestion(),this.getID(),answerListCopy,this.getFileName());
	}

	public Integer getID(){
		return this.ID;
	}

	public String getQuestion()
	{
		return question;
	}

	public void setQuestion(String question)
	{
		this.question = question;
	}

	public String getCodeElementType()
	{
		return codeElementType;
	}

	public void setCodeElementType(String type)
	{
		this.codeElementType = type;
	}

	public CodeSnippet getCodeSnippet()
	{
		return method;
	}

	public CodeElement getCodeElement() {
		return codeElement;
	}

	public void setCodeSnippet(CodeSnippet method)
	{
		this.method = method;
	}

	public Vector<Answer> getAnswerList()
	{
		return answerList;
	}

	public void setAnswerList(Vector<Answer> answerList)
	{
		this.answerList = answerList;
	}

	public void addAnswer(Answer answer){
		this.answerList.add(answer);
	}

	/* getters for the position */
	public Integer getStartingLine() {
		return startingLine;
	}

	public Integer getStartingColumn() {
		return startingColumn;
	}

	public Integer getEndingLine() {
		return endingLine;
	}

	public Integer getEndingColumn() {
		return endingColumn;
	}

	public int getNumberOfAnswers() {
		return this.answerList.size();
	}

	public String getFailureDescription() {
		return failureDescription;
	}

	public String getTestCase(){
		return testCase;
	}

	public String getSnippetHightlights() {
		return snippetHightlights;
	}

	public void setSnippetHightlights(String snippetHightlights) {
		this.snippetHightlights = snippetHightlights;
	}

	public String getCallerHightlights() {
		return callerHightlights;
	}

	public void setCallerHightlights(String callerHightlights) {
		this.callerHightlights = callerHightlights;
	}


	public String getCalleeHightlights() {
		return calleeHightlights;
	}

	public void setCalleeHightlights(String calleeHightlights) {
		this.calleeHightlights = calleeHightlights;
	}

	public void setCallerFileContent(String fileContent){
		this.callerFileContent = fileContent;
		String contentLines[] = callerFileContent.toString().split("\r\n|\r|\n");
		this.callerLOCS = new Integer(contentLines.length);		 
	}

	public String getCallerFileContent(){
		return this.callerFileContent;
	}

	public Integer getCallerLOCS(){
		return this.callerLOCS;
	}

	public void setCalleeFileContent(String fileContent){
		this.calleeFileContent = fileContent;
		String contentLines[] = calleeFileContent.toString().split("\r\n|\r|\n");
		this.calleeLOCS = new Integer(contentLines.length);		 
	}

	public String getCalleeFileContent(){
		return this.calleeFileContent;
	}

	public Integer getCalleeLOCS(){
		return this.calleeLOCS;
	}

	public Answer getAnswerByUserId(String workerId) {
		Answer foundAnswer=null;
		for(Answer answer: answerList){
			if(answer!=null && answer.getWorkerId().matches(workerId)){
				foundAnswer = answer;
				break;
			}
		}
		return foundAnswer;
	}

	public int getAnswerCountByUserId(String workerId) {
		int count = 0;
		for(Answer answer: answerList){
			if(answer!=null && answer.getWorkerId().matches(workerId)){
				count++;
			}
		}
		return count;
	}

	public List<String> getWorkerIds(){
		List<String> workerIDs = new ArrayList<String>();
		for (Answer answer : answerList) {
			if(answer != null){
				workerIDs.add(answer.getWorkerId());
			}
		}
		return workerIDs;
	}

	public String getFileName() {
		return this.fileName;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public ArrayList<String> getAnswerOptions(){
		ArrayList<String> list = new ArrayList<String>();
		for(Answer answer : this.answerList){
			list.add(answer.getOption());
		}
		return list;
	}

	public Integer getLOC_CoveredByQuestion() {
		return LOC_CoveredByQuestion;
	}

	public void setLOC_CoveredByQuestion(Integer locs) {
		this.LOC_CoveredByQuestion = locs;
	}
	
	/** 
	 * Not used anymore.
	 * @param method
	 * @return the line just after the Javadoc comment section
	 */
	private int lineAfterComments(CodeSnippet method){
		
		int position=0;
		boolean hasJavadoc=false;
		String[] lineList = method.codeSnippetFromFileContent.split("\r\n|\r|\n");
		
		for(String line:lineList){
			if(line.indexOf("*/")>=0){
				hasJavadoc = true;
				break;
			}
			position++;
		}
		
		if(hasJavadoc)
			return position+1; //skip the javadoc ending line
		else
			return 0; //assumes code starts at the first line
	}
	
	private ArrayList<String> extractLines(CodeSnippet method){
		
		int start = this.startingLine - method.getElementStartingLine();
		int end = start + this.endingLine - this.startingLine; //start + this.LOC_CoveredByQuestion; //this.endingLine - methodDeclarationStartingLine;
				
		ArrayList<String> list = new ArrayList<String>();

		String[] lineList = method.codeSnippetFromFileContent.split("\r\n|\r|\n");

		for(int i=start;i<=end;i++){
			list.add(lineList[i]);
		}
		
		return list;
	}

	
	public Integer computeCharacterLenght(ArrayList<String> lineList) {
		int count = 0;
		for(String line:lineList) {
			 count = count + line.trim().length();
		}
		return new Integer(count);
	}

}
package edu.uci.ics.sdcl.firefly;
import java.io.Serializable;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.util.CyclomaticComplexityCounter;

import java.util.ArrayList;
import java.util.HashMap;

public class CodeSnippet implements Serializable
{
	
	private static final long serialVersionUID = 1L;
	protected String packageName;					// package name
	protected String className; 					// class name
	protected String fileName;						// file name
	protected String methodBody;					// whole content of method
	
	protected Vector<CodeSnippet> callers;		// all callers of this method within the file
	protected Vector<CodeSnippet> callees;		// all callees of this method within the file
	
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
	
	protected Boolean returnStatement;				// true if there is a return value
	protected MethodSignature methodSignature;		// parsed method declaration
	protected Vector<CodeElement> elements;	// list of statements
	
	//Metrics to evaluate code snippets
	protected Integer CyclomaticComplexity; //McCabe's complexity - counts "if|else|for|while|case|catch|\\|\\|\\?|&&";
	protected Integer sizeCharacters; //Don't consider empty spaces
	private Integer LOCS;
	
	protected String codeSnippetFromFileContent;	// the string that has the whole body of the method
	private HashMap<String, CodeSnippet> calleesMap;

	private final static String newline = System.getProperty("line.separator");	// Just to jump line @toString
	
	/* constructor for methods without body */
	public CodeSnippet(String packageName, String className, MethodSignature methodSignature, 
			String methodBody, Boolean returnStatement,
			Integer elementStartingLineArg, Integer elementStartingColumnArg, 
			Integer elementEndingLineArg, Integer elementEndingColumnArg)
	{
		this.packageName = packageName;
		this.className = className;
		this.methodSignature = methodSignature;
		this.methodBody = methodBody;
		this.returnStatement = returnStatement;
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
		
		this.elements = new Vector<CodeElement>();
		this.callers = new Vector<CodeSnippet>();
		this.callees = new Vector<CodeSnippet>();
	}
	
	/* constructor for methods with body */
	public CodeSnippet(String packageName, String className, MethodSignature methodSignature, 
			String methodBody, Boolean returnStatement, 
			Integer elementStartingLineArg, Integer elementStartingColumnArg, 
			Integer elementEndingLineArg, Integer elementEndingColumnArg,
			Integer bodyStartingLineArg, Integer bodyStartingColumnArg,
			Integer bodyEndingLineArg, Integer bodyEndingColumnArg)
	{
		this.packageName = packageName;
		this.className = className;
		this.methodSignature = methodSignature;
		this.methodBody = methodBody;
		this.returnStatement = returnStatement;
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
		
		this.elements = new Vector<CodeElement>();
		this.callers = new Vector<CodeSnippet>();
		this.callees = new Vector<CodeSnippet>();
	}

	@Override
	public String toString() {
		return "CodeSnippet" 
				+ newline + "package name: " + packageName 
				+ newline + "className: " + className 
				+ newline + "methodSignature: " + methodSignature 
				+ newline + "returnStatment: " + returnStatement 
				+ newline + "methodBody: "+ methodBody
				+ newline+ "statements: "	+ elements
				+ newline+ "Element startingLine: "+ elementStartingLine.toString()
				+ newline+ "Element startingColumn: "+ elementStartingColumn.toString()
				+ newline+ "Element endingLine: "+ elementEndingLine.toString()
				+ newline+ "Element endingColumn: "+ elementEndingColumn.toString()		
				+ newline+ "Body startingLine: "+ bodyStartingLine.toString()
				+ newline+ "Body startingColumn: "+ bodyStartingColumn.toString()
				+ newline+ "Body endingLine: "+ bodyEndingLine.toString()
				+ newline+ "Body endingColumn: "+ bodyEndingColumn.toString();
	}

	
	public void addElement(CodeElement element){
		this.elements.add(element);
	}

	public boolean isEqualTo(CodeSnippet targetSnippet) {
		MethodSignature targetMethod = targetSnippet.getMethodSignature();
		return this.methodSignature.isEqualTo(targetMethod);
	}
	
	public void addCaller(CodeSnippet callerSnippet)
	{
		if(!this.callers.contains(callerSnippet))
			this.callers.add(callerSnippet);
	}
	
	public void addCallee(CodeSnippet calleeSnippet)
	{
		if(!this.callees.contains(calleeSnippet)){
			this.callees.add(calleeSnippet);
		}
	}
	
	public Vector<CodeSnippet> getCallers()
	{
		return this.callers;
	}
	
	public Vector<CodeSnippet> getCallees()
	{
		return this.callees;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodBody() {
		return methodBody;
	}

	public void setMethodBody(String methodBody) {
		this.methodBody = methodBody;
	}

	public Boolean getReturnStatement() {
		return returnStatement;
	}

	public void setReturnStatement(Boolean returnStatement) {
		this.returnStatement = returnStatement;
	}

	public MethodSignature getMethodSignature() {
		return methodSignature;
	}

	public void setMethodSignature(MethodSignature methodSignature) {
		this.methodSignature = methodSignature;
	}

	public Vector<CodeElement> getStatements() {
		return elements;
	}

	public void setStatements(Vector<CodeElement> statements) {
		this.elements = statements;
	}
	/* Getters for the position */
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
	// getting codeSnippet from file content
	public String getCodeSnippetFromFileContent() {
		return codeSnippetFromFileContent;
	}

	public void setCodeSnippetFromFileContent(String codeSnippetFromFileContent) {
		this.codeSnippetFromFileContent = codeSnippetFromFileContent;
		String sourceLines[] = codeSnippetFromFileContent.split("\r\n|\r|\n");
		this.LOCS = sourceLines.length;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Vector<CodeElement> getElementsOfType(String type) {
		Vector<CodeElement> list = new Vector<CodeElement>();
		
		for(CodeElement element : this.elements){
			if(element.getType().matches(type))
				list.add(element);
		}
		
		return list;
	}

	private static boolean isMethodCallee(Vector<CodeSnippet> calleeList, String methodName, int numberOfParameters){
		boolean found=false;
		int i=0;
		while((!found) && i<calleeList.size()){
			CodeSnippet snippet = calleeList.get(i);
			///System.out.println("callee: "+ snippet.getMethodSignature().getName()+" params: "+snippet.getMethodSignature().getParameterList().size() );
			//System.out.println("method : " + methodName+" params: "+ numberOfParameters);
			
			if(snippet.getMethodSignature().getName().matches(methodName)  &&
			(snippet.getMethodSignature().getParameterList().size()==numberOfParameters))
				found=true;
			i++;
		}
		return found;
	}
	
	public static boolean listContainsMethod(Vector<CodeSnippet> calleeList,
			MyMethodCall methodCallElement) {
		return CodeSnippet.isMethodCallee(calleeList, methodCallElement.getName(), methodCallElement.getNumberOfParameters());
	}
	
	public static boolean listContainsMethod(Vector<CodeSnippet> calleeList,
			CodeSnippet snippet) {
		return CodeSnippet.isMethodCallee(calleeList, snippet.getMethodSignature().getName(), snippet.getMethodSignature().getParameterList().size());
	}

	public Integer getLOCS() {
		return this.LOCS;
	}
	
	public ArrayList<String> extractLines(){
		String[] lineList = this.codeSnippetFromFileContent.split("\r\n|\r|\n");
		
		int start = 0;
		int end =  this.elementEndingLine - this.elementStartingLine;
		
		ArrayList<String> list = new ArrayList<String>();
		
		for(int i=start;i<end;i++){
			list.add(lineList[i]);
		}
		return list;
	}
	
		
}
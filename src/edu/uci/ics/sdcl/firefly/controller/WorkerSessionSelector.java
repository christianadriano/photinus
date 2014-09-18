package edu.uci.ics.sdcl.firefly.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import edu.uci.ics.sdcl.firefly.CodeElement;
import edu.uci.ics.sdcl.firefly.CodeSnippet;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.MyMethodCall;
import edu.uci.ics.sdcl.firefly.util.MethodMatcher;
import edu.uci.ics.sdcl.firefly.util.PositionFinder;

/**
 * Selects the a WorkerSession for the first time or an active one.
 * 
 * @author Christian Adriano
 *
 */
public class WorkerSessionSelector {

	public WorkerSessionSelector(){}


	/** Load all data from a microtask to the request 
	 * 
	 * @param request
	 * @param task
	 * @return the new request with data to be displayed on the web page
	 */
	public HttpServletRequest generateRequest(HttpServletRequest request, Microtask task){
		
		System.out.println("Retrieved microtask id:"+task.getID()+" answers: "+task.getAnswerList().toString());
		System.out.println("Retrieved microtask bug report:" + task.getFailureDescription() +  " from fileName: "+task.getCodeSnippet().getFileName());
		
		request.setAttribute("microtaskId", task.getID());  
		request.setAttribute("fileName", task.getCodeSnippet().getFileName());
		
		String fileContent = task.getCodeSnippet().getCodeSnippetFromFileContent();	// Taking the method content instead of the whole file
		request.setAttribute("bugReport", task.getFailureDescription());
		request.setAttribute("question", task.getQuestion());
		request.setAttribute("source", fileContent); 	// content displayed on the first ACE Editor
		
		String sourceLines[] = fileContent.split("\r\n|\r|\n");
		System.out.println("sourceLOCS=" + new Integer(sourceLines.length));
		request.setAttribute("sourceLOCS", new Integer(sourceLines.length));
		
		
		// chasing method positions for highlighting callees on Main Ace Editor
		if (!task.getCodeSnippet().getCallees().isEmpty()){
			StringBuilder commandStorage = new StringBuilder();
			String calleesOnMainCommand = null;
			String methodNameInQuestion = "";
			boolean isMethodInvocationTask = task.getCodeElementType().matches(CodeElement.METHOD_INVOCATION);
			if(isMethodInvocationTask){
				methodNameInQuestion = ((MyMethodCall) task.getCodeElement()).getName();
				System.out.println("Task Method Name: "+methodNameInQuestion); 
			}
			
			for (CodeSnippet callee : task.getCodeSnippet().getCallees()) {
			
				//Ignore method calls to the method that is being questioned, So we avoid highlighting it twice.
				if((!isMethodInvocationTask) || (! methodNameInQuestion.matches(callee.getMethodSignature().getName()))) 
					commandStorage.append( methodChaser(callee.getMethodSignature().getName(), fileContent, false) );
			}
			if(commandStorage.length()>0)
				calleesOnMainCommand = commandStorage.toString().substring(0, commandStorage.length()-1);	// -1 to remove last '#'
			System.out.println("calleesOnMain to be executed: " + calleesOnMainCommand);
					
			request.setAttribute("calleesOnMain", calleesOnMainCommand);
		} else
			request.setAttribute("calleesOnMain", null);

		/* preparing the second ACE Editor - callers */
		StringBuilder newFileContent;
		StringBuilder highlight;
		String highlightCallerCommand;

		String newline = System.getProperty("line.separator");
		if ( task.getCodeSnippet().getCallers().isEmpty() ){
			newFileContent = null;
			highlightCallerCommand = null;
			request.setAttribute("caller", null);
		} 
		else{
			newFileContent = new StringBuilder();
			highlight = new StringBuilder();
			highlightCallerCommand = new String();
			newFileContent.append("METHOD '" + task.getCodeSnippet().getMethodSignature().getName() + 
					"' IS CALLED BY THE FOLLOWING METHOD(S):");
			newFileContent.append(newline);
			newFileContent.append(newline);
			for (CodeSnippet caller : task.getCodeSnippet().getCallers()) {
				newFileContent.append(caller.getCodeSnippetFromFileContent());	// appending caller
				if ( task.getCodeSnippet().getCallers().indexOf(caller) < (task.getCodeSnippet().getCallers().size()-1) ){
					newFileContent.append(newline);	// appending two new lines in case it is NOT the last caller 
					newFileContent.append(newline);
				}
			}
			// chasing method positions for highlighting purposes
			//CodeSnippet caller = task.getMethod();
			//hightlight.append()
			highlight.append( methodChaser(task.getCodeSnippet().getMethodSignature().getName(), newFileContent.toString(), false) );
			if(highlight.length()>0)
				highlightCallerCommand = highlight.toString().substring(0, highlight.length()-1);	// -1 to remove last '#'
			System.out.println("Command to be executed: " + highlightCallerCommand);
			
			String callerLines[] = newFileContent.toString().split("\r\n|\r|\n");
			System.out.println("callerLOCS=" + new Integer(callerLines.length));
			request.setAttribute("callerLOCS", new Integer(callerLines.length));
			
			request.setAttribute("caller", newFileContent.toString());
		}
		// passing to jsp
		request.setAttribute("positionsCaller", highlightCallerCommand);


		/* preparing the third ACE Editor - callees */
		String highlightCalleeCommand;
		if ( task.getCodeSnippet().getCallees().isEmpty() ){
			newFileContent = null;
			highlightCalleeCommand = null;
			request.setAttribute("callee", null);
		} 
		else{
			newFileContent = new StringBuilder();
			highlight = new StringBuilder();
			highlightCalleeCommand = null;
			newFileContent.append("METHOD '" + task.getCodeSnippet().getMethodSignature().getName() + 
					"' CALLS THE FOLLOWING METHOD(S):");
			newFileContent.append(newline);
			newFileContent.append(newline);
			for (CodeSnippet callee : task.getCodeSnippet().getCallees()) {
				newFileContent.append(callee.getCodeSnippetFromFileContent());	// appending callee
				if ( task.getCodeSnippet().getCallees().indexOf(callee) < (task.getCodeSnippet().getCallees().size()-1) ){
					newFileContent.append(newline);	// appending two new lines in case it is NOT the last callee 
					newFileContent.append(newline);
				}
			}	
			// chasing method positions for highlighting purposes
			for (CodeSnippet callee : task.getCodeSnippet().getCallees()) {
				highlight.append( methodChaser(callee.getMethodSignature().getName(), newFileContent.toString(), true) );
			}
			if(highlight.length()>0)
				highlightCalleeCommand = highlight.toString().substring(0, highlight.length()-1);	// -1 to remove last '#'
			System.out.println("Command to be executed: " + highlightCalleeCommand);
			
			String calleeLines[] = newFileContent.toString().split("\r\n|\r|\n");
			System.out.println("calleeLOCS=" + new Integer(calleeLines.length));
			request.setAttribute("calleeLOCS", new Integer(calleeLines.length));
			request.setAttribute("callee", newFileContent.toString());
		}
		// passing to jsp
		request.setAttribute("positionsCallee", highlightCalleeCommand);

		 

		
		request.setAttribute("explanation",""); //clean up the explanation field.

		request.setAttribute("startLine", task.getStartingLine());
		request.setAttribute("startColumn", task.getStartingColumn());
		request.setAttribute("endLine", task.getEndingLine());
		request.setAttribute("endColumn", task.getEndingColumn());

		request.setAttribute("methodStartingLine", task.getCodeSnippet().getMethodSignature().getLineNumber());
		
		return request;
	}
	
	
	
	/**
	 * @return the position of all the methodName occurrences on the following 
	 * format: startingLine#startingColumn#endingLine#endingColumn#... and so on
	 * @param methodName
	 * @param fileContent
	 * @param methodCallStrict
	 * @return
	 */
	protected String methodChaser(String methodName, String fileContent, boolean methodCallStrict)
	{
		StringBuilder highlightCommand = new StringBuilder();
		String stringLines[] = fileContent.split("\r\n|\r|\n");
		int currentLine = 0;
		//		System.out.println("method name: " + methodName);
		for (String line : stringLines) {
			currentLine++;
			//			System.out.println("Line " + currentLine + ": " + line);
			if (line == null || line.length() <= 0)	continue;	// if line does not have any content, just skip it
			if (methodCallStrict)
				if (!line.contains("public") && !line.contains("protected") && !line.contains("private"))
					continue;	// looking just for methodCalls not other occurrences when methodStrict is true (that will be skipped)
			String[] stringWords = line.split("\\s+");			// splitting lines into words
			List<String> wordsPerLine = stringArraytoList(stringWords);
			int wordLengthAccumulator = 0;	// just for the edge case of the same function being executed more than once per line
			
			for (int index = 0; index < wordsPerLine.size(); index++) {
				String codeLine = wordsPerLine.get(index);
				if (( codeLine.contains(methodName)) && !MethodMatcher.containsDifferentMethod(codeLine, methodName)){
					
					//Commented code below highlights only the method name. Current working code highlights the parameters too
					//Integer startLine = currentLine;
					//Integer startColumn = line.indexOf(methodName, wordLengthAccumulator);
					//Integer endColumn = startColumn+ methodName.length();
					//Integer endLine = startLine;
					//System.out.println("compute for caller: "+ startLine+","+startColumn+","+endLine+ ","+ endColumn);

					//					System.out.println("passing column: " + line.indexOf(methodName, wordLengthAcumulator) + " W.A.: " + wordLengthAcumulator);
					PositionFinder positionFinder = 
							new PositionFinder(currentLine, line.indexOf(methodName, wordLengthAccumulator), stringLines, '(', ')');
					positionFinder.computeEndPosition();

					highlightCommand.append(positionFinder.getStartingLineNumber()+"#");
					highlightCommand.append(line.indexOf(methodName, wordLengthAccumulator)+"#");// because I want the start column of the word
					highlightCommand.append(positionFinder.getEndingLineNumber()+"#");
					highlightCommand.append(positionFinder.getEndingColumnNumber()+"#");
					//					wordLengthAccumulator = invocationPosition.getStartingColumnNumber();
					//					System.out.println("Positions appended: " + highlightCommand + " WA: " + wordLengthAcumulator);
				}
				wordLengthAccumulator = line.indexOf(wordsPerLine.get(index), wordLengthAccumulator) + wordsPerLine.get(index).length();
			}
		}
		return highlightCommand.toString();
	}

	
	
	
	private List<String> stringArraytoList( String texts[] ) 
	{
		List<String> list = new ArrayList<String>();
		// transforming into List and removing null and empty entries
		for(String string : texts) {
			if( string != null && string.length() > 0) {
				list.add(string);
			}
		}
		// returning the list
		return list;
	}
	
}

package edu.uci.ics.sdcl.firefly;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.util.PathUtil;

/**
 *  Generates a list of code snippets from all the program statements in a source code.
 *   
 * @author adrianoc
 *
 */
public class CodeSnippetFactory { 
	private String fileContent;
	private String fileName;
	private String[] fileContentPerLine;
	private Vector<CodeSnippet> codeSnippetList= new Vector<CodeSnippet>();

	//public CodeSnippetFactory(String folderPathArg)
	//	{
	//		this.folderPath = folderPathArg;
	//	}

	public CodeSnippetFactory(String fileName, String fileContent){
		this.fileContent = fileContent;//this.sanitizeContent();
		this.fileName = fileName;
		this.fileContentPerLine = this.fileContent.split("\r\n|\r|\n");
	}

	/** Generates code snippets for a file content  in memory (attribute fileContent) */
	public Vector<CodeSnippet> generateSnippetsForFile()
	{
		this.codeSnippetList = new Vector<CodeSnippet>(); //Cleans up the snippet list
		@SuppressWarnings("unused")
		JavaParser parser = new JavaParser(this);
		this.setCallers(this.codeSnippetList);
		this.setMethodTexts(this.codeSnippetList);
		for (CodeSnippet codeSnippet : this.codeSnippetList) {
			codeSnippet.setFileName(PathUtil.removePath(fileName, true));
		}
		return this.codeSnippetList;
	}

	/**
	 *  Generates code snippets for all files in a the provide folder
	 * @param folderPath the folder where all files are
	 * 
	 */
	public Vector<CodeSnippet> generateSnippets(String folderPath)
	{
		try
		{
			ParseFilesInDir(folderPath);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return codeSnippetList;
	}

	public void printAll()
	{
		System.out.println("Printing all methods");
		for (CodeSnippet method : codeSnippetList)
		{
			System.out.println("--");
			System.out.println(method);
		}
	}

	/**
	 * Loops over a folder to get file list and parse all of the files in it.
	 * @param folderPath
	 * @throws IOException
	 */
	private void ParseFilesInDir(String folderPath) throws IOException
	{
		//				File dirs = new File("."); 				// For files under this folder
		//				String dirPath = dirs.getCanonicalPath() + File.separator+"src"+File.separator;
		//				System.out.println(dirPath);
		File root = new File(folderPath);
		File[] files = root.listFiles ( );
		String filePath = null;
		if( files != null)
		{
			for (File f : files ) {
				filePath = f.getAbsolutePath();
				if(f.isFile()){
					/* this implementation require endLine to be update by its codeSnippet constructor */
					this.fileContent = SourceFileReader.readFileToString(filePath);
					this.fileContentPerLine = fileContent.split("\r\n|\r|\n");
					@SuppressWarnings("unused")
					JavaParser parser = new JavaParser(this);
				}
			}
		}
	}

	public void setCallers(Vector<CodeSnippet> codeSnippetList2)
	{
//		System.out.println(" <> ");
		/* building and populating callers-callees structure */
		HashMap<CodeSnippet, ArrayList<MyMethodCall>> callersCalles = new HashMap<CodeSnippet, ArrayList<MyMethodCall>>();
		for (CodeSnippet codeSnippet : codeSnippetList2) {	// for each method (caller)
			ArrayList<MyMethodCall> callees = new ArrayList<MyMethodCall>();
//			System.out.println("Caller: " + codeSnippet.getMethodSignature().getName());
			for (CodeElement codeElement : codeSnippet.getStatements()) {	// find the callees (within the method)
				if (CodeElement.METHOD_INVOCATION == codeElement.getType())
				{
					MyMethodCall methodInvocation = (MyMethodCall)codeElement;
					
					callees.add(methodInvocation);
//					System.out.println("--callee: " + methodInvocation.getName()); 
				}
			}
			callersCalles.put(codeSnippet, callees); //add to the hash map
//			System.out.println(" <> ");
		}

		/* Updating the methods callers */
		for (CodeSnippet codeSnippet : codeSnippetList2) {
			String methodName = codeSnippet.getMethodSignature().getName();	// callee name
			Iterator<Entry<CodeSnippet, ArrayList<MyMethodCall>>> i = callersCalles.entrySet().iterator();
			while(i.hasNext()) 	// searching for caller (from other methods within this codeSnippet list)
			{
				Map.Entry<CodeSnippet, ArrayList<MyMethodCall>> me = (Map.Entry<CodeSnippet, ArrayList<MyMethodCall>>)i.next();
				//Check if the MethodCall matches the Snippet (check name and number of parameters)
				ArrayList<MyMethodCall> list = me.getValue();
				for(MyMethodCall invocation: list){
					if(invocation.getName().matches(methodName)&& 
							invocation.getNumberOfParameters()==codeSnippet.getMethodSignature().getParameterList().size())
					{															// this methodName is called so...
					codeSnippet.addCaller(me.getKey());		// ...update its caller list field
					me.getKey().addCallee(codeSnippet); 	// ...update its caller adding it as a callee
				}
				}
			}
		}

	}

	// assuming that the method is NOT OVERLOADED
	private void setMethodTexts(Vector<CodeSnippet> codeSnippetList2)
	{
		for (CodeSnippet codeSnippet : codeSnippetList2) {
			int startingLine = codeSnippet.getElementStartingLine() -1;	// -1 because the vector starts from 0
			int endingLine = codeSnippet.getBodyEndingLine() -1;	// -1 because the vector starts from 0
			/* getting method content */
			Scanner scanner = new Scanner(this.fileContent);
			int j = 0;
			StringBuilder builder = new StringBuilder();
			String newline = System.getProperty("line.separator");
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if ( (j >= startingLine) && (j <= endingLine) )
				{
					builder.append(line + newline);
//					System.out.println("line: " + line);
				}
				j++;
				if (j > endingLine) break;
//				System.out.println("j: " + j);
			}
			scanner.close();
			/* updating on respective CodeSnippet */
			codeSnippet.setCodeSnippetFromFileContent(builder.toString());
			//System.out.println("@@@@@@@@@@@@@@@@@@@");
			//System.out.println("setMethodTexts: "+ codeSnippet.getCodeSnippetFromFileContent());
			
			
		}
		//System.out.println();
	}
	
	public String getFileName(){
		return fileName;
	}

	public String[] getFileContentPerLine() {
		return fileContentPerLine;
	}

	public Vector<CodeSnippet> getCodeSnippetList()
	{
		return codeSnippetList;
	}

	public void addToCodeSnippetList(CodeSnippet method)
	{
		this.codeSnippetList.add(method);
	}

	public String getFileContent()
	{
		return fileContent;
	}
}

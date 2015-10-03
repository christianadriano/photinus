package test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Hashtable;

import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.sdcl.firefly.CodeElement;
import edu.uci.ics.sdcl.firefly.CodeSnippet;
import edu.uci.ics.sdcl.firefly.FileDebugSession;
import edu.uci.ics.sdcl.firefly.MethodParameter;
import edu.uci.ics.sdcl.firefly.MethodSignature;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.storage.MicrotaskStorage;

public class MicrotaskMementoTest {
	private  HashMap<String, FileDebugSession> debugSessionMap = new HashMap<String, FileDebugSession>();
	private Hashtable<Integer, Microtask> microtaskMap;
	private String fileName = "SimpleSampleCode.java";

	@Before
	public void setUp() throws Exception {
		MethodSignature signature = new MethodSignature("factorial", "public");
		MethodParameter arg1, arg2;
		arg1 = new MethodParameter("Integer", "Seed");
		arg2 = new MethodParameter("Integer", "Iterations");
		signature.addMethodParameters(arg1);
		signature.addMethodParameters(arg2);

		StringBuffer buffer = new StringBuffer("public Integer factorial(Integer Seed, Integer Iterations){");
		buffer.append("\n");
		buffer.append("if(Seed!=null){");
		buffer.append("\n");
		buffer.append("int aux=1;");
		buffer.append("\n");
		buffer.append("for (int i=0;i<Iterations.intValue();i++){");
		buffer.append("\n");
		buffer.append("aux =  aux * Seed;");
		buffer.append("\n");
		buffer.append("	}");
		buffer.append("\n");
		buffer.append("	return new Integer(aux);");
		buffer.append("\n");
		buffer.append("}");
		buffer.append("\n");
		buffer.append("else return null;");
		buffer.append("\n");
		buffer.append("}");

		String body = buffer.toString().substring(buffer.toString().indexOf('{'));
		
		String questionArg = "Is there maybe something wrong in the declaration of function 'factorial' "
				+ "at line 20 (e.g., requires a parameter that is not listed, needs different parameters to "
				+ "produce the correct result, specifies the wrong or no return type, etc .)?";
		
		CodeSnippet codeSnippetFactorial = new CodeSnippet("sample", "SimpleSampleCode", signature, body, true, 
				7, 0, 7, 27, 7, 27, 10, 0); 
		Microtask mtask = new Microtask(CodeElement.METHOD_INVOCATION, codeSnippetFactorial, null, questionArg, 20, 0, 20, 58, 1,"failure description", questionArg);

		//Create the data structure
		this.microtaskMap =  new Hashtable<Integer,Microtask>();
		microtaskMap.put(new Integer(1),mtask);
		FileDebugSession debugMap = new FileDebugSession(fileName,body, microtaskMap);
		
		this.debugSessionMap.put(fileName, debugMap);
	}

	@Test
	public void testCreateNewPersistentFile() {

		MicrotaskStorage memento = MicrotaskStorage.initializeSingleton();;
		memento.insert(fileName, this.debugSessionMap.get(fileName));

		 FileDebugSession debugMap = memento.read(fileName);
		 if((debugMap!=null) && (debugMap.getMicrotaskMap()!=null)){
			 Hashtable<Integer, Microtask> mMap = debugMap.getMicrotaskMap();
			Integer key = new Integer (1);
			Microtask expectedTask = this.microtaskMap.get(key);
			Microtask actualTask = mMap.get(key);
			
			assertEquals(expectedTask.getCodeSnippet().getClassName(),actualTask.getCodeSnippet().getClassName().toString());
		}
		else
			fail("review test setup");
	}

	@Test
	public void testRemoveDebugSession() {
		MicrotaskStorage memento = MicrotaskStorage.initializeSingleton();;
		memento.insert(fileName, this.debugSessionMap.get(fileName));
		
		// memento.remove(fileName);
		 FileDebugSession debugMap = memento.read(fileName);
		 assertNull(debugMap);
	}
}

package test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.sdcl.firefly.FileDebugSession;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;
import edu.uci.ics.sdcl.firefly.servlet.FileUploadServlet;
import edu.uci.ics.sdcl.firefly.storage.MicrotaskStorage;

/**
 * Objective is to test if lines covered by question and cyclomatic complexity were correctly stored
 * @author christian adriano
 *
 */
public class FileUploadTest {

	MicrotaskStorage microtaskStorage;

	@Before
	public void setUpOnce() {
		microtaskStorage = MicrotaskStorage.initializeSingleton();
		microtaskStorage.cleanUp();
	}

	@Test
	public void test() {

		FileUploadServlet servlet = new FileUploadServlet();
		servlet.bulkUpload();

		//restore data
		//Microtask ID 7

		Set<String> sessionNames = microtaskStorage.retrieveDebuggingSessionNames();
		Iterator<String> iter = sessionNames.iterator();
		while(iter.hasNext()){
			String key = iter.next();
			Hashtable<String,FileDebugSession> map = microtaskStorage.readAllDebugSessions();
			FileDebugSession  debugSession = map.get(key);
			Hashtable<Integer, Microtask> serializedMicrotaskMap = debugSession.getMicrotaskMap();
			Microtask mtask = serializedMicrotaskMap.get(new Integer(7));
			if(mtask!=null){
				Integer cyclomaticComplexity = mtask.getCyclomaticComplexity();
				org.junit.Assert.assertTrue(cyclomaticComplexity!=0);
			}
		}
	}



}
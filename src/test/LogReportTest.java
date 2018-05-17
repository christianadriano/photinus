package test;

import static org.junit.Assert.*;

import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.report.voting.LogAnalysis;
import edu.uci.ics.sdcl.firefly.report.voting.LogData;
import edu.uci.ics.sdcl.firefly.report.voting.LogAnalysis.Counter;

public class LogReportTest {

	int numberOfExpectedMicrotasks;

	@Before
	public void setup(){
		this.numberOfExpectedMicrotasks=450;
	}

	//@Test
	public void test_prod2() {
		//String path = "C:\\Users\\Christian Adriano\\Dropbox (PE-C)\\3.Research\\1.Fall2014-Experiments\\Production-1\\Oct25\\SnapShot12\\";
		String path = "C:\\Users\\adrianoc\\Dropbox (PE-C)\\3.Research\\1.Fall2014-Experiments\\Production-2October25\\1Final\\";
		LogData data = new LogData(false,new Double(12000000).doubleValue());
		data.processLogProduction2(path);

		assertEquals(2577,data.getConsents());
		assertEquals(234,data.getSurveys());
		assertEquals(1975,data.getSkillTests());
		assertEquals(233, data.getClosedSessions());
		assertEquals(488, data.getOpenedSessions());
		assertEquals(2778, data.getNumberOfMicrotasks());		
	}


	//@Test
	public void test_prod1() {
		String path = "C:\\Users\\adrianoc\\Dropbox (PE-C)\\3.Research\\1.Fall2014-Experiments\\Production-1\\Oct25\\SnapShot12\\";
		//String path = "C:\\Users\\Christian Adriano\\Dropbox (PE-C)\\3.Research\\1.Fall2014-Experiments\\Production-2October25\\Snapshot12\\";
		LogData report =  new LogData(false,new Double(12000000).doubleValue());
		report.processLogProduction1(path);


		assertEquals(2199,report.getConsents());
		assertEquals(252,report.getSurveys());
		assertEquals(1724,report.getSkillTests());
		assertEquals(254, report.getClosedSessions());
		assertEquals(500, report.getOpenedSessions());
		assertEquals(2927, report.getNumberOfMicrotasks());

	}

	@Test
	public void testTotalPerFile_NoFilter(){
		String path = "C:\\Users\\adrianoc\\Dropbox (PE-C)\\3.Research\\1.Fall2014-Experiments\\Production-2October25\\1Final\\";
		//String path = "C:\\Users\\Christian Adriano\\Dropbox (PE-C)\\3.Research\\1.Fall2014-Experiments\\Production-2October25\\Snapshot12\\";
		LogData data = new LogData(false, new Double(12000000));
		data.processLogProduction2(path);

		path = "C:\\Users\\adrianoc\\Dropbox (PE-C)\\3.Research\\1.Fall2014-Experiments\\Production-1\\Oct25\\SnapShot12\\";
		data.processLogProduction1(path);

		LogAnalysis analysis = new LogAnalysis(data);

		//Configure the MicrotaskMap
		data.workingMicrotaskMap = data.microtaskMap;
		Iterator<String> iter;

		//--------------TOTAL
		
		System.out.println("----TOTAL TESTING EXPECTED YES ----");
		iter = data.fileNameTaskRange.keySet().iterator();
		while(iter.hasNext()){
			String fileName = iter.next();
			Point point = data.fileNameTaskRange.get(fileName);
			analysis.expectedYesAnswers(point,fileName,data.microtaskMap,data.yesMap);
		}
		
		HashMap<String, Counter> totalMap = new HashMap<String, Counter>();
		
		iter = analysis.counterMap.keySet().iterator();
		while(iter.hasNext()){
			String fileName = iter.next();
			Counter counter = analysis.counterMap.get(fileName);
			totalMap.put(fileName, counter);
		}

		//-------------BATCH-1
		System.out.println("----BATCH-1 TESTING EXPECTED YES ----");
		data = new LogData(false,new Double(12000000).doubleValue());
		path = "C:\\Users\\adrianoc\\Dropbox (PE-C)\\3.Research\\1.Fall2014-Experiments\\Production-1\\Oct25\\SnapShot12\\";
		data.processLogProduction1(path);
		analysis = new LogAnalysis(data);
		//Configure the MicrotaskMap
		data.workingMicrotaskMap = data.microtaskMap;
		
		iter = data.fileNameTaskRange.keySet().iterator();
		while(iter.hasNext()){
			String fileName = iter.next();
			Point point = data.fileNameTaskRange.get(fileName);
			analysis.expectedYesAnswers(point,fileName,data.microtaskMap,data.yesMap);
		}
		
		HashMap<String, Counter> batch1Map = new HashMap<String, Counter>();
		
		iter = analysis.counterMap.keySet().iterator();
		while(iter.hasNext()){
			String fileName = iter.next();
			Counter counter = analysis.counterMap.get(fileName);
			batch1Map.put(fileName, counter);
		}
		
		
		//-------------BATCH-2
		System.out.println("----BATCH-2 TESTING EXPECTED YES ----");
		data = new LogData(false,new Double(12000000).doubleValue());
		path = "C:\\Users\\adrianoc\\Dropbox (PE-C)\\3.Research\\1.Fall2014-Experiments\\Production-2October25\\1Final\\";
		data.processLogProduction2(path);
		analysis = new LogAnalysis(data);
		//Configure the MicrotaskMap
		data.workingMicrotaskMap = data.microtaskMap;
		
		iter = data.fileNameTaskRange.keySet().iterator();
		while(iter.hasNext()){
			String fileName = iter.next();
			Point point = data.fileNameTaskRange.get(fileName);
			analysis.expectedYesAnswers(point,fileName,data.microtaskMap,data.yesMap);
		}
		
		HashMap<String, Counter> batch2Map = new HashMap<String, Counter>();
		
		iter = analysis.counterMap.keySet().iterator();
		while(iter.hasNext()){
			String fileName = iter.next();
			Counter counter = analysis.counterMap.get(fileName);
			batch2Map.put(fileName, counter);
		}
		
		//COMPARE
		iter = totalMap.keySet().iterator();
		while(iter.hasNext()){
			String fileName = iter.next();
			Counter counterTotal = totalMap.get(fileName);
			Counter counterBatch1 = batch1Map.get(fileName);
			Counter counterBatch2 = batch2Map.get(fileName);
			
			//Asserts
			assertEquals(counterTotal.fileName+ " total", counterTotal.totalAnswers,counterBatch1.totalAnswers+counterBatch2.totalAnswers,0);
			assertEquals(counterTotal.fileName+ " no",counterTotal.no,counterBatch1.no+counterBatch2.no,0);
			assertEquals(counterTotal.fileName+ " yes",counterTotal.yes,counterBatch1.yes+counterBatch2.yes,0);
			assertEquals(counterTotal.fileName+ " I cant tell",counterTotal.iCantTell,counterBatch1.iCantTell+counterBatch2.iCantTell,0);
				
		}
		
		
	}
	
	
	
	
	//FILTER COMPLETED SESSIONS ONLY
	@Test
	public void testTotalPerFile_CompletedSessions_Filter(){
		String path = "C:\\Users\\adrianoc\\Dropbox (PE-C)\\3.Research\\1.Fall2014-Experiments\\Production-2October25\\1Final\\";
		//String path = "C:\\Users\\Christian Adriano\\Dropbox (PE-C)\\3.Research\\1.Fall2014-Experiments\\Production-2October25\\Snapshot12\\";
		LogData data = new LogData(false,new Double(12000000).doubleValue());

		data.processLogProduction2(path);

		path = "C:\\Users\\adrianoc\\Dropbox (PE-C)\\3.Research\\1.Fall2014-Experiments\\Production-1\\Oct25\\SnapShot12\\";
		data.processLogProduction1(path);
		//Configure the MicrotaskMap
		data.computeMicrotaskFromCompleteSessions();
		
		LogAnalysis analysis = new LogAnalysis(data);
		
		Iterator<String> iter;

		//--------------TOTAL
		
		System.out.println("----TOTAL TESTING EXPECTED YES FILTERED----");
		iter = data.fileNameTaskRange.keySet().iterator();
		while(iter.hasNext()){
			String fileName = iter.next();
			Point point = data.fileNameTaskRange.get(fileName);
			analysis.expectedYesAnswers(point,fileName,data.completeSessions_microtaskMap,data.yesMap);
		}
		
		HashMap<String, Counter> totalMap = new HashMap<String, Counter>();
		
		iter = analysis.counterMap.keySet().iterator();
		while(iter.hasNext()){
			String fileName = iter.next();
			Counter counter = analysis.counterMap.get(fileName);
			totalMap.put(fileName, counter);
		}

		//-------------BATCH-1
		System.out.println("----BATCH1 TESTING EXPECTED YES FILTERED----");
		data = new LogData(false, new Double(12000000).doubleValue());
		path = "C:\\Users\\adrianoc\\Dropbox (PE-C)\\3.Research\\1.Fall2014-Experiments\\Production-1\\Oct25\\SnapShot12\\";
		data.processLogProduction1(path);
		//Configure the MicrotaskMap
		data.computeMicrotaskFromCompleteSessions();
		data.workingMicrotaskMap = data.completeSessions_microtaskMap;
		analysis = new LogAnalysis(data);
		
		iter = data.fileNameTaskRange.keySet().iterator();
		while(iter.hasNext()){
			String fileName = iter.next();
			//System.out.print("BUG REPORT: "+ fileName+", ");
			Point point = data.fileNameTaskRange.get(fileName);
			analysis.expectedYesAnswers(point,fileName,data.completeSessions_microtaskMap,data.yesMap);
		}
		
		HashMap<String, Counter> batch1Map = new HashMap<String, Counter>();
		
		iter = analysis.counterMap.keySet().iterator();
		while(iter.hasNext()){
			String fileName = iter.next();
			Counter counter = analysis.counterMap.get(fileName);
			batch1Map.put(fileName, counter);
		}
		
		
		//-------------BATCH-2
		System.out.println("----BATCH2 TESTING EXPECTED YES FILTERED----");
		data = new LogData(false,new Double(12000000).doubleValue());
		path = "C:\\Users\\adrianoc\\Dropbox (PE-C)\\3.Research\\1.Fall2014-Experiments\\Production-2October25\\1Final\\";
		data.processLogProduction2(path);
		//Configure the MicrotaskMap
		analysis = new LogAnalysis(data);
		data.computeMicrotaskFromCompleteSessions();
		iter = data.fileNameTaskRange.keySet().iterator();
		while(iter.hasNext()){
			String fileName = iter.next();
			Point point = data.fileNameTaskRange.get(fileName);
			analysis.expectedYesAnswers(point,fileName,data.completeSessions_microtaskMap,data.yesMap);
		}
		
		HashMap<String, Counter> batch2Map = new HashMap<String, Counter>();
		
		iter = analysis.counterMap.keySet().iterator();
		while(iter.hasNext()){
			String fileName = iter.next();
			Counter counter = analysis.counterMap.get(fileName);
			batch2Map.put(fileName, counter);
		}
		
		//COMPARE
		iter = totalMap.keySet().iterator(); 
		while(iter.hasNext()){
			String fileName = iter.next();
			//System.out.print("BUG REPORT: "+ fileName+", ");
			Counter counterTotal = totalMap.get(fileName);
			Counter counterBatch1 = batch1Map.get(fileName);
			Counter counterBatch2 = batch2Map.get(fileName);
						
			//Asserts
			assertEquals(counterTotal.fileName+ " total", counterTotal.totalAnswers,counterBatch1.totalAnswers+counterBatch2.totalAnswers,0);
			assertEquals(counterTotal.fileName+ " no",counterTotal.no,counterBatch1.no+counterBatch2.no,0);
			assertEquals(counterTotal.fileName+ " yes",counterTotal.yes,counterBatch1.yes+counterBatch2.yes,0);
			assertEquals(counterTotal.fileName+ " I cant tell",counterTotal.iCantTell,counterBatch1.iCantTell+counterBatch2.iCantTell,0);
			
		}
		
		
	}

	//@Test
	public void printCompletedMap(){
		System.out.println("----BATCH2 TESTING EXPECTED YES FILTERED----");
		LogData data = new LogData(false, new Double(12000000).doubleValue());
		String path = "C:\\Users\\adrianoc\\Dropbox (PE-C)\\3.Research\\1.Fall2014-Experiments\\Production-2October25\\1Final\\";
		data.processLogProduction2(path);
		//Configure the MicrotaskMap
		data.computeMicrotaskFromCompleteSessions();
		
		Iterator<String> iter = data.completeSessions_microtaskMap.keySet().iterator();
		while(iter.hasNext()){
			String id = iter.next();
			Microtask task = data.completeSessions_microtaskMap.get(id);
			Vector<Answer> answerlist = task.getAnswerList();
			for(Answer answer:answerlist){
				System.out.println("Microtask="+ id+"; answer="+answer.getOption());
			}
		}
		
		//analysis = new LogAnalysis(data);
	}

}

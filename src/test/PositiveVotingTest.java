package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.report.predictive.AnswerData;
import edu.uci.ics.sdcl.firefly.report.predictive.PositiveVoting;
import edu.uci.isc.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMap;
import edu.uci.isc.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMapLoader;

public class PositiveVotingTest {

	HashMap<String, ArrayList<String>> answerMap = new HashMap<String, ArrayList<String>>();
	HashMap<String,String> bugCoveringMap = new HashMap<String,String>();
	String hitFileName = "HIT00_0";

	AnswerData data;
	
	public void setup1(){
		
		bugCoveringMap.put("1","1");//1 yes
		bugCoveringMap.put("3","3");//4 yes's

		ArrayList<String> answerList = new ArrayList<String>();//2 yes's =  True Negative
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerList.add("IDK");
		answerList.add(Answer.YES);
		answerMap.put("0",answerList);

		answerList = new ArrayList<String>();//1 yes  = False Negative
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerMap.put("1",answerList);

		answerList = new ArrayList<String>();//3 yes's = False Positive
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerMap.put("2",answerList);

		answerList = new ArrayList<String>();//4 yes's = True positive
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerMap.put("3",answerList);

		

		data = new AnswerData(hitFileName,answerMap,bugCoveringMap,4,4);		
	}
	
	//@Test
	public void testComputeSignal_FirstThreshold() {
		this.setup1();
		PositiveVoting predictor = new PositiveVoting();
		predictor.setCalibrationLevel(2);
		assertTrue(predictor.computeSignal(this.data));
		assertEquals(2,predictor.getThreshold().intValue());	

		
		assertEquals(2, predictor.getFalsePositives().intValue());
		assertEquals(1, predictor.getTruePositives().intValue());
		assertEquals(1, predictor.getFalseNegatives().intValue());
		assertEquals(0, predictor.getTrueNegatives().intValue());
		
		double extraVote = 2;
		double rateOfTP =0.5;
		double expectedSignalStrength = extraVote* rateOfTP;
		System.out.println(predictor.computeSignalStrength(data).doubleValue());
		assertEquals("Signal Strength", expectedSignalStrength, predictor.computeSignalStrength(data).doubleValue(),0.0);
		
		//Test for number of workers
	}
	
	
	
	
	public void setup2(){
		
		bugCoveringMap.put("0","0");//2 yes's
		bugCoveringMap.put("1","1");//1 yes

		ArrayList<String> answerList = new ArrayList<String>();//2 yes's True Positive
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerList.add("IDK");
		answerList.add(Answer.YES);
		answerMap.put("0",answerList);

		answerList = new ArrayList<String>();//1 yes False Negative
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerMap.put("1",answerList);

		answerList = new ArrayList<String>();//2 yes's True Negative
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerMap.put("2",answerList);

		answerList = new ArrayList<String>();//4 yes's False Positive
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerMap.put("3",answerList);

		String hitFileName = "HIT00_0";

		data = new AnswerData(hitFileName,answerMap,bugCoveringMap,4,4);		
	}
	
	
	//@Test
	public void testComputeSignal_SecondThreshold() {
		this.setup2();
		PositiveVoting predictor = new PositiveVoting();
		
		assertTrue(predictor.computeSignal(this.data));
		assertEquals(2,predictor.getThreshold().intValue());	
				
		assertEquals(1, predictor.getFalsePositives().intValue());
		assertEquals(1, predictor.getTruePositives().intValue());
		assertEquals(1, predictor.getFalseNegatives().intValue());
		assertEquals(1, predictor.getTrueNegatives().intValue());
		
		double extraVote = 2.0;
		double rateOfTP = 0.5;
		double expectedSignalStrength = extraVote* rateOfTP;
		assertEquals("Signal Strength",expectedSignalStrength, predictor.computeSignalStrength(data).doubleValue(),0.0);
		
		//Test for number of workers
	}
	
	
	 
	private HashMap<String, QuestionLinesMap> allQuestionLineMaps;
	
	private void setupLineCountTests(){
	
		this.setup1();
				
		HashMap<String, String> allLinesMap_0 = new HashMap<String,String>();
		allLinesMap_0.put("272","272");
		allLinesMap_0.put("273","273");
		QuestionLinesMap linesMap_0 = new QuestionLinesMap(this.hitFileName, 0, allLinesMap_0, null);
		
		HashMap<String, String> allLinesMap_1 = new HashMap<String,String>();
		allLinesMap_1.put("279","279");
		HashMap<String, String> faultyLinesMap_1 = new HashMap<String,String>();
		faultyLinesMap_1.put("279","279");
		QuestionLinesMap linesMap_1 = new QuestionLinesMap(this.hitFileName, 1, allLinesMap_1, faultyLinesMap_1);
		
		HashMap<String, String> allLinesMap_2 = new HashMap<String,String>();
		allLinesMap_2.put("275","275");
		allLinesMap_2.put("276","276");
		allLinesMap_2.put("277","277");
		QuestionLinesMap linesMap_2 = new QuestionLinesMap(this.hitFileName, 2, allLinesMap_2, null);
		
		HashMap<String, String> allLinesMap_3 = new HashMap<String,String>();
		allLinesMap_3.put("278","278");
		allLinesMap_3.put("279","279");
		allLinesMap_3.put("280","280");
		HashMap<String, String> faultyLinesMap_3 = new HashMap<String,String>();
		faultyLinesMap_3.put("279","279");
		QuestionLinesMap linesMap_3 = new QuestionLinesMap(this.hitFileName, 3, allLinesMap_3, faultyLinesMap_3);
		
		allQuestionLineMaps = new HashMap<String, QuestionLinesMap>();
		allQuestionLineMaps.put("0", linesMap_0);
		allQuestionLineMaps.put("1", linesMap_1);
		allQuestionLineMaps.put("2", linesMap_2);
		allQuestionLineMaps.put("3", linesMap_3);
		
		
	}
	
	@Test
	public void testGetTruePositiveFaultyLineCount(){
		
		this.setupLineCountTests();
		
		PositiveVoting predictor =  new PositiveVoting();
		
		assertTrue(predictor.computeSignal(this.data));
		
		assertEquals(3,predictor.getThreshold().intValue());	
		
		HashMap<String,Integer> truePositiveFaultyLineCount = predictor.getTruePositiveFaultyLineCount(this.allQuestionLineMaps);
		printMap("True Positive Faulty", truePositiveFaultyLineCount);
		assertEquals(1, truePositiveFaultyLineCount.size());
		
		HashMap<String,Integer> truePositiveNearFaultyLineCount = predictor.getTruePositiveNearFaultyLineCount(this.allQuestionLineMaps);
		printMap("True Positives Near Faulty", truePositiveNearFaultyLineCount);
		assertEquals(2, truePositiveNearFaultyLineCount.size());
		
		HashMap<String,Integer> falsePositiveLineCount = predictor.getFalsePositiveLineCount(this.allQuestionLineMaps);
		printMap("False Positives", falsePositiveLineCount);
		assertEquals(3, falsePositiveLineCount.size());
		
		HashMap<String,Integer> falseNegativeLineCount = predictor.getFalseNegativeLineCount(this.allQuestionLineMaps);
		printMap("False Negatives", falseNegativeLineCount);
		assertEquals(1, falseNegativeLineCount.size());
		
		HashMap<String,Integer> trueNegativeLineCount = predictor.getTrueNegativeLineCount(this.allQuestionLineMaps);
		printMap("True Negatives", trueNegativeLineCount);
		assertEquals(2, trueNegativeLineCount.size());


	}
	
	private void printMap(String decision, HashMap<String,Integer> map){
		Iterator<String> iter =  map.keySet().iterator();
		System.out.print(decision+" lines :");
		while(iter.hasNext()){
			System.out.print(iter.next()+";");
		}
		System.out.println();
	}
	
	

}

package edu.uci.ics.sdcl.firefly.report.predictive.montecarlo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;
import edu.uci.ics.sdcl.firefly.report.predictive.AnswerData;
import edu.uci.ics.sdcl.firefly.report.predictive.DataPoint;
import edu.uci.ics.sdcl.firefly.report.predictive.FilterCombination;
import edu.uci.ics.sdcl.firefly.report.predictive.WithinQuestionConsensus;
import edu.uci.ics.sdcl.firefly.report.predictive.Outcome;
import edu.uci.ics.sdcl.firefly.report.predictive.SubCrowd;
import edu.uci.ics.sdcl.firefly.report.predictive.SubcrowdConsensusFinder;
import edu.uci.ics.sdcl.firefly.report.predictive.AcrossQuestionsConsensus;
import edu.uci.ics.sdcl.firefly.report.predictive.Consensus;
import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMap;
import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMapLoader;
import edu.uci.ics.sdcl.firefly.util.BugCoveringMap;
import edu.uci.ics.sdcl.firefly.util.MicrotaskMapUtil;

public class MonteCarloSimulator {

	/**  list of precision values calculated for each sample using Positive voting predictor*/
	private ArrayList<DataPoint> outcomes_PositiveVoting;

	/**  list of precision values calculated for each sample using Majority voting predictor*/
	private ArrayList<DataPoint> outcomes_MajorityVoting;

	public static String[] fileNameList = {"HIT01_8", "HIT02_24", "HIT03_6", "HIT04_7", "HIT05_35","HIT06_51","HIT07_33","HIT08_54"};

	private HashMap<String,String> bugCoveringMap;

	private HashMap<String, DataPoint> positiveVoting_AverageDataPointByAnswerLevel = new HashMap<String,DataPoint>();

	private HashMap<String, DataPoint> majorityVoting_AverageDataPointByAnswerLevel = new HashMap<String,DataPoint>();

	private String outputFolder = "";


	public MonteCarloSimulator(String outputFolder){

		this.outputFolder = outputFolder;
		bugCoveringMap = BugCoveringMap.initialize();
	}


	private void computeVoting(FilterCombination filter, ArrayList<HashMap<String,Microtask>> listOfMicrotaskMaps, 
			int sampleSize, boolean isAbsoluteVoting){
		this.outcomes_PositiveVoting = new ArrayList<DataPoint>();
		this.outcomes_MajorityVoting = new ArrayList<DataPoint>();

		QuestionLinesMapLoader loader = new QuestionLinesMapLoader();
		HashMap<String, QuestionLinesMap> lineMapping =  loader.loadList();

		for(int i=0; i<listOfMicrotaskMaps.size();i++){

			HashMap<String, Microtask> microtaskMap = listOfMicrotaskMaps.get(i);

			Double totalDifferentWorkersAmongHITs = MicrotaskMapUtil.countWorkers(microtaskMap, null);

			DataPoint positiveVDataPoint = new DataPoint();
			DataPoint majorityVDataPoint = new DataPoint();

			for(String fileName: fileNameList){//each fileName is a Java method
				HashMap<String, ArrayList<String>> answerMap = MicrotaskMapUtil.extractAnswersForFileName(microtaskMap,fileName);
				if(answerMap!=null && answerMap.size()>0){

					Double workerCountPerHIT = MicrotaskMapUtil.countWorkers(microtaskMap,fileName);
					AnswerData data = new AnswerData(fileName,answerMap,bugCoveringMap,workerCountPerHIT,totalDifferentWorkersAmongHITs);
					Consensus predictor = new AcrossQuestionsConsensus(2,isAbsoluteVoting);
					Outcome outcome = computeDataPoint(filter, data,predictor,lineMapping);
					positiveVDataPoint.fileNameOutcomeMap.put(fileName, outcome);

					predictor = new WithinQuestionConsensus(WithinQuestionConsensus.Balance_YES_NO_Consensus,null,0,isAbsoluteVoting);
					outcome = computeDataPoint(filter, data,predictor,lineMapping);
					majorityVDataPoint.fileNameOutcomeMap.put(fileName, outcome);
				}
			}

			positiveVDataPoint.maxAnswersHIT = MicrotaskMapUtil.countAnswers(microtaskMap);
			majorityVDataPoint.maxAnswersHIT = positiveVDataPoint.maxAnswersHIT;

			positiveVDataPoint.computeAverages();//Compute the average precision and recall for all Java methods
			majorityVDataPoint.computeAverages();

			positiveVDataPoint.elapsedTime = MicrotaskMapUtil.computeElapsedTimeForAnswerLevels(microtaskMap);
			majorityVDataPoint.elapsedTime = positiveVDataPoint.elapsedTime;

			positiveVDataPoint.totalWorkers = new Double(totalDifferentWorkersAmongHITs);
			majorityVDataPoint.totalWorkers = new Double(totalDifferentWorkersAmongHITs);

			this.outcomes_PositiveVoting.add(positiveVDataPoint);
			this.outcomes_MajorityVoting.add(majorityVDataPoint);

		}		
	}


	private void printAnswerMap(HashMap<String, ArrayList<String>> answerMap) {

		int lines = 0;
		Iterator<String> iter = answerMap.keySet().iterator();
		while(iter.hasNext() && lines<5){
			String key = iter.next();
			ArrayList<String> answerList = answerMap.get(key);
			String outcome="";
			for(int i=0;i<answerList.size();i++){
				outcome = outcome+ "," + answerList.get(i);
			}
			lines++;
			System.out.println(key+":"+outcome);
		}
	}


	private void computeAveragesByAnswerLevel(int sampleSize){

		String key = new Integer(sampleSize).toString();

		DataPoint point = this.computeAveragesForList(outcomes_PositiveVoting);
		this.positiveVoting_AverageDataPointByAnswerLevel.put(key, point);

		point = this.computeAveragesForList(outcomes_MajorityVoting);
		this.majorityVoting_AverageDataPointByAnswerLevel.put(key, point);	
	}

	private DataPoint computeAveragesForList(ArrayList<DataPoint> dataPointList){

		int size = dataPointList.size();

		Double sumAnswers=0.0;
		Double sumWorkers=0.0;

		Double sumTP=0.0;
		Double sumTN=0.0;
		Double sumFP=0.0;
		Double sumFN=0.0;
		Double sumElapsedTime=0.0;

		Double sumPrecision=0.0;
		Double sumRecall=0.0;

		Double sumFaultsLocated =0.0;
		Integer sumNumberOfOutcomes = 0;

		for(DataPoint data: dataPointList){
			sumAnswers = sumAnswers + data.maxAnswersHIT;
			sumWorkers = sumWorkers + data.totalWorkers;

			sumTP = sumTP + data.truePositives;
			sumFP = sumFP + data.falsePositives;
			sumTN = sumTN + data.trueNegatives;
			sumFN = sumFN + data.falseNegatives;

			sumPrecision = sumPrecision + data.averagePrecision;
			sumRecall = sumRecall + data.averageRecall;

			sumElapsedTime = sumElapsedTime + data.elapsedTime;

			sumFaultsLocated = sumFaultsLocated + data.faultsLocated;

			sumNumberOfOutcomes = sumNumberOfOutcomes + data.numberOfOutcomes;
		}

		//Averages
		DataPoint averageDataPoint = new DataPoint();

		averageDataPoint.maxAnswersHIT = sumAnswers / size;
		averageDataPoint.totalWorkers = sumWorkers / size;

		averageDataPoint.falseNegatives = sumFN /size;
		averageDataPoint.falsePositives = sumFP / size;
		averageDataPoint.trueNegatives = sumTN / size;
		averageDataPoint.truePositives = sumTP / size;

		averageDataPoint.averagePrecision = sumPrecision / size;
		averageDataPoint.averageRecall = sumRecall / size;

		averageDataPoint.elapsedTime = sumElapsedTime /size;

		averageDataPoint.faultsLocated = sumFaultsLocated / size;

		averageDataPoint.numberOfOutcomes = sumNumberOfOutcomes;

		return averageDataPoint;
	}

	private Outcome computeDataPoint(FilterCombination filter, AnswerData answerData, Consensus predictor, HashMap<String, QuestionLinesMap> lineMapping) {

		predictor.computeThreshold(answerData);
		HashMap<String, Integer> truePositiveLines = predictor.getTruePositiveLines(lineMapping);
		HashMap<String, Integer> nearPositiveLines = predictor.getNearPositiveLines(lineMapping);
		HashMap<String, Integer> falsePositiveLines = predictor.getFalsePositiveLines(lineMapping);
		falsePositiveLines = Consensus.removeFalsePositiveDuplications(nearPositiveLines,falsePositiveLines);

		Outcome outcome = new Outcome(filter, //FilterCombination
				answerData.getHitFileName(), //fileName
				predictor.getName(), //predictorType
				predictor.getTruePositives()>0.0, //faultLocated
				predictor.computeSignalStrength(answerData),//signalStrength
				predictor.computeNumberOfWorkers(answerData), //maxWorkerPerQuestion
				answerData.getTotalAnswers(),//totalAnswers
				predictor.getMinimumNumberYESAnswersThatLocatedFault(),//threshold
				predictor.getTruePositives(),//truePositives
				predictor.getTrueNegatives(),//trueNegatives
				predictor.getFalsePositives(),//falsePositives
				predictor.getFalseNegatives(),//falseNegatives
				answerData.getWorkerCount(),//differentWorkersPerHIT
				answerData.getDifferentWorkersAmongHITs(),//differentWorkersAmongHITs
				truePositiveLines,//truePositiveLines
				nearPositiveLines,//nearPositiveLines
				falsePositiveLines,//falsePositiveLines
				predictor.getQuestionsBelowMinimalAnswers(),//questionsBelowMinimumAnswers
				AnswerData.countCorrectYES(answerData.answerMap, answerData.bugCoveringMap), //correctYES
				AnswerData.countCorrectNO(answerData.answerMap, answerData.bugCoveringMap), //correctNO
				AnswerData.count(answerData.answerMap, Answer.YES),//totalYES
				AnswerData.count(answerData.answerMap, Answer.NO), //totalNO
				AnswerData.count(answerData.answerMap, Answer.I_DONT_KNOW)//totalIDK
				);



		return outcome;
	}

	//------------------------------------------------------------------------------------------------
	//PRINT FUNCTIONS

	public void printDataPointsToFile(int name){

		String nameStr = new Integer(name).toString();
		String destination = "C://firefly//MonteCarloSimulation//ByJavaMethod//"+this.outputFolder+"//"+ nameStr+".csv";
		BufferedWriter log;

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header

			log.write("#,"+DataPoint.getHeader("_PV")+","+DataPoint.getHeader("_MV")+"\n");

			for(int i=0; i<this.outcomes_PositiveVoting.size();i++){
				String index = new Integer(i).toString();
				String positiveVote_Outcomes = this.outcomes_PositiveVoting.get(i).toString();
				String majorytVote_Outcomes = this.outcomes_MajorityVoting.get(i).toString();
				log.write(index+","+positiveVote_Outcomes+","+majorytVote_Outcomes+"\n");
			}

			log.close();
			System.out.println("file written at: "+destination);
		} 
		catch (Exception e) {
			System.out.println("ERROR while processing file:" + destination);
			e.printStackTrace();
		}
	}

	private void printToFile(HashMap<String, DataPoint> positiveVotingAverageMap,
			HashMap<String, DataPoint> majorityVotingAverageMap,String name) {

		String nameStr = name+"_datapoint";
		String destination = "C://firefly//MonteCarloSimulation//ByJavaMethod//DataPoints//"+ nameStr+".csv";
		BufferedWriter log;

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header

			log.write("#,"+DataPoint.getHeader(" Positive Voting")+DataPoint.getHeader(" Majority Voting")+"\n");
			for(String key: positiveVotingAverageMap.keySet()){

				String line= key+","+positiveVotingAverageMap.get(key).toString();
				line = line +","+ majorityVotingAverageMap.get(key).toString(); 

				log.write(line+"\n");
			}		

			log.close();
			System.out.println("file written at: "+destination);
		} 
		catch (Exception e) {
			System.out.println("ERROR while processing file:" + destination);
			e.printStackTrace();
		}
	}

	//------------------------------------------------------------------------------------------------------

	private void generateSimulations(FilterCombination filter, int populationSize, int numberOfSamples, 
			HashMap<String, Microtask> microtaskMap, String crowdName,boolean isAbsoluteVoting, boolean isFixedSampleSize){

		for(int i=1;i<=populationSize;i++){

			//how many answers per question
			int sampleSize = i; 

			//Generate the samples
			RandomSampler sampling = new RandomSampler(sampleSize, numberOfSamples, populationSize, isFixedSampleSize);
			ArrayList<HashMap<String, Microtask>> listOfMicrotaskMaps =sampling.generateMicrotaskMap(microtaskMap);

			//Compute statistics for each sample
			computeVoting(filter, listOfMicrotaskMaps, sampleSize, isAbsoluteVoting);

			//Save samples with statistics to files
			printDataPointsToFile(sampleSize);

			//Takes the average of all samples		
			computeAveragesByAnswerLevel(sampleSize);	

		}
		//print the averages samples from Majority voting and Positive voting
		printToFile(this.positiveVoting_AverageDataPointByAnswerLevel,this.majorityVoting_AverageDataPointByAnswerLevel,crowdName);
	}

	/**
	 * Create the filters corresponding to each subcrowd and extract the microtask map for each subcrowd.
	 * 
	 * @return list of subcrowds with their attributes instantiated
	 */
	private ArrayList<SubCrowd> composeSubcrowds(){
		SubcrowdConsensusFinder finder = new SubcrowdConsensusFinder();
		ArrayList<SubCrowd> subCrowdList =  finder.generateSubCrowdFilters();
		subCrowdList = finder.generateSubCrowdMicrotasks(subCrowdList);
		return subCrowdList;
	}

	//----------------------------------------------------------------------

	public void run(){		
		ArrayList<SubCrowd> subCrowdList = composeSubcrowds();

		boolean isAbsoluteVoting = false;
		boolean isFixedSampleSize = false;
		int numberOfSamples = 10000; //how many simulated crowds

		for(SubCrowd crowd:subCrowdList){
			//SubCrowd crowd =subCrowdList.get(1);	

			HashMap<String, Microtask> microtaskMap = crowd.microtaskMap;

			int maximumSampleSize = RandomSampler.computeMaximumSampleSize(microtaskMap);//total answers per question		
			generateSimulations(crowd.filterCombination, maximumSampleSize, numberOfSamples, microtaskMap,  crowd.name, isAbsoluteVoting,isFixedSampleSize);			 
		}
	}

	/** THis is used to test, but the actual call is made from class SimulationController */
	public static void main(String args[]){
		MonteCarloSimulator sim = new MonteCarloSimulator("SamplingPredictor");
		sim.run();
	}

}



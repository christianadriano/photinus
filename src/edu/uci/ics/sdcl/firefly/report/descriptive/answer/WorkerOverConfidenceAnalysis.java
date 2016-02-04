package edu.uci.ics.sdcl.firefly.report.descriptive.answer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.WorkerSession;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileConsentDTO;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;
import edu.uci.ics.sdcl.firefly.report.predictive.spectra.QuestionLOCs;
import edu.uci.ics.sdcl.firefly.util.BugCoveringMap;

/**
 * The goal here is to analyze whether confidence varies among workers skill and profession.
 * Also evaluate whether:
 * - workers with lower skill found questions more difficult?
 * - workers with lower skill were overly confident?
 * - is the rate of mistakes similar among workers who selected the same level of confidence or difficulty?
 * @author adrianoc
 *
 */
public class WorkerOverConfidenceAnalysis {

	public WorkerPerceptionAnalysis perceptionAnalysis;

	public HashMap<String,String> workerGradeMap =  new HashMap<String,String>();

	public HashMap<String,WorkerConfidenceDistributions> gradeDistributionsMap = new HashMap<String,WorkerConfidenceDistributions> ();

	public WorkerOverConfidenceAnalysis(){

		this.perceptionAnalysis =  new WorkerPerceptionAnalysis();
		this.perceptionAnalysis.loadWorkerSessions(false); //non-independent samples, means collect all answers from workers.
	}

	public void loadDistributionsBySkill(){

		//Categorize workers
		loadWorkerGradeMap();

		//Load distributions
		for(WorkerSessionTuple tuple: this.perceptionAnalysis.sessionTupleList){
			String grade = this.workerGradeMap.get(tuple.workerID);
			if(grade!=null){
				WorkerConfidenceDistributions distributions = this.gradeDistributionsMap.get(grade);
				if(distributions==null){
					distributions = new WorkerConfidenceDistributions();
					distributions.workerScore = grade;
				}
				distributions.addTuple(tuple);
				this.gradeDistributionsMap.put(grade, distributions);
			}
		}
	}

	private void loadWorkerGradeMap() {
		FileConsentDTO dto =  new FileConsentDTO();
		HashMap<String, Worker> workerMap = dto.getWorkers();

		Iterator<String> iter = workerMap.keySet().iterator();
		while(iter.hasNext()){
			String workerID = iter.next();
			Worker worker = workerMap.get(workerID);

			if(worker.getGrade()>=3){//Ignore workers who did not pass the test
				workerGradeMap.put(workerID, worker.getGrade().toString());
			}
		}
	}

	public void printDistributions(){
		String destination = "C://firefly//OverConfidenceAnalysis//confidenceDistributions.csv";
		BufferedWriter log;

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header

			Iterator<String> iter = gradeDistributionsMap.keySet().iterator();

			while(iter.hasNext()){
				String grade = iter.next();

				WorkerConfidenceDistributions distributions = this.gradeDistributionsMap.get(grade);

				ArrayList<StringBuffer> bufferList = distributions.toStringList();
				for(StringBuffer buffer : bufferList){

					log.write(grade+","+buffer.toString()+"\n");
				}
			}

			log.close();
			System.out.println("file written at: "+destination);
		} 
		catch (Exception e) {
			System.out.println("ERROR while processing file:" + destination);
			e.printStackTrace();
		}

	}
	
	public int countAnswers(){
		int count=0;
		for( WorkerSessionTuple tuple : this.perceptionAnalysis.sessionTupleList){
			count = count + tuple.correctAnswers.size() + tuple.wrongAnswers.size();
		}
		return count;
	}

	//-------------------------------------------------------------
	public static void main(String[] args){

		WorkerOverConfidenceAnalysis analysis = new WorkerOverConfidenceAnalysis();
		analysis.loadDistributionsBySkill();
		analysis.printDistributions();

		System.out.println(analysis.countAnswers());
		
	}
}

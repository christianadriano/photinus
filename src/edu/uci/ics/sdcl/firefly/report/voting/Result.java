package edu.uci.ics.sdcl.firefly.report.voting;

public class Result {

	String truePositives="0";
	String trueNegatives="0";
	String falsePositives="0";
	String falseNegatives="0";
	
	String percent_TruePositives;
	String percent_TrueNegatives;
	String percent_FalsePositives;
	String percent_FalseNegatives;
	
	String percent_Inconclusive="0%";
	double inconclusive=0.0;
	
	public Double precision=0.0;
	public Double recall=0.0;
	
	double total=0.0;
	
	String fileName;
	String taskId;
	String questionText;
	String workerId;
	
	public Result(){}
	
	public Result(String truePositives, String trueNegatives, String falsePositives,
			String falseNegatives, String percent_TruePositive,
			String percent_TrueNegative, String percent_FalsePositive,
			String percent_FalseNegative, String fileName, String taskId,
			String questionText, String workerId) {
		super();
		this.truePositives = truePositives;
		this.trueNegatives = trueNegatives;
		this.falsePositives = falsePositives;
		this.falseNegatives = falseNegatives;
		this.percent_TruePositives = percent_TruePositive;
		this.percent_TrueNegatives = percent_TrueNegative;
		this.percent_FalsePositives = percent_FalsePositive;
		this.percent_FalseNegatives = percent_FalseNegative;
		this.fileName = fileName;
		this.taskId = taskId;
		this.questionText = questionText;
		this.workerId = workerId;
	}

	public void printNumbers(){
		System.out.println("|total|"+total+"|%TP|"+percent_TruePositives+"|%TN|"+percent_TrueNegatives+"|%FP|"+percent_FalsePositives+"|%FN|"+percent_FalseNegatives+"|%Inconclusive|"+percent_Inconclusive);
		//System.out.println("|TP|"+truePositives+"|TN|"+trueNegatives+"|FP|"+falsePositives+"|FN|"+falseNegatives+"|Inconclusive|"+inconclusive);
	}

	public void printFileNameNumbers(){
		System.out.println("|"+fileName+"|TP|"+percent_TruePositives+"|TN|"+percent_TrueNegatives+"|FP|"+percent_FalsePositives+"|FN|"+percent_FalseNegatives);
	}

}



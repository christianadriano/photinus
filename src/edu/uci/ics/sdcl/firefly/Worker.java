package edu.uci.ics.sdcl.firefly;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;


public class Worker implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String workerId;
	private String consentDate;
	private Hashtable<String,String> surveyAnswersMap;
	private String[] skillTestAnswerMap; // THIS IS NOT USED BY ANY CLASS
	private String[] rubricMap;
	private boolean[] gradeMap;
	private Integer grade;
	private String skillTestDuration;
	private String sessionId; //Stores the single working session session allowed per worker
	private String currentFileName; //File name that the worker is requesting to work on. 
	private Hashtable<String, Integer> fileHistory = new Hashtable<String, Integer>();
	private Boolean answeredSurvey;
	private ArrayList<String> quitReasonList = null;
	private ArrayList<String> quitFileList = null; //Files in which the worker quit

	
	
	public Worker(String workerId, String consentDateStr, String currentFileName) {
		this.workerId = workerId;
		this.consentDate = consentDateStr;
		this.surveyAnswersMap =  new Hashtable<String,String>();
		this.grade = -1;
		this.skillTestDuration = "";
		this.sessionId = null;		
		this.rubricMap = new String[5];
		this.skillTestAnswerMap = new String[5];
		this.gradeMap = new boolean[5];
		this.currentFileName = currentFileName;
		this.answeredSurvey = false;
	}

	public Boolean hasAnsweredSurvey(){
		return this.answeredSurvey;
	}
	
	public String getWorkerId() {
		return workerId;
	}

	public String getConsentDate() {
		return consentDate;
	}

	public void setConsentDate(String consentDate) {
		this.consentDate = consentDate;
	}	

	public String getCurrentFileName() {
		return currentFileName;
	}

	public void setCurrentFileName(String currentFileName) {
		this.currentFileName = currentFileName;
	}
	
	
	public void addSurveyAnswer(String question, String answer){
		this.surveyAnswersMap.put(question, answer);
		this.answeredSurvey = true;
	}

	public String getSurveyAnswer(String question){
		return this.surveyAnswersMap.get(question);
	}

	public void setSkillAnswers(String[] rubricMap,
			boolean[] gradeMap, String[] answerMap, int grade, String duration) {
		this.setRubricMap(rubricMap);
		this.setGradeMap(gradeMap);
		this.setSkillTestAnswersMap(answerMap);
		this.setGrade(grade);
		this.setSkillTestDuration(duration);
	}

	private void setSkillTestAnswersMap(String[] answerMap) {
		this.skillTestAnswerMap = answerMap;
		
	}

	public void setSkillTestDuration(String duration) {
		this.skillTestDuration = duration;
	}

	public String getSkillTestDuration() {
		return this.skillTestDuration;
	}

	public String[] getRubricMap() {
		return rubricMap;
	}

	public void setRubricMap(String[] rubricMap2) {
		this.rubricMap = rubricMap2;
	}

	public boolean[] getGradeMap() {
		return gradeMap;
	}

	public void setGradeMap(boolean[] gradeMap2) {
		this.gradeMap = gradeMap2;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public Hashtable<String, String> getSurveyAnswers() {
		return surveyAnswersMap;
	}
	
	public String getProfession(){
		if(!this.answeredSurvey)
			return null;
		else{
			return this.surveyAnswersMap.get("Experience");
		}
	}
	
	public String getAge(){
		if(!this.answeredSurvey)
			return null;
		else{
			return this.surveyAnswersMap.get("Age");
		}
	}
	
	public String getGender(){
		if(!this.answeredSurvey)
			return null;
		else{
			return this.surveyAnswersMap.get("Gender");
		}
	}

	public String getCountry(){
		if(!this.answeredSurvey)
			return null;
		else{
			return this.surveyAnswersMap.get("Country");
		}
	}
	
	public String getYearsProgramming(){
		if(!this.answeredSurvey)
			return null;
		else{
			return this.surveyAnswersMap.get("YearsProgramming");
		}
	}
	
	public String getLearnedToProgramming(){
		if(!this.answeredSurvey)
			return null;
		else{
			return this.surveyAnswersMap.get("Learned");
		}
	}
	
	public String getLanguage(){
		if(!this.answeredSurvey)
			return null;
		else{
			return this.surveyAnswersMap.get("Language");
		}
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;		
	}

	public String getSessionId() {
		return this.sessionId;
	}

	public String getSurveyAnswersToString(){
		if(this.surveyAnswersMap==null ||this.surveyAnswersMap.isEmpty())
			return "no survey";
		else{
			Iterator<String> iter = this.surveyAnswersMap.keySet().iterator();
			String value="";
			while(iter.hasNext()){
				String key = iter.next();
				String content = this.surveyAnswersMap.get(key);
				if(content==null || content.isEmpty())
					content="-";
				content = content.replaceAll("[\n]"," ").replaceAll("[\r]"," ");
				value = value+key+"%"+content+"% ";	
			}
			value = value.substring(0, value.lastIndexOf("% "));
			return value;
		}
	}

	public boolean hasTakenTest() {	
		if(this.grade==null || this.grade<0)
			return false;
		else 
			return true;
	}
	
	public boolean hasPassedTest()
	{
		if (this.grade>=3)
			return true;
		else 
			return false;
	}
	
	public void addFileHistory(String file)
	{
		this.setCurrentFileName(file);
		Integer usedTimes = this.fileHistory.get(file);
		if(usedTimes == null)
			this.fileHistory.put(file, 1);
		else
			this.fileHistory.put(file, usedTimes +1);
	}
	
	public boolean isAllowedFile(String file)
	{
		Integer usedTimes = this.fileHistory.get(file);
		if(usedTimes == null)
			return true;
		else 
			return false;
	}

	public ArrayList<String> getQuitReasonList() {
		return quitReasonList;
	}

	public void addQuitReason(String quitReason) {
		if(this.quitReasonList==null)
			this.quitReasonList=new ArrayList<String>();
		this.quitReasonList.add(quitReason);
	}

	public String quitReasonToString(){
		if(this.quitReasonList==null) return null;
		else{
			String result="";
			for(String reason: quitReasonList){
				result = result +":"+ reason;
			}
			return result;
		}
	}
	
	public ArrayList<String> getQuitFileList() {
		return quitFileList;
	}

	public void addQuitFileList(String quitFile) {
		if(this.quitFileList==null)
			this.quitFileList=new ArrayList<String>();
		this.quitFileList.add(quitFile);
	}

	public String quitFileToString(){
		if(this.quitFileList==null) return null;
		else{
			String result="";
			for(String file: quitFileList){
				result = result +":"+ file;
			}
			return result;
		}
	}
	
}

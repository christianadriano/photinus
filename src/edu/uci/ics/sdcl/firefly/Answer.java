package edu.uci.ics.sdcl.firefly;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Answer implements Serializable, Comparable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String YES = "YES, THERE IS AN ISSUE";
	public static final String I_DONT_KNOW = "I DON'T KNOW";
	public static final String NO = "NO, THERE IS NOT AN ISSUE";

	private String option;
	private int confidenceOption;
	private String explanation;
	private String workerId;
	private String elapsedTime; // duration of the answer
	private String timeStamp;
	private int difficulty;
	private int orderInWorkerSession; //1=first, 2=second, 3=third
	private String sessionID;
	private Date timeStampDate;


	public Answer(String option, int confidenceOption, String explanation, String workerId, 
			String elapsedTime, String timeStamp, int difficulty, int orderInWorkerSession, String sessionID){
		
		this.option = option;
		this.confidenceOption = confidenceOption;
		this.explanation = explanation;
		this.workerId = workerId;
		this.elapsedTime = elapsedTime;
		this.timeStamp = timeStamp;
		this.difficulty = difficulty;
		this.orderInWorkerSession = orderInWorkerSession;
		this.setSessionID(sessionID); //So we know from which session this answer came (3 answers per session)
		
		this.timeStampDate = convertDateTime(timeStamp);
		if(timeStampDate!=null)
			this.timeStamp = timeStamp;
	}

	private Date convertDateTime(String tStamp){
		
		Date dateStamp= null;
		if(tStamp!=null && tStamp.length()>0){

//			System.out.println(tStamp);

			DateFormat format = new SimpleDateFormat("EEE yyyy MMM dd HH:mm:ss.S", Locale.ENGLISH);
			try {
				dateStamp = format.parse(tStamp);
				//System.out.println("converted:"+ format.format(dateStamp));
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return dateStamp;
	}


	/**
	 * Support for the old version
	 * 
	 * @param option
	 * @param explanation
	 * @param workerId
	 * @param elapsedTime
	 * @param timeStamp
	 */
	public Answer(String option, String explanation, String workerId,
			String elapsedTime, String timeStamp) {
		super();
		this.option = option;
		this.explanation = explanation;
		this.workerId = workerId;
		this.elapsedTime = elapsedTime;

		this.timeStampDate = convertDateTime(timeStamp);
		if(timeStampDate!=null)
			this.timeStamp = timeStamp;
	}

	public String getShortOption(){
		String option = this.getOption();
		if(option.compareTo("YES, THERE IS AN ISSUE")==0)
			return "YES";
		else
			if(option.compareTo("NO, THERE IS NOT AN ISSUE")==0)
				return "NO";
			else
				if(option.compareTo("I DON'T KNOW")==0)
					return "IDK";
				else
					return "ERROR";
	}

	public String getOption() {
		return option;
	}

	public int getConfidenceOption(){
		return confidenceOption;
	}

	public String getExplanation() {
		return explanation;
	}

	public String getWorkerId() {
		return workerId;
	}

	public String getElapsedTime() {
		return elapsedTime;
	}

	public String getTimeStamp() {
		return timeStamp;
	}
	
	public Date getTimeStampDate(){
		return this.timeStampDate;
	}

	public int getDifficulty(){
		return difficulty;
	}

	public int getOrderInWorkerSession(){
		return orderInWorkerSession;
	}

	public static String mapOptionToString(int number){
		switch(number){
		case 1: return Answer.YES; 
		case 2: return Answer.I_DONT_KNOW; 
		case 3: return Answer.NO; 
		default: return null;
		}
	}

	public static int mapOptionNumber(String option){
		switch(option){
		case  Answer.YES: return 1; 
		case Answer.I_DONT_KNOW: return 2; 
		case Answer.NO: return 3; 		
		default: return 0;
		}
	}


	 @Override
	  public int compareTo(Object o) {
	    if (getTimeStampDate() == null || ((Answer) o).getTimeStampDate() == null)
	      return 0;
	    return getTimeStampDate().compareTo(((Answer) o).getTimeStampDate());
	  }

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}



}

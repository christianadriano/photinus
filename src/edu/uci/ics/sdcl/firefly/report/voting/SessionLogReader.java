package edu.uci.ics.sdcl.firefly.report.voting;

import java.io.File;
import java.util.StringTokenizer;

import edu.uci.ics.sdcl.firefly.util.PropertyManager;

public class SessionLogReader {
	
	//Session Close
	
	//Session Open
	
	//Microtask
	
	private String logPath = "c:/firefly/logs/";
	private String sessionFileName = "session-log.log";
	private String consentFileName = "consent-log.log";
	
	private File sessionFile;
	private File consentFile;
	private StringTokenizer tokenizer;
	
	public SessionLogReader(){
		this.sessionFile = new File(logPath+sessionFileName);
		this.consentFile = new File(logPath+consentFileName);
		this.tokenizer = new StringTokenizer("");
	}
			
	private boolean loadSessions(){
		String line = "";
		StringTokenizer tokeninzer;
		while (line!=null){
			line = readLine(this.sessionFile);
			
		}
			return false;
	}
	
	
	private String readLine(File fileName){ 

		//try(BufferedReader br = new BufferedReader(new FileReader(file))) {
		  //  for(String line; (line = br.readLine()) != null; ) {
		        // process the line.
		    //}
		    // line is not visible here.
		//}
		
		return null;}

}

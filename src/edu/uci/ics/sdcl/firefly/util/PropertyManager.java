package edu.uci.ics.sdcl.firefly.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import edu.uci.ics.sdcl.firefly.storage.MicrotaskStorage;

public class PropertyManager {

	private String fileName="firefly.properties";

	private String devPropertyPath= "C:/Users/Chris/Documents/GitHub/photinus/"; 

	public String serverPropertyPath ="/var/lib/tomcat7/webapps/";   
	
	public String fileUploadFolder = "samples/bulkLoadPhotinus/";
	
	public String skillTestUploadFolder = "/samples/bulkSkillTests/";
	
	public String speedAnalysisPath = "C:/Users/Chris/firefly/SpeedAnalysis/";
	
	public String fileUploadSourcePath;
	
	public String skillTestUploadPath = "C:/Users/Chris/Documents/GitHub/photinus";	
	
	public String serializationPath;

	public String reportPath;
	
	public String sessionLogFileName;
	
	public String consentLogFileName;

	public int answersPerMicrotask;

	public int microtasksPerSession;
	
	public String fileNameTokenList;
	
	public String microtaskTokenList;
	
	public String bugCoveringList;

	public HashMap<String, Coordinate> questionRangeMap;

	private static PropertyManager manager;
	
	public static PropertyManager initializeSingleton(){
		if(manager == null){
			manager = new PropertyManager();
		}
		return manager;
	}
	
	private PropertyManager(){

		String OS = System.getProperty("os.name").toLowerCase();

		if(this.isDevelopmentHost(OS)){
			readDevelopmentProperties();
		}
		else
			if(isServerHost(OS)){
				readServerProperties();
			}
			else{
				System.out.println("Could not load properties, because operation system is not supported.");				
			}	
	}
	
	private void readDevelopmentProperties(){
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(this.devPropertyPath+"/WebContent/"+this.fileName));
			this.consentLogFileName = properties.getProperty("consentLogFileName");
			this.sessionLogFileName = properties.getProperty("sessionLogFileName");
			this.fileUploadSourcePath = this.devPropertyPath + this.fileUploadFolder;
			this.skillTestUploadPath = this.devPropertyPath + this.skillTestUploadFolder;
			this.reportPath = properties.getProperty("development-ReportPath");
			this.serializationPath = properties.getProperty("development-SerializationPath");
			this.answersPerMicrotask = new Integer(properties.getProperty("answersPerMicrotask")).intValue();
			this.microtasksPerSession = new Integer(properties.getProperty("microtasksPerSession")).intValue();
			this.fileNameTokenList = new String(properties.getProperty("fileNameList"));
			this.microtaskTokenList = new String(properties.getProperty("microtaskList"));
			this.bugCoveringList = new String(properties.getProperty("bugCoveringQuestions"));
			this.questionRangeMap = this.readQuestionRange(properties);
		} 
		catch (IOException e) {
			System.out.println("Could not load properties. Please be sure that the property file is located at: "+this.devPropertyPath);
		}
	}
	
	private HashMap<String,Coordinate> readQuestionRange(Properties properties){
		HashMap<String,Coordinate> questionRangeMap = new HashMap<String,Coordinate>();
		
		String[] bugIDList = properties.getProperty("bugIDList").split(";");
		for(String bugID:bugIDList) {
			String rangeStr = properties.getProperty(bugID+"_question_range");
			Integer start = new Integer(rangeStr.split(";")[0]);
			Integer end = new Integer(rangeStr.split(";")[1]);
			questionRangeMap.put(bugID, new Coordinate(start,end));			
		}		
		return questionRangeMap;
	}
	
	private void readServerProperties(){
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(this.serverPropertyPath+this.fileName));
			this.fileUploadSourcePath = this.serverPropertyPath + this.fileUploadFolder;
			this.skillTestUploadPath = this.serverPropertyPath + this.skillTestUploadFolder;
			this.reportPath = properties.getProperty("server-SerializationPath");
			this.serializationPath = properties.getProperty("server-SerializationPath");
			this.answersPerMicrotask = new Integer(properties.getProperty("answersPerMicrotask")).intValue();
			this.microtasksPerSession = new Integer(properties.getProperty("microtasksPerSession")).intValue();
			this.fileNameTokenList = new String(properties.getProperty("fileNameList"));
			this.microtaskTokenList = new String(properties.getProperty("microtaskList"));
			this.bugCoveringList = new String(properties.getProperty("bugCoveringQuestions"));
		} 
		catch (IOException e) {
			System.out.println("Could not load properties. Please be sure that the property file is located at: "+this.serverPropertyPath);
		}
	}

	private boolean isDevelopmentHost(String OS) {	 
		return (OS.indexOf("win") >= 0);
	}

	private boolean isServerHost(String OS) {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
	}

	public static void main(String args[]) {
		PropertyManager manager = PropertyManager.initializeSingleton();
		Collection<Coordinate> collection = manager.questionRangeMap.values();
		for(Coordinate coord:collection) {
			System.out.println(coord.start+":"+coord.end);
		}
		
	}
	
}

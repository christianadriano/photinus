package edu.uci.ics.sdcl.firefly.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import edu.uci.ics.sdcl.firefly.storage.MicrotaskStorage;

public class PropertyManager {

	private String fileName="WebContent/firefly.properties";

	private String devPropertyPath= "C:/Users/igMoreira/Documents/GitHub/photinus/";

	public String serverPropertyPath ="/var/lib/tomcat7/webapps/";   
	
	public String fileUploadFolder = "samples/bulkLoadPhotinus/";
	
	public String skillTestUploadPath = "C:/Users/igMoreira/Documents/GitHub/photinus/samples/bulkSkillTests/";
	
	public String fileUploadSourcePath;
		
	public String serializationPath;

	public String reportPath;

	public int answersPerMicrotask;

	public int microtasksPerSession;

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
			properties.load(new FileInputStream(this.devPropertyPath+this.fileName));
			this.fileUploadSourcePath = this.devPropertyPath + this.fileUploadFolder;
			this.reportPath = properties.getProperty("development-ReportPath");
			this.serializationPath = properties.getProperty("development-SerializationPath");
			this.answersPerMicrotask = new Integer(properties.getProperty("answersPerMicrotask")).intValue();
			this.microtasksPerSession = new Integer(properties.getProperty("microtasksPerSession")).intValue();
		} 
		catch (IOException e) {
			System.out.println("Could not load properties. Please be sure that the property file is located at: "+this.devPropertyPath);
		}
	}
	
	private void readServerProperties(){
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(this.serverPropertyPath+this.fileName));
			this.fileUploadSourcePath = this.serverPropertyPath + this.fileUploadFolder;
			this.reportPath = properties.getProperty("server-SerializationPath");
			this.serializationPath = properties.getProperty("server-SerializationPath");
			this.answersPerMicrotask = new Integer(properties.getProperty("answersPerMicrotask")).intValue();
			this.microtasksPerSession = new Integer(properties.getProperty("microtasksPerSession")).intValue();
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

}

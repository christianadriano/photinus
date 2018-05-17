package edu.uci.ics.sdcl.firefly.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import edu.uci.ics.sdcl.firefly.report.analysis.FilterContent;
import edu.uci.ics.sdcl.firefly.report.voting.AnalysisPath;

/** 
 * Class used in the data analysis process
 * 
 * @author Christian Adriano
 *
 */
public class FilterContentStorage {

	private String persistentFileName = "filtercontent.ser";

	public FilterContentStorage(){}

	public void write(ArrayList<FilterContent> filterContentList){
		try{

			//PropertyManager manager = PropertyManager.initializeSingleton();
			AnalysisPath analysisPath = AnalysisPath.getInstance();
			String path = analysisPath.analysisTypePath;
			this.persistentFileName = path + this.persistentFileName;

			ObjectOutputStream objOutputStream = new ObjectOutputStream( 
					new FileOutputStream(new File(this.persistentFileName)));

			objOutputStream.writeObject( filterContentList );
			objOutputStream.close();
		}
		catch(IOException exception){
			exception.printStackTrace();
		}
		catch(Exception exception){
			exception.printStackTrace();
		}
	}

	public ArrayList<FilterContent> read(){
		try{
			AnalysisPath analysisPath = AnalysisPath.getInstance();
			String path = analysisPath.analysisTypePath;
			this.persistentFileName = path + this.persistentFileName;
			
			ObjectInputStream objInputStream = new ObjectInputStream( 
					new FileInputStream(new File(this.persistentFileName)));

			ArrayList<FilterContent> filterContentList = (ArrayList<FilterContent>) objInputStream.readObject();

			objInputStream.close();
			return filterContentList;
		}
		catch(IOException exception){
			exception.printStackTrace();
			return null;
		}
		catch(Exception exception){
			exception.printStackTrace();
			return null;
		}
	}


}

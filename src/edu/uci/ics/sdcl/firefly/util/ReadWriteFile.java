package edu.uci.ics.sdcl.firefly.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Utility class to read the contents of a file in to an ArrayList of Strings.
 * Also write back to file the content in an ArrayList of Strings.
 * 
 * @author adrianoc
 */
public class ReadWriteFile {

	/** 
	 * Load all lines of file into and Array of Strings 
	 * @return ArrayList where each element has the content of a line in the file
	 */
	public static ArrayList<String> readToBuffer(String path, String sourceFileName){

		ArrayList<String> buffer = new ArrayList<String>();

		BufferedReader log;
		try {
			log = new BufferedReader(new FileReader(path +sourceFileName));
			String line = null;
			while ((line = log.readLine()) != null) {
				buffer.add(line);
			}
			log.close();
			return buffer;
		} 
		catch (Exception e) {
			System.out.println("ERROR while processing file:" + path+sourceFileName);
			e.printStackTrace();
			return null;
		}
	}
	
	
	/** 
	 * Flush the buffer back to the file
	 * @param path folder where the file will be written to
	 * @param destFileName the name of file that will receive the content of the buffer
	 * @param newBuffer
	 * @param destFileName
	 */
	private static void writeBackToBuffer(ArrayList<String> newBuffer, String path, String destFileName){

		String destination = path  + destFileName;
		BufferedWriter log;
		try {
			log = new BufferedWriter(new FileWriter(destination));
			for(String line : newBuffer)
				log.write(line+"\n");
			log.close();
		} 
		catch (Exception e) {
			System.out.println("ERROR while processing file:" + destination);
			e.printStackTrace();
		}
	}
}

package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Internal package class to hold the values of a filter 
 * @author adrianoc
 */
class Range {
	
		Integer max=-1;
		Integer min=-1;
		Double maxD=-1.0;
		Double minD=-1.0;
		int[] list;
		String[] professionExclusionList;
		HashMap<String, Tuple> confidenceDifficultyPairMap;
		TreeMap<String, String> questionsToExcludeMap;
		Date[] startEndDates = new Date[2];

		public Range(int minValue, int maxValue){
			this.min = new Integer(minValue);
			this.max =  new Integer(maxValue);
		}

		public Range(double minValue, double maxValue){
			this.minD = new Double(minValue);
			this.maxD =  new Double(maxValue);
		}
		
		public Range(HashMap<String, Tuple> hashMap) {
			this.confidenceDifficultyPairMap = hashMap;
		}
		
		public Range(TreeMap<String, String> map) {
			this.questionsToExcludeMap = map;
		}

		public Range(String[] professionList){
			this.professionExclusionList = professionList.clone();
		}

		public Range(int[] workerExclusionList) {
			this.list = workerExclusionList;
		}	

		public Range(Date maxDate, Date minDate) {
			this.startEndDates[0] = minDate;
			this.startEndDates[1] = maxDate;
		}

		public String toString(){
			if(list!=null && list.length>0)
				return "[excluded" +listToString(list)+  "]";
			else
				if(this.startEndDates[0]!=null && this.startEndDates[1]!=null) 
					return "First " +listToString(startEndDates)+  " hours";
				else
				if(this.questionsToExcludeMap!=null && this.questionsToExcludeMap.size()>0) 
					return "[excluded" +listToString(questionsToExcludeMap)+  "]";
				else
				if(this.confidenceDifficultyPairMap!=null && confidenceDifficultyPairMap.size()>0) 
					return "[excluded" +listToString(confidenceDifficultyPairMap)+  "]";
				else
				if(professionExclusionList!=null && professionExclusionList.length>0) 
					return "[excluded" +listToString(professionExclusionList)+  "]";
				else
					if(minD!=-1.0 || maxD!=-1.0)
						return "["+minD.toString()+","+maxD.toString()+"]";
					else
						return "["+min.toString()+","+max.toString()+"]";
				
		}

		private String listToString(Date[] startEndDates) {
			
			long millisec = startEndDates[1].getTime() - startEndDates[0].getTime();
			Double hours = new Double(millisec /(1000*3600));
			
			return hours.toString();
		}

		private String listToString(
				TreeMap<String, String> map) {
			String result = "";
			for(String tuple: map.values()){
				String tupleStr = tuple.toString();
				result = result + ","+tupleStr;
			}
			result = result.substring(0, result.length());
			return result;
		}

		private String listToString(String[] list){
			String result ="";
			for(String value: list){
				result = result + ","+value;
			}
			result = result.substring(0, result.length());
			return result;
		}

		private String listToString(int[] list){
			String result ="";
			for(int value: list){
				//System.out.println("value"+value);
				String valueStr = new Integer(value).toString();
				result = result + ","+valueStr;
			}
			result = result.substring(0, result.length());
			return result;
		}
		
		private String listToString(HashMap<String, Tuple> map){
			String result = "";
			for(Tuple tuple: map.values()){
				String tupleStr = tuple.toString();
				result = result + ","+tupleStr;
			}
			result = result.substring(0, result.length());
			return result;
		}
	}


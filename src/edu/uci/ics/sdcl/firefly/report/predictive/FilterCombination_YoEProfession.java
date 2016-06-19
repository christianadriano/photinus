package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * Generate two types of filters
 * 
 * Type-1 
 * See methods generate
 * Generate filters that select answers by workers' Years of Profession (YoE) and
 * worker profession (PROFESSIONAL_DEVELOPER, HOBBYIST, GRADUATE_STUDENT, UNDERGRADUATE_STUDENT, OTHERS)
 * 
 * Type-2
 * UNION of one or more subcrowds (see method composeSubcrowds)
 * 
 *  
 * @author adrianoc
 */
public class FilterCombination_YoEProfession {

	public static String[] STUDENT_LIST = {"Graduate_Student","Undergraduate_Student"};
	public static String[] NON_STUDENT_LIST = {"Professional_Developer","Hobbyist","Other"};
	public static String[] ALL_PROFESSIONS_LIST = {"Professional_Developer","Hobbyist","Other","Graduate_Student","Undergraduate_Student"};
	
	//First position is the First quartile, Median, Third quartile.
	public static Double[] Professional_Developer_YoE_Quartiles = {4.0,6.0,12.0};
	public static Double[] GraduateStudent_YoE_Quartiles = {1.0,2.0,5.0}; 
	public static Double[] UndergraduateStudent_YoE_Quartiles = {1.0,2.0,3.0};
	
	public static Double[] Hobbyist_YoE_Quartiles = {1.0,2.0,6.0};
	public static Double[] Other_YoE_Quartiles = {0.0,1.0,4.75};
	public static Double[] Students_YoE_Quartiles = {1.0,2.0,4.0};
	public static Double[] NonStudents_YoE_Quartiles = {1.0,3.0,9.0};

	//Eliminate the first quartile of all professions

	//Compute all
	//Compute each profession
	
	//non-students without the first quartile
	//students without the first quartile
   
	public static ArrayList<SubCrowd> composeSubCrowds(){

		ArrayList<SubCrowd>  subCrowdList = new ArrayList<SubCrowd>();
		HashMap<String, CombinedFilterRange> map = AttributeRangeGenerator.getMostDifficultyProfession();
		CombinedFilterRange range;
		FilterCombination combination;
		SubCrowd crowd;

		//------------------------------------------------
		//
		crowd = new SubCrowd();
		crowd.name = "Removed Undergrad_YoE_1stQT and Grad_YoE_1stQT";

		range = map.get(AttributeRangeGenerator.UNDERGRAD_YoE_QT_2_UP);			
		combination = generateCombination(range);	
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.GRAD_YoE_QT_2_UP);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);
		
		range = map.get(AttributeRangeGenerator.NON_STUDENTS_YoE_ALL);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);
		
		subCrowdList.add(crowd);

		//------------------------------------------------
	
	
}

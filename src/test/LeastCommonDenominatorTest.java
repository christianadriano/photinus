package test;

import static org.junit.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.sdcl.firefly.util.LeastCommonDenominator;

public class LeastCommonDenominatorTest {

	ArrayList<Integer> list;
	
	@Before
	public void setup(){
		list= new ArrayList<Integer>();
		list.add(6);
		list.add(12);
		list.add(48);
	}
	
	@Test
	public void testLeastCommonMultiplier() {
		Logger logger = LoggerFactory.getLogger(LeastCommonDenominatorTest.class);
		logger.info("testing logger");
		long common = LeastCommonDenominator.lcm(list.toArray());
		assertEquals(48, common);
	}


	@Test
	public void testGreatesCommonDivisor() {
		long common = LeastCommonDenominator.gcd(list.toArray());
		assertEquals(6, common);
	}
	
	
	@Test
	public void testComputeMultiple(){
		
		int size = 10;
		int numberOfTasks = 57;
		
		int remainder = numberOfTasks%size;
		int fillup = size-remainder;
		int target = numberOfTasks+fillup;

		assertEquals(target,60);

		size = 10;
		numberOfTasks = 50;
		
		remainder = numberOfTasks%size;
		System.out.println("remainder:"+remainder);
		if(remainder>0)
			fillup = size-remainder;
		else
			fillup=0;
		target = numberOfTasks+fillup;

		assertEquals(target,50);
		
		
		
	}
	
	
	
	//Testing for bugs 51 and 54
	public static void main(String[] args){
		
		//Bug 54
		char char3='_';
		if(char3<'A' || char3> 'Z')
			System.out.println("char3 out of range");
		
		//Bug 51
		double x = -0.0;
		long value = (long) x;
		System.out.println("expected: "+String.valueOf(x) + ", actual:"+String.valueOf(value));
		
		//Bug 35
		String[] array =null;
		String element=null;
		Class<?> type = array!=null ? array.getClass() : (element!=null ? element.getClass() : Object.class);
	}

}

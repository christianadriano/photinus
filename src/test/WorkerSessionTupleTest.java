package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import edu.uci.ics.sdcl.firefly.report.descriptive.answer.WorkerSessionTuple;

public class WorkerSessionTupleTest {

	@Test
	public void testZeroCount() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(1);list.add(2);list.add(3);		
		WorkerSessionTuple tuple = new WorkerSessionTuple();
		assertEquals(0,tuple.countIdenticalLevels(list));	
	}
	
	@Test
	public void testTwoCount() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(1);list.add(3);list.add(3);		
		WorkerSessionTuple tuple = new WorkerSessionTuple();
		assertEquals(1,tuple.countIdenticalLevels(list));	
	}
	
	@Test
	public void testThreeCount() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(1);list.add(1);list.add(1);		
		WorkerSessionTuple tuple = new WorkerSessionTuple();
		assertEquals(3,tuple.countIdenticalLevels(list));	
	}

}

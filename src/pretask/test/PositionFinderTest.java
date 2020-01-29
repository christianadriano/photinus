package pretask.test;

import static org.junit.Assert.*;

import org.junit.Test;

public class PositionFinderTest {

	@Test
	public void testFindPosition() {
		String[] myArray = {"Hola","Kumusta"} ;//"Hello", "Ciao"};
		org.junit.Assert.assertSame("Wrong position", 2, 
				PositionFinder.findPosition(new String[]{"Hola","Kumusta"}, "Kumusta"));
	}

}

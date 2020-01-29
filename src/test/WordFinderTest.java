/**
 * 
 */
package test;

import static org.junit.Assert.*;

import org.junit.Test;

import pretask.test.WordFinder;

/**
 * @author Christian Adriano
 *
 */
public class WordFinderTest {

	/**
	 * Test method for {@link WordFinder#wordPosition(java.lang.String)}.
	 */
	@Test
	public void testWordPosition() {
		WordFinder finder = new WordFinder("");
		//System.out.println(finder.wordPosition("cogito"));
		org.junit.Assert.assertSame("Wrong position", 2, 
				finder.wordPosition("cogito ergo sum","sum"));

	}

}

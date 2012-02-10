/**
 * 
 */
package net.skcomms.search.backend.server;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author ejej@sk.com
 *
 */
public class KimTest {

	@Test
	public void test() {
		Person person = new Kim();
		assertEquals("Kim", person.getName());
	}

}

/**
 * 
 */
package net.skcomms.search.backend.server;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author pelcious@sk.com
 *
 */
public class KangTest {

	@Test
	public void test() {
		Person person = new Kang();
		assertEquals("Kang", person.getName());
	}

}

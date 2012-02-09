/**
 * 
 */
package net.skcomms.search.backend.server;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author jujang@sk.com
 *
 */
public class JangTest {

	@Test
	public void test() {
		Person person = new Jang();
		assertEquals("Jang", person.getName());
	}

}
